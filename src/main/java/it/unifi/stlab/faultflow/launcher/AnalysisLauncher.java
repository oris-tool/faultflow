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

package it.unifi.stlab.faultflow.launcher;

import it.unifi.hierarchical.analysis.HierarchicalSMPAnalysis;
import it.unifi.hierarchical.analysis.NumericalValues;
import it.unifi.hierarchical.model.HSMP;
import it.unifi.stlab.faultflow.analysis.PetriNetAnalyzer;
import it.unifi.stlab.faultflow.analysis.PetriNetReducer;
import it.unifi.stlab.faultflow.exporter.PetriNetExportMethod;
import it.unifi.stlab.faultflow.launcher.builders.PetroleumSystemBuilder;
import it.unifi.stlab.faultflow.model.knowledge.composition.System;
import it.unifi.stlab.faultflow.model.knowledge.propagation.ErrorMode;
import it.unifi.stlab.faultflow.translator.PetriNetTranslator;
import it.unifi.stlab.transformation.HSMPParser;
import it.unifi.stlab.transformation.TreeParser;
import it.unifi.stlab.transformation.minimalcutset.ImportanceMeasure;
import org.oristool.models.stpn.RewardRate;
import org.oristool.models.stpn.TransientSolution;
import org.oristool.models.stpn.trees.DeterministicEnablingState;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

/**
 * Reproduces the examples shown in section 4.3 in the article. More specifically it calculates:
 * - CDF with Sirio of GDBFailure1 with timeLimit 8000 and timeStep 2, printing the elapsed time and storing the result into a csv file (result-sirio-GDBFailure1.csv);
 * - CDF with Sirio of IBFailure1 with timeLimit 8000 and timeStep 2, printing the elapsed time and storing the result into a csv file (result-sirio-IBFailure1.csv);
 * - CDF with Pyramis of the top level Failure (GDSFailure1) with timeLimit 8000 and timeStep 2, printing the elapsed time and storing the result into a csv file (result-pyramis.csv);
 * - Fussell-Vesely importance measure for every fault in the system with timeLimit 60000 and timeStep 4 and storing the result into a csv file (fussellvesely.csv);
 * - Birnbaum importance measure for every fault in the system with timeLimit 60000 and timeStep 4 and storing the result into a csv file (birnbaum.csv);
 *
 * Each found can be found inside the "export" subdirectory.
 */

public class AnalysisLauncher {

    public static void main(String[] args) throws IOException {
        //Build Petroleum System
        System s = PetroleumSystemBuilder.getInstance().getSystem();

        //Analysis parameters
        double timeLimit = 8000;
        double timeStep = 2.0;
        double error = 0.0;
        //--> ANALYSIS WITH SIRIO
        sirioAnalysis("GDBFailure1", s, timeLimit, timeStep, error);
        sirioAnalysis("IBFailure1", s, timeLimit, timeStep, error);

        //-->ANALYSIS WITH PYRAMIS
        ErrorMode errorMode = PetroleumSystemBuilder.getErrorMode("GDSOR1");
        pyramisAnalysis(s, errorMode, timeLimit, timeStep);

        //Importance measures parameters
        double imStep = 4;
        int timeAnalysis = 60000;
        fussellVesely(s, errorMode, timeAnalysis, imStep);
        birnbaum(s, errorMode, timeAnalysis, imStep);
    }

