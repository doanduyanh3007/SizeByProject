const fs = require('fs');
let content = fs.readFileSync('src/components/MainTopBar.vue', 'utf8');
content = content.replace(/localStorage\.removeItem\("user"\);\s*localStorage\.removeItem\("token"\);/, 'clearSession();');
content = content.replace(/import\s*\{\s*getSession\s*\}\s*from\s*"@\/utils\/auth";/, 'import { getSession, clearSession } from "@/utils/auth";');
fs.writeFileSync('src/components/MainTopBar.vue', content, 'utf8');