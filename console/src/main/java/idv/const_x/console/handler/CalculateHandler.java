package idv.const_x.console.handler;

import idv.const_x.console.io.ConsoleIOProxy;
import idv.const_x.utils.CalculationUtils;

import java.util.Map;

public class CalculateHandler extends AbsInputLineHandler {

	@Override
	public void handleLine(String line, ConsoleIOProxy console, Map<String, String> options, String ...params){
		String expression = line;
		expression = expression.substring(getCmd().length());
		expression = expression.replaceAll(" ", "");
		expression = expression.replaceAll("（", "(");
		expression = expression.replaceAll("）", ")");
		// 去除BigDecimal后面的无用0
		try {
			echoResult(console,expression + " = " + CalculationUtils.evalExp(expression).stripTrailingZeros().toPlainString());
		} catch (Exception e) {
			console.error(e.getMessage());
		}
	}

	@Override
	public String getCmd() {
		return "cal";
	}

	@Override
	public HelpBuilder initHelpBuilder() {
		return new HelpBuilder(getCmd(),"数学表达式计算")
				.appendParamDesc("1+1","表达式")
				.build();
	}

}

