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

import it.unifi.stlab.faultflow.model.knowledge.propagation.PropagationPortType;

public class PropagationPortDto {

    private final String uuid;
    private final String propagatedFailureMode;
    private final String externalFaultMode;
    private final String affectedComponent;
    private final double routingProbability;

    public PropagationPortDto(PropagationPortType propagationPortType) {
        uuid = propagationPortType.getUuid();
        propagatedFailureMode = propagationPortType.getPropagatedFailureMode().getUuid();
        externalFaultMode = propagationPortType.getExternalFaultMode().getUuid();
        affectedComponent = propagationPortType.getAffectedComponent().getUuid();
        routingProbability = propagationPortType.getRoutingProbability().doubleValue();
    }

    public String getUuid() {
        return uuid;
    }

    public String getPropagatedFailureMode() {
        return propagatedFailureMode;
    }

    public String getExternalFaultMode() {
        return externalFaultMode;
    }

    public String getAffectedComponent() {
        return affectedComponent;
    }

    public double getRoutingProbability() {
        return routingProbability;
    }
}
