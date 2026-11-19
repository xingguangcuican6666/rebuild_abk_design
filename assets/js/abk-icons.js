/* ABK UI · 本地图标层（可选） */
(function (global) {
  "use strict";

  const data = global.ABKIconData;
  if (!data?.roles?.length || !data.libraries) return;
  const owns = (object, key) => Object.prototype.hasOwnProperty.call(object, key);
  const libraries = Object.keys(data.libraries).filter(key =>
    data.roles.every(role => owns(data.libraries[key].icons, role))
  );
  if (libraries.length !== 4 || !libraries.includes("lucide")) return;
  let current = "lucide";

  function hosts(root) {
    const result = Array.from(root.querySelectorAll("[data-abk-icon]"));
    if (root.matches?.("[data-abk-icon]")) result.unshift(root);
    return result;
  }

  function render(host) {
    const role = host.dataset.abkIcon;
    const icons = data.libraries[current].icons;
    if (!owns(icons, role)) return false;
    // 只解析本地注册表里的 SVG；角色名或外部输入不作为标记插入。
    const template = document.createElement("template");
    template.innerHTML = icons[role];
    const svg = template.content.firstElementChild;
    svg.setAttribute("aria-hidden", "true");
    svg.setAttribute("focusable", "false");
    host.replaceChildren(svg);
    host.dataset.abkIconLibrary = current;
    return true;
  }

  function renderAll(root = document) {
    const targets = hosts(root);
    if (targets.some(host => !owns(data.libraries[current].icons, host.dataset.abkIcon))) return false;
    targets.forEach(render);
    return true;
  }

  function set(library) {
    if (!libraries.includes(library)) return false;
    if (hosts(document).some(host => !owns(data.libraries[library].icons, host.dataset.abkIcon))) return false;
    current = library;
    return renderAll();
  }

  global.ABKIcons = {
    get: () => current,
    libraries: libraries.map(key => ({ key, name: data.libraries[key].name })),
    render,
    renderAll,
    set,
  };
  renderAll();
})(window);
