# meCash - Wallet Solution

---

## Introduction

This is a financial application that enables businesses and individuals to send and receive money across different currencies.

---

## System Functionality

The system will allow users to:
* Create an account
* Log in to their account
* Deposit money into their account
* Withdraw money from their account
* Transfer money to another user’s account
* View account balance
* View transaction history

The System Develop a set of REST service APIs which are documented using swagger (Open API)  - [http://localhost:8080/swagger-ui/index.html], that allows users to:

- Create an account;
- User authentication to log into the system;
- Deposit money;
- Search and reserve transactions;
- Send registration notification email to users upon registration.
- The value for the placeholder in the config file {mail_password} = yxesggihwlwdymdq

---

## Prerequisites
- Java 17
- Maven

## Build
```bash
mvn clean install
```

## RUN
```bash
mvn spring-boot:run
```
## TEST
````
mvn test

````

:scroll: **END**
