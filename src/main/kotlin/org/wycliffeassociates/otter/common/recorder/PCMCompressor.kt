package org.wycliffeassociates.otter.common.app

import org.wycliffeassociates.otter.common.collections.FloatRingBuffer

class PCMCompressor(private val ringBuffer: FloatRingBuffer) {

    val accumulator = FloatArray(480)
    var index = 0

    fun add(data: FloatArray) {
        for(sample in data) {
            if(index >= accumulator.size){
                sendDataToRingBuffer()
                index = 0
            }
            accumulator[index] = sample
            index++
        }
    }

    fun add(data: Float) {
        if(index >= accumulator.size){
            sendDataToRingBuffer()
            index = 0
        }
        accumulator[index] = data
        index++
    }

    fun sendDataToRingBuffer() {
        var min = Float.MAX_VALUE
        var max = Float.MIN_VALUE

        for(sample in accumulator) {
            max = if(max < sample) sample else max
            min = if(min > sample) sample else min
        }
        ringBuffer.add(min)
        ringBuffer.add(max)
    }
}