object Base {
    private object Version {
        const val jetpack = "1.1.0"
        const val ktxCore = "1.2.0"
        const val compiler = "1.0.0"
        const val fragmentsKtx = "1.2.4"
        const val vectors = "1.1.0"
        const val seekableVectors = "1.0.0-alpha02"
    }

    private const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.4.20"
    private const val appCompat = "androidx.appcompat:appcompat:${Version.jetpack}"
    private const val ktxCore = "androidx.core:core-ktx:${Version.ktxCore}"
    private const val androidArchCompiler = "android.arch.lifecycle:compiler:${Version.compiler}"
    private const val androidFragmentsKtx = "androidx.fragment:fragment-ktx:${Version.fragmentsKtx}"
    private const val vectorDrawable = "androidx.vectordrawable:vectordrawable:${Version.vectors}"
    private const val vectorDrawableAnimated =
        "androidx.vectordrawable:vectordrawable-animated:${Version.vectors}"
    private const val vectorDrawablesAnimatedSeekable =
        "androidx.vectordrawable:vectordrawable-seekable:${Version.seekableVectors}"

    private val missingDependencies = listOf(
        "androidx.palette:palette:1.0.0-alpha3",
        "androidx.fragment:fragment-ktx:1.2.4",
        "androidx.appcompat:appcompat:1.1.0",
        "androidx.core:core:1.3.1",
        "androidx.lifecycle:lifecycle-runtime:2.3.1",
        "androidx.fragment:fragment:1.3.2",
        "androidx.versionedparcelable:versionedparcelable:1.1.0",
        "androidx.annotation:annotation-experimental:1.0.0",
        "androidx.customview:customview:1.0.0",
        "androidx.lifecycle:lifecycle-viewmodel-savedstate:2.3.1",
        "androidx.vectordrawable:vectordrawable:1.2.0-alpha02",
        "androidx.interpolator:interpolator:1.0.0",
        "androidx.loader:loader:1.0.0",
        "androidx.drawerlayout:drawerlayout:1.0.0",
        "androidx.collection:collection:1.1.0",
        "androidx.viewpager:viewpager:1.0.0",
        "androidx.activity:activity:1.2.2",
        "androidx.lifecycle:lifecycle-livedata-core-ktx:2.2.0",
        "androidx.arch.core:core-common:2.1.0",
        "androidx.activity:activity-ktx:1.1.0",
        "androidx.test:runner:1.3.0",
        "androidx.core:core-ktx:1.2.0",
        "androidx.collection:collection-ktx:1.1.0",
        "androidx.annotation:annotation:1.1.0",
        "androidx.savedstate:savedstate:1.1.0",
        "androidx.lifecycle:lifecycle-runtime-ktx:2.2.0",
        "androidx.test:monitor:1.3.0",
        "androidx.lifecycle:lifecycle-common:2.3.1",
        "androidx.appcompat:appcompat-resources:1.1.0",
        "androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0",
        "androidx.test:core:1.3.0",
        "androidx.lifecycle:lifecycle-livedata:2.0.0",
        "androidx.tracing:tracing:1.0.0",
        "androidx.vectordrawable:vectordrawable-seekable:1.0.0-alpha02",
        "androidx.lifecycle:lifecycle-viewmodel:2.3.1",
        "androidx.core:core-animation:1.0.0-alpha02",
        "androidx.lifecycle:lifecycle-livedata-core:2.3.1",
        "androidx.arch.core:core-runtime:2.1.0",
        "androidx.test:rules:1.3.0",
        "androidx.cursoradapter:cursoradapter:1.0.0",
        "androidx.vectordrawable:vectordrawable-animated:1.1.0"
    )
    val bucketImplementation = listOf(
        kotlinStdLib,
        appCompat,
        ktxCore,
        androidFragmentsKtx,
        vectorDrawable,
        vectorDrawableAnimated,
        vectorDrawablesAnimatedSeekable
    ) + missingDependencies
    val bucketImplementationKapt = listOf(androidArchCompiler)

}

object Test {

    private object Version {
        const val junit4 = "4.12"
        const val androidxTest = "1.1.1"
        const val espresso = "3.3.0"
        const val okhttp_mockwebserver = "4.4.0"
        const val mockk = "1.10.0"
        const val assertJ = "3.13.2"
        const val coroutinesTest = "1.3.3"
        const val testCore = "1.3.0"
        const val robolectric = "4.4"
    }

    private const val junit4 = "junit:junit:${Version.junit4}"
    private const val testCore = "androidx.test:core:${Version.testCore}"
    private const val testCoreKtx = "androidx.test:core-ktx:${Version.testCore}"
    private const val testRunner = "androidx.test.ext:junit:${Version.androidxTest}"
    private const val junitKtx = "androidx.test.ext:junit-ktx:${Version.androidxTest}"
    private const val coreTesting = "android.arch.core:core-testing:${Version.androidxTest}"
    private const val espresso = "androidx.test.espresso:espresso-core:${Version.espresso}"
    private const val espressoContrib =
        "androidx.test.espresso:espresso-contrib:${Version.espresso}"
    private const val espressoIntents =
        "androidx.test.espresso:espresso-intents:${Version.espresso}"
    private const val mockWebServer =
        "com.squareup.okhttp3:mockwebserver:${Version.okhttp_mockwebserver}"
    private const val assertJ = "org.assertj:assertj-core:${Version.assertJ}"
    private const val mockkAndroid = "io.mockk:mockk-android:${Version.mockk}"
    private const val mockk = "io.mockk:mockk:${Version.mockk}"
    private const val kotlinTest = "org.jetbrains.kotlin:kotlin-stdlib:1.4.20"
    private const val kotlinTestJUnit = "org.jetbrains.kotlin:kotlin-test-junit:1.4.20"
    private const val coroutinesTest =
        "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Version.coroutinesTest}"
    private const val rules = "androidx.test:rules:${Version.testCore}"
    private const val runner = "androidx.test:runner:${Version.testCore}"
    private const val robolectric = "org.robolectric:robolectric:${Version.robolectric}"

