package idv.const_x.console.ext.handler.gitlab;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import idv.const_x.http.AbsInvokeResultHandler;
import idv.const_x.http.DefaultInvokeHandler;
import idv.const_x.http.HttpInvokeResultHandler;
import idv.const_x.http.HttpInvoker;
import idv.const_x.utils.StringExtUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description <pre>
 *
 * </pre>
 * @Author const.x
 * @Date 2026-05-20
 */
public class GitlabInvoker {


    /**
     * 获取 commit 关联的 merge requests
     * <p>优先使用专用 API，若返回 500 则 fallback 到搜索 API（兼容旧版本 GitLab）
     */
    public static String commitMrs(IGitLabInfoProvider provider,String project,String iid) {
        Map<String, Object> params = new HashMap<>();
        String result = invoke(provider,project,"/repository/commits/"+iid + "/merge_requests",params,HttpInvoker.GET);
        // 如果返回 500 错误，使用搜索 API 作为 fallback（兼容 GitLab 10.7 以下版本）
        if (result != null && result.startsWith("500:")) {
            result = commitMrsBySearch(provider, project, iid);
        }
        return result;
    }

    /**
     * 通过搜索 API 获取 commit 关联的 merge requests
     * <p>兼容旧版本 GitLab
     */
    public static String commitMrsBySearch(IGitLabInfoProvider provider, String project, String iid) {
        Map<String, Object> params = new HashMap<>();
        params.put("scope", "merge_requests");
        params.put("search", iid);
        return invoke(provider, project, "/search", params, HttpInvoker.GET);
    }

    public static String commitDetail(IGitLabInfoProvider provider,String project,String iid) {
        Map<String, Object> params = new HashMap<>();
        params.put("stats",false);
        if (StringExtUtils.isBlank(project)) {
            throw new IllegalArgumentException("project不能为空");
        }
        return invoke(provider,project,"/repository/commits/"+iid,params, HttpInvoker.GET);
    }


    /**
     *
     * @param project
     * @param sourcebranch
     * @param targetbranch
     * @param status opened, closed, locked, or merged
     * @param keywords
     * @return
     * @throws IOException
     */
    public static String mrList(IGitLabInfoProvider provider,String project,String sourcebranch, String targetbranch,String status, String keywords,String beginTime,String endTime) throws IOException {
        Map<String, Object> params = new HashMap<>();
        

        if (StringExtUtils.isNotBlank(sourcebranch)) {
            params.put("source_branch",sourcebranch);
        }
        if (StringExtUtils.isNotBlank(targetbranch)) {
            params.put("target_branch",targetbranch);
        }
        if (StringExtUtils.isNotBlank(status)) {
            params.put("state",status);
        }
        if (StringExtUtils.isNotBlank(keywords)) {
            params.put("search",keywords);
        }
        if (StringExtUtils.isNotBlank(beginTime)) {
            params.put("updated_after",beginTime);
        }
        if (StringExtUtils.isNotBlank(endTime)) {
            params.put("updated_before",endTime);
        }
        params.put("per_page",50);
        int page  = 1;
        JSONArray res = new JSONArray();
        while (true){
            params.put("page",page);
            page++;
            String invoke = invoke(provider,project,"/merge_requests", params, HttpInvoker.GET);
            JSONArray array = null;
            try {
                array = JSON.parseArray(invoke);
            }catch (JSONException e){
                throw new RuntimeException(invoke);
            }
            if (array.size() == 0) {
                break;
            }
            for (Object o : array) {
                res.add(o);
            }
        }

        return res.toJSONString();
    }

    public static String mrCommits(IGitLabInfoProvider provider,String project,String iid) throws IOException {
        Map<String, Object> params = new HashMap<>();
        
        return invoke(provider,project,"/merge_requests/"+iid+"/commits",params,HttpInvoker.GET);
    }

    public static String commitChanges(IGitLabInfoProvider provider,String project,String iid) {
        Map<String, Object> params = new HashMap<>();
        
        return invoke(provider,project,"/repository/commits/"+iid+"/diff",params,HttpInvoker.GET);
    }

    public static String getMr(IGitLabInfoProvider provider,String project,String iid) throws IOException {
        Map<String, Object> params = new HashMap<>();
        
        return invoke(provider,project,"/merge_requests/"+iid,params,HttpInvoker.GET);
    }

    public static String approveMr(IGitLabInfoProvider provider,String project,String id,String token) {
        Map<String, Object> params = new HashMap<>();
        
        return invoke(provider,project,"/merge_requests/"+id + "/approve",params,HttpInvoker.POST,token);
    }

    public static String unApproveMr(IGitLabInfoProvider provider,String project,String id,String token) {
        Map<String, Object> params = new HashMap<>();
        
        return invoke(provider,project,"/merge_requests/"+id + "/unapprove",params,HttpInvoker.POST,token);
    }

    public static String approveMrStatus(IGitLabInfoProvider provider,String project,String id) {
        Map<String, Object> params = new HashMap<>();
        
        return invoke(provider,project,"/merge_requests/"+id + "/approvals",params,HttpInvoker.GET);
    }

    public static String acceptMr(IGitLabInfoProvider provider,String project,String id,String token) {
        Map<String, Object> params = new HashMap<>();
        return invoke(provider,project,"/merge_requests/"+id + "/merge",params,HttpInvoker.PUT,token);
    }


    /**
     *
     * @param project
     * @param scope blobs  commits wiki_blobs notes milestones merge_requests issues
     * @param keyword
     * @return
     */
    public static String search(IGitLabInfoProvider provider,String project,String scope,String keyword) {
        Map<String, Object> params = new HashMap<>();
        params.put("scope",scope);
        params.put("search",keyword);
        String path = "/search";
        return invoke(provider,project,path,params,HttpInvoker.GET);
    }


    protected static String invoke(IGitLabInfoProvider provider,String project,String path, Map<String, Object> params, String method)  {
        return invoke(provider,project,path,params,method,null);
    }

    protected static String invoke(IGitLabInfoProvider provider,String project,String path, Map<String, Object> params, String method,String token)  {
        if (project != null) {
            path =   "/projects/"+ provider.switch2Projectkey(project) +path;
        }
        if (method == null) {
            method = HttpInvoker.GET;
        }
        Map<String, String> hearder = new HashMap<>();
        if (token == null) {
            token = provider.getGitToken(project);
        }
        if (StringExtUtils.isBlank(token)) {
            throw new IllegalArgumentException("gitlab请求token不能为空");
        }
        String url = provider.getBaseApiUrl(project);
        if (StringExtUtils.isBlank(url)) {
            throw new IllegalArgumentException("gitlab请求url不能为空");
        }
        String baseUrl = url + path;
        hearder.put("PRIVATE-TOKEN", token);
        HttpInvokeResultHandler handler = new DefaultInvokeHandler();
        final String[] res = {null};
        try {
            HttpInvoker.invoke(baseUrl, method, params, hearder,  new AbsInvokeResultHandler() {

                @Override
                public void handleException(Exception e) {
                    e.printStackTrace();
                }

                @Override
                protected void handResult(HttpURLConnection connection, int code, String messsage, String result,
                                          String redirectUrl, Map<String, String> cookies) {
                    if( String.valueOf(code).startsWith("20")){
                        res[0] = result;
                    }else {
                        res[0] = code + ":"+messsage;
                    }
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return res[0];
    }

}
