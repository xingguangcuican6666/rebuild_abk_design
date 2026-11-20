# ABK UI · 交接

**当前阶段：网页已收敛为 Lucide；Android Compose 库和独立 catalog App 的源码已完成首轮覆盖。** `:abk-ui` 是可复用库，`:catalog` 只保留完整目录、一个精选组件预览页和两个静态交互示例（构建、模块），不接真实构建服务或网络下载。

## 入口

| 目标 | 入口 |
| --- | --- |
| 网页组件和组合页面 | `index.html` |
| 原生主题、颜色、字体 | `abk-ui/src/main/kotlin/.../theme` |
| 原生 Lucide 图标 | `abk-ui/src/main/kotlin/.../icons`；生成器为 `scripts/generate-compose-icons.py` |
| 原生组件 | `abk-ui/src/main/kotlin/.../components` |
| 原生目录入口 | `catalog/src/main/kotlin/.../MainActivity.kt`、`CatalogSections.kt`、`ExampleScreens.kt` |
| 原生组件预览页 | `catalog/src/main/kotlin/.../ComponentPreview.kt`（独立「预览」底栏入口，精选真实组件组合） |
| 覆盖矩阵与验证界限 | `docs/compose-coverage.md` |

## 原生约定

- 所有公开库 API 统一 `Abk*` 前缀；状态由宿主通过 `value/onValueChange`、`selected/onSelect`、`expanded/onExpandedChange` 等参数持有。
- `AbkBottomNavigation` 只使用等权入口；选择不改变图标、字号或热区，仅用中性短线和选择语义标记当前位置。
- 构建卡片使用普通面色，进度、徽标和反馈才使用状态色；Snackbar 保持主题同底色并支持撤销结果。
- 触控区域至少 48dp；装饰图标使用空描述，独立图标按钮必须传可访问描述；错误、警告和加载不能只靠颜色。
- `AbkTheme` 不持久化偏好。系统动态色与固定种子是两个明确来源；固定种子路径对照网页的 Tonal Spot / 2021 / phone / contrast 0。
- `reduceMotion` 会关闭库内装饰性的 shimmer、spinner、dots 和 indeterminate animation；宿主仍应根据自己的系统偏好传入该值。

## 构建与验证

工具链固定为 JDK 17、Wrapper 8.13、AGP 8.11.1、Kotlin/Compose compiler 2.2.21、Compose BOM 2025.11.01。产物字节码为 Java 17；`Test` 任务由 `abk-ui/build.gradle.kts` 的 `javaLauncher` 固定用 Java 21 启动，因为 Robolectric SDK 36 fixture 需要它，所以命令里的 `JAVA_HOME` 仍是 17。实际 Gradle 构建还需要用户自行审阅并接受 Android SDK 许可，然后安装 API 36 与构建工具；不得复制许可或自动接受。

```bash
JAVA_HOME=/usr/lib/jvm/java-17-openjdk ./gradlew :abk-ui:assembleRelease :catalog:assembleDebug
JAVA_HOME=/usr/lib/jvm/java-17-openjdk ./gradlew :abk-ui:testDebugUnitTest :abk-ui:lintDebug :catalog:lintDebug
```

当前已完成：Wrapper 分发包与校验值、图标生成脚本、JS 语法/差异检查、SDK-free host fixture 中的 Kotlin/Compose 编译，以及 **20 项 host 测试全部通过**（`AbkComponentsTest` 15、`AbkPaletteTest` 2 含 512 个黄金色板向量、`AbkSnackbarTest` 3），在 Java 21 测试 JVM 上运行。网页侧 Edge 回归也通过：主题、57 个图标、折叠面板、对话框/弹层/菜单开关与焦点回归、双主题撤销提示、等权底栏、1440/390/320 三档宽度、图标脚本缺失回退。

当前未完成：Gradle Android 资源打包（`assembleRelease`/`assembleDebug`）、Lint 与设备或模拟器视觉/TalkBack/IME/安装验收。打包被未接受的 `build-tools;35.0.0` 与 `platforms;android-36` 许可阻断；SDK-free fixture 不产出 AAR/APK。没有设备时不能把源检查写成真机通过。

网页预览仍遵循只导出 Git 快照后监听 `127.0.0.1` 的规则，不从仓库根目录直接启动 HTTP 服务，也不把本机凭据、SDK、local.properties、签名或构建产物加入 Git。
