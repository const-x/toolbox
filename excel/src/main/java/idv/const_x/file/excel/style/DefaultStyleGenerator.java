package idv.const_x.file.excel.style;

import idv.const_x.file.excel.ExcelColumnMeta;
import idv.const_x.file.excel.ExcelCreater;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;


public class DefaultStyleGenerator implements IStyleGenerator {

	private final Workbook workBook;

	private CellStyle head;
	private CellStyle cell;

	/**
	 * 创建 DefaultStyleGenerator 实例
	 * 注意：每次调用都会创建新实例，避免内存泄漏
	 */
	public static IStyleGenerator getInstance(Workbook workbook){
		return new DefaultStyleGenerator(workbook);
	}

	/**
	 * 兼容旧 API 的方法
	 */
	public static IStyleGenerator getInstance(ExcelCreater creater){
		return getInstance(creater.getWorkBook());
	}

	public DefaultStyleGenerator(Workbook workbook){
		this.workBook = workbook;
	}
	
	@Override
	public CellStyle getHeadCellStyle(ExcelColumnMeta field) {
		if (head == null) {
			head = getCellStyle();
			Font font = workBook.createFont();
			font.setBoldweight(Font.BOLDWEIGHT_BOLD);
			head.setAlignment(CellStyle.ALIGN_CENTER);
			head.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
			head.setFillForegroundColor(HSSFColor.YELLOW.index);
			head.setFont(font);
		}
		return head;
	}
	
	@Override
	public CellStyle getCellStyle(ExcelColumnMeta field, int rowindex) {
		if (cell == null) {
			cell = getCellStyle();
		}
		return cell;
	}

	@Override
	public CellStyle getLockedCellStyle(ExcelColumnMeta field, int rowindex) {
		return getCellStyle(field,rowindex);
	}

	@Override
	public CellStyle getHighlightCellStyle(ExcelColumnMeta field, int rowindex) {
		return getCellStyle(field,rowindex);
	}

	private CellStyle getCellStyle(){
		CellStyle style = workBook.createCellStyle();
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBorderTop(CellStyle.BORDER_THIN);
		style.setBorderLeft(CellStyle.BORDER_THIN);
		style.setBorderRight(CellStyle.BORDER_THIN);
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setShrinkToFit(false);
		style.setVerticalAlignment(CellStyle.VERTICAL_TOP);
		return style;
	}

	@Override
	public boolean isDisplayGridlines() {
		return true;
	}

	@Override
	public boolean isPrintGridlines() {
		return true;
	}


}
