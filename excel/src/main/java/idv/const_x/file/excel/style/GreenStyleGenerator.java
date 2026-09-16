package idv.const_x.file.excel.style;

import idv.const_x.file.excel.ExcelColumnMeta;
import idv.const_x.file.excel.ExcelCreater;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;

public class GreenStyleGenerator implements IStyleGenerator {

	/**
	 * 创建 GreenStyleGenerator 实例
	 * 注意：每次调用都会创建新实例，避免内存泄漏
	 */
	public static IStyleGenerator getInstance(Workbook workbook){
		return new GreenStyleGenerator(workbook);
	}

	/**
	 * 兼容旧 API 的方法
	 */
	public static IStyleGenerator getInstance(ExcelCreater creater){
		return getInstance(creater.getWorkBook());
	}

	private CellStyle odd;

	private CellStyle even;

	private CellStyle lockedOdd;

	private CellStyle lockedEven;

	private CellStyle highlightOdd;

	private CellStyle highlightEven;

	private CellStyle head;
	
	private final Workbook workBook;
	
	short oddLineColor = HSSFColor.LIGHT_TURQUOISE.index;

	short evenLineColor = HSSFColor.LIGHT_GREEN.index;
	
	short oddBorderColor = HSSFColor.LIME.index;

	short evenBorderColor = HSSFColor.LIME.index;
	
	short headBorderColor = HSSFColor.GREEN.index;

	
	short headFontColor = HSSFColor.WHITE.index;
	
	short fontColor = HSSFColor.TEAL.index;
	
	short highlightFontColor = HSSFColor.RED.index;
	
	public GreenStyleGenerator(Workbook workbook){
		this.workBook = workbook;
	}

	
	private CellStyle getCellStyle(Workbook workBook){
		CellStyle style = workBook.createCellStyle();
		style.setBorderBottom(CellStyle.BORDER_NONE);
		style.setBorderTop(CellStyle.BORDER_NONE);
		style.setBorderLeft(CellStyle.BORDER_NONE);
		style.setBorderRight(CellStyle.BORDER_THIN);
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setShrinkToFit(false);
		style.setWrapText(true);
		style.setVerticalAlignment(CellStyle.VERTICAL_TOP);
		return style;
	}
	
	@Override
	public CellStyle getCellStyle(ExcelColumnMeta field, int rowindex) {
		if (rowindex % 2 == 0) {
			if (even == null) {
				even = this.getCellStyle(workBook);
				Font font = workBook.createFont();
				font.setColor(fontColor);
				even.setLeftBorderColor(evenBorderColor);
				even.setRightBorderColor(evenBorderColor);
				even.setFillForegroundColor(evenLineColor);
				even.setFont(font);
			}
			return even;
		} else {
			if (odd == null) {
				odd = this.getCellStyle(workBook);
				Font font = workBook.createFont();
				font.setColor(fontColor);
				odd.setLeftBorderColor(oddBorderColor);
				odd.setRightBorderColor(oddBorderColor);
				odd.setFillForegroundColor(oddLineColor);
				odd.setFont(font);
			}
			return odd;
		}
	}

	@Override
	public CellStyle getHeadCellStyle(ExcelColumnMeta fields) {
		if (head == null) {
			head = this.getCellStyle(workBook);
			Font font = workBook.createFont();
			font.setBoldweight(Font.BOLDWEIGHT_BOLD);
			font.setColor(headFontColor);
			head.setAlignment(CellStyle.ALIGN_CENTER);
			head.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
			head.setLeftBorderColor(headBorderColor);
			head.setRightBorderColor(headBorderColor);
			head.setFillForegroundColor(HSSFColor.TEAL.index);
			head.setFont(font);
		}
		return head;
	}

	@Override
	public CellStyle getLockedCellStyle(ExcelColumnMeta fields,int rowindex) {
		if (rowindex % 2 == 0) {
			if (lockedEven == null) {
				lockedEven = this.getCellStyle(workBook);
				Font font = workBook.createFont();
				font.setColor(fontColor);
				lockedEven.setLeftBorderColor(evenBorderColor);
				lockedEven.setRightBorderColor(evenBorderColor);
				lockedEven.setFillForegroundColor(HSSFColor.YELLOW.index);
				lockedEven.setFont(font);
				lockedEven.setLocked(true);
			}
			return lockedEven;
		} else {
			if (lockedOdd == null) {
				lockedOdd = this.getCellStyle(workBook);
				Font font = workBook.createFont();
				font.setColor(fontColor);
				lockedOdd.setLeftBorderColor(oddBorderColor);
				lockedOdd.setRightBorderColor(oddBorderColor);
				lockedOdd.setFillForegroundColor(HSSFColor.YELLOW.index);
				lockedOdd.setFont(font);
				lockedOdd.setLocked(true);
			}
			return lockedOdd;
		}
	}

	@Override
	public CellStyle getHighlightCellStyle(ExcelColumnMeta fields,int rowindex) {
		if (rowindex % 2 == 0) {
			if (highlightEven == null) {
				highlightEven = this.getCellStyle(workBook);
				Font font = workBook.createFont();
				font.setColor(HSSFColor.RED.index);
				highlightEven.setLeftBorderColor(evenBorderColor);
				highlightEven.setRightBorderColor(evenBorderColor);
				highlightEven.setFillForegroundColor(evenLineColor);
				highlightEven.setFont(font);
			}
			return highlightEven;
		} else {
			if (highlightOdd == null) {
				highlightOdd = this.getCellStyle(workBook);
				Font font = workBook.createFont();
				font.setColor(highlightFontColor);
				highlightOdd.setLeftBorderColor(oddBorderColor);
				highlightOdd.setRightBorderColor(oddBorderColor);
				highlightOdd.setFillForegroundColor(oddLineColor);
				highlightOdd.setFont(font);
			}
			return highlightOdd;
		}
	}

	@Override
	public boolean isDisplayGridlines() {
		return false;
	}

	@Override
	public boolean isPrintGridlines() {
		return false;
	}
}
