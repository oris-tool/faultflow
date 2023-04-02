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

package it.unifi.stlab.transformation.utils;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Parser used on activation functions to get a list of entities and sub-entities (such as basic events, gates, top
 * events, ...) to be created and represented in fault trees.
 */
public class ActivationFunctionParser {

    /**
     * "Top-level" method that calls for the creation of entities and returns a mapping between the activation function
     * (or sub-function) with an List of strings representation of its contained sub-entities.
     */
    public static Map<String, List<String>> getActivationFunctionEntities(String activationFunction) {
        Map<String, List<String>> activationFunctionEntities = new LinkedHashMap<>();

        List<String> subFunctions = getSubFunctions(activationFunction);

        for (String subFunction : subFunctions)
            if(subFunction != null)
                activationFunctionEntities.put(removeOuterParentheses(subFunction), getFunctionEntities(subFunction));

        return activationFunctionEntities;
    }

    /**
     * Starting from an activation function, this method creates a list of sub-function, with a topological order than
     * first lists sub-functions that are "simple" and depend only on themselves, then all other functions: these are
     * ordered so that all their dependent sub-functions have already been listed previously in the returned list.
     */
    private static List<String> getSubFunctions(String activationFunction) {
        BiMap<String, Interval> subFunctions = HashBiMap.create();
        List<Interval> parentheses = getParenthesesIntervals(activationFunction);

        parentheses.forEach(subFunction -> subFunctions.put(activationFunction.substring(subFunction.getLower(),
                subFunction.getUpper() + 1), subFunction));

        List<String> sortedParentheses = new ArrayList<>();

        while (!parentheses.isEmpty()) {
            for (int index = 0; index < parentheses.size(); index++) {
                Interval cycleInterval = parentheses.get(index);

                /* Check if the interval is contained within another: if an interval is not contained by another,
                it is a step "higher" than all others remaining, and these ordering creates the topological order */
                if (parentheses.stream().noneMatch(interval -> interval.contains(cycleInterval))) {
                    sortedParentheses.add(subFunctions.inverse().get(cycleInterval));
                    parentheses.remove(index);
                    index--;
                }
            }
        }


        return sortedParentheses;
    }

    /**
     * Private utility function that extracts entities from a function or sub-function; it then returns them as a list
     */
    private static List<String> getFunctionEntities(String function) {
        // Polish the function by removing its outermost parentheses
        String polishedFunction = removeOuterParentheses(function);
        List<String> functionEntities = new ArrayList<>();

        // Retrieve the String start and end indexes of each sub-function, identified by its parentheses
        List<Interval> parentheses = getParenthesesIntervals(polishedFunction);

        /* Use a predicate to obtain a List of parentheses that are not contained by any other interval, so that we can
        identify top-level sub-functions in the given function */
        Predicate<Interval> ifNotContained = interval ->
                parentheses.stream().noneMatch(innerInterval -> innerInterval.contains(interval));
        List<Interval> topLevelParentheses = parentheses.stream().filter(ifNotContained).collect(Collectors.toList());

        // Add these intervals as entities of the given function
        for (Interval interval : topLevelParentheses) {
            functionEntities.add(removeOuterParentheses(polishedFunction.substring(interval.getLower(), interval.getUpper() + 1)));
        }

        // Remove the entities already added to the List from the starting polished function
        for (String entity : functionEntities) {
            polishedFunction = polishedFunction.replace(entity, "");
        }

        /* We now check what is left in the polished function, since we already extracted the whole sub-entities from
        the polished function: if it is a gate, we will find its symbolic representation such as && or ||, otherwise the
        function was a simple basic event and nothing more must be done */
        if (polishedFunction.contains("&&")) {
            functionEntities.add(0, "AND");
        } else if (polishedFunction.contains("||")) {
            functionEntities.add(0, "OR");
        } else if (polishedFunction.contains("!")) {
            functionEntities.add(0, polishedFunction.split("!")[1]);
            functionEntities.add(0, "NOT");
        }
        else if (polishedFunction.contains("KOUTOFN")) {
            List<String> subEntities = new ArrayList<>(Arrays.asList(polishedFunction.split("KOUTOFN\\[(.*?)\\]")[1].split(",")));
            subEntities.remove(0);
            functionEntities.addAll(0, subEntities);

            Pattern pattern = Pattern.compile("KOUTOFN\\[(.*?)\\]");
            Matcher matcher = pattern.matcher(polishedFunction);

            if (matcher.find()) {
                functionEntities.add(0, matcher.group());
            } else {
                throw new RuntimeException(); // FIXME
            }
        }

        return functionEntities;
    }

    /**
     * Method used to obtain a list of intervals indicating the starting and ending points of activation functions and
     * sub-functions.
     */
    private static List<Interval> getParenthesesIntervals(String activationFunction) {
        char[] activationFunctionChars = activationFunction.toCharArray();
        Deque<Integer> lowerIndexes = new ArrayDeque<>();
        List<Interval> parenthesesIntervals = new ArrayList<>();

        for (int index = 0; index < activationFunction.length(); index++) {
            if (activationFunctionChars[index] == '(') {
                lowerIndexes.addLast(index);
            } else if (activationFunctionChars[index] == ')' && !lowerIndexes.isEmpty()) {
                parenthesesIntervals.add(new Interval(lowerIndexes.removeLast(), index));
            }
        }

        return parenthesesIntervals;
    }

    /**
     * Method that strips the outermost parentheses of an activation function so that a polished string containing only
     * entities and gates (with eventual sub-gate indicated by other parentheses) can be obtained.
     */
    private static String removeOuterParentheses(String activationFunction) {
        char[] chars = activationFunction.toCharArray();
        int openParentheses = 0;

        List<Integer> parenthesesIndexes = new ArrayList<>();

        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '(')
                openParentheses++;
            else if (chars[i] == ')')
                openParentheses--;

            if (parenthesesIndexes.size() % 2 == 0 && openParentheses == 1)
                parenthesesIndexes.add(i + 1);
            else if (parenthesesIndexes.size() % 2 != 0 && openParentheses == 0)
                parenthesesIndexes.add(i);
        }

        StringBuilder polishedString = new StringBuilder();
        for (int index = 0; index < parenthesesIndexes.size(); index += 2)
            polishedString.append(activationFunction, parenthesesIndexes.get(index), parenthesesIndexes.get(index + 1));

        return polishedString.toString();
    }
}
