package org.opensearch.integrations.action

import com.fasterxml.jackson.databind.JsonNode
import org.opensearch.client.node.NodeClient
import org.opensearch.rest.RestRequest
import org.opensearch.rest.RestResponse

class CreateIntegrationAction : IntegrationAction<JsonNode, JsonNode> {
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
