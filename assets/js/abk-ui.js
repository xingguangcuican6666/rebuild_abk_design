/* ==========================================================================
   ABK UI · 交互层
   零依赖。所有能力都挂在全局 ABK 上，同时通过 data-abk-* 属性自动绑定，
   让静态 HTML 不需要写任何脚本也能工作。
   ========================================================================== */
(function (global) {
  "use strict";

  const $ = (sel, root = document) => root.querySelector(sel);
  const $$ = (sel, root = document) => Array.from(root.querySelectorAll(sel));

  const FOCUSABLE = [
    "a[href]", "button:not([disabled])", "input:not([disabled])",
    "select:not([disabled])", "textarea:not([disabled])",
    "[tabindex]:not([tabindex='-1'])",
  ].join(",");

  const prefersReducedMotion = () =>
    global.matchMedia?.("(prefers-reduced-motion: reduce)").matches ?? false;

  /* ------------------------------------------------------------ 主题 */
  const THEME_KEY = "abk-ui.theme";

  const theme = {
    get() {
      const saved = safeStorage("get", THEME_KEY);
      if (saved === "light" || saved === "dark") return saved;
      return global.matchMedia?.("(prefers-color-scheme: dark)").matches
        ? "dark"
        : "light";
    },

    set(value, { persist = true } = {}) {
      const next = value === "dark" ? "dark" : "light";
      document.documentElement.dataset.theme = next;
      document.documentElement.style.colorScheme = next;
      if (persist) safeStorage("set", THEME_KEY, next);
      syncThemeControls();
      document.dispatchEvent(
        new CustomEvent("abk:themechange", { detail: { theme: next } })
      );
      return next;
    },

    toggle() {
      return theme.set(theme.get() === "dark" ? "light" : "dark");
    },

    init() {
      // 页面加载前可能已被内联脚本设置；没有就按偏好补上。
      const current = document.documentElement.dataset.theme;
      theme.set(current || theme.get(), { persist: false });
    },
  };

  function syncThemeControls() {
    const isDark = document.documentElement.dataset.theme === "dark";
    $$("[data-abk-theme-toggle]").forEach((btn) => {
      btn.setAttribute("aria-pressed", String(isDark));
      const label = isDark ? btn.dataset.labelLight : btn.dataset.labelDark;
      if (label) btn.setAttribute("aria-label", label);
      const icon = $("[data-abk-theme-icon]", btn);
      if (icon) {
        icon.dataset.abkIcon = isDark ? "sun" : "moon";
        icon.textContent = isDark ? icon.dataset.iconLight ?? "☀" : icon.dataset.iconDark ?? "☾";
        global.ABKIcons?.render(icon);
      }
    });
  }

  function safeStorage(op, key, value) {
    try {
      if (op === "get") return global.localStorage.getItem(key);
      if (op === "set") global.localStorage.setItem(key, value);
      if (op === "del") global.localStorage.removeItem(key);
    } catch {
      /* 隐私模式 / 禁用存储时静默降级 */
    }
    return null;
  }

  /* ------------------------------------------------------------ 焦点 */
  function focusables(root) {
    // 固定定位的祖先会让 offsetParent 为 null，所以用渲染盒判断可见性
    return $$(FOCUSABLE, root).filter(
      (el) => el.getClientRects().length > 0 || el === document.activeElement
    );
  }

  function trapFocus(root, event) {
    if (event.key !== "Tab") return;
    const items = focusables(root);
    if (!items.length) return;
    const first = items[0];
    const last = items[items.length - 1];
    if (event.shiftKey && document.activeElement === first) {
      event.preventDefault();
      last.focus();
    } else if (!event.shiftKey && document.activeElement === last) {
      event.preventDefault();
      first.focus();
    }
  }

  let openLayers = 0;
  const returnFocus = new WeakMap();

  function lockScroll() {
    openLayers += 1;
    if (openLayers === 1) {
      const bar = global.innerWidth - document.documentElement.clientWidth;
      document.body.style.overflow = "hidden";
      if (bar > 0) document.body.style.paddingRight = bar + "px";
    }
  }

  function unlockScroll() {
    openLayers = Math.max(0, openLayers - 1);
    if (openLayers === 0) {
      document.body.style.overflow = "";
      document.body.style.paddingRight = "";
    }
  }

  /* ------------------------------------------------------------ 对话框 */
  const dialog = {
    open(id) {
      const el = typeof id === "string" ? $("#" + id) : id;
      if (!el) return null;
      returnFocus.set(el, document.activeElement);
      if (typeof el.showModal === "function") el.showModal();
      else el.setAttribute("open", "");
      lockScroll();
      const target = $("[autofocus]", el) || focusables(el)[0];
      target?.focus({ preventScroll: true });
      el.addEventListener("keydown", (e) => trapFocus(el, e));
      el.addEventListener(
        "close",
        () => {
          unlockScroll();
          returnFocus.get(el)?.focus?.({ preventScroll: true });
        },
        { once: true }
      );
      return el;
    },

    close(id) {
      const el = typeof id === "string" ? $("#" + id) : id;
      if (!el) return;
      if (typeof el.close === "function") el.close();
      else el.removeAttribute("open");
    },
  };

  /* ------------------------------------------------------------ 底部弹层 */
  function scrimOf(el) {
    return el?.dataset.scrim ? $("#" + el.dataset.scrim) : null;
  }

  const sheet = {
    open(id) {
      const el = typeof id === "string" ? $("#" + id) : id;
      if (!el) return null;
      returnFocus.set(el, document.activeElement);
      el.hidden = false;
      // The sheet is still visibility:hidden until is-open is painted, so focus
      // must wait for that frame; focusing early silently leaves focus outside.
      requestAnimationFrame(() => {
        el.classList.add("is-open");
        requestAnimationFrame(() => {
          const target = $("[autofocus]", el) || focusables(el)[0];
          target?.focus({ preventScroll: true });
        });
      });
      lockScroll();
      const scrim = scrimOf(el);
      if (scrim) {
        scrim.hidden = false;
        requestAnimationFrame(() => scrim.classList.add("is-open"));
      }

      el.onkeydown = (e) => {
        if (e.key === "Escape") {
          e.stopPropagation();
          sheet.close(el);
        } else {
          trapFocus(el, e);
        }
      };
      return el;
    },

    close(id) {
      const el = typeof id === "string" ? $("#" + id) : id;
      if (!el) return;
      el.classList.remove("is-open");
      const scrim = scrimOf(el);
      if (scrim) {
        scrim.classList.remove("is-open");
        setTimeout(() => {
          if (!scrim.classList.contains("is-open")) scrim.hidden = true;
        }, prefersReducedMotion() ? 0 : 380);
      }
      unlockScroll();
      const done = () => {
        el.hidden = true;
        el.onkeydown = null;
      };
      prefersReducedMotion()
        ? done()
        : setTimeout(done, 380);
      returnFocus.get(el)?.focus?.({ preventScroll: true });
    },

    toggle(id) {
      const el = typeof id === "string" ? $("#" + id) : id;
      if (!el) return;
      el.classList.contains("is-open") ? sheet.close(el) : sheet.open(el);
    },
  };

  /* ------------------------------------------------------------ 菜单 */
  const menu = {
    open(trigger) {
      const el = $("#" + trigger.dataset.abkMenu);
      if (!el) return;
      menu.closeAll();
      el.classList.add("is-open");
      trigger.setAttribute("aria-expanded", "true");
      // The menu starts visibility:hidden; wait for its open state to paint before focusing.
      requestAnimationFrame(() => {
        const items = focusables(el);
        items[0]?.focus({ preventScroll: true });
      });
      el.dataset.trigger = trigger.dataset.abkMenu;

      el.onkeydown = (e) => {
        const list = focusables(el);
        const i = list.indexOf(document.activeElement);
        if (e.key === "ArrowDown") {
          e.preventDefault();
          list[(i + 1) % list.length]?.focus();
        } else if (e.key === "ArrowUp") {
          e.preventDefault();
          list[(i - 1 + list.length) % list.length]?.focus();
        } else if (e.key === "Escape") {
          e.stopPropagation();
          menu.close(el);
        }
      };
    },

    close(el) {
      if (!el) return;
      el.classList.remove("is-open");
      el.onkeydown = null;
      const trigger = $('[data-abk-menu="' + el.id + '"]');
      trigger?.setAttribute("aria-expanded", "false");
      trigger?.focus({ preventScroll: true });
    },

    closeAll() {
      $$(".menu.is-open").forEach((el) => menu.close(el));
    },
  };

  /* ------------------------------------------------------------ 提示气泡 */
  const tooltip = {
    attach(el) {
      let node = null;
      const show = () => {
        if (!el.dataset.tip) return;
        node = document.createElement("div");
        node.className = "tooltip";
        node.setAttribute("role", "tooltip");
        node.textContent = el.dataset.tip;
        document.body.appendChild(node);
        const r = el.getBoundingClientRect();
        const t = node.getBoundingClientRect();
        const left = Math.min(
          Math.max(8, r.left + r.width / 2 - t.width / 2),
          global.innerWidth - t.width - 8
        );
        const above = r.top > t.height + 10;
        node.style.left = left + "px";
        node.style.top = (above ? r.top - t.height - 6 : r.bottom + 6) + "px";
        requestAnimationFrame(() => node?.classList.add("is-open"));
      };
      const hide = () => {
        node?.remove();
        node = null;
      };
      el.addEventListener("pointerenter", show);
      el.addEventListener("pointerleave", hide);
      el.addEventListener("focus", show);
      el.addEventListener("blur", hide);
      el.addEventListener("click", hide);
    },
  };

  /* ------------------------------------------------------------ Toast */
  const toast = {
    region() {
      let r = $(".toast-region");
      if (!r) {
        r = document.createElement("div");
        r.className = "toast-region";
        r.setAttribute("role", "status");
        r.setAttribute("aria-live", "polite");
        document.body.appendChild(r);
      }
      return r;
    },

    show(message, opts = {}) {
      const {
        type = "default",
        action,
        onAction,
        duration = 3200,
        icon,
      } = opts;

      const el = document.createElement("div");
      el.className = "toast toast--" + type;

      const iconEl = document.createElement("span");
      iconEl.className = "toast__icon";
      iconEl.textContent = icon ?? { success: "✓", danger: "!", warning: "!", info: "i" }[type] ?? "•";
      iconEl.setAttribute("aria-hidden", "true");
      if (icon == null) {
        iconEl.dataset.abkIcon = { success: "check", danger: "alert-mark", warning: "alert-mark", info: "info-mark" }[type] ?? "dot";
        global.ABKIcons?.render(iconEl);
      }

      const text = document.createElement("span");
      text.className = "toast__text";
      text.textContent = message;

      el.append(iconEl, text);

      if (action) {
        const btn = document.createElement("button");
        btn.className = "toast__action";
        btn.type = "button";
        btn.textContent = action;
        btn.addEventListener("click", () => {
          onAction?.();
          dismiss();
        });
        el.appendChild(btn);
      }

      const dismiss = () => {
        if (el.classList.contains("is-leaving")) return;
        el.classList.add("is-leaving");
        const remove = () => el.remove();
        prefersReducedMotion() ? remove() : setTimeout(remove, 180);
      };

      toast.region().prepend(el);

      if (duration > 0) {
        let timer = setTimeout(dismiss, duration);
        el.addEventListener("pointerenter", () => clearTimeout(timer));
        el.addEventListener("pointerleave", () => {
          timer = setTimeout(dismiss, 1200);
        });
      }
      return el;
    },
  };

  /* ------------------------------------------------------------ 标签页 */
  function initTabs(root) {
    const tabs = $$("[role='tab']", root);
    const panels = $$("[role='tabpanel']", root);

    const activate = (tab) => {
      tabs.forEach((t) => {
        const on = t === tab;
        t.setAttribute("aria-selected", String(on));
        t.tabIndex = on ? 0 : -1;
      });
      panels.forEach((p) => {
        p.hidden = p.id !== tab.getAttribute("aria-controls");
      });
    };

    tabs.forEach((tab, index) => {
      tab.addEventListener("click", () => activate(tab));
      tab.addEventListener("keydown", (e) => {
        let next = null;
        if (e.key === "ArrowRight") next = tabs[(index + 1) % tabs.length];
        if (e.key === "ArrowLeft") next = tabs[(index - 1 + tabs.length) % tabs.length];
        if (e.key === "Home") next = tabs[0];
        if (e.key === "End") next = tabs[tabs.length - 1];
        if (next) {
          e.preventDefault();
          next.focus();
          activate(next);
        }
      });
    });

    const initial = tabs.find((t) => t.getAttribute("aria-selected") === "true") || tabs[0];
    if (initial) activate(initial);
  }

  /* ------------------------------------------------------------ 分段控件 */
  function initSegmented(root) {
    const items = $$(".segmented__item", root);
    items.forEach((item) => {
      item.addEventListener("click", () => {
        items.forEach((i) => {
          const on = i === item;
          i.classList.toggle("is-selected", on);
          i.setAttribute("aria-selected", String(on));
        });
        root.dispatchEvent(
          new CustomEvent("abk:segment", {
            bubbles: true,
            detail: { value: item.dataset.value ?? item.textContent.trim() },
          })
        );
      });
    });
  }

  /* ------------------------------------------------------------ 折叠面板 */
  function initAccordion(root) {
    $$(".accordion__trigger", root).forEach((trigger) => {
      trigger.addEventListener("click", () => {
        const item = trigger.closest(".accordion__item");
        const panel = $("#" + trigger.getAttribute("aria-controls"));
        const open = trigger.getAttribute("aria-expanded") === "true";
        const single = item.closest(".accordion")?.dataset.multiple !== "true";

        if (single) {
          $$(".accordion__item", item.parentElement).forEach((other) => {
            if (other === item) return;
            other.classList.remove("is-open");
            const t = $(".accordion__trigger", other);
            t?.setAttribute("aria-expanded", "false");
            const p = $("#" + t?.getAttribute("aria-controls"));
            if (p) p.hidden = true;
          });
        }

        item.classList.toggle("is-open", !open);
        trigger.setAttribute("aria-expanded", String(!open));
        if (panel) panel.hidden = open;
      });
    });
  }

  /* ------------------------------------------------------------ 滑杆填充 */
  function bindSliders(root) {
    $$("input[type='range']", root).forEach((input) => {
      const sync = () => {
        const min = Number(input.min || 0);
        const max = Number(input.max || 100);
        const pct = ((Number(input.value) - min) / (max - min || 1)) * 100;
        input.style.setProperty("--fill", pct.toFixed(1) + "%");
        const out = $("[data-abk-slider-out]", input.closest(".slider") || document);
        if (out) out.textContent = input.dataset.format
          ? input.dataset.format.replace("{}", input.value)
          : input.value;
      };
      input.addEventListener("input", sync);
      sync();
    });
  }

  /* ------------------------------------------------------------ 代码复制 */
  function initCopy(root) {
    $$("[data-abk-copy]", root).forEach((btn) => {
      btn.addEventListener("click", async () => {
        const target = btn.closest(".code");
        const text = target?.textContent.replace(btn.textContent, "").trim();
        try {
          await navigator.clipboard.writeText(text ?? "");
          toast.show("已复制到剪贴板", { type: "success", duration: 1600 });
        } catch {
          toast.show("复制失败，请手动选择", { type: "danger" });
        }
      });
    });
  }

  /* ------------------------------------------------------------ 顶栏 / 目录 */
  function initScrollSpy() {
    const bar = $(".appbar");
    if (bar) {
      const onScroll = () => {
        bar.dataset.elevated = String(global.scrollY > 4);
      };
      global.addEventListener("scroll", onScroll, { passive: true });
      onScroll();
    }

    const links = $$(".toc__link");
    if (!links.length || !("IntersectionObserver" in global)) return;

    const targets = links
      .map((l) => $(l.getAttribute("href")))
      .filter(Boolean);
    if (!targets.length) return;

    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (!entry.isIntersecting) return;
          links.forEach((l) =>
            l.classList.toggle(
              "is-active",
              l.getAttribute("href") === "#" + entry.target.id
            )
          );
        });
      },
      { rootMargin: "-20% 0px -65% 0px" }
    );
    targets.forEach((t) => observer.observe(t));
  }

  /* ------------------------------------------------------------ 事件委托 */
  function bindDelegates() {
    document.addEventListener("click", (e) => {
      const openDialog = e.target.closest("[data-abk-dialog-open]");
      if (openDialog) {
        e.preventDefault();
        dialog.open(openDialog.dataset.abkDialogOpen);
        return;
      }

      const closeDialog = e.target.closest("[data-abk-dialog-close]");
      if (closeDialog) {
        e.preventDefault();
        dialog.close(closeDialog.dataset.abkDialogClose || closeDialog.closest("dialog"));
        return;
      }

      const openSheet = e.target.closest("[data-abk-sheet-open]");
      if (openSheet) {
        e.preventDefault();
        sheet.open(openSheet.dataset.abkSheetOpen);
        return;
      }

      const closeSheet = e.target.closest("[data-abk-sheet-close]");
      if (closeSheet) {
        e.preventDefault();
        sheet.close(closeSheet.dataset.abkSheetClose || closeSheet.closest(".sheet"));
        return;
      }

      const menuTrigger = e.target.closest("[data-abk-menu]");
      if (menuTrigger) {
        e.preventDefault();
        const el = $("#" + menuTrigger.dataset.abkMenu);
        el?.classList.contains("is-open") ? menu.close(el) : menu.open(menuTrigger);
        return;
      }

      if (!e.target.closest(".menu")) menu.closeAll();

      const toastBtn = e.target.closest("[data-abk-toast]");
      if (toastBtn) {
        e.preventDefault();
        toast.show(toastBtn.dataset.abkToast, {
          type: toastBtn.dataset.abkToastType || "default",
          action: toastBtn.dataset.abkToastAction || undefined,
        });
      }
    });

    document.addEventListener("keydown", (e) => {
      if (e.key === "Escape") menu.closeAll();
    });
  }

  /* ------------------------------------------------------------ 初始化 */
  function init(root = document) {
    theme.init();
    syncThemeControls();
    $("[role='tablist']", root) && initTabs(root);
    $$(".segmented", root).forEach(initSegmented);
    initAccordion(root);
    bindSliders(root);
    initCopy(root);
    $$("[data-tip]", root).forEach(tooltip.attach);

    $$("[data-abk-theme-toggle]", root).forEach((btn) =>
      btn.addEventListener("click", () => theme.toggle())
    );

    // 点击遮罩关闭引用它的底部弹层
    $$(".scrim[id]", root).forEach((scrim) => {
      scrim.addEventListener("click", () => {
        const owner = $$(".sheet").find((s) => s.dataset.scrim === scrim.id);
        if (owner) sheet.close(owner);
      });
    });

    initScrollSpy();
    bindDelegates();
  }

  global.ABK = { theme, toast, dialog, sheet, menu, tooltip, init };

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", () => init());
  } else {
    init();
  }
})(window);
