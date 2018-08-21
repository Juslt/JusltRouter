package me.cq.kool.ui.recyclerview

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.ViewGroup
import me.cq.kool.common.IMsg
import me.cq.kool.common.Msg
import java.util.ArrayList

/**
 * Created by phelps on 2018/1/29 0029.
 */

abstract class RVBaseAdapter(private var msgInterface: ((msg: Msg)->Unit)?=null, protected var mLoadMore: LoadMore?=null) : RecyclerView.Adapter<BaseViewHolder>() {
    protected var mMoreFooterVo = RVFooterVo()
    protected var dataList = ArrayList<Any>()
    protected var mRv: RecyclerView? = null
    private var autoLoad: Boolean = false
    val map = HashMap<Any,Any>()

    fun autoLoadMore(auto: Boolean) {
        autoLoad = auto
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        mRv = recyclerView
        if (mRv!!.layoutManager == null) {
            throw IllegalArgumentException("Init layoutManager first")
        }

        //处理gridLayoutManager
        if (mRv!!.layoutManager is GridLayoutManager) {
            (mRv!!.layoutManager as GridLayoutManager).spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (mLoadMore != null && mRv!!.adapter.itemCount - 1 == position) {
                        (mRv!!.layoutManager as GridLayoutManager).spanCount
                    } else 1
                }
            }
        }

        configLoadMore()
    }

    //配置自动加载
    private fun configLoadMore() {
        if (mRv == null) {
            return
        }
        if (!autoLoad) {
            return
        }
        mRv!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            private var layoutManagerType = 0
            private var lastPositions: IntArray? = null
            private var lastVisibleItemPosition: Int = 0
            private var currentScrollState = 0

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView!!.layoutManager
                if (layoutManagerType == 0) {
                    if (layoutManager is LinearLayoutManager) {
                        layoutManagerType = 1
                    } else if (layoutManager is GridLayoutManager) {
                        layoutManagerType = 2
                    } else if (layoutManager is StaggeredGridLayoutManager) {
                        layoutManagerType = 3
                    } else {
                        throw RuntimeException("unknown LayoutManager")
                    }
                }

                when (layoutManagerType) {
                    1 -> lastVisibleItemPosition = (layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                    2 -> lastVisibleItemPosition = (layoutManager as GridLayoutManager).findLastVisibleItemPosition()
                    3 -> {
                        val staggeredGridLayoutManager = layoutManager as StaggeredGridLayoutManager
                        if (lastPositions == null) {
                            lastPositions = IntArray(staggeredGridLayoutManager.spanCount)
                        }
                        staggeredGridLayoutManager.findLastVisibleItemPositions(lastPositions)
                        lastVisibleItemPosition = findMax(lastPositions)
                    }
                }

            }

            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                currentScrollState = newState
                val layoutManager = recyclerView!!.layoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                if (visibleItemCount > 0 && currentScrollState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItemPosition >= totalItemCount - 1) {
                    //加载更多
                    if (mMoreFooterVo != null && mMoreFooterVo!!.status === RVFooterVo.NORMAL) {
                        mMoreFooterVo!!.status = RVFooterVo.LOADING
                        notifyItemChanged(itemCount - 1)
                        mLoadMore!!.loadMoreEvent()
                    }
                }
            }


            private fun findMax(lastPositions: IntArray?): Int {
                var max = lastPositions!![0]
                for (value in lastPositions) {
                    if (value > max) {
                        max = value
                    }
                }
                return max
            }
        })
    }

    private fun addLoadMoreFooter(hasMore: Boolean) {
        if (mLoadMore != null && itemCount > 0) {
            mMoreFooterVo!!.update(hasMore)
            dataList.add(dataList.size, mMoreFooterVo!!)
        }
    }

    @JvmOverloads
    fun update(obj: Any, hasMore: Boolean = false) {
        dataList.clear()
        dataList.addAll(obj as Collection<Any>)
        notifyByFooter(hasMore)
    }

    //判断item类型，过滤了加载更多的item holder
    open fun getItemViewTypeAfterFooter(position: Int): Int {
        return 0
    }

    abstract fun onCreateViewHolderAfterFooter(parent: ViewGroup, viewType: Int): BaseViewHolder

    override fun getItemViewType(position: Int): Int {
        return if (dataList[position] is RVFooterVo) {
            FOOTER
        } else {
            getItemViewTypeAfterFooter(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return if (viewType == FOOTER) {
            MoreFooterHolder.create(parent, this)
        } else {
            onCreateViewHolderAfterFooter(parent, viewType)
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.reset()
        holder.update(dataList[position], position)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun event(msg: Msg) {
        if (msgInterface != null) {
            msgInterface!!.invoke(msg)
        }
    }

    //加载更多
    fun loadMore() {
        if (mLoadMore != null) {
            mLoadMore!!.loadMoreEvent()
        }
    }

    interface LoadMore {
        fun loadMoreEvent()
    }

    fun notifyByFooter(hasMore: Boolean) {
        addLoadMoreFooter(hasMore)
        notifyDataSetChanged()
    }

    companion object {

        val FOOTER = -1
    }

}
