package dev.etsys.patch;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.hooks.weaving.WeavingHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Activator implements BundleActivator {

    private final List<ServiceRegistration<?>> regs = new ArrayList<>();
    private final Logger log = LoggerFactory.getLogger(getClass());
    protected static BundleContext context;

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        context = bundleContext;
        addHook(context, new Hook());
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        for (ServiceRegistration<?> reg : regs) {
            reg.unregister();
        }
        regs.clear();
        context = null;
    }

    private void addHook(BundleContext context, WeavingHook h) {
        log.info("Registering etsys patch Weaving Hook {}", h);
        regs.add(context.registerService(WeavingHook.class.getName(), h, null));
    }

}