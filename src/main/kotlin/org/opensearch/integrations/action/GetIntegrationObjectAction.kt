/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.integrations.action

import org.opensearch.OpenSearchStatusException
import org.opensearch.action.ActionType
import org.opensearch.action.support.ActionFilters
import org.opensearch.client.Client
import org.opensearch.common.inject.Inject
import org.opensearch.commons.authuser.User
import org.opensearch.core.xcontent.NamedXContentRegistry
import org.opensearch.integrations.index.IntegrationIndex
import org.opensearch.integrations.model.IntegrationObjectSearchResult
import org.opensearch.observability.ObservabilityPlugin.Companion.LOG_PREFIX
import org.opensearch.observability.action.GetObservabilityObjectRequest
import org.opensearch.observability.action.GetObservabilityObjectResponse
import org.opensearch.observability.action.ObservabilityActions
import org.opensearch.observability.action.PluginBaseAction
import org.opensearch.observability.metrics.Metrics
import org.opensearch.observability.security.UserAccessManager
import org.opensearch.observability.util.logger
import org.opensearch.rest.RestStatus
import org.opensearch.transport.TransportService

/**
 * Get ObservabilityObject transport action
 */
internal class GetIntegrationObjectAction @Inject constructor(
    transportService: TransportService,
    client: Client,
    actionFilters: ActionFilters,
    val xContentRegistry: NamedXContentRegistry
) : PluginBaseAction<GetIntegrationObjectRequest, GetIntegrationObjectResponse>(
    NAME,
    transportService,
    client,
    actionFilters,
    ::GetIntegrationObjectRequest
) {
    private val log by logger(ObservabilityActions::class.java)
    companion object {
        private const val NAME = "cluster:admin/opensearch/integrations/store/get"
        internal val ACTION_TYPE = ActionType(NAME, ::GetIntegrationObjectResponse)
    }
    /**
     * Get all ObservabilityObject matching the criteria
     * @param request [GetObservabilityObjectRequest] object
     * @param user the user info object
     * @return [GetObservabilityObjectResponse]
     */
    private fun getAll(request: GetIntegrationObjectRequest, user: User?): GetIntegrationObjectResponse {
        log.info("$LOG_PREFIX:IntegrationObject-getAll")
        val searchResult = IntegrationIndex.getAllIntegrationObjects(
            UserAccessManager.getUserTenant(user),
            UserAccessManager.getSearchAccessInfo(user),
            request
        )
        return GetIntegrationObjectResponse(searchResult, UserAccessManager.hasAllInfoAccess(user))
    }

    /**
     * Get IntegrationObject info for one object id
     * @param objectId object id
     * @param user the user info object
     * @return [GetIntegrationObjectResponse]
     */
    private fun getOne(objectId: String, user: User?): GetIntegrationObjectResponse {
        log.info("$LOG_PREFIX:IntegrationObject-info $objectId")
        val objectDoc = IntegrationIndex.getIntegrationObjectDoc(objectId)?.objectDoc
        objectDoc ?: run {
            throw OpenSearchStatusException("Integration $objectId not found", RestStatus.NOT_FOUND)
        }
        if (!UserAccessManager.doesUserHasAccess(user, objectDoc.tenant, objectDoc.access)) {
            Metrics.OBSERVABILITY_PERMISSION_USER_ERROR.counter.increment()
            throw OpenSearchStatusException("Permission denied for Integration $objectId", RestStatus.FORBIDDEN)
        }
        return GetIntegrationObjectResponse(
            IntegrationObjectSearchResult(objectDoc),
            UserAccessManager.hasAllInfoAccess(user)
        )
    }

    /**
     * {@inheritDoc}
     */
    override fun executeRequest(request: GetIntegrationObjectRequest, user: User?): GetIntegrationObjectResponse {
        log.info("$LOG_PREFIX:IntegrationObject-get ${request.objectIds}")
        UserAccessManager.validateUser(user)
        return when (request.objectIds.size) {
            0 -> getAll(request, user)
            1 -> getOne(request.objectIds.first(), user)
            else -> TODO("Not yet implemented")
        }
    }
}
