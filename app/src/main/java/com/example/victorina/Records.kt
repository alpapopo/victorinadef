// Пакет объединяет классы нашего приложения. Во всех этих Kotlin-файлах он одинаковый.
package com.example.victorina

// Context даёт доступ к ресурсам и хранилищам приложения.
import android.content.Context

// Один рекорд: score — очки, mode — подпись режима, correct — число верных ответов. data class удобно хранит эти три поля вместе.
data class Record(val score: Int, val mode: String, val correct: Int)

// Общий объект для чтения и записи рекордов. Экран игры не занимается форматом их хранения.
object Records {
    // Загружаем рекорды с телефона. Context даёт доступ к хранилищу; Activity тоже является Context.
    fun load(context: Context): MutableList<Record> {
        // Открываем отдельное хранилище records. MODE_PRIVATE ограничивает доступ рамками приложения.
        val preferences = context.getSharedPreferences("records", Context.MODE_PRIVATE)
        // Читаем количество записей. Если игра ещё ничего не сохраняла, получаем 0.
        val count = preferences.getInt("count", 0)
        // Создаём пустой изменяемый список объектов Record.
        val results = mutableListOf<Record>()
        // Цикл от 0 до count, не включая count. При трёх записях индексы будут 0, 1, 2.
        for (i in 0 until count) {
            // Читаем очки i-й записи. При i = 0 ключ "score$i" превращается в "score0".
            val score = preferences.getInt("score$i", 0)
            // Читаем подпись режима. ?: "" подставляет пустую строку, если getString вернул null.
            val mode = preferences.getString("mode$i", "") ?: ""
            // Читаем количество правильных ответов этой записи.
            val correct = preferences.getInt("correct$i", 0)
            // Собираем прочитанные значения в один Record и добавляем его в список.
            results.add(Record(score, mode, correct))
        }
        // Возвращаем весь загруженный список вызывающему экрану или функции сохранения.
        return results
    }

    // Добавляет результат и сохраняет десять лучших. Аргументы содержат очки, подпись и число верных ответов.
    fun save(context: Context, score: Int, mode: String, correct: Int) {
        // Сначала загружаем прежние результаты, чтобы они не потерялись при добавлении нового.
        val results = load(context)
        // Собираем прочитанные значения в один Record и добавляем его в список.
        results.add(Record(score, mode, correct))
        // Сортируем список по убыванию очков. it — текущий рекорд; самые большие значения будут первыми.
        results.sortByDescending { it.score }

        // Берём не больше десяти первых записей после сортировки.
        val best = results.take(10)
        // Получаем редактор хранилища, через который можно записывать и удалять значения.
        val editor = context.getSharedPreferences("records", Context.MODE_PRIVATE).edit()
        // Убираем старые ключи только из хранилища records. Ниже целиком запишем обновлённую десятку.
        editor.clear()
        // Записываем, сколько рекордов теперь хранится.
        editor.putInt("count", best.size)
        // Проходим по индексам лучших результатов.
        for (i in best.indices) {
            // Записываем очки под отдельным ключом: score0, score1 и так далее.
            editor.putInt("score$i", best[i].score)
            // Рядом записываем подпись режима: mode0, mode1 и так далее.
            editor.putString("mode$i", best[i].mode)
            // Записываем число правильных ответов: correct0, correct1 и так далее.
            editor.putInt("correct$i", best[i].correct)
        }
        // Применяем все изменения сразу; SharedPreferences записывает их на диск в фоне.
        editor.apply()
    }
}
