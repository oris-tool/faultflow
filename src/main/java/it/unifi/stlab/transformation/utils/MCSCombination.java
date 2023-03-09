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

import it.unifi.stlab.transformation.minimalcutset.MinimalCutSet;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Barbuzzi
 */
public class MCSCombination {

    private MCSCombination() {
    }

    public static List<List<MinimalCutSet>> getCombination(List<MinimalCutSet> mcs) {
        List<List<MinimalCutSet>> ret = new ArrayList<>();

        for (int len = 1; len < mcs.size(); len++) {
            combinations(mcs, len, 0, new ArrayList<MinimalCutSet>(len), ret);
        }

        return ret;
    }


    private static void combinations(List<MinimalCutSet> mcs, int len, int startPosition, List<MinimalCutSet> result, List<List<MinimalCutSet>> res) {
        if (len == 0) {

            res.add(result);
            return;
        }
        for (int i = startPosition; i <= mcs.size() - len; i++) {
            result.add(result.size() - len, mcs.get(i));
            combinations(mcs, len - 1, i + 1, result, res);
        }
    }


}
