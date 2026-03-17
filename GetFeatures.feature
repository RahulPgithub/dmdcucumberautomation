Feature: DMD Features Service - Get Features API

  @features @get @smoke
  Scenario: User should be able to fetch all features
    When User fetches all features
    Then Response status code should be 200
    And Response field "features" should exist

  @features @get @regression
  Scenario: User should be able to fetch feature by ID
    When User fetches feature with ID "FEAT001"
    Then Response status code should be 200
    And Response should contain field "id" with value "FEAT001"
