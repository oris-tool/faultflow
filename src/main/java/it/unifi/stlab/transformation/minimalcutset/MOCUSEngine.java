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

package it.unifi.stlab.transformation.minimalcutset;

import it.unifi.stlab.transformation.faulttree.BasicEvent;
import it.unifi.stlab.transformation.faulttree.Gate;
import it.unifi.stlab.transformation.faulttree.Node;

import java.util.*;

/**
 * @author Minarelli
 */
public final class MOCUSEngine {

    private MOCUSEngine() {
    }

    public static MOCUSEngine getInstance() {
        return MOCUSEngineHolder.INSTANCE;
    }

    public List<MinimalCutSet> getMinimalCutSet(Node topEvent) {
        Node top = topEvent.copy();
        List<MinimalCutSet> ret = new ArrayList<>();

        List<List<Node>> cs = init(top);

        List<Set<Node>> css = list2Set(cs);

        css.sort(Comparator.comparingInt((Set s) -> s.size()));

        for (int i = 0; i < css.size(); i++) {
            for (int j = i + 1; j < css.size(); j++) {
                if (css.get(j).containsAll(css.get(i))) {
                    css.remove(j);
                    j = j - 1;
                }
            }
        }

        for (Set<Node> s : css) {
            MinimalCutSet mcs = new MinimalCutSet();
            for (Node n : s) {
                mcs.addNode((BasicEvent) n);
            }
            ret.add(mcs);
        }

        return ret;
    }

    private List<Set<Node>> list2Set(List<List<Node>> cs) {
        List<Set<Node>> ret = new ArrayList<>();

        for (List<Node> n : cs) {
            ret.add(new HashSet<>(n));
        }

        return ret;
    }

    private List<List<Node>> init(Node topEvent) {
        List<List<Node>> ps = topToInitPath(topEvent);

        while (existExpandableGate(ps)) {
            Result r = findElementToExpand(ps);
            CSHelper(r, ps);
        }

        return ps;
    }

    private boolean existExpandableGate(List<List<Node>> list) {
        boolean isPresent = false;
        for (int i = 0; i < list.size() && !isPresent; i++) {
            for (int j = 0; j < list.get(i).size() && !isPresent; j++) {

                if (!list.get(i).get(j).isBasicEvent()) {
                    isPresent = true;
                }
            }
        }

        return isPresent;
    }

    private void rewriteAnd(Node e, List<Node> row, int index) {
        row.remove(index);
        row.addAll(e.getChildren());
        Collections.reverse(row);
    }

    private List<List<Node>> rewriteOr(Node e, List<Node> row, int index) {
        List<List<Node>> newRows = new ArrayList<>();
        row.remove(index);
        for (Node n : e.getChildren()) {
            List<Node> loc = new ArrayList<>();
            loc.add(n);
            loc.addAll(row);
            newRows.add(loc);
        }
        return newRows;
    }

    private void CSHelper(Result res, List<List<Node>> paths) {

        Node e = paths.get(res.getFirst()).get(res.getSecond());
        List<Node> row = paths.get(res.getFirst());

        if (((Gate) e).getGateType() == Gate.GateType.AND) {
            rewriteAnd(e, row, res.getSecond());
        } else {
            paths.remove(res.getFirst());
            List<List<Node>> newRows = rewriteOr(e, row, res.getSecond());
            paths.addAll(newRows);
        }
    }

    private List<List<Node>> topToInitPath(Node te) {
        List<List<Node>> ret = new ArrayList<>();
        if (((Gate) te).getGateType() == Gate.GateType.AND) {
            ret.add(te.getChildren());
        } else {
            for (Node n : te.getChildren()) {
                List<Node> ln = new ArrayList<>();
                ln.add(n);
                ret.add(ln);
            }
        }
        return ret;
    }

    private Result findElementToExpand(List<List<Node>> paths) {
        Result ret = new Result(0, 0);
        boolean found = false;
        for (int i = 0; i < paths.size() && !found; i++) { // for row in paths
            for (int j = 0; j < paths.get(i).size() && !found; j++) {// for e in row
                if (!paths.get(i).get(j).isBasicEvent()) {
                    ret = new Result(i, j);
                    found = true;
                }
            }
        }
        return ret;
    }

    private static class MOCUSEngineHolder {

        private static final MOCUSEngine INSTANCE = new MOCUSEngine();
    }
}

final class Result {

    private final int first;
    private final int second;

    public Result(int first, int second) {
        this.first = first;
        this.second = second;
    }

    public int getFirst() {
        return first;
    }

    public int getSecond() {
        return second;
    }
}
