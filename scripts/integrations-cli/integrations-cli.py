#!./venv/bin/python3
import click


@click.group()
def integrations_cli():
    """Create and maintain Integrations for the OpenSearch Integrations Plugin."""
    pass


@integrations_cli.command()
def create():
    """Create a new Integration from a specified template."""
    click.echo("Created integration")


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
