package org.wycliffeassociates.otter.common.app

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import net.sourceforge.lame.lowlevel.LameEncoder
import net.sourceforge.lame.mp3.Lame
import net.sourceforge.lame.mp3.MPEGMode
import net.sourceforge.lame.utils.AudioFormat
import org.wycliffeassociates.otter.common.collections.FloatRingBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ActiveRecordingRenderer(
    stream: Observable<ByteArray>
) {

    val floatBuffer = FloatRingBuffer(1845)

    val pcmCompressor = PCMCompressor(floatBuffer, 100)


    val bb = ByteBuffer.allocate(1024)
    val encoded = ByteArray(4000)

    val encoder = LameEncoder(
        AudioFormat(44100F, 16, 1, true, false),
        32,
        MPEGMode.MONO,
        Lame.QUALITY_HIGH,
        false
    )

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

    val mp3 = stream
        .subscribeOn(Schedulers.computation())
        .subscribe {
            val written = encoder.encodeBuffer(it, 0, it.size, encoded)

        }
}