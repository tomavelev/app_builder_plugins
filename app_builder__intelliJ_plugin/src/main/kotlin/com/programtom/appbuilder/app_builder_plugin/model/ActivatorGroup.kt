package com.programtom.appbuilder.app_builder_plugin.model

data class ActivatorGroup(
    var id: String? = null,
    private val codeProviderId: String? = null,
    val name: String? = null,
    val generatorActivatorList: List<Activator>,

    )
