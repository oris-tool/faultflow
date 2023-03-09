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

import it.unifi.stlab.transformation.faulttree.BasicEvent;
import it.unifi.stlab.transformation.faulttree.Node;

import java.util.*;

/**
 * Class that permits to search on the SFT.
 *
 * @author Minarelli
 */
public class GraphSearcher {

    private GraphSearcher() {
    }

    /**
     * Method that given the TopEvent returns all the BasicEvents of the SFT
     *
     * @param top Node representing the Top Event of the SFT
     * @return Lst of all the BasicEvents in the tree
     */
    public static List<BasicEvent> getBasicEvent(Node top) {
        Set<Node> visited = new HashSet<>();
        LinkedList<Node> queue = new LinkedList<>();
        ArrayList<BasicEvent> ret = new ArrayList<>();

        visited.add(top);
        queue.add(top);

        boolean found = false;

        while (!queue.isEmpty() && !found) {
            Node n = queue.poll();
            if (n.isBasicEvent()) {
                visited.add(n);
                ret.add((BasicEvent) n);
            } else {
                for (Node nn : n.getChildren()) {
                    if (!visited.contains(nn)) {
                        queue.add(nn);
                        visited.add(nn);
                    }
                }
            }
        }

        return ret;
    }

    public static SearchResult getNodeByName(String varName, Node top) {
        Set<Node> visited = new HashSet<>();
        LinkedList<Node> queue = new LinkedList<>();

        visited.add(top);
        queue.add(top);
        SearchResult res = new SearchResult(false, null);

        boolean found = false;

        while (!queue.isEmpty() && !found) {
            Node n = queue.poll();
            if (n.toString().equals(varName)) {
                res = new SearchResult(true, n);
                found = true;
            } else {
                if (n.isBasicEvent()) {
                    visited.add(n);
                } else {
                    for (Node nn : n.getChildren()) {
                        if (!visited.contains(nn)) {
                            queue.add(nn);
                            visited.add(nn);
                        }
                    }
                }
            }
        }
        return res;
    }
}


   



