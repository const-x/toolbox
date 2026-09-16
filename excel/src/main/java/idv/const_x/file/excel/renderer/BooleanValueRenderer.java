package idv.const_x.file.excel.renderer;

public class BooleanValueRenderer<T> extends AbsCellRenderer<T>{

	@Override
	public Object contentRender(T entity, Object value, int row) {
		if(value == null){
			return "";
		}
		if(value instanceof Boolean){
			return (Boolean)value?"是":"否";
		}
		String str = String.valueOf(value);
		if(str == null || str.equals("")){
			return "";
		}
		Integer val = Integer.valueOf(str);
		if(val == 1){
			return "是";
		}else{
			return "否";
		}
	}

	@Override
	public String commentRender(T entity, Object value, int row) {
		return null;
	}


}
