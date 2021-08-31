###### catalog-ms-spring

# Running Catalog App on CP4Apps

*This project is part of the 'IBM Cloud Native Reference Architecture' suite, available at
https://github.com/ibm-garage-ref-storefront/refarch-cloudnative-storefront*

## Table of Contents

* [Introduction](#introduction)
    + [APIs](#apis)
* [Pre-requisites](#pre-requisites)
* [Catalog application on CP4Apps](#catalog-application-on-cp4apps)
    + [Get the Catalog application](#get-the-catalog-application)
    + [Application manifest](#application-manifest)
    + [Project Setup](#project-setup)
    + [Deploy Elasticsearch to Openshift](#deploy-elasticsearch-to-openshift)
    + [Deploy the app using Kabanero Pipelines](#deploy-the-app-using-kabanero-pipelines)
      * [Access tekton dashboard](#access-tekton-dashboard)
      * [Create registry secrets](#create-registry-secrets)
      * [Create Webhook for the app repo](#create-webhook-for-the-app-repo)
      * [Deploy the app](#deploy-the-app)
* [Conclusion](#conclusion)

## Introduction

This project will demonstrate how to deploy a Spring Boot Application with an Elasticsearch database onto a Kubernetes Cluster. It is dependent on [Inventory](https://github.com/ibm-garage-ref-storefront/inventory-ms-spring) Microservice. To know more about Inventory microservices, check [this](https://github.com/ibm-garage-ref-storefront/inventory-ms-spring) out.

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

* [RedHat Openshift Cluster](https://cloud.ibm.com/kubernetes/catalog/openshiftcluster)

* IBM Cloud Pak for Applications
  + [Using IBM Console](https://cloud.ibm.com/catalog/content/ibm-cp-applications)
  + [OCP4 CLI installer](https://www.ibm.com/support/knowledgecenter/en/SSCSJL_4.1.x/install-icpa-cli.html)

* Docker Desktop
  + [Docker for Mac](https://docs.docker.com/docker-for-mac/)
  + [Docker for Windows](https://docs.docker.com/docker-for-windows/)

* Command line (CLI) tools
  + [oc](https://www.okd.io/download.html)
  + [git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git)
  + [appsody](https://appsody.dev/docs/getting-started/installation)

* Deploy Inventory Microservice - Refer the instructions [here](https://github.com/ibm-garage-ref-storefront/inventory-ms-spring/blob/master/docs/CP4APPS.md). Catalog microservice is dependent of the Inventory microservice. It serves as cache for the Inventory.

## Catalog application on CP4Apps

### Get the Catalog application

- Clone catalog repository:

```bash
git clone https://github.com/ibm-garage-ref-storefront/catalog-ms-spring.git
cd catalog-ms-spring
```

### Application manifest

When you see the project structure, you should be able to find an `app-deploy.yaml`. This is generated as follows.

```
appsody deploy --generate-only
```

This generates a default `app-deploy.yaml` and on top of this we added necessary configurations that are required by the Catalog application.

### Project Setup

- Create a new project if it does not exist. Or if you have an existing project, skip this step.

```
oc new-project storefront
```

- Once the namespace is created, we need to add it as a target namespace to Kabanero.

Verify if kabanero is present as follows.

```
$ oc get kabaneros -n kabanero
NAME       AGE   VERSION   READY
kabanero   9d    0.6.1     True
```

- Edit the yaml file configuring kabanero as follows.

```
$ oc edit kabanero kabanero -n kabanero
```

- Finally, navigate to the spec label within the file and add the following targetNamespaces label.

```
spec:
  targetNamespaces:
    - storefront
```

### Deploy Elasticsearch to Openshift

- Add security constraints as follows.

```
oc adm policy add-scc-to-user privileged system:serviceaccount:storefront:default
```

- Now deploy the elasticsearch as follows.

```
oc apply --recursive --filename ElasticSearch/
```

- Verify if the pods are up and running.

```
$ oc get pods
NAME                                   READY   STATUS    RESTARTS   AGE
elasticsearch-7b49df8497-9pbt5         1/1     Running   0          34m
inventory-ms-spring-5bd8dcb784-b5l2c   1/1     Running   0          3d18h
inventory-mysql-8667979689-6qfkz       1/1     Running   0          10d
```

### Deploy the app using Kabanero Pipelines

#### Access tekton dashboard

- Open IBM Cloud Pak for Applications and click on `Instance` section. Then select `Manage Pipelines`.

![CP4Apps](static/cp4apps_pipeline.png?raw=true)

- This will open up the Tekton dashboard.

![Tekton dashboard](static/tekton.png?raw=true)

#### Create registry secrets

- To create a secret, in the menu select `Secrets` > `Create` as below.

![Secret](static/secret.png?raw=true)

Provide the below information.

```
Name - <Name for secret>
Namespace - <Your pipeline namespace>
Access To - Docker registry>
username - <registry user name>
Password/Token - <registry password or token>
Service account - kabanero-pipeline
Server Url - Keep the default one
```

- You will see a secret like this once created.

![Docker registry secret](static/docker_registry_secret.png?raw=true)

#### Create Webhook for the app repo

- For the Github repo, create the webhook as follows. To create a webhook, in the menu select `Webhooks` > `Create webhook`

We will have below

![Webhook](static/webhook.png?raw=true)

Provide the below information.

```
Name - <Name for webhook>
Repository URL - <Your github repository URL>
Access Token - <For this, you need to create a Github access token with permission `admin:repo_hook` or select one from the list>
```

To know more about how to create a personal access token, refer [this](https://help.github.com/en/articles/creating-a-personal-access-token-for-the-command-line).

- Now, enter the pipeline details.

![Pipeline Info](static/pipeline_info.png?raw=true)

- Once you click create, the webhook will be generated.

![Catalog Webhook](static/webhook_catalog.png?raw=true)

- You can also see in the app repo as follows.

![Catalog Repo Webhook](static/webhook_catalog_repo.png?raw=true)

#### Deploy the app

Whenever we make changes to the repo, a pipeline run will be triggered and the app will be deployed to the openshift cluster.

- To verify if it is deployed, run below command.

```
oc get pods
```

If it is successful, you will see something like below.

```
$ oc get pods
NAME                                   READY   STATUS    RESTARTS   AGE
catalog-ms-spring-55bb5fccdd-dtcgb     1/1     Running   0          11m
elasticsearch-7b49df8497-9pbt5         1/1     Running   0          34m
inventory-ms-spring-5bd8dcb784-b5l2c   1/1     Running   0          3d18h
inventory-mysql-8667979689-6qfkz       1/1     Running   0          10d
```

- You can access the app as below.

```
oc get route
```

This will return you something like below.

```
$ oc get route
NAME                  HOST/PORT                                                                                                                      PATH   SERVICES              PORT       TERMINATION   WILDCARD
catalog-ms-spring     catalog-ms-spring-storefront.csantana-demos-ocp43-fa9ee67c9ab6a7791435450358e564cc-0000.us-east.containers.appdomain.cloud            catalog-ms-spring     8080-tcp                 None
inventory-ms-spring   inventory-ms-spring-storefront.csantana-demos-ocp43-fa9ee67c9ab6a7791435450358e564cc-0000.us-east.containers.appdomain.cloud          inventory-ms-spring   8080-tcp                 None
```

- Grab the route and hit `/micro/items` which returns you a list of items.

For instance it will be http://catalog-ms-spring-storefront.csantana-demos-ocp43-fa9ee67c9ab6a7791435450358e564cc-0000.us-east.containers.appdomain.cloud/micro/items

## Conclusion

You have successfully deployed and tested the Catalog Microservice and an Elasticsearch database on Openshift using IBM Cloud Paks for Apps.

To see the Catalog application working in a more complex microservices use case, checkout our Microservice Reference Architecture Application [here](https://github.com/ibm-garage-ref-storefront/refarch-cloudnative-storefront).
