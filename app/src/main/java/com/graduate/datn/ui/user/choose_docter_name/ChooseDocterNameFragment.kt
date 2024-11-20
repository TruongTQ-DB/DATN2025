package com.graduate.datn.ui.user.choose_docter_name

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.graduate.datn.R
import com.graduate.datn.adapter.recyclerview.DocterNameAdapter
import com.graduate.datn.base.BaseFragment
import com.graduate.datn.base.adapter.EndlessLoadingRecyclerViewAdapter
import com.graduate.datn.entity.User
import com.graduate.datn.extension.gone
import com.graduate.datn.extension.toast
import com.graduate.datn.extension.visible
import com.graduate.datn.ui.user.make_appointment_time.MakeAppointmentTimeFragment
import com.graduate.datn.utils.BundleKey
import com.graduate.datn.utils.Constant
import com.graduate.datn.utils.Constant.TABLE_USER
import com.graduate.datn.utils.Constant.TYPE_ACCOUNT_BARBER_NAME
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.graduate.datn.adapter.recyclerview.DocterListNameAdapter
import kotlinx.android.synthetic.main.fragment_choose_docter_name.*

class ChooseDocterNameFragment : BaseFragment() {
    private val viewModel: ChooseDocterNameViewModel by activityViewModels()
    private val mAdapter: DocterListNameAdapter by lazy { DocterListNameAdapter(requireContext()) }

    private val databaseReference = FirebaseDatabase.getInstance().reference.child(TABLE_USER)
    private val userCollection = FirebaseFirestore.getInstance().collection(Constant.TABLE_USER)
    override fun backFromAddFragment() {
        refreshData()
    }

    override val layoutId: Int
        get() = R.layout.fragment_choose_docter_name

    override fun initView() {
        rcv_barber_name.apply {
            setAdapter(mAdapter)
            setListLayoutManager(LinearLayoutManager.VERTICAL)
        }

        arguments?.let {
            if (it.containsKey(BundleKey.KEY_ADDRESS_ID)) {
                viewModel.addressId = it.getString(BundleKey.KEY_ADDRESS_ID)
            }
            if (it.containsKey(BundleKey.KEY_ADDRESS_NAME)) {
                viewModel.addressName = it.getString(BundleKey.KEY_ADDRESS_NAME)
            }
            if (it.containsKey(BundleKey.KEY_ADDRESS)) {
                viewModel.address = it.getString(BundleKey.KEY_ADDRESS)
            }
            if (it.containsKey(BundleKey.KEY_SERVICE_ID)) {
                viewModel.serviceId = it.getString(BundleKey.KEY_SERVICE_ID)
            }
            if (it.containsKey(BundleKey.KEY_SERVICE_NAME)) {
                viewModel.serviceName = it.getString(BundleKey.KEY_SERVICE_NAME)
            }
            if (it.containsKey(BundleKey.KEY_OPTIONAL_SERVICE_ID)) {
                viewModel.optionalServiceId = it.getString(BundleKey.KEY_OPTIONAL_SERVICE_ID)
            }
            if (it.containsKey(BundleKey.KEY_OPTIONAL_SERVICE_NAME)) {
                viewModel.optionalServiceName = it.getString(BundleKey.KEY_OPTIONAL_SERVICE_NAME)
            }
            if (it.containsKey(BundleKey.KEY_OPTIONAL_SERVICE_PRICE)) {
                viewModel.optionalServicePrice = it.getString(BundleKey.KEY_OPTIONAL_SERVICE_PRICE)
            }
        }
    }

    override fun initData() {
        refreshData()
    }

