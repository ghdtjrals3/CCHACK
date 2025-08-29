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
</head>
<body>

<%-- 현재 탭 지정 (GNB/하단바 활성화용) --%>
<%
    request.setAttribute("currentTab", "home");
%>

<%-- 공통 내비+모달 (조각) 포함: 이 파일엔 html/head/body가 없어야 함 --%>
<%@ include file="common/common-nav.jsp" %>

<main class="container">
    <h1>Index 페이지</h1>
    <p>여기에 페이지 전용 콘텐츠를 넣으세요.</p>
</main>

</body>
</html>