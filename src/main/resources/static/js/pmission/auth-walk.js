// /static/js/auth-walk.js
import { createTracker, toast } from './auth-core.js';

// walk 90초
const RULE = { targets: ["walk"], confirmSec: 90, simMetersPerStep: 1.3*5 }; // 5초당 ~6.5m

export function startWalkAuth() {
  const tracker = createTracker(RULE, (mode)=>{
    toast("걷기 인증 완료!");
    window.location.href = "/personal/mission.html?complete=1&mode=" + encodeURIComponent(mode);
  });
  tracker.start();
}
