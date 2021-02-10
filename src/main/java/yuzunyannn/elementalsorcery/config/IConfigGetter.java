package yuzunyannn.elementalsorcery.config;

public interface IConfigGetter {

	public double get(String kind, String group, String name, double def, String note);

	public int get(String kind, String group, String name, int def, String note);

	public String get(String kind, String group, String name, String def, String note);

	public double[] get(String kind, String group, String name, double[] def, String note);

	public int[] get(String kind, String group, String name, int[] def, String note);

	public String[] get(String kind, String group, String name, String[] def, String note);

	public boolean get(String kind, String group, String name, boolean def, String note);

}
