# ============================================================
# Feature: Product Browsing
# Covers: TC8, TC9, TC22, TC18, TC19
# ============================================================
@products
Feature: Product Browsing

  Background:
    Given the user navigates to "https://automationexercise.com"
    And the home page is displayed with title "Automation Exercise"

  # ----------------------------------------------------------
  # TC8 - Verify product detail page
  # ----------------------------------------------------------
  @regression @TC8
  Scenario: TC8 - View product detail page shows all required information
    When the user clicks on "Products"
    Then the user is on the "All Products" page at URL "https://automationexercise.com/products"
    And the products list is visible
    When the user clicks "View Product" for the first item
    Then the URL matches pattern ".*/product_details/.*"
    And the product name is visible
    And the product category is visible
    And the product price is visible
    And the product availability is visible
    And the product condition is visible
    And the product brand is visible

  # ----------------------------------------------------------
  # TC9 - Search product (data-driven)
  # ----------------------------------------------------------
  @smoke @TC9
  Scenario Outline: TC9 - Search for a product returns matching results
    When the user clicks on "Products"
    And the user searches for "<searchKey>"
    Then the section title shows "Searched Products"
    And all displayed product names contain "<searchKey>"

    Examples:
      | searchKey |
      | top       |
      | dress     |
      | jeans     |
      | saree     |

  # ----------------------------------------------------------
  # TC22a - Search returns matching results (extended)
  # ----------------------------------------------------------
  @regression @search @TC22
  Scenario Outline: TC22a - Search product returns matching results for various keywords
    When the user clicks on "Products"
    And the user searches for "<keyword>"
    Then the section title shows "Searched Products"
    And at least one product is visible
    And all displayed product names contain "<keyword>"

    Examples:
      | keyword |
      | top     |
      | dress   |
      | jeans   |

  # ----------------------------------------------------------
  # TC22b - Search with no results
  # ----------------------------------------------------------
  @regression @negative @search @TC22
  Scenario: TC22b - Search with gibberish keyword returns no products
    When the user clicks on "Products"
    And the user searches for "xyzzy_no_such_product_12345"
    Then the section title shows "Searched Products"
    And no products are displayed

  # ----------------------------------------------------------
  # TC7 - Verify Test Cases page navigation
  # ----------------------------------------------------------
  @regression @TC7
  Scenario: TC7 - Navigate to Test Cases page successfully
    When the user clicks on "Test Cases"
    Then the user is redirected to "https://automationexercise.com/test_cases"
    And the heading "Test Cases" is visible

  # ----------------------------------------------------------
  # TC18 - View category products
  # ----------------------------------------------------------
  @regression @TC18
  Scenario: TC18 - View products by category Women > Dress then Men > Tshirts
    Then the category sidebar is visible
    When the user selects category "Women" and sub-category "Dress"
    Then the category page title contains "WOMEN - DRESS PRODUCTS"
    When the user selects category "Men" and sub-category "Tshirts"
    Then the category page title contains "MEN - TSHIRTS PRODUCTS"

  # ----------------------------------------------------------
  # TC22c - Category filter
  # ----------------------------------------------------------
  @regression @search @TC22
  Scenario: TC22c - Filter by Women > Dress category shows correct products
    Then the category sidebar is visible
    When the user selects category "Women" and sub-category "Dress"
    Then the category page title contains "Women - Dress Products"
    And the products list is visible

  # ----------------------------------------------------------
  # TC19 - View brand products (Scenario Outline)
  # ----------------------------------------------------------
  @regression @TC19
  Scenario Outline: TC19 - View products by brand "<brand>"
    When the user clicks on "Products"
    Then the brands sidebar is visible
    When the user selects brand "<brand>"
    Then the brand page title contains "Brand - <brand> Products"

    Examples:
      | brand               |
      | POLO                |
      | H&M                 |
      | Madame              |
      | Mast & Harbour      |
      | Babyhug             |
      | Allen Solly Junior  |
      | Kookie Kids         |
      | Biba                |
