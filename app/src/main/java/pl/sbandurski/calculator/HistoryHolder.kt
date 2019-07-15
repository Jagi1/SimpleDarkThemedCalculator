package pl.sbandurski.calculator

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.history_item.view.*

class HistoryHolder(
    itemView: View
): RecyclerView.ViewHolder(itemView) {

    fun bindData(expression: String) {
        itemView.history_item_tv.text = expression
    }
}