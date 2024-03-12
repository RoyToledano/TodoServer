# TodoServer

## 1) Technologies used

* IDE: IntelliJ
* Programming language: Java 17
* Framework: Spring Boot
* Database: Postgresql, MongoDB
* Containerization: Docker

## 2) Overview

This is an HTTP server developed using Spring Boot and is a TODO app. TODO apps allow users to maintain a list of things they need to do.
It features multiple endpoints which allow clients to manage the server's todos.

This project focus on these concepts:
1. Client-server model
2. Databases
3. Containerization 

## 3) Usage
The server's default listening port is 9285.

**Server endpoints:**

### 3.1) Health

`/todo/health`

Method: **GET**

This is a sanity endpoint used to check that the server is up and running.


### 3.2) Create new TODO

`/todo`

Method: **POST**

Creates a new TODO item in the system. 

```diff
Body: json object:
{
    title: <TODO title> //string
    content: <TODO content> //string
    dueDate: <timestamp in millis> // long number
}
```

The TODO is created with the status PENDING.
When a new TODO is created, it is assigned by the server to the next id in turn.

Upon processing, 2 verifications occur:
1) Is there already a TODO with this title (TODOs are distinguished by their title)
2) dueDate is in the future. 

If the operation can be invoked (all verification went OK): the response code will be 200.
The result will hold the (newly) assigned TODO number.


### 3.3) Get TODOs count

`/todo/size`

Method: **GET**

Query Parameter:
* **status**. Value: ALL, PENDING, LATE, DONE (in capital case only).
* **persistenceMethod**. Value: POSTGRES, MONGO

Returns the total number of TODOs in the system, according to the given filter.
The persistence method dictates which of the DBs will the info come from.


### 3.4) Get TODOs data

`/todo/content`

Method: **GET**

Query Parameter: 
* **status**. Value: ALL, PENDING, LATE, DONE.

* **sortBy**. Value: ID, DUE_DATE, TITLE (Note: This is an optional query parameter. It does not have to appear.) 
* **persistenceMethod**. Value: POSTGRES, MONGO

The response will be a json array. The array will hold json objects that describe a single todo. 
Each TODO object holds:

```diff
{
	id: integer,
	title: string,
	content: string,
	status: string,
	dueDate: timestamp (ms): long,
}
```

The array will be sorted according to the sortBy parameter.

The sorting will always be ascending.

In case sortBy is not supplied, the sorting is done by ID

If no TODOs are available the result is an empty array.


### 3.5) Update TODO status

`/todo`

Method: **PUT**

Query Parameter: **id**. Number. The TODO id

Query Parameter: **status**. The status to update. It can be PENDING, LATE, or DONE

If the TODO exists (according to the id), its status gets updated.


### 3.6) Delete TODO

`/todo`

Method: **DELETE**

Query Parameter: **id**. Number. The TODO id

Deletes a todo with the given id.


## 4) Docker Containers

### 4.1) Setting up the environment
In the release section of this repo you will find a docker-compose.yml file. Download this file and place it in your sdesired path. Make sure you have the docker engine installed and running.

### 5.2) Run the server
In order to run the server you need to use the following command:
```
    docker-compose up
```

This will automatically run the server's container (listening port 3769) as well a container of MongoDB and a container of Postgresql for this server's todo Databases

### 5.3) Shutting down the server
In order to shut down the server you need to use the following command:

```
    docker-compose down
```

(You need to install docker on your machine in order to run it properly)


