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

import java.util.ArrayList;
import java.util.Objects;

/**
 * NOT operator, used in the definition of boolean expressions for error mode activation conditions, that represents the
 * NOT logical operator, which evaluates true if its only sub-element is false.
 */
public class NOT extends Operator {

    /**
     * Default constructor that initializes the list of sub-elements
     */
    public NOT() {
        this.elements = new ArrayList<>();
    }

    /**
     * Method used to add a boolean expression as a sub-element in the NOT formula: it is a unary operator,
     * so it must have just one element into its elements list. This means that when it's requested to add another
     * child to the same NOT operator, first it deletes the element it already has then it adds the new one.
     *
     * @param booleanExpression a {@link BooleanExpression} instance that composes the NOT formula
     */
    public void addChild(BooleanExpression booleanExpression) {

        if (this.elements.size() != 0)
            this.removeChild(this.elements.get(0));
        elements.add(booleanExpression);
    }

    /**
     * Method that calculates the boolean expression in a recursive way by calling the compute() method in every
     * child of the operator.
     *
     * @return a boolean value, true if the child of the operator is in a false state and true otherwise
     */
    @Override
    public boolean compute() {
        return !elements.get(0).compute();
    }

    /**
     * Method used to export the NOT formula as a string
     *
     * @return a {@link String} that represents a literal conversion of the formula
     */
    @Override
    public String toString() {
        return "!" + elements.get(0).toString();
    }

    /**
     * Method used to export the NOT formula as a simple string
     *
     * @return a {@link String} that represents a literal conversion of the formula
     */
    @Override
    public String toSimpleString() {
        return "!" + elements.get(0).toSimpleString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NOT operator = (NOT) o;
        return Objects.equals(elements, operator.elements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(elements);
    }
}
