plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.gmail.absolutevanillahelp.quicktap"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.gmail.absolutevanillahelp.quicktap"
        minSdk = 19
        targetSdk = 34
        versionCode = 3
        versionName = "1.12"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}