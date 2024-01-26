package com.programtom.appbuilder.app_builder_plugin.model

data class AppBuilderDataContainer(
    val codeProviders: List<CodeProvider>,
    val platforms: List<CodeProviderPlatform>,
    val activatorGroups: List<ActivatorGroup>,
    val activatorGenerators: Map<String, List<Generator>>,
)
