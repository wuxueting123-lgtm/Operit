package com.ai.assistance.fbx

data class FbxModelInfo(
    val modelName: String,
    val animationNames: List<String>,
    val animationDurationMillisByName: Map<String, Long>,
    val requiredExternalFiles: List<String>,
    val missingExternalFiles: List<String>
) {
    val defaultAnimation: String?
        get() = animationNames.firstOrNull()
}

object FbxInspector {
    fun isAvailable(): Boolean = false
    fun unavailableReason(): String = "FBX module disabled (CI stub)"
    fun getLastError(): String = "FBX module not available"
    fun inspectModel(pathModel: String): FbxModelInfo? = null
}
