// Пакет объединяет классы нашего приложения. Во всех этих Kotlin-файлах он одинаковый.
package com.example.victorina

// data class — класс для хранения данных. name — название, continent — континент, flag — числовой id картинки в ресурсах.
data class Country(val name: String, val continent: String, val flag: Int)

// object создаёт один общий объект. Через Countries можно получить страны из любого экрана.
object Countries {
    // Общий список из 24 стран. R.drawable.flag_... ссылается на соответствующий XML-флаг в res/drawable.
    val all = listOf(
        // Европа: первые четыре страны используются на лёгком уровне, первые шесть — на среднем, все восемь — на сложном.
        Country("Франция", "Европа", R.drawable.flag_france),
        Country("Германия", "Европа", R.drawable.flag_germany),
        Country("Италия", "Европа", R.drawable.flag_italy),
        Country("Украина", "Европа", R.drawable.flag_ukraine),
        Country("Бельгия", "Европа", R.drawable.flag_belgium),
        Country("Нидерланды", "Европа", R.drawable.flag_netherlands),
        Country("Польша", "Европа", R.drawable.flag_poland),
        Country("Австрия", "Европа", R.drawable.flag_austria),
        // Азия: тот же принцип выбора первых 4, 6 или 8 стран.
        Country("Япония", "Азия", R.drawable.flag_japan),
        Country("Китай", "Азия", R.drawable.flag_china),
        Country("Вьетнам", "Азия", R.drawable.flag_vietnam),
        Country("Бангладеш", "Азия", R.drawable.flag_bangladesh),
        Country("Индонезия", "Азия", R.drawable.flag_indonesia),
        Country("Таиланд", "Азия", R.drawable.flag_thailand),
        Country("Армения", "Азия", R.drawable.flag_armenia),
        Country("Лаос", "Азия", R.drawable.flag_laos),
        // Америка: ещё восемь стран, расположенных в порядке добавления в набор сложности.
        Country("Аргентина", "Америка", R.drawable.flag_argentina),
        Country("Чили", "Америка", R.drawable.flag_chile),
        Country("Колумбия", "Америка", R.drawable.flag_colombia),
        Country("Перу", "Америка", R.drawable.flag_peru),
        Country("Куба", "Америка", R.drawable.flag_cuba),
        Country("Ямайка", "Америка", R.drawable.flag_jamaica),
        Country("Панама", "Америка", R.drawable.flag_panama),
        Country("Багамы", "Америка", R.drawable.flag_bahamas)
    )

    // Составляем набор стран для игры. Аргументы — выбранные континент и сложность; после двоеточия указан тип результата.
    fun getPool(continent: String, difficulty: Int): List<Country> {
        // difficulty используется как индекс: listOf(4, 6, 8)[0] даёт 4, [1] даёт 6, [2] даёт 8.
        val size = listOf(4, 6, 8)[difficulty]
        // Создаём пустой изменяемый список стран. В mutableListOf можно добавлять элементы.
        val result = mutableListOf<Country>()
        // Цикл по трём континентам. На каждом проходе region содержит следующее название.
        for (region in listOf("Европа", "Азия", "Америка")) {
            // || означает «или»: добавляем страны, если выбраны все континенты либо этот конкретный континент.
            if (continent == "Все страны" || continent == region) {
                // filter оставляет страны нужного континента, take(size) берёт первые size стран, addAll добавляет их в результат. it — текущая страна фильтра.
                result.addAll(all.filter { it.continent == region }.take(size))
            }
        }
        // Возвращаем готовый набор. В нём 4/6/8 стран для одного континента или 12/18/24 для всех.
        return result
    }
}
