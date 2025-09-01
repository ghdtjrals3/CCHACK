<%@page import="static kopo.poly.util.CmmUtil.nvl"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    String currentTab = (String) request.getAttribute("currentTab");
    if (currentTab == null) currentTab = "";
    String user_id = nvl((String) session.getAttribute("user_id"), "");
%>

<!-- nav 전용 스타일만 (필요시) -->
<style>
    :root{ --container:1200px; --space-3:1rem; --fg:#111; --brand:#2563eb; --line:#e5e7eb; --bg:#fff; }
    .container{ width:100%; max-width:var(--container); margin-inline:auto; padding-inline:var(--space-3); }
    .header-desktop{ display:none; border-bottom:1px solid var(--line); background:#fff; position:sticky; top:0; z-index:1000; }
    .header-desktop .inner{ display:flex; align-items:center; justify-content:space-between; height:56px; }
    .nav a{ text-decoration:none; color:#374151; margin-left:1rem; }
    .nav a.active{ color:var(--brand); font-weight:600; }
    .bottom-nav{ display:flex; position:fixed; left:0; bottom:0; width:100%; height:56px; background:#fff; border-top:1px solid var(--line); z-index:1000; justify-content:space-around; align-items:center; padding-bottom: env(safe-area-inset-bottom); }
    .bottom-nav a{ flex:1 1 0; text-align:center; text-decoration:none; color:#6b7280; font-size:12px; line-height:1; padding-block:6px; }
    .bottom-nav a.active{ color:var(--brand); font-weight:600; }
    .bottom-nav .icon{ display:block; width:22px; height:22px; margin:0 auto 4px; }
    @media (max-width:1023.98px){ body{ padding-bottom:max(56px, env(safe-area-inset-bottom)); } }
    @media (min-width:1024px){ .header-desktop{ display:block !important; } .bottom-nav{ display:none !important; } }
</style>
<style>
    /* 모달 기본 */
    .modal { position: fixed; inset: 0; z-index: 10000; }
    .modal-backdrop { position: absolute; inset: 0; background: rgba(0,0,0,.45); }

    /* 로그인 페이지가 2컬럼(좌컨텐츠+우이미지)이므로 데스크톱은 좀 넓게 */
    .modal-panel {
        position: relative; margin: 6vh auto 0;
        width: min(96vw, 980px);   /* 데스크톱 폭 */
        height: min(88vh, 760px);  /* 높이 제한 */
        background: #fff; border-radius: 12px; box-shadow: 0 10px 30px rgba(0,0,0,.2);
        overflow: hidden;
    }
    .modal-close {
        position: absolute; right: 8px; top: 6px; border: 0; background: transparent;
        font-size: 28px; line-height: 1; cursor: pointer; z-index: 1;
    }

    /* 모바일에선 거의 풀스크린 느낌 */
    @media (max-width: 768px){
        .modal-panel{ width: 96vw; height: 90vh; margin-top: 4vh; border-radius: 10px; }
    }
</style>


<!-- 데스크톱 GNB -->
<header class="header-desktop">
    <div class="inner container">
        <div class="logo">
            <a href="<c:url value='/'/>" style="text-decoration:none;color:var(--fg);font-weight:700;">MySite</a>
        </div>
        <nav class="nav" aria-label="Global Navigation">
            <a href="<c:url value='/'/>"       class="<%= "home".equals(currentTab)   ? "active" : "" %>">홈</a>
            <a href="<c:url value='/search'/>" class="<%= "search".equals(currentTab) ? "active" : "" %>">검색</a>
            <a href="<c:url value='/alerts'/>" class="<%= "alerts".equals(currentTab) ? "active" : "" %>">알림</a>
            <% if (user_id.isEmpty()) { %>
            <a href="<c:url value=''/>" class="<%= "login".equals(currentTab) ? "active" : "" %> js-login-open">로그인</a>
            <% } else { %>
            <a href="<c:url value='/user/myPage'/>"    class="<%= "me".equals(currentTab)    ? "active" : "" %>">내 정보</a>
            <% } %>
        </nav>
    </div>
</header>

<!-- 모바일 하단 내비 -->
<nav class="bottom-nav" role="navigation" aria-label="Bottom Navigation">
    <a href="<c:url value='/'/>" class="<%= "home".equals(currentTab) ? "active" : "" %>">
        <svg class="icon" viewBox="0 0 24 24" aria-hidden="true">
            <path d="M3 10.5 12 3l9 7.5V21a1 1 0 0 1-1 1h-5v-6H9v6H4a1 1 0 0 1-1-1z" fill="currentColor"/>
        </svg>
        <span>홈</span>
    </a>

    <a href="<c:url value='/search'/>" class="<%= "search".equals(currentTab) ? "active" : "" %>">
        <svg class="icon" viewBox="0 0 24 24" aria-hidden="true">
            <path d="M10 4a6 6 0 1 1 0 12 6 6 0 0 1 0-12zm11 17-5.2-5.2" stroke="currentColor" stroke-width="2" fill="none" stroke-linecap="round"/>
        </svg>
        <span>검색</span>
    </a>

    <a href="<c:url value='/alerts'/>" class="<%= "alerts".equals(currentTab) ? "active" : "" %>">
        <svg class="icon" viewBox="0 0 24 24" aria-hidden="true">
            <path d="M12 22a2.5 2.5 0 0 0 2.5-2.5h-5A2.5 2.5 0 0 0 12 22zM19 17H5l1.5-2.5V10a5.5 5.5 0 1 1 11 0v4.5L19 17z" fill="currentColor"/>
        </svg>
        <span>알림</span>
    </a>

    <% if (user_id.isEmpty()) { %>
    <!-- 로그인 미보유: 로그인 -->
    <a href="<c:url value='/user/login'/>" class="<%= "login".equals(currentTab) ? "active" : "" %> js-login-open">
        <svg class="icon" viewBox="0 0 24 24" aria-hidden="true">
            <path d="M12 12a5 5 0 1 0-5-5 5 5 0 0 0 5 5zm0 2c-5 0-9 2.5-9 5v1h18v-1c0-2.5-4-5-9-5z" fill="currentColor"/>
        </svg>
        <span>로그인</span>
    </a>
    <% } else { %>
    <!-- 로그인 보유: 내 정보 -->
    <a href="<c:url value='/me'/>" class="<%= "me".equals(currentTab) ? "active" : "" %>">
        <svg class="icon" viewBox="0 0 24 24" aria-hidden="true">
            <path d="M12 12a5 5 0 1 0-5-5 5 5 0 0 0 5 5zm0 2c-5 0-9 2.5-9 5v1h18v-1c0-2.5-4-5-9-5z" fill="currentColor"/>
        </svg>
        <span>내 정보</span>
    </a>
    <% } %>
</nav>


<!-- 로그인 모달(iframe) + 스크립트 -->
<div id="modal-login" class="modal" aria-hidden="true" role="dialog" aria-modal="true" style="display:none;">
    <div class="modal-backdrop"></div>
    <div class="modal-panel" role="document">
        <button type="button" class="modal-close" aria-label="Close">&times;</button>
        <iframe id="modal-login-iframe" src="about:blank" title="Login" style="width:100%;height:100%;border:0;"></iframe>
    </div>
</div>
<script>
    (function(){
        const modal = document.getElementById('modal-login');
        const iframe = document.getElementById('modal-login-iframe');
        const openLinks = document.querySelectorAll('a.js-login-open');

        function openModal(url) {
            // 같은 오리진(/login)만 iframe으로 로드
            iframe.src = url;
            modal.style.display = 'block';
            modal.setAttribute('aria-hidden', 'false');
            document.documentElement.style.overflow = 'hidden'; // 배경 스크롤 잠금
        }
        function closeModal() {
            modal.style.display = 'none';
            modal.setAttribute('aria-hidden', 'true');
            document.documentElement.style.overflow = '';
            iframe.src = 'about:blank'; // 다음 오픈 때 새로 로드되도록 정리
        }

        // 트리거
        openLinks.forEach(a => {
            a.addEventListener('click', function(e){
                e.preventDefault();
                const url = a.getAttribute('href') || '/user/login';
                openModal(url);
            });
        });

        // 닫기 (배경/X/ESC)
        modal.addEventListener('click', (e)=>{
            if (e.target.classList.contains('modal-backdrop') || e.target.classList.contains('modal-close')){
                closeModal();
            }
        });
        document.addEventListener('keydown', (e)=>{
            if (e.key === 'Escape' && modal.style.display === 'block') closeModal();
        });
    })();
</script>
<script>
</script>
<script>
    function openLoginModal() {
        const modal = document.getElementById('modal-login');
        const frame = document.getElementById('modal-login-iframe');

        // 로그인 페이지를 iframe에 로드 (경로는 실제 매핑에 맞추세요)
        if (!frame.src || frame.src.endsWith('about:blank')) {
            frame.src = '<c:url value="/login"/>'; // 예: /login 또는 /user/login 등
        }
        modal.style.display = 'block';
    }

    function closeLoginModal() {
        const modal = document.getElementById('modal-login');
        modal.style.display = 'none';
        // 선택: 다음에 열 때 초기화하고 싶으면 iframe src 리셋
        // document.getElementById('modal-login-iframe').src = 'about:blank';
    }

    // 열기 버튼 연결 (네비의 로그인 버튼이 .js-login-open 라고 가정)
    document.addEventListener('DOMContentLoaded', function () {
        const openBtn = document.querySelector('.js-login-open');
        if (openBtn) {
            openBtn.addEventListener('click', function (e) {
                e.preventDefault();
                openLoginModal();
            });
        }
        const closeBtn = document.querySelector('#modal-login .modal-close');
        if (closeBtn) {
            closeBtn.addEventListener('click', closeLoginModal);
        }
    });
</script>
