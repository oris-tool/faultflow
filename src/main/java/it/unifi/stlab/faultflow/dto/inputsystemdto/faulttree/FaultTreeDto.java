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

package it.unifi.stlab.faultflow.dto.inputsystemdto.faulttree;

import java.util.List;

public class FaultTreeDto {

	private List<NodeDto> nodes;
	private List<ParentingDto> parentings;
	private List<String> topEvents;


	public List<NodeDto> getNodes() {
		return nodes;
	}
	public void setNodes(List<NodeDto> nodes) {
		this.nodes = nodes;
	}
	public List<ParentingDto> getParentings() {
		return parentings;
	}
	public void setParentings(List<ParentingDto> parentings) {
		this.parentings = parentings;
	}

	public List<String> getTopEvents() {
		return topEvents;
	}

	public void setTopEvents(List<String> topEvents) {
		this.topEvents = topEvents;
	}
}
