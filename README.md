## Features:

* ReST API's
   - ReST API providing two endpoints serving GET / POST HTTP Requests
* Integration with GCM
* Persisting Notifications
* Scheduling Notifications' Submission to GCM
* Authentication / Security

## Getting Started:
To send a notification messsage to a mobile client app, Android or Ios, a notification message has to be posted in JSON format, as the body of an HTTP/1.1 request.

* **Request URL** 
http://host:port/notification/

* **Supported HTTP Request Verbs**
   * **GET** : retrieve all or a particular notficiation
      * **GET/notification** : Retrieves All Notifications
      * **Response Format** : JSON
      * **Response Structure**
      
      ```javascript 
      {
         notifications: [2]
            0:  {
                  title: "Semi Final Result"
                  body: "Argentina beats Brazil 2 - 0! :) "
                  topic: {
                     name: "World Cup 2014"
                  }
               }
               
            1:  {
                  title: "Final Result"
                  body: "Brazil Beats Germany 3 -1 "
                  topic: {
                     name: "World Cup 2018"
                  }
               }
      }
      ```
      
      * **GET/notification?topic=_topic-name_** : Retrieves only notifications for a particular topic
      
      ```javascript 
      {
         notifications: [1]
            0:  {
                  title: "Final Result
                  body: "Brazil Beats Germany 3 -1 :) "
                  topic: {
                     name: "World Cup 2018"
                  }
               }
      }
      ```
    * **POST**: submit a new notification
      * **Request URL**
         * POST/notification
      * **Request Header**
         * Content-Type=_application/json_
      * **Request Body**
         * The body of the http request submitted to the Notification Server, embraces the Notification Message in JSON format 
         * Notification Message Structure:
         
         ```javascript
           {
           "title": "great match !",
           "body": "Argnetina - Brazil, match today night !",
           "topic": {"name": "World Cup 2014"}
           }
         ```