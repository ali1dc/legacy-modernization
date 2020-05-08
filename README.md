# Modernization with Stream Processing and Event-Driven Architecture

<!-- Modernize the legacy system with stream processing and event-driven architecture.
 -->
The purpose of this project is to demonstrate the modernization of a legacy systsem into an Event-Driven microservices architecture.

## Requirements
Legacy system must be running all the time during this the modernization effort and both legacy and modernized data stores must be sync.

## Legacy System
Our sample legacy system has 5 units: Customers, Products, Orders, Payments, and Shipping.

### Diagram
In this diagram, we can see the a Web Server handles user requests by communicating with intermidiate and data layers:
![Legacy Diagram](/images/legacy.png)

### Database
The database is MySQL and contains 7 tables:
1. customers
2. categories
3. products
4. orders
5. order_items
6. payments
7. shipping

## Modernized System

Microservices:
![Modernized Diagram](/images/modernized-system.png)
