package com.ai.assistance.operit.core.tools.javascript

import java.io.Closeable

class OperitQuickJsEngine : Closeable {
    fun bindNativeInterface(instance: Any) {}

    @Suppress("UNCHECKED_CAST")
    fun <T> evaluate(script: String, fileName: String = "<eval>"): T? = null

    @Suppress("UNCHECKED_CAST")
    fun <T> callFunction(
        functionName: String,
        argsJson: String,
        callSite: String = "<call:$functionName>"
    ): T? = null

    fun interrupt() {}

    override fun close() {}
}
