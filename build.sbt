import com.typesafe.sbt.SbtMultiJvm
import com.typesafe.sbt.SbtMultiJvm.MultiJvmKeys.MultiJvm
import sbt.CrossVersion

val akkaVersion = "2.5.13"

val project = Project(
  id = "crdt-recipes",
  base = file("."),
  settings = Defaults.coreDefaultSettings ++ SbtMultiJvm.multiJvmSettings ++ Seq(
    name := "crdt-recipes",
    version := "1.0.0",
    scalaVersion := "2.12.4",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % akkaVersion,
      "com.typesafe.akka" %% "akka-cluster" % akkaVersion,

      "com.typesafe.akka" %%  "akka-cluster-sharding" % akkaVersion,
      "com.typesafe.akka" %% "akka-distributed-data" % akkaVersion,

      "eu.timepit"        %% "crjdt-core" % "0.0.8-SNAPSHOT", //local build

      "com.twitter"       %% "algebird-core" % "0.13.0",
      
      "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
      "ch.qos.logback" % "logback-classic" % "1.2.3",

      "com.github.mpilquist" %% "simulacrum"   % "0.12.0",

      "org.rocksdb"          %  "rocksdbjni"   % "5.5.1",
      "io.dmitryivanov"      %% "scala-crdt"   % "1.0.1",

      //"com.rbmhtechnology" %% "eventuate-crdt" % "0.10",

      "Merlijn Boogerd" %%  "computational-crdts" % "1.0",

      "com.github.mpilquist" %%   "simulacrum" % "0.12.0",
      "com.typesafe.akka"    %%   "akka-multi-node-testkit" % akkaVersion,
      "com.lihaoyi"          %    "ammonite"     % "1.0.3" % "test" cross CrossVersion.full),
    // make sure that MultiJvm test are compiled by the default test compilation
    compile in MultiJvm <<= (compile in MultiJvm) triggeredBy (compile in Test),
    // disable parallel tests
    parallelExecution in Test := false,
    // make sure that MultiJvm tests are executed by the default test target,
    // and combine the results from ordinary test and multi-jvm tests
    executeTests in Test <<= (executeTests in Test, executeTests in MultiJvm) map {
      case (testResults, multiNodeResults)  =>
        val overall =
          if (testResults.overall.id < multiNodeResults.overall.id)
            multiNodeResults.overall
          else
            testResults.overall
          Tests.Output(overall,
            testResults.events ++ multiNodeResults.events,
            testResults.summaries ++ multiNodeResults.summaries)
    }
  )
) configs (MultiJvm)

//https://tpolecat.github.io/2017/04/25/scalac-flags.html
/*
scalacOptions ++= Seq(
  "-deprecation",                      // Emit warning and location for usages of deprecated APIs.
  "-encoding", "utf-8",                // Specify character encoding used by source files.
  "-explaintypes",                     // Explain type errors in more detail.
  "-feature",                          // Emit warning and location for usages of features that should be imported explicitly.
  "-language:existentials",            // Existential types (besides wildcard types) can be written and inferred
  "-language:experimental.macros",     // Allow macro definition (besides implementation and application)
  "-language:higherKinds",             // Allow higher-kinded types
  "-language:implicitConversions",     // Allow definition of implicit functions called views
  "-unchecked",                        // Enable additional warnings where generated code depends on assumptions.
  "-Xcheckinit",                       // Wrap field accessors to throw an exception on uninitialized access.
  "-Xfatal-warnings",                  // Fail the compilation if there are any warnings.
  "-Xfuture",                          // Turn on future language features.
  "-Xlint:adapted-args",               // Warn if an argument list is modified to match the receiver.
  "-Xlint:by-name-right-associative",  // By-name parameter of right associative operator.
  "-Xlint:constant",                   // Evaluation of a constant arithmetic expression results in an error.
  "-Xlint:delayedinit-select",         // Selecting member of DelayedInit.
  "-Xlint:inaccessible",               // Warn about inaccessible types in method signatures.
  "-Xlint:infer-any",                  // Warn when a type argument is inferred to be `Any`.
  "-Xlint:missing-interpolator",       // A string literal appears to be missing an interpolator id.
  "-Xlint:nullary-override",           // Warn when non-nullary `def f()' overrides nullary `def f'.
  "-Xlint:nullary-unit",               // Warn when nullary methods return Unit.
  "-Xlint:package-object-classes",     // Class or object defined in package object.
  "-Xlint:poly-implicit-overload",     // Parameterized overloaded implicit methods are not visible as view bounds.
  "-Ypartial-unification",             // Enable partial unification in type constructor inference
  "-Ywarn-dead-code"                  // Warn when dead code is identified.
)
*/

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

fork in run := true

//"com.rbmhtechnology" %% "eventuate-crdt"  % "0.9"
//com.rbmhtechnology:eventuate-crdt_2.12:0.9         (depends on 2.4.12)
