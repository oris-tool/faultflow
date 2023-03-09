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

public class ImportanceMeasureDTO {

    private String importanceMeasure;
    private String systemUUID;
    private String errorModeUUID;
    private long elapsedAnalysisTime;
    private int evaluationTime;
    private double timestep;
    private List<FaultImportanceMeasureDTO> values;

    public ImportanceMeasureDTO(String importanceMeasure, String systemUUID, String errorModeUUID,
                                long elapsedAnalysisTime, int evaluationTime, double timestep) {
        this.importanceMeasure = importanceMeasure;
        this.systemUUID = systemUUID;
        this.errorModeUUID = errorModeUUID;
        this.evaluationTime = evaluationTime;
        this.timestep = timestep;
        values = new ArrayList<>();
    }

    public void addFaultImportanceMeasure(FaultImportanceMeasureDTO faultImportanceMeasureDTO) {
        values.add(faultImportanceMeasureDTO);
    }

    public String getImportanceMeasure() {
        return importanceMeasure;
    }

    public void setImportanceMeasure(String importanceMeasure) {
        this.importanceMeasure = importanceMeasure;
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

    public long getElapsedAnalysisTime() {
        return elapsedAnalysisTime;
    }

    public void setElapsedAnalysisTime(long elapsedAnalysisTime) {
        this.elapsedAnalysisTime = elapsedAnalysisTime;
    }

    public int getEvaluationTime() {
        return evaluationTime;
    }

    public void setEvaluationTime(int evaluationTime) {
        this.evaluationTime = evaluationTime;
    }

    public double getTimestep() {
        return timestep;
    }

    public void setTimestep(double timestep) {
        this.timestep = timestep;
    }

    public List<FaultImportanceMeasureDTO> getValues() {
        return values;
    }

    public void setValues(List<FaultImportanceMeasureDTO> values) {
        this.values = values;
    }
}
