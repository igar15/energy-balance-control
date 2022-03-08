[![Codacy Badge](https://app.codacy.com/project/badge/Grade/a6fd0d1b442142dd849183e941d3085f)](https://www.codacy.com/gh/igar15/energy-balance-control/dashboard)
[![Build Status](https://app.travis-ci.com/igar15/energy-balance-control.svg?branch=master)](https://app.travis-ci.com/github/igar15/energy-balance-control)

Energy Balance Control project 
=================================

This is the REST API implementation of Energy Balance Control project designed to control the energy balance of the human body.  
The project is cloud-native microservice-based application.

### Technology stack used: 
* Maven
* Spring Cloud
* Spring Boot 2
* Spring MVC
* Spring Data JPA (Hibernate)
* Spring Security
* REST (Jackson)
* Rabbit MQ
* Zipkin
* ELK Stack (Elasticksearch + Logstash + Kibana)
* JSON Web Token
* JUnit 5

### Project key logic:
* System main purpose: Calculation of the energy balance of the human body based on meals consumed, exercise performed
 and the cost of basic metabolism.
* There are 2 types of users: admin and user.
* Admins main task is user management. They can create, update and delete users. Also they can change users passwords.
* Users can register on the app. During the registration process, users provide their physiological data 
(sex, height, weight, age) necessary to calculate the cost of basic metabolism.  
After registration, the user must activate his account by clicking on the link in the received email.
* Users can create, update and delete consumption data for their meals. They can also create, update and delete data on physical 
exercises performed. Based on these data, as well as their physiological data, the energy balance is calculated.
* Every user has access to their profile data and can also change their password. If the user has forgotten his password, 
he can reset it using his email.