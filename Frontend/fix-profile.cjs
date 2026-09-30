const fs = require('fs');
let content = fs.readFileSync('src/pages/AdminSettingsView.vue', 'utf8');
content = content.replace(/localStorage\.setItem\("user",\s*JSON\.stringify\(merged\)\);/, 'saveSession(merged);');
if (content.includes('import { getSession }') && !content.includes('saveSession')) {
    content = content.replace('import { getSession }', 'import { getSession, saveSession }');
}
fs.writeFileSync('src/pages/AdminSettingsView.vue', content, 'utf8');

content = fs.readFileSync('src/pages/ProfileView.vue', 'utf8');
content = content.replace(/localStorage\.setItem\("user",\s*JSON\.stringify\(user\.value\)\);/, 'saveSession(user.value);');
if (content.includes('import { getSession }') && !content.includes('saveSession')) {
    content = content.replace('import { getSession }', 'import { getSession, saveSession }');
}
fs.writeFileSync('src/pages/ProfileView.vue', content, 'utf8');