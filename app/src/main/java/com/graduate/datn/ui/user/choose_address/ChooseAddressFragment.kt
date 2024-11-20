package com.graduate.datn.ui.user.choose_address

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.graduate.datn.R
import com.graduate.datn.adapter.recyclerview.AddressItem
import com.graduate.datn.adapter.recyclerview.ChooseAddressAdapter
import com.graduate.datn.base.BaseFragment
import com.graduate.datn.base.adapter.EndlessLoadingRecyclerViewAdapter
import com.graduate.datn.base.entity.BaseError
import com.graduate.datn.entity.response.ListAddressResponse
import com.graduate.datn.extension.getErrorBody
import com.graduate.datn.extension.gone
import com.graduate.datn.extension.toast
import com.graduate.datn.extension.visible
import com.graduate.datn.ui.admin.list_address.ListAddressModel
import com.graduate.datn.ui.user.choose_service.ChooseServiceFragment
import com.graduate.datn.utils.BundleKey
import com.graduate.datn.utils.Constant
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_choose_address.*

class ChooseAddressFragment : BaseFragment() {
    private val viewModel: ListAddressModel by activityViewModels()
    private val mAdapter: ChooseAddressAdapter by lazy {
        ChooseAddressAdapter(requireContext())
    }
    private val db = FirebaseFirestore.getInstance()
    private val addressesCollection = db.collection(Constant.TABLE_BOOKING_CLINIC_ADDRESS)

    override fun backFromAddFragment() {
        refreshData()
    }

    override val layoutId: Int
        get() = R.layout.fragment_choose_address

    override fun initView() {
        viewModel.lastVisibleDocument = null
        recycler_view.setAdapter(mAdapter)
        recycler_view.setListLayoutManager(LinearLayoutManager.VERTICAL)
    }

    override fun initData() {
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
                    addressesCollection.orderBy("name").startAfter(viewModel.lastVisibleDocument!!)
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
                                        documentSnapshot.toObject(ListAddressResponse::class.java)
                                    AddressItem(id, data)
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
            getVC().addFragment(ChooseServiceFragment::class.java, Bundle().apply {
                putString(BundleKey.KEY_ADDRESS_ID, it.id)
                putString(BundleKey.KEY_ADDRESS_NAME, it.data?.name)
                putString(BundleKey.KEY_ADDRESS, it.data?.address)
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
        addressesCollection.orderBy("name").limit(20).get()
            .addOnSuccessListener { documents ->
                hideLoading()
                if (documents.isEmpty) {
                    tv_show_message_not_result.visible()
                } else {
                    tv_show_message_not_result.gone()
                    viewModel.lastVisibleDocument = documents.documents.lastOrNull()
                    val dataList = documents.map { documentSnapshot ->
                        val id = documentSnapshot.id
                        val data = documentSnapshot.toObject(ListAddressResponse::class.java)
                        AddressItem(id, data)
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