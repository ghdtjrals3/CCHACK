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

    <style>
        body { margin:0; font-family: 'Noto Sans KR', sans-serif; background:#f9fafb; }
        .container { display:flex; height:100vh; }
        .sidebar {
            width: 380px; background:#fff; border-right:1px solid #e5e7eb;
            display:flex; flex-direction:column; padding:16px; overflow-y:auto;
        }
        .sidebar h1 { font-size:18px; font-weight:bold; margin-bottom:12px; }
        .search-box { display:flex; margin-bottom:12px; }
        .search-box input {
            flex:1; padding:8px 10px; border:1px solid #ddd; border-radius:8px;
        }
        .tags { display:flex; gap:8px; margin-bottom:16px; }
        .tag {
            font-size:13px; padding:4px 10px; border-radius:9999px; background:#f3f4f6; cursor:pointer;
        }
        .tag.red { background:#fee2e2; color:#b91c1c; }
        .tag.green { background:#dcfce7; color:#15803d; }
        .card {
            background:#fff; border:1px solid #e5e7eb; border-radius:12px;
            padding:12px; margin-bottom:12px; box-shadow:0 2px 4px rgba(0,0,0,0.05);
        }
        .card h3 { margin:0; font-size:15px; display:flex; align-items:center; gap:6px; }
        .card p { margin:4px 0; font-size:13px; color:#555; }
        .map { flex:1; position:relative; }
        .map iframe { width:100%; height:100%; border:0; }
        .fab {
            position:absolute; bottom:20px; right:20px;
            width:60px; height:60px; border-radius:50%; background:#f97316; color:white;
            display:flex; align-items:center; justify-content:center;
            font-size:28px; cursor:pointer; box-shadow:0 4px 8px rgba(0,0,0,0.2);
        }
        .fab:hover { background:#ea580c; }
    </style>
    <!-- commit test -->
</head>
<body>

<%-- 현재 탭 지정 (GNB/하단바 활성화용) --%>
<%
    request.setAttribute("currentTab", "home");
%>

<%-- 공통 내비+모달 (조각) 포함: 이 파일엔 html/head/body가 없어야 함 --%>
<%@ include file="common/common-nav.jsp" %>

<div class="container">
    <!-- Sidebar -->
    <div class="sidebar">
        <h1>쓰레기 해결 현황</h1>
        <div class="search-box">
            <input type="text" placeholder="검색어를 입력하세요..." />
        </div>
        <div class="tags">
            <div class="tag">#전체</div>
            <div class="tag red">#신고</div>
            <div class="tag green">#해결</div>
        </div>

        <!-- 신고 목록 (반복문 가능) -->
        <div class="card">
            <h3>🚨 불법쓰레기 <span class="tag red">신고</span></h3>
            <p>화순군 도곡면 신성리 78</p>
            <p style="color:#999; font-size:12px;">신고자: kjs66450</p>
        </div>

        <div class="card">
            <h3>✅ 쓰레기 근절 방지 <span class="tag green">해결</span></h3>
            <p>이천시 관고동 21-1</p>
            <p style="color:#999; font-size:12px;">신고자: 국곡</p>
        </div>
    </div>

    <!-- Map -->
    <div class="map">
        <iframe src="https://map.kakao.com/" title="map"></iframe>
        <div class="fab">＋</div>
    </div>
</div>

</body>
</html>