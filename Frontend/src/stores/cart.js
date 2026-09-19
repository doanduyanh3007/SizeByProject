/**
 * Cart Store - Manages shopping cart state and operations
 */
import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import { cartAPI, orderUtils } from '@/services/orders'
import { resolveImageUrl } from '@/services/api'
import {
    resolveColorFromVariant,
    resolveColorMeta,
    resolveSizeFromVariant,
    resolveSizeMeta,
} from '@/utils/variantValues'

export const useCartStore = defineStore('cart', () => {
    // State
    const cartItems = ref([])
    const accountId = ref(null)
    const loading = ref(false)
    const error = ref(null)

    // Computed properties
    const cartCount = computed(() => {
        return cartItems.value.reduce((total, item) => total + item.quantity, 0)
    })

    const cartTotal = computed(() => {
        return cartItems.value.reduce((total, item) => {
            return total + (item.price * item.quantity)
        }, 0)
    })

    const isEmpty = computed(() => cartItems.value.length === 0)

    function setLoginRequiredError() {
        error.value = 'Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng'
    }

    function parseAccountId(rawValue) {
        const source = rawValue && typeof rawValue === 'object' ? rawValue : null
        let candidate = rawValue

        if (source) {
            if (source.accountId !== undefined && source.accountId !== null) {
                candidate = source.accountId
            } else if (source.account_id !== undefined && source.account_id !== null) {
                candidate = source.account_id
            } else {
                candidate = source.id
            }
        }

        const n = Number(candidate)
        return Number.isFinite(n) && n > 0 ? n : null
    }

    function getStoredAccountId() {
        try {
            const rawUser = localStorage.getItem('user')
            if (!rawUser) {
                return null
            }

            const parsedUser = JSON.parse(rawUser)
            return parseAccountId(parsedUser)
        } catch {
            return null
        }
    }

    function normalizeCartImage(rawImage) {
        if (!rawImage) {
            return rawImage
        }

        let image = String(rawImage).trim()
        if (!image) {
            return image
        }

        const isAbsolute = /^https?:\/\//i.test(image)
        const isDataUrl = image.indexOf('data:') === 0
        const hasPath = image.indexOf('/') !== -1

        if (!isAbsolute && !isDataUrl) {
            if (!hasPath) {
                image = `/images/${image}`
            } else if (image.indexOf('images/') === 0) {
                image = `/${image}`
            }
        }

        return resolveImageUrl(image)
    }

    function normalizeProductCode(rawCode, productId) {
        if (rawCode === null || rawCode === undefined) {
            return null
        }

        const code = String(rawCode).trim()
        if (!code) {
            return null
        }

        return code
    }

    function normalizeCartItem(item) {
        const productId = item.productId || item.product_id || null
        const rawCode =
            item.productCode ||
            item.product_code ||
            item.code ||
            (item.product && (item.product.productCode || item.product.product_code || item.product.code)) ||
            null
        const rawName =
            item.productName ||
            item.product_name ||
            item.name ||
            item.variantName ||
            item.variant_name ||
            item.title ||
            (item.product && (item.product.name || item.product.productName || item.product.product_name || item.product.title)) ||
            (item.variant && (item.variant.name || item.variant.productName || item.variant.product_name || item.variant.title)) ||
            null
        const normalizedName = rawName === null || rawName === undefined ? '' : String(rawName).trim()
        const sizeSource = item.size && typeof item.size === 'object' ? item.size : null
        const colorSource = item.color && typeof item.color === 'object' ? item.color : null
        const rawImage =
            item.imageUrl ||
            item.image_url ||
            item.image ||
            (item.variant && (item.variant.imageUrl || item.variant.image_url)) ||
            (item.product && (item.product.imageUrl || item.product.image_url)) ||
            null
        const stock = item.stockAvailable || item.stock_available || item.stockQuantity || item.stock_quantity || 0
        const sizeMeta = resolveSizeMeta({
            id: item.sizeId || item.size_id || (sizeSource && sizeSource.id),
            name: item.sizeName ||
                item.size_name ||
                (typeof item.size === 'string' ? item.size : null) ||
                (sizeSource && sizeSource.name),
        })
        const colorMeta = resolveColorMeta({
            id: item.colorId || item.color_id || (colorSource && colorSource.id),
            name: item.colorName ||
                item.color_name ||
                (typeof item.color === 'string' ? item.color : null) ||
                (colorSource && colorSource.name),
            hexCode: item.colorHexCode ||
                item.color_hex_code ||
                (colorSource && (colorSource.hexCode || colorSource.hex_code)),
        })

        return {
            ...item,
            id: item.id || item.cartItemId,
            cartItemId: item.cartItemId || item.id,
            productId,
            productName: normalizedName || 'Sản phẩm',
            productCode: normalizeProductCode(rawCode, productId),
            imageUrl: normalizeCartImage(rawImage),
            sizeId: sizeMeta.id,
            sizeName: sizeMeta.name,
            colorId: colorMeta.id,
            colorName: colorMeta.name,
            colorHexCode: colorMeta.hexCode,
            stockAvailable: stock,
            stockQuantity: stock,
        }
    }

    function addToCartLocal(product, variant, quantity = 1) {
        const existingItem = cartItems.value.find(
            item => item.variantId === variant.id,
        )

        if (existingItem) {
            existingItem.quantity += quantity
        } else {
            const localProductId = (product && product.id) || null
            const localCodeRaw = (product && (product.productCode || product.product_code || product.code)) || null
            const colorMeta = resolveColorFromVariant(variant)
            const sizeMeta = resolveSizeFromVariant(variant)
            // Mục mới thêm luôn đứng đầu danh sách giỏ hàng.
            cartItems.value.unshift({
                id: Date.now(),
                productId: localProductId,
                productName: (product && product.name) || (variant && variant.productName) || 'Sản phẩm',
                productCode: normalizeProductCode(localCodeRaw, localProductId),
                variantId: variant.id,
                colorId: colorMeta.id,
                colorName: colorMeta.name,
                colorHexCode: colorMeta.hexCode,
                sizeId: sizeMeta.id,
                sizeName: sizeMeta.name,
                price: variant.price,
                imageUrl: normalizeCartImage(variant.imageUrl || (product && product.imageUrl)),
                quantity,
                stockQuantity: variant.stockQuantity,
                isLocalOnly: true,
            })
        }

        saveCart()
    }

    // ============================================
    // BACKEND CART FUNCTIONS
    // ============================================

    async function syncLocalItemsToBackend(normalizedAccountId, localItems) {
        const source = Array.isArray(localItems) ? localItems : []
        let syncedCount = 0

        for (const item of source) {
            const variantId = Number(item && item.variantId)
            if (!Number.isFinite(variantId) || variantId <= 0) {
                continue
            }

            const quantityRaw = Number(item && item.quantity)
            const quantity = Number.isFinite(quantityRaw) && quantityRaw > 0 ? Math.floor(quantityRaw) : 1

            try {
                await cartAPI.addToCart(normalizedAccountId, variantId, quantity)
                syncedCount += 1
            } catch (error_) {
                console.warn('Skip unsynced local cart item:', error_)
            }
        }

        return syncedCount > 0
    }

    async function loadCartFromBackend(id, options = {}) {
        const accountCandidate = id !== undefined && id !== null ? id : accountId.value
        const normalizedAccountId = parseAccountId(accountCandidate)
        if (normalizedAccountId === null) {
            return
        }

        const allowLocalSync = options.allowLocalSync !== false
        const localSnapshot = Array.isArray(cartItems.value) ? cartItems.value.map(normalizeCartItem) : []

        loading.value = true
        try {
            accountId.value = normalizedAccountId

            const response = await cartAPI.getCart(normalizedAccountId)
            const items = (response && response.items && Array.isArray(response.items)) ? response.items : []
            const normalizedRemoteItems = items
                .map(normalizeCartItem)
                // CartItem mới nhất đứng trước để sản phẩm cũ nằm phía dưới.
                .sort((a, b) => Number(b.id || b.cartItemId || 0) - Number(a.id || a.cartItemId || 0))

            if (normalizedRemoteItems.length === 0 && allowLocalSync) {
                const localOnlyItems = localSnapshot.filter(item => item && item.isLocalOnly)
                if (localOnlyItems.length > 0) {
                    const didSync = await syncLocalItemsToBackend(normalizedAccountId, localOnlyItems)
                    if (didSync) {
                        await loadCartFromBackend(normalizedAccountId, { allowLocalSync: false })
                        return
                    }

                    cartItems.value = localOnlyItems
                    saveCart()
                    error.value = null
                    return
                }
            }

            cartItems.value = normalizedRemoteItems
            saveCart()
            error.value = null
        } catch (err) {
            error.value = err.message
            console.error('Error loading cart:', err)

            if (localSnapshot.length > 0) {
                cartItems.value = localSnapshot
                saveCart()
            }
        } finally {
            loading.value = false
        }
    }

    async function addToCartBackend(variantId, quantity, fallbackProduct = null, fallbackVariant = null) {
        const normalizedAccountId = parseAccountId(accountId.value)
        if (normalizedAccountId === null) {
            setLoginRequiredError()
            return false
        }
        loading.value = true
        try {
            await cartAPI.addToCart(normalizedAccountId, variantId, quantity)
            await loadCartFromBackend(normalizedAccountId)
            error.value = null
            return true
        } catch (err) {
            error.value = err.message
            console.error('Error adding to cart:', err)
            return false
        } finally {
            loading.value = false
        }
    }

    async function updateCartItemBackend(cartItemId, quantity) {
        const normalizedAccountId = parseAccountId(accountId.value)
        if (normalizedAccountId === null) {
            updateCartItemQuantity(cartItemId, quantity)
            return
        }
        loading.value = true
        try {
            await cartAPI.updateCartItem(cartItemId, quantity)
            await loadCartFromBackend(normalizedAccountId)
            error.value = null
        } catch (err) {
            error.value = err.message
            console.error('Error updating cart:', err)
        } finally {
            loading.value = false
        }
    }

    async function removeFromCartBackend(cartItemId) {
        const normalizedAccountId = parseAccountId(accountId.value)
        if (normalizedAccountId === null) {
            const item = cartItems.value.find(i => i.id === cartItemId)
            if (item) removeFromCart(item.variantId)
            return
        }
        loading.value = true
        try {
            await cartAPI.removeFromCart(cartItemId)
            await loadCartFromBackend(normalizedAccountId)
            error.value = null
        } catch (err) {
            error.value = err.message
            console.error('Error removing from cart:', err)
        } finally {
            loading.value = false
        }
    }

    async function clearCartBackend() {
        const normalizedAccountId = parseAccountId(accountId.value)
        if (normalizedAccountId === null) {
            clearCart()
            return
        }
        loading.value = true
        try {
            await cartAPI.clearCart(normalizedAccountId)
            cartItems.value = []
            saveCart()
            error.value = null
        } catch (err) {
            error.value = err.message
            console.error('Error clearing cart:', err)
        } finally {
            loading.value = false
        }
    }

    // ============================================
    // LOCAL CART FUNCTIONS (Fallback)
    // ============================================

    function addToCart(product, variant, quantity = 1) {
        if (parseAccountId(accountId.value) !== null) {
            void addToCartBackend(variant.id, quantity, product, variant)
            return true
        }

        setLoginRequiredError()
        return false
    }

    function updateCartItemQuantity(variantId, quantity) {
        try {
            const item = cartItems.value.find(item => item.variantId === variantId)
            if (item) {
                if (quantity <= 0) {
                    removeFromCart(variantId)
                } else if (quantity <= item.stockQuantity) {
                    item.quantity = quantity
                    saveCart()
                } else {
                    error.value = 'Quantity exceeds available stock'
                }
            }
        } catch (error_) {
            error.value = error_.message
            console.error('Error updating cart item:', error_)
        }
    }

    function removeFromCart(variantId) {
        try {
            const index = cartItems.value.findIndex(item => item.variantId === variantId)
            if (index !== -1) {
                cartItems.value.splice(index, 1)
                saveCart()
            }
        } catch (error_) {
            error.value = error_.message
            console.error('Error removing from cart:', error_)
        }
    }

    function clearCart() {
        cartItems.value = []
        saveCart()
    }

    // LocalStorage operations
    function saveCart() {
        try {
            localStorage.setItem('cart', JSON.stringify(cartItems.value))
        } catch (error_) {
            console.error('Error saving cart to localStorage:', error_)
        }
    }

    function loadCart() {
        try {
            const savedCart = localStorage.getItem('cart')
            if (savedCart) {
                const parsed = JSON.parse(savedCart)
                const items = Array.isArray(parsed) ? parsed : []
                cartItems.value = items.map(normalizeCartItem)
            }
        } catch (error_) {
            console.error('Error loading cart from localStorage:', error_)
        }
    }

    // Initialize cart from localStorage
    loadCart()
    accountId.value = getStoredAccountId()

    if (accountId.value === null && cartItems.value.length > 0) {
        cartItems.value = []
        saveCart()
    }

    return {
        // State
        cartItems,
        accountId,
        loading,
        error,

        // Computed
        cartCount,
        cartTotal,
        isEmpty,

        // Local Actions
        addToCart,
        updateCartItemQuantity,
        removeFromCart,
        clearCart,
        saveCart,
        loadCart,

        // Backend Actions
        loadCartFromBackend,
        addToCartBackend,
        updateCartItemBackend,
        removeFromCartBackend,
        clearCartBackend,
    }
})
