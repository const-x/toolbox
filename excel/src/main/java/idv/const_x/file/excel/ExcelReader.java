package idv.const_x.file.excel;

import idv.const_x.utils.DateUtils;
import idv.const_x.utils.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 读取excel文件
 *
 * @since 6.3
 * @version 2014-03-05 14:38:18
 * @author const.x
 */
public class ExcelReader implements AutoCloseable {

	private Workbook book;

	private int currentIndex = -1;

	private Map<String, String> properties;

	private Sheet sheet;

	private final String filePath;
	
	private boolean edited = false;

	public ExcelReader(String path) {
		FileInputStream input = null;
		filePath = path;
		try {
			input = new FileInputStream(path);
			String type = path.substring(path.lastIndexOf(".") + 1);
			if (type.equalsIgnoreCase(ExcelSign.EXCEL_TYPE_XLS.getString())) {
				this.book = new HSSFWorkbook(input);
			} else {
				this.book = new XSSFWorkbook(input);
			}
			this.loadSheet(0);
		} catch (Exception ex) {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                }
            }
			throw new RuntimeException(ex);
		}
	}

	public boolean loadSheet(String sheetname) {
		return this.loadSheet(sheetname, 0);
	}

	/**
	 * 
	 * @param sheetname 表单名
	 * @param begin     起始行索引（0开始）
	 */
	public boolean loadSheet(String sheetname, int begin) {
		this.sheet = this.book.getSheet(sheetname);
		if (sheet == null) {
			return false;
		}
		currentIndex = begin - 1;
		return true;
	}

	public boolean loadSheet(Integer sheetIdx) {
		return this.loadSheet(sheetIdx, 0);
	}

	/**
	 *
	 * @param sheetIdx 表单名
	 * @param begin     起始行索引（0开始）
	 */
	public boolean loadSheet(Integer sheetIdx, int begin) {
		this.sheet = this.book.getSheetAt(sheetIdx);
		if (sheet == null) {
			return false;
		}
		currentIndex = begin - 1;
		return true;
	}

	public String getCurSheetName(){
		if (sheet == null) {
			return null;
		}
		return this.sheet.getSheetName();
	}

	public boolean hasNext() {
		if (this.sheet == null) {
			return false;
		}
		Row nextRow = this.sheet.getRow(this.currentIndex + 1);
        return nextRow != null && nextRow.getLastCellNum() != -1;
    }

	
	public Object[] nextRow() {
		this.currentIndex++;
		Row row = this.sheet.getRow(currentIndex);
		int end = row.getLastCellNum();
		Object[] values = new Object[end];
		for (int i = 0; i < end; i++) {
			Cell cell = row.getCell(i);
			if (cell == null) {
				values[i] = null;
			} else {
				 if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
					values[i] = cell.getStringCellValue();
				} else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
					values[i] = cell.getBooleanCellValue();
				} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
					if(DateUtil.isCellDateFormatted(cell)){
						Date date = cell.getDateCellValue();
						values[i] = DateUtils.toDateTimeString(date);
					}else{
						values[i] = cell.getNumericCellValue();
					}
				}
				
			}
		}
		return values;
	}

	public void updateCell(int rowIndex, int colIndex, String value) {
		this.updateCell(rowIndex, colIndex, value, null, null, null);
	}

	public void updateCell(int rowIndex, int colIndex, String value,
			Short bgcolor, Short fontColor, Boolean boldness) {
		Row row = this.sheet.getRow(rowIndex);
		if (row == null) {
			row = this.sheet.createRow(rowIndex);
		}
		Cell cell = row.getCell(colIndex);
		if (cell == null) {
			cell = row.createCell(colIndex);
		}
		cell.setCellValue(value);
		CellStyle style =this.getCellStyle(bgcolor, fontColor, boldness);
		if(style != null){
			cell.setCellStyle(style);
		}
		edited = true;
	}

	
	
    private final Map<String,CellStyle> styles = new HashMap<String,CellStyle>();
	
	private CellStyle getCellStyle(Short bgcolor, Short fontColor, Boolean boldness){
		if(bgcolor == null && fontColor == null && boldness == null){
			return null;
		}
		String key = String.valueOf(fontColor)  + String.valueOf(bgcolor) + String.valueOf(boldness) ;
		if(styles.containsKey(key)){
			return styles.get(key);
		}
		CellStyle cellStyle = this.book.createCellStyle();
		if (bgcolor != null) {
			cellStyle.setFillForegroundColor(bgcolor);
		}
		if (fontColor != null || boldness != null) {
			cellStyle.setFont(this.getFont(fontColor, boldness));
		}
		styles.put(key, cellStyle);
		return cellStyle;
	}
	
	private final Map<String, Font> fonts = new HashMap<>();

	private Font getFont(Short fontColor, Boolean boldness) {
		String key = String.valueOf(fontColor)  + String.valueOf(boldness) ;
		if (!fonts.containsKey(key)) {
			Font font = book.createFont();
			if (fontColor != null) {
				font.setColor(fontColor);
			}
			if (boldness != null && boldness) {
				font.setBoldweight(Font.BOLDWEIGHT_BOLD);
			}
			fonts.put(key, font);
			return font;
		}
		return fonts.get(key);
	}
	
	

	public Map<String, String> getProperties() {
		if (this.properties == null) {
			this.properties = new HashMap<String, String>();
			Sheet sheet = book.getSheet(ExcelSign.PROPERTIES_SHEET_NAME
					.getString());
			Row names = sheet.getRow(0);
			Row values = sheet.getRow(1);
			for (int i = 0; i < names.getLastCellNum(); i++) {
				this.properties.put(names.getCell(i).getStringCellValue(),
						values.getCell(i).getStringCellValue());
			}
		}
		return this.properties;
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	
	public void flush() {
		if (!this.edited) {
			return;
		}
		try {
			File file = FileUtils.createFile(filePath);
			try (OutputStream out = new FileOutputStream(file)) {
				this.book.write(out);
			}
			this.edited = false;
		} catch (Exception ex) {
			throw new RuntimeException("Excel文件刷新失败", ex);
		}
	}
	
	public void flush(boolean openfile) {
		this.flush();
		if(openfile){
			try {
				File file = FileUtils.createFile(filePath);
				FileUtils.openFile(file);
			} catch (IOException e) {
				throw new RuntimeException("打开Excel文件失败", e);
			}
		}
	}

	/**
	 * 关闭 ExcelReader，释放 Workbook 资源
	 */
	@Override
	public void close() {
		if (this.book != null) {
			if (this.book instanceof SXSSFWorkbook) {
				((SXSSFWorkbook) this.book).dispose();
			}
			this.book = null;
			this.sheet = null;
		}
	}

}
