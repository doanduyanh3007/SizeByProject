import { computed, ref, unref, watch } from 'vue'

export function usePagination(source, pageSize = 10) {
    const currentPage = ref(1)
    const size = ref(pageSize)

    const totalItems = computed(() => {
        const list = unref(source)
        return Array.isArray(list) ? list.length : 0
    })

    const totalPages = computed(() =>
        Math.max(1, Math.ceil(totalItems.value / size.value)),
    )

    const paginatedItems = computed(() => {
        const list = unref(source)
        if (!Array.isArray(list) || list.length === 0) return []
        const start = (currentPage.value - 1) * size.value
        return list.slice(start, start + size.value)
    })

    const pageStart = computed(() =>
        totalItems.value === 0 ? 0 : (currentPage.value - 1) * size.value + 1,
    )

    const pageEnd = computed(() =>
        Math.min(currentPage.value * size.value, totalItems.value),
    )

    watch(
        () => unref(source),
        () => {
            currentPage.value = 1
        },
        { deep: true },
    )

    watch(totalPages, (pages) => {
        if (currentPage.value > pages) {
            currentPage.value = pages
        }
    })

    function goToPage(page) {
        const next = Number(page)
        if (!Number.isFinite(next)) return
        currentPage.value = Math.min(Math.max(1, next), totalPages.value)
    }

    function previousPage() {
        goToPage(currentPage.value - 1)
    }

    function nextPage() {
        goToPage(currentPage.value + 1)
    }

    return {
        currentPage,
        pageSize: size,
        totalPages,
        paginatedItems,
        pageStart,
        pageEnd,
        totalItems,
        goToPage,
        previousPage,
        nextPage,
    }
}
