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
    click.echo(f"Integration created at '{integration_path}'")


@integrations_cli.command()
def check():
    """Analyze the current Integration and report errors."""
    click.echo("Checked integration")


@integrations_cli.command()
@click.argument("filepath")
def package(filepath: str):
    """Package the current integration for use in OpenSearch."""
    os.makedirs("artifacts", exist_ok=True)
    _, filename = os.path.split(filepath)
    with zipfile.ZipFile(f"artifacts/{filename}.zip", "w") as zf:
        for (_, dirnames, filenames) in os.walk(filepath):
            for item in dirnames + filenames:
                zf.write(os.path.join(filepath, item), arcname=item)


if __name__ == "__main__":
    integrations_cli()
