/* ABK UI · 本地 Lucide 图标层（可选） */
(function (global) {
  "use strict";

  const data = global.ABKIconData;
  const icons = data?.libraries?.lucide?.icons;
  const owns = key => Object.prototype.hasOwnProperty.call(icons, key);
  if (!icons || !data.roles?.length || !data.roles.every(owns)) return;

  function hosts(root) {
    const result = Array.from(root.querySelectorAll("[data-abk-icon]"));
    if (root.matches?.("[data-abk-icon]")) result.unshift(root);
    return result;
  }

  function render(host) {
    const role = host.dataset.abkIcon;
    if (!owns(role)) return false;
    // 只解析本地注册表里的 SVG；角色名或外部输入不作为标记插入。
    const template = document.createElement("template");
    template.innerHTML = icons[role];
    const svg = template.content.firstElementChild;
    svg.setAttribute("aria-hidden", "true");
    svg.setAttribute("focusable", "false");
    host.replaceChildren(svg);
    host.dataset.abkIconLibrary = "lucide";
    return true;
  }

  function renderAll(root = document) {
    const targets = hosts(root);
    if (targets.some(host => !owns(host.dataset.abkIcon))) return false;
    targets.forEach(render);
    return true;
  }

  global.ABKIcons = { render, renderAll };
  renderAll();
})(window);
