package org.opensearch.integrations.action

import com.fasterxml.jackson.databind.JsonNode
import org.opensearch.client.node.NodeClient
import org.opensearch.integrations.IntegrationsPlugin.Companion.BASE_INTEGRATIONS_URI
import org.opensearch.rest.RestRequest
import org.opensearch.rest.RestResponse
import org.opensearch.rest.RestRequest.Method

class GetIntegrationAction : IntegrationAction<JsonNode, JsonNode> {
    override fun getName(): String {
        return "integration_store_get"
    }

    override fun getRoutes(): List<Pair<Method, String>> {
        return listOf(
            Pair(Method.GET, "$BASE_INTEGRATIONS_URI/store"),
            Pair(Method.GET, "$BASE_INTEGRATIONS_URI/store/{integration_id}")
        )
    }

    override fun read(rawRequest: RestRequest): JsonNode {
        TODO("Not yet implemented")
    }

    override fun write(response: JsonNode): RestResponse {
        TODO("Not yet implemented")
    }

    override fun execute(request: JsonNode, client: NodeClient): JsonNode {
        TODO("Not yet implemented")
    }
}
