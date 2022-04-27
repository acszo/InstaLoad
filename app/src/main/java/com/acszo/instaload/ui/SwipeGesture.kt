package com.acszo.instaload.ui

import android.content.Context
import android.graphics.*
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import android.graphics.drawable.GradientDrawable
import com.acszo.instaload.R

abstract class SwipeGesture(context: Context): ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    private val scale = context.resources.displayMetrics.density
    private val cornerRadius = 26.0f
    private val textSize = 20f
    private val textDelete = context.getString(R.string.delete)
    private val textShare = context.getString(R.string.share)

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        val itemView = viewHolder.itemView
        val background = GradientDrawable()
        val paint = Paint()
        val bounds = Rect()
        background.cornerRadius = dpToPx(cornerRadius).toFloat()
        background.setBounds(itemView.left, itemView.top, itemView.right, itemView.bottom)
        paint.color = Color.WHITE
        paint.textSize = dpToPx(textSize).toFloat()
        paint.isAntiAlias = true
        if (dX < 0) {
            background.setColor(Color.parseColor("#D2042D"))
            background.draw(c)
            paint.getTextBounds(textDelete, 0, textDelete.length, bounds)
            c.drawText(
                textDelete,
                (itemView.right - bounds.width() - dpToPx(cornerRadius).toFloat()),
                itemView.top + (itemView.height / 2) + (paint.textSize / 4),
                paint
            )
        } else {
            background.setColor(Color.parseColor("#0095F6"))
            background.draw(c)
            paint.getTextBounds(textShare, 0, textShare.length, bounds)
            c.drawText(
                textShare,
                (itemView.left + dpToPx(cornerRadius).toFloat()),
                itemView.top + (itemView.height / 2) + (paint.textSize / 4),
                paint
            )
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun dpToPx(size: Float): Int {
        return (size * scale + 0.5f).toInt()
    }

}