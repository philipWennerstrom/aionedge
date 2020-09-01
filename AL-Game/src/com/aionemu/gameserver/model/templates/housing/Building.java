/*
 * This file is part of aion-lightning <aion-lightning.com>.
 *
 *  aion-lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with aion-lightning.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.model.templates.housing;

import javax.xml.bind.annotation.*;

import com.aionemu.gameserver.dataholders.DataManager;
import com.mysql.jdbc.StringUtils;

/**
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "parts" })
@XmlRootElement(name = "building")
public class Building {

  private Parts parts;

	@XmlAttribute(name = "default")
	protected boolean isDefault;

	@XmlAttribute(name = "parts_match")
	protected String partsMatch;

	@XmlAttribute
	protected String size;

	@XmlAttribute
	protected BuildingType type;

	@XmlAttribute(required = true)
	protected int id;

	public boolean isDefault() {
		return isDefault;
	}

	// All methods for DataManager call are just to ensure integrity 
	// if called from housing land templates, because it only has id and isDefault 
	// for the buildings. Buildings template has full info though, except isDefault
	// value for the land.

	public String getPartsMatchTag() {
		if (StringUtils.isNullOrEmpty(partsMatch))
			return DataManager.HOUSE_BUILDING_DATA.getBuilding(id).getPartsMatchTag();
		return partsMatch;
	}

	public String getSize() {
		if (StringUtils.isNullOrEmpty(size))
			return DataManager.HOUSE_BUILDING_DATA.getBuilding(id).getSize();
		return size;
	}

	public BuildingType getType() {
		if (type == null)
			return DataManager.HOUSE_BUILDING_DATA.getBuilding(id).getType();
		return type;
	}

	public int getId() {
		return id;
	}

	public Parts getDefaultParts() {
		if (parts == null)
			return DataManager.HOUSE_BUILDING_DATA.getBuilding(id).getDefaultParts();
		return parts;
	}

}