    public static void sirioAnalysis(String failureName, System s, double timeLimit, double timeStep, double error) throws IOException {
        //Reduce Petri Net
        PetriNetTranslator pnt = new PetriNetTranslator();
        pnt.translate(s, PetriNetExportMethod.FAULT_ANALYSIS);
        PetriNetReducer petriNetReducer = new PetriNetReducer(pnt.getPetriNet(), pnt.getMarking());
        petriNetReducer.reduce(failureName, PetroleumSystemBuilder.getPropagationPorts(), PetroleumSystemBuilder.getErrorModes());
        //Analyze reduced Petri Net
        PetriNetAnalyzer petriNetAnalyzer = new PetriNetAnalyzer(petriNetReducer.getPetriNet(), petriNetReducer.getMarking());
        Date start = new Date();
        TransientSolution<DeterministicEnablingState, RewardRate> rewards =
                petriNetAnalyzer.regenerativeTransient(failureName, new BigDecimal(timeLimit), new BigDecimal(timeStep), new BigDecimal(error));
        Date end = new Date();
        long time = end.getTime() - start.getTime();
        java.lang.System.out.println("Elapsed analysis time with Sirio of failure " + failureName + ": " + time + " ms");
        //Get results
        FileWriter writer = new FileWriter("export/result-sirio-" + failureName + ".csv");
        writer.append("Analysis Result of failure " + failureName + "\n");
        writer.append("Time: " + rewards.getTimeLimit().toString() + ", Step: " + rewards.getStep().toString() + ", Error: " + error + "\n");
        writer.append("Values: \n");
        for (int index = 0; index < rewards.getSolution().length; index++) {
            writer.append(String.valueOf(rewards.getSolution()[index][0][0]));
            writer.append("\n");
        }
        writer.close();
    }

    private static void pyramisAnalysis(System s, ErrorMode errorMode, double timeLimit, double timeStep) throws IOException {
        TreeParser treeParser = new TreeParser(s);
        HSMP hsmp = HSMPParser.parseTree(treeParser.createTree(errorMode));
        HierarchicalSMPAnalysis analysis = new HierarchicalSMPAnalysis(hsmp, 0);
        Date start = new Date();
        analysis.evaluate(timeStep, timeLimit);
        Date end = new Date();
        long time = end.getTime() - start.getTime();
        java.lang.System.out.println("Elapsed analysis time with Pyramis: " + time + " ms");
        FileWriter writer = new FileWriter("export/result-pyramis.csv");
        writer.append("Analysis Result of failure " + errorMode.getOutgoingFailure().getDescription() + "\n");
        writer.append("Time: " + timeLimit + ", Step: " + timeStep + "\n");
        NumericalValues cdf = HierarchicalSMPAnalysis.cdf;
        writer.append("Values: \n");

        for (double v : cdf.getValues()) {
            writer.append(String.valueOf(v));
            writer.append("\n");
        }
        writer.close();
    }

    private static void fussellVesely(System s, ErrorMode errorMode, int timeAnalysis, double timeStep) throws IOException {
        ImportanceMeasure importanceMeasure = new ImportanceMeasure();
        FileWriter writer = new FileWriter("export/fussellvesely.csv");
        writer.append("Fussell-Vesely Importance Measure Result of Errormode " + errorMode.getName() + "\n");
        writer.append("Time: " + timeAnalysis + ", Step: " + timeStep + "\n");
        writer.append("Values: \n");
        Map<String, double[]> fussellvesely = importanceMeasure.getImportanceMeasure(s, errorMode, "fusselvesely", timeStep, timeAnalysis);
        for (Map.Entry<String, double[]> entry : fussellvesely.entrySet()) {
            writer.append(entry.getKey() + ": " + Arrays.toString(entry.getValue()));
            writer.append("\n");
        }
        writer.close();
    }

    private static void birnbaum(System s, ErrorMode errorMode, int timeAnalysis, double timeStep) throws IOException {
        ImportanceMeasure importanceMeasure = new ImportanceMeasure();
        FileWriter writer = new FileWriter("export/birnbaum.csv");
        writer.append("Birnbaum Importance Measure Result of Errormode " + errorMode.getName() + "\n");
        writer.append("Time: " + timeAnalysis + ", Step: " + timeStep + "\n");
        writer.append("Values: \n");
        Map<String, double[]> birnbaum = importanceMeasure.getImportanceMeasure(s, errorMode, "birnbaum", timeStep, timeAnalysis);
        for (Map.Entry<String, double[]> entry : birnbaum.entrySet()) {
            writer.append(entry.getKey() + ": " + Arrays.toString(entry.getValue()));
            writer.append("\n");
        }
        writer.close();
    }
}
