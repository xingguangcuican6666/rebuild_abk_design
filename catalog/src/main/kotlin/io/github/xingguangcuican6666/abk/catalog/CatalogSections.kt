package io.github.xingguangcuican6666.abk.catalog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.xingguangcuican6666.abk.ui.components.*
import io.github.xingguangcuican6666.abk.ui.icons.AbkIcon
import io.github.xingguangcuican6666.abk.ui.icons.AbkIcons
import io.github.xingguangcuican6666.abk.ui.theme.*
import kotlinx.coroutines.launch

/**
 * Same inventory as the web catalog: every component with every variant and state on one page.
 * Sample state lives here, never in the library.
 */
internal enum class CatalogSection(val title: String, val desc: String) {
    Tokens("设计变量", "Material You 从同一个种子色生成浅色与深色。更换种子会更新全页配色，明暗单独控制。"),
    Typography("字体与排版", "标题带一点笔锋，正文同一家族保证气质统一；等宽用于数值、路径和命令。"),
    Icons("图标", "Lucide 已定稿，与网页共用同一份语义角色；图标跟随文字颜色，导航保持等权。"),
    Buttons("按钮", "一个页面只允许一个主按钮。次级操作用描边或幽灵，破坏性操作配危险色与二次确认。"),
    Forms("表单", "标签始终在框外，错误状态用底色 + 文案双重提示，不依赖颜色单一通道。"),
    Toggles("开关与选择", "开关用于立即生效的设置；需要提交的选择用勾选或卡片式单选。"),
    Segmented("分段控件", "用于 2–4 个互斥视图的快速切换，比下拉更直观。"),
    Navigation("导航", "移动端主结构是吸顶栏 + 底部导航；顶部栏滚动后才出现分隔线。"),
    Collections("卡片、列表与标签", "卡片用普通面色承载内容，徽标与标签只在状态处着色。"),
    Data("数据展示", "统计、键值、代码与表格；数值用等宽字体避免对齐跳动。"),
    Sequence("步骤与时间线", "构建流程的进度与历史，完成、当前、待处理同时用图标和文字表达。"),
    Alerts("提示条", "四种语气，状态同时以图标和文字说明，不只依赖颜色。"),
    Progress("进度与加载", "确定、不确定、条纹与骨架屏；减少动效时保留静态度量与语义。"),
    Empty("空状态", "说明为什么为空，以及下一步可以做什么。"),
    Toast("Toast", "排队显示，带语气、动作与关闭；撤销结果由宿主处理。"),
    Overlays("浮层", "对话框、底部弹层、菜单与提示；关闭浮层与发出反馈是两个明确动作。"),
    Composition("页面组合", "把组件放到一起的少量示例，全部只操作本地样例。"),
}

@Composable
internal fun CatalogSectionContent(
    section: CatalogSection, snackbars: AbkSnackbarHostState, feedback: (String) -> Unit,
    onOpenBuild: () -> Unit, onOpenModules: () -> Unit,
) {
    when (section) {
        CatalogSection.Tokens -> TokenSamples()
        CatalogSection.Typography -> TypographySamples()
        CatalogSection.Icons -> IconSamples()
        CatalogSection.Buttons -> ButtonSamples(feedback)
        CatalogSection.Forms -> FormSamples()
        CatalogSection.Toggles -> ToggleSamples()
        CatalogSection.Segmented -> SegmentedSamples()
        CatalogSection.Navigation -> NavigationSamples(feedback, onOpenBuild, onOpenModules)
        CatalogSection.Collections -> CollectionSamples(feedback)
        CatalogSection.Data -> DataSamples(feedback)
        CatalogSection.Sequence -> SequenceSamples()
        CatalogSection.Alerts -> AlertSamples()
        CatalogSection.Progress -> ProgressSamples()
        CatalogSection.Empty -> EmptySamples(feedback)
        CatalogSection.Toast -> ToastSamples(snackbars, feedback)
        CatalogSection.Overlays -> OverlaySamples(snackbars, feedback)
        CatalogSection.Composition -> CompositionSamples(feedback, onOpenBuild, onOpenModules)
    }
}

/** A labeled group, matching the web catalog's `demo-block` + `demo-label`. */
@Composable
internal fun CatalogBlock(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        AbkEyebrow(title)
        content()
    }
}

