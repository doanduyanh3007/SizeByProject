// src/services/orders.js
// Complete API service for cart and order operations

import { normalizeAppliedVoucher } from '@/utils/voucherValues'
import { getSession } from '@/utils/auth'

const API_BASE_URL =
    import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

function getAuthHeaders(extra = {}) {
    const token = getSession('token');
    return token ? { 'Authorization': `Bearer ${token}`, ...extra } : extra;
}

function toFiniteNumber(value, fallback = 0) {
    const n = Number(value)
    return Number.isFinite(n) ? n : fallback
}

function parseJsonSafe(response) {
    return response.json().catch(async() => {
        try {
            const text = await response.text();
            return text || null;
        } catch {
            return null;
        }
    })
}

function normalizeText(value) {
    if (value === undefined || value === null) {
        return ''
    }
    return String(value).trim()
}

function normalizeStatusKey(value) {
    return String(value || '')
        .trim()
        .normalize('NFD')
        .replace(/[\u0300-\u036f]/g, '')
        .replace(/\s+/g, '_')
        .replace(/-/g, '_')
        .toUpperCase()
}

function normalizeOrderStatus(value) {
    const key = normalizeStatusKey(value)

    if (['NEW', 'PENDING', 'WAITING', 'CHUA_XU_LY', 'CHO_XU_LY'].includes(key)) return 'PENDING'
    if (['CONFIRMED', 'PROCESSING', 'DANG_XU_LY', 'XAC_NHAN', 'IN_PROGRESS'].includes(key)) return 'PROCESSING'
    if (['SHIPPING', 'DELIVERING', 'DANG_GIAO', 'IN_TRANSIT', 'TRANSIT'].includes(key)) return 'SHIPPING'
    if (['SUCCESS', 'COMPLETED', 'DELIVERED', 'THANH_CONG', 'HOAN_THANH', 'RECEIVED'].includes(key)) return 'SUCCESS'
    if (['PARTIAL_RETURN_REQUEST', 'YEU_CAU_HOAN_MOT_PHAN'].includes(key)) return 'PARTIAL_RETURN_REQUEST'
    if (['PARTIAL_RETURNED', 'HOAN_MOT_PHAN'].includes(key)) return 'PARTIAL_RETURNED'
    if (['RETURNING', 'TRA_HANG', 'YEU_CAU_TRA'].includes(key)) return 'RETURNING'
    if (['RETURNED', 'HOAN_TRA', 'HOAN_TIEN', 'REFUND', 'REFUNDED'].includes(key)) return 'RETURNED'
    if (['CANCELLED', 'CANCELED', 'DA_HUY', 'HUY', 'FAILED'].includes(key)) return 'CANCELLED'

    return key || 'PENDING'
}

function normalizePaymentStatus(value) {
    const key = normalizeStatusKey(value)

    if (['PENDING', 'UNPAID', 'CHUA_THANH_TOAN', 'AWAITING_PAYMENT'].includes(key)) return 'PENDING'
    if (['PAID', 'DA_THANH_TOAN', 'SETTLED', 'CAPTURED'].includes(key)) return 'PAID'
    if (['REFUNDED', 'REFUND', 'HOAN_TIEN', 'CHARGEBACK', 'REVERSED'].includes(key)) return 'REFUNDED'

    return key || 'PENDING'
}

function validatePlaceOrderPayload(orderData) {
    const source = orderData && typeof orderData === 'object' ? orderData : {}
    const accountId = toFiniteNumber(
        source.accountId !== undefined && source.accountId !== null ? source.accountId : source.account_id,
        null,
    )
    const fullName = normalizeText(source.fullname || source.fullName)
    const email = normalizeText(source.email || source.gmail)
    const phone = normalizeText(source.phone).replace(/[\s.-]/g, '')
    const shippingAddress = normalizeText(source.shippingAddress || source.address)

    if (accountId === null) {
        throw new Error('Bạn cần đăng nhập để đặt hàng')
    }

    if (!fullName || fullName.length < 2) {
        throw new Error('Tên người nhận chưa hợp lệ')
    }

    if (email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
        throw new Error('Email liên hệ chưa hợp lệ')
    }

    if (!phone || !/^\+?[0-9]{9,15}$/.test(phone)) {
        throw new Error('Số điện thoại chưa hợp lệ')
    }

    if (!shippingAddress || shippingAddress.length < 5) {
        throw new Error('Địa chỉ giao hàng chưa đủ chi tiết')
    }
}

