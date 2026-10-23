// Пакет объединяет классы нашего приложения. Во всех этих Kotlin-файлах он одинаковый.
package com.example.victorina

// Bundle хранит состояние экрана как набор значений по строковым ключам.
import android.os.Bundle
// Button — кнопка интерфейса.
import android.widget.Button
// TextView — элемент для вывода текста.
import android.widget.TextView
// AppCompatActivity — готовый базовый класс для экранов приложения.
import androidx.appcompat.app.AppCompatActivity

// Экран таблицы рекордов. Он показывает данные, загруженные через объект Records.
class RecordsActivity : AppCompatActivity() {
    // При создании экрана собираем текст таблицы и назначаем кнопку возврата.
    override fun onCreate(savedInstanceState: Bundle?) {
        // Стандартная подготовка Activity.
        super.onCreate(savedInstanceState)
        // Подключаем activity_records.xml и отступы от системных панелей.
        prepareScreen(this, R.layout.activity_records)
        // Загружаем список сохранённых результатов.
        val records = Records.load(this)
        // Пока строка пустая. var позволяет дописывать в неё записи в цикле.
        var text = ""
        // Перебираем позиции всех рекордов по очереди.
        for (i in records.indices) {
            // Получаем один объект Record из списка по позиции i.
            val item = records[i]
            // Дописываем место и очки. i + 1 нужен, чтобы места начинались с 1. \n — переход на новую строку.
            text += "${i + 1}. ${item.score} очков\n" +
                // На следующей строке указываем континент, сложность и число вопросов.
                "${item.mode}\n" +
                // Затем число верных ответов и пустую строку между рекордами.
                "Правильных ответов: ${item.correct}\n\n"
        }
        // Если список был пуст, цикл ничего не добавил. Показываем подсказку вместо пустого экрана.
        if (text.isEmpty()) text = "Рекордов пока нет. Сыграй первую игру."
        // Находим текстовое поле из XML и присваиваем ему собранную строку.
        findViewById<TextView>(R.id.recordsText).text = text
        // По нажатию «Назад» закрываем эту Activity и возвращаемся на предыдущий экран.
        findViewById<Button>(R.id.backButton).setOnClickListener { finish() }
    }
}
