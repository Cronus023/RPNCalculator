package com.rpn;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ExpressionException extends Exception {

    @Getter
    private ExpressionExceptionStatuses resultExceptionStatus = null;

    public static final String EMPTY_EXPRESSION_STRING = "The entered expression is empty!";
    public static final String WRONG_PARENTHESES_STRING = "Check parentheses in the entered expression!";
    public static final String WRONG_FIRST_SYMBOL_STRING = "The first symbol must be equals to '-' '+' '(' or number!";
    public static final String WRONG_AFTER_ARITHMETIC_OPERATOR_SYMBOL_STRING =
            "There is a wrong symbol after the arithmetic operator!";
    public static final String WRONG_AFTER_OPEN_PARENTHESIS_SYMBOL_STRING =
            "There is a wrong symbol after the opened parenthesis!";

    public enum ExpressionExceptionStatuses {
        EMPTY_EXPRESSION,
        WRONG_PARENTHESES,
        WRONG_SYMBOL,
        WRONG_FIRST_SYMBOL,
        WRONG_AFTER_POINT_SYMBOL,
        WRONG_AFTER_NUMBER_SYMBOL,
        WRONG_AFTER_ARITHMETIC_OPERATOR_SYMBOL,
        WRONG_AFTER_OPEN_PARENTHESIS_SYMBOL,
        WRONG_AFTER_CLOSE_PARENTHESIS_SYMBOL
    }

    public ExpressionException(ExpressionExceptionStatuses resultExceptionStatus, String message) {
        super(message);
        this.resultExceptionStatus = resultExceptionStatus;
    }

    public static ExpressionExceptionStatuses convertStringToStatus(String expressionStatus) throws Exception {
        if(expressionStatus.trim().isEmpty())
            throw new Exception("Empty expression status!");

        switch(expressionStatus) {
            case "null": return null;
            case "EMPTY_EXPRESSION": return ExpressionExceptionStatuses.EMPTY_EXPRESSION;
            case "WRONG_PARENTHESES": return ExpressionExceptionStatuses.WRONG_PARENTHESES;
            case "WRONG_SYMBOL": return ExpressionExceptionStatuses.WRONG_SYMBOL;
            case "WRONG_FIRST_SYMBOL": return ExpressionExceptionStatuses.WRONG_FIRST_SYMBOL;
            case "WRONG_AFTER_POINT_SYMBOL": return ExpressionExceptionStatuses.WRONG_AFTER_POINT_SYMBOL;
            case "WRONG_AFTER_NUMBER_SYMBOL": return ExpressionExceptionStatuses.WRONG_AFTER_NUMBER_SYMBOL;
            case "WRONG_AFTER_ARITHMETIC_OPERATOR_SYMBOL": return ExpressionExceptionStatuses.WRONG_AFTER_ARITHMETIC_OPERATOR_SYMBOL;
            case "WRONG_AFTER_OPEN_PARENTHESIS_SYMBOL": return ExpressionExceptionStatuses.WRONG_AFTER_OPEN_PARENTHESIS_SYMBOL;
            case "WRONG_AFTER_CLOSE_PARENTHESIS_SYMBOL": return ExpressionExceptionStatuses.WRONG_AFTER_CLOSE_PARENTHESIS_SYMBOL;
            default: throw new Exception("Unexpected status!");
        }
    }

}
