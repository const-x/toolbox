package idv.const_x.console.io;

import idv.const_x.utils.ColoredStringUtils;
import idv.const_x.utils.DateUtils;
import idv.const_x.utils.ReflectUtils;
import idv.const_x.utils.StringExtUtils;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;
import org.fusesource.jansi.Ansi.Erase;
import org.fusesource.jansi.AnsiConsole;

import java.awt.*;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.fusesource.jansi.Ansi.ansi;

public class ConsoleIOProxy {
		private boolean inited = false;

		private String encoding = "UTF-8";
		private PrintStream out = System.out;
		private PrintStream err = System.err;

		private Inputer inputer;

		public static final int INFO_LEVEL_DEBUG = 0;
		public static final int INFO_LEVEL_INFO = 1;
		public static final int INFO_LEVEL_WARN = 2;
		public static final int INFO_LEVEL_ERROR = 3;

		private int level = INFO_LEVEL_INFO;

		private boolean silent = false;

		private ConsoleIOProxy(String encoding) {
			if (StringExtUtils.isNotBlank(encoding)) {
				this.encoding = encoding;
			}
		}

		public static ConsoleIOProxy getInstance(String encoding) {
			ConsoleIOProxy instance = new ConsoleIOProxy(encoding);
			instance.init();
			return instance;
		}

		public static ConsoleIOProxy getInstance() {
			ConsoleIOProxy instance = new ConsoleIOProxy("UTF-8");
			instance.init();
			return instance;
		}

		public static ConsoleIOProxy getInstance(String encoding, PrintStream out, PrintStream err) {
			ConsoleIOProxy instance = new ConsoleIOProxy(encoding);
			instance.out = out;
			if (err != null) {
				instance.err = err;
			} else {
				instance.err = out;
			}
			instance.init();
			return instance;
		}

		public String getEncoding() {
			return encoding;
		}

