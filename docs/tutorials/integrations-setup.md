# Setting up an Index for OpenSearch Integrations

**Date:** 1 June, 2023

## Introduction

In order to use the integrations plugin, one needs to have an appropriately configured OpenSearch index.
This tutorial will cover how to set up one of these indexes for an application using OTEL collector.
After this tutorial, you will be ready to start exploring the integrations plugin.

## Before you start

You will need to have the following installed or configured:

- OpenSearch 2.7.0 or better (see [Download & Get Started](https://opensearch.org/versions/opensearch-2-7-0.html)).
- A compatible version of [OpenSearch Dashboards](https://opensearch.org/versions/opensearch-2-7-0.html#opensearch-dashboards).
- [Docker](https://www.docker.com/).
    - For this tutorial, we recommend [running the OpenSearch instance in Docker](https://opensearch.org/docs/latest/install-and-configure/install-opensearch/docker/).

Start an instance of OpenSearch and OpenSearch Dashboards.
You should be able to access OpenSearch Dashboards at `http://localhost:5601/`.
The default username and password is `admin` and `admin`.

## Deciding on a target

Before we can set up an index, we need some idea of the data we want to display.
For this tutorial series, we will be using OTEL-formatted logs.

All integrations are built on top of an index template.
A index template specifies the format that the data must comply with.
These templates are defined in the mapping files of the catalog.
In order to store OTEL-formatted logs, we want to create a template using the OTEL logs mapping.
A copy of this schema is stored [in the schema folder for observability](../../src/main/resources/schema/observability/logs/logs.mapping).

## Creating Component Templates

Under the mapping file, we can see it relies on two dependencies:

```json5
{
    // ...
    "composed_of": [
        "http_template",
        "communication_template"
    ]
    // ...
}
```

These dependencies are known as "component templates".
We will begin by creating these dependencies.
This step can be skipped if the template has no dependencies.

Begin by logging into OpenSearch Dashboards.
Then, open the [Dev Tools](http://localhost:5601/app/dev_tools#/console).
If the link doesn't work, you can click the `OpenSearch Dashboards` logo in the top left, and select `Interact with the OpenSearch API`.

To create a template, we `POST` to the `_component_template` endpoint.
Create a `POST` to `_component_template/http_template` and paste the body of the [HTTP Mapping](../../src/main/resources/schema/observability/logs/http.mapping).
You should get:

<details>
    <summary>Full Request for copy-pasting</summary>

```json5
POST _component_template/http_template
{
  "template": {
    "mappings": {
      "_meta": {
        "version": "1.0.0",
        "catalog": "observability",
        "type": "logs",
        "component": "http"
      },
      "dynamic_templates": [
        {
          "request_header_map": {
            "mapping": {
              "type": "keyword"
            },
            "path_match": "request.header.*"
          }
        },
        {
          "response_header_map": {
            "mapping": {
              "type": "keyword"
            },
            "path_match": "response.header.*"
          }
        }
      ],
      "properties": {
        "http": {
          "properties": {
            "flavor": {
              "type": "keyword",
              "ignore_above": 256
            },
            "user_agent": {
              "type": "keyword",
              "ignore_above": 2048
            },
            "url": {
              "type": "keyword",
              "ignore_above": 2048
            },
            "schema": {
              "type": "keyword",
              "ignore_above": 1024
            },
            "target": {
              "type": "keyword",
              "ignore_above": 1024
            },
            "route": {
              "type": "keyword",
              "ignore_above": 1024
            },
            "client.ip": {
              "type": "ip"
            },
            "resent_count": {
              "type": "integer"
            },
            "request": {
              "type": "object",
              "properties": {
                "id": {
                  "type": "text",
                  "fields": {
                    "keyword": {
                      "type": "keyword",
                      "ignore_above": 256
                    }
                  }
                },
                "body.content": {
                  "type": "text"
                },
                "bytes": {
                  "type": "long"
                },
                "method": {
                  "type": "keyword",
                  "ignore_above": 256
                },
                "referrer": {
                  "type": "keyword",
                  "ignore_above": 1024
                },
                "mime_type": {
                  "type": "keyword",
                  "ignore_above": 1024
                }
              }
            },
            "response": {
              "type": "object",
              "properties": {
                "id": {
                  "type": "text",
                  "fields": {
                    "keyword": {
                      "type": "keyword",
                      "ignore_above": 256
                    }
                  }
                },
                "body.content": {
                  "type": "text"
                },
                "bytes": {
                  "type": "long"
                },
                "status_code": {
                  "type": "integer"
                }
              }
            }
          }
        }
      }
    }
  }
}
```
</details>

After running the request, we should get an `{"acknowledged": true}` response.

Do the same for the communication mapping to `_component_template/communication_template`.

<details>
    <summary>Full Request for copy-pasting</summary>

```json5
POST _component_template/communication_template
{
  "template": {
    "mappings": {
      "_meta": {
        "version": "1.0.0",
        "catalog": "observability",
        "type": "logs",
        "component": "communication"
      },
      "properties": {
        "communication": {
          "properties": {
            "sock.family": {
              "type": "keyword",
              "ignore_above": 256
            },
            "source": {
              "type": "object",
              "properties": {
                "address": {
                  "type": "text",
                  "fields": {
                    "keyword": {
                      "type": "keyword",
                      "ignore_above": 1024
                    }
                  }
                },
                "domain": {
                  "type": "text",
                  "fields": {
                    "keyword": {
                      "type": "keyword",
                      "ignore_above": 1024
                    }
                  }
                },
                "bytes": {
                  "type": "long"
                },
                "ip": {
                  "type": "ip"
                },
                "port": {
                  "type": "long"
                },
                "mac": {
                  "type": "keyword",
                  "ignore_above": 1024
                },
                "packets": {
                  "type": "long"
                }
              }
            },
            "destination": {
              "type": "object",
              "properties": {
                "address": {
                  "type": "text",
                  "fields": {
                    "keyword": {
                      "type": "keyword",
                      "ignore_above": 1024
                    }
                  }
                },
                "domain": {
                  "type": "text",
                  "fields": {
                    "keyword": {
                      "type": "keyword",
                      "ignore_above": 1024
                    }
                  }
                },
                "bytes": {
                  "type": "long"
                },
                "ip": {
                  "type": "ip"
                },
                "port": {
                  "type": "long"
                },
                "mac": {
                  "type": "keyword",
                  "ignore_above": 1024
                },
                "packets": {
                  "type": "long"
                }
              }
            }
          }
        }
      }
    }
  }
}
```
</details>

Similar to the last one, we should get an `{"acknowledged": true}` response.
As an additional sanity check, we can run `GET _component_template` and verify that the two templates are returned.
