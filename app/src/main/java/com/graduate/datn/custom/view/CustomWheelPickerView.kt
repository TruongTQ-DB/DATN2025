package com.graduate.datn.custom.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.graduate.datn.R
import sh.tyy.wheelpicker.core.BaseWheelPickerView

class CustomWheelViewHolder(itemView: View) :
    BaseWheelPickerView.ViewHolder<CustomWheelPickerView.Item>(itemView) {
    private val textView: TextView = itemView.findViewById(R.id.tv_provinces_picker)
    override fun onBindData(data: CustomWheelPickerView.Item) {
        textView.text = data.text
    }
}

class CustomWheelAdapter :
    BaseWheelPickerView.Adapter<CustomWheelPickerView.Item, CustomWheelViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomWheelViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.custom_wheel_picker_item, parent, false)
        return CustomWheelViewHolder(view)
    }
}

class CustomWheelPickerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseWheelPickerView(context, attrs, defStyleAttr) {
    data class Item(val id: String, val text: String)

    private val highlightView: View = run {
        val view = View(context)
        view.background = ContextCompat.getDrawable(context, R.drawable.custom_wheel_highlight_bg)
        view
    }

    val adapter: CustomWheelAdapter = CustomWheelAdapter()

    init {
        setAdapter(adapter)
        addView(highlightView)
        (highlightView.layoutParams as? LayoutParams)?.apply {
            width = ViewGroup.LayoutParams.MATCH_PARENT
            height =
                context.resources.getDimensionPixelSize(R.dimen.margin_34_dp)
            gravity = Gravity.CENTER_VERTICAL
        }
    }
}