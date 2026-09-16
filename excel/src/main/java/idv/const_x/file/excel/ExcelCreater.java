package idv.const_x.file.excel;

import idv.const_x.file.excel.renderer.AbsCellRenderer;
import idv.const_x.file.excel.style.DefaultStyleGenerator;
import idv.const_x.file.excel.style.IStyleGenerator;
import idv.const_x.file.excel.utils.ExcelUtils;
import idv.const_x.utils.FileUtils;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;


/**
 * 创建excel文件
 *
 * @since 6.3
 * @version 2014-03-05 14:37:56
 * @author const.x
 */
public class ExcelCreater implements AutoCloseable {

	

	private IStyleGenerator style;
	
	private Sheet properties;
	private Workbook workBook;
	private final ExcelSign type;
	
	private final Map<String, Sheet> sheets = new HashMap<String, Sheet>();
	private final Map<Sheet, Drawing> drawings = new HashMap<Sheet, Drawing>();

	private final Map<Sheet, ExcelSheetMeta> sheetMetas = new HashMap<Sheet, ExcelSheetMeta>();
	private final Map<Sheet, Integer> sheetRowIndex = new HashMap<Sheet, Integer>();
	
	private final Map<Sheet, Map<Integer,Integer>> sheetColMaxWidth = new HashMap<Sheet, Map<Integer,Integer>>();
	private final Map<Sheet, Boolean> hasFormula = new HashMap<Sheet,  Boolean>();
	private int currentIndex = -1;
	private Sheet currentSheet;

	private ExcelSheetMeta currentSheetMeta;
	
	private String defaultSheetName = "Sheet1";

	private String file;
	
	public int getCurrentIndex() {
		return currentIndex;
	}


