package com.rpn;

import lombok.Getter;

@Getter
public class Result {

    private Double result;
    private ExpressionException expressionException = new ExpressionException();
    private resultStatuses resultStatus = resultStatuses.OK;

    public enum resultStatuses {
        OK,
        BAD
    }

    public Result(Double result) {
        this.result = result;
    }

    public Result(ExpressionException expressionException) {
        this.expressionException = expressionException;
        this.resultStatus = resultStatuses.BAD;
    }

    public static Result.resultStatuses convertStringToStatus(String resultStatus) throws Exception {
        if(resultStatus == null || resultStatus.trim().isEmpty()) {
            throw new Exception("Empty result status!");
        } else if(!resultStatus.equals("OK") && !resultStatus.equals("BAD")) {
            throw new Exception("Unexpected result status!");
        } else return resultStatus.equals("OK") ? resultStatuses.OK : resultStatuses.BAD;
    }

}
