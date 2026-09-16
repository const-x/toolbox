package idv.const_x.console.handler;

import idv.const_x.console.io.ConsoleIOProxy;
import idv.const_x.console.io.ConsoleIOProxy.ProgressBarHandler;
import idv.const_x.console.completer.FirstLimitedCompleter;
import idv.const_x.console.completer.StringTreeCompleter;
import idv.const_x.utils.StringExtUtils;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Attribute;
import org.fusesource.jansi.Ansi.Color;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DemoHandler extends AbsInputLineHandler {

	ThreadPoolExecutor pool;

	@Override
	public void handleLine(String line, ConsoleIOProxy console, Map<String, String> options, String ...params){
		String name = getParam(params,0);
		while (StringExtUtils.isBlank(name)) {
			name = console.waittingInput("请输入你的名字");
		}

		if ("constx".equalsIgnoreCase(name)) {
			ProgressBarHandler bar = console.createProgressBar();
			bar.progress(" welcome ");
			while (true){}
		}
		//信息级别
		console.println("打印演示");
		console.println("out： hello "+name);
		console.errPrintln("err：hello error");


		Map<String,Integer> levels = new TreeMap<>();
		levels.put("DEBUG", ConsoleIOProxy.INFO_LEVEL_DEBUG);
		levels.put("INFO", ConsoleIOProxy.INFO_LEVEL_INFO);
		levels.put("WARN", ConsoleIOProxy.INFO_LEVEL_WARN);
		levels.put("ERROR", ConsoleIOProxy.INFO_LEVEL_ERROR);
		Integer choose = console.waittingChoose(levels, "请选择日志打印级别");
		if (choose == null) {
			console.println("默认打印级别为INFO");
		}else {
			String lvl = null;
			for (Map.Entry<String, Integer> entry : levels.entrySet()) {
				if (Objects.equals(entry.getValue(),choose)) {
					lvl = entry.getKey();
				}
			}
			console.println("当前打印级别为:" + lvl);
			console.setLevel(choose);
		}

		console.debug("hello "+name);
		console.info("hello "+name);
		console.warning("hello "+name);
		console.error("hello "+name);

		console.important("importent hello "+name);

		//自定义
		console.println("自定义打印");
		console.rander("Hello @|yellow C|@ @|blue o|@ @|cyan l|@ @|green o|@ @|magenta r|@ @|red f|@ @|white u|@ @|yellow l|@  "+name);
		console.rander(Ansi.ansi().bg(Color.RED).fg(Color.GREEN).a("hello").reset().a(Attribute.UNDERLINE).a("world").toString());

		console.println("打印表格");
		console.warning("注意:推荐使用等宽字体(一个汉字算2个字宽),否则会出现表格对不齐的情况");
        List<String> headers = Arrays.asList("商品名","price");
		List<List<Object>> values = new ArrayList<>();
		values.add(Arrays.asList("apple",5));
		values.add(Arrays.asList("banana",10));
		console.printTable(headers,values,"apple",10);

		//擦除
		console.println("清屏演示");
		console.print("这是当前行内容 3秒后即将被清除");

		try {
			Thread.currentThread().sleep(3000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		console.clearLine();

		if (console.waittingComfirm("是否清除当前屏幕内容?")) {
			console.clearScreen();
		}

		//进度条
		console.println("进度条演示");
		ProgressBarHandler bar = console.createProgressBar();
		bar.progress("预加载3秒");
		Thread.currentThread();
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		bar.completed("加载完成");
		int p = 0;
		bar = console.createProgressBar();
		bar.progress(p,100, "进度处理");
		while(p < 100){
			Thread.currentThread();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			p+= 5;
			bar.progress(p,100,"处理第"+p/5+"步");
			if(p ==50 || p ==60){
				console.warning((70-p)/10+"秒后中断操作");
			}
			if(p > 60){
				bar.shutdown("任务中断");
				break;
			}
		}

		//多任务
		console.println("多任务进度条演示");

		if (pool == null) {
			pool = new ThreadPoolExecutor(
					3, 3, 1, TimeUnit.SECONDS,
					new LinkedBlockingQueue<Runnable>(30),
					new ThreadPoolExecutor.CallerRunsPolicy());
		}

		List<Future> futures = new ArrayList<>();
		futures.add(pool.submit(new ProgressThread(console,"子任务123")));
		futures.add(pool.submit(new ProgressThread(console,"子任务ABC")));
		futures.add(pool.submit(new ProgressThread(console,"子任务xyz")));

		for (Future future : futures) {
			try {
				future.get();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			} catch (ExecutionException e) {
				throw new RuntimeException(e);
			}
		}

		console.beep();
		console.info("所有任务完成");


	}

	@Override
	public String getCmd() {
		return "hello";
	}

	@Override
	public HelpBuilder initHelpBuilder() {
		return new HelpBuilder(getCmd(),"交互demo")
				.appendParamDesc("name","你的名字",false)
				.build();
	}


	@Override
	public FirstLimitedCompleter initCompleter(){
		return null;
	}

	class ProgressThread implements Callable {
		ConsoleIOProxy console;
		String name;
		ProgressThread(ConsoleIOProxy console, String name ){
			this.console = console;
			this.name = name;
		}


		@Override
		public Object call() throws Exception {
			int p = 0;
			ProgressBarHandler bar = console.createProgressBar();
			bar.progress(p,100, name);
			while(p < 100){
				Thread.currentThread();
				try {
					long s = (long) (1000*Math.random());
					Thread.sleep(s);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				p+= Math.random()*10;
				//console.info("当前任务" + name +"已完成:" + p);
				bar.progress(p,100,"当前任务" + name +"已完成:" + p);
			}
			bar.completed(name + " 已完成");
			return null;
		}
	}

}


