package idv.const_x.console;

import idv.const_x.console.io.ConsoleColorEnum;
import idv.const_x.console.io.ConsoleIOProxy;
import idv.const_x.console.io.Inputer;
import idv.const_x.console.completer.FirstLimitedCompleter;
import idv.const_x.console.completer.StringTreeCompleter;
import idv.const_x.console.completer.TestCompleter;
import idv.const_x.console.handler.AbsInputLineHandler;
import idv.const_x.console.handler.HelpBuilder;
import idv.const_x.console.handler.InputLineHandler;
import idv.const_x.swing.AppAppearance;
import idv.const_x.utils.ColoredStringUtils;
import idv.const_x.utils.StringExtUtils;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.completer.AggregateCompleter;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * @Description <pre>
 *
 * </pre>
 * @Author const.x
 * @Date 2023-03-17
 */
public class JlineShell implements Inputer {

    private static JlineShell instance = null;

    public static JlineShell getInstance() {
        return getInstance("UTF-8");
    }

    public static JlineShell getInstance(String encoding) {
        if (instance == null) {
            synchronized (JlineShell.class) {
                if (instance == null) {
                    JlineShell jlineShell = new JlineShell(encoding);
                    instance = jlineShell;
                }
            }
        }
        return instance;
    }

    private JlineShell(String encoding) {
        this.io_encoding = encoding;
    }

    private String appName = "JlineShell";

    private String iconResource = "/images/logo-x.png";

    public static final String prompt = "[36m->\u001B[39m";

    private ConsoleIOProxy console;

    private LineReader lineReader;

    private String io_encoding = null == System.getProperty("sun.jnu.encoding") ? "GBK"
            : System.getProperty("sun.jnu.encoding");

    public boolean debug =false;

    public void run(String[] args) {
        try {
            AppAppearance.configure(appName,iconResource);
            initHandlers();
            console = ConsoleIOProxy.getInstance(this.io_encoding);
            console.setInputer(this);
            Terminal terminal = TerminalBuilder.builder().system(true).encoding(io_encoding).build();
            List<Completer> completers = new ArrayList<>();
            List<String> cmds = new ArrayList<>();
            for (Map.Entry<String, InputLineHandler> entry : handles.entrySet()) {
                InputLineHandler handler = entry.getValue();
                if (handler.getCompleter() != null) {
                    completers.add(handler.getCompleter());
                }
                cmds.add(entry.getKey());
            }
            if (debug) {
                TestCompleter testCompleter = new TestCompleter();
                completers.add(testCompleter);
            }

            ArgumentCompleter completer = new ArgumentCompleter(
                    new StringsCompleter(cmds),
                    new AggregateCompleter(completers)
            );
            completer.setStrict(false);

            lineReader = LineReaderBuilder.builder().terminal(terminal).completer(completer).build();
            if (args != null && args.length > 0) {
                handleCommad(args);
            }
            while (true) {
                String line = lineReader.readLine(prompt); // 获取输入的信息
                line = line.trim();
                if (!line.equals("")) {
                    if ("exit".equalsIgnoreCase(line)) {
                        return;
                    }
                    String[] params = splitParams(line);
                    handleCommad(params);
                }
            }
        } catch (org.jline.reader.UserInterruptException e) {
            console.warning("强制退出");
        } catch (NoClassDefFoundError e) {
            if (e.getMessage() != null && e.getMessage().contains("jline")) {
                console.warning("强制退出");
            }else if (e.getMessage() != null && e.getMessage().contains("fusesource")) {
                console.warning("强制退出");
            } else {
                e.printStackTrace(console.getErr());
            }
        } catch (Exception e) {
            e.printStackTrace(console.getErr());
        }
    }

    private String[] splitParams(String str) {
        List<String> parts = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();
        boolean in = false;
        char start = ' ';
        for (char c : str.toCharArray()) {
            if (c == '\'' || c == '\"') {
                if (in) {
                    if (start == c) {
                        in = false;
                        buffer.append(c);
                        String s = buffer.toString().trim();
                        buffer.setLength(0);
                        if (StringExtUtils.isNotBlank(s)) {
                            parts.add(s);
                        }
                    } else {
                        buffer.append(c);
                    }
                } else {
                    in = true;
                    start = c;
                    buffer.append(c);
                }
            } else if (c == ' ') {
                if (in) {
                    buffer.append(c);
                } else {
                    String s = buffer.toString().trim();
                    buffer.setLength(0);
                    if (StringExtUtils.isNotBlank(s)) {
                        parts.add(s);
                    }
                }
            } else {
                buffer.append(c);
            }
        }
        if (buffer.length() > 0) {
            String s = buffer.toString().trim();
            buffer.setLength(0);
            if (StringExtUtils.isNotBlank(s)) {
                parts.add(s);
            }
        }
        return parts.toArray(new String[0]);
    }


