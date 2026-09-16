package idv.const_x.file.excel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description <pre>
 *
 * </pre>
 * @Author const.x
 * @Date 2024-01-29
 */
public class ExcelSheetMeta {

    private String sheetName;

    private ExcelColumnMeta sheetExcelColumnMeta;

    /**
     * 表单最大行数,不能超过65536行
     */
    private int maxRowPreSheet = 150000;

    private Map<String,Integer> curPage = new HashMap<>();

    private List<ExcelColumnMeta> fields = new ArrayList<>();

    public ExcelSheetMeta(String sheetName) {
        this.sheetName = sheetName;
    }


    public ExcelSheetMeta(String sheetname, List<ExcelColumnMeta> fields){
        this.sheetName = sheetName;
        this.fields = fields;
    }

    public ExcelSheetMeta(ExcelColumnMeta sheetExcelColumnMeta){
        this.sheetExcelColumnMeta = sheetExcelColumnMeta;
    }

    public ExcelSheetMeta(ExcelColumnMeta sheetExcelColumnMeta,List<ExcelColumnMeta> fields){
        this.sheetExcelColumnMeta = sheetExcelColumnMeta;
        this.fields = fields;
    }

    public String getSheetName() {
        return sheetName;
    }

    public ExcelSheetMeta setSheetName(String sheetName) {
        this.sheetName = sheetName;
        return this;
    }

    public ExcelColumnMeta getSheetExcelColumnMeta() {
        return sheetExcelColumnMeta;
    }

    public ExcelSheetMeta setSheetExcelColumnMeta(ExcelColumnMeta sheetExcelColumnMeta) {
        this.sheetExcelColumnMeta = sheetExcelColumnMeta;
        return this;
    }

    public int getMaxRowPreSheet() {
        return maxRowPreSheet;
    }

    public ExcelSheetMeta setMaxRowPreSheet(int maxRowPreSheet) {
        this.maxRowPreSheet = maxRowPreSheet;
        return this;
    }

    public List<ExcelColumnMeta> getFields() {
        return fields;
    }

    public ExcelColumnMeta getField(int colIndex) {
        if (colIndex >= fields.size()) {
            return null;
        }
        return fields.get(colIndex);
    }

    public ExcelSheetMeta setFields(List<ExcelColumnMeta> fields) {
        this.fields = fields;
        return this;
    }

    public ExcelSheetMeta addField(ExcelColumnMeta field) {
        this.fields.add(field);
        return this;
    }

    public int getCurPage(String sheetName) {
        int page = 1;
        if (this.curPage.containsKey(sheetName)) {
            page = curPage.get(sheetName);
        }
        curPage.put(sheetName, page + 1);
        return page;
    }


}
