package org.wycliffeassociates.otter.common.app

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.wycliffeassociates.otter.common.io.wav.WavFile
import org.wycliffeassociates.otter.common.io.wav.WavOutputStream

class WavFileWriter(
    private val wav: WavFile,
    private val audioStream: Observable<ByteArray>)
{
    val writer = Observable.using(
    { WavOutputStream(wav, false, WavOutputStream.BUFFERING.BUFFERED) },
    { writer -> audioStream.map {
        writer.write(it)
    } },
    { writer ->
        writer.close()
    }
    ).subscribeOn(Schedulers.io())
    .subscribe()


}