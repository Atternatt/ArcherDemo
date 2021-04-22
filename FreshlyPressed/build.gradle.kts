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

import org.jetbrains.kotlin.konan.properties.Properties
import java.io.FileInputStream

sealed class BuildType(val type: String) {
    object Debug : BuildType("debug")
    object Release : BuildType("release")
}

enum class KeystoreFields(val value: String) {
    StoreFile("storeFile"),
    StorePassword("storePassword"),
    KeyAlias("keyAlias"),
    KeyPassword("keyPassword")
}

lateinit var keystoreProperties: Properties

plugins {
    id(BuildPlugins.androidApplication)
    id(BuildPlugins.kotlinAndroid)
    id(BuildPlugins.kaptPlugin)
    id(BuildPlugins.hilt)
    id(BuildPlugins.allOpen)
}

dependencies {
    BaseDependencies.dependencies.forEach {
        it.implementatons.forEach { implementation(it) }
        it.apt.forEach { kapt(it) }
    }

    Test.bucketTestImpl.forEach { testImplementation(it) }
    Test.bucketAndroidTestImpl.forEach { androidTestImplementation(it) }
    DI.implementatons.forEach { androidTestImplementation(it) }
    DI.apt.forEach { kaptAndroidTest(it) }
    Test.bucketDebugImpl.forEach { debugImplementation(it) }

    AppModule.dependsOn.forEach { api(project(it)) }
}

allOpen {
    annotation("org.wordpress.android.testing.OpenClassAnnotation")
    // annotations("com.another.Annotation", "com.third.Annotation")
}

android {
    compileSdkVersion(AndroidSdk.compile)

    defaultConfig {
        applicationId = AppDefaultConfig.applicationId
        minSdkVersion(AndroidSdk.min)
        targetSdkVersion(AndroidSdk.target)
        buildToolsVersion(AndroidSdk.buildToolsVersion)
        versionCode = AppDefaultConfig.appVersionCode
        versionName = AppDefaultConfig.versionName
        testInstrumentationRunner = Testing.testRunner
        vectorDrawables.useSupportLibrary = true
    }

    buildFeatures {
        viewBinding = true
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }

    hilt {
        enableTransformForLocalTests = true
    }

    signingConfigs {
        create(BuildType.Release.type) {
            keystoreProperties = getKeystoreProperties(BuildType.Release.type)
            val success = preCheck(true)
            if (success) {
                storeFile = file(keystoreProperties.getKeystoreProperty(KeystoreFields.StoreFile))
                storePassword = keystoreProperties.getKeystoreProperty(KeystoreFields.StorePassword)
                keyAlias = keystoreProperties.getKeystoreProperty(KeystoreFields.KeyAlias)
                keyPassword = keystoreProperties.getKeystoreProperty(KeystoreFields.KeyPassword)
            }
        }

        getByName(BuildType.Debug.type) {
            keystoreProperties = getKeystoreProperties(BuildType.Debug.type)
            val success = preCheck(false)
            if (success) {
                storeFile = file(keystoreProperties.getKeystoreProperty(KeystoreFields.StoreFile))
                keyAlias = keystoreProperties.getKeystoreProperty(KeystoreFields.KeyAlias)
            }
        }
    }

    buildTypes {
        getByName(BuildType.Debug.type) {
            applicationIdSuffix = ".${BuildType.Debug.type}"
            versionNameSuffix = "-${BuildType.Debug.type}"
            isDebuggable = true
            isMinifyEnabled = false
            signingConfig = signingConfigs.findByName(BuildType.Debug.type)
            buildConfigField("String", "API_URL", "\"${BuildVariant.Release.endpoint}\"")

        }

        getByName(BuildType.Release.type) {
            isMinifyEnabled = true
            signingConfig = signingConfigs.findByName(BuildType.Release.type)
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "API_URL", "\"${BuildVariant.Debug.endpoint}\"")
        }
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
}

