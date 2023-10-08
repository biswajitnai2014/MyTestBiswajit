plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")


}

android {
    namespace = "com.bis.mytestbiswajit"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.bis.mytestbiswajit"
        minSdk = 26
        targetSdk = 33
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

    buildFeatures {
        dataBinding =true
        viewBinding= true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-crashlytics-ktx:18.4.1")
    implementation("com.google.firebase:firebase-analytics-ktx:21.3.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")




    // ViewModel
    val lifecycle_version = "2.6.2"
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")

    //kotlin coroutine
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    // Kotlin navigation
    val nav_version = "2.7.2"
    implementation ("androidx.navigation:navigation-fragment-ktx:$nav_version")
    implementation ("androidx.navigation:navigation-ui-ktx:$nav_version")
    implementation ("androidx.navigation:navigation-common:$nav_version")
    androidTestImplementation ("androidx.navigation:navigation-testing:$nav_version")

    implementation ("com.karumi:dexter:6.2.3")


    val camerax_version = "1.2.3"
    implementation ("androidx.camera:camera-core:${camerax_version}")
    implementation ("androidx.camera:camera-camera2:${camerax_version}")
    implementation ("androidx.camera:camera-lifecycle:${camerax_version}")
    implementation ("androidx.camera:camera-video:${camerax_version}")

    implementation ("androidx.camera:camera-view:${camerax_version}")
    implementation ("androidx.camera:camera-extensions:${camerax_version}")

    //circle shape image
    implementation ("de.hdodenhof:circleimageview:3.1.0")

    implementation ("com.mikhaellopez:circularprogressbar:3.1.0")

    //glide
    implementation ("com.github.bumptech.glide:glide:4.14.2")


    //https://square.github.io/retrofit/
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")


    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")



    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

// define any required OkHttp artifacts without version
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
}


