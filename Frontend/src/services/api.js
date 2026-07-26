/**
 * API Service - Handles all backend API calls
 * Base URL should be configured based on your backend server address
 */

const API_BASE_URL =
    import.meta.env.VITE_API_URL || "http://localhost:8080/api";

function isAbsoluteUrl(url) {
    return /^https?:\/\//i.test(url);
}

function getBackendOrigin() {
    if (!isAbsoluteUrl(API_BASE_URL)) {
        return "";
    }
    try {
        return new URL(API_BASE_URL).origin;
    } catch {
        return "";
    }
}

/**
 * Resolve a backend-served asset path (e.g. `/images/foo.jpg`) into a URL that works
 * both when using a dev proxy (relative paths) and when using an absolute `VITE_API_URL`.
 */
export function resolveBackendAssetUrl(assetPath) {
    if (!assetPath) {
        return assetPath;
    }
    if (isAbsoluteUrl(assetPath) || assetPath.startsWith("data:")) {
        return assetPath;
    }

    const normalizedPath = assetPath.startsWith("/") ?
        assetPath :
        `/${assetPath}`;
    const origin = getBackendOrigin();

    // If API base URL is relative (e.g. `/api`), rely on Vite proxy / same-origin.
    if (!origin) {
        return normalizedPath;
    }

    return `${origin}${normalizedPath}`;
}

/**
 * Normalize image URLs coming from backend data.
 * - Supports backend values like `/images/testshoes` (adds `.jpg`)
 * - Supports absolute URLs and data URLs
 */
export function resolveImageUrl(imageUrl) {
    if (!imageUrl) {
        return imageUrl;
    }

    // Ensure `/images/testshoes` becomes `/images/testshoes.jpg`
    // but leave `/images/testshoes.png` or any URL with an extension as-is.
    if (typeof imageUrl === "string" && imageUrl.startsWith("/images/")) {
        const lastSegment = imageUrl.split("/").pop() || "";
        const hasExtension = /\.[a-z0-9]+$/i.test(lastSegment);
        if (!hasExtension) {
            return resolveBackendAssetUrl(`${imageUrl}.jpg`);
        }
    }

    return resolveBackendAssetUrl(imageUrl);
}

// Helper function for API calls
async function apiCall(endpoint, options = {}) {
    try {
        const url = `${API_BASE_URL}${endpoint}`;
        console.log(`Making ${options.method || "GET"} request to: ${url}`);
        if (options.body) {
            try {
                console.log("Request body:", JSON.parse(options.body));
            } catch {
                console.log("Request body:", options.body);
            }
        }

        const response = await fetch(url, {
            headers: {
                "Content-Type": "application/json",
                ...options.headers,
            },
            ...options,
        });

        let data = null;
        const contentType = response.headers.get("content-type") || "";
        const canHaveBody = response.status !== 204 && response.status !== 205;

        // 204/205 responses have no body (common for DELETE).
        if (canHaveBody) {
            const rawBody = await response.text();
            if (rawBody) {
                const isJson = contentType.indexOf("application/json") !== -1;
                if (isJson) {
                    try {
                        data = JSON.parse(rawBody);
                    } catch {
                        data = { raw: rawBody };
                    }
                } else {
                    data = { raw: rawBody };
                }
            }
        }

        console.log("Response status:", response.status);
        console.log("Response data:", data);

        if (!response.ok) {
            const errorMessage =
                data && typeof data === "object" && data.message ?
                data.message :
                `API Error: ${response.status} ${response.statusText}`;

            const error = {
                status: response.status,
                message: errorMessage,
                data,
            };

            if (data && typeof data === "object" && data.errors) {
                error.errors = data.errors;
                console.error("Validation errors:", data.errors);
            }

            throw error;
        }

        return data;
    } catch (error) {
        console.error("API Call Error:", error);
        throw error;
    }
}

// Auth API
export const authApi = {
    async register(userData) {
        const requestData = {
            username: userData.username,
            gmail: userData.gmail,
            phone: userData.phone,
            address: userData.address || "",
            password: userData.password,
            isActive: true,
        };

        console.log("Sending to backend:", requestData);

        return apiCall("/accounts", {
            method: "POST",
            body: JSON.stringify(requestData),
        });
    },

    async login(credentials) {
        return apiCall("/accounts/login", {
            method: "POST",
            body: JSON.stringify({
                gmail: credentials.gmail,
                password: credentials.password,
            }),
        });
    },

    async requestPasswordResetCode(payload) {
        return apiCall("/accounts/forgot-password/request", {
            method: "POST",
            body: JSON.stringify({
                gmail: payload.gmail,
            }),
        });
    },

    async verifyPasswordResetCode(payload) {
        return apiCall("/accounts/forgot-password/verify", {
            method: "POST",
            body: JSON.stringify({
                gmail: payload.gmail,
                code: payload.code,
            }),
        });
    },

    async resetPassword(payload) {
        return apiCall("/accounts/forgot-password/reset", {
            method: "POST",
            body: JSON.stringify({
                gmail: payload.gmail,
                code: payload.code,
                newPassword: payload.newPassword,
            }),
        });
    },

    // ĐÂY CHÍNH LÀ HÀM LOGIN GOOGLE CHUẨN DÙNG apiCall CỦA BẠN:
    async loginWithGoogle(payload) {
        return apiCall("/accounts/google", {
            method: "POST",
            body: JSON.stringify({
                token: payload.token,
            }),
        });
    },
};

export const accountsApi = {
    async getAll() {
        return apiCall("/accounts");
    },
    async getById(id) {
        return apiCall(`/accounts/${id}`);
    },
    async update(id, accountData) {
        return apiCall(`/accounts/${id}`, {
            method: "PUT",
            body: JSON.stringify(accountData),
        });
    },
};

export const reviewsApi = {
    async getAll(page = 0, size = 100) {
        return apiCall(`/reviews?page=${page}&size=${size}`);
    },
    async getByProductId(productId, page = 0, size = 100) {
        return apiCall(`/reviews?productId=${productId}&page=${page}&size=${size}`);
    },
    async getByAccountId(accountId, page = 0, size = 100) {
        return apiCall(`/reviews?accountId=${accountId}&page=${page}&size=${size}`);
    },
    async create(reviewData) {
        return apiCall("/reviews", {
            method: "POST",
            body: JSON.stringify(reviewData),
        });
    },
    async update(reviewId, reviewData) {
        return apiCall(`/reviews/${reviewId}`, {
            method: "PUT",
            body: JSON.stringify(reviewData),
        });
    },
    async delete(reviewId) {
        return apiCall(`/reviews/${reviewId}`, {
            method: "DELETE",
        });
    },
};

export const brandsApi = {
    async getAll() {
        return apiCall("/brands");
    },
    async getById(id) {
        return apiCall(`/brands/${id}`);
    },
    // THÊM CÁC METHOD SAU:
    async create(data) {
        return apiCall("/brands", {
            method: "POST",
            body: JSON.stringify(data),
        });
    },
    async update(id, data) {
        return apiCall(`/brands/${id}`, {
            method: "PUT",
            body: JSON.stringify(data),
        });
    },
    async delete(id) {
        return apiCall(`/brands/${id}`, {
            method: "DELETE",
        });
    },
};

