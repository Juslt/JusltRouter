package com.gx.timer.fragment.new

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gx.timer.R
import com.gx.timer.activity.CreateTimerActivity
import kotlinx.android.synthetic.main.f_create_timer.*
import me.cq.timer.common.BaseFragment
import me.cq.timer.common.Timers
import me.cq.timer.common.database.DBManager
import me.cq.timer.common.lb.EventLBFilter
import me.cq.timer.common.lb.LBDispatcher
import org.jetbrains.anko.support.v4.toast

class CreateTimerFragment : BaseFragment(){

    val fragmentList by lazy {
        arrayOf(
                CreateTimerPagerFragment.create(CreateTimerPagerFragment.TYPE_SIMPLE),
                CreateTimerPagerFragment.create(CreateTimerPagerFragment.TYPE_MULTI),
                CreateTimerPagerFragment.create(CreateTimerPagerFragment.TYPE_INTERVAL)
        )
    }
    val fragmentTitleList by lazy {
        arrayOf(
                getString(R.string.simple),
                getString(R.string.multi),
                getString(R.string.interval)
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.f_create_timer,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        title_bar.ivClose.setImageResource(R.mipmap.ic_close_balck)
        title_bar.ivClose.setOnClickListener { activity?.finish() }
        title_bar.tvTitle.text = getString(R.string.new_timer)
        title_bar.ivRight.setImageResource(R.mipmap.ic_arrow_right_black)
        title_bar.ivRight.setOnClickListener { finishCreateTimer() }
        title_bar.ivRight.visibility = View.VISIBLE

        view_pager.offscreenPageLimit = 3
        view_pager.adapter = object : FragmentPagerAdapter(childFragmentManager){
            override fun getCount(): Int {
                return fragmentList.size
            }

            override fun getItem(position: Int): Fragment {
                return fragmentList[position]
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return fragmentTitleList[position]
            }
        }

        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                when(position){
                    0 -> (activity as CreateTimerActivity).type = CreateTimerPagerFragment.TYPE_SIMPLE
                    1 -> (activity as CreateTimerActivity).type = CreateTimerPagerFragment.TYPE_MULTI
                    2 -> (activity as CreateTimerActivity).type = CreateTimerPagerFragment.TYPE_INTERVAL
                }
            }
        })

        tab_layout.setupWithViewPager(view_pager)
    }

    override fun onResume() {
        super.onResume()
        when((activity as CreateTimerActivity).type){
            CreateTimerPagerFragment.TYPE_SIMPLE -> view_pager.currentItem = 0
            CreateTimerPagerFragment.TYPE_MULTI -> view_pager.currentItem = 1
            CreateTimerPagerFragment.TYPE_INTERVAL -> view_pager.currentItem = 2
        }
    }

    private fun finishCreateTimer(){

        val parentActivity = activity as CreateTimerActivity
        when(parentActivity.type){
            CreateTimerPagerFragment.TYPE_SIMPLE -> createSimpleTimer(parentActivity)
            CreateTimerPagerFragment.TYPE_MULTI -> createMultiTimer(parentActivity)
            CreateTimerPagerFragment.TYPE_INTERVAL -> createIntervalTimer(parentActivity)
        }
    }

    private fun createIntervalTimer(parentActivity: CreateTimerActivity){

        val timerVo = parentActivity.intervalTimerVo

        //校验时间是否设置
        if(timerVo.name.isNullOrEmpty()){
            toast(getString(R.string.fill_timer_name_please))
            return
        }
        if(timerVo.secondsTraining<=0){
            toast(getString(R.string.training_time_can_be_0))
            return
        }
        //插入到数据库
        val result = DBManager.insert(timerVo)
        if(result==null || result<0){
            toast(getString(R.string.timer_create_fail_retry_please))
            return
        }

        toast("计时器创建成功")
        parentActivity.finish()
        Timers.updateAll()
    }


    private fun createMultiTimer(parentActivity: CreateTimerActivity){

        val timerVo = parentActivity.multiTimerVo

        //校验时间是否设置
        if(timerVo.name.isNullOrEmpty()){
            toast(getString(R.string.fill_timer_name_please))
            return
        }
        if(timerVo.multiSubTimerVoList.isEmpty()){
            toast(getString(R.string.sub_timer_quantity_can_not_be_0))
            return
        }
        //插入到数据库
        val result = DBManager.insert(timerVo)
        if(result==null || result<0){
            toast(getString(R.string.timer_create_fail_retry_please))
            return
        }

        toast("计时器创建成功")
        parentActivity.finish()
        Timers.updateAll()
    }

    private fun createSimpleTimer(parentActivity: CreateTimerActivity){

        val timerVo = parentActivity.simpleTimerVo

        //校验时间是否设置
        if(timerVo.name.isNullOrEmpty()){
            toast(getString(R.string.fill_timer_name_please))
            return
        }
        if(timerVo.seconds<=0){
            toast(getString(R.string.timer_seconds_should_not_be_0))
            return
        }
        //插入到数据库
        val result = DBManager.insert(timerVo)
        if(result==null || result<0){
            toast(getString(R.string.timer_create_fail_retry_please))
            return
        }

        toast("计时器创建成功")
        parentActivity.finish()
        Timers.updateAll()
    }

}