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
 * K-out-of-N operator, used in the definition of boolean expressions for error mode activation conditions, that
 * represents the K-out-of-N logical operator, which evaluates true if at least k of its n sub-elements are true.
 */
public class KofN extends Operator {

    /**
     * Parameter that indicates the minimum number of sub-elements that need to be true for the operator to evaluate
     * true
     */
    private final int k;
    /**
     * Parameter that indicates the total number of sub-elements that are contained in the operator
     */
    private final int n;

    /**
     * Default constructor of the K-out-of-N operator
     *
     * @param k minimum number of sub-elements that need to be true for the operator to evaluate true
     * @param n total number of sub-elements that are contained in the operator
     */
    public KofN(int k, int n) {
        if (k > n || k <= 0)
            throw new IllegalArgumentException("K can't be negative, zero, or > N");
        else {
            this.k = k;
            this.n = n;
            this.elements = new ArrayList<>();
        }
    }

    /**
     * Method used to add a boolean expression as a sub-element in the K-out-of-N formula
     *
     * @param booleanExpression a {@link BooleanExpression} instance that composes the K-out-of-N formula
     */
    public void addChild(BooleanExpression booleanExpression) {
        if (this.elements.size() < n)
            elements.add(booleanExpression);
        else
            throw new IllegalCallerException("Too many elements: there are already " + n + " FailureModes");

    }

    /**
     * Method that calculates the boolean expression in a recursive way by calling the compute() method in every
     * child of the operator.
     *
     * @return a boolean value, returns true if k of its n children have been set to true, false otherwise
     */
    @Override
    public boolean compute() {
        int sum = 0;
        for (BooleanExpression fm : this.elements)
            if (fm.compute())
                sum++;
        return sum >= k;
    }

    /**
     * Method used to export the K-out-of-N formula as a string
     *
     * @return a {@link String} that represents a literal conversion of the formula
     */
    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        if (k == 1) {
            res.append(this.elements.get(0).toString());
            for (int i = 1; i < this.n; i++) {
                res.append("||").append(this.elements.get(i).toString()).append("||");
            }
        } else if (k == n) {
            res.append(this.elements.get(0).toString());
            for (int i = 1; i < this.n; i++) {
                res.append("&&").append(this.elements.get(i).toString());
            }
        } else {
            for (int i = 0; i <= n - k; i++) {
                BooleanExpression first = elements.get(i);
                for (int z = i + 1; z <= n - k + 1; z++) {
                    res.append("(");
                    res.append(first.toString());
                    BooleanExpression next;
                    for (int j = 0; j < k - 1; j++) {
                        next = elements.get(z + j);
                        res.append("&&").append(next.toString());
                    }
                    res.append(")");
                    res.append("||");
                }
            }
            res = new StringBuilder(res.substring(0, res.length() - 2));
        }
        return res.toString();
    }

    /**
     * Method used to export the K-out-of-N formula as a simple string
     *
     * @return a {@link String} that represents a literal conversion of the formula
     */
    @Override
    public String toSimpleString() {
        StringBuilder res = new StringBuilder();
        res.append(this.k).append("/").append(this.n).append("(");
        for (BooleanExpression faultMode : this.elements) {
            res.append(faultMode.toSimpleString()).append(",");
        }
        res = new StringBuilder(res.substring(0, res.length() - 1));
        res.append(")");
        return res.toString();
    }

    @Override
    public String toBracketFormat() {
        StringBuilder res = new StringBuilder();
        if (k == 1) {
            res.append("(").append(this.elements.get(0).toBracketFormat()).append(")");
            for (int i = 1; i < this.n; i++) {
                res.append("||").append("(").append(this.elements.get(i).toBracketFormat()).append(")");
            }
        } else if (k == n) {
            res.append("(").append(this.elements.get(0).toBracketFormat()).append(")");
            for (int i = 1; i < this.n; i++) {
                res.append("&&").append("(").append(this.elements.get(i).toBracketFormat()).append(")");
            }
        } else {
            for (int i = 0; i <= n - k; i++) {
                BooleanExpression first = elements.get(i);
                for (int z = i + 1; z <= n - k + 1; z++) {
                    res.append("(");
                    res.append("(").append(first.toBracketFormat()).append(")");
                    BooleanExpression next;
                    for (int j = 0; j < k - 1; j++) {
                        next = elements.get(z + j);
                        res.append("&&").append("(").append(next.toBracketFormat()).append(")");
                    }
                    res.append(")");
                    res.append("||");
                }
            }
            res = new StringBuilder(res.substring(0, res.length() - 2));
        }
        return res.toString();
    }

    /**
     * Getter of the k parameter
     *
     * @return an {@link Integer} representing the k parameter
     */
    public int getK() {
        return k;
    }

    /**
     * Getter of the n parameter
     *
     * @return an {@link Integer} representing the n parameter
     */
    public int getN() {
        return n;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KofN operator = (KofN) o;
        return Objects.equals(elements, operator.elements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(elements);
    }
}
