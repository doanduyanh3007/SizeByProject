function firstDefined(values) {
    for (const value of values) {
        if (value !== null && value !== undefined) {
            return value
        }
    }
    return null
}

function normalizeCode(value) {
    if (value === null || value === undefined) {
        return ''
    }
    const code = String(value).trim()
    return code
}

function toFiniteNumber(value) {
    if (value === null || value === undefined) {
        return null
    }
    const n = Number(value)
    return Number.isFinite(n) ? n : null
}

export function normalizeAppliedVoucher(rawVoucher, fallbackCode = '') {
    const source = rawVoucher && typeof rawVoucher === 'object' ? rawVoucher : {}
    const nested = source.voucher && typeof source.voucher === 'object' ? source.voucher : {}

    const normalizedCode = normalizeCode(
        firstDefined([
            source.code,
            source.voucherCode,
            source.voucher_code,
            nested.code,
            fallbackCode,
        ]),
    )

    const voucherId = toFiniteNumber(
        firstDefined([
            source.voucherId,
            source.voucher_id,
            source.id,
            nested.id,
        ]),
    )

    const discountType = normalizeCode(
        firstDefined([
            source.discountType,
            source.discount_type,
            nested.discountType,
            nested.discount_type,
        ]),
    )

    const discountValue = Math.max(
        0,
        toFiniteNumber(
            firstDefined([
                source.discountValue,
                source.discount_value,
                nested.discountValue,
                nested.discount_value,
            ]),
        ) || 0,
    )

    const discountAmount = Math.max(
        0,
        toFiniteNumber(
            firstDefined([
                source.discountAmount,
                source.discount_amount,
                source.amount,
                source.value,
                nested.discountAmount,
                nested.discount_amount,
            ]),
        ) || 0,
    )

    const minOrderValue = Math.max(
        0,
        toFiniteNumber(
            firstDefined([
                source.minOrderValue,
                source.min_order_value,
                nested.minOrderValue,
                nested.min_order_value,
            ]),
        ) || 0,
    )

    const maxDiscountAmount = toFiniteNumber(
        firstDefined([
            source.maxDiscountAmount,
            source.max_discount_amount,
            nested.maxDiscountAmount,
            nested.max_discount_amount,
        ]),
    )

    return {
        id: voucherId,
        code: normalizedCode,
        discountType,
        discountValue,
        discountAmount,
        minOrderValue,
        maxDiscountAmount: maxDiscountAmount != null ? Math.max(0, maxDiscountAmount) : null,
        raw: source,
    }
}

export function computeVoucherDiscount(voucher, baseAmount) {
    const type = voucher?.discountType || voucher?.discount_type
    const value = Number(voucher?.discountValue ?? voucher?.discount_value ?? 0) || 0
    const safeBase = Math.max(0, Number(baseAmount) || 0)

    if (type === 'AMOUNT') {
        return Math.min(safeBase, value)
    }

    if (type === 'PERCENT') {
        let discount = (safeBase * value) / 100
        const maxCap = toFiniteNumber(
            voucher?.maxDiscountAmount ?? voucher?.max_discount_amount,
        )
        if (maxCap != null && maxCap > 0 && discount > maxCap) {
            discount = maxCap
        }
        return Math.min(safeBase, discount)
    }

    return Math.max(0, Number(voucher?.discountAmount ?? voucher?.discount_amount ?? 0) || 0)
}
