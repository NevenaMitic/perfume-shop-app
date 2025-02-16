# Perfume Shop

Perfume Shop is a mobile application that allows users to easily browse and purchase their favorite perfumes directly from their phones. The app is developed using **Android Studio** and features secure and fast payments via **PayPal**, while **Firebase Firestore** ensures efficient data storage.

---

## Features
- User authentication via Firebase Authentication
- Secure payment processing with PayPal
- Smooth and responsive UI using Material Design
- High-performance image loading with Glide
- GIF support for an enhanced user experience
- Optimized for Android 8.0+ (API level 23 and above)

---

## Tech Stack
- **Programming Language:** Java
- **Build System:** Gradle
- **Payment Integration:** PayPal Android SDK
- **Database:** Firebase Firestore
- **UI Components:** Material Design, ConstraintLayout
- **Image Loading:** Glide
- **GIF Support:** Droidsonroids GIF Library

---

## Installation
1. Clone the repository:
   ```sh
   git clone https://github.com/your-username/perfume-shop.git
   ```
2. Open the project in **Android Studio**.
3. Connect Firebase to the project and add the `google-services.json` file in the `app/` directory.
4. Sync Gradle dependencies.
5. Run the app on an emulator or a physical device.

---

## Dependencies
The following dependencies are used in the project:
```gradle
dependencies {
    implementation "androidx.appcompat:appcompat:1.6.1"
    implementation "com.google.android.material:material:1.11.0"
    implementation "androidx.constraintlayout:constraintlayout:2.1.4"
    
    // Firebase
    implementation "com.google.firebase:firebase-auth:22.3.1"
    implementation "com.google.firebase:firebase-firestore:24.10.3"
    
    // UI
    implementation "androidx.activity:activity:1.8.0"
    
    // Testing
    testImplementation "junit:junit:4.13.2"
    androidTestImplementation "androidx.test.ext:junit:1.1.5"
    androidTestImplementation "androidx.test.espresso:espresso-core:3.5.1"
    
    // Image Loading
    implementation "com.github.bumptech.glide:glide:4.12.0"
    annotationProcessor "com.github.bumptech.glide:compiler:4.12.0"
    
    // Payment
    implementation "com.paypal.checkout:android-sdk:1.2.1"
    
    // GIF Support
    implementation "pl.droidsonroids.gif:android-gif-drawable:1.2.28"
}
```

---

## Build Configuration
### `build.gradle (Project-level)`
```gradle
buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.1")
    }
}
```

### `build.gradle (App-level)`
```gradle
plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.perfumeshop"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.paypaltest"
        minSdk = 23
        targetSdk = 34
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
```