@Composable
internal fun CatalogSectionHeader(index: Int, section: CatalogSection) {
    AbkEyebrow("%02d · 组件".format(index))
    AbkHeading(section.title)
    AbkText(section.desc, color = AbkTheme.colors.inkMuted)
}

@Composable
private fun TokenSamples() {
    val c = AbkTheme.colors
    val swatches = listOf(
        Triple("页面底色", "background", c.background), Triple("浮起", "raised", c.raised),
        Triple("凹陷", "sunken", c.sunken), Triple("输入框", "input", c.input),
        Triple("墨", "ink", c.ink), Triple("次级文字", "inkSoft", c.inkSoft),
        Triple("辅助文字", "inkMuted", c.inkMuted), Triple("淡墨", "inkFaint", c.inkFaint),
        Triple("分隔线", "line", c.line), Triple("强分隔线", "lineStrong", c.lineStrong),
        Triple("主强调色", "accent", c.accent.color), Triple("成功", "success", c.success.color),
        Triple("警告", "warning", c.warning.color), Triple("危险", "danger", c.danger.color),
        Triple("信息", "info", c.info.color), Triple("遮罩", "scrim", c.scrim),
    )
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        CatalogBlock("当前配色 · 颜色角色") {
            SwatchGrid(swatches)
        }
        CatalogBlock("状态容器") {
            SwatchGrid(listOf(AbkTone.Accent, AbkTone.Success, AbkTone.Warning, AbkTone.Danger, AbkTone.Info).map {
                Triple(it.name, it.name.lowercase(), c.status(it).container)
            })
        }
        CatalogBlock("圆角") {
            AbkCluster {
                listOf("Small" to AbkShapes.Small, "Medium" to AbkShapes.Medium, "Large" to AbkShapes.Large,
                    "ExtraLarge" to AbkShapes.ExtraLarge, "Pill" to AbkShapes.Pill).forEach { (name, shape) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(Modifier.size(56.dp).clip(shape).background(c.sunken).border(1.dp, c.line, shape))
                        AbkText(name, style = MaterialTheme.typography.labelSmall, color = c.inkMuted)
                    }
                }
            }
        }
        CatalogBlock("间距") {
            AbkPanel {
                listOf("Xs" to AbkSpacing.Xs, "Sm" to AbkSpacing.Sm, "Md" to AbkSpacing.Md, "Lg" to AbkSpacing.Lg,
                    "Xl" to AbkSpacing.Xl, "Xxl" to AbkSpacing.Xxl, "Section" to AbkSpacing.Section,
                    "Wide" to AbkSpacing.Wide, "Hero" to AbkSpacing.Hero).forEach { (name, value) ->
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        AbkText(name, Modifier.width(64.dp), style = MaterialTheme.typography.labelSmall, color = c.inkMuted)
                        Box(Modifier.width(value).height(10.dp).clip(AbkShapes.Pill).background(c.accent.color))
                        AbkText("${value.value.toInt()}dp", style = MaterialTheme.typography.labelSmall, color = c.inkFaint)
                    }
                }
            }
        }
    }
}

@Composable
private fun SwatchGrid(items: List<Triple<String, String, Color>>) {
    val c = AbkTheme.colors
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items.chunked(4).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { (name, token, color) ->
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(Modifier.fillMaxWidth().height(44.dp).clip(AbkShapes.Medium).background(color)
                            .border(1.dp, c.line, AbkShapes.Medium))
                        AbkText(name, style = MaterialTheme.typography.labelSmall)
                        AbkText(token, style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                            color = c.inkFaint)
                    }
                }
                repeat(4 - row.size) { Box(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun TypographySamples() {
    val typography = MaterialTheme.typography
    AbkPanel(grain = true) {
        AbkEyebrow("基础排版 · 可选纸纹")
        AbkHeading("一笔一画，把事情写清楚", large = true)
        AbkText("正文保留舒适的行高，长段落每行不超过 60 个字符。中文、Latin 与数字 12345 共享清晰的层级。")
        AbkDivider()
        listOf(
            "3xl · display" to typography.displayLarge,
            "2xl · headline" to typography.headlineLarge,
            "xl · title" to typography.titleLarge,
            "md · body" to typography.bodyLarge,
            "sm · body small" to typography.bodySmall,
            "xs · label" to typography.labelSmall,
        ).forEach { (meta, style) ->
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                AbkText(meta, style = MaterialTheme.typography.labelSmall, color = AbkTheme.colors.inkMuted)
                AbkText("今天的构建已完成", style = style)
            }
        }
        AbkText("6.1.99-android14 · sha256:34e5e843", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace))
        AbkDivider()
        AbkCluster { AbkInlineCode("./gradlew build"); AbkKeycap("Ctrl"); AbkKeycap("K"); AbkHighlight("轻量，不单调") }
    }
}

