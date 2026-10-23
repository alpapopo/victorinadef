// Подключаем инструменты Gradle. Сам Gradle собирает проект и превращает исходники в APK.
plugins {
    // Плагин для сборки Android-приложения. Его версия находится в gradle/libs.versions.toml.
    alias(libs.plugins.android.application)
}

// Настройки Android-модуля приложения.
android {
    // Пакет для сгенерированного класса R и других файлов сборки.
    namespace = "com.example.victorina"
    // Версия Android SDK, с которой компилируется проект.
    compileSdk {
        // Используем установленный Android SDK 37.
        version = release(37)
    }

    // Основные параметры готового приложения.
    defaultConfig {
        // Уникальный идентификатор приложения на устройстве.
        applicationId = "com.example.victorina"
        // Минимальная поддерживаемая версия API: 24 соответствует Android 7.0.
        minSdk = 24
        // Версия API, на поведение которой рассчитано приложение.
        targetSdk = 37
        // Целочисленный номер сборки. При выпуске обновлений его увеличивают.
        versionCode = 1
        // Понятная пользователю строка версии.
        versionName = "1.0"
    }

    // Разные варианты сборки: отладочный debug и итоговый release.
    buildTypes {
        // Настройки итоговой сборки. При обычном Run запускается debug.
        release {
            // Стандартные настройки оптимизации, оставшиеся от шаблона Android Studio.
            optimization {
                // Включаем оптимизацию для release.
                enable = true
                // Указываем области пакетов для оптимизации.
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    // Совместимость исходников и скомпилированного кода Java.
    compileOptions {
        // Синтаксис Java — уровня Java 11.
        sourceCompatibility = JavaVersion.VERSION_11
        // Версия генерируемого байткода — Java 11.
        targetCompatibility = JavaVersion.VERSION_11
    }
}

// Библиотеки, используемые приложением. implementation подключает библиотеку к основному коду.
dependencies {
    // Расширения Kotlin для работы с Activity.
    implementation(libs.androidx.activity.ktx)
    // Базовый класс AppCompatActivity и поддержка тем.
    implementation(libs.androidx.appcompat)
    // Библиотека расположения элементов из шаблона. Текущие экраны используют LinearLayout.
    implementation(libs.androidx.constraintlayout)
    // Базовые инструменты AndroidX, в том числе работа с системными отступами.
    implementation(libs.androidx.core.ktx)
    // Готовые Material-тема и элементы интерфейса.
    implementation(libs.material)
}
