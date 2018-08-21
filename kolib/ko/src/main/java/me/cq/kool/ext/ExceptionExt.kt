package me.cq.kool.ext

/**
 * Created by phelps on 2018/2/28 0028.
 */
fun tryJust(f: ()->Unit){
    try {
        f()
    }catch (e : Exception){
        e.printStackTrace()
    }
}