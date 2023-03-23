#!./venv/bin/python3
import os
import zipfile

import click

import helpers


@click.group()
def integrations_cli():
    """Create and maintain Integrations for the OpenSearch Integrations Plugin."""
    pass


@integrations_cli.command()
@click.argument("filepath")
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
    click.echo(f"Integration created at '{integration_path}'")


@integrations_cli.command()
@click.argument("name")
def check(name: str):
    """Analyze the current Integration and report errors."""
    click.echo(f"Checking integration '{name}'")
    # No-op for now
    click.echo(f"Checked integration '{name}'")


@integrations_cli.command()
@click.argument("name")
def package(name: str):
    """Package the current integration for use in OpenSearch."""
    click.echo(f"Packaging integration '{name}'")
    os.makedirs("artifacts", exist_ok=True)
    integration_path = os.path.join(os.getcwd(), "integrations", name)
    with zipfile.ZipFile(f"artifacts/{name}.zip", "w") as zf:
        for (_, dirnames, filenames) in os.walk(integration_path):
            for item in dirnames + filenames:
                zf.write(os.path.join(integration_path, item), arcname=item)
    click.echo(f"Packaged integration '{name}'")


if __name__ == "__main__":
    integrations_cli()
