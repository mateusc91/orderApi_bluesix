
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
Ensure you have docker installed in your machine before running the application.

- run ```docker-compose up --build``` in the root folder of the project. By default, the API will be exposed to port 8082




## API Reference

#### Get all orders

```http
  GET /api/orders/export
```

#### Receive an order

```http
  POST /api/orders/receive
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

