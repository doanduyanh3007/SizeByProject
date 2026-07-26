# Frontend Codebase Summary

## Paths
- New frontend workspace: C:/Users/Dell/Downloads/custom/New folder/FinalProject-Vue
- Original frontend (reference): C:/Users/Dell/Downloads/custom/FinalProject-1

## Project metadata (from package.json)
- name: `frontend-shopgiay`
- framework: Vue 3 + Vuetify 3
- state: Pinia
- bundler: Vite
- key dependencies: `axios`, `pinia`, `vue-router`, `vuetify`, `vue3-google-login`, `leaflet`

## Key directories and purpose
- `src/pages/` — main SPA pages (shop, product detail, cart, checkout, account, admin pages)
- `src/components/` — shared UI components
- `src/services/api.js` — central axios instance and API helper functions
- `src/stores/` — Pinia stores: `cart`, `products`, `favorites`, `compare`, `app`, `reviews`
- `src/router/index.js` — route mapping and guards
- `src/plugins/` — Vuetify and other plugin initialization (Google Login)

## API usage mapping
- Check `src/services/api.js` to confirm base URL — update to point at the new backend `FinalProject-Inteliji` environment (e.g., `http://localhost:8080/api`).
- In each store or page, search for `api.get`, `api.post`, etc., to map which endpoints are used.

## Files to check when debugging API-related issues
- `src/services/api.js`
- `src/stores/products.js`
- `src/stores/cart.js`
- `src/stores/reviews.js`
- `src/pages/CheckoutView.vue`
- `src/pages/LoginView.vue`

## How this file helps future analysis
- When you open an issue or request a fix, paste the filename and function (e.g., `src/stores/cart.js -> addToCart`) and I can instantly locate the related API call and backend mapping.

## Next recommended actions
1. Confirm `src/services/api.js` base URL and update to the backend if needed.
2. Run the dev server and test major user flows: browse, add-to-cart, checkout, login.
3. Optionally create a small `API_MAP.md` listing exact endpoints and which frontend files call them.

---
Generated: 2026-05-22
