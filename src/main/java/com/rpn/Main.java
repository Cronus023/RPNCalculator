package com.rpn;

import java.util.*;

public class Main {

    private static final char PLUS = '+';
    private static final char MINUS = '-';
    private static final char MULTIPLY = '*';
    private static final char DIVISION = '/';
    private static final char POINT = '.';
    private static final char OPEN_PARENTHESIS = '(';
    private static final char CLOSE_PARENTHESIS = ')';
    private static final char SEPARATOR_CHARACTER = ' ';

    private static final int CLOSE_PARENTHESIS_PRIORITY = 0;
    private static final int NUMBERS_PRIORITY = 1;
    private static final int OPEN_PARENTHESIS_PRIORITY = 2;
    private static final int PLUS_AND_MINUS_PRIORITY = 3;
    private static final int DIVISION_AND_MULTIPLY_PRIORITY = 4;

    private static final String ROUNDING = "%.2f";

    public static Result getResult(String initialExpression) {
        try {
            checkInitialExpression(initialExpression);
            String preparedExpression = prepareExpression(initialExpression);
            String convertedExpression = convertExpressionToRPN(preparedExpression);
            return new Result(convertRPNtoResult(convertedExpression));
        } catch(ExpressionException e) {
            return new Result(e);
        }
    }

    public static String getConvertedExpressionToRPN(String initialExpression) throws ExpressionException {
        checkInitialExpression(initialExpression);
        String preparedExpression = prepareExpression(initialExpression);
        return convertExpressionToRPN(preparedExpression);
    }

    private static void checkInitialExpression(String initialExpression) throws ExpressionException {
        if(initialExpression == null || initialExpression.trim().isEmpty()) {
            throw new ExpressionException(ExpressionException.ExpressionExceptionStatuses.EMPTY_EXPRESSION,
                   ExpressionException.EMPTY_EXPRESSION_STRING);
        } else if(!isParenthesesValid(initialExpression)) {
            throw new ExpressionException(ExpressionException.ExpressionExceptionStatuses.WRONG_PARENTHESES,
                    ExpressionException.WRONG_PARENTHESES_STRING);
        } else if(!String.valueOf(initialExpression.charAt(0)).matches("[-+(\\d]")) {
            throw new ExpressionException(ExpressionException.ExpressionExceptionStatuses.WRONG_FIRST_SYMBOL,
                    ExpressionException.WRONG_FIRST_SYMBOL_STRING);
        } else {
            for(int i = 0; i < initialExpression.length(); i++) {
                String symbol = String.valueOf(initialExpression.charAt(i));
                String nextSymbol = i + 1 == initialExpression.length() ? null :
                        String.valueOf(initialExpression.charAt(i + 1));

                if(!symbol.matches("[-+*/(). \\d]")) {
                    throw new ExpressionException(ExpressionException.ExpressionExceptionStatuses.WRONG_SYMBOL,
                            "There is a wrong symbol '" + symbol + "' in expression!");
                }
                else if(symbol.matches("\\d") && nextSymbol != null && !nextSymbol.matches("[-.*+/)\\d\\s]")) {
                    throw new ExpressionException(ExpressionException.ExpressionExceptionStatuses.WRONG_AFTER_NUMBER_SYMBOL,
                            "There is a wrong symbol '" + nextSymbol + "' after the number!");
                }
                else if(symbol.charAt(0) == POINT && (nextSymbol == null || !nextSymbol.matches("\\d"))) {
                    throw new ExpressionException(ExpressionException.ExpressionExceptionStatuses.WRONG_AFTER_POINT_SYMBOL,
                            "There is a wrong symbol '" + nextSymbol + "' after the point!");
                }
                else if(symbol.matches("[-+*/]") && (nextSymbol == null || nextSymbol.matches("[-+*/).]"))) {
                    throw new ExpressionException(
                            ExpressionException.ExpressionExceptionStatuses.WRONG_AFTER_ARITHMETIC_OPERATOR_SYMBOL,
                            ExpressionException.WRONG_AFTER_ARITHMETIC_OPERATOR_SYMBOL_STRING);
                }
                else if(symbol.charAt(0) == OPEN_PARENTHESIS && (nextSymbol == null || !nextSymbol.matches("[-(\\d]"))) {
                    throw new ExpressionException(
                            ExpressionException.ExpressionExceptionStatuses.WRONG_AFTER_OPEN_PARENTHESIS_SYMBOL,
                            ExpressionException.WRONG_AFTER_OPEN_PARENTHESIS_SYMBOL_STRING);
                }
                else if(symbol.charAt(0) == CLOSE_PARENTHESIS && nextSymbol != null && !nextSymbol.matches("[-+*/]")) {
                    throw new ExpressionException(
                            ExpressionException.ExpressionExceptionStatuses.WRONG_AFTER_CLOSE_PARENTHESIS_SYMBOL,
                            "There is a wrong symbol '" + nextSymbol + "' after the closed parenthesis!");
                }
            }
        }
    }

