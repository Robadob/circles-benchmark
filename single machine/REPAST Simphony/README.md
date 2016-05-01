#Circles Benchmark
##REPAST Simphony Guide

The results provided within were obtained using Repast Simphony version 2.3.1

In order to execute these benchmarks you require an installation of Repast Simphony and JDK (Repast Simphony is built over Eclipse, which *may* install JDK for you).

The file `repast-circles.jar` located within the model directory is a fat jar containing all the classes necessary to execute batch runs of the repast model.

Note: Since the Repast model was updated to utilise multi-threading the visualisation stopped working, System.out has been used to validate that results are within range.

Note: When the grid was updated in parallel it would break causing null pointer errors when later requesting values from the grid. Therefore it is updated in serial.

