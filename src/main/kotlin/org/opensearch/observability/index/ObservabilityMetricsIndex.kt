/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.opensearch.observability.index

import org.opensearch.ResourceAlreadyExistsException
import org.opensearch.ResourceNotFoundException
import org.opensearch.action.admin.indices.datastream.CreateDataStreamAction
import org.opensearch.action.admin.indices.datastream.GetDataStreamAction
import org.opensearch.action.admin.indices.template.get.GetIndexTemplatesRequest
import org.opensearch.action.admin.indices.template.put.PutComposableIndexTemplateAction
import org.opensearch.client.Client
import org.opensearch.cluster.metadata.ComposableIndexTemplate
import org.opensearch.cluster.metadata.Template
import org.opensearch.cluster.service.ClusterService
import org.opensearch.common.component.LifecycleListener
import org.opensearch.common.compress.CompressedXContent
import org.opensearch.common.settings.Settings
import org.opensearch.observability.ObservabilityPlugin.Companion.LOG_PREFIX
import org.opensearch.observability.settings.PluginSettings
import org.opensearch.observability.util.SecureIndexClient
import org.opensearch.observability.util.logger
import java.util.*

/**
 * Class for doing OpenSearch Metrics schema mapping & default index init operation
 */
internal object ObservabilityMetricsIndex : LifecycleListener() {
    private val log by logger(ObservabilityMetricsIndex::class.java)
    private const val METRICS_MAPPING_TEMPLATE_NAME = "sso_metric_template"
    private const val METRICS_MAPPING_TEMPLATE_FILE = "metrics-mapping-template.json"
    private const val METRIC_PATTERN_NAME = "sso_metrics-*-*"

    private lateinit var client: Client
    private lateinit var clusterService: ClusterService

    /**
     * Initialize the class
     * @param client The OpenSearch client
     * @param clusterService The OpenSearch cluster service
     */
    fun initialize(client: Client, clusterService: ClusterService): ObservabilityMetricsIndex {
        this.client = SecureIndexClient(client)
        this.clusterService = clusterService
        return this
    }

    /**
     * once lifecycle indicate start has occurred - instantiating the mapping template
     */
    override fun afterStart() {
        // create default mapping
        createMappingTemplate()
    }


    /**
     * Create the pre-defined mapping template
     */
    @Suppress("TooGenericExceptionCaught", "MagicNumber")
    private fun createMappingTemplate() {
        log.info("$LOG_PREFIX:createMappingTemplate $METRICS_MAPPING_TEMPLATE_NAME API called")
        if (!isTemplateExists(METRICS_MAPPING_TEMPLATE_NAME)) {
            val classLoader = ObservabilityMetricsIndex::class.java.classLoader
            val indexMappingSource = classLoader.getResource(METRICS_MAPPING_TEMPLATE_FILE)?.readText()!!
            val settings = Settings.builder()
                .put("index.number_of_shards", 3)
                .put("index.auto_expand_replicas", "0-2")
                .build()
            val template = Template(settings, CompressedXContent(indexMappingSource), null)
            val request = PutComposableIndexTemplateAction.Request(METRICS_MAPPING_TEMPLATE_NAME)
                .indexTemplate(
                    ComposableIndexTemplate(
                        listOf(METRIC_PATTERN_NAME),
                        template,
                        Collections.emptyList(),
                        1,
                        1,
                        Collections.singletonMap("description", "Observability Metrics Mapping Template") as Map<String, Any>?,
                        ComposableIndexTemplate.DataStreamTemplate()
                    )
                )
            try {
                val validationException = request.validateIndexTemplate(null)
                if (validationException != null && !validationException.validationErrors().isEmpty()) {
                    error("$LOG_PREFIX:Index Template $METRICS_MAPPING_TEMPLATE_NAME validation errors ${validationException.message}")
                }
                val actionFuture = client.admin().indices().execute(PutComposableIndexTemplateAction.INSTANCE, request)
                val response = actionFuture.actionGet(PluginSettings.operationTimeoutMs)
                if (response.isAcknowledged) {
                    log.info("$LOG_PREFIX:Mapping Template $METRICS_MAPPING_TEMPLATE_NAME creation Acknowledged")
                } else {
                    error("$LOG_PREFIX:Mapping Template $METRICS_MAPPING_TEMPLATE_NAME creation not Acknowledged")
                }
            } catch (exception: ResourceAlreadyExistsException) {
                log.warn("message: ${exception.message}")
            } catch (exception: Exception) {
                if (exception.cause !is ResourceAlreadyExistsException) {
                    throw exception
                }
            }
        }
    }

    /**
     * Check if the mapping template is created and available.
     * @param template name
     * @return true if template is available, false otherwise
     */
    @Suppress("TooGenericExceptionCaught", "SwallowedException", "RethrowCaughtException")
    private fun isTemplateExists(template: String): Boolean {
        try {
            val indices = client.admin().indices()
            val response = indices.getTemplates(GetIndexTemplatesRequest(template)).get()
            return response.indexTemplates.isNotEmpty()
        } catch (exception: ResourceNotFoundException) {
            return false
        } catch (exception: ResourceAlreadyExistsException) {
            return true
        } catch (exception: Exception) {
            throw exception
        }
    }

    /**
     * Check if the data-stream is created and available.
     * @param index
     * @return true if index is available, false otherwise
     */
    @Suppress("TooGenericExceptionCaught", "RethrowCaughtException", "SwallowedException")
    private fun isDataStreamExists(index: String): Boolean {
        try {
            val streams = client.admin().indices().getDataStreams(GetDataStreamAction.Request(arrayOf(index)))
            val response = streams.actionGet()
            return response.dataStreams.isNotEmpty()
        } catch (exception: ResourceNotFoundException) {
            return false
        } catch (exception: ResourceAlreadyExistsException) {
            return true
        } catch (exception: Exception) {
            throw exception
        }
    }
}
