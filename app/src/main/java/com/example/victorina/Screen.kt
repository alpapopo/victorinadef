// Пакет объединяет классы нашего приложения. Во всех этих Kotlin-файлах он одинаковый.
package com.example.victorina

// AppCompatActivity — готовый базовый класс для экранов приложения.
import androidx.appcompat.app.AppCompatActivity
// ViewCompat помогает работать с системными отступами через AndroidX.
import androidx.core.view.ViewCompat
// WindowInsetsCompat содержит сведения о размерах системных панелей.
import androidx.core.view.WindowInsetsCompat

// Общая функция для всех экранов. activity — нужный экран, layout — id его XML-разметки.
fun prepareScreen(activity: AppCompatActivity, layout: Int) {
    // Создаём элементы интерфейса из переданного XML-файла.
    activity.setContentView(layout)
    // Находим внешний контейнер с id screen. Он есть в каждой из четырёх разметок.
    val screen = activity.findViewById<android.view.View>(R.id.screen)
    // Назначаем обработчик размеров системных панелей. Android передаст view — контейнер, insets — отступы.
    ViewCompat.setOnApplyWindowInsetsListener(screen) { view, insets ->
        // Получаем размеры верхней строки состояния и нижней панели навигации телефона.
        val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        // Добавляем эти отступы слева, сверху, справа и снизу, чтобы интерфейс не перекрывался панелями.
        view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
        // Последнее выражение обработчика возвращает insets Android: отступы остаются доступны вложенным элементам.
        insets
    }
    // Просим Android передать актуальные размеры системных панелей этому контейнеру.
    ViewCompat.requestApplyInsets(screen)
}
