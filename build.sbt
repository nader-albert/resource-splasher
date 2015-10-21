name := "splasher"

version := "1.0"

scalaVersion := "2.11.7"

val akkaV = "2.4.0"
val sprayV = "1.2.0"

resolvers += "typesafe" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Bintray sbt plugin releases" at "http://dl.bintray.com/sbt/sbt-plugin-releases/"

libraryDependencies ++= {
  Seq(
    //"org.java-websocket"  %   "Java-WebSocket" % "1.3.1",
    "io.spray" %% "spray-json" % "1.3.2",
    "io.spray" %% "spray-can" % "1.3.3",
    "io.spray" %% "spray-routing" % "1.3.3",
    // "io.reactivex"        %%  "rxscala"        % "0.24.1",
    // "com.wandoulabs.akka" %%  "spray-websocket" % "0.1.3",
    "org.reactivemongo" %% "reactivemongo" % "0.11.7",
    "com.typesafe.akka"   %%  "akka-actor"     % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"   % akkaV,
    //"io.spray"          %%  "spray-testkit"  % sprayV  % "test",
    "org.scalatest" %% "scalatest" % "2.2.4" % "test" //,
    //"junit"               %%  "junit"          % "4.11"  % "test"//,
    //"org.specs2"        %%  "specs2"         % "2.2.3" % "test"
    //"com.imaginea" %% "socket.io.play" % "0.0.3-SNAPSHOT",
    //"com.wandoulabs.akka" %% "spray-socketio" % "0.1.3-SNAPSHOT"
  )
}