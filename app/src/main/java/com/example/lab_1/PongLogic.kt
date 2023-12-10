package com.example.lab_1

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.SurfaceHolder
import com.example.lab_1.objects.Ball
import com.example.lab_1.objects.Palette


class PongLogic(private val surface: SurfaceHolder) : Thread() {
    private val player1: Palette
    private val player2: Palette
    private val ball: Ball
    private val layoutPaint: Paint
    private val textPaint: Paint
    var canvasHeight: Int = 0
    var canvasWidth: Int = 0
    var runBoolean = true

    init {
        val paddleHeight = 350
        val paddleWidth = 125
        val ballRadius = 25

        // player 1
        val player1Paint = Paint()
        player1Paint.isAntiAlias = true
        player1Paint.color = Color.WHITE
        player1 = Palette(paddleWidth, paddleHeight, player1Paint)

        // player 2
        val player2Paint = Paint()
        player2Paint.isAntiAlias = true
        player2Paint.color = Color.WHITE
        player2 = Palette(paddleWidth, paddleHeight, player2Paint)

        // ball
        val ballPaint = Paint()
        ballPaint.isAntiAlias = true
        ballPaint.color = Color.WHITE
        ball = Ball(ballRadius.toInt(), ballPaint)

        // layout
        layoutPaint = Paint()
        layoutPaint.isAntiAlias = true
        layoutPaint.color = Color.WHITE
        layoutPaint.style = Paint.Style.STROKE
        layoutPaint.strokeWidth = 2.0f

        // score
        textPaint = Paint()
        textPaint.color = Color.WHITE
        textPaint.style = Paint.Style.FILL
        textPaint.textSize = 100f
        textPaint.textAlign = Paint.Align.CENTER
    }

    private fun drawLayout(canvas: Canvas) {
        canvas.drawColor(Color.BLACK)
        canvas.drawRect(0f, 0f, canvasWidth.toFloat(), canvasHeight.toFloat(), layoutPaint)
        canvas.drawLine(
            (canvasWidth / 2).toFloat(),
            1f,
            (canvasWidth / 2).toFloat(),
            canvasHeight.toFloat(),
            layoutPaint
        )
        canvas.drawText("${player1.score} ${player2.score}", (canvasWidth / 2).toFloat(), 150f, textPaint)
        canvas.drawRoundRect(player1.size, 5f, 5f, player1.paint)
        canvas.drawRoundRect(player2.size, 5f, 5f, player2.paint)
        canvas.drawCircle(ball.cx.toFloat(), ball.cy.toFloat(), ball.r.toFloat(), ball.paint)
    }

    private fun reset() {
        ball.cx = canvasWidth / 2
        ball.cy = canvasHeight / 2
        ball.dx = -10
        ball.dy = 0
        movePlayer(player1, 2f, (canvasHeight - player1.paddleHeight).toFloat() / 2)
        movePlayer(player2, (canvasWidth - player2.paddleWidth - 2).toFloat(), (canvasHeight - player2.paddleHeight).toFloat() / 2)
    }

    override fun run() {
        reset()
        while (runBoolean) {
            var canvas: Canvas? = null
            try {
                canvas = surface.lockCanvas(null)
                if (canvas != null) {
                    updatePlayers()
                    drawLayout(canvas)
                }
            } catch (e: Exception) {
                Log.i("Error", e.message!!)
            } finally {
                if (canvas != null) {
                    surface.unlockCanvasAndPost(canvas)
                }
            }
        }
    }

    private fun movePlayer(player: Palette, left: Float, top: Float) {
        var left = left
        var top = top
        if (left < 2) {
            left = 2f
        } else if (left + player.paddleWidth >= canvasWidth - 2) {
            left = (canvasWidth - player.paddleWidth - 2).toFloat()
        }
        if (top < 0) {
            top = 0f
        } else if (top + player.paddleHeight >= canvasHeight) {
            top = (canvasHeight - player.paddleHeight - 1).toFloat()
        }
        player.size.offsetTo(left, top)
    }

    fun movePlayer1(dy: Float) {
        movePlayer(player1, player1.size.left, player1.size.top + dy)
    }

    fun movePlayer2(dy: Float) {
        movePlayer(player2, player2.size.left, player2.size.top + dy)
    }

    private fun updatePlayers() {
        if (player1.collision > 0) {
            player1.collision--
        }
        if (player2.collision > 0) {
            player2.collision--
        }

        // check collision
        if (collision(player1, ball)) {
            handleCollision(player1, ball)
            player1.collision = 5
        } else if (collision(player2, ball)) {
            handleCollision(player2, ball)
            player2.collision = 5
        } else if (ball.cy <= ball.r
            || ball.cy + ball.r >= canvasHeight - 1
        ) {
            ball.dy = -ball.dy
        } else if (ball.cx + ball.r >= canvasWidth - 1) {
            player1.score++
            reset()
            return
        } else if (ball.cx <= ball.r) {
            player2.score++
            reset()
            return
        }

        // ball
        ball.cx += ball.dx
        ball.cy += ball.dy
        if (ball.cy < ball.r) {
            ball.cy = ball.r
        } else if (ball.cy + ball.r >= canvasHeight) {
            ball.cy = canvasHeight - ball.r - 1
        }
    }

    private fun collision(player: Palette, ball: Ball): Boolean {
        return player.size.intersects(
            (ball.cx - this.ball.r).toFloat(),
            (ball.cy - this.ball.r).toFloat(),
            (ball.cx + this.ball.r).toFloat(),
            (ball.cy + this.ball.r).toFloat()
        )
    }

    private fun handleCollision(player: Palette, ball: Ball) {
        val relative: Float = player.size.top + player.paddleHeight / 2 - ball.cy
        val normalized: Float = relative / (player.paddleHeight / 2)
        val bounceAngle = normalized * 5 * Math.PI / 12
        ball.dx = (-Math.signum(ball.dx.toFloat()) * 10 * Math.cos(bounceAngle)).toInt()
        ball.dy = (10 * -Math.sin(bounceAngle)).toInt()
        if (player === player1) {
            this.ball.cx = (player1.size.right + this.ball.r).toInt()
        } else {
            this.ball.cx = (player2.size.left - this.ball.r).toInt()
        }
    }

}