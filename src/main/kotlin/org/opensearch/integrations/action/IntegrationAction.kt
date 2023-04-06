package org.opensearch.integrations.action

import org.opensearch.client.node.NodeClient
import org.opensearch.rest.RestRequest
import org.opensearch.rest.RestResponse

interface IntegrationAction<Request, Response> {
    fun getName(): String
    fun getRoutes(): List<Pair<RestRequest.Method, String>>
    fun read(rawRequest: RestRequest): Request
    fun execute(request: Request, client: NodeClient): Response
    fun write(response: Response): RestResponse
}
