import json
import os
import unittest

import jsonschema

import helpers.constants as constants
import helpers.validate as validate


def load_repository_system_schema(name):
    path = os.path.join("../../schema/system", name)
    with open(path, "r") as file:
        schema = json.load(file)
    return schema


class TestDefaults(unittest.TestCase):
    def test_default_config_is_valid(self):
        config_schema = load_repository_system_schema("integration.schema")
        jsonschema.validate(constants.DEFAULT_CONFIG, config_schema)