    private static String convertExpressionToRPN(String expression) {
        StringBuilder convertedExpression = new StringBuilder();
        Stack<Character> operatorStack = new Stack<>();

        for(int i = 0; i < expression.length(); i++) {
            char symbol = expression.charAt(i);
            int symbolPriority = getPriority(symbol);

            switch(symbolPriority) {
                case NUMBERS_PRIORITY:
                    convertedExpression.append(symbol);
                    break;
                case OPEN_PARENTHESIS_PRIORITY:
                    operatorStack.push(symbol);
                    break;
                case CLOSE_PARENTHESIS_PRIORITY:
                    convertedExpression.append(SEPARATOR_CHARACTER);
                    while(getPriority(operatorStack.peek()) != OPEN_PARENTHESIS_PRIORITY)
                        convertedExpression.append(operatorStack.pop());

                    operatorStack.pop();
                    break;
                default:
                    convertedExpression.append(SEPARATOR_CHARACTER);

                    while(!operatorStack.empty()) {
                        if(getPriority(operatorStack.peek()) >= symbolPriority) {
                            convertedExpression.append(operatorStack.pop());
                            convertedExpression.append(SEPARATOR_CHARACTER);
                        }
                        else break;
                    }
                    operatorStack.push(symbol);
                    break;
            }
        }

        while(!operatorStack.empty()) {
            convertedExpression.append(SEPARATOR_CHARACTER);
            convertedExpression.append(operatorStack.pop());
        }

        return convertedExpression.toString();
    }

    private static Double convertRPNtoResult(String rpn) {
        StringBuilder operand = new StringBuilder();
        Stack<Double> stack = new Stack<>();

        for(int i = 0; i < rpn.length(); i++) {
            char symbol = rpn.charAt(i);

            if(symbol == SEPARATOR_CHARACTER) continue;

            if(getPriority(symbol) == NUMBERS_PRIORITY) {
                while (rpn.charAt(i) != SEPARATOR_CHARACTER && getPriority(rpn.charAt(i)) == NUMBERS_PRIORITY) {
                    operand.append(rpn.charAt(i++));
                }
                stack.push(Double.parseDouble(operand.toString()));
                operand = new StringBuilder();
            } else if(getPriority(rpn.charAt(i)) > OPEN_PARENTHESIS_PRIORITY) {
                stack.push(performAction(rpn.charAt(i), stack.pop(), stack.pop()));
            }
        }

        return Double.parseDouble(String.format(ROUNDING, stack.pop()));
    }

    private static String prepareExpression(String initialExpression) {
        StringBuilder preparedExpression = new StringBuilder();
        initialExpression = initialExpression.replaceAll("\\s+", "");

        for(int i = 0; i < initialExpression.length(); i++) {
            char symbol = initialExpression.charAt(i);
            if(i == 0 && symbol == PLUS)
                preparedExpression.append(0);
            if(symbol == MINUS && (i == 0 || initialExpression.charAt(i - 1) == OPEN_PARENTHESIS))
                preparedExpression.append(0);

            preparedExpression.append(symbol);
        }
        
        return preparedExpression.toString();
    }

    private static Double performAction(char operator, Double firstNumber, Double secondNumber) {
        switch (operator) {
            case PLUS: return  secondNumber + firstNumber;
            case MINUS: return secondNumber - firstNumber;
            case DIVISION: return secondNumber / firstNumber;
            default: return secondNumber * firstNumber;
        }
    }

    private static int getPriority(char symbol) {
        if (symbol == PLUS || symbol == MINUS)
            return PLUS_AND_MINUS_PRIORITY;
        else if (symbol == MULTIPLY || symbol == DIVISION)
            return DIVISION_AND_MULTIPLY_PRIORITY;
        else if (symbol == OPEN_PARENTHESIS)
            return OPEN_PARENTHESIS_PRIORITY;
        else if (symbol == CLOSE_PARENTHESIS)
            return CLOSE_PARENTHESIS_PRIORITY;
        else return NUMBERS_PRIORITY;
    }

    private static boolean isParenthesesValid(String initialExpression) {
        Stack<Character> stack = new Stack<>();
        for(char symbol: initialExpression.toCharArray()) {
            if(symbol == OPEN_PARENTHESIS) {
                stack.push(symbol);
            } else if(symbol == CLOSE_PARENTHESIS) {
                if(stack.isEmpty() || stack.pop() != OPEN_PARENTHESIS)
                    return false;
            }
        }

        return stack.isEmpty();
    }

}
