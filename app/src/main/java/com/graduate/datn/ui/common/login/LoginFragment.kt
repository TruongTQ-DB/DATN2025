package com.graduate.datn.ui.common.login

import android.content.Intent
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.activityViewModels
import com.graduate.datn.R
import com.graduate.datn.base.BaseFragment
import com.graduate.datn.base.entity.BaseError
import com.graduate.datn.entity.User
import com.graduate.datn.extension.*
import com.graduate.datn.ui.admin.homeScreen.HomeScreenFragment
import com.graduate.datn.ui.docterName.container_docter.TabContainerDocterFragment
import com.graduate.datn.ui.user.container_user.TabContainerUserFragment
import com.graduate.datn.ui.user.register.RegisterFragment
import com.graduate.datn.utils.Constant
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.login_fragment.*

class LoginFragment : BaseFragment() {
    private val viewModel: LoginViewModel by activityViewModels()

    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager

    private val userCollection = FirebaseFirestore.getInstance().collection(Constant.TABLE_USER)


    private companion object {
        private const val REQ_ONE_TAP = 100
        private const val TAG = "GOOGLE_SIGN_IN_TAG"
    }

    override fun backFromAddFragment() {
    }

    override val layoutId: Int
        get() = R.layout.login_fragment

    override fun initView() {
        mAuth = Firebase.auth
        callbackManager = CallbackManager.Factory.create()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_token_id)).requestEmail().build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
    }

    override fun initData() {

    }

    override fun initListener() {
        btn_login.onAvoidDoubleClick {
            val username = input_username.getText()
            val password = input_password.getText()
            if (!validateLogin(username, password)) {
                return@onAvoidDoubleClick
            }
            showLoading()
            mAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    mAuth.currentUser?.let { user ->
                        saveOrGetLoginUser(user.uid, mAuth.currentUser)
                    }
                } else {
                    toast(R.string.login_failed)
                    hideLoading()
                }
            }
        }

        login_tv_forget_password.onAvoidDoubleClick {
            toast("Chức năng này chưa được kích hoạt. Xin thông cảm!")
        }

        tv_register.onAvoidDoubleClick {
            getVC().addFragment(RegisterFragment::class.java)
        }

        ll_facebook.onAvoidDoubleClick {
            with(LoginManager.getInstance()) {
                logInWithReadPermissions(this@LoginFragment, listOf("public_profile"))
                registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                    override fun onSuccess(loginResult: LoginResult) {
                        showLoading()
                        firebaseAuthWithFacebook(loginResult.accessToken.token)
                    }

                    override fun onCancel() {
                        toast(R.string.login_failed)
                        Log.d("thiss", "facebook:onCancel")

                    }

                    override fun onError(error: FacebookException) {
                        toast(R.string.login_failed)
                        Log.d("thiss", "facebook:onError", error)
                    }
                })
            }
        }

        ll_google.onAvoidDoubleClick {
            showLoading()
            googleSignIn()
        }


    }

    private fun validateLogin(username: String, password: String): Boolean {
        if (TextUtils.isEmpty(username.trim())) {
            toast(R.string.login_validate_user_name)
            return false
        }
        if (!(username).isEmailValid()) {
            toast(R.string.login_validate_user_name_not_email)
            return false
        }
        if (TextUtils.isEmpty(password)) {
            toast(R.string.login_validate_password)
            return false
        }
        return true
    }

    private fun saveOrGetLoginUser(currentUserId: String, currentUser: FirebaseUser?) {
        currentUserId.let { userId ->
            userCollection
                .whereEqualTo("id", userId)
                .get().addOnCompleteListener { documents ->
                    if (documents.isSuccessful) {
                        if (!documents.result.documents.isNullOrEmpty()) {
                            val userData = documents.result.documents[0].toObject(User::class.java)
                            userData?.let { loginSuccessful(it) }
                        } else {
                            val user = User(id = mAuth.currentUser?.uid,
                                account = 0,
                                name = currentUser?.displayName.toString(),
                                email = currentUser?.email.toString(),
                                phone = currentUser?.phoneNumber,
                                avatar = currentUser?.photoUrl.toString())
                            userCollection.add(user)
                                .addOnSuccessListener { _ ->
                                    loginSuccessful(user)
                                }
                                .addOnFailureListener { e ->
                                    hideLoading()
                                    toast(R.string.login_failed)
                                }
                        }
                    } else {
                        hideLoading()
                        toast(R.string.login_failed)
                    }
                }
        }
    }

    private fun loginSuccessful(userData: User) {
//        requireContext()?.let {Toast.makeText(requireContext(), "Đăng nhập thành công", Toast.LENGTH_SHORT).show()  }
        Log.d("thiss", userData.toString())
        getToken()
    }

    private fun getToken() {
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { this.updateToken(it) }
            .addOnFailureListener{hideLoading()}
    }

    private fun updateToken(token: String) {
        mAuth.currentUser?.let {
            FirebaseFirestore.getInstance().collection(Constant.TABLE_USER)
                .whereEqualTo("id", mAuth.currentUser!!.uid)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    hideLoading()
                    if (querySnapshot.isEmpty) {
                        Log.d("FCM", "Không có id này")
                    } else {
                        querySnapshot.map {
                            val typeAccount = it.toObject(User::class.java)
                            Log.d("thisss", "ssssss" + typeAccount.account)
                            when (typeAccount.account) {
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
                    hideLoading()
                }
        }
    }

    override fun backPressed(): Boolean {
        return false
    }

    override fun handleValidateError(throwable: BaseError?) {
        toast(throwable!!.error)
    }

    override fun handleNetworkError(throwable: Throwable?, isShowDialog: Boolean) {
        throwable?.getErrorBody()?.msg?.let { toast(it) }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(firebaseCredential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                saveOrGetLoginUser(mAuth.currentUser?.uid.toString(),
                    mAuth.currentUser)
            } else {
                hideLoading()
                toast(R.string.login_failed)
            }
        }
    }

    private fun firebaseAuthWithFacebook(idToken: String) {
        val credential = FacebookAuthProvider.getCredential(idToken)
        mAuth.signInWithCredential(credential).addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                mAuth.currentUser?.let {
                    saveOrGetLoginUser(it.uid, mAuth.currentUser)
                }
            } else {
                hideLoading()
                toast(R.string.login_failed)
            }
        }
    }

    private fun googleSignIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, REQ_ONE_TAP)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_ONE_TAP) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception
            if (task.isSuccessful) {
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                }
            } else {
                hideLoading()
                Log.w("GoogleSignInActivity", exception.toString())
            }
        } else {
//            login facebook
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }

}