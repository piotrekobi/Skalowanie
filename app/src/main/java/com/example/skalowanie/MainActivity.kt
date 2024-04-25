package com.example.skalowanie

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.EditText
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.content.SharedPreferences
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.view.View
import java.text.SimpleDateFormat
import java.util.*
import android.text.TextWatcher
import android.text.Editable
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private var tableId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Retrieve the table ID or generate a new one if null (for safety, although it should always be passed)
        tableId = intent.getStringExtra("tableId") ?: System.currentTimeMillis().toString()



        webView = WebView(this).apply {
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    print()
                }
            }
        }
        loadData()
        loadGlobalData()

        val useCurrentDateCheckBox = findViewById<CheckBox>(R.id.useCurrentDateCheckBox)
        val dateEditText = findViewById<EditText>(R.id.dateEditText)

        useCurrentDateCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val today = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())
                dateEditText.setText(today)
            } else {
                dateEditText.setText("")
            }
        }

        if (dateEditText.text.toString() == "") useCurrentDateCheckBox.isChecked = true

        dateEditText.setOnClickListener {
            showDatePicker()
            useCurrentDateCheckBox.isChecked = false
        }

        setupTextWatchers()
    }

    fun printDocument(view: View) {
        val htmlDocument = PrintUtils.generateHtmlForPrint(retrieveData())
        webView.loadDataWithBaseURL(null, htmlDocument, "text/HTML", "UTF-8", null)
    }

    private fun print() {
        val printManager = getSystemService(Context.PRINT_SERVICE) as PrintManager
        val printAdapter = webView.createPrintDocumentAdapter("Document")
        val printAttributes = PrintAttributes.Builder()
            .setMediaSize(PrintAttributes.MediaSize.ISO_A4.asLandscape())
            .setDuplexMode(PrintAttributes.DUPLEX_MODE_SHORT_EDGE)
            .build()
        printManager.print("Document", printAdapter, printAttributes)
    }


    private fun retrieveData(): Map<String, String> {
        val data = mutableMapOf<String, String>()
        data["situation"] = findViewById<EditText>(R.id.editTextSytuacja).text.toString()
        data["thoughtsBefore"] = findViewById<EditText>(R.id.editTextMysliBefore).text.toString()
        data["thoughtsDuring"] = findViewById<EditText>(R.id.editTextMysliDuring).text.toString()
        data["thoughtsAfter"] = findViewById<EditText>(R.id.editTextMysliAfter).text.toString()
        data["emotionsBefore"] = findViewById<EditText>(R.id.editTextEmotionsBefore).text.toString()
        data["emotionsDuring"] = findViewById<EditText>(R.id.editTextEmotionsDuring).text.toString()
        data["emotionsAfter"] = findViewById<EditText>(R.id.editTextEmotionsAfter).text.toString()
        data["bodyBefore"] = findViewById<EditText>(R.id.editTextBodyBefore).text.toString()
        data["bodyDuring"] = findViewById<EditText>(R.id.editTextBodyDuring).text.toString()
        data["bodyAfter"] = findViewById<EditText>(R.id.editTextBodyAfter).text.toString()
        data["behaviorBefore"] = findViewById<EditText>(R.id.editTextBehaviorBefore).text.toString()
        data["behaviorDuring"] = findViewById<EditText>(R.id.editTextBehaviorDuring).text.toString()
        data["behaviorAfter"] = findViewById<EditText>(R.id.editTextBehaviorAfter).text.toString()
        data["achievements"] = findViewById<EditText>(R.id.editTextAchievements).text.toString()
        data["toDo"] = findViewById<EditText>(R.id.editTextToDo).text.toString()
        data["convictions"] = findViewById<EditText>(R.id.editTextConvictions).text.toString()
        data["advocate"] = findViewById<EditText>(R.id.editTextAdvocate).text.toString()
        data["name"] = findViewById<EditText>(R.id.nameEditText).text.toString()
        data["group"] = findViewById<EditText>(R.id.groupEditText).text.toString()
        data["date"] = findViewById<EditText>(R.id.dateEditText).text.toString()
        data["useTodayDate"] = findViewById<CheckBox>(R.id.useCurrentDateCheckBox).isChecked.toString()
        return data
    }

    private fun getGlobalPreferences(): SharedPreferences {
        return getSharedPreferences("GlobalSettings", Context.MODE_PRIVATE)
    }

    private fun saveGlobalData() {
        val editor = getGlobalPreferences().edit()


        val data = JSONObject().apply {
            put("name", findViewById<EditText>(R.id.nameEditText).text.toString())
            put("group", findViewById<EditText>(R.id.groupEditText).text.toString())
        }

        editor.putString("GlobalSettings", data.toString())

        editor.apply()
    }
    private fun loadGlobalData() {
        val sharedPreferences = getGlobalPreferences()

        sharedPreferences.getString("GlobalSettings", null)?.let {
            val jsonObject = JSONObject(it)
            findViewById<EditText>(R.id.nameEditText).setText(jsonObject.getString("name"))
            findViewById<EditText>(R.id.groupEditText).setText(jsonObject.getString("group"))
        }
    }


    private fun saveTableData() {
        val sharedPreferences = getSharedPreferences("TableData", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Save individual table data
        val data = JSONObject().apply {
            var situation = findViewById<EditText>(R.id.editTextSytuacja).text.toString()
            if (situation.trim() == ""){situation = "Nieopisana sytuacja"}
            put("situation", situation)
            put("thoughtsBefore", findViewById<EditText>(R.id.editTextMysliBefore).text.toString())
            put("thoughtsDuring", findViewById<EditText>(R.id.editTextMysliDuring).text.toString())
            put("thoughtsAfter", findViewById<EditText>(R.id.editTextMysliAfter).text.toString())
            put("emotionsBefore", findViewById<EditText>(R.id.editTextEmotionsBefore).text.toString())
            put("emotionsDuring", findViewById<EditText>(R.id.editTextEmotionsDuring).text.toString())
            put("emotionsAfter", findViewById<EditText>(R.id.editTextEmotionsAfter).text.toString())
            put("bodyBefore", findViewById<EditText>(R.id.editTextBodyBefore).text.toString())
            put("bodyDuring", findViewById<EditText>(R.id.editTextBodyDuring).text.toString())
            put("bodyAfter", findViewById<EditText>(R.id.editTextBodyAfter).text.toString())
            put("behaviorBefore", findViewById<EditText>(R.id.editTextBehaviorBefore).text.toString())
            put("behaviorDuring", findViewById<EditText>(R.id.editTextBehaviorDuring).text.toString())
            put("behaviorAfter", findViewById<EditText>(R.id.editTextBehaviorAfter).text.toString())
            put("achievements", findViewById<EditText>(R.id.editTextAchievements).text.toString())
            put("toDo", findViewById<EditText>(R.id.editTextToDo).text.toString())
            put("convictions", findViewById<EditText>(R.id.editTextConvictions).text.toString())
            put("advocate", findViewById<EditText>(R.id.editTextAdvocate).text.toString())
            put("date", findViewById<EditText>(R.id.dateEditText).text.toString())

        }
        editor.putString(tableId, data.toString())

        editor.apply()
    }

    private fun loadData() {
        val sharedPreferences = getSharedPreferences("TableData", Context.MODE_PRIVATE)

        // Load individual table data
        sharedPreferences.getString(tableId, null)?.let {
            val jsonObject = JSONObject(it)
            findViewById<EditText>(R.id.editTextSytuacja).setText(jsonObject.getString("situation"))
            findViewById<EditText>(R.id.editTextMysliBefore).setText(jsonObject.getString("thoughtsBefore"))
            findViewById<EditText>(R.id.editTextMysliDuring).setText(jsonObject.getString("thoughtsDuring"))
            findViewById<EditText>(R.id.editTextMysliAfter).setText(jsonObject.getString("thoughtsAfter"))
            findViewById<EditText>(R.id.editTextEmotionsBefore).setText(jsonObject.getString("emotionsBefore"))
            findViewById<EditText>(R.id.editTextEmotionsDuring).setText(jsonObject.getString("emotionsDuring"))
            findViewById<EditText>(R.id.editTextEmotionsAfter).setText(jsonObject.getString("emotionsAfter"))
            findViewById<EditText>(R.id.editTextBodyBefore).setText(jsonObject.getString("bodyBefore"))
            findViewById<EditText>(R.id.editTextBodyDuring).setText(jsonObject.getString("bodyDuring"))
            findViewById<EditText>(R.id.editTextBodyAfter).setText(jsonObject.getString("bodyAfter"))
            findViewById<EditText>(R.id.editTextBehaviorBefore).setText(jsonObject.getString("behaviorBefore"))
            findViewById<EditText>(R.id.editTextBehaviorDuring).setText(jsonObject.getString("behaviorDuring"))
            findViewById<EditText>(R.id.editTextBehaviorAfter).setText(jsonObject.getString("behaviorAfter"))
            findViewById<EditText>(R.id.editTextAchievements).setText(jsonObject.getString("achievements"))
            findViewById<EditText>(R.id.editTextToDo).setText(jsonObject.getString("toDo"))
            findViewById<EditText>(R.id.editTextConvictions).setText(jsonObject.getString("convictions"))
            findViewById<EditText>(R.id.editTextAdvocate).setText(jsonObject.getString("advocate"))
            findViewById<EditText>(R.id.dateEditText).setText(jsonObject.getString("date"))
        }

    }




    private fun setupTextWatchers() {
        val editTextIds = arrayOf(
            R.id.editTextSytuacja, R.id.editTextMysliBefore, R.id.editTextMysliDuring, R.id.editTextMysliAfter,
            R.id.editTextEmotionsBefore, R.id.editTextEmotionsDuring, R.id.editTextEmotionsAfter,
            R.id.editTextBodyBefore, R.id.editTextBodyDuring, R.id.editTextBodyAfter,
            R.id.editTextBehaviorBefore, R.id.editTextBehaviorDuring, R.id.editTextBehaviorAfter,
            R.id.editTextAchievements, R.id.editTextToDo, R.id.editTextConvictions, R.id.editTextAdvocate,
            R.id.nameEditText, R.id.groupEditText, R.id.dateEditText
        )
        editTextIds.forEach { editTextId ->
            findViewById<EditText>(editTextId).addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    saveTableData()
                    saveGlobalData()
                }
            })
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val formattedDay = String.format("%02d", selectedDay)
            val formattedMonth = String.format("%02d", selectedMonth + 1)  // Month is zero-based, add 1 for display
            val selectedDate = "$formattedDay.$formattedMonth.$selectedYear"
            findViewById<EditText>(R.id.dateEditText).setText(selectedDate)
        }, year, month, day)

        datePickerDialog.show()
    }







}





