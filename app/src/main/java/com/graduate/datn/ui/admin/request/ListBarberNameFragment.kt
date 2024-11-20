package com.graduate.datn.ui.admin.request

import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.graduate.datn.R
import com.graduate.datn.adapter.recyclerview.DocterNameAdapter
import com.graduate.datn.base.BaseFragment
import com.graduate.datn.base.adapter.EndlessLoadingRecyclerViewAdapter
import com.graduate.datn.entity.User
import com.graduate.datn.extension.gone
import com.graduate.datn.extension.onAvoidDoubleClick
import com.graduate.datn.extension.toast
import com.graduate.datn.extension.visible
import com.graduate.datn.ui.admin.add_docter_name.AddDocterNameFragment
import com.graduate.datn.ui.barber_name.RequestViewModel
import com.graduate.datn.utils.Constant
import com.graduate.datn.utils.Constant.TYPE_ACCOUNT_BARBER_NAME
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_list_docter_name.*

class ListBarberNameFragment : BaseFragment() {
    private val viewModel: RequestViewModel by activityViewModels()
    private val mAdapter: DocterNameAdapter by lazy { DocterNameAdapter(requireContext()) }

    //    private val databaseReference = FirebaseDatabase.getInstance().reference.child(TABLE_USER)
    private val userCollection = FirebaseFirestore.getInstance().collection(Constant.TABLE_USER)
    override fun backFromAddFragment() {
        refreshData()
    }

    override val layoutId: Int
        get() = R.layout.fragment_list_docter_name

    override fun initView() {
        rcv_barber_name.setAdapter(mAdapter)
        rcv_barber_name.setListLayoutManager(LinearLayoutManager.VERTICAL)
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
                    userCollection
                        .whereEqualTo("account", TYPE_ACCOUNT_BARBER_NAME)
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
                                    documentSnapshot.toObject(User::class.java)
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

        ll_add_barber_name.onAvoidDoubleClick {
            getVC().addFragment(AddDocterNameFragment::class.java)
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
        mAdapter.clear()
        userCollection
            .whereEqualTo("account", TYPE_ACCOUNT_BARBER_NAME)
            .orderBy("name")
            .limit(20).get()
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
                toast(R.string.error_400)
            }
    }
}