package utils;

import org.testng.annotations.DataProvider;

public class TestData {

    private static final String TEST_DATA_FILE_PATH = "src/test/resources/AutomationTestData.xlsx";

    // One test case -> one sheet convention (TC1, TC2, ...).
    @DataProvider(name = "tc1DataProvider")
    public static Object[][] tc1DataProvider() {
        return ExcelReader.getTestData(TEST_DATA_FILE_PATH, "TC1");
    }

    @DataProvider(name = "tc2DataProvider")
    public static Object[][] tc2DataProvider() {
        return ExcelReader.getTestData(TEST_DATA_FILE_PATH, "TC2");
    }

    @DataProvider(name = "tc3DataProvider")
    public static Object[][] tc3DataProvider() {
        return ExcelReader.getTestData(TEST_DATA_FILE_PATH, "TC3");
    }

    @DataProvider(name = "tc5DataProvider")
    public static Object[][] tc5DataProvider() {
        return ExcelReader.getTestData(TEST_DATA_FILE_PATH, "TC5");
    }

    @DataProvider(name = "tc6DataProvider")
    public static Object[][] tc6DataProvider() {
        return ExcelReader.getTestData(TEST_DATA_FILE_PATH, "TC6");
    }

    @DataProvider(name = "tc9DataProvider")
    public static Object[][] tc9DataProvider() {
        return ExcelReader.getTestData(TEST_DATA_FILE_PATH, "TC9");
    }

    @DataProvider(name = "tc10DataProvider")
    public static Object[][] tc10DataProvider() {
        return ExcelReader.getTestData(TEST_DATA_FILE_PATH, "TC10");
    }

    @DataProvider(name = "tc11DataProvider")
    public static Object[][] tc11DataProvider() {
        return ExcelReader.getTestData(TEST_DATA_FILE_PATH, "TC11");
    }

    @DataProvider(name = "tc14DataProvider")
    public static Object[][] tc14DataProvider() {
        return ExcelReader.getTestData(TEST_DATA_FILE_PATH, "TC14");
    }

    @DataProvider(name = "tc15DataProvider")
    public static Object[][] tc15DataProvider() {
        return ExcelReader.getTestData(TEST_DATA_FILE_PATH, "TC15");
    }

    @DataProvider(name = "tc16DataProvider")
    public static Object[][] tc16DataProvider() {
        return ExcelReader.getTestData(TEST_DATA_FILE_PATH, "TC16");
    }

    @DataProvider(name = "e2ePurchaseDataProvider")
    public static Object[][] e2ePurchaseDataProvider() {
        return ExcelReader.getTestData(TEST_DATA_FILE_PATH, "E2EPurchase");
    }
}
