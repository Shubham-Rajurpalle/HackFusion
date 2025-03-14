plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
}

android {
    namespace = "com.cricketapp.hackfusion"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.cricketapp.hackfusion"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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
    kotlinOptions {
        jvmTarget = "1.8"
    }

    viewBinding {
        enable = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.barcode.scanning.common)
    implementation(libs.androidx.cardview)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.database.ktx)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.media3.common.ktx)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.firebase.storage.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //Responsive
    implementation ("com.intuit.sdp:sdp-android:1.1.1")
    implementation ("com.intuit.ssp:ssp-android:1.1.1")

    //Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.9.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.google.firebase:firebase-firestore-ktx:24.8.1")
    implementation ("com.google.android.gms:play-services-base:18.0.1")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //Barcode scanner
    implementation("com.google.mlkit:barcode-scanning:17.2.0")
    implementation("com.google.android.gms:play-services-mlkit-barcode-scanning:18.1.0")
    implementation ("com.google.android.material:material:1.9.0")

    implementation ("androidx.camera:camera-core:1.3.0")
    implementation ("androidx.camera:camera-lifecycle:1.3.0")
    implementation ("androidx.camera:camera-view:1.3.0")
    implementation ("androidx.camera:camera-extensions:1.3.0")
    implementation ("androidx.camera:camera-core:1.2.3")
    implementation ("androidx.camera:camera-camera2:1.2.3")
    implementation ("androidx.camera:camera-lifecycle:1.2.3")
    implementation ("androidx.camera:camera-view:1.3.0-beta01")

    implementation ("com.google.mlkit:barcode-scanning:17.2.0")
    implementation ("com.google.mlkit:barcode-scanning:17.2.0")

    implementation ("androidx.camera:camera-core:1.3.0")
    implementation ("androidx.camera:camera-camera2:1.3.0")

    // CameraX Lifecycle
    implementation ("androidx.camera:camera-lifecycle:1.3.0")

    // CameraX View (PreviewView)
    implementation ("androidx.camera:camera-view:1.3.0")

    //Glide
    implementation ("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")

    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")

    // RecyclerView (for displaying lists)
    implementation ("androidx.recyclerview:recyclerview:1.3.2")

    // Fragments (for FragmentContainerView support)
    implementation ("androidx.fragment:fragment-ktx:1.6.2")

    implementation ("com.google.mlkit:barcode-scanning:17.2.0")
    implementation ("com.google.android.gms:play-services-mlkit-barcode-scanning:18.3.0")
    implementation ("com.google.mlkit:barcode-scanning:17.2.0")
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")

    implementation ("com.google.mlkit:barcode-scanning:17.2.0")
    implementation ("com.google.android.gms:play-services-mlkit-barcode-scanning:18.2.0")

    implementation ("com.google.mlkit:barcode-scanning:17.2.0")
    implementation ("com.google.android.gms:play-services-mlkit-barcode-scanning:18.3.0")
    implementation ("com.google.mlkit:barcode-scanning:17.2.0")
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")

    implementation ("com.google.mlkit:barcode-scanning:17.2.0")
    implementation ("com.google.android.gms:play-services-mlkit-barcode-scanning:18.2.0")

    implementation ("com.google.mlkit:barcode-scanning:17.2.0")
    implementation ("com.google.android.gms:play-services-mlkit-barcode-scanning:18.0.1")

    implementation ("com.sun.mail:android-mail:1.6.2")
    implementation ("com.sun.mail:android-activation:1.6.2")
}
