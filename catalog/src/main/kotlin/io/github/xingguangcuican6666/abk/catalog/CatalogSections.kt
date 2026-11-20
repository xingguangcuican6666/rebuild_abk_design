package io.github.xingguangcuican6666.abk.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.xingguangcuican6666.abk.ui.components.*
import io.github.xingguangcuican6666.abk.ui.icons.AbkIcon
import io.github.xingguangcuican6666.abk.ui.icons.AbkIcons
import io.github.xingguangcuican6666.abk.ui.theme.*
import kotlinx.coroutines.launch

internal enum class CatalogSection(val title: String) {
    Typography("排版与布局"), Buttons("按钮"), Forms("表单与选择"), Navigation("导航"),
    Collections("卡片、列表与标签"), Data("数据展示"), Sequence("步骤与时间线"),
    Feedback("反馈与加载"), Overlays("浮层与 Toast"), Icons("Lucide 图标"),
}

@Composable
internal fun CatalogSectionContent(section: CatalogSection, snackbars: AbkSnackbarHostState, feedback: (String) -> Unit) {
    when (section) {
        CatalogSection.Typography -> TypographySamples()
        CatalogSection.Buttons -> ButtonSamples(feedback)
        CatalogSection.Forms -> FormSamples()
        CatalogSection.Navigation -> NavigationSamples(feedback)
        CatalogSection.Collections -> CollectionSamples(feedback)
        CatalogSection.Data -> DataSamples(feedback)
        CatalogSection.Sequence -> SequenceSamples()
        CatalogSection.Feedback -> FeedbackSamples(feedback)
        CatalogSection.Overlays -> OverlaySamples(snackbars, feedback)
        CatalogSection.Icons -> AbkCluster {
            AbkIcons.all.forEach { (role, vector) ->
                AbkCard { AbkIcon(vector, role); AbkText(role, style = MaterialTheme.typography.bodySmall) }
            }
        }
    }
}

@Composable
private fun TypographySamples() {
    AbkPanel(grain = true) {
        AbkEyebrow("基础排版 · 可选纸纹")
        AbkHeading("把复杂留给组件", large = true)
        AbkText("正文使用霞鹜文楷，随系统字体大小缩放。中文、Latin 与数字 12345 共享清晰的层级。")
        AbkDivider()
        AbkCluster { AbkInlineCode("./gradlew build"); AbkKeycap("Ctrl"); AbkKeycap("K"); AbkHighlight("轻量，不单调") }
    }
}

@Composable
private fun ButtonSamples(feedback: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        AbkCluster {
            AbkButtonStyle.entries.forEach { style -> AbkButton({ feedback("${style.name} 按钮") }, style = style) { Text(style.name) } }
        }
        AbkCluster { AbkSize.entries.forEach { size -> AbkButton({ feedback(size.name) }, size = size) { Text(size.name) } } }
        AbkCluster {
            AbkButton({}, enabled = false) { Text("不可用") }
            AbkButton({}, loading = true) { Text("正在处理") }
            AbkIconButton(AbkIcons.Settings, "打开设置示例", { feedback("设置示例") })
            AbkButton({ feedback("已刷新") }, style = AbkButtonStyle.Soft) { AbkIcon(AbkIcons.Refresh, null); Text("刷新") }
        }
        AbkButton({ feedback("整行按钮") }, Modifier.fillMaxWidth(), style = AbkButtonStyle.Primary) { Text("整行主按钮") }
    }
}

