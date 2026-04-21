package utils;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Creates missing sheets/headers in AutomationTestData.xlsx so tests can be data-driven.
 * Safe behavior:
 * - If a sheet exists and has at least a header row, it is left unchanged.
 * - If a sheet is missing, it's created with header + one sample row.
 */
public final class ExcelSeeder {
    public static void main(String[] args) throws Exception {
        Path file = Path.of("src/test/resources/AutomationTestData.xlsx");
        if (!Files.exists(file)) {
            throw new IllegalStateException("Excel file not found: " + file.toAbsolutePath());
        }

        Map<String, List<String>> schema = new LinkedHashMap<>();
        schema.put("ExistingEmailRegistration", List.of("name", "email", "expectedError"));
        schema.put("ContactUs", List.of("name", "email", "subject", "message", "uploadFile", "expectedSuccessText"));
        schema.put("SubscriptionHome", List.of("email"));
        schema.put("SubscriptionCart", List.of("email"));

        schema.put("TC14", List.of(
                "user",
                "password", "day", "month", "year",
                "lastName", "address", "state", "city", "zipcode", "mobile",
                "comment",
                "cardName", "cardNumber", "cvc", "expMonth", "expYear"
        ));
        schema.put("TC15", List.of(
                "user",
                "emailPrefix",
                "password", "day", "month", "year",
                "firstName", "lastName", "address", "state", "city", "zipcode", "mobile",
                "productIndex",
                "comment",
                "cardName", "cardNumber", "cvc", "expMonth", "expYear"
        ));
        schema.put("TC16", List.of(
                "user",
                "password",
                "productIndex",
                "comment",
                "cardName", "cardNumber", "cvc", "expMonth", "expYear"
        ));
        schema.put("E2EPurchase", List.of(
                "searchKey",
                "user",
                "password", "day", "month", "year",
                "lastName", "address", "state", "city", "zipcode", "mobile",
                "comment",
                "cardName", "cardNumber", "cvc", "expMonth", "expYear"
        ));

        try (FileInputStream fis = new FileInputStream(file.toFile());
             Workbook wb = new XSSFWorkbook(fis)) {
            boolean changed = false;
            for (Map.Entry<String, List<String>> e : schema.entrySet()) {
                String sheetName = e.getKey();
                List<String> headers = e.getValue();
                Sheet sheet = wb.getSheet(sheetName);
                if (sheet != null && sheet.getRow(0) != null && sheet.getRow(0).getLastCellNum() > 0) {
                    continue;
                }

                if (sheet == null) {
                    sheet = wb.createSheet(sheetName);
                }

                Row header = sheet.createRow(0);
                for (int c = 0; c < headers.size(); c++) {
                    header.createCell(c).setCellValue(headers.get(c));
                }

                // One sample row so DataProvider has something to feed.
                Row row = sheet.createRow(1);
                seedSample(sheetName, headers, row);
                changed = true;
            }

            if (changed) {
                try (FileOutputStream out = new FileOutputStream(file.toFile())) {
                    wb.write(out);
                }
                System.out.println("Seeded missing sheets in: " + file.toAbsolutePath());
            } else {
                System.out.println("No changes needed: " + file.toAbsolutePath());
            }
        }
    }

    private static void seedSample(String sheetName, List<String> headers, Row row) {
        Map<String, String> sample = switch (sheetName) {
            case "ExistingEmailRegistration" -> Map.of(
                    "name", "Existing User",
                    "email", "ducanhdhtb@gmail.com",
                    "expectedError", "Email Address already exist!"
            );
            case "ContactUs" -> Map.of(
                    "name", "Test User",
                    "email", "contact@test.com",
                    "subject", "Support",
                    "message", "Please help",
                    "uploadFile", "src/test/java/automation_exercise/resources/upload-sample.txt",
                    "expectedSuccessText", "Success! Your details have been submitted successfully."
            );
            case "SubscriptionHome" -> Map.of("email", "tester_pro@example.com");
            case "SubscriptionCart" -> Map.of("email", "cart_tester@example.com");
            case "TC14" -> Map.ofEntries(
                    Map.entry("user", "Automation Test"),
                    Map.entry("password", "Password123"),
                    Map.entry("day", "1"),
                    Map.entry("month", "January"),
                    Map.entry("year", "1990"),
                    Map.entry("lastName", "Auto"),
                    Map.entry("address", "119 Tran Duy Hung"),
                    Map.entry("state", "VN"),
                    Map.entry("city", "Ha Noi"),
                    Map.entry("zipcode", "94043"),
                    Map.entry("mobile", "0123456789"),
                    Map.entry("comment", "Please deliver during business hours."),
                    Map.entry("cardName", "Automation Test"),
                    Map.entry("cardNumber", "4111111111111111"),
                    Map.entry("cvc", "123"),
                    Map.entry("expMonth", "12"),
                    Map.entry("expYear", "2028")
            );
            case "TC15" -> Map.ofEntries(
                    Map.entry("user", "Herry"),
                    Map.entry("emailPrefix", "user_pro_"),
                    Map.entry("password", "Password123"),
                    Map.entry("day", "15"),
                    Map.entry("month", "May"),
                    Map.entry("year", "1995"),
                    Map.entry("firstName", "Herry"),
                    Map.entry("lastName", "Mr"),
                    Map.entry("address", "1600 Amphitheatre"),
                    Map.entry("state", "California"),
                    Map.entry("city", "Mountain View"),
                    Map.entry("zipcode", "94043"),
                    Map.entry("mobile", "0987654321"),
                    Map.entry("productIndex", "0"),
                    Map.entry("comment", "Please deliver by evening."),
                    Map.entry("cardName", "Herry"),
                    Map.entry("cardNumber", "4111111111111111"),
                    Map.entry("cvc", "123"),
                    Map.entry("expMonth", "12"),
                    Map.entry("expYear", "2028")
            );
            case "TC16" -> Map.ofEntries(
                    Map.entry("user", "Automation Tester"),
                    Map.entry("password", "ducanh123"),
                    Map.entry("productIndex", "0"),
                    Map.entry("comment", "Returning customer order."),
                    Map.entry("cardName", "Automation Tester"),
                    Map.entry("cardNumber", "4111111111111111"),
                    Map.entry("cvc", "123"),
                    Map.entry("expMonth", "05"),
                    Map.entry("expYear", "2029")
            );
            case "E2EPurchase" -> Map.ofEntries(
                    Map.entry("searchKey", "Blue Top"),
                    Map.entry("user", "E2E_User"),
                    Map.entry("password", "a_strong_password"),
                    Map.entry("day", "10"),
                    Map.entry("month", "July"),
                    Map.entry("year", "2000"),
                    Map.entry("lastName", "Test"),
                    Map.entry("address", "123 Test Lane"),
                    Map.entry("state", "Texas"),
                    Map.entry("city", "Austin"),
                    Map.entry("zipcode", "73301"),
                    Map.entry("mobile", "1234567890"),
                    Map.entry("comment", "This is an E2E Test Order"),
                    Map.entry("cardName", "E2E_User"),
                    Map.entry("cardNumber", "4100000000000000"),
                    Map.entry("cvc", "123"),
                    Map.entry("expMonth", "01"),
                    Map.entry("expYear", "2025")
            );
            default -> Map.of();
        };

        for (int c = 0; c < headers.size(); c++) {
            String key = headers.get(c);
            row.createCell(c).setCellValue(sample.getOrDefault(key, ""));
        }
    }
}

