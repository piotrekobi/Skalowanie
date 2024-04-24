package com.example.skalowanie

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PrintUtils {

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }


    fun generateHtmlForPrint(data: Map<String, String>): String {
        val userName = data["name"]?.replace("\n", "<br>") ?: ""
        val userGroup = data["group"]?.replace("\n", "<br>") ?: ""
        var date = data["date"]?.replace("\n", "<br>") ?: ""
        if (data["useTodayDate"] == "true") {
            date = getCurrentDate().replace("\n", "<br>")
        }
        val situation = data["situation"]?.replace("\n", "<br>") ?: ""
        val thoughtsBefore = data["thoughtsBefore"]?.replace("\n", "<br>") ?: ""
        val thoughtsDuring = data["thoughtsDuring"]?.replace("\n", "<br>") ?: ""
        val thoughtsAfter = data["thoughtsAfter"]?.replace("\n", "<br>") ?: ""
        val emotionsBefore = data["emotionsBefore"]?.replace("\n", "<br>") ?: ""
        val emotionsDuring = data["emotionsDuring"]?.replace("\n", "<br>") ?: ""
        val emotionsAfter = data["emotionsAfter"]?.replace("\n", "<br>") ?: ""
        val bodyBefore = data["bodyBefore"]?.replace("\n", "<br>") ?: ""
        val bodyDuring = data["bodyDuring"]?.replace("\n", "<br>") ?: ""
        val bodyAfter = data["bodyAfter"]?.replace("\n", "<br>") ?: ""
        val behaviorBefore = data["behaviorBefore"]?.replace("\n", "<br>") ?: ""
        val behaviorDuring = data["behaviorDuring"]?.replace("\n", "<br>") ?: ""
        val behaviorAfter = data["behaviorAfter"]?.replace("\n", "<br>") ?: ""
        val achievements = data["achievements"]?.replace("\n", "<br>") ?: ""
        val toDo = data["toDo"]?.replace("\n", "<br>") ?: ""
        val convictions = data["convictions"]?.replace("\n", "<br>") ?: ""
        val advocate = data["advocate"]?.replace("\n", "<br>") ?: ""

        return """
    <!DOCTYPE html>
    <html>
<head>
<style>
@page {
    size: A4 landscape;
    margin: 10mm; /* Top, Right, Bottom, Left */
}
  .header-info {
    text-align: center;
    margin-bottom: 5px; /* Adjust as needed */
  }
  .header-title {
    display: inline-block;
    margin-right: 160px; /* Space between header items */
  }
  body, html {
    margin: 0;
    padding: 0;
    height: 100%;
    box-sizing: border-box;
  }
  @media print {
    .page {
width: 277mm;
    height: 190mm; /* Adjust height slightly less than A4 to ensure content fits */
    display: flex;
    flex-direction: column;
    justify-content: space-between; /* Ensures no unnecessary stretch */
    overflow: hidden;
    }
    table {
      width: 100%;
      height: 100%;
   
    border-collapse: collapse;
    table-layout: fixed;
    }
  }
  .page {
    width: 277mm; /* Adjusted width for the page content area */
    height: 190mm; /* Adjusted height for the page content area */
    margin: auto; /* Centers the page content area if needed */
  }
  table {
    width: 100%;
    height: 100%;
    border-collapse: collapse;
    table-layout: fixed;
  }
  body { 
    font-family: 'Arial', sans-serif; 
    margin: 0;
  }
  table { 
    width: 100%; 
    border-collapse: collapse; 
    table-layout: fixed; /* Ensures the table stretches to full width */
    height: 100%; /* Stretch table to container height */
  }
  th, td {
    border: 1px solid #000;
    padding: 8px;
    text-align: left;
    vertical-align: top;
  }
  th {
    background-color: #f2f2f2;
    text-align: center;
  }
  .vertical-text { 
    writing-mode: vertical-lr; 
    transform: rotate(270deg); 
    text-align: middle;
  }
  .text-cell { 
    height: 135px; /* Height will be dynamic to fit content */
  }
    .situation-cell { 
      height: 600px; /* Height will be dynamic to fit content */
    }
  
    .no-left-border {
      border-left: none;
    }
  .no-right-border {
    border-right: none;
  }

  .no-bottom-border {
    border-bottom: none;
  }
  
    .minimized-text-cell { 
      height: auto; /* Height will be dynamic to fit content */
    }
    .situation-header-cell { 
      width: 8%; /* Adjust width for 6 equal columns */
    }
      .labels-header-cell { 
        width: 4%; /* Adjust width for 6 equal columns */
      }
  .header-cell { 
    width: 18%; /* Adjust width for 6 equal columns */
  }
  .minimized-row td {
    border-top: none;
    padding: 0;
    height: 1px; /* Minimal height for the row */
    white-space: nowrap; /* Ensure the content does not wrap */
  }
  .minimized-row th {
    border: 1px solid #000;
    padding: 5px; /* Smaller padding */
  }
  .no-border {
    border: 0;
  }
</style>
</head>
<body>
<div class="page">
<div class="header-info">
    <span class="header-title">Imię i nazwisko: $userName</span>
    <span class="header-title">Grupa: $userGroup</span>
    <span class="header-title">Data: $date</span>
  </div>
    <table>
      <tr>
        <th class="situation-header-cell">Sytuacja</th>
        <th class="labels-header-cell"></th>
        <th class="header-cell">Przed sytuacją</th>
        <th class="header-cell">W trakcie sytuacji</th>
        <th class="header-cell">Po sytuacji</th>
        <th class="header-cell">Moje osiągnięcia</th>
        <th class="header-cell">Co jeszcze moge zrobić</th>
      </tr>
      <tr>
        <td class="situation-cell" rowspan="5"><SPAN STYLE="writing-mode: vertical-rl; transform: rotate(180deg);
                      position: relative;">$situation</SPAN></td>
        <td class="text-cell"><SPAN STYLE="writing-mode: vertical-lr;
                     -ms-writing-mode: tb-rl;
                     transform: rotate(180deg);position: relative; top: 30%;">MYŚLI</SPAN></td>
        <td class="text-cell">$thoughtsBefore</td>
        <td class="text-cell">$thoughtsDuring</td>
        <td class="text-cell">$thoughtsAfter</td>
        <td class="text-cell" rowspan="2">$achievements</td>
        <td class="text-cell" rowspan="2">$toDo</td>
      </tr>
      <tr>
        <td class="text-cell no-bottom-border"><SPAN STYLE="writing-mode: vertical-lr;
                     -ms-writing-mode: tb-rl;
                     transform: rotate(180deg); position: relative; top: 25%;">EMOCJE</SPAN></td>
        <td class="text-cell no-bottom-border">$emotionsBefore</td>
        <td class="text-cell no-bottom-border">$emotionsDuring</td>
        <td class="text-cell no-bottom-border">$emotionsAfter</td>
      </tr>
<tr class="minimized-row">
  <td class="minimized-row"></td>
  <td class="minimized-row"></td>
  <td class="minimized-row"></td>
  <td class="minimized-row"></td>
  <th class="minimized-text-cell">Przekonania</th>
  <th class="minimized-text-cell">Adwokat</th>
</tr>

      <tr>
        <td class="text-cell"><SPAN STYLE="writing-mode: vertical-lr;
                     -ms-writing-mode: tb-rl;
                     transform: rotate(180deg); position: relative; top: 30%;">CIAŁO</SPAN></td>
        <td class="text-cell">$bodyBefore</td>
        <td class="text-cell">$bodyDuring</td>
        <td class="text-cell">$bodyAfter</td>
        <td class="text-cell" rowspan="2">$convictions</td>
        <td class="text-cell" rowspan="2">$advocate</td>
      </tr>
      <tr>
        <td class="text-cell"><SPAN STYLE="writing-mode: vertical-lr;
                     -ms-writing-mode: tb-rl;
                     transform: rotate(180deg); position: relative; top: 5%;">ZACHOWANIE</SPAN></td>
        <td class="text-cell">$behaviorBefore</td>
        <td class="text-cell">$behaviorDuring</td>
        <td class="text-cell">$behaviorAfter</td>
      </tr>

    </table>
    </div>
    </body>
    </html>
    """.trimIndent()
    }
}