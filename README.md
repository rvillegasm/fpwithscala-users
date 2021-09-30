# fpwithscala-users

fpwithscala-users is a Scala web back-end for dealing with user creation, deletion, modification and querying by leveraging the capabilities of http4s, doobie, cats and more libraries.

## Setup

Clone the repository locally from [Github](https://github.com/rvillegasm/fpwithscala-users) through your preferred method or simply by using:

```bash
git clone https://github.com/rvillegasm/fpwithscala-users.git
```

Make sure you have [***Scala 2.13.x***](https://www.scala-lang.org/download/scala2.html) installed and a compatible version of [***Sbt***](https://www.scala-sbt.org/download.html) to run the project, as well as [***Docker***](https://docs.docker.com/get-docker/) and [***Docker Compose***](https://docs.docker.com/compose/install/) (in order to run the database).

**Note:** Other versions of scala have not been tested. They may work... or not.


## Usage

Get inside the cloned directory by issuing:
```bash
cd fpwithscala-users
```
Then start the PostgreSQL database using docker:
```bash
docker-compose up -d
```
Finally, start the server simply by running:
```bash
sbt run
```

From then on, you can start making Http requests to the app on the address *http://localhost:8000*, by abiding to the following set of requirementes for each Http method:

### POST
The POST method expects a request to the address ***/users*** with a request body consisting of a JSON containing the information about the user to be created, following the structure:
```javascript
{
  "legalId": String,
  "firstName": String,
  "lastName": String,
  "email": String,
  "phone": String
}
```

It returns the information about the newly created user as follows:
```javascript
{
  "id": Number,
  "legalId": String,
  "firstName": String,
  "lastName": String,
  "email": String,
  "phone": String
}
```

Request format example:
```bash
curl -X POST -d '{"legalId":"104", "firstName":"John", "lastName":"Doe", "email": "j@d.com", "phone":"123"}' http://localhost:8000/users
```

### GET
The GET method expects a request to the address ***/users/{legalId}***, where **legalId** stands for the legal identification of the user that is being queried.

It returns the complete information about said user as a JSON, following the structure:
```javascript
{
  "id": Number,
  "legalId": String,
  "firstName": String,
  "lastName": String,
  "email": String,
  "phone": String
}
```

Request format example:
```bash
curl http://localhost:8000/users/101
```

### PUT
The PUT method expects a request to the address ***/users*** with a request body consisting of a JSON containing the information about the user to be modified, following the structure:
```javascript
{
  "legalId": String,
  "firstName": String,
  "lastName": String,
  "email": String,
  "phone": String
}
```

It returns the information about the already modified user as follows:
```javascript
{
  "id": Number,
  "legalId": String,
  "firstName": String,
  "lastName": String,
  "email": String,
  "phone": String
}
```

Request format example:
```bash
curl -X PUT -d '{"legalId":"104", "firstName":"Johnny", "lastName":"Doelington", "email": "j@d.com", "phone":"123"}' http://localhost:8000/users
```

### DELETE
The DELETE method expects a request to the address ***/users/{legalId}***, where **legalId** stands for the legal identification of the user to be deleted.

It returns a message confirming that said user was indeed deleted.

Request format example:
```bash
curl -X DELETE http://localhost:8000/users/104
```

## Credits
Developed by:
- [Daniel Otero](https://github.com/danoteroS4N)
- [Camilo Del Valle](https://github.com/delvallecamilo)
- [Juan David Gómez](https://github.com/juangomez9619)
- [Rafael Villegas](https://github.com/rvillegasm)

## Acknowledgements
This would not have been possible without our teachers Carlos Mario Zuluaga and Juan Francisco Cardona. Also, shout-out to Lina Murcia, Daniel Benavides and everyone at S4N/EPAM Latam!.
