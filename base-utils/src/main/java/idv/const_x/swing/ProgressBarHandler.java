package idv.const_x.swing;

import javax.swing.*;

public class ProgressBarHandler {
	JLabel mainlabel,sublabel;
	JProgressBar main,sub;
	JTextArea logOutput;
	JFrame window;
	
	ProgressBarHandler(JProgressBar main,JProgressBar sub,JLabel mainlabel,JLabel sublabel,JTextArea logOutput,JFrame window){
		this.main = main;
		this.sub = sub;
		this.mainlabel = mainlabel;
		this.sublabel = sublabel;
		this.logOutput = logOutput;
		this.window = window;
	}
	
	public void startMainProgress(String msg, Integer max, Integer begin){
		this.mainlabel.setText(msg);
		if(max == null || max < 0 ){
			this.main.setIndeterminate(true);
		}else{
			this.main.setIndeterminate(false);
			this.main.setStringPainted(true);
			this.main.setMaximum(max);
			if(begin != null && begin >= 0 ){
				this.main.setValue(begin);
			}
		}
		sublabel.getParent().setVisible(false);
		sub.setVisible(false);
	}
	
	public void setMainValue(int value){
		this.main.setValue(value);
	}
	
	public void setMainLabel(String value){
		this.mainlabel.setText(value);
	}
	
	public void startSubProgress(String msg, Integer max, Integer begin){
		sublabel.getParent().setVisible(true);
		sub.setVisible(true);
		this.sublabel.setText(msg);
		if(max == null || max < 0 ){
			this.sub.setIndeterminate(true);
		}else{
			this.sub.setIndeterminate(false);
			this.sub.setStringPainted(true);
			this.sub.setMaximum(max);
			if(begin == null || begin < 0 ){
				this.sub.setValue(begin);
			}
		}
	}
	
	public void setSubValue(int value){
		this.sub.setValue(value);
	}
	
	public void setSubLabel(String value){
		this.sublabel.setText(value);
	}
	
	public void setLog(String value){
		logOutput.getParent().setVisible(true);
		this.logOutput.append(value);
	}
	
	public void stop(){
		window.dispose();
	}
}
