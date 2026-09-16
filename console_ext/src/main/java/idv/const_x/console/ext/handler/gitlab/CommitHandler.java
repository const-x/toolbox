package idv.const_x.console.ext.handler.gitlab;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import idv.const_x.console.handler.HelpBuilder;
import idv.const_x.console.io.ConsoleIOProxy;
import idv.const_x.utils.DateUtils;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommitHandler extends AbsGitLabHandler {

	public CommitHandler(IGitLabInfoProvider gitLabInfoProvider) {
		super(gitLabInfoProvider);
	}

	@Override
	public void handleLine(String line, ConsoleIOProxy console, Map<String, String> options, String... params) {
		String project = getParam(params,0);
		String iid = getParam(params,1);


		String s = GitlabInvoker.commitDetail(gitLabInfoProvider,project, iid);
		JSONObject obj = null;
		try {
			obj = JSON.parseObject(s);
		}catch (Exception e){
			console.error(s);
			return;
		}
		super.echoResult(console,"commit:["+obj.getString("short_id") + "] " +  obj.getString("title") + " " + obj.getString("author_name")+ " " + obj.getString("created_at") );

		s = GitlabInvoker.commitMrs(gitLabInfoProvider,project, iid);

		JSONArray array = null;
		try {
			array = JSON.parseArray(s);
		}catch (Exception e){
			console.error(s);
			return;
		}

		JSONArray res = new JSONArray();


		List<JSONObject> collect = array.stream().map(x -> (JSONObject)x).collect(Collectors.toList());

		collect.sort(new Comparator<JSONObject>() {
			@Override
			public int compare(JSONObject o1, JSONObject o2) {
				Date date1 = o1.getDate("created_at");
				Date date2 = o2.getDate("created_at");
				return date1.before(date2)?-1:1;
			}
		});

		for (int i = 0; i < collect.size(); i++) {
			JSONObject object = collect.get(i);
			String state = object.getString("state");
			String timeField = "created_at";
			String byField = "author";
			if ("closed".equals(state)) {
				timeField = "closed_at";
				byField ="closed_by";
				continue;
			}else if ("merged".equals(state)) {
				timeField = "merged_at";
				byField ="merged_by";
			}

			JSONObject r = new JSONObject(true);
			r.put("title  ",object.getString("title"));
			r.put("branch ",object.getString("source_branch") + " -> " + object.getString("target_branch"));
			r.put("author ",object.getJSONObject("author").getString("name") + " at " + DateUtils.toDateTimeString(object.getDate("created_at")) + " [" + state
					+ " at " + DateUtils.toDateTimeString(object.getDate(timeField))
					+ " by " + object.getJSONObject(byField).getString("name")+"]"
			);
			r.put("request",object.getString("web_url"));

			res.add(r);
			if (!super.hasOptions(options,"f")) {
				break;
			}
		}

		super.echoResult(console,"mr    :"+res.toString(SerializerFeature.PrettyFormat));
	}

	@Override
	public String getCmd() {
		return "commit";
	}


	public HelpBuilder initHelpBuilder() {
		return new HelpBuilder(getCmd(),"查询commit信息")
				.appendParamDesc(new HelpBuilder.Param("project","项目名称").addSelecet(gitLabInfoProvider.getProjects()))
				.appendParamDesc("iid","commit id或shortId")
				.appendOptionDesc("f","展示所有相关的合并记录")
				.build();
	}

}

