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

package it.unifi.stlab.faultflow.dto.petrinet;

import java.util.List;

public class PetriNetDto {
    private List<PlaceDto> places;
    private List<TransitionDto> transitions;
    private List<PreconditionDto> preconditions;
    private List<PostconditionDto> postconditions;

    public List<PlaceDto> getPlaces() {
        return places;
    }

    public void setPlaces(List<PlaceDto> places) {
        this.places = places;
    }

    public List<TransitionDto> getTransitions() {
        return transitions;
    }

    public void setTransitions(List<TransitionDto> transitions) {
        this.transitions = transitions;
    }

    public List<PreconditionDto> getPreconditions() {
        return preconditions;
    }

    public void setPreconditions(List<PreconditionDto> preconditions) {
        this.preconditions = preconditions;
    }

    public List<PostconditionDto> getPostconditions() {
        return postconditions;
    }

    public void setPostconditions(List<PostconditionDto> postconditions) {
        this.postconditions = postconditions;
    }
}