export const categoriesApi = {
    async getAll() {
        return apiCall("/categories");
    },
    async getById(id) {
        return apiCall(`/categories/${id}`);
    },
};

export const productsApi = {
    async getAll(page = 0, size = 100) {
        return apiCall(`/products?page=${page}&size=${size}`);
    },
    async getById(id) {
        return apiCall(`/products/${id}`);
    },
    async getByCategory(categoryId, page = 0, size = 100) {
        return apiCall(
            `/products?categoryId=${categoryId}&page=${page}&size=${size}`,
        );
    },
    async getByBrand(brandId, page = 0, size = 100) {
        return apiCall(`/products?brandId=${brandId}&page=${page}&size=${size}`);
    },
};

// Product Variants API
export const variantsApi = {
    async getAll(page = 0, size = 100) {
        return apiCall(`/variants?page=${page}&size=${size}`);
    },
    async getById(id) {
        return apiCall(`/variants/${id}`);
    },
    async getByProductId(productId, page = 0, size = 100) {
        return apiCall(
            `/variants?productId=${productId}&page=${page}&size=${size}`,
        );
    },
};

// Colors API
export const colorsApi = {
    async getAll() {
        return apiCall("/colors");
    },
    async getById(id) {
        return apiCall(`/colors/${id}`);
    },
    async create(data) {
        return apiCall("/colors", {
            method: "POST",
            body: JSON.stringify(data),
        });
    },
    async update(id, data) {
        return apiCall(`/colors/${id}`, {
            method: "PUT",
            body: JSON.stringify(data),
        });
    },
    async delete(id) {
        return apiCall(`/colors/${id}`, {
            method: "DELETE",
        });
    },
};

// Sizes API
export const sizesApi = {
    async getAll() {
        return apiCall("/sizes");
    },
    async getById(id) {
        return apiCall(`/sizes/${id}`);
    },
    async create(data) {
        return apiCall("/sizes", {
            method: "POST",
            body: JSON.stringify(data),
        });
    },
    async update(id, data) {
        return apiCall(`/sizes/${id}`, {
            method: "PUT",
            body: JSON.stringify(data),
        });
    },
    async delete(id) {
        return apiCall(`/sizes/${id}`, {
            method: "DELETE",
        });
    },
};

// Cart API
export const cartApi = {
    async getCart(accountId) {
        return apiCall(`/carts/${accountId}`);
    },
    async addToCart(cartId, variantId, quantity) {
        return apiCall(`/cart-items`, {
            method: "POST",
            body: JSON.stringify({ cartId, variantId, quantity }),
        });
    },
    async updateCartItem(cartItemId, quantity) {
        return apiCall(`/cart-items/${cartItemId}`, {
            method: "PUT",
            body: JSON.stringify({ quantity }),
        });
    },
    async removeCartItem(cartItemId) {
        return apiCall(`/cart-items/${cartItemId}`, {
            method: "DELETE",
        });
    },
};

// Orders API
export const ordersApi = {
    async getUserOrders(accountId) {
        return apiCall(`/orders?accountId=${accountId}`);
    },
    async createOrder(orderData) {
        return apiCall("/orders", {
            method: "POST",
            body: JSON.stringify(orderData),
        });
    },
    async getOrderById(orderId) {
        return apiCall(`/orders/${orderId}`);
    },
};

// Vouchers API - bổ sung
export const vouchersApi = {
    async getUserVouchers(accountId) {
        try {
            // Thử nhiều endpoint khác nhau
            const response = await apiCall(`/vouchers?accountId=${accountId}`);
            return response;
        } catch (error) {
            console.error("Error fetching vouchers:", error);
            return { data: [] };
        }
    },

    async validateVoucher(code) {
        return apiCall(`/vouchers/validate?code=${code}`);
    },

    async getAllVouchers() {
        return apiCall(`/vouchers`);
    },
};

function asFiniteNumber(value, fallback = 0) {
    const n = Number(value);
    return Number.isFinite(n) ? n : fallback;
}

function extractListPayload(payload) {
    if (Array.isArray(payload)) {
        return payload;
    }

    if (!payload || typeof payload !== "object") {
        return [];
    }

    if (Array.isArray(payload.content)) {
        return payload.content;
    }

    if (Array.isArray(payload.list)) {
        return payload.list;
    }

    if (Array.isArray(payload.data)) {
        return payload.data;
    }

    return [];
}

function firstDefinedValue(candidates, fallback = null) {
    const values = Array.isArray(candidates) ? candidates : [];
    for (const value of values) {
        if (value !== undefined && value !== null) {
            return value;
        }
    }
    return fallback;
}

function normalizeRoleName(roleValue) {
    if (typeof roleValue !== "string") {
        return null;
    }

    const normalized = roleValue.trim().toUpperCase();
    if (!normalized) {
        return null;
    }

    return normalized.startsWith("ROLE_") ? normalized.slice(5) : normalized;
}

function resolveAccountRole(source) {
    if (!source || typeof source !== "object") {
        return "USER";
    }

    const directRole =
        normalizeRoleName(source.role) ||
        normalizeRoleName(source.roleName) ||
        normalizeRoleName(source.authority);

    if (directRole) {
        return directRole;
    }

    if (!Array.isArray(source.roles)) {
        return "USER";
    }

    const roleNames = source.roles
        .map((item) => {
            if (typeof item === "string") {
                return normalizeRoleName(item);
            }

            if (!item || typeof item !== "object") {
                return null;
            }

            return (
                normalizeRoleName(item.role) ||
                normalizeRoleName(item.roleName) ||
                normalizeRoleName(item.name) ||
                normalizeRoleName(item.authority)
            );
        })
        .filter(Boolean);

    if (roleNames.includes("ADMIN")) {
        return "ADMIN";
    }

    if (roleNames.includes("USER")) {
        return "USER";
    }

    if (roleNames.includes("CUSTOMER")) {
        return "CUSTOMER";
    }

    if (roleNames.includes("GUEST")) {
        return "GUEST";
    }

    return roleNames[0] || "USER";
}

function normalizeBooleanValue(value, fallback = true) {
    if (value === true || value === 1 || value === "1" || value === "true") {
        return true;
    }
    if (value === false || value === 0 || value === "0" || value === "false") {
        return false;
    }
    return fallback;
}

