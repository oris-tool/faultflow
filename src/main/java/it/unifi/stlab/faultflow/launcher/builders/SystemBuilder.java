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

package it.unifi.stlab.faultflow.launcher.builders;

import it.unifi.stlab.faultflow.model.knowledge.composition.SystemType;
import it.unifi.stlab.faultflow.model.knowledge.propagation.ErrorMode;
import it.unifi.stlab.faultflow.model.knowledge.propagation.FailureMode;
import it.unifi.stlab.faultflow.model.knowledge.propagation.FaultMode;
import it.unifi.stlab.faultflow.model.knowledge.propagation.PropagationPortType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class SystemBuilder {
    protected static SystemBuilder single_instance;
    protected static SystemType system;
    protected static HashMap<String, FaultMode> faultModes;
    protected static HashMap<String, FailureMode> failureModes;
    protected static HashMap<String, ErrorMode> errorModes;
    protected static List<PropagationPortType> propagationPortTypes;

    public SystemType getSystem() {
        return system;
    }

    public HashMap<String, FaultMode> getFaultModes() {
        return faultModes;
    }

    public static List<PropagationPortType> getPropagationPorts() {
        return propagationPortTypes;
    }

    public static List<ErrorMode> getErrorModes() {
        return new ArrayList<>(errorModes.values());
    }

    public static ErrorMode getErrorMode(String errorModeName) {
        return errorModes.get(errorModeName);
    }
}
