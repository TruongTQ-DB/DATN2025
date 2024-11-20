package com.graduate.datn.base

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.graduate.datn.base.custom.HSBALoadingDialog
import com.graduate.datn.base.entity.BaseError
import com.graduate.datn.base.entity.BaseListLoadMoreResponse
import com.graduate.datn.base.entity.BaseListResponse
import com.graduate.datn.base.entity.BaseObjectResponse
import com.graduate.datn.utils.Define

abstract class BaseFragment : Fragment() {
    private var viewController: ViewController? = null
    protected var mSavedInstanceState: Bundle? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return LayoutInflater.from(context).inflate(layoutId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mSavedInstanceState = savedInstanceState
        initView()
        initData()
        initListener()
    }

    abstract fun backFromAddFragment()

    //
    @get: LayoutRes
    protected abstract val layoutId: Int
    protected abstract fun initView()
    protected abstract fun initData()
    protected abstract fun initListener()
    abstract fun backPressed(): Boolean

    fun setVC(viewController: ViewController) {
        this.viewController = viewController
    }

    fun getVC(): ViewController {
        if (viewController == null) {
            viewController = (activity as BaseActivity).getViewController()
        }

        return viewController!!
    }

    fun hintKeyBoard() {
        val inputMethodManager =
            context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    protected open fun handleListResponse(response: BaseListResponse<*>) {
        when (response.type) {
            Define.ResponseStatus.LOADING -> showLoading()
            Define.ResponseStatus.SUCCESS -> {
                hideLoading()
                getListResponse(response.data)
            }
            Define.ResponseStatus.ERROR -> {
                hideLoading()
                if (response.isShowingError) {
                    handleNetworkError(response.error, true)
                } else {
                    handleValidateError(response.error as? BaseError?)
                }
            }
        }
    }

    protected open fun <U> handleObjectResponse(response: BaseObjectResponse<U>) {
        when (response.type) {
            Define.ResponseStatus.LOADING -> showLoading()
            Define.ResponseStatus.SUCCESS -> {
                hideLoading()
                getObjectResponse(response.data)
            }
            Define.ResponseStatus.ERROR -> {
                hideLoading()
                if (response.isShowingError) {
                    Log.d("Log_msg", response.msg.toString())
                    handleNetworkError(response.error, true)
                } else {
                    handleValidateError(response.error as? BaseError?)
                }
            }
        }
    }

    protected open fun handleLoadMoreResponse(response: BaseListLoadMoreResponse<*>) {
        when (response.type) {
            Define.ResponseStatus.LOADING -> showLoading()
            Define.ResponseStatus.SUCCESS -> {
                getListResponse(response.data, response.isRefresh, response.isLoadmore)
                hideLoading()
            }
            Define.ResponseStatus.ERROR -> {
                hideLoading()
                if (response.isShowingError) {
                    handleNetworkError(response.error, true)
                } else {
                    if (response.error is BaseError) {
                        handleValidateError(response.error)
                    }
                }
            }
        }
    }

    open fun <U> getObjectResponse(data: U) {

    }

    open fun <U> getListResponse(data: List<U>?) {

    }

    protected open fun getListResponse(data: List<*>?, isRefresh: Boolean, canLoadmore: Boolean) {}

    protected fun showLoading() {
        HSBALoadingDialog.getInstance(requireContext()).show()
    }

    protected fun hideLoading() {
        HSBALoadingDialog.getInstance(requireContext()).hidden()
    }

    protected open fun handleNetworkError(throwable: Throwable?, isShowDialog: Boolean) {
        if (activity != null && activity is BaseActivity) {
            (activity as BaseActivity?)?.handleNetworkError(
                throwable,
                isShowDialog
            )
        }
    }

    protected open fun handleValidateError(throwable: BaseError?) {}

}