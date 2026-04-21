package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelReader {

    /**
     * Reads data from a specified sheet in an Excel file.
     *
     * @param filePath The path to the .xlsx file.
     * @param sheetName The name of the sheet to read data from.
     * @return A 2D Object array containing the data from the sheet.
     */
    public static Object[][] getTestData(String filePath, String sheetName) {
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new IllegalArgumentException(
                        "Sheet '" + sheetName + "' does not exist in Excel file: " + filePath);
            }

            Row header = sheet.getRow(0);
            if (header == null) {
                throw new IllegalStateException("Sheet '" + sheetName + "' is missing header row (row 0).");
            }

            int colCount = header.getLastCellNum();
            if (colCount <= 0) {
                throw new IllegalStateException("Sheet '" + sheetName + "' has no columns in header row.");
            }

            DataFormatter formatter = new DataFormatter();
            List<Object[]> rows = new ArrayList<>();

            int lastRow = sheet.getLastRowNum();
            for (int i = 1; i <= lastRow; i++) { // Start from 1 to skip header
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                Object[] values = new Object[colCount];
                boolean allBlank = true;
                for (int j = 0; j < colCount; j++) {
                    Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    String v = formatter.formatCellValue(cell);
                    values[j] = v;
                    if (v != null && !v.isBlank()) {
                        allBlank = false;
                    }
                }

                // Skip empty rows so we don't feed blank datasets into tests.
                if (!allBlank) {
                    rows.add(values);
                }
            }

            return rows.toArray(new Object[0][0]);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read Excel test data: " + filePath + " sheet=" + sheetName, e);
        }
    }
}