function normalizeOrderStatus(statusValue) {
    const normalized = String(statusValue || "PENDING")
        .trim()
        .toUpperCase();

    if (!normalized) {
        return "PENDING";
    }

    const compact = normalized
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .replace(/\s+/g, "_")
        .replace(/-/g, "_");

    if (
        ["NEW", "PENDING", "WAITING", "CHO_XU_LY", "CHUA_XU_LY"].includes(compact)
    ) {
        return "PENDING";
    }

    if (
        [
            "CONFIRMED",
            "XAC_NHAN",
            "PROCESSING",
            "DANG_XU_LY",
            "IN_PROGRESS",
            "INPROGRESS",
        ].includes(compact)
    ) {
        return "PROCESSING";
    }

    if (
        ["SHIPPING", "DELIVERING", "DANG_GIAO", "IN_TRANSIT", "TRANSIT"].includes(
            compact,
        )
    ) {
        return "SHIPPING";
    }

    if (
        ["SUCCESS", "COMPLETED", "DELIVERED", "DA_GIAO", "GIAO_THANH_CONG", "THANH_CONG", "HOAN_THANH", "RECEIVED"].includes(
            compact,
        )
    ) {
        return "SUCCESS";
    }

    if (["RETURN_REQUEST", "YEU_CAU_TRA"].includes(compact)) {
        return "RETURN_REQUEST";
    }

    if (["RETURNING", "TRA_HANG", "DANG_HOAN"].includes(compact)) {
        return "RETURNING";
    }

    if (["RETURNED", "REFUND", "HOAN_TRA"].includes(compact)) {
        return "RETURNED";
    }

    if (
        ["DELIVERY_FAILED", "FAILED_DELIVERY", "GIAO_THAT_BAI", "UNDELIVERED"].includes(
            compact,
        )
    ) {
        return "CANCELLED";
    }

    if (["LOST", "THAT_LAC", "PARCEL_LOST"].includes(compact)) {
        return "CANCELLED";
    }

    if (
        ["CANCELLED", "CANCELED", "HUY", "DA_HUY", "FAILED", "REJECTED"].includes(
            compact,
        )
    ) {
        return "CANCELLED";
    }

    return compact;
}

function normalizePaymentStatus(statusValue) {
    const normalized = String(statusValue || "PENDING")
        .trim()
        .toUpperCase();

    if (!normalized) {
        return "PENDING";
    }

    const compact = normalized
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .replace(/\s+/g, "_")
        .replace(/-/g, "_");

    if (
        ["PENDING", "UNPAID", "CHUA_THANH_TOAN", "AWAITING_PAYMENT"].includes(
            compact,
        )
    ) {
        return "PENDING";
    }

    if (["PAID", "DA_THANH_TOAN", "SETTLED", "CAPTURED"].includes(compact)) {
        return "PAID";
    }

    if (
        ["REFUNDED", "REFUND", "HOAN_TIEN", "CHARGEBACK", "REVERSED"].includes(
            compact,
        )
    ) {
        return "REFUNDED";
    }

    return compact;
}

function isSuccessfulOrder(order) {
    const paymentStatus = normalizePaymentStatus(order && order.paymentStatus);
    return ["PAID", "SETTLED", "CAPTURED"].includes(paymentStatus);
}

function isDeductedOrder(order) {
    const paymentStatus = normalizePaymentStatus(order && order.paymentStatus);
    return ["REFUNDED", "REFUND", "CHARGEBACK", "REVERSED"].includes(
        paymentStatus,
    );
}

function getOrderAmount(order) {
    const source = order && typeof order === "object" ? order : {};
    return asFiniteNumber(
        firstDefinedValue(
            [
                source.finalAmount,
                source.final_amount,
                source.totalMoney,
                source.total_money,
                source.totalAmount,
                source.total_amount,
            ],
            0,
        ),
        0,
    );
}

function getSuccessfulRevenue(orders) {
    const rows = Array.isArray(orders) ? orders : [];
    return rows.reduce((sum, order) => {
        const amount = Math.abs(getOrderAmount(order));
        if (isDeductedOrder(order)) {
            return sum - amount;
        }
        if (isSuccessfulOrder(order)) {
            return sum + amount;
        }
        return sum;
    }, 0);
}

function isCustomerAccount(account) {
    const source = account && typeof account === "object" ? account : {};
    const role = normalizeRoleName(source.role) || "USER";

    if (role === "ADMIN" || role === "GUEST") {
        return false;
    }

    const username = String(source.username || "")
        .trim()
        .toLowerCase();
    const email = String(source.gmail || source.email || "")
        .trim()
        .toLowerCase();

    if (username === "guest" || email === "guest" || email.startsWith("guest@")) {
        return false;
    }

    return true;
}

function normalizeDatetimeValue(value) {
    if (value === undefined || value === null || value === "") {
        return null;
    }

    const normalized = String(value).trim();
    if (!normalized) {
        return null;
    }

    // Convert datetime-local values (no timezone) to ISO format accepted by Instant.parse.
    if (/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}(:\d{2}(\.\d{1,3})?)?$/.test(normalized)) {
        const parsed = new Date(normalized);
        if (!Number.isNaN(parsed.getTime())) {
            return parsed.toISOString();
        }
    }

    return normalized;
}

function normalizeVoucherDiscountType(value) {
    const normalized = String(value || "PERCENT")
        .trim()
        .toUpperCase();

    if (normalized === "FIXED") {
        return "AMOUNT";
    }

    if (normalized === "AMOUNT" || normalized === "PERCENT") {
        return normalized;
    }

    return "PERCENT";
}

function normalizeVariantStatus(value, stockQuantity = null) {
    const normalized = String(value || "")
        .trim()
        .toUpperCase();
    const hasKnownStock = stockQuantity !== null && stockQuantity !== undefined && stockQuantity !== "";
    const hasStock = hasKnownStock ? Number(stockQuantity) > 0 : true;

    if (["SELLING", "AVAILABLE", "ACTIVE", "ON_SALE"].includes(normalized)) {
        return hasStock ? "SELLING" : "OUT_OF_STOCK";
    }

    if (["OUT_OF_STOCK", "SOLD_OUT", "OUTOFSTOCK"].includes(normalized)) {
        return "OUT_OF_STOCK";
    }

    if (["HIDDEN", "INACTIVE", "DISCONTINUED", "STOP_SELLING"].includes(normalized)) {
        return "HIDDEN";
    }

    return hasStock ? "SELLING" : "OUT_OF_STOCK";
}

function cleanupPayload(payload) {
    const source = payload && typeof payload === "object" ? payload : {};
    return Object.fromEntries(
        Object.entries(source).filter(([, value]) => value !== undefined),
    );
}

