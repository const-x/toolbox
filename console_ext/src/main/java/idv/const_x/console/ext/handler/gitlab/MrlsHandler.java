package idv.const_x.console.ext.handler.gitlab;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import idv.const_x.console.completer.FirstLimitedCompleter;
import idv.const_x.console.completer.StringTreeCompleter;
import idv.const_x.console.handler.HelpBuilder;
import idv.const_x.console.io.ConsoleIOProxy;
import idv.const_x.utils.ColoredStringUtils;
import idv.const_x.utils.DateUtils;
import idv.const_x.utils.StringExtUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class MrlsHandler extends AbsGitLabHandler {


	public MrlsHandler(IGitLabInfoProvider gitLabInfoProvider) {
		super(gitLabInfoProvider);
	}

	@Override
	public String getCmd() {
		return "mrls";
	}


	@Override
	public FirstLimitedCompleter initCompleter(){
		StringTreeCompleter completer = new StringTreeCompleter(getCmd());
		completer.addSubStrings(1, gitLabInfoProvider.getProjects());
		List<String> branchs = gitLabInfoProvider.getBranchs();
		completer.addSubStrings(2,branchs);
		completer.addSubStrings(3,"-k:", "-t:", "-s:",  "-e:'"+ DateUtils.toDateTimeString(new Date())+"'",
				"-d:1","-b:'"+ DateUtils.toDateTimeString(DateUtils.getDateAfter(-1,new Date()))+"'");
		return completer;
	}

	@Override
	public void handleLine(String line, ConsoleIOProxy console, Map<String, String> options, String... params) {
		String project = getParam(params,0);
		String branch = getParam(params,1);
		String file = null;
		if (params.length >= 3) {
			file = params[2];
		}
		List<String> fileList = null;
		if (StringExtUtils.isNotBlank(file)) {
			fileList = Arrays.asList(file.split(","));
		}

		String keywords = options.getOrDefault("k", null);
		String title = options.getOrDefault("t", null);
		String sourcebranch = options.getOrDefault("s", null);
		int day = options.containsKey("d")?Integer.parseInt(options.get("d")):1;
		String beginTime = options.getOrDefault("b", null);
		if (beginTime !=null &&beginTime.contains("T")) {
			beginTime = beginTime.replace("T"," ");
		}
		String endTime = options.getOrDefault("e", null);
		if (endTime !=null && endTime.contains("T")) {
			endTime = endTime.replace("T"," ");
		}
		getBranchChanges(project,branch,title,keywords,sourcebranch,day,beginTime,endTime,console,fileList,options);
	}


	/**
	 * 获取分支最近变更
	 * @param project 项目
	 * @param branch  指定分支
	 * @param keywords    commit关键字
	 * @param sourcebranch 合并来源分支
	 * @param file    指定文件
	 * @param day  按当前时间往前推移量 作为beginTime
	 * @param beginTime 开始时间
	 * @param endTime  结束时间
	 */
	public  void getBranchChanges(String project,String branch,String titleKey,String keywords,String sourcebranch,Integer day,String beginTime,String endTime,ConsoleIOProxy console,List<String> fileList,Map<String, String> options) {
		ConsoleIOProxy.ProgressBarHandler progressBarHandler = null;
		try {
			if (beginTime !=null &&beginTime.contains("T")) {
				beginTime = beginTime.replace("T"," ");
			}
			if (endTime !=null && endTime.contains("T")) {
				endTime = endTime.replace("T"," ");
			}

			//System.out.println(String.join(" ",project,branch,keywords,String.valueOf(day),file));
			progressBarHandler = console.createProgressBar();
			progressBarHandler.progress("数据请求中");

			if (StringExtUtils.isBlank(beginTime)) {
				beginTime = DateUtils.getDateTimeAfter(-day,DateUtils.getNowDateTime());
			}
			console.info(beginTime + " to " + endTime);
			String merged = GitlabInvoker.mrList(gitLabInfoProvider,project, sourcebranch, branch, "merged", titleKey, beginTime, endTime);

			JSONArray array = JSON.parseArray(merged);
			if (array == null|| array.size() == 0) {
				progressBarHandler.completed("处理完成");
				System.out.println("[]");
				return;
			}

			StringBuilder all = new StringBuilder();
			List<String> commitIds = new ArrayList<>();
			int counter = 1;
			for (int i = 0; i < array.size(); i++) {
				boolean containsFile = false;
				StringBuilder builder = new StringBuilder();
				JSONObject jsonObject = array.getJSONObject(i);
				String title = jsonObject.getString("title");
				title = title.replace("\n"," ");
				if (title.length() > 100) {
					title = title.substring(0,97) + "...";
				}
				progressBarHandler.progress(i,array.size(),"查找合并:"+title);
				builder.append("[32m").append(jsonObject.getString("title") ).append(" from:").append(jsonObject.getString("source_branch")).append(" by:").append(jsonObject.getJSONObject("author").getString("name")).append(" ").append(jsonObject.getString("merged_at")).append("\u001b[0m").append("\n");
				builder.append("\t web_url: ").append(jsonObject.getString("web_url")).append("\n");


				String commitStr = GitlabInvoker.mrCommits(super.gitLabInfoProvider,project, jsonObject.getString("iid"));
				JSONArray commits = JSON.parseArray(commitStr);
				for (int i1 = 0; i1 < commits.size(); i1++) {
					JSONObject commit = commits.getJSONObject(i1);
					String sha = commit.getString("id");
					String shortId = commit.getString("short_id");
					if (commitIds.contains(shortId)) {
						continue;
					}
					commitIds.add(shortId);

					StringBuilder commitBuilder = new StringBuilder();
					boolean containsCommit = false;
					String title2 = commit.getString("title");
					title2 = title2.replace("\n"," ");
					commitBuilder.append("\t commit[").append(shortId).append("]:").append(title2).append(" by:").append(commit.getString("committer_name")).append(" ").append(commit.getString("committed_date")).append("\n");
					String changeStr = GitlabInvoker.commitChanges(gitLabInfoProvider,project, sha);
					JSONArray changes = JSON.parseArray(changeStr);
					for (int i2 = 0; i2 < changes.size(); i2++) {
						JSONObject change = changes.getJSONObject(i2);
						String new_path = change.getString("new_path");
						String text = getChangeText(change);


						if (fileList == null && keywords == null) {
							commitBuilder.append("\t\t").append(text).append("\n");
							containsCommit = true;
							continue;
						}

						String found = null;
						if (fileList != null ) {
							for (String file : fileList) {
								if (new_path.contains(file)) {
									found = file;
									break;
								}
							}
                            if (found == null) {
								continue;
                            }
						}
						if (keywords !=null) {
							String diff = change.getString("diff");
							if (!diff.contains(keywords)) {
								continue;
							}
						}

						containsCommit = true;
						commitBuilder.append("\t\t").append(ColoredStringUtils.lightUpWords(text,found )).append("\n");
						if (keywords != null) {
							String diff = change.getString("diff");
							if (diff.contains(keywords)) {
								String[] lines = diff.split("\n");
								// 计算每一行对应的行号：[0]=旧文件行号, [1]=新文件行号，-1表示无效（如@@行）
								int[][] lineNums = new int[lines.length][2];
								int oldLine = 0, newLine = 0;
								java.util.regex.Pattern hunkPattern = java.util.regex.Pattern.compile("@@ -(\\d+)(?:,\\d+)? \\+(\\d+)(?:,\\d+)? @@");
								for (int li = 0; li < lines.length; li++) {
									String l = lines[li];
									if (l.startsWith("@@")) {
										java.util.regex.Matcher m = hunkPattern.matcher(l);
										if (m.find()) {
											oldLine = Integer.parseInt(m.group(1));
											newLine = Integer.parseInt(m.group(2));
										}
										lineNums[li][0] = -1;
										lineNums[li][1] = -1;
									} else if (l.startsWith("+")) {
										lineNums[li][0] = -1;
										lineNums[li][1] = newLine++;
									} else if (l.startsWith("-")) {
										lineNums[li][0] = oldLine++;
										lineNums[li][1] = -1;
									} else {
										lineNums[li][0] = oldLine++;
										lineNums[li][1] = newLine++;
									}
								}
								Set<Integer> printedLines = new LinkedHashSet<>();
								for (int li = 0; li < lines.length; li++) {
									if (lines[li].contains(keywords)) {
										for (int j = Math.max(0, li - 2); j <= Math.min(lines.length - 1, li + 2); j++) {
											printedLines.add(j);
										}
									}
								}
								int prevIdx = -1;
								for (int lineIdx : printedLines) {
									if (prevIdx != -1 && lineIdx > prevIdx + 1) {
										commitBuilder.append(ColoredStringUtils.colorString("\t\t\t\t    ...  \n", ColoredStringUtils.COLOR_GRAY));
									}
									String line = lines[lineIdx];
									boolean unchanged = !line.startsWith("+") && !line.startsWith("-");
									int color = unchanged ? ColoredStringUtils.COLOR_YELLOW : ColoredStringUtils.COLOR_PURPLE;
									Integer defaultColor = unchanged ? ColoredStringUtils.COLOR_GRAY : (line.startsWith("-")?ColoredStringUtils.COLOR_BLUE:ColoredStringUtils.COLOR_AQUA);
									int oldNum = lineNums[lineIdx][0];
									int newNum = lineNums[lineIdx][1];
									String lineNo;
									if (oldNum == -1 && newNum == -1) {
										lineNo = "      ";
									} else if (oldNum == -1) {
										lineNo = String.format("+%-5d ", newNum);
									} else if (newNum == -1) {
										lineNo = String.format("-%-5d ", oldNum);
									} else {
										lineNo = String.format(" %-5d ", newNum);
									}
									commitBuilder.append("\t\t\t")
											.append(ColoredStringUtils.colorString(lineNo, ColoredStringUtils.COLOR_GRAY))
											.append(ColoredStringUtils.lightUpWords(line, color,defaultColor, keywords)).append("\n");
									prevIdx = lineIdx;
								}
							}
						}
					}
					if (containsCommit) {
						builder.append(commitBuilder.toString());
						containsFile = true;
					}
				}
				builder.append("\n");

				if (containsFile) {
					all.append("[").append(counter++).append("]").append(builder.toString());
				}
			}
			progressBarHandler.completed("处理完成");
			console.outPrintln(all.toString());
		}catch (Exception e){
			if (progressBarHandler != null) {
				progressBarHandler.shutdown(e.getMessage());
			}
			e.printStackTrace();
		}
	}

	private String getChangeText(JSONObject change) {
		String new_path = change.getString("new_path");
		boolean delete = change.getBoolean("deleted_file") != null && change.getBoolean("deleted_file");
		boolean create = change.getBoolean("new_file") != null && change.getBoolean("new_file");
		boolean renamed = change.getBoolean("renamed_file") != null && change.getBoolean("renamed_file");
		boolean changePath = false;
		String oldName = "";
		if (renamed && !Objects.equals(getPath(change,"new_path"),getPath(change,"old_path"))) {
			changePath = true;
		}
		if (renamed) {
			renamed = Objects.equals(getName(change,"new_path"),getName(change,"old_path"));
			oldName = getName(change,"old_path");
		}
		StringBuilder s = new StringBuilder();
		if (delete) {
			s.append("[删除]");
		}
		if (create) {
			s.append("[新增]");
		}
		if (renamed) {
			s.append("[重命名:").append(oldName).append("]");
		}
		if (changePath) {
			s.append("[包变更]");
		}
		s.append(new_path);
		String text = s.toString();
		return text;
	}

	private String getPath(JSONObject change,String key) {
		String path = change.getString(key);
		if (path != null) {
			path = path.substring(0,path.lastIndexOf('/'));
		}
		return path;
	}

	private String getName(JSONObject change,String key) {
		String path = change.getString(key);
		if (path != null) {
			path = path.substring(path.lastIndexOf('/') +1);
		}
		return path;
	}

	@Override
	public HelpBuilder initHelpBuilder() {
		HelpBuilder help =  new HelpBuilder("mrls","合并内容检索");
		help.appendParamDesc(new HelpBuilder.Param("project","查询项目").addSelecet(gitLabInfoProvider.getProjects()));
		help.appendParamDesc(new HelpBuilder.Param("branch","查询分支").addSelecet(gitLabInfoProvider.getBranchs()));
		help.appendParamDesc("file","过滤文件名,多个以逗号分隔",false);
		help.appendOptionDesc("t","合并标题关键字",true);
		help.appendOptionDesc("k","合并内容关键字",true);
		help.appendOptionDesc("s","合并来源分支",true);
		help.appendOptionDesc("d","指定查询天数范围 默认查一天",true);
		help.appendOptionDesc("b","指定开始时间",true);
		help.appendOptionDesc("e","指定结束时间",true);
		help.appendOptionDesc("f","详情");
		return help.build();
	}

	public static void main(String[] args) {
		IGitLabInfoProvider provider = null;
		new MrlsHandler(provider).handleLine("mrls", ConsoleIOProxy.getInstance("UTF-8"), Collections.singletonMap("k",null), "erp-core","gray3/240515","TradeWarehouseMatchFilter");
	}
}

