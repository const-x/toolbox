package idv.const_x.utils;

import idv.const_x.http.DefaultInvokeHandler;
import idv.const_x.http.HttpInvoker;

import java.io.IOException;

/**
 * @Description <pre>
 *
 * </pre>
 * @Author const.x
 * @Date 2024-02-04
 */
public class HttpInvokerTest {

    public static void main(String[] args) throws IOException {
        String baseUrl ="http://sql.superboss.cc/new_index.jsp";
        DefaultInvokeHandler invokeHandler = new DefaultInvokeHandler();
        HttpInvoker.invoke(baseUrl, HttpInvoker.GET,invokeHandler);
        System.out.println(invokeHandler.getResult());

    }
}
