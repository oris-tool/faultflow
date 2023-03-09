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

import java.util.List;

public class KOutN extends Gate {

    private final int k;
    private final int id;

    public KOutN(String name, int k) {
        super(name);
        this.k = k;
        this.gateType = GateType.KoutN;
        id = lastId++;
    }

    public KOutN(int k) {
        this.k = k;
        this.gateType = GateType.KoutN;
        id = lastId;
        lastId++;
    }

    public KOutN(List<Node> children, int k) {
        this.k = k;
        Node node;

        for (Node child : children) {
            node = child;
            addChild(node);
        }

        this.gateType = GateType.KoutN;
        id = lastId;
        lastId++;
    }

    public int getK() {
        return k;
    }
}
