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
        //val CHANNEL_TYPE = AudioFormat.CHANNEL_IN_MONO
        val NUM_CHANNELS = 1
        //val ENCODING = AudioFormat.ENCODING_PCM_16BIT
        val BLOCKSIZE = 2
        val HEADER_SIZE = 44
        val AUDIO_LENGTH_LOCATION = 40
        val SIZE_OF_SHORT = 2
        val AMPLITUDE_RANGE = 32767
        val BPP = 16
    }


    //var metadata: WavMetadata?
    internal var totalAudioLength = 0
    internal var totalDataLength = 0

    private var metadataLength = 0
    fun getTotalMetadataLength(): Int {
        return metadataLength + 20
    }


    /**
     * Loads an existing wav file and parses metadata it may have
     * @param file an existing wav file to load
     */
    constructor(file: File) {
        this.file = file
        //metadata = WavMetadata(file)
        parseHeader()
    }

//    /**
//     * Creates a new Wav file and initializes the header
//     * @param file the path to use for creating the wav file
//     * @param metadata metadata to attach to the wav file
//     */
//    constructor(file: File, metadata: WavMetadata) {
//        this.file = file
//
//        initializeWavFile()
//        this.metadata = metadata
//    }

    @Throws(IOException::class)
    fun finishWrite(totalAudioLength: Int) {
        this.totalAudioLength = totalAudioLength
        //writeMetadata(totalAudioLength)
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

//    @Throws(IOException::class)
//    private fun writeMetadata(totalAudioLength: Int) {
//        this.totalAudioLength = totalAudioLength
//        byte[] cueChunk = mMetadata.createCueChunk()
//        byte[] labelChunk = mMetadata.createLabelChunk()
//        byte[] trMetadata = mMetadata.createTrMetadataChunk()
//        try (
//            FileOutputStream out = new FileOutputStream(mFile, true)
//            BufferedOutputStream bof = new BufferedOutputStream(out)
//        ){
//            //truncates existing metadata- new metadata may not be as long
//            out.getChannel().truncate(HEADER_SIZE + mTotalAudioLength)
//            bof.write(cueChunk)
//            bof.write(labelChunk)
//            bof.write(trMetadata)
//        }
//        mMetadataLength = cueChunk.length + labelChunk.length + trMetadata.length
//        mTotalDataLength = mTotalAudioLength + mMetadataLength + HEADER_SIZE - 8
//        overwriteHeaderData()
//        return
//    }

    fun overwriteHeaderData() {

        if (totalDataLength == (HEADER_SIZE - 8)) {
            totalAudioLength = this.file.length().toInt() - HEADER_SIZE - metadataLength
            totalDataLength = totalAudioLength + HEADER_SIZE - 8 + metadataLength
        }
        RandomAccessFile(file, "rw").use {
            it.seek(0)
            //if total length is still just the header, then check the file size

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


//    /**
//     * Adds a marker to the wav file at the given position
//     * Does not write to the file until commit is called.
//     * @param label string for the label of the marker
//     * @param position block index of the PCM array ex 44100 for 1 second
//     * @return a reference to this to allow chaining with commit
//     */
//    fun addMarker(String label, int position): WavFile {
//        WavCue cue = new WavCue(label, position)
//        mMetadata.addCue(cue)
//        return this
//    }
//
//    fun commit(){
//        writeMetadata(mTotalAudioLength)
//    }
}
