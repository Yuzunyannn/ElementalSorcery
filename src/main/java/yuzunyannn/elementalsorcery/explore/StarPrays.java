package yuzunyannn.elementalsorcery.explore;

import java.util.ArrayList;
import java.util.List;

import yuzunyannn.elementalsorcery.api.tile.IStarPray;

public class StarPrays {

	public static final List<IStarPray> prays = new ArrayList<>();

	static {
		prays.add(new StarPrayMeal());
		prays.add(new StarPrayHeal());
		prays.add(new StarPrayTool());
		prays.add(new StarPrayRepair());
		prays.add(new StarPrayBattle());
		prays.add(new StarPrayBackHome());
		prays.add(new StarPrayEnchantment());
		prays.add(new StarPrayItemChange());
	}

}
