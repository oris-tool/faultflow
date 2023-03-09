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

package it.unifi.stlab.transformation.faulttree;

import java.util.ArrayList;
import java.util.List;

/**
 * Visitor used to cut sub-fault trees based on whether the nodes of each gate are present in the minimal cutset or not.
 * This is used in the Fussel-Vesely Importance measure calculation to obtain separate Fault Trees for each minimal
 * cutset, containing only the events that contribute to the top event.
 */
public class TreeCutterVisitor {

    private final List<String> minimalCutSet;

    public TreeCutterVisitor(List<String> minimalCutSet) {
        this.minimalCutSet = minimalCutSet;
    }

    public boolean visit(Node node) {
        if (node instanceof BasicEvent)
            return visit((BasicEvent) node);
        else
            return visit((Gate) node);
    }

    public boolean visit(BasicEvent basicEvent) {
        return minimalCutSet.contains(basicEvent.getFaultMode().getName());
    }

    public boolean visit(Gate gate) {
        if (gate instanceof AND)
            return visit((AND) gate);
        else
            return visit((OR) gate);
    }

    /**
     * Visit method for AND gates. Returns true only if all of its children are present in the cutset, false otherwise.
     * To be noted that there is no possibility of a child of an AND gate to be in a minimal cutset without all other
     * children, so this is just a "quick check".
     */
    public boolean visit(AND and) {
        for (Node node : and.getChildren())
            if (!visit(node))
                return false;

        return true;
    }

    /**
     * Visit method for OR gates. Returns true if at least one of its children is present in the minimal cutset, false
     * otherwise. Also removes all children of the gate that are not present in the minimal cutset.
     */
    public boolean visit(OR or) {
        List<Node> toBeRemoved = new ArrayList<>();

        for (Node node : or.getChildren()) {
            if (!visit(node))
                toBeRemoved.add(node);
        }

        toBeRemoved.forEach(or::removeChild);

        return or.getChildren().size() != 0;
    }
}
