import java.io.PipedInputStream
import java.io.PipedOutputStream
import java.net.URI
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.StandardCopyOption

buildscript {
    dependencies {
        // для формирования манифеста
        classpath("biz.aQute.bnd:biz.aQute.bnd.gradle.plugin:6.4.0")
    }
}

plugins {
    java
    id("io.freefair.aspectj.post-compile-weaving") version "8.0.1"
}

repositories {
    mavenCentral()
}

val edtPluginsPath: String by project
val edtDropinsPath: String by project

val edtPlugins = fileTree(baseDir = edtPluginsPath) {
    include(
        "*.jar"
    )
}

dependencies {
    // без этого не запустится post-compile-weaving а без него не сработает LTW
    implementation("org.aspectj:aspectjrt:1.9.6")
    implementation("org.aspectj:aspectjweaver:1.9.6")
    implementation(edtPlugins)
}

tasks.jar {
    manifest {
        attributes["Export-Package"] = "*"	// заменится analyzer.setProperty
        attributes["Import-Package"] = "*"	// заменится analyzer.setProperty
        attributes["Bundle-Activator"] = "dev.etsys.patch.Activator"
        // добавит зависимости в бандлы чтобы класслоадер видел аспекты и спог определить AjcClosure в целевом пакете
        attributes["Eclipse-SupplementBundle"] = "com._1c.g5.v8.dt.debug.core,com._1c.g5.v8.dt.xml"
    }
}

tasks.register("manifestBundle") {
    dependsOn(tasks.jar)
    doLast {
        val configs = project.configurations.getByName("archives").outgoing
        val publication = configs.artifacts.first { it.type == ArtifactTypeDefinition.JAR_TYPE }

        val analyzer = aQute.bnd.osgi.Analyzer()

        analyzer.setJar(publication.file) // give bnd the contents

        // настройки для manifest
        analyzer.setProperty("Bundle-SymbolicName", "dev.etsys.patch")
        analyzer.setProperty("Export-Package", "dev.etsys.patch, dev.etsys.patch.aspects")
        analyzer.setProperty("Import-Package", "org.aspectj.runtime.internal, *")
        analyzer.setProperty("Bundle-Version", "1.0.8")

        // расчет манифеста
        val manifest = analyzer.calcManifest()
        analyzer.close()

        // параметры работы с jar/zip
        val jarProperties: Map<String, String> = mapOf(Pair("create", "false"), Pair("useTempFile","true"))

        val jarFile = URI.create("jar:" + publication.file.toURI())

        // создать jar фс
        val jarFS = FileSystems.newFileSystem(jarFile, jarProperties, this.javaClass.classLoader)
        // пусть к манифесту внутри jar
        val manifestJarPath = jarFS.getPath("META-INF/MANIFEST.MF")

        val pis = PipedInputStream()
        val pos = PipedOutputStream(pis)

        // создать нить для добавление манифеста из потока
        val fct = Thread { Files.copy(pis, manifestJarPath, StandardCopyOption.REPLACE_EXISTING) }
        // запустить нить
        fct.start()

        // записать манивест в поток
        manifest.write(pos)
        pos.close()

        // дождаться завершения нити
        fct.join()

        // закрыть поток и ФС
        pis.close()
        jarFS.close()
    }

}

tasks.register("deployBundle") {
    dependsOn(tasks["manifestBundle"])
    doLast {
        val configs = project.configurations.getByName("archives").outgoing
        val publication = configs.artifacts.first { it.type == ArtifactTypeDefinition.JAR_TYPE }

//        val jarFile = URI.create("jar:" + publication.file.toURI())
        Files.copy(publication.file.toPath(),
            File(edtDropinsPath + "\\" + publication.file.name).toPath(),
            StandardCopyOption.REPLACE_EXISTING)

    }

}