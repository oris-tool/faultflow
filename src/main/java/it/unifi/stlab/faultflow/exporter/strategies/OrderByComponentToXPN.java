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

package it.unifi.stlab.faultflow.exporter.strategies;

import it.unifi.stlab.faultflow.exporter.xpn.*;
import it.unifi.stlab.faultflow.model.knowledge.composition.ComponentType;
import it.unifi.stlab.faultflow.model.knowledge.composition.SystemType;
import it.unifi.stlab.faultflow.model.knowledge.propagation.ErrorMode;
import it.unifi.stlab.faultflow.model.knowledge.propagation.FaultMode;
import it.unifi.stlab.faultflow.model.knowledge.propagation.PropagationPortType;
import it.unifi.stlab.faultflow.translator.PetriNetTranslator;
import org.oristool.petrinet.Marking;
import org.oristool.petrinet.PetriNet;
import org.oristool.petrinet.Postcondition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Create an export strategy that implements and order between places and transition.
 * Places that represent Failures or ErrorMode that works or happen in the same Component will appear near to each other
 * and far from other places that affect other Components.
 * Also as the BasicExportStrategy do, places and transition connected to each other will appear aligned horizontally (with the same y coordinate)
 */
public class OrderByComponentToXPN implements ExportToXPN {
    private final SystemType system;
    private PetriNet petriNet;
    private Marking marking;

    public OrderByComponentToXPN(SystemType system, PetriNet petriNet, Marking marking) {
        this.system = system;
        this.petriNet = petriNet;
        this.marking = marking;
    }

    @Override
    public TpnEditor translate() {
        ObjectFactory objectFactory = new ObjectFactory();
        TpnEditor tpnEditor = objectFactory.createTpnEditor();
        TPNEntities tpnEntities = objectFactory.createTPNEntities();
        translateOccurrences(system, petriNet, marking, tpnEntities);
        tpnEditor.setTpnEntities(tpnEntities);
        return tpnEditor;
    }

    private void translateOccurrences(SystemType system, PetriNet petriNet, Marking marking, TPNEntities tpnEntities) {
        int x, y;
        y = Y_START;
        HashMap<ComponentType, List<org.oristool.petrinet.Place>> componentPlaces = getOccurrencesOrderedByMetaComponent(system, petriNet);
        for (Map.Entry<ComponentType, List<org.oristool.petrinet.Place>> mapElement : componentPlaces.entrySet()) {

            for (org.oristool.petrinet.Place place : mapElement.getValue()) {
                x = X_START;
                Place occurrPlace = addPlace(tpnEntities, place, marking, x, y);

                x += 70;

                org.oristool.petrinet.Transition transition = petriNet.getTransition(PetriNetTranslator.getTransitionName(place.getName()));
                Transition occurrTransit = addTransition(tpnEntities, transition, x, y);

                addArc(tpnEntities, place.getName(), transition.getName());

                x += X_SPACING;

                Postcondition postcondition = petriNet.getPostconditions(transition).iterator().next();
                Place fault = getPlace(tpnEntities, postcondition.getPlace().getName());
                if(fault != null) {
                    adjustTransitionPosition(occurrTransit, fault.getX()-100, fault.getY()-100);
                    adjustPlacePosition(occurrPlace, occurrTransit.getX()-200, occurrTransit.getY());
                }
                else{
                    if(postcondition.getPlace() != null)
                        fault = addPlace(tpnEntities, postcondition.getPlace(), marking, x, y);
                }

                addArc(tpnEntities, transition.getName(), postcondition.getPlace().getName());

                y += Y_SPACING;
                if (!getErrorModesFromFault(fault, mapElement.getKey()).isEmpty())
                    propagateTranslate(tpnEntities, mapElement.getKey(), petriNet, marking, fault);
            }
            y += 100;
        }
    }

