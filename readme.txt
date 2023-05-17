для поддержки 8.3.23

добавить в 1cedt.ini
-javaagent:plugins/org.aspectj.weaver_1.9.6.202202111622.jar
-Dorg.aspectj.weaver.loadtime.configuration=file:C:\DevCloud\notes\1C\Patches\EDT\OSGI-Weaving-Patches\src\main\resources\META-INF\aop.xml

положить собранный jar в dropins