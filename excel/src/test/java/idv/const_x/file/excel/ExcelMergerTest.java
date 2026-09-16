package idv.const_x.file.excel;

import idv.const_x.file.excel.matcher.EqualToMatcher;
import idv.const_x.file.excel.matcher.ILineMatcher;
import idv.const_x.file.excel.matcher.LineIndexMatcher;
import idv.const_x.utils.OSUtils;

/**
 * @Description <pre>
 *
 * </pre>
 * @Author const.x
 * @Date 2024-02-03
 */
public class ExcelMergerTest {

    public static void main(String[] args) throws Exception{

        String aimFile = OSUtils.getDesktopPath()+"/ExcelMergerTest.xlsx";
        String sourceFile = OSUtils.getDesktopPath()+"/ExcelMergerTest.xlsx";

        //定义要合并到的文件和表单 a
        ExcelMerger meger = new ExcelMerger(aimFile);
        //定义匹配方式:源页签（s）哪列与目标页签（a）哪列做比较 0开始
        ILineMatcher matcher = new EqualToMatcher("E","A");

        //定义更新方式:从源表单（s）哪个列写到目标表单（a）哪个列 值渲染方式
        ExcelMerger.SourceColumn sc = new ExcelMerger.SourceColumn("F","B");
        ExcelMerger.SourceColumn sc2 = new ExcelMerger.SourceColumn(6,2);
        //定义源文件及源表单 s
        meger.addSource(sourceFile, null, matcher, sc,sc2);

        //定义匹配方式:源页签（s）哪列与目标页签（a）哪列做比较 0开始
        ILineMatcher matcher2 = new LineIndexMatcher();
        ExcelMerger.SourceColumn sc3 = new ExcelMerger.SourceColumn("A","D");
        meger.addSource(sourceFile, "Sheet2", matcher2, sc3);

        meger.parse();
    }

}
