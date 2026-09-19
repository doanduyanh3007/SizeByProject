const fs = require('fs');
let code = fs.readFileSync('C:/Users/khanh/Documents/SizeByProject/Frontend/src/utils/adminAnalytics.js', 'utf8');

// 1. Fix getOrderAmount to subtract 30000 for non-POS orders
const getOrderAmountTarget = 'export function getOrderAmount(order) {\n    let amount = 0;\n\n    if (order && order.finalAmount !== undefined && order.finalAmount !== null) {\n        amount = order.finalAmount;\n    } else if (order && order.totalMoney !== undefined && order.totalMoney !== null) {\n        amount = order.totalMoney;\n    }\n\n    const shipping = order && order.shippingFee !== undefined && order.shippingFee !== null ? asNumber(order.shippingFee, 0) : 0;\n    const finalVal = Math.max(0, Math.abs(asNumber(amount, 0)) - shipping);\n    return finalVal;\n}';

const newGetOrderAmount = `export function getOrderAmount(order) {
    let amount = 0;

    if (order && order.finalAmount !== undefined && order.finalAmount !== null) {
        amount = order.finalAmount;
    } else if (order && order.totalMoney !== undefined && order.totalMoney !== null) {
        amount = order.totalMoney;
    }

    let shipping = 0;
    if (order && order.shippingFee !== undefined && order.shippingFee !== null) {
        shipping = asNumber(order.shippingFee, 0);
    } else if (!isPosOrder(order)) {
        shipping = 30000;
    }

    const finalVal = Math.max(0, Math.abs(asNumber(amount, 0)) - shipping);
    return finalVal;
}`;

code = code.replace(getOrderAmountTarget, newGetOrderAmount);

// 2. Fix getOrderPaymentImpact
const getOrderPaymentImpactTarget = 'export function getOrderPaymentImpact(order) {\n    const paymentStatus = normalizePaymentStatusValue(order && order.paymentStatus ? order.paymentStatus : null);\n    const amount = getOrderAmount(order);\n\n    if (profitablePaymentStatuses.includes(paymentStatus)) {\n        return amount;\n    }\n\n    if (deductedPaymentStatuses.includes(paymentStatus)) {\n        return -amount;\n    }\n\n    return 0;\n}';

const newGetOrderPaymentImpact = `export function getOrderPaymentImpact(order) {
    const paymentStatus = normalizePaymentStatusValue(order && order.paymentStatus ? order.paymentStatus : null);
    const amount = getOrderAmount(order);

    if (profitablePaymentStatuses.includes(paymentStatus)) {
        return amount;
    }

    // Đối với những đơn hoàn thì k lưu số tiền nhận dc nữa = 0, k dc âm
    if (deductedPaymentStatuses.includes(paymentStatus)) {
        return 0;
    }

    return 0;
}`;

code = code.replace(getOrderPaymentImpactTarget, newGetOrderPaymentImpact);

// 3. Fix buildRevenueBuckets
const bucketTarget = '        } else if (deductedPaymentStatuses.includes(paymentStatus)) {\n            bucket.refundedRevenue += amount;\n            bucket.netRevenue -= amount;\n            bucket.refundedCount += 1;\n        }';

const newBucketTarget = `        } else if (deductedPaymentStatuses.includes(paymentStatus)) {
            bucket.refundedRevenue += amount;
            // Không trừ vào netRevenue nữa
            bucket.refundedCount += 1;
        }`;

code = code.replace(bucketTarget, newBucketTarget);

fs.writeFileSync('C:/Users/khanh/Documents/SizeByProject/Frontend/src/utils/adminAnalytics.js', code);
console.log('Fixed adminAnalytics.js');