@Composable
private fun IconSamples() {
    AbkText("每个图标都来自网页同一份 Lucide 注册表，方向性图标自动镜像。", color = AbkTheme.colors.inkMuted)
    AbkCluster {
        AbkIcons.all.forEach { (role, vector) ->
            AbkCard {
                AbkIcon(vector, role, size = 22.dp)
                AbkText(role, style = MaterialTheme.typography.labelSmall, color = AbkTheme.colors.inkMuted)
            }
        }
    }
}

@Composable
private fun ButtonSamples(feedback: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        CatalogBlock("层级") {
            AbkCluster {
                AbkButtonStyle.entries.forEach { style -> AbkButton({ feedback("${style.name} 按钮") }, style = style) { Text(style.name) } }
            }
        }
        CatalogBlock("尺寸与状态") {
            AbkCluster {
                AbkSize.entries.forEach { size -> AbkButton({ feedback("${size.name} 尺寸") }, size = size) { Text(size.name) } }
            }
            AbkCluster {
                AbkButton({}, enabled = false) { Text("禁用") }
                AbkButton({}, loading = true) { Text("保存中") }
                AbkIconButton(AbkIcons.Refresh, "刷新", { feedback("刷新") })
                AbkIconButton(AbkIcons.More, "更多", { feedback("更多") }, size = AbkSize.Small)
                AbkButton({ feedback("已刷新") }, style = AbkButtonStyle.Soft) { AbkIcon(AbkIcons.Refresh, null); Text("刷新") }
            }
        }
        CatalogBlock("整行按钮 · 底部操作区") {
            Column(Modifier.width(320.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AbkButton({ feedback("开始构建（示例）") }, Modifier.fillMaxWidth(), style = AbkButtonStyle.Primary) { Text("开始构建") }
                AbkButton({ feedback("稍后再说") }, Modifier.fillMaxWidth(), style = AbkButtonStyle.Ghost) { Text("稍后再说") }
            }
        }
    }
}

