package com.programtom.appbuilder.app_builder_plugin.model


data class CodeProviderPlatform(
    private var id: String? = null,
    private val userId: String? = null,
    private val codeProviderId: String? = null,
    private val name: String? = null,
    private val codeProvider: CodeProvider? = null,
    private val builders: List<Builder>,
    private val countBuilder: Long = 0,
    val generators: List<Generator>,
    val fileChanges: List<FileChange>,
    private val countGenerator: Long = 0,
)
