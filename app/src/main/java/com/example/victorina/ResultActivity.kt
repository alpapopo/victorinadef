// Пакет объединяет классы нашего приложения. Во всех этих Kotlin-файлах он одинаковый.
package com.example.victorina

// Intent передаёт данные между экранами и указывает Android, какую Activity открыть.
import android.content.Intent
// Bundle хранит состояние экрана как набор значений по строковым ключам.
import android.os.Bundle
// Button — кнопка интерфейса.
import android.widget.Button
// TextView — элемент для вывода текста.
import android.widget.TextView
// AppCompatActivity — готовый базовый класс для экранов приложения.
import androidx.appcompat.app.AppCompatActivity

// Экран итогов: показывает результат игры и передаёт его на сохранение.
class ResultActivity : AppCompatActivity() {
    // Android вызывает эту функцию при первом открытии и при пересоздании экрана.
    override fun onCreate(savedInstanceState: Bundle?) {
        // Стандартная подготовка Activity.
        super.onCreate(savedInstanceState)
        // Подключаем activity_result.xml и отступы от системных панелей.
        prepareScreen(this, R.layout.activity_result)
        // Читаем очки из Intent. Второй аргумент 0 — запасное значение, если параметр не передали.
        val score = intent.getIntExtra("score", 0)
        // Читаем количество правильных ответов.
        val correct = intent.getIntExtra("correct", 0)
        // Читаем число вопросов. Оно понадобится для отображения результата и процента.
        val count = intent.getIntExtra("count", 5)
        // Читаем континент. ?: подставляет «Все страны», если получено null.
        val continent = intent.getStringExtra("continent") ?: "Все страны"
        // Читаем индекс сложности: 0, 1 или 2.
        val difficulty = intent.getIntExtra("difficulty", 0)
        // Превращаем индекс в понятное название сложности, обращаясь к списку по индексу.
        val level = listOf("Легко", "Средне", "Сложно")[difficulty]
        // Выводим итог в текстовое поле. \n переносит текст на следующую строку.
        findViewById<TextView>(R.id.resultText).text =
            // Подставляем количество верных ответов и общее число вопросов в строку.
            "Правильных ответов: $correct из $count\n" +
            // Процент считаем как correct * 100 / count. Деление целых чисел отбрасывает дробную часть.
            "Очки: $score\nПравильных ответов: ${correct * 100 / count}%"

        // null означает первое создание без сохранённого состояния. При повороте Bundle уже есть, поэтому второй записи не будет.
        if (savedInstanceState == null) {
            // Передаём очки, подпись режима и число верных ответов в хранилище рекордов.
            Records.save(this, score, "$continent, $level, $count вопросов", correct)
        }
        // Назначаем действие кнопке просмотра рекордов.
        findViewById<Button>(R.id.resultRecordsButton).setOnClickListener {
            // Открываем RecordsActivity поверх экрана результатов.
            startActivity(Intent(this, RecordsActivity::class.java))
        }
        // Закрываем результаты. Игровой экран уже закрыт, поэтому возвращаемся в меню.
        findViewById<Button>(R.id.resultMenuButton).setOnClickListener { finish() }
    }
}
