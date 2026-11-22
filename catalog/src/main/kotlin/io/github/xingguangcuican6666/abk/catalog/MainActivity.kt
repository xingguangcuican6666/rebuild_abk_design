package io.github.xingguangcuican6666.abk.catalog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import io.github.xingguangcuican6666.abk.ui.components.*
import io.github.xingguangcuican6666.abk.ui.icons.AbkIcon
import io.github.xingguangcuican6666.abk.ui.icons.AbkIcons
import io.github.xingguangcuican6666.abk.ui.theme.*
import kotlinx.coroutines.launch

public class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { AbkCatalog() }
    }
}

@Composable
internal fun AbkCatalog() {
    var page by rememberSaveable { mutableStateOf("catalog") }
    var section by rememberSaveable { mutableStateOf("全部") }
    var sectionsOpen by rememberSaveable { mutableStateOf(false) }
    val themeState = rememberCatalogThemeState()
    val theme = themeState.value
    val dark = when (theme.theme) { "dark" -> true; "light" -> false; else -> isSystemInDarkTheme() }
    val density = LocalDensity.current
    val fontScale = when (theme.scale) { "1.0" -> 1f; "1.5" -> 1.5f; "2.0" -> 2f; else -> density.fontScale }
    CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale)) {
        AbkTheme(darkTheme = dark, colorSource = if (theme.source == "system") AbkColorSource.System else AbkColorSource.Seed,
            seed = when (theme.seed) { "green" -> Color(0xFF526A43); "purple" -> Color(0xFF795282); else -> AbkDefaultSeed },
            reduceMotion = theme.reduceMotion) {
            CompositionLocalProvider(LocalCatalogTheme provides theme) {
                val snackbars = rememberAbkSnackbarHostState()
                val scope = rememberCoroutineScope()
                val feedback: (String) -> Unit = { message -> scope.launch { snackbars.show(message) }; Unit }
                val catalogList = rememberLazyListState()
                AbkScaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { AbkTopBar("ABK UI", subtitle = when (page) { "preview" -> "组件预览 · 本地交互"; "build" -> "构建示例 · 无真实任务"; "modules" -> "模块示例 · 本地交互"; else -> "原生 Compose 组件目录" },
                        actions = if (page == "catalog") {
                            { AbkIconButton(AbkIcons.Controls, "分类列表", { sectionsOpen = true }) }
                        } else emptyActions) },
                    bottomBar = { AbkBottomNavigation(listOf(
                        AbkNavigationItem("catalog", "组件", AbkIcons.Controls), AbkNavigationItem("preview", "预览", AbkIcons.Home),
                        AbkNavigationItem("build", "构建", AbkIcons.Build), AbkNavigationItem("modules", "模块", AbkIcons.Modules)), page, { page = it }) },
                    snackbarHost = { AbkSnackbarHost(snackbars) },
                ) { padding ->
                    when (page) {
                        "preview" -> ComponentPreview(Modifier.padding(padding).consumeWindowInsets(padding), snackbars, { page = "catalog" })
                        "build" -> BuildExample(Modifier.padding(padding).consumeWindowInsets(padding), snackbars)
                        "modules" -> ModulesExample(Modifier.padding(padding).consumeWindowInsets(padding), snackbars)
                        else -> CatalogPage(
                            modifier = Modifier.padding(padding).consumeWindowInsets(padding), listState = catalogList,
                            section, { section = it; sectionsOpen = false }, snackbars, feedback,
                            onOpenPreview = { page = "preview" }, onOpenBuild = { page = "build" }, onOpenModules = { page = "modules" })
                    }
                }
                if (sectionsOpen) AbkBottomSheet("分类", { sectionsOpen = false }) {
                    AbkList(plain = true) {
                        CatalogSectionChoices.forEach { (id, title) ->
                            AbkListItem(title, onClick = { section = id; sectionsOpen = false },
                                trailing = if (section == id) { { AbkIcon(AbkIcons.Check, "当前分类") } } else null)
                        }
                    }
                }
            }
        }
    }
}

private val CatalogSectionChoices: List<Pair<String, String>> =
    listOf("全部" to "全部组件") + CatalogSection.entries.map { it.name to it.title }

