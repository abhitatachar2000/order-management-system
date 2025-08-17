# Order Management System

### Overview

The **Order Management System** is a modular Spring Boot application designed for e-commerce platforms to manage the product catalog, inventory, and order lifecycle. It allows users to browse products, check stock availability, and place orders while ensuring real-time validation of product details and stock. The system is structured into three independent modules that communicate with each other using **WebClients**.

The modules are as follows:
- **Catalog Module**: Provides product details including cost and availability, by interacting with the Inventory module for real-time stock data.  
- **Inventory Module**: Maintains product stock levels for products listed in the catalog.  
- **Orders Module**: Manages the lifecycle of orders, validates stock with the Inventory module, retrieves pricing from the Catalog module, and calculates the total order value.  

Together, these modules ensure seamless interactions and accurate processing of orders, making the system reliable and scalable for e-commerce applications.

![OMS Overview](./docs/images/oms-overview-block-diagram-updated.png)
