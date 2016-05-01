#Circles Benchmark
##MASON Guide

The results provided within were obtained using MASON v19.

In order to execute these benchmarks you require:
* JDK installed, and it's bin directory on your path environment variable
* MASON downloaded, and it's .jar('s) located within your classpath
* If wishing to run the graphical model, JAVA3D will also need to be setup

MASON was slightly modified to support timing the runtime of the execution, independent of the initialisation. The class these changes were made to is `sim.engine.SimState`. If your class path is configured correctly, and you compile this modified class (as found within the model directory), it should automatically override the one present within MASON's .jar at runtime. If this fails you should be able to replace the SimState.class within the jar with the externally compiled version.