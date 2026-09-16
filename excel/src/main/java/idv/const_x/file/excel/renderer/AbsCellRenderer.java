package idv.const_x.file.excel.renderer;

import idv.const_x.file.excel.style.IStyleGenerator;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * 单元格渲染器
 * 
 *
 * @author const.x
 */
public abstract class AbsCellRenderer<T> {
	

	
	/**
	 * 需要写入到单元格数据
	 * 
	 * @param entity  当前实体
	 * @param value    需渲染的值
	 * @param field    需渲染的字段名
	 * @param row      当前excel行索引
	 * @return
	 *
	 * @author const.x
	 * @throws Exception 
	 * @createDate 2015年6月4日
	 */
	public abstract Object contentRender(final T entity,Object value,final int row);


	/**
	 * 单元格数据注释内容
	 *
	 * @param entity  当前实体
	 * @param value    需渲染的值
	 * @param field    需渲染的字段名
	 * @param row      当前excel行索引
	 * @return
	 *
	 * @author const.x
	 * @throws Exception
	 * @createDate 2015年6月4日
	 */
	public String commentRender(final T entity,Object value,final int row){
		return null;
	}
	/**
	 *  子类可覆盖此方法以实现对单元格样式的处理
	 * 
	 * @param entity  当前实体
	 * @param value    当前单元格值
	 * @param style    当前单元格的样式对象
	 * @param row      当前excel行索引
	 *
	 * @author const.x
	 * @createDate 2015年6月4日
	 */
	public CellStyle cellStyleRender(final T entity, final Object value, final Workbook book, IStyleGenerator style, final int row){
		return null;
	}

}
