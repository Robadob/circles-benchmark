#Validation

Due to the effects of floating point precision being compounded after multiple iterations, validation compares the results of each model after a single iteration working from a unified initial state. Agents are exported in the same order that they are initialised (as sorting based on location can cause mismatches) and each location pair is then compared.

VectorMatch is the source code for a simple project for comparing these exported files.

##Model 1
* Width: 100
* Density: 0.01
* Interaction Radius: 5
* Attraction Force: 0.1
* Repulsion Force: 0.1
* Calculated Agent Count: 10,000

###Results
Provided is an example output from the data in the folder `Validation Model`

Min Difference
|          | Init State | FLAMEGPU | MASON | Repast |
|----------|------------|----------|-------|--------|
| Init     | -          | -        | -     | -      |
| FLAMEGPU | 0.081      | -        | -     | -      |
| MASON    | 0.081      | 0.0      | -     | -      |
| Repast   | 0.081      | 0.0      | 0.0   | -      |

Max Difference
|          | Init State | FLAMEGPU | MASON   | Repast  |
|----------|------------|----------|---------|---------|
| Init     | -          | -        | -       | -       |
| FLAMEGPU | 4.932      | -        | -       | -       |
| MASON    | 4.932      | 0.0      | -       | -       |
| Repast   | 4.932      | 0.0      | 0.0     | -       |

Mean difference
|          | Init State | FLAMEGPU | MASON | Repast |
|----------|------------|----------|-------|--------|
| Init     | -          | -        | -     | -      |
| FLAMEGPU | 1.610      | -        | -     | -      |
| MASON    | 1.610      | 0.0      | -     | -      |
| Repast   | 1.610      | 0.0      | 0.0   | -      |

Agents with over 1 unit difference
|          | Init State | FLAMEGPU | MASON | Repast |
|----------|------------|----------|-------|--------|
| Init     | -          | -        | -     | -      |
| FLAMEGPU | 6839       | -        | -     | -      |
| MASON    | 6839       | 0        | -     | -      |
| Repast   | 6839       | 0        | 0     | -      |