package idv.const_x.file.excel.matcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Description <pre>
 *
 * </pre>
 * @Author const.x
 * @Date 2024-01-29
 */
public class LineGroupMatcher implements ILineMatcher{

    public static final String GROUP_LOGIC_AND = "and";

    public static final String GROUP_LOGIC_OR = "or";

    private List<ILineMatcher> matchers = new ArrayList<>();

    private String logic = GROUP_LOGIC_AND;

    public LineGroupMatcher(ILineMatcher ... matchers){
        addMatchers(matchers);
    }

    public LineGroupMatcher(String logic,ILineMatcher ... matchers){
        this.logic = logic;
        addMatchers(matchers);
    }

    public void setMatchers(List<ILineMatcher> matchers) {
        this.matchers = matchers;
    }

    public void setLogic(String logic) {
        this.logic = logic;
    }

    public LineGroupMatcher addMatchers(ILineMatcher ... matchers) {
        if (matchers != null) {
            for (ILineMatcher matcher : matchers) {
                this.matchers.add(matcher);
            }
        }
        return this;
    }

    @Override
    public boolean match(Object[] source, int sRowIdx, Object[] aim, int aRowIdx) throws Exception {
        if (matchers == null || matchers.size() == 0) {
            throw new RuntimeException("未指定行数据匹配器");
        }
        if (Objects.equals(logic,GROUP_LOGIC_AND)) {
            for (ILineMatcher matcher : matchers) {
                boolean matched = matcher.match(source,sRowIdx,aim,aRowIdx);
                if (!matched) {
                    return false;
                }
            }
            return true;
        }else if(Objects.equals(logic,GROUP_LOGIC_OR)){
            for (ILineMatcher matcher : matchers) {
                boolean matched = matcher.match(source,sRowIdx,aim,aRowIdx);
                if (matched) {
                    return true;
                }
            }
            return false;
        }else {
            throw new RuntimeException("未知的逻辑关系:" + logic);
        }



    }
}
