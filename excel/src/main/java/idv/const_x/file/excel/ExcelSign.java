package idv.const_x.file.excel;

public enum ExcelSign {
	
	EXCEL_TYPE_XLS("xls"),

	EXCEL_TYPE_XLSX("xlsx"),

	PROPERTIES_SHEET_NAME("properties_hidden"),


	EXCEL_CUR_ROW_IDX_HOLDER("cur_row_holder"),

	EXCEL_LAST_ROW_IDX_HOLDER("last_row_holder"),

	;
	private final String value;

	ExcelSign(String value) {
		this.value = value;
	}

	public String getString() {
		return this.value;
	}

}
