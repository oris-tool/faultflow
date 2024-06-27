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


import it.unifi.stlab.faultflow.model.knowledge.composition.ComponentType;
import it.unifi.stlab.faultflow.model.knowledge.composition.SystemType;

import java.util.ArrayList;
import java.util.List;

public class OutputSystemDto {
    private final String uuid;
    private final String name;
    private final String manufacturer;
    private final String model;
    private final List<ComponentDto> components;
    private final String topLevelComponent;

    public OutputSystemDto(SystemType system) {
        uuid = system.getUuid();
        name = system.getName();
        manufacturer = system.getManufacturer();
        model = system.getModel();
        topLevelComponent = system.getTopLevelComponent().getUuid();
        components = new ArrayList<>();

        for (ComponentType componentType : system.getComponents()) {
            components.add(new ComponentDto(componentType));
        }
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getModel() {
        return model;
    }

    public List<ComponentDto> getComponents() {
        return components;
    }

    public String getTopLevelComponent() {
        return topLevelComponent;
    }

}
