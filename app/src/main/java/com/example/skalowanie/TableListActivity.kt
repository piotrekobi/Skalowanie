package com.example.skalowanie

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONObject

class TableListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TablesAdapter
    private lateinit var fabAddTable: FloatingActionButton
    private lateinit var selectAllButton: Button
    private lateinit var deselectAllButton: Button
    private lateinit var deleteButton: Button
    private lateinit var printButton: Button

    override fun onResume() {
        super.onResume()
        adapter.updateTables(loadTables())
        updateActionBar()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        recyclerView = findViewById(R.id.recyclerView)
        fabAddTable = findViewById(R.id.fab_add_table)
        selectAllButton = findViewById(R.id.selectAllButton)
        deselectAllButton = findViewById(R.id.deselectAllButton)
        deleteButton = findViewById(R.id.deleteButton)
        printButton = findViewById(R.id.printButton)
        adapter = TablesAdapter(loadTables(), { table ->
            val intent = Intent(this@TableListActivity, MainActivity::class.java)
            intent.putExtra("tableId", table.id)
            startActivity(intent)
        }, { isSelected ->
            updateActionBar()
        })

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        fabAddTable.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            val uniqueId = System.currentTimeMillis().toString()
            intent.putExtra("tableId", uniqueId)
            startActivityForResult(intent, 1)
        }

        selectAllButton.setOnClickListener {
            adapter.selectAll()
            updateActionBar()
        }

        deselectAllButton.setOnClickListener {
            adapter.deselectAll()
            updateActionBar()
        }

        deleteButton.setOnClickListener {
            adapter.deleteSelected(this)
            updateActionBar()
        }

        printButton.setOnClickListener {
            printSelectedTables()
        }

        updateActionBar()

    }

    private fun printSelectedTables() {
        val htmlContent = adapter.getSelectedTablesHtml(this)
        val webView = WebView(this)
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                printWebViewContent(view)
            }
        }
        webView.loadDataWithBaseURL(null, htmlContent, "text/HTML", "UTF-8", null)
    }

    private fun printWebViewContent(webView: WebView) {
        val printManager = getSystemService(Context.PRINT_SERVICE) as PrintManager
        val printAdapter = webView.createPrintDocumentAdapter("Document")
        val printAttributes = PrintAttributes.Builder()
            .setMediaSize(PrintAttributes.MediaSize.ISO_A4.asLandscape())
            .build()
        printManager.print("Document", printAdapter, printAttributes)
    }

    fun updateActionBar() {
        val anySelected = adapter.getItemCount() > 0 && adapter.tables.any(Table::isSelected)
        if (adapter.getItemCount() > 0) selectAllButton.isEnabled = !adapter.tables.all(Table::isSelected)
        else selectAllButton.isEnabled = false

        deselectAllButton.isEnabled = anySelected
        deleteButton.isEnabled = anySelected
        printButton.isEnabled = anySelected
    }


    private fun loadTables(): List<Table> {
        val sharedPreferences = getSharedPreferences("TableData", MODE_PRIVATE)
        val allEntries = sharedPreferences.all
        return allEntries.entries.map { entry ->
            val jsonObject = JSONObject(entry.value.toString())
            val situation = jsonObject.getString("situation")
            Table(situation, entry.key)
        }.sortedByDescending { it.id.toLong() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 1) {
            adapter.updateTables(loadTables())
            updateActionBar()
        }
    }
}

data class Table(val situation: String, val id: String, var isSelected: Boolean = false)

class TablesAdapter(var tables: List<Table>, private val onClick: (Table) -> Unit, private val onLongClick: (Boolean) -> Unit) : RecyclerView.Adapter<TablesAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: android.widget.TextView = view.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        val view = android.view.LayoutInflater.from(parent.context).inflate(R.layout.list_item_table, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val table = tables[position]
        holder.textView.text = formatSituationText(table.situation)

        holder.itemView.setOnClickListener {
            if (tables.any { it.isSelected }) {
                table.isSelected = !table.isSelected
                notifyItemChanged(position)
                onLongClick(tables.any { it.isSelected })
            } else {
                onClick(table)
            }
        }

        holder.itemView.setOnLongClickListener {
            table.isSelected = !table.isSelected
            notifyItemChanged(position)
            onLongClick(tables.any { it.isSelected })
            true
        }

        holder.itemView.setBackgroundColor(if (table.isSelected) Color.parseColor("#ADD8E6") else Color.TRANSPARENT)
    }

    override fun getItemCount(): Int = tables.size

    private fun formatSituationText(situation: String): String {
        return if (situation.length > 50) "${situation.substring(0, 50)}..." else situation
    }

    fun updateTables(newTables: List<Table>) {
        tables = newTables
        notifyDataSetChanged()
    }

    fun selectAll() {
        tables.forEach { it.isSelected = true }
        notifyDataSetChanged()
    }

    fun deselectAll() {
        tables.forEach { it.isSelected = false }
        notifyDataSetChanged()
    }

    fun deleteSelected(context: Context) {
        val sharedPreferences = context.getSharedPreferences("TableData", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        tables.filter { it.isSelected }.forEach { editor.remove(it.id) }
        editor.apply()
        tables = tables.filterNot { it.isSelected }
        notifyDataSetChanged()
    }

    fun getSelectedTablesHtml(context: Context): String {
        val stringBuilder = StringBuilder()
        val sharedPreferences = context.getSharedPreferences("TableData", Context.MODE_PRIVATE)
        val globalSharedPreferences = context.getSharedPreferences("GlobalSettings", Context.MODE_PRIVATE)
        val globalDataJson = globalSharedPreferences.getString("GlobalSettings", null)
        val globalDataMap = globalDataJson?.let {
            val globalJsonObject = JSONObject(it)
            mapOf(
                "name" to globalJsonObject.getString("name"),
                "group" to globalJsonObject.getString("group")
            )
        } ?: emptyMap()

        tables.filter { it.isSelected }.forEach { table ->
            val jsonData = sharedPreferences.getString(table.id, null)
            jsonData?.let {
                val jsonObject = JSONObject(it)
                val dataMap = mutableMapOf(
                    "situation" to jsonObject.getString("situation"),
                    "thoughtsBefore" to jsonObject.getString("thoughtsBefore"),
                    "thoughtsDuring" to jsonObject.getString("thoughtsDuring"),
                    "thoughtsAfter" to jsonObject.getString("thoughtsAfter"),
                    "emotionsBefore" to jsonObject.getString("emotionsBefore"),
                    "emotionsDuring" to jsonObject.getString("emotionsDuring"),
                    "emotionsAfter" to jsonObject.getString("emotionsAfter"),
                    "bodyBefore" to jsonObject.getString("bodyBefore"),
                    "bodyDuring" to jsonObject.getString("bodyDuring"),
                    "bodyAfter" to jsonObject.getString("bodyAfter"),
                    "behaviorBefore" to jsonObject.getString("behaviorBefore"),
                    "behaviorDuring" to jsonObject.getString("behaviorDuring"),
                    "behaviorAfter" to jsonObject.getString("behaviorAfter"),
                    "achievements" to jsonObject.getString("achievements"),
                    "toDo" to jsonObject.getString("toDo"),
                    "convictions" to jsonObject.getString("convictions"),
                    "advocate" to jsonObject.getString("advocate"),
                    "date" to jsonObject.optString("date", "")
                )
                dataMap.putAll(globalDataMap) // Add global data to the map
                val htmlForPrint = PrintUtils.generateHtmlForPrint(dataMap)
                stringBuilder.append(htmlForPrint)
            }
        }
        return stringBuilder.toString()
    }

}