    val bucketTestImpl = listOf(
        junit4,
        mockk,
        mockWebServer,
        assertJ,
        kotlinTestJUnit,
        coreTesting,
        coroutinesTest,
        testCoreKtx,
        espresso,
        espressoIntents,
        espressoContrib,
        testRunner,
        robolectric,
        testCore
    )

    val bucketAndroidTestImpl = listOf(
        testRunner,
        junitKtx,
        testCoreKtx,
        mockkAndroid,
        kotlinTest,
        espresso,
        espressoContrib,
        rules,
        runner,
        testCore
    )

    val bucketDebugImpl = listOf(testCore, rules, runner)
}


object DI {
    private object Version {
        const val java_inject = "1"
    }


    private const val javaInject = "javax.inject:javax.inject:${Version.java_inject}"
    private const val hilt = "com.google.dagger:hilt-android:2.38.1"
    private const val hiltCompiler = "com.google.dagger:hilt-compiler:2.38.1"

    val bucketDI = listOf(hilt, javaInject)
    val annotationDI = listOf(hiltCompiler)
}

object UI {

    private object Version {
        const val androidx_cardview = "1.0.0"
        const val androidx_recyclerview = "1.2.0-alpha05"
        const val constraintLayout = "2.0.0"
        const val swipeRefresh = "1.0.0"
        const val viewPager = "1.0.0"
        const val material = "1.3.0-alpha02"
    }

    //Android libraries
    private const val recyclerview =
        "androidx.recyclerview:recyclerview:${Version.androidx_recyclerview}"
    private const val cardview = "androidx.cardview:cardview:${Version.androidx_cardview}"
    private const val constraintLayout =
        "androidx.constraintlayout:constraintlayout:${Version.constraintLayout}"
    private const val swiperefresh =
        "androidx.swiperefreshlayout:swiperefreshlayout:${Version.swipeRefresh}"
    private const val materialDesignComponents =
        "com.google.android.material:material:${Version.material}"
    private const val viewPager = "androidx.viewpager2:viewpager2:${Version.viewPager}"

    val bucketUIImpl = listOf(
        recyclerview,
        cardview,
        swiperefresh,
        constraintLayout,
        materialDesignComponents,
        viewPager
    )
}

object HTTP {

    private object Version {
        const val okhttp_interceptor = "4.4.0"
        const val retrofit_version = "2.7.2"
    }

    //Okhttp Interceptor
    const val okhttpInterceptor =
        "com.squareup.okhttp3:logging-interceptor:${Version.okhttp_interceptor}"

    //Retrofit
    private const val retrofit = "com.squareup.retrofit2:retrofit:${Version.retrofit_version}"
    private const val retrofitRx =
        "com.squareup.retrofit2:adapter-rxjava2:${Version.retrofit_version}"
    private const val retrofitGson =
        "com.squareup.retrofit2:converter-gson:${Version.retrofit_version}"

    val bucketHTTPImpl = listOf(okhttpInterceptor, retrofit, retrofitRx, retrofitGson)
}

object LiveData {

    private object Version {
        const val archComponents = "2.2.0"
    }

    private const val lifecycleExtensions =
        "androidx.lifecycle:lifecycle-extensions:${Version.archComponents}"
    private const val lifecycleVM =
        "androidx.lifecycle:lifecycle-viewmodel-ktx:${Version.archComponents}"
    private const val lifecycleLivedata =
        "androidx.lifecycle:lifecycle-livedata-ktx:${Version.archComponents}"
    private const val lifecycleRuntime =
        "androidx.lifecycle:lifecycle-runtime-ktx:${Version.archComponents}"

    val bucketLiveData =
        listOf(lifecycleExtensions, lifecycleLivedata, lifecycleRuntime, lifecycleVM)
}

object Coroutines {

    private object Version {
        const val coroutines = "1.3.5"
    }

    private const val coroutinesCore =
        "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Version.coroutines}"
    private const val coroutinesAndroid =
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Version.coroutines}"
    private const val coroutinesStdLib = "org.jetbrains.kotlin:kotlin-stdlib:1.4.20"

    val bucketCoroutines = listOf(coroutinesAndroid, coroutinesCore, coroutinesStdLib)
}

object Functional {

    object Version {
        const val arrow = "0.13.0"
    }

    private const val arrowCore = "io.arrow-kt:arrow-core-data:${Version.arrow}"
    private const val arrowSyntax = "io.arrow-kt:arrow-syntax:${Version.arrow}"

    val missingDependencies = listOf(
        "io.arrow-kt:arrow-fx-coroutines:1.0.0",
        "io.arrow-kt:arrow-fx-stm:1.0.0"
    )
    val bucketFunctional = listOf(arrowCore, arrowSyntax) + missingDependencies
}

object Image {

    private object Version {
        const val glide = "4.11.0"
    }

    private const val glide = "com.github.bumptech.glide:glide:${Version.glide}"
    private const val glideKapt = "com.github.bumptech.glide:compiler:${Version.glide}"

    val bucketImage = listOf(glide)
    val bucketImageKapt = listOf(glideKapt)
}

object Database {

    private const val sqldelight = "com.squareup.sqldelight:android-driver:1.5.1"
    const val sqldelightTestDriver = "com.squareup.sqldelight:sqlite-driver:1.5.1"

    val bucketDB = listOf(sqldelight)
}