function extractOrderIdFromResponseHeaders(headers) {
    if (!headers || typeof headers.get !== 'function') {
        return null
    }

    const headerCandidates = [
        headers.get('x-order-id'),
        headers.get('X-Order-Id'),
        headers.get('order-id'),
        headers.get('Order-Id'),
        headers.get('location'),
        headers.get('Location'),
    ]

    for (const value of headerCandidates) {
        if (value === null || value === undefined) {
            continue
        }

        const normalized = String(value).trim()
        if (!normalized) {
            continue
        }

        const direct = Number(normalized)
        if (Number.isFinite(direct) && direct > 0) {
            return direct
        }

        const match = normalized.match(/\/(\d+)(?:\D*)$/)
        if (match && match[1]) {
            const parsed = Number(match[1])
            if (Number.isFinite(parsed) && parsed > 0) {
                return parsed
            }
        }
    }

    return null
}

function normalizeOrderItem(rawOrder) {
    const source = rawOrder && typeof rawOrder === 'object' ? rawOrder : {}

    const normalizedId = toFiniteNumber(
        source.id !== undefined && source.id !== null ? source.id :
        source.orderId !== undefined && source.orderId !== null ? source.orderId :
        source.order_id,
        null,
    )

    const createdAt =
        source.createdAt ||
        source.created_at ||
        source.orderDate ||
        source.order_date ||
        null

    const finalAmount = toFiniteNumber(
        source.finalAmount !== undefined && source.finalAmount !== null ? source.finalAmount :
        source.final_amount !== undefined && source.final_amount !== null ? source.final_amount :
        source.totalAmount !== undefined && source.totalAmount !== null ? source.totalAmount :
        source.total_amount !== undefined && source.total_amount !== null ? source.total_amount :
        source.totalMoney !== undefined && source.totalMoney !== null ? source.totalMoney :
        source.total_money,
        0,
    )

    const paymentStatus =
        source.paymentStatus ||
        source.payment_status ||
        source.paymentMethod ||
        source.payment_method ||
        'PENDING'

    const status =
        source.status ||
        source.orderStatus ||
        source.order_status ||
        'PENDING'

    return {
        ...source,
        id: normalizedId,
        createdAt,
        finalAmount,
        paymentStatus: normalizePaymentStatus(paymentStatus),
        status: normalizeOrderStatus(status),
    }
}

function normalizeOrderListPayload(payload) {
    const source = payload && typeof payload === 'object' ? payload : null
    const list =
        source && Array.isArray(source.content) ? source.content :
        source && Array.isArray(source.data) ? source.data :
        Array.isArray(payload) ? payload : []

    return list
        .map(normalizeOrderItem)
        .sort((a, b) => {
            const timeA = new Date(a.createdAt || 0).getTime()
            const timeB = new Date(b.createdAt || 0).getTime()
            if (timeA !== timeB) {
                return timeB - timeA
            }
            return toFiniteNumber(b.id, 0) - toFiniteNumber(a.id, 0)
        })
}

function filterOrdersByAccountId(orderList, accountId) {
    return (Array.isArray(orderList) ? orderList : []).filter(order => {
        const source = order && typeof order === 'object' ? order : {}
        const normalizedAccountId = toFiniteNumber(
            source.accountId !== undefined && source.accountId !== null ? source.accountId : source.account_id,
            null,
        )
        return normalizedAccountId === accountId
    })
}

function normalizeOrderDetailPayload(payload) {
    const source = payload && typeof payload === 'object' ? payload : null
    const detail =
        source && source.data && typeof source.data === 'object' ? source.data :
        source && source.content && typeof source.content === 'object' ? source.content :
        source || {}

    const normalized = normalizeOrderItem(detail)
    const orderItems =
        Array.isArray(detail.orderItems) ? detail.orderItems :
        Array.isArray(detail.order_items) ? detail.order_items :
        Array.isArray(detail.items) ? detail.items :
        Array.isArray(detail.orderDetails) ? detail.orderDetails :
        Array.isArray(detail.order_details) ? detail.order_details : []

    return {
        ...detail,
        ...normalized,
        orderItems,
    }
}

