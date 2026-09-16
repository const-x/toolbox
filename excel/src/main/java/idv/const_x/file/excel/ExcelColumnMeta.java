package idv.const_x.file.excel;

import idv.const_x.file.excel.renderer.AbsCellRenderer;

import java.util.List;

/**
 * excel表头定义
 *
 * @author const.x
 */
public class ExcelColumnMeta<T>  {
	
	/**excel列名*/
	 String columnName;
	/**excel列索引*/
	private Integer index;
	/**excel列说明*/
	private String tips;
	/**是否显示/隐藏列*/
	private boolean visable;
	/**值类型*/
	private ExcelTypeEnum type = ExcelTypeEnum.TEXT;
	/**可选值范围*/
	private List<Object> selects;

	/**单元格渲染器*/
	private AbsCellRenderer<T>  cellRenderer;
	
	public ExcelColumnMeta(String columnName){
		this.columnName = columnName;
	}
	
	
	public ExcelColumnMeta(String columnName, ExcelTypeEnum type){
		this.columnName = columnName;
		this.type = type;
	}
	
	public ExcelColumnMeta(String columnName, ExcelTypeEnum type, String tips){
		this.columnName = columnName;
		this.type = type;
		this.tips = tips;
	}

	/** 
	* 设置 excel列名
	* @param String
	*/ 
	public void setColumnName(String value) {
		this.columnName = value; 
	} 

	/** 
	 * 获取 excel列名
	 * @return String 
	 */ 
	public String getColumnName() {
		return  this.columnName; 
	} 

	/** 
	* 设置 excel列索引
	* @param Integer
	*/ 
	public void setIndex(Integer value) {
		this.index = value; 
	} 

	/** 
	 * 获取 excel列索引
	 * @return Integer 
	 */ 
	public Integer getIndex() {
		return  this.index; 
	} 

	/** 
	* 设置 excel列说明
	* @param String
	*/ 
	public void setTips(String value) {
		this.tips = value; 
	} 

	/** 
	 * 获取 excel列说明
	 * @return String 
	 */ 
	public String getTips() {
		return  this.tips; 
	} 

	/** 
	* 设置 是否显示隐藏列
	* @param boolean
	*/ 
	public void setVisable(boolean value) {
		this.visable = value; 
	} 

	/** 
	 * 是否 是否显示隐藏列
	 * @return boolean 
	 */ 
	public boolean getVisable() {
		return  this.visable; 
	} 



	/** 
	* 设置 值类型
	* @param ExcelTypeEnum
	*/ 
	public void setType(ExcelTypeEnum value) {
		this.type = value; 
	} 

	/** 
	 * 获取 值类型
	 * @return ExcelTypeEnum 
	 */ 
	public ExcelTypeEnum getType() {
		return  this.type; 
	}

	public List<Object> getSelects() {
		return selects;
	}

	public void setSelects(List<Object> selects) {
		this.selects = selects;
	}

	public AbsCellRenderer<T>  getCellRenderer() {
		return cellRenderer;
	}

	public void setCellRenderer(AbsCellRenderer<T>  cellRenderer) {
		this.cellRenderer = cellRenderer;
	}
}
