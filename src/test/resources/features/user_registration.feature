# ============================================================
# Feature: User Registration
# Covers: TC1, TC5, API-TC01
# ============================================================
@registration
Feature: User Registration

  Background:
    Given the user navigates to "https://automationexercise.com"
    And the home page is displayed with title "Automation Exercise"

  # ----------------------------------------------------------
  # TC1 - Register a new user (data-driven)
  # ----------------------------------------------------------
  @smoke @TC1
  Scenario Outline: TC1 - Register a new user successfully
    When the user clicks on "Signup / Login"
    And the user fills the signup form with name "<name>" and a random email
    And the user clicks the Signup button
    And the user fills account details with password "<password>", day "<day>", month "<month>", year "<year>"
    And the user fills address details with firstName "<firstName>", lastName "<lastName>", company "<company>", address "<address>", country "<country>", state "<state>", city "<city>", zipcode "<zipcode>", mobile "<mobile>"
    And the user clicks "Create Account"
    Then the message "Account Created!" is visible
    When the user clicks "Continue"
    Then the header shows "Logged in as <name>"
    When the user clicks "Delete Account"
    Then the message "Account Deleted!" is visible

    Examples:
      | name      | password    | day | month | year | firstName | lastName | company   | address        | country       | state    | city   | zipcode | mobile     |
      | John Doe  | Pass@123    | 10  | July  | 1995 | John      | Doe      | TestCorp  | 123 Main St    | United States | Texas    | Austin | 73301   | 1234567890 |
      | Jane Smith| Secret@456  | 5   | March | 1990 | Jane      | Smith    | QACorp    | 456 Oak Ave    | United States | New York | NYC    | 10001   | 9876543210 |

  # ----------------------------------------------------------
  # TC5 - Register with an already existing email
  # ----------------------------------------------------------
  @regression @TC5
  Scenario: TC5 - Register with an already registered email shows error
    Given a user account already exists with name "ExistingUser" and password "Password123"
    When the user logs out
    And the user navigates to "https://automationexercise.com"
    And the user clicks on "Signup / Login"
    And the user fills the signup form with name "ExistingUser" and the existing email
    And the user clicks the Signup button
    Then the error message "Email Address already exist!" is visible

  # ----------------------------------------------------------
  # API-TC01a - Create user via API
  # ----------------------------------------------------------
  @api @smoke @API_TC01
  Scenario: API-TC01a - Create a new user account via API returns 201
    When a POST request is sent to "/api/createAccount" with valid user data
    Then the HTTP status code is 200
    And the API responseCode is 201
    And the API message contains "User created"

  # ----------------------------------------------------------
  # API-TC01b - Duplicate email via API
  # ----------------------------------------------------------
  @api @regression @API_TC01
  Scenario: API-TC01b - Create user with duplicate email via API returns 400
    Given a user account already exists via API with email "dup@testmail.com" and password "Password123"
    When a POST request is sent to "/api/createAccount" with the same email "dup@testmail.com"
    Then the HTTP status code is 200
    And the API responseCode is 400
