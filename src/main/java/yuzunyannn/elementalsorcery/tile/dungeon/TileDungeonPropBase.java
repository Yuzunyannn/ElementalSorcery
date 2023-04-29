package yuzunyannn.elementalsorcery.tile.dungeon;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;

public abstract class TileDungeonPropBase extends TileDungeonBase {

	public void onHarvest(EntityPlayer player) {

	}

	public void onDrops(NonNullList<ItemStack> drops) {

	}

	public boolean onActivated(EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY,
			float hitZ) {
		return false;
	}

	public void onDestroyed() {

	}

	public void onEntityCollision(Entity entity) {

	}

}
