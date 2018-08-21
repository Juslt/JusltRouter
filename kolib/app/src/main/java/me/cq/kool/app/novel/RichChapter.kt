package me.cq.kool.app.novel

import android.content.res.Resources
import android.graphics.*
import android.util.Log
import me.cq.kool.app.R
import me.cq.kool.dip2px
import me.cq.kool.sp2px
import java.util.regex.Pattern

/*
1，获取需要绘制的页面的宽高
2，计算页面可以容纳的行数，以及每行能容纳的字数
3，从给定的章节读取字符串，用string流来一个个读。
    细节：
    换行符占一整行，如果在某个字后面则占当前行剩余所有位置，
    起始tag当做两个字来处理，结束tag不占字符位置，
    tag用来绘制对应的两个字宽度大小的图标，绘制的时候不绘制结束tag，只在起始tag处绘制
4，一个字一个字的绘制，遇到起始tag则绘制对应的图标，遇到结束tag不绘制，需要保存当前的图标的位置，用于处理点击事件
 */

data class Page(
        val lines: ArrayList<Line> = ArrayList(),
        val tagRect: ArrayList<Rect> = ArrayList(),
        var bitmap: Bitmap? = null
)

data class Line(
        val slices: ArrayList<Slice> = ArrayList()
){
    fun length() : Int{
        return slices.sumBy { it.content.length }
    }

    fun hasTag() : Boolean{
        return slices.find { it.isTag }!=null
    }
}

data class Slice(
        val content: String,
        val isTag: Boolean = false
)

data class Tag(
        val start: Int = 0,
        val end: Int = 0,
        val content: String = "",
        val isOpening: Boolean = false,
        val tagWidth: Int = 1 //tag所占据的字符宽度
){
    fun inTag(index: Int) : Boolean{
        return index in start..(end - 1)
    }
}

data class PageConfig(
    var pageWidth: Int,
    var pageHeight: Int,
    //页面padding
    val pagePadding: Float = 15.dip2px,
    //字体大小
    val fontSize: Float = 16.sp2px,
    //行间距
    val lineSpacing: Float = 20f
){
    val contentWidth: Float = pageWidth-pagePadding*2
    val contentHeight: Float = pageHeight-pagePadding*2
}

