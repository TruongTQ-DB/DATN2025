package com.graduate.datn.custom.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.text.Html
import android.text.InputFilter
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.graduate.datn.R
import com.graduate.datn.extension.*
import kotlinx.android.synthetic.main.view_input.view.*


class InputView(context: Context, attrs: AttributeSet?) :
    CustomViewConstraintLayout(context, attrs) {

    private var isShow = true

    var onClickBirthday: () -> Unit = {}

    companion object {
        const val TYPE_PASSWORD = 1
        const val TYPE_NORMAL = 2
        const val TYPE_REGISTER_PASSWORD = 3
        const val TYPE_REGISTER_CONFIRM_PASSWORD = 4
        const val TYPE_BIRTHDAY = 5
        const val TYPE_LOGIN = 6
        const val TYPE_REGISTER = 7
        const val TYPE_PHONE = 8
        const val TYPE_SEARCH = 9
        const val TYPE_CHANGE_PASSWORD = 14
    }

    override fun getLayoutRes(): Int {
        return R.layout.view_input
    }

    override fun getStyableRes(): IntArray? {
        return R.styleable.InputView
    }

    override fun initView() {

    }

    override fun initListener() {

    }

    override fun initData() {

    }

    @SuppressLint("ResourceAsColor")
    override fun initDataFromStyable(attr: TypedArray?) {
        if (attr != null) {
            try {
                if (attr.hasValue(R.styleable.InputView_set_focusable)) {
                    if(attr.getBoolean(R.styleable.InputView_set_enabled, true)){
                        edt_input.isFocusable = false
                        edt_input.isFocusableInTouchMode = false
                    }
                }
                if (attr.hasValue(R.styleable.InputView_set_padding_end)) {
                    val pading = attr.getInt(R.styleable.InputView_set_padding_end, 0)
                    edt_input.setPadding(0, 0, pading.dpToPx, 0)
                }
                if (attr.hasValue(R.styleable.InputView_set_text_font)) {
                    val font = attr.getFont(R.styleable.InputView_set_text_font)
                    edt_input.typeface = font
                }
                if (attr.hasValue(R.styleable.InputView_set_text_size)) {
                    val number = attr.getInt(R.styleable.InputView_set_text_size, 0)
                    edt_input.textSize = number.toFloat()
                }
                if (attr.hasValue(R.styleable.InputView_max_length)) {
                    val number = attr.getInt(R.styleable.InputView_max_length, 0)
                    edt_input.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(number))
                }
                if (attr.hasValue(R.styleable.InputView_set_enabled)) {
                    val enabled = attr.getBoolean(R.styleable.InputView_set_enabled, true)
                    edt_input.isEnabled = enabled
                }
                if (attr.hasValue(R.styleable.InputView_background_input_view)) {
                    layout_input.background = attr.getDrawable(R.styleable.InputView_background_input_view)
                }
                if (attr.hasValue(R.styleable.InputView_color_hint)) {
                    edt_input.setHintTextColor(attr.getColor(R.styleable.InputView_color_hint, R.color.color_text_hint))
                }
                if (attr.hasValue(R.styleable.InputView_margin_start)) {
                    bg_icon.gone()
                    val marginStart= attr.getInt(R.styleable.InputView_margin_start, 0)
                    edt_input.setPaddingRelative(marginStart.dpToPx,0,0,0)
                }
                if (attr.hasValue(R.styleable.InputView_input_hint)) {
                    edt_input.hint = attr.getString(R.styleable.InputView_input_hint)
                }
                if (attr.hasValue(R.styleable.InputView_disable_paste)) {
                    if (attr.getBoolean(R.styleable.InputView_disable_paste, false)) {
                        edt_input.disablePaste()
                    }
                }
                if (attr.hasValue(R.styleable.InputView_left_drawable)) {
                    bg_icon.visible()
                    ic_left.setImageResource(
                        attr.getResourceId(
                            R.styleable.InputView_left_drawable,
                            0
                        )
                    )
                }
                if (attr.hasValue(R.styleable.InputView_input_type)) {
                    when (attr.getInt(R.styleable.InputView_input_type, 0)) {
                        TYPE_NORMAL -> {
                            edt_input.inputType = InputType.TYPE_CLASS_TEXT
                            val type = ResourcesCompat.getFont(context,R.font.roboto_medium)
                            edt_input.typeface = type
                            edt_input.textSize = 16.toFloat()
                        }
                        TYPE_PASSWORD -> {
                            ic_show_hide_password.visible()
                            ic_show_hide_password.setOnClickListener {
                                showPassword(isShow)
                                isShow = !isShow
                            }
                            edt_input.passwordType()
                        }
                        TYPE_REGISTER_PASSWORD -> {
                            ic_show_hide_password.visible()
                            ic_show_hide_password.setOnClickListener {
                                showPassword(isShow)
                                isShow = !isShow
                            }
                            edt_input.hint =
                                Html.fromHtml(resources.getString(R.string.register_password_hint))
                            edt_input.passwordType()
                        }
                        TYPE_REGISTER_CONFIRM_PASSWORD -> {
                            ic_show_hide_password.visible()
                            ic_show_hide_password.setOnClickListener {
                                showPassword(isShow)
                                isShow = !isShow
                            }
                            edt_input.passwordType()
                        }
                        TYPE_BIRTHDAY -> {
                            edt_input.inputType = InputType.TYPE_CLASS_TEXT
                            edt_input.isFocusable = false
                            edt_input.onAvoidDoubleClick {
                                onClickBirthday()
                            }
                        }
                        TYPE_PHONE -> {
                            edt_input.filters = arrayOf(InputFilter.LengthFilter(10))
                            edt_input.inputType = InputType.TYPE_CLASS_PHONE
                        }
                        TYPE_SEARCH -> {
                            edt_input.inputType = InputType.TYPE_CLASS_TEXT
                            bg_icon.setPadding(
                                12.dpToPx, 8.dpToPx, 8.dpToPx, 4.dpToPx
                            )
                            edt_input.setPadding(0, 0, 38.dpToPx, 0)
                            edt_input.imeOptions = EditorInfo.IME_ACTION_SEARCH
                            edt_input.textSize = 14.toFloat()
                            val type = ResourcesCompat.getFont(context,R.font.roboto_medium)
                            edt_input.typeface = type
                        }
                    }
                }
                if (attr.hasValue(R.styleable.InputView_type)) {
                    when (attr.getInt(R.styleable.InputView_type, 0)) {
                        TYPE_LOGIN -> {
//                            bg_icon.background =
//                                ContextCompat.getDrawable(context, R.drawable.bg_icon_edt_login)
                        }
                        TYPE_REGISTER -> {
                            bg_icon.setBackgroundColor(Color.TRANSPARENT)
                        }
                    }
                }
                if (attr.hasValue(R.styleable.InputView_padding_vertical)) {
                    val paddingVertical = attr.getInt(R.styleable.InputView_padding_vertical, 0)
                    if (paddingVertical > 0) {
                        bg_icon.setPadding(
                            18.dpToPx,
                            paddingVertical.dpToPx,
                            13.dpToPx,
                            paddingVertical.dpToPx
                        )
                    }
                }
            } finally {
                attr.recycle()
            }

            edt_input.setOnEditorActionListener { textView, actionId, keyEvent ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    edt_input.hideKeyboard()
                }
                true
            }
        }
    }

    fun setText(text: String?) {
        edt_input.setText(text)
    }

    fun getText() = edt_input.text?.trim().toString()

    fun getTextNotTrim() = edt_input.text.toString()

    private fun showPassword(isShow: Boolean) {
        if (isShow) {
            ic_show_hide_password.setImageResource(R.drawable.ic_hide_password_login)
            edt_input.inputType = InputType.TYPE_CLASS_TEXT
            val type = ResourcesCompat.getFont(context,R.font.roboto_medium)
            edt_input.typeface = type
        } else {
            ic_show_hide_password.setImageResource(R.drawable.ic_show_password_login)
            edt_input.inputType =
                InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
            val type = ResourcesCompat.getFont(context,R.font.roboto_medium)
            edt_input.typeface = type
        }
    }

    fun textChangedListener(action: (text: String) -> Unit) {
        edt_input.textChangedListener {
            after {
                action(it.toString().trim())
            }
        }
    }

    fun setOnEditorActionListener(action: (text: String) -> Unit) {
        edt_input.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                action(edt_input.text.toString().trim())
                return@OnEditorActionListener true
            }
            false
        })
    }

    fun setPaddingHorizontal(padding: Float) {
        edt_input.setPadding(padding.toInt(), 0, padding.toInt(), 0)
    }

    fun setPaddingContent(
        left: Float = 0F,
        top: Float = 0F,
        right: Float = 0F,
        bottom: Float = 0F
    ) {
        edt_input.setPadding(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
    }

    fun hideKeyboard() {
        edt_input.hideKeyboard()
    }

    fun showKeyboard() {
        edt_input.showKeyboard()
    }

    fun setMaxLength(maxLength: Int = 100) {
        edt_input.filters = arrayOf(InputFilter.LengthFilter(maxLength))
    }

    fun showButtonRight(status: Boolean) {
        if (status)
            ic_show_hide_password.visible()
        else
            ic_show_hide_password.gone()
    }

    fun setBackgroundButtonRight(id: Int) {
        ic_show_hide_password.setImageResource(id)
    }

    fun rightIconClick(onClick: (View) -> Unit) {
        ic_show_hide_password.onAvoidDoubleClick {
            onClick(it)
        }
    }

    fun setBackgroundButtonLeft(id: Int) {
        bg_icon.background = ContextCompat.getDrawable(context, id)
    }

    fun setBackgroundLayoutInput(id: Int) {
        layout_input.background = ContextCompat.getDrawable(context, id)
    }

    fun setTextHide(text: String){
        edt_input.hint = text
    }

    fun restrictInputOnlyNumber(){
        edt_input.keyListener = DigitsKeyListener.getInstance("0123456789")
    }
    fun restrictInputForInsuranceHealth(){
        edt_input.keyListener = DigitsKeyListener.getInstance("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ")
    }

    fun enableInput(enable: Boolean = true) {
        edt_input.isEnabled = enable
    }

    fun textFocusListener(action: (focus: Boolean) -> Unit) {
        edt_input.setOnFocusChangeListener{ _, hasFocus ->
            action(hasFocus)
        }
    }

    fun setSelectionEnd(){
        edt_input.setSelection(edt_input.length())
    }

    fun setTextSize(size: Int = 16) {
        edt_input.textSize = size.toFloat()
    }

    fun setUnFocusable() {
        edt_input.isFocusable = false
    }
}