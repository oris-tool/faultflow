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

import it.unifi.hierarchical.model.*;
import it.unifi.hierarchical.model.visitor.DepthVisitor;
import it.unifi.stlab.faultflow.model.knowledge.propagation.ErrorMode;
import it.unifi.stlab.faultflow.model.knowledge.propagation.InternalFaultMode;
import it.unifi.stlab.transformation.faulttree.*;
import org.oristool.math.OmegaBigDecimal;
import org.oristool.math.expression.Expolynomial;
import org.oristool.math.expression.Variable;
import org.oristool.math.function.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class HSMPParser {

    public static HSMP parseTree(Node tree) {
        LogicalLocation initialLocation = createFromNode(tree);
        HSMP hsmp = new HSMP(initialLocation);

        DepthVisitor depthVisitor = new DepthVisitor(0);
        depthVisitor.visit(initialLocation);
        SimpleStep initialPropagation = (SimpleStep) initialLocation.getNextLocations().get(0);
        initialPropagation.setNextLocations(new ArrayList<>(), new ArrayList<>());
        initialPropagation.addNextLocation(initialLocation, 1.0);

        return hsmp;
    }

    public static LogicalLocation createFromNode(Node node) {
        if (node instanceof BasicEvent)
            return createSimpleStep((BasicEvent) node);
        else
            return createCompositeStep((Gate) node);
    }

    public static LogicalLocation createSimpleStep(BasicEvent node) {
        InternalFaultMode faultMode = node.getFaultMode();
        PartitionedFunction partitionedFunction = getPartitionedFunction(faultMode.getTimeToFaultPDFToString());
        FinalLocation finalLocation = new FinalLocation(faultMode.getName() + "_final");

        return new SimpleStep(faultMode.getName(), partitionedFunction, List.of(finalLocation),
                List.of(1.0), 0);
    }

    public static LogicalLocation createCompositeStep(Gate node) {
        ErrorMode errorMode = node.getErrorMode();
        String name;
        LogicalLocation nextLocation;

        if (errorMode != null) {
            PartitionedFunction partitionedFunction = getPartitionedFunction(errorMode.getFaultToFailurePDFToString());
            name = errorMode.getName();
            FinalLocation finalLocation = new FinalLocation(name + "_final");
            if(node.getRoutingProbability() < 1.0){
                SimpleStep absorbingStep = new SimpleStep(errorMode.getName()+"_absorbing", getPartitionedFunction("dirac(0)"),
                        new ArrayList<>(), new ArrayList<>() , 0);
                //step.addNextLocation(step, 1.0);
                Double routingprob = node.getRoutingProbability();
                nextLocation = new SimpleStep(errorMode.getName() + "_propagation", partitionedFunction,
                        List.of(finalLocation, absorbingStep), List.of(routingprob, 1-routingprob), 0);
            }
            else{
                nextLocation = new SimpleStep(errorMode.getName() + "_propagation", partitionedFunction,
                        List.of(finalLocation), List.of(1.0), 0);
                ((SimpleStep) nextLocation).addNextLocation(finalLocation, 1.0);
            }
        } else {
            name = node.getName();
            nextLocation = new FinalLocation(name + "_final");
        }

        CompositeStep compositeStep;

        if (node instanceof AND)
            compositeStep = new CompositeStep(name, CompositeStepType.LAST, 0);
        else if (node instanceof OR)
            compositeStep = new CompositeStep(name, CompositeStepType.FIRST, 0);
        else
            throw new RuntimeException("Gate type currently not supported");

        compositeStep.addNextLocations(List.of(nextLocation), List.of(1.0));

        for (Node subNode : node.getChildren()) {
            Region region = new Region(createFromNode(subNode), RegionType.ENDING);
            compositeStep.addRegion(region);
        }

        return compositeStep;
    }

    public static PartitionedFunction getPartitionedFunction(String arisingPDF) {
        String typePDF = arisingPDF.toLowerCase().replaceAll("\\s*\\([^()]*\\)\\s*", "");
        String arguments = arisingPDF.substring(typePDF.length() + 1, arisingPDF.length() - 1);
        String[] args;

        switch (typePDF) {
            case "erlang":
                args = arguments.split(",");
                double lambda = checkDivision(args[1]);
                return new Erlang(Variable.X, Integer.parseInt(args[0]), new BigDecimal(lambda));
            case "dirac":
                return GEN.newDeterministic(new BigDecimal(arguments));
            case "exp":
                double rate = checkDivision(arguments);
                return new EXP(Variable.X, new BigDecimal(rate));
            case "uniform":
                args = arguments.split(",");
                return GEN.newUniform(new OmegaBigDecimal(args[0]), new OmegaBigDecimal(args[1]));
            case "gaussian":
                args = arguments.split(",");
                double factor = Math.sqrt(3 * Double.parseDouble(args[1]));
                String a = "" + (Double.parseDouble(args[0]) - factor);
                String b = "" + (Double.parseDouble(args[0]) + factor);
                return GEN.newUniform(new OmegaBigDecimal(a), new OmegaBigDecimal(b));
            case "expoly":
                args = arguments.split(",");
                String density = args[0];
                OmegaBigDecimal eft = new OmegaBigDecimal(args[1]);
                OmegaBigDecimal lft = new OmegaBigDecimal(args[2]);
                if (Expolynomial.isValid(density))
                    return GEN.newExpolynomial(density, eft, lft);
                else
                    throw new UnsupportedOperationException("Function not well formed");
            case "piecewise":
                String[] functs = arguments.split(";");
                List<GEN> functions = new ArrayList<>();
                for(String funct: functs){
                    args = funct.split(",");
                    String densityi = args[0];
                    OmegaBigDecimal efti = new OmegaBigDecimal(args[1]);
                    OmegaBigDecimal lfti = new OmegaBigDecimal(args[2]);
                    if (Expolynomial.isValid(densityi)) {
                        functions.add(GEN.newExpolynomial(densityi, efti, lfti));
                    }
                    else
                        throw new UnsupportedOperationException("Function not well formed");
                }
                return new PartitionedGEN(functions);

            default:
                throw new UnsupportedOperationException("PDF not supported");
        }
    }

    private static double checkDivision(String arg) {
        if (arg.contains("/")) {
            String[] factors = arg.split("/");
            Double num = Double.parseDouble(factors[0]);
            Double denom = Double.parseDouble(factors[1]);
            return num / denom;
        } else {
            return Double.parseDouble(arg);
        }
    }
}
