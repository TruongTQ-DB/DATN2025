package com.graduate.datn.ui.admin.home_admin

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.graduate.datn.R
import com.graduate.datn.adapter.recyclerview.TheNewAdapter
import com.graduate.datn.adapter.recyclerview.TheNewItem
import com.graduate.datn.base.BaseFragment
import com.graduate.datn.base.adapter.EndlessLoadingRecyclerViewAdapter
import com.graduate.datn.entity.User
import com.graduate.datn.entity.request.TheNewRequest
import com.graduate.datn.extension.*
import com.graduate.datn.ui.admin.add_the_new.AddTheNewFragment
import com.graduate.datn.ui.admin.notification_schedule.NotificationScheduleFragment
import com.graduate.datn.ui.common.login.LoginFragment
import com.graduate.datn.utils.Constant
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_home_admin.*
import java.text.SimpleDateFormat
import java.util.*

class HomeAdminFragment : BaseFragment() {
    private val viewModel: HomeAdminViewModel by activityViewModels()
    private val mAdapter: TheNewAdapter by lazy {
        TheNewAdapter(requireContext())
    }
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    private val collectionReference = db.collection(Constant.TABLE_ACTIVITY_STATISTICS)
    private val bookingCollection = db.collection(Constant.TABLE_BOOKING)
    private val userCollection = db.collection(Constant.TABLE_USER)
    private val theNewCollection = db.collection(Constant.TABLE_THE_NEW)

    override fun backFromAddFragment() {
        refreshData()
    }

    override val layoutId: Int
        get() = R.layout.fragment_home_admin

    override fun initView() {
        auth = Firebase.auth
        viewModel.lastVisibleDocument = null
        recycler_view.setAdapter(mAdapter)
        recycler_view.setListLayoutManager(LinearLayoutManager.VERTICAL)
        refreshData()
        recycler_view.nestedScrollingEnabled()
    }

    private fun refreshData() {
        getInforAdmin()
        reloadDataTheNew()
        checkItemsForDateRanges(0, 3, 9, 30)
    }


    private fun getInforAdmin() {
        userCollection.whereEqualTo("id", auth.currentUser?.uid).limit(1).get()
            .addOnSuccessListener { documents ->
                val userData = documents.mapNotNull { documentSnapshot ->
                    documentSnapshot.toObject(User::class.java)
                }
                if (!userData.isNullOrEmpty()) {
                    setUpView(userData.first())
                }
            }
    }

    private fun setUpView(userData: User) {
        img_avatar.loadImageUrl(userData.avatar)
        tv_full_name.text = userData.name
    }

    override fun initData() {
        recycler_view.apply {
            setOnRefreshListener {
                recycler_view.enableRefresh(false)
                reloadDataTheNew()
            }
            setOnLoadingMoreListener(object :
                EndlessLoadingRecyclerViewAdapter.OnLoadingMoreListener {
                override fun onLoadMore() {
                    mAdapter.showLoadingItem(true)
                    showLoading()
                    theNewCollection
                        .orderBy("timestamp", Query.Direction.DESCENDING)
                        .startAfter(viewModel.lastVisibleDocument!!)
                        .limit(20)
                        .get().addOnSuccessListener { documents ->
                            mAdapter.hideLoadingItem()
                            hideLoading()
                            if (documents.isEmpty) {
                                // Không còn dữ liệu để tải thêm
                            } else {
                                val dataList = documents.map { documentSnapshot ->
                                    val id = documentSnapshot.id
                                    val data = documentSnapshot.toObject(TheNewRequest::class.java)
                                    TheNewItem(id, data)
                                }
                                viewModel.lastVisibleDocument = documents.documents.lastOrNull()
                                mAdapter.addModels(dataList, false)
                            }
                        }.addOnFailureListener {
                            hideLoading()
                            toast(R.string.error_400)
                        }
                }
            })
        }

        mAdapter.onClick = {
            getVC().addFragment(AddTheNewFragment::class.java, Bundle().apply {
//                putString(BundleKey.ID_SCHEDULE, it)
            })
        }

        ll_add.onAvoidDoubleClick {
            getVC().addFragment(AddTheNewFragment::class.java)
        }

        tv_logout.onAvoidDoubleClick {
            showLoading()
            clearToken()
        }
        img_avatar.onAvoidDoubleClick {
            getVC().addFragment(NotificationScheduleFragment::class.java)
        }
    }

