Feature: Testing the Order Service
#To check internal product API call from Product service
  Scenario: calling product welcome API from the Order Service
    Given API endpoint "http://localhost:8082/api/order/callProduct" to call the Product welcome API
    When client make call to above API then return status 200 OK
    Then client will receive the message 'Welcome to Product Service'

#To create new Order  http://localhost:8082/api/order/createNewOrder/{customerName}
  Scenario: Create new order by passing customer Name, url and product List
    Given customer name "Customer1", url "http://localhost:8082/api/order/createNewOrder/{customerName}" and product list
      |productId|productName|price  |quantity|
      |1        |Dell Laptop|40000.0|1       |
      |2        |HP Laptop  |45000.0|1       |
    When client make call for above API then return status 200 OK
    Then client will receive the order details
      |orderId|customerName|orderStatus|paymentType| totalBillAmount|productList                                                                                                                                           |
      |1000   |Customer1   |NEW        |CASH       |  85000.0       |[{"productId":1,"productName":"Dell Laptop","price":40000.0,"quantity":1},{"productId":2,"productName":"HP Laptop","price":45000.0,"quantity":1}]   |

#To get all the orders http://localhost:8082/api/order/getAll
  Scenario: get all the order from the Order service
    Given to get the all orders url "http://localhost:8082/api/order/getAll"
    When client make call for the above API to get the all orders then return status 200 OK
    Then client will receive all the order details
      |orderId|customerName|orderStatus|paymentType| totalBillAmount|productList                                                                                                                                           |
      |1000   |Customer1   |NEW        |CASH       |  85000.0       |[{"productId":1,"productName":"Dell Laptop","price":40000.0,"quantity":1},{"productId":2,"productName":"HP Laptop","price":45000.0,"quantity":1}]   |

#To get the order by the order id http://localhost:8082/api/order/get/{orderId}
  Scenario: get all the order from the Order service by providing orderId in path variable
    Given to get the order details by the orderId 1000 and url "http://localhost:8082/api/order/get/{orderId}"
    When client make call for the above API to get the order details return status 200 OK
    Then client will receive the searched order details
      |orderId|customerName|orderStatus|paymentType| totalBillAmount|productList                                                                                                                                           |
      |1000   |Customer1   |NEW        |CASH       |  85000.0       |[{"productId":1,"productName":"Dell Laptop","price":40000.0,"quantity":1},{"productId":2,"productName":"HP Laptop","price":45000.0,"quantity":1}]   |

#/addProductIntoCart/{orderId}
  Scenario: add product into the given orderId by providing list of products with required quantity
    Given order Id 1000 is given with the below required list of product and required quantity to call url "http://localhost:8082/api/order/addProductIntoCart/{orderId}"
      |productId|productName|price  |quantity|
      |1        |Dell Laptop|40000.0|1       |
      |2        |HP Laptop  |45000.0|1       |
    When client make call for the above API to add the product quantity into the order and return status 200 OK
    Then client will receive the updated order details after adding the product quantity into the given order
      |orderId|customerName|orderStatus|paymentType| totalBillAmount|productList                                                                                                                                           |
      |1000   |Customer1   |NEW        |CASH       |  170000.0       |[{"productId":1,"productName":"Dell Laptop","price":40000.0,"quantity":2},{"productId":2,"productName":"HP Laptop","price":45000.0,"quantity":2}]   |

#/deleteProductFromCart/{orderId}
  Scenario: delete the products from the given orderId by providing the list of products with the required product quantity
    Given order Id 1000 is given with the below required list of product and required quantity to be deleted from the order and url "http://localhost:8082/api/order/deleteProductFromCart/{orderId}"
      |productId|productName|price  |quantity|
      |1        |Dell Laptop|40000.0|1       |
      |2        |HP Laptop  |45000.0|1       |
    When client makes the call to above API to delete the product quantity from the given order and return status 200 OK
    Then client will receive the updated order details after deleted the product quantity
      |orderId|customerName|orderStatus|paymentType| totalBillAmount|productList                                                                                                                                         |
      |1000   |Customer1   |NEW        |CASH       |  85000.0       |[{"productId":1,"productName":"Dell Laptop","price":40000.0,"quantity":1},{"productId":2,"productName":"HP Laptop","price":45000.0,"quantity":1}]   |





