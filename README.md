# Project Title

## Table of Contents
+ [Problem](#problem)
+ [Considerations / Assumptions](#considerations-/-assumptions)
+ [Solution](#solution)
+ [Service Flow](#service-flow)
+ [API](#api)
+ [Running locally](#running-in-a-local-machine)
+ [Running tests](#running-tests)
+ [Configuration](#configuration)
+ [Design Choices](#design-choices)
+ [Components](#components)

## Problem 
Create a service that accepts the necessary information and sends emails.
The application should provide an abstraction between two different email service providers. If one of the services goes down, your service can quickly failover to a different provider without affecting your customers.
The solution should be implemented as one or more RESTful API calls

## Considerations / Assumptions
* No authentication is required for the scope of this exercise
* No 3rd party client library should be used to integrate with service providers

## Solution
Using a failover mechanism, abstract several different service providers from the user of the service.

The service is implemented using an asynchronous mvc pattern.
Upon a api request to send an email
* If there are no invalid fields in the request the service would respond with a `202 Accepted` code.
* If there is an invalid field (i.e. invalid formatting of an email or not supplying required information)
 the service would response with  a `400 Bad Request`
 
see [API](#api) for more details
 
The failover mechanism is implemented by the `FailoverEmailRouter` class which handles all service providers
and failover's if an exception is thrown and put into an `Exchange`. The `FailoverEmailRouter` would 
process an `Exchange` via an `ExecutorService` with a fixed thread pool of `10` which
creates a new thread upon recieving a process request. For more info see [FailoverEmailRouter](#failoveremailrouter)

## Service flow
* The flow of a request is as follows:
* The user requests to send email via `POST /email/send/`
* The request handled and routed to the application by the `EmailController`
* The `EmailController` validates the requests (e.g proper email format and required fields)
* The `EmailController` sends the request to the `EmailService` and responsds with an accepted http status 
* The `EmailService` creates a new `EmailExchange` object that implements `Exchange` interface to encapsulate the request
* The `EmailService` then sends the request for processing thru the `FailoverEmailRouter`
* The `FailoverEmailRouter` was created via the `EmailServiceConfig` and has been injected with providers implementing the `BaseEmailServiceProvider`
* The `FailoverEmailRouter` would create an inner `State` and submit the request to an `ExecutorService`
* The `State` would handle checking for continous failover or maxAttempts
* The `State` would then retrieve an `EmailServiceProvider` from the last known good index and call it's process function via the abstract class `BaseEmailServiceProvider`
* The `BaseEmailServiceProvider` would handle the preparation of headers and body for sending the message via a `RestTemplate`
* The `Exchange`'s exception would be set if there is an error from sending the message 


## API
The service is deployed on port `5000`
### email
`POST /email/send/`  

Sends an email an email service provider

### Headers
- **content-type** - should be `application/json` the accepted content type

### Body

** Request **

| Field        | Type           | Description  | Required?  |
| ------------- |-------------|-----|-----|
| subject      | String | the subject of the email |   yes |
| content      | String | content to send text |   yes |
| sender|  EmailUser         | a sender name and email  | yes  |
| recipient|  Array[EmailUser]           | a recipient with a name and email  | yes  |
| cc | Array[EmailUser]      |    a list of name and emails to cc |   no |
| bb | Array[EmailUser]      |    a list of name and emails to bcc |   no |


**EmailUser Object** 

| Field        | Type           | Description  | Required?  |
| ------------- |-------------|-----|-----|
| sender.name    | String      |   a sender name |   yes |
| sender.email      | String      |  a sender emil  |   yes |

### Responses
- **202 Accepted** - Accepted by the service passed all validations
- **400 Bad Request** - A field is incorrect or a required field is not supplied. If the request is incorrect, an array of errors will be returned.
- **500 Internal Server Error** - Service is inaccessible


#### Sample Request
```
curl -X POST \
  http://localhost:5000/email/send \
  -H 'content-type: application/json' \
  -d '{
   "subject":"subject",
   "content":"content",
   "sender":{
      "name":"name",
      "email":"test@mail.com"
   },
   "recipient":[
      {
         "name":"name",
         "email":"test@mail.com"
      }
   ],
   "cc":[
      {
         "name":"name",
         "email":"test@mail.com"
      }
   ],
   "bcc":[
      {
         "name":"name",
         "email":"test@mail.com"
      }
   ]
}'
```

#### Sample Response
**Code** : `202 Accepted`


## Running in a local machine
1. Clone or download the code.
2. open and edit `/src/main/resources/application.yml` for the api keys and urls
3. using a terminal open the project root and start the app 
```bash
$ cd siteminder-email-service
$ mvn clean spring-boot:run
```

## Running tests
using a terminal open the project root and run using maven test runner
```bash
$ mvn test
```

## Configuration
Available configuration is specified on a .yml file at `/src/main/resources/application.yml`
You can add additional providers via extending the `BaseEmailServiceProvider` and adding the appropriate 
provider config via `providersConfig` field.

**Sample yml configuration** 
```
email-service:
  providersConfig:
    mailgun:
      name: mailgun
      url: https://api.mailgun.net/v3
      apiKey: mailgun.apikey
    sendgrid:
      name: sendgrid
      url: https://api.sendgrid.com/v3/send
      apiKey: sendgrid.apikey
```

### Additional configuration
#### Disabling a provider
You can set a provider to be disabled by setting an `enabled : false` field
for a provider.
e.g.
```
email-service:
  providersConfig:
    mailgun:
      name: mailgun
      url: https://api.mailgun.net/v3
      apiKey: mailgun.apikey
      enabled: false
```

#### Max Attempts
Set to more than 0 to indicate failover attempts before we should exhaust (give up).  
Use -1 to indicate never give up and continuously try to failover until last provider.  
Use 0 to never failover.  

Default: -1  

Sample
```
email-service:
  maxAttempts: 1
```

 
#### Sticky mode 
If sticky is enabled, then it keeps state and will continue with the last known good endpoint.  
If not, then it will always start from the first endpoint when a new message is to be processed. In other words it restart from the top for every message. 

Default : true  

Sample
```
email-service:
  sticky: false
```

## Design Choices
### Spring boot + Spring web
  For easier app development and setup. 
  Other choice is the javaee javax webservice for a much simpler
  rest service creation.
  
### Failover pattern
  The `FailoverEmailRouter` uses a Chain of responsibility pattern for provider failover but using an
  inside state instead of managing it's own 'next' provider. This way I could add more email service
  providers by simple extending the `BaseEmailServiceProvider` and spring's autowiring would take care
  of adding that to available providers via the config `EmailServiceConfig`.
  
### Exchange pattern
  Instead of returning or using the main pojo, i've used an exchange pattern that encapsulates
  the message input, output and exception and other data related to an exchange. This allows for asynchronously
  updating the exchange without side effects to the Email object.

## Components

### FailoverEmailRouter
The fail over email router implements the failover mechanism by managing a new `State` upon construction.
This state object would:
 * handle conditionals if the failover should happen or end.
 * handle the last good index if the property `sticky` is set to `true` (defaults to true)
 * handle max attempts for each failover if set
 * handle calling the appropriate service provider (classes that implement the `EmailServiceProvider` interface) 