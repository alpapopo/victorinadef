package com.example.victorina

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ResultActivity : AppCompatActivity() {

    // Показываем итог и записываем результат в таблицу рекордов.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prepareScreen(this, R.layout.activity_result)
        val score = intent.getIntExtra("score", 0)
        val correct = intent.getIntExtra("correct", 0)
        val count = intent.getIntExtra("count", 5)
        val continent = intent.getStringExtra("continent") ?: "Все страны"
        val difficulty = intent.getIntExtra("difficulty", 0)
        val level = listOf("Легко", "Средне", "Сложно")[difficulty]
        findViewById<TextView>(R.id.resultText).text =
            "Правильных ответов: $correct из $count\n" +
            "Очки: $score\nПравильных ответов: ${correct * 100 / count}%"
        if (savedInstanceState == null) {
            Records.save(this, score, "$continent, $level, $count вопросов", correct)
        }
        findViewById<Button>(R.id.resultRecordsButton).setOnClickListener {
            startActivity(Intent(this, RecordsActivity::class.java))
        }
        findViewById<Button>(R.id.resultMenuButton).setOnClickListener { finish() }
    }
}
