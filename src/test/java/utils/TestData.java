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

    @DataProvider(name = "existingEmailRegistrationDataProvider")
    public static Object[][] existingEmailRegistrationDataProvider() {
        return ExcelReader.getTestData(TEST_DATA_FILE_PATH, "ExistingEmailRegistration");
    }

    @DataProvider(name = "contactUsDataProvider")
    public static Object[][] contactUsDataProvider() {
        return ExcelReader.getTestData(TEST_DATA_FILE_PATH, "ContactUs");
    }

    @DataProvider(name = "subscriptionHomeDataProvider")
    public static Object[][] subscriptionHomeDataProvider() {
        return ExcelReader.getTestData(TEST_DATA_FILE_PATH, "SubscriptionHome");
    }

    @DataProvider(name = "subscriptionCartDataProvider")
    public static Object[][] subscriptionCartDataProvider() {
        return ExcelReader.getTestData(TEST_DATA_FILE_PATH, "SubscriptionCart");
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
