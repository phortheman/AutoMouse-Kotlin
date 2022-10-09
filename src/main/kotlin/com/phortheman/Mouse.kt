package com.phortheman

import kotlinx.coroutines.*

import kotlin.random.Random

import java.awt.Dimension
import java.awt.MouseInfo
import java.awt.Toolkit

import java.awt.Point
import java.awt.Robot
import kotlin.math.roundToInt

class Mouse {

    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private var isActive = false
    private val robot = Robot()
    private var jitterJob: Job? = null
    private val screenSize = Toolkit.getDefaultToolkit().screenSize

    private val duration = 5000L // Eventually will be set in the settings
    private val minMoveDuration = 100 // 100 milliseconds minimum



    // Jitter function called by the button
    fun jitterMouse() {
        if( isActive ) return // Do nothing if the button was already pressed

        this@Mouse.isActive = true
        jitterJob = coroutineScope.launch {
            while( this@Mouse.isActive ) {
                val endPoint = getRandPoint( screenSize )
                val mouseSteps = calculateMousePointsLinear( endPoint )
                moveMouseAlongPoints( mouseSteps )
                println( "The mouse was moved to-> X: ${endPoint.x} Y: ${endPoint.y} ")
                delay(duration)
            }
        }
    }

    // Function called to stop the jitter job
    fun stop(): Unit {
        isActive = false
        jitterJob?.cancel()
        println( "Stopped!" )
    }

    // Repeatedly click TODO
    fun repeatedlyClick( rep: Int = 0 ) {}

    // Get a random point on the screen given the dimensions
    private fun getRandPoint( dim: Dimension ): Point {
        return Point( Random.nextInt( dim.width ), Random.nextInt( dim.height ) )
    }

    // Calculate the points the mouse needs to move in a linear fashion
    private fun calculateMousePointsLinear(finalPosition: Point ): ArrayList<Point> {
        val returnList: ArrayList<Point> = ArrayList<Point>()
        val startPosition: Point = MouseInfo.getPointerInfo().location

        if( duration < minMoveDuration )
        {
            returnList.add( finalPosition )
            return returnList
        }
        val steps = maxOf(screenSize.width, screenSize.height)

        for( n in 1 until steps ) {
            returnList.add( getPointOnLine(startPosition, finalPosition,  n.toDouble() / steps ) )
        }
        returnList.add( finalPosition )

        return returnList
    }

    // Takes an array of points that the mouse needs to move to
    private fun moveMouseAlongPoints(points: ArrayList<Point> ) {
        for ( p in points ) {
            robot.mouseMove(p.x, p.y)
        }
    }

    // Returns a point along the line between the start and end points at n
    private fun getPointOnLine(startPoint: Point, endPoint: Point, n: Double ): Point {
        val x = (( ( endPoint.x - startPoint.x ) * n ) + startPoint.x ).roundToInt()
        val y = (( ( endPoint.y - startPoint.y ) * n ) + startPoint.y ).roundToInt()
        return Point( x, y )
    }

}