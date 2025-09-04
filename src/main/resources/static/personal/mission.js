// mission.js (교체)
const grid = document.getElementById('grid');
const missionModal = document.getElementById('missionModal');
const mTitle = document.getElementById('mTitle');
const mMeta  = document.getElementById('mMeta');
const mDesc  = document.getElementById('mDesc');
const btnStart = document.getElementById('btnStart');

const uploadModal  = document.getElementById('uploadModal');
const uMissionTitle = document.getElementById('uMissionTitle');
const uPoints = document.getElementById('uPoints');
const uFile = document.getElementById('uFile');
const uPreview = document.querySelector('#uPreview img');
const uMemo = document.getElementById('uMemo');
const uSubmit = document.getElementById('uSubmit');

const MISSIONS = Array.isArray(window.MISSIONS) ? window.MISSIONS : []; // 서버 주입
// code -> UI 메타 매핑
const META = {
    COMMUTE_TRANSIT: { icon:'🚌', tag:'대중교통 타기', desc:'자가용 대신 버스/지하철로 출퇴근해요.' },
    COMMUTE_BIKE:    { icon:'🚴', tag:'자전거 타기',   desc:'가까운 거리는 자전거로 이동해요.' },
    RECYCLING_PROPER:{ icon:'♻️', tag:'분리배출',      desc:'재활용 분류표대로 꼼꼼히 분리해요.' },
    NO_SINGLE_USE:   { icon:'🚫🥤', tag:'일회용 안쓰기', desc:'텀블러/리유저블 컵을 사용해요.' },
    BRING_BAG:       { icon:'🛒', tag:'장바구니',      desc:'장바구니로 비닐봉투를 줄여요.' },
    TURN_OFF_STANDBY_POWER:{ icon:'🔌', tag:'절전',   desc:'멀티탭 스위치를 꺼서 대기전력 절감.' },
    JOIN_NETZERO_EVENT:{ icon:'🌳', tag:'행사참여',     desc:'지역 환경 봉사/나무심기에 참여해요.' },
    DEFAULT:         { icon:'✅', tag:'미션',          desc:'탄소중립 실천으로 포인트를 모아봐요.' }
};

let current = null;

// 렌더링
function renderCard(m){
    const meta = META[m.code] || META.DEFAULT;
    const points = (m.awardedPoints ?? m.points) || 0;
    const el = document.createElement('div');
    el.className = 'card mission-card';
    if (m.status === 'completed') el.classList.add('is-done');
    el.dataset.assignmentId = m.assignmentId;
    el.dataset.templateId = m.templateId;
    el.dataset.title = m.title;
    el.dataset.verifyType = m.verifyType;
    el.dataset.points = points;
    el.dataset.algoExpected = m.algoExpected || '';

    el.innerHTML = `
    <div class="card-head">
      <div class="icon" aria-hidden="true">${meta.icon}</div>
      <div>
        <div class="meta">${meta.tag}</div>
        <div class="cta">${m.title}</div>
      </div>
    </div>
    <div class="foot">
      <span class="meta">${m.verifyType === 'photo' ? '사진 인증' : '자동 판정'}</span>
      <span class="badge">+ ${points}p</span>
    </div>
  `;
    el.addEventListener('click', ()=> openMissionModal(el, meta, points));
    return el;
}

function render(){
    grid.innerHTML = '';
    MISSIONS.forEach(m => grid.appendChild(renderCard(m)));
}
render();

// 모달
function lockScroll(lock){ document.documentElement.style.overflow = lock ? 'hidden' : ''; }
function openMissionModal(cardEl, meta, points){
    current = {
        assignmentId: cardEl.dataset.assignmentId,
        templateId  : cardEl.dataset.templateId,
        title       : cardEl.dataset.title,
        verifyType  : cardEl.dataset.verifyType,
        points      : parseInt(points,10),
        algoExpected: cardEl.dataset.algoExpected
    };
    mTitle.textContent = current.title;
    mMeta.innerHTML = `<span class="meta">${meta.tag}</span> <span class="badge" style="margin-left:8px">+ ${current.points}p</span>`;
    mDesc.textContent = (META[current.code]?.desc) || meta.desc;
    missionModal.classList.add('open'); lockScroll(true);
}
function closeMissionModal(){ missionModal.classList.remove('open'); lockScroll(false); }
document.querySelectorAll('[data-close="mission"]').forEach(el=>el.addEventListener('click', closeMissionModal));
window.addEventListener('keydown',(e)=>{ if(e.key==='Escape' && missionModal.classList.contains('open')) closeMissionModal(); });

// 업로드 모달
function openUpload(){
    uMissionTitle.textContent = current.title;
    uPoints.textContent = `+ ${current.points}p`;
    uFile.value = ''; uPreview.src = ''; uPreview.parentElement.style.display='none';
    uMemo.value = '';
    uploadModal.classList.add('open'); lockScroll(true);
}
function closeUpload(){ uploadModal.classList.remove('open'); lockScroll(false); }
document.querySelectorAll('[data-close="upload"]').forEach(el=>el.addEventListener('click', closeUpload));
uFile.addEventListener('change', ()=>{
    const f = uFile.files?.[0];
    if(!f) return;
    uPreview.src = URL.createObjectURL(f);
    document.getElementById('uPreview').style.display = 'block';
});

// 시작(도전하기/인증하기)
btnStart.addEventListener('click', ()=>{
    closeMissionModal();
    if(!current) return;

    if(current.verifyType === 'photo'){
        openUpload();
    } else {
        // ⚠️ 임시: 서버 템플릿의 기대값(algoExpected)을 보냄
        // 실제 배포에서는 앱/센서에서 계산한 결과 문자열로 바꿔 전송하세요.
        const result = current.algoExpected || '';
        fetch(`/missions/${current.templateId}/verify-algo?result=${encodeURIComponent(result)}`, { method:'POST' })
            .then(r=>r.json())
            .then(j=>{
                if(j.ok){ markDone(current.assignmentId); alert('미션 완료!'); }
                else alert(j.msg || '조건을 충족하지 못했습니다.');
            })
            .catch(()=>alert('처리 중 오류'));
    }
});

// 사진 업로드 제출
uSubmit.addEventListener('click', ()=>{
    if(!current) return;
    const f = uFile.files?.[0];
    if(!f){ alert('사진을 업로드하세요.'); return; }
    const fd = new FormData();
    fd.append('photo', f);
    fd.append('note', uMemo.value || '');

    fetch(`/missions/${current.assignmentId}/submit-photo`, { method:'POST', body: fd })
        .then(r=>r.json())
        .then(j=>{
            if(j.ok){ closeUpload(); markDone(current.assignmentId); alert('업로드 완료! 미션이 완료되었습니다.'); }
            else alert(j.msg || '업로드 실패');
        })
        .catch(()=>alert('업로드 중 오류'));
});

// 완료 UI 반영
function markDone(assignmentId){
    const card = grid.querySelector(`.mission-card[data-assignment-id="${assignmentId}"]`);
    if(!card) return;
    card.classList.add('is-done');
    // 버튼 제거·완료 뱃지 추가 등 UI 업그레이드가 필요하면 여기서 처리
}
