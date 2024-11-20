package com.graduate.datn.ui.admin.home

import android.os.Handler
import android.os.Looper
import androidx.core.view.marginTop
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.graduate.datn.R
import com.graduate.datn.adapter.recyclerview.ButtonAdapter
import com.graduate.datn.adapter.recyclerview.Buttons
import com.graduate.datn.adapter.recyclerview.TheNewByCustomerAdapter
import com.graduate.datn.adapter.recyclerview.TheNewItem
import com.graduate.datn.base.BaseFragment
import com.graduate.datn.base.adapter.RecyclerViewAdapter
import com.graduate.datn.entity.User
import com.graduate.datn.entity.request.ActivityStatistics
import com.graduate.datn.entity.request.TheNewRequest
import com.graduate.datn.extension.*
import com.graduate.datn.ui.common.listChat.ListChatFragment
import com.graduate.datn.ui.user.container_user.TabContainerUserFragment
import com.graduate.datn.utils.Constant
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.graduate.datn.ui.user.container_slider.ContainSliderAdapter
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.home_fragment.*
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : BaseFragment() {
    private val viewModel: HomeViewModel by activityViewModels()
    private val alphaIsEnabled = 0.9
    private val buttonAdapter: ButtonAdapter by lazy {
        ButtonAdapter(requireContext())
    }
    private val mAdapter: TheNewByCustomerAdapter by lazy {
        TheNewByCustomerAdapter(requireContext())
    }
    private val sliderAdapter: ContainSliderAdapter by lazy{
        ContainSliderAdapter(requireContext())

    }
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    private val activityCollection = db.collection(Constant.TABLE_ACTIVITY_STATISTICS)
    private val userCollection = db.collection(Constant.TABLE_USER)
    private val theNewCollection = db.collection(Constant.TABLE_THE_NEW)

    //    private val a = db.collection(Constant.TABLE_BOOKING)
//    private val b = db.collection(Constant.TABLE_NOTIFICATION)
//    private val c = db.collection(Constant.TABLE_WORK_SCHEDULE)
//    private val d = db.collection(Constant.TABLE_USER)
//    private val e = db.collection(Constant.TABLE_BARBER_SHOP_ADDRESS)
//    private val f = db.collection(Constant.TABLE_SERVICE)
//    private val g = db.collection(Constant.TABLE_OPTIONAL_SERVICE)
    private var mDisposable = CompositeDisposable()

    override fun onDestroy() {
        super.onDestroy()
        mDisposable.clear()
    }

    override fun backFromAddFragment() {

    }

    override val layoutId: Int
        get() = R.layout.home_fragment

    override fun initView() {
//        a.get()
//            .addOnSuccessListener {
//                it.map {
//                    it.reference.delete()
//                }
//            }
//        b.get()
//            .addOnSuccessListener {
//                it.map {
//                    it.reference.delete()
//                }
//            }
//        c.get()
//            .addOnSuccessListener {
//                it.map {
//                    it.reference.delete()
//                }
//            }
//        d.get()
//            .addOnSuccessListener {
//                it.map {
//                    it.reference.delete()
//                }
//            }
//        e.get()
//            .addOnSuccessListener {
//                it.map {
//                    it.reference.delete()
//                }
//            }
//        f.get()
//            .addOnSuccessListener {
//                it.map {
//                    it.reference.delete()
//                }
//            }
//        g.get()
//            .addOnSuccessListener {
//                it.map {
//                    it.reference.delete()
//                }
//            }

        viewModel.dataUser = null
        auth = Firebase.auth
        getUserInfor()
        getActivityStatisticsToDay()

        viewpager_2.adapter = sliderAdapter
        handler.post(runnable)
        rcv_service.adapter = buttonAdapter
        rcv_the_new.adapter = mAdapter
        nsv_container.smoothScrollTo(0, 0)
        nsv_container.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY > oldScrollY) {
                scrollDown(scrollX, scrollY, oldScrollX, oldScrollY)
            }
            if (scrollY < oldScrollY) {
                scrollUp(scrollX, scrollY, oldScrollX, oldScrollY)
            }
        })
        reloadDataTheNew()
        reloadDataTheNew2()
    }

    private fun getUserInfor() {
        showLoading()
        userCollection
            .whereEqualTo("id", auth.currentUser?.uid)
            .limit(1)
            .get()
            .addOnSuccessListener {
                hideLoading()
                if (!it.isEmpty) {
                    it.mapNotNull { documentSnapshot ->
                        val data = documentSnapshot.toObject(User::class.java)
                        viewModel.dataUser = data
                        setUpInforView(data)
                    }
                }
            }
            .addOnFailureListener {
                hideLoading()
            }
    }

    private fun setUpInforView(data: User) {
        img_avatar.loadImageUrl(data.avatar)
        tv_full_name.text = data.name

        val height = data.height?.toFloatOrNull()?.let { String.format("%.1f", it) } ?: "..."
        val weight = data.weight?.toFloatOrNull()?.let { String.format("%.1f", it) } ?: "..."

        tv_height.text = "$height Cm"
        tv_weight.text = "$weight Kg"
        tv_blood_type.text = "${data.blood_type ?: "..."}"
    }

    private fun getActivityStatisticsToDay() {
        activityCollection
            .whereEqualTo("userId", auth.currentUser?.uid)
            .whereEqualTo("date", getDayString())
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    saveActivityStatistics()
                }
            }
    }

    private fun saveActivityStatistics() {
        val data = ActivityStatistics(
            userId = auth.currentUser?.uid,
            userName = viewModel.dataUser?.name,
            date = getDayString(),
            timestamp = getCurrentDaystamp()
        )
        activityCollection.add(data)
    }

    private fun getCurrentDaystamp(): Timestamp? {
        return SimpleDateFormat("dd/MM/yyyy").parse(getDayString())?.let { Timestamp(it) }
    }

    private fun getDayString(): String {
        return SimpleDateFormat("dd/MM/yyyy",
            Locale.getDefault()).format(Calendar.getInstance().time)
    }

    private fun scrollUp(scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
        val pointChange = input_search.marginTop - 23.dpToPx
        var scrollYTemp = scrollY
        if (scrollYTemp > pointChange)
            scrollYTemp = pointChange
        var height =
            (15.dpToPx * ((pointChange.toFloat() - scrollYTemp.toFloat()) / pointChange.toFloat())).toInt()
        android.util.Log.e("thiss", "height: ----- $height")
        if (height == 0) {
            height = 1
        }
        android.util.Log.e("thiss", "scrollYTemp: ----- ${scrollYTemp.toFloat()} / ${pointChange.toFloat()} == ${scrollYTemp.toFloat() / pointChange.toFloat()}")

        val paramsLine = view_line_top.layoutParams
        paramsLine.height = height
        view_line_top.layoutParams = paramsLine
        ll_button_top.alpha = scrollYTemp.toFloat() / pointChange.toFloat()
        rl_info.alpha = 1 - (scrollYTemp.toFloat() / pointChange.toFloat())
        ll_button.alpha = 1 - (scrollYTemp.toFloat() / pointChange.toFloat())
        val slideOffset = (scrollYTemp.toFloat() / pointChange.toFloat())
        android.util.Log.e("thiss", "alpha: ----- ${1 - (scrollYTemp.toFloat() / pointChange.toFloat())}")
        android.util.Log.e("thiss", "slideOffset: ----- ${(253.dpToPx - (133.dpToPx * slideOffset)).toInt()}")
        view_root.setMargins(top = (253.dpToPx - (133.dpToPx * slideOffset)).toInt())
        if (slideOffset == 1F) {
//            view_root.setBackgroundColor(Color.WHITE)
            view_root.setBackgroundResource(R.drawable.bg_home)
        } else {
            view_root.setBackgroundResource(R.drawable.bg_home)
        }
        imv_message.isEnabled =
            rl_info.alpha >= alphaIsEnabled
        cv_ic_notification.isEnabled =
            ll_button_top.alpha >= alphaIsEnabled
        cv_profile.isEnabled =
            ll_button_top.alpha >= alphaIsEnabled
        ll_notification.isEnabled =
            ll_button.alpha >= alphaIsEnabled
        ll_profile.isEnabled =
            ll_button.alpha >= alphaIsEnabled
        if (ll_button.alpha >= alphaIsEnabled) {
            ll_button.elevation =
                nsv_container.elevation * 2
        } else {
            ll_button.elevation = 0F

        }
    }

    private fun scrollDown(scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
        val pointChange = input_search.marginTop - 23.dpToPx
        var scrollYTemp = scrollY
        if (scrollYTemp > pointChange)
            scrollYTemp = pointChange
        var height =
            (15.dpToPx * ((pointChange.toFloat() - scrollYTemp.toFloat()) / pointChange.toFloat())).toInt()
        android.util.Log.d("thiss", "height: ----- $height")
        if (height == 0) {
            height = 1
        }
        val paramsLine = view_line_top.layoutParams
        paramsLine.height = height
        view_line_top.layoutParams = paramsLine
        android.util.Log.d("thiss", "scrollYTemp: ----- ${scrollYTemp.toFloat()} / ${pointChange.toFloat()} == ${scrollYTemp.toFloat() / pointChange.toFloat()}")
        ll_button_top.alpha = scrollYTemp.toFloat() / pointChange.toFloat()
        rl_info.alpha = 1 - (scrollYTemp.toFloat() / pointChange.toFloat())
        ll_button.alpha = 1 - (scrollYTemp.toFloat() / pointChange.toFloat())
        val slideOffset = (scrollYTemp.toFloat() / pointChange.toFloat())
        view_root.setMargins(top = (253.dpToPx - (133.dpToPx * slideOffset)).toInt())
        android.util.Log.d("thiss", "alpha: ----- ${1 - (scrollYTemp.toFloat() / pointChange.toFloat())}")
        android.util.Log.d("thiss", "slideOffset: ----- ${(253.dpToPx - (133.dpToPx * slideOffset)).toInt()}")

        if (slideOffset == 1F) {
            view_root.setBackgroundResource(R.drawable.bg_home)
        } else {
            view_root.setBackgroundResource(R.drawable.bg_home)
        }
        // để anh tìm nguyên nhân đã, trc anh làm mà h quên mất, em trc khi sửa thì vẫn hoạt động bth đúng ko
        // van bth anh em thay trong cai meda em lam giong rua. em sua thi loi em nghi co cong thuc => tdr, cái này scroll là tính mới có view dep

        imv_message.isEnabled =
            rl_info.alpha >= alphaIsEnabled
        cv_ic_notification.isEnabled =
            ll_button_top.alpha >= alphaIsEnabled
        cv_profile.isEnabled =
            ll_button_top.alpha >= alphaIsEnabled
        ll_notification.isEnabled =
            ll_button.alpha >= alphaIsEnabled
        ll_profile.isEnabled =
            ll_button.alpha >= alphaIsEnabled
        if (ll_button.alpha >= alphaIsEnabled) {
            ll_button.elevation =
                nsv_container.elevation * 2
        } else {
            ll_button.elevation = 0F
        }
    }

    override fun initData() {
        buttonAdapter.refresh(Buttons.values().toList())
    }

    override fun initListener() {
        ll_notification.onAvoidDoubleClick {
            (parentFragment?.parentFragment as? TabContainerUserFragment)?.moveToTabNotification()
        }
        ll_profile.onAvoidDoubleClick {
            (parentFragment?.parentFragment as? TabContainerUserFragment)?.moveToProfile()
        }
        cv_profile.onAvoidDoubleClick {
            (parentFragment?.parentFragment as? TabContainerUserFragment)?.moveToProfile()
        }
        cv_ic_notification.onAvoidDoubleClick {
            (parentFragment?.parentFragment as? TabContainerUserFragment)?.moveToTabNotification()
        }

        buttonAdapter.addOnItemClickListener(object : RecyclerViewAdapter.OnItemClickListener {
            override fun onItemClick(
                adapter: RecyclerView.Adapter<*>,
                viewHolder: RecyclerView.ViewHolder?,
                viewType: Int,
                position: Int,
            ) {
                val item = buttonAdapter.getItem(position, Buttons::class.java)
                item?.let {
                    when (it) {
                        Buttons.SPECIALIST -> {
                            (parentFragment?.parentFragment as? TabContainerUserFragment)?.moveToCreateSchedule()
                        }
                        Buttons.COUNSELING -> {
                            toast("Chức năng này chưa được kích hoạt. Xin thông cảm!")
                        }
                        Buttons.GENERA -> {
                            toast("Chức năng này chưa được kích hoạt. Xin thông cảm!")
                        }
                        Buttons.TEST -> {
                            toast("Chức năng này chưa được kích hoạt. Xin thông cảm!")
                        }
                        Buttons.CANCER -> {
                            toast("Chức năng này chưa được kích hoạt. Xin thông cảm!")
                        }
                        Buttons.AT_HOME -> {
                            toast("Chức năng này chưa được kích hoạt. Xin thông cảm!")
                        }
                        else -> {}
                    }
                }
            }
        })

        imv_message.onAvoidDoubleClick {
            getVC().addFragment(ListChatFragment::class.java)
        }
    }

    private fun reloadDataTheNew() {
        showLoading()
        mAdapter.clear()

        theNewCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .addOnSuccessListener { documents ->
                hideLoading()
                if (documents.isEmpty) {
                } else {
                    val dataList = documents.map { documentSnapshot ->
                        val id = documentSnapshot.id
                        val data = documentSnapshot.toObject(TheNewRequest::class.java)
                        TheNewItem(id, data)
                    }
                    mAdapter.refresh(dataList)
                }
            }.addOnFailureListener {
                hideLoading()
                toast(R.string.error_400)
            }
    }
    private fun reloadDataTheNew2() {
        showLoading()
        sliderAdapter.clear()

        theNewCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .addOnSuccessListener { documents ->
                hideLoading()
                if (documents.isEmpty) {
                } else {
                    val dataList = documents.map { documentSnapshot ->
                        val id = documentSnapshot.id
                        val data = documentSnapshot.toObject(TheNewRequest::class.java)
                        TheNewItem(id, data)
                    }
                    sliderAdapter.refresh(dataList)
                }
            }.addOnFailureListener {
                hideLoading()
                toast(R.string.error_400)
            }
    }
    val handler = Handler(Looper.getMainLooper())
    val runnable = object : Runnable {
        var currentPage = 0

        override fun run() {
            if (sliderAdapter.itemCount > 0) {
                currentPage = (currentPage + 1) % sliderAdapter.itemCount
                viewpager_2.currentItem = currentPage
            }
            handler.postDelayed(this, 10000)
        }
    }

    override fun backPressed(): Boolean {
        return true
    }

    override fun onResume() {
        super.onResume()
        getUserInfor()
    }
}