class RichChapter(
    private var resource: Resources,
    private val pageConfig: PageConfig
){

    private val textPaint = Paint()

    //一些测量的数据
    private var numCharPerLine = 0
    private var numLinePerPage = 0

    //html标签匹配正则表达式
    val pattern = Pattern.compile("<([^>]*)>")

    //章节内容
    var src: String = ""
    //包含当前章节所有行
    private val pageList = ArrayList<Page>()
    //当前页码
    private var currentPageIndex = 0

    //初始化章节，进行一些分页，分行，正则匹配等操作
    fun prepare(chapterStr: String){
        src = chapterStr

        preparePaint()

        //每行字数
        numCharPerLine = textPaint.breakText(strMeasure, true, pageConfig.contentWidth, null)
        //行数
        numLinePerPage = ((pageConfig.pageHeight-pageConfig.lineSpacing)/(pageConfig.fontSize+pageConfig.lineSpacing)).toInt()

        convert()
    }

    fun pageAt(index: Int) : Page {
        return drawPage(pageList[index])
    }

    fun next() : Page {
        if(currentPageIndex>=pageList.size){
            return Page()
        }
        //清空Bitmap
        pageList.forEach { it.bitmap = null }
        val drawPage = drawPage(pageList[currentPageIndex])
        currentPageIndex++
        return drawPage
    }

    private fun drawPage(page: Page) : Page {
        val bitmap = Bitmap.createBitmap(pageConfig.pageWidth, pageConfig.pageHeight, Bitmap.Config.RGB_565)
        val c = Canvas(bitmap)
        c.drawColor(Color.WHITE)

        if (page.lines.isNotEmpty()) {

            //绘制内容
            var y = pageConfig.fontSize + pageConfig.pagePadding
            //遍历行
            for (lineIndex in page.lines.indices) {
                val line = page.lines[lineIndex]
                //检查当前行是否有标签
                val hasTag = line.hasTag()
                if(!hasTag){
                    //没有标签就是只有一行
                    c.drawText(line.slices[0].content, pageConfig.pagePadding, y, textPaint)
                }else{
                    //按照slice绘制
                    var startX = pageConfig.pagePadding
                    line.slices.forEach {
                        if(it.isTag){
                            if(!it.content.startsWith("</")){
                                val bmp = BitmapFactory.decodeResource(resource, R.mipmap.ic_launcher_round)
                                val src = Rect(0,0,bmp.width,bmp.height)
                                val dst = Rect(startX.toInt(), (y-pageConfig.fontSize).toInt(),startX.toInt()+50,(y-pageConfig.fontSize).toInt()+50)
                                page.tagRect.add(dst)
                                c.drawBitmap(bmp,src,dst,textPaint)
                                startX += 50
                            }
                        }else{
                            c.drawText(it.content,startX,y,textPaint)
                            startX += textPaint.measureText(it.content)
                        }
                    }
                }
                y += pageConfig.fontSize + pageConfig.lineSpacing
            }

        }
        page.bitmap = bitmap
        return page
    }


    //把当前章节拆成page
    private fun convert(){
        val tagList = preReadTags(src)//包含当前章节所有tag
        val lineList = ArrayList<Line>()//包含当前章节所有行

        //按照行 分割章节
        var start = 0
        while (start< src.length){
            val readLine = readLine(start, numCharPerLine, src,tagList)
            start += readLine.length()
            lineList.add(readLine)
        }

        //分成每页
        var startLine = 0
        pageList.clear()
        while (startLine<lineList.size){
            val page = Page()
            (0..numLinePerPage).forEach {
                if(startLine>=lineList.size){
                    return@forEach
                }
                page.lines.add(lineList[startLine++])
            }
            pageList.add(page)
        }
    }

    //读取一行
    private fun readLine(start: Int, charNum: Int, src: String,tagList: ArrayList<Tag>) : Line {
        val builder = StringBuilder()
        var lineLength = charNum //当前行最大长度
        var i = 0 //读取次数
        while (i<lineLength && (start+i)<src.length){//最多读取次数
            //读取下一个字
            val s = src[start+i]
            if(s == '\n'){
                //如果读到换行符，则直接添加后返回
                builder.append(s)
                break
            }else if(s == '<'){
                //如果是以<,并且是在当前的index在标签列表中，说明读到了标签，则读入整个标签
                tagList.forEach {
                    if(it.inTag(start+i)){
                        //加入当前标签的所有内容后继续读取
                        builder.append(it.content)
                        lineLength += if(it.isOpening){
                            it.content.length-1
                        }else{
                            //结束标签不占位置
                            it.content.length
                        }
                        i += it.content.length
                        return@forEach
                    }
                }
            }else{
                //否则是正常字符
                builder.append(s)
                i++
            }
        }

        //切片,把连续的文字或者标签设置成一个slice
        val s = builder.toString()
        val line = Line()
        val m = pattern.matcher(s)
        var startPos = 0
        while (m.find()){
            val start = m.start()
            val end = m.end()
            if(startPos<start){
                line.slices.add(Slice(s.substring(startPos, start)))
            }
            line.slices.add(Slice(m.group(), true))
            startPos = end
        }
        if(startPos<s.length){
            line.slices.add(Slice(s.substring(startPos, s.length)))
        }
        return line
    }

    private fun preparePaint(){
        textPaint.isAntiAlias = true
        textPaint.textAlign = Paint.Align.LEFT
        textPaint.textSize = pageConfig.fontSize
        textPaint.color = Color.BLACK
        textPaint.typeface = null
    }

    //查找所有的标签
    private fun preReadTags(src: String) : ArrayList<Tag>{
        val list = ArrayList<Tag>()
        val m = pattern.matcher(src)
        while (m.find()) {
            list.add(Tag(m.start(), m.end(), m.group(), !m.group().startsWith("</")))
        }
        Log.i("find_tags",list.toString())
        return list
    }

    val strMeasure = "测量测量测量测量测量测.量测量测量测量测量测量测量测量测量测量测量测量测量测量测量测量测量"
}