// Настраиваем, откуда Gradle получает плагины сборки.
pluginManagement {
    // Репозитории — серверы с библиотеками или плагинами.
    repositories {
        // Репозиторий Google для Android-инструментов.
        google {
            // Ограничиваем список пакетов, которые ищутся в этом репозитории.
            content {
                // Разрешаем поиск группы пакетов по шаблону имени.
                includeGroupByRegex("com\\.android.*")
                // Разрешаем поиск группы пакетов по шаблону имени.
                includeGroupByRegex("com\\.google.*")
                // Разрешаем поиск группы пакетов по шаблону имени.
                includeGroupByRegex("androidx.*")
            }
        }
        // Общий репозиторий Java/Kotlin-библиотек Maven Central.
        mavenCentral()
        // Репозиторий плагинов Gradle.
        gradlePluginPortal()
    }
}
// Подключаем инструменты Gradle. Сам Gradle собирает проект и превращает исходники в APK.
plugins {
    // Плагин шаблона для поиска и получения подходящего JDK — среды сборки Java.
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
// Общие правила поиска библиотек для модулей проекта.
dependencyResolutionManagement {
    // Используем репозитории из этого файла, а не отдельные репозитории в модулях.
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    // Репозитории — серверы с библиотеками или плагинами.
    repositories {
        // Репозиторий библиотек Google и AndroidX.
        google()
        // Общий репозиторий Java/Kotlin-библиотек Maven Central.
        mavenCentral()
    }
}

// Название проекта в Android Studio.
rootProject.name = "victorina"
// Подключаем модуль app: именно в нём находится игра.
include(":app")
