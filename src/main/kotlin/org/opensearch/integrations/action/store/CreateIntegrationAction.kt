package org.opensearch.integrations.action.store

import com.fasterxml.jackson.databind.ObjectMapper
import org.opensearch.common.Strings
import org.opensearch.common.xcontent.json.JsonXContent
import org.opensearch.commons.utils.contentParserNextToken
import org.opensearch.commons.utils.logger
import org.opensearch.integrations.model.Integration
import org.opensearch.integrations.resthandler.IntegrationStoreRestHandler
import org.opensearch.integrations.validation.Validator
import org.opensearch.integrations.validation.Success
import org.opensearch.integrations.validation.Rejected
import org.opensearch.integrations.validation.SchemaNotLoaded
import org.opensearch.integrations.validation.MalformedJson
import org.opensearch.integrations.validation.schema.system.SystemComponent
import org.opensearch.rest.BytesRestResponse
import org.opensearch.rest.RestChannel
import org.opensearch.rest.RestRequest
import org.opensearch.rest.RestStatus

object CreateIntegrationAction {
    private val log by logger(IntegrationStoreRestHandler::class.java)

    fun handle(channel: RestChannel, request: RestRequest) {
        require(request.hasContent())
        val parser = request.contentParserNextToken()
        val builder = JsonXContent.contentBuilder()
        builder.copyCurrentStructure(parser)
        log.error("$builder")
        val validator = Validator(SystemComponent.INTEGRATION_INSTANCE)
        val text = Strings.toString(builder)
        log.error(text)
        when (validator.validate(text)) {
            is Success -> handleSuccess(channel, text)
            is MalformedJson -> handleFailure(channel, RestStatus.BAD_REQUEST, "Request body JSON was malformed")
            is Rejected -> handleFailure(channel, RestStatus.BAD_REQUEST, "Request body is not a valid integration")
            is SchemaNotLoaded -> handleFailure(channel, RestStatus.INTERNAL_SERVER_ERROR, "Failed to load integration instance schema")
        }
    }

    private fun handleFailure(channel: RestChannel, status: RestStatus, message: String) {
        // TODO generate with XContent
        channel.sendResponse(BytesRestResponse(status, "{\"status\": ${status.status}, \"error\": \"$message\"}"))
    }

    private fun handleSuccess(channel: RestChannel, json: String) {
        val mapper = ObjectMapper()
        // TODO
        val integ = mapper.readValue(json, Integration::class.java)
        channel.sendResponse(BytesRestResponse(RestStatus.CREATED, "{\"message\": \"dummy response\", \"object\": \"$integ\"}"))
    }
}
