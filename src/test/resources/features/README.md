# Cucumber Feature Files

Generated từ toàn bộ test scripts của project.

## Cấu trúc

```
features/
├── user_registration.feature    # TC1, TC5, API-TC01
├── user_login.feature           # TC2, TC3, TC4, TC20, TC21, TC24a/b, API-TC02
├── user_account.feature         # TC24c, API-TC03, API-TC04
├── product_browsing.feature     # TC7, TC8, TC9, TC18, TC19, TC22
├── cart_management.feature      # TC12, TC13, TC17, TC23
├── checkout_and_order.feature   # TC14, TC15, TC16, E2E
├── subscription.feature         # TC10, TC11
└── contact_us.feature           # TC6
```

## Tags

| Tag           | Mô tả                                      |
|---------------|--------------------------------------------|
| `@smoke`      | Critical path, chạy nhanh                  |
| `@regression` | Full regression suite                      |
| `@e2e`        | End-to-end flows                           |
| `@api`        | API-only tests (không cần browser)         |
| `@api-ui`     | Hybrid: API setup + UI verification        |
| `@negative`   | Negative / error scenarios                 |
| `@search`     | Product search & filter                    |
| `@cart`       | Cart management                            |
| `@checkout`   | Checkout & order placement                 |
| `@registration`| User registration                         |
| `@login`      | User login / logout                        |
| `@account`    | Account management                         |
| `@subscription`| Newsletter subscription                   |
| `@contact`    | Contact us form                            |

## Tổng số Scenarios

| Feature file              | Scenario | Scenario Outline | Total rows |
|---------------------------|----------|------------------|------------|
| user_registration         | 3        | 1 (2 rows)       | 5          |
| user_login                | 7        | 2 (2+3 rows)     | 12         |
| user_account              | 4        | 0                | 4          |
| product_browsing          | 4        | 3 (4+3+8 rows)   | 19         |
| cart_management           | 5        | 0                | 5          |
| checkout_and_order        | 1        | 3 (1+1+1 rows)   | 4 + E2E    |
| subscription              | 0        | 2 (3+2 rows)     | 5          |
| contact_us                | 0        | 1 (2 rows)       | 2          |
| **TOTAL**                 | **24**   | **12 outlines**  | **~56**    |
