package edu.skku.cs.pa1

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    val wordList = mutableListOf<Word>()

    val wrongLetterList = mutableListOf<Letter>()
    val wrongPositionLetterList = mutableListOf<Letter>()
    val rightLetterList = mutableListOf<Letter>()

    val stringSet = mutableSetOf<String>()
    var answer: String = ""

    var rightColor = 0
    var wrongPositionColor = 0
    var wrongColor = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initColors()
        loadWords(stringSet)
        answer = stringSet.random()
        Log.i("stringSetSize", stringSet.size.toString())
        Log.i("answer", answer)

        val wordRecyclerView = findViewById<RecyclerView>(R.id.wordleWordList)
        val rightLetterRecyclerView = findViewById<RecyclerView>(R.id.rightLetterList)
        val wrongPositionLetterRecyclerView = findViewById<RecyclerView>(R.id.wrongPositionLetterList)
        val wrongLetterRecyclerView = findViewById<RecyclerView>(R.id.wrongLetterList)

        val wordAdapter = WordAdapter(wordList)
        val rightLetterAdapter = LetterAdapter(rightLetterList)
        val wrongPositionLetterAdapter = LetterAdapter(wrongPositionLetterList)
        val wrongLetterAdapter = LetterAdapter(wrongLetterList)

        val recyclerViews = listOf(
            wordRecyclerView,
            rightLetterRecyclerView,
            wrongPositionLetterRecyclerView,
            wrongLetterRecyclerView
        )

        val adapters = listOf(
            wordAdapter,
            rightLetterAdapter,
            wrongPositionLetterAdapter,
            wrongLetterAdapter
        )

        recyclerViews.forEachIndexed { index, recyclerView ->
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
            recyclerView.adapter = adapters[index]
        }

        val wordleTextView = findViewById<TextView>(R.id.wordleText)
        val submitButton = findViewById<Button>(R.id.submitButton)

        submitButton.setOnClickListener {
            val input = wordleTextView.text.toString().lowercase()
            if (stringSet.contains(input)) {
                Log.i("input", input)
                calculateInput(input)
                notifyToAdapters(rightLetterAdapter, wrongPositionLetterAdapter, wrongLetterAdapter, wordAdapter)
                wordRecyclerView.scrollToPosition(wordList.size - 1)
            } else {
                Log.i("word", "not found")
                Toast.makeText(this, "Word $input not in dictionary!", Toast.LENGTH_SHORT).show()
            }
            wordleTextView.text = ""
        }
    }

    private fun loadWords(stringSet: MutableSet<String>) {
        applicationContext.assets.open("wordle_words.txt").bufferedReader().useLines { lines ->
            lines.forEach {
                stringSet.add(it)
            }
        }
    }

    private fun initColors() {
        rightColor = ContextCompat.getColor(this, R.color.right)
        wrongPositionColor = ContextCompat.getColor(this, R.color.wrong_position)
        wrongColor = ContextCompat.getColor(this, R.color.wrong)
    }

    private fun notifyToAdapters(rightLetterAdapter: LetterAdapter, wrongPositionLetterAdapter: LetterAdapter, wrongLetterAdapter: LetterAdapter, wordAdapter: WordAdapter) {
        rightLetterAdapter.notifyDataSetChanged()
        wrongPositionLetterAdapter.notifyDataSetChanged()
        wrongLetterAdapter.notifyDataSetChanged()
        wordAdapter.notifyItemInserted(wordList.size - 1)
    }

    private fun calculateInput(input: String) {
        val word = Word(mutableListOf())
        for (i in 0..4) {
            if (input[i] == answer[i]) {
                word.letters.add(Letter(input[i], rightColor, Color.BLACK))
                if (rightLetterList.contains(Letter(input[i], rightColor, Color.BLACK))) {
                    continue
                }
                rightLetterList.add(Letter(input[i], rightColor, Color.BLACK))
            } else if (answer.contains(input[i])) {
                word.letters.add(Letter(input[i], wrongPositionColor, Color.BLACK))
                if (wrongPositionLetterList.contains(Letter(input[i], wrongPositionColor, Color.BLACK))) {
                    continue
                }
                wrongPositionLetterList.add(Letter(input[i], wrongPositionColor, Color.BLACK))

            } else {
                word.letters.add(Letter(input[i], wrongColor, Color.WHITE))
                if (wrongLetterList.contains(Letter(input[i], wrongColor, Color.WHITE))) {
                    continue
                }
                wrongLetterList.add(Letter(input[i], wrongColor, Color.WHITE))
            }
        }

        for (letter in rightLetterList) {
            if (wrongPositionLetterList.contains(letter)) {
                wrongPositionLetterList.remove(letter)
            }
        }

        rightLetterList.sort()
        wrongPositionLetterList.sort()
        wrongLetterList.sort()
        wordList.add(word)
    }
}