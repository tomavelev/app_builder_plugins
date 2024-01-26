package com.programtom.appbuilder.app_builder_plugin.model

data class ExistingFileCodeAssist(
    private var id: String? = null,
    private var createdAt: String? = null,
    private var updatedAt: String? = null,
    var name: String? = null,
    var description: String? = null,
    private var userId: String? = null,
    private var codeProviderId: String? = null,
    var activatorGroupId: String? = null,
    var inlineActivatorGroupId: String? = null,
    var lineActivatorGroupId: String? = null,
    var codeTransformUrl: String? = null,
) : NamedObject {
    override val displayName: String
        get() = name ?: ""

}
