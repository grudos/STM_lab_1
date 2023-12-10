package com.example.lab_1.objects

import android.graphics.Paint
import android.graphics.RectF


class Palette(var paddleWidth: Int, var paddleHeight: Int, paint: Paint) {
    var size: RectF
    var paint: Paint
    var score: Int
    var collision: Int

    init {
        size = RectF(0f, 0f, paddleWidth.toFloat(), paddleHeight.toFloat())
        this.paint = paint
        score = 0
        collision = 0
    }
}