    private void propagateTranslate(TPNEntities tpnEntities, ComponentType componentType, PetriNet petriNet, Marking marking, Place fault) {
        int y = fault.getY();
        int xvalue = 200;
        for (ErrorMode em : getErrorModesFromFault(fault, componentType)) {
            if (!isPlaceInXML(tpnEntities, em.getName()) && petriNet.getPlace(em.getName()) != null) {
                int x = fault.getX() + xvalue;

                addPlace(tpnEntities, petriNet.getPlace(em.getName()), marking, x, y);
                x += xvalue;
                org.oristool.petrinet.Transition transition = petriNet.getTransition(PetriNetTranslator.getTransitionName(em.getOutgoingFailure().getDescription()));
                addTransition(tpnEntities, transition, x, y);

                addArc(tpnEntities, em.getName(), transition.getName());

                x += xvalue;

                Place next = addPlace(tpnEntities, petriNet.getPlace(em.getOutgoingFailure().getDescription()), marking, x, y);
                addArc(tpnEntities, transition.getName(), em.getOutgoingFailure().getDescription());
                List<PropagationPortType> propagationPortTypes = componentType.getPropagationPorts().stream().filter(pp -> pp.getPropagatedFailureMode().getDescription().equals(em.getOutgoingFailure().getDescription())).collect(Collectors.toList());
                if (!propagationPortTypes.isEmpty()) {
                    int i = propagationPortTypes.size()==1?0:1;
                    for (PropagationPortType propagationPortType : propagationPortTypes) {
                        FaultMode exoFault = propagationPortType.getExternalFaultMode();
                        String transitionName = propagationPortType.getPropagatedFailureMode().getDescription()+"toFaults";
                        Transition t = getTransition(tpnEntities, transitionName);

                        if(t==null) {
                            t = addTransition(tpnEntities, petriNet.getTransition(propagationPortType.getPropagatedFailureMode().getDescription() + "toFaults"), next.getX() + 70, next.getY());
                            addArc(tpnEntities, next.getUuid(), t.getUuid());
                        }
                        //routingProbability
                        Transition p = null;
                        if(petriNet.getPlace("Router"+exoFault.getName()) != null)
                        {
                            //create routerPlace
                            org.oristool.petrinet.Place routerPN = petriNet.getPlace("Router"+exoFault.getName());
                            Place router = addPlace(tpnEntities, routerPN, marking, t.getX()+180, t.getY());
                            addArc(tpnEntities, t.getUuid(), router.getUuid());
                            p = addTransition(tpnEntities, petriNet.getTransition(propagationPortType.getRoutingProbability().toString()), router.getX()+180, router.getY());
                            Transition minusp = addTransition(tpnEntities, petriNet.getTransition(""+(1- propagationPortType.getRoutingProbability().doubleValue())), router.getX()+180, router.getY()+45);
                            addArc(tpnEntities, router.getUuid(), p.getUuid());
                            addArc(tpnEntities, router.getUuid(), minusp.getUuid());
                        }

                        Place b = getPlace(tpnEntities, exoFault.getName());
                        if (b == null) {
                            b = addPlace(tpnEntities, petriNet.getPlace(exoFault.getName()), marking, t.getX() + 180,
                                    (t.getY()+(((-1)^i)*45*((i+1)/2))));
                            i++;
                        }
                        else {
                            Place occurPlace = getPlace(tpnEntities, exoFault.getName()+"Occurrence");
                            if(occurPlace != null){
                                adjustOccurrencePlaceTransition(tpnEntities, exoFault.getName(), t.getX()+80, t.getY()-100);
                            }
                            if (b.getY() != fault.getY()) {
                                adjustPlacePosition(b, t.getX() + 180, t.getY());
                            }
                        }
                        if(p == null)
                            addArc(tpnEntities, t.getUuid(), b.getUuid());
                        else {
                            adjustPlacePosition(b, p.getX() + 180, p.getY());
                            addArc(tpnEntities, p.getUuid(), b.getUuid());
                        }
                        if (!getErrorModesFromFault(b, propagationPortType.getAffectedComponent()).isEmpty())
                            propagateTranslate(tpnEntities, propagationPortType.getAffectedComponent(), petriNet, marking, b);

                    }
                }
                y += Y_SPACING;
            }
        }
    }
    private HashMap<ComponentType, List<org.oristool.petrinet.Place>> getOccurrencesOrderedByMetaComponent(SystemType system, PetriNet petrinet) {
        HashMap<ComponentType, List<org.oristool.petrinet.Place>> componentPlaces = new HashMap<>();
        for (ComponentType componentType : system.getComponents()) {
            for (ErrorMode errorMode : componentType.getErrorModes()) {
                for (FaultMode faultMode : errorMode.getInputFaultModes()) {
                    org.oristool.petrinet.Place place = petrinet.getPlace(faultMode.getName() + "Occurrence");
                    if (place != null) {
                        if (componentPlaces.get(componentType) == null)
                            componentPlaces.computeIfAbsent(componentType, k -> new ArrayList<>()).add(place);
                        else {
                            if (!componentPlaces.get(componentType).contains(place))
                                componentPlaces.get(componentType).add(place);
                        }
                    }
                }
                //check failures too
                org.oristool.petrinet.Place place = petrinet.getPlace(errorMode.getOutgoingFailure().getDescription() + "Occurrence");
                if (place != null) {
                    if (componentPlaces.get(componentType) == null)
                        componentPlaces.computeIfAbsent(componentType, k -> new ArrayList<>()).add(place);
                    else {
                        if (!componentPlaces.get(componentType).contains(place))
                            componentPlaces.get(componentType).add(place);
                    }
                }
            }
        }
        return componentPlaces;
    }
    private void adjustOccurrencePlaceTransition(TPNEntities tpnEntities, String placeToMove, int newX, int newY){
        Transition occurrTransition = getTransition(tpnEntities, PetriNetTranslator.getTransitionName(placeToMove+"Occurrence"));
        adjustTransitionPosition(occurrTransition, newX, newY);
        Place occurrPlace = getPlace(tpnEntities, placeToMove+"Occurrence");
        adjustPlacePosition(occurrPlace, occurrTransition.getX()-200, occurrTransition.getY());
    }

    private List<ErrorMode> getErrorModesFromFault(Place fault, ComponentType componentType) {
        return componentType.getErrorModes().stream().filter(x -> x.checkFaultIsPresent(fault.getUuid())).collect(Collectors.toList());
    }

}
