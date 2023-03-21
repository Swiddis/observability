import logging

import jsonschema

from .constants import SCHEMAS


def validate_config(config: dict) -> dict:
    if "integration.schema" in SCHEMAS:
        jsonschema.validate(SCHEMAS["integration.schema"], config)
    else:
        logging.warn("Config schema not found, skipping config validation")
    return config
