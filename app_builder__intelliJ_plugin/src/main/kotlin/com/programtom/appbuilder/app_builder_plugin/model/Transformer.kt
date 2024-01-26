package com.programtom.appbuilder.app_builder_plugin.model

data class Transformer(
    private var id: String? = null,
    private val generatorId: String? = null,
    val find: String?,
    val replace: String?,
)
