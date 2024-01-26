package com.programtom.appbuilder.app_builder_plugin.model

data class CodeProvider(
    val id: String,
    val userId: String,
    val name: String,
    val countGenerator: Long,
    val generators: List<Generator>,
    val countBuilder: Long,
    val builders: List<Builder>,
    val countPlatform: Long,
    val platforms: List<CodeProviderPlatform>,
    val existingFileCodeAssists: List<ExistingFileCodeAssist>,
)
