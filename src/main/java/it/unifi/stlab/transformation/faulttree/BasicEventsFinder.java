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
 * Utility class used to retrieve a list of basic events given the top event of a fault tree.
 */
public class BasicEventsFinder {

    private final List<BasicEvent> basicEvents;

    public BasicEventsFinder() {
        basicEvents = new ArrayList<>();
    }

    public void visit(Node node) {
        if (node instanceof BasicEvent)
            basicEvents.add((BasicEvent) node);
        else if (node instanceof Gate)
            ((Gate) node).children.forEach(this::visit);
    }

    public List<BasicEvent> getBasicEvents() {
        return basicEvents;
    }
}
