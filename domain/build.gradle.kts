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
    id(BuildPlugins.androidLibrary)
    id(BuildPlugins.kotlinAndroid)
    id(BuildPlugins.kaptPlugin)
    id(BuildPlugins.hilt)
    id(BuildPlugins.sqldelight)
    id(BuildPlugins.allOpen)
}

android {
    compileSdkVersion(AndroidSdk.compile)
    buildToolsVersion(AndroidSdk.buildToolsVersion)

    defaultConfig {
        minSdkVersion(AndroidSdk.min)
        targetSdkVersion(AndroidSdk.target)
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
            buildConfigField("String", "API_URL", "\"${BuildVariant.Release.endpoint}\"")

        }

        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "API_URL", "\"${BuildVariant.Debug.endpoint}\"")
        }
    }
}


dependencies {
    BaseDependencies.dependencies.forEach {
        it.implementatons.forEach { implementation(it) }
        it.apt.forEach { kapt(it) }
    }

    Test.bucketTestImpl.forEach { testImplementation(it) }
    Test.bucketDebugImpl.forEach { debugImplementation(it) }

    testImplementation(Database.testDriver) {
        isForce = true
    }

    DomainModule.dependsOn.forEach { api(project(it)) }
}