package yuzunyannn.elementalsorcery.item.prop;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import yuzunyannn.elementalsorcery.init.ESInit;

public class ItemBlessingJadePiece extends Item {
	/**
	 * 获取方式 <br>
	 * 0 封藏石有几率掉落 <br>
	 * 1 用噬魂武器击倒怪物有几率掉落 <br>
	 * 2 精灵商人有几率卖 <br>
	 * 3 钓鱼有几率得到 <br>
	 * 4 村民箱子大概率 <br>
	 * 5 研究得到 <br>
	 * 6 烧蓝晶石有概率<br>
	 * 7 任务 <br>
	 */
	public static ItemStack createPiece(int n) {
		return new ItemStack(ESInit.ITEMS.BLESSING_JADE_PIECE, 1, n % 8);
	}

	public ItemBlessingJadePiece() {
		this.setHasSubtypes(true);
		this.setTranslationKey("blessingJadePiece");
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (!this.isInCreativeTab(tab)) return;
//		for (int i = 0; i < 8; i++) items.add(new ItemStack(this, 1, i));
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		return super.getItemStackDisplayName(stack) + " " + (stack.getMetadata() + 1);
	}

	@Override
	public int getMetadata(int damage) {
		return damage;
	}

	public static void onFished(EntityPlayer player, EntityFishHook hook) {
		float luck = player.getLuck();
		float p = 0.04f * (1 + luck / 8f);
		if (player.getRNG().nextFloat() > p) return;

		EntityItem eItem = new EntityItem(player.world, hook.posX, hook.posY, hook.posZ, createPiece(3));
		eItem.setPickupDelay(0);
		eItem.setPosition(hook.posX, hook.posY, hook.posZ);
		Vec3d vec = player.getPositionVector().subtract(eItem.getPositionVector());
		Vec3d speed = vec.scale(0.1);
		eItem.motionX = speed.x;
		eItem.motionY = speed.y + MathHelper.sqrt(vec.length()) * 0.08;
		eItem.motionZ = speed.z;
		player.world.spawnEntity(eItem);
	}
}
