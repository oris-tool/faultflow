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
import it.unifi.stlab.faultflow.model.knowledge.propagation.ErrorMode;
import it.unifi.stlab.faultflow.model.knowledge.propagation.PropagationPortType;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a component, simple or composed by other sub-components, of a System of Systems. It is identified by its
 * UUID and name, and it is characterized by its children, error modes and propagation ports.
 */
@Entity
@Table(name = "components")
public class ComponentType extends BaseEntity {

    /**
     * User-friendly name of the component
     */
    private String name;

    /**
     * Collection of all the error modes that affect the component
     */
    @OneToMany(orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(
            name = "component_errormodes",
            joinColumns = @JoinColumn(name = "component_uuid"),
            inverseJoinColumns = @JoinColumn(name = "errormode_fk")
    )
    private List<ErrorMode> errorModes;

    /**
     * Collection of all the propagations of failures as external fault modes to other components of the system
     */
    @OneToMany(orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(
            name = "component_propagationports",
            joinColumns = @JoinColumn(name = "component_uuid"),
            inverseJoinColumns = @JoinColumn(name = "propagationport_fk")
    )
    private List<PropagationPortType> propagationPortTypes;

    /**
     * Collection of children (sub-components) of the component, identified by their composition ports
     */
    @OneToMany(mappedBy = "parent",
            orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<CompositionPortType> children;

    /**
     * Default constructor that sets an empty name and initializes all the collections of the component
     */
    public ComponentType() {
        this.name = "";
        this.children = new ArrayList<>();
        this.errorModes = new ArrayList<>();
        this.propagationPortTypes = new ArrayList<>();
    }

    /**
     * Constructor that sets the component name and initializes all the collections of the component
     *
     * @param name a {@link String} representing the user-friendly name of the component
     */
    public ComponentType(String name) {
        this.name = name;
        this.children = new ArrayList<>();
        this.errorModes = new ArrayList<>();
        this.propagationPortTypes = new ArrayList<>();
    }

    /**
     * Getter of the name of the component
     *
     * @return a {@link String} representing the user-friendly name of the component
     */
    public String getName() {
        return this.name;
    }

    /**
     * Setter of the name of the component
     *
     * @param name a {@link String} representing the user-friendly name of the component
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter of the collection of composition ports of the component
     *
     * @return a {@link List} of {@link CompositionPortType} instances of the component
     */
    public List<CompositionPortType> getChildren() {
        return this.children;
    }

    /**
     * Setter of the collection of composition port of the component
     *
     * @param children a {@link List} of {@link CompositionPortType} instances of the component
     */
    protected void setChildren(List<CompositionPortType> children) {
        this.children = children;
    }

    /**
     * Getter of the collection of error modes of the component
     *
     * @return a {@link List} of {@link ErrorMode} instances of the component
     */
    public List<ErrorMode> getErrorModes() {
        return errorModes;
    }

    /**
     * Setter of the collection of error modes of the component
     *
     * @param errorModes a {@link List} of {@link ErrorMode} instances of the component
     */
    public void setErrorModes(List<ErrorMode> errorModes) {
        this.errorModes = errorModes;
    }

    /**
     * Getter of the collection of propagation ports of the component
     *
     * @return a {@link List} of {@link PropagationPortType} instances of the component
     */
    public List<PropagationPortType> getPropagationPorts() {
        return propagationPortTypes;
    }

    /**
     * Setter of the collection of propagation ports of the component
     *
     * @param propagationPortTypes a {@link List} of {@link PropagationPortType} instances of the component
     */
    public void setPropagationPorts(List<PropagationPortType> propagationPortTypes) {
        this.propagationPortTypes = propagationPortTypes;
    }

    /**
     * Method that adds one or more error modes to the collection of the component
     *
     * @param errorMode one or more {@link ErrorMode} instances to be added to the component
     */
    public void addErrorMode(ErrorMode... errorMode) {
        this.errorModes.addAll(Arrays.asList(errorMode));
    }

    /**
     * Method that adds one or more propagation ports to the collection of the component
     *
     * @param propagationPortType one or more {@link PropagationPortType} instances to be added to the component
     */
    public void addPropagationPort(PropagationPortType... propagationPortType) {
        this.propagationPortTypes.addAll(Arrays.asList(propagationPortType));
    }

    /**
     * Method that adds one or more composition ports to the collection of the component
     *
     * @param compositionPortType one or more {@link CompositionPortType} instances to be added to the component
     */
    public void addCompositionPorts(CompositionPortType... compositionPortType) {
        this.children.addAll(Arrays.asList(compositionPortType));
    }

    /**
     * Method that checks if the component has an error mode with the specified name
     *
     * @param errorModeName a {@link String} that represents the user-friendly name of the {@link ErrorMode} instance
     *                      to check
     * @return true if there is an error mode with the specified name, false otherwise
     */
    public boolean isErrorModeNamePresent(String errorModeName) {
        return errorModes.stream().anyMatch(x -> x.getName().equalsIgnoreCase(errorModeName));
    }
}
