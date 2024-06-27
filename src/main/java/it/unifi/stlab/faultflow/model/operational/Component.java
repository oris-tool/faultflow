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

package it.unifi.stlab.faultflow.model.operational;

import it.unifi.stlab.faultflow.model.knowledge.composition.ComponentType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "concrete_components")
public class Component {

    @Id
    private String serial;

    @OneToMany
    private List<Fault> faultList;

    @ManyToOne
    private ComponentType componentType;

    public Component() {
        faultList = new ArrayList<>();
    }

    /**
     * Create a Component by giving its serial number (which must be unique) and its component type.
     *
     * @param serial        a string that describes the serial number of the component.
     * @param componentType a reference to MetaComponent that expresses the object's Component type.
     */
    public Component(String serial, ComponentType componentType) {
        this();
        this.serial = serial;
        this.componentType = componentType;
    }

    public String getSerial() {
        return this.serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public ComponentType getComponentType() {
        return this.componentType;
    }

    public void setComponentType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public List<Fault> getFaultList() {
        return faultList;
    }

    public void setFaultList(List<Fault> faultList) {
        this.faultList = faultList;
    }

    public void addFault(Fault fault) {
        this.faultList.add(fault);
    }

    public void removeFault(Fault fault) {
        if (faultList.contains(fault))
            this.faultList.remove(fault);
        else
            throw new IllegalArgumentException("No such failure inside the list");
    }

    /**
     * Check if the component is already Failed with the same Failure.
     * This means that if, for example, the Component A has already had a Failure of type Electric Defect, can't
     * fail again with the same failure, but can, instead, have another Failure of type Mechanic Defect.
     *
     * @param failDescription the description of the Failure. It must be unique.
     * @return a boolean value that's true if the component is already failed, else it's false.
     */
    public boolean isFailureAlreadyOccurred(String failDescription) {
        return faultList.stream().anyMatch(failure -> failure.getDescription().equals(failDescription));
    }
}
