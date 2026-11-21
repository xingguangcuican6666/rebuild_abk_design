package io.github.xingguangcuican6666.abk.catalog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import io.github.xingguangcuican6666.abk.ui.components.*
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
    var theme by rememberSaveable { mutableStateOf("system") }
    var source by rememberSaveable { mutableStateOf("system") }
    var seed by rememberSaveable { mutableStateOf("blue") }
    var scale by rememberSaveable { mutableStateOf("system") }
    var reduceMotion by rememberSaveable { mutableStateOf(false) }
    val dark = when (theme) { "dark" -> true; "light" -> false; else -> isSystemInDarkTheme() }
    val density = LocalDensity.current
    val fontScale = when (scale) { "1.0" -> 1f; "1.5" -> 1.5f; "2.0" -> 2f; else -> density.fontScale }
    CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale)) {
        AbkTheme(darkTheme = dark, colorSource = if (source == "system") AbkColorSource.System else AbkColorSource.Seed,
            seed = when (seed) { "green" -> Color(0xFF526A43); "purple" -> Color(0xFF795282); else -> AbkDefaultSeed },
            reduceMotion = reduceMotion) {
            val snackbars = rememberAbkSnackbarHostState()
            val scope = rememberCoroutineScope()
            val feedback: (String) -> Unit = { message -> scope.launch { snackbars.show(message) }; Unit }
            AbkScaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = { AbkTopBar("ABK UI", subtitle = when (page) { "preview" -> "组件预览 · 本地交互"; "build" -> "构建示例 · 无真实任务"; "modules" -> "模块示例 · 本地交互"; else -> "原生 Compose 组件目录" }) },
                bottomBar = { AbkBottomNavigation(listOf(
                    AbkNavigationItem("catalog", "组件", AbkIcons.Controls), AbkNavigationItem("preview", "预览", AbkIcons.Home),
                    AbkNavigationItem("build", "构建", AbkIcons.Build), AbkNavigationItem("modules", "模块", AbkIcons.Modules)), page, { page = it }) },
                snackbarHost = { AbkSnackbarHost(snackbars) },
            ) { padding ->
                when (page) {
                    "preview" -> ComponentPreview(Modifier.padding(padding).consumeWindowInsets(padding), snackbars, { page = "catalog" })
                    "build" -> BuildExample(Modifier.padding(padding).consumeWindowInsets(padding), snackbars)
                    "modules" -> ModulesExample(Modifier.padding(padding).consumeWindowInsets(padding), snackbars)
                    else -> LazyColumn(Modifier.padding(padding).consumeWindowInsets(padding).fillMaxSize(),
                        contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        item {
                            AbkHero("简约，而不空泛", eyebrow = "LUCIDE · 霞鹜文楷", description = "可复用的 Android 组件。状态由调用方持有，示例数据不进入库。",
                                actions = { AbkButton({ page = "preview" }, style = AbkButtonStyle.Ghost) { AbkText("打开组件预览页") } })
                        }
                        item {
                            AbkPanel {
                                AbkHeading("主题与可访问性")
                                AbkSegmented(listOf(AbkOption("system", "跟随系统"), AbkOption("light", "浅色"), AbkOption("dark", "深色")), theme, { theme = it })
                                AbkSegmented(listOf(AbkOption("system", "系统动态色"), AbkOption("seed", "固定种子")), source, { source = it })
                                if (source == "seed") AbkSegmented(listOf(AbkOption("blue", "蓝灰"), AbkOption("green", "苔绿"), AbkOption("purple", "紫")), seed, { seed = it })
                                AbkSelect(listOf(AbkOption("system", "系统字体大小"), AbkOption("1.0", "1.0 倍"), AbkOption("1.5", "1.5 倍"), AbkOption("2.0", "2.0 倍")), scale, { scale = it }, "字体缩放")
                                AbkSwitch(reduceMotion, { reduceMotion = it }, "减少装饰动效")
                                AbkText("Android 12 以下系统色回退到蓝灰种子；固定种子模式用于与网页对照。", style = MaterialTheme.typography.bodySmall, color = AbkTheme.colors.inkMuted)
                            }
                        }
                        item {
                            AbkSelect(listOf(AbkOption("全部", "全部组件")) + CatalogSection.entries.map { AbkOption(it.title, it.title) },
                                section, { section = it }, "跳到分类")
                        }
                        items(CatalogSection.entries.filter { section == "全部" || it.title == section }, key = { it.name }) { category ->
                            Surface(Modifier.fillMaxWidth(), color = AbkTheme.colors.background) {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    AbkHeading(category.title)
                                    CatalogSectionContent(category, snackbars, feedback)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
