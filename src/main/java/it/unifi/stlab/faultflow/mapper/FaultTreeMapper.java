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

package it.unifi.stlab.faultflow.mapper;

import it.unifi.stlab.faultflow.dto.inputsystemdto.faulttree.*;
import it.unifi.stlab.faultflow.dto.petrinet.*;
import it.unifi.stlab.faultflow.dto.system.OutputSystemDto;
import it.unifi.stlab.faultflow.model.knowledge.composition.Component;
import it.unifi.stlab.faultflow.model.knowledge.composition.System;
import it.unifi.stlab.faultflow.model.knowledge.propagation.*;
import it.unifi.stlab.faultflow.model.utils.PDFParser;
import org.oristool.models.stpn.trees.StochasticTransitionFeature;
import org.oristool.petrinet.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class FaultTreeMapper {
    private static boolean isComponentInSystem(System system, String name) {
        return system.getComponents().stream().anyMatch(x -> x.getName().equalsIgnoreCase(name));
    }

    private static Component getComponentInSystem(System system, String name) {
        Optional<Component> mc = system.getComponents().stream().filter(x -> x.getName().equalsIgnoreCase(name)).findAny();
        if (mc.isPresent())
            return mc.get();
        else {
            Component component = new Component(name);
            system.addComponent(component);
            return component;
        }
    }

    public static void decorateSystem(FaultTreeDto faultTreeDto, System system) {
        Queue<NodeDto> nodeToVisit = new LinkedList<>();
        HashMap<String, FaultMode> faultModes = new HashMap<>();
        HashMap<String, FailureMode> failureModes = new HashMap<>();
        for (String topEvent : faultTreeDto.getTopEvents()) {
            nodeToVisit.add(getNodeFromID(faultTreeDto, topEvent));
        }
        while (!nodeToVisit.isEmpty()) {
            NodeDto parent = nodeToVisit.remove();
            List<ParentingDto> parentings = getChildrenFromNodeID(faultTreeDto, parent.getExternalId());
            for (ParentingDto parenting : parentings) {
                NodeDto currentNode = getNodeFromID(faultTreeDto, parenting.getChildId());

                if (currentNode.getNodeType() == NodeType.GATE) {
                    Component mc = getComponentInSystem(system, currentNode.getComponentName());
                    if (!mc.isErrorModeNamePresent(currentNode.getLabel())) {
                        ErrorMode errorMode = new ErrorMode(currentNode.getLabel());
                        FailureMode fm = failureModes.get(parent.getLabel());
                        if (fm != null)
                            errorMode.setOutgoingFailure(fm);
                        else {
                            errorMode.setOutgoingFailure(new FailureMode(parent.getLabel()));
                            failureModes.put(errorMode.getOutgoingFailure().getDescription(), errorMode.getOutgoingFailure());
                        }
                        errorMode.setPDF(parent.getPdf());
                        getEnablingFunctionByNavigatingTree(faultTreeDto, currentNode.getExternalId(), errorMode, faultModes, failureModes, nodeToVisit, system);
                        mc.addErrorMode(errorMode);
                    }
                } else if (currentNode.getNodeType() == NodeType.FAILURE) {
                    //add its children
                    nodeToVisit.add(currentNode);
                }
            }

        }
    }

    public static System generateSystemFromFaultTree(FaultTreeDto faultTreeDto) {
        System system = null;
        for (String topEvent : faultTreeDto.getTopEvents()) {
            NodeDto rootNode = getNodeFromID(faultTreeDto, topEvent);
            Component topComponent = new Component(rootNode.getComponentName());
            system = new System(rootNode.getComponentName() + "_SYS");
            system.addComponent(topComponent);
            system.setTopLevelComponent(topComponent);
        }
        decorateSystem(faultTreeDto, system);
        return system;
    }

    public static List<ParentingDto> getChildrenFromNodeID(FaultTreeDto faultTree, String nodeID) {
        return faultTree.getParentings().stream().filter(x -> x.getParentId().equals(nodeID)).collect(Collectors.toList());
    }

    public static List<ParentingDto> getParentsFromNodeID(FaultTreeDto faultTree, String nodeID) {
        return faultTree.getParentings().stream().filter(x -> x.getChildId().equals(nodeID)).collect(Collectors.toList());
    }

    public static NodeDto getNodeFromID(FaultTreeDto faultTree, String nodeID) {
        return faultTree.getNodes().stream().filter(x -> x.getExternalId().equalsIgnoreCase(nodeID)).findFirst().get();
    }

    public static NodeDto getNodeFromLabel(FaultTreeDto faultTree, String label) {
        return faultTree.getNodes().stream().filter(x -> x.getLabel().equalsIgnoreCase(label)).findFirst().get();
    }

    public static String getEnablingFunctionByNavigatingTree(FaultTreeDto faultTreeDto, String currentNode, ErrorMode errorMode,
                                                             HashMap<String, FaultMode> faultModes, HashMap<String, FailureMode> failureModes,
                                                             Queue<NodeDto> nodeToVisit, System system) {
        StringBuilder be = new StringBuilder();
        NodeDto nodeDto = getNodeFromID(faultTreeDto, currentNode);
        switch (nodeDto.getGateType()) {
            case OR:
                for (ParentingDto parentingDto : getChildrenFromNodeID(faultTreeDto, nodeDto.getExternalId())) {
                    NodeDto child = getNodeFromID(faultTreeDto, parentingDto.getChildId());
                    switch (child.getNodeType()) {
                        case GATE:
                            be.append("(" + getEnablingFunctionByNavigatingTree(faultTreeDto, child.getExternalId(), errorMode, faultModes, failureModes, nodeToVisit, system)).append(")||");
                            break;
                        case FAILURE:
                            FailureMode failureMode = failureModes.get(child.getLabel());
                            if (failureMode == null) {
                                failureMode = new FailureMode(child.getLabel());
                                failureModes.put(child.getLabel(), failureMode);
                            }
                            String[] faultNameAndProbability = getFailuresFaultNameAndProbability(child, nodeDto.getComponentName());
                            String faultname = faultNameAndProbability[0];
                            BigDecimal routingProbability = BigDecimal.valueOf(Double.parseDouble(faultNameAndProbability[1]));
                            ExternalFaultMode externalFaultMode = new ExternalFaultMode(faultname);
                            errorMode.addInputFaultMode(externalFaultMode);
                            faultModes.put(faultname, externalFaultMode);
                            be.append(faultname).append("||");
                            if (!nodeToVisit.contains(child))
                                nodeToVisit.add(child);
                            Component mc = getComponentInSystem(system, child.getComponentName());
                            Component affectedComponent = getComponentInSystem(system, nodeDto.getComponentName());
                            mc.addPropagationPort(new PropagationPort(failureMode, externalFaultMode, affectedComponent, routingProbability));
                            break;
                        default:
                            be.append(child.getLabel()).append("||");
                            InternalFaultMode internalFaultMode = new InternalFaultMode(child.getLabel());
                            internalFaultMode.setTimeToFaultPDF(child.getPdf());
                            errorMode.addInputFaultMode(internalFaultMode);
                            faultModes.put(child.getLabel(), internalFaultMode);
                    }
                }
                be.delete(be.length() - 2, be.length());
                break;
            case AND:
                for (ParentingDto parentingDto : getChildrenFromNodeID(faultTreeDto, nodeDto.getExternalId())) {
                    NodeDto child = getNodeFromID(faultTreeDto, parentingDto.getChildId());
                    switch (child.getNodeType()) {
                        case GATE:
                            be.append("(" + getEnablingFunctionByNavigatingTree(faultTreeDto, child.getExternalId(), errorMode, faultModes, failureModes, nodeToVisit, system)).append(")&&");
                            break;
                        case FAILURE:
                            FailureMode failureMode = failureModes.get(child.getLabel());
                            if (failureMode == null) {
                                failureMode = new FailureMode(child.getLabel());
                                failureModes.put(child.getLabel(), failureMode);
                            }
                            String[] faultNameAndProbability = getFailuresFaultNameAndProbability(child, nodeDto.getComponentName());
                            String faultname = faultNameAndProbability[0];
                            BigDecimal routingProbability = BigDecimal.valueOf(Double.parseDouble(faultNameAndProbability[1]));
                            ExternalFaultMode externalFaultMode = new ExternalFaultMode(faultname);
                            errorMode.addInputFaultMode(externalFaultMode);
                            faultModes.put(faultname, externalFaultMode);
                            be.append(faultname).append("&&");
                            if (!nodeToVisit.contains(child))
                                nodeToVisit.add(child);
                            Component mc = getComponentInSystem(system, child.getComponentName());
                            Component affectedComponent = getComponentInSystem(system, nodeDto.getComponentName());
                            mc.addPropagationPort(new PropagationPort(failureMode, externalFaultMode, affectedComponent, routingProbability));
                            break;
                        default:
                            be.append(child.getLabel()).append("&&");
                            InternalFaultMode internalFaultMode = new InternalFaultMode(child.getLabel());
                            internalFaultMode.setTimeToFaultPDF(child.getPdf());
                            errorMode.addInputFaultMode(internalFaultMode);
                            faultModes.put(child.getLabel(), internalFaultMode);
                    }
                }
                be.delete(be.length() - 2, be.length());
                break;
            case KOUTOFN:
                be.append(nodeDto.getK()).append("/").append(nodeDto.getN()).append("(");
                for (ParentingDto parentingDto : getChildrenFromNodeID(faultTreeDto, nodeDto.getExternalId())) {
                    NodeDto child = getNodeFromID(faultTreeDto, parentingDto.getChildId());
                    switch (child.getNodeType()) {
                        case FAILURE:
                            FailureMode failureMode = failureModes.get(child.getLabel());
                            if (failureMode == null) {
                                failureMode = new FailureMode(child.getLabel());
                                failureModes.put(child.getLabel(), failureMode);
                            }
                            String[] faultNameAndProbability = getFailuresFaultNameAndProbability(child, nodeDto.getComponentName());
                            String faultname = faultNameAndProbability[0];
                            BigDecimal routingProbability = BigDecimal.valueOf(Double.parseDouble(faultNameAndProbability[1]));
                            be.append(faultname).append(",");
                            ExternalFaultMode externalFaultMode = new ExternalFaultMode(faultname);
                            errorMode.addInputFaultMode(externalFaultMode);
                            faultModes.put(faultname, externalFaultMode);
                            if (!nodeToVisit.contains(child))
                                nodeToVisit.add(child);
                            Component mc = getComponentInSystem(system, child.getComponentName());
                            Component affectedComponent = getComponentInSystem(system, nodeDto.getComponentName());
                            mc.addPropagationPort(new PropagationPort(failureMode, externalFaultMode, affectedComponent, routingProbability));
                            break;
                        default:
                            be.append(child.getLabel()).append(",");
                            InternalFaultMode internalFaultMode = new InternalFaultMode(child.getLabel());
                            internalFaultMode.setTimeToFaultPDF(child.getPdf());
                            errorMode.addInputFaultMode(internalFaultMode);
                            faultModes.put(child.getLabel(), internalFaultMode);
                    }
                }
                be.delete(be.length() - 1, be.length());
                be.append(")");
                break;
            default:
        }
        errorMode.setEnablingCondition(be.toString(), faultModes);
        return be.toString();
    }

    public static PetriNetDto petriNetToJSON(PetriNet net) {
        PetriNetDto outputNet = new PetriNetDto();
        List<PlaceDto> outputPlaces = new ArrayList<>();
        for (Place place : net.getPlaces()) {
            PlaceDto outputPlace = new PlaceDto();
            outputPlace.setName(place.getName());
            outputPlaces.add(outputPlace);
        }
        outputNet.setPlaces(outputPlaces);
        List<TransitionDto> outputTransitions = new ArrayList<>();
        List<PreconditionDto> outputPreconditions = new ArrayList<>();
        List<PostconditionDto> outputPostconditions = new ArrayList<>();
        for (Transition transition : net.getTransitions()) {
            TransitionDto outputTransition = new TransitionDto();
            outputTransition.setName(transition.getName());
            List<FeaturesDto> features = new ArrayList<>();
            if (transition.getFeature(EnablingFunction.class) != null) {
                EnablingFunctionDto enablingFunctionDto = new EnablingFunctionDto();
                enablingFunctionDto.setEnablingFunction(transition.getFeature(EnablingFunction.class).toString());
                features.add(enablingFunctionDto);
            }
            if (transition.getFeature(StochasticTransitionFeature.class) != null) {
                PDFDto pdf = new PDFDto();
                pdf.setPdf(PDFParser.parseStochasticTransitionFeatureToString(transition.getFeature(StochasticTransitionFeature.class)));
                features.add(pdf);
            }
            outputTransition.setFeatures(features);
            outputTransitions.add(outputTransition);

            for (Postcondition postcondition : net.getPostconditions(transition)) {
                PostconditionDto outputPostcondition = new PostconditionDto();
                outputPostcondition.setTransition(postcondition.getTransition().getName());
                outputPostcondition.setPlace(postcondition.getPlace().getName());
                outputPostconditions.add(outputPostcondition);
            }
            for (Precondition precondition : net.getPreconditions(transition)) {
                PreconditionDto outputPrecondition = new PreconditionDto();
                outputPrecondition.setTransition(precondition.getTransition().getName());
                outputPrecondition.setPlace(precondition.getPlace().getName());
                outputPreconditions.add(outputPrecondition);
            }
        }
        outputNet.setTransitions(outputTransitions);
        outputNet.setPreconditions(outputPreconditions);
        outputNet.setPostconditions(outputPostconditions);
        return outputNet;
    }

    public static OutputSystemDto systemToOutputSystem(System system) {
        return new OutputSystemDto(system);
    }

    private static String[] getFailuresFaultNameAndProbability(NodeDto failure, String gateComponentID) {
        //first element is the fault's name, second element is fault routing probability
        String[] res = new String[2];
        if (failure.getActsAs() != null) {
            for (AliasDto alias : failure.getActsAs()) {
                if (alias.getComponentName().equalsIgnoreCase(gateComponentID)) {
                    res[0] = alias.getFaultName();
                    if (alias.getRoutingProbability() != null) {
                        if (alias.getRoutingProbability() <= 1) {
                            res[1] = "" + alias.getRoutingProbability();
                            return res;
                        }

                    } else {
                        res[1] = "1";
                        return res;
                    }
                }
            }
        } else {
            res[0] = failure.getLabel().replace("Failure", "Fault");
            res[1] = "1";
            return res;
        }

        throw new NullPointerException("There's no ActAs in" + failure.getLabel() + " with the same ComponentID as the Gate's: " + gateComponentID);
    }


}
