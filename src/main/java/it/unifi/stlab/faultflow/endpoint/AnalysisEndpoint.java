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

package it.unifi.stlab.faultflow.endpoint;

import it.unifi.hierarchical.analysis.HierarchicalSMPAnalysis;
import it.unifi.hierarchical.analysis.NumericalValues;
import it.unifi.hierarchical.model.HSMP;
import it.unifi.stlab.faultflow.dao.knowledge.ErrorModeDao;
import it.unifi.stlab.faultflow.dao.knowledge.SystemDao;
import it.unifi.stlab.faultflow.dto.analysis.CutsetCalculationDTO;
import it.unifi.stlab.faultflow.dto.analysis.FaultImportanceMeasureDTO;
import it.unifi.stlab.faultflow.dto.analysis.ImportanceMeasureDTO;
import it.unifi.stlab.faultflow.dto.analysis.TFLResultsDTO;
import it.unifi.stlab.faultflow.endpoint.exception.NoEntityFoundException;
import it.unifi.stlab.faultflow.endpoint.response.NotFoundResponse;
import it.unifi.stlab.faultflow.model.knowledge.composition.SystemType;
import it.unifi.stlab.faultflow.model.knowledge.propagation.ErrorMode;
import it.unifi.stlab.faultflow.model.knowledge.propagation.FaultMode;
import it.unifi.stlab.faultflow.model.knowledge.propagation.InternalFaultMode;
import it.unifi.stlab.transformation.HSMPParser;
import it.unifi.stlab.transformation.TreeParser;
import it.unifi.stlab.transformation.faulttree.BasicEvent;
import it.unifi.stlab.transformation.faulttree.BasicEventsFinder;
import it.unifi.stlab.transformation.faulttree.Node;
import it.unifi.stlab.transformation.faulttree.TreeNodeChanger;
import it.unifi.stlab.transformation.minimalcutset.MOCUSEngine;
import it.unifi.stlab.transformation.minimalcutset.MinimalCutSet;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;

@Path("/analysis")
public class AnalysisEndpoint {

    @Inject
    SystemDao systemDao;

    @Inject
    ErrorModeDao errorModeDao;

    @GET
    @Path("/getMinimalCutsets")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMinimalCutsets(@QueryParam("systemUUID") String systemUUID,
                                      @QueryParam("errorModeUUID") String errorModeUUID) {
        Node node;

        try {
            node = getTreeFromSystemErrorMode(systemUUID, errorModeUUID);
        } catch (NoEntityFoundException e) {
            return Response
                    .ok(NotFoundResponse.create(e.getEntityClass(), e.getEntityExternalID()))
                    .build();
        }

        List<MinimalCutSet> minimalCutSets = MOCUSEngine.getInstance().getMinimalCutSet(node);

        return Response
                .ok(new CutsetCalculationDTO(systemUUID, errorModeUUID, minimalCutSets))
                .build();
    }

