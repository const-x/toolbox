package idv.const_x.swing.text.param;

public class BooleanParamComponent extends ComboBoxParamComponent<Boolean> {
	
	public BooleanParamComponent(String key){
		super(key);
		super.addItem("是", true);
		super.addItem("否", false);
	}


	@Override
	public void addItem(String name, Boolean value) {
		
	}



}
