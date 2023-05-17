package dev.etsys.patch.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class ApiResolveDebugHttpClientAspect {
    public static final String ver8323 = "8.3.23";
    public static final String ver8322 = "8.3.22";
    public static final String VNET_1550 = "http://1c-dev-cl.vnet:1550";

    @Around(value = "execution(public String com._1c.g5.v8.dt.internal.debug.core.runtime.client.ApiResolveDebugHttpClient.getVersion(String) throws com._1c.g5.v8.dt.debug.core.runtime.client.RuntimeDebugClientException) && args(debugServerUrl)", argNames = "thisJoinPoint,debugServerUrl")
    public String aroundGetVersion(ProceedingJoinPoint thisJoinPoint, String debugServerUrl) throws Throwable {
        Object version = thisJoinPoint.proceed();
        if(ver8323.equals((String)version) && debugServerUrl.equals(VNET_1550)) {
            return ver8322;
        } else {
            return (String)version;
        }
    }
}
