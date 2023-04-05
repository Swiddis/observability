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

class GetIntegrationStoreRestHandler : BaseRestHandler() {
    companion object {
        private const val INTEGRATIONS_ACTION = "integration_store_get"
        private const val URI = "$BASE_INTEGRATIONS_URI/store"
        private const val ID = "integration_id"
        private val log by logger(CreateIntegrationStoreRestHandler::class.java)
    }

    override fun getName(): String {
        return INTEGRATIONS_ACTION
    }

    override fun routes(): List<Route> {
        return listOf(
            /**
             * By passing in the appropriate search attributes,
             * you can search for available integrations in the store
             */
            Route(Method.GET, URI),
            /**
             * Get the stored Integration's status
             */
            Route(Method.GET, "$URI/{$ID}")
        )
    }

    override fun prepareRequest(request: RestRequest?, client: NodeClient?): RestChannelConsumer {
        requireNotNull(request)
        log.debug("Received: ${request.path()}")
        return when (request.hasParam(ID)) {
            // TODO
            true -> RestChannelConsumer {
                it.sendResponse(BytesRestResponse(RestStatus.NOT_IMPLEMENTED, "{\"error\": \"${request.path()} not implemented\"}"))
            }
            // TODO
            false -> RestChannelConsumer {
                it.sendResponse(BytesRestResponse(RestStatus.NOT_IMPLEMENTED, "{\"error\": \"${request.path()} not implemented\"}"))
            }
        }
    }
}
