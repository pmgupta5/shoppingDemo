Feature: Testing the Product Service

#To check Welcome API from Product service
  Scenario: Welcome to product service API
    Given Welcome product url "http://localhost:8081/api/product/"
    When  user make call to above welcome API
    Then  user will receive the message 'Welcome to Product Service'

#To check get All product API from product Service
  Scenario: Get All the product from the inventory API call
    Given To get the all products URL is "http://localhost:8081/api/product/getAll"
    When user make call to the above URL
    Then user will get the all Product List
      |productId|productName    |price  |quantity|
      |1        |Dell Laptop    |40000.0|10      |
      |2        |HP Laptop      |45000.0|11      |
      |3        |Acer Laptop    |30000.0|6       |
      |4        |Lenovo Laptop  |32000.0|8       |

#To get Product by Product ID successful scenario
  Scenario: To get product by product ID success scenario
    Given Product ID 1 is given as input
    And Product Id 1 pass to get the product from API "http://localhost:8081/api/product/get/"
    When above API will get call with the success response and status code 200 OK
    Then user will get the Product details for the requested product Id
      |productId|productName    |price  |quantity|
      |1        |Dell Laptop    |40000.0|10      |

#To get Product by Product ID failure scenario
  Scenario: Failure scenario to call the API with the wrong Product ID
    Given wrong Product ID 0 is given as input which is not available in the inventory
    And making API "http://localhost:8081/api/product/get/" call with the given product id 0 in the path variable
    When API will return the failure response without the product details
    Then user will get the message "Invalid ProductId:0" and status equal to "400 BAD_REQUEST"


#Update product inventory when client request for the add product into cart endpoint http://localhost:8081/api/product/addProductToCart
#with the list of product provided as input from request body
  Scenario: remove product from the inventory when the product service endpoint http://localhost:8081/api/product/addProductToCart get call
    Given endpoint "http://localhost:8081/api/product/addProductToCart" to update the product inventory by providing the product information are here
    |productId|productName|price  |quantity|
    |1        |Dell Laptop|40000.0|1       |
    |2        |HP Laptop  |45000.0|1       |
    When above API will get invoke successfully with the response status code 200 OK
    Then API will return the response "SUCCESS";

#Update product inventory when client request for the add product into cart endpoint http://localhost:8081/api/product/deleteProductFromCart
#with the list of product provided as input from request body
  Scenario: add product into the inventory when the product service endpoint http://localhost:8081/api/product/addProductToCart get call
    Given endpoint "http://localhost:8081/api/product/deleteProductFromCart" to delete the product from the inventory by providing the product information are here
      |productId|productName|price  |quantity|
      |1        |Dell Laptop|40000.0|1       |
      |2        |HP Laptop  |45000.0|1       |
    When API will get invoke successfully with the response status code 200 OK
    Then will get response "SUCCESS";