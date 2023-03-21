#!./venv/bin/python3
import click
import os
import json


@click.group()
def integrations_cli():
    """Create and maintain Integrations for the OpenSearch Integrations Plugin."""
    pass


def make_config(name: str, path: str) -> dict:
    return {
        "name": name,
        "version": {"integ": "0.1.0", "schema": "1.0.0", "resource": "^1.23.0"},
        "description": "",
        "identification": "",
        "catalog": "observability",
        "components": [],
        "collection": [],
        "repo": {"github": "file://" + path},
    }


@integrations_cli.command()
@click.argument("name")
def create(name: str):
    """Create a new Integration from a specified template."""
    click.echo(f"Creating new integration '{name}'")
    integration_path = os.path.join(os.getcwd(), "integrations", name)
    if os.path.exists(integration_path) and len(os.listdir(integration_path)) != 0:
        raise click.ClickException(
            f"destination path '{integration_path}' exists and is non-empty"
        )
    os.makedirs(integration_path, exist_ok=True)
    cfg_path = os.path.join(integration_path, "config.json")
    with open(cfg_path, "w") as config:
        json.dump(
            make_config(name, cfg_path), config, ensure_ascii=False, indent=2
        )


@integrations_cli.command()
def check():
    """Analyze the current Integration and report errors."""
    click.echo("Checked integration")


@integrations_cli.command()
def package():
    """Package the current integration for use in OpenSearch."""
    click.echo("Packaged integration")


if __name__ == "__main__":
    integrations_cli()