@Composable
private fun FormSamples() {
    var name by rememberSaveable { mutableStateOf("Pixel 8 Pro") }
    var search by rememberSaveable { mutableStateOf("") }
    var command by rememberSaveable { mutableStateOf("make -j8 O=out") }
    var repo by rememberSaveable { mutableStateOf("github.com/kernel") }
    var choice by rememberSaveable { mutableStateOf("stable") }
    var notes by rememberSaveable { mutableStateOf("") }
    val options = listOf(AbkOption("stable", "稳定", "适合日常使用"), AbkOption("preview", "预览", "尝试新功能"),
        AbkOption("off", "不可用", enabled = false))
    Column(Modifier.width(420.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
        CatalogBlock("输入框") {
            AbkTextField(name, { name = it }, "设备名称", placeholder = "例如：Pixel 8 Pro", hint = "会写入产物 manifest，不影响构建。")
            AbkTextField(search, { search = it }, "搜索", placeholder = "搜索模块、补丁或提交", optionalLabel = "可选",
                prefix = { AbkIcon(AbkIcons.Search, null) },
                suffix = { AbkIconButton(AbkIcons.Close, "清空搜索", { search = "" }, size = AbkSize.Small) })
            AbkTextField(command, { command = it }, "构建命令", hint = "在源码根目录执行。", prefix = { AbkText("$") })
            AbkTextField(repo, { repo = it }, "源码仓库", error = "地址无效，需要以 .git 结尾的 HTTPS 地址")
            AbkTextField(notes, { notes = it }, "备注", placeholder = "记录这次构建的目的…", singleLine = false, minLines = 3, maxLines = 6)
            AbkTextField("ABK-2026.11.19-001", {}, "只读字段", readOnly = true)
            AbkTextField("当前不可修改", {}, "禁用字段", enabled = false)
            AbkSelect(options, choice, { choice = it }, "分支", hint = "下拉选项支持禁用项。")
            AbkSelect(options, null, {}, "禁用下拉", enabled = false)
        }
    }
}

@Composable
private fun ToggleSamples() {
    var checked by rememberSaveable { mutableIntStateOf(2) }
    var radio by rememberSaveable { mutableStateOf("stable") }
    var card by rememberSaveable { mutableStateOf("upstream") }
    var enabled by rememberSaveable { mutableStateOf(true) }
    var small by rememberSaveable { mutableStateOf(false) }
    var tasks by rememberSaveable { mutableFloatStateOf(0.5f) }
    val options = listOf(AbkOption("stable", "稳定", "适用于绝大多数设备"), AbkOption("preview", "预览", "模拟器与部分平板"),
        AbkOption("off", "不可用", enabled = false))
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        CatalogBlock("勾选与单选") {
            AbkCheckbox(ToggleableState.entries[checked], { checked = (checked + 1) % 3 }, "三态选择",
                description = "点击轮换未选、选中与半选")
            AbkCheckbox(ToggleableState.Off, {}, "禁用复选框", enabled = false)
            AbkField("单选组") { AbkRadioGroup(options, radio, { radio = it }) }
        }
        CatalogBlock("卡片式单选") {
            AbkField("跟随上游 or 固定版本") {
                AbkRadioGroup(listOf(AbkOption("upstream", "跟随上游", "自动使用最新 SDK，适合日常使用。"),
                    AbkOption("fixed", "固定版本", "锁定到指定提交，可复现构建结果。")), card, { card = it }, cards = true)
            }
        }
        CatalogBlock("开关与设置行") {
            AbkSwitch(enabled, { enabled = it }, "自动重试")
            AbkSwitch(small, { small = it }, "小号", size = AbkSize.Small)
            AbkSwitch(false, {}, "禁用", enabled = false)
            AbkSettingSwitch("使用缓存加速构建", "复用上次的 ccache，首次构建会更慢。", enabled, { enabled = it })
            AbkSettingSwitch("构建完成后通知", "结果推送会发送到手机。", small, { small = it })
        }
        CatalogBlock("滑杆") {
            AbkSlider(tasks, { tasks = it }, "并行编译任务", steps = 7, valueLabel = "${1 + (tasks * 16).toInt()}")
            AbkSlider(0.3f, {}, "禁用滑杆", enabled = false, valueLabel = "30%")
        }
    }
}

@Composable
private fun SegmentedSamples() {
    var view by rememberSaveable { mutableStateOf("all") }
    var range by rememberSaveable { mutableStateOf("day") }
    var theme by rememberSaveable { mutableStateOf("light") }
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        CatalogBlock("常规 · 小号") {
            AbkCluster {
                AbkSegmented(listOf(AbkOption("all", "全部"), AbkOption("running", "构建中"), AbkOption("failed", "失败")),
                    view, { view = it })
                AbkSegmented(listOf(AbkOption("day", "日"), AbkOption("week", "周"), AbkOption("month", "月")),
                    range, { range = it }, size = AbkSize.Small)
            }
        }
        CatalogBlock("整行 segmented") {
            AbkSegmented(listOf(AbkOption("light", "浅色"), AbkOption("dark", "深色"), AbkOption("system", "跟随系统")),
                theme, { theme = it }, Modifier.fillMaxWidth())
        }
        CatalogBlock("不可用选项") {
            AbkSegmented(listOf(AbkOption("a", "可用"), AbkOption("b", "暂不可用", enabled = false)), "a", {})
        }
    }
}

