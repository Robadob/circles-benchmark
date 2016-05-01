#Circles Benchmark
##FLAMEGPU Guide

The results provided within were obtained using [this](https://github.com/FLAMEGPU/FLAMEGPU/tree/c2b8f7d16c2b03038a9a66f0c391db55c0f39560) FLAMEGPU state (it's a specific commit on master, currently has no formal version number).

In order to execute these benchmarks you require a CUDA capable GPU. It's also likely that you will require the CUDA toolkit (FLAMEGPU currently builds with v7.0) and Visual Studio 2012 or higher, so that you can build FLAMEGPU yourself.

Provided are an application for generating initial state files (within the `init` directory) and the initial states used to provide the results. Additionally the executables directory contains all the builds of FLAMEGPU used to produce the results.

Due to the nature of FLAMEGPUs implementation and use of XLST templates, adjusting the environment width or interaction RADIUS, resizes compile-time allocated memory. Additionally there is no support for providing runtime model paramters or initial state generation. This has meant that in benchmarking FLAMEGPU, each individual configuration required a seperate build.

The changes required to update the parameters are:
* `XMLModelFile.xml:106` - Interaction Radius x 2 (double the value of this parameter and insert it at this line)
* `XMLModelFile.xml:108` - Width
* `XMLModelFile.xml:110` - Width
* `XMLModelFile.xml:112` - Width
* `XMLModelFile.xml:114` - Total Agent Count
* `functions.c:22` - Interaction Radius
* `functions.c:23` - Attraction Force
* `functions.c:24` - Repulsion Force
* `functions.c:25` - Width