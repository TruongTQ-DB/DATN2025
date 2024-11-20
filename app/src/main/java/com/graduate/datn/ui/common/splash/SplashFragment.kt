package com.graduate.datn.ui.common.splash

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.activityViewModels
import com.graduate.datn.R
import com.graduate.datn.base.BaseFragment
import com.graduate.datn.entity.User
import com.graduate.datn.ui.admin.homeScreen.HomeScreenFragment
import com.graduate.datn.ui.docterName.container_docter.TabContainerDocterFragment
import com.graduate.datn.ui.common.login.LoginFragment
import com.graduate.datn.ui.user.container_user.TabContainerUserFragment
import com.graduate.datn.utils.Constant.TABLE_USER
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment : BaseFragment() {
    private val viewModel: SplashViewModel by activityViewModels()
    private lateinit var auth: FirebaseAuth
    private val typeAccount = 0

    override fun backFromAddFragment() {

    }

    override val layoutId: Int
        get() = R.layout.splash_fragment

    override fun backPressed(): Boolean {
        return false
    }

    override fun initView() {
        FirebaseApp.initializeApp(requireContext())
        auth = Firebase.auth

        Looper.getMainLooper()?.let {
            Handler(it).postDelayed({
                if (auth.currentUser == null) {
                    getVC().replaceFragment(LoginFragment::class.java)
                } else {
                    getToken()
                }
            }, 2000)
        }
    }

    private fun getToken(){
        FirebaseMessaging.getInstance().token.addOnSuccessListener { this.updateToken(it) }
    }

    private fun updateToken(token: String){
        auth.currentUser?.let {
            FirebaseFirestore.getInstance().collection(TABLE_USER)
                .whereEqualTo("id", auth.currentUser!!.uid)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.isEmpty) {
                        Log.d("FCM", "Không có id này")
                    } else {
                        querySnapshot.map {
                            val typeAccount = it.toObject(User::class.java)
                            Log.d("thisss", "ssssss"+typeAccount.account)
                            when(typeAccount.account){
                                0 -> {
                                    getVC().replaceFragment(TabContainerUserFragment::class.java)
                                }
                                1 -> {
                                    getVC().replaceFragment(TabContainerDocterFragment::class.java)
                                }
                                2 -> {
                                    getVC().replaceFragment(HomeScreenFragment::class.java)
                                }
                                else -> {
                                    getVC().replaceFragment(LoginFragment::class.java)
                                }
                            }
                        }
                        for (document in querySnapshot.documents) {
                            document.reference.update("token", token)
                                .addOnSuccessListener {
                                    Log.d("FCM", "update token: $token")
                                }
                                .addOnFailureListener { e ->
                                    Log.d("FCM", "update token fail: $it")
                                }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.d("FCM", "update token fail: $e")
                }
        }
    }

    private fun Logout(){
        auth.signOut()
    }

    private fun clearToken() {
        auth.currentUser?.let {
            FirebaseFirestore.getInstance().collection(TABLE_USER)
                .whereEqualTo("id", auth.currentUser!!.uid)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.isEmpty) {
                        Log.d("FCM", "Không có id này")
                    } else {
                        for (document in querySnapshot.documents) {
                            document.reference.update("token", FieldValue.delete())
                                .addOnSuccessListener {
                                    Logout()
                                    getVC().replaceFragment(LoginFragment::class.java)
                                    Log.d("FCM", "clear token")
                                }
                                .addOnFailureListener { e ->
                                    Log.d("FCM", "clear token fail: $it")
                                }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.d("FCM", "clear token fail: $e")
                }
        }
    }

    override fun initData() {

    }

    override fun initListener() {

    }
}
