// Подключаем инструменты Gradle. Сам Gradle собирает проект и превращает исходники в APK.
plugins {
    // Плагин для сборки Android-приложения. Его версия находится в gradle/libs.versions.toml.
    alias(libs.plugins.android.application) apply false
}
