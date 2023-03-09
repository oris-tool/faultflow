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

package it.unifi.stlab.faultflow.model.knowledge.propagation.operators;

import it.unifi.stlab.faultflow.model.knowledge.propagation.BooleanExpression;
import it.unifi.stlab.faultflow.model.knowledge.propagation.FaultMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class that represents an operator used in boolean expressions, extended by {@link AND}, {@link KofN},
 * {@link NOT}, {@link OR}.
 */
public abstract class Operator implements BooleanExpression {

    /**
     * Collection of sub-elements of the operator
     */
    protected List<BooleanExpression> elements;

    /**
     * Method that removes a boolean expression from the sub-elements of the operator.
     *
     * @param booleanExpression the operator or FailureMode to be removed
     * @return a boolean expression if it successfully deletes the sub-element, else returns null
     */
    @Override
    public BooleanExpression removeChild(BooleanExpression booleanExpression) {
        for (BooleanExpression elem : elements) {
            if (elem == booleanExpression) {
                elements.remove(elem);
                return booleanExpression;
            } else {
                //check into Operator's children too
                if (elem instanceof Operator)
                    if (elem.removeChild(booleanExpression) != null)
                        return booleanExpression;
            }
        }
        return null;
    }

    /**
     * Method that extracts a list of incoming faults that comprise the operator
     *
     * @return a {@link List} of {@link FaultMode} instances that appear inside the boolean expression
     */
    public List<FaultMode> extractIncomingFaults() {
        List<FaultMode> incomingFails = new ArrayList<>();
        for (BooleanExpression be : elements) {
            if (be instanceof FaultMode)
                incomingFails.add((FaultMode) be);
            else {
                incomingFails.addAll(be.extractIncomingFaults());
            }
        }
        return incomingFails;
    }

    /**
     * Getter of the collection of sub-elements that comprise the operator
     *
     * @return a {@link List} of {@link BooleanExpression} sub-instances that comprise the boolean expression
     */
    public List<BooleanExpression> getElements() {
        return elements;
    }
}
