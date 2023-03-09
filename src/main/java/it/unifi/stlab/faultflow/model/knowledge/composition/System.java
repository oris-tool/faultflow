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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Represents the top-level system of systems, identified by a UUID, a name, its manufacturer and its model.
 * It is the top of the components hierarchy that composes the SoS.
 */
@Entity
@Table(name = "systems")
public class System extends BaseEntity {

    /**
     * User-friendly name of the system
     */
    private String name;

    /**
     * User-friendly name of the manufacturer of the system
     */
    private String manufacturer;

    /**
     * User-friendly string that represents the name of the model of the system
     */
    private String model;

    /**
     * Collection of all the components that form the System of Systems
     */
    @OneToMany(orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinTable(
            name = "system_components",
            joinColumns = @JoinColumn(name = "system_uuid"),
            inverseJoinColumns = @JoinColumn(name = "component_fk")
    )
    private List<Component> components;

    /**
     * Top-level component from which the sub-components hierarchy starts
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "top_level_component_fk")
    private Component topLevelComponent;

    /**
     * Default constructor that sets a default name and an empty list of components
     */
    public System() {
        this.name = "nd";
        this.components = new ArrayList<>();
    }

    /**
     * Constructor that sets the name of the system
     *
     * @param name a {@link String} representing the user-friendly name of the system
     */
    public System(String name) {
        this.name = name;
        this.components = new ArrayList<>();
    }

    /**
     * Complete constructor that sets the name, the manufacturer and the model of the system
     *
     * @param name         a {@link String} representing the user-friendly name of the system
     * @param manufacturer a {@link String} representing the user-friendly name or identifier of the manufacturer of the
     *                     system
     * @param model        a {@link String} representing the name, serial or identifier of the model of the system
     */
    public System(String name, String manufacturer, String model) {
        this(name);
        this.manufacturer = manufacturer;
        this.model = model;
    }

    /**
     * Getter of the name of the system
     *
     * @return a {@link String} representing the user-friendly name of the system
     */
    public String getName() {
        return name;
    }

    /**
     * Setter of the name of the system
     *
     * @param name a {@link String} representing the user-friendly name of the system
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter of the manufacturer of the system
     *
     * @return a {@link String} representing the user-friendly name or identifier of the manufacturer of the system
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * Setter of the manufacturer of the system
     *
     * @param manufacturer a {@link String} representing a user-friendly name or identifier of the manufacturer of the system
     */
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    /**
     * Getter of the model of the system
     *
     * @return a {@link String} representing the name, serial or identifier of the model of the system
     */
    public String getModel() {
        return model;
    }

    /**
     * Setter of the model of the system
     *
     * @param model a {@link String} representing the name, serial or identifier of the model of the system
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Getter of the collection of components in the system
     *
     * @return a {@link List} of components in the system
     */
    public List<Component> getComponents() {
        return components;
    }

    /**
     * Setter of the list of components in the system
     *
     * @param components a {@link List} of components in the system
     */
    public void setComponents(List<Component> components) {
        this.components = components;
    }

    /**
     * Getter of the top-level component of the system
     *
     * @return the top-level {@link Component} instance of the system
     */
    public Component getTopLevelComponent() {
        return topLevelComponent;
    }

    /**
     * Setter of the top-level component of the system
     *
     * @param component the top-level {@link Component} instance of the system
     * @throws IllegalArgumentException if the specified top-level component is not contained in the system
     */
    public void setTopLevelComponent(Component component) throws IllegalArgumentException {
        //assure that top level component it's inside the list of components that make up the system
        if (components.contains(component))
            this.topLevelComponent = component;
        else
            throw new IllegalArgumentException("Top level component must be inside the system");
    }

    /**
     * Method that adds a {@link Component} instance to the system
     *
     * @param components one or more {@link Component} instances to be added to the system
     */
    public void addComponent(Component... components) {
        this.components.addAll(Arrays.asList(components));
    }

    /**
     * Utility method used to retrieve a {@link Component} instance from the system using its name
     *
     * @param componentName a {@link String} representing the user-friendly name of the component
     * @return a {@link Component} instance with the specified name
     * @throws NoSuchElementException if there is no component with the specified name
     */
    @Transient
    public Component getComponent(String componentName) throws NoSuchElementException {
        return components.stream().filter(x -> x.getName().equals(componentName)).findFirst().orElseThrow();
    }
}
