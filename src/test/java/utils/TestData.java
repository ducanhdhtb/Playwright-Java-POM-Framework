package utils;

import org.testng.annotations.DataProvider;

public class TestData {

    private static final String TEST_DATA_FILE_PATH = "src/test/resources/AutomationTestData.xlsx";

    @DataProvider(name = "invalidLoginDataProvider")
    public static Object[][] invalidLoginDataProvider() {
        return ExcelReader.getTestData(TEST_DATA_FILE_PATH, "InvalidLogin");
    }

    @DataProvider(name = "validLoginDataProvider")
    public static Object[][] validLoginDataProvider() {
        return ExcelReader.getTestData(TEST_DATA_FILE_PATH, "ValidLogin");
    }

    @DataProvider(name = "productSearchDataProvider")
    public static Object[][] productSearchDataProvider() {
        return ExcelReader.getTestData(TEST_DATA_FILE_PATH, "ProductSearch");
    }

    @DataProvider(name = "newUserRegistrationDataProvider")
    public static Object[][] newUserRegistrationDataProvider() {
        return ExcelReader.getTestData(TEST_DATA_FILE_PATH, "NewUserRegistration");
    }
}