    private fun clearToken() {
        auth.currentUser?.let {
            FirebaseFirestore.getInstance().collection(Constant.TABLE_USER)
                .whereEqualTo("id", auth.currentUser!!.uid)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.isEmpty) {
                        Log.d("FCM", "Không có id này")
                    } else {
                        for (document in querySnapshot.documents) {
                            document.reference.update("token", FieldValue.delete())
                                .addOnSuccessListener {
                                    hideLoading()
                                    auth.signOut()
                                    (parentFragment?.parentFragment as BaseFragment).getVC()
                                        .replaceFragment(LoginFragment::class.java)
                                    Log.d("FCM", "clear token")
                                }
                                .addOnFailureListener { e ->
                                    hideLoading()
                                    toast("Đăng xuất thất bại")
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

    override fun initListener() {

    }

    override fun backPressed(): Boolean {
        getVC().backFromAddFragment()
        return false
    }

    private fun reloadDataTheNew() {
        showLoading()
        mAdapter.clear()
        theNewCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(20)
            .get()
            .addOnSuccessListener { documents ->
                hideLoading()
                if (documents.isEmpty) {
                    tv_show_message_not_result.visible()
                } else {
                    tv_show_message_not_result.gone()
                    viewModel.lastVisibleDocument = documents.documents.lastOrNull()
                    val dataList = documents.map { documentSnapshot ->
                        val id = documentSnapshot.id
                        val data = documentSnapshot.toObject(TheNewRequest::class.java)
                        TheNewItem(id, data)
                    }
                    mAdapter.refresh(dataList)
                }
            }.addOnFailureListener {
                hideLoading()
                tv_show_message_not_result.visible()
                toast(R.string.error_400)
            }
    }

    private fun checkItemsForDateRanges(vararg daysRanges: Int) {
        for (days in daysRanges) {
            val currentDate = Calendar.getInstance()
            currentDate.add(Calendar.DAY_OF_YEAR, -days)
            val ss = SimpleDateFormat("dd/MM/yyyy").parse(SimpleDateFormat("dd/MM/yyyy",
                Locale.getDefault()).format(currentDate.time))?.let { Timestamp(it) }
            getCurrentDaystamp()?.let {
                ss?.let { it1 ->
                    bookingCollection
                        .whereLessThanOrEqualTo("timeStampCurrent", it)
                        .whereGreaterThanOrEqualTo("timeStampCurrent", it1)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            println("Items in the last $days days: ${querySnapshot.size()}")
                            setSizeAcivityBooking(querySnapshot.size(), days)
                        }
                        .addOnFailureListener { exception ->
                            println("Error getting documents: $exception")
                        }

                    collectionReference
                        .whereLessThanOrEqualTo("timestamp", it)
                        .whereGreaterThanOrEqualTo("timestamp", it1)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            println("Items in the last $days days: ${querySnapshot.size()}")
                            setSizeAcivityStatistics(querySnapshot.size(), days)
                        }
                        .addOnFailureListener { exception ->
                            println("Error getting documents: $exception")
                        }
                }
            }
        }
    }

    private fun setSizeAcivityBooking(size: Int, days: Int) {
        when (days) {
            TYPE_TODAY -> {
                tv_schedule.text = size.toString()
            }
            TYPE_3_DAY -> {
                tv_3_schedule.text = size.toString()
            }
            TYPE_9_DAY -> {
                tv_9_schedule.text = size.toString()
            }
            TYPE_30_DAY -> {
                tv_30_schedule.text = size.toString()
            }
        }
    }

    private fun setSizeAcivityStatistics(size: Int, days: Int) {
        when (days) {
            TYPE_TODAY -> {
                tv_today.text = size.toString()
            }
            TYPE_3_DAY -> {
                tv_3_day.text = size.toString()
            }
            TYPE_9_DAY -> {
                tv_9_day.text = size.toString()
            }
            TYPE_30_DAY -> {
                tv_30_day.text = size.toString()
            }
        }
    }

    private fun getCurrentDaystamp(): Timestamp? {
        return SimpleDateFormat("dd/MM/yyyy").parse(getDayString())?.let { Timestamp(it) }
    }

    private fun getDayString(): String {
        return SimpleDateFormat("dd/MM/yyyy",
            Locale.getDefault()).format(Calendar.getInstance().time)
    }

    companion object {
        const val TYPE_TODAY = 0
        const val TYPE_3_DAY = 3
        const val TYPE_9_DAY = 9
        const val TYPE_30_DAY = 30
    }
}
