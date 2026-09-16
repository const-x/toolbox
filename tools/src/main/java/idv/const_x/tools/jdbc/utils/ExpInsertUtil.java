package idv.const_x.tools.jdbc.utils;

import idv.const_x.file.FileOutWriter;
import idv.const_x.tools.jdbc.JDBCConnection;
import idv.const_x.tools.jdbc.info.AbsDBInfo;
import idv.const_x.tools.jdbc.table.GeneratorFactory;
import idv.const_x.tools.jdbc.table.ITableGenerator;
import idv.const_x.tools.jdbc.table.meta.Field;
import idv.const_x.utils.OSUtils;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpInsertUtil {

	
	
	public static void main(String[] args){
		ExpInsertUtil util = new ExpInsertUtil();
//		util.setSchma(SchmeEnum.TMS.getSchme());
////		util.setRenderer("ID", new IDSeqRenderer("SEQ_SECURITY_MODULE"));
////
////		util.setRenderer("PARENT_ID", new IValueRenderer(){
////			@Override
////			public Object renderValue(Map<String, Object> record, Object value) {
////				return "SELECT ID from SECURITY_MODULE where sn = '' ";
////			}
////		});
		
		util.setTypeRenderer("TIMESTAMP(6)", new TimestampRenderer());
		util.setPageSize(300);
		util.export("AREA_AREA", " code not like '43%' and id <> 0 ", null);
	}
	
	
	
	
	
	Map<String,IValueRenderer> valueRenderers = new HashMap<String,IValueRenderer>();
	
	Map<String,IValueRenderer> typeRenderers = new HashMap<String,IValueRenderer>();
	
	private FileOutWriter writer;
	private Connection connnection;
	private String aim = OSUtils.getDesktopPath();
	private int pageSize = -1;
	
    private AbsDBInfo schma;
	
	public String getAim() {
		return aim;
	}
	public void setAim(String aim) {
		this.aim = aim;
	}
	public AbsDBInfo getSchma() {
		return schma;
	}
	public void setSchma(AbsDBInfo schma) {
		this.schma = schma;
	}
	public void setRenderer(String column,IValueRenderer renderer){
		valueRenderers.put(column.toLowerCase(), renderer);
	}
	public void setTypeRenderer(String columnType,IValueRenderer renderer){
		typeRenderers.put(columnType.toLowerCase(), renderer);
	}
	
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
	public void export(String table, String where, List<String> columns) {
		try{
			
			ITableGenerator generator = GeneratorFactory.getTableGenerator(schma.getType());
			List<Field> allFields = generator.getFields(table, schma);
			List<Field> fields = null;
			if (columns != null) {
				fields = new ArrayList<Field>(columns.size());
				for (Field field : allFields) {
					if (columns.contains(field.getColumn())) {
						fields.add(field);
					}
				}
			} else {
				fields = allFields;
			}
			StringBuilder insertBuilder = new StringBuilder("insert into ");
			insertBuilder.append(table).append(" ( ");
			for (Field field : fields) {
				insertBuilder.append(field.getColumn()).append(",");
			}
			insertBuilder.deleteCharAt(insertBuilder.length() - 1);
			insertBuilder.append("　) ");
			String insertSql = insertBuilder.toString();

			String sql = this.buildeSql(fields, table, where);
			int page = 1;
			List<Map<String, Object>> res = null;
			
			while (true) {
				if (this.connnection == null) {
					connnection = JDBCConnection.getConnection(schma);
				}
				try {
					if(pageSize > 0){
						String pageSql = generator.wapperPageSql(sql, page, pageSize);
						res = QueryUtils.query(pageSql, connnection);
						if(writer != null){
							writer.close();
						}
						writer = new FileOutWriter(aim + File.separator + table + File.separator + page+ ".sql", true);
					}else{
						res = QueryUtils.query(sql, connnection);
						if(writer == null){
							writer = new FileOutWriter(aim + File.separator + table + ".sql", true);
						}
					}
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
				if (res.size() == 0) {
					break;
				}
				for (Map<String, Object> record : res) {
					StringBuilder insert = new StringBuilder(insertSql);
					insert.append(" values (");
					for (Field field : fields) {
						Object value = record.get(field.getColumn());
						String column = field.getColumn().toLowerCase();
						String type = field.getType().getJdbcType().toLowerCase();
						
						if(valueRenderers.containsKey(column)){
							IValueRenderer renderer = valueRenderers.get(column);
							insert.append(renderer.renderValue(record, value)).append(",");
							continue;
						}
						if(typeRenderers.containsKey(type)){
							IValueRenderer renderer = typeRenderers.get(type);
							insert.append(renderer.renderValue(record, value)).append(",");
							continue;
						}
						if(value == null || value.toString().equalsIgnoreCase("null")){
							insert.append("NULL,");
							continue;
						}
						if(type.equalsIgnoreCase("DECIMAL")|| type.equalsIgnoreCase("NUMBER")|| type.equalsIgnoreCase("INTEGER")){
							insert.append(value).append(",");
						}else{
							insert.append("'").append(value).append("',");
						}
					}
					insert.deleteCharAt(insert.length() - 1);
					insert.append("　);");
					writer.writeLine(insert.toString());
				}
				System.out.println("已处理 "+page + "页共 " + ((page-1)*pageSize + res.size()) +"条数据");
				if (res.size() < pageSize || pageSize <=0 ) {
					break;
				}
				page++;
			}
		}finally{
			try {
				writer.close();
				this.connnection.close();
			} catch (SQLException e) {
			}
		}
	}
	
	
	private String buildeSql(List<Field> fields,String table,String where){
		StringBuilder sb = new StringBuilder("select  ");
		for(Field field : fields){
			sb.append(field.getColumn()).append(",");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(" from ").append(table);
		if(where != null){
			sb.append(" where ").append(where);
		}
		return sb.toString();
	}
	
}

interface IValueRenderer{
	Object renderValue(Map<String,Object> record,Object value);
}

class IDSeqRenderer implements IValueRenderer{
	private String seqName = null;
	
	public IDSeqRenderer(String seqName){
		this.seqName = seqName;
	}

	@Override
	public Object renderValue(Map<String, Object> record, Object value) {
		return "'selecet "+seqName+".NEXTVAL'";
	}
	
}

class TimestampRenderer implements IValueRenderer{

	@Override
	public Object renderValue(Map<String, Object> record, Object value) {
		if(value == null){
			return "NULL";
		}
		String s = value.toString();
		if(s.endsWith(".0")){
			s = s.substring(0,s.length() -2);
		}
		return "TO_DATE('" +s +"','YYYY-MM-DD HH24:MI:SS')";
	}
	
}
