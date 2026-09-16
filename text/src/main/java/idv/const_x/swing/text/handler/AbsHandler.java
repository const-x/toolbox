package idv.const_x.swing.text.handler;

import idv.const_x.swing.text.IPrinter;
import idv.const_x.swing.text.param.AbsParamComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbsHandler{
	
	private IPrinter printer;


	public AbsHandler() {
	}

	private List<AbsParamComponent> params = null;
	
	public abstract void handle(String input,Map<String,Object> params) throws Exception;
	
	public abstract String  getName();
	
	public  boolean  canExit(){
		return false;
	}
	
	public abstract String  getDescription();
	
	public List<AbsParamComponent> getParamsComponent(){
		return params;
	}
	
	protected void addParamComponent(AbsParamComponent p){
		if(params == null){
			params =  new ArrayList<>();
		}
		params.add(p);
	}

	protected void block(String msg){
		throw new RuntimeException(msg);
	}
	
	public void print(String str,String desc) {
		printer.print(str,desc);
	}

	public void printLog(String str) {
		printer.printLog(str);
	}
	
	public void clear() {
		printer.clear();
	}

	public void clearLog() {
		printer.clearLog();
	}

	public void printWarn(String str) {
		printer.printWarn(str);
	}

	public void clearWarn() {
		printer.clearWarn();
	}

	public void printSuccess(String str) {
		printer.printSuccess(str);
	}

	public void printSuccess() {
		printer.printSuccess(null);
	}

	public void setPrinter(IPrinter printer) {
		this.printer = printer;
	}
}


