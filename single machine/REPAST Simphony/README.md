#Circles Benchmark
##REPAST Simphony Guide

The results provided within were obtained using Repast Simphony version 2.3.1

In order to execute these benchmarks you require an installation of Repast Simphony and JDK (Repast Simphony is built over Eclipse, which *may* install JDK for you).

The file `repast-circles.jar` located within the model directory is a fat jar containing all the classes necessary to execute batch runs of the repast model.

Note: Since the Repast model was updated to utilise multi-threading the visualisation stopped working.

Note: When the grid was updated in parallel it would break causing null pointer errors when later requesting values from the grid. Therefore it is updated in serial in a seperate step to the main agent processing which is carried out in parallel.

It was found that this model executes 2-3x slower than an identical model implemented in Mason. Early microbenchmarking suggested that MASON was able to update it's data structure significantly faster than Repast. As both frameworks require this operation to be executed in serial, it is expected that this may be the reason for the performance disparity between the frameworks in many cases.

------

A secondary adder `TextFileAdder` can be used within `ParticleBuilder` to instead load particles according to a static starting configuration. This has been used in combination with the logging of agents final positions to validate the model implementations and to standardise the benchmark initialisation.