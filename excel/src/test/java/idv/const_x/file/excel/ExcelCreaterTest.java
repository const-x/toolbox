package idv.const_x.file.excel;

import idv.const_x.file.excel.renderer.AbsCellRenderer;
import idv.const_x.file.excel.renderer.BooleanValueRenderer;
import idv.const_x.file.excel.style.GreenStyleGenerator;
import idv.const_x.file.excel.style.IStyleGenerator;
import idv.const_x.file.excel.utils.ExcelUtils;
import idv.const_x.utils.FileUtils;
import idv.const_x.utils.OSUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.util.Arrays;

/**
 * @Description <pre>
 *
 * </pre>
 * @Author const.x
 * @Date 2024-02-03
 */
public class ExcelCreaterTest {



    public static void main(String[] args) throws Exception{
        ExcelCreater creater = new ExcelCreater(ExcelSign.EXCEL_TYPE_XLSX);


        ExcelColumnMeta  a = new ExcelColumnMeta("商品名称", ExcelTypeEnum.TEXT);
        //支持指定可选数据范围
        ExcelColumnMeta b = new ExcelColumnMeta("商品类别",  ExcelTypeEnum.TEXT);
        b.setSelects(Arrays.asList("日化","食品"));
        //支持指定渲染器(写入值渲染  tips 和 样式)
        ExcelColumnMeta c = new ExcelColumnMeta("售价", ExcelTypeEnum.TEXT);
        c.setCellRenderer(new AbsCellRenderer<Object[]>() {
            @Override
            public Object contentRender(Object[] values, Object value, int row) {
                return "$" + value;
            }

            @Override
            public String commentRender(Object[] values, Object value, int row) {
                Double d = Double.valueOf(value.toString());
                if (d < 4.5) {
                    return "售价过低";
                }
                return null;
            }

            @Override
            public CellStyle cellStyleRender(Object[] values, Object value, Workbook book, IStyleGenerator style, int row) {
                Double d = Double.valueOf(value.toString());
                if (d < 4.5) {
                    return style.getHighlightCellStyle(c,row);
                }
                return super.cellStyleRender(values, value, book, style, row);
            }
        });

        ExcelColumnMeta d = new ExcelColumnMeta("库存量", ExcelTypeEnum.INTEGER);

        ExcelColumnMeta e = new ExcelColumnMeta("是否下架",ExcelTypeEnum.TEXT, "包含库存不足自动下架");
        e.setCellRenderer(new BooleanValueRenderer());

        //支持按照指定字段的值自动创建表单
        ExcelSheetMeta sheetMeta = new ExcelSheetMeta(b);
        sheetMeta.addField(a)
                .addField(b)
                .addField(c)
                .addField(d)
                .addField(e);
        //支持按指定行数(含表头)自动创建分页表单
        sheetMeta.setMaxRowPreSheet(3);


        creater.createSheet(sheetMeta);

        //支持设置主题风格
        creater.setStyle(GreenStyleGenerator.getInstance(creater));


        creater.addProperty("test", "隐藏属性");


        creater.writeRow(new Object[] { "蓝月亮洁净自然瓶1KG+云南白药120G","日化", "2.12", "2", "0" });
        creater.writeRow(new Object[] { "蓝月亮洁净熏瓶装3KG+蓝月亮洁净熏瓶2KG","日化", "14.45", "12", false });
        creater.writeRow(new Object[] { "Q-天添优品2.5kg+500g薰衣草香型柔肤洗衣液","日化", "14.12", "1", false });

        Object[][] values = {
                new Object[] { "超A",null, "14.12", "1", false },
                new Object[] { "旺旺150g椰果菠萝维多粒","食品", "4.33", "20", false },
                new Object[] { "旺旺80ml吸吸冰香草味", "食品","4.53", "100", false }
        };
        creater.writeRows(values);

        //写入锁定内容
        creater.writeRow(new Object[] { "金元宝10kg优选东北大米（价格锁定）","食品", "45.0", "13", false });
        creater.lockCurrentSheet("123456");
        creater.writeLocked(creater.getCurrentIndex(), 2, "45.0", "不可调价");

        //写入高亮内容
        creater.writeHighlight(creater.getCurrentIndex(), 4, "是", null);

        //写入公式
        final String colName = ExcelUtils.columnNumberToName(3);
        creater.writeFormula(creater.getCurrentIndex(), 5, "SUM("+ colName +"2)", "行公式");

        //creater.writeFormulaForAllSheet(3, "SUM("+ colName +"2,"+ colName +ExcelSign.EXCEL_LAST_ROW_IDX_HOLDER.getString() + ")", "统计公式");


//		creater.createSheet("测试数据",new ExcelColumnMeta[] { a, b,c, d, e });
//		creater.createrow();
//		creater.createHead(new ExcelColumnMeta[] { a, b,c, d });
//		creater.write(creater.getCurrentIndex(), 0, "旺旺80ml吸吸冰薄荷味",);
//		creater.write(creater.getCurrentIndex(), 1, "食品");
//		creater.writeHighlight(creater.getCurrentIndex(), 2, "50.01");
//		creater.write(creater.getCurrentIndex(), 3, "100");
////	creater.writeFormula(creater.getCurrentIndex(), 2, "", null, a);
//		creater.writeLocked(creater.getCurrentIndex(), e, "Y", null);

        String path = OSUtils.getDesktopPath() + File.separator + "test." + creater.getType().getString();
        creater.write2File(path);
        FileUtils.openFile(path);
    }
}
