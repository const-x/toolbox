package idv.const_x.console.ext.handler.gitlab;

import idv.const_x.console.handler.AbsInputLineHandler;

/**
 * @Description <pre>
 *
 * </pre>
 * @Author const.x
 * @Date 2026-05-20
 */
public abstract class AbsGitLabHandler extends AbsInputLineHandler {

    protected IGitLabInfoProvider gitLabInfoProvider;

    public AbsGitLabHandler(IGitLabInfoProvider gitLabInfoProvider) {
        this.gitLabInfoProvider = gitLabInfoProvider;
        super.init();
    }

    @Override
    protected void init() {
        // 覆盖父类 init()，在 gitLabInfoProvider 赋值前不执行任何操作
        // 真正的初始化在构造函数完成后通过 super.init() 调用
        if (gitLabInfoProvider == null) {
            return;
        }
        super.init();
    }
}
