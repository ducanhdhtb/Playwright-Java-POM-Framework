package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.LoginPage;

public class LoginTest extends BaseTest {

    @Test
    public void testLoginTitle() {
        System.out.println("RUN TEST!!");
        LoginPage loginPage = new LoginPage(page);

        loginPage.navigateToLogin("https://www.facebook.com/");

        // Kiểm tra title trang web
        String title = page.title();
        System.out.println("Title là: " + title);

        Assert.assertTrue(title.contains("Facebook"));

        // Thử thực hiện hành động login
        loginPage.login("testuser@gmail.com", "123456");
    }
}