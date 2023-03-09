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

package it.unifi.stlab.faultflow.model.knowledge.propagation;

import it.unifi.stlab.faultflow.model.knowledge.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "failuremodes")
public class FailureMode extends BaseEntity {
    private final String description;

    public FailureMode() {
        this.description = "";
    }

    /**
     * Create a FailureMode by saying its description
     *
     * @param description a string describing the FailureMode. Must be unique.
     */
    public FailureMode(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getUuid() {
        return uuid;
    }

    /**
     * Override toString Method inside BooleanExpression.
     *
     * @return A string that describes FailureMode's state in a way that resembles the enabling Functions in a Petri Net.
     */

    public String toString() {
        return this.description + ">0";
    }
}
