package org.wycliffeassociates.otter.common.recorder

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.wycliffeassociates.otter.common.collections.FloatRingBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ActiveRecordingRenderer(
    stream: Observable<ByteArray>,
    width: Int
) {
    //double the width as for each pixel there will be a min and max value
    val floatBuffer = FloatRingBuffer(width * 2)
    val pcmCompressor = PCMCompressor(floatBuffer)
    val bb = ByteBuffer.allocate(1024)

    init {
        bb.order(ByteOrder.LITTLE_ENDIAN)
    }

    val activeRenderer = stream
        .subscribeOn(Schedulers.io())
        .subscribe {
            bb.put(it)
            bb.position(0)
            while (bb.hasRemaining()) {
                val short = bb.short
                pcmCompressor.add(short.toFloat())
            }
            bb.clear()
        }
}