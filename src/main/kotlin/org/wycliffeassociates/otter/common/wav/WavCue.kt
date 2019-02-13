package org.wycliffeassociates.otter.common.io.wav

/**
 * Created by sarabiaj on 10/4/2016.
 */
data class WavCue(var label: String = "", var location: Int = 0) {

    inline fun getLoctionInMilliseconds(): Int = (this.location / 44.1).toInt()

    inline fun complete(): Boolean = (this.label != null && this.location != null)

}
