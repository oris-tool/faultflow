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
import it.unifi.stlab.transformation.utils.GraphSearcher;
import it.unifi.stlab.transformation.utils.SearchResult;

import java.util.ArrayList;
import java.util.List;

public abstract class Gate implements Node {

    protected static int lastId = 0;
    protected List<Node> children;
    protected GateType gateType;
    String name;
    private ErrorMode errorMode;
    private Double routingProbability;

    public Gate() {
        children = new ArrayList<>();
        errorMode = null;
    }

    public Gate(ErrorMode errorMode) {
        this();
        this.errorMode = errorMode;
        name = errorMode.getName();
    }

    public Gate(String name) {
        this();
        this.name = name;
    }

    @Override
    public void addChild(Node node) {
        children.add(node);
    }

    @Override
    public void removeChild(Node node) {
        children.remove(node);
    }

    @Override
    public boolean isBasicEvent() {
        return false;
    }

    public List<Node> getChildren() {
        return children;
    }

    public ErrorMode getErrorMode() {
        return errorMode;
    }

    public String getName() {
        return name;
    }

    public GateType getGateType() {
        return gateType;
    }

    public Double getRoutingProbability() {
        return routingProbability;
    }

    public void setRoutingProbability(Double routingProbability) {
        this.routingProbability = routingProbability;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Node copy() {
        Node copied = null;
        if (this.gateType == GateType.AND) {
            copied = new AND(name);
        } else if (this.gateType == GateType.OR) {
            copied = new OR(name);
        } else if (this.gateType == GateType.KoutN) {
            copied = new KOutN(name, ((KOutN) this).getK());
        }

        for (Node n : children) {
            if (n.isBasicEvent()) {
                SearchResult sr = GraphSearcher.getNodeByName(((BasicEvent) n).getDescription(), this);
                if (sr.isPresent()) {
                    copied.addChild(sr.getNode());
                } else {
                    copied.addChild(((BasicEvent) n).copy());
                }
            } else {
                SearchResult sr = GraphSearcher.getNodeByName(((Gate) n).getName(), this);
                if (sr.isPresent()) {
                    copied.addChild(sr.getNode());
                } else {
                    copied.addChild(n.copy());
                }

            }
        }

        return copied;
    }

    public enum GateType {
        AND, OR, KoutN
    }
}
