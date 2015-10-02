

package com.example.hfy;

import org.javia.arity.Symbols;
import org.javia.arity.SyntaxException;
import org.javia.arity.Util;

public class CalculatorExpressionEvaluator {

    private static final int MAX_DIGITS = 12;
    private static final int ROUNDING_DIGITS = 2;

    private final Symbols mSymbols;
    private final CalculatorExpressionTokenizer mTokenizer;

    public CalculatorExpressionEvaluator(CalculatorExpressionTokenizer tokenizer) {
        mSymbols = new Symbols();
        mTokenizer = tokenizer;
    }

    public void evaluate(CharSequence expr, EvaluateCallback callback) {
        evaluate(expr.toString(), callback);
    }

    public void evaluate(String expr, EvaluateCallback callback) {
        expr = mTokenizer.getNormalizedExpression(expr);

        // remove any trailing operators删除任何尾随操作符
        while (expr.length() > 0 && "+-/*".indexOf(expr.charAt(expr.length() - 1)) != -1) {
            expr = expr.substring(0, expr.length() - 1);
        }

        try {
            if (expr.length() == 0 || Double.valueOf(expr) != null) {
                callback.onEvaluate(expr, null, Calculator.INVALID_RES_ID);
                return;
            }
        } catch (NumberFormatException e) {
            // expr is not a simple number他不是一个简单的数字
        }

        try {
            double result = mSymbols.eval(expr);
            if (Double.isNaN(result)) {
                callback.onEvaluate(expr, null, R.string.error_nan);
            } else {
                // The arity library uses floating point arithmetic when evaluating the expression
                // leading to precision errors in the result. The method doubleToString hides these
                // errors; rounding the result by dropping N digits of precision.
                //数库使用浮点运算时，计算表达式
                //导致精度误差的结果。方法double to string隐藏这些
                //误差；通过对精度下降的数字计算。
                final String resultString = mTokenizer.getLocalizedExpression(
                        Util.doubleToString(result, MAX_DIGITS, ROUNDING_DIGITS));
                callback.onEvaluate(expr, resultString, Calculator.INVALID_RES_ID);
            }
        } catch (SyntaxException e) {
            callback.onEvaluate(expr, null, R.string.error_syntax);
        }
    }

    public interface EvaluateCallback {
        public void onEvaluate(String expr, String result, int errorResourceId);
    }
}
