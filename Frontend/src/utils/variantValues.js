const FALLBACK_COLORS_BY_ID = Object.freeze({
    1: { name: 'Black', hexCode: '#000000' },
    2: { name: 'White', hexCode: '#FFFFFF' },
    3: { name: 'Red', hexCode: '#FF0000' },
    4: { name: 'Blue', hexCode: '#0000FF' },
    5: { name: 'Gray', hexCode: '#808080' },
})

const FALLBACK_SIZES_BY_ID = Object.freeze({
    1: '38',
    2: '39',
    3: '40',
    4: '41',
    5: '42',
})

function normalizeText(value) {
    if (typeof value !== 'string') return null
    const text = value.trim()
    return text || null
}

export function toVariantId(value) {
    if (value === null || value === undefined) return null

    if (typeof value === 'number') {
        return Number.isFinite(value) ? value : null
    }

    if (typeof value === 'string') {
        const text = value.trim()
        if (!text) return null
        const n = Number(text)
        return Number.isFinite(n) ? n : null
    }

    return null
}

export function normalizeHexColor(value) {
    const text = normalizeText(value)
    if (!text) return null

    if (/^#[a-f\d]{3}([a-f\d]{3})?$/i.test(text)) return text
    if (/^[a-f\d]{3}([a-f\d]{3})?$/i.test(text)) return `#${text}`
    return null
}

export function resolveColorMeta(input) {
    const source = input || {}
    const normalizedId = toVariantId(source.id)
    const fallback = normalizedId !== null ? FALLBACK_COLORS_BY_ID[normalizedId] : null

    const fallbackName = fallback ? fallback.name : null
    const fallbackHex = fallback ? fallback.hexCode : null

    const resolvedName =
        normalizeText(source.name) ||
        fallbackName ||
        (normalizedId !== null ? `Màu ${normalizedId}` : null)

    const resolvedHex =
        normalizeHexColor(source.hexCode) ||
        fallbackHex ||
        null

    return {
        id: normalizedId,
        name: resolvedName,
        hexCode: resolvedHex,
    }
}

export function resolveSizeMeta(input) {
    const source = input || {}
    const normalizedId = toVariantId(source.id)
    const fallback = normalizedId !== null ? FALLBACK_SIZES_BY_ID[normalizedId] : null

    const resolvedName =
        normalizeText(source.name) ||
        fallback ||
        (normalizedId !== null ? `Size ${normalizedId}` : null)

    return {
        id: normalizedId,
        name: resolvedName,
    }
}

export function resolveColorFromVariant(variant) {
    const source = variant || null
    const color = source && source.color ? source.color : null

    const idValue =
        (source && source.colorId !== undefined && source.colorId !== null) ?
        source.colorId :
        (source && source.color_id !== undefined && source.color_id !== null) ?
        source.color_id :
        (color && color.id !== undefined && color.id !== null) ?
        color.id :
        null

    const nameValue =
        (color && color.name !== undefined && color.name !== null) ?
        color.name :
        (source && source.colorName !== undefined && source.colorName !== null) ?
        source.colorName :
        (source && source.color_name !== undefined && source.color_name !== null) ?
        source.color_name :
        null

    const hexValue =
        (color && color.hexCode !== undefined && color.hexCode !== null) ?
        color.hexCode :
        (color && color.hex_code !== undefined && color.hex_code !== null) ?
        color.hex_code :
        (color && color.hexCode1 !== undefined && color.hexCode1 !== null) ?
        color.hexCode1 :
        (source && source.colorHexCode !== undefined && source.colorHexCode !== null) ?
        source.colorHexCode :
        (source && source.color_hex_code !== undefined && source.color_hex_code !== null) ?
        source.color_hex_code :
        null

    return resolveColorMeta({
        id: idValue,
        name: nameValue,
        hexCode: hexValue,
    })
}

export function resolveSizeFromVariant(variant) {
    const source = variant || null
    const size = source && source.size ? source.size : null

    const idValue =
        (source && source.sizeId !== undefined && source.sizeId !== null) ?
        source.sizeId :
        (source && source.size_id !== undefined && source.size_id !== null) ?
        source.size_id :
        (size && size.id !== undefined && size.id !== null) ?
        size.id :
        null

    const nameValue =
        (size && size.name !== undefined && size.name !== null) ?
        size.name :
        (source && source.sizeName !== undefined && source.sizeName !== null) ?
        source.sizeName :
        (source && source.size_name !== undefined && source.size_name !== null) ?
        source.size_name :
        null

    return resolveSizeMeta({
        id: idValue,
        name: nameValue,
    })
}

export function getVariantColorKey(variant) {
    const meta = resolveColorFromVariant(variant)
    if (meta.id !== null) return `c:${meta.id}`
    const name = normalizeText(meta.name)
    if (name) return `cn:${name.toLowerCase()}`
    return null
}

export function getVariantSizeKey(variant) {
    const meta = resolveSizeFromVariant(variant)
    if (meta.id !== null) return `s:${meta.id}`
    const name = normalizeText(meta.name)
    if (name) return `sn:${name.toLowerCase()}`
    return null
}

export function variantMatchesColorKey(variant, colorKey) {
    if (!colorKey) return true
    return getVariantColorKey(variant) === colorKey
}

export function variantMatchesSizeKey(variant, sizeKey) {
    if (!sizeKey) return true
    return getVariantSizeKey(variant) === sizeKey
}