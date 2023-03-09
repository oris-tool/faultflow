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

package it.unifi.stlab.transformation.minimalcutset;

import it.unifi.stlab.transformation.faulttree.BasicEvent;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class that represent a Minimal Cut Set (MCS).
 *
 * @author Minarelli
 */
public final class MinimalCutSet {
    Set<BasicEvent> cs;

    public MinimalCutSet() {
        cs = new HashSet<>();
    }

    /**
     * Methods that returns the BasicEvent componing this MinimalCutSet.
     * Note that the Basic Event are returned in alphabetcally order, based on their description.
     *
     * @return List of BasicEvemt that compose this MinimalCutSet
     */
    public List<BasicEvent> getCutSet() {
        return cs.stream().sorted(Comparator.comparing(BasicEvent::getDescription)).collect(Collectors.toList());
    }


    /**
     * Method that allow to add a node to the current MCS.
     * A BasicEvent may aooear only once in a MCS, e.g: given a BasicEvent A, the
     * MCS AA is illegal (while the MCS AB is not).
     *
     * @param be The BasicEvent that will be added to this MCS
     * @throws NullPointerException     If the BasicEvent passed is null
     * @throws IllegalArgumentException If an already added
     *                                  BasicEvent is passed.
     */
    public void addNode(BasicEvent be) {
        if (be == null) {
            throw new NullPointerException("Null Node passed");
        }
        if (!cs.add(be)) {
            throw new IllegalArgumentException("A Minimal Cut set cannot contains the same Basic Event twice");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MinimalCutSet other = (MinimalCutSet) obj;
        return cs.equals(other.cs);

    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.cs);
        return hash;
    }

    @Override
    public String toString() {
        return "MinimalCutSet{" + "cs=" + cs + '}';
    }
}
