package org.opensearch.integrations

import org.opensearch.cluster.metadata.IndexNameExpressionResolver
import org.opensearch.cluster.node.DiscoveryNodes
import org.opensearch.common.settings.ClusterSettings
import org.opensearch.common.settings.IndexScopedSettings
import org.opensearch.common.settings.Settings
import org.opensearch.common.settings.SettingsFilter
import org.opensearch.integrations.action.GetIntegrationAction
import org.opensearch.integrations.action.CreateIntegrationAction
import org.opensearch.integrations.action.ActivateIntegrationAction
import org.opensearch.integrations.action.ValidateIntegrationAction
import org.opensearch.integrations.action.UploadIntegrationAssetAction
import org.opensearch.integrations.resthandler.SimpleRestHandler
import org.opensearch.plugins.ActionPlugin
import org.opensearch.plugins.Plugin
import org.opensearch.rest.RestController
import org.opensearch.rest.RestHandler
import org.opensearch.rest.RestRequest.Method
import java.util.function.Supplier

class IntegrationsPlugin: Plugin(), ActionPlugin {
    companion object {
        const val PLUGIN_NAME = "opensearch-integrations"
        const val LOG_PREFIX = "integrations"
        const val BASE_INTEGRATIONS_URI = "/_plugins/_integrations"
    }

    override fun getRestHandlers(
        settings: Settings,
        restController: RestController,
        clusterSettings: ClusterSettings,
        indexScopedSettings: IndexScopedSettings,
        settingsFilter: SettingsFilter,
        indexNameExpressionResolver: IndexNameExpressionResolver,
        nodesInCluster: Supplier<DiscoveryNodes>
    ): List<RestHandler> {
        return listOf(
            SimpleRestHandler.from(
                "integration_store_get",
                listOf(
                    Pair(Method.GET, "$BASE_INTEGRATIONS_URI/store"),
                    Pair(Method.GET, "$BASE_INTEGRATIONS_URI/store/{integration_id}")
                ),
                GetIntegrationAction()
            ),
            SimpleRestHandler.from(
                "integration_store_create",
                listOf(
                    Pair(Method.POST, "$BASE_INTEGRATIONS_URI/store"),
                ),
                CreateIntegrationAction()
            ),
            SimpleRestHandler.from(
                "integration_store_activate",
                listOf(Pair(Method.PUT, "$BASE_INTEGRATIONS_URI/store/{integration_id}/activate")),
                ActivateIntegrationAction()
            ),
            SimpleRestHandler.from(
                "integration_store_upload",
                listOf(Pair(Method.PUT, "$BASE_INTEGRATIONS_URI/store/{integration_id}/upload")),
                UploadIntegrationAssetAction()
            ),
            SimpleRestHandler.from(
                "integration_store_validate",
                listOf(Pair(Method.PUT, "$BASE_INTEGRATIONS_URI/store/{integration_id}/validate")),
                ValidateIntegrationAction()
            ),
        )
    }
}