function buildOrderUpdateCandidates(id, payload) {
    const requests = [];
    const normalizedStatus =
        payload.status !== undefined ?
        normalizeOrderStatus(payload.status) :
        undefined;
    const normalizedPaymentStatus =
        payload.paymentStatus !== undefined ?
        normalizePaymentStatus(payload.paymentStatus) :
        undefined;

    const statusAliases = normalizedStatus === undefined ? [undefined] :
        Array.from(new Set([
            normalizedStatus,
            normalizedStatus.toLowerCase(),
            ...(normalizedStatus === "RETURN_REQUEST" ? ["TRA_HANG", "YEU_CAU_TRA"] : []),
            ...(normalizedStatus === "RETURNING" ? ["DANG_HOAN", "TRA_HANG"] : []),
            ...(normalizedStatus === "RETURNED" ? ["HOAN_TRA"] : []),
            ...(normalizedStatus === "PENDING" ? ["CHO_XU_LY"] : []),
            ...(normalizedStatus === "PROCESSING" ? ["XAC_NHAN", "DANG_XU_LY"] : []),
            ...(normalizedStatus === "DELIVERED" ? ["DA_GIAO"] : []),
            ...(normalizedStatus === "DELIVERY_FAILED" ? ["GIAO_THAT_BAI"] : []),
            ...(normalizedStatus === "LOST" ? ["THAT_LAC"] : []),
        ]));

    const paymentAliases = normalizedPaymentStatus === undefined ? [undefined] :
        Array.from(new Set([
            normalizedPaymentStatus,
            normalizedPaymentStatus.toLowerCase(),
            ...(normalizedPaymentStatus === "PAID" ? ["DA_THANH_TOAN"] : []),
            ...(normalizedPaymentStatus === "REFUNDED" ? ["HOAN_TIEN"] : []),
            ...(normalizedPaymentStatus === "PENDING" ? ["CHUA_THANH_TOAN"] : []),
        ]));

    const payloadVariants = [
        payload,
        cleanupPayload({
            ...payload,
            ...(normalizedStatus !== undefined ? { status: normalizedStatus } : {}),
            ...(normalizedPaymentStatus !== undefined ? { paymentStatus: normalizedPaymentStatus } : {}),
        }),
        cleanupPayload({
            ...payload,
            ...(normalizedStatus !== undefined ? { status: normalizedStatus.toLowerCase() } : {}),
            ...(normalizedPaymentStatus !== undefined ? { paymentStatus: normalizedPaymentStatus.toLowerCase() } : {}),
        }),
        cleanupPayload({
            ...payload,
            ...(normalizedStatus !== undefined ? { status: normalizedStatus } : {}),
            ...(normalizedPaymentStatus !== undefined ? { payment_status: normalizedPaymentStatus, paymentStatus: undefined } : {}),
        }),
    ];

    for (const statusAlias of statusAliases) {
        for (const paymentAlias of paymentAliases) {
            payloadVariants.push(cleanupPayload({
                ...payload,
                ...(statusAlias !== undefined ? { status: statusAlias } : {}),
                ...(paymentAlias !== undefined ? { paymentStatus: paymentAlias } : {}),
            }));
        }
    }

    for (const body of payloadVariants) {
        requests.push({ endpoint: `/orders/${id}`, method: "PUT", body });
        requests.push({ endpoint: `/orders/${id}`, method: "PATCH", body });
        requests.push({ endpoint: `/orders/update/${id}`, method: "PUT", body });
    }

    return requests;
}

function normalizeOrderRecord(rawOrder) {
    const source = rawOrder && typeof rawOrder === "object" ? rawOrder : {};
    return {
        ...source,
        id: asFiniteNumber(
            firstDefinedValue([source.id, source._id, source.orderId, source.order_id], null),
            null,
        ),
        accountId: asFiniteNumber(
            firstDefinedValue([source.accountId, source.account_id, source._accountId, source.account_id], null),
            null,
        ),
        fullName: firstDefinedValue(
            [source.fullName, source.fullname, source.customerName, source.customer],
            "",
        ),
        email: firstDefinedValue(
            [source.email, source.gmail, source.customerEmail],
            "",
        ),
        phone: firstDefinedValue(
            [source.phone, source.customerPhone, source.mobile],
            "",
        ),
        status: normalizeOrderStatus(
            firstDefinedValue(
                [source.status, source.orderStatus, source.order_status],
                "PENDING",
            ),
        ),
        paymentStatus: normalizePaymentStatus(
            firstDefinedValue(
                [
                    source.paymentStatus,
                    source.payment_status,
                ],
                "PENDING",
            ),
        ),
        paymentMethodId: asFiniteNumber(
            firstDefinedValue(
                [
                    source.paymentMethodId,
                    source.payment_method_id,
                    source.paymentMethod && source.paymentMethod.id,
                    source.payment_method && source.payment_method.id,
                ],
                null,
            ),
            null,
        ),
        paymentMethodName: firstDefinedValue(
            [
                source.paymentMethodName,
                source.payment_method_name,
                source.paymentMethod && source.paymentMethod.name,
                source.payment_method && source.payment_method.name,
                typeof source.paymentMethod === "string" ? source.paymentMethod : null,
                typeof source.payment_method === "string" ? source.payment_method : null,
            ],
            "",
        ),
        totalMoney: asFiniteNumber(
            firstDefinedValue(
                [
                    source.totalMoney,
                    source.total_money,
                    source.totalAmount,
                    source.total_amount,
                ],
                0,
            ),
            0,
        ),
        finalAmount: asFiniteNumber(
            firstDefinedValue(
                [
                    source.finalAmount,
                    source.final_amount,
                    source.totalMoney,
                    source.total_money,
                ],
                0,
            ),
            0,
        ),
        createdAt: firstDefinedValue(
            [
                source.createdAt,
                source.created_at,
                source.orderDate,
                source.order_date,
            ],
            null,
        ),
    };
}

function normalizeAccountRecord(rawAccount) {
    const source = rawAccount && typeof rawAccount === "object" ? rawAccount : {};
    return {
        ...source,
        id: asFiniteNumber(
            firstDefinedValue([source.id, source.accountId, source.account_id], null),
            null,
        ),
        username: firstDefinedValue([source.username, source.userName], ""),
        gmail: firstDefinedValue([source.gmail, source.email], ""),
        phone: firstDefinedValue([source.phone], ""),
        address: firstDefinedValue([source.address], ""),
        role: resolveAccountRole(source),
        isActive: normalizeBooleanValue(
            firstDefinedValue([source.isActive, source.is_active], true),
            true,
        ),
        imgUrl: resolveImageUrl(
            firstDefinedValue([source.imgUrl, source.img_url], null),
        ),
    };
}

function normalizeProductRecord(rawProduct) {
    const source = rawProduct && typeof rawProduct === "object" ? rawProduct : {};
    return {
        ...source,
        id: asFiniteNumber(
            firstDefinedValue([source.id, source.productId, source.product_id], null),
            null,
        ),
        name: firstDefinedValue([source.name], ""),
        productCode: firstDefinedValue(
            [source.productCode, source.product_code],
            "",
        ),
        status: firstDefinedValue([source.status], "AVAILABLE"),
        categoryId: asFiniteNumber(
            firstDefinedValue([source.categoryId, source.category_id], null),
            null,
        ),
        brandId: asFiniteNumber(
            firstDefinedValue([source.brandId, source.brand_id], null),
            null,
        ),
        imageUrl: resolveImageUrl(
            firstDefinedValue([source.imageUrl, source.image_url], null),
        ),
    };
}

