Un-Delegate Run Actions for IDEA
================================

This is a small plugin for IDEA that allows to only delegate build actions
to Gradle, but not run actions. It's a proof of concept, I'm hoping to get
this functionality integrated into IDEA.

Known Issues
------------

- Class reloading doesn't work. Class reloading actually never works when
  delegating build/run actions to Gradle, so this isn't a new issue. But
  the plugin unfortunately doesn't fix it.
  
Build
-----

```
./gradlew build
```

Install
-------

*Install plugin from disk*, then choose *idea-plugin/target/distributions/undelegate-run-idea-plugin.zip*.


Usage
-----

The plugin adds a settings panel in *Build, Exection, Deployment / Build Tools / Un-Delegate Run Actions*.


License
-------

[WTFPL](http://www.wtfpl.net/)
