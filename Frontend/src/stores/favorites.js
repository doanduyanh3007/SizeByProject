import { defineStore } from 'pinia'
import { computed, ref } from 'vue'

const STORAGE_KEY = 'favorites'

function loadSet () {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    const arr = raw ? JSON.parse(raw) : []
    return new Set(Array.isArray(arr) ? arr : [])
  } catch {
    return new Set()
  }
}

function persistSet (set) {
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify([...set]))
  } catch {
    // ignore
  }
}

export const useFavoritesStore = defineStore('favorites', () => {
  const ids = ref(loadSet())

  const favoriteIds = computed(() => [...ids.value])
  const count = computed(() => ids.value.size)

  function isFavorite (productId) {
    return ids.value.has(Number(productId))
  }

  function toggle (productId) {
    const id = Number(productId)
    if (!Number.isFinite(id)) {
      return
    }
    const next = new Set(ids.value)
    if (next.has(id)) {
      next.delete(id)
    } else {
      next.add(id)
    }
    ids.value = next
    persistSet(ids.value)
  }

  function clear () {
    ids.value = new Set()
    persistSet(ids.value)
  }

  return { favoriteIds, count, isFavorite, toggle, clear }
})
