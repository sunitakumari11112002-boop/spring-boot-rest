# UK Banking System - Spring Boot REST API 🏦

![Java](https://img.shields.io/badge/Java-8%2B-orange) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7%2B-brightgreen) ![DDD](https://img.shields.io/badge/Architecture-DDD-blue) ![License](https://img.shields.io/badge/License-MIT-yellow)

## 📋 Table of Contents
- [Executive Summary](#executive-summary)
- [System Architecture Overview](#system-architecture-overview)
- [Domain-Driven Design (DDD) Implementation](#domain-driven-design-ddd-implementation)
- [Project Structure](#project-structure)
- [Technical Stack](#technical-stack)
- [System Flow Diagrams](#system-flow-diagrams)
- [API Documentation](#api-documentation)
- [Database Design](#database-design)
- [Getting Started](#getting-started)
- [Testing Strategy](#testing-strategy)
- [Deployment Guide](#deployment-guide)

## 🎯 Executive Summary

### System Purpose
The UK Banking System is a **Domain-Driven Design (DDD)** compliant Spring Boot REST API designed specifically for UK financial institutions. It implements core banking operations with strict adherence to UK banking regulations, including GDPR compliance, FCA guidelines, and PCI DSS standards.

### Key Business Capabilities
- **Customer Lifecycle Management**: Complete customer onboarding, KYC validation, and account management
- **Account Operations**: Multi-account support with UK-specific account types and overdraft facilities
- **Transaction Processing**: Real-time transaction processing with fraud detection and audit trails
- **Regulatory Compliance**: UK-specific validations for NI numbers, postcodes, and phone numbers
- **Event-Driven Architecture**: Domain events for system integration and audit logging

## 🏗️ System Architecture Overview

```ascii
┌─────────────────────────────────────────────────────────────────────┐
│                        UK BANKING SYSTEM                           │
│                     (Domain-Driven Design)                         │
└─────────────────────────────────────────────────────────────────────┘

┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   PRESENTATION  │    │   APPLICATION   │    │     DOMAIN      │
│     LAYER       │    │      LAYER      │    │     LAYER       │
├─────────────────┤    ├─────────────────┤    ├─────────────────┤
│ • REST APIs     │───▶│ • App Services  │───▶│ • Aggregates    │
│ • Controllers   │    │ • DTOs          │    │ • Entities      │
│ • Exception     │    │ • Mappers       │    │ • Value Objects │
│   Handlers      │    │ • Validation    │    │ • Domain Events │
│ • HTTP Mapping  │    │ • Coordination  │    │ • Business Rules│
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────────────────────────────────────────────────────────┐
│                    INFRASTRUCTURE LAYER                            │
├─────────────────┬─────────────────┬─────────────────┬─────────────────┤
│   PERSISTENCE   │    MESSAGING    │    EXTERNAL     │    SECURITY     │
│                 │                 │    SERVICES     │                 │
│ • JPA Repos     │ • Event Pub/Sub │ • Payment Gws   │ • Authentication│
│ • Database      │ • Message Queue │ • KYC Services  │ • Authorization │
│ • Transactions  │ • Event Store   │ • Fraud Detection• Encryption    │
└─────────────────┴─────────────────┴─────────────────┴─────────────────┘
```

## 🧠 Domain-Driven Design (DDD) Implementation

### Bounded Contexts

```ascii
┌─────────────────────────────────────────────────────────────────────┐
│                        BANKING DOMAIN                              │
├─────────────────┬─────────────────┬─────────────────┬─────────────────┤
│   CUSTOMER      │     ACCOUNT     │   TRANSACTION   │   COMPLIANCE    │
│   MANAGEMENT    │   MANAGEMENT    │   PROCESSING    │   & AUDIT       │
│                 │                 │                 │                 │
│ • Customer      │ • BankAccount   │ • Transaction   │ • AuditLog      │
│ • PersonalName  │ • AccountType   │ • Money         │ • ComplianceRule│
│ • UKAddress     │ • AccountStatus │ • TransferResult│ • RiskAssessment│
│ • UKPhoneNumber │ • Overdraft     │ • Reference     │ • EventStore    │
│ • NINumber      │ • Interest      │ • Balance       │ • Notification  │
└─────────────────┴─────────────────┴─────────────────┴─────────────────┘
```

### Aggregate Design

```ascii
┌─────────────────────────────────────────────────────────────────────┐
│                         CUSTOMER AGGREGATE                          │
├─────────────────────────────────────────────────────────────────────┤
│  ┌─────────────────┐                                                │
│  │    CUSTOMER     │ (Aggregate Root)                               │
│  │   (Entity)      │                                                │
│  ├─────────────────┤                                                │
│  │ + customerId    │                                                │
│  │ + personalName  │──┐                                             │
│  │ + email         │  │  ┌─────────────────┐                       │
│  │ + phoneNumber   │  └─▶│  PersonalName   │ (Value Object)         │
│  │ + dateOfBirth   │     │   (Embedded)    │                       │
│  │ + address       │──┐  ├─────────────────┤                       │
│  │ + niNumber      │  │  │ + firstName     │                       │
│  │ + status        │  │  │ + lastName      │                       │
│  │ + registeredAt  │  │  └─────────────────┘                       │
│  └─────────────────┘  │                                             │
│                       │  ┌─────────────────┐                       │
│                       └─▶│   UKAddress     │ (Value Object)         │
│                          │   (Embedded)    │                       │
│                          ├─────────────────┤                       │
│                          │ + addressLine   │                       │
│                          │ + postcode      │                       │
│                          └─────────────────┘                       │
└─────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│                       BANK ACCOUNT AGGREGATE                        │
├─────────────────────────────────────────────────────────────────────┤
│  ┌─────────────────┐                                                │
│  │   BANK ACCOUNT  │ (Aggregate Root)                               │
│  │    (Entity)     │                                                │
│  ├─────────────────┤                                                │
│  │ + accountId     │                                                │
│  │ + identifier    │──┐  ┌─────────────────┐                       │
│  │ + accountType   │  └─▶│AccountIdentifier│ (Value Object)         │
│  │ + balance       │──┐  │   (Embedded)    │                       │
│  │ + overdraftLimit│  │  ├─────────────────┤                       │
│  │ + interestRate  │  │  │ + sortCode      │                       │
│  │ + status        │  │  │ + accountNumber │                       │
│  │ + openedAt      │  │  └─────────────────┘                       │
│  │ + customer      │  │                                             │
│  └─────────────────┘  │  ┌─────────────────┐                       │
│          │             └─▶│     Money       │ (Value Object)         │
│          │                │   (Embedded)    │                       │
│          │                ├─────────────────┤                       │
│          │                │ + amount        │                       │
│          │                │ + currency      │                       │
│          │                └─────────────────┘                       │
│          │                                                          │
│          ▼                                                          │
│  ┌─────────────────┐                                                │
│  │  TRANSACTION    │ (Entity)                                       │
│  │                 │                                                │
│  ├─────────────────┤                                                │
│  │ + transactionId │                                                │
│  │ + amount        │                                                │
│  │ + type          │                                                │
│  │ + description   │                                                │
│  │ + reference     │                                                │
│  │ + processedAt   │                                                │
│  └─────────────────┘                                                │
└─────────────────────────────────────────────────────────────────────┘
```

## 📁 Project Structure

```ascii
src/main/java/br/com/ukbank/
│
├── UKBankingApplication.java           # Spring Boot Main Class
│
├── presentation/                       # Presentation Layer (Controllers)
│   └── controllers/
│       └── CustomerController.java     # REST API Endpoints
│
├── application/                        # Application Layer
│   ├── services/
│   │   ├── CustomerApplicationService.java    # Customer Operations
│   │   ├── BankingAccountService.java         # Account Operations
│   │   └── DomainEventPublisher.java          # Event Publishing
│   │
│   ├── dto/                           # Data Transfer Objects
│   │   ├── CustomerRegistrationRequest.java
│   │   ├── CustomerResponse.java
│   │   ├── AccountOpeningRequest.java
│   │   └── MoneyTransferRequest.java
│   │
│   └── exceptions/                    # Application Exceptions
│       ├── CustomerNotFoundException.java
│       ├── BankAccountNotFoundException.java
│       └── InsufficientFundsException.java
│
├── domain/                           # Domain Layer (Core Business Logic)
│   ├── model/                        # Domain Entities & Aggregates
│   │   ├── Customer.java             # Customer Aggregate Root
│   │   ├── BankAccount.java          # BankAccount Aggregate Root
│   │   ├── Transaction.java          # Transaction Entity
│   │   ├── AccountType.java          # Account Type Enum
│   │   └── AccountStatus.java        # Account Status Enum
│   │
│   ├── valueobjects/                 # Value Objects
│   │   ├── PersonalName.java         # Name Value Object
│   │   ├── UKAddress.java            # UK Address Value Object
│   │   ├── UKPhoneNumber.java        # UK Phone Number Value Object
│   │   ├── NationalInsuranceNumber.java  # UK NI Number Value Object
│   │   ├── AccountIdentifier.java    # Account ID Value Object
│   │   └── Money.java                # Money Value Object
│   │
│   └── events/                       # Domain Events
│       ├── DomainEvent.java          # Base Domain Event
│       ├── CustomerRegisteredEvent.java
│       └── TransactionProcessedEvent.java
│
└── infrastructure/                   # Infrastructure Layer
    └── repositories/
        ├── CustomerRepository.java   # Customer Data Access
        └── BankAccountRepository.java # Account Data Access
```

## 🛠️ Technical Stack

### Core Technologies
```ascii
┌─────────────────────────────────────────────────────────────────────┐
│                          TECHNOLOGY STACK                          │
├─────────────────┬─────────────────┬─────────────────┬─────────────────┤
│    FRAMEWORK    │    PERSISTENCE  │    VALIDATION   │     UTILITIES   │
│                 │                 │                 │                 │
│ • Spring Boot   │ • Spring Data   │ • Bean          │ • Lombok        │
│   2.7+          │   JPA           │   Validation    │ • Jackson       │
│ • Spring Web    │ • Hibernate     │ • Custom        │ • ModelMapper   │
│ • Spring        │ • H2 Database   │   Validators    │ • JavaFaker     │
│   Security      │ • Connection    │ • UK Regex     │ • SLF4J Logging │
│                 │   Pooling       │   Patterns      │                 │
└─────────────────┴─────────────────┴─────────────────┴─────────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│                          TESTING STACK                             │
├─────────────────┬─────────────────┬─────────────────┬─────────────────┤
│   UNIT TESTING  │ INTEGRATION     │  PERFORMANCE    │    QUALITY      │
│                 │   TESTING       │    TESTING      │   ASSURANCE     │
│ • JUnit 5       │ • Spring Boot   │ • JMeter        │ • SpotBugs      │
│ • Mockito       │   Test          │ • Artillery     │ • PMD           │
│ • AssertJ       │ • TestContainers│ • Gatling       │ • Checkstyle    │
│ • MockMvc       │ • WireMock      │                 │ • SonarQube     │
└─────────────────┴─────────────────┴─────────────────┴─────────────────┘
```

## 🔄 System Flow Diagrams

### Customer Registration Flow

```ascii
┌─────────┐    ┌──────────────┐    ┌─────────────────┐    ┌──────────┐    ┌─────────┐
│ Client  │    │ Controller   │    │ Application     │    │ Domain   │    │Database │
│         │    │              │    │ Service         │    │ Model    │    │         │
└────┬────┘    └──────┬───────┘    └────────┬────────┘    └────┬─────┘    └────┬────┘
     │                │                     │                  │               │
     │ POST /customers│                     │                  │               │
     ├───────────────▶│                     │                  │               │
     │                │                     │                  │               │
     │                │ registerCustomer()  │                  │               │
     │                ├────────────────────▶│                  │               │
     │                │                     │                  │               │
     │                │                     │ checkDuplicates()│               │
     │                │                     ├─────────────────────────────────▶│
     │                │                     │                  │               │
     │                │                     │◀─────────────────────────────────┤
     │                │                     │                  │               │
     │                │                     │ Customer.registerNewCustomer()   │
     │                │                     ├─────────────────▶│               │
     │                │                     │                  │               │
     │                │                     │                  │ validate()    │
     │                │                     │                  │──┐            │
     │                │                     │                  │◀─┘            │
     │                │                     │                  │               │
     │                │                     │◀─────────────────┤               │
     │                │                     │                  │               │
     │                │                     │ save(customer)   │               │
     │                │                     ├─────────────────────────────────▶│
     │                │                     │                  │               │
     │                │                     │◀─────────────────────────────────┤
     │                │                     │                  │               │
     │                │                     │ publishEvent(CustomerRegistered)  │
     │                │                     │──┐               │               │
     │                │                     │◀─┘               │               │
     │                │                     │                  │               │
     │                │◀────────────────────┤                  │               │
     │                │                     │                  │               │
     │◀───────────────┤                     │                  │               │
     │   201 Created  │                     │                  │               │
     │   Customer ID  │                     │                  │               │
```

### Money Transfer Flow

```ascii
┌─────────┐    ┌──────────────┐    ┌─────────────────┐    ┌──────────┐    ┌─────────┐
│ Client  │    │ Controller   │    │ Banking         │    │ Domain   │    │Database │
│         │    │              │    │ Service         │    │ Model    │    │         │
└────┬────┘    └──────┬───────┘    └────────┬────────┘    └────┬─────┘    └────┬────┘
     │                │                     │                  │               │
     │ POST /transfer │                     │                  │               │
     ├───────────────▶│                     │                  │               │
     │                │                     │                  │               │
     │                │ processTransfer()   │                  │               │
     │                ├────────────────────▶│                  │               │
     │                │                     │                  │               │
     │                │                     │ BEGIN TRANSACTION│               │
     │                │                     │──┐               │               │
     │                │                     │◀─┘               │               │
     │                │                     │                  │               │
     │                │                     │ findAccount(from)│               │
     │                │                     ├─────────────────────────────────▶│
     │                │                     │                  │               │
     │                │                     │◀─────────────────────────────────┤
     │                │                     │                  │               │
     │                │                     │ findAccount(to)  │               │
     │                │                     ├─────────────────────────────────▶│
     │                │                     │                  │               │
     │                │                     │◀─────────────────────────────────┤
     │                │                     │                  │               │
     │                │                     │ account.processDebit(amount)     │
     │                │                     ├─────────────────▶│               │
     │                │                     │                  │               │
     │                │                     │                  │ checkBalance()│
     │                │                     │                  │──┐            │
     │                │                     │                  │◀─┘            │
     │                │                     │                  │               │
     │                │                     │                  │ debit()       │
     │                │                     │                  │──┐            │
     │                │                     │                  │◀─┘            │
     │                │                     │                  │               │
     │                │                     │◀─────────────────┤               │
     │                │                     │                  │               │
     │                │                     │ account.processCredit(amount)    │
     │                │                     ├─────────────────▶│               │
     │                │                     │                  │               │
     │                │                     │                  │ credit()      │
     │                │                     │                  │──┐            │
     │                │                     │                  │◀─┘            │
     │                │                     │                  │               │
     │                │                     │◀─────────────────┤               │
     │                │                     │                  │               │
     │                │                     │ save(accounts)   │               │
     │                │                     ├─────────────────────────────────▶│
     │                │                     │                  │               │
     │                │                     │ COMMIT TRANSACTION               │
     │                │                     │──┐               │               │
     │                │                     │◀─┘               │               │
     │                │                     │                  │               │
     │                │                     │ publishEvent(TransactionProcessed)│
     │                │                     │──┐               │               │
     │                │                     │◀─┘               │               │
     │                │                     │                  │               │
     │                │◀────────────────────┤                  │               │
     │                │                     │                  │               │
     │◀───────────────┤                     │                  │               │
     │   200 OK       │                     │                  │               │
     │ Transfer Result│                     │                  │               │
```

### Account Opening Flow

```ascii
┌─────────┐    ┌──────────────┐    ┌─────────────────┐    ┌──────────┐
│ Client  │    │ Controller   │    │ Banking         │    │ Domain   │
│         │    │              │    │ Service         │    │ Model    │
└────┬────┘    └──────┬───────┘    └────────┬────────┘    └────┬─────┘
     │                │                     │                  │
     │ POST /accounts │                     │                  │
     ├───────────────▶│                     │                  │
     │                │                     │                  │
     │                │ openAccount()       │                  │
     │                ├────────────────────▶│                  │
     │                │                     │                  │
     │                │                     │ validateCustomer()│
     │                │                     │──┐               │
     │                │                     │◀─┘               │
     │                │                     │                  │
     │                │                     │ BankAccount.openAccount()
     │                │                     ├─────────────────▶│
     │                │                     │                  │
     │                │                     │                  │ generateAccountNumber()
     │                │                     │                  │──┐
     │                │                     │                  │◀─┘
     │                │                     │                  │
     │                │                     │                  │ validateBusinessRules()
     │                │                     │                  │──┐
     │                │                     │                  │◀─┘
     │                │                     │                  │
     │                │                     │◀─────────────────┤
     │                │                     │                  │
     │                │◀────────────────────┤                  │
     │                │                     │                  │
     │◀───────────────┤                     │                  │
     │   201 Created  │                     │                  │
     │ Account Details│                     │                  │
```

## 🔌 API Documentation

### Customer Management Endpoints

#### 1. Register New Customer
```http
POST /api/customers
Content-Type: application/json

{
    "firstName": "John",
    "lastName": "Smith",
    "email": "john.smith@email.com",
    "phoneNumber": "+447911123456",
    "dateOfBirth": "1985-03-15",
    "addressLine": "123 High Street, London",
    "postcode": "SW1A 1AA",
    "nationalInsuranceNumber": "AB123456C"
}
```

**Response (201 Created):**
```json
{
    "customerId": 1,
    "personalName": {
        "firstName": "John",
        "lastName": "Smith"
    },
    "email": "john.smith@email.com",
    "phoneNumber": "+447911123456",
    "dateOfBirth": "1985-03-15",
    "address": {
        "addressLine": "123 High Street, London",
        "postcode": "SW1A 1AA"
    },
    "nationalInsuranceNumber": "AB123456C",
    "status": "ACTIVE",
    "registeredAt": "2025-09-10T10:30:00Z"
}
```

#### 2. Get Customer Details
```http
GET /api/customers/{customerId}
```

**Response (200 OK):**
```json
{
    "customerId": 1,
    "personalName": {
        "firstName": "John",
        "lastName": "Smith"
    },
    "email": "john.smith@email.com",
    "status": "ACTIVE",
    "totalAccounts": 2,
    "registeredAt": "2025-09-10T10:30:00Z"
}
```

### Account Management Endpoints

#### 3. Open New Account
```http
POST /api/accounts
Content-Type: application/json

{
    "customerId": 1,
    "accountType": "CURRENT",
    "initialDeposit": 1000.00,
    "overdraftLimit": 500.00
}
```

**Response (201 Created):**
```json
{
    "accountId": 1,
    "identifier": {
        "sortCode": "400001",
        "accountNumber": "12345678"
    },
    "accountType": "CURRENT",
    "balance": {
        "amount": 1000.00,
        "currency": "GBP"
    },
    "overdraftLimit": {
        "amount": 500.00,
        "currency": "GBP"
    },
    "status": "ACTIVE",
    "openedAt": "2025-09-10T11:00:00Z"
}
```

#### 4. Transfer Money
```http
POST /api/transfers
Content-Type: application/json

{
    "fromAccountId": 1,
    "toSortCode": "400001",
    "toAccountNumber": "87654321",
    "amount": {
        "amount": 250.00,
        "currency": "GBP"
    },
    "reference": "Monthly Rent",
    "payeeName": "Property Manager"
}
```

**Response (200 OK):**
```json
{
    "transactionId": "TXN-20250910-001",
    "status": "COMPLETED",
    "fromAccount": {
        "sortCode": "400001",
        "accountNumber": "12345678"
    },
    "toAccount": {
        "sortCode": "400001",
        "accountNumber": "87654321"
    },
    "amount": {
        "amount": 250.00,
        "currency": "GBP"
    },
    "reference": "Monthly Rent",
    "processedAt": "2025-09-10T14:30:00Z",
    "newBalance": {
        "amount": 750.00,
        "currency": "GBP"
    }
}
```

### Error Response Format
```json
{
    "timestamp": "2025-09-10T14:30:00Z",
    "status": 400,
    "error": "Bad Request",
    "message": "Invalid UK postcode format",
    "path": "/api/customers",
    "errorCode": "VALIDATION_ERROR",
    "details": [
        {
            "field": "postcode",
            "rejectedValue": "INVALID",
            "message": "Postcode must follow UK format (e.g., SW1A 1AA)"
        }
    ]
}
```

## 🗃️ Database Design

### Entity Relationship Diagram (ERD)

```ascii
┌─────────────────────────────────────────────────────────────────────┐
│                           DATABASE SCHEMA                          │
└─────────────────────────────────────────────────────────────────────┘

┌─────────────────┐    1    ∞ ┌─────────────────┐    1    ∞ ┌──────────────┐
│    CUSTOMERS    │───────────│  BANK_ACCOUNTS  │───────────│ TRANSACTIONS │
├─────────────────┤           ├─────────────────┤           ├──────────────┤
│ customer_id (PK)│           │ account_id (PK) │           │transaction_id│
│ first_name      │           │ customer_id (FK)│           │ account_id   │
│ last_name       │           │ sort_code       │           │ amount       │
│ email (UNIQUE)  │           │ account_number  │           │ currency     │
│ phone_number    │           │ account_type    │           │ type         │
│ date_of_birth   │           │ balance         │           │ description  │
│ address_line    │           │ overdraft_limit │           │ reference    │
│ postcode        │           │ interest_rate   │           │ processed_at │
│ ni_number       │           │ status          │           │ balance_after│
│ status          │           │ opened_at       │           └──────────────┘
│ registered_at   │           │ closed_at       │
│ last_updated_at │           └─────────────────┘
└─────────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│                         TABLE INDEXES                              │
├─────────────────────────────────────────────────────────────────────┤
│ CUSTOMERS:                                                          │
│ • PRIMARY KEY (customer_id)                                         │
│ • UNIQUE INDEX (email)                                              │
│ • UNIQUE INDEX (ni_number)                                          │
│ • INDEX (status)                                                    │
│                                                                     │
│ BANK_ACCOUNTS:                                                      │
│ • PRIMARY KEY (account_id)                                          │
│ • FOREIGN KEY (customer_id) → CUSTOMERS(customer_id)                │
│ • UNIQUE INDEX (sort_code, account_number)                          │
│ • INDEX (customer_id)                                               │
│ • INDEX (status)                                                    │
│                                                                     │
│ TRANSACTIONS:                                                       │
│ • PRIMARY KEY (transaction_id)                                      │
│ • FOREIGN KEY (account_id) → BANK_ACCOUNTS(account_id)              │
│ • INDEX (account_id)                                                │
│ • INDEX (processed_at)                                              │
│ • INDEX (reference)                                                 │
└─────────────────────────────────────────────────────────────────────┘
```

### Database Constraints

```ascii
┌─────────────────────────────────────────────────────────────────────┐
│                        BUSINESS CONSTRAINTS                         │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│ CUSTOMERS TABLE:                                                    │
│ ├─ email: NOT NULL, UNIQUE, EMAIL FORMAT                            │
│ ├─ phone_number: UK PHONE FORMAT (+44xxxxxxxxxx)                    │
│ ├─ postcode: UK POSTCODE FORMAT (XX# #XX, X## #XX)                  │
│ ├─ ni_number: UK NI FORMAT (XX######X)                              │
│ ├─ date_of_birth: MINIMUM AGE 18 YEARS                              │
│ └─ status: ENUM('ACTIVE', 'SUSPENDED', 'CLOSED')                    │
│                                                                     │
│ BANK_ACCOUNTS TABLE:                                                │
│ ├─ sort_code: 6 DIGITS (DEFAULT: 400001)                            │
│ ├─ account_number: 8 DIGITS, UNIQUE PER SORT_CODE                   │
│ ├─ account_type: ENUM('CURRENT', 'SAVINGS', 'ISA', 'BUSINESS')      │
│ ├─ balance: DECIMAL(15,2), CHECK (balance >= -overdraft_limit)      │
│ ├─ overdraft_limit: DECIMAL(10,2), CHECK (overdraft_limit >= 0)     │
│ └─ status: ENUM('ACTIVE', 'FROZEN', 'CLOSED')                       │
│                                                                     │
│ TRANSACTIONS TABLE:                                                 │
│ ├─ amount: DECIMAL(15,2), CHECK (amount > 0)                        │
│ ├─ type: ENUM('DEBIT', 'CREDIT', 'TRANSFER_IN', 'TRANSFER_OUT')     │
│ ├─ reference: VARCHAR(50), NOT NULL                                 │
│ └─ processed_at: TIMESTAMP, DEFAULT CURRENT_TIMESTAMP               │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

## 🚀 Getting Started

### Prerequisites
```bash
# System Requirements
Java 8+ (OpenJDK or Oracle JDK)
Maven 3.6+
Git 2.20+

# Optional (for development)
IDE: IntelliJ IDEA, Eclipse, or VS Code
Database Client: DBeaver, pgAdmin
API Client: Postman, Insomnia
```

### Quick Start Guide

#### 1. Clone Repository
```bash
git clone https://github.com/your-org/uk-banking-system.git
cd uk-banking-system
```

#### 2. Build Project
```bash
# Clean build with tests
mvn clean install

# Skip tests for faster build
mvn clean install -DskipTests

# Build without running integration tests
mvn clean install -DskipITs
```

#### 3. Run Application
```bash
# Using Maven
mvn spring-boot:run

# Using Java directly
java -jar target/uk-banking-system-1.0.0.jar

# With custom profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

#### 4. Verify Installation
```bash
# Health check
curl http://localhost:8080/actuator/health

# Expected response:
# {"status":"UP"}
```

### Configuration

#### Application Properties (application.yml)
```yaml
server:
  port: 8080
  servlet:
    context-path: /api

spring:
  application:
    name: uk-banking-system
    
  datasource:
    url: jdbc:h2:mem:ukbank
    driver-class-name: org.h2.Driver
    username: sa
    password: 
    
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop
    show-sql: false
    properties:
      hibernate:
        format_sql: true
        
  h2:
    console:
      enabled: true
      path: /h2-console
      
  jackson:
    default-property-inclusion: NON_NULL
    serialization:
      write-dates-as-timestamps: false

logging:
  level:
    br.com.ukbank: DEBUG
    org.springframework.web: INFO
    org.hibernate.SQL: DEBUG
    
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always
  metrics:
    export:
      prometheus:
        enabled: true
```

## 🧪 Testing Strategy

### Testing Pyramid

```ascii
┌─────────────────────────────────────────────────────────────────────┐
│                         TESTING PYRAMID                            │
└─────────────────────────────────────────────────────────────────────┘

                           ┌─────────────┐
                           │    E2E      │ ← Full system tests
                           │   Tests     │   Contract tests
                           └─────────────┘
                       ┌─────────────────────┐
                       │   Integration       │ ← API tests
                       │     Tests           │   Database tests
                       │                     │   External service tests
                       └─────────────────────┘
               ┌─────────────────────────────────────┐
               │            Unit Tests               │ ← Domain logic tests
               │                                     │   Service layer tests
               │     (Majority of test coverage)     │   Validation tests
               └─────────────────────────────────────┘
```

### Test Categories

#### Unit Tests
```java
// Domain Model Tests
@Test
void shouldCreateValidCustomer() {
    // Given
    String firstName = "John";
    String lastName = "Smith";
    String email = "john.smith@email.com";
    
    // When
    Customer customer = Customer.registerNewCustomer(
        firstName, lastName, email, 
        "+447911123456", LocalDate.of(1985, 3, 15),
        "123 High Street", "SW1A 1AA", "AB123456C"
    );
    
    // Then
    assertThat(customer.getPersonalName().getFirstName()).isEqualTo(firstName);
    assertThat(customer.getStatus()).isEqualTo(Customer.CustomerStatus.ACTIVE);
}

// Business Logic Tests
@Test
void shouldProcessTransferSuccessfully() {
    // Given
    BankAccount fromAccount = createAccountWithBalance(1000.00);
    BankAccount toAccount = createAccount();
    Money transferAmount = Money.of(250.00);
    
    // When
    TransactionResult result = bankingService.transferMoney(
        fromAccount.getAccountId(), 
        toAccount.getIdentifier(), 
        transferAmount, 
        "Test Transfer"
    );
    
    // Then
    assertThat(result.isSuccess()).isTrue();
    assertThat(fromAccount.getBalance().getAmount()).isEqualTo(750.00);
}
```

#### Integration Tests
```java
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerIntegrationTest {
    
    @Test
    @Transactional
    void shouldRegisterCustomerEndToEnd() {
        // Given
        CustomerRegistrationRequest request = createValidRegistrationRequest();
        
        // When
        ResponseEntity<CustomerResponse> response = restTemplate.postForEntity(
            "/api/customers", 
            request, 
            CustomerResponse.class
        );
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getCustomerId()).isNotNull();
    }
}
```

### Running Tests
```bash
# Run all tests
mvn test

# Run specific test category
mvn test -Dtest="**/*UnitTest"
mvn test -Dtest="**/*IntegrationTest"

# Generate test report
mvn surefire-report:report

# Run tests with coverage
mvn test jacoco:report
```

## 📦 Deployment Guide

### Docker Deployment

#### Dockerfile
```dockerfile
FROM openjdk:8-jre-alpine

LABEL maintainer="UK Banking System Team"
LABEL version="1.0.0"

VOLUME /tmp

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
```

#### Docker Compose
```yaml
version: '3.8'

services:
  uk-banking-api:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/ukbank
    depends_on:
      - db
    networks:
      - banking-network

  db:
    image: postgres:13-alpine
    environment:
      POSTGRES_DB: ukbank
      POSTGRES_USER: bankuser
      POSTGRES_PASSWORD: bankpass
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - banking-network

volumes:
  postgres_data:

networks:
  banking-network:
    driver: bridge
```

### Production Deployment

#### Build Commands
```bash
# Create production build
mvn clean package -Pprod

# Build Docker image
docker build -t uk-banking-system:latest .

# Run with Docker Compose
docker-compose up -d

# Scale services
docker-compose up --scale uk-banking-api=3 -d
```

#### Environment Configuration
```bash
# Production environment variables
export SPRING_PROFILES_ACTIVE=prod
export SPRING_DATASOURCE_URL=jdbc:postgresql://prod-db:5432/ukbank
export SPRING_DATASOURCE_USERNAME=${DB_USER}
export SPRING_DATASOURCE_PASSWORD=${DB_PASS}
export JAVA_OPTS="-Xmx1024m -Xms512m"
```

## 📊 Monitoring and Observability

### Metrics and Health Checks
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: always
  metrics:
    export:
      prometheus:
        enabled: true
```

### Custom Health Indicators
```java
@Component
public class DatabaseHealthIndicator implements HealthIndicator {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Override
    public Health health() {
        try {
            customerRepository.count();
            return Health.up()
                .withDetail("database", "Available")
                .build();
        } catch (Exception e) {
            return Health.down()
                .withDetail("database", "Unavailable")
                .withException(e)
                .build();
        }
    }
}
```

## 🔐 Security Considerations

### Data Protection
- **Encryption**: All sensitive data encrypted at rest and in transit
- **PCI DSS**: Payment card industry compliance
- **GDPR**: Data protection regulation compliance
- **Audit Trail**: Complete transaction and access logging

### API Security
- **Authentication**: JWT token-based authentication
- **Authorization**: Role-based access control (RBAC)
- **Rate Limiting**: API request throttling
- **Input Validation**: Comprehensive input sanitization

## 📚 Additional Resources

### UK Banking Regulations
- [Financial Conduct Authority (FCA) Guidelines](https://www.fca.org.uk/)
- [Bank of England Prudential Regulation](https://www.bankofengland.co.uk/prudential-regulation)
- [UK Finance Standards](https://www.ukfinance.org.uk/)

### Technical Documentation
- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Domain-Driven Design](https://domainlanguage.com/ddd/)
- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)

## 📋 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Contributing

### Development Guidelines
1. Follow Domain-Driven Design principles
2. Maintain high test coverage (>80%)
3. Adhere to UK banking regulations
4. Use conventional commit messages
5. Update documentation for new features

### Code Standards
- Java 8+ features and best practices
- Spring Boot conventions
- Clean code principles
- SOLID design principles

---

**UK Banking System** - Developed with ❤️ for secure and compliant banking operations.
