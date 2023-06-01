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

```http
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

```http
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

## Creating the Index Template

After the dependencies are added, we can make the index template.
This is the template that will be applied to the data.
Giving our template the name `logs`, we'll `POST` the mapping file to `_index_template/logs`.

<details>
    <summary>Full Request for copy-pasting</summary>

```http
POST _index_template/logs
{
  "index_patterns": [
    "sso_logs-*-*"
  ],
  "data_stream": {},
  "template": {
    "mappings": {
      "_meta": {
        "version": "1.0.0",
        "catalog": "observability",
        "type": "logs",
        "component": "log",
        "correlations": [
          {
            "field": "spanId",
            "foreign-schema": "traces",
            "foreign-field": "spanId"
          },
          {
            "field": "traceId",
            "foreign-schema": "traces",
            "foreign-field": "traceId"
          }
        ]
      },
      "_source": {
        "enabled": true
      },
      "dynamic_templates": [
        {
          "resources_map": {
            "mapping": {
              "type": "keyword"
            },
            "path_match": "resource.*"
          }
        },
        {
          "attributes_map": {
            "mapping": {
              "type": "keyword"
            },
            "path_match": "attributes.*"
          }
        },
        {
          "instrumentation_scope_attributes_map": {
            "mapping": {
              "type": "keyword"
            },
            "path_match": "instrumentationScope.attributes.*"
          }
        }
      ],
      "properties": {
        "severity": {
          "properties": {
            "number": {
              "type": "long"
            },
            "text": {
              "type": "text",
              "fields": {
                "keyword": {
                  "type": "keyword",
                  "ignore_above": 256
                }
              }
            }
          }
        },
        "attributes": {
          "type": "object",
          "properties": {
            "data_stream": {
              "properties": {
                "dataset": {
                  "ignore_above": 128,
                  "type": "keyword"
                },
                "namespace": {
                  "ignore_above": 128,
                  "type": "keyword"
                },
                "type": {
                  "ignore_above": 56,
                  "type": "keyword"
                }
              }
            }
          }
        },
        "body": {
          "type": "text"
        },
        "@timestamp": {
          "type": "date"
        },
        "observedTimestamp": {
          "type": "date"
        },
        "observerTime": {
          "type": "alias",
          "path": "observedTimestamp"
        },
        "traceId": {
          "ignore_above": 256,
          "type": "keyword"
        },
        "spanId": {
          "ignore_above": 256,
          "type": "keyword"
        },
        "schemaUrl": {
          "type": "text",
          "fields": {
            "keyword": {
              "type": "keyword",
              "ignore_above": 256
            }
          }
        },
        "instrumentationScope": {
          "properties": {
            "name": {
              "type": "text",
              "fields": {
                "keyword": {
                  "type": "keyword",
                  "ignore_above": 128
                }
              }
            },
            "version": {
              "type": "text",
              "fields": {
                "keyword": {
                  "type": "keyword",
                  "ignore_above": 256
                }
              }
            },
            "dropped_attributes_count": {
              "type": "integer"
            },
            "schemaUrl": {
              "type": "text",
              "fields": {
                "keyword": {
                  "type": "keyword",
                  "ignore_above": 256
                }
              }
            }
          }
        },
        "event": {
          "properties": {
            "domain": {
              "ignore_above": 256,
              "type": "keyword"
            },
            "name": {
              "ignore_above": 256,
              "type": "keyword"
            },
            "source": {
              "ignore_above": 256,
              "type": "keyword"
            },
            "category": {
              "ignore_above": 256,
              "type": "keyword"
            },
            "type": {
              "ignore_above": 256,
              "type": "keyword"
            },
            "kind": {
              "ignore_above": 256,
              "type": "keyword"
            },
            "result": {
              "ignore_above": 256,
              "type": "keyword"
            },
            "exception": {
              "properties": {
                "message": {
                  "ignore_above": 1024,
                  "type": "keyword"
                },
                "type": {
                  "ignore_above": 256,
                  "type": "keyword"
                },
                "stacktrace": {
                  "type": "text"
                }
              }
            }
          }
        }
      }
    },
    "settings": {
      "index": {
        "mapping": {
          "total_fields": {
            "limit": 10000
          }
        },
        "refresh_interval": "5s"
      }
    }
  },
  "composed_of": [
    "http_template",
    "communication_template"
  ],
  "version": 1,
  "_meta": {
    "description": "Simple Schema For Observability",
    "catalog": "observability",
    "type": "logs",
    "correlations": [
      {
        "field": "spanId",
        "foreign-schema": "traces",
        "foreign-field": "spanId"
      },
      {
        "field": "traceId",
        "foreign-schema": "traces",
        "foreign-field": "traceId"
      }
    ]
  }
}
```
</details>

On success, there will be an `{"acknowledged": true}` response.
We can additionally verify the addition with `GET _index_template/logs`.

At this point, the template will be enforced on all new indices with the template's index pattern.

```json5
{
    "index_patterns": [
        "sso_logs-*-*"
    ],
    // ...
}
```

We can verify this by attempting to create an invalid document on one of these indices.

```http
POST sso_logs-demo-demo/_doc
{
  "invalid": true
}
```

An error response with `"type": "mapper_parsing_exception"` will be returned.

In contrast, attempting to create a valid log should work:

```http
POST sso_logs-demo-demo/_doc
{
  "@timestamp": "2022-12-09T10:39:23.000Z",
  "http": {
    "method": "GET",
    "flavor": "1.1",
    "url": "https://example.com:8080/webshop/articles/4?s=1"
  },
  "destination": {
    "domain": "example.com",
    "address": "192.0.2.5",
    "port": 8080
  },
  "response": {
    "status_code": 200
  }
}
```

Should return a response with `"result": "created"`.

The demo stream can be cleared with `DELETE _data_stream/sso_logs-demo-demo`.

## Conclusion

At this point, an index template has been set up for use with the Integrations Plugin.
In later tutorials, we will show how to create integrations using this template, and how to load them as a user.
