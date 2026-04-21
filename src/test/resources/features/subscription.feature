# ============================================================
# Feature: Newsletter Subscription
# Covers: TC10, TC11
# ============================================================
@subscription
Feature: Newsletter Subscription

  Background:
    Given the user navigates to "https://automationexercise.com"
    And the home page is displayed with title "Automation Exercise"

  # ----------------------------------------------------------
  # TC10 - Verify subscription on home page (data-driven)
  # ----------------------------------------------------------
  @smoke @TC10
  Scenario Outline: TC10 - Subscribe to newsletter from home page
    When the user scrolls down to the footer
    Then the "SUBSCRIPTION" section title is visible
    When the user enters email "<email>" in the subscription field and clicks subscribe
    Then the subscription success message "You have been successfully subscribed!" is visible

    Examples:
      | email                      |
      | subscriber1@example.com    |
      | subscriber2@testmail.com   |
      | test.user+sub@gmail.com    |

  # ----------------------------------------------------------
  # TC11 - Verify subscription on cart page (data-driven)
  # ----------------------------------------------------------
  @smoke @TC11
  Scenario Outline: TC11 - Subscribe to newsletter from cart page
    When the user clicks "Cart"
    And the user scrolls down to the footer on cart page
    Then the "SUBSCRIPTION" section title is visible
    When the user enters email "<email>" in the subscription field and clicks subscribe
    Then the subscription success message "You have been successfully subscribed!" is visible

    Examples:
      | email                      |
      | cartsub1@example.com       |
      | cartsub2@testmail.com      |
