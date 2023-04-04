package org.opensearch.integrations.model

import com.fasterxml.jackson.annotation.JsonProperty

data class Integration(
    @JsonProperty("template-name") val templateName: String,
    val namespace: String,
    val dataset: String,
    val id: String,
    val version: String,
    val description: String,
    val template: String,
    @JsonProperty("creationDate") val creationDate: String,
    val status: String,
    val assets: List<Asset>
)

data class Asset(
    val name: String,
    @JsonProperty("creationDate") val creationDate: String,
    val status: String
)
