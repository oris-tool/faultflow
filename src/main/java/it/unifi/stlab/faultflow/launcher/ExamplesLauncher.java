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

import it.unifi.stlab.faultflow.analysis.PetriNetAnalyzer;
import it.unifi.stlab.faultflow.analysis.PetriNetReducer;
import it.unifi.stlab.faultflow.exporter.PetriNetExportMethod;
import it.unifi.stlab.faultflow.exporter.PetriNetExporter;
import it.unifi.stlab.faultflow.launcher.builders.FlightControlSystemBuilder;
import it.unifi.stlab.faultflow.model.knowledge.composition.System;
import it.unifi.stlab.faultflow.model.knowledge.propagation.ErrorMode;
import it.unifi.stlab.faultflow.translator.PetriNetTranslator;
import it.unifi.hierarchical.analysis.NumericalValues;
import it.unifi.hierarchical.model.HSMP;
import it.unifi.stlab.transformation.HSMPParser;
import it.unifi.stlab.transformation.TreeParser;
import it.unifi.hierarchical.analysis.HierarchicalSMPAnalysis;
import it.unifi.stlab.transformation.minimalcutset.ImportanceMeasure;
import org.oristool.models.stpn.RewardRate;
import org.oristool.models.stpn.TransientSolution;
import org.oristool.models.stpn.trees.DeterministicEnablingState;

import javax.xml.bind.JAXBException;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

public class ExamplesLauncher {

    public static void main(String[] args) throws JAXBException, IOException {
        //Per cambiare sistema da buildare è sufficiente modificare l'istruzione seguente
        //con il builder del sistema desiderato.
        System s = FlightControlSystemBuilder.getInstance().getSystem();
        //Exporting petri net as fault injection -faults with deterministic occurrence sampled from the pdfs
        PetriNetExporter.exportPetriNetFromSystem(s, PetriNetExportMethod.FAULT_INJECTION);
        //Exporting petri net as fault analysis -faults with their original pdf
        PetriNetTranslator pnt = PetriNetExporter.exportPetriNetFromSystem(s, PetriNetExportMethod.FAULT_ANALYSIS);

        //ANALYSIS WITH SIRIO

        //Reduce Petri Net
        PetriNetReducer petriNetReducer = new PetriNetReducer(pnt.getPetriNet(), pnt.getMarking());
        String failureName = "Asyml1Failure";
        petriNetReducer.reduce(failureName, FlightControlSystemBuilder.getPropagationPorts(), FlightControlSystemBuilder.getErrorModes());
        //Analyze reduced Petri Net
        PetriNetAnalyzer petriNetAnalyzer = new PetriNetAnalyzer(petriNetReducer.getPetriNet(), petriNetReducer.getMarking());
        String exoFaultName = "Asyml1fail";
        String error = "0.1";
        Date start = new Date();
        TransientSolution<DeterministicEnablingState, RewardRate> rewards =
                petriNetAnalyzer.regenerativeTransient(exoFaultName, new BigDecimal(200000), new BigDecimal(200), new BigDecimal(error));
        Date end = new Date();
        long time = end.getTime() - start.getTime();
        java.lang.System.out.println("Elapsed analysis time with Sirio: "+time+" ms");
        //Get results
        FileWriter writer = new FileWriter("export/result-sirio.csv");
        writer.append("Analysis Result of failure "+ failureName+"\n");
        writer.append("Time: "+rewards.getTimeLimit().toString()+", Step: "+rewards.getStep().toString()+", Error: "+error+"\n");
        writer.append("Values: \n");
        for (int index = 0; index < rewards.getSolution().length; index++) {
            writer.append(String.valueOf(rewards.getSolution()[index][0][0]));
            writer.append("\n");
        }
        writer.close();

        //TRANSLATE SYSTEM TO HSMP AND RUN ANALYSIS WITH PYRAMIS

        TreeParser treeParser = new TreeParser(s);
        ErrorMode errorMode = FlightControlSystemBuilder.getErrorMode("lefOR1");
        HSMP hsmp = HSMPParser.parseTree(treeParser.createTree(errorMode));
        HierarchicalSMPAnalysis analysis = new HierarchicalSMPAnalysis(hsmp, 0);
        start = new Date();
        analysis.evaluate(rewards.getStep().doubleValue(), rewards.getTimeLimit().doubleValue());
        end = new Date();
        time = end.getTime() - start.getTime();
        java.lang.System.out.println("Elapsed analysis time with Pyramis: "+time+" ms");
        writer = new FileWriter("export/result-pyramis.csv");
        writer.append("Analysis Result of failure "+ errorMode.getOutgoingFailure().getDescription()+"\n");
        writer.append("Time: "+rewards.getTimeLimit().doubleValue()+", Step: "+rewards.getStep().doubleValue()+"\n");
        NumericalValues cdf = HierarchicalSMPAnalysis.cdf;
        writer.append("Values: \n");

        for (double v : cdf.getValues()) {
            writer.append(String.valueOf(v));
            writer.append("\n");
        }
        writer.close();

        ImportanceMeasure importanceMeasure = new ImportanceMeasure();
        double timeStep = 200;
        int timeAnalysis = 200000;
        writer = new FileWriter("export/fusselvesely.csv");
        writer.append("Fussel Vesely Importance Measure Result of Errormode "+ errorMode.getName()+"\n");
        writer.append("Time: "+timeAnalysis+", Step: "+timeStep+"\n");
        writer.append("Values: \n");
        Map<String, double[]> fusselvesely = importanceMeasure.getImportanceMeasure(s, errorMode, "fusselvesely", timeStep, timeAnalysis);
        for (Map.Entry<String, double[]> entry : fusselvesely.entrySet()) {
            writer.append(entry.getKey()+": "+ Arrays.toString(entry.getValue()));
            writer.append("\n");
        }
        writer.close();

        writer = new FileWriter("export/birnbaum.csv");
        writer.append("Birnbaum Importance Measure Result of Errormode "+ errorMode.getName()+"\n");
        writer.append("Time: "+timeAnalysis+", Step: "+timeStep+"\n");
        writer.append("Values: \n");
        Map<String, double[]> birnbaum = importanceMeasure.getImportanceMeasure(s, errorMode, "birnbaum", timeStep, timeAnalysis);
        for (Map.Entry<String, double[]> entry : birnbaum.entrySet()) {
            writer.append(entry.getKey()+": "+ Arrays.toString(entry.getValue()));
            writer.append("\n");
        }
        writer.close();
    }
}
