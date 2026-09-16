package idv.const_x.swing.text.handler;

import idv.const_x.swing.text.param.ComboBoxParamComponent;
import idv.const_x.swing.text.param.TextInputParamComponent;
import idv.const_x.utils.StringExtUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class LineMergeHandler extends AbsHandler{

	public LineMergeHandler() {
		TextInputParamComponent split = new TextInputParamComponent("split");
		split.setDescription("原始分隔符");
		split.setDefaultValue(",");
		super.addParamComponent(split);

		ComboBoxParamComponent<Integer> detail = new ComboBoxParamComponent("detail");
		detail.setDescription("展示明细");
		detail.addItem("所有",0);
		detail.addItem("相同项",1);
		detail.addItem("差异项",2);
		detail.addItem("差异项:源",21);
		detail.addItem("差异项:对比",22);
		detail.setDefaultValue(0);
		super.addParamComponent(detail);
		
		TextInputParamComponent source = new TextInputParamComponent("source");
		source.setDescription("对比数据");
		source.setRows(29);
		source.setCols(60);
		super.addParamComponent(source);


	}
		
	@Override
	public void handle(String input, Map<String, Object> params) {
		String split = (String) params.get("split");
		String source = (String) params.get("source");
		List<String> a = this.getLines(input, split);
		super.printLog("源项数量:" + a.size());
		List<String> b = this.getLines(source, split);
		super.printLog("对比项数量:" + b.size());
		
		List<String> c = new ArrayList<>();
		Iterator<String> iter = a.iterator();  
		while(iter.hasNext()){  
		    String s = iter.next();  
		    if(b.contains(s)){  
		        iter.remove();
		        b.remove(s);
		        c.add(s);
		    }  
		}

		StringBuilder sb2 = new StringBuilder();

		int type = (int) params.get("detail");
        if (type == 0 || type == 1) {
			sb2.append("------ 相同项(").append(c.size()).append(") ------\n");
			for (String string : c) {
				sb2.append(string).append("\n");
			}
        }

		if (type == 2 || type == 21|| type == 0) {
			sb2.append("\n------ 源独有项(").append(a.size()).append(") ------\n");
			for (String string : a) {
				sb2.append(string).append("\n");
			}
		}

		if (type == 2 || type == 22|| type == 0) {
			sb2.append("\n------ 对比独有项(").append(b.size()).append(") ------\n");
			for (String string : b) {
				sb2.append(string).append("\n");
			}
		}
		String desc = "相同项:"+c.size() + "  源独有项:"+ a.size() + "  对比独有项:"+ b.size() + "  合计项:"+ (a.size()+b.size()+c.size());
		super.print(sb2.toString(),desc);
    }
		
	
	private List<String>  getLines(String s,String split){
		List<String> lines = new ArrayList<>();
		if(StringExtUtils.isBlank(s)){
			return lines;
		}
		String[] res = s.split("\n");
		for (String string : res) {
			string = string.trim();
			if(StringExtUtils.isNotBlank(split)){
				if(string.startsWith(split)){
					string = string.substring(1);
				}
				if(string.endsWith(split)){
					string = string.substring(0,string.length() - 1);
				}
				String[] ids = string.split(split);
				for (String string2 : ids) {
					lines.add(string2);
				}
			}else{
				lines.add(string);
			}
		}
		return lines;
	}

	

	@Override
	public String getName() {
		return "合并";
	}

	@Override
	public String getDescription() {
		return "合并两组数据 并列出差异项";
	}

}
