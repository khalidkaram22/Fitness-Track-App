plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.fitnesstrackerapp"
    compileSdk = 34

    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.example.fitnesstrackerapp"
        minSdk = 26
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

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //navigation
    implementation ("androidx.navigation:navigation-fragment-ktx:2.8.2")
    implementation ("androidx.navigation:navigation-ui-ktx:2.8.2")

    implementation ("com.google.android.material:material:1.9.0")
    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("androidx.core:core-ktx:1.10.1")

    // firebase
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")

    // Retrofit and Gson converter
    implementation ("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
   
    // glide
    implementation ("com.github.bumptech.glide:glide:4.16.0")

// location
    implementation("com.google.android.gms:play-services-location:21.3.0")

// pie chart
    implementation ("com.github.PhilJay:MPAndroidChart:3.1.0")
}