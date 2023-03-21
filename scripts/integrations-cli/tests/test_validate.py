import json
import os
import unittest

import jsonschema

import helpers.constants as constants
import helpers.validate as validate


class TestDefaults(unittest.TestCase):
    def test_default_config_is_valid(self):
        config_schema = constants.SCHEMAS["integration.schema"]
        jsonschema.validate(constants.DEFAULT_CONFIG, config_schema)
