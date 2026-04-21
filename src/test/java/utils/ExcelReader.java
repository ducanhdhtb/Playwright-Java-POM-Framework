package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;

public class ExcelReader {

    /**
     * Reads data from a specified sheet in an Excel file.
     *
     * @param filePath The path to the .xlsx file.
     * @param sheetName The name of the sheet to read data from.
     * @return A 2D Object array containing the data from the sheet.
     */
    public static Object[][] getTestData(String filePath, String sheetName) {
        Object[][] data = null;
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new RuntimeException("Sheet with name '" + sheetName + "' does not exist in the Excel file.");
            }

            int rowCount = sheet.getPhysicalNumberOfRows();
            int colCount = sheet.getRow(0).getLastCellNum();

            // Create a 2D array, ignoring the header row for data
            data = new Object[rowCount - 1][colCount];

            for (int i = 1; i < rowCount; i++) { // Start from 1 to skip header
                Row row = sheet.getRow(i);
                for (int j = 0; j < colCount; j++) {
                    Cell cell = row.getCell(j);
                    DataFormatter formatter = new DataFormatter();
                    // Format cell value to String to avoid type issues
                    data[i - 1][j] = formatter.formatCellValue(cell);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}