package device

import io.reactivex.Observable

interface IAudioRecorder {
    fun record()
    fun stop()
    fun getAudioByteObservable(): Observable<ByteArray>
}