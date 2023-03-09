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

package it.unifi.stlab.transformation;

import it.unifi.stlab.faultflow.model.knowledge.composition.System;
import it.unifi.stlab.faultflow.model.knowledge.propagation.*;
import it.unifi.stlab.transformation.faulttree.*;
import it.unifi.stlab.transformation.utils.ActivationFunctionParser;

import java.util.*;

public class TreeParser {

    private final Map<String, FaultMode> faults;
    private final Map<FailureMode, ErrorMode> errorModes;
    private final Map<String, ErrorMode> activationFunctions;
    private final List<PropagationPort> propagations;
    private final System system;

    public TreeParser(System system) {
        faults = new HashMap<>();
        errorModes = new HashMap<>();
        propagations = new ArrayList<>();
        activationFunctions = new HashMap<>();

        this.system = system;
    }

    /**
     * Populates the data structures later used to retrieve the necessary entities.
     */
    private void setStructures() {
        system.getComponents().forEach(component -> {
            component.getErrorModes().forEach(errorMode -> {
                errorModes.put(errorMode.getOutgoingFailure(), errorMode);
                activationFunctions.put(errorMode.getActivationFunction().toSimpleString(), errorMode);
                errorMode.getInputFaultModes().forEach(faultMode -> faults.put(faultMode.getName(), faultMode));
            });
            propagations.addAll(component.getPropagationPorts());
        });
    }

    /**
     * Method used to create a fault tree from a given error mode, then returning the top event of the fault tree
     */
    public Node createTree(ErrorMode errorMode) {
        setStructures();

        String activationFunction = errorMode.getActivationFunction().toSimpleString();
        activationFunction = "(" + activationFunction + ")";
        Map<String, List<String>> errorModeEntities = new LinkedHashMap<>();

        // Receive all the entities to be created for the fault tree and then calls the appropriate method
        errorModeEntities.putAll(ActivationFunctionParser.getActivationFunctionEntities(activationFunction));
        return createTreeEntity(errorMode.getActivationFunction().toSimpleString(), errorModeEntities);
    }

    /**
     * Private utility method used to create each entity necessary for the fault tree, starting from a map of entities
     * and their related sub-entities
     */
    private Node createTreeEntity(String entity, Map<String, List<String>> errorModeEntities) {
        // Get the list of subentities of a given entity
        List<String> subEntities = errorModeEntities.get(entity);

        // If it has no subentities, it is a simple fault mode, internal or external
        if (subEntities.size() == 0) {
            FaultMode faultMode = faults.get(entity);

            if (faultMode instanceof InternalFaultMode) {
                return new BasicEvent((InternalFaultMode) faultMode);
            } else {
                /* If it is an external fault mode, we need to retrieve the activation function of the failure mode that
                causes it and create the sub-tree related to the external fault mode*/
                FailureMode failureMode = getFailureModeFromPropagation((ExternalFaultMode) faultMode);
                ErrorMode errorMode = errorModes.get(failureMode);

                String activationFunction = errorMode.getActivationFunction().toSimpleString();
                activationFunction = "(" + activationFunction + ")";
                errorModeEntities.putAll(ActivationFunctionParser.getActivationFunctionEntities(activationFunction));

                return createTreeEntity(errorMode.getActivationFunction().toSimpleString(), errorModeEntities);
            }
        } else {
            // If it has subentities, it is a gate!
            String gateType = subEntities.get(0);
            Gate gate;
            ErrorMode errorMode = activationFunctions.get(entity);

            switch (gateType) {
                case "AND":
                    if (errorMode != null)
                        gate = new AND(activationFunctions.get(entity));
                    else
                        gate = new AND(entity);
                    break;
                case "OR":
                    if (errorMode != null)
                        gate = new OR(activationFunctions.get(entity));
                    else
                        gate = new OR(entity);
                    break;
                default:
                    throw new RuntimeException();
            }

            for (int index = 1; index < subEntities.size(); index++)
                gate.addChild(createTreeEntity(subEntities.get(index), errorModeEntities));

            return gate;
        }
    }

    /**
     * Method that reduces the tree by cutting leaves or intermediate nodes depending on whether they are contained in
     * the minimal cutset or not
     */
    public Node reduceTree(List<String> minimalCutSet, Node tree) {
        TreeCutterVisitor treeCutterVisitor = new TreeCutterVisitor(minimalCutSet);
        treeCutterVisitor.visit(tree);

        return tree;
    }

    /**
     * Private utility method used to retrieve the failure mode that propagates to a given external fault mode
     */
    private FailureMode getFailureModeFromPropagation(ExternalFaultMode externalFaultMode) {
        for (PropagationPort propagationPort : propagations) {
            if (propagationPort.getExternalFaultMode().equals(externalFaultMode))
                return propagationPort.getPropagatedFailureMode();
        }

        return null;
    }
}