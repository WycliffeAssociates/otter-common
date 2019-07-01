package org.wycliffeassociates.otter.common.io.wav

import java.io.*
import java.nio.ByteBuffer
import java.nio.ByteOrder


/**
 * Created by sarabiaj on 6/2/2016.
 */
class WavFile {

    internal val file: File

    companion object {
        val SAMPLERATE = 44100
        val NUM_CHANNELS = 1
        val BLOCKSIZE = 2
        val HEADER_SIZE = 44
        val AUDIO_LENGTH_LOCATION = 40
        val SIZE_OF_SHORT = 2
        val AMPLITUDE_RANGE = 32767
        val BPP = 16
    }

    internal var totalAudioLength = 0
    internal var totalDataLength = 0


    /**
     * Loads an existing wav file and parses metadata it may have
     * @param file an existing wav file to load
     */
    constructor(file: File) {
        this.file = file
        parseHeader()
    }

    @Throws(IOException::class)
    fun finishWrite(totalAudioLength: Int) {
        this.totalAudioLength = totalAudioLength
    }


    fun initializeWavFile() {
        totalDataLength = HEADER_SIZE - 8
        totalAudioLength = 0

        FileOutputStream(file, false).use {
            it.write(generateHeaderArray())
        }
    }

    fun generateHeaderArray(): ByteArray {
        val header = ByteBuffer.allocate(44)
        val longSampleRate = SAMPLERATE
        val byteRate = (BPP * SAMPLERATE * NUM_CHANNELS) / 8

        header.order(ByteOrder.BIG_ENDIAN)
        header.put("RIFF".toByteArray(Charsets.US_ASCII))

        header.order(ByteOrder.LITTLE_ENDIAN)
        header.putInt(totalDataLength)

        header.order(ByteOrder.BIG_ENDIAN)
        header.put("WAVE".toByteArray(Charsets.US_ASCII))
        header.put("fmt ".toByteArray(Charsets.US_ASCII))

        header.order(ByteOrder.LITTLE_ENDIAN)
        header.putInt(16)
        header.putShort(1) // format = 1
        header.putShort(NUM_CHANNELS.toShort()) //number of channels
        header.putInt(longSampleRate)
        header.putInt(byteRate)
        header.putShort(((NUM_CHANNELS * BPP) / 8).toShort()) // block align
        header.putShort(BPP.toShort()) // bits per sample

        header.order(ByteOrder.BIG_ENDIAN)
        header.put("data".toByteArray(Charsets.US_ASCII))

        header.order(ByteOrder.LITTLE_ENDIAN)
        header.putInt(totalAudioLength) // initial size

        header.flip()

        return header.array()
    }

    fun overwriteHeaderData() {
        if (totalDataLength == (HEADER_SIZE - 8)) {
            totalAudioLength = this.file.length().toInt() - HEADER_SIZE
            totalDataLength = totalAudioLength + HEADER_SIZE - 8
        }
        RandomAccessFile(file, "rw").use {
            it.seek(0)
            it.write(generateHeaderArray())
        }
    }

    fun parseHeader() {
        if (file != null && file.length() >= HEADER_SIZE) {
            RandomAccessFile(file, "r").use {
                val header = ByteArray(HEADER_SIZE)
                it.read(header)
                val bb = ByteBuffer.wrap(header)
                bb.order(ByteOrder.LITTLE_ENDIAN)
                //Skip over "RIFF"
                bb.getInt()
                this.totalDataLength = bb.getInt()
                //Seek to the audio length field
                bb.position(AUDIO_LENGTH_LOCATION)
                totalAudioLength = bb.getInt()
            }
        } else {
            initializeWavFile()
        }
    }
}
