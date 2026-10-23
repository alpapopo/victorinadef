package com.example.victorina

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // Создаём меню и заполняем списки выбора.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prepareScreen(this, R.layout.activity_main)
        val continent = findViewById<Spinner>(R.id.continentSpinner)
        val count = findViewById<Spinner>(R.id.countSpinner)
        val difficulty = findViewById<Spinner>(R.id.difficultySpinner)
        fillSpinner(continent, listOf("Все страны", "Европа", "Азия", "Америка"))
        fillSpinner(count, listOf("5", "10", "15"))
        fillSpinner(difficulty, listOf("Легко", "Средне", "Сложно"))
        findViewById<Button>(R.id.startButton).setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("continent", continent.selectedItem.toString())
            intent.putExtra("count", count.selectedItem.toString().toInt())
            intent.putExtra("difficulty", difficulty.selectedItemPosition)
            startActivity(intent)
        }
        findViewById<Button>(R.id.recordsButton).setOnClickListener {
            startActivity(Intent(this, RecordsActivity::class.java))
        }
    }

    // Показываем список значений в выпадающем меню.
    private fun fillSpinner(spinner: Spinner, items: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }
}
