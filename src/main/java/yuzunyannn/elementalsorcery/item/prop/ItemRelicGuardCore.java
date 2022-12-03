package yuzunyannn.elementalsorcery.item.prop;

import java.util.List;
import java.util.UUID;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.entity.EntityThrow;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class ItemRelicGuardCore extends Item implements EntityThrow.IItemThrowAction {

	public ItemRelicGuardCore() {
		this.setTranslationKey("relicGuardCore");
	}

	@Override
	public void onImpact(EntityThrow entity, RayTraceResult result) {
		Vec3d vec = result.hitVec;
		if (vec == null) return;
		if (entity.world.isRemote) return;
		for (int i = 0; i < entity.getRandom().nextInt(3) + 1; i++)
			entity.entityDropItem(new ItemStack(ESObjects.ITEMS.RELIC_GEM), 0);
		entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.BLOCK_GLASS_BREAK,
				SoundCategory.NEUTRAL, 1, 1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		NBTTagCompound nbt = stack.getSubCompound("cMaster");
		if (nbt == null) return;
		String name = nbt.getString("name");
		tooltip.add(TextFormatting.YELLOW + I18n.format("info.my.master", name));
	}

	static public void setCoreMaster(ItemStack stack, EntityLivingBase master) {
		if (master == null) {
			stack.removeSubCompound("cMaster");
			return;
		}
		NBTTagCompound nbt = stack.getOrCreateSubCompound("cMaster");
		nbt.setUniqueId("uuid", master.getUniqueID());
		nbt.setString("name", master.getName());
	}

	static public UUID getCoreMaster(ItemStack stack) {
		NBTTagCompound nbt = stack.getSubCompound("cMaster");
		if (nbt == null) return null;
		return nbt.getUniqueId("uuid");
	}

	static public void setCoreType(ItemStack stack, int type) {
		NBTTagCompound nbt = ItemHelper.getOrCreateTagCompound(stack);
		nbt.setByte("cType", (byte) type);
	}

	static public int getCoreType(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) return -1;
		return nbt.getInteger("cType");
	}

}
