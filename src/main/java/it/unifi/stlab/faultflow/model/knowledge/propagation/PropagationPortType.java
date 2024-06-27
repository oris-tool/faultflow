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

package it.unifi.stlab.faultflow.model.knowledge.propagation;

import it.unifi.stlab.faultflow.model.knowledge.BaseEntity;
import it.unifi.stlab.faultflow.model.knowledge.composition.ComponentType;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "propagationports")
public class PropagationPortType extends BaseEntity {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "failure_mode_fk")
    private final FailureMode propagatedFailureMode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fault_mode_fk")
    private final ExternalFaultMode externalFaultMode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "component_fk")
    private final ComponentType affectedComponentType;

    private final BigDecimal routingProbability;

    public PropagationPortType() {
        this.externalFaultMode = null;
        this.affectedComponentType = null;
        this.propagatedFailureMode = null;
        this.routingProbability = null;
    }

    /**
     * Add a propagationPort by specifying four parameters: the inputFail already happened, the outputFault in which
     * the inputFail propagates, the MetaComponent affected by this propagation and (optional) the routing probability.
     *
     * @param inFail            the FailureMode that triggers the propagation.
     * @param outFault          the ExternalFaultMode in which the inputFail propagates
     * @param affectedComponentType the MetaComponent affected by the propagation. This means that the outputFault specified
     *                          is one of the FailureModes that could happen inside this metaComponent.
     */
    public PropagationPortType(FailureMode inFail, ExternalFaultMode outFault, ComponentType affectedComponentType) {
        this.propagatedFailureMode = inFail;
        this.externalFaultMode = outFault;
        this.affectedComponentType = affectedComponentType;
        this.routingProbability = BigDecimal.ONE;
    }

    public PropagationPortType(FailureMode inFail, ExternalFaultMode outFault, ComponentType affectedComponentType, BigDecimal routingProbability) {
        this.propagatedFailureMode = inFail;
        this.externalFaultMode = outFault;
        this.affectedComponentType = affectedComponentType;
        this.routingProbability = routingProbability;
    }

    public FailureMode getPropagatedFailureMode() {
        return propagatedFailureMode;
    }

    public ExternalFaultMode getExternalFaultMode() {
        return externalFaultMode;
    }

    public ComponentType getAffectedComponent() {
        return affectedComponentType;
    }

    public BigDecimal getRoutingProbability() {
        return routingProbability;
    }
}
