Feature: DMD DeviceInfo Service - Get Device Info API

  @deviceinfo @get @smoke
  Scenario: User should be able to fetch all device info
    When User fetches all device info
    Then Device info response status code should be 200
    And Response field "devices" should exist

  @deviceinfo @get @regression
  Scenario: User should be able to fetch device info by ID
    When User fetches device info with ID "DEV001"
    Then Device info response status code should be 200
    And Device info response should contain field "deviceId" with value "DEV001"
