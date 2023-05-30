# Setting up an Index for OpenSearch Integrations

**Date:** 30 May, 2023

## Introduction

In order to use the integrations plugin, one needs to have an appropriately configured OpenSearch index.
This tutorial will cover how to set up one of these indexes for an application using OTEL collector.
After this tutorial, you will be ready to start exploring the integrations plugin.

## Before you start

You will need to have the following installed or configured:

- OpenSearch 2.7.0 or better (see [Download & Get Started](https://opensearch.org/versions/opensearch-2-7-0.html)).
- A tool to make HTTP requests against an OpenSearch cluster.
    - We use the developer tools from a running version of [OpenSearch Dashboards](https://opensearch.org/versions/opensearch-2-7-0.html#opensearch-dashboards).
      After logging in, from the landing page select "Interact with the OpenSearch API", or alternatively select "Dev Tools" under the "Management" section of the Menu.


