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

package it.unifi.stlab.faultflow.translator;

import it.unifi.stlab.faultflow.exporter.PetriNetExportMethod;
import it.unifi.stlab.faultflow.model.knowledge.composition.Component;
import it.unifi.stlab.faultflow.model.knowledge.composition.System;
import it.unifi.stlab.faultflow.model.knowledge.propagation.*;
import it.unifi.stlab.faultflow.model.operational.Error;
import it.unifi.stlab.faultflow.model.operational.Event;
import it.unifi.stlab.faultflow.model.operational.Failure;
import it.unifi.stlab.faultflow.model.operational.Fault;
import it.unifi.stlab.faultflow.model.utils.PDFParser;
import org.oristool.models.pn.Priority;
import org.oristool.models.stpn.MarkingExpr;
import org.oristool.models.stpn.trees.StochasticTransitionFeature;
import org.oristool.petrinet.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that implements the method translate that transforms the system OO into a petriNet model.
 * This means translating all the errorModes and FailureMode into places and transitions and connecting them following the
 * propagation ports.
 */
public class PetriNetTranslator implements Translator {
    private final PetriNet net;
    private final Marking marking;
    private String sysName;

    public PetriNetTranslator() {
        net = new PetriNet();
        marking = new Marking();
    }

    /**
     * Simple method that returns the name of a Transition accordingly to the place connected to it. Place->Transition
     * Places' names will begin with a capital letter, transitions' names will start with the first letter in lower case.
     *
     * @param placeName name of the place before the transition. PlaceName->TransitionName
     * @return the name of a Transition accordingly to the place connected to it
     */
    public static String getTransitionName(String placeName) {
        return Character.toString(placeName.charAt(0)).toLowerCase() + placeName.substring(1);
    }

    public void translate(System system, PetriNetExportMethod method) {
        this.sysName = system.getName();
        //First add ErrorModes to the net, thus the ErrorMode and its outgoing failure become places and
        //between them there's a transition with the ErrorMode's enabling function
        Place a, b;
        Transition t;
        int k = 1;
        for (Component component : system.getComponents()) {
            for (ErrorMode e : component.getErrorModes()) {
                //add ErrorMode and its failureMode
                a = net.addPlace(e.getName());
                b = net.addPlace(e.getOutgoingFailure().getDescription());
                t = net.addTransition(getTransitionName(b.getName()));
                t.addFeature(new EnablingFunction(e.getActivationFunction().toString()));
                t.addFeature(PDFParser.parseStringToStochasticTransitionFeature(e.getTimetofailurePDFToString()));
                net.addPrecondition(a, t);
                net.addPostcondition(t, b);
                marking.setTokens(a, 1);

                //add its faultModes

                List<FaultMode> inFaults = e.getInputFaultModes();
                for (FaultMode fault : inFaults) {
                    if (net.getPlace(fault.getName()) == null) {
                        //if fault is not already in the net
                        b = net.addPlace(fault.getName());
                        if (fault instanceof InternalFaultMode) {
                            a = net.addPlace(fault.getName() + "Occurrence");
                            marking.setTokens(a, 1);
                            t = net.addTransition(getTransitionName(a.getName()));
                            if (((InternalFaultMode) fault).getArisingPDFToString() != null) {
                                if (method == PetriNetExportMethod.FAULT_INJECTION) {
                                    BigDecimal sample = PDFParser.generateSample(((InternalFaultMode) fault).getArisingPDFToString());
                                    t.addFeature(StochasticTransitionFeature.newDeterministicInstance(new BigDecimal("" + sample), MarkingExpr.from("1", net)));
                                } else
                                    t.addFeature(PDFParser.parseStringToStochasticTransitionFeature(((InternalFaultMode) fault).getArisingPDFToString()));
                            } else
                                t.addFeature(StochasticTransitionFeature.newDeterministicInstance(new BigDecimal("1"), MarkingExpr.from("1", net)));
                            t.addFeature(new Priority(0));
                            net.addPrecondition(a, t);
                            net.addPostcondition(t, b);
                        }
                    }
                }
            }
            //cycle through propPorts to connect propagatedFailureMode to its externalFaultModeS
            if (!component.getPropagationPorts().isEmpty()) {
                for (PropagationPort pp : component.getPropagationPorts()) {
                    a = net.getPlace(pp.getPropagatedFailureMode().getDescription());
                    b = net.addPlace(pp.getExternalFaultMode().getName());
                    t = net.getTransition(a.getName() + "toFaults");
                    if (t == null) {
                        t = net.addTransition(a.getName() + "toFaults");
                        TransitionFeature tf = t.getFeature(StochasticTransitionFeature.class);
                        if (tf == null) {
                            t.addFeature(StochasticTransitionFeature.newDeterministicInstance(new BigDecimal("0"), MarkingExpr.from("1", net)));
                            t.addFeature(new Priority(0));
                        }
                        net.addPrecondition(a, t);
                    }
                    if (pp.getRoutingProbability().equals(BigDecimal.ONE) || pp.getRoutingProbability().equals(BigDecimal.valueOf(1.0)) || pp.getRoutingProbability().toString().equals("1.00"))
                        net.addPostcondition(t, b);
                    else {
                        Place router = net.addPlace("Router" + b.getName());
                        net.addPostcondition(t, router);
                        Transition p = net.addTransition(pp.getRoutingProbability().toString());
                        p.addFeature(StochasticTransitionFeature.newDeterministicInstance(new BigDecimal("0"), MarkingExpr.from(p.getName(), net)));
                        p.addFeature(new Priority(k));
                        Transition minusp = net.addTransition("" + (1 - pp.getRoutingProbability().doubleValue()));
                        minusp.addFeature(StochasticTransitionFeature.newDeterministicInstance(new BigDecimal("0"), MarkingExpr.from(minusp.getName(), net)));
                        minusp.addFeature(new Priority(k));
                        net.addPrecondition(router, p);
                        net.addPrecondition(router, minusp);
                        net.addPostcondition(p, b);
                        k++;
                    }
                }
            }
        }
    }