@Composable
private fun NavigationSamples(feedback: (String) -> Unit, onOpenBuild: () -> Unit, onOpenModules: () -> Unit) {
    var tab by rememberSaveable { mutableStateOf("all") }
    var scroll by rememberSaveable { mutableStateOf("0") }
    var destination by rememberSaveable { mutableStateOf("build") }
    var wide by rememberSaveable { mutableStateOf("build") }
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        CatalogBlock("顶部栏 · 未滚动 / 已滚动") {
            AbkTopBar("页面标题", subtitle = "未滚动时与内容连成一片", navigationIcon = {
                AbkIconButton(AbkIcons.Back, "返回示例", { feedback("返回") })
            }, actions = { AbkIconButton(AbkIcons.More, "更多操作", { feedback("更多") }) })
            AbkDivider()
            AbkTopBar("页面标题", subtitle = "滚动后出现分隔线", scrolled = true)
        }
        CatalogBlock("大标题页头") {
            AbkHero("大标题页头", eyebrow = "页面眉题", description = "说明与动作均为可选插槽",
                actions = { AbkButton({ feedback("页头动作") }) { Text("页头动作") } })
        }
        CatalogBlock("标签栏 · 固定与滚动") {
            AbkTabs(listOf(AbkTabItem("all", "全部", 8), AbkTabItem("done", "完成", 6), AbkTabItem("off", "不可用", enabled = false)),
                tab, { tab = it })
            AbkTabs((0..6).map { AbkTabItem(it.toString(), "分类 $it", it) }, scroll, { scroll = it }, scrollable = true)
        }
        CatalogBlock("面包屑") {
            AbkBreadcrumb(listOf(AbkBreadcrumbItem("首页") { feedback("首页") }, AbkBreadcrumbItem("组件") { feedback("组件") },
                AbkBreadcrumbItem("导航")))
        }
        CatalogBlock("底部导航 · 等权 3 / 5 个入口") {
            AbkBottomNavigation(listOf(AbkNavigationItem("build", "构建", AbkIcons.Build), AbkNavigationItem("modules", "模块", AbkIcons.Modules),
                AbkNavigationItem("settings", "设置", AbkIcons.Settings)), destination, { destination = it })
            AbkBottomNavigation(listOf(AbkNavigationItem("home", "首页", AbkIcons.Home), AbkNavigationItem("build", "构建", AbkIcons.Build),
                AbkNavigationItem("modules", "模块", AbkIcons.Modules), AbkNavigationItem("settings", "设置", AbkIcons.Settings),
                AbkNavigationItem("more", "更多", AbkIcons.More)), wide, { wide = it })
        }
        AbkCluster {
            AbkButton(onOpenBuild, style = AbkButtonStyle.Ghost) { Text("构建页示例") }
            AbkButton(onOpenModules, style = AbkButtonStyle.Ghost) { Text("模块页示例") }
        }
    }
}

@Composable
private fun CollectionSamples(feedback: (String) -> Unit) {
    var expanded by rememberSaveable { mutableStateOf(true) }
    var chip by rememberSaveable { mutableStateOf(true) }
    var removed by rememberSaveable { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        CatalogBlock("卡片 · 风格") {
            AbkCard(header = { AbkHeading("默认卡片") }) { AbkText("普通面色，局部状态色。") }
            AbkCard(style = AbkCardStyle.Flat) { AbkText("平面卡片") }
            AbkCard(style = AbkCardStyle.Sunken) { AbkText("下沉卡片") }
        }
        CatalogBlock("卡片 · 媒体、尾部与点击") {
            AbkCard(media = { Box(Modifier.fillMaxWidth().height(96.dp).background(AbkTheme.colors.sunken)) },
                header = { AbkHeading("媒体卡片") },
                footer = { AbkButton({ feedback("卡片动作") }) { Text("查看") } }) {
                AbkText("媒体、标题、正文与尾部均由调用方提供。")
            }
            AbkCard(onClick = { feedback("点击卡片") }) { AbkText("可点击卡片") }
            AbkCard(enabled = false) { AbkText("禁用卡片") }
        }
        CatalogBlock("列表") {
            AbkList {
                AbkListSectionTitle("设备")
                AbkListItem("当前设备", description = "本地样例", leading = { AbkAvatar("AB", online = true, statusDescription = "在线") })
                AbkDivider()
                AbkListItem("构建记录", description = "点击查看反馈", onClick = { feedback("打开记录") },
                    leading = { AbkIcon(AbkIcons.Build, null) }, trailing = { AbkIcon(AbkIcons.Forward, null) })
                AbkDivider()
                AbkListItem("不可用记录", onClick = {}, enabled = false)
            }
        }
        CatalogBlock("折叠面板 · 展开 / 收起") {
            AbkAccordion("为什么使用状态上提？", expanded, { expanded = it }) { AbkText("宿主控制展开状态；库不保存业务偏好。") }
            AbkAccordion("收起状态", false, {}) { AbkText("不可见内容") }
            AbkAccordion("禁用", false, {}, enabled = false) { AbkText("不可见内容") }
        }
        CatalogBlock("头像") {
            AbkCluster {
                AbkSize.entries.forEach { AbkAvatar("AB", size = it) }
                AbkAvatar("ON", online = true, statusDescription = "在线")
                AbkAvatar("OF", online = false, statusDescription = "离线")
                AbkAvatar("ID")
            }
        }
        CatalogBlock("徽标") {
            AbkCluster { AbkTone.entries.forEach { AbkBadge(it.name, tone = it) }; AbkBadge("无圆点", dot = false) }
        }
        CatalogBlock("标签") {
            AbkCluster {
                AbkChip("已选标签", chip, { chip = it }, icon = AbkIcons.Modules)
                AbkChip("未选标签", false, {})
                AbkChip("不可用", false, {}, enabled = false)
                if (!removed) AbkRemovableChip("arm64", { removed = true })
                else AbkButton({ removed = false }, style = AbkButtonStyle.Ghost) { Text("恢复标签") }
            }
        }
    }
}

