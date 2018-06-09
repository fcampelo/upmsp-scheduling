# Unrelated Parallel Machine Scheduling Problem with Sequence Dependent Setup Times

> **Contributors:** Letícia Mayra Pereira<sup>1,3</sup>, André L. Maravilha<sup>1,3</sup>, Felipe Campelo<sup>2,3</sup>  
> <sup>1</sup> *Graduate Program in Electrical Engineering, Universidade Federal de Minas Gerais ([PPGEE](https://www.ppgee.ufmg.br/), [UFMG](https://www.ufmg.br/))*  
> <sup>2</sup> *Dept. Electrical Engineering, Universidade Federal de Minas Gerais ([DEE](http://www.dee.ufmg.br/), [UFMG](https://www.ufmg.br/))*  
> <sup>3</sup> *Operations Research and Complex Systems Lab., Universidade Federal de Minas Gerais ([ORCS Lab](http://orcslab.ppgee.ufmg.br/), [UFMG](https://www.ufmg.br/))*


## 1. Problem description

Given a set N = {1, ..., n} of n jobs and a set M = {1, ..., m} of m parallel machines, each job j in N has to be processed on exactly one machine k in M. The machines are unrelated, i.e., the processing time of a job depends on the machine to which it is assigned to. Moreover, setup times dependent on the sequence and machine are considered. In this work, the objective is to minimize the makespan, i.e., the time of completion of the last job to leave the system.

### 1.1. Instance files

The instances proposed by [1](#references) can be downloaded from the research group [Applied Optimization Systems](http://soa.iti.es/problem-instances):
* [Large and small instances](http://soa.iti.es/files/RSDST.7z)
* [Test instances for calibration experiments](http://soa.iti.es/files/RSDSTCalibration.7z)

## 2. How to build and run the project

### 2.1. Building the project

This project was developed with Java 8 and it uses the. To compile this project you need the Java SE Development Kit 8 (JDK 8) installed in your computer. Inside the root directory of the project, run the following commands:
```
./gradlew clean
./gradlew shadow
```

After running the commands above, the file `upmsp-all.jar` can be found in the directory `build/libs`. You do not need any additional library to run the project. The gradle is configured to include all dependencies in the jar file.


### 2.2. Running the project

#### 2.2.1. Show the help message:

```
java -jar upmsp-all --help
```

#### 2.2.2. General structure of the command line

```
java -jar upmsp-all [command] [command options]
```  
in which `[command]` is the command to run and `[command options]` are command specific options. There are the following commands available:
* `optimize`: Optimize an instance of the problem.
* `analyze`: Perform the neighborhood analysis throughout the optimization process.
* `track`: Save all solutions which, at some point of the optimization process, were incumbents. It uses a Simulated Annealing [2](#references) as optimization algorithm.


#### 2.2.3. Command "optimize"

Usage:  
```
java -jar upmsp-all optimize [options]
```

Example:  
```
java -jar upmsp-all optimize --algorithm simulated-annealing --instance I_50_10_S_1-9_1.txt --verbose
```

Parameters:  
`--algorithm <VALUE>`  
(Default: `simulated-annealing`)  
Optimization algorithm. Specific parameters for the algorithms are described in the next subsections.

`--instance <VALUE>`  
(Required)  
Name of the file containing the instance data.

`--seed <VALUE>`  
(Default: timestamp)  
Seed used to initialize the random number generator used by the algorithms.

`--time-limit <VALUE>`  
(Default: calculated according to the instance size)  
Total time for running the algorithm (in milliseconds).

`--verbose`  
If used, the algorithm progress is displayed on the screen.

`--print-solution`  
If used, the best solution is displayed on the screen at the end of the optimization process.


##### Simulated Annealing specific parameters

The Simulated Annealing implemented in this project is the one described in [2](#references). The specific parameters for the Simulated Annealing are described bellow:

`--param iterations-limit=<VALUE>`  
(Default: a very large number)  
The maximum number of iterations to run.

`--param iterations-per-temperature=<VALUE>`  
(Default: `1176628L`)  
Number of iterations to run before change the temperature value.

`--param initial-temperature=<VALUE>`  
(Default: `1.0`)  
The initial temperature.

`--param cooling-rate=<VALUE>`  
(Default: `0.96`)  
Cooling rate.

The Simulated Annealing implemented uses six different neighborhood functions: shift, switch, task-move, swap, two-shift, direct-swap. By default, it uses all of them. To disable one or more neighborhood functions, you can use:
```
--param disable=<VALUE>
```  
in which `<VALUE>` is the name of the neighborhood function. For example:
```
--param disable=shift
```  
This parameter can be used more than once to disable multiple neighborhood functions, for example:
```
--param disable=shift --param disable=two-shift
```


#### 2.2.4. Command "analyze"

Usage:  
```
java -jar upmsp-all analyze [options]
```

Examples:  
```
java -jar upmsp-all analyze --instances ./upmsp/instances --verbose
```

Parameters:  
`--instances <VALUE>`  
(Required; Default: `.`)  
Path to the directory containing the instance files.

`--output <VALUE>`  
(Default: `.`)  
Path to the file in which the data will be saved.

`--optimize`  
If used, performs the analysis of the neighborhoods throughout the optimization process. The Simulated Annealing [2](#references) is used as optimization algorithm.

`--track`  
If used, save all solutions which at some point were incumbents.

`--repetitions <VALUE>`  
(Default: `1`)  
Number of times the analysis will be repeated.

`--threads <VALUE>`  
(Default: number of threads available minus 1)  
The number of threads used to perform the analysis.

`--verbose`  
If used, the progress is displayed on the screen.


#### 2.2.5. Command "track"

Usage:  
```
java -jar upmsp-all track [options]
```

Examples:  
```
java -jar upmsp-all track --instances ./upmsp/instances --output ./upmsp/track --verbose
```

Parameters:  
`--instances <VALUE>`  
(Required; Default: `.`)  
Path to the directory containing the instance files.

`--output <VALUE>`  
(Default: `.`)  
Path to the directory in which the data will be saved.

`--repetitions <VALUE>`  
(Default: `1`)  
Number of times the analysis will be repeated.

`--threads <VALUE>`  
(Default: number of threads available minus 1)  
The number of threads used to perform the analysis.

`--verbose`  
If used, the progress is displayed on the screen.


## References

1. Vallada, E.; Ruiz, R. "Genetic algorithms for the unrelated parallel machine scheduling problem with sequence dependent setup times". European Journal of Operational Research, 211(3), 612-622, 2011. (doi: [10.1016/j.ejor.2011.01.011](https://doi.org/10.1016/j.ejor.2011.01.011))

2. Santos, H.G.; Toffolo, T.A.M.; Silva, C.L.T.F.; Berghe, G.V. "Analysis of stochastic local search methods for the unrelated parallel machine scheduling problem". International Transactions in Operational Research, 2016. (doi: [10.1111/itor.12316](https://doi.org/10.1111/itor.12316))