    public void translate(System system) {
        translate(system, PetriNetExportMethod.FAULT_ANALYSIS);
    }

    /**
     * Decorate the already instanced petri net with information coming at the Operational Level: this includes
     * -the event (which can be an Error, Fault or Failure) and its timestamp
     * This method adds the timestamp to Fault/Failure occurrences' transitions and
     * decorates Fault/Failure occurrence's places with tokens if they're active.
     * Changes the ErroMode's transition with a deterministic transition if specified.
     *
     * @param event     the instance of an event created at Operational Level in a Scenario, that has to be translated
     *                  into a Place in the PetriNet. This can be a Fault, Error or Failure
     * @param timestamp the moment in which the event is expected to occur. For an error, this parameter specifies
     *                  a different delay (deterministic) in the propagation of the failure.
     */
    public void decorate(Event event, BigDecimal timestamp, PetriNetTranslatorMethod pntMethod) {
        switch (event.getClass().getSimpleName()) {
            case "Error":
                decorateError((Error) event, timestamp);
                break;
            case "Failure":
                decorateFailure((Failure) event, timestamp, pntMethod);
                break;
            default:
                decorateOccurrence((Fault) event, timestamp, pntMethod);
        }
    }

    private void decorateOccurrence(Fault fault, BigDecimal timestamp, PetriNetTranslatorMethod pntMethod) {
        if (fault.getFaultMode() instanceof ExternalFaultMode) {
            decorateExternalFaultOccurrence(fault, pntMethod);
        }
        Place a = net.getPlace((fault.getFaultMode().getName() + "Occurrence"));
        marking.setTokens(a, 1);
        Transition t = net.getTransition(getTransitionName(a.getName()));
        TransitionFeature tf = t.getFeature(StochasticTransitionFeature.class);
        if (tf != null)
            t.removeFeature(StochasticTransitionFeature.class);
        t.addFeature(PDFParser.parseStringToStochasticTransitionFeature("dirac(" + timestamp.toString() + ")"));
    }

    private void decorateExternalFaultOccurrence(Fault fault, PetriNetTranslatorMethod pntMethod) {
        Place a = net.addPlace(fault.getFaultMode().getName() + "Occurrence");
        Transition t = net.addTransition(getTransitionName(a.getName()));
        Place b = net.getPlace(fault.getFaultMode().getName());
        List<Transition> transitionsToEdit = new ArrayList<>();
        for (Transition transition : net.getTransitions()) {
            Postcondition pc = net.getPostcondition(transition, b);
            if (pc != null) {
                transitionsToEdit.add(pc.getTransition());
            }
        }
        switch (pntMethod) {
            case CONCURRENT:
                for (Transition transition : transitionsToEdit) {
                    if (net.getPostconditions(transition).size() > 1) {
                        net.removePostcondition(net.getPostcondition(transition, b));
                        Place a1 = net.addPlace("To" + fault.getFaultMode().getName());
                        net.addPostcondition(transition, a1);
                        Transition t1 = net.addTransition(getTransitionName(fault.getFaultMode().getName()));
                        net.addPrecondition(a1, t1);
                        net.addPostcondition(t1, b);
                        t1.addFeature(new EnablingFunction("(" + b.getName() + "==0)"));
                        t1.addFeature(StochasticTransitionFeature.newDeterministicInstance(new BigDecimal("0"), MarkingExpr.from("1", net)));
                    } else {
                        transition.addFeature(new EnablingFunction("(" + b.getName() + "==0)"));
                    }
                }
                net.addPrecondition(a, t);
                net.addPostcondition(t, b);
                t.addFeature(new EnablingFunction("(" + b.getName() + "==0)"));
                break;
            case DETERMINISTIC:
                List<String> transitionsToDelete = new ArrayList<>();
                for (Transition transition : transitionsToEdit) {
                    if (net.getPostconditions(transition).size() > 1) {
                        net.removePostcondition(net.getPostcondition(transition, b));
                    } else {
                        net.removePostcondition(net.getPostcondition(transition, b));
                        transitionsToDelete.addAll(navigateBackAndRemove(transition));
                    }
                }
                for (String tName : transitionsToDelete) {
                    if (net.getTransition(tName) != null)
                        net.removeTransition(net.getTransition(tName));
                }
                net.addPrecondition(a, t);
                net.addPostcondition(t, b);
                break;
        }
    }

