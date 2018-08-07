package device

import io.reactivex.Completable
import java.io.File
import java.net.URI

interface IAudioPlayer {

    fun load(file: File): Completable 

    fun load(path: String): Completable

    fun load(uri: URI): Completable

    fun play()

    fun pause()

    fun stop()

    fun getAbsoluteDurationInFrames(): Int

    fun getAbsoluteDurationMs(): Int

    fun getAbsoluteLocationInFrames(): Int

    fun getAbsoluteLocationMs(): Int
}