package com.graduate.datn.ui.admin.list_optionnal_service

import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.graduate.datn.R
import com.graduate.datn.adapter.recyclerview.AddressItem
import com.graduate.datn.adapter.recyclerview.OptionalServiceAdapter
import com.graduate.datn.adapter.recyclerview.OptionalServiceItem
import com.graduate.datn.base.BaseFragment
import com.graduate.datn.base.adapter.EndlessLoadingRecyclerViewAdapter
import com.graduate.datn.base.entity.BaseError
import com.graduate.datn.entity.response.ListAddressResponse
import com.graduate.datn.entity.response.OptionalServiceResponse
import com.graduate.datn.extension.*
import com.graduate.datn.ui.admin.add_optional_service.AddOptionalServiceFragment
import com.graduate.datn.utils.Constant.TABLE_OPTIONAL_SERVICE
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_list_optional_service.*

class ListOptionalServiceFragment : BaseFragment() {
    private val viewModel: ListOptionalServiceViewModel by activityViewModels()
    private val mAddressAdapter: OptionalServiceAdapter by lazy {
        OptionalServiceAdapter(requireContext())
    }
    private val db = FirebaseFirestore.getInstance()
    private val addressesCollection = db.collection(TABLE_OPTIONAL_SERVICE)

    override fun backFromAddFragment() {
        refreshData()
    }

    override val layoutId: Int
        get() = R.layout.fragment_list_optional_service

    override fun initView() {
        rcv_optional_service.setAdapter(mAddressAdapter)
        rcv_optional_service.setListLayoutManager(LinearLayoutManager.VERTICAL)
    }

    override fun initData() {
        refreshData()
    }

    override fun initListener() {
        header.onLeftClick = {
            backPressed()
        }
        rcv_optional_service.apply {
            setOnRefreshListener {
                rcv_optional_service.enableRefresh(false)
                refreshData()
            }
            setOnLoadingMoreListener(object :
                EndlessLoadingRecyclerViewAdapter.OnLoadingMoreListener {
                override fun onLoadMore() {
                    mAddressAdapter.showLoadingItem(true)
                    showLoading()
                    addressesCollection.orderBy("name").startAfter(viewModel.lastVisibleDocument!!)
                        .limit(20)
                        .get().addOnSuccessListener { documents ->
                            mAddressAdapter.hideLoadingItem()
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
                                mAddressAdapter.addModels(dataList, false)
                            }
                        }.addOnFailureListener {
                            hideLoading()
                            toast(R.string.error_400)
                        }
                }
            })
        }

        mAddressAdapter.onClick = {

        }

        ll_add_optional_service.onAvoidDoubleClick {
            getVC().addFragment(AddOptionalServiceFragment::class.java)
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
        mAddressAdapter.clear()
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
                        val data = documentSnapshot.toObject(OptionalServiceResponse::class.java)
                        OptionalServiceItem(id, data)
                    }
                    mAddressAdapter.refresh(dataList)
                }
            }.addOnFailureListener {
                hideLoading()
                tv_show_message_not_result.visible()
                toast(R.string.error_400)
            }
    }
}