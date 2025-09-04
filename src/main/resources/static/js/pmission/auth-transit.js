// /static/js/auth-transit.js
import { createTracker, toast } from './auth-core.js';

// bus/subway 60초 이상
const RULE = { targets: ["bus","subway"], confirmSec: 60, simMetersPerStep: 150 }; // 5초당 150m

export function startTransitAuth() {
  const tracker = createTracker(RULE, (mode)=>{
    toast(`대중교통 확인됨: ${mode}`);
    window.location.href = "/personal/mission.html?complete=1&mode=" + encodeURIComponent(mode);
  });
  tracker.start();
}
