// Пакет объединяет классы нашего приложения. Во всех этих Kotlin-файлах он одинаковый.
package com.example.victorina

// Intent передаёт данные между экранами и указывает Android, какую Activity открыть.
import android.content.Intent
// Bundle хранит состояние экрана как набор значений по строковым ключам.
import android.os.Bundle
// Готовый таймер Android: вызывает onTick во время отсчёта и onFinish по окончании.
import android.os.CountDownTimer
// SystemClock нужен для отсчёта времени, который не зависит от перевода часов.
import android.os.SystemClock
// Button — кнопка интерфейса.
import android.widget.Button
// ImageView — элемент для показа изображения флага.
import android.widget.ImageView
// ProgressBar — полоса прогресса.
import android.widget.ProgressBar
// TextView — элемент для вывода текста.
import android.widget.TextView
// AppCompatActivity — готовый базовый класс для экранов приложения.
import androidx.appcompat.app.AppCompatActivity
// ceil округляет дробное число вверх, например 12,4 до 13.
import kotlin.math.ceil

// Игровой экран: хранит состояние игры, показывает флаги, проверяет ответы и считает очки.
class GameActivity : AppCompatActivity() {
    // Ссылка на изображение флага. lateinit — значение присвоим позже, в onCreate. var позволяет менять значение.
    private lateinit var flagImage: ImageView
    // Текст номера вопроса, например «Вопрос 2 из 5».
    private lateinit var progressText: TextView
    // Текст с общим количеством очков.
    private lateinit var scoreText: TextView
    // Текст с оставшимися секундами.
    private lateinit var timerText: TextView
    // Сообщение после ответа: верно, неверно или время вышло.
    private lateinit var feedbackText: TextView
    // Полоса прогресса по вопросам.
    private lateinit var progressBar: ProgressBar
    // Кнопка перехода к следующему вопросу или результату.
    private lateinit var nextButton: Button
    // Четыре кнопки, на которых показаны названия стран.
    private lateinit var answerButtons: List<Button>

    // Порядок вопросов: список индексов стран в pool. Например, индекс 2 означает третью страну набора.
    private var questions = arrayListOf<Int>()
    // Четыре индекса стран для кнопок текущего вопроса. Один индекс правильный, остальные три неправильные.
    private var options = arrayListOf<Int>()
    // Набор стран, соответствующий выбранным континенту и сложности.
    private var pool = listOf<Country>()
    // Позиция текущего вопроса в questions. Начинаем с 0; игроку показываем номер на единицу больше.
    private var questionNumber = 0
    // Сумма заработанных очков. В начале игры равна нулю.
    private var score = 0
    // Число правильных ответов, отдельно от количества очков.
    private var correctAnswers = 0
    // true — ответ уже принят или время вышло. false — игрок ещё может отвечать.
    private var answered = false
    // Сохраняем текст сообщения, чтобы восстановить его после поворота экрана.
    private var feedback = ""
    // Срок окончания вопроса в миллисекундах с момента запуска устройства. 0L — число типа Long.
    private var deadline = 0L
    // Количество секунд для вывода. После ответа значение остаётся зафиксированным.
    private var secondsLeft = 20
    // Объект таймера. Знак ? разрешает хранить null — пока таймер ещё не создан.
    private var timer: CountDownTimer? = null

