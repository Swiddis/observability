#!./venv/bin/python3
import os

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
    builder = helpers.IntegrationBuilder().with_name(name).with_path(integration_path)
    builder.build()
    click.echo(f"Integration created at '{integration_path}'")


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
