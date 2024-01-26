package com.programtom.appbuilder.app_builder_plugin.model

data class Builder(
    private var id: String? = null,
    private val userId: String? = null,
    private val platformId: String? = null,
    private val codeProviderId: String? = null,
    private val name: String? = null,
    private val codeProvider: CodeProvider? = null,
    private val platform: CodeProviderPlatform? = null,
)
