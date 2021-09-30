/*
 * Copyright (c) 2021.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */


plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id(BuildPlugins.hilt)
    id(BuildPlugins.allOpen)
    //id("com.squareup.sqldelight")
}



android {
    compileSdkVersion(31)

    defaultConfig {
        minSdkVersion(26)
        targetSdkVersion(31)
        buildToolsVersion = "30.0.2"
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
        languageVersion = "1.5"
        apiVersion = "1.5"
    }

    buildTypes {
        getByName("debug") {
            isDebuggable = true
            isMinifyEnabled = false
            buildConfigField("String", "API_URL", "\"https://public-api.wordpress.com/rest/v1.1/\"")

        }

        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "API_URL", "\"https://public-api.wordpress.com/rest/v1.1/\"")
        }
    }

}


dependencies {
    Base.bucketImplementation.forEach { implementation(it) }
    Base.bucketImplementationKapt.forEach { kapt(it) }

    Test.bucketTestImpl.forEach { testImplementation(it) }
    Test.bucketDebugImpl.forEach { debugImplementation(it) }

    testImplementation(Database.sqldelightTestDriver) {
        endorseStrictVersions()
    }

    DomainModule.dependsOn.forEach { api(project(it)) }

    DI.bucketDI.forEach { androidTestImplementation(it) }
    DI.bucketDI.forEach { implementation(it) }
    DI.annotationDI.forEach { kaptAndroidTest(it) }
    DI.annotationDI.forEach { kapt(it) }

    implementation(platform("io.arrow-kt:arrow-stack:1.0.0"))
    implementation("io.arrow-kt:arrow-core")
    implementation("io.arrow-kt:arrow-fx-coroutines")
    implementation("io.arrow-kt:arrow-fx-stm")

    HTTP.bucketHTTPImpl.forEach { implementation(it) }

    Database.bucketDB.forEach { implementation(it) }
}