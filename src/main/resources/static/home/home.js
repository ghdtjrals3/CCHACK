// 탭(카테고리) 활성 토글
document.querySelectorAll('.cats .cat').forEach((btn) => {
  btn.addEventListener('click', () => {
    document.querySelectorAll('.cats .cat').forEach(b => b.classList.remove('active'));
    btn.classList.add('active');
  });
});

// 모바일 하단 공통 네비(nav.css)의 활성 표시는 URL 기준으로 처리
(function markActiveBottomNav() {
  const path = location.pathname;
  const map = [
    { sel: '.bn-item:nth-child(1)', match: /^\/$/ },
    { sel: '.bn-item:nth-child(2)', match: /^\/trash/ },
    { sel: '.bn-item:nth-child(3)', match: /^\/personal/ },
    { sel: '.bn-item:nth-child(4)', match: /^\/center/ },
    { sel: '.bn-item:nth-child(5)', match: /^\/info/ },
  ];
  map.forEach(({sel, match}) => {
    const el = document.querySelector(sel);
    if (el) el.classList.toggle('active', match.test(path));
  });
})();

// 상단 네비(active)
(function markActiveTopNav() {
  const path = location.pathname;
  document.querySelectorAll('.site-header .nav-link').forEach(a => {
    const href = a.getAttribute('href') || '';
    try {
      const u = new URL(href, location.origin);
      a.classList.toggle('active',
        (u.pathname === '/' && path === '/') ||
        (u.pathname !== '/' && path.startsWith(u.pathname))
      );
    } catch (_) {}
  });
})();
