plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.scannertest"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.scannertest"
        minSdk = 24
        targetSdk = 19
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(files("C:/Users/Sumin.Bang/AndroidStudioProjects/ScannerTest/libs/barcode_scanner_library_v2.0.8.0.aar"))
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}