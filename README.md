# ABK UI

面向 App 的手写体组件库。先用 HTML / CSS / JS 定稿视觉与交互，定稿后再 1:1 移植到 Kotlin / Compose。

设计取向：**简约而不空泛**——留白多，但每个组件都有明确的层级、状态和反馈；字体统一使用手写体（霞鹜文楷）。全页统一使用 Material You 动态配色。

项目状态、定稿验收项和后续移植清单见 [HANDOFF.md](HANDOFF.md)。

## 快速预览

在仓库根目录用 Bash 执行。只预览已提交的快照，并只监听本机：

```bash
(
  set -o pipefail
  preview=$(mktemp -d) || exit 1
  trap 'rm -rf -- "$preview"' EXIT
  git archive HEAD | tar -xf - -C "$preview" || exit 1
  python3 -I -m http.server 8080 --bind 127.0.0.1 --directory "$preview"
)
```

打开 `http://127.0.0.1:8080`，按 `Ctrl+C` 停止服务并清理预览目录。若只想看未提交的页面改动，可以直接在浏览器打开 `index.html`；不要从仓库根目录启动 HTTP 服务。

### 动态配色

官方 Material Color Utilities 的 Tonal Spot（2021）从同一个种子生成浅色和深色。默认种子是 `#496a78`；在「设计变量 → 种子颜色」可以实时预览其他种子，不读取系统壁纸。右上角单独控制明暗。

`tokens.css` 自带默认种子的浅深色，脚本不可用时也不会换成另一套风格。更换种子由本地打包的脚本支持，预览无需安装依赖。更新第三方库时参照[本地打包说明](assets/vendor/material-color-utilities/README.md)。

### 图标库对照

「图标」区与「页面示例」上方各有一个同步选择器，可切换 Lucide、Tabler Outline、Phosphor Regular、Material Symbols Rounded；默认 Lucide，四套目前用于比较，尚未定稿。切换整页静态图标及 Toast，不改变种子颜色、明暗或底栏当前项，也不保存图标库偏好。

图标为本地固定来源的 SVG 子集，支持 `file://`，无需在线图标字体或额外依赖。来源、完整许可及重建方法见[图标说明](assets/vendor/icons/README.md)。图标脚本不可用时保留备用符号，切换控件禁用。霞鹜文楷的既有字体请求不受影响。

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
assets/js/abk-icons.js      可选本地图标渲染层：语义角色、单个与全页刷新
assets/js/demo-icons.js     展示页两处图标库选择器同步
assets/vendor/icons/       固定来源的四库 SVG 子集、映射、许可证与重建脚本
assets/js/demo-palette.js   动态配色：把种子生成的 Material 角色映射到 ABK 颜色变量
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
- **底部导航入口等权**：图标、文字和点击区一致；当前页只用中性短标记和 `aria-current="page"`，不设凸起的主入口。
- **构建状态用局部色**：构建卡片用普通面色，仅进度等状态控件用动态强调色。带操作的 Toast 也跟随当前明暗。
- **状态不只靠颜色**：错误、警告同时给图标与文案；徽标带圆点形状语义。
- **动效克制**：只做 1–3px 的位移与轻微缩放，统一 120 / 220 / 360ms 三档；`prefers-reduced-motion` 下全部归零。
- **焦点可见**：交互元素有可见焦点环，底栏使用中性色焦点环；对话框与弹层做焦点陷阱并把焦点还给触发元素。
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
| `.bottom-nav` | `NavigationBar` / `NavigationBarItem`，去掉默认选中胶囊，保留等权布局、中性位置标记和当前项语义 |
| `[data-abk-icon]` | 定稿图标的 `ImageVector` / VectorDrawable，保留同一造型与文字语义 |
| `.toast` | `SnackbarHost` |
| `.progress` / `.ring` | `LinearProgressIndicator` / `CircularProgressIndicator` |
| `.skeleton` | 自定义 `Modifier.placeholder` 或 shimmer |
