package org.wycliffeassociates.otter.common.collections


/**
 * Created by sarabiaj on 2/21/2017.
 */
class FloatRingBuffer(capacity: Int) {

    private var head = 0
    private var tail = 0
    private val buffer = FloatArray(capacity)
    private val returnBuffer = FloatArray(capacity * 4)


    val isEmpty: Boolean
        get() = head == tail

    val array: FloatArray
        @Synchronized
        get() {
            var i = 0
            while (i < size()) {
                returnBuffer[i * 4] = i.toFloat()
                returnBuffer[i * 4 + 1] = get(i)
                returnBuffer[i * 4 + 2] = i.toFloat()
                returnBuffer[i * 4 + 3] = get(i + 1)
                i += 2
            }
//            while(i < returnBuffer.size) {
//                returnBuffer[i * 4] = i.toFloat()
//                returnBuffer[i * 4 +1] = 0f
//                i+=2
//            }
            return returnBuffer
        }

    @Synchronized
    fun add(i: Float) {
        buffer[tail] = i
        tail = (tail + 1) % buffer.size
        if (head == tail) {
            head = (head + 1) % buffer.size
        }
    }

    fun clear() {
        head = 0
        tail = 0
    }

    operator fun get(i: Int): Float {
        val index = (head + i) % buffer.size
        return buffer[index]
    }

    fun size(): Int {
        return if (head == 0 && tail < buffer.size) {
            tail
        } else {
            buffer.size
        }
    }

    fun scaleAndGetArray(scalingFactor: Float): FloatArray {
        val buffer = FloatArray(size() * 4)
        var i = 0
        while (i < size()) {
            buffer[i * 4] = i.toFloat()
            buffer[i * 4 + 1] = scalingFactor * get(i)
            buffer[i * 4 + 2] = i.toFloat()
            buffer[i * 4 + 3] = scalingFactor * get(i + 1)
            i += 2
        }
        return buffer
    }
}