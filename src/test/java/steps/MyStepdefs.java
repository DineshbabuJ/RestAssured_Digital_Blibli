package steps;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class MyStepdefs {
    RequestSpecification requestSpecification;
    Response response;
    ObjectMapper objectMapper=new ObjectMapper();
    FileReader reader=new FileReader("application.properties");
    static Properties properties= new Properties();

    static int brandID;
    static String productTypeCode;
    static String cartID;
    static String defaultProviderId;
    static String defaultProviderName;
    static String skuCode;
    static String paymentMethod;
    static String orderId;
    static String brandName;
    static String deviceid;
    static String INTERNAL_NAME;

    public MyStepdefs() throws FileNotFoundException {
    }

    @Given("User has access to the api {string}")
    public void userHasAccessToTheApi(String api) throws IOException {
        properties.load(reader);
        String baseurl;
        if("catalog".equals(api)) baseurl=properties.getProperty("catologUrl");
        else baseurl=properties.getProperty("pulsaUrl");
        brandName=properties.getProperty("brandname");
        deviceid=properties.getProperty("deviceid");
        INTERNAL_NAME=properties.getProperty("internalname");
        paymentMethod=properties.getProperty("paymentMethod");
        requestSpecification=null;
        requestSpecification = RestAssured.given()
                .baseUri(baseurl)
                .contentType(ContentType.JSON)
                .header("accept","application/json")
                .header("Content-Type","application/json");

    }

    @When("user hits api with get method with endpoint of {string} with params of {int} and {string} and {string},{string},{string}")
    @Step("get items")
    public void userHitsApiWithGetMethodWithEndpointOfWithParamsOfAndAnd(String endpoint, int storeId, String reqid, String channelId, String clientId, String username) {
        System.out.println("----------GET items in digital catalog and get brand id-----------");
        response = requestSpecification
                .queryParam("storeId",storeId)
                .queryParam("requestId",reqid)
                .queryParam("channelId",channelId)
                .queryParam("clientId",clientId)
                .queryParam("username",username)
                .when()
                .get(endpoint);
    }

    @And("Filter response with product type {string} BrandName {string} andget BrandId")
    @Step("filter and get brand id")
    public void filterResponseWithProductTypeBrandNameAndgetBrandId(String productType, String brandname) {

        productTypeCode=productType;
        brandName=brandname;
        String responseBody = response.getBody().asString();
        JSONObject jsonResponse = new JSONObject(responseBody);
        JSONArray contentArray = jsonResponse.getJSONArray("content");
        for (int i = 0; i < contentArray.length(); i++) {
            JSONObject item = contentArray.getJSONObject(i);
            JSONObject brand = item.getJSONObject("brand");
            JSONObject productTypes = item.getJSONObject("productType");
            if (productTypeCode.equals(productTypes.getString("productTypeCode")) &&
                    brandName.equals(brand.getString("brandName"))) {
                System.out.println("brandId: " + brand.getInt("brandId"));
                brandID=brand.getInt("brandId");
            }
        }
        Assert.assertEquals(200,response.statusCode());
        Assert.assertTrue(responseBody.contains("\"success\":true"), "Response should indicate success");
    }

    @Then("user should get response code {int}")
    public void userShouldGetResponseCode(int responseCode) {
        Assert.assertEquals(responseCode,response.statusCode());
    }

    @When("user hits api with get method with endpoint of {string} with params of {int} and {string} and {string},{string},{string},brandid,ProductType")
    @Step("GET default providerId and providerName")
    public void userHitsApiWithGetMethodWithEndpointOfWithParamsOfAndAndBrandidProductType(String endpoint, int storeId, String reqid, String channelId, String clientId, String username) {
        System.out.println("----------GET default providerId and providerName-----------");
        System.out.println("brandId : "+brandID);
        System.out.println("product : "+productTypeCode);
        response = requestSpecification
                .queryParam("storeId",storeId)
                .queryParam("requestId",reqid)
                .queryParam("channelId",channelId)
                .queryParam("clientId",clientId)
                .queryParam("username",username)
                .queryParam("brandId",brandID)
                .queryParam("productTypeCode",productTypeCode)
                .when()
                .get(endpoint)
                .then()
                .body(matchesJsonSchemaInClasspath("getProviderSchema.json")).extract().response();
    }
    @And("Get default providerid and provider name")
    @Step("GET default providerId and providerName")
    public void getDefaultProvideridAndProviderName() {
        JSONObject jsonResponse = new JSONObject(response.getBody().asString());
        JSONObject value = jsonResponse.getJSONObject("value");
        defaultProviderId = value.getString("defaultProviderId");
        defaultProviderName = value.getString("defaultProviderName");
        String productType = value.getJSONObject("productType").getString("productTypeCode");
        String brand = value.getJSONObject("brand").getString("brandName");
        System.out.println("Default Provider Id :"+defaultProviderId);
        System.out.println("Default Provider Name :"+defaultProviderName);

        Assert.assertTrue(response.getBody().asString().contains("\"success\":true"), "Response should indicate success");
        Assert.assertEquals(productType,productTypeCode, "Product Type Code mismatch");
        Assert.assertEquals( brand,brandName, "Brand Name mismatch");
    }

    @When("user hits api with post method with endpoint of {string} with params of {int} and {string} and {string},{string},{string}")
    @Step("Save item mapping")
    public void userHitsApiWithPostMethodWithEndpointOfWithParamsOfAndAnd(String endpoint, int storeId, String reqId, String channelId, String clientId, String username) {
        System.out.println("-----------------SAVE ITEM MAPPING---------------------");
        JSONObject requestBody = new JSONObject();
        requestBody.put("active", true);
        requestBody.put("autoSwitch", "false");
        requestBody.put("brandId", brandID);
        requestBody.put("defaultProviderId",defaultProviderId);
        requestBody.put("defaultProviderName",defaultProviderName);
        requestBody.put("msisdn", "0814,0815,0816,0855,0856,0857,0858");
        requestBody.put("productTypeCode",productTypeCode);

        response = requestSpecification
                .queryParam("storeId",storeId)
                .queryParam("requestId",reqId)
                .queryParam("channelId",channelId)
                .queryParam("clientId",clientId)
                .queryParam("username",username)
                .body(requestBody.toString())
                .when()
                .post(endpoint).then()
                .log().all()
                .body(matchesJsonSchemaInClasspath("saveItemSchema.json")).extract().response();
        Assert.assertEquals(response.statusCode(), 200, "Expected status code 200 OK");
        String jsonResponse = response.body().asString();
        System.out.println("-------save Item Mapping------- \n"+jsonResponse);
        Assert.assertTrue(jsonResponse.contains("\"requestId\":\"RANDOM\""), "Response should contain requestId");
        Assert.assertTrue(jsonResponse.contains("\"success\":true"), "Response should indicate success");
        Assert.assertTrue(jsonResponse.contains("\"defaultProviderId\":\"BLP-25978\""), "Response should contain defaultProviderId");
        Assert.assertTrue(jsonResponse.contains("\"defaultProviderName\":\"NARINDO\""), "Response should contain defaultProviderName");
        Assert.assertTrue(jsonResponse.contains("\"productTypeCode\":\"PHONE_CREDIT\""), "Response should contain productTypeCode");
    }

    @When("user hits api with delete method with endpoint of {string} with params of {int} and {string} and {string},{string},{string}, cartId {string}")
    @Step("delete existing cart")
    public void userHitsApiWithDeleteMethodWithEndpointOfWithParamsOfAndAndCartId(String endpoint, int storeId, String reqId, String channelId, String clientId, String username, String cart) {
        System.out.println("----------DELETE EXISTING CART ----------------");
        response = requestSpecification
                .queryParam("storeId",storeId)
                .queryParam("requestId",reqId)
                .queryParam("channelId",channelId)
                .queryParam("clientId",clientId)
                .queryParam("username",username)
                .queryParam("cartId",cart)
                .when()
                .delete(endpoint)
                .then().log().all().extract().response();
        cartID=cart;
        System.out.println("cartid -------"+cartID);
    }

    @When("user hits api with get method with endpoint of {string} with params of {int} and {string} and {string},{string},{string},ProductType,brandname")
    @Step("Get sku code")
    public void userHitsApiWithGetMethodWithEndpointOfWithParamsOfAndAndProductTypeBrandname(String endpoint, int storeId, String reqId, String channelId, String clientId, String username) {
        System.out.println("----------GET SKU CODE-------------------");
        response = requestSpecification
                .queryParam("storeId",storeId)
                .queryParam("requestId",reqId)
                .queryParam("channelId",channelId)
                .queryParam("clientId",clientId)
                .queryParam("username",username)
                .queryParam("productType",productTypeCode)
                .queryParam("brandName",brandName)
                .when()
                .get(endpoint);
//                .then().log().all().extract().response();
    }

    @And("user fliters and get the sku code")
    public void userFlitersAndGetTheSkuCode() {
        JSONObject jsonResponse = new JSONObject(response.body().asString());
        JSONArray contentArray = jsonResponse.getJSONArray("content");
        for (int i = 0; i < contentArray.length(); i++) {
            JSONObject product = contentArray.getJSONObject(i);
            JSONObject digitalProduct = product.getJSONObject("digitalProduct");
            String internalName = digitalProduct.optString("internalName");
            if (INTERNAL_NAME.equals(internalName)) {
                JSONArray merchantSkuList = product.getJSONArray("merchantDigitalSkuList");
                for (int j = 0; j < merchantSkuList.length(); j++) {
                    JSONObject merchantSku = merchantSkuList.getJSONObject(j);
                    String merchantName = merchantSku.optString("merchantName");
                    if (defaultProviderName.equals(merchantName)) {
                        skuCode = merchantSku.optString("skuCode");
                        break;
                    }
                }
                if (skuCode != null) {
                    break;
                }
            }
        }
        System.out.println("Sku code ---------  :"+skuCode);
    }

    @When("user hits api with post method with endpoint of {string} with params of {int} and {string} and {string},{string},{string}, cartid, customerid ,cartType for checkout")
    @Step("Do checkout")
    public void userHitsApiWithPostMethodWithEndpointOfWithParamsOfAndAndCartidCustomeridCartType(String endpoint, int storeId, String reqId, String channelId, String clientId, String username) {
        System.out.println("----------DO checkout--------------------");
        response = requestSpecification
                .queryParam("storeId",storeId)
                .queryParam("requestId",reqId)
                .queryParam("channelId",channelId)
                .queryParam("clientId",clientId)
                .queryParam("username",username)
                .queryParam("cartId",cartID)
                .queryParam("customerLogonId",cartID)
                .queryParam("cartOwnershipType","MEMBER")
                .when()
                .post(endpoint)
                .then().log().all().extract().response();
    }

    @When("user hits api with post method with endpoint of {string} with params of {int} and {string} and {string},{string},{string}, cartid, {string} msisdn,productType for setting cart")
    @Step("set cart")
    public void userHitsApiWithPostMethodWithEndpointOfWithParamsOfAndAndCartidMsisdnProductTypeForCheckout(String endpoint, int storeId, String reqId, String channelId, String clientId, String username,String msisdn) {
        System.out.println("----------SET PULSA CART-----------");
        HashMap<String,Object> reqBody= new HashMap<>();
        reqBody.put("itemSku",skuCode);
        reqBody.put("operatorCode",brandName);
        reqBody.put("providerName",defaultProviderName);

        response = requestSpecification
                .queryParam("storeId", storeId)
                .queryParam("requestId", reqId)
                .queryParam("channelId", channelId)
                .queryParam("clientId", clientId)
                .queryParam("username", username)
                .queryParam("cartId",cartID)
                .queryParam("msisdn",msisdn)
                .queryParam("productType",productTypeCode)
                .body(reqBody)
                .when()
                .post("pulsaCart/setPulsaCartMsisdn")
                .then().log().all().extract().response();
    }


    @When("hit api with post method in endpoint {string} and with params of {int} and {string} and {string},{string},{string} add product to cart")
    @Step("Add product to cart")
    public void hitApiWithPostMethodInEndpointAndWithParamsOfAndAndAddProductToCart(String endpoint, int storeId, String reqId, String channelId, String clientId, String username) {
        System.out.println("----------ADD PRODUCT TO CART----------------------");
        HashMap<String,Object> reqBody= new HashMap<>();
        reqBody.put("productType",productTypeCode);
        reqBody.put("itemSku",skuCode);
        reqBody.put("cartId",cartID);
        reqBody.put("operatorName",brandName);
        reqBody.put("deviceId",deviceid);

        response = requestSpecification
                .queryParam("storeId",storeId)
                .queryParam("requestId",reqId)
                .queryParam("channelId",channelId)
                .queryParam("clientId",clientId)
                .queryParam("username",username)
                .body(reqBody)
                .when()
                .post(endpoint)
                .then().log().all().extract().response();
    }

    @When("hit api with post method in endpoint {string} and with params of {int} and {string} and {string},{string},{string} , payment method")
    @Step("Change payment flow")
    public void hitApiWithPostMethodInEndpointAndWithParamsOfAndAndPaymentMethod(String endpoint, int storeId, String reqId, String channelId, String clientId, String username) {
        System.out.println("----------CHANGE PAYMENT FLOW------------------------");
        response = requestSpecification
                .queryParam("storeId",storeId)
                .queryParam("requestId",reqId)
                .queryParam("channelId",channelId)
                .queryParam("clientId",clientId)
                .queryParam("username",username)
                .queryParam("cartId",cartID)
                .queryParam("paymentMethod",paymentMethod)
                .when()
                .post(endpoint)
                .then().log().all().extract().response();
        System.out.println("pymnt changed");
    }

    @When("hit api with post method in endpoint {string} and with params of {int} and {string} and {string},{string},{string} add pay order by passing req body with cartId")
    @Step("Pay order")
    public void hitApiWithPostMethodInEndpointAndWithParamsOfAndAndAddPayOrderByPassingReqBodyWithCartId(String endpoint, int storeId, String reqId, String channelId, String clientId, String username) throws JsonProcessingException {
        System.out.println("----------PAY ORDER -----------------------");
        HashMap<String,Object> reqBody= new HashMap<>();
        reqBody.put("pulsaCartId",cartID);
        reqBody.put("extendedData", new HashMap<String, Object>());

        response = requestSpecification
                .queryParam("storeId",storeId)
                .queryParam("requestId",reqId)
                .queryParam("channelId",channelId)
                .queryParam("clientId",clientId)
                .queryParam("username",username)
                .body(reqBody)
                .when()
                .post(endpoint)
                .then()
                .body(matchesJsonSchemaInClasspath("payOrderSchema.json"))
                .log().all().extract().response();

        JsonNode rootNode = objectMapper.readTree(response.body().asString());
        JsonNode valueNode = rootNode.path("value");
        orderId = valueNode.path("orderId").asText();
        System.out.println("order Id: "+orderId);

    }

    @When("hit api with post method in endpoint {string} and with params of {int} and {string} and {string},{string},{string} add approve order by order id")
    public void hitApiWithPostMethodInEndpointAndWithParamsOfAndAndAddApproveOrderByOrderId(String endpoint, int storeId, String reqId, String channelId, String clientId, String username) {
        System.out.println("----------APPROVE ORDER--------------------");
        HashMap<String,Object> reqBody= new HashMap<>();
        reqBody.put("orderId",orderId);
        reqBody.put("extData",new HashMap<String, Object>());

        response = requestSpecification
                .queryParam("storeId",storeId)
                .queryParam("requestId",reqId)
                .queryParam("channelId",channelId)
                .queryParam("clientId",clientId)
                .queryParam("username",username)
                .body(reqBody)
                .when()
                .post(endpoint)
                .then()
                .body(matchesJsonSchemaInClasspath("approveOrder.json"))
                .log().all().extract().response();
    }

    @When("user hit api with get method endpoint of {string} and with params of {int} and {string} and {string},{string},{string}, orderId")
    public void userHitApiWithGetMethodEndpointOfAndWithParamsOfAndAndOrderId(String endpoint, int storeId, String reqId, String channelId, String clientId, String username) {
        System.out.println("----------GET order and verify--------------");
        System.out.println("ORDER ID : "+orderId);
        response = requestSpecification
                .queryParam("storeId",storeId)
                .queryParam("requestId",reqId)
                .queryParam("channelId",channelId)
                .queryParam("clientId",clientId)
                .queryParam("username",username)
                .queryParam("orderId",orderId)
                .when()
                .get(endpoint)
                .then()
                .log().all().extract().response();

        String responseBody = response.body().asString();
        JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
        JsonObject pulsaCartItem = jsonResponse
                .getAsJsonObject("value")
                .getAsJsonObject("pulsaOrder")
                .getAsJsonObject("pulsaCartItem");
        String providerName = pulsaCartItem.get("providerName").getAsString();
        String description = pulsaCartItem.get("description").getAsString();
        Assert.assertEquals(providerName,defaultProviderName);
        Assert.assertEquals(description,INTERNAL_NAME);
        Assert.assertEquals(200,response.statusCode());
    }
}
