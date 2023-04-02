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
 * AND operator, used in the definition of boolean expressions for error mode activation conditions, that represents the
 * AND logical operator, which evaluates true if all its sub-elements are true.
 */
public class AND extends Operator {

    /**
     * Default constructor that initializes the list of sub-elements
     */
    public AND() {
        this.elements = new ArrayList<>();
    }

    /**
     * Method used to add a boolean expression as a sub-element in the AND formula
     *
     * @param booleanExpression a {@link BooleanExpression} instance that composes the AND formula
     */
    public void addChild(BooleanExpression booleanExpression) {
        elements.add(booleanExpression);
    }

    /**
     * Method that calculates the boolean expression truth value in a recursive way by calling the compute() method in
     * every child of the operator.
     *
     * @return a boolean value, true if every child of the operator is in a true state and false otherwise
     */
    @Override
    public boolean compute() {
        for (BooleanExpression e : elements) {
            if (!e.compute())
                return false;
        }
        return true;
    }

    /**
     * Method used to export the AND formula as a string
     *
     * @return a {@link String} that represents a literal conversion of the formula
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (BooleanExpression be : elements) {
            sb.append(be.toString()).append(")&&(");
        }
        return sb.substring(0, sb.length() - 3);
    }

    /**
     * Method used to export the AND formula as a simple string
     *
     * @return a {@link String} that represents a literal conversion of the formula
     */
    @Override
    public String toSimpleString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (BooleanExpression be : elements) {
            sb.append(be.toSimpleString()).append(")&&(");
        }
        return sb.substring(0, sb.length() - 3);
    }

    @Override
    public String toBracketFormat() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (BooleanExpression be : elements) {
            sb.append(be.toBracketFormat()).append(")&&(");
        }
        return sb.substring(0, sb.length() - 3);    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AND operator = (AND) o;
        return Objects.equals(elements, operator.elements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(elements);
    }
}
