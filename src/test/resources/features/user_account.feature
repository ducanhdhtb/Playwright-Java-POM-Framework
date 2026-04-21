# ============================================================
# Feature: User Account Management
# Covers: TC24c, API-TC03, API-TC04
# ============================================================
@account
Feature: User Account Management

  # ----------------------------------------------------------
  # TC24c - API user details match registration data
  # ----------------------------------------------------------
  @regression @api-ui @TC24
  Scenario: TC24c - API returns correct user details after registration
    When a user "DetailCheck" is created via API with email "detail@testmail.com" and password "Password123"
    And a GET request is sent to "/api/getUserDetailByEmail" with email "detail@testmail.com"
    Then the API responseCode is 200
    And the response user email is "detail@testmail.com"
    And the response user name is "DetailCheck"

  # ----------------------------------------------------------
  # API-TC03 - Get user details by email
  # ----------------------------------------------------------
  @api @smoke @API_TC03
  Scenario: API-TC03a - Get user details for existing user returns 200 with correct data
    Given a user "DetailUser" is created via API with password "Password123"
    When a GET request is sent to "/api/getUserDetailByEmail" with the created user email
    Then the HTTP status code is 200
    And the API responseCode is 200
    And the response contains a "user" object
    And the response user email matches the created user email
    And the response user name is "DetailUser"

  @api @regression @API_TC03
  Scenario: API-TC03b - Get user details for non-existent email returns 404
    When a GET request is sent to "/api/getUserDetailByEmail" with email "ghost@nowhere.com"
    Then the HTTP status code is 200
    And the API responseCode is 404

  # ----------------------------------------------------------
  # API-TC04 - Delete user account via API
  # ----------------------------------------------------------
  @api @smoke @API_TC04
  Scenario: API-TC04a - Delete existing user account returns 200
    Given a user "DeleteMe" is created via API with password "Password123"
    When a DELETE request is sent to "/api/deleteAccount" with the created user credentials
    Then the HTTP status code is 200
    And the API responseCode is 200
    And the API message contains "Account deleted"
    When a GET request is sent to "/api/getUserDetailByEmail" with the deleted user email
    Then the API responseCode is 404

  @api @regression @API_TC04
  Scenario: API-TC04b - Delete non-existent user returns 404
    When a DELETE request is sent to "/api/deleteAccount" with email "ghost@nowhere.com" and password "Password123"
    Then the HTTP status code is 200
    And the API responseCode is 404
