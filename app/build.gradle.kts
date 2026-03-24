import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace   = "com.crumbatelier"
    compileSdk  = 35

    defaultConfig {
        applicationId = "com.crumbatelier"
        minSdk        = 26
        targetSdk     = 35
        versionCode   = 1
        versionName   = "1.0"

        // Reads from local.properties
        val localProps = Properties().apply {
            load(rootProject.file("local.properties").inputStream())
        }
        buildConfigField("String", "SUPABASE_URL",      "\"${localProps.getProperty("SUPABASE_URL", "")}\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"${localProps.getProperty("SUPABASE_ANON_KEY", "")}\"")
    }

    buildFeatures {
        compose     = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions { jvmTarget = "17" }
}

dependencies {

    // ── Compose BOM ──────────────────────────────────────────────────
    val composeBom = platform("androidx.compose:compose-bom:2024.12.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // ── Activity / Lifecycle ─────────────────────────────────────────
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")

    // ── Navigation ───────────────────────────────────────────────────
    implementation("androidx.navigation:navigation-compose:2.8.4")

    // ── Hilt ─────────────────────────────────────────────────────────
    implementation("com.google.dagger:hilt-android:2.52")
    ksp("com.google.dagger:hilt-android-compiler:2.52")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // ── Supabase 3.x BOM ─────────────────────────────────────────────
    val supabaseBom = platform("io.github.jan-tennert.supabase:bom:3.0.3")
    implementation(supabaseBom)
    implementation("io.github.jan-tennert.supabase:postgrest-kt")
    implementation("io.github.jan-tennert.supabase:auth-kt")        // 3.x auth
    implementation("io.github.jan-tennert.supabase:realtime-kt")
    implementation("io.github.jan-tennert.supabase:storage-kt")
    implementation("io.github.jan-tennert.supabase:functions-kt")

    // Ktor engine — must match 3.x (ktor 3.x)
    implementation("io.ktor:ktor-client-android:3.0.3")

    // ── Serialization ────────────────────────────────────────────────
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    // ── Image loading ────────────────────────────────────────────────
    implementation("io.coil-kt:coil-compose:2.7.0")

    // ── Core ─────────────────────────────────────────────────────────
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.core:core-splashscreen:1.0.1")
}