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
}

android {
    compileSdkVersion(31)

    defaultConfig {
        minSdkVersion(26)
        buildToolsVersion = "30.0.3"
        targetSdkVersion(31)
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
    }

}

dependencies {
    Coroutines.bucketCoroutines.forEach { implementation(it) }
    Test.bucketTestImpl.forEach { testImplementation(it) }
    Test.bucketAndroidTestImpl.forEach { androidTestImplementation(it) }
    Test.bucketTestImpl.forEach { testImplementation(it) }

    implementation(platform("io.arrow-kt:arrow-stack:1.0.0"))
    implementation("io.arrow-kt:arrow-core")
    implementation("io.arrow-kt:arrow-fx-coroutines")
    implementation("io.arrow-kt:arrow-fx-stm")
}