// Cart Operations
export const cartAPI = {
    /**
     * Get all cart items for a user
     * @param {number} accountId - User ID
     * @returns {Promise} Response with cart items and total
     */
    async getCart(accountId) {
        const response = await fetch(`${API_BASE_URL}/cart?accountId=${accountId}`, {
            headers: getAuthHeaders()
        });

        let data = null;
        try {
            data = await response.json();
        } catch {
            data = null;
        }

        if (!response.ok) {
            const errorMsg = (data && (data.message || data.error)) || 'Failed to fetch cart';
            throw new Error(errorMsg);
        }

        return data;
    },

    /**
     * Add item to cart or update quantity
     * @param {number} accountId - User ID
     * @param {number} variantId - Product variant ID
     * @param {number} quantity - Quantity to add
     * @returns {Promise}
     */
    async addToCart(accountId, variantId, quantity) {
        const response = await fetch(`${API_BASE_URL}/cart/add`, {
            method: 'POST',
            headers: getAuthHeaders({ 'Content-Type': 'application/json' }),
            body: JSON.stringify({ accountId, variantId, quantity })
        });

        let data = null;
        try {
            data = await response.json();
        } catch {
            data = null;
        }

        if (!response.ok) {
            const errorMsg = (data && (data.message || data.error)) || 'Failed to add to cart';
            throw new Error(errorMsg);
        }

        return data;
    },

    /**
     * Update cart item quantity
     * @param {number} cartItemId - Cart item ID
     * @param {number} quantity - New quantity
     * @returns {Promise}
     */
    async updateCartItem(cartItemId, quantity) {
        const response = await fetch(`${API_BASE_URL}/cart/update/${cartItemId}`, {
            method: 'PUT',
            headers: getAuthHeaders({ 'Content-Type': 'application/json' }),
            body: JSON.stringify({ quantity })
        });
        if (!response.ok) throw new Error('Failed to update cart');
        return response.json();
    },

    /**
     * Remove item from cart
     * @param {number} cartItemId - Cart item ID
     * @returns {Promise}
     */
    async removeFromCart(cartItemId) {
        const response = await fetch(`${API_BASE_URL}/cart/remove/${cartItemId}`, {
            method: 'DELETE',
            headers: getAuthHeaders()
        });

        let data = null;
        try {
            data = await response.json();
        } catch {
            data = null;
        }

        if (!response.ok) {
            const errorMsg = (data && (data.message || data.error)) || 'Failed to remove item';
            throw new Error(errorMsg);
        }

        return data;
    },

    /**
     * Clear entire cart
     * @param {number} accountId - User ID
     * @returns {Promise}
     */
    async clearCart(accountId) {
        const response = await fetch(`${API_BASE_URL}/cart/clear?accountId=${accountId}`, {
            method: 'DELETE',
            headers: getAuthHeaders()
        });
        if (!response.ok) throw new Error('Failed to clear cart');
        return response.json();
    }
};

