const fs = require('fs');
let content = fs.readFileSync('src/pages/CheckoutView.vue', 'utf8');
let buffer = Buffer.from(content, 'latin1');
let restored = buffer.toString('utf8');
fs.writeFileSync('src/pages/CheckoutView.vue.restored', restored);