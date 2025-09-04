// app-home.js
// 라이트 테마 UX 디테일: 탭/카테고리 활성화, 헤더 그림자, 휠 스크롤 개선

// 하단 탭 활성화 표시
document.querySelectorAll('.bottom .tab').forEach(tab=>{
  tab.addEventListener('click', e=>{
    document.querySelectorAll('.bottom .tab').forEach(t=>t.classList.remove('active'));
    tab.classList.add('active');
  });
});

// 카테고리 탭 활성화 및 ARIA 업데이트
document.querySelectorAll('.cats .cat').forEach(cat=>{
  cat.addEventListener('click', ()=>{
    document.querySelectorAll('.cats .cat').forEach(c=>{
      c.classList.remove('active');
      c.setAttribute('aria-selected','false');
    });
    cat.classList.add('active');
    cat.setAttribute('aria-selected','true');
  });
});

// 상단 앱바 스크롤 시 미세한 그림자
const appbar = document.querySelector('.appbar');
if(appbar){
  const onScroll = ()=>{
    appbar.style.boxShadow = (window.scrollY>6) ? '0 4px 14px rgba(10,16,28,.06)' : 'none';
  };
  document.addEventListener('scroll', onScroll, {passive:true});
  onScroll();
}

// 가로 스크롤 영역(카테고리/배너)에서 휠로 좌우 스크롤
document.querySelectorAll('.cats, .banners, .web-banners').forEach(el=>{
  el.addEventListener('wheel', (e)=>{
    if(Math.abs(e.deltaY) > Math.abs(e.deltaX)){
      el.scrollLeft += e.deltaY;
      e.preventDefault();
    }
  }, {passive:false});
});

// app-home.js

// 내비 active 자동 처리
(() => {
  const path = location.pathname.replace(/\/+$/,'') || '/';
  const links = document.querySelectorAll('.web-nav .nav-link');
  let matched = false;
  links.forEach(a => {
    const href = (a.getAttribute('href') || '').replace(/\/+$/,'') || '/';
    if (href !== '#' && href === path) { matched = true; a.classList.add('active'); }
  });
  if (!matched && links.length) links[0].classList.add('active');
})();

// 가로 스크롤 영역에서 휠로 좌우 스크롤
document.querySelectorAll('.banners, .web-banners').forEach(el=>{
  el.addEventListener('wheel', e=>{
    if(Math.abs(e.deltaY) > Math.abs(e.deltaX)){
      el.scrollLeft += e.deltaY;
      e.preventDefault();
    }
  }, {passive:false});
});

