package com.graduate.datn.extension

import android.text.Editable

class TextWatcherWrapper {
    internal var after: ((Editable) -> Unit)? = null
    internal var before: ((CharSequence, Int, Int, Int) -> Unit)? = null
    internal var on: ((CharSequence, Int, Int, Int) -> Unit)? = null

    fun after(block: (Editable) -> Unit) {
        after = block
    }

    fun before(block: (s: CharSequence, start: Int, count: Int, after: Int) -> Unit) {
        before = block
    }

    fun on(block: (s: CharSequence, start: Int, before: Int, count: Int) -> Unit) {
        on = block
    }
}

class OnPageChangeListenerWrapper {
    internal var onPageScrolled: ((Int, Float, Int) -> Unit)? = null
    internal var onPageSelected: ((Int) -> Unit)? = null
    internal var onPageScrollStateChanged: ((Int) -> Unit)? = null

    fun onPageScrolled(block: (position: Int, positionOffset: Float, positionOffsetPixels: Int) -> Unit) {
        onPageScrolled = block
    }

    fun onPageSelected(block: (Int) -> Unit) {
        onPageSelected = block
    }

    fun onPageScrollStateChanged(block: (Int) -> Unit) {
        onPageScrollStateChanged = block
    }
}