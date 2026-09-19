# Backend Context For Codex

This note is for future Codex sessions working against the active backend in this folder.

## Active Project

- Backend folder: `C:\Users\Dell\Downloads\custom\New folder\FinalProject-Inteliji`
- Package: `com.finalproject2`
- Stack: Spring Boot, Java 17, Maven, SQL Server, Spring Data JPA, Spring Security, Cloudinary, Google login, SMTP mail.
- Frontend folder: `C:\Users\Dell\Downloads\custom\New folder\FinalProject-Vue`
- Frontend dev URL: `http://localhost:3002`
- Backend API URL: `http://localhost:8080/api`

## Security And Config

- `src/main/resources/application.properties` contains real database and service credentials. Do not paste credential values into chat, commits, or docs.
- JPA uses `ddl-auto=update` and SQL Server database `ShopGiay`.
- Security config currently disables CSRF, uses stateless sessions, permits `/api/accounts/**`, and permits all other requests.
- CORS allows `http://localhost:3002` and `http://127.0.0.1:3002`.

## Current SQL Constraints To Respect

User reported these active constraints on 2026-05-22:

```sql
Roles.name IN ('GUEST', 'USER', 'ADMIN', 'STAFF')
Orders.status IN ('PENDING', 'PROCESSING', 'SHIPPING', 'SUCCESS', 'CANCELLED', 'RETURN_REQUEST', 'RETURNING', 'RETURNED')
```

Do not use old order statuses `NEW` or `CONFIRMED` in outbound API behavior. `Order.status` still has `@ColumnDefault("NEW")`; that is stale against the current SQL constraint and should be fixed when backend edits are requested.

## Main Controllers

- `AccountController`: `/api/accounts`, login, Google login, forgot password, avatar upload.
- `ProductController`: `/api/products`, product CRUD, product image upload.
- `ProductVariantController`: `/api/variants`, variant CRUD, primary/extra image upload.
- `CartController`: `/api/cart`, `/init`, `/add`, `/update/{cartItemId}`, `/remove/{cartItemId}`, `/clear`.
- `OrderController`: `/api/orders`, `/api/orders/{id}`, `/api/orders/account/{accountId}`.
- `VoucherController`: `/api/vouchers`, `/apply`, `/validate`, `/use`, `/status/active`.
- `PaymentController`: VNPay endpoints.

## Order Contract

`POST /api/orders` accepts a map payload.

Required:

- `accountId`
- `paymentMethodId`
- item list under `items`, `cartItems`, `cart`, or `orderItems`

Creation behavior:

- Loads account and payment method.
- Optionally loads voucher by `voucherCode` or `voucherId`.
- Validates stock and decrements variant stock.
- Sets `status = PENDING`.
- Sets `paymentStatus = PENDING`.
- Calculates totals and discount.
- Saves `OrderItems`.
- Increments voucher usage and creates `AccountVoucher` when voucher is attached.
- Clears the account cart.
- Returns `{ success: true, orderId }`.

`PUT /api/orders/{id}`:

- Accepts `status`, `paymentStatus`, `returnReason`.
- Restocks order items when status changes to `CANCELLED` or `RETURNED`.
- Returns `{ success: true }`.

Order response mapping:

- `mapOrderToMap()` returns `paymentMethodName`, but currently does not return `paymentMethodId`.
- It returns both `items` and `orderItems` for frontend compatibility.

## Voucher Contract

- `GET /api/vouchers` returns `{ success, data, currentPage, totalPages, totalElements }`.
- `GET /api/vouchers/apply` and `/validate` preview discount.
- `POST /api/vouchers/use` increments usage.
- `POST /api/orders` also increments voucher usage when a voucher is attached; avoid double-counting from frontend.
- Derived voucher statuses: `ACTIVE`, `SCHEDULED`, `EXPIRED`, `USED`, `INACTIVE`.

## Cart Contract

Active cart API is `/api/cart`:

- `GET /api/cart?accountId=...`
- `POST /api/cart/init`
- `POST /api/cart/add`
- `PUT /api/cart/update/{cartItemId}`
- `DELETE /api/cart/remove/{cartItemId}`
- `DELETE /api/cart/clear?accountId=...`

The frontend should not use older `/carts` or `/cart-items` routes unless the backend is extended.

## Known Backend Issues / Watch Items

- `Order.status` entity default still says `NEW`; this conflicts with the active SQL constraint.
- `OrderController.mapOrderToMap()` should include `paymentMethodId` in a future backend fix to avoid frontend inference.
- Several controllers use `Map<String,Object>` payloads, so frontend field names must be exact and defensive.
- Many source strings show mojibake Vietnamese; avoid broad encoding rewrites unless explicitly fixing UI text.
- Hard deletes may fail due to FKs; frontend often uses soft-delete/status fallback behavior.

## Verification

Run backend tests with:

```powershell
cd "C:\Users\Dell\Downloads\custom\New folder\FinalProject-Inteliji"
.\mvnw.cmd test
```

## Recent Frontend Fixes Depending On Backend Contract

2026-05-22:

- Admin orders now uses backend-valid statuses `PENDING` and `PROCESSING`.
- Admin order payment method display derives from `paymentMethodName` when `paymentMethodId` is absent.
- Admin POS creates orders with `paymentMethodId` and then updates orders to `SUCCESS` / `PAID` for completed in-store purchases.
