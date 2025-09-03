// scripts/gen-config.js
const fs = require('fs');
const path = require('path');

// 환경 변수에서 JavaScript Key 가져오기
const JS_KEY = process.env.KAKAO_JS_KEY || '';

const out = `window.KAKAO_JS_KEY = "${JS_KEY}";\n`;

// config.local.js 파일을 resources/static/js 밑에 생성
const target = path.resolve(__dirname, '../src/main/resources/static/js/config.local.js');

fs.mkdirSync(path.dirname(target), { recursive: true });
fs.writeFileSync(target, out, 'utf8');
console.log('generated:', target);
