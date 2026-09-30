const fs = require('fs');
const path = require('path');

function processDir(dir) {
    const files = fs.readdirSync(dir);
    for (const file of files) {
        const fullPath = path.join(dir, file);
        if (fs.statSync(fullPath).isDirectory()) {
            processDir(fullPath);
        } else if (file.endsWith('.vue') || file.endsWith('.js')) {
            let content = fs.readFileSync(fullPath, 'utf8');
            let original = content;

            content = content.replace(/localStorage\.getItem\("user"\)/g, 'getSession("user")');
            content = content.replace(/localStorage\.getItem\("userRole"\)/g, 'getSession("userRole")');
            content = content.replace(/localStorage\.getItem\("token"\)/g, 'getSession("token")');

            if (content !== original) {
                if (!content.includes('import { getSession } from "@/utils/auth"') && !content.includes('import { getSession, saveSession } from "@/utils/auth"')) {
                    if (content.includes('<script setup>')) {
                        content = content.replace('<script setup>', "<script setup>\nimport { getSession } from \"@/utils/auth\";");
                    } else if (fullPath.includes('router') || fullPath.includes('cart')) {
                         // manually inject
                         content = "import { getSession } from '@/utils/auth';\n" + content;
                    }
                }
                fs.writeFileSync(fullPath, content, 'utf8');
                console.log('Updated ' + fullPath);
            }
        }
    }
}

processDir('src');