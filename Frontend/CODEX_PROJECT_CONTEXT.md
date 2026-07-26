# Codex Project Context

This note is for future Codex sessions. Read it before changing frontend or backend code.

## Workspace

- Frontend: `C:\Users\Dell\Downloads\custom\New folder\FinalProject-Vue`
- Backend: `C:\Users\Dell\Downloads\custom\New folder\FinalProject-Inteliji`
- Frontend stack: Vue 3, Vite, Pinia, Vue Router, Vuetify, Tailwind CSS.
- Backend stack: Spring Boot, Java 17, Maven, SQL Server, Spring Data JPA, Spring Security.
- Frontend dev server: Vite on port `3002`.
- Backend API: `http://localhost:8080/api`.
- Vite proxy maps `/api` and `/images` to `http://localhost:8080`.

## Security And Secrets

- Backend `application.properties` contains real SQL Server, Cloudinary, Google, and SMTP credentials. Do not copy credential values into chat, commits, or docs.
- Backend security currently permits all API requests. Admin protection is mostly frontend route guarding.
- Backend CORS allows `http://localhost:3002` and `http://127.0.0.1:3002`.

## Current Database Constraints To Respect

User reported the active SQL constraints on 2026-05-22:

```sql
ALTER TABLE Roles ADD CONSTRAINT CK_Roles_Name
CHECK (name IN ('GUEST', 'USER', 'ADMIN', 'STAFF'));

ALTER TABLE Orders ADD CONSTRAINT CK_Orders_Status
CHECK (status IN ('PENDING', 'PROCESSING', 'SHIPPING', 'SUCCESS', 'CANCELLED', 'RETURN_REQUEST', 'RETURNING', 'RETURNED'));
```

Do not introduce older order statuses such as `NEW` or `CONFIRMED` into backend payloads. Frontend normalizers may still accept those as legacy aliases, but outgoing order status values must be backend-valid.

## Important Backend Contracts

Active backend package: `com.finalproject2`.

Main controllers:

- `AccountController`: `/api/accounts`, login, Google login, forgot password, avatar upload.
- `ProductController`: `/api/products`, product CRUD, product image upload.
- `ProductVariantController`: `/api/variants`, variant CRUD, primary and extra image upload.
- `CartController`: `/api/cart`, `/init`, `/add`, `/update/{cartItemId}`, `/remove/{cartItemId}`, `/clear`.
- `OrderController`: `/api/orders`, `/api/orders/{id}`, `/api/orders/account/{accountId}`.
- `VoucherController`: `/api/vouchers`, `/apply`, `/validate`, `/use`, `/status/active`.
- `PaymentController`: VNPay endpoints.

Order details:

- `POST /api/orders` accepts a flexible map payload.
- Required for order creation: `accountId`, `paymentMethodId`, and item list under `items`, `cartItems`, `cart`, or `orderItems`.
- Order creation sets `status = PENDING` and `paymentStatus = PENDING`.
- `PUT /api/orders/{id}` accepts `status`, `paymentStatus`, and `returnReason`.
- Backend restocks items when status changes to `CANCELLED` or `RETURNED`.
- `mapOrderToMap()` returns `paymentMethodName`, but currently does not return `paymentMethodId`; frontend must derive the method ID from name when needed.
- `mapOrderToMap()` returns both `items` and `orderItems`.

Voucher details:

- `GET /api/vouchers` returns an object with `success`, `data`, paging fields.
- Voucher status is derived: `ACTIVE`, `SCHEDULED`, `EXPIRED`, `USED`, `INACTIVE`.
- `GET /api/vouchers/apply` and `/validate` preview discount.
- `POST /api/vouchers/use` increments `usedCount`.
- `POST /api/orders` also increments voucher usage when a voucher is attached; avoid double-counting in frontend flows.

Cart details:

- Active cart API is `/api/cart`, not older `/api/carts`.
- `GET /api/cart?accountId=...` returns `items`, `totalPrice`, `itemCount`, `cartId`.
- Cart item shape includes `cartItemId`, `variantId`, `productId`, `productName`, `productCode`, `price`, `quantity`, `stockAvailable`, `size`, `color`, and `image`.

## Frontend Files That Matter Most

- `src/services/api.js`: main API facade, admin helpers, record normalization, soft-delete fallbacks.
- `src/services/orders.js`: cart, checkout, order history, voucher, payment helpers.
- `src/router/index.js`: route definitions and frontend role guards.
- `src/components/admin/AdminShell.vue`: admin layout and nav.
- Admin pages:
  - `AdminOrdersView.vue`
  - `AdminPOSView.vue`
  - `AdminProductsView.vue`
  - `AdminInventoryView.vue`
  - `AdminCustomersView.vue`
  - `AdminVouchersView.vue`

## Frontend Normalization Rules

Preserve tolerance for camelCase and snake_case because backend responses are mixed:

- `accountId` / `account_id`
- `productId` / `product_id`
- `variantId` / `variant_id`
- `imageUrl` / `image_url`
- `stockQuantity` / `stock_quantity`
- `paymentStatus` / `payment_status`
- `paymentMethodId` / `payment_method_id`
- `paymentMethodName` / `payment_method_name`
- `finalAmount` / `final_amount`
- `totalMoney` / `total_money`

Order status normalization:

- Legacy `NEW`, `WAITING`, `CHO_XU_LY`, `CHUA_XU_LY` should display as `PENDING`.
- Legacy `CONFIRMED`, `DANG_XU_LY`, `XAC_NHAN`, `IN_PROGRESS` should display as `PROCESSING`.
- Backend-valid statuses are `PENDING`, `PROCESSING`, `SHIPPING`, `SUCCESS`, `CANCELLED`, `RETURN_REQUEST`, `RETURNING`, `RETURNED`.

Payment method handling:

- Backend order list currently gives `paymentMethodName` but not `paymentMethodId`.
- Admin order edit form must not rely on a disabled select matching a missing numeric value.
- Use `getPaymentMethodLabel()` and derive ID by known names when ID is absent.

## Known Fragile Areas

- Many Vue files have mojibake Vietnamese text. Avoid broad automated text rewrites unless explicitly fixing encoding.
- Backend controllers use `Map<String,Object>` payloads in important places. Validate frontend payload names carefully.
- Backend `Order.status` entity annotation still has `@ColumnDefault("NEW")`, while the SQL constraint now rejects `NEW`. Do not rely on the entity default.
- Frontend admin delete operations are often soft-delete or status-change fallbacks because database FKs can block hard deletes.
- POS page is sensitive to Vue template ref unwrapping. In templates, use `invoices.length`, not `invoices.value.length`.
- The project is dark-themed in admin screens. Avoid adding white hover/background states to admin tables or sticky cells.

## Verification Commands

Frontend:

```powershell
cd "C:\Users\Dell\Downloads\custom\New folder\FinalProject-Vue"
npm run build
```

Backend:

```powershell
cd "C:\Users\Dell\Downloads\custom\New folder\FinalProject-Inteliji"
.\mvnw.cmd test
```

## Change Log

2026-05-22:

- Updated frontend order status normalization to use backend-valid `PENDING` and `PROCESSING`.
- Admin orders:
  - Replaced status filter pills with a dropdown.
  - Made action column sticky on the right and ID sticky on the left.
  - Adjusted column widths so status/date values do not wrap badly.
  - Changed the payment method edit field to a read-only label derived from normalized method data.
  - Styled horizontal scrollbar with transparent track and thumb-only dark-theme behavior.
  - Removed the white row-hover highlight from the admin order table.
- POS:
  - Fixed template usage of `invoices.value.length`; templates must use `invoices.length`.
  - This fixes a blank POS screen caused by accessing `.value` after Vue template unwrapping.
- Frontend build passed after the fixes.
