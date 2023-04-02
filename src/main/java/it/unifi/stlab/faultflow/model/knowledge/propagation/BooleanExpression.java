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

import it.unifi.stlab.faultflow.model.knowledge.propagation.operators.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Interface, later implemented by {@link Operator}, {@link AND}, {@link KofN}, {@link NOT} and {@link OR}, that
 * represent a boolean logic expression used to evaluate the activation of {@link ErrorMode} instances.
 */
public interface BooleanExpression {

    /**
     * Method that transforms a boolean expression written as a {@link String} into a tree composed of operators and
     * fault modes. The operator at the root of the tree is the last operator to be computed. Every leaf is a fault
     * mode.
     *
     * @param booleanExpression is the {@link String} form of the booleanExpression, written in first order logic. The operators
     *                          supported are: && as AND, || as OR, ! as NOT, K/N (i.e. 2/3) as the K out of N operator
     *                          (i.e. 2 out of 3).
     * @param faultModes        is an {@link java.util.HashMap} filled with the fault modes expected in the system under test.
     *                          The {@link java.util.HashMap} could also be empty, since its job is to populate it with
     *                          the fault modes that appear in the boolean expression.
     * @return an instance of {@link BooleanExpression} which could be described as a tree composed of instances of
     * operators and fault modes.
     */
    static BooleanExpression config(String booleanExpression, Map<String, FaultMode> faultModes) {
        //Preprocess string because some regex operations escapes with particular characters
        String newString = booleanExpression;
        newString = newString.replaceAll("&&", "&");
        newString = newString.replace("||", "°");
        return _config(newString, faultModes);

    }

    /**
     * Private method, used in a recursive way, to create the final boolean expression.
     *
     * @param booleanExpression is the {@link String} form of the booleanExpression, written in first order logic. The operators
     *                          supported are: && as AND, || as OR, ! as NOT, K/N (i.e. 2/3) as the K out of N operator
     *                          (i.e. 2 out of 3).
     * @param faultModes        is an {@link java.util.HashMap} filled with the fault modes expected in the system under test.
     *                          The {@link java.util.HashMap} could also be empty, since its job is to populate it with
     *                          the fault modes that appear in the boolean expression.
     * @return an instance of {@link BooleanExpression} which could be described as a tree composed of instances of
     * operators and fault modes.
     */
    private static BooleanExpression _config(String booleanExpression, Map<String, FaultMode> faultModes) {
        BooleanExpression be;
        switch (findOuterOperator(booleanExpression)) {
            case '°':
                be = new OR();
                for (String literal : booleanExpression.split("°"))
                    be.addChild(_config(literal.replaceAll("[\\[\\](){}]", ""), faultModes));
                return be;
            case '&':
                be = new AND();
                for (String literal : booleanExpression.split("&"))
                    be.addChild(_config(literal.replaceAll("[\\[\\](){}]", ""), faultModes));
                return be;
            case '!':
                be = new NOT();
                be.addChild(_config(parseNOT(booleanExpression), faultModes));
                return be;
            case '/':
                be = new KofN(Integer.parseInt(booleanExpression.substring(booleanExpression.indexOf("/") - 1, booleanExpression.indexOf("/"))),
                        Integer.parseInt(booleanExpression.substring(booleanExpression.indexOf("/") + 1, booleanExpression.indexOf("/") + 2)));
                String failNames = booleanExpression.substring(booleanExpression.indexOf("/") + 2);
                for (String literal : failNames.split(","))
                    be.addChild(_config(literal, faultModes));
                return be;
            default:
                be = addFaultMode(booleanExpression, faultModes);
        }
        return be;
    }

