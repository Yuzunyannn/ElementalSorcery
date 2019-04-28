package yuzunyan.elementalsorcery.parchment;

public class PageSimple extends Page {

	private final String name;

	public PageSimple(String name) {
		this.name = name;
	}

	@Override
	public String getTitle() {
		return "page." + name;
	}

	@Override
	public String getContext() {
		return "page." + name + ".ct";
	}

}
