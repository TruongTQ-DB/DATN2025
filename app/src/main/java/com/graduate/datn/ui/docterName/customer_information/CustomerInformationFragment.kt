package com.graduate.datn.ui.docterName.customer_information

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.graduate.datn.R
import com.graduate.datn.adapter.recyclerview.BookingItem
import com.graduate.datn.adapter.recyclerview.ScheduleAdapter
import com.graduate.datn.base.BaseFragment
import com.graduate.datn.entity.User
import com.graduate.datn.entity.response.BookingResponse
import com.graduate.datn.extension.gone
import com.graduate.datn.extension.loadImageUrl
import com.graduate.datn.extension.onAvoidDoubleClick
import com.graduate.datn.extension.visible
import com.graduate.datn.ui.docterName.list_schedule_by_user.ListScheduleByUserFragment
import com.graduate.datn.ui.docterName.schedule_details.ScheduleDetailsFragment
import com.graduate.datn.utils.BundleKey
import com.graduate.datn.utils.Constant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_customer_information.*

class CustomerInformationFragment : BaseFragment() {
    private val viewModel: CustomerInformationViewModel by activityViewModels()
    private val db = FirebaseFirestore.getInstance()
    private val bookingCollection = db.collection(Constant.TABLE_BOOKING)
    private val userCollection = db.collection(Constant.TABLE_USER)
    private lateinit var auth: FirebaseAuth
    private lateinit var bookingDate: String
    private val mScheduleAdapter: ScheduleAdapter by lazy {
        ScheduleAdapter(requireContext())
    }

    override fun backFromAddFragment() {

    }

    override val layoutId: Int
        get() = R.layout.fragment_customer_information

    override fun initView() {
        viewModel.phone = null
        auth = Firebase.auth
        viewModel.idUser = null
        recycler_view.setAdapter(mScheduleAdapter)
        recycler_view.setListLayoutManager(LinearLayoutManager.VERTICAL)
//        bookingCollection.get()
//            .addOnSuccessListener {
//                it.map {
//                    it.reference.delete()
//                }
//            }
    }

    override fun initData() {
        arguments?.let {
            if (it.containsKey(BundleKey.ID_CUSTOMER_BOOKING)) {
                viewModel.idUser = it.getString(BundleKey.ID_CUSTOMER_BOOKING)
            }
        }
        getUser()
        refreshData()
    }

    override fun initListener() {
        header.onLeftClick = {
            backPressed()
        }
        ll_call_phone.onAvoidDoubleClick {
            if (!viewModel.phone.isNullOrEmpty()) {
                val phone = viewModel.phone
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:$phone")
                requireContext().startActivity(intent)
            }
        }

        mScheduleAdapter.onClick = {
            getVC().addFragment(ScheduleDetailsFragment::class.java, Bundle().apply {
                putString(BundleKey.ID_SCHEDULE, it)
            })
        }
        tv_all.onAvoidDoubleClick {
            getVC().addFragment(ListScheduleByUserFragment::class.java, Bundle().apply {
                putString(BundleKey.ID_USER, viewModel.idUser)
            })
        }
    }

    override fun backPressed(): Boolean {
        getVC().backFromAddFragment()
        return false
    }

    private fun getUser() {
        userCollection.whereEqualTo("id", viewModel.idUser).limit(1).get()
            .addOnSuccessListener { documents ->
                val userData = documents.mapNotNull { documentSnapshot ->
                    documentSnapshot.toObject(User::class.java)
                }
                setUpUserInformationView(userData.first())
            }
    }

    private fun setUpUserInformationView(userData: User) {
        img_avatar.loadImageUrl(userData.avatar)
        tv_name.text = userData.name
        tv_birthday.text = userData.birthday
        tv_email.text = userData.email
        tv_phone.text = userData.phone
        tv_sex.text = setUpGender(userData.gander)
        tv_address.text = userData.address
        viewModel.phone = userData.phone
    }

    private fun setUpGender(gander: Int?): String {
        return when (gander) {
            1 -> {
                "Nam"
            }
            2 -> {
                "Nữ"
            }
            3 -> {
                "Khác"
            }
            else -> {
                ""
            }
        }
    }

    private fun refreshData() {
        showLoading()
        mScheduleAdapter.clear()
        bookingCollection
            .whereEqualTo("userId", viewModel.idUser)
            .whereEqualTo("docterId", auth.currentUser?.uid)
            .orderBy("date").limit(3)
            .get()
            .addOnSuccessListener { documents ->
                hideLoading()
                if (documents.isEmpty) {
                    tv_show_message_not_result.visible()
                    recycler_view.gone()
                } else {
                    tv_show_message_not_result.gone()
                    val dataList = documents.map { documentSnapshot ->
                        val id = documentSnapshot.id
                        val data = documentSnapshot.toObject(BookingResponse::class.java)
                        BookingItem(id, data)
                    }
                    mScheduleAdapter.refresh(dataList)
                }
            }.addOnFailureListener {
                hideLoading()
                recycler_view.gone()
                tv_show_message_not_result.visible()
            }
    }
}