    /**
     * Method that adds a fault mode used in the boolean expression.
     *
     * @param literal    name {@link String} of the fault mode to be added as a leaf in the boolean expression
     * @param faultModes {@link List} of the fault modes expected in the system under test
     * @return an instance of {@link BooleanExpression} to be added as a child in the composition pattern.
     */
    private static BooleanExpression addFaultMode(String literal, Map<String, FaultMode> faultModes) {
        BooleanExpression be;
        String cleanName;
        //Se il literal contiene una negazione, va isolato il vero faultName:
        if (literal.indexOf('!') != -1) {
            //c'è un not tra parentesi, richiama il tutto senza parentesi
            //e metti il failure come figlio di NOT
            be = new NOT();
            be.addChild(addFaultMode(parseNOT(literal), faultModes));
        } else {
            cleanName = literal.trim().replaceAll("[\\[\\](){}]", "");
            if (faultModes.containsKey(cleanName))
                be = faultModes.get(cleanName);
            else {
                be = new InternalFaultMode(cleanName);
                faultModes.put(cleanName, (FaultMode) be);
            }
        }
        return be;
    }

    /**
     * As the NOT operator could be used with a single instance of {@link FaultMode} (i.e. !A_Fault1) or with
     * a single {@link Operator} (i.e. !(A_Fault1&&A_Fault2)), a specific method was needed to support every special
     * case and be able to extract correctly the name of the fault mode under the NOT operator.
     *
     * @param booleanExpression a {@link String} from which the method will extract the names of the fault modes that
     *                          appear in it.
     * @return the {@link String} name of the fault mode under the {@link NOT} operator
     * (i.e. (!A_Fault1 && A_Fault2)-> return A_Fault1)
     */
    private static String parseNOT(String booleanExpression) {
        //clean booleanExpression from '!' first
        booleanExpression = booleanExpression.replace("!", "");
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < booleanExpression.length(); i++) {
            switch (booleanExpression.charAt(i)) {
                case '(':
                    return booleanExpression.substring(i + 1, booleanExpression.indexOf(')'));
                case '^':
                case '°':
                    return res.toString();
                default:
                    res.append(booleanExpression.charAt(i));
            }
        }
        return res.toString();
    }

    /**
     * Method that finds the operator that will be at the root of the boolean expression tree. This means to find the
     * outer operator, or the operator that will be computed for last in the boolean expression
     * (i.e. (A_Fault1)&&(A_Fault2||A_Fault3)-> &&(AND) will be the outer operator
     *
     * @param booleanExpression the string form of the booleanExpression in input
     * @return the character that represents the outer operator
     */
    private static char findOuterOperator(String booleanExpression) {
        //remove everything inside brackets
        String newBooleanExpression;
        while (booleanExpression.contains("(") && booleanExpression.contains(")")) {
            newBooleanExpression = booleanExpression.replaceAll("\\s*\\([^()]*\\)\\s*", "");
            if (newBooleanExpression.length() == 0)
                break;
            booleanExpression = newBooleanExpression;
        }
        //remove everything except special characters
        booleanExpression = booleanExpression.replaceAll("[^&°!/]", "");
        char operator;
        //take last operator
        if (booleanExpression.length() != 0)
            operator = booleanExpression.charAt(booleanExpression.length() - 1);
        else {
            operator = '.';
        }
        return operator;
    }

    static List<String> extractInputFault(String plainSirioEnablingCondition) {
        String newString = plainSirioEnablingCondition;
        newString = newString.replaceAll("&&", "°");
        newString = newString.replace("||", "°");
        newString = newString.replaceAll("[^°]", "");
        return Arrays.asList(newString.split("°"));
    }

    /**
     * Method used to add a boolean expression as a sub-expression
     *
     * @param booleanExpression the {@link BooleanExpression} instance to be added as a sub-expression
     */
    default void addChild(BooleanExpression booleanExpression) {
        throw new IllegalArgumentException("Cannot add in Leaf");
    }

    /**
     * Method used to remove a boolean expression from its sub-expressions
     *
     * @param booleanExpression the {@link BooleanExpression} instance to be removed from its sub-expressions
     * @return the {@link BooleanExpression} to be removed
     */
    default BooleanExpression removeChild(BooleanExpression booleanExpression) {
        throw new IllegalArgumentException("Cannot remove in Leaf");
    }

    List<FaultMode> extractIncomingFaults();

    boolean compute();

    String toString();

    String toSimpleString();

    String toBracketFormat();
}
