plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "io.github.xingguangcuican6666.abk.catalog"
    compileSdk = 36
    defaultConfig {
        applicationId = "io.github.xingguangcuican6666.abk.catalog"
        minSdk = 23
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0"
    }
    buildFeatures { compose = true }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
kotlin { jvmToolchain(17) }

dependencies {
    implementation(project(":abk-ui"))
    implementation(libs.activity.compose)
    implementation(libs.core.ktx)
    implementation(libs.compose.preview)
    debugImplementation(libs.compose.tooling)
}
