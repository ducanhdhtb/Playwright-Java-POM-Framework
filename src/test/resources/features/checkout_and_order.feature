# ============================================================
# Feature: Checkout and Order Placement
# Covers: TC14, TC15, TC16, E2EPurchaseTest
# ============================================================
@checkout @e2e
Feature: Checkout and Order Placement

  Background:
    Given the user navigates to "https://automationexercise.com"
    And the home page is displayed with title "Automation Exercise"

  # ----------------------------------------------------------
  # TC14 - Place order: Register while checkout
  # ----------------------------------------------------------
  @e2e @regression @TC14
  Scenario Outline: TC14 - Place order by registering during checkout
    When the user adds the first product to cart
    And the user clicks "View Cart"
    Then the current URL is "https://automationexercise.com/view_cart"
    When the user proceeds to checkout
    And the user clicks "Register / Login" on the modal
    And the user fills the signup form with name "<user>" and a random email
    And the user fills account details with password "<password>", day "<day>", month "<month>", year "<year>"
    And the user fills address details with firstName "<user>", lastName "<lastName>", address "<address>", state "<state>", city "<city>", zipcode "<zipcode>", mobile "<mobile>"
    And the user clicks "Create Account"
    Then the message "Account Created!" is visible
    When the user clicks "Continue"
    Then the header shows "Logged in as <user>"
    When the user clicks "Cart"
    And the user proceeds to checkout
    And the user enters comment "<comment>"
    And the user clicks "Place Order"
    And the user enters payment details with name "<cardName>", card "<cardNumber>", cvc "<cvc>", month "<expMonth>", year "<expYear>"
    And the user clicks "Pay and Confirm Order"
    Then the order success message is visible
    When the user clicks "Delete Account"
    Then the message "Account Deleted!" is visible

    Examples:
      | user      | password    | day | month | year | lastName | address     | state    | city   | zipcode | mobile     | comment       | cardName  | cardNumber       | cvc | expMonth | expYear |
      | OrderUser | Pass@123    | 15  | June  | 1992 | Tester   | 789 Test Rd | Texas    | Dallas | 75001   | 5551234567 | Test order    | John Doe  | 4111111111111111 | 123 | 12       | 2027    |

  # ----------------------------------------------------------
  # TC15 - Place order: Register before checkout
  # ----------------------------------------------------------
  @e2e @regression @TC15
  Scenario Outline: TC15 - Place order after registering before checkout
    When the user clicks on "Signup / Login"
    And the user fills the signup form with name "<username>" and email "<emailPrefix>+<timestamp>@test.com"
    And the user fills account details with password "<password>", day "<day>", month "<month>", year "<year>"
    And the user fills address details with firstName "<firstName>", lastName "<lastName>", address "<address>", state "<state>", city "<city>", zipcode "<zipcode>", mobile "<mobile>"
    And the user clicks "Create Account"
    Then the message "Account Created!" is visible
    When the user clicks "Continue"
    Then the header shows "Logged in as <username>"
    When the user clicks on "Products"
    And the user adds product at index <productIndex> to cart
    And the user clicks "Continue Shopping"
    And the user clicks "Cart"
    Then the current URL is "https://automationexercise.com/view_cart"
    When the user proceeds to checkout
    And the user enters comment "<comment>"
    And the user clicks "Place Order"
    And the user enters payment details with name "<cardName>", card "<cardNumber>", cvc "<cvc>", month "<expMonth>", year "<expYear>"
    And the user clicks "Pay and Confirm Order"
    Then the order success message is visible
    When the user clicks "Delete Account"
    Then the message "Account Deleted!" is visible

    Examples:
      | username  | emailPrefix | password    | day | month    | year | firstName | lastName | address     | state    | city   | zipcode | mobile     | productIndex | comment    | cardName  | cardNumber       | cvc | expMonth | expYear |
      | PreUser   | preuser     | Pass@123    | 20  | August   | 1988 | Pre       | User     | 321 Oak St  | Florida  | Miami  | 33101   | 3051234567 | 0            | Pre order  | Pre User  | 4111111111111111 | 456 | 06       | 2026    |

  # ----------------------------------------------------------
  # TC16 - Place order: Login before checkout
  # ----------------------------------------------------------
  @e2e @regression @TC16
  Scenario Outline: TC16 - Place order after logging in before checkout
    Given a user "<user>" is created via API with password "<password>"
    When the user clicks on "Signup / Login"
    And the user logs in with email from API-created user and password "<password>"
    Then the header shows "Logged in as <user>"
    When the user clicks on "Products"
    And the user adds product at index <productIndex> to cart
    And the user clicks "Continue Shopping"
    And the user clicks "Cart"
    Then the current URL is "https://automationexercise.com/view_cart"
    When the user proceeds to checkout
    And the user enters comment "<comment>"
    And the user clicks "Place Order"
    And the user enters payment details with name "<cardName>", card "<cardNumber>", cvc "<cvc>", month "<expMonth>", year "<expYear>"
    And the user clicks "Pay and Confirm Order"
    Then the order success message is visible
    When the user clicks "Delete Account"
    Then the message "Account Deleted!" is visible

    Examples:
      | user       | password    | productIndex | comment      | cardName   | cardNumber       | cvc | expMonth | expYear |
      | LoginOrder | Pass@123    | 0            | Login order  | Login User | 4111111111111111 | 789 | 03       | 2028    |

  # ----------------------------------------------------------
  # E2E - Full purchase flow (search → register → checkout → delete)
  # ----------------------------------------------------------
  @e2e @E2E
  Scenario Outline: E2E - Full purchase flow: search, register, checkout, delete account
    When the user clicks on "Products"
    And the user searches for "<searchKey>"
    And the user adds the first product to cart
    And the user clicks "View Cart"
    When the user proceeds to checkout
    And the user clicks "Register / Login" on the modal
    And the user fills the signup form with name "<username>" and a random email
    And the user fills account details with password "<password>", day "<day>", month "<month>", year "<year>"
    And the user fills address details with firstName "<username>", lastName "<lastName>", address "<address>", state "<state>", city "<city>", zipcode "<zipcode>", mobile "<mobile>"
    And the user clicks "Create Account"
    Then the message "Account Created!" is visible
    When the user clicks "Continue"
    Then the header shows "Logged in as <username>"
    When the user clicks "Cart"
    And the user proceeds to checkout
    And the user enters comment "<comment>"
    And the user clicks "Place Order"
    And the user enters payment details with name "<cardName>", card "<cardNumber>", cvc "<cvc>", month "<expMonth>", year "<expYear>"
    And the user clicks "Pay and Confirm Order"
    Then the order success message is visible
    When the user clicks "Delete Account"
    Then the message "Account Deleted!" is visible

    Examples:
      | searchKey | username  | password | day | month | year | lastName | address    | state | city   | zipcode | mobile     | comment   | cardName | cardNumber       | cvc | expMonth | expYear |
      | dress     | E2EUser   | Pass@123 | 1   | Jan   | 1995 | Tester   | 1 Test St  | Texas | Austin | 73301   | 1234567890 | E2E order | E2E User | 4111111111111111 | 321 | 01       | 2029    |
