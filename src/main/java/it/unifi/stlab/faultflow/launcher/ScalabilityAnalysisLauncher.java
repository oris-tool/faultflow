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

import com.google.gson.Gson;
import it.unifi.hierarchical.analysis.HierarchicalSMPAnalysis;
import it.unifi.hierarchical.model.HSMP;
import it.unifi.stlab.faultflow.dto.inputsystemdto.InputSystemDto;
import it.unifi.stlab.faultflow.mapper.FaultTreeMapper;
import it.unifi.stlab.faultflow.mapper.SystemMapper;
import it.unifi.stlab.faultflow.model.knowledge.composition.SystemType;
import it.unifi.stlab.faultflow.model.knowledge.propagation.ErrorMode;
import it.unifi.stlab.transformation.HSMPParser;
import it.unifi.stlab.transformation.TreeParser;
import it.unifi.stlab.transformation.minimalcutset.ImportanceMeasure;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Reproduces the examples shown in section 4.3 in the article. More specifically it calculates the execution times
 * with Pyramis of the analysis of variants of the system of Fig. 6 in the article, with larger SFT depth and
 * larger number of internal faults. The results are shown in Table 2 in the article.
 *
 * It starts by calculating the execution time of the CDF of the top level failure,
 * then it calculates the analysis time of Birnbaum and Fussell-Vesely Importance Measures (printing the Minimal CutSet too).
 */
public class ScalabilityAnalysisLauncher {
    public static void main(String[] args) throws IOException {
        Gson gson = new Gson();
        List<String> files = Arrays.asList("PetroleumSystem", "PetroleumSystem48", "PetroleumSystem96d4", "PetroleumSystem96d5", "PetroleumSystem192");
        double timeLimit = 8000.0;
        double timeStep = 2.0;

        java.lang.System.out.println("CALCULATE CDF WITH PYRAMIS: ");
        for (String file : files) {

            InputSystemDto inputSystemDto = gson.fromJson(new FileReader("examples/" + file + ".json"), InputSystemDto.class);
            SystemType sys = SystemMapper.BddToSystem(inputSystemDto.getBdd());
            FaultTreeMapper.decorateSystem(inputSystemDto.getFaultTree(), sys);

            calculateCDF(sys, timeStep, timeLimit);
        }

        java.lang.System.out.println("\n\nCALCULATE BIRNBAUM IMPORTANCE MEASURE: ");
        for (String file : files) {
            InputSystemDto inputSystemDto = gson.fromJson(new FileReader("examples/" + file + ".json"), InputSystemDto.class);
            SystemType sys = SystemMapper.BddToSystem(inputSystemDto.getBdd());
            FaultTreeMapper.decorateSystem(inputSystemDto.getFaultTree(), sys);

            calculateBirnbaum(sys, timeStep, timeLimit);
        }
        java.lang.System.out.println("\n\nCALCULATE FUSSELL-VESELY IMPORTANCE MEASURE: ");
        for (String file : files) {
            InputSystemDto inputSystemDto = gson.fromJson(new FileReader("examples/" + file + ".json"), InputSystemDto.class);
            SystemType sys = SystemMapper.BddToSystem(inputSystemDto.getBdd());
            FaultTreeMapper.decorateSystem(inputSystemDto.getFaultTree(), sys);

            calculateFussellVesely(sys, timeStep, timeLimit);
        }
    }

    private static void calculateCDF(SystemType sys, double timeStep, double timeLimit) {
        TreeParser treeParser = new TreeParser(sys);
        ErrorMode errorMode = sys.getTopLevelComponent().getErrorModes().get(0);

        HSMP hsmp = HSMPParser.parseTree(treeParser.createTree(errorMode));
        HierarchicalSMPAnalysis analysis = new HierarchicalSMPAnalysis(hsmp, 0);
        Date start = new Date();
        analysis.evaluate(timeStep, timeLimit);
        Date end = new Date();
        long time = end.getTime() - start.getTime();
        java.lang.System.out.println("-- " + sys.getTopLevelComponent().getName() + " model --");
        java.lang.System.out.println("Elapsed analysis time with Pyramis: " + time + " ms\n");

    }

    private static void calculateFussellVesely(SystemType sys, double timeStep, double timeLimit) {
        ErrorMode errorMode = sys.getTopLevelComponent().getErrorModes().get(0);
        ImportanceMeasure importanceMeasure = new ImportanceMeasure();
        java.lang.System.out.println("-- " + sys.getTopLevelComponent().getName() + " model --");
        importanceMeasure.getImportanceMeasure(sys, errorMode, "fusselvesely", timeStep, (int) timeLimit);
        java.lang.System.out.println("\n\n");

    }

    private static void calculateBirnbaum(SystemType sys, double timeStep, double timeLimit) {
        ErrorMode errorMode = sys.getTopLevelComponent().getErrorModes().get(0);
        ImportanceMeasure importanceMeasure = new ImportanceMeasure();
        java.lang.System.out.println("-- " + sys.getTopLevelComponent().getName() + " model --");
        importanceMeasure.getImportanceMeasure(sys, errorMode, "birnbaum", timeStep, (int) timeLimit);
        java.lang.System.out.println("\n");
    }

}
