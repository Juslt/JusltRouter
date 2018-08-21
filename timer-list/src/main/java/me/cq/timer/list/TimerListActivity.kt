package me.cq.timer.list

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import kotlinx.android.synthetic.main.a_timer_list.*
import me.cq.kool.utils.ResHelper
import me.cq.router.annotation.Route
import me.cq.router.api.Router
import me.cq.timer.common.BaseActivity
import me.cq.timer.common.Sounder
import me.cq.timer.common.TimerVo
import me.cq.timer.common.Timers
import me.cq.timer.common.database.DBManager
import me.cq.timer.common.lb.EventLBFilter
import me.cq.timer.common.lb.LBDispatcher
import me.cq.timer.common.widget.ExtViewPager

@Route(path = "list")
class TimerListActivity : BaseActivity(){

    val fragmentList by lazy {
        arrayOf(
                TimerListFragment.create(TimerListFragment.TYPE_ALL),
                TimerListFragment.create(TimerListFragment.TYPE_SIMPLE),
                TimerListFragment.create(TimerListFragment.TYPE_MULTI),
                TimerListFragment.create(TimerListFragment.TYPE_INTERVAL)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_timer_list)

        LBDispatcher.instance().register(this,EventLBFilter.eventTimerListRefresh)

        btn_add.setOnClickListener {
            val intent = Router.buildIntent(this, "/create")
            startActivity(intent)
        }
        //检查一共有多少个计时器，按照计时器来显示页面
        if(Timers.isEmpty()){
            iv_menu.visibility = View.GONE
        }

        val list = ArrayList<TimerVo>()
        list.addAll(DBManager.querySimpleList())
        list.addAll(DBManager.queryIntervalList())
        list.addAll(DBManager.queryMultiList())
        if(list.size < 5){
            ll_tab.visibility = View.GONE
            view_pager.visibility = View.GONE
            fl_sub_container.visibility = View.VISIBLE

            supportFragmentManager.beginTransaction()
                    .replace(R.id.fl_sub_container,fragmentList[0])
                    .commit()

            iv_menu.setOnClickListener {
                fragmentList[0].editMode()
                iv_complete.visibility = View.VISIBLE
                btn_add.visibility = View.GONE
                iv_complete.setOnClickListener {
                    iv_complete.visibility = View.GONE
                    btn_add.visibility = View.VISIBLE
                    fragmentList[0].cancelEdieMode()
                }
            }
            return
        }


        iv_menu.setOnClickListener {
            fragmentList[view_pager.currentItem].editMode()
            showCompleteOpt()
        }

        view_pager.adapter = object : FragmentPagerAdapter(supportFragmentManager){
            override fun getItem(position: Int): Fragment {
                return fragmentList[position]
            }

            override fun getCount(): Int {
                return fragmentList.size
            }

        }
        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                tv_item_all.setTextColor(ResHelper.getColor(R.color.text_normal_30_per))
                tv_item_simple.setTextColor(ResHelper.getColor(R.color.text_normal_30_per))
                tv_item_multi.setTextColor(ResHelper.getColor(R.color.text_normal_30_per))
                tv_item_interval.setTextColor(ResHelper.getColor(R.color.text_normal_30_per))
                when(position){
                    0 -> tv_item_all.setTextColor(ResHelper.getColor(R.color.text_normal))
                    1 -> tv_item_simple.setTextColor(ResHelper.getColor(R.color.text_normal))
                    2 -> tv_item_multi.setTextColor(ResHelper.getColor(R.color.text_normal))
                    3 -> tv_item_interval.setTextColor(ResHelper.getColor(R.color.text_normal))
                }
            }
        })

        tv_item_all.setOnClickListener { view_pager.currentItem = 0 }
        tv_item_simple.setOnClickListener { view_pager.currentItem = 1 }
        tv_item_multi.setOnClickListener { view_pager.currentItem = 2 }
        tv_item_interval.setOnClickListener { view_pager.currentItem = 3 }


        tv_item_all.setTextColor(ResHelper.getColor(R.color.text_normal))

    }

    override fun lbCallback(context: Context?, intent: Intent?) {
        if(intent?.action == EventLBFilter.eventTimerListRefresh){
            if(Timers.isEmpty()){
                iv_menu.visibility = View.GONE
            }else{
                iv_menu.visibility = View.VISIBLE
            }
        }
    }

    private fun enableTabClick(enable: Boolean){
        tv_item_all.isClickable = enable
        tv_item_simple.isClickable = enable
        tv_item_multi.isClickable = enable
        tv_item_interval.isClickable = enable
    }

    private fun showCompleteOpt(){
        //禁止viewpager 滑动
        (view_pager as ExtViewPager).enableScrollable(false)
        iv_complete.visibility = View.VISIBLE
        btn_add.visibility = View.GONE
        enableTabClick(false)
        iv_complete.setOnClickListener {
            //允许viewpager 滑动
            (view_pager as ExtViewPager).enableScrollable(true)
            enableTabClick(true)
            iv_complete.visibility = View.GONE
            btn_add.visibility = View.VISIBLE
            fragmentList[view_pager.currentItem].cancelEdieMode()
        }

        if(Timers.isEmpty()){
            iv_menu.visibility = View.GONE
        }else{
            iv_menu.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Sounder.destroy()
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
                .setTitle(getString(R.string.exit))
                .setPositiveButton(R.string.submit) { _, _ ->
                    finish()
                }.setNegativeButton(R.string.cancel){ _, _ ->}
                .show()
    }

}