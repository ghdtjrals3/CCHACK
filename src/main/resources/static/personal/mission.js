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
    const done = (m.status === 'completed' || m.status === 'success');

    const el = document.createElement('div');
    el.className = 'card mission-card';
    el.dataset.assignmentId = m.assignmentId;
    el.dataset.templateId = m.templateId;
    el.dataset.title = m.title;
    el.dataset.verifyType = m.verifyType;
    el.dataset.points = points;
    el.dataset.algoExpected = m.algoExpected || '';

    // 기본 본문
    el.innerHTML = `
    <div class="card-head">
      <div class="icon" aria-hidden="true">${meta.icon}</div>
      <div>
        <div class="meta">${meta.tag}</div>
        <div class="cta">${m.title}</div>
      </div>
    </div>
    <div class="foot">
      <span class="meta">${done ? '완료' : (m.verifyType === 'photo' ? '사진 인증' : '자동 판정')}</span>
      ${done ? `<span class="badge done" aria-label="해결완료">해결완료</span>` : `<span class="badge">+ ${points}p</span>`}
    </div>
  `;

    if (done) {
        el.classList.add('is-done');
        el.setAttribute('aria-disabled', 'true');
        el.tabIndex = -1;                       // 키보드 포커스도 막기
        el.title = '이미 완료된 미션입니다';
        // 가시성 올리는 오버레이
        el.insertAdjacentHTML('beforeend', `
      <div class="done-overlay" aria-hidden="true">✅ 해결완료</div>
    `);
    } else {
        el.addEventListener('click', ()=> openMissionModal(el, meta, points));
    }
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




    if (current.verifyType !== 'photo') {
        const activeId = getActiveAssignment(current.templateId);
        btnStart.textContent = activeId ? '인증하기' : '도전하기';
        } else {
        btnStart.textContent = '인증 사진 올리기';
        }









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











// // 시작(도전하기/인증하기)
// btnStart.addEventListener('click', ()=>{
//     if (!current) return;
//
//     const missionName = current.title;   // ← 여기서 잡음
//     console.log('missionName:', missionName);
//
//
//     closeMissionModal();
//     if(!current) return;
//
//     if(current.verifyType === 'photo'){
//         openUpload();
//     } else {
//         // ⚠️ 임시: 서버 템플릿의 기대값(algoExpected)을 보냄
//         // 실제 배포에서는 앱/센서에서 계산한 결과 문자열로 바꿔 전송하세요.
//         const result = current.algoExpected || '';
//         fetch(`/missions/${current.templateId}/verify-algo?result=${encodeURIComponent(result)}`, { method:'POST' })
//             .then(r=>r.json())
//             .then(j=>{
//                 if(j.ok){ markDone(current.assignmentId); alert('미션 완료!'); }
//                 else alert(j.msg || '조건을 충족하지 못했습니다.');
//             })
//             .catch(()=>alert('처리 중 오류'));
//     }
// });





// 시작(도전하기/인증하기)
    btnStart.addEventListener('click', async ()=>{
        if (!current) return;
        const isPhoto = current.verifyType === 'photo';
        if (isPhoto) {
            closeMissionModal();
            openUpload();
            return;
            }
// === 알고리즘 미션 ===
        const templateId = current.templateId;
        const headers = csrfHeaders();
        btnStart.disabled = true;
        const originalLabel = btnStart.textContent;
        try {
            const activeId = getActiveAssignment(templateId);
            if (!activeId) {
                // ---- 도전하기: 시작 좌표/시간 저장 ----


                if (LAT == null || LON == null) throw new Error('위치가 아직 확보되지 않았습니다.');
                btnStart.textContent = '위치 확인 중…';
                const at = new Date(LAST_TS || Date.now()).toISOString();
                const { assignmentId } = await postForm('/personal/start',
                    { templateId, lat: LAT, lng: LON, at }, headers);


                setActiveAssignment(templateId, assignmentId);
                btnStart.textContent = '인증하기';
                alert('미션 시작! 목적지에 도착하면 인증하기를 눌러주세요.');
                    } else {
                btnStart.textContent = '검증 중…';
                      if (LAT == null || LON == null) throw new Error('위치가 아직 확보되지 않았습니다.');
                      const at = new Date(LAST_TS || Date.now()).toISOString();
                      const dto = await postForm('/personal/complete',
                            { assignmentId: activeId, lat: LAT, lng: LON, at, autoVerify: true }, headers)



                      // ---- 인증하기: 종료 좌표/시간 저장 + 자동 판정 ----
                      setActiveAssignment(templateId, null);
                      btnStart.textContent = '도전하기';

                         // 완료 UI 반영 (서버가 반환한 status/points 사용)
                              if (dto?.status) {
                            // grid 내 해당 카드 찾기 (assignmentId가 없다면 templateId로 대체)
                                const card = grid.querySelector(`.mission-card[data-template-id="${templateId}"]`);
                            if (dto.status.toUpperCase() === 'SUCCESS') {
                                  if (card) markDone(card.dataset.assignmentId || activeId);
                                  alert(`성공! +${dto.awardedPoints ?? 0}p 적립`);
                                } else {
                                  alert('아쉽지만 실패 😢');
                                }
                          } else {
                            alert('검증 결과를 받지 못했습니다.');
                          }
                      closeMissionModal();
                    }
              } catch (e) {
                console.error(e);
                alert(e.message || '처리 중 오류');
              } finally {
                btnStart.disabled = false;
                // 모달을 닫지 않았다면 원래 라벨 복구
                    if (missionModal.classList.contains('open')) btnStart.textContent = originalLabel;
              }
        });








