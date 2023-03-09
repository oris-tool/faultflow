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

package it.unifi.stlab.faultflow.model.knowledge.composition;

import it.unifi.stlab.faultflow.model.knowledge.BaseEntity;

import javax.persistence.*;

/**
 * Associative class used to relate different components in a parent-child compositional hierarchy, where one component
 * can have one or more sub-components.
 */
@Entity
@Table(name = "compositionports")
public class CompositionPort extends BaseEntity {

    /**
     * Component that acts as a parent in the compositional hierarchy
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_fk")
    private Component parent;

    /**
     * Component that acts as a child in the compositional hierarchy
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "child_fk")
    private Component child;

    /**
     * Default constructor that initializes the composition port with null references to parent and child
     */
    public CompositionPort() {
        this.parent = null;
        this.child = null;
    }

    /**
     * Constructor that creates a composition port with the references to the hierarchical structure
     *
     * @param parent {@link Component} in the hierarchical structure of the system that acts as the parent
     * @param child  {@link Component} in the hierarchical structure of the system that acts as the child
     */
    public CompositionPort(Component child, Component parent) {
        this.parent = parent;
        this.child = child;
    }

    /**
     * Getter of the parent component in the compositional hierarchy
     *
     * @return a {@link Component} reference to the parent in the compositional hierarchy
     */
    public Component getParent() {
        return this.parent;
    }

    /**
     * Setter of the parent component in the compositional hierarchy
     *
     * @param parent {@link Component} reference to the parent in the compositional hierarchy
     */
    public void setParent(Component parent) {
        this.parent = parent;
    }

    /**
     * Getter of the child component in the compositional hierarchy
     *
     * @return a {@link Component} reference to the child in the compositional hierarchy
     */
    public Component getChild() {
        return this.child;
    }

    /**
     * Setter of the child component in the compositional hierarchy
     *
     * @param child {@link Component} reference to the child in the compositional hierarchy
     */
    public void setChild(Component child) {
        this.child = child;
    }
}
