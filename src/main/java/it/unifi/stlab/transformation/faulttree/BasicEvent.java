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

import it.unifi.stlab.faultflow.model.knowledge.propagation.InternalFaultMode;

import java.util.List;
import java.util.Objects;

public class BasicEvent implements Node {

    private static int lastId = 0;
    private final String description;
    private final int id;
    private InternalFaultMode faultMode;

    public BasicEvent(String description) {
        this.description = description;
        id = lastId;
        lastId++;
    }

    public BasicEvent(String description, int id) {
        this.description = description;
        this.id = id;
    }

    public BasicEvent(InternalFaultMode faultMode) {
        this(faultMode.getName());
        this.faultMode = faultMode;
    }

    public InternalFaultMode getFaultMode() {
        return faultMode;
    }

    public void setFaultMode(InternalFaultMode faultMode) {
        this.faultMode = faultMode;
    }

    @Override
    public void addChild(Node node) {
        throw new UnsupportedOperationException("Child operation on leaf object.");
    }

    @Override
    public void removeChild(Node node) {
        throw new UnsupportedOperationException("Child operation on leaf object.");
    }

    @Override
    public boolean isBasicEvent() {
        return true;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BasicEvent that = (BasicEvent) o;
        return id == that.id ;
    }
    @Override
    public BasicEvent copy() {
        BasicEvent copied = new BasicEvent(description, id);
        return copied;
    }

    @Override
    public List<Node> getChildren() {
        throw new UnsupportedOperationException("Child operation on leaf object.");
    }

    @Override
    public String toString() {
        return description;
    }
}
