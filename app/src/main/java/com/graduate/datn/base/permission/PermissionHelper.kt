package com.graduate.datn.base.permission

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import android.provider.Settings;

class PermissionHelper {
    private val PERMISSION_REQUEST_CODE = 98
    private var activity: Activity? = null
    private var fragment: Fragment? = null
    private lateinit var permissions: ArrayList<String>
    private var successListener: Runnable? = null
    private var deniedListener: Runnable? = null
    private var neverAskAgainListener: Runnable? = null
    private var dialogBeforeRunBuilder: AlertDialog.Builder? = null
    private var dialogBeforeAskPositiveButton = 0
    private var dialogBeforeAskPositiveButtonColor = DIALOG_WITHOUT_CUSTOM_COLOR

    companion object {
        const val DIALOG_WITHOUT_CUSTOM_COLOR = 0
    }


    /**
     * Default activity constructor
     *
     * @param activity is activity instance. Use it only in activities. Don't use in fragments!
     */
    fun withActivity(activity: Activity?): PermissionHelper  {
        this.activity = activity
        return this
    }


    /**
     * Default fragment constructor
     *
     * @param fragment is fragment instance. Use it only in fragments
     */
    fun withFragment(fragment: Fragment?) : PermissionHelper {
        this.fragment = fragment
        return this
    }


    /**
     * @param permission is single permission, which you want to ask
     * @return current object
     */
    fun check(permission: String): PermissionHelper {
        permissions = arrayListOf()
        permissions.add(permission)
        return this
    }


    /**
     * @param permissions is array of permissions, which you want to ask
     * @return current object
     */
    fun check(vararg permissions: String): PermissionHelper {
        this.permissions = arrayListOf()
        this.permissions.addAll(permissions)
        return this
    }


    /**
     * Setup success callback
     *
     * @param listener called when user deny permission
     * @return current object
     */
    fun onSuccess(listener: Runnable?): PermissionHelper {
        successListener = listener
        return this
    }

    /**
     * Setup denied callback
     *
     * @param listener called when user deny permission
     * @return current object
     */
    fun onDenied(listener: Runnable?): PermissionHelper {
        deniedListener = listener
        return this
    }


    @Deprecated("replaced by  {@link #onDenied(Runnable)} ()")
    fun onFailure(listener: Runnable?): PermissionHelper {
        deniedListener = listener
        return this
    }

    /**
     * This method setup never ask again callback
     *
     * @param listener called when permission in status "never ask again"
     * @return current object
     */
    fun onNeverAskAgain(listener: Runnable?): PermissionHelper {
        neverAskAgainListener = listener
        return this
    }


    /**
     * This method setup custom dialog before permissions will be asked.
     * Dialog will be shown only if permissions not granted.
     *
     * @param titleRes          dialog title string resource
     * @param messageRes        dialog message string resource
     * @param positiveButtonRes dialog positive button string resource
     * @return current object
     */
    fun withDialogBeforeRun(
        @StringRes titleRes: Int,
        @StringRes messageRes: Int,
        @StringRes positiveButtonRes: Int
    ): PermissionHelper? {
        dialogBeforeAskPositiveButton = positiveButtonRes
        dialogBeforeRunBuilder = getDialogBuilder(titleRes, messageRes)
        return this
    }


    /**
     * This method setup custom dialog positive button color
     *
     * @param colorRes dialog positive button string resource
     * @return current object
     */
    fun setDialogPositiveButtonColor(@ColorRes colorRes: Int): PermissionHelper {
        dialogBeforeAskPositiveButtonColor = ContextCompat.getColor(getContext(), colorRes)
        return this
    }


    /**
     * This method return dialog builder with default settings.
     * It is created for the future customization
     *
     * @param titleRes   dialog title string resource
     * @param messageRes dialog message string resource
     * @return new dialog builder object
     */
    private fun getDialogBuilder(@StringRes titleRes: Int, @StringRes messageRes: Int): AlertDialog.Builder {
        val context: Context = getContext()
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(getContext())
        dialogBuilder.setTitle(context.getString(titleRes))
        dialogBuilder.setMessage(context.getString(messageRes))
        dialogBuilder.setCancelable(false)
        return dialogBuilder
    }

    /**
     * This method return context, depending on what you use: activity or fragment
     *
     * @return context
     */
    private fun getContext(): Context {
        return if (activity == null) fragment?.context!! else activity!!
    }


    /**
     * This method check API-version and listeners
     *
     * @throws RuntimeException if isListenersCorrect return false
     */
    fun run() {
        if (isListenersCorrect()) {
            runSuccessOrAskPermissions()
        } else {
            throw RuntimeException("permissionSuccessListener or permissionDeniedListener have null reference. You must realize onSuccess and onDenied methods")
        }
    }


    /**
     * This method run successListener if all permissions granted,
     * and run method c[.checkPermissions], if [.isNeedToAskPermissions] return false
     */
    private fun runSuccessOrAskPermissions() {
        if (isNeedToAskPermissions()) {
            checkPermissions()
        } else {
            successListener!!.run()
        }
    }


    /**
     * This method request only those permissions that are not granted.
     * If all are granted, success callback called
     * otherwise [.checkDialogAndAskPermissions] will called
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun checkPermissions() {
        val permissionsForRequest = getPermissionsForRequest()
        if (permissionsForRequest.isNotEmpty()) {
            checkDialogAndAskPermissions(permissionsForRequest)
        } else {
            successListener!!.run()
        }
    }

    /**
     * This method check your dialog
     * If you set it, [.withDialogBeforeRun], that dialog will be show before system permission dialog
     * otherwise [.askPermissions] will be called
     * Note, custom dialog will show only if permissions not granted.
     *
     * @param permissionsForRequest = permissions, when currently not granted and will be asked
     */
    @SuppressLint("NewApi")
    private fun checkDialogAndAskPermissions(permissionsForRequest: Array<String?>) {
        if (dialogBeforeRunBuilder != null && isNotContainsNeverAskAgain(permissionsForRequest)) {
            showDialogBeforeRun(permissionsForRequest)
        } else {
            askPermissions(permissionsForRequest)
        }
    }

