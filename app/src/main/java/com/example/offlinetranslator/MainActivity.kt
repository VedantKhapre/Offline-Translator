package com.example.offlinetranslator

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.nl.translate.Translation

class MainActivity : ComponentActivity() {
    private lateinit var inputEditText: EditText
    private lateinit var sourceLanguageSpinner: Spinner
    private lateinit var targetLanguageSpinner: Spinner
    private lateinit var translateButton: Button
    private lateinit var translatedTextView: TextView

    // Map of supported languages
    private val languageMap = mapOf(
        "Marathi" to TranslateLanguage.MARATHI,
        "Hindi" to TranslateLanguage.HINDI,
        "Bengali" to TranslateLanguage.BENGALI,
        "Tamil" to TranslateLanguage.TAMIL,
        "English" to TranslateLanguage.ENGLISH
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        inputEditText = findViewById(R.id.inputEditText)
        sourceLanguageSpinner = findViewById(R.id.sourceLanguageSpinner)
        targetLanguageSpinner = findViewById(R.id.targetLanguageSpinner)
        translateButton = findViewById(R.id.translateButton)
        translatedTextView = findViewById(R.id.translatedTextView)

        // Language options in spinners
        setupLanguageSpinners()

        // Translation button click listener
        translateButton.setOnClickListener {
            performTranslation()
        }
    }

    private fun setupLanguageSpinners() {
        // Get a list of language names from the languageMap
        val languages = languageMap.keys.toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sourceLanguageSpinner.adapter = adapter
        targetLanguageSpinner.adapter = adapter
    }

    private fun performTranslation() {
        // Get the input text from the EditText field
        val inputText = inputEditText.text.toString()
        val sourceLanguage = languageMap[sourceLanguageSpinner.selectedItem.toString()]
        val targetLanguage = languageMap[targetLanguageSpinner.selectedItem.toString()]

        if (inputText.isNotEmpty() && sourceLanguage != null && targetLanguage != null) {
            // Translator with the selected languages
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(sourceLanguage)
                .setTargetLanguage(targetLanguage)
                .build()

            val translator: Translator = Translation.getClient(options)

            // Download the model if needed
            translator.downloadModelIfNeeded()
                .addOnSuccessListener {
                    // Perform the translation
                    translator.translate(inputText)
                        .addOnSuccessListener { translatedText ->
                            translatedTextView.text = translatedText
                        }
                        .addOnFailureListener { exception ->
                            translatedTextView.text = "Translation failed: ${exception.message}"
                        }
                }
                .addOnFailureListener { exception ->
                    translatedTextView.text = "Model download failed: ${exception.message}"
                }
        } else {
            translatedTextView.text = "Please enter text and select languages."
        }
    }
}
