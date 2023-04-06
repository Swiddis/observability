package org.opensearch.integrations.action

import com.fasterxml.jackson.databind.JsonNode
import org.opensearch.client.node.NodeClient
import org.opensearch.integrations.IntegrationsPlugin
import org.opensearch.rest.RestRequest
import org.opensearch.rest.RestResponse

class ValidateIntegrationAction : IntegrationAction<JsonNode, JsonNode> {
    override fun getName(): String {
        return "integration_store_validate"
    }

    override fun getRoutes(): List<Pair<RestRequest.Method, String>> {
        return listOf(
            Pair(RestRequest.Method.PUT, "${IntegrationsPlugin.BASE_INTEGRATIONS_URI}/store/{integration_id}/validate")
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
