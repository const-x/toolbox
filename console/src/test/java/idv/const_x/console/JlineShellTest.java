package idv.const_x.console;

import idv.const_x.console.handler.CalculateHandler;
import idv.const_x.console.handler.DemoHandler;

/**
 * @Description <pre>
 *
 * </pre>
 * @Author const.x
 * @Date 2024-02-03
 */
public class JlineShellTest {

    public static void main(String[] args) {
        JlineShell shell = JlineShell.getInstance();
        shell.registryHandler(new DemoHandler());
        shell.registryHandler(new CalculateHandler());
        shell.run(args);
    }

}