// 사진 업로드 제출
uSubmit.addEventListener('click', async ()=>{
    if (!current) { alert('미션 정보가 없습니다.'); return; }

    const templateId = current.templateId;            // ← 여기서 확보
    const missionName = current.title;
    const points = current.points;

    const f = uFile.files?.[0];
    if (!f) { alert('사진을 업로드하세요.'); return; }

    console.log(uMemo.value || '');
    console.log(missionName);
    console.log(String(templateId));

    const fd = new FormData();
    fd.append('image', f);
    fd.append('awardedPoints', points);
    fd.append('proof_note', uMemo.value || '');
    fd.append('title', missionName);
    fd.append('templateId', String(templateId));      // ← 같이 전송(문자열로)

    // (필요하면) 배정 아이디도 같이
    if (current.assignmentId) fd.append('assignmentId', String(current.assignmentId));

    const headers = {};
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;
    const csrfToken  = document.querySelector('meta[name="_csrf"]')?.content;
    if (csrfHeader && csrfToken) headers[csrfHeader] = csrfToken;

    try {
        const res = await fetch('/personal/goMission', { method:'POST', body: fd, headers });
        if (!res.ok) throw new Error(`서버 오류: ${res.status}`);
        alert('신고가 등록되었습니다.');
        window.location.href = '/personal/mission';
    } catch (e) {
        alert(e.message || '업로드 중 오류');
    }
});

// 완료 UI 반영
function markDone(assignmentId){
    // const card = grid.querySelector(`.mission-card[data-assignment-id="${assignmentId}"]`);

      let card = assignmentId ? grid.querySelector(`.mission-card[data-assignment-id="${assignmentId}"]`) : null;
      if (!card && current?.templateId) {
            card = grid.querySelector(`.mission-card[data-template-id="${current.templateId}"]`);
          }





    if(!card) return;
    card.classList.add('is-done');
    card.setAttribute('aria-disabled','true');
    card.tabIndex = -1;
    card.title = '이미 완료된 미션입니다';

    const foot = card.querySelector('.foot');
    foot.querySelector('.meta').textContent = '완료';
    const badge = foot.querySelector('.badge');
    if (badge) { badge.classList.add('done'); badge.textContent = '해결완료'; }
    if (!card.querySelector('.done-overlay')) {
        card.insertAdjacentHTML('beforeend', `<div class="done-overlay" aria-hidden="true">✅ 해결완료</div>`);
    }
}









// === [ADD] 공통 유틸 ===
async function postForm(url, bodyObj, headers = {}) {
    const body = new URLSearchParams(bodyObj);
    const res = await fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8', ...headers },
        body
    });
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    const ct = res.headers.get('content-type') || '';
    return ct.includes('application/json') ? res.json() : res.text();
}

// 브라우저 위치 얻기 (소수점 6자리)
function geolocate(opts={ enableHighAccuracy:true, timeout:12000, maximumAge:0 }) {
    return new Promise((resolve, reject) => {
        if (!navigator.geolocation) return reject(new Error('이 브라우저는 위치를 지원하지 않음'));
        navigator.geolocation.getCurrentPosition(
            pos => resolve({
                lat:+pos.coords.latitude.toFixed(6),
                lng:+pos.coords.longitude.toFixed(6),
                at: new Date().toISOString()
            }),
            err => reject(err),
            opts
        );
    });
}

// 템플릿별 진행중 assignmentId 저장/조회 (도전하기→인증하기 토글용)
const assignKey = (templateId) => `mc_active_${templateId}`;
const getActiveAssignment = (templateId) => {
    const v = localStorage.getItem(assignKey(templateId));
    return v ? Number(v) : null;
};
const setActiveAssignment = (templateId, idOrNull) => {
    if (idOrNull) localStorage.setItem(assignKey(templateId), String(idOrNull));
    else localStorage.removeItem(assignKey(templateId));
};

// (선택) CSRF 사용 시 헤더 주입
function csrfHeaders() {
    const h = {};
    const k = document.querySelector('meta[name="_csrf_header"]')?.content;
    const v = document.querySelector('meta[name="_csrf"]')?.content;
    if (k && v) h[k] = v;
    return h;
}







/**/

// ===== 전역 위치 변수 =====
let LAT = null;
let LON = null;
let LAST_TS = null;

function geoSuccess({ coords, timestamp }) {
    LAT = coords.latitude;
    LON = coords.longitude;
    LAST_TS = timestamp;
    console.log('업데이트:', LAT, LON, 'ts=', LAST_TS);
}

function geoError(err) {
    console.warn('위치 에러:', err);
}


// 페이지 시작할 때 실행 → 실시간 위치 업데이트
if (navigator.geolocation) {
    navigator.geolocation.watchPosition(geoSuccess, geoError, {
        enableHighAccuracy: true,
        timeout: 10000,
        maximumAge: 0
    });
} else {
    console.warn('이 브라우저는 geolocation을 지원하지 않음');
}

