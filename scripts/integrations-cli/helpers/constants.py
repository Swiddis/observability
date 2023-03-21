import glob
import json
import os

DEFAULT_CONFIG = {
    "template-name": "default",
    "version": {"integration": "0.1.0", "schema": "1.0.0", "resource": "^1.23.0"},
    "description": "",
    "identification": "",
    "catalog": "observability",
    "components": [],
    "collection": [],
    "repository": {"url": "https://example.com/"},
}

SCHEMAS = {}

# For now, assume we're running in the current relative directory
if not os.path.isdir("../../../schema"):
    for filename in glob.glob("../../schema/**/*.schema", recursive=True):
        schema_name = os.path.split(filename)[1]
        with open(filename, "r") as schema_file:
            SCHEMAS[schema_name] = json.load(schema_file)
