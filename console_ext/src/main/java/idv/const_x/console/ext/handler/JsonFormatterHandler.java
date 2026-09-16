package idv.const_x.console.ext.handler;

import idv.const_x.console.handler.AbsInputLineHandler;
import idv.const_x.console.handler.HelpBuilder;
import idv.const_x.console.io.ConsoleIOProxy;
import idv.const_x.utils.OSUtils;

import java.net.URLEncoder;
import java.util.Collections;
import java.util.Map;

public class JsonFormatterHandler extends AbsInputLineHandler {

	@Override
	public void handleLine(String line, ConsoleIOProxy console, Map<String, String> options, String ...params){
		String url = "https://const-x.github.io/htmls/json/JSONFormater.htm";
		String param = getParam(params, 0);

		if (isNotBlank(param)) {
			url += ("?json="+ URLEncoder.encode(param));
		}else if(hasOptions(options,"c")){
			param = OSUtils.getSysClipboardText();
			if (isNotBlank(param)) {
				url += ("?json="+ URLEncoder.encode(param));
			}
		}
		try {
			OSUtils.browse(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean isNotBlank(String s){
		return s !=null && !s.trim().equals("");
	}

	@Override
	public String getCmd() {
		return "json";
	}

	public HelpBuilder initHelpBuilder() {
		return new HelpBuilder(getCmd(),"打开JSON格式化页面")
				.appendOptionDesc("c","复制粘贴板内容")
				.build();
	}


	public static void main(String[] args) {
		new JsonFormatterHandler().handleLine("", ConsoleIOProxy.getInstance("UTF-8"), Collections.singletonMap("c",null),"[]");
	}

}

