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
import it.unifi.stlab.faultflow.model.utils.BooleanExpressionConverter;
import it.unifi.stlab.faultflow.model.utils.PDFParser;
import org.apache.commons.math3.distribution.RealDistribution;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Represents an error mode in the failure model, which is an erroneous state of a component triggered by a combination
 * of faults that propagate their activation.
 */
@Entity
@Table(name = "errormodes")
public class ErrorMode extends BaseEntity {

    /**
     * User-friendly name of the error mode
     */
    private String name;

    /**
     * Collection of fault modes that concur to the activation of the error mode
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "errormode_faultmodes",
            joinColumns = @JoinColumn(name = "errormode_uuid"),
            inverseJoinColumns = @JoinColumn(name = "faultmode_fk")
    )
    private List<FaultMode> inputFaultModes;

    /**
     * Boolean expression that represents the boolean activation function of the error mode
     */
    @Lob
    @Convert(converter = BooleanExpressionConverter.class)
    private BooleanExpression activationFunction;

    /**
     * Failure mode that is activated by the propagation of the error mode
     */
    @OneToOne
    private FailureMode outgoingFailure;

    /**
     * Probability density failure that express the time before the propagation of the error mode to the failure mode
     */
    @Column(name = "fault_to_failure_pdf")
    private String faultToFailurePDF;

    /**
     * Default constructor that sets an empty name for the error mode and initializes all the collections of fault modes
     */
    public ErrorMode() {
        name = "";
        inputFaultModes = new ArrayList<>();
        activationFunction = null;
        outgoingFailure = null;
        faultToFailurePDF = "";
    }

    /**
     * Constructor that sets the name of the error mode
     *
     * @param name user-friendly name of the error mode, used in the PetriNet translation of the model, to give a name
     *             to its corresponding place. Must be unique.
     */
    public ErrorMode(String name) {
        this.name = name;
        activationFunction = null;
        inputFaultModes = new ArrayList<>();
        outgoingFailure = null;
        faultToFailurePDF = "";
    }

    /**
     * Constructor that sets the name of the error mode and sets the boolean expression that activates the error mode
     *
     * @param name     user-friendly name of the error mode, used in the PetriNet translation of the model, to give a name
     *                 to its corresponding place. Must be unique.
     * @param function activation function (or enabling function) of the error mode, expressed as an instance of
     *                 {@link BooleanExpression}.
     */
    public ErrorMode(String name, BooleanExpression function) {
        this(name);
        this.activationFunction = function;
        this.inputFaultModes = activationFunction.extractIncomingFaults();
    }

    /**
     * Constructor that sets the name of the error mode, the boolean expression that activates the error mode and the
     * outgoing failure mode
     *
     * @param name            user-friendly name of the error mode, used in the PetriNet translation of the model, to give a name
     *                        to its corresponding place. Must be unique.
     * @param function        activation function (or enabling function) of the error mode, expressed as an instance of
     *                        {@link BooleanExpression}.
     * @param outgoingFailure {@link FailureMode} instance that is activated after the error mode propagation
     */
    public ErrorMode(String name, BooleanExpression function, FailureMode outgoingFailure) {
        this(name, function);
        this.outgoingFailure = outgoingFailure;
    }

    /**
     * Constructor that sets the name of the error mode, the boolean expression that activates the error mode, the
     * outgoing failure mode and the probability density function of the error propagation time
     *
     * @param name             user-friendly name of the error mode, used in the PetriNet translation of the model, to give a name
     *                         to its corresponding place. Must be unique.
     * @param function         activation function (or enabling function) of the error mode, expressed as an instance of
     *                         {@link BooleanExpression}.
     * @param outgoingFailure  {@link FailureMode} instance that is activated after the error mode propagation
     * @param faultToFailurePDF {@link RealDistribution} expression that indicates the probability density function of
     *                         the error mode propagation to its failure mode
     */
    public ErrorMode(String name, BooleanExpression function, FailureMode outgoingFailure, RealDistribution faultToFailurePDF) {
        this(name, function, outgoingFailure);
        this.faultToFailurePDF = PDFParser.parseRealDistributionToString(faultToFailurePDF);
    }

