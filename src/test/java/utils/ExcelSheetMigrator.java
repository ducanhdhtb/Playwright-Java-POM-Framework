package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Migrates shared sheets (e.g. ValidLogin) into per-test-case sheets (e.g. TC2).
 * This keeps the "one test case -> one sheet" convention without manual Excel edits.
 */
public final class ExcelSheetMigrator {
    public static void main(String[] args) throws Exception {
        Path file = Path.of("src/test/resources/AutomationTestData.xlsx");
        if (!Files.exists(file)) {
            throw new IllegalStateException("Excel file not found: " + file.toAbsolutePath());
        }

        Map<String, String> copies = new LinkedHashMap<>();
        copies.put("TC1", "NewUserRegistration");
        copies.put("TC2", "ValidLogin");
        copies.put("TC3", "InvalidLogin");
        copies.put("TC6", "ContactUs");
        copies.put("TC9", "ProductSearch");
        copies.put("TC10", "SubscriptionHome");
        copies.put("TC11", "SubscriptionCart");

        try (FileInputStream fis = new FileInputStream(file.toFile());
             Workbook wb = new XSSFWorkbook(fis)) {
            boolean changed = false;
            for (Map.Entry<String, String> e : copies.entrySet()) {
                String targetName = e.getKey();
                String sourceName = e.getValue();

                if (hasDataSheet(wb, targetName)) {
                    continue;
                }
                Sheet source = wb.getSheet(sourceName);
                if (source == null) {
                    throw new IllegalStateException("Missing source sheet: " + sourceName);
                }

                Sheet target = wb.createSheet(targetName);
                copySheet(source, target);
                changed = true;
            }

            // TC5 needs a different schema (we now generate the "existing email" dynamically).
            if (!hasDataSheet(wb, "TC5")) {
                Sheet tc5 = wb.createSheet("TC5");
                createHeader(tc5, List.of("name", "password", "expectedError"));

                // Seed from ExistingEmailRegistration if present, otherwise defaults.
                String name = "Existing User";
                String expected = "Email Address already exist!";
                Sheet src = wb.getSheet("ExistingEmailRegistration");
                if (src != null && src.getPhysicalNumberOfRows() > 1) {
                    Row r = src.getRow(1);
                    if (r != null) {
                        DataFormatter fmt = new DataFormatter();
                        name = fmt.formatCellValue(r.getCell(0));
                        expected = fmt.formatCellValue(r.getCell(2));
                    }
                }
                Row row = tc5.createRow(1);
                row.createCell(0).setCellValue(name);
                row.createCell(1).setCellValue("Password123");
                row.createCell(2).setCellValue(expected);
                changed = true;
            }

            if (changed) {
                try (FileOutputStream out = new FileOutputStream(file.toFile())) {
                    wb.write(out);
                }
                System.out.println("Migrated per-test-case sheets into: " + file.toAbsolutePath());
            } else {
                System.out.println("No migration needed: " + file.toAbsolutePath());
            }
        }
    }

    private static boolean hasDataSheet(Workbook wb, String name) {
        Sheet sheet = wb.getSheet(name);
        return sheet != null && sheet.getRow(0) != null && sheet.getRow(0).getLastCellNum() > 0;
    }

    private static void copySheet(Sheet source, Sheet target) {
        int lastRow = source.getLastRowNum();
        for (int r = 0; r <= lastRow; r++) {
            Row srcRow = source.getRow(r);
            if (srcRow == null) {
                continue;
            }
            Row dstRow = target.createRow(r);
            short lastCell = srcRow.getLastCellNum();
            for (int c = 0; c < lastCell; c++) {
                Cell srcCell = srcRow.getCell(c);
                Cell dstCell = dstRow.createCell(c);
                if (srcCell == null) {
                    continue;
                }
                dstCell.setCellValue(new DataFormatter().formatCellValue(srcCell));
            }
        }
    }

    private static void createHeader(Sheet sheet, List<String> headers) {
        Row header = sheet.createRow(0);
        for (int i = 0; i < headers.size(); i++) {
            header.createCell(i).setCellValue(headers.get(i));
        }
    }
}

