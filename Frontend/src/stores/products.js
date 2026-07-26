/**
 * Product Store - Manages all product, brand, category, and variant data
 */
import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import {
    brandsApi,
    categoriesApi,
    productsApi,
    resolveImageUrl,
    variantsApi,
} from '@/services/api'
import {
    resolveColorFromVariant,
    resolveSizeFromVariant,
    toVariantId,
} from '@/utils/variantValues'

export const useProductStore = defineStore('products', () => {
    // State
    const products = ref([])
    const brands = ref([])
    const categories = ref([])
    const variants = ref([])

    const loading = ref(false)
    const error = ref(null)

    function dedupeById(list) {
        const arr = Array.isArray(list) ? list : []
        const map = new Map()
        for (const item of arr) {
            if (!item) {
                continue
            }
            const id = item.id
            if (id === null || id === undefined) {
                continue
            }
            map.set(Number(id), item)
        }
        return [...map.values()]
    }

    // Brand and Category lookup maps for quick access
    const brandMap = computed(() => {
        return brands.value.reduce((map, brand) => {
            map[brand.id] = brand
            return map
        }, {})
    })

    const categoryMap = computed(() => {
        return categories.value.reduce((map, cat) => {
            map[cat.id] = cat
            return map
        }, {})
    })

    const enrichCategory = category => {
        if (!category) {
            return category
        }
        return {
            ...category,
            imageUrl: resolveImageUrl(category.imageUrl || category.image_url),
        }
    }

    // Helper function to enrich products with brand and category info
    const enrichProduct = product => {
        // Normalize field names (handle both camelCase and snake_case)
        const normalizedProduct = {
            ...product,
            imageUrl: resolveImageUrl(product.imageUrl || product.image_url),
            categoryId: product.categoryId || product.category_id,
            brandId: product.brandId || product.brand_id,
            productCode: product.productCode || product.product_code,
            brand: brandMap.value[product.brandId || product.brand_id],
            category: categoryMap.value[product.categoryId || product.category_id],
        }
        return normalizedProduct
    }

    // Helper function to enrich variants with product and color/size info
    const enrichVariant = variant => {
        const productId = toVariantId(variant.productId || variant.product_id)
        const product = products.value.find(p => p.id === productId)
        const colorMeta = resolveColorFromVariant(variant)
        const sizeMeta = resolveSizeFromVariant(variant)
        const variantColor = variant && variant.color ? variant.color : null
        const variantSize = variant && variant.size ? variant.size : null
        const variantColorId =
            colorMeta.id !== null ?
            colorMeta.id :
            (variantColor && variantColor.id !== undefined && variantColor.id !== null) ?
            variantColor.id :
            null
        const variantColorName = (variantColor && variantColor.name) || colorMeta.name
        const variantColorHex =
            (variantColor && (variantColor.hexCode || variantColor.hex_code || variantColor.hexCode1)) ||
            colorMeta.hexCode
        const variantSizeId =
            sizeMeta.id !== null ?
            sizeMeta.id :
            (variantSize && variantSize.id !== undefined && variantSize.id !== null) ?
            variantSize.id :
            null
        const variantSizeName = (variantSize && variantSize.name) || sizeMeta.name
        const stockValue =
            (variant && variant.stockQuantity !== undefined && variant.stockQuantity !== null) ?
            variant.stockQuantity :
            (variant && variant.stock_quantity !== undefined && variant.stock_quantity !== null) ?
            variant.stock_quantity :
            (variant && variant.quantity !== undefined && variant.quantity !== null) ?
            variant.quantity :
            0
        const rawStatus = String(
            (variant && (variant.status || variant.variantStatus || variant.variant_status)) || '',
        ).trim().toUpperCase()
        const knownStock = stockValue !== null && stockValue !== undefined && stockValue !== ''
        const hasStock = knownStock ? Number(stockValue) > 0 : true
        const variantStatus =
            ['SELLING', 'AVAILABLE', 'ACTIVE', 'ON_SALE'].includes(rawStatus) ?
            (hasStock ? 'SELLING' : 'OUT_OF_STOCK') :
            ['OUT_OF_STOCK', 'SOLD_OUT', 'OUTOFSTOCK'].includes(rawStatus) ?
            'OUT_OF_STOCK' :
            ['HIDDEN', 'INACTIVE', 'DISCONTINUED', 'STOP_SELLING'].includes(rawStatus) ?
            'HIDDEN' :
            (hasStock ? 'SELLING' : 'OUT_OF_STOCK')

        // Normalize field names (handle both camelCase and snake_case)
        const normalizedVariant = {
            ...variant,
            imageUrl: resolveImageUrl(variant.imageUrl || variant.image_url),
            extraImages: (Array.isArray(variant.extraImages) ? variant.extraImages : [])
                .map(url => resolveImageUrl(url))
                .filter(Boolean),
            productId,
            colorId: colorMeta.id,
            sizeId: sizeMeta.id,
            colorName: colorMeta.name,
            sizeName: sizeMeta.name,
            colorHexCode: colorMeta.hexCode,
            color: colorMeta.name ? {
                ...(variantColor || {}),
                id: variantColorId,
                name: variantColorName,
                hexCode: variantColorHex,
            } : variantColor,
            size: sizeMeta.name ? {
                ...(variantSize || {}),
                id: variantSizeId,
                name: variantSizeName,
            } : variantSize,
            stockQuantity: stockValue,
            status: variantStatus,
            product,
            brand: product ? brandMap.value[product.brandId] : null,
        }
        return normalizedVariant
    }

    // Actions to fetch data
    async function fetchBrands() {
        try {
            loading.value = true
            error.value = null
                // Map backend field names to frontend expectations
            const data = await brandsApi.getAll()
                // Handle both camelCase (logoUrl) and snake_case (logo_url) responses
            brands.value = dedupeById(Array.isArray(data.list) ? data.list : data)
        } catch (error_) {
            error.value = error_.message
            console.error('Error fetching brands:', error_)
        } finally {
            loading.value = false
        }
    }

    async function fetchCategories() {
        try {
            loading.value = true
            error.value = null
            const data = await categoriesApi.getAll()
            const list = Array.isArray(data.list) ? data.list : data
            categories.value = dedupeById(list.map(enrichCategory))
        } catch (error_) {
            error.value = error_.message
            console.error('Error fetching categories:', error_)
        } finally {
            loading.value = false
        }
    }

    async function fetchProducts(page = 0, size = 100) {
        try {
            loading.value = true
            error.value = null
            const data = await productsApi.getAll(page, size)
                // Handle Spring Boot Page response with content array
            const productList = data.content || data.list || (Array.isArray(data) ? data : [])
            products.value = dedupeById(productList.map(enrichProduct))
        } catch (error_) {
            error.value = error_.message
            console.error('Error fetching products:', error_)
        } finally {
            loading.value = false
        }
    }

    async function fetchVariants(page = 0, size = 100) {
        try {
            loading.value = true
            error.value = null
            const data = await variantsApi.getAll(page, size)
                // Handle Spring Boot Page response with content array
            const variantList = data.content || data.list || (Array.isArray(data) ? data : [])
            variants.value = dedupeById(variantList.map(enrichVariant))
        } catch (error_) {
            error.value = error_.message
            console.error('Error fetching variants:', error_)
        } finally {
            loading.value = false
        }
    }

    async function fetchProductById(id) {
        try {
            loading.value = true
            error.value = null
            const product = await productsApi.getById(id)
                // Ensure we have enriched data from other stores
            return enrichProduct(product)
        } catch (error_) {
            error.value = error_.message
            console.error('Error fetching product:', error_)
            return null
        } finally {
            loading.value = false
        }
    }

    async function fetchVariantsByProductId(productId) {
        try {
            loading.value = true
            error.value = null
            const data = await variantsApi.getByProductId(productId)
            const rawList =
                data?.content ??
                data?.list ??
                (Array.isArray(data) ? data : [])
            const normalized = rawList.map(enrichVariant)
            const id = Number(productId)
            const fromApi = normalized.filter(v => Number(v.productId) === id)
            if (fromApi.length > 0) {
                return fromApi
            }
            return variants.value.filter(v => Number(v.productId) === id)
        } catch (error_) {
            error.value = error_.message
            console.error('Error fetching variants:', error_)
            const id = Number(productId)
            return variants.value.filter(v => Number(v.productId) === id)
        } finally {
            loading.value = false
        }
    }

    async function fetchProductsByCategory(categoryId) {
        try {
            loading.value = true
            error.value = null
            const data = await productsApi.getByCategory(categoryId)
            return (Array.isArray(data.list) ? data.list : data).map(enrichProduct)
        } catch (error_) {
            error.value = error_.message
            console.error('Error fetching products by category:', error_)
            return []
        } finally {
            loading.value = false
        }
    }

    async function fetchProductsByBrand(brandId) {
        try {
            loading.value = true
            error.value = null
            const data = await productsApi.getByBrand(brandId)
            return (Array.isArray(data.list) ? data.list : data).map(enrichProduct)
        } catch (error_) {
            error.value = error_.message
            console.error('Error fetching products by brand:', error_)
            return []
        } finally {
            loading.value = false
        }
    }

    // Initialize all data (call this on app startup)
    async function initializeStore() {
        try {
            loading.value = true
            await Promise.all([
                fetchBrands(),
                fetchCategories(),
                fetchProducts(),
                fetchVariants(),
            ])
        } catch (error_) {
            error.value = error_.message
            console.error('Error initializing store:', error_)
        } finally {
            loading.value = false
        }
    }

    // Getters
    const getProductById = id => {
        return products.value.find(p => p.id === Number.parseInt(id))
    }

    const getVariantById = id => {
        return variants.value.find(v => v.id === Number.parseInt(id))
    }

    const getBrandById = id => {
        return brands.value.find(b => b.id === Number.parseInt(id))
    }

    const getCategoryById = id => {
        return categories.value.find(c => c.id === Number.parseInt(id))
    }

    const getProductsByBrandId = brandId => {
        return products.value.filter(p => p.brandId === Number.parseInt(brandId))
    }

    const getProductsByCategoryId = categoryId => {
        return products.value.filter(p => p.categoryId === Number.parseInt(categoryId))
    }

    const getVariantsByProductId = productId => {
        return variants.value.filter(v => v.productId === Number.parseInt(productId))
    }

    const getFeaturedProducts = (limit = 6) => {
        return products.value.slice(0, limit)
    }

    return {
        // State
        products,
        brands,
        categories,
        variants,
        loading,
        error,

        // Actions
        fetchBrands,
        fetchCategories,
        fetchProducts,
        fetchVariants,
        fetchProductById,
        fetchVariantsByProductId,
        fetchProductsByCategory,
        fetchProductsByBrand,
        initializeStore,

        // Getters
        getProductById,
        getVariantById,
        getBrandById,
        getCategoryById,
        getProductsByBrandId,
        getProductsByCategoryId,
        getVariantsByProductId,
        getFeaturedProducts,
    }
})