function normalizeVariantRecord(rawVariant) {
    const source = rawVariant && typeof rawVariant === "object" ? rawVariant : {};
    const colorObject =
        source.color && typeof source.color === "object" ? source.color : {};
    const sizeObject =
        source.size && typeof source.size === "object" ? source.size : {};
    const productObject =
        source.product && typeof source.product === "object" ? source.product : {};

    const rawExtraImages = firstDefinedValue(
        [source.extraImages, source.extra_images], [],
    );
    const normalizedExtraImages = (Array.isArray(rawExtraImages) ?
            rawExtraImages : [])
        .map((url) => resolveImageUrl(url))
        .filter((url) => !!url);
    const stockQuantity = asFiniteNumber(
        firstDefinedValue(
            [source.stockQuantity, source.stock_quantity, source.quantity],
            0,
        ),
        0,
    );

    return {
        ...source,
        id: asFiniteNumber(
            firstDefinedValue([source.id, source.variantId, source.variant_id], null),
            null,
        ),
        productId: asFiniteNumber(
            firstDefinedValue(
                [source.productId, source.product_id, productObject.id],
                null,
            ),
            null,
        ),
        colorId: asFiniteNumber(
            firstDefinedValue(
                [source.colorId, source.color_id, colorObject.id],
                null,
            ),
            null,
        ),
        sizeId: asFiniteNumber(
            firstDefinedValue([source.sizeId, source.size_id, sizeObject.id], null),
            null,
        ),
        productName: firstDefinedValue(
            [source.productName, source.product_name, productObject.name],
            "",
        ),
        colorName: firstDefinedValue(
            [source.colorName, source.color_name, colorObject.name],
            "",
        ),
        sizeName: firstDefinedValue(
            [source.sizeName, source.size_name, sizeObject.name],
            "",
        ),
        colorHex: firstDefinedValue(
            [
                source.colorHex,
                source.color_hex,
                colorObject.hexCode,
                colorObject.hex_code,
            ],
            null,
        ),
        price: asFiniteNumber(
            firstDefinedValue([source.price, source.salePrice, source.sale_price], 0),
            0,
        ),
        stockQuantity,
        status: normalizeVariantStatus(
            firstDefinedValue([source.status, source.variantStatus, source.variant_status], null),
            stockQuantity,
        ),
        imageUrl: resolveImageUrl(
            firstDefinedValue([source.imageUrl, source.image_url], null),
        ),
        extraImages: normalizedExtraImages,
    };
}

function normalizeVoucherRecord(rawVoucher) {
    const source = rawVoucher && typeof rawVoucher === "object" ? rawVoucher : {};

    const startDate = normalizeDatetimeValue(
        firstDefinedValue([source.startDate, source.start_date], null),
    );
    const endDate = normalizeDatetimeValue(
        firstDefinedValue([source.endDate, source.end_date], null),
    );
    const usedCount = asFiniteNumber(
        firstDefinedValue([source.usedCount, source.used_count], 0),
        0,
    );
    const usageLimit = asFiniteNumber(
        firstDefinedValue([source.usageLimit, source.usage_limit], null),
        null,
    );
    const isActive = normalizeBooleanValue(
        firstDefinedValue([source.isActive, source.is_active], true),
        true,
    );

    const normalized = {
        ...source,
        id: asFiniteNumber(
            firstDefinedValue([source.id, source.voucherId, source.voucher_id], null),
            null,
        ),
        code: String(
                firstDefinedValue(
                    [source.code, source.voucherCode, source.voucher_code],
                    "",
                ),
            )
            .trim()
            .toUpperCase(),
        discountType: normalizeVoucherDiscountType(
            firstDefinedValue([source.discountType, source.discount_type], "PERCENT"),
        ),
        discountValue: asFiniteNumber(
            firstDefinedValue([source.discountValue, source.discount_value], 0),
            0,
        ),
        minOrderValue: asFiniteNumber(
            firstDefinedValue([source.minOrderValue, source.min_order_value], 0),
            0,
        ),
        maxDiscountAmount: asFiniteNumber(
            firstDefinedValue([source.maxDiscountAmount, source.max_discount_amount], null),
            null,
        ),
        startDate,
        endDate,
        usedCount,
        usageLimit,
        isActive,
        statusLabel: firstDefinedValue([source.statusLabel, source.status_label], null),
    };

    const now = Date.now();
    const startTime = startDate ? new Date(startDate).getTime() : null;
    const endTime = endDate ? new Date(endDate).getTime() : null;

    let status = String(
            firstDefinedValue([source.status, source.statusLabel, source.status_label], "")
        )
        .trim()
        .toUpperCase();

    if (!["ACTIVE", "INACTIVE", "SCHEDULED", "USED", "EXPIRED"].includes(status)) {
        status = isActive ? "ACTIVE" : "INACTIVE";
        if (Number.isFinite(endTime) && endTime < now) {
            status = "EXPIRED";
        } else if (Number.isFinite(startTime) && startTime > now) {
            status = "SCHEDULED";
        } else if (
            usageLimit !== null &&
            Number.isFinite(usageLimit) &&
            usageLimit > 0 &&
            usedCount >= usageLimit
        ) {
            status = "USED";
        }
    }

    return {
        ...normalized,
        status,
    };
}

function normalizeAccountPayload(rawAccount, { forCreate = false } = {}) {
    const source = rawAccount && typeof rawAccount === "object" ? rawAccount : {};
    const role =
        normalizeRoleName(firstDefinedValue([source.role], "USER")) || "USER";
    const payload = {
        username: firstDefinedValue([source.username, source.userName], ""),
        gmail: firstDefinedValue([source.gmail, source.email], ""),
        phone: firstDefinedValue([source.phone], ""),
        address: firstDefinedValue([source.address], ""),
        role,
        isActive: normalizeBooleanValue(
            firstDefinedValue([source.isActive, source.is_active], true),
            true,
        ),
        imgUrl: firstDefinedValue([source.imgUrl, source.img_url], null),
    };

    if (forCreate || source.password) {
        payload.password = firstDefinedValue([source.password], "");
    }

    return cleanupPayload(payload);
}

function normalizeProductPayload(rawProduct) {
    const source = rawProduct && typeof rawProduct === "object" ? rawProduct : {};
    const payload = {
        name: firstDefinedValue([source.name], ""),
        productCode: firstDefinedValue(
            [source.productCode, source.product_code],
            "",
        ),
        categoryId: asFiniteNumber(
            firstDefinedValue([source.categoryId, source.category_id], null),
            null,
        ),
        brandId: asFiniteNumber(
            firstDefinedValue([source.brandId, source.brand_id], null),
            null,
        ),
        imageUrl: firstDefinedValue([source.imageUrl, source.image_url], null),
    };

    if (source.status !== undefined || source.productStatus !== undefined) {
        payload.status = String(firstDefinedValue([source.status, source.productStatus], "AVAILABLE"))
            .trim()
            .toUpperCase();
    }

    return cleanupPayload(payload);
}

