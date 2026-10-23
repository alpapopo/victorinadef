package com.example.victorina


import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RecordsActivity : AppCompatActivity() {

    // Загружаем рекорды и показываем их списком.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prepareScreen(this, R.layout.activity_records)
        val records = Records.load(this)
        var text = ""
        for (i in records.indices) {
            val item = records[i]
            text += "${i + 1}. ${item.score} очков\n" +
                "${item.mode}\n" +
                "Правильных ответов: ${item.correct}\n\n"
        }
        if (text.isEmpty()) text = "Рекордов пока нет. Сыграй первую игру."
        findViewById<TextView>(R.id.recordsText).text = text
        findViewById<Button>(R.id.backButton).setOnClickListener { finish() }
    }
}
