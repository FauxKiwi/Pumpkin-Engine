package com.pumpkin.core

private val coreLogger = Logger("Pumpkin")
private val appLogger = Logger("Application")

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

private val resetColor = "\u001B[0m"

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

internal  fun logErrorCore(message: String, t: Throwable? = null) {
    coreLogger.error(message + if (t != null) ("\n" + t.stackTraceToString()) else "")
}

internal fun logFatalCore(message: String, t: Throwable? = null) {
    coreLogger.fatal(message + if (t != null) ("\n" + t.stackTraceToString()) else "")
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
    appLogger.error(message + if (t != null) ("\n" + t.stackTraceToString()) else "")
}

fun logFatal(message: String, t: Throwable? = null) {
    appLogger.fatal(message + if (t != null) ("\n" + t.stackTraceToString()) else "")
}

class Logger(private val name: String) {

    fun log(level: Level, message: String) {
        println("${color(level)}[${level.name}] $name: $message$resetColor")
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