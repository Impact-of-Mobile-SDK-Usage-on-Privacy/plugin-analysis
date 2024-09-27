# Plotalyzer Plugin

## Build

Go into the root folder (i.e., the folder the `README.md` resides in) and run `sbt stage`.
Then copy the resulting `jar` into the pluginfolder of the `scala-plotalyzer`.

## Use

Go to the base folder of the `scala-plotalyzer` and run

```
    ./run.sh analysis <experimentId> <pathToOutputJsonFile> SDKTrafficAnalysis
```

The result json will appear at the specified (`pathToOutputJsonFile`) location.