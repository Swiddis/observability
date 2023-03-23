#!./venv/bin/python3
import json
import os
import zipfile

import click
import jsonschema
from termcolor import colored

import helpers


@click.group()
def integrations_cli():
    """Create and maintain Integrations for the OpenSearch Integrations Plugin."""
    pass


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
    for subdir in ["assets", "info", "samples", "schema", "test"]:
        os.makedirs(os.path.join(integration_path, subdir))
    builder = helpers.IntegrationBuilder().with_name(name).with_path(integration_path)
    builder.build()
    click.echo(colored(f"Integration created at '{integration_path}'", "green"))


def full_integration_is_valid(name: str) -> bool:
    integration_path = os.path.join(os.getcwd(), "integrations", name)
    integration_parts = {"config.json": helpers.validate.validate_config}
    encountered_errors = False
    for item, validator in integration_parts.items():
        item_path = os.path.join(integration_path, item)
        with open(item_path, "r") as item_data:
            try:
                loaded = json.load(item_data)
                validator(loaded)
            except jsonschema.exceptions.ValidationError as err:
                click.echo(colored(f"'{item_path}' is invalid", "red"))
                err_msg = ("> " + str(err)).replace("\n", "\n> ")
                click.echo(colored(err_msg, "red"), err=True)
                encountered_errors = True
    return not encountered_errors


@integrations_cli.command()
@click.argument("name")
def check(name: str):
    """Analyze the current Integration and report errors."""
    click.echo(f"Checking integration '{name}'")
    if full_integration_is_valid(name):
        click.echo(colored("Integration is valid", "green"))


@integrations_cli.command()
@click.argument("name")
def package(name: str):
    """Package the current integration for use in OpenSearch."""
    click.echo(f"Checking integration '{name}'")
    if not full_integration_is_valid(name):
        return
    click.echo(f"Packaging integration '{name}'")
    os.makedirs("artifacts", exist_ok=True)
    integration_path = os.path.join(os.getcwd(), "integrations", name)
    artifact_path = os.path.join("artifacts", f"{name}.zip")
    with zipfile.ZipFile(artifact_path, "w") as zf:
        for _, dirnames, filenames in os.walk(integration_path):
            for item in dirnames + filenames:
                zf.write(os.path.join(integration_path, item), arcname=item)
    click.echo(colored(f"Packaged integration as '{artifact_path}'", "green"))


if __name__ == "__main__":
    integrations_cli()