	public void setCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;
	}

	public ExcelCreater(int keepInMemRows, String defaultSheetName) {
		this.workBook = new SXSSFWorkbook(keepInMemRows);
        this.type = ExcelSign.EXCEL_TYPE_XLSX;
        this.defaultSheetName = defaultSheetName;
        style = DefaultStyleGenerator.getInstance(this.getWorkBook());
	}

	public ExcelCreater(ExcelSign type, String defaultSheetName) {
        if(type.equals(ExcelSign.EXCEL_TYPE_XLS)){
        	this.workBook = new HSSFWorkbook();
        }else{
        	this.workBook = new XSSFWorkbook();
        	
        }
        this.type = type;
        style = DefaultStyleGenerator.getInstance(this.getWorkBook());
        this.defaultSheetName = defaultSheetName;
	}

	
	public ExcelCreater(ExcelSign type) {
        if(type.equals(ExcelSign.EXCEL_TYPE_XLS)){
        	this.workBook = new HSSFWorkbook();
        }else{
        	this.workBook = new XSSFWorkbook();
        }
        style = DefaultStyleGenerator.getInstance(this.getWorkBook());
        this.type = type;
	}
  
	
	
	public void setFile(String file) {
		this.file = file;
	}


	public void setStyle(IStyleGenerator style) {
		this.style = style;
	}

	public void createSheet(ExcelSheetMeta sheetMeta){
		this.createSheet(sheetMeta.getSheetName(),sheetMeta);
	}



	private void createSheet(String sheetname,ExcelSheetMeta meta) {
		this.sheetRowIndex.put(this.currentSheet, this.currentIndex);
		if (sheetname != null) {
			this.currentSheet = this.workBook.createSheet(sheetname);
		} else {
			this.currentSheet = this.workBook.createSheet();
		}
		this.currentSheetMeta = meta;
//		this.currentSheet.setDisplayGridlines(false);
		this.sheets.put(sheetname, currentSheet);
		sheetMetas.put(currentSheet,meta);
		this.currentSheet.setAutobreaks(true);
		this.currentSheet.setDefaultColumnWidth(16);
		this.currentIndex = -1;
		if (meta.getFields()!=null && meta.getFields().size() > 0) {
			this.createHead(meta.getFields());
		}
	    this.currentSheet.createFreezePane( 0, 1, 0, 1 );
		this.currentSheet.setDisplayGridlines(this.style.isDisplayGridlines());//隐藏Excel网格线,默认值为true
		this.currentSheet.setPrintGridlines(this.style.isPrintGridlines());// 打印Excel网格线,默认值为false
	}

	
	public void createSheet(String sheetname,String password,boolean hidden,ExcelSheetMeta meta) {
		this.createSheet(sheetname,meta);
		if(password != null){
			currentSheet.protectSheet(password);
		}
		if(hidden){
			workBook.setSheetHidden(workBook.getSheetIndex(currentSheet), false);
		}
	}

	public void createSheet(String sheetname,String ... fields) {
		List<ExcelColumnMeta> f = getExcelColumnMetas(fields);
		ExcelSheetMeta meta = new ExcelSheetMeta(sheetname,f);
		this.createSheet(sheetname, meta);
	}

	private List<ExcelColumnMeta> getExcelColumnMetas(String[] fields) {
		List<ExcelColumnMeta> f = new ArrayList<>(fields.length);
		for (int i = 0; i < fields.length; i++) {
			ExcelColumnMeta field = new ExcelColumnMeta(fields[i]);
			f.add(field);
		}
		return f;
	}

	public void switchToSheet(String sheetname){
		this.sheetRowIndex.put(this.currentSheet, this.currentIndex);
		if(!sheets.containsKey(sheetname)){
			
		}
		this.currentSheet = sheets.get(sheetname);
		this.currentSheetMeta = sheetMetas.get(currentSheet);
		this.currentIndex = sheetRowIndex.get(currentSheet);
	}

	public void createHead(String ...  fields) {
		List<ExcelColumnMeta> f = getExcelColumnMetas(fields);
		createHead(f);
	}

	public void createHead(List<ExcelColumnMeta>  fields) {
		Row row = this.createrow();
		Cell cell;
		for (int i = 0; i < fields.size(); i++) {
			ExcelColumnMeta field = fields.get(i);
			cell = row.createCell(i);
			CellStyle  cs = style.getHeadCellStyle(field);
			if (field.getSelects() != null && !field.getSelects().isEmpty()) {
				List<String> s = (List<String>) field.getSelects().stream().map(x -> String.valueOf(x)).collect(Collectors.toList());
				this.setCellRef(field.columnName,i,s);
			}
			this.fillCell(cell, field.getColumnName(),field.getTips(), ExcelTypeEnum.TEXT,cs);
		}
	}

	
	public Row createrow() {
		return createrow(false);
	}

	private Row createrow(boolean ignoreMax) {
		if(this.currentIndex == currentSheetMeta.getMaxRowPreSheet() - 1 && !ignoreMax){
			String sheetName = currentSheet.getSheetName();
			Integer	page = currentSheetMeta.getCurPage(sheetName);
			if(page > 0){
				sheetName = sheetName + "(" + page + ")";
			}
			this.createSheet(sheetName, null,workBook.isSheetHidden(workBook.getSheetIndex(currentSheet)),currentSheetMeta);
		}
		this.currentIndex++;
		Row row = this.currentSheet.createRow(this.currentIndex);
		return row;
	}

	private void fillCell(Cell cell, Object value,String comment, ExcelColumnMeta meta,CellStyle style) {
		ExcelTypeEnum type = meta.getType();
		this.fillCell(cell, value, comment, type, style);
	}
	
	private void fillCell(Cell cell, Object value,String comment, ExcelTypeEnum type,CellStyle style) {
		if(style != null){
			cell.setCellStyle(style);
		}
		this.setCellValue(cell, type, value);
		if(comment != null){
			cell.setCellComment(createComment(cell,comment));
		}
		this.setColMaxWidth(cell,value == null ? "":value.toString());
	}
	
	private void setCellValue(Cell cell,ExcelTypeEnum type,Object value){
		if(value == null ){
			cell.setCellType(Cell.CELL_TYPE_BLANK);
		}else if(type == ExcelTypeEnum.NUMBER){
			cell.setCellType(Cell.CELL_TYPE_NUMERIC);
			cell.setCellValue(Double.valueOf(value.toString()));
		}else if(type == ExcelTypeEnum.INTEGER){
			cell.setCellType(Cell.CELL_TYPE_NUMERIC);
			cell.setCellValue(Long.valueOf(value.toString()));
		}else if(type == ExcelTypeEnum.BOOLEAN){
			cell.setCellType(Cell.CELL_TYPE_BOOLEAN);
			cell.setCellValue(value.toString());
		}else{
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellValue(value.toString());
		}
	}

    private void setColMaxWidth(Cell cell,String value){
    	//设置最大列宽
		Map<Integer,Integer> maxColWidth =  sheetColMaxWidth.get(currentSheet);
		if(maxColWidth == null){
			maxColWidth = new HashMap<Integer,Integer>();
			sheetColMaxWidth.put(currentSheet, maxColWidth);
		}
		int colIndex = cell.getColumnIndex();
		if(!maxColWidth.containsKey(colIndex)){
			maxColWidth.put(colIndex, currentSheet.getDefaultColumnWidth());
		}
		int maxwidth = maxColWidth.get(colIndex);
		//setColumnWidth的单位是一个英文字符的1/256宽度， 按汉字占2个英文字符宽度计算
		int currentWidth =  value.length() > 50 ? 50 * 512 :value.length() * 512;
		if(currentWidth > maxwidth){
			this.currentSheet.setColumnWidth((short) cell.getColumnIndex(),currentWidth);
			maxColWidth.put(colIndex, currentWidth);
		}
    }
	
	private Comment createComment(Cell cell,String alias){
		Drawing drawing = this.drawings.get(this.currentSheet);
		if(drawing == null){
			drawing = this.currentSheet.createDrawingPatriarch();
			this.drawings.put(this.currentSheet, drawing);
		}
		ClientAnchor anchor = null;
		RichTextString text = null;
		if(this.type == ExcelSign.EXCEL_TYPE_XLS){
			anchor = new HSSFClientAnchor();
			text = new HSSFRichTextString(alias);
		}else{
			anchor = new XSSFClientAnchor();
			text = new XSSFRichTextString(alias);
		}
		Comment comment = drawing.createCellComment(anchor);
		comment.setString(text);
		return comment;
	}


	public void setProperties(Map<String, Object> properties) {
		if(this.properties == null){
			this.properties = this.workBook.createSheet(ExcelSign.PROPERTIES_SHEET_NAME.getString());
			workBook.setSheetHidden(workBook.getSheetIndex(this.properties), Workbook.SHEET_STATE_VERY_HIDDEN);
		}
		Row names  = this.properties.createRow(0);
		Row values  = this.properties.createRow(1);
		int counter =  names.getLastCellNum();
		if(counter == -1){
			counter = 0;
		}
		Cell cell;
		for (Entry<String, Object> entry : properties.entrySet()) {
			cell = names.createCell(counter);
			cell.setCellValue(entry.getKey());
			cell = values.createCell(counter);
			cell.setCellValue(entry.getValue().toString());
			counter++;
		}
	}
	
	public void addProperty(String name,Object value) {
		if(this.properties == null){
			this.properties = this.workBook.createSheet(ExcelSign.PROPERTIES_SHEET_NAME.getString());
			workBook.setSheetHidden(workBook.getSheetIndex(this.properties), Workbook.SHEET_STATE_VERY_HIDDEN);
		}
		Row names  = this.properties.getRow(0);
		if(names == null){
			names  = this.properties.createRow(0);
		}
		Row values  = this.properties.getRow(1);
		if(values == null){
			values  = this.properties.createRow(1);
		}
		int counter = names.getLastCellNum();
		if(counter == -1){
			counter = 0;
		}
		Cell cell = names.createCell(counter);
		cell.setCellValue(name);
		cell = values.createCell(counter);
		cell.setCellValue(value.toString());
	
	}

	public void writeBlankRow() {
		this.createrow();
	}

	
	public void writeRow(Collection<Object> values) {
		this.writeRow(values.toArray());
	}
	

	public void writeRow(Object[] values) {
		Cell cell;
		final ExcelColumnMeta sheetExcelColumnMeta = currentSheetMeta.getSheetExcelColumnMeta();
		if(this.currentSheetMeta.getSheetExcelColumnMeta() != null){
			String value = null;
			for (int counter = 0; counter < values.length; counter++) {
				ExcelColumnMeta field = currentSheetMeta.getField(counter);
				if(field == sheetExcelColumnMeta){
					if(values[counter] == null){
						value = "无" + field.getColumnName();
					}else{
						value = values[counter].toString();
					}
					break;
				}
			}
			if(this.sheets.containsKey(value)){
				this.switchToSheet(value);
			}else{
				this.createSheet(value,currentSheetMeta);
			}
		}

		List<ExcelColumnMeta> metas = currentSheetMeta.getFields();

		Row row = this.createrow();
		for (int counter = 0; counter < values.length; counter++) {
			Object value = values[counter];
			cell = row.createCell(counter );
			if(metas != null && counter < metas.size()){
				ExcelColumnMeta field = metas.get(counter);
				CellStyle  cs = null;
				String comment = null;
				if (field.getCellRenderer() != null) {
					AbsCellRenderer cellRenderer = field.getCellRenderer();
					comment = cellRenderer.commentRender(values,value, this.currentIndex);
					cs = cellRenderer.cellStyleRender(values,value,workBook,style, this.currentIndex);
					value = cellRenderer.contentRender(values,value, this.currentIndex);
				}
				if (cs == null) {
					cs = style.getCellStyle(field, this.currentIndex);
				}

				this.fillCell(cell,value, comment, field,cs);
			}else{
				this.fillCell(cell, value, null, ExcelTypeEnum.TEXT, null);
			}
		}
	}
	
	public void writeRows(Object[][] values) {
		for (int i = 0; i < values.length; i++) {
			this.writeRow(values[i]);
		}
	}
	
	public void writeRows(Collection<? extends Collection<Object>> values) {
		for (Collection<Object> collection : values) {
			this.writeRow(collection.toArray());
		}
	}

	
	public void write(int rowIndex,int colIndex,String value,String comment){
		ExcelColumnMeta field = getExcelColumnMeta(colIndex);
		Cell cell = this.getCell(rowIndex, colIndex);
		this.fillCell(cell, value, comment,  field, style.getCellStyle(field, rowIndex));
	}

	private ExcelColumnMeta getExcelColumnMeta(int colIndex) {
		ExcelColumnMeta field = currentSheetMeta.getField(colIndex);
		return field;
	}

	public void writeFormula(int rowIndex,int colIndex,String formula,String comment){
		ExcelColumnMeta field = getExcelColumnMeta(colIndex);
		Cell cell = this.getCell(rowIndex, colIndex);
		cell.setCellStyle(style.getCellStyle(field, rowIndex));
		cell.setCellFormula(formula);
		if(comment != null){
			cell.setCellComment(createComment(cell,comment));
		}
		hasFormula.put(currentSheet, true);
	}

	public void writeFormulaForAllSheet(int colIndex,String formula,String comment){
		for (Sheet sheet : sheets.values()) {
			this.switchToSheet(sheet.getSheetName());
			formula = formula.replaceAll(ExcelSign.EXCEL_LAST_ROW_IDX_HOLDER.getString(),String.valueOf(sheetRowIndex.get(currentSheet)));
			createrow(true);
			ExcelColumnMeta field = getExcelColumnMeta(colIndex);
			int rowIndex = sheet.getLastRowNum();
			Cell cell = this.getCell(rowIndex, colIndex);
			cell.setCellStyle(style.getCellStyle(field, rowIndex));
			cell.setCellFormula(formula);
			if(comment != null){
				cell.setCellComment(createComment(cell,comment));
			}
			hasFormula.put(sheet, true);
		}
	}
	
	public void writeLocked(int rowIndex,int colIndex,String value,String comment){
		ExcelColumnMeta field = getExcelColumnMeta(colIndex);
		Cell cell = this.getCell(rowIndex, colIndex);
		this.fillCell(cell, value, comment,  field, style.getLockedCellStyle(field, rowIndex));
	}
	
	public void writeHighlight(int rowIndex,int colIndex,String value,String comment){
		ExcelColumnMeta field = getExcelColumnMeta(colIndex);
		Cell cell = this.getCell(rowIndex, colIndex);
		this.fillCell(cell, value, comment,  field, style.getHighlightCellStyle(field, rowIndex));
	}
	
	
	public void write(int rowIndex,ExcelColumnMeta field,String value,String comment){
		List<ExcelColumnMeta> fields = currentSheetMeta.getFields();
		int colIndex = -1;
		for (int i = 0; i < fields.size(); i++) {
			ExcelColumnMeta field2 = fields.get(i);
			if(field == field2){
				colIndex = i;
				break;
			}
		}
		Cell cell = this.getCell(rowIndex, colIndex);
		this.fillCell(cell, value, comment,  field, style.getCellStyle(field, rowIndex));
	}
	
	public void writeFormula(int rowIndex,ExcelColumnMeta field,String formula,String comment){
		hasFormula.put(currentSheet, true);
		List<ExcelColumnMeta> fields = currentSheetMeta.getFields();
		int colIndex = -1;
		for (int i = 0; i < fields.size(); i++) {
			ExcelColumnMeta field2 = fields.get(i);
			if(field == field2){
				colIndex = i;
				break;
			}
		}
		Cell cell = this.getCell(rowIndex, colIndex);
		cell.setCellStyle(style.getCellStyle(field, rowIndex));
		cell.setCellFormula(formula);
		if(comment != null){
			cell.setCellComment(createComment(cell,comment));
		}
	}
	
	public void writeLocked(int rowIndex,ExcelColumnMeta field,String value,String comment){
		List<ExcelColumnMeta> fields = currentSheetMeta.getFields();
		int colIndex = -1;
		for (int i = 0; i < fields.size(); i++) {
			ExcelColumnMeta field2 = fields.get(i);
			if(field == field2){
				colIndex = i;
				break;
			}
		}
		Cell cell = this.getCell(rowIndex, colIndex);
		this.fillCell(cell, value, comment,  field, style.getLockedCellStyle(field, rowIndex));
	}
	
	public void writeHighlight(int rowIndex,ExcelColumnMeta field,String value,String comment){
		List<ExcelColumnMeta> fields = currentSheetMeta.getFields();
		int colIndex = -1;
		for (int i = 0; i < fields.size(); i++) {
			ExcelColumnMeta field2 = fields.get(i);
			if(field == field2){
				colIndex = i;
				break;
			}
		}
		Cell cell = this.getCell(rowIndex, colIndex);
		this.fillCell(cell, value, comment,  field, style.getHighlightCellStyle(field, rowIndex));
	}

	private Cell getCell(int rowIndex,int colIndex){
		Row row = currentSheet.getRow(rowIndex);
		if(row == null){
			row = this.createrow();
		}
		Cell cell = row.getCell(colIndex);
		if(cell == null){
			cell = row.createCell(colIndex);
		}
		return cell;
	}
	
	
	public void write2File( ){
		try {
			File file = FileUtils.createFile(this.file);
			try (OutputStream out = new FileOutputStream(file)) {
				this.write2OutputStream(out);
			}
		} catch (Exception ex) {
			throw new RuntimeException("Excel写入文件失败", ex);
		}
	}
	
	public void write2File(String filename) {
		this.file = filename;
		this.write2File();
	}
	
	
	public void write2OutputStream(OutputStream out) {
		try {
			if(workBook != null){
				for (Entry<Sheet, Boolean> entry : hasFormula.entrySet()) {
					entry.getKey().setForceFormulaRecalculation(true);
				}
				this.workBook.write(out);
				out.flush();
				// 调用者负责关闭 OutputStream
			}
		} catch (Exception ex) {
			throw new RuntimeException("Excel写入输出流失败", ex);
		}
	}

	/**
	 * 关闭 ExcelCreater，释放 Workbook 资源
	 * 对于 SXSSFWorkbook，会调用 dispose() 清理临时文件
	 */
	@Override
	public void close() {
		if (this.workBook != null) {
			if (this.workBook instanceof SXSSFWorkbook) {
				((SXSSFWorkbook) this.workBook).dispose();
			}
			this.workBook = null;
			this.currentSheet = null;
			this.sheets.clear();
			this.sheetMetas.clear();
			this.sheetRowIndex.clear();
			this.sheetColMaxWidth.clear();
			this.hasFormula.clear();
			this.drawings.clear();
		}
	}


	public void setCellRef(String name,int colIndex,List<String> values) {
		if (currentSheet instanceof XSSFSheet ){
			XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper((XSSFSheet) currentSheet);
			XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) dvHelper.createExplicitListConstraint(values.toArray(new String[0]));
			CellRangeAddressList addressList = new CellRangeAddressList(currentIndex, currentSheetMeta.getMaxRowPreSheet(), colIndex, colIndex);
			XSSFDataValidation validation = (XSSFDataValidation) dvHelper.createValidation(dvConstraint, addressList);
			//设置输入信息提示信息
			validation.createPromptBox("下拉选择提示", "请使用下拉方式选择合适的值！");
			//设置输入错误提示信息
			validation.createErrorBox("选择错误提示", "你输入的值未在备选列表中，请下拉选择合适的值！");
			currentSheet.addValidationData(validation);
		}else {
			CellRangeAddressList regions = new CellRangeAddressList(currentIndex, currentSheetMeta.getMaxRowPreSheet(), colIndex, colIndex);
			DVConstraint constraint = DVConstraint.createExplicitListConstraint(values.toArray(new String[0]));
			HSSFDataValidation validation = new HSSFDataValidation(regions,constraint);
			//设置输入信息提示信息
			validation.createPromptBox("下拉选择提示", "请使用下拉方式选择合适的值！");
			//设置输入错误提示信息
			validation.createErrorBox("选择错误提示", "你输入的值未在备选列表中，请下拉选择合适的值！");
			currentSheet.addValidationData(validation);
		}

	}

	/*
	 * 获取指定表单上 指定矩形范围的表达式
	 * 公式=[123.xlsx]'Sheet1'!$A$1:$AA$5，表示引用工作簿123.xlsx的Sheet1工作表的A列第2行 到AA列第6行的矩形范围。
	 * @param sheetName
	 * @param beginCol
	 * @param beginLine
	 * @param colRange  列跨度 为空则指第一个单元格
	 * @param lineRange 行跨度 为空则指第一个单元格
	 * @return
	 */
	private String getArea(String sheetName,int beginCol,int beginLine,Integer colRange,Integer lineRange){
		StringBuilder sb = new StringBuilder();
		if (sheetName != null) {
			sb.append("'").append(sheetName).append("'").append("!");
		}
		sb.append("$").append(this.getColumnNo(beginCol)).append("$").append(beginLine+1);
		if (colRange != null || colRange != lineRange ){
			sb.append(":").append("$").append(this.getColumnNo(beginCol-1 + (colRange == null? 0:colRange))).append("$").append(beginLine+1+ (lineRange == null? 0:lineRange));
		}
		return  sb.toString();
	}

	/*
	 * 获取一列对应的字母。例如：ColumnNum=0，则返回值为A 列号转字母
	 */
	private String getColumnNo(int columnNum) {
		return ExcelUtils.columnNumberToName(columnNum);
	}


	public ExcelSign getType() {
		return type;
	}

	public Workbook getWorkBook() {
		return workBook;
	}


	public void lockCurrentSheet(String password){
		if (!currentSheet.getProtect()) {
			this.currentSheet.protectSheet(password);
		}
	}
	
}
