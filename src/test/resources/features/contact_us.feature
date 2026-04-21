# ============================================================
# Feature: Contact Us Form
# Covers: TC6
# ============================================================
@contact
Feature: Contact Us Form

  Background:
    Given the user navigates to "https://automationexercise.com"
    And the home page is displayed with title "Automation Exercise"

  # ----------------------------------------------------------
  # TC6 - Submit contact us form (data-driven)
  # ----------------------------------------------------------
  @regression @TC6
  Scenario Outline: TC6 - Submit contact us form successfully
    When the user clicks on "Contact Us"
    Then the heading "Get In Touch" is visible
    When the user fills the contact form with name "<name>", email "<email>", subject "<subject>", message "<message>"
    And the user uploads file "<uploadFile>"
    And the user submits the contact form
    Then the success message "<expectedSuccessText>" is visible
    When the user clicks "Home" on the contact page
    Then the user is redirected to "https://automationexercise.com/"

    Examples:
      | name       | email                  | subject          | message                    | uploadFile                                    | expectedSuccessText                    |
      | John Doe   | john@example.com       | Test Inquiry     | This is a test message     | src/test/resources/upload-sample.txt          | Success! Your details have been submitted successfully. |
      | Jane Smith | jane@testmail.com      | Bug Report       | Found a bug on checkout    | src/test/resources/upload-sample.txt          | Success! Your details have been submitted successfully. |
