/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.integrations.rest

import org.junit.Assert
import org.opensearch.integrations.PluginRestTestCase
import org.opensearch.integrations.model.IntegrationInstance
import org.opensearch.observability.ObservabilityPlugin.Companion.BASE_INTEGRATIONS_URI
import org.opensearch.observability.getJsonString
import org.opensearch.rest.RestRequest
import org.opensearch.rest.RestStatus

class GetIntegrationIT : PluginRestTestCase() {
    private val sampleIntegration = IntegrationInstance(
        "sample_integration",
        "This is a sample integration"
    )

    private fun getSampleIntegrationID(): String {
        val body = getJsonString(sampleIntegration)
        val response = executeRequest(
            RestRequest.Method.POST.name,
            "$BASE_INTEGRATIONS_URI/store",
            body,
            RestStatus.OK.status
        )
        return response.get("objectId").asString
    }

    fun `test get one object`() {
        val sampleId = getSampleIntegrationID()
        val response = executeRequest(
            RestRequest.Method.GET.name,
            "$BASE_INTEGRATIONS_URI/store/$sampleId",
            "",
            RestStatus.OK.status
        )
        // TODO get one should return single object, not list
        val responseObject = response.get("hits").asJsonArray.first().asJsonObject
        Assert.assertEquals(sampleId, responseObject.get("objectId").asString)
    }

    fun `test get two objects has correct total hits`() {
        val sampleIds = listOf(getSampleIntegrationID(), getSampleIntegrationID()).sorted()
        val response = executeRequest(
            RestRequest.Method.GET.name,
            "$BASE_INTEGRATIONS_URI/store",
            "",
            RestStatus.OK.status
        )
        Assert.assertEquals(2, response.get("totalHits").asInt)
        Assert.assertEquals(
            sampleIds,
            response.get("hits").asJsonArray.map {
                it.asJsonObject.get("objectId").asString
            }.sorted()
        )
    }

    fun `test get two objects returns correct objects`() {
        val sampleIds = listOf(getSampleIntegrationID(), getSampleIntegrationID()).sorted()
        val response = executeRequest(
            RestRequest.Method.GET.name,
            "$BASE_INTEGRATIONS_URI/store",
            "",
            RestStatus.OK.status
        )
        Assert.assertEquals(
            sampleIds,
            response.get("hits").asJsonArray.map {
                it.asJsonObject.get("objectId").asString
            }.sorted()
        )
    }

    fun `test get empty object list`() {
        val emptyResponse = executeRequest(
            RestRequest.Method.GET.name,
            "$BASE_INTEGRATIONS_URI/store",
            "",
            RestStatus.OK.status
        )
        Assert.assertEquals(0, emptyResponse.get("totalHits").asInt)
    }
}
