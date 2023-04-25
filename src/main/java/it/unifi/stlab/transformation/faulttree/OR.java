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

import it.unifi.stlab.faultflow.model.knowledge.propagation.ErrorMode;

import java.util.List;
import java.util.Objects;

public class OR extends Gate {

    private int id;

    public OR(List<Node> children) {
        Node node;

        for (Node child : children) {
            node = child;
            addChild(node);
        }

        this.gateType = GateType.OR;
        id = lastId;
        lastId++;
    }

    public OR() {
        this.gateType = GateType.OR;
        id = lastId;
        lastId++;
    }

    public OR(List<Node> children, String name) {
        super(name);
        Node node;

        for (Node child : children) {
            node = child;
            addChild(node);
        }

        this.gateType = GateType.OR;
        id = lastId;
        lastId++;
    }

    public OR(ErrorMode errorMode) {
        super(errorMode);
        this.gateType = GateType.OR;
        id = lastId;
        lastId++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OR or = (OR) o;
        return id == or.id;
    }

    public OR(String name) {
        super(name);
    }
}
