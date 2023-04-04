package org.opensearch.integrations.rest

import org.junit.Assert
import org.opensearch.integrations.IntegrationsPlugin
import org.opensearch.integrations.PluginRestTestCase
import org.opensearch.integrations.jsonify
import org.opensearch.rest.RestRequest
import org.opensearch.rest.RestStatus
import java.io.File

class PostStoreIT: PluginRestTestCase() {
    fun `test post an integration to a store`() {
        val sampleJsonPath = "../../../docs/schema/system/samples/integration-instance.json"
        val sampleJson = File(sampleJsonPath).readText(Charsets.UTF_8)
        val result = executeRequest(
            RestRequest.Method.POST.name,
            "${IntegrationsPlugin.BASE_INTEGRATIONS_URI}/store",
            sampleJson,
            RestStatus.CREATED.status
        )
        println(result.toString())
        Assert.assertEquals(result, jsonify(sampleJson))
    }
}
