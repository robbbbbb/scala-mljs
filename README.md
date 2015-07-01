# scala-mljs
An implementation of some machine learning algorithms in scala that transpiles with scala-js in order to do visualisation using the C3 library

## Why?

I wanted to implement some ML algorithms and immediately you need to visualise stuff. Rather than write some crufty Java FX code I wanted to use one of the many good JS visualisation libraries that exist now and that then gave me a good opportunity to have a play with scala-js too.

You can see it running here: http://robbbbbb.github.io/scala-mljs/

The project is fully functional once downloaded, just navigate to the index.html page.

The transpiled javascript is already included and loaded by the html. Make changes to the scala and re-compile with

    sbt fastOpt

Then reload the html to see the results of the updated javascript.

## Random Notes

When trying to get scala-js working with IntelliJ you may get bitten by the fact that at ToW the IntelliJ sbt auto-importer uses an out of date sbt-launcher jar. This causes it to fail on the enablePlugins line in build.sbt. To fix this you can select a more up to date launcher in the 'Import Project from SBT' dialog you get when opening a project in IntelliJ: Global SBT Settings > Launcher > Custom and select a sbt jar > 0.13.8 (hint: they're probably in ~/.sbt/launchers if you use the sbt-extras script from github).

A random bit of functionality in scala-js which is under-documented is the JSArray class which allows you to pass and work with javascript arrays in scala. This instantly makes interop between the two languages radically easier. Take a look at the KMeansJSInterface class to see use of it.
