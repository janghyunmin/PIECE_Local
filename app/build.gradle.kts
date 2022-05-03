import org.jetbrains.kotlin.gradle.plugin.statistics.ReportStatisticsToElasticSearch.enable
import org.jetbrains.kotlin.kapt3.base.Kapt.kapt
import org.jetbrains.kotlin.storage.CacheResetOnProcessCanceled.enabled

/**
 * @author jhm
 * @since 2022/03/21
 * @commnet Migration Groovy => Kotlin DSL
 **/
plugins {
    kotlin("android")
    kotlin("kapt")
    kotlin("android.extensions")
    id("com.android.application")
    id("com.google.gms.google-services")
    id("realm-android")
}

android {
    compileSdkVersion(Apps.compileSdk)
    buildToolsVersion = Apps.buildTools
    defaultConfig {
        applicationId = Apps.APP_NAME
        minSdkVersion(Apps.minSdk)
        targetSdkVersion(Apps.targetSdk)
        versionCode = Apps.versionCode
        versionName = Apps.versionName
        multiDexEnabled = true
        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = Apps.ANDROID_JUNIT_RUNNER
    }


    /**
     * @author jhm
     * @since 2022/03/21
     * @commnet build Type 정의
     **/
    buildTypes {
        getByName("debug") {
            isDebuggable = true
            isShrinkResources = false
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
        getByName("release") {
            isDebuggable = false
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    packagingOptions {
        resources {
            excludes += setOf(
                "META-INF/DEPENDENCIES",
                "META-INF/DEPENDENCIES.txt",
                "META-INF/dependencies.txt",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/license.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt",
                "META-INF/notice.txt",
                "META-INF/LGPL2.1",
                "META-INF/ASL2.0",
                "build.xml",
                "META-INF/rxjava.properties"
            )
        }
    }

/*    flavorDimensions += listOf ("alpha","beta","prod")
    flavorDimensions += "version"
    productFlavors {
        create("alpha") {
            initWith(getByName("alpha"))
            dimension = "version"
            applicationIdSuffix = ".alpha"
            buildConfigField("boolean", "ALPHA", "true")
        }
        create("beta") {
            initWith(getByName("beta"))
            dimension = "version"
            applicationIdSuffix = ".beta"
            buildConfigField("boolean", "BETA", "false")
        }
        create("prod") {
            initWith(getByName("prod"))
            dimension = "version"
            applicationIdSuffix = ".prod"
            buildConfigField("boolean", "PROD", "false")
        }
    }*/


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures.viewBinding = true   // jhm_2022/03/21_ gradle plgin v3.6 이상
    buildFeatures.dataBinding = true   // jhm_2022/03/21_ gradle plgin v4.0 이상

    /*buildFeatures.compose = true
    composeOptions.kotlinCompilerExtensionVersion = Versions.COMPOSE_UI*/

}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))



    /** { presentation => data / domain 빌드 gradle 종속성 추가 } - jhm 2022/04/22 **/
    implementation(project(":data"))
    implementation(project(":domain"))


    /** { Test } - jhm 2022/04/22 **/
    testImplementation(Test.JUNIT)
    androidTestImplementation(Test.TEST_RUNNER)
    androidTestImplementation(Test.EXT_JUNIT)
    androidTestImplementation(Test.ESPRESSO_CORE)

    /** { KTX } - jhm 2022/04/22 **/
    implementation(KTX.CORE)
    implementation(KTX.LIFECYCLE_RUNTIME)
    implementation(KTX.COROUTINES_CORE)
    implementation(KTX.COROUTINES)


    /** { RxJava2 } - jhm 2022/04/22 **/
    implementation(RxJava2.ANDROID)
    implementation(RxJava2.JAVA)
    implementation(RxJava2.ROOM)
    implementation(RxJava2.RETROFIT)
    implementation(RxJava2.BINDING)
    implementation (RxJava2.RXRELAY2)


    /** {RxJava3} - jhm 2022/04/22 **/
    implementation(RxJava3.JAVA)
    implementation(RxJava3.KOTLIN)
    implementation(RxJava3.ANDROID)
    implementation(RxJava3.BINDING)
    implementation(RxJava3.RETROFIT_ADAPTER)

    /** { AndroidX } - jhm 2022/04/22 **/
    implementation(AndroidX.APPCOMPAT)
    implementation(AndroidX.MULTIDEX)
    implementation(AndroidX.BROWSER)
    implementation(AndroidX.CONSTRAINT_LAYOUT)
    implementation(AndroidX.CONSTRAINT_LAYOUT_COMPOSE)
    implementation(AndroidX.VIEWPAGER)
    implementation(AndroidX.VIEWPAGER2)
    implementation(AndroidX.LIFECYCLE_EXTENSIONS)
    implementation(AndroidX.LIFECYCLE_VIEWMODEL)
    implementation(AndroidX.LIFECYCLE_LIVEDATA)
    implementation(AndroidX.COMMON)
    implementation(AndroidX.DATABINDING)
    implementation(AndroidX.SWIPEREFRESH)

    /** { Compose } - jhm 2022/04/22 **/
    implementation(Compose.UI)
    implementation(Compose.UI_TEST)
    implementation(Compose.THEME)
    implementation(Compose.ACCOMPANIST)
    implementation(Compose.UI_TOOLING)
    implementation(Compose.FOUNDATION)
    implementation(Compose.ANIMATION)
    implementation(Compose.ALPHA)
    implementation(Compose.VIEWMODEL)
    implementation(Compose.LIVEDATA)
    implementation(Compose.RXJAVA2)
    implementation(Compose.MATERIAL)
    implementation(Compose.MATERIAL_ICONS)
    implementation(Compose.MATERIAL_ICONS_EXTENDED)

    /** { Navigation } - jhm 2022/04/22 **/
    implementation(Navigation.NAVIGATION_FRAGMENT)
    implementation(Navigation.NAVIGATION_UI)
    implementation(Navigation.NAVIGATION_FRAGMENT_KTX)
    implementation(Navigation.NAVIGATION_FRAGMENT_UI_KTX)
    implementation(Navigation.NAVIGATION_DYNAMIC_FEATURES)
    implementation(Navigation.NAVIGATION_TESTING)
    implementation(Navigation.NAVIGATION_COMPOSE)

    /** { Local Database Room , Realm } - jhm 2022/04/22 **/
    implementation(Room.COMMON)
    implementation(Room.RUNTIME)
    annotationProcessor(Room.COMPILER)
    implementation(Room.RXJAVA2)
    implementation(Room.RXJAVA3)
    implementation(Room.GUAVA)
    testImplementation(Room.TESTING)
    implementation(Room.PAGING)
    implementation(Realm.PLUGIN)

    /** { Firebase } - jhm 2022/04/22 **/
    implementation(Firebase.MESSAGING)
    implementation(Firebase.DATABASE)
    implementation(Firebase.CORE)


    /** { Dagger } - jhm 2022/04/29 **/
    implementation(Dagger.DAGGER)
    implementation(Dagger.HILT)
    implementation(Dagger.HILT_VIEW_MODEL)
    implementation(Dagger.SUPPORT)
    implementation(Dagger.PROCESSOR)
    implementation(Dagger.COMPILER)


    /** { Koin } - jhm 2022/04/26 **/
    implementation(Koin.ANDROIDX)
    implementation(Koin.TEST)
    implementation(Koin.ANDROID)
    implementation(Koin.COMPAT)
    implementation(Koin.WORKMANAGER)
    implementation(Koin.NAVIGATION)
    implementation(Koin.COMPOSE)
    implementation(Koin.VIEWMODEL)


    /** { Okhttp3 } - jhm 2022/04/22 **/
    implementation(OkHttp.OKHTTP_3)
    implementation(OkHttp.OKHTTP_3_URLCONNECTION)
    implementation(OkHttp.OKHTTP_3_INTERCEPTOR)

    /** { Retrofit2 } - jhm 2022/04/22 **/
    implementation(Retrofit.RETROFIT_2)
    implementation(Retrofit.RETROFIT_2_ADAPTER)
    implementation(Retrofit.RETROFIT_2_GSON)
    implementation(Retrofit.RETROFIT_2_SCALARS)
    implementation(Retrofit.RETROFIT_2_SIMPLEXML)
    implementation(Retrofit.CONVERTER_JAXB)

    /** { Lottie } - jhm 2022/04/22 **/
    implementation(Lottie.LOTTIE)
    implementation(Lottie.GLIDE)

    /** { Google } - jhm 2022/04/27 **/
    implementation(Google.MATERIAL)
    implementation(Google.GSON)


    /** { Lib } - jhm 2022/05/02 **/
    implementation(Libs.FAST_JSON)



}