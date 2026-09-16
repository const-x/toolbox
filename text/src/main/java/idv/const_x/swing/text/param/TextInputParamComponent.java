package idv.const_x.swing.text.param;

import idv.const_x.swing.LineNumberHeaderView;
import idv.const_x.swing.VFlowLayout;
import idv.const_x.utils.StringExtUtils;

import javax.swing.*;
import javax.swing.text.JTextComponent;

public class TextInputParamComponent extends AbsParamComponent<String> {

	public TextInputParamComponent(String key){
		super(key);
	}
	
	private JTextComponent input = null;

	private int rows = 1;
	
	private int cols = 1;

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getCols() {
		return cols;
	}

	public void setCols(int cols) {
		this.cols = cols;
	}

	@Override
	protected JComponent initUI() {
		JPanel panel = new JPanel(new VFlowLayout() );
		if(StringExtUtils.isNotBlank(getDescription())){
			 JLabel label = new JLabel(getDescription());
		     panel.add(label);
		}
		if (getRows() > 1) {
			JTextArea area = new JTextArea();
			area.setLineWrap(true);
			area.setRows(getRows());
			area.setColumns(cols);
			JScrollPane mainInputJScroll = new JScrollPane(area);
			mainInputJScroll.setRowHeaderView(new LineNumberHeaderView(area));
			input = area;
			panel.add(mainInputJScroll);
		} else {
			input = new JTextField();
			 panel.add(input);
		}
		if (getDefaultValue() != null) {
			input.setText(getDefaultValue());
		}
		return panel;
	}

	@Override
	public String getValue() {
		return input.getText();
	}
	
	
	@Override
	public void setValue(String value) {
		input.setText(value);
		
	}

	@Override
	public void clear() {
		input.setText("");
	}

}
