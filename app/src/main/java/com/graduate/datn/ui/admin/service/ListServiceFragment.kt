package com.graduate.datn.ui.admin.service

import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.graduate.datn.R
import com.graduate.datn.adapter.recyclerview.ServiceAdapter
import com.graduate.datn.base.BaseFragment
import com.graduate.datn.base.adapter.EndlessLoadingRecyclerViewAdapter
import com.graduate.datn.base.entity.BaseError
import com.graduate.datn.entity.response.ServiceResponse
import com.graduate.datn.extension.*
import com.graduate.datn.ui.admin.add_service.AddServiceFragment
import com.graduate.datn.utils.Constant.TABLE_SERVICE
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_list_service.*

class ListServiceFragment : BaseFragment() {
    private val viewModel: ListServiceViewModel by activityViewModels()
    private val mAdapter: ServiceAdapter by lazy { ServiceAdapter(requireContext()) }

    private val db = FirebaseFirestore.getInstance()
    private val servicesCollection = db.collection(TABLE_SERVICE)
    override fun backFromAddFragment() {
        refreshData()
    }

    override val layoutId: Int
        get() = R.layout.fragment_list_service

    override fun initView() {
        rcv_service.setAdapter(mAdapter)
        rcv_service.setListLayoutManager(LinearLayoutManager.VERTICAL)
    }

    override fun initData() {
        refreshData()
    }

    override fun initListener() {
        header.onLeftClick = {
            backPressed()
        }
        rcv_service.apply {
            setOnRefreshListener {
                rcv_service.enableRefresh(false)
                refreshData()
            }
            setOnLoadingMoreListener(object :
                EndlessLoadingRecyclerViewAdapter.OnLoadingMoreListener {
                override fun onLoadMore() {
                    mAdapter.showLoadingItem(true)
                    showLoading()
                    servicesCollection.orderBy("name").startAfter(viewModel.lastVisibleDocument!!).limit(20)
                        .get().addOnSuccessListener { documents ->
                            hideLoading()
                            mAdapter.hideLoadingItem()
                            if (documents.isEmpty) {
                                // Không còn dữ liệu để tải thêm
                            } else {
                                val dataList = documents.map { documentSnapshot ->
                                    documentSnapshot.toObject(ServiceResponse::class.java)
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

        }


        ll_add_service.onAvoidDoubleClick {
            getVC().addFragment(AddServiceFragment::class.java)
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
        servicesCollection.orderBy("name").limit(20).get()
            .addOnSuccessListener { documents ->
                hideLoading()
                if (documents.isEmpty) {
                    tv_show_message_not_result.visible()
                } else {
                    tv_show_message_not_result.gone()
                    viewModel.lastVisibleDocument = documents.documents.lastOrNull()
                    val dataList = documents.map { documentSnapshot ->
                        documentSnapshot.toObject(ServiceResponse::class.java)
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