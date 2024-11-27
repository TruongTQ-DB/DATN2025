package com.graduate.datn.ui.user.choose_service

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.graduate.datn.R
import com.graduate.datn.adapter.recyclerview.ChooseServiceAdapter
import com.graduate.datn.adapter.recyclerview.ServiceItem
import com.graduate.datn.base.BaseFragment
import com.graduate.datn.base.adapter.EndlessLoadingRecyclerViewAdapter
import com.graduate.datn.base.entity.BaseError
import com.graduate.datn.entity.response.ServiceResponse
import com.graduate.datn.extension.getErrorBody
import com.graduate.datn.extension.gone
import com.graduate.datn.extension.toast
import com.graduate.datn.extension.visible
import com.graduate.datn.ui.user.chooseOptionalService.ChooseOptionalServiceFragment
import com.graduate.datn.utils.BundleKey
import com.graduate.datn.utils.Constant
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_choose_service.*

class ChooseServiceFragment : BaseFragment() {
    private val viewModel: ChooseServiceViewModel by activityViewModels()
    private val mAdapter: ChooseServiceAdapter by lazy {
        ChooseServiceAdapter(requireContext())
    }
//    em sữa cái gì và ở đau
    private val db = FirebaseFirestore.getInstance()
    private val addressesCollection = db.collection(Constant.TABLE_SERVICE)

    override fun backFromAddFragment() {
        refreshData()
    }

    override val layoutId: Int
        get() = R.layout.fragment_choose_service

    override fun initView() {
        viewModel.lastVisibleDocument = null
        recycler_view.setAdapter(mAdapter)
        recycler_view.setGridLayoutManager(3)
    }

    override fun initData() {
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
        }
        refreshData()
    }

    override fun initListener() {
        header.onLeftClick = {
            backPressed()
        }
        recycler_view.apply {
            setOnRefreshListener {
                recycler_view.enableRefresh(false)
                refreshData()
            }
            setOnLoadingMoreListener(object :
                EndlessLoadingRecyclerViewAdapter.OnLoadingMoreListener {
                override fun onLoadMore() {
                    mAdapter.showLoadingItem(true)
                    showLoading()
                    addressesCollection
                        //em sua nhieu lam cai nao co barber em thay het bang docter
                        .whereEqualTo("clinicShopAddressId", viewModel.addressId)
                        .orderBy("name")
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
                                    val data =
                                        documentSnapshot.toObject(ServiceResponse::class.java)
                                    ServiceItem(id, data)
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

        mAdapter.onClick = { id, name ->
            getVC().addFragment(ChooseOptionalServiceFragment::class.java, Bundle().apply {
                putString(BundleKey.KEY_ADDRESS_ID, viewModel.addressId)
                putString(BundleKey.KEY_ADDRESS_NAME, viewModel.addressName)
                putString(BundleKey.KEY_ADDRESS, viewModel.address)
                putString(BundleKey.KEY_SERVICE_ID, id)
                putString(BundleKey.KEY_SERVICE_NAME, name)
            })
        }
    }

    override fun backPressed(): Boolean {
        getVC().backFromAddFragment()
        return false
    }

    override fun handleValidateError(throwable: BaseError?) {
        toast(throwable!!.error)
    }

    override fun handleNetworkError(throwable: Throwable?, isShowDialog: Boolean) {
        throwable?.getErrorBody()?.msg?.let { toast(it) }
    }

    private fun refreshData() {
        showLoading()
        mAdapter.clear()
        addressesCollection
            .whereEqualTo("clinicShopAddressId", viewModel.addressId)
            .orderBy("name").limit(20).get()
            .addOnSuccessListener { documents ->
                hideLoading()
                if (documents.isEmpty) {
                    tv_show_message_not_result.visible()
                } else {
                    tv_show_message_not_result.gone()
                    viewModel.lastVisibleDocument = documents.documents.lastOrNull()
                    val dataList = documents.map { documentSnapshot ->
                        val id = documentSnapshot.id
                        val data = documentSnapshot.toObject(ServiceResponse::class.java)
                        ServiceItem(id, data)
                    }
                    mAdapter.refresh(dataList)
                }
            }.addOnFailureListener {
                hideLoading()
                tv_show_message_not_result.visible()
                toast(R.string.error_400)
            }
    }
}