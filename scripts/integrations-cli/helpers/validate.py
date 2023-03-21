def validate_config(config: dict) -> dict:
    assert isinstance(config["name"], str)
    assert isinstance(config["repository"]["url"], str)
    return config
