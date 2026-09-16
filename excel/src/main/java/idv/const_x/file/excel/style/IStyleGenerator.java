package idv.const_x.file.excel.style;

import idv.const_x.file.excel.ExcelColumnMeta;
import org.apache.poi.ss.usermodel.CellStyle;

public interface IStyleGenerator {
	
   
	CellStyle getCellStyle(ExcelColumnMeta field, int rowindex);
	
	CellStyle getLockedCellStyle(ExcelColumnMeta field, int rowindex);
	
	CellStyle getHighlightCellStyle(ExcelColumnMeta field, int rowindex);
	
	CellStyle getHeadCellStyle(ExcelColumnMeta field);

	/**
	 * 是否隐藏Excel网格线
	 * @return
	 */
	boolean isDisplayGridlines();

	/**
	 * 打印时是否打印Excel网格线
	 * @return
	 */
	boolean isPrintGridlines();
}
