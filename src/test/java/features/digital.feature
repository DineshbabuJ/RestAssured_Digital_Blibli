Feature: API testing for digital
  Scenario: GET items in digital catalog and get brand id
    Given User has access to the api "catalog"
    When user hits api with get method with endpoint of "itemMapping/findAll" with params of 10001 and "RANDOM" and "pulsa-web","pulsa","username"
    And Filter response with product type "PHONE_CREDIT" BrandName "Indosat" andget BrandId
    Then user should get response code 200

  Scenario: GET default providerId and providerName
    Given User has access to the api "catalog"
    When user hits api with get method with endpoint of "itemMapping/findOne" with params of 10001 and "RANDOM" and "pulsa-web","pulsa","username",brandid,ProductType
    And Get default providerid and provider name
    Then user should get response code 200

  Scenario:Save item mapping api for the defaultProviderId
    Given User has access to the api "catalog"
    When user hits api with post method with endpoint of "itemMapping/save" with params of 10001 and "RANDOM" and "pulsa-web","pulsa","username"
    Then user should get response code 200

  Scenario:To delete the existing cart for the customer
    Given User has access to the api "pulsa"
    When user hits api with delete method with endpoint of "pulsaCart/deletePulsaCart" with params of 10001 and "RANDOM" and "pulsa-web","pulsa","username", cartId "40d5edd5-25b7-4ff7-8ddf-dd327916cce5@blibli"
    Then user should get response code 200

  Scenario: Fetch Api and filter and get the Skucode of it
    Given User has access to the api "catalog"
    When user hits api with get method with endpoint of "merchantDigitalSku/getProductListByFilter" with params of 10001 and "RANDOM" and "pulsa-web","pulsa","username",ProductType,brandname
    And user fliters and get the sku code
    Then user should get response code 200

  Scenario: Do Checkout
    Given User has access to the api "pulsa"
    When user hits api with post method with endpoint of "pulsaCart/doCheckout" with params of 10001 and "RANDOM" and "pulsa-web","pulsa","username", cartid, customerid ,cartType for checkout
    Then user should get response code 200

  Scenario: Set Pulsa cart
    Given User has access to the api "pulsa"
    When user hits api with post method with endpoint of "pulsaCart/setPulsaCartMsisdn" with params of 10001 and "RANDOM" and "pulsa-web","pulsa","username", cartid, "085765160652" msisdn,productType for setting cart
    Then user should get response code 200

  Scenario:  Add product to cart
    Given User has access to the api "pulsa"
    When hit api with post method in endpoint "pulsaCart/addToCart" and with params of 10001 and "RANDOM" and "pulsa-web","pulsa","username" add product to cart
    Then user should get response code 200

  Scenario: Change payment flow
    Given User has access to the api "pulsa"
    When hit api with post method in endpoint "pulsaCart/changePayment" and with params of 10001 and "RANDOM" and "pulsa-web","pulsa","username" , payment method
    Then user should get response code 200

  Scenario: Pay Order
    Given User has access to the api "pulsa"
    When hit api with post method in endpoint "pulsaCart/payOrder" and with params of 10001 and "RANDOM" and "pulsa-web","pulsa","username" add pay order by passing req body with cartId
    Then user should get response code 200

  Scenario: Approve order by order id
    Given User has access to the api "pulsa"
    When hit api with post method in endpoint "approveOrder/approveOrderPayment" and with params of 10001 and "RANDOM" and "pulsa-web","pulsa","username" add approve order by order id
    Then user should get response code 200

  Scenario: Get order and verify
    Given User has access to the api "pulsa"
    When user hit api with get method endpoint of "pulsaOrder/getPulsaOrderByOrderId" and with params of 10001 and "RANDOM" and "pulsa-web","pulsa","username", orderId
    Then user should get response code 200