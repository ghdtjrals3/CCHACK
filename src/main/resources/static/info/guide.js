document.addEventListener("DOMContentLoaded", () => {
  const tabs = document.querySelectorAll(".tab");
  const sections = document.querySelectorAll(".section");
  tabs.forEach(btn => {
    btn.addEventListener("click", () => {
      tabs.forEach(b => b.classList.remove("active"));
      btn.classList.add("active");
      sections.forEach(sec => sec.hidden = true);
      document.getElementById(btn.dataset.tab).hidden = false;
      window.scrollTo({ top: document.querySelector(".tabs").offsetTop - 8, behavior: "smooth" });
    });
  });
});
document.addEventListener("DOMContentLoaded", () => {
  // 상단/하단 네비 활성화
  const path = location.pathname;
  document.querySelectorAll('[data-route]').forEach(a => {
    const r = a.getAttribute('data-route');
    if (path.startsWith(r)) a.classList.add('active');
    else a.classList.remove('active');
  });

  // --- 기존 "재활용/음식물/분리배출/수거업체" 탭 전환 ---
  const tabs = document.querySelectorAll(".tab");
  const sections = document.querySelectorAll(".section");
  tabs.forEach(btn => {
    btn.addEventListener("click", () => {
      tabs.forEach(b => b.classList.remove("active"));
      btn.classList.add("active");
      sections.forEach(sec => sec.hidden = true);
      document.getElementById(btn.dataset.tab).hidden = false;
      window.scrollTo({ top: document.querySelector(".tabs").offsetTop - 8, behavior: "smooth" });
    });
  });
});
document.addEventListener("DOMContentLoaded", () => {
  const norm = s => (s || "").replace(/\/+$/,""); // 끝 슬래시 제거
  const path = norm(location.pathname);

  document.querySelectorAll("[data-route]").forEach(el => {
    const route = norm(el.getAttribute("data-route"));
    const active = (route === "" || route === "/") ? (path === "" || path === "/")
                                                   : path.startsWith(route);
    if (active) el.classList.add("active");
  });

  // --- (기존 콘텐츠 탭 전환 로직 그대로 유지) ---
  const tabs = document.querySelectorAll(".tab");
  const sections = document.querySelectorAll(".section");
  tabs.forEach(btn => {
    btn.addEventListener("click", () => {
      tabs.forEach(b => b.classList.remove("active"));
      btn.classList.add("active");
      sections.forEach(sec => sec.hidden = true);
      document.getElementById(btn.dataset.tab).hidden = false;
      window.scrollTo({ top: document.querySelector(".tabs").offsetTop - 8, behavior: "smooth" });
    });
  });
});

