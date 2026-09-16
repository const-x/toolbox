package idv.const_x.console.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.Charset;

/**
 * 基于 System.in 的输入器实现
 *
 * @Author const.x
 * @Date 2023-03-15
 */
public class SystemInInputer implements Inputer {

    private BufferedReader reader;
    private String encoding;
    private PrintStream out;

    public SystemInInputer() {
        this("UTF-8");
    }

    public SystemInInputer(String encoding) {
        this(encoding, System.out);
    }

    public SystemInInputer(String encoding, PrintStream out) {
        this.encoding = encoding;
        this.out = out == null ? System.out : out;
        this.reader = new BufferedReader(new InputStreamReader(System.in, Charset.forName(encoding)));
    }

    @Override
    public String waittingInput(String prompt) {
        out.print(prompt);
        out.flush();
        try {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getEncoding() {
        return encoding;
    }
}
