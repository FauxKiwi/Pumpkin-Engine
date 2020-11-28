package com.pumpkin.core

private val coreLogger = Logger("Pumpkin")
private val appLogger = Logger("Application")

internal fun init() {
    /*val appender = ConsoleAppender()
    appender.name = "Console"
    appender.layout = object: Layout() {
        override fun activateOptions() {

        }

        override fun format(event: LoggingEvent): String {
            return "${color(event.getLevel())}[${event.getLevel()}] ${event.loggerName}: ${event.message}\n"
        }

        override fun ignoresThrowable(): Boolean {
            return false
        }
    }
    appender.setWriter(System.out.bufferedWriter())
    //Logger.getRootLogger().addAppender(appender)
    Logger.getLogger("Pumpkin").addAppender(appender)
    Logger.getLogger("Application").addAppender(appender)*/
}

private fun color(level: Level): String {
    return when(level) {
        Level.DEBUG -> "\u001B[0;36m"
        Level.INFO -> "\u001B[0;32m"
        Level.WARN -> "\u001B[0;33m"
        Level.ERROR -> "\u001B[0;31m"
        Level.FATAL -> "\u001B[1;31m"
        else -> "\u001b[0;37m"
    }
}


internal fun logTraceCore(message: String) {
    coreLogger.trace(message)
}

internal fun logDebugCore(message: String) {
    coreLogger.debug(message)
}

internal fun logInfoCore(message: String) {
    coreLogger.info(message)
}

internal fun logWarnCore(message: String) {
    coreLogger.warn(message)
}

internal fun logErrorCore(message: String, t: Throwable? = null) {
    coreLogger.error(message + "\n" + t?.stackTraceToString())
}

internal fun logFatalCore(message: String, t: Throwable? = null) {
    coreLogger.fatal(message + "\n" + t?.stackTraceToString())
}


fun logTrace(message: String) {
    appLogger.trace(message)
}

fun logDebug(message: String) {
    appLogger.debug(message)
}

fun logInfo(message: String) {
    appLogger.info(message)
}

fun logWarn(message: String) {
    appLogger.warn(message)
}

fun logError(message: String, t: Throwable? = null) {
    appLogger.error(message + "\n" + t?.stackTraceToString())
}

fun logFatal(message: String, t: Throwable? = null) {
    appLogger.fatal(message + "\n" + t?.stackTraceToString())
}

class Logger(private val name: String) {

    fun log(level: Level, message: String) {
        println("${color(level)}[${level.name}] $name: $message")
    }

    fun trace(message: String) {
        log(Level.TRACE, message)
    }

    fun debug(message: String) {
        log(Level.DEBUG, message)
    }

    fun info(message: String) {
        log(Level.INFO, message)
    }

    fun warn(message: String) {
        log(Level.WARN, message)
    }

    fun error(message: String) {
        log(Level.ERROR, message)
    }

    fun fatal(message: String) {
        log(Level.FATAL, message)
    }
}

enum class Level {
    TRACE,
    DEBUG,
    INFO,
    WARN,
    ERROR,
    FATAL,
}