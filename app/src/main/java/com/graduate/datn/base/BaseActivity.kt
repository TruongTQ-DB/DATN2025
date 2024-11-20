package com.graduate.datn.base

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.graduate.datn.R
import com.graduate.datn.network.NetworkCheckerInterceptor
import com.graduate.datn.network.entity.RequestError
import com.graduate.datn.utils.Define
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

abstract class BaseActivity : AppCompatActivity() {

    private var viewController : ViewController? = null

    fun getViewController() : ViewController {
        if(viewController == null){
            viewController = ViewController(layoutId,supportFragmentManager)
        }
        return viewController!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewController = ViewController(layoutId,supportFragmentManager)
        handleFullScreen()
        setContentView(layoutResId)
        initView()
        initData()
        initListener()
    }

    override fun onBackPressed() {
        if(viewController!= null && viewController?.currentFragment!=null){
            if(viewController?.currentFragment?.backPressed() == true){
                super.onBackPressed()
            }
        }else{
            super.onBackPressed()
        }

    }
    open fun handleFullScreen(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
        }
    }

    open fun handleNetworkError(throwable: Throwable?, isShowDialog: Boolean): RequestError? {
        var requestError = RequestError()
        if (throwable is NetworkCheckerInterceptor.NoConnectivityException) {
            requestError.errorCode = (Define.Api.NO_NETWORK_CONNECTION)
            requestError.errorMessage = (throwable.message)
        } else if (throwable is HttpException) {
            val errorBody: String
            try {
                throwable.response()?.errorBody()?.let {
                    errorBody = it.string()
                    val gson = GsonBuilder().create()
                    requestError = gson.fromJson(errorBody, RequestError::class.java)
                } ?: run {
                    requestError.errorCode = (Define.Api.TIME_OUT)
                    requestError.errorMessage = (getString(R.string.error_place_holder))
                }
                //                ApiObjectResponse apiResponse = gson.fromJson(errorBody, ApiObjectResponse.class);
//                if (apiResponse != null && apiResponse.getRequestError() != null) {
//                    requestError = apiResponse.getRequestError();
//                } else {
//                    requestError.errorCode(String.valueOf(httpException.code()));
//                    requestError.errorMessage =(getString(R.string.error_place_holder));
//                }
            } catch (e: IOException) {
                requestError.errorCode = (Define.Api.TIME_OUT)
                requestError.errorMessage = (getString(R.string.error_place_holder))
            } catch (e: IllegalStateException) {
                requestError.errorCode = (Define.Api.TIME_OUT)
                requestError.errorMessage = (getString(R.string.error_place_holder))
            } catch (e: JsonSyntaxException) {
                requestError.errorCode = (Define.Api.TIME_OUT)
                requestError.errorMessage = (getString(R.string.error_place_holder))
            }
        } else if (throwable is ConnectException
            || throwable is SocketTimeoutException
            || throwable is UnknownHostException
            || throwable is IOException
        ) {
            requestError.errorCode = (Define.Api.TIME_OUT)
            requestError.errorMessage = (getString(R.string.timeout_error))
        } else {
            requestError.errorCode = (Define.Api.UNKNOWN)
            requestError.errorMessage = (getString(R.string.error_place_holder))
        }
        if (isShowDialog) {
            requestError.errorMessage?.let {
//                toast("Error Get Api: $it")
            }
        }
        return requestError
    }


    protected abstract val layoutResId :  Int
    protected abstract val layoutId :  Int
    protected abstract fun initView()
    protected abstract fun initData()
    protected abstract fun initListener()

}