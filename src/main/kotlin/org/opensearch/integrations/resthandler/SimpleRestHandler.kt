package org.opensearch.integrations.resthandler

import org.opensearch.client.node.NodeClient
import org.opensearch.commons.utils.logger
import org.opensearch.integrations.action.IntegrationAction
import org.opensearch.rest.BaseRestHandler
import org.opensearch.rest.RestHandler
import org.opensearch.rest.RestRequest

/**
 * Generic class for building Rest Handlers with less boilerplate.
 * If your handler needs more elaborate behavior, then manually override [org.opensearch.rest.BaseRestHandler].
 */
object SimpleRestHandler {
    fun <Request, Response> from(actionName: String, uris: List<Pair<RestRequest.Method, String>>, action: IntegrationAction<Request, Response>): BaseRestHandler {
        val handler = object : BaseRestHandler() {
            private val log by logger(SimpleRestHandler::class.java)

            override fun getName(): String {
                return actionName
            }

            override fun routes(): List<RestHandler.Route> {
                return uris.map {
                    RestHandler.Route(it.first, it.second)
                }
            }

            override fun prepareRequest(request: RestRequest, client: NodeClient): RestChannelConsumer {
                log.debug("${actionName}: ${request.method()} ${request.path()}")
                // TODO error handling
                // Not sure what approach to take for robustness
                // Some options:
                // - Try railway-oriented and use `Result`s,
                // - Stick with Kotlin's idiomatic `require`, `check`, `assert`
                // - Null safety
                // - Full java-style exceptions
                val requestData: Request = action.read(request)
                val responseData: Response = action.execute(requestData, client)
                val response = action.write(responseData)
                return RestChannelConsumer {
                    it.sendResponse(response)
                }
            }
        }
        return handler
    }
}
