package idv.const_x.console;

import idv.const_x.console.io.ConsoleIOProxy;

/**
 * @Description <pre>
 *
 * </pre>
 * @Author const.x
 * @Date 2024-02-03
 */
public class ConsoleIOTest {

    public static void main(String[] args) {
        ConsoleIOProxy instance = ConsoleIOProxy.getInstance();
        String s = instance.waittingInput("输入任意内容");
        instance.info("你的输入为:"+s);
    }

}