@Composable
private fun FormSamples() {
    var name by rememberSaveable { mutableStateOf("") }
    var search by rememberSaveable { mutableStateOf("") }
    var notes by rememberSaveable { mutableStateOf("") }
    var choice by rememberSaveable { mutableStateOf("stable") }
    var radio by rememberSaveable { mutableStateOf("stable") }
    var card by rememberSaveable { mutableStateOf("stable") }
    var checked by rememberSaveable { mutableIntStateOf(2) }
    var enabled by rememberSaveable { mutableStateOf(true) }
    var small by rememberSaveable { mutableStateOf(false) }
    var slider by rememberSaveable { mutableFloatStateOf(0.6f) }
    var segment by rememberSaveable { mutableStateOf("stable") }
    val options = listOf(AbkOption("stable", "稳定", "适合日常使用"), AbkOption("preview", "预览", "尝试新功能"), AbkOption("off", "不可用", enabled = false))
    AbkPanel {
        AbkTextField(name, { name = it }, "构建名称", placeholder = "例如：我的内核", hint = "独立外置标签，没有前缀装饰", optionalLabel = "可选")
        AbkTextField(search, { search = it }, "搜索", prefix = { AbkIcon(AbkIcons.Search, null) },
            suffix = { AbkIconButton(AbkIcons.Close, "清空搜索", { search = "" }, size = AbkSize.Small) })
        AbkTextField("", {}, "校验错误", error = "请输入有效的分支名称", placeholder = "分支名称")
        AbkTextField("只读内容可以选择", {}, "只读", readOnly = true)
        AbkTextField("当前不可修改", {}, "禁用", enabled = false)
        AbkTextField(notes, { notes = it }, "说明", singleLine = false, minLines = 3, maxLines = 6)
        AbkSelect(options, choice, { choice = it }, "发布通道")
        AbkSelect(options, null, {}, "禁用下拉", enabled = false)
        AbkCheckbox(ToggleableState.entries[checked], { checked = (checked + 1) % 3 }, "三态选择", description = "点击轮换未选、选中与半选")
        AbkCheckbox(ToggleableState.Off, {}, "禁用复选框", enabled = false)
        AbkField("单选组") { AbkRadioGroup(options, radio, { radio = it }) }
        AbkField("卡片单选") { AbkRadioGroup(options, card, { card = it }, cards = true) }
        AbkSwitch(enabled, { enabled = it }, "常规开关")
        AbkSwitch(small, { small = it }, "小号开关", size = AbkSize.Small)
        AbkSwitch(false, {}, "禁用开关", enabled = false)
        AbkSettingSwitch("完成后通知", "说明与开关共用一个触控目标", enabled, { enabled = it })
        AbkSlider(slider, { slider = it }, "并行度", steps = 4, valueLabel = "${(slider * 100).toInt()}%")
        AbkSegmented(options, segment, { segment = it })
        AbkSegmented(options.take(2), segment, { segment = it }, size = AbkSize.Small)
    }
}

@Composable
private fun NavigationSamples(feedback: (String) -> Unit) {
    var tab by rememberSaveable { mutableStateOf("all") }
    var scroll by rememberSaveable { mutableStateOf("0") }
    var destination by rememberSaveable { mutableStateOf("build") }
    AbkPanel {
        AbkTopBar("页面标题", subtitle = "滚动后分隔线", scrolled = true,
            navigationIcon = { AbkIconButton(AbkIcons.Back, "返回示例", { feedback("返回") }) },
            actions = { AbkIconButton(AbkIcons.More, "更多操作", { feedback("更多") }) })
        AbkHero("大标题页头", eyebrow = "页面眉题", description = "说明与动作均为可选插槽")
        AbkTabs(listOf(AbkTabItem("all", "全部", 8), AbkTabItem("done", "完成", 6), AbkTabItem("off", "不可用", enabled = false)), tab, { tab = it })
        AbkTabs((0..6).map { AbkTabItem(it.toString(), "分类 $it", it) }, scroll, { scroll = it }, scrollable = true)
        AbkBreadcrumb(listOf(AbkBreadcrumbItem("首页") { feedback("首页") }, AbkBreadcrumbItem("组件") { feedback("组件") }, AbkBreadcrumbItem("导航")))
        AbkBottomNavigation(listOf(AbkNavigationItem("build", "构建", AbkIcons.Build), AbkNavigationItem("modules", "模块", AbkIcons.Modules),
            AbkNavigationItem("settings", "设置", AbkIcons.Settings)), destination, { destination = it })
    }
}

