package com.pumpkin.core

object Debug {
    private val coreLogger = Logger("Pumpkin")
    private val appLogger = Logger("Application")

    internal fun logCore(level: LogLevel, message: String) = coreLogger.log(level, message)

    internal fun logTraceCore(message: String) = coreLogger.trace(message)

    internal fun logDebugCore(message: String) = coreLogger.debug(message)

    internal fun logInfoCore(message: String) = coreLogger.info(message)

    internal fun logWarnCore(message: String) = coreLogger.warn(message)

    internal fun logErrorCore(message: String, t: Throwable? = null) = coreLogger.error(message + if (t != null) ("\n" + t.stackTraceToString()) else "")

    internal fun logFatalCore(message: String, t: Throwable? = null) = coreLogger.fatal(message + if (t != null) ("\n" + t.stackTraceToString()) else "")


    fun log(level: LogLevel, message: String) = appLogger.trace(message)

    fun logTrace(message: String) = appLogger.trace(message)

    fun logDebug(message: String) = appLogger.debug(message)

    fun logInfo(message: String) = appLogger.info(message)

    fun logWarn(message: String) = appLogger.warn(message)

    fun logError(message: String, t: Throwable? = null) = appLogger.error(message + if (t != null) ("\n" + t.stackTraceToString()) else "")

    fun logFatal(message: String, t: Throwable? = null) = appLogger.fatal(message + if (t != null) ("\n" + t.stackTraceToString()) else "")


    fun assert(predicate: Boolean, failMessage: String? = null) {
        if (!predicate) exception(failMessage)
    }

    fun assert(not0: Number, failMessage: String? = null) {
        if (not0 == 0) exception(failMessage)
    }

    fun assert(notNull: Any?, failMessage: String? = null) {
        if (notNull == null) exception(failMessage)
    }


    fun exception(message: String? = null): Nothing = throw PumpkinError(message)
}

class Logger(private val name: String) {
    private fun color(level: LogLevel): String {
        return when (level) {
            LogLevel.DEBUG -> "\u001B[0;36m"
            LogLevel.INFO -> "\u001B[0;32m"
            LogLevel.WARN -> "\u001B[0;33m"
            LogLevel.ERROR -> "\u001B[0;31m"
            LogLevel.FATAL -> "\u001B[1;31m"
            else -> "\u001b[0;37m"
        }
    }

    val minLevel = LogLevel.DEBUG

    private val resetColor = "\u001B[0m"

    fun log(level: LogLevel, message: String) {
        println("${color(level)}[${level.name}] $name: $message$resetColor")
    }

    fun trace(message: String) { if (minLevel <= LogLevel.TRACE) {
        log(LogLevel.TRACE, message)
    }}

    fun debug(message: String) { if (minLevel <= LogLevel.DEBUG) {
        log(LogLevel.DEBUG, message)
    }}

    fun info(message: String) { if (minLevel <= LogLevel.INFO) {
        log(LogLevel.INFO, message)
    }}

    fun warn(message: String) { if (minLevel <= LogLevel.WARN) {
        log(LogLevel.WARN, message)
    }}

    fun error(message: String) { if (minLevel <= LogLevel.ERROR) {
        log(LogLevel.ERROR, message)
    }}

    fun fatal(message: String) { if (minLevel <= LogLevel.FATAL) {
        log(LogLevel.FATAL, message)
    }}
}

enum class LogLevel {
    TRACE,
    DEBUG,
    INFO,
    WARN,
    ERROR,
    FATAL,
}