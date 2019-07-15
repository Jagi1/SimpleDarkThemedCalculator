package pl.sbandurski.calculator

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.constraint.ConstraintSet
import android.support.v7.widget.LinearLayoutManager
import android.transition.TransitionManager
import android.view.View
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var expressionList: ArrayList<String> = ArrayList()
    private var calculated = false
    private var newOperation = false
    private lateinit var oldLayout: ConstraintSet
    private lateinit var buttons: ArrayList<View>
    private lateinit var historyAdapter: HistoryAdapter
    private var historyExpressions: ArrayList<String> = ArrayList()
    private lateinit var vibrator: Vibrator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        oldLayout = ConstraintSet()
        oldLayout.clone(main_cl)
        buttons = arrayListOf(
            btn_ac, btn_plus_minus, btn_percent, btn_divide, btn_7, btn_8, btn_9, btn_multiply,
            btn_4, btn_5, btn_6, btn_substraction, btn_1, btn_2, btn_3, btn_addition,
            btn_0, btn_history, btn_dot, btn_equals
        )
        historyAdapter = HistoryAdapter(historyExpressions)
        main_cv_rv.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        main_cv_rv.adapter = historyAdapter
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    fun deleteExpression(view: View) =
        main_tv.setText("0").also {
            main_tv1.text = ""
        }.also { expressionList.clear() }.also {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    20,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        }

    fun changeSign(view: View) {
        vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
        var value = main_tv.text.toString()
        if (value.startsWith("-")) value.drop(1)
        else if (value != "0") value = "-$value"
        main_tv.text = value
    }

    /**
     * Add signs: 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, ..
     * */
    fun addSign(view: View) {
        // Prevent adding too long numbers
        if (main_tv.text.toString().lastIndex >= 7 && !newOperation) return
        vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
        if (calculated) {
            calculated = false
            if (view.tag.toString() == ".") main_tv.text = "0."
            else main_tv.text = view.tag.toString()
            main_tv1.text = ""
            expressionList.clear()
        } else if (newOperation) {
            if (view.tag.toString() == ".") main_tv.text = "0."
            else main_tv.text = view.tag.toString()
            newOperation = false
        } else {
            // Prevent adding multiple dots
            if (main_tv.text.toString().endsWith(".") && view.tag.toString() == ".") return
            else if (main_tv.text.toString() == "0" && view.tag.toString() != ".") main_tv.text =
                view.tag.toString()
            else main_tv.append(view.tag.toString())
        }
    }

    /**
     * Handles operations: +, -, *, /.
     * */
    fun addOperation(view: View) {
        vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
        if (calculated) {
            calculated = false
            expressionList.clear()
            main_tv1.text = ""
        }
        if (main_tv.text.toString().endsWith(".")) main_tv.text.toString().dropLast(1)
        if (main_tv.text.toString().startsWith("-")) main_tv1.append("(${main_tv.text})")
        else main_tv1.append(main_tv.text.toString())
        main_tv1.append(view.tag.toString())
        expressionList.add(main_tv.text.toString())
        expressionList.add(view.tag.toString())
        newOperation = true
    }

    fun calculate(view: View) {
        vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
        if (!main_tv1.text.toString().isEmpty()) {
            if (main_tv.text.toString().endsWith(".")) main_tv.text.toString().dropLast(1)
            if (main_tv.text.toString().startsWith("-")) main_tv1.append("(${main_tv.text})")
            else main_tv1.append(main_tv.text.toString())
            expressionList.add(main_tv.text.toString())
            val historyExpression = main_tv1.text.toString()
            var position: Int
            while (true) {
                position = expressionList.findFirstMulOrDiv()
                if (position == -1) break
                val newValue = when (expressionList[position]) {
                    "*" -> expressionList[position - 1].toDouble().times(expressionList[position + 1].toDouble())
                    else -> expressionList[position - 1].toDouble().div(expressionList[position + 1].toDouble())
                }
                expressionList[position] = newValue.toString()
                expressionList.removeAt(position + 1)
                expressionList.removeAt(position - 1)
            }
            while (true) {
                position = expressionList.findFirstAddOrSub()
                if (position == -1) break
                val newValue = when (expressionList[position]) {
                    "+" -> expressionList[position - 1].toDouble().plus(expressionList[position + 1].toDouble())
                    else -> expressionList[position - 1].toDouble().minus(expressionList[position + 1].toDouble())
                }
                expressionList[position] = newValue.toString()
                expressionList.removeAt(position + 1)
                expressionList.removeAt(position - 1)
            }
            main_tv.text = expressionList.first()
            calculated = true
            historyAdapter.expressions.add("$historyExpression=${expressionList.first()}")
            historyAdapter.notifyDataSetChanged()
        }
    }

    fun showHistory(view: View) {
        vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
        val constraintSet = ConstraintSet()
        constraintSet.clone(oldLayout)
        constraintSet.connect(R.id.main_cv, ConstraintSet.TOP, R.id.main_tl, ConstraintSet.TOP)
        TransitionManager.beginDelayedTransition(main_cl)
        constraintSet.applyTo(main_cl)
        buttons.forEach { component ->
            component.isClickable = false
            component.isFocusable = false
        }
    }

    fun closeHistory(view: View) {
        vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
        TransitionManager.beginDelayedTransition(main_cl)
        oldLayout.applyTo(main_cl)
        buttons.forEach { component ->
            component.isClickable = true
            component.isFocusable = true
        }
    }

    fun clearHistory(view: View) {
        vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
        historyAdapter.expressions.clear()
        historyAdapter.notifyDataSetChanged()
        closeHistory(view)
    }

    private fun ArrayList<String>.findFirstMulOrDiv(): Int {
        forEach { string ->
            if (string == "*" || string == "/") return this.indexOf(string)
        }
        return -1
    }

    private fun ArrayList<String>.findFirstAddOrSub(): Int {
        forEach { string ->
            if (string == "+" || string == "-") return this.indexOf(string)
        }
        return -1
    }
}
