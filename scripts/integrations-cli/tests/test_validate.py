import unittest
from copy import deepcopy

import jsonschema

import helpers.constants as constants
import helpers.validate as validate


class TestDefaults(unittest.TestCase):
    def test_default_config_is_valid(self):
        config_schema = constants.SCHEMAS["integration.schema"]
        jsonschema.validate(constants.DEFAULT_CONFIG, config_schema)
    
class TestFailingValidations(unittest.TestCase):
    def test_default_with_no_name_is_invalid(self):
        config = deepcopy(constants.DEFAULT_CONFIG)
        del config["template-name"]
        self.assertRaises(
            jsonschema.exceptions.SchemaError,
            validate.validate_config(constants.DEFAULT_CONFIG)
        )
