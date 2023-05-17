package dev.etsys.patch.aspects;

import com._1c.g5.v8.dt.platform.version.Version;
import com.google.common.collect.ImmutableMap;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Aspect
public class XmlVersionRegistryAspect {

    @Around(value = "execution(private void com._1c.g5.v8.dt.internal.xml.XmlVersionRegistry.initialize()) ", argNames = "thisJoinPoint")
    public void aroundInitialize(ProceedingJoinPoint thisJoinPoint) throws Throwable {

        Version version = null;
        try {
            version = Version.create("8.3.23");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        thisJoinPoint.proceed();
        Object instance = thisJoinPoint.getTarget();

        Field pf = instance.getClass().getDeclaredField("xmlVersionToRuntimeVersions");
        pf.setAccessible(true);
        Map<String, List<Version>> pXmlVersionToRuntimeVersions = (Map<String, List<Version>>) pf.get(instance);
        HashMap<String, List<Version>> map = new HashMap(pXmlVersionToRuntimeVersions);


        map.put("2.16", Arrays.asList(version));

        pf.set(instance, ImmutableMap.copyOf(map));
        pf.setAccessible(false);
    }



    //    private void initialize() {
//        Map<Version, String> map = this.readExtensionData();
//        this.runtimeVersionToXmlVersion = ImmutableMap.copyOf(map);
//        Map<String, List<Version>> tmp = Maps.newHashMap();
//
//        Map.Entry entry;
//        Object list;
//        for(Iterator var4 = map.entrySet().iterator(); var4.hasNext(); ((List)list).add((Version)entry.getKey())) {
//            entry = (Map.Entry)var4.next();
//            list = (List)tmp.get(entry.getValue());
//            if (list == null) {
//                tmp.put((String)entry.getValue(), list = Lists.newArrayList());
//            }
//        }
//
//        this.xmlVersionToRuntimeVersions = ImmutableMap.copyOf(tmp);
//    }

}
