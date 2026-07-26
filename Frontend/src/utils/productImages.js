/**
 * Shared product image resolver used across pages.
 * Prefers variant image for the product, then falls back to product image, then placeholder.
 */

export function getProductImageUrl ({ product, variants, width = 800, height = 600 }) {
  if (!product) {
    return ''
  }

  const productId = product.id
  const list = Array.isArray(variants) ? variants : []

  const variant = list.find(v => v && v.productId === productId)
  const url = variant?.imageUrl || product.imageUrl
  if (url) {
    return url
  }

  return `https://via.placeholder.com/${width}x${height}?text=${encodeURIComponent(product.name || 'Product')}`
}
