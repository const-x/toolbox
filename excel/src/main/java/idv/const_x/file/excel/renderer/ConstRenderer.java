package idv.const_x.file.excel.renderer;



public class ConstRenderer<T>  extends AbsCellRenderer<T>{

	private T value;
	
	public ConstRenderer(){
		
	}
	
    public ConstRenderer(T value){
		this.value = value;
	}


	@Override
	public Object contentRender(Object entity, Object value, int row) {
		return value;
	}

	@Override
	public String commentRender(Object entity, Object value, int row) {
		return null;
	}
}
