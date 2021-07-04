# The Divider Game
 The game is implemented with Spring Boot.
 The users (browsers) communicate using WebSocket-style messaging with STOMP as an application level WebSocket sub-protocol.
 
 ## How to run 
 * Download the repo
 * Build with ***./gradlew build***
 * Run the following command in a terminal window (in the complete) directory:
 ***./gradlew bootRun***
 
 ## How to play
 * The application runs on http://localhost:9008/
 * To play as single player, check the "Single player" checkbox and press "Start"
 * To play with another player, press "Start" and wait until someone joins the game. If there are already two players in the game, a new game will be created.
 