    /**
     * Constructor that sets the name of the error mode, the boolean expression that activates the error mode, the
     * outgoing failure mode and the probability density function of the error propagation time
     *
     * @param name             user-friendly name of the error mode, used in the PetriNet translation of the model, to give a name
     *                         to its corresponding place. Must be unique.
     * @param function         activation function (or enabling function) of the error mode, expressed as an instance of
     *                         {@link BooleanExpression}.
     * @param outgoingFailure  {@link FailureMode} instance that is activated after the error mode propagation
     * @param faultToFailurePDF {@link String} expression that indicates the probability density function of
     *                         the error mode propagation to its failure mode
     */
    public ErrorMode(String name, BooleanExpression function, FailureMode outgoingFailure, String faultToFailurePDF) {
        this(name, function, outgoingFailure);
        setPDF(faultToFailurePDF);
    }

    /**
     * Getter of the name of the error mode
     *
     * @return a {@link String} representing the user-friendly name of the error mode
     */
    public String getName() {
        return this.name;
    }

    /**
     * Setter of the name of the error mode
     *
     * @param name a {@link String} representing the user-friendly name of the error mode
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter of the activation function of the error mode
     *
     * @return a {@link BooleanExpression} representing the activation function of the error mode
     */
    public BooleanExpression getActivationFunction() {
        return activationFunction;
    }

    /**
     * Setter of the activation function of the error mode
     *
     * @param activationFunction a {@link BooleanExpression} representing the activation function of the error mode
     */
    public void setActivationFunction(BooleanExpression activationFunction) {
        this.activationFunction = activationFunction;
    }

    /**
     * Getter of the collection of fault modes that compose the error mode activation function
     *
     * @return a {@link List} of {@link FaultMode} instances that compose the error mode activation function
     */
    public List<FaultMode> getInputFaultModes() {
        return activationFunction.extractIncomingFaults();
    }

    /**
     * Setter of the collection of fault modes that compose the error mode activation function
     *
     * @param inputFaultModes a {@link List} of {@link FaultMode} instances that compose the error mode activation
     *                        function
     */
    public void setInputFaultModes(List<FaultMode> inputFaultModes) {
        this.inputFaultModes = inputFaultModes;
    }

    /**
     * Getter of the outgoing failure mode activated by the error mode
     *
     * @return the failure mode that is activated by the propagation of the error mode
     */
    public FailureMode getOutgoingFailure() {
        return this.outgoingFailure;
    }

    /**
     * Setter of the outgoing failure mode activated by the error mode
     *
     * @param outgoingFailure the failure mode that is activated by the propagation of the error mode
     */
    public void setOutgoingFailure(FailureMode outgoingFailure) {
        this.outgoingFailure = outgoingFailure;
    }

    /**
     * Getter of the time to failure probability density function, in the form of string
     *
     * @return a {@link String} version of the time to failure probability density function
     */
    public String getFaultToFailurePDFToString() {
        return faultToFailurePDF;
    }

    /**
     * Getter of the time to failure probability density function
     *
     * @return a {@link RealDistribution} representation of the time to failure probability density function
     */
    public RealDistribution getFaultToFailurePDF() {
        return PDFParser.parseStringToRealDistribution(faultToFailurePDF);
    }

    /**
     * Setter of the time to failure probability density function, in the form of string
     *
     * @param faultToFailurePDF a {@link String} version of the time to failure probability density function
     */
    public void setPDF(String faultToFailurePDF) {
        this.faultToFailurePDF = faultToFailurePDF;
    }

    /**
     * Method that computes the truth value of the activation function of the error mode
     *
     * @return a boolean value, true if the error mode has its activation function ready for propagation, false
     * otherwise
     */
    public boolean checkActivationFunction() {
        return activationFunction.compute();
    }

    /**
     * Method that checks whether there is an input fault with the specified name in the error mode
     *
     * @param name {@link String} that represents the user-friendly name of the fault mode
     * @return true if the fault is present, false otherwise
     */
    public boolean checkFaultIsPresent(String name) {
        return this.inputFaultModes.stream().anyMatch(x -> x.name.equals(name));
    }

    /**
     * Method used to add an input fault mode to the error mode
     *
     * @param faultMode {@link FaultMode} to be added to the error mode
     */
    public void addInputFaultMode(FaultMode... faultMode) {
        this.inputFaultModes.addAll(Arrays.asList(faultMode));
    }

    /**
     * Method used to set the enabling condition (activation function) of the error mode, by giving the boolean
     * expression and its composing fault modes as input
     *
     * @param booleanExpression {@link BooleanExpression} that represents the activation function of the error mode
     * @param faultModes        {@link List} of {@link FaultMode} instances that compose the error mode activation function
     */
    public void setEnablingCondition(String booleanExpression, HashMap<String, FaultMode> faultModes) {
        this.activationFunction = BooleanExpression.config(booleanExpression, faultModes);
    }
}
