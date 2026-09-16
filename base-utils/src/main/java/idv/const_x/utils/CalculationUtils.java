package idv.const_x.utils;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;


public class CalculationUtils {

	// 操作符集合
	private static final Set C_OperatorSet = new HashSet() {

		private static final long serialVersionUID = 1L;

		{
			add('+');
			add('-');
			add('*');
			add('/');
			add('(');
			add(')');
		}
	};

	// 获取优先级
	private static int getOperatorPriority(char ch) {
		if (ch == '+' || ch == '-')
			return 0;
		else if (ch == '*' || ch == '/')
			return 1;
		else
			return -1;
	}

	// 中缀转后缀
	private static String infixToSuffix(String expression) {
		Stack Operators = new Stack<>();
		Operators.clear();
		StringBuilder sBuilder = new StringBuilder();
		for (int i = 0; i < expression.length(); i++) {
			char ch = expression.charAt(i);
			if (ch == ' ')
				continue;
			if (C_OperatorSet.contains(ch)) {
				// 如果操作符栈为空
				if (Operators.empty()) {
					if (ch == ')') { // 只要不是 ） 就入栈
						throw new RuntimeException("表达式错误");
						// System.out.println("括号不匹配");
						// return sBuilder.toString();
					}
					Operators.push(ch);
				} else if (ch == '(') {
					Operators.push(ch);
				} else if (ch == ')') { // 如果是 ） 括号
					char top;
					while ((top = (char) Operators.peek()) != '(') { // 如果当前操作符栈的第一个不是(

						if (Operators.empty()) {
							// System.out.println("括号不匹配");
							// return sBuilder.toString();
							throw new RuntimeException("表达式错误");
						}
						// 把运算符加入sBuilder
						sBuilder.append(top);
						Operators.pop();
					}
					Operators.pop();
				} else { // 如果是运算符
					char top = (char) Operators.peek();
					if (getOperatorPriority(ch) <= getOperatorPriority(top)) { // 如果优先级小于操作符栈中第一个
						while (!Operators.empty()
								&& getOperatorPriority(ch) <= getOperatorPriority(top = (char) Operators.peek())) {
							// 把运算符加入sBuilder
							sBuilder.append(top);
							Operators.pop();
						}
					}
					Operators.push(ch);
				}
			} else { // 如果是数字 加入sBuilder（把[3]的括号去除）
				sBuilder.append("[" + ch);
				while (i + 1 < expression.length()
						&& (((ch = expression.charAt(i + 1)) == '.') || (ch >= '0' && ch <= '9'))) {
					sBuilder.append(ch);
					++i;
				}
				sBuilder.append(']');
			}
		}
		while (!Operators.empty()) {
			if ((char) Operators.peek() == '(') {
				throw new RuntimeException("表达式错误");
				// System.out.println("括号不匹配");
				// return "";
			}
			sBuilder.append(Operators.peek());
			Operators.pop();
		}
		return sBuilder.toString();
	}

	// 处理负号
	public static String validate(String expression) {
		expression = expression.replaceAll(" ","");
		expression = expression.replaceAll("（","(");
		expression = expression.replaceAll("）",")");
		boolean insert = false;
		for (int i = 0; i < expression.length(); i++) {
			char ch = expression.charAt(i);
			//补)
			if (insert && operator(ch)) {
				expression = insert(expression,")",i);
				insert = false;
				i++;
			}
			if (ch == '(' && num(expression.charAt(i - 1))){
				expression = insert(expression,"*",i);
				i++;
			}
			// 遍历表达式，如果遍历到负号
			if (ch == '-' || ch == '+') {
				// 如果负号在第一位
				if (i == 0 && num(expression.charAt(i + 1))) {
					// 直接在前面加0
					expression = "0" + expression;
					i++;
					// 如果负号前面是括号（说明负号在表达式中） 采用的办法是把0插入到负号之前 5*(-3) -> 5*(0-3)
				} else if (expression.charAt(i - 1) == '(' && num(expression.charAt(i + 1))) {
					expression = insert(expression,"0",i);
					i++;
				} else if (operator(expression.charAt(i - 1))) {
					expression = insert(expression,"(0",i);
					insert = true;
					i+=2;
				}
			}
		}
		return expression;

	}

	private static String insert(String s, String ch, int index){
		if (index == 0) {
			return ch +s;
		}
		if (index == s.length() - 1) {
			return s +ch;
		}
		String start = s.substring(0, index);
		String end = s.substring(index);
		return start + ch + end;
	}

	private static boolean num(char ch){
		return ch >= '0' && ch <= '9';
	}


	private static boolean operator(char ch){
		return ch == '+' || ch == '-' || ch == '*'|| ch == '/';
	}


	public static BigDecimal evalExp(String expression){
		return evalExp(expression,null);
	}

	public static BigDecimal evalExp(String expression,Integer scale){
		expression = validate(expression);
		//System.out.println(expression);
		Stack Operands = new Stack<>();
		Operands.clear();
		BigDecimal ret = null;
		String suffix = infixToSuffix(expression);
		int maxScale = 3;
		//System.out.println(suffix);
		for (int i = 0; i < suffix.length(); i++) {
			if (suffix.charAt(i) == '[') {
				i++;
				int beginIndex = i, endIndex = i;
				while (']' != suffix.charAt(i)) {
					i++;
					endIndex++;
				}
				// 取两个括号中间的数字
				BigDecimal decimal = BigDecimal.valueOf(Double.valueOf(suffix.substring(beginIndex, endIndex)));
                //中间结果 保留比输入的最大位数大一位
				if (decimal.scale() > maxScale) {
					maxScale = decimal.scale() +1;
				}
				Operands.push(decimal);
			} else {
				BigDecimal left, right;
				BigDecimal res = null;

				right = (BigDecimal) Operands.peek();
				Operands.pop();

				left = (BigDecimal) Operands.peek();
				Operands.pop();
				switch (suffix.charAt(i)) {
				case '+':
					res = left.add(right);
					break;
				case '-':
					res = left.subtract(right);
					break;
				case '*':
					res = left.multiply(right);
					break;
				case '/':
					res = left.divide(right,9,BigDecimal.ROUND_HALF_UP);
					break;
				}
				Operands.push(res);
			}
		}
		if (Operands.size() > 1) {
			throw new RuntimeException("表达式错误");
		}

		ret = (BigDecimal) Operands.peek();
		Operands.pop();
		if (scale != null) {
			return ret.setScale(scale, BigDecimal.ROUND_HALF_UP);
		}else {
			return ret.setScale(maxScale-1, BigDecimal.ROUND_HALF_UP).stripTrailingZeros();
		}
	}

	public static void main(String[] args) throws Exception {
		//String expression = "+1.031231 - 5*-1/3 + 2(（2(-1+6)）)";
		String expression = "100000/3*0.000000003";
		System.out.println(expression + " = " + evalExp(expression));
	}
}