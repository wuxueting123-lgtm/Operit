package com.arthenica.ffmpegkit

class Session(
    val returnCode: ReturnCode = ReturnCode(0),
    val output: String = "",
    val failStackTrace: String? = null
)

class ReturnCode(val value: Int) {
    companion object {
        const val SUCCESS = 0
        const val CANCEL = 1

        fun isSuccess(code: ReturnCode): Boolean = code.value == SUCCESS
        fun isCancel(code: ReturnCode): Boolean = code.value == CANCEL
    }
}

class MediaInformation(
    val format: String = "",
    val duration: String = "0",
    val bitrate: String = "0",
    val size: String = "0",
    val streams: List<StreamInformation> = emptyList()
)

class StreamInformation(
    val index: Int = 0,
    val type: String = "",
    val codec: String = "",
    val width: Int = 0,
    val height: Int = 0,
    val bitrate: String = "0",
    val sampleRate: String = "0",
    val channelLayout: String = ""
)

object FFmpegKit {
    fun execute(command: String): Session = Session()
    fun executeWithArguments(arguments: Array<String>): Session = Session()
    fun cancel() {}
    fun cancel(sessionId: Long) {}
}

object FFprobeKit {
    fun getMediaInformation(path: String): MediaInformation? = null
    fun getMediaInformation(path: String, timeout: Int): MediaInformation? = null
    fun execute(arguments: Array<String>): Session = Session()
}

object FFmpegKitConfig {
    fun enableLogCallback(callback: LogCallback) {}
    fun enableStatisticsCallback(callback: StatisticsCallback) {}
}

interface LogCallback {
    fun apply(log: Log)
}

interface StatisticsCallback {
    fun apply(statistics: Statistics)
}

class Log(val sessionId: Long, val message: String, val level: Int)

class Statistics(
    val sessionId: Long,
    val videoFrameNumber: Int,
    val videoFps: Float,
    val videoQuality: Float,
    val size: Long,
    val time: Long,
    val bitrate: Double,
    val speed: Double
)
