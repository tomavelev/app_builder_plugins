package com.programtom.appbuilder.app_builder_plugin.api

import com.google.gson.Gson
import com.programtom.appbuilder.app_builder_plugin.model.AppBuilderDataContainer
import com.programtom.appbuilder.app_builder_plugin.model.ExistingFileCodeAssist
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class ProgramTomAPI {
    companion object {

//        private val type = TypeToken.getParameterized(ArrayList::class.java, CodeProvider::class.java).type

        //        private const val HOST = "http://localhost:8884/appbuilder"
        private const val HOST = "https://programtom.xyz/appbuilder"
        private val client: HttpClient = HttpClient.newBuilder().build()

        fun parseText(text: String, parserUrl: String): String? {
            val request =
                HttpRequest.newBuilder().uri(
                    URI.create(
                        parserUrl
                    )
                )
                    .POST(HttpRequest.BodyPublishers.ofString(text))
                    .build()
            val response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body()

        }

        fun generateCode(dataModel: String, generateURL: String): String? {
            val request =
                HttpRequest.newBuilder().uri(
                    URI.create(
                        generateURL
                    )
                )
                    .POST(HttpRequest.BodyPublishers.ofString(dataModel))
                    .build()
            val response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body()
        }

        fun getCodeProviders(): AppBuilderDataContainer? {
            //TODO make the host configurable
//            https://plugins.jetbrains.com/docs/intellij/persisting-sensitive-data.html#retrieve-stored-credentials
            //TODO cache code providers and load them on restart IDE - so no API call is required
            val request =
                HttpRequest.newBuilder().uri(URI.create("$HOST/api/codeProviders"))
                    .GET()
                    .build()
            val response = client.send(request, HttpResponse.BodyHandlers.ofString());
            val body = response.body()
            return Gson().fromJson(body, AppBuilderDataContainer::class.java)
        }

        fun improveCode(existingFileCodeAssist: ExistingFileCodeAssist, text: String, lineNumber: Int): String {
            val request =
                HttpRequest.newBuilder().uri(existingFileCodeAssist.codeTransformUrl?.let { URI.create(it) })
                    .POST(HttpRequest.BodyPublishers.ofString(text))
                    .header("lineNumber", lineNumber.toString())
                    .build()
            val response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body()
        }
    }
}
