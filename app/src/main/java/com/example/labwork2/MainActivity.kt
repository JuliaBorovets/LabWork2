package com.example.labwork2

import android.graphics.Color
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.DataPointInterface
import com.jjoe64.graphview.series.LineGraphSeries
import com.jjoe64.graphview.series.PointsGraphSeries
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*


class MainActivity : AppCompatActivity() {

    private val nullArray = Array(1) { Array(1) { 0} }
    var arrays: Array<Array<Int>> = nullArray
    private var plottedValues: MutableMap<Int, Double> = mutableMapOf()
    private var result: TextView? = null
    private var pathFile: String? = null
    var nameOfFile: String = "myLab2.txt"
    
    private val baseFileContent: String = """
12 -2 32 4 52 6 -7 38 -2 32 4 52 6 -7 38
1 12 6 9 -1 2 32 2 12 3 34 9 -1 2 32 2 12 3 34
5 2 8 3 1 3 5 2 1 6 9 -1 2 32 -2 32 4 52 6 -7 38
2 1 5 2 4 3 32 4 52 6
""".trimIndent()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE) //will hide the title
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)
        result = findViewById(R.id.textView)
        textView.movementMethod = ScrollingMovementMethod()

        pathFile = getExternalFilesDir(null)?.absolutePath.toString()
        pathToFile.append(pathFile)
    }


    fun plot(view: View) {
        graph.removeAllSeries()
        graph.visibility = GraphView.VISIBLE
        if (plottedValues == mutableMapOf<Int, Double>()) {
            Toast.makeText(
                this,
                "Please sort the array first!",
                Toast.LENGTH_SHORT
            ).show()
            return
        } else {
            val plottVal = plottedValues
            val minSize: Int = plottVal.keys.min() ?: 1
            val maxSize: Int = plottVal.keys.max() ?: 1
            val graphPoints: Array<DataPoint> =
                plottVal.map { key -> DataPoint(key.key.toDouble(), key.value) }.toTypedArray()
            val series: PointsGraphSeries<DataPointInterface> = PointsGraphSeries(graphPoints)
            
            val function = interpolate()
            val squarePlotData: Array<DataPoint> = (minSize..maxSize step 1).map {
                DataPoint(it.toDouble(), function(it.toDouble())) }.toTypedArray()
            val interpolatedPlot: LineGraphSeries<DataPointInterface> =
                LineGraphSeries(squarePlotData)

            graph.viewport.isYAxisBoundsManual = true
            graph.viewport.setMaxY(squarePlotData.last().y + 0.05)
            graph.viewport.isXAxisBoundsManual = true

            graph.viewport.setMinY(squarePlotData.first().x)
            graph.viewport.setMinX(squarePlotData.first().x)
            graph.viewport.setMaxX(squarePlotData.last().x)
            series.shape = PointsGraphSeries.Shape.RECTANGLE
            series.size = 4.5f
            series.color = Color.rgb(215, 0, 36)
            interpolatedPlot.color = Color.GRAY

            graph.addSeries(interpolatedPlot)
            graph.addSeries(series)

        }
    }

    private fun interpolate() : (x:Double) -> Double {
        val valuesList = plottedValues
        val maxSize: Int? = valuesList.keys.max()
        val maxSize2: Double = maxSize?.toDouble() ?: 1.0
        val maxTimeSet: Double = valuesList[maxSize]!!.toDouble()
        val k: Double = maxTimeSet/maxSize2
        return {x:Double -> x*k }
    }


    fun generateArrOnClick (view: View) {
        val generated: Array<Array<Int>> = generateArr()
        val text = parseForTextView(generated)
        arrays = generated
        show(view,text)
    }
    

    fun generateSortedArr (view: View) {
        if (arrays.equals(nullArray)) {
            Toast.makeText(this, "No arrays!", Toast.LENGTH_SHORT).show()
        }
        else {
            val sorted: Array<RadixSort> = Array(arrays.size) { i -> RadixSort(arrays[i]) }
            fun add(sum: MutableMap<Int, Double>, el: RadixSort): MutableMap<Int, Double> {
                sum[el.arr.size] = el.time
                el.end = 0.0
                return sum
            }
            plottedValues = sorted.fold(mutableMapOf(), { sum, el -> add(sum, el) })
            show(view, parseForTextView(sorted))
        }
    }
    
    private fun parseForTextView(sortedArr: Array<RadixSort>) : String {
        var res = ""
        for (i in sortedArr) {
            var text: String = "["
            for (j in i.sorted)
                text+="$j, "
            res+= "${text.dropLast(2)}] time: ${i.time} s\n\n"
        }
        return res
    }

    private fun parseForTextView(array: Array<Array<Int>>) : String {
        var res = ""
        for (i in array) {
            var text: String = "["
            for (j in i)
                text += "$j, "
            res += "${text.dropLast(2)}]\n\n"
        }
        return res
    }
    
    private fun show(view: View, text: String) {
        textView.visibility = TextView.VISIBLE
        textView.setText(text)

        val params: ViewGroup.LayoutParams = textView.layoutParams
        textView.layoutParams = params
    }


    private fun createFile(){
        if (!File("$pathFile/$nameOfFile").exists()){
            val inFile = File("$pathFile/$nameOfFile")
            PrintWriter(inFile).use { out -> out.println(baseFileContent) }
            Toast.makeText(
                this,
                "File was created!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    fun generateFromFile(view: View) {
        try {
            val sdPath = File("$pathFile/$nameOfFile")
            if (!sdPath.exists())
                createFile()
            val stringSdcardPath: String = File(sdPath.absolutePath.toString().plus("/")).toString()
            val file = File(stringSdcardPath)
            try {
                val reader = BufferedReader(FileReader(file))
                var str = ""
                var line: String? = ""
                while (line != null) {
                    line = reader.readLine()
                    str += line.plus("\n")
                }
                textView.setText(str)
                this.arrays = parseInputFromFile(str)

                show(view, parseForTextView(arrays))

                fromFile.setOnClickListener { v -> generateSortedArr(v) }
                plot.setOnClickListener { v -> plotFromFile(v) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } catch (e: FileNotFoundException) {
            Toast.makeText(this, "Ooops... Something went wrong.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun plotFromFile(v: View?) {

        val plottVal = plottedValues
        val minSize: Int = plottVal.keys.min() ?: 1
        val maxSize: Int = plottVal.keys.max() ?: 1

        val function = interpolate()
        val squarePlotData: Array<DataPoint> = (minSize..maxSize step 1).map {
            DataPoint(it.toDouble(), function(it.toDouble())) }.toTypedArray()
        val interpolatedPlot: LineGraphSeries<DataPointInterface> =
            LineGraphSeries(squarePlotData)

        graph.viewport.isYAxisBoundsManual = true
        graph.viewport.setMaxY(squarePlotData.last().y + 0.05)
        graph.viewport.isXAxisBoundsManual = true

        graph.viewport.setMinY(squarePlotData.first().y)
        graph.viewport.setMinX(squarePlotData.first().x)
        graph.viewport.setMaxX(squarePlotData.last().x)

        interpolatedPlot.color = Color.GRAY

        graph.addSeries(interpolatedPlot)

    }
}
