##### catalog-ms-spring

# Microservice Apps Integration with MySQL Database

*This project is part of the 'IBM Cloud Native Reference Architecture' suite, available at
https://cloudnativereference.dev/*

## Table of Contents

* [Introduction](#introduction)
    + [APIs](#apis)
* [Pre-requisites](#pre-requisites)
* [Implementation Details](#implementation-details)
* [Running the application on Docker](#running-the-application-on-docker)
    + [Get the Catalog application](#get-the-catalog-application)
    + [Deploy the Elasticsearch Docker Container](#deploy-the-elasticsearch-docker-container)
    + [Run the Catalog application](#run-the-catalog-application)
    + [Validating the application](#validating-the-application)
    + [Exiting the application](#exiting-the-application)
* [Conclusion](#conclusion)

## Introduction

This project will demonstrate how to deploy a Spring Boot Application with an Elasticsearch database onto a Kubernetes Cluster. At the same time, it will also demonstrate how to deploy a dependency Microservice (Inventory) and its MySQL datastore. To know more about Inventory microservices, check [this](https://github.com/ibm-garage-ref-storefront/inventory-ms-spring) out.

![Application Architecture](static/catalog.png?raw=true)

Here is an overview of the project's features:
- Leverage [`Spring Boot`](https://projects.spring.io/spring-boot/) framework to build a Microservices application.
- Uses [`Elasticsearch`](https://www.elastic.co/products/elasticsearch) to persist Catalog data to Elasticsearch database.
- Uses [`Spring data elasticsearch`](https://spring.io/projects/spring-data-elasticsearch) to persist the data to Elasticsearch.
- Uses [`Docker`](https://docs.docker.com/) to package application binary and its dependencies.

### APIs

* Get all items in catalog:
    + `http://localhost:8080/micro/items`
* Get item from catalog using id:
    + `http://localhost:8080/micro/items/{item-id}`

## Pre-requisites:

* [Appsody](https://appsody.dev/)
    + [Installing on MacOS](https://appsody.dev/docs/installing/macos)
    + [Installing on Windows](https://appsody.dev/docs/installing/windows)
    + [Installing on RHEL](https://appsody.dev/docs/installing/rhel)
    + [Installing on Ubuntu](https://appsody.dev/docs/installing/ubuntu)
For more details on installation, check [this](https://appsody.dev/docs/installing/installing-appsody/) out.

* Docker Desktop
    + [Docker for Mac](https://docs.docker.com/docker-for-mac/)
    + [Docker for Windows](https://docs.docker.com/docker-for-windows/)

* Clone inventory repository:

```bash
git clone https://github.com/ibm-garage-ref-storefront/refarch-cloudnative-micro-inventory-spring.git
cd refarch-cloudnative-micro-inventory-spring
```

* Run the MySQL Docker Container

Run the below command to get MySQL running via a Docker container.

```bash
# Start a MySQL Container with a database user, a password, and create a new database
docker run --name inventorymysql \
    -e MYSQL_ROOT_PASSWORD=admin123 \
    -e MYSQL_USER=dbuser \
    -e MYSQL_PASSWORD=password \
    -e MYSQL_DATABASE=inventorydb \
    -p 3306:3306 \
    -d mysql:5.7.14
```

If it is successfully deployed, you will see something like below.

```
$ docker ps
CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS              PORTS                    NAMES
d88a6e5973de        mysql:5.7.14        "docker-entrypoint.s…"   3 minutes ago       Up 3 minutes        0.0.0.0:3306->3306/tcp   inventorymysql
```
* Populate the MySQL Database

Now let us populate the MySQL with data.

- Firstly ssh into the MySQL container.

```
docker exec -it inventorymysql bash
```

* Now, run the below command for table creation.

```
mysql -udbuser -ppassword
```

* This will take you to something like below.

```
root@d88a6e5973de:/# mysql -udbuser -ppassword
mysql: [Warning] Using a password on the command line interface can be insecure.
Welcome to the MySQL monitor.  Commands end with ; or \g.
Your MySQL connection id is 2
Server version: 5.7.14 MySQL Community Server (GPL)

Copyright (c) 2000, 2016, Oracle and/or its affiliates. All rights reserved.

Oracle is a registered trademark of Oracle Corporation and/or its
affiliates. Other names may be trademarks of their respective
owners.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

mysql>
```

* Go to `scripts > mysql_data.sql`. Copy the contents from [mysql_data.sql](./scripts/mysql_data.sql) and paste the contents in the console.

* You can exit from the console using `exit`.

```
mysql> exit
Bye
```

* To come out of the container, enter `exit`.

```
root@d88a6e5973de:/# exit
```

* Run the Inventory application as follows.

```
# Build inventory docker image
appsody build

# Run the inventory
docker run --name inventory \
    -e MYSQL_HOST=<docker_host> \
    -e MYSQL_PORT=3306 \
    -e MYSQL_USER=root \
    -e MYSQL_PASSWORD=password \
    -e MYSQL_DATABASE=inventorydb \
    -p 8081:8080 \
    -d dev.local/inventory-ms-spring
```

For instance, if it is `docker-for-mac` it will be `docker.for.mac.localhost`.

* You can also verify it as follows.

```
$ docker ps
CONTAINER ID        IMAGE                                                 COMMAND                  CREATED             STATUS              PORTS                                            NAMES
f192bd40fbbf        dev.local/inventory-ms-spring                         "/start.sh"              20 hours ago        Up 20 hours         8443/tcp, 0.0.0.0:8081->8080/tcp                 inventory
efed65e2ba5a        mysql:5.7.14                                          "docker-entrypoint.s…"   2 days ago          Up 2 days           0.0.0.0:3300->3306/tcp                           inventorymysql
```

## Implementation Details

We created a new springboot project using appsody as follows.

```
appsody repo add kabanero https://github.com/kabanero-io/kabanero-stack-hub/releases/download/0.6.5/kabanero-stack-hub-index.yaml

appsody init kabanero/java-spring-boot2
```

And then we defined the necessary code for the application on top on this template.

## Running the application on Docker

### Get the Catalog application

- Clone inventory repository:

```bash
git clone https://github.com/ibm-garage-ref-storefront/catalog-ms-spring.git
cd catalog-ms-spring
```

### Deploy the Elasticsearch Docker Container

Run the below command to get Elasticsearch running via a Docker container.

```bash
# Start an Elasticsearch Container
docker run --name catalogelasticsearch \
      -e "discovery.type=single-node" \
      -p 9200:9200 \
      -p 9300:9300 \
      -d docker.elastic.co/elasticsearch/elasticsearch:6.3.2
```

If it is successfully deployed, you will see something like below.

```
$ docker ps
CONTAINER ID        IMAGE                                                 COMMAND                  CREATED             STATUS              PORTS                                                                                              NAMES
0653c983fc6b        docker.elastic.co/elasticsearch/elasticsearch:6.3.2   "/usr/local/bin/dock…"   21 seconds ago      Up 18 seconds       0.0.0.0:9200->9200/tcp, 0.0.0.0:9300->9300/tcp
```

### Run the Catalog application

- To test the unit test cases for the catalog application, run the below command.

```
appsody test --docker-options "-e ELASTIC_CLUSTER_NAME=docker-cluster -e ELASTIC_NODE_URL=host.docker.internal:9300 -e INVENTORY_URL=http://docker.for.mac.localhost:8081/micro/inventory"
```

- To run the catalog application, run the below command.

```
appsody run --docker-options "-e ELASTIC_CLUSTER_NAME=docker-cluster -e ELASTIC_NODE_URL=host.docker.internal:9300 -e INVENTORY_URL=http://docker.for.mac.localhost:8081/micro/inventory"
```

- If it is successfully running, you will see something like below.

```
[Container] 2020-04-29 12:07:08.326  INFO 178 --- [  restartedMain] d.s.w.p.DocumentationPluginsBootstrapper : Context refreshed
[Container] 2020-04-29 12:07:08.379  INFO 178 --- [  restartedMain] d.s.w.p.DocumentationPluginsBootstrapper : Found 1 custom documentation plugin(s)
[Container] 2020-04-29 12:07:08.424  INFO 178 --- [  restartedMain] s.d.s.w.s.ApiListingReferenceScanner     : Scanning for api listing references
[Container] 2020-04-29 12:07:08.704  INFO 178 --- [  restartedMain] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path '/micro'
[Container] 2020-04-29 12:07:08.712  INFO 178 --- [  restartedMain] application.Main                         : Started Main in 14.364 seconds (JVM running for 17.025)
[Container] 2020-04-29 12:07:08.737  INFO 178 --- [  restartedMain] application.Main                         : Starting Inventory Refresh background task ...
[Container] 2020-04-29 12:07:09.185  INFO 178 --- [cTaskExecutor-1] a.catalog.InventoryRefreshTask           : Loaded in to the cache
```

- You can also verify it as follows.

```
$ docker ps
CONTAINER ID        IMAGE                                                 COMMAND                  CREATED             STATUS              PORTS                                                                                              NAMES
d829322e2a00        kabanero/java-spring-boot2:0.3                        "/.appsody/appsody-c…"   2 minutes ago       Up 2 minutes        0.0.0.0:5005->5005/tcp, 0.0.0.0:8080->8080/tcp, 0.0.0.0:8443->8443/tcp, 0.0.0.0:35729->35729/tcp   catalog-ms-spring
f192bd40fbbf        dev.local/inventory-ms-spring                         "/start.sh"              20 hours ago        Up 20 hours         8443/tcp, 0.0.0.0:8081->8080/tcp                                                                   inventory
0653c983fc6b        docker.elastic.co/elasticsearch/elasticsearch:6.3.2   "/usr/local/bin/dock…"   21 hours ago        Up 21 hours         0.0.0.0:9200->9200/tcp, 0.0.0.0:9300->9300/tcp                                                     catalogelasticsearch
efed65e2ba5a        mysql:5.7.14                                          "docker-entrypoint.s…"   2 days ago          Up 2 days           0.0.0.0:3300->3306/tcp                                                                          inventorymysql
```

### Validating the application

Now, you can validate the application as follows.

- Try to hit http://localhost:8080/micro/items/ and you should be able to see a list of items.

- You can also do it using the below command.

```
curl http://localhost:8080/micro/items/
```

![Catalog api](static/catalog_api_result.png?raw=true)

- Also you can access the swagger ui at http://localhost:8080/micro/swagger-ui.html

![Catalog Swagger UI](static/swagger_catalog.png?raw=true)

- We also enabled sonarqube as part of the application.

To run the sonarqube as a docker container, run the below command.

```
docker run -d --name sonarqube -p 9000:9000 sonarqube
```

To test the application, run the below command.

```
./mvnw sonar:sonar -Dsonar.login=admin -Dsonar.password=admin
```

Now, access `http://localhost:9000/`, login using the credentials `admin/admin`, and then you will see something like below.

![Catalog SonarQube](static/catalog_sonarqube.png?raw=true)

- We included contract testing as part of our application too.

To run Pact as a docker container, run the below command.

```
cd pact_docker/
docker-compose up -d
```

To publish the pacts to pacts broker, run the below command.

```
./mvnw clean install pact:publish -Dpact.broker.url=http://localhost:8500 -Ppact-consumer
```

To verify the results, run the below command.

```
 ./mvnw test -Dpact.verifier.publishResults='true' -Dpactbroker.host=localhost -Dpactbroker.port=8500 -Ppact-producer
```

Now you can access the pact broker to see if the tests are successful at http://localhost:8500/.

![Catalog Pact Broker](static/catalog_pactbroker.png?raw=true)

### Exiting the application

To exit the application, just press `Ctrl+C`.

It shows you something like below.

```
^CRunning command: docker stop catalog-ms-spring
[Container] [INFO] ------------------------------------------------------------------------
[Container] [INFO] BUILD SUCCESS
[Container] [INFO] ------------------------------------------------------------------------
[Container] [INFO] Total time:  10:01 min
[Container] [INFO] Finished at: 2020-04-29T12:16:34Z
[Container] [INFO] ------------------------------------------------------------------------
Closing down development environment.
```

## Conclusion

You have successfully deployed and tested the Catalog Microservice and an Elasticsearch database in local Docker Containers using Appsody.

To see the Catalog application working in a more complex microservices use case, checkout our Microservice Reference Architecture Application [here](https://github.com/ibm-garage-ref-storefront/docs).
