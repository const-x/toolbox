package idv.const_x.console.ext.handler.gitlab;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import idv.const_x.console.handler.HelpBuilder;
import idv.const_x.console.io.ConsoleIOProxy;
import idv.const_x.utils.ColoredStringUtils;
import idv.const_x.utils.StringExtUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GitViewHandler extends AbsGitLabHandler {

    private static final int CONTEXT_LINES = 3;
    private static final DateTimeFormatter COMMIT_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public GitViewHandler(IGitLabInfoProvider gitLabInfoProvider) {
        super(gitLabInfoProvider);
    }

    @Override
    public String getCmd() {
        return "gitView";
    }

    @Override
    public HelpBuilder initHelpBuilder() {
        return new HelpBuilder(getCmd(), "查看指定分支的文件内容及逐行提交信息")
                .appendParamDesc(new HelpBuilder.Param("project", "项目名称").addSelecet(gitLabInfoProvider.getProjects()))
                .appendParamDesc(new HelpBuilder.Param("branch", "分支名称").addSelecet(gitLabInfoProvider.getBranchs()))
                .appendParamDesc("file", "文件名、无扩展名文件名或完整路径")
                .appendOptionDesc(new HelpBuilder.Option("l", "目标行号", true, true, new String[0], true))
                .build();
    }

    @Override
    public void handleLine(String line, ConsoleIOProxy console, Map<String, String> options, String... params) {
        try {
            String project = getParam(params, 0);
            String branch = getParam(params, 1);
            String file = getParam(params, 2);
            String lineOption = getOptions(options, "l");
            if (StringExtUtils.isBlank(project) || StringExtUtils.isBlank(branch) || StringExtUtils.isBlank(file)) {
                console.error("项目、分支和文件名不能为空");
                return;
            }

            int targetLine;
            try {
                targetLine = Integer.parseInt(lineOption);
            } catch (NumberFormatException e) {
                console.error("行号必须是正整数");
                return;
            }
            if (targetLine < 1) {
                console.error("行号必须是正整数");
                return;
            }

            List<String> matches = findFiles(project, branch, file);
            if (matches.isEmpty()) {
                console.error("指定分支未找到文件: " + file);
                return;
            }
            if (matches.size() > 1) {
                console.error("找到多个同名文件，请使用完整路径:");
                for (String path : matches) {
                    console.println("  " + path);
                }
                return;
            }

            String path = matches.get(0);
            List<BlameLine> lines = loadBlame(project, branch, path);
            if (targetLine > lines.size()) {
                console.error("行号超出文件范围，共 " + lines.size() + " 行");
                return;
            }

            console.info(project + " " + branch + " " + path);
            int first = Math.max(1, targetLine - CONTEXT_LINES);
            int last = Math.min(lines.size(), targetLine + CONTEXT_LINES);
            for (int number = first; number <= last; number++) {
                BlameLine blameLine = lines.get(number - 1);
                String commitInfo = blameLine.time + " " + blameLine.author + " " + blameLine.shortId;

                console.println(ColoredStringUtils.colorString(commitInfo, ColoredStringUtils.COLOR_BLUE)
                        + " | " +ColoredStringUtils.colorString(String.valueOf(number), (number == targetLine ?  ColoredStringUtils.COLOR_PURPLE: ColoredStringUtils.COLOR_YELLOW))
                        +  " | " + blameLine.content);

            }
        } catch (Exception e) {
            console.error("gitView 查询失败: " + e.getMessage());
        }
    }

    private List<BlameLine> loadBlame(String project, String branch, String path) {
        String response = GitlabInvoker.fileBlame(gitLabInfoProvider, project, branch, path);
        if (response != null && response.startsWith("404:")) {
            return parseHtmlBlame(GitlabInvoker.fileBlameHtml(gitLabInfoProvider, project, branch, path));
        }

        JSONArray blame = parseArray(response);
        List<BlameLine> lines = new ArrayList<>();
        for (int i = 0; i < blame.size(); i++) {
            JSONObject block = blame.getJSONObject(i);
            JSONObject commit = block.getJSONObject("commit");
            JSONArray blockLines = block.getJSONArray("lines");
            if (commit == null || blockLines == null) {
                throw new IllegalStateException("GitLab blame 响应缺少提交信息或文件行");
            }
            String id = commit.getString("id");
            String time = commit.getString("committed_date");
            String author = commit.getString("author_name");
            for (int j = 0; j < blockLines.size(); j++) {
                lines.add(new BlameLine(blockLines.getString(j), time, author, id));
            }
        }
        return lines;
    }

    private List<BlameLine> parseHtmlBlame(String response) {
        if (StringExtUtils.isBlank(response) || response.startsWith("404:")) {
            throw new IllegalStateException("GitLab Web blame 返回错误: " + response);
        }
        Document document = Jsoup.parse(response);
        List<BlameLine> lines = new ArrayList<>();
        for (Element row : document.select(".file-content.blame tr")) {
            Element commit = row.selectFirst("td.blame-commit");
            if (commit == null) {
                continue;
            }
            Element id = commit.selectFirst("a.commit-sha");
            Element time = commit.selectFirst("time[datetime]");
            Element author = commit.selectFirst("a.commit-author-link");
            if (id == null || time == null || author == null) {
                throw new IllegalStateException("GitLab Web blame 缺少提交信息");
            }
            for (Element numberElement : row.select("a.diff-line-num[data-line-number]")) {
                int number = Integer.parseInt(numberElement.attr("data-line-number"));
                Element content = row.selectFirst("span#LC" + number);
                if (number != lines.size() + 1 || content == null) {
                    throw new IllegalStateException("GitLab Web blame 行号或内容不连续: " + number);
                }
                lines.add(new BlameLine(content.wholeText(), time.attr("datetime"), author.text(), id.text()));
            }
        }
        if (lines.isEmpty()) {
            throw new IllegalStateException("GitLab Web blame 未返回文件内容");
        }
        return lines;
    }

    private static class BlameLine {
        private final String content;
        private final String time;
        private final String author;
        private final String shortId;

        private BlameLine(String content, String time, String author, String id) {
            if (StringExtUtils.isBlank(time) || StringExtUtils.isBlank(author) || StringExtUtils.isBlank(id)) {
                throw new IllegalStateException("GitLab blame 响应缺少提交时间、提交者或 ID");
            }
            this.content = content;
            this.time = OffsetDateTime.parse(time).format(COMMIT_TIME_FORMAT);
            this.author = author;
            this.shortId = id.substring(0, Math.min(8, id.length()));
        }
    }

    private List<String> findFiles(String project, String branch, String file) {
        if (file.contains("/")) {
            List<String> matches = new ArrayList<>();
            matches.add(file);
            return matches;
        }
        Set<String> matches = new LinkedHashSet<>();
        for (int page = 1; ; page++) {
            JSONArray entries = parseArray(GitlabInvoker.search(gitLabInfoProvider, project, "blobs", file, branch, page));
            for (int i = 0; i < entries.size(); i++) {
                JSONObject entry = entries.getJSONObject(i);
                String path = entry.getString("path");
                if (StringExtUtils.isBlank(path)) {
                    path = entry.getString("filename");
                }
                if (StringExtUtils.isBlank(path)) {
                    continue;
                }
                String name = path.substring(path.lastIndexOf('/') + 1);
                if (file.equals(name) || (!file.contains(".") && name.startsWith(file + "."))) {
                    matches.add(path);
                }
            }
            if (entries.size() < 100) {
                return new ArrayList<>(matches);
            }
        }
    }

    private JSONArray parseArray(String response) {
        if (StringExtUtils.isBlank(response)) {
            throw new IllegalStateException("GitLab 未返回数据");
        }
        try {
            JSONArray result = JSON.parseArray(response);
            if (result != null) {
                return result;
            }
        } catch (Exception ignored) {
            // GitlabInvoker returns HTTP errors as status:message.
        }
        throw new IllegalStateException("GitLab 返回错误: " + response);
    }
}
