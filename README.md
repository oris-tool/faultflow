# Fault Flow Library

This repository provides FaultFlow, a Java library implementing a Model Driven Engineering (MDE) approach to dependability evaluation of component-based coherent systems. 

FaultFlow is presented in a paper titled "FaultFlow: an MDE Library for Dependability Evaluation of Component-Based System", authored by Laura Carnevali, Stefania Cerboni, Leonardo Montecchi, and Enrico Vicario, currently submitted to the IEEE Transactions on Dependable and Secure Computing.

The most distinctive features of FaultFlow are: 
- definition of a custom-made extensible metamodel to specify system structure and failure logic, modeling both intra-component fault-to-failure propagations (i.e., faults propagating into errors, in turn yielding failures) and inter-component failure-to-fault propagations (i.e., component failures acting as faults for other same- or higher-level, possibly not physically connected, components) characterized by non-Markovian durations; 
- automated derivation of metamodel instances from SysML Block Definition Diagrams (BDDs) modeling the system structure and Stochastic Static Fault Trees (SSFTs) modeling failure propagations; 
- automated translation of metamodel instances into metamodel instances of the [Sirio library](https://github.com/oris-tool/sirio) to derive the distribution of the failure process duration; 
- if each fault does not propagate into multiple failures and viceversa (i.e., if the corresponding SSFT does not contain repeated events at any level), automated translation of metamodel instances into metamodel instances of the [Pyramis library](https://github.com/oris-tool/pyramis) to derive the duration distribution of the failure process and importance measures characterizing how faults contribute to failures over time, achieving evaluation efficiency even for significantly complex systems with hundreds of fault modes.

## Experimental reproducibility

To support reproducibility of the experimental results reported in the paper, this repository contains the code that builds and evaluates the considered system, and the steps reported below illustrate how to repeat the experiments.

- Run the main method of `faultflow/src/main/java/it/unifi/stlab/faultflow/launcher/AnalysisLauncher.java` to analyze the system shown in Figure 6 of the paper and reproduce the results shown in Figure 4b (i.e., the .cvs files containing the distribution of the duration of the failure processes that determine Failure1 of component IB and Failure1 of component GDB), Figure 8c (i.e., the .csv file containing the distribution of the duration of the failure process of the entire system), Figure 10 (i.e., the .csv files containing the Birnbaum measures and the Fussell-Vesely measures of all internal faults). For the entire system, the minimal cut sets, shown in Table 1, are also provided. For each experiment, the execution time is also provided.

- Run the main method of `faultflow/src/main/java/it/unifi/stlab/faultflow/launcher/ScalabilityAnalysisLauncher.java` to analyze the variants of the system shown in Figure 6 of the paper (which are described in Section 4.3 of the paper) and reproduce the results reported in Table 2 (i.e., the execution times needed to compute the time-to-failure CDF of the system, and the .csv files containing the Birnbaum measures and the Fussel-Vesely measure of all internal faults). For each model, the minimal cut sets are also provided.

## Installation

This repository provides a ready-to-use Maven project that you can easily import into an Eclipse workspace to start working with the [FaultFlow library](https://github.com/oris-tool/faultflow/) (the version `2.0.0-SNAPSHOT` of the [Sirio library](https://github.com/oris-tool/sirio) is included as a Maven dependency). Just follow these steps:

1. **Install Java >= 11.** For Windows, you can download a [package from Oracle](https://www.oracle.com/java/technologies/downloads/#java11); for Linux, you can run `apt-get install openjdk-11-jdk`; for macOS, you can run `brew install --cask java`. 

2. **Download Eclipse.** The [Eclipse IDE for Java Developers](http://www.eclipse.org/downloads/eclipse-packages/) package is sufficient.

3. **Clone this project.** Inside Eclipse:
   - Select `File > Import > Maven > Check out Maven Projects from SCM` and click `Next`.
   - If the `SCM URL` dropbox is grayed out, click on `m2e Marketplace` and install `m2e-egit`. You will have to restart Eclipse.
   - As `SCM URL`, type: `git@github.com:oris-tool/faultflow.git` and click `Next` and then `Finish`.


## Licence

FaultFlow is released under the [GNU Affero General Public License v3.0](https://choosealicense.com/licenses/agpl-3.0).
