package org.opensearch.integrations.action

import com.fasterxml.jackson.databind.JsonNode
import org.opensearch.client.node.NodeClient
import org.opensearch.integrations.IntegrationsPlugin
import org.opensearch.rest.RestRequest
import org.opensearch.rest.RestResponse

class CreateIntegrationAction : IntegrationAction<JsonNode, JsonNode> {
    override fun getName(): String {
        return "integration_store_create"
    }

    override fun getRoutes(): List<Pair<RestRequest.Method, String>> {
        return listOf(
            Pair(RestRequest.Method.POST, "${IntegrationsPlugin.BASE_INTEGRATIONS_URI}/store"),
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
