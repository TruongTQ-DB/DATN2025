package com.graduate.datn.ui.user.chooseOptionalService

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.graduate.datn.R
import com.graduate.datn.adapter.recyclerview.*
import com.graduate.datn.base.BaseFragment
import com.graduate.datn.base.adapter.EndlessLoadingRecyclerViewAdapter
import com.graduate.datn.base.entity.BaseError
import com.graduate.datn.entity.response.OptionalServiceResponse
import com.graduate.datn.entity.response.ServiceResponse
import com.graduate.datn.extension.getErrorBody
import com.graduate.datn.extension.gone
import com.graduate.datn.extension.toast
import com.graduate.datn.extension.visible
import com.graduate.datn.ui.user.choose_docter_name.ChooseDocterNameFragment
import com.graduate.datn.utils.BundleKey
import com.graduate.datn.utils.Constant
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_choose_optional_service.*

class ChooseOptionalServiceFragment : BaseFragment() {
    private val viewModel: ChooseOptionalServiceViewModel by activityViewModels()
    private val serviceAdapter: ChooseServiceTopAdapter by lazy {
        ChooseServiceTopAdapter(requireContext())
    }
    private val optionalServiceAdapter: OptionalServiceAdapter by lazy {
        OptionalServiceAdapter(requireContext())
    }
    private val db = FirebaseFirestore.getInstance()
    private val serviceCollection = db.collection(Constant.TABLE_SERVICE)
    private val optionServiceCollection = db.collection(Constant.TABLE_OPTIONAL_SERVICE)

    override fun backFromAddFragment() {
        refreshOptionalService()
    }

    override val layoutId: Int
        get() = R.layout.fragment_choose_optional_service

    override fun initView() {
        viewModel.apply {
            lastVisibleOptionService = null
            lastVisibleService = null
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
        }
    }

    override fun initData() {
        reloadService()
        refreshOptionalService()
    }

