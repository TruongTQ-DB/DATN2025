package com.graduate.datn.custom.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Window
import com.graduate.datn.R
import com.graduate.datn.extension.*
import kotlinx.android.synthetic.main.dialog_cancel_schedule.*


class CancelScheduleDialog(context: Context) : Dialog(context) {

    var onClickBtnYes: (String) -> Unit = {}
    private var reason = ""

    init {
        setOnDismissListener {
            rg_reason.clearCheck()
            this.dismiss()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_cancel_schedule)
        window?.setBackgroundDrawableResource(R.drawable.bg_border_info)

        cb_other_reason.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                edt_other_reason.visible()
            } else {
                edt_other_reason.gone()
            }
        }

        rg_reason.setOnCheckedChangeListener { buttonView, isChecked ->
            if (rg_reason.checkedRadioButtonId != -1) {
                reason = when {
                    cb_no_test.isChecked -> cb_no_test.text.toString()
                    cb_change_doctor.isChecked -> cb_change_doctor.text.toString()
                    else -> cb_change_time.text.toString()
                }
                btn_yes.setBackgroundResource(R.drawable.bg_add_address )
                btn_yes.isEnabled = true
            } else {
                btn_yes.setBackgroundResource(R.drawable.bg_cancel_schedule_unclicked)
                btn_yes.isEnabled = false
            }
            if (cb_other_reason.isChecked) {
                if (edt_other_reason.text.toString().isEmpty()) {
                    btn_yes.isEnabled = false
                    btn_yes.setBackgroundResource(R.drawable.bg_cancel_schedule_unclicked)
                } else {
                    btn_yes.isEnabled = true
                    btn_yes.setBackgroundResource(R.drawable.bg_add_address)
                }
                edt_other_reason.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {}
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        if (s.isNullOrEmpty()) {
                            btn_yes.isEnabled = false
                            btn_yes.setBackgroundResource(R.drawable.bg_cancel_schedule_unclicked)
                        } else {
                            btn_yes.isEnabled = true
                            reason = s.toString()
                            btn_yes.setBackgroundResource(R.drawable.bg_add_address)
                        }

                    }
                })
            }
        }

        btn_yes.setOnSafeClickListener {
            if(cb_other_reason.isChecked && edt_other_reason.text.isNullOrBlank()){
                context.toast("Vui lòng nhập lý do cảu bạn!")
                return@setOnSafeClickListener
            }else if(cb_other_reason.isChecked && !edt_other_reason.text.isNullOrBlank()){
                reason = edt_other_reason.text.toString()
            }
            onClickBtnYes(reason)
            rg_reason.clearCheck()
            edt_other_reason.apply {
                gone()
                text = null
            }
            this.dismiss()
        }
    }
}