    @GET
    @Path("/importanceMeasure")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getImportanceMeasure(@QueryParam("systemUUID") String systemUUID,
                                         @QueryParam("errorModeUUID") String errorModeUUID,
                                         @QueryParam("measure") String measure,
                                         @QueryParam("timeStep") double timestep,
                                         @QueryParam("time") int time) {
        SystemType system = systemDao.findById(systemUUID);
        ErrorMode errorMode = errorModeDao.findById(errorModeUUID);

        if (system == null)
            return Response
                    .ok(NotFoundResponse.create("System", systemUUID))
                    .build();

        if (errorMode == null)
            return Response
                    .ok(NotFoundResponse.create("ErrorMode", errorModeUUID))
                    .build();

        // Create the fault tree given the system and its error mode of interest
        TreeParser treeParser = new TreeParser(system);
        Node node = treeParser.createTree(errorMode);

        // Find all the basic events in the fault tree
        BasicEventsFinder finder = new BasicEventsFinder();
        finder.visit(node);
        List<BasicEvent> basicEvents = finder.getBasicEvents();

        // Create the importance measure DTO to be populated later
        ImportanceMeasureDTO importanceMeasureDTO = new ImportanceMeasureDTO(measure, systemUUID, errorModeUUID, 0,
                time, timestep);

        if (measure.equals("fusselvesely")) {
            Map<String, double[]> importanceMeasures = new HashMap<>();
            Date start = new Date(); // Mark the starting time of the calculation

            List<MinimalCutSet> minimalCutSets = MOCUSEngine.getInstance().getMinimalCutSet(node);

            // Calculate the CDF of each minimal cutset
            Map<List<String>, double[]> mcsCDFs = calculateMCSCDF(minimalCutSets, system, errorMode,
                    timestep, time);

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
            importanceMeasureDTO.setElapsedAnalysisTime(elapsedAnalysisTime);

            for (String key : importanceMeasures.keySet()) {
                importanceMeasureDTO.addFaultImportanceMeasure(new FaultImportanceMeasureDTO(key,
                        importanceMeasures.get(key)));
            }

            return Response
                    .ok(importanceMeasureDTO)
                    .build();
        } else if (measure.equals("birnbaum")) {
            Date start = new Date();

            for (BasicEvent basicEvent : basicEvents) {
                /* Creates a copy of the basic event taken into account with a dirac(0) PDF to compute the CDF with
                the fault already happened in the system; at the same time, it saves the original fault mode to be
                restored later */
                FaultMode originalFault = TreeNodeChanger.changeFaultMode(node, basicEvent.getDescription(),
                        new InternalFaultMode(basicEvent.getFaultMode().getName(),"dirac(0)"));

                // Creates the HSMP with P(1) for the basic event
                HSMP hsmpOne = HSMPParser.parseTree(node);
                HierarchicalSMPAnalysis modifiedAnalysis = new HierarchicalSMPAnalysis(hsmpOne, 0);
                modifiedAnalysis.evaluate(timestep, time);

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
                modifiedAnalysis.evaluate(timestep, time);

                NumericalValues zeroCdf = HierarchicalSMPAnalysis.cdf;
                double[] zeroCdfValues = Arrays.copyOf(zeroCdf.getValues(), zeroCdf.getValues().length);
                double[] difference = new double[oneCdfValues.length];

                // Computes the point-by-point CDF difference for each time tick
                for (int index = 0; index < oneCdfValues.length; index++)
                    difference[index] = oneCdfValues[index] - zeroCdfValues[index];

                // Restores the original fault mode in the fault tree
                TreeNodeChanger.changeFaultMode(node, basicEvent.getDescription(), (InternalFaultMode) originalFault);

                importanceMeasureDTO.addFaultImportanceMeasure(new FaultImportanceMeasureDTO(originalFault.getName(),
                        difference));
            }

            Date end = new Date();
            long elapsedAnalysisTime = end.getTime() - start.getTime();
            importanceMeasureDTO.setElapsedAnalysisTime(elapsedAnalysisTime);

            return Response
                    .ok(importanceMeasureDTO)
                    .build();
        }

        return Response
                .noContent()
                .build();
    }

    // Calculates the top event CDF using the Pyramis analysis engine
    @GET
    @Path("/pyramisCDF")
    @Produces(MediaType.APPLICATION_JSON)
    public Response pyramisCDF(@QueryParam("systemUUID") String systemUUID,
                               @QueryParam("errorModeUUID") String errorModeUUID,
                               @QueryParam("timeStep") double timestep,
                               @QueryParam("timeLimit") double timeLimit) {
        HSMP hsmp;

        try {
            hsmp = HSMPParser.parseTree(getTreeFromSystemErrorMode(systemUUID, errorModeUUID));
        } catch (NoEntityFoundException e) {
            return Response
                    .ok(NotFoundResponse.create(e.getEntityClass(), e.getEntityExternalID()))
                    .build();
        }

        HierarchicalSMPAnalysis analysis = new HierarchicalSMPAnalysis(hsmp, 0);

        Date start = new Date();
        analysis.evaluate(timestep, timeLimit);
        Date end = new Date();
        long time = end.getTime() - start.getTime();

        NumericalValues cdf = HierarchicalSMPAnalysis.cdf;

        TFLResultsDTO tflResultsDTO = new TFLResultsDTO(systemUUID, errorModeUUID, timestep, timeLimit, time,
                cdf.getValues());

        return Response
                .ok(tflResultsDTO)
                .build();
    }

    /**
     * Private utility method used to create a Fault Tree and return its top event starting from the String version
     * of the activation function of an error mode.
     */
    private Node getTreeFromSystemErrorMode(String systemUUID, String errorModeUUID) throws NoEntityFoundException {
        SystemType system = systemDao.findById(systemUUID);
        ErrorMode errorMode = errorModeDao.findById(errorModeUUID);

        if (system == null)
            throw new NoEntityFoundException("System", systemUUID);

        if (errorMode == null)
            throw new NoEntityFoundException("ErrorMode", errorModeUUID);

        TreeParser treeParser = new TreeParser(system);
        return treeParser.createTree(errorMode);
    }

    /**
     * Calculates the CDF of the top event for each minimal cutset: to do so, it reduces the fault tree at every
     * iteration to calculate the CDF by causing the top event only with the fault modes in the minimal cutset. It then
     * returns a Map to associate each cutset to an array of doubles representing the CDF over time
     */
    private Map<List<String>, double[]> calculateMCSCDF(List<MinimalCutSet> minimalCutSets, SystemType system,
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
