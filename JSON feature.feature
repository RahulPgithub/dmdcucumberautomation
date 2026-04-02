cat > /home/claude/dmd-testng-framework/src/test/resources/features/dmd-device-info/dmd_device_info_json.feature << 'FEATURE'
@dmd-device-info @json
Feature: DMD Device Info Service — JSON API Validation
  JSON REST API tests for the dmd-device-info microservice.
  Base URL is resolved per environment:
    stage → https://dmdstage-api.verizon.com/device-info/v1
    qa    → https://dmdqa-api.verizon.com/device-info/v1
    prod  → https://dmdprod-api.verizon.com/device-info/v1

  Background:
    Given the API name is "dmd-device-info"

  # ── Device Lookup ──────────────────────────────────────────────────────────

  @smoke @json-device-lookup
  Scenario: JSON - device lookup by deviceId
    Given the JSON API path is "/devices/359324081412299"
          for microservice "dmd-device-info"
    When  the client sends a JSON GET request
    Then  the JSON response should be successful
    And   the JSON DMD status code should be "00"
    And   the JSON DMD message should be "SUCCESS"
    And   the JSON response should contain field "deviceId"
    And   the JSON response should contain field "makeModel"
    And   the JSON response should contain field "deviceType"
    And   the JSON field "deviceId" should be "359324081412299"

  @smoke @json-device-lookup
  Scenario: JSON - device lookup by MEID
    Given the JSON API path is "/devices/99000044000090?idType=MEID"
          for microservice "dmd-device-info"
    When  the client sends a JSON GET request
    Then  the JSON response should be successful
    And   the JSON DMD status code should be "00"
    And   the JSON response should contain field "deviceId"
    And   the JSON response should contain field "makeModel"

  @regression @json-device-lookup
  Scenario Outline: JSON - device lookup for multiple device types
    Given the JSON API path is "/devices/<deviceId>?idType=<idType>"
          for microservice "dmd-device-info"
    When  the client sends a JSON GET request
    Then  the JSON response status code should be 200
    And   the JSON DMD status code should be "00"
    And   the JSON response should contain field "deviceId"

    Examples:
      | deviceId           | idType |
      | 359324081412299    | IMEI   |
      | 99000044000090     | MEID   |
      | A0000003023FD3     | ESN    |

  # ── SIM Pair Lookup ────────────────────────────────────────────────────────

  @smoke @json-sim-pair
  Scenario: JSON - SIM and device pair validation
    Given the JSON API path is "/sim-pair?deviceId=359324081412299&simId=89148000004735113345"
          for microservice "dmd-device-info"
    When  the client sends a JSON GET request
    Then  the JSON response should be successful
    And   the JSON DMD status code should be "00"
    And   the JSON response should contain field "deviceId"
    And   the JSON response should contain field "simId"
    And   the JSON field "deviceId" should be "359324081412299"
    And   the JSON field "simId" should be "89148000004735113345"

  # ── Lost / Stolen ──────────────────────────────────────────────────────────

  @smoke @json-lost-stolen
  Scenario: JSON - lost stolen non-pay check by simId
    Given the JSON API path is "/lost-stolen?simId=89148000002543383696"
          for microservice "dmd-device-info"
    When  the client sends a JSON GET request
    Then  the JSON response should be successful
    And   the JSON DMD status code should be "00"
    And   the JSON response should contain field "lostStolenStatus"
    And   the JSON response should contain field "nonPayStatus"

  # ── Warranty ──────────────────────────────────────────────────────────────

  @regression @json-warranty
  Scenario: JSON - warranty vendor lookup
    Given the JSON API path is "/warranty?deviceId=359324081412299&appType=NETACE"
          for microservice "dmd-device-info"
    When  the client sends a JSON GET request
    Then  the JSON response should be successful
    And   the JSON DMD status code should be "00"
    And   the JSON response should contain field "warrantyVendor"

  # ── Multi Make/Model ───────────────────────────────────────────────────────

  @smoke @json-make-model
  Scenario: JSON - multi make model lookup
    Given the JSON API path is "/make-model?deviceIds=A0000007077057,A0000007077058"
          for microservice "dmd-device-info"
    When  the client sends a JSON GET request
    Then  the JSON response should be successful
    And   the JSON array "devices" should not be empty

  # ── Dynamic data-driven ────────────────────────────────────────────────────

  @dynamic @json-device-lookup
  Scenario Outline: JSON - data-driven device lookup
    Given the JSON API path is "/devices/<deviceId>"
          for microservice "dmd-device-info"
    When  the client sends a JSON GET request
    Then  the JSON response should be successful
    And   the JSON DMD status code should be "00"
    And   the JSON field "deviceId" should be "<deviceId>"

    Examples:
      | deviceId        |
      | 359324081412299 |
      | 99000044000074  |
      | A0000007077057  |
FEATURE
echo "dmd-device-info JSON feature created"
