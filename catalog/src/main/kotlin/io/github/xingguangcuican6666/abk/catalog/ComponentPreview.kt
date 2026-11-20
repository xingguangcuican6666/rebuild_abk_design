package io.github.xingguangcuican6666.abk.catalog

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.xingguangcuican6666.abk.ui.components.*
import io.github.xingguangcuican6666.abk.ui.icons.AbkIcon
import io.github.xingguangcuican6666.abk.ui.icons.AbkIcons
import io.github.xingguangcuican6666.abk.ui.theme.*
import kotlinx.coroutines.launch

@Composable
internal fun ComponentPreview(modifier: Modifier, snackbars: AbkSnackbarHostState, onOpenCatalog: () -> Unit) {
    var name by rememberSaveable { mutableStateOf("我的组件预览") }
    var channel by rememberSaveable { mutableStateOf("stable") }
    var layout by rememberSaveable { mutableStateOf("list") }
    var notifications by rememberSaveable { mutableStateOf(true) }
    var loading by rememberSaveable { mutableStateOf(false) }
    var selected by rememberSaveable { mutableStateOf(true) }
    var progress by rememberSaveable { mutableFloatStateOf(0.64f) }
    var validated by rememberSaveable { mutableStateOf(false) }
    var removed by rememberSaveable { mutableStateOf(false) }
    var dialog by rememberSaveable { mutableStateOf(false) }
    var sheet by rememberSaveable { mutableStateOf(false) }
    var menu by rememberSaveable { mutableStateOf(false) }
    // An in-flight coroutine is not restored after recreation; the sample card still is.
    var undoPending by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val feedback: (String) -> Unit = { message -> scope.launch { snackbars.show(message) }; Unit }
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(Modifier.widthIn(max = 840.dp).fillMaxSize(), contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)) {
            item {
                AbkHero("组件，放在一起看", eyebrow = "ABK UI · 原生预览", description = "真实 Compose 组件，少量精选组合。可直接编辑、切换、打开浮层和撤销；所有动作仅影响本地样例。",
                    actions = { AbkButton(onOpenCatalog, style = AbkButtonStyle.Ghost) { Text("浏览全部组件与主题设置") } })
            }
            item {
                AbkPanel(Modifier.fillMaxWidth()) {
                    AbkEyebrow("01 · 按钮与动作")
                    AbkHeading("有层级，不喧宾夺主")
                    AbkCluster {
                        AbkButton({ feedback("主按钮已点击（示例）") }, style = AbkButtonStyle.Primary) {
                            AbkIcon(AbkIcons.Plus, null); Text("新建示例")
                        }
                        AbkButton({ feedback("描边按钮已点击（示例）") }, style = AbkButtonStyle.Outline) { Text("次要动作") }
                        AbkButton({ feedback("轻量按钮已点击（示例）") }, style = AbkButtonStyle.Ghost) { Text("轻量动作") }
                    }
                    AbkCluster {
                        AbkButton({ feedback("示例已保存") }, loading = loading, style = AbkButtonStyle.Soft) {
                            Text(if (loading) "正在保存" else "保存示例")
                        }
                        AbkButton({}, enabled = false) { Text("不可用") }
                    }
                    AbkSwitch(loading, { loading = it }, "预览加载状态")
                }
            }
            item {
                AbkPanel(Modifier.fillMaxWidth()) {
                    AbkEyebrow("02 · 表单与选择")
                    AbkHeading("标签在外，状态清楚")
                    AbkTextField(name, { name = it }, "预览名称", placeholder = "为预览取个名字",
                        hint = "不会保存到文件或发送到服务端", error = if (validated && name.isBlank()) "请输入预览名称" else null)
                    AbkSelect(listOf(AbkOption("stable", "稳定通道"), AbkOption("preview", "预览通道"),
                        AbkOption("unavailable", "暂不可用", enabled = false)), channel, { channel = it }, "发布通道")
                    AbkField("布局选择") {
                        AbkSegmented(listOf(AbkOption("list", "列表"), AbkOption("grid", "网格")), layout, { layout = it })
                    }
                    AbkSettingSwitch("完成后通知", "只改变本地开关，不申请通知权限", notifications, { notifications = it })
                    AbkButton({
                        validated = true
                        if (name.isNotBlank()) feedback("「${name.trim()}」校验通过（示例）")
                    }) { Text("校验输入") }
                }
            }
            item {
                AbkEyebrow("03 · 卡片与状态")
            }
            item {
                if (removed) AbkEmptyState("预览卡片已移除", "可在底部提示中撤销，也可恢复本地样例。",
                    actions = { AbkButton({ removed = false }, enabled = !undoPending) { Text("恢复卡片") } })
                else AbkCard(Modifier.fillMaxWidth(), footer = {
                    AbkButton({ sheet = true }, style = AbkButtonStyle.Soft) { Text("查看详情") }
                    AbkButton({ dialog = true }, enabled = !undoPending, style = AbkButtonStyle.Ghost) { Text("移除卡片") }
                }) {
                    AbkHeading(name.ifBlank { "未命名预览" })
                    AbkCluster {
                        AbkBadge("本地样例", tone = AbkTone.Info)
                        AbkBadge(if (channel == "stable") "稳定通道" else "预览通道", dot = false)
                        AbkChip("已选架构", selected, { selected = it }, icon = AbkIcons.Cpu)
                    }
                    AbkText("普通面色承载内容，颜色只留给进度和状态。", color = AbkTheme.colors.inkMuted)
                    AbkLinearProgress(progress, label = "预览进度")
                    AbkKeyValue("当前进度", "${(progress * 100).toInt()}%")
                    AbkSlider(progress, { progress = it }, "调整示例进度", valueLabel = "${(progress * 100).toInt()}%")
                }
            }
            item {
                AbkPanel(Modifier.fillMaxWidth()) {
                    AbkEyebrow("04 · 反馈与浮层")
                    AbkHeading("每一步，都有回应")
                    AbkAlert("仅演示交互", "不会构建、下载、安装或修改设备。", tone = AbkTone.Info)
                    AbkAlert("状态不只依赖颜色", "警告同时保留图标和文字说明。", tone = AbkTone.Warning)
                    AbkCluster {
                        AbkButton({ sheet = true }) { Text("底部弹层") }
                        Box {
                            AbkButton({ menu = true }) { Text("打开菜单") }
                            AbkMenu(menu, { menu = false }, listOf(
                                AbkMenuEntry.Label("预览操作"),
                                AbkMenuEntry.Action("refresh", "刷新样例", AbkIcons.Refresh),
                                AbkMenuEntry.Action("unavailable", "暂不可用", enabled = false),
                            ), { feedback("样例已刷新，仅演示菜单反馈") })
                        }
                        AbkButton({ scope.launch { snackbars.show("操作成功（示例）", AbkTone.Success, withDismissAction = true) } }) { Text("成功提示") }
                    }
                    AbkText("明暗、取色和字体缩放沿用目录中的主题设置。", style = MaterialTheme.typography.bodySmall,
                        color = AbkTheme.colors.inkMuted)
                }
            }
        }
    }
    if (dialog) AbkDialog("移除这张预览卡片？", { dialog = false }, actions = {
        AbkButton({ dialog = false }, style = AbkButtonStyle.Ghost) { Text("取消") }
        AbkButton({
            dialog = false
            removed = true
            undoPending = true
            scope.launch {
                try {
                    val result = snackbars.show("预览卡片已移除", actionLabel = "撤销", withDismissAction = true)
                    if (result == SnackbarResult.ActionPerformed) {
                        removed = false
                        snackbars.show("预览卡片已恢复", AbkTone.Success)
                    }
                } finally { undoPending = false }
            }
        }, style = AbkButtonStyle.Danger) { Text("确认移除") }
    }) { AbkText("只影响当前本地样例。关闭对话框后，可以在底部提示中撤销。") }
    if (sheet) AbkBottomSheet("预览详情", { sheet = false }, description = "真实组件 · 本地状态",
        actions = { AbkButton({ sheet = false }, style = AbkButtonStyle.Ghost) { Text("关闭") } }) {
        AbkKeyValue("名称", name.ifBlank { "未命名预览" })
        AbkKeyValue("发布通道", if (channel == "stable") "稳定通道" else "预览通道")
        AbkKeyValue("布局选择", if (layout == "list") "列表" else "网格")
        AbkKeyValue("通知示例", if (notifications) "已开启" else "已关闭")
        AbkText("这些值来自上方的受控输入，组件库不保存业务偏好。")
    }
}

@Preview(name = "Component preview · Light", showBackground = true, widthDp = 390, heightDp = 844)
@Preview(name = "Component preview · Dark", showBackground = true, widthDp = 390, heightDp = 844,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Component preview · Narrow", showBackground = true, widthDp = 320, heightDp = 844, fontScale = 2f)
@Composable
private fun ComponentPagePreview() {
    AbkTheme(darkTheme = isSystemInDarkTheme(), colorSource = AbkColorSource.Seed, reduceMotion = true) {
        val snackbars = rememberAbkSnackbarHostState()
        AbkScaffold(snackbarHost = { AbkSnackbarHost(snackbars) }) { padding ->
            ComponentPreview(Modifier.padding(padding), snackbars, {})
        }
    }
}