@Suppress("DEPRECATION")
@Composable
private fun DataSamples(feedback: (String) -> Unit) {
    var copied by rememberSaveable { mutableStateOf(false) }
    val clipboard = LocalClipboardManager.current
    val code = "AbkTheme {\n    AbkButton(onClick = ::build) {\n        Text(\"开始构建\")\n    }\n}"
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        CatalogBlock("统计") {
            AbkCluster {
                AbkStat("累计构建", "128", delta = "+12 本周", deltaTone = AbkTone.Success)
                AbkStat("平均用时", "8m", delta = "-1m", deltaTone = AbkTone.Success)
                AbkStat("失败率", "4%", delta = "+1%", deltaTone = AbkTone.Danger)
                AbkStat("无变化", "36")
            }
        }
        CatalogBlock("键值") {
            AbkPanel {
                AbkKeyValue("架构", "arm64-v8a"); AbkDivider(); AbkKeyValue("工具链", "Clang 19")
            }
        }
        CatalogBlock("代码块 · 复制反馈") {
            AbkCodeBlock(code, title = "最小组件用法",
                onCopy = { clipboard.setText(AnnotatedString(code)); copied = true; feedback("代码已复制") }, copied = copied)
        }
        CatalogBlock("表格") {
            AbkTable(listOf(AbkTableColumn("构建", 160.dp), AbkTableColumn("状态", 120.dp), AbkTableColumn("大小", 100.dp, true)),
                (1..12).map { listOf("kernel-$it", if (it % 3 == 0) "失败" else "完成", "${120 + it} MB") })
        }
    }
}

@Composable
private fun SequenceSamples() {
    val steps = listOf(AbkStep("拉取源码", "使用指定分支", AbkStepState.Complete), AbkStep("编译内核", "正在运行", AbkStepState.Current),
        AbkStep("打包产物"))
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        CatalogBlock("垂直步骤") { AbkSteps(steps) }
        CatalogBlock("水平步骤") { AbkSteps(steps, horizontal = true) }
        CatalogBlock("时间线 · 语气") {
            AbkTimeline(listOf(AbkTimelineItem("10:28", "开始编译", tone = AbkTone.Accent),
                AbkTimelineItem("10:26", "依赖就绪", "下载与校验已完成", AbkTone.Success),
                AbkTimelineItem("10:24", "任务创建")))
        }
    }
}

@Composable
private fun AlertSamples() {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        CatalogBlock("四种语气") {
            listOf(AbkTone.Info, AbkTone.Success, AbkTone.Warning, AbkTone.Danger).forEach {
                AbkAlert(it.name, "状态同时以图标和文字说明，不只依赖颜色。", tone = it)
            }
        }
        CatalogBlock("带动作") {
            AbkAlert("需要更新", "当前版本落后于稳定通道。", tone = AbkTone.Warning,
                actions = { AbkButton({}) { Text("查看更新") } })
        }
    }
}

