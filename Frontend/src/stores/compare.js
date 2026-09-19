import { defineStore } from 'pinia'
import { computed, ref } from 'vue'

const STORAGE_KEY = 'compare'
const MAX_ITEMS = 3

function normalizeIds(list) {
    const arr = Array.isArray(list) ? list : []
    const seen = new Set()
    const normalized = []

    for (const value of arr) {
        const id = Number(value)
        if (!Number.isFinite(id) || seen.has(id)) {
            continue
        }
        seen.add(id)
        normalized.push(id)
    }

    return normalized.slice(-MAX_ITEMS)
}

function loadList() {
    try {
        const raw = localStorage.getItem(STORAGE_KEY)
        const arr = raw ? JSON.parse(raw) : []
        return normalizeIds(arr)
    } catch {
        return []
    }
}

function persistList(list) {
    try {
        localStorage.setItem(STORAGE_KEY, JSON.stringify(normalizeIds(list)))
    } catch {
        // ignore
    }
}

export const useCompareStore = defineStore('compare', () => {
    const ids = ref(loadList())

    const compareIds = computed(() => ids.value)
    const count = computed(() => ids.value.length)

    function setIds(nextIds) {
        ids.value = normalizeIds(nextIds)
        persistList(ids.value)
    }

    function has(productId) {
        const id = Number(productId)
        if (!Number.isFinite(id)) {
            return false
        }
        return ids.value.includes(id)
    }

    function add(productId) {
        const id = Number(productId)
        if (!Number.isFinite(id) || has(id)) {
            return
        }
        setIds([...ids.value, id])
    }

    function remove(productId) {
        const id = Number(productId)
        if (!Number.isFinite(id) || !has(id)) {
            return
        }
        setIds(ids.value.filter(x => x !== id))
    }

    function toggle(productId) {
        if (has(productId)) {
            remove(productId)
            return
        }
        add(productId)
    }

    function clear() {
        setIds([])
    }

    return { compareIds, count, has, add, remove, toggle, clear }
})