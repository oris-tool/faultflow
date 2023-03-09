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

package it.unifi.stlab.faultflow.mapper;

import it.unifi.stlab.faultflow.dto.inputsystemdto.bdd.InputBddDto;
import it.unifi.stlab.faultflow.dto.inputsystemdto.bdd.InputBlockDto;
import it.unifi.stlab.faultflow.dto.inputsystemdto.bdd.InputParentingDto;
import it.unifi.stlab.faultflow.dto.system.OutputSystemDto;
import it.unifi.stlab.faultflow.model.knowledge.composition.Component;
import it.unifi.stlab.faultflow.model.knowledge.composition.CompositionPort;
import it.unifi.stlab.faultflow.model.knowledge.composition.System;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SystemMapper {
    public SystemMapper() {
    }

    public static System BddToSystem(InputBddDto inputBddDto){
        System bddSystem = new System();
        Map<String, String> idToNames = new HashMap<>();
        for(InputBlockDto block: inputBddDto.getBlocks()){
            Component component = new Component(block.getName());
            bddSystem.addComponent(component);
            idToNames.put(block.getExternalId(), block.getName());
        }
        Component topLevelComponent = bddSystem.getComponent(idToNames.get(inputBddDto.getRootId()));
        bddSystem.setTopLevelComponent(topLevelComponent);
        bddSystem.setName(topLevelComponent.getName()+"_System");
        Map<String, List<InputParentingDto>> orderByParent = inputBddDto.getParentings().stream()
                .collect(Collectors.groupingBy(InputParentingDto::getParentId));
        for(Map.Entry<String, List<InputParentingDto>> entry : orderByParent.entrySet()){
            Component mainComponent = bddSystem.getComponent(idToNames.get(entry.getKey()));
            for(InputParentingDto inputParentingDto: entry.getValue()){
                Component child = bddSystem.getComponent(idToNames.get(inputParentingDto.getChildId()));
                CompositionPort compositionPort = new CompositionPort(child, mainComponent);
                mainComponent.addCompositionPorts(compositionPort);
            }
        }
        return bddSystem;
    }
    public static OutputSystemDto systemToOutputSystem(System system){
        return new OutputSystemDto(system);
    }
}
