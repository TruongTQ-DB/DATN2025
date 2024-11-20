package com.graduate.datn.custom.dialog

import android.content.Context
import androidx.lifecycle.lifecycleScope
import com.graduate.datn.R
import com.graduate.datn.adapter.recyclerview.AddressItem
import com.graduate.datn.adapter.recyclerview.OptionalServiceItem
import com.graduate.datn.adapter.recyclerview.ServiceItem
import com.graduate.datn.base.custom.BaseFullScreenDialog
import com.graduate.datn.custom.view.CustomWheelPickerView
import com.graduate.datn.entity.User
import com.graduate.datn.extension.visible
import com.graduate.datn.ui.admin.work_schedule.ScheduleTime
import kotlinx.android.synthetic.main.bottom_sheet_while.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sh.tyy.wheelpicker.core.BaseWheelPickerView

class BottomSheetWheelPicker(
    context: Context,
    val isService: Boolean = false,
    val isOptionalService: Boolean = false,
    val isAddress: Boolean = false,
    val isUser: Boolean = false,
    val isScheduleTime: Boolean = false,
    val indexSelected: Int = 0,
) : BaseFullScreenDialog() {
    var index: Int? = -1
    var listData = mutableListOf<Any>()

    override fun initView() {
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        cl_bottom_sheet.visible()
        val screenHeight = resources.displayMetrics.heightPixels
        cl_bottom_sheet.translationY = screenHeight.toFloat()
        cl_bottom_sheet.animate()
            .translationY(0f)
            .setDuration(600)
            .start()
    }

    fun addValues(values: List<String>) {
        custom_picker_view.adapter.values = (values.indices).map {
            CustomWheelPickerView.Item(
                "$it",
                "${values[it]}"
            )
        }
    }

    fun addValueObject(data: List<Any>, position: String? = null) {
        listData.addAll(data)
        setPositionItem(position)
        custom_picker_view.adapter.values = (data.indices).map {
            CustomWheelPickerView.Item(
                "$it",
                "${
                    if (isService) {
                        (data[it] as ServiceItem).data.name
                    } else if (isOptionalService) {
                        (data[it] as OptionalServiceItem).data.name
                    } else if (isAddress) {
                        (data[it] as AddressItem).data?.name
                    } else if (isUser) {
                        (data[it] as User).name
                    } else if (isScheduleTime) {
                        (data[it] as ScheduleTime).stringValue
                    } else {
                        null
                    }
                }"
            )
        }
    }

    private fun setPositionItem(position: String?) {
        if (!position.isNullOrEmpty()) {
            if (isService) {
                val index =
                    listData.indexOfFirst { (it as ServiceItem).id == position }
                if (index > 0) custom_picker_view.selectedIndex = index
            }
            if (isOptionalService) {
                val index =
                    listData.indexOfFirst { (it as OptionalServiceItem).id == position }
                if (index > 0) custom_picker_view.selectedIndex = index
            }
            if (isAddress) {
                val index =
                    listData.indexOfFirst { (it as AddressItem).id == position }
                if (index > 0) custom_picker_view.selectedIndex = index
            }
            if (isUser) {
                val index =
                    listData.indexOfFirst { (it as User).id == position }
                if (index > 0) custom_picker_view.selectedIndex = index
            }
            if (isScheduleTime) {
                val index =
                    listData.indexOfFirst { (it as ScheduleTime).id == position }
                if (index > 0) custom_picker_view.selectedIndex = index
            }
        } else {
            custom_picker_view.selectedIndex = listData.size
        }
    }

    fun setCircularPicker(isChecked: Boolean) {
        custom_picker_view.isCircular = isChecked
    }

    fun setVibrationFeedback(isChecked: Boolean) {
        custom_picker_view.isHapticFeedbackEnabled = isChecked
    }

    override fun initListener() {
        custom_picker_view.setWheelListener(object : BaseWheelPickerView.WheelPickerViewListener {
            override fun didSelectItem(picker: BaseWheelPickerView, index: Int) {
                this@BottomSheetWheelPicker.index = index
//                valueSelected = custom_picker_view.adapter.values.getOrNull(index)?.text.toString()
            }
        })

        tv_ok.setOnClickListener {
            val data =
                if (isService || isOptionalService || isAddress || isUser || isScheduleTime && index != null) {
                    if (index!! >= 0) {
                        listData[index!!]
                    } else {
                        if (listData.isNotEmpty()) {
                            listData[0]
                        } else {
                            null
                        }
                    }
                } else {
                    null
                }
            if (data != null) {
                onclickProvincesPicker(data)
            }
            // open this line t
            hideBottomSheet()
        }

        tv_cancel.setOnClickListener {
            hideBottomSheet()
            onclickCancel
        }
    }

    private fun hideBottomSheet() {
        lifecycleScope.launch {
            delay(500)
            dismissAllowingStateLoss()
        }
        val screenHeight = resources.displayMetrics.heightPixels
        listData.clear()
        cl_bottom_sheet.animate()
            .translationY(screenHeight.toFloat())
            .setDuration(600)
            .start()
    }

    override val layoutId: Int
        get() = R.layout.bottom_sheet_while


    var onclickProvincesPicker: (Any) -> Unit = {}

    var onclickCancel: () -> Unit = {}

}