    /**
     * This method check permissions for never again.
     *
     * @param permissionsForRequest = permissions, when currently not granted and will be asked
     * @return false if one of them never ask gain
     */
    private fun isNotContainsNeverAskAgain(permissionsForRequest: Array<String?>): Boolean {
        for (permissions in permissionsForRequest) {
            if (isNeverAskAgain(permissions)) {
                return false
            }
        }
        return true
    }

    /**
     * This method set positive button and custom color to your dialog
     * method [.askPermissions] called when positive button clicked
     *
     * @param permissionsForRequest = permissions, when currently not granted and will be asked
     */
    private fun showDialogBeforeRun(permissionsForRequest: Array<String?>) {
        dialogBeforeRunBuilder?.setPositiveButton(
            dialogBeforeAskPositiveButton
        ) { _, _ ->
            askPermissions(
                permissionsForRequest
            )
        }
        val dialogBeforeRun = dialogBeforeRunBuilder?.create()
        dialogBeforeRun?.show()
        if (dialogBeforeAskPositiveButtonColor != DIALOG_WITHOUT_CUSTOM_COLOR) {
            dialogBeforeRun?.getButton(DialogInterface.BUTTON_POSITIVE)
                ?.setTextColor(dialogBeforeAskPositiveButtonColor)
        }
    }

    /**
     * This method ask permission
     *
     * @param permissionsForRequest array of permissions which you want to ask
     */
    @SuppressLint("NewApi")
    private fun askPermissions(permissionsForRequest: Array<String?>) {
        activity?.requestPermissions(permissionsForRequest, PERMISSION_REQUEST_CODE) ?:run{
            fragment?.requestPermissions(permissionsForRequest, PERMISSION_REQUEST_CODE)
        }
    }


    /**
     * This method check listeners for null
     *
     * @return true if you realized method onSuccess and onDenied
     */
    private fun isListenersCorrect(): Boolean {
        return successListener != null && deniedListener != null
    }


    /**
     * This method ckeck api version
     *
     * @return true if API >=23
     */
    private fun isNeedToAskPermissions(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }


    /**
     * @return Array of permissions, that will be request
     */
    private fun getPermissionsForRequest(): Array<String?> {
        val permissionsForRequest: MutableList<String?> = ArrayList()
        for (permission in permissions) {
            if (isPermissionNotGranted(permission)) {
                permissionsForRequest.add(permission)
            }
        }
        return permissionsForRequest.toTypedArray()
    }


    /**
     * if permission not granted, check neverAskAgain, else call failure
     * if permission grander, call success
     *
     * @param grantResults Permissions, which granted
     * @param permissions  Permissions, which you asked
     * @param requestCode  requestCode of out request
     */
    @SuppressLint("NewApi")
    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray?
    ) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (permission in permissions) {
                if (isPermissionNotGranted(permission)) {
                    runDeniedOrNeverAskAgain(permission)
                    return
                }
            }
        }
        successListener!!.run()
        unbind()
    }

    /**
     * This method run denied or neverAskAgain callbacks
     *
     * @param permission Permissions, which granted
     */
    @SuppressLint("NewApi")
    private fun runDeniedOrNeverAskAgain(permission: String?) {
        if (isNeverAskAgain(permission)) {
            runNeverAskAgain()
        } else {
            deniedListener!!.run()
        }
        unbind()
    }


    /**
     * This method run neverAskAgain callback if neverAskAgainListener not null
     */
    private fun runNeverAskAgain() {
        if (neverAskAgainListener != null) {
            neverAskAgainListener!!.run()
        }
    }


    /**
     * @param permission for check
     * @return true if permission granted and false if permission not granted
     */
    private fun isPermissionNotGranted(permission: String?): Boolean {
        return if (activity != null) {
            ActivityCompat.checkSelfPermission(
                activity!!,
                permission!!
            ) != PackageManager.PERMISSION_GRANTED
        } else {
            ActivityCompat.checkSelfPermission(
                fragment?.context!!,
                permission!!
            ) != PackageManager.PERMISSION_GRANTED
        }
    }


    /**
     * @param permission for check neverAskAgain
     * @return true if user checked "Never Ask Again"
     */
    @SuppressLint("NewApi")
    private fun isNeverAskAgain(permission: String?): Boolean {
        return if (activity != null) {
            !activity!!.shouldShowRequestPermissionRationale(permission!!)
        } else {
            !fragment?.shouldShowRequestPermissionRationale(permission!!)!!
        }
    }


    /**
     * This method start application settings activity
     * Note: is not possible to open at once screen with application permissions.
     */
    fun startApplicationSettingsActivity() {
        val context: Context = getContext()
        val intent = Intent()
        val uri: Uri = Uri.fromParts("package", context.getPackageName(), null)
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.setData(uri)
        context.startActivity(intent)
    }

    /**
     * This method change listeners reference to avoid memory leaks
     * Don't forget setup callbacks and your settings again!
     */
    private fun unbind() {
        deniedListener = null
        successListener = null
        if (dialogBeforeRunBuilder != null) {
            dialogBeforeRunBuilder = null
            dialogBeforeAskPositiveButton = DIALOG_WITHOUT_CUSTOM_COLOR
        }
        if (neverAskAgainListener != null) {
            neverAskAgainListener = null
        }
    }
}