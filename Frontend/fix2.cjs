const fs = require('fs');
let code = fs.readFileSync('C:/Users/khanh/Documents/SizeByProject/Frontend/src/pages/AdminHomeView.vue', 'utf8');

const target = 'overviewPreset.value = "custom";\n});';

const applyOverviewPresetCode = `

function applyOverviewPreset(preset) {
  const now = new Date();
  const end = new Date(now.getFullYear(), now.getMonth(), now.getDate());
  let start = new Date(end);

  if (preset === "7d") {
    start.setDate(end.getDate() - 6);
    overviewMode.value = "day";
  } else if (preset === "30d") {
    start.setDate(end.getDate() - 29);
    overviewMode.value = "day";
  } else if (preset === "qtd") {
    const quarterStartMonth = Math.floor(end.getMonth() / 3) * 3;
    start = new Date(end.getFullYear(), quarterStartMonth, 1);
    overviewMode.value = "month";
  } else if (preset === "ytd") {
    start = new Date(end.getFullYear(), 0, 1);
    overviewMode.value = "month";
  } else {
    start = new Date(end.getFullYear(), end.getMonth(), 1);
    overviewMode.value = "day";
  }

  overviewPreset.value = preset;
  startDateInput.value = formatDateInput(start);
  endDateInput.value = formatDateInput(end);
}`;

if (code.includes(target) && !code.includes('function applyOverviewPreset')) {
    code = code.replace(target, target + applyOverviewPresetCode);
    fs.writeFileSync('C:/Users/khanh/Documents/SizeByProject/Frontend/src/pages/AdminHomeView.vue', code);
    console.log('applyOverviewPreset added back!');
} else {
    console.log('Target not found or applyOverviewPreset already exists.');
}
