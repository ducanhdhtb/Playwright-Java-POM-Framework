# ============================================================
# Feature: User Login
# Covers: TC2, TC3, TC4, TC21, TC20, TC24a, TC24b, API-TC02
# ============================================================
@login
Feature: User Login

  Background:
    Given the user navigates to "https://automationexercise.com"

  # ----------------------------------------------------------
  # TC2 - Login with correct credentials
  # ----------------------------------------------------------
  @smoke @TC2
  Scenario Outline: TC2 - Login with valid credentials
    Given a user account exists with username "<username>" and password "<password>"
    And the user is logged out
    When the user clicks on "Signup / Login"
    Then the heading "Login to your account" is visible
    When the user fills the login form with email "<email>" and password "<password>"
    And the user clicks the Login button
    Then the header shows "Logged in as <username>"
    When the user clicks "Delete Account"
    Then the message "ACCOUNT DELETED!" is visible

    Examples:
      | username  | password    | email                        |
      | TestUser1 | Password123 | testuser1@example.com        |
      | TestUser2 | Secret@456  | testuser2@example.com        |

  # ----------------------------------------------------------
  # TC3 - Login with incorrect credentials (data-driven)
  # ----------------------------------------------------------
  @smoke @TC3
  Scenario Outline: TC3 - Login with invalid credentials shows error
    When the user clicks on "Signup / Login"
    Then the heading "Login to your account" is visible
    When the user fills the login form with email "<email>" and password "<password>"
    And the user clicks the Login button
    Then the error message "<expectedError>" is visible

    Examples:
      | email                    | password      | expectedError                          |
      | wrong@example.com        | wrongpass     | Your email or password is incorrect!   |
      | invalid@nowhere.com      | 12345         | Your email or password is incorrect!   |

  # ----------------------------------------------------------
  # TC4 - Logout user
  # ----------------------------------------------------------
  @regression @TC4
  Scenario: TC4 - Logout redirects to login page
    Given a user is logged in with username "TestUser" and password "ducanh123"
    When the user clicks "Logout"
    Then the user is redirected to "https://automationexercise.com/login"
    And the heading "Login to your account" is visible

  # ----------------------------------------------------------
  # TC20 - Login with API-created user
  # ----------------------------------------------------------
  @smoke @api-ui @TC20
  Scenario: TC20 - Login via UI with a user created through the API
    Given a user "ApiUiUser" is created via API with password "Password123"
    When the user clicks on "Signup / Login"
    And the user fills the login form with the API-created user credentials
    And the user clicks the Login button
    Then the header shows "Logged in as ApiUiUser"

  # ----------------------------------------------------------
  # TC21 - Negative login scenarios
  # ----------------------------------------------------------
  @regression @negative @TC21
  Scenario: TC21a - Login with wrong password shows error
    Given a user "NegUser" is created via API with password "Password123"
    When the user clicks on "Signup / Login"
    And the user fills the login form with the correct email and wrong password "WrongPass999"
    And the user clicks the Login button
    Then the error message "Your email or password is incorrect!" is visible
    And the header does not contain "Logged in as"

  @regression @negative @TC21
  Scenario: TC21b - Login with non-existent email shows error
    When the user clicks on "Signup / Login"
    And the user fills the login form with email "ghost_unknown@nowhere.com" and password "Password123"
    And the user clicks the Login button
    Then the error message "Your email or password is incorrect!" is visible

  @regression @negative @TC21
  Scenario: TC21c - Login with empty credentials stays on login page
    When the user clicks on "Signup / Login"
    And the user fills the login form with email "" and password ""
    And the user clicks the Login button
    Then the current URL matches pattern ".*(login|signup).*"

  # ----------------------------------------------------------
  # TC24a - Logout redirects to Signup/Login page
  # ----------------------------------------------------------
  @smoke @regression @TC24
  Scenario: TC24a - Logout redirects to Signup/Login page
    Given a user "LogoutUser" is created via API with password "Password123"
    When the user clicks on "Signup / Login"
    And the user logs in with the API-created user credentials
    Then the header shows "Logged in as LogoutUser"
    When the user clicks "Logout"
    Then the page title is "Automation Exercise - Signup / Login"
    And the header does not contain "Logged in as"

  # ----------------------------------------------------------
  # TC24b - Deleted account cannot login
  # ----------------------------------------------------------
  @regression @negative @api-ui @TC24
  Scenario: TC24b - Account deleted via API cannot login via UI
    Given a user "DeletedUser" is created via API with password "Password123"
    And the user account is deleted via API
    When the user clicks on "Signup / Login"
    And the user fills the login form with the deleted user credentials
    And the user clicks the Login button
    Then the error message "Your email or password is incorrect!" is visible

  # ----------------------------------------------------------
  # API-TC02 - Verify login via API
  # ----------------------------------------------------------
  @api @smoke @API_TC02
  Scenario: API-TC02a - Verify login with valid credentials returns 200
    Given a user account exists via API with email "logintest@testmail.com" and password "Password123"
    When a POST request is sent to "/api/verifyLogin" with email "logintest@testmail.com" and password "Password123"
    Then the HTTP status code is 200
    And the API responseCode is 200
    And the API message contains "User exists"

  @api @regression @API_TC02
  Scenario: API-TC02b - Verify login with invalid credentials returns 404
    When a POST request is sent to "/api/verifyLogin" with email "ghost@nowhere.com" and password "WrongPass"
    Then the HTTP status code is 200
    And the API responseCode is 404

  @api @regression @API_TC02
  Scenario: API-TC02c - Verify login with missing email parameter returns 400
    When a POST request is sent to "/api/verifyLogin" with only password "Password123"
    Then the HTTP status code is 200
    And the API responseCode is 400
