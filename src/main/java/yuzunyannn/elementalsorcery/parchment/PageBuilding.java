package yuzunyannn.elementalsorcery.parchment;

import yuzunyannn.elementalsorcery.building.Building;

public class PageBuilding extends Page {

	public final Building building;
	public final String title;

	public PageBuilding(String title, Building building) {
		this.building = building;
		this.title = title;
	}

	public Building getBuilding() {
		return building;
	}

	@Override
	public String getTitle() {
		return "page." + this.title;
	}
}
