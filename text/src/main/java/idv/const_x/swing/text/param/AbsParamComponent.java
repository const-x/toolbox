package idv.const_x.swing.text.param;

import javax.swing.*;

public abstract class AbsParamComponent<T> {
	private final String key;
	private String description;
	private boolean visable = true;
	private boolean enabled = true;
	
	private T defaultValue;
	
	private JComponent ui = null;
	
	public AbsParamComponent(String key){
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public T getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(T defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	public JComponent getUI(){
		if(ui == null){
			ui = this.initUI();
			ui.setEnabled(enabled);
			ui.setVisible(visable);
		}
		return ui;
	}

    protected abstract JComponent initUI();
	
	public abstract T getValue();
	
	public abstract void setValue(T value);
	
	public void setVisable(boolean visable){
		this.visable = visable;
		if(ui != null){
			ui.setVisible(visable);
		}
	}

	public void setEnabled(boolean enabled){
		this.enabled = enabled;
		if(ui != null){
			ui.setEnabled(enabled);
		}
	}
	
	public abstract void clear();
	
}
