import sbt._
import Keys._
import play.routes.compiler.InjectedRoutesGenerator
import play.sbt.routes.RoutesKeys.routesGenerator
import play.twirl.sbt.Import.TwirlKeys

val appName    = "play2-auth"

val playVersion = play.core.PlayVersion.current

val Scala211 = "2.11.12"
val Scala212 = "2.12.8"

lazy val baseSettings = Seq(
  version            := "0.16.0-SNAPSHOT",
  scalaVersion       := Scala211,
  crossScalaVersions := Seq(Scala211, Scala212),
  organization       := "jp.t2v",
  resolvers          ++= Seq(
    Resolver.typesafeRepo("releases"),
    Resolver.sonatypeRepo("releases"),
    "Opt-Technologies Snapshots" at "https://raw.githubusercontent.com/opt-tech/maven-repository/master/snapshots"
  ),
  scalacOptions      ++= Seq("-language:_", "-deprecation")
)

lazy val appPublishMavenStyle = true
lazy val appPublishArtifactInTest = false
lazy val appPomIncludeRepository = { _: MavenRepository => false }
lazy val appPomExtra = {
  <url>https://github.com/t2v/play2-auth</url>
    <licenses>
      <license>
        <name>Apache License, Version 2.0</name>
        <url>http://www.apache.org/licenses/LICENSE-2.0.html</url>
        <distribution>repo</distribution>
      </license>
    </licenses>
    <scm>
      <url>git@github.com:t2v/play2-auth.git</url>
      <connection>scm:git:git@github.com:t2v/play2-auth.git</connection>
    </scm>
    <developers>
      <developer>
        <id>gakuzzzz</id>
        <name>gakuzzzz</name>
        <url>https://github.com/gakuzzzz</url>
      </developer>
    </developers>
}


lazy val core = project.in(file("module"))
  .settings(
    baseSettings,
    libraryDependencies += "com.typesafe.play"  %%   "play"                   % playVersion        % "provided",
    libraryDependencies += "com.typesafe.play"  %%   "play-cache"             % playVersion        % "provided",
    libraryDependencies += "jp.t2v"             %%   "stackable-controller"   % "0.7.0-SNAPSHOT",
    name                    := appName,
    publishMavenStyle       := appPublishMavenStyle,
    publishArtifact in Test := appPublishArtifactInTest,
    pomIncludeRepository    := appPomIncludeRepository,
    publishTo               := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    },
    pomExtra                := appPomExtra
  )

lazy val test = project.in(file("test"))
  .settings(
    baseSettings,
    libraryDependencies += "com.typesafe.play"  %% "play-test"   % playVersion,
    name                    := appName + "-test",
    publishMavenStyle       := appPublishMavenStyle,
    publishArtifact in Test := appPublishArtifactInTest,
    pomIncludeRepository    := appPomIncludeRepository,
    publishTo               := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    },
    pomExtra                := appPomExtra
  ).dependsOn(core)

lazy val sample = project.in(file("sample"))
  .enablePlugins(play.sbt.PlayScala)
  .settings(
    baseSettings,
    resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
    libraryDependencies += play.sbt.Play.autoImport.guice,
    libraryDependencies += play.sbt.Play.autoImport.cache,
    libraryDependencies += play.sbt.Play.autoImport.specs2 % Test,
    libraryDependencies += play.sbt.Play.autoImport.jdbc,
    libraryDependencies += "com.h2database" % "h2" % "1.4.193",
    libraryDependencies += "org.mindrot"           % "jbcrypt"                           % "0.3m",
    libraryDependencies += "org.scalikejdbc"      %% "scalikejdbc"                       % "3.2.0",
    libraryDependencies += "org.scalikejdbc"      %% "scalikejdbc-config"                % "3.2.0",
    libraryDependencies += "org.scalikejdbc"      %% "scalikejdbc-syntax-support-macro"  % "3.2.0",
    libraryDependencies += "org.scalikejdbc"      %% "scalikejdbc-test"                  % "3.2.0"   % "test",
    libraryDependencies += "org.scalikejdbc"      %% "scalikejdbc-play-initializer"      % "2.6.0-scalikejdbc-3.2",
    libraryDependencies += "org.scalikejdbc"      %% "scalikejdbc-play-dbapi-adapter"    % "2.6.0-scalikejdbc-3.2",
    libraryDependencies += "org.scalikejdbc"      %% "scalikejdbc-play-fixture"          % "2.6.0-scalikejdbc-3.2",
    libraryDependencies += "org.flywaydb"         %% "flyway-play"                       % "5.0.0",
    TwirlKeys.templateImports in Compile ++= Seq(
      "jp.t2v.lab.play2.auth.sample._",
      "play.api.data.Form",
      "play.api.mvc.Flash",
      "views._",
      "views.html.helper",
      "controllers._"
    ),
    publish           := { },
    publishArtifact   := false,
    packagedArtifacts := Map.empty,
    publishTo         := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    },
    pomExtra          := appPomExtra,
    routesGenerator   := InjectedRoutesGenerator
  )
  .dependsOn(core, test % "test")

lazy val social = Project (id = "social", base = file ("social"))
  .settings(
    baseSettings,
    name                := appName + "-social",
    libraryDependencies += "com.typesafe.play" %% "play"       % playVersion % "provided",
    libraryDependencies += "com.typesafe.play" %% "play-ws"    % playVersion % "provided",
    libraryDependencies += "com.typesafe.play" %% "play-cache" % playVersion % "provided",
    libraryDependencies += "com.typesafe.play" %% "play-ahc-ws-standalone" % "1.1.9",
    publishMavenStyle       := appPublishMavenStyle,
    publishArtifact in Test := appPublishArtifactInTest,
    pomIncludeRepository    := appPomIncludeRepository,
    publishTo               := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    },
    pomExtra                := appPomExtra
  ).dependsOn(core)

lazy val socialSample = Project("social-sample", file("social-sample"))
  .enablePlugins(play.sbt.PlayScala)
  .settings(
    baseSettings,
    name                := appName + "-social-sample",
    resourceDirectories in Test += baseDirectory.value / "conf",
    resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play-ws"                           % playVersion,
      "com.typesafe.play" %% "play-cache"                        % playVersion,
      "org.flywaydb"      %% "flyway-play"                       % "5.0.0",
      "org.scalikejdbc"   %% "scalikejdbc"                       % "3.2.0",
      "org.scalikejdbc"   %% "scalikejdbc-config"                % "3.2.0",
      "org.scalikejdbc"   %% "scalikejdbc-syntax-support-macro"  % "3.2.0",
      "org.scalikejdbc"   %% "scalikejdbc-test"                  % "3.2.0"   % "test",
      "org.scalikejdbc"   %% "scalikejdbc-play-initializer"      % "2.6.0-scalikejdbc-3.2",
      "org.scalikejdbc"   %% "scalikejdbc-play-dbapi-adapter"    % "2.6.0-scalikejdbc-3.2",
      "org.scalikejdbc"   %% "scalikejdbc-play-fixture"          % "2.6.0-scalikejdbc-3.2"
    ),
    publish           := { },
    publishArtifact   := false,
    packagedArtifacts := Map.empty,
    publishTo         := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    },
    pomExtra          := appPomExtra
  )
  .dependsOn(core, social)

lazy val root = project.in(file("."))
  .settings(baseSettings)
  .settings(
    publish           := { },
    publishArtifact   := false,
    packagedArtifacts := Map.empty,
    publishTo         := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    },
    pomExtra          := appPomExtra
  ).aggregate(core, test, sample, social, socialSample)
