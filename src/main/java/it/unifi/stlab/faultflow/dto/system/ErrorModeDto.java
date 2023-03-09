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

package it.unifi.stlab.faultflow.dto.system;

import it.unifi.stlab.faultflow.model.knowledge.propagation.ErrorMode;
import it.unifi.stlab.faultflow.model.knowledge.propagation.FaultMode;

import java.util.ArrayList;
import java.util.List;

public class ErrorModeDto {

    private final String uuid;
    private final String name;
    private final String activationFunction;
    private final String outgoingFailure;
    private final List<FaultModeDto> inputFaultModes;
    private final String timetofailurePDF;

    public ErrorModeDto(ErrorMode errorMode) {
        uuid = errorMode.getUuid();
        name = errorMode.getName();
        activationFunction = errorMode.getActivationFunction().toSimpleString();
        outgoingFailure = errorMode.getOutgoingFailure().getUuid();
        timetofailurePDF = errorMode.getTimetofailurePDFToString();
        inputFaultModes = new ArrayList<>();

        for (FaultMode faultMode : errorMode.getInputFaultModes()) {
            inputFaultModes.add(new FaultModeDto(faultMode));
        }
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getActivationFunction() {
        return activationFunction;
    }

    public String getOutgoingFailure() {
        return outgoingFailure;
    }

    public List<FaultModeDto> getInputFaultModes() {
        return inputFaultModes;
    }

    public String getTimetofailurePDF() {
        return timetofailurePDF;
    }
}
