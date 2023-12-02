package yuzunyannn.elementalsorcery.potion;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.ChunkPos;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.dungeon.DungeonWorld;

public class PotionDungeonNightmare extends PotionCommon {

	static public boolean inDungeon(EntityPlayer player) {
		DungeonWorld dw = DungeonWorld.getDungeonWorld(player.getEntityWorld());
		return dw.getAreaExcerpt(new ChunkPos(player.getPosition())) != null;
	}

	static public int getNightmareValidLevel(EntityPlayer player) {
		PotionEffect effect = player.getActivePotionEffect(ESObjects.POTIONS.DUNGEON_NIGHTMARE);
		if (effect == null) return -1;
		if (!inDungeon(player)) return -1;
		return effect.getAmplifier() + 1;
	}

	public PotionDungeonNightmare() {
		super(true, 0x262626, "dungeonNightmare");
		iconIndex = 28;
	}

	@Override
	public List<ItemStack> getCurativeItems() {
		return new ArrayList<>();
	}
}
