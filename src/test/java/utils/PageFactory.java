package utils;

import com.microsoft.playwright.Page;
import pages.*;

public class PageFactory {

    private final Page page;

    public PageFactory(Page page) {
        this.page = page;
    }

    public HomePage getHomePage() {
        return new HomePage(page);
    }

    public SignupLoginPage getSignupLoginPage() {
        return new SignupLoginPage(page);
    }

    public AccountInformationPage getAccountInformationPage() {
        return new AccountInformationPage(page);
    }

    public ProductsPage getProductsPage() {
        return new ProductsPage(page);
    }

    public CartPage getCartPage() {
        return new CartPage(page);
    }

    public PaymentPage getPaymentPage() {
        return new PaymentPage(page);
    }

    public ContactUsPage getContactUsPage() {
        return new ContactUsPage(page);
    }

    public ProductDetailPage getProductDetailPage() {
        return new ProductDetailPage(page);
    }

    public CheckoutPage getCheckoutPage() {
        return new CheckoutPage(page);
    }
}
