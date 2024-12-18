
# Order API

Bluesix/NTT API Java challenge


## Technologies

- Java 17
- Spring 3.4.0
- Maven
- Redis
- Docker
- H2 database



## How to run
Ensure you have docker installed on your machine before running the application.

### Locally
- run command ```docker run --name redis-container -p 6379:6379 -d redis``` to have a redis up and running inside a docker container
- run the application in your IDE. By default, the API will be exposed to port 8082 

### Docker environment
- run command ```mvn clean package``` and make sure your jar was generated inside the target folder
- run ```docker-compose up --build``` in the root folder of the project. By default, the API will be exposed to port 8082 and redis on port 6379

### Database
- H2 Database can be accessed on http://localhost:8082/h2-console


## API Reference

#### Get all orders

```http
  GET /api/order-manangement/orders
```

#### Receive an order

```http
  POST /api/order-manangement/receive
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `externalOrderId`      | `string` | **Required**. Order external id |
| `items`      | `list` | **Required**. List of items |

#### Payload Example

```{
  "externalOrderId": "ORD1234",
  "items": [
    {
      "productId": "P001",
      "quantity": 2,
      "price": 200.00
    },
    {
      "productId": "P002",
      "quantity": 1,
      "price": 1500.00
    },
    {
      "productId": "P003",
      "quantity": 5,
      "price": 20.00
    }
  ]
}

