package com.arman.assignment3;

import GraphViewModel
import android.app.Activity
import android.app.Application
import android.graphics.Typeface
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.*
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.LineType
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import com.arman.assignment3.ui.theme.Assignment3Theme
import com.arman.assignment3.ui.theme.Colors


public class GraphActivity : ComponentActivity() {
    private lateinit var viewModel: GraphViewModel;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val policy = ThreadPolicy.Builder().permitAll().build()
        setThreadPolicy(policy)


        setContent {
            Assignment3Theme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier
                        .fillMaxSize()
                        .background(Colors.zinc900)
                        .padding(top = 64.dp, bottom = 18.dp, start = 16.dp, end = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ){

                        DynamicGraphs()
                    }
                }
            }
        }
    }

}


@Composable
fun DynamicGraphs() {
    // persistent list of roll, pitch, yaws
    val pointsDataRoll = remember { mutableListOf<Point>() }
    val pointsDataPitch = remember { mutableListOf<Point>() }
    val pointsDataYaw = remember { mutableListOf<Point>() }

    val viewModel = viewModel<SensorViewModel>();
    val orientation by viewModel.orientationAngles.collectAsState();

    val roll = orientation.roll;
    val pitch = orientation.pitch;
    val yaw = orientation.yaw;

    // add the new roll, pitch, yaw to the list
    pointsDataRoll.add(Point(pointsDataRoll.size.toFloat(), roll));
    pointsDataPitch.add(Point(pointsDataPitch.size.toFloat(), pitch));
    pointsDataYaw.add(Point(pointsDataYaw.size.toFloat(), yaw));

    // keep the list size to 10
    if (pointsDataRoll.size > 30) {
        pointsDataRoll.removeAt(0)
        pointsDataPitch.removeAt(0)
        pointsDataYaw.removeAt(0)
    }


    // display the line charts

    Column(modifier = Modifier.padding(top = 20.dp)) {
        MyLinechart(pointsDataRoll)
        Spacer(modifier = Modifier.height(20.dp))
        MyLinechart(pointsDataPitch)
        Spacer(modifier = Modifier.height(20.dp))
        MyLinechart(pointsDataYaw)
        Spacer(modifier = Modifier.height(20.dp))
    }



}

@Composable
private fun MyLinechart(pointsData: List<Point>) {
    val xAxisData = AxisData.Builder()
        .axisStepSize(40.dp)
        .steps(pointsData.size - 1)
        .labelData { i -> (0 + i).toString() }
        .axisLabelAngle(20f)
        .labelAndAxisLinePadding(15.dp)
        .axisLabelColor(Color.Green)
        .axisLineColor(Color.DarkGray)
        .typeFace(Typeface.DEFAULT_BOLD)
        .build()
    val yAxisData = AxisData.Builder()
        .steps(30)
        .labelData { i -> "$i" }
        .labelAndAxisLinePadding(30.dp)
        .axisLabelColor(Color.Blue)
        .axisLineColor(Color.DarkGray)
        .typeFace(Typeface.DEFAULT_BOLD)
        .build()
    val data = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = pointsData,
                    lineStyle = LineStyle(lineType = LineType.Straight(), color = Color.Blue),
                    intersectionPoint = IntersectionPoint(color = Color.Red),
                    selectionHighlightPopUp = SelectionHighlightPopUp(popUpLabel = { x, y ->
                        val xLabel = "x : ${(1900 + x).toInt()} "
                        val yLabel = "y : ${String.format("%.2f", y)}"
                        "$xLabel $yLabel"
                    })
                )
            )
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData
    )
    LineChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        lineChartData = data
    )
}
