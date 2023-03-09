/*
 * This program is part of the ORIS Tool.
 * Copyright (C) 2011-2023 The ORIS Authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package it.unifi.stlab.transformation.minimalcutset;

import it.unifi.hierarchical.analysis.NumericalValues;
import it.unifi.hierarchical.analysis.HierarchicalSMPAnalysis;
import it.unifi.hierarchical.model.HSMP;
import it.unifi.stlab.faultflow.model.knowledge.composition.System;
import it.unifi.stlab.faultflow.model.knowledge.propagation.ErrorMode;
import it.unifi.stlab.faultflow.model.knowledge.propagation.FaultMode;
import it.unifi.stlab.faultflow.model.knowledge.propagation.InternalFaultMode;
import it.unifi.stlab.transformation.HSMPParser;
import it.unifi.stlab.transformation.TreeParser;
import it.unifi.stlab.transformation.faulttree.BasicEvent;
import it.unifi.stlab.transformation.faulttree.BasicEventsFinder;
import it.unifi.stlab.transformation.faulttree.Node;
import it.unifi.stlab.transformation.faulttree.TreeNodeChanger;

import java.util.*;
import java.util.stream.Collectors;

public class ImportanceMeasure {

    public Map<String, double[]> getImportanceMeasure(System system, ErrorMode errorMode, String measure,
                                                      double timeStep, int time) {
        // Create the fault tree given the system and its error mode of interest
        TreeParser treeParser = new TreeParser(system);
        Node node = treeParser.createTree(errorMode);

        // Find all the basic events in the fault tree
        BasicEventsFinder finder = new BasicEventsFinder();
        finder.visit(node);
        List<BasicEvent> basicEvents = finder.getBasicEvents();
        Map<String, double[]> importanceMeasures = new HashMap<>();
        if (measure.equals("fusselvesely")) {
            Date start = new Date(); // Mark the starting time of the calculation

            List<MinimalCutSet> minimalCutSets = MOCUSEngine.getInstance().getMinimalCutSet(node);

            // Calculate the CDF of each minimal cutset
            Map<List<String>, double[]> mcsCDFs = calculateMCSCDF(minimalCutSets, system, errorMode,
                    timeStep, time);

            int length = 0;

            for (Map.Entry<List<String>, double[]> entry : mcsCDFs.entrySet()) {
                length = entry.getValue().length;
                break;
            }

            for (BasicEvent basicEvent : basicEvents) {
                String faultName = basicEvent.getDescription();
                importanceMeasures.put(faultName, new double[length]);
                List<List<String>> interestedCutSets = new ArrayList<>();

                // Save each cutset in which the basic event participates in
                for (List<String> key : mcsCDFs.keySet()) {
                    if (key.contains(basicEvent.getDescription()))
                        interestedCutSets.add(key);
                }

                for (int index = 0; index < length; index++) {
                    double sum = 0;

                    for (List<String> interestedCutSet : interestedCutSets) {
                        sum += mcsCDFs.get(interestedCutSet)[index];
                    }

                    /* FV measure obtained as the sum of CDFs of each cutset in which the basic event participates in
                    divided by the number of participating cutsets */
                    importanceMeasures.get(faultName)[index] = sum / interestedCutSets.size();
                }
            }

            Date end = new Date(); // Saves the ending time of the calculation...
            long elapsedAnalysisTime = end.getTime() - start.getTime(); // ...and calculates the delta to get the corresponding computation time
            java.lang.System.out.println("Fussel Vesely time: " + elapsedAnalysisTime);
        } else if (measure.equals("birnbaum")) {
            Date start = new Date();

            for (BasicEvent basicEvent : basicEvents) {
                /* Creates a copy of the basic event taken into account with a dirac(0) PDF to compute the CDF with
                the fault already happened in the system; at the same time, it saves the original fault mode to be
                restored later */
                FaultMode originalFault = TreeNodeChanger.changeFaultMode(node, basicEvent.getDescription(),
                        new InternalFaultMode(basicEvent.getFaultMode().getName(), "dirac(0)"));
                // Creates the HSMP with P(1) for the basic event
                HSMP hsmpOne = HSMPParser.parseTree(node);
                HierarchicalSMPAnalysis modifiedAnalysis = new HierarchicalSMPAnalysis(hsmpOne, 0);
                modifiedAnalysis.evaluate(timeStep, time);

                NumericalValues oneCdf = HierarchicalSMPAnalysis.cdf;
                double[] oneCdfValues = Arrays.copyOf(oneCdf.getValues(), oneCdf.getValues().length);

                /* Find all the basic events excluding the one already taken into consideration: this is used later
                to reduce the fault tree so that it will contain all the basic events excluding the one considered,
                so that P(0) can be calculated for the Birnbaum measure */
                List<String> basicEventsMinusOne = new ArrayList<>();
                basicEvents.forEach(fault -> {
                    if (!fault.getDescription().equals(basicEvent.getFaultMode().getName()))
                        basicEventsMinusOne.add(fault.getDescription());
                });

                treeParser = new TreeParser(system);
                Node newTree = treeParser.createTree(errorMode);
                // Reduces the fault tree to exclude the currently considered basic event
                newTree = treeParser.reduceTree(basicEventsMinusOne, newTree);

                HSMP hsmpZero = HSMPParser.parseTree(newTree);
                modifiedAnalysis = new HierarchicalSMPAnalysis(hsmpZero, 0);
                modifiedAnalysis.evaluate(timeStep, time);

                NumericalValues zeroCdf = HierarchicalSMPAnalysis.cdf;
                double[] zeroCdfValues = Arrays.copyOf(zeroCdf.getValues(), zeroCdf.getValues().length);
                double[] difference = new double[oneCdfValues.length];

                // Computes the point-by-point CDF difference for each time tick
                for (int index = 0; index < oneCdfValues.length; index++)
                    difference[index] = oneCdfValues[index] - zeroCdfValues[index];

                // Restores the original fault mode in the fault tree
                TreeNodeChanger.changeFaultMode(node, basicEvent.getDescription(), (InternalFaultMode) originalFault);

                importanceMeasures.put(originalFault.getName(), difference);
            }

            Date end = new Date();
            long elapsedAnalysisTime = end.getTime() - start.getTime();
            java.lang.System.out.println("Birnbaum time: " + elapsedAnalysisTime);
        }
        return importanceMeasures;


    }


    /**
     * Calculates the CDF of the top event for each minimal cutset: to do so, it reduces the fault tree at every
     * iteration to calculate the CDF by causing the top event only with the fault modes in the minimal cutset. It then
     * returns a Map to associate each cutset to an array of doubles representing the CDF over time
     */
    private Map<List<String>, double[]> calculateMCSCDF(List<MinimalCutSet> minimalCutSets, System system,
                                                        ErrorMode errorMode, double timeStep, int time) {
        TreeParser treeParser = new TreeParser(system);
        Map<List<String>, double[]> cdfValues = new HashMap<>();
        for (MinimalCutSet minimalCutSet : minimalCutSets) {
            Node tree = treeParser.createTree(errorMode);
            List<String> mcs = minimalCutSet.getCutSet().stream()
                    .map(BasicEvent::getDescription)
                    .collect(Collectors.toList());

            Node reducedTree = treeParser.reduceTree(mcs, tree);
            HSMP hsmp = HSMPParser.parseTree(reducedTree);
            HierarchicalSMPAnalysis analysis = new HierarchicalSMPAnalysis(hsmp, 0);
            analysis.evaluate(timeStep, time);
            NumericalValues cdf = HierarchicalSMPAnalysis.cdf;
            cdfValues.put(mcs, cdf.getValues());
        }

        return cdfValues;
    }
}
