# Modernization with Stream Processing and Event-Driven Architecture

<!-- Modernize the legacy system with stream processing and event-driven architecture.
 -->
The purpose of this project is to demonstrate the modernization of a legacy systsem into an Event-Driven microservices architecture.

## Requirements
Legacy system must be running all the time during this the modernization effort and both legacy and modernized data stores must be sync.

## Legacy System
Our sample legacy system has 5 units: Customers, Products, Orders, Payments, and Shipping. The database is MySQL and contains 7 tables.

### Diagram
In this diagram, we can see the a Web Server handles user requests by communicating with intermidiate and data layers:
![Legacy Diagram](/images/legacy.png)
