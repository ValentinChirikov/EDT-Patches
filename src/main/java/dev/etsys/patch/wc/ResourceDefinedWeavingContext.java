package dev.etsys.patch.wc;

import org.aspectj.weaver.loadtime.DefaultWeavingContext;
import org.aspectj.weaver.loadtime.definition.Definition;
import org.aspectj.weaver.loadtime.definition.DocumentParser;
import org.aspectj.weaver.tools.WeavingAdaptor;

import java.net.URL;
import java.util.*;

public class ResourceDefinedWeavingContext extends DefaultWeavingContext {

    public ResourceDefinedWeavingContext(ClassLoader loader) {
        super(loader);
    }

    @Override
    public List<Definition> getDefinitions(ClassLoader loader, WeavingAdaptor adaptor) {
        List<Definition> definitions = new ArrayList<>();
        try {
            StringTokenizer st = new StringTokenizer("dev/etsys/patch/aop.xml", ";");

            while (st.hasMoreTokens()) {
                String nextDefinition = st.nextToken();
                 {
                    Enumeration<URL> xmls = getResources(nextDefinition);

                    Set<URL> seenBefore = new HashSet<URL>();
                    while (xmls.hasMoreElements()) {
                        URL xml = xmls.nextElement();
                        if (!seenBefore.contains(xml)) {
                            definitions.add(DocumentParser.parse(xml));
                            seenBefore.add(xml);
                        } else {
//                            debug("ignoring duplicate definition: " + xml);
                        }
                    }
                }
            }
            if (definitions.isEmpty()) {
//                info("no configuration found. Disabling weaver for class loader " + getClassLoaderName(loader));
            }
        } catch (Exception e) {
            definitions.clear();
//            warn("parse definitions failed", e);
        }

        return definitions;

    }
}