function normalizeVariantPayload(rawVariant) {
    const source = rawVariant && typeof rawVariant === "object" ? rawVariant : {};
    const rawExtraImages = firstDefinedValue(
        [source.extraImages, source.extra_images], [],
    );
    const normalizedExtraImages = (Array.isArray(rawExtraImages) ?
            rawExtraImages : [])
        .map((url) => (typeof url === "string" ? url.trim() : ""))
        .filter(Boolean);

    const payload = {
        productId: asFiniteNumber(
            firstDefinedValue([source.productId, source.product_id], null),
            null,
        ),
        colorId: asFiniteNumber(
            firstDefinedValue([source.colorId, source.color_id], null),
            null,
        ),
        sizeId: asFiniteNumber(
            firstDefinedValue([source.sizeId, source.size_id], null),
            null,
        ),
        price: asFiniteNumber(firstDefinedValue([source.price], 0), 0),
        stockQuantity: asFiniteNumber(
            firstDefinedValue(
                [source.stockQuantity, source.stock_quantity, source.quantity],
                0,
            ),
            0,
        ),
        imageUrl: firstDefinedValue([source.imageUrl, source.image_url], null),
        extraImages: normalizedExtraImages,
    };

    if (source.status !== undefined || source.variantStatus !== undefined || source.variant_status !== undefined) {
        payload.status = normalizeVariantStatus(
            firstDefinedValue([source.status, source.variantStatus, source.variant_status], null),
            firstDefinedValue([source.stockQuantity, source.stock_quantity, source.quantity], 0),
        );
    }

    return cleanupPayload(payload);
}

function normalizeVoucherPayload(rawVoucher) {
    const source = rawVoucher && typeof rawVoucher === "object" ? rawVoucher : {};
    return cleanupPayload({
        code: String(
                firstDefinedValue(
                    [source.code, source.voucherCode, source.voucher_code],
                    "",
                ),
            )
            .trim()
            .toUpperCase(),
        discountType: normalizeVoucherDiscountType(
            firstDefinedValue([source.discountType, source.discount_type], "PERCENT"),
        ),
        discountValue: asFiniteNumber(
            firstDefinedValue([source.discountValue, source.discount_value], 0),
            0,
        ),
        minOrderValue: asFiniteNumber(
            firstDefinedValue([source.minOrderValue, source.min_order_value], 0),
            0,
        ),
        startDate: normalizeDatetimeValue(
            firstDefinedValue([source.startDate, source.start_date], null),
        ),
        endDate: normalizeDatetimeValue(
            firstDefinedValue([source.endDate, source.end_date], null),
        ),
        usageLimit: asFiniteNumber(
            firstDefinedValue([source.usageLimit, source.usage_limit], null),
            null,
        ),
        maxDiscountAmount: asFiniteNumber(
            firstDefinedValue([source.maxDiscountAmount, source.max_discount_amount], null),
            null,
        ),
        isActive: normalizeBooleanValue(
            firstDefinedValue([source.isActive, source.is_active], true),
            true,
        ),
    });
}

function shouldTryNextEndpoint(status) {
    return [400, 404, 405, 409, 501].includes(status);
}

function stringifyErrorPayload(error) {
    const source = error && typeof error === "object" ? error : {};
    const parts = [source.message];

    if (source.data && typeof source.data === "object") {
        parts.push(source.data.message);
        parts.push(source.data.error);
        if (source.data.raw) {
            parts.push(source.data.raw);
        }
    }

    return parts
        .filter((part) => typeof part === "string" && part.trim().length > 0)
        .join(" ")
        .toLowerCase();
}

function isForeignKeyConstraintError(error) {
    const text = stringifyErrorPayload(error);
    if (!text) {
        return false;
    }

    return [
        "foreign key",
        "constraint",
        "violates",
        "cannot delete",
        "order item",
        "order_items",
        "integrity",
    ].some((keyword) => text.includes(keyword));
}

async function softDisableProductById(id) {
    const current = await productsApi.getById(id);
    const statusPayload = {
        ...current,
        status: "HIDDEN",
    };

    try {
        await tryApiRequests([
            { endpoint: `/products/${id}`, method: "PUT", body: statusPayload }
        ]);
        return true;
    } catch {
        return false;
    }
}

async function softDisableVariantById(id) {
    const current = await variantsApi.getById(id);
    const updatePayload = {
        ...current,
        stockQuantity: 0,
        status: "HIDDEN",
    };
    await tryApiRequests([
        { endpoint: `/variants/${id}`, method: "PUT", body: updatePayload }
    ]);
    return true;
}

async function softDisableAccountById(id) {
    const current = await accountsApi.getById(id);
    const payload = {...current, isActive: false };
    await tryApiRequests([
        { endpoint: `/accounts/${id}`, method: "PUT", body: payload }
    ]);
    return true;
}

async function softCancelOrderById(id) {
    const payload = { status: "CANCELLED" };
    await tryApiRequests(buildOrderUpdateCandidates(id, payload));
    return true;
}

async function softDisableBrandById(id) {
    const current = await brandsApi.getById(id);
    const payload = {...current, isActive: false };
    await tryApiRequests([
        { endpoint: `/brands/${id}`, method: "PUT", body: payload }
    ]);
    return true;
}

async function tryApiRequests(requestCandidates) {
    const candidates = Array.isArray(requestCandidates) ? requestCandidates : [];
    let lastError = null;

    for (const candidate of candidates) {
        const endpoint =
            candidate && candidate.endpoint ? candidate.endpoint : null;
        if (!endpoint) {
            continue;
        }

        const method = candidate.method || "GET";
        const options = {
            method,
        };

        if (candidate.body !== undefined) {
            options.body = JSON.stringify(candidate.body);
        }

        if (candidate.headers && typeof candidate.headers === "object") {
            options.headers = candidate.headers;
        }

        try {
            return await apiCall(endpoint, options);
        } catch (error) {
            lastError = error;
            const status = error && typeof error === "object" ? error.status : null;
            if (shouldTryNextEndpoint(status)) {
                continue;
            }
            throw error;
        }
    }

    if (lastError) {
        throw lastError;
    }

    return null;
}

async function tryApiEndpoints(endpointCandidates) {
    const candidates = (
        Array.isArray(endpointCandidates) ? endpointCandidates : []
    ).map((endpoint) => ({
        endpoint,
        method: "GET",
    }));
    return tryApiRequests(candidates);
}

