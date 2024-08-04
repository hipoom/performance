package com.hipoom.performance.timing

import com.hipoom.holder.ObjectPool
import java.lang.IllegalStateException

/**
 * @author ZhengHaiPeng
 * @since 2024/8/3 23:34
 */
class Frame {

    /* ======================================================= */
    /* Fields                                                  */
    /* ======================================================= */

    var beginMills: Long = 0
        get() {
            if (isRecycled) {
                throw IllegalStateException("这个对象已经被回收了，不应该再继续使用了.")
            }
            return field
        }

    var endMills: Long = 0
        get() {
            if (isRecycled) {
                throw IllegalStateException("这个对象已经被回收了，不应该再继续使用了.")
            }
            return field
        }

    var methodDescription: String = ""
        get() {
            if (isRecycled) {
                throw IllegalStateException("这个对象已经被回收了，不应该再继续使用了.")
            }
            return field
        }

    private var isRecycled = false

    companion object {

        private val pool = ObjectPool {
            return@ObjectPool Frame()
        }

        @JvmStatic
        fun obtain(): Frame {
            return pool.obtain().apply {
                this.isRecycled = false
                this.beginMills = 0
                this.endMills = 0
                this.methodDescription = ""
            }
        }
    }



    /* ======================================================= */
    /* Public Methods                                          */
    /* ======================================================= */

    fun recycle() {
        isRecycled = true
        pool.recycle(this)
    }

    fun getCostMills(): Long {
        return endMills - beginMills
    }

}