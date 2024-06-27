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

import it.unifi.stlab.faultflow.launcher.builders.PressureTankSystemBuilder;
import it.unifi.stlab.faultflow.model.knowledge.composition.SystemType;
import it.unifi.stlab.faultflow.model.knowledge.propagation.ErrorMode;

import java.io.IOException;

public class PressureTankAnalysisLauncher {
    public static void main(String[] args) throws IOException {
        //Build Pressure Tank System
        //You can change the instance this way:
        //                      PressureTankSystemBuilder.geFirstExample().getSystem();
        //                      PressureTankSystemBuilder.getSecondExample().getSystem();
        //[...]
        //                      PressureTankSystemBuilder.getSeventhExample().getSystem();
        SystemType s = PressureTankSystemBuilder.getSeventhExample().getSystem();;

        //Analysis parameters
        double timeLimit = 30;
        double timeStep = 0.005;
        //-->ANALYSIS WITH PYRAMIS
        ErrorMode errorMode = PressureTankSystemBuilder.getErrorMode("TE2OR1");
        AnalysisLauncher.pyramisAnalysis(s, errorMode, timeLimit, timeStep);

        //Importance measures parameters
        double imStep = 0.005;
        int timeAnalysis = 30;
        AnalysisLauncher.fussellVesely(s, errorMode, timeAnalysis, imStep);
        AnalysisLauncher.birnbaum(s, errorMode, timeAnalysis, imStep);
    }

}
