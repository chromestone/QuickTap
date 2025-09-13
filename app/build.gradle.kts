plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.gmail.absolutevanillahelp.quicktap"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.gmail.absolutevanillahelp.quicktap"
        minSdk = 19
        targetSdk = 36
        versionCode = 5
        versionName = "1.13"

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