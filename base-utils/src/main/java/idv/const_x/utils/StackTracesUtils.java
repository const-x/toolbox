package idv.const_x.utils;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description <pre>
 *  堆栈信息精简工具
 * </pre>
 * @Author const.x
 * @Date 2023-03-09
 */
public class StackTracesUtils {


    public static Throwable filterStackTraces(Throwable e){
        return filterStackTraces(e,0);
    }

    public static Throwable filterStackTraces(Throwable e,int begin){
        if (e == null) {
            return null;
        }
        StackTraceElement[] stackTraces = e.getStackTrace();
        List<StackTraceElement> filtered = new ArrayList<>();

        for (int i = begin; i < stackTraces.length; i++) {
            StackTraceElement stackTraceElement = stackTraces[i];
            //第一个堆栈不做过滤
            if (i == begin) {
                filtered.add(stackTraceElement);
            }else {
                String line = stackTraceElement.getClassName();
                boolean filted = false;
                for (String proFix : filters) {
                    if (line.contains(proFix)) {
                        filted = true;
                        for (String wProFix : whiteList) {
                            if (line.contains(wProFix)) {
                                filted = false;
                                break;
                            }
                        }
                        if (filted) {
                            break;
                        }
                    }
                }
                if (!filted) {
                    filtered.add(stackTraceElement);
                }
            }
        }

        e.setStackTrace(filtered.toArray(new StackTraceElement[0]));
        Throwable cause = e.getCause();
        if (cause != null) {
            filterStackTraces(cause);
        }
        return e;
    }

    static List<String> filters = Arrays.asList(
            "org.springframework.cglib",
            "org.springframework.aop",
            "org.springframework.transaction.interceptor",
            "org.springframework.web",

            "sun.reflect",
            "java.lang.reflect",
            "java.lang.Thread",
            "java.util.concurrent",
            "java.util.stream",

            "org.apache.catalina",
            "org.apache.coyote",
            "org.apache.tomcat",
            "javax.servlet.http.HttpServlet",

            "com.alibaba.dubbo",

            "$$FastClassBySpringCGLIB$$",
            "$$EnhancerBySpringCGLIB$$",

            "com.netflix.hystrix",
            "rx.internal.operators",
            "rx.observers",
            "rx.Observable"


    );

    static List<String> whiteList = Arrays.asList(
            "org.springframework.transaction.interceptor.TransactionAspectSupport",
            "com.alibaba.dubbo.monitor.support.MonitorFilter"
    );

}
