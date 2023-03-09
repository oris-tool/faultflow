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

import it.unifi.stlab.faultflow.model.knowledge.composition.Component;
import it.unifi.stlab.faultflow.model.knowledge.composition.CompositionPort;
import it.unifi.stlab.faultflow.model.knowledge.propagation.ErrorMode;
import it.unifi.stlab.faultflow.model.knowledge.propagation.PropagationPort;

import java.util.ArrayList;
import java.util.List;

public class ComponentDto {

    private final String uuid;
    private final String name;
    private final List<CompositionPortDto> compositionPorts;
    private final List<ErrorModeDto> errorModes;
    private final List<PropagationPortDto> propagationPorts;

    public ComponentDto(Component component) {
        uuid = component.getUuid();
        name = component.getName();
        compositionPorts = new ArrayList<>();
        errorModes = new ArrayList<>();
        propagationPorts = new ArrayList<>();

        for (CompositionPort compositionPort : component.getChildren()) {
            compositionPorts.add(new CompositionPortDto(compositionPort));
        }

        for (ErrorMode errorMode : component.getErrorModes()) {
            errorModes.add(new ErrorModeDto(errorMode));
        }

        for (PropagationPort propagationPort : component.getPropagationPorts()) {
            propagationPorts.add(new PropagationPortDto(propagationPort));
        }
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public List<CompositionPortDto> getCompositionPorts() {
        return compositionPorts;
    }

    public List<ErrorModeDto> getErrorModes() {
        return errorModes;
    }

    public List<PropagationPortDto> getPropagationPorts() {
        return propagationPorts;
    }
}
