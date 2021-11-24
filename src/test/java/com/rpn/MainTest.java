package com.rpn;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    private static final Logger logger = LoggerFactory.getLogger(MainTest.class);

    private static final String FILE_PATH_TEST_RESULT_DATA =
            System.getProperty("user.dir") + "/src/test/java/com/rpn/TestData/TestResultData.xlsx";
    private static final String FILE_PATH_TEST_RPN_DATA =
            System.getProperty("user.dir") + "/src/test/java/com/rpn/TestData/TestRPNData.xlsx";

    private static final int INITIAL_EXPRESSION_COLUMN = 0;
    private static final int EXPECTED_RESULT_COLUMN = 1;
    private static final int EXPECTED_RESULT_STATUS_COLUMN = 2;
    private static final int EXPECTED_EXPRESSION_EXCEPTION_COLUMN = 3;
    private static final int EXPECTED_RPN_COLUMN = 1;

    @Test
    void getResult() throws IOException {
        logger.info("START: Testing of getting the result of an expression");

        Sheet testResultSheet = getSheetFromFile(FILE_PATH_TEST_RESULT_DATA);
        int errorsCount = 0;

        for(Row row: testResultSheet) {
            if(row.getRowNum() == 0) continue;

            try {
                String initialExpression = row.getCell(INITIAL_EXPRESSION_COLUMN).toString();
                Double expectedResult = !row.getCell(EXPECTED_RESULT_COLUMN).toString().equals("null") ?
                        Double.parseDouble(row.getCell(EXPECTED_RESULT_COLUMN).toString()): null;
                Result.resultStatuses expectedResultStatus =
                        Result.convertStringToStatus(row.getCell(EXPECTED_RESULT_STATUS_COLUMN).toString());
                ExpressionException.ExpressionExceptionStatuses exceptionStatus =
                        ExpressionException.convertStringToStatus(row.getCell(EXPECTED_EXPRESSION_EXCEPTION_COLUMN).toString());

                Result result = Main.getResult(initialExpression);

                assertEquals(expectedResult, result.getResult());
                assertEquals(expectedResultStatus, result.getResultStatus());
                assertEquals(exceptionStatus, result.getExpressionException().getResultExceptionStatus());
            } catch(Exception e) {
                errorsCount += 1;
                int errorRow = row.getRowNum() + 1;
                logger.error(e.getMessage() + " on line " + errorRow + " of the file TestResultData.xlsx");
            }
        }

        logger.info("Errors count: " + errorsCount);
        logger.info("COMPLETE: Testing of getting the result");
    }

    @Test
    void getConvertedExpressionToRPN() throws IOException {
        logger.info("START: Testing of getting the converted to RPN expression");

        Sheet testResultSheet = getSheetFromFile(FILE_PATH_TEST_RPN_DATA);
        int errorsCount = 0;

        for(Row row: testResultSheet) {
            if(row.getRowNum() == 0) continue;

            try {
                String initialExpression = row.getCell(INITIAL_EXPRESSION_COLUMN).toString();
                String expectedRPNConvertedString = row.getCell(EXPECTED_RPN_COLUMN).toString();

                String convertedExpression = Main.getConvertedExpressionToRPN(initialExpression);

                assertEquals(expectedRPNConvertedString, convertedExpression);
            } catch(Exception e) {
                errorsCount += 1;
                int errorRow = row.getRowNum() + 1;
                logger.error(e.getMessage() + " in row " + errorRow + " of the file TestRPNData.xlsx");
            }
        }

        logger.info("Errors count: " + errorsCount);
        logger.info("COMPLETE: Testing of getting the converted to RPN expression");
    }

    private Sheet getSheetFromFile(String filePath) throws IOException {
        FileInputStream file = new FileInputStream(filePath);
        Workbook workbook = new XSSFWorkbook(file);
        file.close();
        return workbook.getSheetAt(0);
    }

}