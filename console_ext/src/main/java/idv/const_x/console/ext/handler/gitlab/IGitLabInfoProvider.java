package idv.const_x.console.ext.handler.gitlab;

import java.net.URLEncoder;
import java.util.List;

/**
 * @Description <pre>
 *   gitlab相关配置信息
 * </pre>
 * @Author const.x
 * @Date 2026-05-20
 */
public interface IGitLabInfoProvider {

    /**
     * <pre>
     * gitlab api请求的根目录
     * eg. http://gitlab.example.com/api/v4
     * </pre>
     */
     String getBaseApiUrl(String project);

    /**
     * <pre>
     * gitlab web根目录
     * eg. http://gitlab.example.com/
     * </pre>
     */
    String getBaseWebUrl(String project);

    /**
     * <pre>
     * gitlab api请求 使用的token
     * 可访问你的gitlab项目地址(eg. http://gitlab.example.com/help/user/profile/personal_access_tokens.md ) 以获取或创建 你的token
     * </pre>
     */
    String getGitToken(String project);

    /**
     * 关注的项目
     */
    List<String> getProjects();

    /**
     * 关注的分支
     */
    List<String> getBranchs();

    /**
     * <pre>
     * 将项目名 转换为对应的ID值 或 经url转换后的路径名
     * 如果你的项目 是在idv目录下, 如 idv/demo,需要转换为 idv%2Fdemo
     * </pre>
     */
    default  String switch2Projectkey(String project){
        return URLEncoder.encode(project);
    }

    /**
     * gitlab 代码合并使用的token
     */
    String getGitMrToken(String project);


}
