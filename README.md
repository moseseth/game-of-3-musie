### Built With
- Java 16 with Maven 
- Web client written in HTML, CSS & JS [No framework]
- Socket.io based both in client and server
- A desire to see a closer look at the task :)


### Getting Started

- Inorder to setup the project , it is straight forward.
  * All you need is a java running in your machine with version 16+
- The .jar is already packaged with dependencies in the root of the repository.
- Find the terminal and run:
```commandline
  java -jar gameof3.jar
 ```
Once the socket based application is running. 
- Open the `client/` folder to access the html, js and css combo.
- (Run the index.html) you will be able to navigate your way.
- Please read Key notes.
- Perhaps, all technical details will be left for the interview

### Docker compose
```
docker-compose up -d

localhost:8080 for webclient 
localhost:9092 for ws // refer console for docker container
```

### Key notes:
- The game has two modes, one manual the other automatic
- By default it is automatic, all that is required is start the game by pressing the button
- Once that round is over, refresh the browsers in 2 separate tabs (to simulate 2 users)
- Then toggle the button on the top corner of the page.
- Once you do that start the game again, you will then be able to enter the numbers of your 
- choice

- There might be fundamental issues with frontend, not perfect!
- Nevertheless, you can watch the progress right from the terminal where the socket server is
- running, there are outputs about the move.

That is alles! 
