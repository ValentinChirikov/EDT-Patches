package dev.etsys.patch.aspects;

import com._1c.g5.v8.dt.platform.version.Version;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.util.Arrays;
import java.util.List;

@Aspect
public class XmlVersionServiceAspect {

    public static final String v216 = "2.16";

    @Around(value = "execution(public List<Version> com._1c.g5.v8.dt.internal.xml.XmlVersionService.getRuntimeVersions(String)) && args(xmlVersion) ", argNames = "thisJoinPoint, xmlVersion")
    public List<Version> aroundGetRuntimeVersions(ProceedingJoinPoint thisJoinPoint, String xmlVersion) throws Throwable {

        if(v216.equals(xmlVersion)) return Arrays.asList(Version.V8_3_22);
        return (List<Version>) thisJoinPoint.proceed();
    }

    @Around(value = "execution(public boolean com._1c.g5.v8.dt.internal.xml.XmlVersionService.isValidXmlVersionRepresentation(String)) && args(xmlVersion)", argNames = "thisJoinPoint, xmlVersion")
    public boolean aroundIsValidXmlVersionRepresentation(ProceedingJoinPoint thisJoinPoint, String xmlVersion) throws Throwable {

        if(v216.equals(xmlVersion)) return true;
        return (boolean) thisJoinPoint.proceed();
    }

}
