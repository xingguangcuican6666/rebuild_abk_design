# 本地图标子集

四套图标暂用于展示页比较，尚未选定最终库：Lucide、Tabler Outline、Phosphor Regular、Material Symbols Rounded。`icon-data.js` 是已生成的经典脚本，导出 `window.ABKIconData`，只含 `sources.json` 中列出的角色。预览不需要 Python、curl、npm、网络图标字体或构建步骤，支持直接打开 `file://`。

## 来源与许可

固定提交、原始图标名及路径以 [sources.json](sources.json) 为准；同一角色数组按 `libraries` 的顺序对应四库。

| 库 | 官方仓库 | 许可文件 |
| --- | --- | --- |
| Lucide | https://github.com/lucide-icons/lucide | [ISC 及源自 Feather 图标的 MIT 声明](LICENSE-lucide) |
| Tabler Outline | https://github.com/tabler/tabler-icons | [MIT](LICENSE-tabler) |
| Phosphor Regular | https://github.com/phosphor-icons/core | [MIT](LICENSE-phosphor) |
| Material Symbols Rounded | https://github.com/google/material-design-icons | [Apache-2.0](LICENSE-material) |

SVG 保留原始 `viewBox`、路径、描边和填充方式。生成时去除固定宽高与非图形元数据，让外部 CSS 控制显示尺寸；未声明 `fill` 的根节点补 `currentColor`，装饰语义由渲染器添加。不统一覆盖 `stroke` 或把填充路径改成描边。

`info-mark` 专用于现有圆形状态载体，避免再套一圈：Lucide 使用 `letter-i` 并在固定上游竖线路径上补回同一图标的 `i` 点；Tabler 使用 `info-small`，Material 使用 `info_i`。Phosphor 使用 `info` 的原始内部信息标记，按 `transforms` 中的精确路径前缀移除外圈并保留内部信息符号。这些是局部几何处理，不代表上游提供了完全相同的独立图标；目录中的完整 `info` 角色不变。

## 重新生成

在仓库根目录运行（Python 3 标准库及 curl）：

```bash
python3 assets/vendor/icons/regenerate.py
node --check assets/vendor/icons/icon-data.js
```

脚本从固定提交下载、校验受支持的 SVG 元素与属性、保存完整许可证，再生成注册表。不会在预览时下载。网络失败时命令报错且不覆盖现有注册表；更新来源或映射后应核对四套角色覆盖并重新做浅深色、小尺寸及 `file://` 验收。日常页面修改不必重新运行。

## 接入

按 `icon-data.js → abk-icons.js → abk-ui.js` 加载经典 `defer` 脚本；展示页再加载 `demo-icons.js`。可选宿主示例：

```html
<span data-abk-icon="download" aria-hidden="true">↧</span>
```

宿主内保留备用符号。图标层只更新内部 SVG，不替换宿主按钮或链接；`ABKIcons.set("tabler")` 切换全页，`ABKIcons.render(host)` 用于组件新建图标。没有图标层时，`abk-ui.js` 仍可独立工作。显式 `ABK.toast.show(..., { icon: "自定义文字" })` 保留为自定义文本，不随库切换。