export const adminApi = {
    async getOrders({ page = 0, size = 100 } = {}) {
        const data = await tryApiEndpoints([
            `/orders?page=${page}&size=${size}`,
            `/orders`,
        ]);

        // Debug: log raw payload from backend to help diagnose missing IDs
        try {
            console.log("[DEBUG] adminApi.getOrders raw payload:", data);
        } catch (e) {
            /* ignore logging errors */
        }

        return extractListPayload(data)
            .map(normalizeOrderRecord)
            .sort((a, b) => {
                const timeA = new Date(a.createdAt || 0).getTime();
                const timeB = new Date(b.createdAt || 0).getTime();
                if (timeA !== timeB) {
                    return timeB - timeA;
                }
                return asFiniteNumber(b.id, 0) - asFiniteNumber(a.id, 0);
            });
    },

    async createOrder(orderData) {
        const payload = cleanupPayload(orderData);
        const data = await tryApiRequests([
            { endpoint: "/orders", method: "POST", body: payload },
            { endpoint: "/orders/create", method: "POST", body: payload },
        ]);

        return normalizeOrderRecord(
            data && typeof data === "object" ? data : payload,
        );
    },
    async getBrands() {
        return brandsApi.getAll();
    },
    async createBrand(data) {
        return brandsApi.create(data);
    },
    async updateBrand(id, data) {
        return brandsApi.update(id, data);
    },
    async deleteBrand(id) {
        const numericId = asFiniteNumber(id, null);
        if (numericId === null) {
            throw new Error("Thiếu brandId hợp lệ");
        }
        await softDisableBrandById(numericId);
        return {
            softDeleted: true,
            message: "Thương hiệu đã được vô hiệu hóa (xóa mềm)."
        };
    },

    async restoreBrand(id) {
        const numericId = asFiniteNumber(id, null);
        const current = await brandsApi.getById(numericId);
        const payload = {...current, isActive: true };
        await tryApiRequests([
            { endpoint: `/brands/${numericId}`, method: "PUT", body: payload }
        ]);
        return true;
    },

    async updateOrder(orderId, orderData) {
        const id = asFiniteNumber(orderId, null);
        if (id === null) {
            throw new Error("Thiếu orderId hợp lệ");
        }

        const payload = cleanupPayload(orderData);
        const data = await tryApiRequests(buildOrderUpdateCandidates(id, payload));

        return normalizeOrderRecord(
            data && typeof data === "object" ? data : {...payload, id },
        );
    },

    async deleteOrder(orderId) {
        const id = asFiniteNumber(orderId, null);
        if (id === null) {
            throw new Error("Thiếu orderId hợp lệ");
        }
        await softCancelOrderById(id);
        return {
            softDeleted: true,
            message: "Đơn hàng đã được hủy (xóa mềm).",
        };
    },

    async restoreOrder(orderId, status) {
        const id = asFiniteNumber(orderId, null);
        await tryApiRequests([
            { endpoint: `/orders/${id}`, method: "PUT", body: { status: status || "PENDING" } },
            { endpoint: `/orders/${id}`, method: "PATCH", body: { status: status || "PENDING" } }
        ]);
        return true;
    },

    async getAccounts() {
        try {
            const data = await tryApiEndpoints(["/accounts"]);
            return extractListPayload(data).map(normalizeAccountRecord);
        } catch {
            const fallback = await accountsApi.getAll();
            return extractListPayload(fallback).map(normalizeAccountRecord);
        }
    },

    async getCustomerAccounts() {
        const accounts = await this.getAccounts();
        return accounts.filter(isCustomerAccount);
    },

    async createAccount(accountData) {
        const payload = normalizeAccountPayload(accountData, { forCreate: true });
        const data = await tryApiRequests([
            { endpoint: "/accounts", method: "POST", body: payload },
        ]);

        return normalizeAccountRecord(
            data && typeof data === "object" ? data : payload,
        );
    },

    async updateAccount(accountId, accountData) {
        const id = asFiniteNumber(accountId, null);
        if (id === null) {
            throw new Error("Thiếu accountId hợp lệ");
        }

        const payload = normalizeAccountPayload(accountData, { forCreate: false });
        const data = await tryApiRequests([
            { endpoint: `/accounts/${id}`, method: "PUT", body: payload },
            { endpoint: `/accounts/${id}`, method: "PATCH", body: payload },
        ]);

        return normalizeAccountRecord(
            data && typeof data === "object" ? data : {...payload, id },
        );
    },

    async deleteAccount(accountId) {
        const id = asFiniteNumber(accountId, null);
        if (id === null) {
            throw new Error("Thiếu accountId hợp lệ");
        }

        try {
            await softDisableAccountById(id);
            return {
                softDeleted: true,
                message: "Tài khoản đã được vô hiệu hóa thay vì xóa.",
            };
        } catch (error) {
            throw new Error("Không thể vô hiệu hóa tài khoản.");
        }
    },

    async restoreAccount(accountId) {
        const id = asFiniteNumber(accountId, null);
        const current = await accountsApi.getById(id);
        const payload = {...current, isActive: true };
        await tryApiRequests([
            { endpoint: `/accounts/${id}`, method: "PUT", body: payload }
        ]);
        return true;
    },

    async getProducts({ page = 0, size = 100 } = {}) {
        try {
            const data = await tryApiEndpoints([
                `/products?page=${page}&size=${size}`,
                `/products`,
            ]);
            return extractListPayload(data).map(normalizeProductRecord);
        } catch {
            const fallback = await productsApi.getAll(page, size);
            return extractListPayload(fallback).map(normalizeProductRecord);
        }
    },

    async createProduct(productData) {
        const payload = normalizeProductPayload(productData);
        const data = await tryApiRequests([
            { endpoint: "/products", method: "POST", body: payload },
        ]);

        return normalizeProductRecord(
            data && typeof data === "object" ? data : payload,
        );
    },

    async updateProduct(productId, productData) {
        const id = asFiniteNumber(productId, null);
        if (id === null) {
            throw new Error("Thiếu productId hợp lệ");
        }

        const payload = normalizeProductPayload(productData);
        const data = await tryApiRequests([
            { endpoint: `/products/${id}`, method: "PUT", body: payload },
            { endpoint: `/products/${id}`, method: "PATCH", body: payload },
        ]);

        return normalizeProductRecord(
            data && typeof data === "object" ? data : {...payload, id },
        );
    },

    async deleteProduct(productId) {
        const id = asFiniteNumber(productId, null);
        if (id === null) {
            throw new Error("Thiếu productId hợp lệ");
        }
        await softDisableProductById(id);
        return {
            softDeleted: true,
            message: "Sản phẩm đã được ẩn (xóa mềm).",
        };
    },

    async restoreProduct(productId) {
        const id = asFiniteNumber(productId, null);
        const current = await productsApi.getById(id);
        const payload = {...current, status: "AVAILABLE" };
        await tryApiRequests([
            { endpoint: `/products/${id}`, method: "PUT", body: payload }
        ]);
        return true;
    },

    async getVariants({ page = 0, size = 300 } = {}) {
        const data = await tryApiEndpoints([
            `/variants?page=${page}&size=${size}`,
            "/variants",
        ]);

        return extractListPayload(data)
            .map(normalizeVariantRecord)
            .sort((a, b) => asFiniteNumber(a.id, 0) - asFiniteNumber(b.id, 0));
    },

    async createVariant(variantData) {
        const payload = normalizeVariantPayload(variantData);
        const data = await tryApiRequests([
            { endpoint: "/variants", method: "POST", body: payload },
        ]);

        return normalizeVariantRecord(
            data && typeof data === "object" ? data : payload,
        );
    },

    async updateVariant(variantId, variantData) {
        const id = asFiniteNumber(variantId, null);
        if (id === null) {
            throw new Error("Thiếu variantId hợp lệ");
        }

        const payload = normalizeVariantPayload(variantData);
        const data = await tryApiRequests([
            { endpoint: `/variants/${id}`, method: "PUT", body: payload },
            { endpoint: `/variants/${id}`, method: "PATCH", body: payload },
        ]);

        return normalizeVariantRecord(
            data && typeof data === "object" ? data : {...payload, id },
        );
    },

    async deleteVariant(variantId) {
        const id = asFiniteNumber(variantId, null);
        if (id === null) {
            throw new Error("Thiếu variantId hợp lệ");
        }
        await softDisableVariantById(id);
        return {
            softDeleted: true,
            message: "Biến thể đã được ẩn (xóa mềm).",
        };
    },

    async restoreVariant(variantId, stockQuantity) {
        const id = asFiniteNumber(variantId, null);
        const current = await variantsApi.getById(id);
        const payload = {...current, status: "SELLING", stockQuantity: stockQuantity || 0 };
        await tryApiRequests([
            { endpoint: `/variants/${id}`, method: "PUT", body: payload }
        ]);
        return true;
    },

    async getVouchers({ page = 0, size = 300 } = {}) {
        const data = await tryApiEndpoints([
            `/vouchers?page=${page}&size=${size}`,
            "/vouchers",
        ]);

        return extractListPayload(data)
            .map(normalizeVoucherRecord)
            .sort((a, b) => {
                const endA = new Date(a.endDate || 0).getTime();
                const endB = new Date(b.endDate || 0).getTime();
                if (endA !== endB) {
                    return endA - endB;
                }
                return String(a.code || "").localeCompare(String(b.code || ""));
            });
    },

    async createVoucher(voucherData) {
        const payload = normalizeVoucherPayload(voucherData);
        const data = await tryApiRequests([
            { endpoint: "/vouchers", method: "POST", body: payload },
        ]);

        return normalizeVoucherRecord(
            data && typeof data === "object" ? data : payload,
        );
    },

    async updateVoucher(voucherId, voucherData) {
        const id = asFiniteNumber(voucherId, null);
        if (id === null) {
            throw new Error("Thiếu voucherId hợp lệ");
        }

        const payload = normalizeVoucherPayload(voucherData);
        const data = await tryApiRequests([
            { endpoint: `/vouchers/${id}`, method: "PUT", body: payload },
            { endpoint: `/vouchers/${id}`, method: "PATCH", body: payload },
        ]);

        return normalizeVoucherRecord(
            data && typeof data === "object" ? data : {...payload, id },
        );
    },

    async deleteVoucher(voucherId) {
        const id = asFiniteNumber(voucherId, null);
        if (id === null) {
            throw new Error("Thiếu voucherId hợp lệ");
        }

        try {
            await tryApiRequests([
                { endpoint: `/vouchers/${id}`, method: "PUT", body: { isActive: false } },
                { endpoint: `/vouchers/${id}`, method: "PATCH", body: { isActive: false } }
            ]);
            return {
                softDeleted: true,
                message: "Voucher đã được vô hiệu hóa.",
            };
        } catch (_disableError) {
            throw new Error("Không thể vô hiệu hóa voucher.");
        }
    },

    async restoreVoucher(voucherId) {
        const id = asFiniteNumber(voucherId, null);
        await tryApiRequests([
            { endpoint: `/vouchers/${id}`, method: "PUT", body: { isActive: true } },
            { endpoint: `/vouchers/${id}`, method: "PATCH", body: { isActive: true } }
        ]);
        return true;
    },

    async getDashboardStats() {
        const [
            statsResult,
            ordersResult,
            productsResult,
            customersResult,
            vouchersResult,
        ] = await Promise.allSettled([
            apiCall("/dashboard/stats"),
            this.getOrders({ page: 0, size: 400 }),
            this.getProducts({ page: 0, size: 400 }),
            this.getCustomerAccounts(),
            this.getVouchers({ page: 0, size: 400 }),
        ]);

        const baseStats =
            statsResult.status === "fulfilled" &&
            statsResult.value &&
            typeof statsResult.value === "object" ?
            statsResult.value : {};

        const orders =
            ordersResult.status === "fulfilled" ? ordersResult.value : [];
        const products =
            productsResult.status === "fulfilled" ? productsResult.value : [];
        const customers =
            customersResult.status === "fulfilled" ? customersResult.value : [];
        const vouchers =
            vouchersResult.status === "fulfilled" ? vouchersResult.value : [];
        const successOrdersCount = orders.filter(isSuccessfulOrder).length;
        const deductedOrdersCount = orders.filter(isDeductedOrder).length;
        const revenue = getSuccessfulRevenue(orders);

        return {
            ...baseStats,
            revenue,
            ordersCount: orders.length ||
                asFiniteNumber(
                    firstDefinedValue([baseStats.ordersCount, baseStats.totalOrders], 0),
                    0,
                ),
            successOrdersCount,
            deductedOrdersCount,
            productsCount: products.length ||
                asFiniteNumber(
                    firstDefinedValue(
                        [baseStats.productsCount, baseStats.totalProducts],
                        0,
                    ),
                    0,
                ),
            customersCount: customers.length ||
                asFiniteNumber(
                    firstDefinedValue(
                        [baseStats.customersCount, baseStats.totalCustomers],
                        0,
                    ),
                    0,
                ),
            vouchersCount: vouchers.length ||
                asFiniteNumber(
                    firstDefinedValue(
                        [baseStats.vouchersCount, baseStats.totalVouchers],
                        0,
                    ),
                    0,
                ),
        };
    },

    async getRecentOrders(limit = 10) {
        try {
            const data = await tryApiEndpoints([`/orders/recent?limit=${limit}`]);
            return extractListPayload(data).map(normalizeOrderRecord).slice(0, limit);
        } catch {
            const orders = await this.getOrders({
                page: 0,
                size: Math.max(limit, 30),
            });
            return orders.slice(0, limit);
        }
    },

    async getSystemInfo() {
        try {
            return await apiCall("/system/info");
        } catch {
            return {
                apiStatus: "Online",
                syncStatus: "Fallback mode",
                checkedAt: new Date().toISOString(),
            };
        }
    },
};

// ─────────────────────────────────────────────────────────────────────
//  PAYMENT API — VNPay integration
// ─────────────────────────────────────────────────────────────────────
export const paymentApi = {
    /**
     * Create VNPay payment URL.
     * Backend builds the signed URL; frontend redirects user to it.
     * @param {number} orderId - The order ID to pay for
     * @param {number} amount  - Payment amount in VND
     * @returns {Promise<{success: boolean, paymentUrl: string, orderId: number}>}
     */
    async createVnpayPayment(orderId, amount) {
        const body = { orderId };
        if (amount !== undefined && amount !== null) {
            body.amount = amount;
        }
        return await apiCall("/payment/vnpay/create", {
            method: "POST",
            body: JSON.stringify(body),
        });
    },

    /**
     * Verify VNPay return result via backend.
     * @param {Object} queryParams - All vnp_* query params from VNPay redirect
     * @returns {Promise<{success: boolean, orderId: string, message: string}>}
     */
    async verifyVnpayReturn(queryParams) {
        const qs = new URLSearchParams(queryParams).toString();
        return await apiCall(`/payment/vnpay/return?${qs}`);
    },
};
