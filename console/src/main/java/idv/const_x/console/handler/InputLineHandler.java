package idv.const_x.console.handler;

import idv.const_x.console.io.ConsoleIOProxy;
import idv.const_x.console.completer.FirstLimitedCompleter;

/**
 * @Description <pre>
 *
 * </pre>
 * @Author const.x
 * @Date 2024-04-30
 */
public interface InputLineHandler {

    String getCmd();

    String getDesc();
    
    void handleLine(ConsoleIOProxy console, String[] args);

    String getHelp();

    FirstLimitedCompleter getCompleter();

    boolean enable();
}
