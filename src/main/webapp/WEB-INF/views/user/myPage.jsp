<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <!-- 반응형 필수 -->
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>홈</title>

    <!-- [선택] 이 페이지에만 필요한 스타일/스크립트(내비 관련은 넣지 말기) -->
    <style>
        /* page-specific styles only */
        main.container { padding: 24px 0; }
    </style>
    <!-- <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"> -->

    <style>
        :root { --primary:#5a7dff; --muted:#e9e9ee; --text:#222; }
        body{font-family:system-ui,AppleSDGothicNeo,Segoe UI,Roboto,Arial,sans-serif; color:var(--text); margin:0;}
        .container{max-width: 1080px; margin: 40px auto; padding: 0 16px;}
        h1{font-size: 28px; margin: 8px 0 24px; text-align:center;}
        h2{font-size: 22px; margin: 40px 0 16px;}
        .card{background:#fff; border:1px solid #f0f0f2; border-radius:14px; padding:24px; box-shadow:0 2px 10px rgba(0,0,0,.03);}
        .pill{display:inline-flex; background:var(--muted); border-radius:999px; padding:6px; gap:6px;}
        .pill button{border:0; background:transparent; padding:10px 18px; border-radius:999px; cursor:pointer; color:#777; font-weight:600;}
        .pill button.active{background:var(--primary); color:#fff;}
        .center{display:flex; flex-direction:column; align-items:center; gap:8px; color:#666; margin: 28px 0;}
        .grid{display:grid; grid-template-columns:1fr 1fr; gap:16px;}
        .form-label{font-weight:700; display:block; margin-bottom:6px;}
        .form-control,.form-select{width:100%; border:1px solid #e6e6ea; border-radius:10px; padding:12px 14px; background:#f7f8fb;}
        .btn{display:inline-block; padding:10px 16px; border-radius:10px; border:1px solid #d9d9df; background:#f6f6fa; cursor:pointer;}
        .btn-primary{background:var(--primary); border-color:var(--primary); color:#fff;}
        .row-actions{display:flex; gap:8px; align-items:center;}
        .muted{color:#888; font-size:12px;}
        .hidden{display:none;}
        @media (max-width: 768px){ .grid{grid-template-columns:1fr;} }
    </style>
</head>
<body>

<%-- 현재 탭 지정 (GNB/하단바 활성화용) --%>
<%
    request.setAttribute("currentTab", "home");
%>

<%-- 공통 내비+모달 (조각) 포함: 이 파일엔 html/head/body가 없어야 함 --%>
<%@ include file="../common/common-nav.jsp" %>
<div class="container">

    <!-- 내 신고해결 -->
    <h1>내 신고해결</h1>
    <div class="card" id="reportSection">
        <div class="pill" role="tablist" aria-label="신고/해결 전환">
            <button id="tab-report" class="active" aria-selected="true">신고</button>
            <button id="tab-resolved" aria-selected="false">해결</button>
        </div>

        <!-- 신고 탭 컨텐츠 -->
        <div id="pane-report" class="pane">
            <div class="center">
                <img src="<c:url value='/static/img/empty-note.png'/>" alt="" width="120" height="120"
                     onerror="this.style.display='none'">
                <div>신고 내역이 없어요</div>
            </div>
            <!-- TODO: 신고 목록이 있을 때는 위 빈 상태 대신 리스트를 렌더링 -->
        </div>

        <!-- 해결 탭 컨텐츠 -->
        <div id="pane-resolved" class="pane hidden">
            <div class="center">
                <img src="<c:url value='/static/img/empty-note.png'/>" alt="" width="120" height="120"
                     onerror="this.style.display='none'">
                <div>해결된 내역이 없어요</div>
            </div>
            <!-- TODO: 해결 목록 렌더링 -->
        </div>
    </div>
    <!-- 정보수정 -->
    <h2>정보수정</h2>
    <div class="card">
        <form id="mypageForm" onsubmit="return false;">
            <div class="grid">
                <!-- 좌측 -->
                <div>
                    <!-- user_nick_name -->
                    <label class="form-label">닉네임</label>
                    <div class="row-actions">
                        <input id="user_nick_name" name="user_nick_name" class="form-control" placeholder="닉네임 입력">
                    </div>

                    <!-- user_pwd -->
                    <label class="form-label" style="margin-top:14px;">비밀번호</label>
                    <input id="user_pwd" name="user_pwd" type="password" class="form-control" placeholder="비밀번호 입력">

                    <!-- commute_by_car -->
                    <label class="form-label" style="margin-top:14px;">자가용 출퇴근</label>
                    <select id="commute_by_car" name="commute_by_car" class="form-select">
                        <option value="true">예</option>
                        <option value="false">아니오</option>
                    </select>

                    <!-- cafe_use_freq -->
                    <label class="form-label" style="margin-top:14px;">카페 이용 빈도</label>
                    <select id="cafe_use_freq" name="cafe_use_freq" class="form-select">
                        <option value="주 1회">주 1회</option>
                        <option value="주 3회">주 3회</option>
                        <option value="매일">매일</option>
                    </select>

                    <!-- grocery_freq -->
                    <label class="form-label" style="margin-top:14px;">장보기 빈도</label>
                    <select id="grocery_freq" name="grocery_freq" class="form-select">
                        <option value="주 1회">주 1회</option>
                        <option value="주 2회">주 2회</option>
                        <option value="필요시">필요시</option>
                    </select>

                    <!-- practice_energy_saving -->
                    <label class="form-label" style="margin-top:14px;">에너지 절약 실천</label>
                    <select id="practice_energy_saving" name="practice_energy_saving" class="form-select">
                        <option value="true">예</option>
                        <option value="false">아니오</option>
                    </select>
                </div>

                <!-- 우측 -->
                <div>
                    <!-- residence_dong -->
                    <label class="form-label">거주 행정동</label>
                    <input id="residence_dong" name="residence_dong" class="form-control" placeholder="예: 서울 강남구 역삼동">

                    <!-- workplace_or_school_dong -->
                    <label class="form-label" style="margin-top:14px;">직장/학교 행정동</label>
                    <input id="workplace_or_school_dong" name="workplace_or_school_dong" class="form-control" placeholder="예: 서울 서초구 서초동">
                </div>
            </div>

            <div style="margin-top:20px; display:flex; gap:8px; justify-content:flex-end;">
                <button type="button" class="btn" id="btnCancel">취소</button>
                <button type="button" class="btn btn-primary" id="btnSave">저장</button>
            </div>
        </form>
    </div>
</div>

<!-- (선택) jQuery 쓰면 아래 CDN 추가 -->
<!-- <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script> -->

<script>
    // --- 신고/해결 탭 전환 ---
    const tabReport   = document.getElementById('tab-report');
    const tabResolved = document.getElementById('tab-resolved');
    const paneReport  = document.getElementById('pane-report');
    const paneResolved= document.getElementById('pane-resolved');

    function activate(tab){
        if(tab==='report'){
            tabReport.classList.add('active'); tabReport.setAttribute('aria-selected','true');
            tabResolved.classList.remove('active'); tabResolved.setAttribute('aria-selected','false');
            paneReport.classList.remove('hidden'); paneResolved.classList.add('hidden');
        }else{
            tabResolved.classList.add('active'); tabResolved.setAttribute('aria-selected','true');
            tabReport.classList.remove('active'); tabReport.setAttribute('aria-selected','false');
            paneResolved.classList.remove('hidden'); paneReport.classList.add('hidden');
        }
    }
    tabReport.addEventListener('click', ()=>activate('report'));
    tabResolved.addEventListener('click', ()=>activate('resolved'));

    // --- 정보 저장(예시: Ajax 연동 지점) ---
    document.getElementById('btnSave').addEventListener('click', function(){
        // TODO: 값 수집 후 Ajax로 /mypage/update 같은 엔드포인트 호출
        // 예) const payload = { user_nick_name: document.getElementById('user_nick_name').value, ... };
        alert('저장 로직을 연결하세요.');
    });

    document.getElementById('btnCancel').addEventListener('click', function(){
        history.back(); // 필요 시 동작 변경
    });
</script>
</body>
</html>