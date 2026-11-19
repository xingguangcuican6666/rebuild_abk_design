/* ABK UI · 图标库对照控件 */
(() => {
  "use strict";

  const selects = Array.from(document.querySelectorAll("[data-demo-icon-library]"));
  const statuses = Array.from(document.querySelectorAll("[data-demo-icon-status]"));
  if (!selects.length) return;
  const icons = window.ABKIcons;
  function status(message) {
    statuses.forEach(element => { element.textContent = message; });
  }
  if (!icons || !icons.renderAll()) {
    status("图标库暂不可切换，当前显示备用图标。");
    return;
  }

  function sync() {
    selects.forEach(select => { select.value = icons.get(); });
  }
  selects.forEach(select => {
    select.disabled = false;
    select.addEventListener("change", () => {
      const applied = icons.set(select.value);
      sync();
      status(applied ? "" : "无法应用这套图标，已保留当前图标库。");
    });
  });
  sync();
  status("");
})();
