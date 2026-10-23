// Пакет объединяет классы нашего приложения. Во всех этих Kotlin-файлах он одинаковый.
package com.example.victorina

// Intent передаёт данные между экранами и указывает Android, какую Activity открыть.
import android.content.Intent
// Bundle хранит состояние экрана как набор значений по строковым ключам.
import android.os.Bundle
// ArrayAdapter связывает список значений с элементами интерфейса.
import android.widget.ArrayAdapter
// Button — кнопка интерфейса.
import android.widget.Button
// Spinner — выпадающий список.
import android.widget.Spinner
// AppCompatActivity — готовый базовый класс для экранов приложения.
import androidx.appcompat.app.AppCompatActivity

// Главное меню. Activity — отдельный экран приложения. Двоеточие означает наследование от готового класса Android.
class MainActivity : AppCompatActivity() {
    // Android вызывает onCreate при создании экрана. override означает, что мы задаём своё поведение готовому методу.
    override fun onCreate(savedInstanceState: Bundle?) {
        // Сначала выполняем стандартную подготовку экрана, предусмотренную Android.
        super.onCreate(savedInstanceState)
        // Подключаем activity_main.xml и выставляем отступы от системных панелей. this — текущая Activity.
        prepareScreen(this, R.layout.activity_main)

        // Находим выпадающий список континентов по его id из XML. val — переменная, которой нельзя присвоить другое значение.
        val continent = findViewById<Spinner>(R.id.continentSpinner)
        // Список выбора количества вопросов. Spinner — стандартный выпадающий список Android.
        val count = findViewById<Spinner>(R.id.countSpinner)
        // Список выбора сложности. findViewById связывает код с элементом XML-разметки.
        val difficulty = findViewById<Spinner>(R.id.difficultySpinner)
        // Заполняем список континентов. listOf создаёт список значений.
        fillSpinner(continent, listOf("Все страны", "Европа", "Азия", "Америка"))
        // Даём выбрать 5, 10 или 15 вопросов. Пока это строки для отображения в списке.
        fillSpinner(count, listOf("5", "10", "15"))
        // Позиции этих пунктов: 0 — легко, 1 — средне, 2 — сложно. Индексы списков начинаются с нуля.
        fillSpinner(difficulty, listOf("Легко", "Средне", "Сложно"))

        // Открываем хранилище settings. SharedPreferences сохраняет простые значения на телефоне. MODE_PRIVATE — доступ внутри приложения.
        val settings = getSharedPreferences("settings", MODE_PRIVATE)
        // Возвращаем ранее выбранный пункт. Если ключа continent ещё нет, getInt вернёт запасное значение 0.
        continent.setSelection(settings.getInt("continent", 0))
        // Восстанавливаем позицию количества вопросов из настроек.
        count.setSelection(settings.getInt("count", 0))
        // Восстанавливаем выбранную сложность.
        difficulty.setSelection(settings.getInt("difficulty", 0))

        // Всё внутри фигурных скобок выполнится, когда игрок нажмёт кнопку начала игры.
        findViewById<Button>(R.id.startButton).setOnClickListener {
            // Открываем редактор настроек. Последующие вызовы добавляют значения в одну запись.
            settings.edit()
                // Сохраняем номер выбранного континента. selectedItemPosition — именно позиция, а не название.
                .putInt("continent", continent.selectedItemPosition)
                // Сохраняем позицию пункта 5, 10 или 15.
                .putInt("count", count.selectedItemPosition)
                // Сохраняем позицию сложности.
                .putInt("difficulty", difficulty.selectedItemPosition)
                // Применяем настройки: значения сразу доступны в памяти, запись на диск выполняется в фоне.
                .apply()
            // Intent — сообщение Android с указанием, какой экран нужно открыть.
            val intent = Intent(this, GameActivity::class.java)
            // Прикладываем к сообщению название континента. Ключ continent понадобится на игровом экране.
            intent.putExtra("continent", continent.selectedItem.toString())
            // Берём выбранную строку, например "10", и превращаем её в целое число через toInt().
            intent.putExtra("count", count.selectedItem.toString().toInt())
            // Передаём номер сложности от 0 до 2.
            intent.putExtra("difficulty", difficulty.selectedItemPosition)
            // Открываем экран игры и передаём ему подготовленные настройки.
            startActivity(intent)
        }
        // Отдельный обработчик нажатия кнопки таблицы рекордов.
        findViewById<Button>(R.id.recordsButton).setOnClickListener {
            // Открываем экран рекордов. Дополнительных данных ему передавать не нужно.
            startActivity(Intent(this, RecordsActivity::class.java))
        }
    }

    // Вспомогательная функция заполнения списка. private — доступна только внутри этого класса.
    private fun fillSpinner(spinner: Spinner, items: List<String>) {
        // Адаптер связывает список строк с выпадающим списком. android.R — готовые ресурсы Android.
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        // Задаём стандартный вид пунктов в раскрытом списке.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Подключаем адаптер к элементу Spinner, чтобы тот показал строки.
        spinner.adapter = adapter
    }
}
