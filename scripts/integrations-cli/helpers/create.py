import os
import json
from copy import deepcopy

from .constants import DEFAULT_CONFIG
from .validate import validate_config


class IntegrationBuilder:
    def __init__(self):
        self.path = None
        self.config = deepcopy(DEFAULT_CONFIG)

    def with_name(self, name: str) -> "IntegrationBuilder":
        self.config["name"] = name
        return self
    
    def with_path(self, path: str) -> "IntegrationBuilder":
        self.path = path
        self.config["repository"]["url"] = f"file://{path}"
        return self
    
    def build(self):
        assert self.path is not None
        os.makedirs(self.path, exist_ok=True)
        files = {
            "config.json": validate_config(self.config)
        }
        for filename, data in files.items():
            with open(os.path.join(self.path, filename), "w") as file:
                json.dump(data, file, ensure_ascii=False, indent=2)
