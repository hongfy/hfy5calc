
package com.example.hfy;

import java.math.BigDecimal;

public class Expression {
	String mExpression;  //要计算的表达式
	private int mScale = 200;	//除法运算的默认保留位数，只针对除不尽的数

	public int getMScale() {  //mScale的get方法，供其他类读取mScale的值
		return mScale;
	}

	public void setMScale(int scale) { //mScale的set方法，供其他类设置mScale的值
		mScale = scale;
	}

	public Expression(String s) {	//构造函数，new出新对象时，顺便把表达式中多余的空格去掉
		this.mExpression = takeOffBlank(s);
	}

	public String calcuCurrentExpression() {	//计算表达式的类入口，供外部调用
		String calc_result;
		if ("".equals(mExpression))
			return null;
		calc_result = calcuThisExpression(mExpression).toString();
		return calc_result;
	}

	private int findTheLowestPriority(String s) {	//寻找表达式中的最低优先级符号，返回该最低符号在表达式中的位置
		int i = 0, k = 0, min_priority = 99999, p = 0;
		for (i = 0; i < s.length(); i++) {
			switch (s.charAt(i)) {
				case '+':
				case '-':
					if ((i == 0) || (s.charAt(i - 1) == '('))
						break;

					if ((k + 1) <= min_priority) {
						min_priority = k + 1;
						p = i;
					}
					break;
				case '*':
				case '/':
					if ((k + 2) <= min_priority) {
						min_priority = k + 2;
						p = i;
					}
					break;
				case '(':
					k += 10;
					break;
				case ')':
					k -= 10;
					break;
			}
		}
		//if (k != 0)
		//throw new Exception("Parentheses are not matching !");

		return p;
	}

	private String takeOffOuterParenthesis(String s) {	//去除外层对应括号
		int i = 0, k = 0;
		boolean flag = true;
		while ((s.charAt(0) == '(') && ((s.charAt(s.length() - 1) == ')'))
				&& flag) {
			k = 0;
			i = 0;
			flag = false;
			for (i = 0; i < s.length(); i++) {
				switch (s.charAt(i)) {
					case '(':
						k += 10;
						break;
					case ')':
						k -= 10;
						break;
				}
				if (k == 0)
					break;
			}
			if (i == (s.length() - 1)) {
				flag = true;
				s = s.substring(1, s.length() - 1);
			}
		}
		return s;
	}

	private String takeOffBlank(String s) {	//去除表达式多余空格
		String c = new String("");
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) != ' ')
				c = c + s.charAt(i);
		}
		return c;
	}

	public BigDecimal calcuThisExpression(String s){	//表达式计算的核心函数，返回一个BigDecimal类

		BigDecimal calc_result = null;	//calc_result用于记录表达式计算的结果

		int lowest_priority = 0;	//用于记录最低优先级
		s = takeOffOuterParenthesis(s);	//主要步骤1: 去除外层括号

		lowest_priority = findTheLowestPriority(s);	//主要步骤2: 找到最低优先级的符号
		if (lowest_priority == 0) {	//主要步骤4: 若优先级为0，则说明子表达式已为单一的值，直接返回
			calc_result = new BigDecimal(s);
			return calc_result;
		}
		BigDecimal num1,num2 = null;

		num1 =calcuThisExpression(s.substring(0, lowest_priority));	//主要步骤3: 递归计算最低优先级符号左边的子表达式
		num2 =calcuThisExpression(s.substring(lowest_priority + 1, s.length())); //计算最低优先级符号右边的子表达式

		switch (s.charAt(lowest_priority)) {	//根据最低优先级符号，对左右表达式的结果进行运算，对两个数运算的部分使用了BigDecimal类
			case '+':
				calc_result = num1.add(num2);
				break;
			case '-':
				calc_result = num1.subtract(num2);
				break;
			case '*':
				calc_result = num1.multiply(num2);
				break;
			case '/':
				calc_result = num1.divide(num2,mScale,BigDecimal.ROUND_HALF_UP);
				break;
		}

		return calc_result;		//返回该函数的运算结果
	}
}