@Composable
private fun ProgressSamples() {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        CatalogBlock("线性进度") {
            AbkLinearProgress(0.64f, label = "确定进度")
            AbkLinearProgress(0.8f, tone = AbkTone.Success, striped = true, label = "条纹进度")
            AbkLinearProgress(0.35f, tone = AbkTone.Warning, label = "警告语气")
            AbkLinearProgress(null, label = "不确定进度")
        }
        CatalogBlock("环形进度") {
            AbkCluster { AbkTone.entries.filter { it != AbkTone.Neutral }.forEach { AbkRingProgress(0.64f, tone = it) } }
        }
        CatalogBlock("加载指示") {
            AbkCluster { AbkSize.entries.forEach { AbkSpinner(size = it) }; AbkLoadingDots() }
        }
        CatalogBlock("骨架屏") {
            AbkSkeleton(style = AbkSkeletonStyle.Title)
            AbkSkeleton()
            AbkSkeleton(style = AbkSkeletonStyle.Circle)
        }
    }
}

@Composable
private fun EmptySamples(feedback: (String) -> Unit) {
    AbkPanel {
        AbkEmptyState("暂无记录", "试着创建第一条记录。", actions = { AbkButton({ feedback("创建动作示例") }) { Text("创建示例") } })
        AbkDivider()
        AbkEmptyState("没有找到图标", "换个关键词，或浏览全部 30 个语义角色。", icon = AbkIcons.Search)
    }
}

@Composable
private fun ToastSamples(snackbars: AbkSnackbarHostState, feedback: (String) -> Unit) {
    var undoPending by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        CatalogBlock("四种语气") {
            AbkCluster {
                AbkTone.entries.forEach { tone ->
                    AbkButton({ scope.launch { snackbars.show("${tone.name} 提示", tone, withDismissAction = true) } }) { Text(tone.name) }
                }
            }
        }
        CatalogBlock("带动作与撤销") {
            AbkCluster {
                AbkButton({ scope.launch { snackbars.show("记录已归档", actionLabel = "查看") } }) { Text("带动作") }
                AbkButton({ scope.launch { snackbars.show("可关闭的提示", withDismissAction = true) } }) { Text("带关闭") }
                AbkButton({
                    undoPending = true
                    scope.launch {
                        try {
                            val result = snackbars.show("记录已删除", actionLabel = "撤销", withDismissAction = true)
                            feedback(if (result == SnackbarResult.ActionPerformed) "已恢复（一次）" else "保留删除")
                        } finally { undoPending = false }
                    }
                }, enabled = !undoPending) { Text("带撤销") }
            }
        }
    }
}

@Composable
private fun OverlaySamples(snackbars: AbkSnackbarHostState, feedback: (String) -> Unit) {
    var dialog by rememberSaveable { mutableStateOf(false) }
    var wide by rememberSaveable { mutableStateOf(false) }
    var toned by rememberSaveable { mutableStateOf(false) }
    var sheet by rememberSaveable { mutableStateOf(false) }
    var menu by rememberSaveable { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        CatalogBlock("浮层") {
            AbkCluster {
                AbkButton({ wide = false; toned = false; dialog = true }) { Text("对话框") }
                AbkButton({ wide = true; toned = false; dialog = true }) { Text("宽对话框") }
                AbkButton({ wide = false; toned = true; dialog = true }, style = AbkButtonStyle.Outline) { Text("警告对话框") }
                AbkButton({ sheet = true }) { Text("底部弹层") }
                Box {
                    AbkButton({ menu = true }) { Text("菜单") }
                    AbkMenu(menu, { menu = false }, listOf(AbkMenuEntry.Label("构建操作"),
                        AbkMenuEntry.Action("copy", "复制", AbkIcons.Copy, "Ctrl+C"),
                        AbkMenuEntry.Action("off", "暂不可用", enabled = false),
                        AbkMenuEntry.Divider, AbkMenuEntry.Action("delete", "删除", AbkIcons.Trash, danger = true)),
                        { feedback("菜单：$it") })
                }
                AbkTooltip("长按或悬停查看提示") { AbkIconButton(AbkIcons.Info, "组件说明", { feedback("组件说明") }) }
            }
        }
        CatalogBlock("提示排队") {
            AbkButton({
                snackbars.show("提示一", withDismissAction = true)
            }) { Text("查看 Toast 示例") }
        }
    }
    if (dialog) AbkDialog(if (toned) "关闭签名校验？" else "删除构建记录？", { dialog = false }, wide = wide,
        tone = if (toned) AbkTone.Warning else null,
        actions = {
            AbkButton({ dialog = false }, style = AbkButtonStyle.Ghost) { Text("取消") }
            AbkButton({ dialog = false; feedback(if (toned) "已关闭签名校验（示例）" else "对话框已关闭，记录已删除") },
                style = if (toned) AbkButtonStyle.Soft else AbkButtonStyle.Danger) {
                Text(if (toned) "关闭校验" else "删除")
            }
        }) {
        AbkText(if (toned) "关闭后，设备将不再校验安装包的签名一致性。该操作可以随时重新开启。"
        else "关闭浮层与发出反馈是两个明确动作。目录只操作样例数据。")
    }
    if (sheet) AbkBottomSheet("构建产物", { sheet = false }, description = "仅演示动作，不进行网络下载",
        actions = { AbkButton({ sheet = false }, style = AbkButtonStyle.Ghost) { Text("取消") } }) {
        AbkList(plain = true) {
            AbkListItem("下载 .zip", description = "132 MB · AnyKernel3",
                onClick = { sheet = false; feedback("下载动作示例，未发起下载") }, leading = { AbkIcon(AbkIcons.Download, null) })
            AbkListItem("复制链接", onClick = { sheet = false; feedback("复制链接动作示例") }, leading = { AbkIcon(AbkIcons.Copy, null) })
        }
    }
}

