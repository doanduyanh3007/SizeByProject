import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import { accountsApi, reviewsApi } from '@/services/api'

const STORAGE_KEY = 'reviews'

function firstDefined() {
    for (const value of arguments) {
        if (value !== undefined && value !== null) {
            return value
        }
    }
    return undefined
}

function loadReviews() {
    try {
        const raw = localStorage.getItem(STORAGE_KEY)
        let parsed = []
        if (raw) {
            parsed = JSON.parse(raw)
        }
        if (Array.isArray(parsed)) {
            return parsed
        }
        return []
    } catch {
        return []
    }
}

function persistReviews(list) {
    try {
        localStorage.setItem(STORAGE_KEY, JSON.stringify(list))
    } catch {
        // ignore
    }
}

function toList(data) {
    if (Array.isArray(data)) return data
    if (data && Array.isArray(data.content)) return data.content
    if (data && Array.isArray(data.list)) return data.list
    return []
}

function normalizeReview(item, fallback = {}) {
    const productRaw = firstDefined(
        item && item.productId,
        item && item.product_id,
        fallback.productId,
    )
    const accountRaw = firstDefined(
        item && item.accountId,
        item && item.account_id,
        fallback.accountId,
    )
    const ratingRaw = firstDefined(item && item.rating, fallback.rating, 0)

    const productId = Number(productRaw)
    const accountId = Number(accountRaw)
    const rating = Number(ratingRaw)

    const reviewId = firstDefined(item && item.id, fallback.id, Date.now())
    const commentRaw = firstDefined(item && item.comment, fallback.comment, '')
    const createdAtRaw = firstDefined(
        item && item.createdAt,
        item && item.created_at,
        fallback.createdAt,
        new Date().toISOString(),
    )

    return {
        id: reviewId,
        productId: Number.isFinite(productId) ? productId : null,
        accountId: Number.isFinite(accountId) ? accountId : null,
        rating: Number.isFinite(rating) ? rating : 0,
        comment: String(commentRaw).trim(),
        createdAt: createdAtRaw,
    }
}

export const useReviewsStore = defineStore('reviews', () => {
    const reviews = ref(loadReviews())
    const accountNameById = ref({})
    const loaded = ref(false)
    const loading = ref(false)

    const all = computed(() => reviews.value)

    const getByProductId = productId => {
        const id = Number(productId)
        if (!Number.isFinite(id)) {
            return []
        }
        return reviews.value.filter(r => r.productId === id)
    }

    const getByAccountId = accountId => {
        const id = Number(accountId)
        if (!Number.isFinite(id)) {
            return []
        }
        return reviews.value.filter(r => r.accountId === id)
    }

    const getAverageRating = productId => {
        const list = getByProductId(productId)
        if (list.length === 0) {
            return null
        }
        const total = list.reduce((sum, r) => sum + (r.rating || 0), 0)
        return total / list.length
    }

    const getAuthorName = review => {
        const rawAccountId = review && review.accountId
        const accountId = Number(rawAccountId)

        if (Number.isFinite(accountId) && accountNameById.value[accountId]) {
            return accountNameById.value[accountId]
        }
        if (review && review.author) return review.author
        if (Number.isFinite(accountId)) return `Account #${accountId}`
        return 'Anonymous'
    }

    async function fetchAccounts() {
        try {
            const response = await accountsApi.getAll()
            const list = toList(response)
            const map = {}
            for (const account of list) {
                if (!account || account.id === null || account.id === undefined) continue
                map[Number(account.id)] = account.username || account.gmail || `Account #${account.id}`
            }
            accountNameById.value = map
        } catch (error) {
            console.error('Error fetching accounts for review authors:', error)
        }
    }

    async function fetchReviews({ force = false } = {}) {
        if (loading.value) return
        if (loaded.value && !force) return

        loading.value = true
        try {
            const response = await reviewsApi.getAll(0, 200)
            const list = toList(response)
            reviews.value = list
                .map(item => normalizeReview(item))
                .filter(item => Number.isFinite(item.productId) && Number.isFinite(item.accountId))
            persistReviews(reviews.value)
            loaded.value = true
        } catch (error) {
            console.error('Error fetching product reviews:', error)
                // Keep existing cached reviews on failure.
        } finally {
            loading.value = false
        }
    }

    const getExistingReview = (productId, accountId) => {
        const pId = Number(productId)
        const aId = Number(accountId)
        if (!Number.isFinite(pId) || !Number.isFinite(aId)) {
            return null
        }
        return reviews.value.find(r => r.productId === pId && r.accountId === aId) || null
    }

    async function addReview({ productId, accountId, rating, comment }) {
        const normalizedPayload = normalizeReview({
            productId,
            accountId,
            rating,
            comment,
        }, {
            id: Date.now(),
        }, )

        if (!Number.isFinite(normalizedPayload.productId) || !Number.isFinite(normalizedPayload.accountId)) {
            return null
        }
        if (normalizedPayload.rating <= 0 || normalizedPayload.comment.length === 0) {
            return null
        }

        const requestData = {
            productId: normalizedPayload.productId,
            accountId: normalizedPayload.accountId,
            rating: normalizedPayload.rating,
            comment: normalizedPayload.comment,
        }

        try {
            const created = await reviewsApi.create(requestData)
            const createdReview = normalizeReview(created, normalizedPayload)
            reviews.value = [createdReview, ...reviews.value.filter(r => r.id !== createdReview.id)]
            persistReviews(reviews.value)
            return { success: true, review: reviews.value[0] }
        } catch (error) {
            console.error('Error creating review:', error)
                // Check if it's a duplicate error
            if (error.data && error.data.error === 'DUPLICATE_REVIEW') {
                return { success: false, error: 'DUPLICATE_REVIEW', existingReview: error.data.existingReview }
            }
            return { success: false, error: 'UNKNOWN_ERROR', message: error.message || 'Failed to create review' }
        }
    }

    async function updateReview(reviewId, { rating, comment }) {
        const id = Number(reviewId)
        if (!Number.isFinite(id)) {
            return { success: false, error: 'Invalid review ID' }
        }

        const existingIndex = reviews.value.findIndex(r => r.id === id)
        if (existingIndex === -1) {
            return { success: false, error: 'Review not found' }
        }

        const existing = reviews.value[existingIndex]
        const requestData = {
            productId: existing.productId,
            accountId: existing.accountId,
            rating,
            comment,
        }

        try {
            const updated = await reviewsApi.update(id, requestData)
            const updatedReview = normalizeReview(updated, existing)
            reviews.value[existingIndex] = updatedReview
            persistReviews(reviews.value)
            return { success: true, review: updatedReview }
        } catch (error) {
            console.error('Error updating review:', error)
            return { success: false, error: 'Failed to update review' }
        }
    }

    async function deleteReview(reviewId) {
        const id = Number(reviewId)
        if (!Number.isFinite(id)) {
            return { success: false, error: 'Invalid review ID' }
        }

        try {
            await reviewsApi.delete(id)
            reviews.value = reviews.value.filter(r => r.id !== id)
            persistReviews(reviews.value)
            return { success: true }
        } catch (error) {
            console.error('Error deleting review:', error)
            return { success: false, error: 'Failed to delete review' }
        }
    }

    return {
        reviews,
        all,
        loading,
        getByProductId,
        getByAccountId,
        getAverageRating,
        getAuthorName,
        getExistingReview,
        fetchAccounts,
        fetchReviews,
        addReview,
        updateReview,
        deleteReview,
    }
})