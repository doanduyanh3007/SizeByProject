# API Map For Codex

This is a concise frontend/backend contract map for quick debugging.

## Base URLs

- Frontend API default: `import.meta.env.VITE_API_URL || "http://localhost:8080/api"`
- Vite dev server: `http://localhost:3002`
- Backend: `http://localhost:8080/api`

## Accounts

Backend: `AccountController`, `/api/accounts`

- `POST /accounts` creates account.
- `GET /accounts` lists accounts.
- `GET /accounts/{id}` gets account.
- `PUT /accounts/{id}` updates account.
- `DELETE /accounts/{id}` deletes account.
- `POST /accounts/login`
- `POST /accounts/google`
- `POST /accounts/forgot-password/request`
- `POST /accounts/forgot-password/verify`
- `POST /accounts/forgot-password/reset`
- `POST /accounts/{id}/upload-avatar`

Frontend:

- `src/services/api.js`: `authApi`, `accountsApi`, `adminApi.getAccounts()`, customer admin flows.
- `src/router/index.js`: frontend role guards.

Roles:

- Active SQL constraint: `GUEST`, `USER`, `ADMIN`, `STAFF`.
- Admin routes mostly require `ADMIN`; customers route allows `ADMIN` or `STAFF`.

## Products And Variants

Backend:

- `ProductController`, `/api/products`
- `ProductVariantController`, `/api/variants`

Products:

- `POST /products`
- `GET /products`
- `GET /products/{id}`
- `PUT /products/{id}`
- `DELETE /products/{id}`
- `POST /products/{id}/upload-image`

Variants:

- `POST /variants`
- `GET /variants`
- `GET /variants/{id}`
- `PUT /variants/{id}`
- `DELETE /variants/{id}`
- `POST /variants/{id}/upload-image`
- `POST /variants/{id}/add-extra-image`
- `DELETE /variants/{id}/extra-images/{index}`
- `PUT /variants/{id}/extra-images`

Frontend:

- `src/services/api.js`: `productsApi`, `variantsApi`, `adminApi`.
- `src/stores/products.js`: product store and normalization.
- `src/pages/AdminProductsView.vue`, `AdminInventoryView.vue`, `ShopView.vue`, `ProductDetailView.vue`.

## Cart

Backend: `CartController`, `/api/cart`

- `POST /cart/init`
- `GET /cart?accountId=...`
- `POST /cart/add`
- `PUT /cart/update/{cartItemId}`
- `DELETE /cart/remove/{cartItemId}`
- `DELETE /cart/clear?accountId=...`

Frontend:

- Active cart service is `src/services/orders.js` via `cartAPI`.
- `src/stores/cart.js` uses the newer `/cart` endpoints.
- Ignore older `cartApi` paths in `src/services/api.js` unless deliberately refactoring.

## Orders

Backend: `OrderController`, `/api/orders`

- `POST /orders`
- `PUT /orders/{id}`
- `GET /orders/{id}`
- `GET /orders?page=0&size=10`
- `GET /orders/account/{accountId}`

Create order payload:

- Required: `accountId`, `paymentMethodId`.
- Items accepted under `items`, `cartItems`, `cart`, or `orderItems`.
- Item fields: `variantId`, `quantity`; backend uses current variant price.
- Optional voucher fields: `voucherCode` or `voucherId`.

Order response:

- Includes `id`, `status`, `totalMoney`, `discountAmount`, `finalAmount`, `fullname`, `phone`, `shippingAddress`, `paymentStatus`, `returnReason`, `createdAt`, `accountId`, `paymentMethodName`, `items`, `orderItems`.
- Currently does not include `paymentMethodId`.

Status:

- Active SQL constraint: `PENDING`, `PROCESSING`, `SHIPPING`, `SUCCESS`, `CANCELLED`, `RETURN_REQUEST`, `RETURNING`, `RETURNED`.
- Backend creation sets `PENDING`.
- Backend update accepts raw `status`; frontend must only send backend-valid values.
- Backend restocks when status changes to `CANCELLED` or `RETURNED`.

Frontend:

- `src/services/api.js`: `ordersApi`, `adminApi.getOrders()`, `adminApi.updateOrder()`.
- `src/services/orders.js`: customer order history and checkout helpers.
- `src/pages/AdminOrdersView.vue`: admin order management.
- `src/pages/AdminPOSView.vue`: POS creates order then marks it `SUCCESS` / `PAID`.
- `src/pages/OrderHistoryView.vue`: customer order list/detail.

## Vouchers

Backend: `VoucherController`, `/api/vouchers`

- `GET /vouchers?page=0&size=10&sortBy=createdAt&direction=DESC`
- `GET /vouchers/{voucherId}`
- `POST /vouchers`
- `PUT /vouchers/{voucherId}`
- `DELETE /vouchers/{voucherId}`
- `GET /vouchers/apply?code=...&orderTotal=...`
- `GET /vouchers/validate?code=...&orderTotal=...`
- `POST /vouchers/use?code=...&orderTotal=...`
- `GET /vouchers/status/active`

Frontend:

- `src/services/orders.js`: customer voucher API.
- `src/services/api.js`: admin voucher API.
- `src/pages/AdminVouchersView.vue`, `VoucherView.vue`, `CheckoutView.vue`, `AdminPOSView.vue`.

Important:

- `POST /orders` increments voucher usage if voucher is attached.
- Avoid calling `/vouchers/use` after order creation unless checking that it will not double-count.

## Payments

Backend: `PaymentController`

- `POST /payment/vnpay/create`
- `GET /payment/vnpay/ipn`
- `GET /payment/vnpay/return`

Frontend:

- `src/services/api.js`: `paymentApi`.
- `src/pages/VnpayReturnView.vue`
- `src/pages/CheckoutView.vue`

## Recent Fix Log

2026-05-22:

- Admin orders filter is a dropdown, not pipeline pills.
- Admin orders table has dark-only hover behavior and transparent scrollbar track.
- POS blank view fixed by removing `.value` from template access to `invoices`.
- Frontend order status normalization now sends backend-valid status values.