    override fun initListener() {
        header.onLeftClick = {
            backPressed()
        }

        rcv_barber_name.apply {
            setOnRefreshListener {
                rcv_barber_name.enableRefresh(false)
                refreshData()
            }
            setOnLoadingMoreListener(object :
                EndlessLoadingRecyclerViewAdapter.OnLoadingMoreListener {
                override fun onLoadMore() {
                    mAdapter.showLoadingItem(true)
                    showLoading()
                    viewModel.serviceId?.let {
                        viewModel.optionalServiceId?.let { it1 ->
                            userCollection
                                .whereEqualTo("account", TYPE_ACCOUNT_BARBER_NAME)
                                .whereEqualTo("barberShopAddressId", viewModel.addressId)
                                .whereArrayContains("optionalService",
                                    mapOf(
                                        "disable" to false,
                                        "optionalServiceId" to it1,
                                        "serviceId" to it,
                                        "optionalServiceName" to viewModel.optionalServiceName))
                                .orderBy("name").startAfter(viewModel.lastVisibleDocument!!)
                                .limit(20)
                                .get().addOnSuccessListener { documents ->
                                    mAdapter.hideLoadingItem()
                                    hideLoading()
                                    if (documents.isEmpty) {
                                        // Không còn dữ liệu để tải thêm
                                    } else {
                                        val dataList = documents.map { documentSnapshot ->
                                            documentSnapshot.toObject(User::class.java)
                                        }
                                        viewModel.lastVisibleDocument =
                                            documents.documents.lastOrNull()
                                        mAdapter.addModels(dataList, false)
                                    }
                                }.addOnFailureListener {
                                    hideLoading()
                                    toast(R.string.error_400)
                                }
                        }
                    }
                }
            })
        }

        mAdapter.onClick = {
            if (it is User) {
                getVC().addFragment(MakeAppointmentTimeFragment::class.java, Bundle().apply {
                    putString(BundleKey.KEY_ADDRESS_ID, viewModel.addressId)
                    putString(BundleKey.KEY_ADDRESS_NAME, viewModel.addressName)
                    putString(BundleKey.KEY_ADDRESS, viewModel.address)
                    putString(BundleKey.KEY_SERVICE_ID, viewModel.serviceId)
                    putString(BundleKey.KEY_OPTIONAL_SERVICE_ID, viewModel.optionalServiceId)
                    putString(BundleKey.KEY_SERVICE_NAME, viewModel.serviceName)
                    putString(BundleKey.KEY_OPTIONAL_SERVICE_NAME, viewModel.optionalServiceName)
                    putString(BundleKey.KEY_OPTIONAL_SERVICE_PRICE, viewModel.optionalServicePrice)
                    putSerializable(BundleKey.KEY_DOCTER_NAME, it)
                })
            }

        }
    }

    override fun <U> getObjectResponse(data: U) {

    }

    override fun backPressed(): Boolean {
        getVC().backFromAddFragment()
        return false
    }

    private fun refreshData() {
        showLoading()
        viewModel.optionalServiceId?.let { Log.d("thiss", it) }
        mAdapter.clear()
        viewModel.serviceId?.let {
            viewModel.optionalServiceId?.let { it1 ->
                userCollection
                    .whereEqualTo("account", TYPE_ACCOUNT_BARBER_NAME)
                    .whereEqualTo("barberShopAddressId", viewModel.addressId)
                    .whereArrayContains("optionalService",
                        mapOf(
                            "disable" to false,
                            "optionalServiceId" to it1,
                            "serviceId" to it,
                            "optionalServiceName" to viewModel.optionalServiceName))
                    .orderBy("name").limit(20).get()
                    .addOnSuccessListener { documents ->
                        hideLoading()
                        if (documents.isEmpty) {
                            tv_show_message_not_result.visible()
                        } else {
                            tv_show_message_not_result.gone()
                            viewModel.lastVisibleDocument = documents.documents.lastOrNull()
                            val dataList = documents.map { documentSnapshot ->
                                documentSnapshot.toObject(User::class.java)
                            }
                            mAdapter.refresh(dataList)
                        }
                    }.addOnFailureListener {
                        hideLoading()
                        tv_show_message_not_result.visible()
                    }
            }
        }
    }
}