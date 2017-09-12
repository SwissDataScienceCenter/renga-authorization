# renga-authorization
Renga Resource Authorization Service

Documentation: https://renga.readthedocs.io/en/latest/developer/resource_manager_service.html

## Development
Building is done using [sbt](http://www.scala-sbt.org/).

To create a docker image:
```bash
$ sbt docker:publishLocal
[...]
[info] Successfully tagged renga-authorization:<version>
[info] Built image renga-authorization:<version>
```

Image name and tag can be manipulated with sbt settings, see
[sbt-native-packager](https://sbt-native-packager.readthedocs.io/en/v1.2.2/formats/docker.html).
