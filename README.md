# scala-mljs
An implementation of some machine learning algorithms in scala that transpiles with scala-js in order to do visualisation using the C3 library

## Random Notes

When trying to get scala-js working with IntelliJ you may get bitten by the fact that at ToW the IntelliJ sbt auto-importer uses an out of date sbt-launcher jar. This causes it to fail on the enablePlugins line in build.sbt. To fix this you can select a more up to date launcher in the 'Import Project from SBT' dialog you get when opening a project in IntelliJ: Global SBT Settings > Launcher > Custom and select a sbt jar > 0.13.8 (hint: they're probably in ~/.sbt/launchers if you use the sbt-extras script from github).