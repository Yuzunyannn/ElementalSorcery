package yuzunyannn.elementalsorcery.item;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;

public class ItemCrystal extends Item {

	/** 咒术水晶 */
	static public Item newSpellCrystal() {
		return new ItemCrystal("spellCrystal");
	}

	/** 湛蓝水晶 */
	static public Item newAzureCrystal() {
		return new ItemCrystal("azureCrystal");
	}

	/** 秩序水晶 */
	static public Item newOrderCrystal() {
		return new ItemCrystal("orderCrystal");
	}

	public ItemCrystal() {

	}

	public ItemCrystal(String unlocalizedName) {
		this.setUnlocalizedName(unlocalizedName);
	}

}
