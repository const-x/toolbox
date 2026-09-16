package idv.const_x.file.excel.renderer;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberRenderer extends AbsCellRenderer<String>{

	private int scale = -1;
	
	public NumberRenderer(){
		
	}
	
    public NumberRenderer(int scale){
		this.scale = scale;
	}


	@Override
	public Object contentRender(String entity, Object value, int row) {
		if(value == null || StringUtils.isBlank(value.toString())){
			return null;
		}
		if(scale < 0){
			return value;
		}
		BigDecimal b = new BigDecimal(value.toString());
		Double f1 = b.setScale(scale, RoundingMode.HALF_UP).doubleValue();
		return f1.toString();
	}

	@Override
	public String commentRender(String entity, Object value, int row) {
		return null;
	}

	
}
