package org.opensearch.integrations.resthandler

import org.opensearch.client.node.NodeClient
import org.opensearch.commons.utils.logger
import org.opensearch.integrations.IntegrationsPlugin.Companion.BASE_INTEGRATIONS_URI
import org.opensearch.rest.BaseRestHandler
import org.opensearch.rest.BytesRestResponse
import org.opensearch.rest.RestHandler.Route
import org.opensearch.rest.RestRequest
import org.opensearch.rest.RestRequest.Method
import org.opensearch.rest.RestStatus

class CreateIntegrationStoreRestHandler: BaseRestHandler() {
    companion object {
        private const val INTEGRATIONS_ACTION = "integration_store_create"
        private const val URI = "$BASE_INTEGRATIONS_URI/store"
        private val log by logger(CreateIntegrationStoreRestHandler::class.java)
    }

    override fun getName(): String {
        return INTEGRATIONS_ACTION
    }

    override fun routes(): List<Route> {
        return listOf(
            /**
             * Adds an Integration to the system store,
             * expecting the internal URLs to be accessible
             */
            Route(Method.POST, URI),
        )
    }

    override fun prepareRequest(request: RestRequest?, client: NodeClient?): RestChannelConsumer {
        requireNotNull(request)
        log.debug("Received: ${request.path()}")
        return RestChannelConsumer {
            it.sendResponse(BytesRestResponse(RestStatus.NOT_IMPLEMENTED, "{\"error\": \"${request.path()} not implemented\"}"))
        }
    }
}
