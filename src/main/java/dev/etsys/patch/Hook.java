package dev.etsys.patch;

import dev.etsys.patch.wc.ResourceDefinedWeavingContext;
import org.aspectj.weaver.loadtime.ClassLoaderWeavingAdaptor;
import org.osgi.framework.hooks.weaving.WeavingHook;
import org.osgi.framework.hooks.weaving.WovenClass;

import java.util.*;

public class Hook implements WeavingHook {

    // целевые классы
    // TODO: сделать динамическиую генерацию aop.xml
    //  или разобраться со старым пакетом org.eclipse.equinox.weaving.aspectj
    final Set<String> targetClasses = Set.of(
        "com._1c.g5.v8.dt.internal.debug.core.runtime.client.ApiResolveDebugHttpClient",
        "com._1c.g5.v8.dt.internal.xml.XmlVersionService");

    // на всякий случай, вообще должно хватать Eclipse-SupplementBundle который генерится
    final Set<String> dynamicImports = Set.of(
            "*"
    );

    final Set<String> wovenClasses = new HashSet<>();

    // закешируем адаптеры, если нужно шить несколько классов в одном пакете
    private final Map<ClassLoader, ClassLoaderWeavingAdaptor> adapters = new HashMap<>();

    @Override
    public synchronized void weave(WovenClass wovenClass) {
        String wovenClassClassName = wovenClass.getClassName();

        if (targetClasses.contains(wovenClassClassName) && !wovenClasses.contains(wovenClassClassName)) {
            try {
                ClassLoader wovenClassloader = wovenClass.getBundleWiring().getClassLoader();

                ClassLoaderWeavingAdaptor weavingAdaptor = adapters.computeIfAbsent(wovenClassloader, cl ->  {

                    ResourceDefinedWeavingContext dwc = new ResourceDefinedWeavingContext(cl);
                    ClassLoaderWeavingAdaptor adaptor = new ClassLoaderWeavingAdaptor();
                    adaptor.initialize(cl, dwc);

                    return adaptor;
                });

                wovenClass.getDynamicImports().addAll(dynamicImports);
                wovenClass.setBytes(weavingAdaptor.weaveClass(wovenClassClassName, wovenClass.getBytes()));
                wovenClasses.add(wovenClassClassName);
            } catch (Exception  e ) {
                System.err.println(e);

            }
        }

    }

}