// Order Operations
export const orderAPI = {
    /**
     * Place order for authenticated user (items from cart)
     * @param {object} orderData - Order details
     * @returns {Promise}
     */
    async placeOrder(orderData) {
        validatePlaceOrderPayload(orderData)
        const response = await fetch(`${API_BASE_URL}/orders`, {
            method: 'POST',
            headers: getAuthHeaders({ 'Content-Type': 'application/json' }),
            body: JSON.stringify(orderData)
        });

        const data = await parseJsonSafe(response);

        if (!response.ok) {
            const errorMsg = typeof data === 'string' ? data : (data && (data.message || data.error)) || `Failed to place order (HTTP ${response.status})`;
            throw new Error(errorMsg);
        }

        const idFromHeaders = extractOrderIdFromResponseHeaders(response.headers)
        if (data && typeof data === 'object') {
            const candidateId = toFiniteNumber(
                data.id !== undefined && data.id !== null ? data.id :
                data.orderId !== undefined && data.orderId !== null ? data.orderId :
                data.order_id,
                null,
            )

            if (candidateId !== null || idFromHeaders === null) {
                return data
            }

            return {
                ...data,
                id: idFromHeaders,
                orderId: idFromHeaders,
            }
        }

        if (idFromHeaders !== null) {
            return {
                id: idFromHeaders,
                orderId: idFromHeaders,
            }
        }

        return data;
    },

    /**
     * Place guest order
     * @param {object} orderData - Order details with orderItems array
     * @returns {Promise}
     */
    async placeGuestOrder(orderData) {
        return this.placeOrder(orderData);
    },

    /**
     * Get orders by account id with backend endpoint fallback.
     * @param {number|string} accountId - User account ID
     * @returns {Promise<Array>}
     */
    async getUserOrders(accountId) {
        const normalizedAccountId = toFiniteNumber(accountId, null)
        if (normalizedAccountId === null) {
            throw new Error('Thiếu accountId hợp lệ để tải đơn hàng')
        }

        const endpoints = [
            `${API_BASE_URL}/orders/account/${normalizedAccountId}`,
            `${API_BASE_URL}/orders?accountId=${normalizedAccountId}`,
            `${API_BASE_URL}/orders/my-orders?accountId=${normalizedAccountId}`,
        ]

        let lastError = null

        for (const endpoint of endpoints) {
            const response = await fetch(endpoint, { headers: getAuthHeaders() })
            const data = await parseJsonSafe(response)

            if (response.ok) {
                const normalizedList = normalizeOrderListPayload(data)

                if (endpoint.indexOf('/orders/account/') !== -1) {
                    return normalizedList
                }

                return filterOrdersByAccountId(normalizedList, normalizedAccountId)
            }

            const errorMsg = (data && (data.message || data.error)) || `HTTP ${response.status}`
            lastError = new Error(errorMsg)

            if (response.status === 400 || response.status === 404) {
                continue
            }

            throw lastError
        }

        throw lastError || new Error('Không tải được danh sách đơn hàng')
    },

    /**
     * Cancel a waiting order.
     * @param {number|string} orderId - Order ID
     * @returns {Promise<object>}
     */
    async cancelOrder(orderId) {
        const normalizedOrderId = toFiniteNumber(orderId, null)
        if (normalizedOrderId === null) {
            throw new Error('Thiếu orderId hợp lệ để hủy đơn hàng')
        }

        const attempts = [{
                url: `${API_BASE_URL}/orders/${normalizedOrderId}`,
                method: 'PUT',
                body: { status: 'CANCELLED' },
            },
            {
                url: `${API_BASE_URL}/orders/${normalizedOrderId}`,
                method: 'PATCH',
                body: { status: 'CANCELLED' },
            },
            { url: `${API_BASE_URL}/orders/${normalizedOrderId}/cancel`, method: 'PUT' },
            { url: `${API_BASE_URL}/orders/${normalizedOrderId}/cancel`, method: 'PATCH' },
            { url: `${API_BASE_URL}/orders/${normalizedOrderId}/cancel`, method: 'POST' },
            { url: `${API_BASE_URL}/orders/cancel/${normalizedOrderId}`, method: 'POST' },
            { url: `${API_BASE_URL}/orders/cancel?orderId=${normalizedOrderId}`, method: 'POST' },
        ]

        let lastError = null

        for (const attempt of attempts) {
            let response
            let data

            try {
                const requestOptions = {
                    method: attempt.method,
                    headers: getAuthHeaders({ 'Content-Type': 'application/json' }),
                }

                if (attempt.body && typeof attempt.body === 'object') {
                    requestOptions.body = JSON.stringify(attempt.body)
                }

                response = await fetch(attempt.url, requestOptions)
                data = await parseJsonSafe(response)
            } catch (error) {
                lastError = error instanceof Error ? error : new Error('Network error while cancelling order')
                continue
            }

            if (response.ok) {
                if (data && typeof data === 'object') {
                    return normalizeOrderDetailPayload(data)
                }

                return normalizeOrderDetailPayload({
                    id: normalizedOrderId,
                    status: 'CANCELLED',
                })
            }

            const errorMsg = (data && (data.message || data.error)) || `HTTP ${response.status}`
            lastError = new Error(errorMsg)

            if (response.status === 400 || response.status === 404 || response.status === 405) {
                continue
            }

            throw lastError
        }

        throw lastError || new Error('Không thể hủy đơn hàng')
    },

    async updateOrder(orderId, payload) {
        const response = await fetch(`${API_BASE_URL}/orders/${orderId}`, {
            method: 'PUT', // Nếu backend dùng PATCH thì bạn đổi thành PATCH nhé
            headers: getAuthHeaders({ 'Content-Type': 'application/json' }),
            body: JSON.stringify(payload)
        });

        let data = null;
        try {
            data = await response.json();
        } catch {
            data = null;
        }

        if (!response.ok) {
            const errorMsg = (data && (data.message || data.error)) || `Lỗi cập nhật (HTTP ${response.status})`;
            throw new Error(errorMsg);
        }

        return data;
    },

    /**
     * Confirm that a shipping order has been received.
     * @param {number|string} orderId - Order ID
     * @returns {Promise<object>}
     */
    async confirmOrderReceived(orderId) {
        const normalizedOrderId = toFiniteNumber(orderId, null)
        if (normalizedOrderId === null) {
            throw new Error('Thiếu orderId hợp lệ để xác nhận nhận hàng')
        }

        const statusCandidates = ['SUCCESS', 'DELIVERED', 'COMPLETED', 'RECEIVED']
        const methods = ['PUT', 'PATCH']
        const requestBodies = statusCandidates.flatMap(status => ([
            { status, paymentStatus: 'PAID' },
            { status, payment_status: 'PAID' },
            { status },
        ]))
        let lastError = null

        for (const body of requestBodies) {
            for (const method of methods) {
                let response
                let data

                try {
                    response = await fetch(`${API_BASE_URL}/orders/${normalizedOrderId}`, {
                        method,
                        headers: getAuthHeaders({ 'Content-Type': 'application/json' }),
                        body: JSON.stringify(body),
                    })
                    data = await parseJsonSafe(response)
                } catch (error) {
                    lastError = error instanceof Error ? error : new Error('Network error while confirming order delivery')
                    continue
                }

                if (response.ok) {
                    if (data && typeof data === 'object') {
                        const normalized = normalizeOrderDetailPayload(data)
                        return {
                            ...normalized,
                            paymentStatus: 'PAID',
                        }
                    }

                    return normalizeOrderDetailPayload({
                        id: normalizedOrderId,
                        status: normalizeOrderStatus(body.status),
                        paymentStatus: 'PAID',
                    })
                }

                const errorMsg = (data && (data.message || data.error)) || `HTTP ${response.status}`
                lastError = new Error(errorMsg)

                if (response.status === 400 || response.status === 404 || response.status === 405) {
                    continue
                }

                throw lastError
            }
        }

        throw lastError || new Error('Không thể xác nhận đã nhận hàng')
    },



    /**
     * Get order details
     * @param {number} orderId - Order ID
     * @returns {Promise}
     */
    async getOrder(orderId) {
        const response = await fetch(`${API_BASE_URL}/orders/${orderId}`, {
            headers: getAuthHeaders()
        });
        const data = await parseJsonSafe(response)

        if (!response.ok) {
            const errorMsg = (data && (data.message || data.error)) || 'Failed to fetch order'
            throw new Error(errorMsg)
        }

        return normalizeOrderDetailPayload(data)
    }

};

