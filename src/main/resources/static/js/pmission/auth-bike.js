// /static/js/auth-bike.js
import { createTracker, toast } from './auth-core.js';

// bike 60초
const RULE = { targets: ["bike"], confirmSec: 60, simMetersPerStep: 5.0*5 }; // 5초당 25m

export function startBikeAuth() {
  const tracker = createTracker(RULE, (mode)=>{
    toast("자전거 인증 완료!");
    window.location.href = "/personal/mission.html?complete=1&mode=" + encodeURIComponent(mode);
  });
  tracker.start();
}