private val emptyActions: @Composable RowScope.() -> Unit = {}

private class CatalogThemeState(
    theme: String, source: String, seed: String, scale: String, reduceMotion: Boolean,
) {
    var theme by mutableStateOf(theme)
    var source by mutableStateOf(source)
    var seed by mutableStateOf(seed)
    var scale by mutableStateOf(scale)
    var reduceMotion by mutableStateOf(reduceMotion)
}

private val LocalCatalogTheme = staticCompositionLocalOf { CatalogThemeState("system", "system", "blue", "system", false) }

/** Backs every control by its own `rememberSaveable`, so toggling never resets category or scroll state. */
@Composable
private fun rememberCatalogThemeState(): State<CatalogThemeState> {
    var theme by rememberSaveable { mutableStateOf("system") }
    var source by rememberSaveable { mutableStateOf("system") }
    var seed by rememberSaveable { mutableStateOf("blue") }
    var scale by rememberSaveable { mutableStateOf("system") }
    var reduceMotion by rememberSaveable { mutableStateOf(false) }
    val state = remember { CatalogThemeState(theme, source, seed, scale, reduceMotion) }
    state.theme = theme
    state.source = source
    state.seed = seed
    state.scale = scale
    state.reduceMotion = reduceMotion
    return remember { derivedStateOf { state } }
}

@Composable
private fun CatalogPage(
    modifier: Modifier, listState: LazyListState, section: String, onSectionChange: (String) -> Unit,
    snackbars: AbkSnackbarHostState, feedback: (String) -> Unit,
    onOpenPreview: () -> Unit, onOpenBuild: () -> Unit, onOpenModules: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val visible = CatalogSectionChoices.drop(1).withIndex().filter { section == "全部" || it.value.first == section }
    LazyColumn(modifier.fillMaxSize(), state = listState, contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                AbkHero("简约，而不空泛", eyebrow = "LUCIDE · 悠哉字体",
                    description = "与网页目录同一份清单：每个组件的每个变体与状态都在这里。状态由调用方持有，示例数据不进入库。",
                    actions = { AbkButton(onOpenPreview, style = AbkButtonStyle.Ghost) { AbkText("从组件预览开始") } })
                ThemeControls()
            }
        }
        items(visible, key = { it.value.first }) { (index, entry) ->
            val category = CatalogSection.valueOf(entry.first)
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                CatalogSectionHeader(index + 1, category)
                CatalogSectionContent(category, snackbars, feedback, onOpenBuild, onOpenModules)
            }
        }
        item {
            AbkCluster {
                AbkButton({ scope.launch { listState.animateScrollToItem(0) } }, style = AbkButtonStyle.Ghost) { Text("回到顶部") }
                AbkButton({ onSectionChange("全部") }, style = AbkButtonStyle.Ghost) { Text("显示全部组件") }
            }
        }
    }
}

@Composable
private fun ThemeControls() {
    val theme = LocalCatalogTheme.current
    AbkPanel {
        AbkHeading("主题与可访问性")
        AbkSegmented(listOf(AbkOption("system", "跟随系统"), AbkOption("light", "浅色"), AbkOption("dark", "深色")),
            theme.theme, { theme.theme = it })
        AbkSegmented(listOf(AbkOption("system", "系统动态色"), AbkOption("seed", "固定种子")), theme.source, { theme.source = it })
        if (theme.source == "seed") AbkSegmented(listOf(AbkOption("blue", "蓝灰"), AbkOption("green", "苔绿"), AbkOption("purple", "紫")),
            theme.seed, { theme.seed = it })
        AbkSelect(listOf(AbkOption("system", "系统字体大小"), AbkOption("1.0", "1.0 倍"), AbkOption("1.5", "1.5 倍"),
            AbkOption("2.0", "2.0 倍")), theme.scale, { theme.scale = it }, "字体缩放")
        AbkSwitch(theme.reduceMotion, { theme.reduceMotion = it }, "减少装饰动效")
        AbkText("Android 12 以下系统色回退到蓝灰种子；固定种子模式用于与网页对照。",
            style = MaterialTheme.typography.bodySmall, color = AbkTheme.colors.inkMuted)
    }
}
