package yuzunyannn.elementalsorcery.parchment;

import yuzunyannn.elementalsorcery.building.Building;

public class PageBuildingSimple extends PageBuilding {

	final String title;

	public PageBuildingSimple(String title, Building building) {
		super(building);
		this.title = title;
	}

	@Override
	public String getTitle() {
		return title;
	}
}