    private List<String> navigateBackAndRemove(Transition transition) {
        List<String> transitionToDelete = new ArrayList<>();
        transitionToDelete.add(transition.getName());
        for (Precondition precondition : net.getPreconditions(transition)) {
            net.removePlace(precondition.getPlace());
            for (Transition t : net.getTransitions()) {
                for (Postcondition pc : net.getPostconditions(t)) {
                    if (pc.getPlace() == precondition.getPlace()) {
                        transitionToDelete.addAll(navigateBackAndRemove(t));
                    }
                }
            }


         /*
        for (Precondition precondition : net.getPreconditions(transition)) {
            Transition t = net.getTransition(getTransitionName(precondition.getPlace().getName()));
            if (t != null) {
                navigateBackAndRemove(t);
            }
            //check if it's been declared a failure occurrence before and delete it
            t = net.getTransition(getTransitionName(precondition.getPlace().getName() + "Occurrence"));
            if (t != null) {
                navigateBackAndRemove(t);
            }
            //check if it's a routing probability and delete the places before it

        }

          */
        }
        return transitionToDelete;
    }

    private void decorateFailure(Failure failure, BigDecimal timestamp, PetriNetTranslatorMethod pntMethod) {
        Place b = net.getPlace(failure.getFailureMode().getDescription());
        if (b != null) {
            Place a = net.addPlace(b.getName() + "Occurrence");
            marking.setTokens(a, 1);
            Transition errorModeTransition = net.getTransition(getTransitionName(b.getName()));
            Transition t = net.addTransition(getTransitionName(a.getName()));
            t.addFeature(PDFParser.parseStringToStochasticTransitionFeature("dirac(" + timestamp.toString() + ")"));
            net.addPrecondition(a, t);
            net.addPostcondition(t, b);
            switch (pntMethod) {
                case CONCURRENT:
                    //ConcurrentMode: keeps the errorMode and add FailureOccurrence
                    String enablingFunction = createEnablingFunctionToAppend(b);
                    t.addFeature(new EnablingFunction(enablingFunction));
                    String newEnablingFunction = errorModeTransition.getFeature(EnablingFunction.class).toString() + "&&" + enablingFunction;
                    errorModeTransition.removeFeature(EnablingFunction.class);
                    errorModeTransition.addFeature(new EnablingFunction(newEnablingFunction));
                    break;
                case DETERMINISTIC:
                    //DeterministicMode: deletes the errorMode and add FailureOccurrence
                    for (Precondition precondition : net.getPreconditions(errorModeTransition)) {
                        net.removePrecondition(precondition);
                        net.removePlace(precondition.getPlace());
                    }
                    net.removeTransition(errorModeTransition);
                    break;
            }
        }
    }

    private String createEnablingFunctionToAppend(Place failure) {
        StringBuilder enablingFunction = new StringBuilder("((" + failure.getName() + "==0)");
        Transition t = net.getTransition(failure.getName() + "toFaults");
        for (Postcondition pc : net.getPostconditions(t)) {
            enablingFunction.append("&&(").append(pc.getPlace().getName()).append("==0)");
        }
        enablingFunction.append(")");
        return enablingFunction.toString();
    }

    private void decorateError(Error error, BigDecimal timestamp) {
        Transition t = net.getTransition(getTransitionName(error.getErrorMode().getOutgoingFailure().getDescription()));
        TransitionFeature tf = t.getFeature(StochasticTransitionFeature.class);
        if (tf != null)
            t.removeFeature(StochasticTransitionFeature.class);
        t.addFeature(PDFParser.parseStringToStochasticTransitionFeature("dirac(" + timestamp.toString() + ")"));
    }


    public PetriNet getPetriNet() {
        return net;
    }

    public Marking getMarking() {
        return marking;
    }

    public String getName() {
        return sysName;
    }
}
