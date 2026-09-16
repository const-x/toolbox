package idv.const_x.console.ext.handler.gitlab;

import idv.const_x.console.completer.FirstLimitedCompleter;
import idv.const_x.console.completer.StringTreeCompleter;
import idv.const_x.console.handler.HelpBuilder;
import idv.const_x.console.io.ConsoleIOProxy;
import idv.const_x.utils.StringExtUtils;

import java.util.Map;

/**
 * @Description <pre>
 *
 * </pre>
 * @Author const.x
 * @Date 2023-06-16
 */
public class GitSearchHandler  extends AbsGitLabHandler {


    public GitSearchHandler(IGitLabInfoProvider gitLabInfoProvider) {
        super(gitLabInfoProvider);
    }



    @Override
    public void handleLine(String line, ConsoleIOProxy console, Map<String, String> options, String... params) {
        String project = getParam(params, 0);
        String keyword = getParam(params, 1);
        if (StringExtUtils.isBlank(project) || StringExtUtils.isBlank(keyword)) {
            return;
        }
        String blobs = GitlabInvoker.search(gitLabInfoProvider,project, "blobs", keyword);
        console.println(blobs);
    }

    @Override
    public HelpBuilder initHelpBuilder() {
        HelpBuilder help = new HelpBuilder(getCmd(),"查询gitlib");
        help.appendParamDesc("project","查询项目");
        help.appendParamDesc("keyword","查询关键字");
        return help.build();
    }


    @Override
    public FirstLimitedCompleter initCompleter() {
        StringTreeCompleter completer = new StringTreeCompleter(getCmd());
        completer.addSubStrings(getCmd(), gitLabInfoProvider.getProjects());
        return completer;
    }

    @Override
    public String getCmd() {
        return "gitsearch";
    }



}
