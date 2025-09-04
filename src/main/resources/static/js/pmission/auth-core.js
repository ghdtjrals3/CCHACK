// /static/js/auth-core.js
export const API_BASE = ""; // 같은 도메인이면 빈 문자열

// 디바이스 ID (브라우저당 고정)
const DEVICE_KEY = "transitDeviceId";
export function getDeviceId() {
  let id = localStorage.getItem(DEVICE_KEY);
  if (!id) { id = "DEV_" + Math.random().toString(36).slice(2, 10); localStorage.setItem(DEVICE_KEY, id); }
  return id;
}
export const deviceId = getDeviceId();

// 간단 토스트
export function toast(msg){
  let t = document.getElementById('transitToast');
  if(!t){
    t = document.createElement('div');
    t.id='transitToast';
    Object.assign(t.style, {
      position:'fixed', left:'50%', bottom:'24px', transform:'translateX(-50%)',
      background:'#222', color:'#fff', padding:'10px 14px', borderRadius:'10px',
      fontSize:'14px', opacity:'0.9', zIndex:'9999'
    });
    document.body.appendChild(t);
  }
  t.textContent = msg;
  t.style.display='block';
  clearTimeout(t._hide);
  t._hide = setTimeout(()=>{ t.style.display='none'; }, 2000);
}

// 서버 호출(공통)
export async function pushPoint(t, lat, lon, accuracy = 10, speed = null, bearing = null) {
  const body = { deviceId, t, lat, lon, accuracy, speed, bearing };
  const res = await fetch(API_BASE + "/api/transport/push", {
    method: "POST",
    headers: {"Content-Type": "application/json"},
    body: JSON.stringify(body)
  });
  return res.json();
}

// 공통 트래커 팩토리
// rule: { targets: ["walk"| "bike"| "bus"|"subway"...], confirmSec: number, simMetersPerStep: number }
export function createTracker(rule, onComplete) {
  const state = {
    running:false, timer:null, t:0, lat:37.5, lon:127.0, hitSec:0, rule
  };

  async function updatePosition() {
    if (navigator.geolocation) {
      await new Promise((resolve) => {
        navigator.geolocation.getCurrentPosition(pos => {
          state.lat = pos.coords.latitude;
          state.lon = pos.coords.longitude;
          resolve();
        }, _ => { simulateStep(); resolve(); },
        { enableHighAccuracy:true, maximumAge:0, timeout:1500 });
      });
    } else {
      simulateStep();
    }
  }

  function simulateStep(){
    const meters = state.rule?.simMetersPerStep ?? 60;
    const dLat = meters / 111111.0; // 북쪽으로 이동
    state.lat += dLat;
  }

  async function tick(){
    try {
      await updatePosition();
      const r = await pushPoint(state.t, state.lat, state.lon, 10);
      const mode = r?.mode ?? "unknown";
      const conf = r?.confidence ? Math.round(r.confidence*100) : 0;
      toast(`현재 모드: ${mode} (${conf}%)`);

      if (state.rule.targets.includes(mode)) state.hitSec += 5;
      else state.hitSec = 0;

      const pct = Math.min(100, Math.round(state.hitSec / state.rule.confirmSec * 100));
      if (pct > 0) toast(`인증 진행 ${pct}%`);

      if (state.hitSec >= state.rule.confirmSec) {
        stop();
        onComplete?.(mode);
        return;
      }
    } catch (e) {
      console.error(e);
    } finally {
      state.t += 5;
      if (state.running) state.timer = setTimeout(tick, 5000);
    }
  }

  function start(){
    if (state.running) stop();
    state.t = 0; state.hitSec = 0; state.running = true;
    toast("자동 인증을 시작합니다.");
    tick();
  }

  function stop(){
    state.running = false;
    if (state.timer) clearTimeout(state.timer);
    toast("자동 인증이 종료되었습니다.");
  }

  return { start, stop };
}
