package io.github.xingguangcuican6666.abk.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.xingguangcuican6666.abk.ui.components.*
import io.github.xingguangcuican6666.abk.ui.icons.AbkIcon
import io.github.xingguangcuican6666.abk.ui.icons.AbkIcons
import io.github.xingguangcuican6666.abk.ui.theme.AbkTheme
import io.github.xingguangcuican6666.abk.ui.theme.AbkTone
import kotlinx.coroutines.launch

@Composable
internal fun BuildExample(modifier: Modifier, snackbars: AbkSnackbarHostState) {
    var branch by rememberSaveable { mutableStateOf("main") }
    var target by rememberSaveable { mutableStateOf("arm64") }
    var tab by rememberSaveable { mutableStateOf("recent") }
    var running by rememberSaveable { mutableStateOf(false) }
    var deleted by rememberSaveable { mutableStateOf(false) }
    var sheet by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    LazyColumn(modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { AbkHero("准备好，开始构建", eyebrow = "本地演示", description = "这里只演示组件状态，不连接构建服务。") }
        item {
            AbkCard {
                AbkHeading("构建配置")
                AbkTextField(branch, { branch = it }, "源码分支", placeholder = "main")
                AbkSelect(listOf(AbkOption("arm64", "arm64-v8a"), AbkOption("x86", "x86_64")), target, { target = it }, "目标架构")
                AbkButton({
                    running = !running
                    scope.launch { snackbars.show(if (running) "已切换到构建中示例，未创建真实任务" else "示例已停止", AbkTone.Info) }
                }, Modifier.fillMaxWidth(), style = AbkButtonStyle.Primary, enabled = branch.isNotBlank()) {
                    AbkIcon(AbkIcons.Build, null); Text(if (running) "停止示例" else "开始构建示例")
                }
            }
        }
        if (running) item {
            AbkCard {
                AbkHeading("正在编译内核")
                AbkBadge("示例运行中", tone = AbkTone.Info)
                AbkText("kernel · $branch · $target", color = AbkTheme.colors.inkMuted)
                AbkLinearProgress(0.64f, label = "示例构建进度")
                AbkKeyValue("当前阶段", "编译 · 64%")
                AbkText("卡片保持普通面色；只在进度等局部使用状态色。", style = MaterialTheme.typography.bodySmall, color = AbkTheme.colors.inkMuted)
            }
        }
        item { AbkTabs(listOf(AbkTabItem("recent", "最近"), AbkTabItem("success", "成功")), tab, { tab = it }) }
        if (!deleted) item {
            AbkCard(footer = {
                AbkButton({ sheet = true }, style = AbkButtonStyle.Soft) { Text("产物") }
                AbkButton({
                    deleted = true
                    scope.launch {
                        val result = snackbars.show("记录已删除", actionLabel = "撤销", withDismissAction = true)
                        if (result == SnackbarResult.ActionPerformed) {
                            deleted = false
                            snackbars.show("已恢复", AbkTone.Success)
                        }
                    }
                }, style = AbkButtonStyle.Ghost) { Text("删除") }
            }) {
                AbkHeading("kernel-main #128")
                AbkBadge("构建成功", tone = AbkTone.Success)
                AbkKeyValue("耗时", "8 分 24 秒")
                AbkKeyValue("产物", "132 MB")
            }
        }
        if (tab == "recent") item { AbkAlert("上次构建未完成", "缺少配置文件。此处仅展示失败状态。", tone = AbkTone.Warning) }
        if (deleted && tab == "success") item { AbkEmptyState("暂无成功记录", "可以在底部提示中撤销删除。") }
    }
    if (sheet) AbkBottomSheet("构建产物", { sheet = false }, description = "不发起实际下载",
        actions = { AbkButton({ sheet = false }, style = AbkButtonStyle.Ghost) { Text("关闭") } }) {
        AbkListItem("AnyKernel3.zip", description = "132 MB · arm64-v8a", leading = { AbkIcon(AbkIcons.Download, null) }, onClick = {
            sheet = false
            scope.launch { snackbars.show("下载动作示例，未发起网络请求", AbkTone.Info) }
        })
    }
}

private data class ModuleSample(val id: String, val title: String, val description: String, val version: String)
private val modules = listOf(
    ModuleSample("susfs", "SUSFS", "文件系统增强模块", "1.5.5"),
    ModuleSample("bbr", "BBR", "网络拥塞控制", "v3"),
    ModuleSample("zram", "ZRAM", "压缩交换配置", "2.1"),
)

@Composable
internal fun ModulesExample(modifier: Modifier, snackbars: AbkSnackbarHostState) {
    var search by rememberSaveable { mutableStateOf("") }
    var susfs by rememberSaveable { mutableStateOf(true) }
    var bbr by rememberSaveable { mutableStateOf(true) }
    var zram by rememberSaveable { mutableStateOf(false) }
    var settings by rememberSaveable { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val filtered = modules.filter { it.title.contains(search, ignoreCase = true) || it.description.contains(search) }
    LazyColumn(modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { AbkHero("挑选你的模块", eyebrow = "模块库", description = "状态保存在示例页面，不会修改设备内核。") }
        item { AbkTextField(search, { search = it }, "搜索模块", placeholder = "名称或描述", prefix = { AbkIcon(AbkIcons.Search, null) }) }
        items(filtered, key = { it.id }) { module ->
            val checked = when (module.id) { "susfs" -> susfs; "bbr" -> bbr; else -> zram }
            AbkCard(footer = {
                AbkBadge(module.version, dot = false)
                AbkButton({ settings = module.id }, style = AbkButtonStyle.Ghost) { Text("详情") }
            }) {
                AbkSettingSwitch(module.title, module.description, checked, { value ->
                    when (module.id) { "susfs" -> susfs = value; "bbr" -> bbr = value; else -> zram = value }
                    scope.launch { snackbars.show("${module.title} ${if (value) "已选中" else "已取消"}（示例）") }
                })
            }
        }
        if (filtered.isEmpty()) item {
            AbkEmptyState("没有匹配的模块", "换一个关键词试试。", actions = { AbkButton({ search = "" }) { Text("清空搜索") } })
        }
    }
    settings?.let { id ->
        val module = modules.first { it.id == id }
        AbkDialog(module.title, { settings = null }, actions = { AbkButton({ settings = null }) { Text("知道了") } }) {
            AbkText(module.description)
            AbkKeyValue("版本", module.version)
            AbkAlert("样例数据", "未下载安装或修改任何真实模块。", tone = AbkTone.Info)
        }
    }
}
