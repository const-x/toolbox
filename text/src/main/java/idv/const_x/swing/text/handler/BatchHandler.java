package idv.const_x.swing.text.handler;

import idv.const_x.swing.text.param.BooleanParamComponent;
import idv.const_x.swing.text.param.ComboBoxParamComponent;
import idv.const_x.swing.text.param.ComboBoxParamComponent.IChangeListener;
import idv.const_x.swing.text.param.TextInputParamComponent;
import idv.const_x.utils.ListSpliter;
import idv.const_x.utils.StringExtUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class BatchHandler extends AbsHandler{

	public BatchHandler() {

		TextInputParamComponent limitPerLine = new TextInputParamComponent("limitPerLine");
		limitPerLine.setDescription("每行项目数");
		limitPerLine.setDefaultValue("10");
		super.addParamComponent(limitPerLine);
		
		TextInputParamComponent maxIn = new TextInputParamComponent("maxIn");
		maxIn.setDescription("每组项目数");
		maxIn.setDefaultValue("10000");
		super.addParamComponent(maxIn);
		
		TextInputParamComponent split = new TextInputParamComponent("split");
		split.setDescription("原始分隔符");
		split.setDefaultValue(",");
		super.addParamComponent(split);
		
		ComboBoxParamComponent<Integer> isString = new ComboBoxParamComponent<Integer>("isString");
		isString.setDescription("加工方式");
		isString.setDefaultValue(0);
		isString.addItem("加L", 2);
		isString.addItem("加''（单引号）", 1);
		isString.addItem("加\"\"（双引号）", 4);
		isString.addItem("批量替换", 3);
		isString.addItem("无", 0);
		super.addParamComponent(isString);

		TextInputParamComponent join = new TextInputParamComponent("join");
		join.setDescription("连接符");
		join.setDefaultValue(",");
		super.addParamComponent(join);

		BooleanParamComponent rmMuti = new BooleanParamComponent("rmMuti");
		rmMuti.setDescription("是否去重");
		rmMuti.setDefaultValue(true);
		super.addParamComponent(rmMuti);
		
		BooleanParamComponent alignment = new BooleanParamComponent("alignment");
		alignment.setDescription("是否对齐");
		alignment.setDefaultValue(true);
		super.addParamComponent(alignment);
		
		BooleanParamComponent sort = new BooleanParamComponent("sort");
		sort.setDescription("是否排序");
		sort.setDefaultValue(false);
		super.addParamComponent(sort);
		
		TextInputParamComponent text = new TextInputParamComponent("text");
		text.setDescription("处理模板");
		text.setRows(10);
		text.setDefaultValue("行 A,b,c 用模板 ${0}${2} 将会得到 Ac");
		text.setVisable(false);
		super.addParamComponent(text);
		
		
		isString.addChangeListener(new IChangeListener<Integer>(){
			@Override
			public void onChange(Integer orgi, Integer selected) {
				if(3 == selected){
					text.setVisable(true);
					limitPerLine.setValue("1");
				}else{
					text.setVisable(false);
				}
				
			}
			
		});
	}
	

	@Override
	public void handle(String input, Map<String, Object> params) {
    	String s = input;
		String limitStr = params.get("limitPerLine").toString();
		String text = params.get("text").toString();
		boolean alignment = (boolean) params.get("alignment");
		boolean sort = (boolean) params.get("sort");
		
		
		int limitPerLine = StringExtUtils.isBlank(limitStr)? 10: Integer.valueOf(limitStr);
		String maxInStr = params.get("maxIn").toString();
		int maxIn = StringExtUtils.isBlank(maxInStr)? 100000: Integer.valueOf(maxInStr);
		int isString = (int) params.get("isString");
		String split = (String) params.get("split");
		String join = (String) params.get("join");
		boolean rmMuti = (boolean) params.get("rmMuti");
		
		Set<String> all = new HashSet<>();
		Map<String,Integer> muti = new HashMap<>();
		String[] res = s.split("\n");
		List<String> list = new ArrayList<>();
		int maxLen = 0;
		int total = 0;
		
		for (String string : res) {
			string = string.trim();
			if(string.startsWith(split)){
				string = string.substring(1);
			}
			if(string.endsWith(split)){
				string = string.substring(0,string.length() - 1);
			}
			String[] ids = string.split(split);
			if(isString == 3){
				String temp = text;
				for (int i = 0; i < ids.length; i++) {
					String id = ids[i];
					String symb = "${"+i+"}";
					if(id == null){
						id = "";
					}else{
						id =id.trim();
					}
					if(temp.contains(symb)){
						temp = temp.replace(symb, id);
					}
					
				}
				list.add(temp);
				total++;
			}else{
				for (String id : ids) {
					if(StringExtUtils.isNotBlank(id)){
						if(all.contains(id)){
							if(muti.containsKey(id)){
								muti.put(id, muti.get(id) + 1);
							}else{
								muti.put(id, 1);
							}
							if(rmMuti){
								continue;
							}
						}
						
						all.add(id);
						
						String f = id;
						if(isString == 1){
							f = "'"+id+"'";
						}else if(isString == 2){
							f = id+"L";
						}else if(isString == 4){
							f = "\""+id+"\"";
						}
						list.add(f);
						if(f.length() > maxLen){
							maxLen = f.length();
						}
						total++;
					}
				}
			}
		}
		
		if(sort){
			list.sort(new Comparator<String>() {

				@Override
				public int compare(String o1, String o2) {
					return o1.compareTo(o2);
				}
			});
		}
		
		StringBuilder sb = new StringBuilder();
		ListSpliter<String> inSpliter = new ListSpliter<>(list,maxIn);
	    while (inSpliter.hasNext()) {
			List<String> in = inSpliter.next();
		    ListSpliter<String> lineSpliter = new ListSpliter<>(in,limitPerLine);
		    while (lineSpliter.hasNext()) {
				for (String str : lineSpliter.next()) {
					if(alignment){
						sb.append(StringExtUtils.fillStringLen(str, maxLen,' ',0)).append(join);
					}else{
						sb.append(str).append(",");
					}
				}
				sb.append("\n");
			}
		    sb= sb.deleteCharAt(sb.lastIndexOf("\n"));
		    sb= sb.deleteCharAt(sb.lastIndexOf(join));
		    sb.append("\n\n");
		}
		
	
		
		StringBuilder sb2 = new StringBuilder();
		sb2.append("合计:").append(total).append("项, 其中").append(all.size()).append("个有效项 ").append(muti.size()).append("个项存在重复").append("\n");
		if(!muti.isEmpty()){
			sb2.append("+---重复项---+--重复出现次数--+\n");
			for (Entry<String, Integer> entry : muti.entrySet()) {
				sb2.append(entry.getKey()).append(" : ").append(entry.getValue()).append("\n");
			}
		}
		super.print(sb.toString(),sb2.toString());
    }
		

	

	@Override
	public String getName() {
		return "批处理";
	}

	@Override
	public String getDescription() {
		return "文本行数据批量处理";
	}

}
