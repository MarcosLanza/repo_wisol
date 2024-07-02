plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.wisol.wisolapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.wisol.wisolapp"
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
}

dependencies {


    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    implementation ("com.github.pwittchen:reactivenetwork-rx2:3.0.8")

    implementation ("io.reactivex.rxjava2:rxjava:2.2.21")
    implementation ("com.google.android.gms:play-services-drive:17.0.0")

    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:x.y.z")

    implementation ("com.opencsv:opencsv:5.9")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.0")



    implementation("com.android.volley:volley:1.2.1")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation ("androidx.recyclerview:recyclerview:1.3.2")

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation("androidx.privacysandbox.tools:tools-core:1.0.0-alpha05")
    implementation("com.google.android.gms:play-services-mlkit-text-recognition-common:19.0.0")
    implementation("com.google.android.gms:play-services-vision:20.1.3")
    implementation("com.google.firebase:firebase-inappmessaging-ktx:20.4.0")



    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

}