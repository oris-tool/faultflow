# Fault Flow Library

This repository provides FaultFlow, a Java library implementing a Model Driven Engineering (MDE) approach to dependability evaluation of component-based Systems of Systems (SoS). 

FaultFlow is presented in a paper titled "FaultFlow: an MDE Java Library for Dependability Evaluation of Systems of System", authored by Laura Carnevali, Stefania Cerboni, Leonardo Montecchi, and Enrico Vicario, currently submitted to the IEEE Transactions on Dependable and Secure Computing.

The most distinctive features of FaultFlow are: 
- definition of a custom-made extensible metamodel to specify SoS structure and failure logic, modeling both intra-component fault-to-failure propagations (i.e., faults propagating into errors, in turn yielding failures) and inter-component failure-to-fault propagations (i.e., component failures acting as faults for other same- or higher-level, possibly not physically connected, components) characterized by non-Markovian durations; 
- automated derivation of metamodel instances from SysML Block Definition Diagrams (BDDs) modeling SoS structure and Stochastic Fault Trees (SFTs) modeling failure propagations; 
- automated translation of metamodel instances into metamodel instances of the [Sirio library](https://github.com/oris-tool/sirio) to derive the distribution of the failure process duration; 
- if each fault does not propagate into multiple failures and viceversa (i.e., if the corresponding SFT does not contain repeated events at any level), automated translation of metamodel instances into metamodel instances of the [Pyramis library](https://github.com/oris-tool/pyramis) to derive the duration distribution of the failure process and importance measures characterizing how faults contribute to failures over time, achieving evaluation efficiency even for significantly complex SoS with hundreds of fault modes.

## Experimental reproducibility

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
