package yuzunyannn.elementalsorcery.parchment;

@Deprecated
public class PageSimpleInfo extends PageSimple {

	private final String info;

	@Deprecated
	public PageSimpleInfo(String name, String info) {
		super(name);
		this.info = info;
	}

	@Override
	public String getContext() {
		return super.getContext() + "." + info;
	}

}
