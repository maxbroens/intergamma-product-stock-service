# Product Stock Service

This is a simple product stock service that allows you to manage products and their stock levels.

## Run locally

in order to run the application locally, use the following command:
```./gradlew run```

## Run tests

in order to run the tests, use the following command:
```./gradlew test```

## Build the application

in order to build the application, use the following command:
```./gradlew build```

## See API documentation

The API documentation is available at the following URL: http://localhost:8080/swagger-ui/index.html
after the application is started.

All GET calls require basic authentication. The default username `reader` and password can be found in application.yaml.
All POST, PUT and DELETE calls require basic authentication. The default username `writer` and password can be found in
application.yaml.

## Test usage flow

Get all products

``` 
curl --location 'http://localhost:8080/api/products' \
--header 'Authorization: Basic cmVhZGVyOnJlYWRlcjEyMzQ='
```

Use one of the product ids perform stock related calls. See API documentation.