// Voucher Operations
export const voucherAPI = {
    /**
     * Get all vouchers (shared page for all accounts)
     * @returns {Promise}
     */
    async getAllVouchers(page = 0, size = 100, sortBy = 'createdAt', direction = 'DESC') {
        const query = new URLSearchParams({
            page: String(page),
            size: String(size),
            sortBy,
            direction,
        })
        const response = await fetch(`${API_BASE_URL}/vouchers?${query.toString()}`);

        let data;
        try {
            data = await response.json();
        } catch {
            throw new Error(`Server error (${response.status}): Invalid response`);
        }

        if (!response.ok) {
            const errorMsg = (data && (data.message || data.error)) || `HTTP ${response.status}`;
            throw new Error(errorMsg);
        }

        return data;
    },

    async findVoucherByCode(code) {
        const normalizedCode = String(code || '').trim().toLowerCase()
        if (!normalizedCode) {
            return null
        }

        const response = await this.getAllVouchers(0, 100, 'createdAt', 'DESC')
        const responseData = response && response.data
        const voucherList = Array.isArray(responseData) ?
            responseData :
            Array.isArray(response) ?
            response : []

        return voucherList.find(voucher => String((voucher && voucher.code) || '').trim().toLowerCase() === normalizedCode) || null
    },

    /**
     * Apply voucher code
     * @param {string} code - Voucher code
     * @param {number} orderTotal - Order total
     * @returns {Promise}
     */
    async applyVoucher(code, orderTotal, accountId = null, phone = null) {
        const params = new URLSearchParams({
            code,
            orderTotal: String(orderTotal),
        })
        if (accountId != null && accountId !== '') {
            params.set('accountId', String(accountId))
        }
        if (phone != null && phone !== '') {
            params.set('phone', String(phone))
        }

        const response = await fetch(
            `${API_BASE_URL}/vouchers/apply?${params.toString()}`
        );

        let data;
        try {
            data = await response.json();
        } catch {
            throw new Error(`Server error (${response.status}): Invalid response`);
        }

        if (!response.ok) {
            const errorMsg = (data && (data.message || data.error)) || `HTTP ${response.status}`;
            throw new Error(errorMsg);
        }

        const normalized = normalizeAppliedVoucher((data && data.data) || data, code)
        const numericTotal = Number(orderTotal)

        if (normalized.minOrderValue > 0 && Number.isFinite(numericTotal) && numericTotal < normalized.minOrderValue) {
            throw new Error(`Đơn tối thiểu ${normalized.minOrderValue.toLocaleString('vi-VN')}đ mới áp dụng được mã này`)
        }

        if (normalized.id === null) {
            try {
                const matchedVoucher = await this.findVoucherByCode(normalized.code || code)
                if (matchedVoucher) {
                    normalized.id = Number(matchedVoucher.id)
                    if (!normalized.discountType) {
                        normalized.discountType = matchedVoucher.discountType || matchedVoucher.discount_type || ''
                    }
                    if (!normalized.discountValue) {
                        normalized.discountValue = Number(matchedVoucher.discountValue || matchedVoucher.discount_value || 0)
                    }
                    if (!normalized.minOrderValue) {
                        normalized.minOrderValue = Number(matchedVoucher.minOrderValue || matchedVoucher.min_order_value || 0)
                    }
                    if (normalized.maxDiscountAmount == null) {
                        const maxCap = matchedVoucher.maxDiscountAmount ?? matchedVoucher.max_discount_amount
                        if (maxCap != null) {
                            normalized.maxDiscountAmount = Number(maxCap)
                        }
                    }
                }
            } catch {
                // Leave voucher id unresolved if lookup fails; caller can still use code and discount amount.
            }
        }

        return normalized;
    },

    /**
     * Record voucher usage — call after a successful order placement.
     * Increments usedCount on the backend (POST /api/vouchers/use).
     * @param {string} code - Voucher code
     * @param {number} orderTotal - Finalised order total
     * @returns {Promise}
     */
    async useVoucher(code, orderTotal) {
        const response = await fetch(
            `${API_BASE_URL}/vouchers/use?code=${encodeURIComponent(code)}&orderTotal=${orderTotal}`, { method: 'POST' }
        );

        let data;
        try {
            data = await response.json();
        } catch {
            // Non-critical — swallow parse errors so order flow isn't interrupted
            return null;
        }

        return data;
    },

    /**
     * Get user's available vouchers
     * @param {number} accountId - User ID
     * @returns {Promise}
     */
    async getMyVouchers(accountId) {
        const response = await fetch(`${API_BASE_URL}/vouchers/my-vouchers?accountId=${accountId}`);

        let data;
        try {
            data = await response.json();
        } catch {
            throw new Error(`Server error (${response.status}): Invalid response`);
        }

        if (!response.ok) {
            const errorMsg = (data && (data.message || data.error)) || `HTTP ${response.status}`;
            throw new Error(errorMsg);
        }

        return data;
    }
};

// Payment Methods
export const paymentAPI = {
    /**
     * Get available payment methods
     * @returns {Promise}
     */
    async getPaymentMethods() {
        // This would call your actual API endpoint
        // For now returning common payment methods
        return {
            success: true,
            methods: [
                { id: 1, name: 'Cash On Delivery' },
                { id: 2, name: 'VNPay' },
                { id: 3, name: 'Momo' },
                { id: 4, name: 'Bank Transfer' },
                { id: 5, name: 'ZaloPay' }
            ]
        };
    }
};

// Utility Functions
export const orderUtils = {
    /**
     * Format price to Vietnamese currency
     * @param {number} price - Price value
     * @returns {string} Formatted price
     */
    formatPrice(price) {
        return new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND'
        }).format(price);
    },

    /**
     * Calculate cart total from items
     * @param {array} items - Cart items
     * @returns {number} Total price
     */
    calculateTotal(items) {
        return items.reduce((sum, item) => sum + (item.price * item.quantity), 0);
    }
};