@Composable
private fun CompositionSamples(feedback: (String) -> Unit, onOpenBuild: () -> Unit, onOpenModules: () -> Unit) {
    var wifi by rememberSaveable { mutableStateOf(true) }
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        CatalogBlock("构建结果卡片") {
            AbkCard(header = {
                AbkCluster { AbkHeading("kernel-6.6.12"); AbkBadge("已完成", tone = AbkTone.Success) }
            }, footer = {
                AbkButton({ feedback("查看日志（示例）") }, style = AbkButtonStyle.Soft) { Text("日志") }
                AbkButton({}, style = AbkButtonStyle.Ghost) { Text("分享") }
            }) {
                AbkKeyValue("设备", "Pixel 8 Pro")
                AbkKeyValue("产物", "132 MB · AnyKernel3")
                AbkLinearProgress(1f, tone = AbkTone.Success)
            }
        }
        CatalogBlock("设置面板") {
            AbkPanel {
                AbkSettingSwitch("仅在 Wi-Fi 下下载", "避免使用移动数据", wifi, { wifi = it })
                AbkDivider()
                AbkSettingSwitch("自动重试失败构建", "最多重试两次", true, {})
            }
        }
        CatalogBlock("空与错误") {
            AbkAlert("构建失败", "编译在第 3 步退出，查看日志了解详情。", tone = AbkTone.Danger,
                actions = { AbkButton({}, style = AbkButtonStyle.Ghost) { Text("打开日志") } })
            AbkEmptyState("还没有构建", "从构建页开始第一个示例。", actions = { AbkButton(onOpenBuild) { Text("去构建页") } })
        }
        AbkCluster {
            AbkButton(onOpenBuild, style = AbkButtonStyle.Ghost) { Text("构建页示例") }
            AbkButton(onOpenModules, style = AbkButtonStyle.Ghost) { Text("模块页示例") }
        }
    }
}

@Preview(name = "ABK Light", showBackground = true, widthDp = 390)
@Composable
private fun CardPreview() {
    AbkTheme(colorSource = AbkColorSource.Seed, reduceMotion = true) {
        AbkCard(Modifier.padding(16.dp)) { AbkHeading("构建就绪"); AbkText("普通面色，局部状态色"); AbkBadge("已完成", tone = AbkTone.Success) }
    }
}

@Preview(name = "ABK Dark", showBackground = true, widthDp = 390)
@Composable
private fun DarkPreview() {
    AbkTheme(darkTheme = true, colorSource = AbkColorSource.Seed, reduceMotion = true) { AlertSamples() }
}

@Preview(name = "ABK Tokens", showBackground = true, widthDp = 390, heightDp = 1200)
@Composable
private fun TokensPreview() {
    AbkTheme(colorSource = AbkColorSource.Seed, reduceMotion = true) { TokenSamples() }
}
