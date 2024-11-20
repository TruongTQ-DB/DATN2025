package com.graduate.datn.ui.common.listChat

import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.graduate.datn.R
import com.graduate.datn.adapter.recyclerview.AddressItem
import com.graduate.datn.adapter.recyclerview.ListChatAdapter
import com.graduate.datn.base.BaseFragment
import com.graduate.datn.base.adapter.EndlessLoadingRecyclerViewAdapter
import com.graduate.datn.base.entity.BaseError
import com.graduate.datn.entity.response.ListAddressResponse
import com.graduate.datn.extension.getErrorBody
import com.graduate.datn.extension.gone
import com.graduate.datn.extension.toast
import com.graduate.datn.extension.visible
import com.graduate.datn.ui.common.chatDetail.ChatDetailFragment
import com.graduate.datn.utils.Constant.TABLE_LIST_CHAT
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_list_chat.*


class ListChatFragment : BaseFragment() {
    private val viewModel: ListChatViewModel by activityViewModels()
    private val mAddressAdapter: ListChatAdapter by lazy {
        ListChatAdapter(requireContext())
    }
    private val db = FirebaseFirestore.getInstance()
    private val addressesCollection = db.collection(TABLE_LIST_CHAT)

    override fun backFromAddFragment() {
        refreshData()
    }

    override val layoutId: Int
        get() = R.layout.fragment_list_chat

    override fun initView() {
        viewModel.lastVisibleDocument = null
        rcv_list_chat.setAdapter(mAddressAdapter)
        rcv_list_chat.setListLayoutManager(LinearLayoutManager.VERTICAL)
    }

    override fun initData() {
        refreshData()
    }


    override fun initListener() {
        header.onLeftClick = {
            backPressed()
        }
        rcv_list_chat.apply {
            setOnRefreshListener {
                rcv_list_chat.enableRefresh(false)
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
            getVC().addFragment(ChatDetailFragment::class.java)
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
                        val data = documentSnapshot.toObject(ListAddressResponse::class.java)
                        AddressItem(id, data)
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