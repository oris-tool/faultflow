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

package it.unifi.stlab.faultflow.dto.analysis;

import it.unifi.stlab.transformation.minimalcutset.MinimalCutSet;

import java.util.ArrayList;
import java.util.List;

public class CutsetCalculationDTO {

    private String systemUUID;
    private String errorModeUUID;
    private List<List<String>> minimalCutSets;

    public CutsetCalculationDTO(String systemUUID, String errorModeUUID, List<MinimalCutSet> minimalCutSets) {
        this.systemUUID = systemUUID;
        this.errorModeUUID = errorModeUUID;
        this.minimalCutSets = new ArrayList<>();

        minimalCutSets.forEach(minimalCutset -> {
            List<String> cutset = new ArrayList<>();
            minimalCutset.getCutSet().forEach(basicEvent -> cutset.add(basicEvent.getDescription()));
            this.minimalCutSets.add(cutset);
        });
    }

    public String getSystemUUID() {
        return systemUUID;
    }

    public void setSystemUUID(String systemUUID) {
        this.systemUUID = systemUUID;
    }

    public String getErrorModeUUID() {
        return errorModeUUID;
    }

    public void setErrorModeUUID(String errorModeUUID) {
        this.errorModeUUID = errorModeUUID;
    }

    public List<List<String>> getMinimalCutSets() {
        return minimalCutSets;
    }

    public void setMinimalCutSets(List<List<String>> minimalCutSets) {
        this.minimalCutSets = minimalCutSets;
    }
}