    override fun initListener() {
        header.onLeftClick = {
            backPressed()
        }

        recycler_view_optional_service.apply {
            setAdapter(optionalServiceAdapter)
            setListLayoutManager(LinearLayoutManager.VERTICAL)
            setOnRefreshListener {
                recycler_view_optional_service.enableRefresh(false)
                refreshOptionalService()
            }
            setOnLoadingMoreListener(object :
                EndlessLoadingRecyclerViewAdapter.OnLoadingMoreListener {
                override fun onLoadMore() {
                    optionalServiceAdapter.showLoadingItem(true)
                    showLoading()
                    optionServiceCollection
                        .whereEqualTo("barberShopAddressId", viewModel.addressId)
                        .whereEqualTo("serviceId", viewModel.serviceId)
                        .orderBy("name").startAfter(viewModel.lastVisibleOptionService!!)
                        .limit(20)
                        .get().addOnSuccessListener { documents ->
                            optionalServiceAdapter.hideLoadingItem()
                            hideLoading()
                            if (documents.isEmpty) {
                                // Không còn dữ liệu để tải thêm
                            } else {
                                val dataList = documents.map { documentSnapshot ->
                                    val id = documentSnapshot.id
                                    val data = documentSnapshot.toObject(OptionalServiceResponse::class.java)
                                    OptionalServiceItem(id, data)
                                }
                                viewModel.lastVisibleOptionService = documents.documents.lastOrNull()
                                optionalServiceAdapter.addModels(dataList, false)
                            }
                        }.addOnFailureListener {
                            hideLoading()
                            toast(R.string.error_400)
                        }
                }
            })
        }

        recycler_view_service.apply {
            setAdapter(serviceAdapter)
            setListLayoutManager(LinearLayoutManager.HORIZONTAL)
            setEnableRefresh(false)
            setOnLoadingMoreListener(object :
                EndlessLoadingRecyclerViewAdapter.OnLoadingMoreListener {
                override fun onLoadMore() {
                    serviceAdapter.showLoadingItem(true)
                    showLoading()
                    serviceCollection
                        .whereEqualTo("barberShopAddressId", viewModel.addressId)
                        .orderBy("name").startAfter(viewModel.lastVisibleService!!).limit(20)
                        .get().addOnSuccessListener { documents ->
                            hideLoading()
                            serviceAdapter.hideLoadingItem()
                            if (documents.isEmpty) {
                                // Không còn dữ liệu để tải thêm
                            } else {
                                val dataList = documents.map { documentSnapshot ->
                                    val id = documentSnapshot.id
                                    val data =
                                        documentSnapshot.toObject(ServiceResponse::class.java)
                                    ServiceItem(id, data)
                                }
                                viewModel.lastVisibleService = documents.documents.lastOrNull()
                                serviceAdapter.addModels(dataList, false)
                            }
                        }.addOnFailureListener {
                            hideLoading()
                            toast(R.string.error_400)
                        }
                }
            })
        }

        serviceAdapter.onClick = {
            if (viewModel.serviceId != it) {
                viewModel.serviceId = it
            }
            refreshOptionalService()
        }
        optionalServiceAdapter.onClick = {
            getVC().addFragment(ChooseDocterNameFragment::class.java, Bundle().apply {
                putString(BundleKey.KEY_ADDRESS_ID, viewModel.addressId)
                putString(BundleKey.KEY_ADDRESS_NAME, viewModel.addressName)
                putString(BundleKey.KEY_ADDRESS, viewModel.address)
                putString(BundleKey.KEY_SERVICE_ID, viewModel.serviceId)
                putString(BundleKey.KEY_SERVICE_NAME, viewModel.serviceName)
                putString(BundleKey.KEY_OPTIONAL_SERVICE_ID, it.id)
                putString(BundleKey.KEY_OPTIONAL_SERVICE_NAME, it.data.name)
                putString(BundleKey.KEY_OPTIONAL_SERVICE_PRICE, it.data.price)
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


    private fun reloadService() {
        showLoading()
        serviceAdapter.clear()
        serviceCollection
            .whereEqualTo("barberShopAddressId", viewModel.addressId)
            .orderBy("name").limit(20).get()
            .addOnSuccessListener { documents ->
                hideLoading()
                if (documents.isEmpty) {

                } else {
//                    tv_show_message_not_result.gone()
                    viewModel.lastVisibleService = documents.documents.lastOrNull()
                    val dataList = documents.map { documentSnapshot ->
                        val id = documentSnapshot.id
                        val data = documentSnapshot.toObject(ServiceResponse::class.java)
                        ServiceItem(id, data)
                    }
                    serviceAdapter.refresh(dataList)
                    dataList.forEach {
                        if (it.id == viewModel.serviceId) {
                            it.data.isSelected = true
                            serviceAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }.addOnFailureListener {
                hideLoading()
                toast(R.string.error_400)
            }

    }

    private fun refreshOptionalService() {
        showLoading()
        optionalServiceAdapter.clear()
        optionServiceCollection
            .whereEqualTo("barberShopAddressId", viewModel.addressId)
            .whereEqualTo("serviceId", viewModel.serviceId)
            .orderBy("name").limit(20).get()
            .addOnSuccessListener { documents ->
                hideLoading()
                if (documents.isEmpty) {
                    tv_show_message_not_result.visible()
                } else {
                    tv_show_message_not_result.gone()
                    viewModel.lastVisibleOptionService = documents.documents.lastOrNull()
                    val dataList = documents.map { documentSnapshot ->
                        val id = documentSnapshot.id
                        val data = documentSnapshot.toObject(OptionalServiceResponse::class.java)
                        OptionalServiceItem(id, data)
                    }
                    optionalServiceAdapter.refresh(dataList)
                }
            }.addOnFailureListener {
                hideLoading()
                tv_show_message_not_result.visible()
                toast(R.string.error_400)
            }
    }
}