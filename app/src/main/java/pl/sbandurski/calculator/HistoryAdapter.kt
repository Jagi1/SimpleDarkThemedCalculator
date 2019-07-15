package pl.sbandurski.calculator

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

class HistoryAdapter(
    val expressions: ArrayList<String>
) : RecyclerView.Adapter<HistoryHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): HistoryHolder =
        HistoryHolder(LayoutInflater.from(p0.context).inflate(R.layout.history_item, p0, false))

    override fun getItemCount(): Int = expressions.size

    override fun onBindViewHolder(p0: HistoryHolder, p1: Int) {
        p0.bindData(expressions[expressions.size-1-p1])
    }
}