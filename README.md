# ABK UI

面向 App 的手写体组件库。先用 HTML / CSS / JS 定稿视觉与交互，定稿后再 1:1 移植到 Kotlin / Compose。

设计取向：**简约而不空泛**——留白多，但每个组件都有明确的层级、状态和反馈；字体统一使用手写体（霞鹜文楷），配色只有纸白、墨色与一个朱砂强调色。

## 快速预览

```bash
python3 -m http.server 8080
# 打开 http://localhost:8080
```

直接双击 `index.html` 也能看，只是 `file://` 下远程字体可能加载更慢。

## 目录

```
assets/css/fonts.css        字体（LXGW WenKai 分片，按 unicode-range 按需下载）
assets/css/tokens.css       设计变量：颜色、字体、间距、圆角、动效、层级
assets/css/base.css         reset、纸张质感、排版、布局原语、可访问性
assets/css/components.css   按钮、徽标、卡片、列表、表格、步骤、进度、骨架屏、空状态
assets/css/forms.css        输入、勾选、单选、卡片单选、开关、设置行、滑杆、分段控件
assets/css/overlays.css     对话框、底部弹层、下拉菜单、提示气泡、Toast
assets/css/navigation.css   顶栏、大标题、标签栏、底部导航、面包屑、侧边目录
assets/css/demo.css         展示页自身的布局（不属于组件库）
assets/js/abk-ui.js         交互层：主题、焦点陷阱、弹层、菜单、Toast、标签页、滑杆
index.html                  组件总览与页面示例
```

引入顺序：`fonts → tokens → base → components → forms → overlays → navigation`，页面自有样式最后。

## 用法

静态页面不需要写脚本，用 `data-abk-*` 声明即可：

```html
<button data-abk-dialog-open="dlg">打开对话框</button>
<button data-abk-sheet-open="sheet">底部弹层</button>
<button data-abk-menu="menu" aria-haspopup="menu" aria-expanded="false">菜单</button>
<button data-abk-toast="已保存" data-abk-toast-type="success">提示</button>
<button data-abk-theme-toggle>切换主题</button>
```

需要动态调用时使用全局 `ABK`：

```js
ABK.toast.show("构建完成", { type: "success", action: "查看", onAction: () => {} });
ABK.dialog.open("dlg");
ABK.sheet.open("sheet");
ABK.theme.set("dark");
```

## 设计约定

- **一个页面一个主按钮**：`btn--primary` 只用于最高频的主路径，其余用描边或幽灵按钮。
- **尺寸对指腹友好**：可点击元素最小 44px（小号 36px），底部导航与弹层考虑安全区。
- **状态不只靠颜色**：错误、警告同时给图标与文案；徽标带圆点形状语义。
- **动效克制**：只做 1–3px 的位移与轻微缩放，统一 120 / 220 / 360ms 三档；`prefers-reduced-motion` 下全部归零。
- **焦点可见**：所有交互元素有 2px 强调色焦点环；对话框与弹层做焦点陷阱并把焦点还给触发元素。
- **手写体只用在标题、按钮和数字**：长正文仍用同一家族但字号行高按阅读优化，避免整页“花”。

## 移植到 Kotlin

| 网页 | Compose |
| --- | --- |
| `--accent` / `--bg` / `--ink` | `MaterialTheme.colorScheme.primary / background / onBackground` |
| `--r-sm/md/lg/xl` | `MaterialTheme.shapes.small/medium/large/extraLarge` |
| `--sp-*` | `Dimens` 对象或 `Arrangement.spacedBy` |
| `--font-hand` | `FontFamily`（同名字体资源） |
| `.btn--primary` 等 | `Button` / `OutlinedButton` / `TextButton` / `FilledTonalButton` |
| `.dialog` | `AlertDialog` / `Dialog` |
| `.sheet` | `ModalBottomSheet` |
| `.menu` | `DropdownMenu` |
| `.segmented` | `SingleChoiceSegmentedButtonRow` |
| `.tabs` | `TabRow` / `ScrollableTabRow` |
| `.bottom-nav` | `NavigationBar` |
| `.toast` | `SnackbarHost` |
| `.progress` / `.ring` | `LinearProgressIndicator` / `CircularProgressIndicator` |
| `.skeleton` | 自定义 `Modifier.placeholder` 或 shimmer |