    // Вызывается при создании игрового экрана, в том числе заново после поворота телефона.
    override fun onCreate(savedInstanceState: Bundle?) {
        // Запускаем стандартную подготовку Activity.
        super.onCreate(savedInstanceState)
        // Подключаем XML игрового экрана и отступы от системных панелей.
        prepareScreen(this, R.layout.activity_game)
        // Находим ImageView в XML. Ниже так же находим тексты, полосу прогресса и кнопки по их id.
        flagImage = findViewById(R.id.flagImage)
        progressText = findViewById(R.id.progressText)
        scoreText = findViewById(R.id.scoreText)
        timerText = findViewById(R.id.timerText)
        feedbackText = findViewById(R.id.feedbackText)
        progressBar = findViewById(R.id.questionProgress)
        nextButton = findViewById(R.id.nextButton)
        // Собираем кнопки в один список, чтобы назначать им текст и действия через цикл.
        answerButtons = listOf(findViewById(R.id.answer1), findViewById(R.id.answer2),
            findViewById(R.id.answer3), findViewById(R.id.answer4))

        // Получаем настройки из Intent и выбираем страны. ?: означает запасное значение, если слева null.
        pool = Countries.getPool(intent.getStringExtra("continent") ?: "Все страны",
            intent.getIntExtra("difficulty", 0))

        // Если Android передал Bundle с сохранённым состоянием, продолжаем прежнюю игру вместо создания новой.
        if (savedInstanceState != null) {
            // Восстанавливаем порядок вопросов. !! означает, что ожидаем непустое значение: этот список сами записываем при сохранении.
            questions = savedInstanceState.getIntegerArrayList("questions")!!
            // Возвращаем прежний порядок четырёх вариантов, чтобы после поворота они не перемешались.
            options = savedInstanceState.getIntegerArrayList("options")!!
            // Возвращаем позицию текущего вопроса.
            questionNumber = savedInstanceState.getInt("questionNumber")
            // Возвращаем накопленные очки.
            score = savedInstanceState.getInt("score")
            // Возвращаем количество правильных ответов.
            correctAnswers = savedInstanceState.getInt("correctAnswers")
            // Восстанавливаем, был ли уже дан ответ, чтобы не позволить ответить второй раз.
            answered = savedInstanceState.getBoolean("answered")
            // Восстанавливаем сообщение о результате ответа.
            feedback = savedInstanceState.getString("feedback", "")
            // Восстанавливаем прежний срок окончания. Благодаря этому поворот не даёт дополнительные 20 секунд.
            deadline = savedInstanceState.getLong("deadline")
            // Восстанавливаем число секунд, которое нужно показать после уже данного ответа.
            secondsLeft = savedInstanceState.getInt("secondsLeft")
        } else {
            // Новая игра: читаем количество вопросов. Если параметр отсутствует, используем 5.
            val count = intent.getIntExtra("count", 5)
            // Повторяем добавление стран, пока не наберётся нужное количество вопросов.
            while (questions.size < count) {
                // indices — все индексы набора; shuffled перемешивает их; toMutableList создаёт изменяемый список для одного круга.
                val round = pool.indices.shuffled().toMutableList()
                // && означает «и». Если новый круг начинается той же страной, которой закончился предыдущий, исправляем это.
                if (questions.isNotEmpty() && round[0] == questions.last()) {
                    // Убираем первый индекс из нового круга и запоминаем его.
                    val first = round.removeAt(0)
                    // Ставим этот индекс в конец круга: соседние вопросы теперь не совпадут.
                    round.add(first)
                }
                // Перебираем перемешанные индексы стран по одному.
                for (index in round) {
                    // Добавляем индекс, только если ещё нужны вопросы. size — текущее количество элементов списка.
                    if (questions.size < count) questions.add(index)
                }
            }
            // Подготавливаем варианты ответа и новый срок окончания вопроса.
            prepareQuestion()
        }

        // Проходим по позициям четырёх кнопок: 0, 1, 2, 3.
        for (i in answerButtons.indices) {
            // При нажатии передаём в checkAnswer индекс страны, написанной на этой кнопке.
            answerButtons[i].setOnClickListener { checkAnswer(options[i]) }
        }
        // Действие кнопки перехода. До ответа на вопрос эта кнопка отключена.
        nextButton.setOnClickListener {
            // lastIndex — индекс последнего элемента. На последнем вопросе следующим экраном будет результат.
            if (questionNumber == questions.lastIndex) {
                // Открываем итоги игры.
                showResult()
            } else {
                // Переходим к следующей позиции. ++ увеличивает число на единицу.
                questionNumber++
                // Подготавливаем варианты ответа и новый срок окончания вопроса.
                prepareQuestion()
                // Обновляем изображение, подписи и доступность кнопок по текущему состоянию.
                showQuestion()
                // Запускаем отсчёт до срока окончания текущего вопроса.
                startTimer()
            }
        }
        // Закрываем игру через finish(). Под ней остаётся меню; незавершённый результат не записывается.
        findViewById<Button>(R.id.menuButton).setOnClickListener { finish() }
        // Обновляем изображение, подписи и доступность кнопок по текущему состоянию.
        showQuestion()
    }

    // Готовим данные одного нового вопроса. private — эта функция используется только внутри GameActivity.
    private fun prepareQuestion() {
        // Разрешаем отвечать на новый вопрос.
        answered = false
        // Убираем сообщение от предыдущего вопроса.
        feedback = ""
        // Находим индекс правильной страны в pool. Саму страну можно получить через pool[correct].
        val correct = questions[questionNumber]
        // Из всех индексов исключаем правильный через filter, перемешиваем и берём три. ArrayList нужен также для сохранения в Bundle.
        options = ArrayList(pool.indices.filter { it != correct }.shuffled().take(3))
        // Добавляем индекс правильного ответа: теперь вариантов четыре.
        options.add(correct)
        // Перемешиваем варианты, чтобы правильный ответ не стоял всегда на одном месте.
        options.shuffle()
        // elapsedRealtime — время с запуска устройства. Прибавляем 20 000 мс, то есть 20 секунд.
        deadline = SystemClock.elapsedRealtime() + 20000L
        // Для нового вопроса начинаем показ времени с 20 секунд.
        secondsLeft = 20
    }

