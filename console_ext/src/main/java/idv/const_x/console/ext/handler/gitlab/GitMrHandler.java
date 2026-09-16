package idv.const_x.console.ext.handler.gitlab;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import idv.const_x.console.completer.FirstLimitedCompleter;
import idv.const_x.console.completer.StringTreeCompleter;
import idv.const_x.console.handler.HelpBuilder;
import idv.const_x.console.io.ConsoleIOProxy;
import idv.const_x.utils.StringExtUtils;

import java.util.Map;
import java.util.Objects;

public class GitMrHandler extends AbsGitLabHandler {


	public GitMrHandler(IGitLabInfoProvider gitLabInfoProvider) {
		super(gitLabInfoProvider);
	}

	@Override
	public String getCmd() {
		return "mr";
	}


	@Override
	public void handleLine(String s, ConsoleIOProxy console, Map<String, String> options, String... params) {
		String project = getParam(params,0);
		String iid = getParam(params,1);
        if (StringExtUtils.isBlank(project)) {
			console.error("project 不能为空");
			return;
        }
		if (StringExtUtils.isBlank(iid)) {
			console.error("合并请求ID 不能为空");
			return;
		}

		try {
			String mr = GitlabInvoker.getMr(gitLabInfoProvider,project, iid);
            if (Objects.equals("404:Not Found",mr)) {
				console.error("未找到对应mr 请确认项目["+project+"] 或mr请求ID["+iid+"]是否正确");
				return;
            }
			JSONObject parsed = JSON.parseObject(mr);

			console.info("共 " + parsed.getString("changes_count")+" 个变更 对应commits:");
			JSONArray objects = JSON.parseArray(GitlabInvoker.mrCommits(gitLabInfoProvider,project, iid));
			for (int i = 0; i < objects.size(); i++) {
				console.println("  - " + objects.getJSONObject(i).getString("title"));
			}

			String state = parsed.getString("state");
			if (Objects.equals("merged",state)) {
				console.warning("当前请求已经被合并 by " + parsed.getJSONObject("merged_by").getString("name") + " at " + parsed.getString("merged_at"));
				return;
			}
			if (Objects.equals("closed",state)) {
				console.warning("当前请求已经被关闭 by " + parsed.getJSONObject("closed_by").getString("name") + " at " + parsed.getString("closed_at"));
				return;
			}

			String mergeStatus = parsed.getString("merge_status");
			if (!Objects.equals("can_be_merged",mergeStatus)) {
				console.warning("当前请求不允许合并 请检查是否存在冲突 ");
				return;
			}
            if (!console.waittingComfirm("确认合并:" + parsed.getString("title") + " 到分支:" + parsed.getString("target_branch"))) {
				return;
            }
			String token = gitLabInfoProvider.getGitMrToken(project);
            if (hasOptions(options,"s")) {
				token = gitLabInfoProvider.getGitToken(project);
            }
            if (StringExtUtils.isBlank(token)) {
                throw new IllegalArgumentException("没有配置合并代码对应token");
            }

			String approveMrStatus = GitlabInvoker.approveMrStatus(gitLabInfoProvider,project, iid);
			JSONObject status = JSON.parseObject(approveMrStatus);
			Integer approvalsLeft = status.getInteger("approvals_left");
			if (approvalsLeft != null && approvalsLeft > 0) {
				String approveMr = GitlabInvoker.approveMr(gitLabInfoProvider,project, iid, token);
				if (validate(console, approveMr)){
					return;
				}
			}else {
				JSONArray approvedBy = status.getJSONArray("approved_by");
                if (approvedBy != null && approvedBy.size() > 0) {
					console.warning("当前请求已经被审核 by " + approvedBy.getJSONObject(0).getJSONObject("user").getString("name") + " at " + status.getString("updated_at"));
                }
			}


			String acceptMr = GitlabInvoker.acceptMr(gitLabInfoProvider,project, iid, token);
			if (validate(console, acceptMr)){
				return;
			}
			console.info("合并成功");
			return;

		}catch (Exception e){
			console.error(e.getMessage());
		}

	}

	private static boolean validate(ConsoleIOProxy console, String acceptMr) {
		if (acceptMr.startsWith("401")) {
			console.error("指定合并人无当前代码操作权限");
			return true;
		}
		if (acceptMr.startsWith("406")) {
			console.error("merge request is already merged or closed");
			return true;
		}
		if (acceptMr.startsWith("405")) {
			console.error("it has some conflicts and can not be merged");
			return true;
		}
		if (acceptMr.startsWith("40")) {
			console.error(acceptMr);
			return true;
		}
		return false;
	}

	@Override
	public FirstLimitedCompleter initCompleter(){
		StringTreeCompleter completer = new StringTreeCompleter(getCmd());
		completer.addSubStrings(1, gitLabInfoProvider.getProjects());
		return completer;
	}

	@Override
	public HelpBuilder initHelpBuilder() {
		return new HelpBuilder(getCmd(),"代码合并")
				.appendParamDesc(new HelpBuilder.Param("project","项目名称").addSelecet(gitLabInfoProvider.getProjects()))
				.appendParamDesc("iid","mr请求Id",false)
				.appendOptionDesc("s","当前作为审核人");
	}


}

