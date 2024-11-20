package com.graduate.datn.ui.admin.work_schedule_detail

import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.graduate.datn.R
import com.graduate.datn.adapter.recyclerview.RangerTimeAdapter
import com.graduate.datn.base.BaseFragment
import com.graduate.datn.entity.request.DateToWorkRequest
import com.graduate.datn.extension.*
import com.graduate.datn.ui.admin.list_work_schedule.DateToWorkItem
import com.graduate.datn.ui.admin.work_schedule.CreateWorkScheduleFragment
import com.graduate.datn.utils.BundleKey
import com.graduate.datn.utils.Constant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_work_schedule_details.*

class WorkScheduleDetailsFragment : BaseFragment() {
    private val viewModel: WorkScheduleDetailsViewModel by activityViewModels()
    private val db = FirebaseFirestore.getInstance()
    private val wordScheduleCollection = db.collection(Constant.TABLE_WORK_SCHEDULE)
    private lateinit var auth: FirebaseAuth

    private val mAdapter: RangerTimeAdapter by lazy {
        RangerTimeAdapter(requireContext())
    }

    override fun backFromAddFragment() {

    }

    override val layoutId: Int
        get() = R.layout.fragment_work_schedule_details

    override fun initView() {
        auth = Firebase.auth
        viewModel.data = null
        viewModel.isApprove = false
        viewModel.lastVisibleDocument = null
        recycler_view.setAdapter(mAdapter)
        recycler_view.setListLayoutManager(LinearLayoutManager.VERTICAL)
    }

    override fun initData() {
        arguments?.let {
            if (it.containsKey(BundleKey.KEY_WORD_SCHEDULE_DETAIL)) {
                viewModel.data =
                    it.getSerializable(BundleKey.KEY_WORD_SCHEDULE_DETAIL) as DateToWorkItem?
            }
            if (it.containsKey(BundleKey.KEY_WORD_SCHEDULE_DETAIL_APPROVE)) {
                viewModel.isApprove = it.getBoolean(BundleKey.KEY_WORD_SCHEDULE_DETAIL_APPROVE)
            }
        }
        setUpViewInfor()
        refreshData()
    }

    private fun setUpViewInfor() {
        viewModel.apply {
            tv_date.text = data?.data?.date
            img_avatar.loadImageUrl(data?.data?.avatar)
            tv_name.text = data?.data?.name
            if (viewModel.isApprove == true) {
                btn_approve.text = "Chấp thuận"
                image.setImageDrawable(context?.getDrawable(R.drawable.ic_done_all_white_24dp))
            }
        }
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
        }

        ll_add.onAvoidDoubleClick {
            if (viewModel.isApprove == true) {
                showLoading()
                viewModel.data?.id?.let { it1 ->
                    wordScheduleCollection.document(it1).update("approve", true)
                        .addOnSuccessListener {
                            hideLoading()
                            toast("Chấp thuần thành công!")
                            backPressed()
                        }.addOnFailureListener{
                            hideLoading()
                            toast("Chấp thuần thất bại!")
                        }
                }
            } else {
                getVC().addFragment(CreateWorkScheduleFragment::class.java)
            }
        }
    }

    override fun backPressed(): Boolean {
        getVC().backFromAddFragment()
        return false
    }

    private fun refreshData() {
        showLoading()
        mAdapter.clear()
        viewModel.data?.id?.let {
            wordScheduleCollection.document(it).get()
                .addOnSuccessListener { documents ->
                    hideLoading()
                    if (documents.data.isNullOrEmpty()) {
                        tv_show_message_not_result.visible()
                    } else {
                        tv_show_message_not_result.gone()
                        val data = documents.toObject(DateToWorkRequest::class.java)
                        data?.timeRanges?.let { it1 -> mAdapter.refresh(it1) }
                    }
                }.addOnFailureListener {
                    hideLoading()
                    tv_show_message_not_result.visible()
                    toast(R.string.error_400)
                }
        }
    }
}