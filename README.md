# Virtual Banking System API

Virtual Banking System(VBS) is one of the services used in Apiator project. 
This service is used to manage cards and to make payments in the [Shop service](https://gitlab.com/restlesslizard/apiator-shop).

## Authentication

Authentication is done by remote keycloak server.

## Authorization

Authorization is handled on the resource server side. It is stored as the part of the Account entity of this application.

## Openapi documentation

> TODO

## Environment variables

The following environment variables must be present for the application to operate:
- DB_URL - database url in format `jdbc:mariadb://....`
- DB_USER - database user
- DB_PASSWORD - password for the database user
- OAUTH_ISSUER_URI - keycloak realm URI in format `http://<domain.com>/realms/<realm>`
