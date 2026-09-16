package idv.const_x.console.handler;

import idv.const_x.console.io.ConsoleIOProxy;
import idv.const_x.utils.OSUtils;
import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * 创建一个cmd实例 并将标准输入(system.in)的内容写入实例的outputStream
 * 并监听实例的inputStream 读取返回内容写入标准输出(system.out)
 * <pre>
 * [-------]             [-------]              [-------]
 * |       |   sys.in    |       |   output     |       |
 * | term  | --------->  | curr  |  --------->  | inst  |
 * |       |   sys.out   |       |   input      |       |
 * |       | <---------  |       |  <---------  |       |
 * [-------]             [-------]              [-------]
 *
 * </pre>
 *
 *
 */
public class CmdHandler extends AbsInputLineHandler {

	
	private PrintWriter writer = null;

	/**
	 * window系统默认语言:GBK,即CMD所使用的编码格式
	 */
	private static final String DEFAULT_LANGUAGE = null == System.getProperty("sun.jnu.encoding") ? "GBK"
			: System.getProperty("sun.jnu.encoding");

	@Override
	public void handleLine(String line, ConsoleIOProxy console, Map<String, String> options, String ...params){
		init(console);
		writer.println(line);
        writer.flush();
	}

	private void init( ConsoleIOProxy console) {
		try {
			if(writer == null){
				synchronized (CmdHandler.class) {
					if(writer == null){
						Process process = null;
						if (OSUtils.isWindows()) {
							process = Runtime.getRuntime().exec("cmd");
						}else {
							process = Runtime.getRuntime().exec("/bin/sh");
						}

						// PrintWriter自身并没有处理编码的职责，它还是应该看成一个装饰器比较好：它就是为了输出更方便而设计的，
						// 提供print、println、printf等便利方法。要设置编码的话，可以在它的底层Writer上设置：（这里以OutputStreamWriter为底层Writer）
						PrintWriter writer = new PrintWriter(new OutputStreamWriter(process.getOutputStream(), DEFAULT_LANGUAGE));
						// 监听标准输出和异常输出
						ProcessInputStreamThread outPrintThread = new ProcessInputStreamThread(process.getInputStream(),console, false);
						ProcessInputStreamThread errPrintThread = new ProcessInputStreamThread(process.getErrorStream(),console, true);
						outPrintThread.setName("InputStreamThread");
						outPrintThread.start();
						errPrintThread.setName("ErrorStreamThread");
						errPrintThread.start();
						this.writer = writer;

						Signal intSignal = new Signal("INT");
                        Signal.handle(intSignal,new SignalHandler(){
							@Override
							public void handle(Signal signal) {
								writer.write("\n");
								writer.flush();
							}
						});
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//
	}

	class ProcessInputStreamThread extends Thread {

		InputStream input;
		BufferedReader breader = null;
		ConsoleIOProxy console = null;
		boolean error = false;

		ProcessInputStreamThread(InputStream input, ConsoleIOProxy console, boolean error) {
			this.input = input;
			this.error = error;
			this.console = console;
			// 避免出现乱码问题,直接使用系统默认的编码格式
			breader = new BufferedReader(new InputStreamReader(input, Charset.forName(DEFAULT_LANGUAGE)));
		}

		@Override
		public void run() {
			try {
				String str = null;
				while ((str = breader.readLine()) != null) {
					if (error) {
						console.errPrintln(str);
					} else {
						console.println(str);
					}

				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (null != input) {
					try {
						input.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (null != breader) {
					try {
						breader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	public String getCmd() {
		return "cmd";
	}

	@Override
	public HelpBuilder initHelpBuilder() {
		return new HelpBuilder(getCmd(),"系统命令行工具")
				.build();
	}
}
