package com.graduate.datn.ui.admin.container_service

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.graduate.datn.R
import com.graduate.datn.adapter.recyclerview.OptionalServiceItem
import com.graduate.datn.adapter.recyclerview.ServiceAdapter
import com.graduate.datn.adapter.recyclerview.ServiceItem
import com.graduate.datn.base.BaseFragment
import com.graduate.datn.base.adapter.EndlessLoadingRecyclerViewAdapter
import com.graduate.datn.entity.response.OptionalServiceResponse
import com.graduate.datn.entity.response.ServiceResponse
import com.graduate.datn.extension.gone
import com.graduate.datn.extension.onAvoidDoubleClick
import com.graduate.datn.extension.toast
import com.graduate.datn.extension.visible
import com.graduate.datn.ui.admin.add_optional_service.AddOptionalServiceFragment
import com.graduate.datn.ui.admin.add_service.AddServiceFragment
import com.graduate.datn.utils.BundleKey
import com.graduate.datn.utils.Constant
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_container_service.*

class ContainerServiceFragment : BaseFragment() {
    private val viewModel: ContainerServiceViewModel by activityViewModels()
    private val mAdapter: ServiceAdapter by lazy { ServiceAdapter(requireContext()) }

    private val db = FirebaseFirestore.getInstance()
    private val servicesCollection = db.collection(Constant.TABLE_SERVICE)
    private val optionalServiceCollection = db.collection(Constant.TABLE_OPTIONAL_SERVICE)
    override fun backFromAddFragment() {
        refreshData()
    }

    override val layoutId: Int
        get() = R.layout.fragment_container_service

    override fun initView() {
        viewModel.tabLayout = 1
        tab_service.setText("Khoa khám", "Dịch vụ")
        recycler_view.setAdapter(mAdapter)
        recycler_view.setListLayoutManager(LinearLayoutManager.VERTICAL)
        refreshData()
    }

    override fun initData() {
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
                    if (viewModel.tabLayout == 1) {
                        servicesCollection.orderBy("name")
                            .startAfter(viewModel.lastVisibleDocument!!).limit(20)
                            .get().addOnSuccessListener { documents ->
                                hideLoading()
                                mAdapter.hideLoadingItem()
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
                    } else {
                        optionalServiceCollection.orderBy("name")
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
                                            documentSnapshot.toObject(OptionalServiceResponse::class.java)
                                        OptionalServiceItem(id, data)
                                    }
                                    viewModel.lastVisibleDocument = documents.documents.lastOrNull()
                                    mAdapter.addModels(dataList, false)
                                }
                            }.addOnFailureListener {
                                hideLoading()
                                toast(R.string.error_400)
                            }
                    }
                }
            })
        }
    }

    private fun refreshData() {
        showLoading()
        mAdapter.clear()
        if (viewModel.tabLayout == 1) {
            servicesCollection.orderBy("name").limit(20).get()
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
        } else {
            optionalServiceCollection.orderBy("name").limit(20).get()
                .addOnSuccessListener { documents ->
                    hideLoading()
                    if (documents.isEmpty) {
                        tv_show_message_not_result.visible()
                    } else {
                        tv_show_message_not_result.gone()
                        viewModel.lastVisibleDocument = documents.documents.lastOrNull()
                        val dataList = documents.map { documentSnapshot ->
                            val id = documentSnapshot.id
                            val data =
                                documentSnapshot.toObject(OptionalServiceResponse::class.java)
                            OptionalServiceItem(id, data)
                        }
                        mAdapter.refresh(dataList)
                    }
                }.addOnFailureListener {
                    hideLoading()
                    tv_show_message_not_result.visible()
                    toast(R.string.error_400)
                }
        }
        mAdapter.onClick = {
            if (it is ServiceItem) {
                getVC().addFragment(AddServiceFragment::class.java, Bundle().apply {
                    putSerializable(BundleKey.KEY_SERVICE_BARBER_SHOP, it)
                })
            } else if (it is OptionalServiceItem) {
                getVC().addFragment(AddOptionalServiceFragment::class.java, Bundle().apply {
                    putSerializable(BundleKey.KEY_OPTIONAL_SERVICE_SHOP, it)

                })
            }
        }

    }

    override fun initListener() {
        header.onLeftClick = {
            backPressed()
        }

//        btn_service.onAvoidDoubleClick {
//            getVC().addFragment(ListServiceFragment::class.java)
//        }
//
//        btn_optional_service.onAvoidDoubleClick {
//            getVC().addFragment(ListOptionalServiceFragment::class.java)
//        }

        tab_service.onClickLeft = {
            tv_add.text = "Thêm mới Khoa khám"
            viewModel.tabLayout = 1
            refreshData()
        }

        tab_service.onClickRight = {
            tv_add.text = "Thêm mới Dịch vụ"
            viewModel.tabLayout = 2
            refreshData()
        }

        ll_add.onAvoidDoubleClick {
            if (viewModel.tabLayout == 1) {
                getVC().addFragment(AddServiceFragment::class.java)
            } else {
                getVC().addFragment(AddOptionalServiceFragment::class.java)
            }
        }
    }

    override fun backPressed(): Boolean {
        getVC().backFromAddFragment()
        return false
    }
}