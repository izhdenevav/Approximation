package com.example.approximationlsmhyperbole

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet
import kotlin.math.*

class MainActivity : AppCompatActivity() {

    private lateinit var mpLineChart: LineChart;
    private lateinit var xArray: Array<Double>;
    private lateinit var yArray: Array<Double>;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        mpLineChart = findViewById<LineChart>(R.id.line_chart);
        xArray = arrayOf(1.01, 1.02, 1.04, 1.13, 2.06, 3.92, 4.73, 4.92, 5.38, 5.45)
        yArray = arrayOf(0.199, 0.198, 0.196, 0.19, 0.14, 0.09, 0.08, 0.078, 0.073, 0.072)

        val lineDataSet1: LineDataSet = LineDataSet(dataValues1(), "Значения")
        lineDataSet1.setColor(0)
        val dataSets1: ArrayList<ILineDataSet> = ArrayList<ILineDataSet>()
        dataSets1.add(lineDataSet1)

        val lineDataSet2: LineDataSet = LineDataSet(dataValues2(), "Аппроксимация")
        lineDataSet1.setColor(10)
        dataSets1.add(lineDataSet2)


        val data: LineData = LineData(dataSets1)
        mpLineChart.setData(data)
        mpLineChart.invalidate()
    }

    fun matrixA(): Array<Array<Double>> {
        var A: Array<Array<Double>> = Array(2, {Array(2, {0.00})})

        var Aoo: Double = 0.00
        var AotORto: Double = 0.00
        var Att: Double = xArray.size.toDouble()


        for (i in xArray.indices) {
            Aoo += 1/(xArray.get(i).pow(2))
            AotORto +=  1/xArray.get(i)
        }

        A[0] = arrayOf(Math.round(Aoo * 1000.0) / 1000.0, Math.round(AotORto * 1000.0) / 1000.0)
        A[1] = arrayOf(Math.round(AotORto * 1000.0) / 1000.0, Math.round(Att * 1000.0) / 1000.0)

        return A
    }

    fun matrixB(): Array<Array<Double>> {
        var B: Array<Array<Double>> = Array(2, {Array(1, {0.00})})

        var Aoo: Double = 0.00
        var Ato: Double = 0.00

        for (i in xArray.indices) {
            Aoo += yArray.get(i)/xArray.get(i)
            Ato += yArray.get(i)
        }

        B[0] = arrayOf(Math.round(Aoo * 1000.0) / 1000.0)
        B[1] = arrayOf(Math.round(Ato * 1000.0) / 1000.0)


        return B
    }

    fun getDetOfMatrix(): Double {
        var det: Double = (matrixA()[0][0] * matrixA()[1][1]) - (matrixA()[0][1] * matrixA()[1][0])

        return Math.round(det * 1000.0) / 1000.0
    }

    fun getAlgDopMatrix(): Array<Array<Double>> {
        var newMatrix: Array<Array<Double>> = Array(2, {Array(2, {0.00})})

        var ADoo: Double = (-1.00).pow(0) * matrixA()[1][1]
        var ADot: Double = (-1.00).pow(1) * matrixA()[1][0]
        var ADto: Double = (-1.00).pow(1) * matrixA()[0][1]
        var ADtt: Double = (-1.00).pow(2) * matrixA()[0][0]


        newMatrix[0] = arrayOf(ADoo, ADot)
        newMatrix[1] = arrayOf(ADto, ADtt)

        return newMatrix
    }

    fun getTransposedMatrix(): Array<Array<Double>> {
        var transposedMatrix: Array<Array<Double>> = Array(2, {Array(2, {0.00})})

        transposedMatrix[0] = arrayOf(getAlgDopMatrix()[0][0], getAlgDopMatrix()[1][0])
        transposedMatrix[1] = arrayOf(getAlgDopMatrix()[0][1], getAlgDopMatrix()[1][1])

        return transposedMatrix
    }

    fun getReverseMatrix(): Array<Array<Double>> {
        var reverseMatrix: Array<Array<Double>> = Array(2, {Array(2, {0.00})})

        reverseMatrix[0] = arrayOf(getTransposedMatrix()[0][0]/getDetOfMatrix(), getTransposedMatrix()[0][1]/getDetOfMatrix())
        reverseMatrix[1] = arrayOf(getTransposedMatrix()[1][0]/getDetOfMatrix(), getTransposedMatrix()[1][1]/getDetOfMatrix())

        return reverseMatrix
    }

    fun getCompositionOfMatrixAandB(): Array<Array<Double>> {
        var Z: Array<Array<Double>> = Array(2, {Array(1, {0.00})})

        val Zoo: Double = getReverseMatrix()[0][0] * matrixB()[0][0] + getReverseMatrix()[0][1] * matrixB()[1][0]
        val Zto: Double = getReverseMatrix()[1][0] * matrixB()[0][0] + getReverseMatrix()[1][1] * matrixB()[1][0]
        Z[0] = arrayOf(Zoo)
        Z[1] = arrayOf(Zto)

        return Z
    }

    fun dataValues1(): ArrayList<Entry>  {
        var dataVals: ArrayList<Entry> = ArrayList<Entry>()

        for (i in yArray.indices) {
            dataVals.add(Entry(xArray[i].toFloat(), yArray[i].toFloat()))
        }

        return dataVals
    }

   fun dataValues2(): ArrayList<Entry>  {
        var dataVals: ArrayList<Entry> = ArrayList<Entry>()

        for (i in yArray.indices) {
            dataVals.add(Entry(xArray[i].toFloat(), (getCompositionOfMatrixAandB()[0][0]/xArray[i] + getCompositionOfMatrixAandB()[1][0]).toFloat()))
        }

        return dataVals
    }
}