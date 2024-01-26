package com.programtom.appbuilder.app_builder_plugin.model

data class Generator(
    private var id: String? = null,
    private val userId: String? = null,
    private val codeProviderId: String? = null,
    private val platformId: String? = null,
    private val activatorGroupId: String? = null,
    val name: String? = null,
    val parseUrl: String,
    val generateUrl: String,
    private val url: String? = null,
    private val description: String? = null,
    private val codeProvider: CodeProvider? = null,
    private val platform: CodeProviderPlatform? = null,
    val transformers: List<Transformer>,
) : NamedObject {
    override val displayName: String
        get() = name ?: ""

}