//region publication

/**
 * Set up a secrets folder and put there your (release/debug).properties file with all the properties to sign the app.
 *
 * @param name the name of the folder to load.
 */
fun getKeystoreProperties(name: String): Properties {
    val keystorePropertiesFile = rootProject.file("secrets/$name.properties")
    val keystoreProperties = Properties()
    if (keystorePropertiesFile.exists()) {
        val file = FileInputStream(keystorePropertiesFile)
        keystoreProperties.load(file)
    } else {
        println("No \"secrets/$name.properties\" file found, please add it to your project in order to build your app in $name mode")
    }
    return keystoreProperties
}

/**
 * Obtains the desired property.
 */
fun Properties.getKeystoreProperty(field: KeystoreFields): String {
    val environmentVariable = getEnvVariable(field)
    var fieldValue = this[field.value]
    if (fieldValue == null) {
        fieldValue = System.getenv(environmentVariable)
    }
    return fieldValue.toString()
}

/**
 * Checks if we have all the properties required to sign the apk
 *
 * @param wether we are signing the release build.
 */
fun preCheck(isReleaseBuild: Boolean): Boolean {
    val propertiesNotFound = emptyList<String>().toMutableList()
    KeystoreFields.values().forEach {
        val environmentVariable = getEnvVariable(it)
        var fieldValue = keystoreProperties[it.value]
        if (fieldValue == null) {
            fieldValue = System.getenv(environmentVariable)
            if (fieldValue == null) {
                propertiesNotFound.add(it.value)
            } else {
                println("Success!")
            }
        }
    }

    if (propertiesNotFound.isNotEmpty()) {
        showError(propertiesNotFound, isReleaseBuild)
    }
    return propertiesNotFound.isEmpty()
}

/**
 * You can set this keys in order to sign the release build without having a explicit file in the project.
 *
 * In this case We set keys just in case we want to use a CI (Bitsire) to build and sign our app.
 *
 * @param field the name of the field that we are trying to find.
 */
fun getEnvVariable(field: KeystoreFields): String =
    when (field) {
        KeystoreFields.StoreFile -> "BITRISEIO_ANDROID_KEYSTORE_URL"
        KeystoreFields.StorePassword -> "BITRISEIO_ANDROID_KEYSTORE_PASSWORD"
        KeystoreFields.KeyAlias -> "BITRISEIO_ANDROID_KEYSTORE_ALIAS"
        KeystoreFields.KeyPassword -> "BITRISEIO_ANDROID_KEYSTORE_PRIVATE_KEY_PASSWORD"
    }

/**
 * Shows an error in the console just in case we don't have set any key or environment properties required in order to sign the apk.
 *
 * @param fields fields not found
 *
 * @param isReleaseBuild whether we are using the release signing config
 */
fun showError(fields: List<String>, isReleaseBuild: Boolean) =
    println(
        "Ouch! We are not able to find \"$fields\" fields, here is some help:\n" +
            "- isRelease:$isReleaseBuild\n" +
            "- Check that your properties file inside secrets folder is following this structure:\n" +
            "     storeFile=../secrets/debug.keystore\n" +
            "     storePassword=StorePassword\n" +
            "     keyAlias=Alias\n" +
            "     keyPassword=KeyPassword\n" +
            "- Check that your environment variables are defined in the following way:\n" +
            "        KeystoreFields.StoreFile -> \"BITRISEIO_ANDROID_KEYSTORE_URL\"\n" +
            "        KeystoreFields.StorePassword -> \"BITRISEIO_ANDROID_KEYSTORE_PASSWORD\"\n" +
            "        KeystoreFields.KeyAlias -> \"BITRISEIO_ANDROID_KEYSTORE_ALIAS\"\n" +
            "        KeystoreFields.KeyPassword -> \"BITRISEIO_ANDROID_KEYSTORE_PRIVATE_KEY_PASSWORD\""
    )
//endregion