@Composable
private fun CollectionSamples(feedback: (String) -> Unit) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var chip by rememberSaveable { mutableStateOf(true) }
    var removed by rememberSaveable { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        AbkCard(media = { Box(Modifier.fillMaxWidth().height(96.dp).background(AbkTheme.colors.sunken)) },
            header = { AbkHeading("媒体卡片") }, footer = { AbkButton({ feedback("卡片动作") }) { Text("查看") } }) {
            AbkText("媒体、标题、正文与尾部均由调用方提供。")
        }
        AbkCard(style = AbkCardStyle.Flat) { AbkText("平面卡片") }
        AbkCard(style = AbkCardStyle.Sunken) { AbkText("下沉卡片") }
        AbkCard(onClick = { feedback("点击卡片") }) { AbkText("可点击卡片") }
        AbkList {
            AbkListSectionTitle("设备")
            AbkListItem("当前设备", description = "本地样例", leading = { AbkAvatar("AB", online = true, statusDescription = "在线") })
            AbkDivider()
            AbkListItem("构建记录", description = "点击查看反馈", onClick = { feedback("打开记录") },
                leading = { AbkIcon(AbkIcons.Build, null) }, trailing = { AbkIcon(AbkIcons.Forward, null) })
            AbkDivider()
            AbkListItem("不可用记录", onClick = {}, enabled = false)
        }
        AbkAccordion("为什么使用状态上提？", expanded, { expanded = it }) { AbkText("宿主控制展开状态；库不保存业务偏好。") }
        AbkCluster { AbkSize.entries.forEach { AbkAvatar("AB", size = it) } }
        AbkCluster { AbkTone.entries.forEach { AbkBadge(it.name, tone = it) }; AbkBadge("无圆点", dot = false) }
        AbkCluster {
            AbkChip("可选标签", chip, { chip = it }, icon = AbkIcons.Modules)
            AbkChip("不可用", false, {}, enabled = false)
            if (!removed) AbkRemovableChip("arm64", { removed = true })
            else AbkButton({ removed = false }, style = AbkButtonStyle.Ghost) { Text("恢复标签") }
        }
    }
}

@Suppress("DEPRECATION")
@Composable
private fun DataSamples(feedback: (String) -> Unit) {
    var copied by rememberSaveable { mutableStateOf(false) }
    val clipboard = LocalClipboardManager.current
    val code = "AbkTheme {\n    AbkButton(onClick = ::build) {\n        Text(\"开始构建\")\n    }\n}"
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        AbkCluster { AbkStat("累计构建", "128", delta = "+12 本周", deltaTone = AbkTone.Success); AbkStat("平均用时", "8m", delta = "-1m", deltaTone = AbkTone.Success) }
        AbkPanel { AbkKeyValue("架构", "arm64-v8a"); AbkDivider(); AbkKeyValue("工具链", "Clang 19") }
        AbkCodeBlock(code, title = "最小组件用法", onCopy = { clipboard.setText(AnnotatedString(code)); copied = true; feedback("代码已复制") }, copied = copied)
        AbkTable(listOf(AbkTableColumn("构建", 160.dp), AbkTableColumn("状态", 120.dp), AbkTableColumn("大小", 100.dp, true)),
            (1..12).map { listOf("kernel-$it", if (it % 3 == 0) "失败" else "完成", "${120 + it} MB") })
    }
}

@Composable
private fun SequenceSamples() {
    val steps = listOf(AbkStep("拉取源码", "使用指定分支", AbkStepState.Complete), AbkStep("编译内核", "正在运行", AbkStepState.Current), AbkStep("打包产物"))
    AbkPanel {
        AbkSteps(steps)
        AbkDivider()
        AbkSteps(steps, horizontal = true)
        AbkTimeline(listOf(AbkTimelineItem("10:28", "开始编译", tone = AbkTone.Accent),
            AbkTimelineItem("10:26", "依赖就绪", "下载与校验已完成", AbkTone.Success), AbkTimelineItem("10:24", "任务创建")))
    }
}

@Composable
private fun FeedbackSamples(feedback: (String) -> Unit = {}) {
    AbkPanel {
        listOf(AbkTone.Info, AbkTone.Success, AbkTone.Warning, AbkTone.Danger).forEach {
            AbkAlert(it.name, "状态同时以图标和文字说明，不只依赖颜色。", tone = it)
        }
        AbkLinearProgress(0.64f)
        AbkLinearProgress(0.8f, tone = AbkTone.Success, striped = true)
        AbkLinearProgress(null)
        AbkCluster { AbkRingProgress(0.64f); AbkSize.entries.forEach { AbkSpinner(size = it) }; AbkLoadingDots() }
        AbkSkeleton(style = AbkSkeletonStyle.Title)
        AbkSkeleton()
        AbkSkeleton(style = AbkSkeletonStyle.Circle)
        AbkEmptyState("暂无记录", "试着创建第一条记录。", actions = { AbkButton({ feedback("创建动作示例") }) { Text("创建示例") } })
    }
}

