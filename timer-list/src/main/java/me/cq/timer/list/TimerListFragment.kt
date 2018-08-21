package me.cq.timer.list

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.*
import android.widget.*
import com.gx.timer.controller.TimerBus
import com.gx.timer.controller.TimerDevice
import com.gx.timer.controller.TimerSession
import kotlinx.android.synthetic.main.f_timer_list.*
import me.cq.kool.common.Msg
import me.cq.kool.ext.dip2px
import me.cq.kool.ext.secondToHourStr
import me.cq.kool.post
import me.cq.kool.toast
import me.cq.kool.ui.recyclerview.BaseViewHolder
import me.cq.kool.ui.recyclerview.RVBaseAdapter
import me.cq.kool.ui.recyclerview.initVertical
import me.cq.router.api.Router
import me.cq.timer.common.*
import me.cq.timer.common.database.DBManager
import me.cq.timer.common.lb.EventLBFilter
import me.cq.timer.common.lb.LBDispatcher
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.find
import java.io.Serializable
import java.util.*

class TimerListFragment : BaseFragment(){

    companion object {
        const val TYPE_ALL = 0
        const val TYPE_SIMPLE = 1
        const val TYPE_MULTI = 2
        const val TYPE_INTERVAL = 3

        fun create(type: Int) : TimerListFragment {
            val fragment = TimerListFragment()
            fragment.arguments = bundleOf("type" to type)
            return fragment
        }
    }

