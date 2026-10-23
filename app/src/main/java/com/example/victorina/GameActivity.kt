package com.example.victorina

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.ceil

class GameActivity : AppCompatActivity() {
    private lateinit var flagImage: ImageView
    private lateinit var progressText: TextView
    private lateinit var scoreText: TextView
    private lateinit var timerText: TextView
    private lateinit var feedbackText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var nextButton: Button
    private lateinit var answerButtons: List<Button>
    private var questions = arrayListOf<Int>()
    private var options = arrayListOf<Int>()
    private var pool = listOf<Country>()
    private var questionNumber = 0
    private var score = 0
    private var correctAnswers = 0
    private var answered = false
    private var feedback = ""
    private var deadline = 0L
    private var secondsLeft = 20
    private var timer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prepareScreen(this, R.layout.activity_game)
        flagImage = findViewById(R.id.flagImage)
        progressText = findViewById(R.id.progressText)
        scoreText = findViewById(R.id.scoreText)
        timerText = findViewById(R.id.timerText)
        feedbackText = findViewById(R.id.feedbackText)
        progressBar = findViewById(R.id.questionProgress)
        nextButton = findViewById(R.id.nextButton)
        answerButtons = listOf(findViewById(R.id.answer1), findViewById(R.id.answer2),
            findViewById(R.id.answer3), findViewById(R.id.answer4))
        pool = Countries.getPool(intent.getStringExtra("continent") ?: "Все страны",
            intent.getIntExtra("difficulty", 0))
        if (savedInstanceState != null) {
            questions = savedInstanceState.getIntegerArrayList("questions")!!
            options = savedInstanceState.getIntegerArrayList("options")!!
            questionNumber = savedInstanceState.getInt("questionNumber")
            score = savedInstanceState.getInt("score")
            correctAnswers = savedInstanceState.getInt("correctAnswers")
            answered = savedInstanceState.getBoolean("answered")
            feedback = savedInstanceState.getString("feedback", "")
            deadline = savedInstanceState.getLong("deadline")
            secondsLeft = savedInstanceState.getInt("secondsLeft")
        } else {
            val count = intent.getIntExtra("count", 5)
            while (questions.size < count) {
                val round = pool.indices.shuffled().toMutableList()
                if (questions.isNotEmpty() && round[0] == questions.last()) {
                    val first = round.removeAt(0)
                    round.add(first)
                }
                for (index in round) {
                    if (questions.size < count) questions.add(index)
                }
            }
            prepareQuestion()
        }
        for (i in answerButtons.indices) {
            answerButtons[i].setOnClickListener { checkAnswer(options[i]) }
        }
        nextButton.setOnClickListener {
            if (questionNumber == questions.lastIndex) {
                showResult()
            } else {
                questionNumber++
                prepareQuestion()
                showQuestion()
                startTimer()
            }
        }
        findViewById<Button>(R.id.menuButton).setOnClickListener { finish() }
        showQuestion()
    }

    // Выбираем варианты ответа и даём 20 секунд.
    private fun prepareQuestion() {
        answered = false
        feedback = ""
        val correct = questions[questionNumber]
        options = ArrayList(pool.indices.filter { it != correct }.shuffled().take(3))
        options.add(correct)
        options.shuffle()
        deadline = SystemClock.elapsedRealtime() + 20000L
        secondsLeft = 20
    }

    // Показываем флаг, ответы и текущие очки.
    private fun showQuestion() {
        val country = pool[questions[questionNumber]]
        flagImage.setImageResource(country.flag)
        flagImage.contentDescription = "Флаг страны, название которой нужно угадать"
        progressText.text = "Вопрос ${questionNumber + 1} из ${questions.size}"
        scoreText.text = "Очки: $score"
        progressBar.max = questions.size
        progressBar.progress = questionNumber + 1
        feedbackText.text = feedback
        for (i in answerButtons.indices) {
            answerButtons[i].text = pool[options[i]].name
            answerButtons[i].isEnabled = !answered
        }
        nextButton.isEnabled = answered
        nextButton.text = if (questionNumber == questions.lastIndex) "Результат" else "Следующий вопрос"
        updateTimerText()
    }

    // Показываем оставшееся время.
    private fun updateTimerText() {
        if (!answered) {
            secondsLeft = ceil((deadline - SystemClock.elapsedRealtime()).coerceAtLeast(0) / 1000.0).toInt()
        }
        timerText.text = "Время: $secondsLeft с"
    }

    // Запускаем таймер; по окончании засчитываем пропуск.
    private fun startTimer() {
        timer?.cancel()
        if (answered) return
        val remaining = deadline - SystemClock.elapsedRealtime()
        if (remaining <= 0) {
            checkAnswer(-1)
            return
        }
        timer = object : CountDownTimer(remaining, 100) {
            override fun onTick(millisUntilFinished: Long) { updateTimerText() }
            override fun onFinish() { checkAnswer(-1) }
        }.start()
    }

    // Проверяем ответ и начисляем очки за оставшееся время.
    private fun checkAnswer(selected: Int) {
        if (answered) return
        val remaining = deadline - SystemClock.elapsedRealtime()
        secondsLeft = ceil(remaining.coerceAtLeast(0) / 1000.0).toInt()
        answered = true
        timer?.cancel()
        val correct = questions[questionNumber]
        if (selected == correct && remaining > 0) {
            val points = ceil(remaining / 1000.0).toInt() * 10
            score += points
            correctAnswers++
            feedback = "Верно! +$points очков"
        } else if (selected == -1 || remaining <= 0) {
            feedback = "Время вышло. Правильный ответ: ${pool[correct].name}"
        } else {
            feedback = "Неверно. Правильный ответ: ${pool[correct].name}"
        }
        showQuestion()
    }

    // Передаём итог на экран результатов.
    private fun showResult() {
        val result = Intent(this, ResultActivity::class.java)
        result.putExtra("score", score)
        result.putExtra("correct", correctAnswers)
        result.putExtra("count", questions.size)
        result.putExtra("continent", intent.getStringExtra("continent"))
        result.putExtra("difficulty", intent.getIntExtra("difficulty", 0))
        startActivity(result)
        finish()
    }

    // Продолжаем таймер при возвращении в игру.
    override fun onStart() {
        super.onStart()
        startTimer()
    }

    // Останавливаем обновление таймера, пока экран скрыт.
    override fun onStop() {
        timer?.cancel()
        super.onStop()
    }

    // Сохраняем текущую игру на случай поворота экрана.
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putIntegerArrayList("questions", questions)
        outState.putIntegerArrayList("options", options)
        outState.putInt("questionNumber", questionNumber)
        outState.putInt("score", score)
        outState.putInt("correctAnswers", correctAnswers)
        outState.putBoolean("answered", answered)
        outState.putString("feedback", feedback)
        outState.putLong("deadline", deadline)
        outState.putInt("secondsLeft", secondsLeft)
    }
}
