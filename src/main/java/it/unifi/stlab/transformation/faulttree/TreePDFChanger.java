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

import it.unifi.stlab.faultflow.model.knowledge.propagation.FaultMode;
import it.unifi.stlab.faultflow.model.knowledge.propagation.InternalFaultMode;

import java.util.ArrayDeque;
import java.util.Queue;

public class TreePDFChanger {

    public static FaultMode changeFaultMode(Node tree, String nodeName, InternalFaultMode newFaultMode) {
        Queue<Node> nodes = new ArrayDeque<>();
        nodes.add(tree);
        FaultMode oldFaultMode = null;

        while (!nodes.isEmpty()) {
            Node node = nodes.poll();

            if (node instanceof BasicEvent) {
                if (((BasicEvent) node).getFaultMode().getName().equals(nodeName)) {
                    oldFaultMode = ((BasicEvent) node).getFaultMode();
                    ((BasicEvent) node).setFaultMode(newFaultMode);
                }
            } else {
                nodes.addAll(node.getChildren());
            }
        }

        return oldFaultMode;
    }
}
