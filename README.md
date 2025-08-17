# Order Management System

### Overview

The **Order Management System** is a modular Spring Boot application designed for e-commerce platforms to manage the product catalog, inventory, and order lifecycle. It allows users to browse products, check stock availability, and place orders while ensuring real-time validation of product details and stock. The system is structured into three independent modules that communicate with each other using **WebClients**.

The modules are as follows:
- **Catalog Module**: Provides product details including cost and availability, by interacting with the Inventory module for real-time stock data.  
- **Inventory Module**: Maintains product stock levels for products listed in the catalog.  
- **Orders Module**: Manages the lifecycle of orders, validates stock with the Inventory module, retrieves pricing from the Catalog module, and calculates the total order value.  

![OMS Overview](./docs/images/oms-overview-block-diagram-updated.png)

### API Specifications For Each Microservice

#### Orders API

| HTTP Method | Endpoint | Description | Request Body | Query Params | Path Params | Responses |
|-------------|----------|-------------|--------------|--------------|-------------|-----------|
| **POST** | `/api/v1/orders` | Create a new order | ``` { "itemId": 1, "quantity": 2, "status": "new", "contact": "test@example.com" }``` | – | – | **201 Created** – Returns created order`.<br>**500 Internal Server Error** – If creation fails. |
| **GET** | `/api/v1/orders` | Fetch all orders | – | – | – | **200 OK** – Returns list of orders.<br>**500 Internal Server Error** – If retrieval fails. |
| **GET** | `/api/v1/orders/{id}` | Fetch order by ID | – | – | `id` (integer) | **200 OK** – Returns found order.<br>**404 Not Found** – If order does not exist.<br>**500 Internal Server Error** – If retrieval fails. |
| **PUT** | `/api/v1/orders/{id}` | Update an existing order by ID | ```{ "itemId": 1, "quantity": 2, "status": "processing", "contact": "test@example.com" }``` | – | `id` (integer) | **200 OK** – Returns updated order.<br>**404 Not Found** – If order does not exist.<br>**500 Internal Server Error** – If update fails. |
| **DELETE** | `/api/v1/orders/{id}` | Delete an order by ID | – | – | `id` (integer) | **200 OK** – Successfully deleted.<br>**404 Not Found** – If order does not exist.<br>**500 Internal Server Error** – If deletion fails. |
| **GET** | `/api/v1/orders?status={status}` | Fetch all orders by status | – | `status` (string) | – | **200 OK** – Returns List of orders with that status.<br>**500 Internal Server Error** – If retrieval fails. |

#### Inventory API (v1)

| HTTP Method | Endpoint | Description | Request Body | Query Params | Path Params | Responses |
|-------------|----------|-------------|--------------|--------------|-------------|-----------|
| **GET** | `/api/v1/inventory/items` | Fetch all inventory items | – | – | – | **200 OK** – Returns list of inventory items.<br>**500 Internal Server Error** – If retrieval fails. |
| **POST** | `/api/v1/inventory/items` | Add a new inventory item | ``` { "id": 1, "quantity": 100 }``` | – | – | **201 Created** – Returns created Inventory item.<br>**409 Conflict** – If item with given ID already exists.<br>**500 Internal Server Error** – If creation fails. |
| **GET** | `/api/v1/inventory/items/{id}` | Fetch single inventory item by ID | – | – | `id` (integer) | **200 OK** – Returns the found inventory item.<br>**404 Not Found** – If item does not exist.<br>**500 Internal Server Error** – If retrieval fails. |
| **DELETE** | `/api/v1/inventory/items/item?id={id}` | Delete inventory item by ID | – | `id` (integer) | – | **200 OK** – Successfully deleted.<br>**404 Not Found** – If item does not exist.<br>**500 Internal Server Error** – If deletion fails. |
| **PATCH** | `/api/v1/inventory/items` | Update an inventory item | ``` { "id": 1, "quantity": 150 }``` | – | – | **200 OK** – Successfully updated.<br>**404 Not Found** – If item does not exist.<br>**500 Internal Server Error** – If update fails. |

#### Catalog API (v1)

| HTTP Method | Endpoint | Description | Request Body | Query Params | Path Params | Responses |
|-------------|----------|-------------|--------------|--------------|-------------|-----------|
| **GET** | `/api/v1/catalog/items` | Fetch all catalog items | – | – | – | **200 OK** – Returns list of catalog items.<br>**500 Internal Server Error** – If retrieval fails. |
| **POST** | `/api/v1/catalog/items` | Create a new catalog item |```{ "name": "Laptop", "pricePerUnit": 1200.0, "category": "Electronics" }``` | – | – | **201 Created** – Item created successfully.<br>**500 Internal Server Error** – If creation fails. |
| **GET** | `/api/v1/catalog/items/{id}` | Fetch single catalog item by ID | – | – | `id` (integer) | **200 OK** – Returns a catalog item.<br>**404 Not Found** – If item does not exist.<br>**500 Internal Server Error** – If retrieval fails. |
| **GET** | `/api/v1/catalog/items/search?category={category}` | Fetch all catalog items by category | – | `category` (string) | – | **200 OK** – Returns list of catalog items of a specific category.<br>**500 Internal Server Error** – If retrieval fails. |
| **PUT** | `/api/v1/catalog/items/{id}` | Update a catalog item by ID | ``` { "name": "Gaming Laptop", "pricePerUnit": 1500.0, "category": "Electronics" }``` | – | `id` (integer) | **200 OK** – Returns updated catalog item.<br>**500 Internal Server Error** – If update fails. |
| **DELETE** | `/api/v1/catalog/items/{id}` | Delete a catalog item by ID | – | – | `id` (integer) | **204 No Content** – Successfully deleted.<br>**404 Not Found** – If item does not exist.<br>**500 Internal Server Error** – If deletion fails. |
| **DELETE** | `/api/v1/catalog/items?category={category}` | Delete all catalog items by category | – | `category` (string) | – | **204 No Content** – Items deleted.<br>**404 Not Found** – If no items in that category exist.<br>**500 Internal Server Error** – If deletion fails. |

