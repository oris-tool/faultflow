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

package it.unifi.stlab.transformation.utils;

import java.util.Objects;

/**
 * Utility classed used to identify the starting and ending indexes of parentheses in a string representing an
 * activation formula.
 */
public class Interval {

    private int lower;
    private int upper;

    public Interval(int lower, int upper) {
        this.lower = lower;
        this.upper = upper;
    }

    public boolean contains(Interval interval) {
        return lower < interval.lower && upper > interval.upper;
    }

    public int getLower() {
        return lower;
    }

    public void setLower(int lower) {
        this.lower = lower;
    }

    public int getUpper() {
        return upper;
    }

    public void setUpper(int upper) {
        this.upper = upper;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        Interval interval = (Interval) o;
        return lower == interval.lower && upper == interval.upper;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lower, upper);
    }
}