    private val itemTouchHelper by lazy {
        ItemTouchHelper(
                object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN,0) {

                    var lastHolder: RecyclerView.ViewHolder?=null

                    override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
                        //更新order
                        val from = viewHolder!!.adapterPosition
                        val to = target!!.adapterPosition
                        val fromOrder = list[from].order
                        val toOrder = list[to].order
                        list[from].order = toOrder
                        list[to].order = fromOrder
                        Collections.swap(list,from,to)
                        rv.adapter.notifyItemMoved(from,to)
                        return true
                    }

                    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                        super.onSelectedChanged(viewHolder, actionState)
                        //选中，需要改变显示状态
                        when(viewHolder){
                            null -> {
                                (lastHolder as? TimerSubListHolder)?.move(false)
                                lastHolder = null
                                //更新数据库
                                list.forEach {
                                    DBManager.update(it)
                                }
                            }
                            is TimerSubListHolder -> {
                                lastHolder = viewHolder
                                (lastHolder as TimerSubListHolder).move(true)
                            }
                        }
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {

                    }

                    override fun isLongPressDragEnabled(): Boolean {
                        return false
                    }
                }
        )
    }

    private val type by lazy { arguments!!.getInt("type") }
    private val list = ArrayList<TimerVo>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.f_timer_list,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        rv.initVertical()
        itemTouchHelper.attachToRecyclerView(rv)
        rv.adapter = TimerSubListAdapter(
                {
                    when(it.what){
                        //单击
                        0 -> {
                            val i = Router.buildIntent(context!!, "/timer")
                            i!!.putExtra("timer_vo",it.obj as Serializable)
                            startActivity(i)
                        }
                        //长按
                        1 -> {
                            //长按进入的是单个的编辑，不可移动
                            val timerVo = (it.obj as TimerVo)
                            if(timerVo.mode!=0){
                                return@TimerSubListAdapter
                            }
                            timerVo.mode=2
                            updateList()
                        }
                        //删除
                        2 -> {
                            //需要对话框确认
                            AlertDialog.Builder(context!!)
                                    .setTitle(getString(R.string.exit))
                                    .setPositiveButton(R.string.delete) { _, _ ->
                                        Timers.remove(it.obj as TimerVo)
                                        list.remove(it.obj as TimerVo)
                                        updateList()
                                    }.setNegativeButton(R.string.cancel){ _, _ ->}
                                    .show()
                        }
                        //编辑
                        3 -> {
                            val i = Router.buildIntent(context!!, "/edit")
                            i!!.putExtra("timer_vo",it.obj as Serializable)
                            startActivity(i)
                        }
                        //开始计时
                        4 -> {
                            var timerSession = TimerBus.get((it.obj as TimerVo).id)
                            when {
                                timerSession==null -> {
                                    timerSession = TimerBus.add((it.obj as TimerVo))
                                    timerSession.start()
                                }
                                timerSession.isStarted() -> {
                                    timerSession.stop()
                                    TimerBus.remove((it.obj as TimerVo).id)
                                    updateList()
                                }
                                else -> timerSession.start()
                            }
                            post(10) { updateList() }
                        }
                    }
                },
                itemTouchHelper
        )

        when(type){
            TYPE_SIMPLE -> initSimple()
            TYPE_MULTI -> initMulti()
            TYPE_INTERVAL -> initInterval()
            else -> initAll()
        }

        LBDispatcher.instance().register(this, EventLBFilter.eventTimerListRefresh)
    }

    override fun onResume() {
        super.onResume()
        updateList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LBDispatcher.instance().unregister(this)
    }

    override fun lbCallback(context: Context?, intent: Intent?) {
        when(intent?.action){
            EventLBFilter.eventTimerListRefresh -> {
                when(type){
                    TYPE_SIMPLE -> initSimple()
                    TYPE_MULTI -> initMulti()
                    TYPE_INTERVAL -> initInterval()
                    else -> initAll()
                }
            }
        }
    }

    fun editMode(){
        list.forEach { it.mode = 1 }
        updateList()
    }

    fun cancelEdieMode(){
        list.forEach { it.mode = 0 }
        updateList()
        LBDispatcher.instance().send(EventLBFilter.eventTimerListRefresh)
    }

    private fun initAll(){

        list.clear()
        list.addAll(Timers.simpleList)
        list.addAll(Timers.multiList)
        list.addAll(Timers.intervalList)
        list.sortBy { it.order }
        updateList()
    }

    private fun initInterval(){
        list.clear()
        list.addAll(Timers.intervalList)
        updateList()
    }

    private fun initSimple(){
        list.clear()
        list.addAll(Timers.simpleList)
        updateList()
    }

    private fun initMulti(){
        list.clear()
        list.addAll(Timers.multiList)
        updateList()
    }

    private fun updateList(){
        (rv.adapter as RVBaseAdapter).update(list)
        if(list.isEmpty()){
            ll_empty.visibility = View.VISIBLE
        }else{
            ll_empty.visibility = View.GONE
        }
    }

    class TimerSubListAdapter(msgInterface: ((msg: Msg)->Unit)?=null,val itemTouchHelper: ItemTouchHelper) : RVBaseAdapter(msgInterface){

        override fun onCreateViewHolderAfterFooter(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return TimerSubListHolder(parent, this)
        }
    }

    class TimerSubListHolder(parent: ViewGroup, adapter: RVBaseAdapter) : BaseViewHolder(R.layout.h_timer_list, parent, adapter) {

        val cv by lazy { itemView.find<FrameLayout>(R.id.cv) }
        val pb by lazy { itemView.find<ProgressBar>(R.id.pb) }
        private val llBg by lazy { itemView.find<LinearLayout>(R.id.ll_bg) }
        private val llInfo by lazy { itemView.find<LinearLayout>(R.id.ll_info) }
        private val tvName by lazy { itemView.find<TextView>(R.id.tv_name) }
        private val tvTime by lazy { itemView.find<TextView>(R.id.tv_time) }
//        private val ivPlay by lazy { itemView.find<ImageView>(R.id.iv_play) }
        private val vPlayState by lazy { itemView.find<PlayStateView>(R.id.v_play_state) }
        private val ivDelete by lazy { itemView.find<ImageView>(R.id.iv_delete) }
        private val ivEdit by lazy { itemView.find<ImageView>(R.id.iv_edit) }
        private val ivMove by lazy { itemView.find<ImageView>(R.id.iv_move) }

        init {
            tvTime.typeface = Typeface.createFromAsset(itemView.context.assets,"fonts/din.ttf")
        }

        val padding = 16.dip2px.toInt()
        private lateinit var timerVo: TimerVo
        private var timerSession: TimerSession?=null

        override fun update(obj: Any, position: Int) {

            llBg.setPadding(padding,padding,padding,padding)
            pb.max = 100

            timerVo = obj as TimerVo
            timerSession = TimerBus.get(timerVo.id)

            //拖动
            ivMove.setOnTouchListener { _, event ->
                if(event.action == MotionEvent.ACTION_DOWN) {
                    (mAdapter as TimerSubListAdapter).itemTouchHelper.startDrag(this)
                }
                false
            }
            //删除
            ivDelete.setOnClickListener { mAdapter.event(Msg(what = 2,obj = timerVo)) }
            //编辑
            ivEdit.setOnClickListener { mAdapter.event(Msg(what = 3,obj = timerVo)) }
            //进入计时器界面
            cv.setOnClickListener { mAdapter.event(Msg(what = 0,obj = timerVo)) }
            //单个编辑
            cv.setOnLongClickListener {
                //如果有正在运行的，则不能编辑
                if(timerSession!=null && timerSession!!.isStarted()){
                    toast("计时器正在运行状态，无法进行编辑")
                    return@setOnLongClickListener true
                }
                mAdapter.event(Msg(what = 1,obj = obj))
                true
            }
            //开始计时
            vPlayState.setOnClickListener { mAdapter.event(Msg(what = 4,obj = timerVo)) }

            //三种模式，分别展示三种ui
            when(timerVo.mode){
                0->{
                    ivDelete.visibility = View.GONE
                    ivEdit.visibility = View.GONE
                    ivMove.visibility = View.GONE
                    vPlayState.visibility = View.VISIBLE
                    llInfo.gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
                }
                1->{
                    ivDelete.visibility = View.VISIBLE
                    ivEdit.visibility = View.VISIBLE
                    ivMove.visibility = View.VISIBLE
                    vPlayState.visibility = View.GONE
                    llInfo.gravity = Gravity.CENTER
                }
                2->{
                    ivDelete.visibility = View.VISIBLE
                    ivEdit.visibility = View.VISIBLE
                    ivMove.visibility = View.GONE
                    vPlayState.visibility = View.GONE
                    llInfo.gravity = Gravity.CENTER
                }
            }
            //未开始计时和开始计时， 两种ui
            if(timerSession==null){
                pb.visibility = View.GONE
                vPlayState.play(0f,false)
                ivEdit.setImageResource(R.mipmap.list_ic_edit)
                ivEdit.isClickable = true
                ivDelete.isClickable = true
            }else{
                if(timerVo.mode==0){
                    pb.visibility = View.GONE
                }else{
                    pb.visibility = View.VISIBLE
                    pb.progress=0
                }
                ivEdit.setImageResource(R.mipmap.list_ic_edit_disable)
                ivEdit.isClickable = false
                ivDelete.isClickable = false
                updateUIByTimer(timerSession!!.getCurrentTime(),timerVo.totalSeconds,!timerSession!!.isStarted())
            }

            //处理数据
            tvName.text = obj.name
            tvTime.text = obj.totalSeconds.secondToHourStr


            //需要检查当前对象是否在timer中是计时项，如果是，则需要在timer中注册，用于实时更新进度条
            if(timerSession!=null && timerSession!!.isStarted()){

                timerSession!!.listen(object : TimerDevice.TickCallback{

                    override fun onPause(past: Long, total: Long, id: String) {
                        if(id == timerVo.id){
                            updateUIByTimer(past,total,true)
                        }
                    }

                    override fun onTick(past: Long, total: Long, id: String) {
                        if(id == timerVo.id){
                            updateUIByTimer(past,total,false)
                        }
                    }

                    override fun onEnd(id: String) {
                        if(id == timerVo.id){
                            pb.progress = 0
                            pb.visibility = View.GONE
                            vPlayState.play(0f,false)
                            timerSession=null
                        }
                    }

                })
            }
        }

        private fun updateUIByTimer(past: Long, total: Long, pause: Boolean){
            //检查holder的复用情况，防止乱刷新
            pb?.post {
                //需要判断当前计时器是否暂停
                pb.progress = (past.toFloat()*100/timerVo.totalSeconds/1000).toInt()
                vPlayState.play((past.toFloat()*100/timerVo.totalSeconds/1000),pause)
                tvTime.text = (timerVo.totalSeconds-(Math.round(past.toFloat()/1000))).secondToHourStr
            }
        }

        fun move(boolean: Boolean){
            if(boolean){
                ivEdit.visibility = View.GONE
                ivDelete.visibility = View.GONE
                ivMove.setImageResource(R.mipmap.list_ic_move_disable)
                llBg.setBackgroundResource(R.drawable.bg_shadow)
            }else{
                ivEdit.visibility = View.VISIBLE
                ivDelete.visibility = View.VISIBLE
                ivMove.setImageResource(R.mipmap.list_ic_move)
                llBg.setBackgroundResource(R.drawable.bg_shadow_light)
            }
        }

    }
}