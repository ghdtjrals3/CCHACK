// /static/js/app.js
import { startTransitAuth } from '../js/pmission/auth-transit.js';
import { startWalkAuth } from '../js/pmission/auth-walk.js';
import { startBikeAuth } from '../js/pmission/auth-bike.js';
import { bindUploadModal } from '../js/pmission/auth-upload.js';

/* 포인트/데이터 */
const POINTS = { easy: 50, normal: 100, hard: 200 };
const missions = [
  { id:1, icon:"🚌", tag:"대중교통 타기", level:"easy",
    title:"대중교통으로 출근하기", desc:"자가용 대신 버스/지하철로 출퇴근하면 탄소를 확 줄일 수 있어요." },
  { id:2, icon:"🥤", tag:"일회용 안쓰기", level:"normal",
    title:"일회용품 사용하지 않기", desc:"텀블러/리유저블 컵 사용으로 일회용 컵을 줄여요." },
  { id:3, icon:"🛒", tag:"장바구니", level:"easy",
    title:"장바구니 챙기기", desc:"마트/편의점에서 장바구니 사용으로 비닐봉투 줄이기." },
  { id:4, icon:"🍚", tag:"음식물 줄이기", level:"normal",
    title:"음식물 쓰레기 20% 줄이기", desc:"한 끼 양을 조절하고 남긴 음식 포장 대신 비우기 실천." },
  { id:5, icon:"🚶", tag:"걷기", level:"easy",
    title:"하루 1만 보 걷기", desc:"가까운 거리는 도보로 이동해요." },
  { id:9, icon:"🚴", tag:"자전거 타기", level:"normal",
    title:"자전거로 이동하기", desc:"가까운 거리는 자전거로 이동해요." },
  { id:6, icon:"🔌", tag:"절전", level:"hard",
    title:"콘센트 대기전력 차단", desc:"멀티탭 스위치를 꺼서 대기전력 절감하기." },
  { id:7, icon:"🌳", tag:"나무심기", level:"hard",
    title:"탄소중립 행사 참여", desc:"지역 환경 봉사/나무심기 행사 참가." },
  { id:8, icon:"♻️", tag:"분리배출", level:"normal",
    title:"정확한 분리배출 실천", desc:"재활용 분류표대로 꼼꼼히 분리하기." },
];

/* 카드 렌더링 */
const grid = document.getElementById('grid');

function renderCard(m){
  const p = POINTS[m.level] || 50;
  const el = document.createElement('div');
  el.className = 'card';
  el.tabIndex = 0;
  el.setAttribute('aria-label', `${m.title} – ${p}p`);
  el.innerHTML = `
    <div class="card-head">
      <div class="icon" aria-hidden="true">${m.icon}</div>
      <div>
        <div class="meta">${m.tag}</div>
        <div class="cta">${m.title}</div>
      </div>
    </div>
    <div class="foot">
      <span class="meta">미션 난이도: ${m.level}</span>
      <span class="badge">+ ${p}p</span>
    </div>`;
  el.addEventListener('click', () => openMissionModal(m, p));
  el.addEventListener('keydown', (e)=>{
    if(e.key==='Enter' || e.key===' ') { e.preventDefault(); openMissionModal(m,p); }
  });
  return el;
}
function render(){ grid.innerHTML=''; missions.forEach(m=>grid.appendChild(renderCard(m))); }
render();

/* 모달 제어 */
const missionModal = document.getElementById('missionModal');
const mTitle = document.getElementById('mTitle');
const mMeta = document.getElementById('mMeta');
const mDesc = document.getElementById('mDesc');
const btnStart = document.getElementById('btnStart');

let currentMission=null, currentPoints=0;
function lockScroll(lock){ document.documentElement.style.overflow = lock ? 'hidden' : ''; }

function openMissionModal(m,p){
  currentMission=m; currentPoints=p;
  mTitle.textContent=m.title;
  mMeta.innerHTML=`<span class="meta">${m.tag}</span> <span class="badge" style="margin-left:8px">+ ${p}p</span>`;
  mDesc.textContent=m.desc;
  missionModal.classList.add('open'); lockScroll(true);
}
function closeMissionModal(){ missionModal.classList.remove('open'); lockScroll(false); }
document.querySelectorAll('[data-close="mission"]').forEach(el=>el.onclick=closeMissionModal);
window.addEventListener('keydown',(e)=>{
  if(e.key==='Escape' && missionModal.classList.contains('open')) closeMissionModal();
});

/* 업로드 모달 바인딩 */
const { openUploadModal } = bindUploadModal();

/* 시작 버튼: 미션 라우팅 */
btnStart.onclick = () => {
  closeMissionModal();
  const tag = currentMission?.tag || "";
  if (tag === "대중교통 타기")      startTransitAuth();
  else if (tag === "걷기")          startWalkAuth();
  else if (tag === "자전거 타기")    startBikeAuth();
  else                               openUploadModal(currentMission, currentPoints);
};
document.addEventListener("DOMContentLoaded", () => {
  const norm = s => (s||"").replace(/\/+$/,"");
  const path = norm(location.pathname);
  document.querySelectorAll("[data-route]").forEach(el=>{
    const r = norm(el.getAttribute("data-route"));
    const active = (r==="/" ? (path==="/") : path.startsWith(r));
    if (active) el.classList.add("active");
  });
});