@Composable
private fun OverlaySamples(snackbars: AbkSnackbarHostState, feedback: (String) -> Unit) {
    var dialog by rememberSaveable { mutableStateOf(false) }
    var wide by rememberSaveable { mutableStateOf(false) }
    var sheet by rememberSaveable { mutableStateOf(false) }
    var menu by rememberSaveable { mutableStateOf(false) }
    var undoPending by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    AbkCluster {
        AbkButton({ wide = false; dialog = true }) { Text("对话框") }
        AbkButton({ wide = true; dialog = true }) { Text("宽对话框") }
        AbkButton({ sheet = true }) { Text("底部弹层") }
        Box {
            AbkButton({ menu = true }) { Text("菜单") }
            AbkMenu(menu, { menu = false }, listOf(AbkMenuEntry.Label("构建操作"),
                AbkMenuEntry.Action("copy", "复制", AbkIcons.Copy, "Ctrl+C"), AbkMenuEntry.Action("off", "暂不可用", enabled = false),
                AbkMenuEntry.Divider, AbkMenuEntry.Action("delete", "删除", AbkIcons.Trash, danger = true)), { feedback("菜单：$it") })
        }
        AbkTooltip("长按或悬停查看提示") { AbkIconButton(AbkIcons.Info, "组件说明", { feedback("组件说明") }) }
        AbkTone.entries.forEach { tone -> AbkButton({ scope.launch { snackbars.show("${tone.name} 提示", tone, withDismissAction = true) } }) { Text(tone.name) } }
        AbkButton({
            undoPending = true
            scope.launch {
                try {
                    val result = snackbars.show("记录已删除", actionLabel = "撤销", withDismissAction = true)
                    feedback(if (result == SnackbarResult.ActionPerformed) "已恢复（一次）" else "保留删除")
                } finally { undoPending = false }
            }
        }, enabled = !undoPending) { Text("带撤销 Toast") }
    }
    if (dialog) AbkDialog("删除构建记录？", { dialog = false }, wide = wide,
        actions = {
            AbkButton({ dialog = false }, style = AbkButtonStyle.Ghost) { Text("取消") }
            AbkButton({ dialog = false; feedback("对话框已关闭，记录已删除") }, style = AbkButtonStyle.Danger) { Text("删除") }
        }) { AbkText("关闭浮层与发出反馈是两个明确动作。目录只操作样例数据。") }
    if (sheet) AbkBottomSheet("构建产物", { sheet = false }, description = "仅演示动作，不进行网络下载",
        actions = { AbkButton({ sheet = false }, style = AbkButtonStyle.Ghost) { Text("取消") } }) {
        AbkList(plain = true) {
            AbkListItem("下载 .zip", description = "132 MB · AnyKernel3", onClick = { sheet = false; feedback("下载动作示例，未发起下载") }, leading = { AbkIcon(AbkIcons.Download, null) })
            AbkListItem("复制链接", onClick = { sheet = false; feedback("复制链接动作示例") }, leading = { AbkIcon(AbkIcons.Copy, null) })
        }
    }
}

@Preview(name = "ABK Light", showBackground = true, widthDp = 390)
@Composable
private fun CardPreview() { AbkTheme(colorSource = AbkColorSource.Seed, reduceMotion = true) { AbkCard(Modifier.padding(16.dp)) { AbkHeading("构建就绪"); AbkText("普通面色，局部状态色"); AbkBadge("已完成", tone = AbkTone.Success) } } }

@Preview(name = "ABK Dark", showBackground = true, widthDp = 390)
@Composable
private fun DarkPreview() { AbkTheme(darkTheme = true, colorSource = AbkColorSource.Seed, reduceMotion = true) { FeedbackSamples() } }
