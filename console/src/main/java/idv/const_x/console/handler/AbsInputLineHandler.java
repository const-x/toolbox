package idv.const_x.console.handler;

import idv.const_x.console.io.ConsoleColorEnum;
import idv.const_x.console.io.ConsoleIOProxy;
import idv.const_x.console.completer.FirstLimitedCompleter;
import idv.const_x.console.completer.StringTreeCompleter;
import idv.const_x.utils.StringExtUtils;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbsInputLineHandler implements InputLineHandler{

	protected  HelpBuilder builder = null;

	protected FirstLimitedCompleter completer = null;

	protected boolean hasRequiredParams = false;


	public AbsInputLineHandler() {
		init();
	}

	protected void init(){
		if (builder != null) {
			return;
		}
		builder = initHelpBuilder();
		completer = initCompleter();
		if (completer == null) {
			List<HelpBuilder.Arg> args = builder.getArgs();
			if (args.size() > 0) {

				StringTreeCompleter stringTreeCompleter = new StringTreeCompleter(getCmd());
				int counter = 1;
				for (HelpBuilder.Arg arg : args) {
					if (arg instanceof HelpBuilder.Option) {
						HelpBuilder.Option option = (HelpBuilder.Option) arg;
						String key = "-" + option.getName();
						if (option.valueRequired) {
							key = key + ":";
						}
						if (StringExtUtils.isNotBlank(arg.desc)) {
							//用于展示说明信息
							stringTreeCompleter.addFreeStrings(Collections.singletonMap(key,arg.desc));
						}else {
							stringTreeCompleter.addFreeStrings(key);
						}
						if (option.selects.size() > 0) {
							for (String select : option.selects) {
								stringTreeCompleter.addFreeStrings(key + select);
							}
						}
					}else {
						if (arg.selects.size() > 0) {
							stringTreeCompleter.addSubStrings(counter,arg.selects);
						}else {
                            if (StringExtUtils.isNotBlank(arg.desc)) {
								//用于展示说明信息
								stringTreeCompleter.addSubStrings(counter, Collections.singletonMap("",arg.desc));
                            }
						}
						counter ++;
					}
					if (arg.required) {
						hasRequiredParams = true;
					}
				}


				completer = stringTreeCompleter;
			}
		}
	}

	public void handleLine(ConsoleIOProxy console, String ...args){
		if (args.length == 1) {
			if (showHelpWhenParamNul()) {
				console.println(getHelp());
				return;
			}
		}
		if (args.length > 1) {
			for (String arg : args) {
				if(arg.equals("?") || arg.equals("-?") || arg.equals("？") || arg.equals("-？")){
					console.println(getHelp());
					return;
				}
			}
		}
		List<String> params = new ArrayList<>();
		Map<String,String> options = new HashMap<>();
		if(args.length > 1){
			for (int i = 1; i < args.length; i++) {
				String arg = args[i];
				if (arg.startsWith("-")) {
					arg = arg.substring(1);
					if (arg.contains(":")) {
						int index = arg.indexOf(":");
						String s = arg.substring(0, index);
						options.put(s,arg.substring(index+1));
					}else {
						options.put(arg,null);
					}
				}else {
					if (StringExtUtils.isNotBlank(arg)) {
						params.add(arg);
					}
				}
			}
		}

		String line = String.join(" ", args);
		if (params.isEmpty() && options.isEmpty() ) {

		}
		handleLine(line,console,options, params.toArray(new String[0]));
	}

	public abstract String getCmd();

	public String getHelp(){
		return builder.getHelp();
	}

	@Override
	public String getDesc() {
		return builder.getDesc();
	}

	public boolean enable(){
		return true;
	}

	public boolean showHelpWhenParamNul(){
		return hasRequiredParams;
	}

	public FirstLimitedCompleter initCompleter(){
		return null;
	}


	public abstract void handleLine(String line, ConsoleIOProxy console, Map<String, String> options, String... params);

	public abstract HelpBuilder initHelpBuilder();


	protected String getParam(String[] params,int index){
		HelpBuilder.Param p = null;
		try {
			p = builder.getParams().get(index);
		}catch (Exception e){

		}
		if (params == null || params.length <= index ) {
			if ( p != null && p.required) {
				throw new IllegalArgumentException("第"+(index+1)+"个参数 <"+p.name+"> 不能为空");
			}
			return null;
		}
		String s = params[index];
		if (StringExtUtils.isBlank(s) && p != null &&  p.required) {
			throw new IllegalArgumentException("第"+(index+1)+"个参数 <"+p.name+"> 不能为空");
		}
		return s.trim();
	}



	protected String getOptions(Map<String, String> options,String key){
		HelpBuilder.Option option = builder.getOptions().get(key);
		if (options == null || !options.containsKey(key)) {
			if (option != null && option.required) {
				throw new IllegalArgumentException("选项 <"+option.name+"> 不能为空");
			}
			return null;
		}
		String s = options.get(key);
		if (StringExtUtils.isBlank(s) && option != null &&  option.valueRequired) {
			throw new IllegalArgumentException("选项 <"+option.name+"> 值不能为空");
		}
		return s;
	}

	protected boolean hasOptions(Map<String, String> options,String key){
		if (options != null && options.containsKey(key)) {
			return true;
		}
		return false;
	}

	protected void echoResult(ConsoleIOProxy console,String res){
		console.println(res, ConsoleColorEnum.GREEN,ConsoleColorEnum.DEFAULT);
	}


	public FirstLimitedCompleter getCompleter() {
		return completer;
	}

	public HelpBuilder getHelpBuilder() {
		return builder;
	}


}
