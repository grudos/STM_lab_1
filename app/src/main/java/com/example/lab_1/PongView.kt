package com.example.lab_1

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView


class PongView(context: Context?, attr: AttributeSet?) : SurfaceView(context, attr),
    SurfaceHolder.Callback {
    private val pongLogic: PongLogic
    private var lastPlayer1 = 0f
    private var lastPlayer2 = 0f
    private var player1Pointer = -1
    private var player2Pointer = -1

    init {
        val holder = holder
        holder.addCallback(this)
        pongLogic = PongLogic(holder)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        pongLogic.canvasWidth = width
        pongLogic.canvasHeight = height
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        pongLogic.start()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {}

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val pointerCount = event.pointerCount
        for (i in 0 until pointerCount) {
            val pointerId = event.getPointerId(i)
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> handleDownEvent(i, pointerId, event)
                MotionEvent.ACTION_MOVE -> {
                    val pointerCount2 = event.pointerCount
                    handleMoveEvent(i, pointerId, event)
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_CANCEL -> handleUpEvent(
                    i,
                    pointerId,
                    event
                )
            }
        }
        return true
    }

    private fun handleDownEvent(i: Int, pointerId: Int, event: MotionEvent) {
        if (!pongLogic.runBoolean) {
            pongLogic.runBoolean = true
        } else {
            val x = event.getX(i)
            val y = event.getY(i)
            if (x < pongLogic.canvasHeight) {
                player1Pointer = pointerId
                lastPlayer1 = y
            } else if (x >= pongLogic.canvasHeight) {
                player2Pointer = pointerId
                lastPlayer2 = y
            }
        }
    }

    private fun handleMoveEvent(i: Int, pointerId: Int, event: MotionEvent) {
        if (pongLogic.runBoolean) {
            val pointerIndex = event.findPointerIndex(pointerId)
            val x = event.getX(pointerIndex)
            val y = event.getY(pointerIndex)
            if (x < pongLogic.canvasHeight) {
            // movement for player 1
                if (player1Pointer == pointerId) {
                    pongLogic.movePlayer1(y - lastPlayer1)
                    lastPlayer1 = y
                }
            // movement for player 2
            } else if (x >= pongLogic.canvasHeight) {
                if (player2Pointer == pointerId) {
                    pongLogic.movePlayer2(y - lastPlayer2)
                    lastPlayer2 = y
                }
            }
        }
    }

    private fun handleUpEvent(i: Int, pointerId: Int, event: MotionEvent) {
    }
}