    // Выводит данные на экран. Эта функция сама не выбирает новый вопрос и не начисляет очки.
    private fun showQuestion() {
        // Сначала берём индекс из questions, затем по этому индексу получаем объект Country из pool.
        val country = pool[questions[questionNumber]]
        // Загружаем в ImageView ресурс флага выбранной страны.
        flagImage.setImageResource(country.flag)
        // Описание картинки для программы озвучивания экрана. Название правильной страны здесь не раскрываем.
        flagImage.contentDescription = "Флаг страны, название которой нужно угадать"
        // Подставляем значения в строку: ${...} вычисляет выражение. Для игрока нумерация начинается с 1.
        progressText.text = "Вопрос ${questionNumber + 1} из ${questions.size}"
        // Показываем накопленные очки. $score подставляет значение переменной в строку.
        scoreText.text = "Очки: $score"
        // Максимум полосы прогресса — общее число вопросов.
        progressBar.max = questions.size
        // Текущее положение полосы — номер отображаемого вопроса.
        progressBar.progress = questionNumber + 1
        // Выводим сохранённое сообщение; до ответа это пустая строка.
        feedbackText.text = feedback
        // Проходим по позициям четырёх кнопок: 0, 1, 2, 3.
        for (i in answerButtons.indices) {
            // options[i] — индекс страны для кнопки; .name — её название.
            answerButtons[i].text = pool[options[i]].name
            // ! означает отрицание: кнопка доступна, пока answered равно false.
            answerButtons[i].isEnabled = !answered
        }
        // Переход разрешён только после ответа или окончания времени.
        nextButton.isEnabled = answered
        // На последнем вопросе пишем «Результат», на остальных — «Следующий вопрос».
        nextButton.text = if (questionNumber == questions.lastIndex) "Результат" else "Следующий вопрос"
        // Обновляем подпись с оставшимися секундами.
        updateTimerText()
    }

    // Переводит остаток миллисекунд в секунды и показывает его.
    private fun updateTimerText() {
        // После ответа время на экране не пересчитываем: оставляем зафиксированное значение.
        if (!answered) {
            // Вычитаем текущее время; coerceAtLeast(0) убирает отрицательные значения. Делим на 1000, ceil округляет вверх, toInt даёт целое число.
            secondsLeft = ceil((deadline - SystemClock.elapsedRealtime()).coerceAtLeast(0) / 1000.0).toInt()
        }
        // Выводим рассчитанный остаток времени.
        timerText.text = "Время: $secondsLeft с"
    }

    // Запускает таймер на фактически оставшееся время, а не каждый раз заново на 20 секунд.
    private fun startTimer() {
        // ?. вызывает cancel только если таймер существует. Останавливаем старый таймер перед новым запуском или после ответа.
        timer?.cancel()
        // Если вопрос уже закрыт, сразу выходим из функции. Это защищает и от повторного ответа.
        if (answered) return
        // Вычисляем, сколько миллисекунд осталось до срока окончания вопроса.
        val remaining = deadline - SystemClock.elapsedRealtime()
        // Если срок уже прошёл, ответить больше нельзя, например после долгого сворачивания приложения.
        if (remaining <= 0) {
            // Индекс -1 не соответствует ни одной стране. Здесь он обозначает пропуск из-за истечения времени.
            checkAnswer(-1)
            // Выходим из текущей функции: код ниже в этом вызове уже не выполняется.
            return
        }
        // Создаём CountDownTimer: первый аргумент — оставшееся время, второй — интервал обновления 100 мс.
        timer = object : CountDownTimer(remaining, 100) {
            // Android вызывает onTick примерно каждые 100 мс. Каждый раз обновляем показ секунд.
            override fun onTick(millisUntilFinished: Long) { updateTimerText() }
            // Когда отсчёт закончен, обрабатываем пропуск вопроса.
            override fun onFinish() { checkAnswer(-1) }
        // Сразу запускаем созданный таймер.
        }.start()
    }

