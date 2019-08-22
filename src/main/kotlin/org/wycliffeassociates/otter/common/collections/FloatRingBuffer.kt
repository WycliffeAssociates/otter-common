package org.wycliffeassociates.otter.common.collections


/**
 * Created by sarabiaj on 2/21/2017.
 */
class FloatRingBuffer(private val capacity: Int) {

    private var head = 0
    private var tail = 0
    private val buffer = FloatArray(capacity)
    //this buffer contains x and y values for the high and low in each sample
    private val returnBuffer = FloatArray(capacity)

    val isEmpty: Boolean
        @Synchronized
        get() = head == tail

    val array: FloatArray
        @Synchronized
        get() {
            val headToEndSize = capacity - head
            //copies all values from head to the end of the array
            System.arraycopy(buffer, head, returnBuffer, 0, headToEndSize)

            //if head is 0, the entire array will have already been copied
            //this case handles the wrap-around
            if (head != 0) {
                //length to copy is head, as that covers 0 to head
                System.arraycopy(buffer, 0, returnBuffer, headToEndSize, head)
            }
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

    @Synchronized
    fun clear() {
        head = 0
        tail = 0
    }

    @Synchronized
    operator fun get(i: Int): Float {
        val index = (head + i) % buffer.size
        return buffer[index]
    }

    @Synchronized
    fun size(): Int {
        return if (head == 0 && tail < buffer.size) {
            tail
        } else {
            buffer.size
        }
    }
}