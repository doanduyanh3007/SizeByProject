# Project Transfer and Comparison Summary

## Purpose
This file documents the migration of the ShopGiay project from the old original repositories into the new working locations.

## Repository mapping
- Original frontend: `C:\Users\Dell\Downloads\custom\FinalProject-1`
- Original backend: `C:\Users\Dell\Downloads\custom\FinalProject2`
- New backend location: `C:\Users\Dell\Downloads\custom\New folder\FinalProject-Inteliji`
- New frontend location (current workspace): `C:\Users\Dell\Downloads\custom\New folder\FinalProject-Vue`

> The current workspace is the new frontend repo where development continues.

## Current frontend repo status
- Project name: `frontend-shopgiay`
- Frameworks: Vue 3, Vuetify 3, Pinia, Vue Router, Vite
- Important packages:
  - `axios` for HTTP requests
  - `pinia` for state
  - `vue3-google-login` for Google sign-in
  - `vuetify` for UI
  - `tailwindcss` for styling utilities
- Scripts:
  - `npm run dev` — start development server
  - `npm run build` — production build
  - `npm run preview` — preview built app
  - `npm run lint` — lint code with ESLint

## Current frontend structure
- `src/pages/` contains the main app pages:
  - `HomeView.vue`, `ShopView.vue`, `ProductView.vue`, `ProductDetailView.vue`
  - `CartView.vue`, `CheckoutView.vue`, `OrderHistoryView.vue`, `ProfileView.vue`, `ProfileEdit.vue`
  - `LoginView.vue`, `RegisterView.vue`, `ForgotPasswordView.vue`
  - Admin pages under `src/pages/`: `AdminDashboardView.vue`, `AdminProductsView.vue`, `AdminOrdersView.vue`, etc.
- `src/components/` contains reusable UI components and admin shell components.
- `src/services/` contains backend integration code:
  - `api.js` — likely defines `axios` base URL and API helpers
  - `orders.js` — order-related request helpers
- `src/stores/` contains application state modules:
  - `app.js`, `cart.js`, `compare.js`, `favorites.js`, `products.js`, `reviews.js`

## Original backend / database summary
The original SQL schema is for a database named `ShopGiay` and includes the following major entities:

### Core domain tables
- `Accounts`
- `Roles`
- `AccountRoles`
- `AccountVouchers`
- `Brands`
- `Categories`
- `Colors`
- `Sizes`
- `Products`
- `ProductVariants`
- `VariantImages`
- `ProductReviews`
- `Carts`
- `CartItems`
- `Orders`
- `OrderItems`
- `PaymentMethods`
- `Vouchers`
- `ProductCompares`
- `Feedbacks`

### Key relationships and business rules
- `Products` belongs to `Brands` and `Categories`
- `ProductVariants` belongs to `Products`, `Sizes`, and `Colors`
- `CartItems` belong to `Carts` and `ProductVariants`
- `Orders` belong to `Accounts` and `PaymentMethods`, optionally a `Voucher`
- `OrderItems` belong to `Orders` and `ProductVariants`
- `ProductReviews` are unique per `product_id` + `account_id`
- `Vouchers` have `discount_type` (`AMOUNT` or `PERCENT`) and usage limits

### Column notes from the schema
- `Accounts` contains `gmail`, `username`, `password_hash`, `address`, `img_url`, and activation fields
- `Products` includes `name`, `product_code`, `description`, `category_id`, `brand_id`, `status`, `image_url`, `created_at`, `updatedAt`
- `ProductVariants` includes `price`, `stock_quantity`, `image_url`, `status`, `updated_at`
- `Orders` includes `fullname`, `phone`, `shipping_address`, `total_money`, `discount_amount`, `final_amount`, `payment_method_id`, `payment_status`, `status`, and `email`

## Comparison: old frontend vs current new frontend
### Similarities
- Both frontends are Vue-based and use Vuetify styling.
- Both target the same ShopGiay e-commerce domain: products, cart, checkout, account management, and admin pages.
- The current workspace includes more complete page coverage for the same domain entities.
- Both rely on backend API connectivity for auth, products, orders, reviews, vouchers, and cart behavior.

### Differences
- The current repo contains a complete `src/pages/` and `src/stores/` structure that matches the ShopGiay app functionality.
- The README in the current frontend is still generic Vuetify scaffolding and should be replaced with a project-specific README.
- The original backend schema is SQL Server, and the new backend should keep the same table structure for compatibility.
- The old original backend repo location is likely Java/Maven-based (`FinalProject2`), while the new backend location is `FinalProject-Inteliji`.

## What should be transferred into the new project
1. API endpoint contracts from the original backend.
2. Database table mapping for accounts, products, variants, orders, vouchers, reviews, carts.
3. Frontend page flows for login/register, shop browsing, cart checkout, order history, profile management, and admin management.
4. Admin-specific functionality for inventory, orders, reports, customers, vouchers, and settings.
5. State structure in Pinia stores for cart, compare, favorites, products, reviews, and app settings.

## Recommended next steps
- Update the new backend in `FinalProject-Inteliji` to implement the `ShopGiay` schema and expose REST endpoints used by the current frontend.
- Confirm the frontend `src/services/api.js` base URL points to the new backend environment.
- Replace the generic Vuetify README with a ShopGiay-specific README describing how to run the frontend and backend together.
- Keep using the `vue-shopgiay` branch for the frontend and `master` for the backend if that branch layout is already established.
- Verify the database schema in the new backend matches the `ShopGiay` tables and foreign keys from the original SQL script.

## Notes for ongoing development
- Treat `FinalProject-Vue` as the active frontend workspace.
- Treat `FinalProject-Inteliji` as the new backend workspace.
- Use the original database script as the source of truth for table design and constraints.
- If you need a second markdown file in the backend repo, I can create a matching `BACKEND_TRANSFER_SUMMARY.md` there too.