		void init() {
			if (!inited) {
				try {
					PrintStream outWapper = new PrintStream(AnsiConsole.wrapOutputStream(out), false, encoding);
					PrintStream errWapper = new PrintStream(AnsiConsole.wrapOutputStream(err), false, encoding);
					ReflectUtils.setStaticFinalValueByFieldName(AnsiConsole.class, "out", outWapper);
					ReflectUtils.setStaticFinalValueByFieldName(AnsiConsole.class, "err", errWapper);

					this.inputer = new SystemInInputer(encoding, outWapper);
					inited = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
				AnsiConsole.systemInstall();
			}
		}

		public void error(String message) {
			if (level <= INFO_LEVEL_ERROR) {
				printLabel("ERROR", ConsoleColorEnum.RED, message);
			}

		}

		public void warning(String message) {
			if (level <= INFO_LEVEL_WARN && !silent) {
				printLabel("WARN ", ConsoleColorEnum.YELLOW, message);
			}
		}

		public void info(String message) {
			if (level <= INFO_LEVEL_INFO && !silent) {
				printLabel("INFO ", ConsoleColorEnum.GREEN, message);
			}
		}

		public void debug(String message) {
			if (level == INFO_LEVEL_DEBUG && !silent) {
				printLabel("DEBUG", ConsoleColorEnum.BLUE, message);
			}
		}

		public void important(String message) {
			String cmd = ansi().eraseLine().reset().fg(Color.WHITE).bold().bg(Color.RED).a("[IMPOR]").reset().fg(Color.RED)
					.a(" " + message).newline().reset().toString();
			run(cmd);
		}

		public void printLabel(String title, ConsoleColorEnum bgColor, String message) {
			//eraseScreen() 并不能清空背景色
//		String cmd = ansi().bg(Color.valueOf(bgColor.getName())).bold().a("["+title+"]").boldOff().bg(Color.BLACK)
//				.fg(Color.valueOf(bgColor.getName())).a(" " + message).newline().reset().toString();
			String cmd = ansi().eraseLine().reset().fg(Color.valueOf(bgColor.getName())).bold().a("["+title+"]").reset().fg(Color.DEFAULT)
					.a(" " + message).newline().reset().toString();
			run(cmd);
		}

		public void print(Object obj) {
			print(obj,ConsoleColorEnum.DEFAULT,ConsoleColorEnum.DEFAULT);
		}

		public void print(Object obj, ConsoleColorEnum fgcolor, ConsoleColorEnum bgcolor) {
			Ansi ansi = ansi();
			if (fgcolor != null) {
				ansi.fg(Color.valueOf(fgcolor.getName()));
				if (fgcolor.getBright()) {
					ansi.bold();
				}
			}
			if (bgcolor != null) {
				ansi.bg(Color.valueOf(bgcolor.getName()));
			}
			String cmd = ansi.a(obj)
//				.newline().cursorUp(1)
					.reset().toString();
			run(cmd);
		}

		public void println(Object obj) {
			println(obj, ConsoleColorEnum.DEFAULT, ConsoleColorEnum.DEFAULT);
		}

		public void printTable(List<List<Object>> rows,Object ... keyWords) {
			printTable(null,rows,false,keyWords);
		}

		public void printTable(List<String> headers, List<List<Object>> rows,Object ... keyWords) {
			printTable(headers,rows,true,keyWords);
		}

		public void printTable(List<String> headers, List<List<Object>> rows,boolean showBorder,Object ... keyWords) {
			List<Object> lightUps = new ArrayList<>();
			if (keyWords != null && keyWords.length > 0) {
				lightUps = Arrays.asList(keyWords);
			}

			Map<Integer,Integer> maxLengths = new HashMap<>();
			if (headers != null) {
				for (int i = 0; i < headers.size(); i++) {
					maxLengths.put(i,getLength(headers.get(i)));
				}
			}
			for (List<Object> row : rows) {
				for (int i = 0; i < row.size(); i++) {
					Object o = row.get(i);
					if (o == null) {
						continue;
					}
					if (o instanceof Date){
						Date date = (Date) o;
						o = DateUtils.toDateTimeString(date);
						row.set(i, o);
					}

					int length = getLength(o);
					if (!maxLengths.containsKey(i)|| length > maxLengths.get(i)) {
						maxLengths.put(i,length);
					}
				}
			}
			String space = "    ";
			StringBuilder builder = new StringBuilder();
			int total = 0;
			for (int i = 0; i < maxLengths.size(); i++) {
				Integer maxLength = maxLengths.get(i);
				maxLength = maxLength + 2;
				maxLengths.put(i,maxLength);
				total += maxLength;
			}
			if (headers != null) {
				builder.append(space);
				for (int i = 0; i < headers.size(); i++) {
					String header = headers.get(i);
					builder.append(fill(header,maxLengths.get(i),header,showBorder));
				}
				builder.append("\n");
				builder.append(StringExtUtils.fillStringLen(space,total,'-',0)).append("----").append("\n");
			}

			for (List<Object> row : rows) {
				builder.append(space);
				for (int i = 0; i < row.size(); i++) {
					Object o = row.get(i);
					String s = String.valueOf(o);
					if (lightUps.contains(o)) {
						s = ColoredStringUtils.colorString(s, ColoredStringUtils.COLOR_PURPLE);
					}
					String fill = fill(s, maxLengths.get(i),o,showBorder);
					builder.append(fill);
				}
				builder.append("\n");
			}
			print(builder.toString());
		}

		private int  getLength(Object value){
			if (value == null) {
				return "null".length();
			}
			String s = String.valueOf(value);
			int length = s.length();
			int i = StringExtUtils.countChineseCharacters(s);
			//一个中文按两个宽度算
			length = (length- i + i*2);

			if (s.contains("\u001B")) {
				length = length - 9;
			}
			return  length;
		}

		private String  fill(String value,int maxLength,Object o,boolean showBorder){
			int length = getLength(value);
			if (length >= maxLength) {
				return value;
			}
			String r = StringExtUtils.fillStringLen("", length, '+', 0);
			String s = StringExtUtils.fillStringLen(r, maxLength-1, ' ', (o != null && o instanceof Number)?1:0);
			s = s.replace(r,value);
			if (showBorder) {
				return s+"|";
			}
			return s;
		}

		public void outPrintln(Object obj) {
			run(String.valueOf(obj));
		}

		public void errPrintln(Object obj) {
			this.beep();
			println(obj, ConsoleColorEnum.RED, ConsoleColorEnum.DEFAULT);
		}

		public void println(Object obj, ConsoleColorEnum fgcolor, ConsoleColorEnum bgcolor) {
			Ansi ansi = ansi();
			ansi.reset();
			if (fgcolor != null) {
				ansi.fg(Color.valueOf(fgcolor.getName()));
				if (fgcolor.getBright()) {
					ansi.bold();
				}
			}
			if (bgcolor != null) {
				ansi.bg(Color.valueOf(bgcolor.getName()));
			}
			String cmd = ansi.a(obj).newline().reset().toString();
			run(cmd);
		}

		/**
		 * @param text
		 *            eg. "@|red Hello|@ @|green World|@"
		 */
		public void rander(String text) {
			String cmd = ansi().render(text).newline().reset().toString();
			run(cmd);
		}

		public void newline() {
			String cmd = ansi().newline().reset().toString();
			run(cmd);
		}

		public void clearLine() {
			clearLine(1);
		}

		public void clearLine(int lineCount) {
			if (lineCount <= 0) {
				return;
			}
			Ansi ansi = ansi();
			for (int i = 0; i < lineCount; i++) {
				ansi.eraseLine(Erase.ALL);
				if (i < lineCount - 1) {
					ansi.cursorUp(1);
				}
			}
			String cmd = ansi.a('\r').reset().toString();
			run(cmd);
			out.flush();
		}

		public void clearScreen() {
			out.print("\033[H\033[2J");
			out.flush();
		}

		public ProgressBarHandler createProgressBar() {
			Long threadId = Thread.currentThread().getId();
			Color c = Color.GREEN;
			if(!"Thread-0".equals(Thread.currentThread().getName())){
				c = colors[getColor(threadId)];
			}
			ProgressBarHandler handler = new ProgressBarHandler(c);
			return handler;
		}

		private final Color[] colors = {Color.YELLOW,Color.CYAN,Color.MAGENTA,Color.BLUE};
		private final AtomicInteger colorIdx = new AtomicInteger(0);
		private final Map<Long,Integer> map = new HashMap<>();

		private int getColor(Long id){
			if(!map.containsKey(id)){
				int i = colorIdx.getAndIncrement()%colors.length;
				map.put(id, i);
				return i;
			}
			return map.get(id);
		}

		protected void run(String cmd) {
			out.print(cmd);
		}

		public ConsoleIOProxy setLevel(int level) {
			this.level = level;
			return this;
		}

		public ConsoleIOProxy setSilent(boolean silent) {
			this.silent = silent;
			return this;
		}

		public class ProgressBarHandler {
			private final String joy1 = "┈┈Œ┈Œ┈Œ┈┈┈┈┈┈┈๏[-ิ_•ิ]๏┈┈┈┈┈┈┈";

			private final String f = "▧";

			private final String e = "☐";

			private int size = 10;
			private int progressing = -1;
			private int muti = -1;
			private String message = "处理中";

			private final Color color;

			private Long  start = null,end = null;

			private boolean recorded = false;


			ProgressBarHandler(Color color){
				this.color = color;
			}


			public void progress(String message){
				progressing = -1;
				muti = -1;
				if(StringExtUtils.isNotBlank(message)){
					this.message = message;
				}
				final String msg = this.message;
				new Thread() {
					@Override
					public void run() {
						int begin = 0;
						if (progressing == 0) {
							return;
						}
						progressing = 1;
						while (progressing == 1) {
							StringBuilder sb = new StringBuilder("[");
							for (int j = 0; j < size; j++) {
								int index = (begin + j) % joy1.length();
								sb.append(joy1.charAt(index));
							}
							sb.append("]").append(" ").append(msg);
							synchronized(ProgressBarHandler.class){
								clearLine();
								ConsoleIOProxy.this.run(Ansi.ansi().fg(color).a(sb.toString()).newline().cursorUp(1).reset().toString());
							}
							begin++;
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}.start();
			}

			public void progress(int step,int total,String message){
				String cost = "";
				end = System.currentTimeMillis();
				if (start == null) {
					start = end;
				}else {
					long avg = (end - start)/step;
					long left = avg * (total - step);
					if (recorded || left > 30 * 1000 ) {
						recorded = true;
						cost = "[avg:"+DateUtils.getTimeString(avg)+" left:"+ DateUtils.getTimeString(left)+"]";
					}
				}
				progressing = 0;
				muti = 0;
				if(StringExtUtils.isBlank(message)){
					message = this.message;
				}
				if (message.length() > 100) {
					message = message.substring(0,97) + "...";
				}
				this.message = "[" +step + "/"  + total + "]"+ cost+ message;
				muti = (step*size)/total;
				StringBuilder sb = new StringBuilder("[");
				for (int j = 0; j < muti; j++) {
					sb.append(f);
				}
				for (int j = 0; j < size - muti; j++) {
					sb.append(e);
				}
				sb.append("]").append(this.message);
				synchronized(ProgressBarHandler.class){
					clearLine();
					run(Ansi.ansi().fg(color).a(sb.toString()).newline().cursorUp(1).reset().toString());
				}
			}

			public void completed(String message){
				if(StringExtUtils.isNotBlank(message)){
					this.message = message;
				}else{
					this.message = "处理完成";
				}
				progressing = 0;
				synchronized(ProgressBarHandler.class){
					clearLine();
					StringBuilder sb = new StringBuilder("[");
					for (int i = 0; i < size; i++) {
						sb.append(f);
					}
					sb.append("] ").append(this.message);
					run(Ansi.ansi().fg(color).a(sb.toString()).reset().newline().toString());
				}
			}

			public void shutdown(String message){
				progressing = 0;
				if(StringExtUtils.isNotBlank(message)){
					this.message = message;
				}else{
					this.message = "处理中断";
				}
				StringBuilder sb = new StringBuilder("[");
				if(muti >-1){
					for (int j = 0; j < muti; j++) {
						sb.append(f);
					}
					for (int j = 0; j < size - muti; j++) {
						sb.append(e);
					}
					sb.append("]").append("[").append(muti*10).append("%]").append(this.message);
				}else{
					for (int i = 0; i < size; i++) {
						sb.append(f);
					}
					sb.append("]").append(this.message);
				}
				clearLine();
				println(sb.toString(),ConsoleColorEnum.RED,ConsoleColorEnum.DEFAULT);
			}
		}


		public void beep(){
			Toolkit.getDefaultToolkit().beep();
		}

		public PrintStream getOut() {
			return out;
		}

		public PrintStream getErr() {
			return err;
		}

		public Inputer getInputer() {
			return inputer;
		}

		public void setInputer(Inputer inputer) {
			this.inputer = inputer;
		}

		public String waittingInput(String msg,String ... expectInput){
			List<String> expects = null;

			String profix = ": ";
			if (expectInput != null && expectInput.length > 0) {
				expects = Arrays.asList(expectInput);
				expects = expects.stream().map(String::toUpperCase).collect(Collectors.toList());
				profix = ("["+String.join("/",expectInput)+"]: ");
			}
			String prompt = msg + profix;
			while (true){
				String input = this.inputer.waittingInput(prompt).trim();
				if (expects != null && !expects.contains(input.toUpperCase())) {
					this.error("输入非法");
					continue;
				}
				return input;
			}
		}

		public <T> T waittingChoose(Map<String,T> values, String message){
			if (values.size() == 1) {
				for (T value : values.values()) {
					return value;
				}
			}

			StringBuilder builder = new StringBuilder();
			int i= 0;
			Map<Integer,String> keys = new HashMap<>();
			println(message, ConsoleColorEnum.YELLOW,ConsoleColorEnum.DEFAULT);
			for (Map.Entry<String, T> entry : values.entrySet()) {
				builder.append(StringExtUtils.fillStringLen(i+"",String.valueOf(values.size()).length(),' ',0)).append(":  ")
						.append(entry.getKey()).append("\n");
				keys.put(i,entry.getKey());
				i++;
			}
			print(builder.toString(), ConsoleColorEnum.GREEN,ConsoleColorEnum.DEFAULT);

			while (true){
				String input = waittingInput("请输入对应选项编号,退出请输入-1 或回传").trim();
				if ("-1".equals(input) || StringExtUtils.isBlank(input)) {
					return null;
				}
				if (StringExtUtils.isNumeric(input) && keys.containsKey(Integer.parseInt(input))) {
					return values.get(keys.get(Integer.parseInt(input)));
				}
				error("输入非法:"+input);
			}
		}

		public <T> T waittingChoose(Collection<T> values, String message, Function<T,String> function){
			if (values.size() == 1) {
				return values.iterator().next();
			}
			Map<String,T> map = new HashMap<>();
			for (T value : values) {
				if (function != null) {
					map.put(function.apply(value),value);
				}else {
					map.put(String.valueOf(value),value);
				}

			}
			return waittingChoose(map,message);
		}

		public <T> T waittingChoose(Collection<T> values, String message){
			return waittingChoose(values,message,null);
		}

		public boolean waittingComfirm(String msg){
			String s = waittingInput(msg, "Y", "N");
			return s.equalsIgnoreCase("Y");
		}




	}
