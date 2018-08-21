package me.cq.kool.app.novel

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.widget.Button
import android.widget.ImageView
import me.cq.kool.Kool
import me.cq.kool.app.R
import me.cq.kool.toast

/**
 * Created by cq on 2017/12/11.
 */
class NovelActivity : AppCompatActivity() {

    val iv by lazy { findViewById<ImageView>(R.id.iv) }
    val btn by lazy { findViewById<Button>(R.id.btn) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_novel)

        Kool.init(applicationContext)

        iv.post {
            val pageConfig = PageConfig(iv.measuredWidth, iv.measuredHeight)
            val richChapter = RichChapter(this.resources, pageConfig)
            richChapter.prepare(str)

            btn.setOnClickListener {
                val page =richChapter.next()
                if(page.bitmap==null){
                    toast("没有更多")
                    return@setOnClickListener
                }
                iv.setImageBitmap(page.bitmap)
                iv.setOnTouchListener { v, event ->
                    if(event.action == MotionEvent.ACTION_DOWN){
                        page.tagRect.forEach {
                            if(it.contains(event.x.toInt(), event.y.toInt())){
                                toast("点到了")
                            }
                        }
                    }
                    super.onTouchEvent(event)
                }
            }
            btn.performClick()
        }
    }
}

val str = "这几年，市面上有关励志的书可谓多<span data-seg-id=\"1\">矣。今天我要为你推荐</span>的却是别有特色的一本。这就像窗外的春花般一样，有迎春，有玉兰，有丁香，我特别挑出一种，说这朵花儿很美，值得欣赏，那必有其中的道理。\n" +
        "\n" +
        "　　励志的“励”字，我记得查过词典，说是将金属制造的刀具放置于平滑的石块上，上下磨动以求锋利之意。可见，如果将人的意志放置于一种特定的场合，加以磨练，那当然是一种“玉汝于成”之事。不过，这“励”的方法，尚有不同。有的是教你如何开始“磨动”的，有的是教你如何快速“磨动”的，这本书却是教你如何持久“磨动”的。要知道，在方向正确的前提下，成功者与不成功者的区别，往往在于能否坚持不断，能否坚持到底，所谓“水滴石穿”是也。人们往往把这归之于“意志”，其实，有一种比“意志”要轻松得多的好方法，那就是让习惯性动作植根于你的潜意识。11111111111<span data-seg-id=\"1\">111\n" +
        "\n" +
        "　　人的意识有潜显之分</span>。显意识是你警醒状态的知觉活动，而潜意识则在显意识之下，静静等待。如果把人的意识比作海上冰山的话，显意识仅仅是冰山一角。人类的许多奥妙，就存在于潜意识之中。学者们的研究指出，一个人的日常活动，90%已经通过不断地重复某个动作，在潜意识中，转化为程序化的惯性。也就是，不用思考，便自动运作。这种自动运作的力量，即习惯的力量。它是非常巨大的，长此以往将使其主体将发生巨大的变化。\n" +
        "\n" +
        "　　如果抽象地讲，就是：行为变为习惯，习惯养成性格，性格决定命运。一个动作，一种行为，多次重复，就能进入人的潜意识，变成习惯性动作。人的知识积累才能增长，极限突破等等，都是习惯性动作、行为不断重复的结果。如果学会运作潜意识技巧，就可以建立一种自动运作的“长效机制”，从而达到人生的奋斗目标。\n" +
        "\n" +
        "　　在我写过的一本有关潜能开发的书稿中曾经讲过一则大象“林旺”的故事。“林旺”小时候被放进了动物园，鼻子被一根链条拴在了木桩上。有一次它想挣开铁链到猴山看看猴老弟，没想到挣得太猛，把鼻子挣得生疼。“哎哟，不得了！”林旺眼里含着泪，心里自言自语：“我这头小象是挣不开这个链条的。”半年后，林旺又想到大街上去转转，一挣链条，又把鼻子挣得生疼：“我这头小象是挣不开这个链条的。”两次失败，两次心中自言自语，使得它从此再也不敢去挣那根链条。后来，年复一年，小林旺长大了。但是，此时的它，从不想到外面去玩儿，它以为自己是不可能挣脱链条的，而实际上，这时的它只要一挣，完全可以到外面潇洒走一回。小林旺从小到大，从大到老，终于老死在了象房。这样的悲剧故事，岂止是动物世界在上演？人们可以自问一声：我自己是小林旺吗？非常可能！\n" +
        "\n" +
        "　　林旺的悲剧，基于它从两次挣脱动作中得出的一种负面认识：我是挣不脱那根链条的。关键是两次自我否定的暗示，是两次否定性的自我评价进入了它的潜意识之中，每当碰到这样的事，就成为习惯性反应。由此可见，习惯性的思考一旦进入潜意识，就有多么大的力量。\n" +
        "\n" +
        "　　这是一个反面的案例。\n" +
        "\n" +
        "　　现在需要把事情颠倒过来，如果是一种正面的思考，正面的动作呢？那将产生一种不可思议的积极的力量、成功的力量。这本书就是教你如何养成积极向上的习惯，从而一步步地走向成功的。作者说，“想想看，我们大多数的日常活动都只是习惯而已，我们几点钟起床，怎么洗澡，刷牙，穿衣，读报，吃早餐，驾车上班等等，一天之内上演着几百种习惯。”这是在提醒着我们：你非常需要仔细检查一遍自己的习惯。看看哪些是有益的，哪些是无益的，哪些是有害的，而后，将无益、有害的改为有益的。哪怕一个小小的改变，假以时日，必能受益无穷。\n" +
        "\n" +
        "　　非常可贵的是，本书的作者为一些立志成才者提供了不少行之有效的帮助你改变不良习惯、建立良好习惯的实用技巧。例如，设立一个适合于自己的好的目标，为此目标去努力，要大声的说出自我肯定的话，要用笔记下自己的前进脚步，改掉“总有一天”综合症，抓住一天中的两个重要时刻……这些都是很简单的事情，却是很有效的动作。要记住：让良性习惯进入你的潜意识，这样的话，你将不自觉地追随一个指向成功的导航仪而行进，而成功，正在前方向你招手呢！\n" +
        "\n" +
        "　　<span data-seg-id=\"1\">励志各有方法，巧妙各有不同。让我们以新奇的目光翻开它的扉页，开始阅读吧，潜意识的力量正从你的脑海深处涌起。</span>\n" +
        "\n" +
        "　　<custom>二零零四年四月十二日</custom>\n" +
        "\n" +
        "　　王通讯：国家人事部中国人事科学研究院副院长，人事与人才研究所所长。因其在人才研究、人才资源开发方面成绩卓著，被国务院授予突出贡献专家称号，在国际上曾获美国加州蓝带大印模奖，被载入英国剑桥世界名人大辞典。著有《微观人才学》、《宏观人才学》、《人才学通论》、《人才资源论》、《潜能开发论》等。"

