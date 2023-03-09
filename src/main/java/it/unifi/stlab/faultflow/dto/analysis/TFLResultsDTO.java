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

import java.util.ArrayList;
import java.util.List;

public class TFLResultsDTO {

    private String systemUUID;
    private String errorModeUUID;
    private double timestep;
    private double timeLimit;
    private long elapsedAnalysisTime;
    private List<Double> cdf;

    public TFLResultsDTO(String systemUUID, String errorModeUUID, double timestep, double timeLimit,
                         long elapsedAnalysisTime, double[] cdf) {
        this.systemUUID = systemUUID;
        this.errorModeUUID = errorModeUUID;
        this.timestep = timestep;
        this.timeLimit = timeLimit;
        this.elapsedAnalysisTime = elapsedAnalysisTime;
        this.cdf = new ArrayList<>();

        for (double v : cdf)
            this.cdf.add(v);
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

    public double getTimestep() {
        return timestep;
    }

    public void setTimestep(double timestep) {
        this.timestep = timestep;
    }

    public double getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(double timeLimit) {
        this.timeLimit = timeLimit;
    }

    public long getElapsedAnalysisTime() {
        return elapsedAnalysisTime;
    }

    public void setElapsedAnalysisTime(long elapsedAnalysisTime) {
        this.elapsedAnalysisTime = elapsedAnalysisTime;
    }

    public List<Double> getCdf() {
        return cdf;
    }

    public void setCdf(List<Double> cdf) {
        this.cdf = cdf;
    }
}
