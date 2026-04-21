# ============================================================
# Feature: Cart Management
# Covers: TC12, TC13, TC17, TC23
# ============================================================
@cart
Feature: Cart Management

  Background:
    Given the user navigates to "https://automationexercise.com"
    And the home page is displayed with title "Automation Exercise"

  # ----------------------------------------------------------
  # TC12 - Add multiple products to cart
  # ----------------------------------------------------------
  @smoke @TC12
  Scenario: TC12 - Add two products to cart and verify details
    When the user clicks on "Products"
    And the user adds product at index 0 to cart
    And the user clicks "Continue Shopping"
    And the user adds product at index 1 to cart
    And the user clicks "View Cart"
    Then the cart contains 2 products
    And product at row 0 has price "Rs. 500", quantity "1", total "Rs. 500"
    And product at row 1 has price "Rs. 400", quantity "1", total "Rs. 400"

  # ----------------------------------------------------------
  # TC13 - Verify product quantity in cart
  # ----------------------------------------------------------
  @regression @TC13
  Scenario: TC13 - Add product with custom quantity to cart
    When the user clicks "View Product" for product at index 0
    Then the product detail page is displayed
    When the user sets quantity to "4"
    And the user clicks "Add to cart"
    And the user clicks "View Cart"
    Then the cart quantity for the product is "4"

  # ----------------------------------------------------------
  # TC17 - Remove product from cart
  # ----------------------------------------------------------
  @regression @TC17
  Scenario: TC17 - Remove a product from cart leaves cart empty
    When the user clicks on "Products"
    And the user adds product at index 0 to cart
    And the user clicks "Continue Shopping"
    And the user clicks "Cart"
    Then the current URL is "https://automationexercise.com/view_cart"
    When the user removes product at index 0 from cart
    Then the cart is empty with message visible

  # ----------------------------------------------------------
  # TC23a - Add multiple products to cart
  # ----------------------------------------------------------
  @regression @cart @TC23
  Scenario: TC23a - Add two different products and verify cart count
    When the user clicks on "Products"
    And the user adds product at index 0 to cart
    And the user clicks "Continue Shopping"
    And the user adds product at index 1 to cart
    And the user clicks "View Cart"
    Then the cart contains 2 products

  # ----------------------------------------------------------
  # TC23b - Remove product from cart
  # ----------------------------------------------------------
  @regression @cart @TC23
  Scenario: TC23b - Remove product from cart leaves cart empty
    When the user clicks on "Products"
    And the user adds the first product to cart
    And the user clicks "View Cart"
    Then the cart contains 1 products
    When the user removes product at index 0 from cart
    Then the cart is empty with message visible

  # ----------------------------------------------------------
  # TC23c - Cart persists after login
  # ----------------------------------------------------------
  @regression @cart @api-ui @TC23
  Scenario: TC23c - Cart is accessible after login with API-created user
    Given a user "CartUser" is created via API with password "Password123"
    When the user clicks on "Products"
    And the user adds the first product to cart
    And the user clicks "View Cart"
    And the user proceeds to checkout
    And the user clicks "Register / Login" on the modal
    And the user logs in with the API-created user credentials
    And the user clicks "Cart"
    Then the current URL matches pattern ".*view_cart.*"
