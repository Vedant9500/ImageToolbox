import android.content.Context
import android.media.*
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import java.io.File

class VideoConverterService @Inject constructor(
    private val context: Context
) {
    fun convertVideo(
        inputUri: Uri,
        outputFormat: String,
        onProgress: (Float) -> Unit,
        onComplete: (Uri) -> Unit,
        onError: (Exception) -> Unit
    ) {
        try {
            val mediaExtractor = MediaExtractor()
            mediaExtractor.setDataSource(context, inputUri, null)
            
            // Get video track and format
            val videoTrackIndex = selectTrack(mediaExtractor, "video/")
            val sourceFormat = mediaExtractor.getTrackFormat(videoTrackIndex)
            
            // Create output format
            val outputFormat = MediaFormat.createVideoFormat(
                outputFormat,
                sourceFormat.getInteger(MediaFormat.KEY_WIDTH),
                sourceFormat.getInteger(MediaFormat.KEY_HEIGHT)
            ).apply {
                setInteger(
                    MediaFormat.KEY_COLOR_FORMAT,
                    MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
                )
                setInteger(MediaFormat.KEY_BIT_RATE, sourceFormat.getInteger(MediaFormat.KEY_BIT_RATE))
                setInteger(MediaFormat.KEY_FRAME_RATE, sourceFormat.getInteger(MediaFormat.KEY_FRAME_RATE))
                setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)
            }

            // Create encoder and decoder
            val encoder = MediaCodec.createEncoderByType(outputFormat.getString(MediaFormat.KEY_MIME)!!)
            val decoder = MediaCodec.createDecoderByType(sourceFormat.getString(MediaFormat.KEY_MIME)!!)

            // Configure codec
            encoder.configure(outputFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
            decoder.configure(sourceFormat, encoder.createInputSurface(), null, 0)

            // Start codecs
            encoder.start()
            decoder.start()

            // Create output file
            val outputFile = createOutputFile(context, getFileName(context, inputUri))
            val muxer = MediaMuxer(outputFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
            
            // Process frames
            val bufferInfo = MediaCodec.BufferInfo()
            var isEOS = false
            var muxerStarted = false
            var muxerTrackIndex = -1
            
            while (!isEOS) {
                // Feed input to decoder
                val inputBufferId = decoder.dequeueInputBuffer(TIMEOUT_US)
                if (inputBufferId >= 0) {
                    val inputBuffer = decoder.getInputBuffer(inputBufferId)!!
                    val sampleSize = mediaExtractor.readSampleData(inputBuffer, 0)
                    
                    if (sampleSize < 0) {
                        decoder.queueInputBuffer(
                            inputBufferId, 0, 0, 0,
                            MediaCodec.BUFFER_FLAG_END_OF_STREAM
                        )
                        isEOS = true
                    } else {
                        decoder.queueInputBuffer(
                            inputBufferId, 0, sampleSize,
                            mediaExtractor.sampleTime, 0
                        )
                        mediaExtractor.advance()
                    }
                }

                // Process output from decoder/encoder
                val encoderStatus = encoder.dequeueOutputBuffer(bufferInfo, TIMEOUT_US)
                if (encoderStatus >= 0) {
                    val encodedData = encoder.getOutputBuffer(encoderStatus)!!
                    
                    if (bufferInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG != 0) {
                        bufferInfo.size = 0
                    }

                    if (bufferInfo.size > 0) {
                        if (!muxerStarted) {
                            muxerTrackIndex = muxer.addTrack(encoder.outputFormat)
                            muxer.start()
                            muxerStarted = true
                        }

                        encodedData.position(bufferInfo.offset)
                        encodedData.limit(bufferInfo.offset + bufferInfo.size)
                        muxer.writeSampleData(muxerTrackIndex, encodedData, bufferInfo)
                        
                        // Update progress
                        onProgress(bufferInfo.presentationTimeUs / sourceFormat.getLong(MediaFormat.KEY_DURATION).toFloat())
                    }
                    
                    encoder.releaseOutputBuffer(encoderStatus, false)
                    
                    if (bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                        break
                    }
                }
            }

            // Release resources
            mediaExtractor.release()
            decoder.stop()
            decoder.release()
            encoder.stop()
            encoder.release()
            muxer.stop()
            muxer.release()

            // Complete conversion
            onComplete(Uri.fromFile(outputFile))
            
        } catch (e: Exception) {
            onError(e)
        }
    }

    private fun selectTrack(extractor: MediaExtractor, mimeType: String): Int {
        for (i in 0 until extractor.trackCount) {
            val format = extractor.getTrackFormat(i)
            format.getString(MediaFormat.KEY_MIME)?.let { mime ->
                if (mime.startsWith(mimeType)) {
                    extractor.selectTrack(i)
                    return i
                }
            }
        }
        throw IllegalStateException("No video track found")
    }

    private fun createOutputFile(context: Context, inputFileName: String): File {
        val outputDir = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        return File(outputDir, "converted_${inputFileName}")
    }

    private fun getFileName(context: Context, uri: Uri): String {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            return cursor.getString(nameIndex)
        } ?: throw IllegalStateException("Cannot get filename")
    }

    companion object {
        private const val TIMEOUT_US = 10000L
    }
}