    // Принимает индекс выбранной страны и проверяет его. При пропуске selected равен -1.
    private fun checkAnswer(selected: Int) {
        // Если вопрос уже закрыт, сразу выходим из функции. Это защищает и от повторного ответа.
        if (answered) return
        // Вычисляем, сколько миллисекунд осталось до срока окончания вопроса.
        val remaining = deadline - SystemClock.elapsedRealtime()
        // Запоминаем время на момент ответа, округляя оставшиеся секунды вверх и убирая отрицательные значения.
        secondsLeft = ceil(remaining.coerceAtLeast(0) / 1000.0).toInt()
        // Закрываем вопрос. Теперь повторные нажатия не смогут добавить очки.
        answered = true
        // ?. вызывает cancel только если таймер существует. Останавливаем старый таймер перед новым запуском или после ответа.
        timer?.cancel()
        // Находим индекс правильной страны в pool. Саму страну можно получить через pool[correct].
        val correct = questions[questionNumber]
        // Ответ считается правильным, только если совпал индекс страны И ещё осталось время.
        if (selected == correct && remaining > 0) {
            // Начисляем по 10 очков за оставшуюся секунду с округлением вверх: например, 12,4 с превращаются в 130 очков.
            val points = ceil(remaining / 1000.0).toInt() * 10
            // Прибавляем очки за этот вопрос к общей сумме. То же самое, что score = score + points.
            score += points
            // Увеличиваем число правильных ответов на один.
            correctAnswers++
            // Готовим сообщение о верном ответе и заработанных очках.
            feedback = "Верно! +$points очков"
        // Отдельно обрабатываем пропуск или попытку ответить уже после срока.
        } else if (selected == -1 || remaining <= 0) {
            // Сообщаем об окончании времени и показываем правильную страну.
            feedback = "Время вышло. Правильный ответ: ${pool[correct].name}"
        } else {
            // Если игрок выбрал другую страну, показываем правильный ответ без начисления очков.
            feedback = "Неверно. Правильный ответ: ${pool[correct].name}"
        }
        // Обновляем изображение, подписи и доступность кнопок по текущему состоянию.
        showQuestion()
    }

    // Передаёт итоги в ResultActivity и закрывает игровой экран.
    private fun showResult() {
        // Создаём Intent для открытия экрана результатов. ::class.java даёт Android нужный класс экрана.
        val result = Intent(this, ResultActivity::class.java)
        // Передаём итоговые очки.
        result.putExtra("score", score)
        // Передаём количество верных ответов.
        result.putExtra("correct", correctAnswers)
        // Передаём общее число вопросов для расчёта процента.
        result.putExtra("count", questions.size)
        // Передаём континент, чтобы подписать режим в таблице рекордов.
        result.putExtra("continent", intent.getStringExtra("continent"))
        // Передаём номер сложности для той же подписи.
        result.putExtra("difficulty", intent.getIntExtra("difficulty", 0))
        // Открываем экран итогов с переданными параметрами.
        startActivity(result)
        // Закрываем игровую Activity, чтобы кнопка «Назад» не возвращала в завершённую игру.
        finish()
    }

    // Android вызывает onStart, когда экран становится видимым, в том числе после возврата из фона.
    override fun onStart() {
        // Сохраняем стандартное поведение Android при появлении экрана.
        super.onStart()
        // Запускаем отсчёт до срока окончания текущего вопроса.
        startTimer()
    }

    // Android вызывает onStop, когда экран становится невидимым.
    override fun onStop() {
        // ?. вызывает cancel только если таймер существует. Останавливаем старый таймер перед новым запуском или после ответа.
        timer?.cancel()
        // Выполняем стандартное завершение этого этапа жизненного цикла Activity.
        super.onStop()
    }

    // Перед пересозданием экрана Android даёт Bundle — набор значений по ключам. В него записываем состояние.
    override fun onSaveInstanceState(outState: Bundle) {
        // Даём Android сохранить стандартное состояние элементов интерфейса.
        super.onSaveInstanceState(outState)
        // Сохраняем порядок всех вопросов.
        outState.putIntegerArrayList("questions", questions)
        // Сохраняем порядок четырёх вариантов текущего вопроса.
        outState.putIntegerArrayList("options", options)
        // Сохраняем позицию текущего вопроса.
        outState.putInt("questionNumber", questionNumber)
        // Сохраняем накопленные очки.
        outState.putInt("score", score)
        // Сохраняем число верных ответов.
        outState.putInt("correctAnswers", correctAnswers)
        // Сохраняем признак уже принятого ответа.
        outState.putBoolean("answered", answered)
        // Сохраняем сообщение после ответа.
        outState.putString("feedback", feedback)
        // Сохраняем срок окончания вопроса. Это позволяет продолжить отсчёт после поворота.
        outState.putLong("deadline", deadline)
        // Сохраняем зафиксированное число секунд, нужное после уже данного ответа.
        outState.putInt("secondsLeft", secondsLeft)
    }
}
