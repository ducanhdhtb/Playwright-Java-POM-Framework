package utils;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.nio.file.Path;

/**
 * Local utility for inspecting the AutomationTestData.xlsx structure (sheets + headers).
 * Not used by the framework runtime.
 */
public final class ExcelInspector {
    public static void main(String[] args) throws Exception {
        Path file = Path.of("src/test/resources/AutomationTestData.xlsx");
        try (FileInputStream fis = new FileInputStream(file.toFile());
             Workbook wb = new XSSFWorkbook(fis)) {
            DataFormatter fmt = new DataFormatter();
            System.out.println("Excel: " + file.toAbsolutePath());
            System.out.println("Sheets: " + wb.getNumberOfSheets());
            for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                Sheet sheet = wb.getSheetAt(i);
                System.out.println("- " + sheet.getSheetName());
                Row header = sheet.getRow(0);
                if (header == null) {
                    System.out.println("  header: <empty>");
                    continue;
                }
                int last = header.getLastCellNum();
                StringBuilder sb = new StringBuilder();
                for (int c = 0; c < last; c++) {
                    String v = fmt.formatCellValue(header.getCell(c));
                    if (c > 0) sb.append(" | ");
                    sb.append(v);
                }
                System.out.println("  header: " + sb);
            }
        }
    }
}