    private void handleCommad(String[] args) {
        if (args.length == 1 && (args[0].equals("?") || args[0].equals("？") || args[0].equals("-?") || args[0].equals("-？"))) {
            printHelp();
            return;
        }

        String main = args[0];
        if (main.startsWith(prompt)) {
            main = main.substring(prompt.length());
            args[0] = main;
        }

        InputLineHandler handler = null;
        if (handles.containsKey(main.toLowerCase())) {
            handler = handles.get(main.toLowerCase());
        } else {
            handler = this.defaultHandler;
        }
        try {
            handler.handleLine(console, args);
        } catch (Exception e) {
            e.printStackTrace(console.getErr());
        }
    }

    private void printHelp() {
        int counter = 1;
        List<InputLineHandler> list = new ArrayList<>(handles.values());
        for (InputLineHandler handler : list) {
            console.println(StringExtUtils.fillStringLen(String.valueOf(counter),3) + " " + StringExtUtils.fillStringLen(handler.getCmd(),10)  +" : "+ handler.getDesc(), ConsoleColorEnum.GREEN,ConsoleColorEnum.DEFAULT);
            counter++;
        }

        while (true){
            String input = console.waittingInput("请选择对应序号以打印命令详细信息,退出请输入-1或回车").trim();
            if ("-1".equals(input) || StringExtUtils.isBlank(input)) {
                break;
            }
            int idx;
            try {
                idx = Integer.valueOf(input);
            }catch (Exception e){
                console.error("输入非法:"+input);
                continue;
            }
            if (idx> 0 && idx <= handles.size()) {
                console.println(list.get(idx-1).getHelp());
                continue;
            }
            console.error("输入非法:"+input);
        }




    }

    private void initHandlers() {
        registryHandler(new AbsInputLineHandler() {

                            int page = 1;

                            @Override
                            public void handleLine(String line, ConsoleIOProxy console, Map<String, String> options, String... params) {
                                String s = "---------------------------------------"
                                        + StringExtUtils.fillStringLen(String.valueOf(page), 3, '-', 1)
                                        + "---------------------------------------";
                                console.println(ColoredStringUtils.colorString(s,ColoredStringUtils.COLOR_AQUA) );
                                console.clearScreen();
                                page++;
                            }

                            @Override
                            public boolean showHelpWhenParamNul() {
                                return false;
                            }

                            @Override
                            public String getCmd() {
                                return "clear";
                            }


                            public HelpBuilder initHelpBuilder() {
                                return new HelpBuilder(getCmd(), "清屏")
                                        .build();
                            }

                        }
        );

        registryHandler(new AbsInputLineHandler() {

                            @Override
                            public void handleLine(String line, ConsoleIOProxy console, Map<String, String> options, String... params) {
                                String param = getParam(params, 0);
                                if (param == null || param.equalsIgnoreCase("on")) {
                                    console.setLevel(ConsoleIOProxy.INFO_LEVEL_DEBUG);
                                } else {
                                    console.setLevel(ConsoleIOProxy.INFO_LEVEL_INFO);
                                }
                            }

                            @Override
                            public String getCmd() {
                                return "debug";
                            }

                            @Override
                            public boolean showHelpWhenParamNul() {
                                return false;
                            }

                            public HelpBuilder initHelpBuilder() {
                                return new HelpBuilder(getCmd(), "debug模式")
                                        .build();
                            }

                            @Override
                            public FirstLimitedCompleter initCompleter() {
                                return new StringTreeCompleter(getCmd()).addSubStrings(getCmd(),"on","off");
                            }
                        }
        );
        //setDefaultHandler(cmdHandler);
    }

    public void setDefaultHandler(AbsInputLineHandler defaultHandler) {
        this.defaultHandler = defaultHandler;
    }


    private AbsInputLineHandler defaultHandler = new AbsInputLineHandler() {


        @Override
        public String getCmd() {
            return "defalut";
        }

        @Override
        public void handleLine(String line, ConsoleIOProxy console, Map<String, String> options, String... params) {
            console.error("unknown " + line);
        }

        public HelpBuilder initHelpBuilder() {
            return new HelpBuilder(getCmd(), "默认")
                    .build();
        }

    };

    public void registryHandler(InputLineHandler handler) {
        if (handler.enable()) {
            handles.put(handler.getCmd().toLowerCase(), handler);
        }
    }

    private final Map<String, InputLineHandler> handles = new LinkedHashMap<>();


    @Override
    public String waittingInput(String prompt) {
        if (prompt != null) {
            return lineReader.readLine("[33m" + prompt + "\u001B[39m");
        }
        return lineReader.readLine();
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getIconResource() {
        return iconResource;
    }

    public void setIconResource(String iconResource) {
        this.iconResource = iconResource;
    }
}
