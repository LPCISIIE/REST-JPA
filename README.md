# RESTful Web Services with JAX-RS (JavaEE)
<p align="center"><img style="margin-bottom:3em;" width="150"src="http://www.monitis.com/blog/wp-content/uploads/uploads/2012/03/jboss_logo2.jpg"> 
</p>  <br>

## About
This is an example of a RESTful API written in Java, using Jax-RS and JPA.
This application allows you to manage an online fast-food store.
Therefore, you can create, edit and remove : sandwiches, customers, orders, receipts, ingredients and categories. </br>
This application follows the Entity-Control-Boundary pattern. <br>
Documentation available at the index page : http://your-project.org/

## Warning
In order to create PDF files, the application will create a folder inside of your JBoss Server (such as Wildfly) make sure to have the access right to write files, else you can change the path in ``` src/main/java/control/ReceiptGenerator.java ``` at line 30.

## Features
- Account
- CORS Filter
- Digest access authentication
- Documentation (Powered by Swagger.io)
- JToken
- Password Hashing
- PDF Generator
- Role Accounts

## Authors

Xavier CHOPIN, Corentin LABROCHE, David LEBRUN and Alexis WURTH


## License

This application is open-sourced software licensed under the MIT license.
