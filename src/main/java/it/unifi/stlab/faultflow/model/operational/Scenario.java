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

import it.unifi.stlab.faultflow.model.knowledge.BaseEntity;
import it.unifi.stlab.faultflow.model.knowledge.composition.SystemType;
import it.unifi.stlab.faultflow.model.knowledge.propagation.ErrorMode;
import it.unifi.stlab.faultflow.model.knowledge.propagation.FaultMode;
import it.unifi.stlab.faultflow.model.knowledge.propagation.InternalFaultMode;
import it.unifi.stlab.faultflow.translator.PetriNetTranslator;
import it.unifi.stlab.faultflow.translator.PetriNetTranslatorMethod;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Class Scenario gives a test scenario in which we want to simulate how
 * a set of incoming faults will propagate into failures in a certain system.
 */
@Entity
@Table(name = "scenarios")
public class Scenario extends BaseEntity {

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_fk")
    private List<Event> incomingEvents;

    @OneToMany
    @JoinTable(name = "scenario_concretecomponents",
            inverseJoinColumns = {@JoinColumn(name = "concretecomponent_serial", referencedColumnName = "serial")})
    private List<Component> components;

    public Scenario() {
        components = new ArrayList<>();
        incomingEvents = new ArrayList<>();
    }

    public Scenario(SystemType system, String serial) {
        this();
        this.components = system.getComponents().stream()
                .map(c -> new Component(c.getName() + serial, c))
                .collect(Collectors.toList());
    }

    public void InitializeScenarioFromSystem() {
        if (this.components != null) {
            for (Component component : this.components) {
                for (ErrorMode errorMode : component.getComponentType().getErrorModes()) {
                    for (FaultMode faultMode : errorMode.getInputFaultModes()) {
                        if (faultMode instanceof InternalFaultMode) {
                            Fault basicEvent = new Fault(faultMode.getName() + "Occurred", (InternalFaultMode) faultMode);
                            addEvent(basicEvent, component);
                        }
                    }
                }
            }
        }
    }

    public void InitializeScenarioFromSystem(SystemType system, String serial) {
        if (this.components == null) {
            this.components = system.getComponents().stream()
                    .map(c -> new Component(c.getName() + serial, c))
                    .collect(Collectors.toList());
        }
        InitializeScenarioFromSystem();
    }

    public void InitializeScenarioFromSystem(SystemType system) {
        InitializeScenarioFromSystem(system, "_Base");
    }

    public void addEvent(Event event, Component component) {
        incomingEvents.add(event);
        component.addFault((Fault) event);
    }

    public void removeEvent(Event event) {
        incomingEvents.remove(event);
    }

    public List<Event> getIncomingEvents() {
        return incomingEvents;
    }

    public void setIncomingEvents(List<Event> incomingEvents) {
        this.incomingEvents = incomingEvents;
    }

    public Map<String, Component> getConcreteComponents() {
        return this.components.stream().collect(Collectors.toMap(Component::getSerial, Function.identity()));
    }

    public void setConcreteComponents(List<Component> components) {
        this.components = components;
    }

    /**
     * In a visitor design pattern way, the Scenario Class accepts a Visit from the PetriNetTranslator class
     * to translate all the information concerning Fault and Failure occurrences and their timestamps into petriNet places
     * and markings
     *
     * @param pnt a PetriNetTranslator instance
     */

    public void accept(PetriNetTranslator pnt) {
        for (Event e : this.incomingEvents) {
            pnt.decorate(e, e.getTimestamp(), PetriNetTranslatorMethod.CONCURRENT);
        }
    }

    public void accept(PetriNetTranslator pnt, PetriNetTranslatorMethod pntMethod) {
        for (Event e : this.incomingEvents) {
            pnt.decorate(e, e.getTimestamp(), pntMethod);
        }
    }

    public void printReport() {
        for (Component component : components) {
            java.lang.System.out.println(
                    "Component: " + component.getSerial() +
                            " of Type: " + component.getComponentType().getName() +
                            " has Faults:");
            for (Fault fault : component.getFaultList()) {
                java.lang.System.out.println(fault.getDescription() + " Occurred at time: " + fault.getTimestamp());
            }
            java.lang.System.out.println();
        }
    }
}
