package yuzunyannn.elementalsorcery.item.crystal;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.entity.EntityPortal;
import yuzunyannn.elementalsorcery.explore.ExploreManagement;
import yuzunyannn.elementalsorcery.explore.Explores;

public class ItemNatureCrystal extends ItemCrystal {

	public ItemNatureCrystal() {
		super("natureCrystal", 24.99f, 0xb26e0c);
		this.setMaxStackSize(1);
	}

	/** 获取数据 */
	static public NBTTagCompound getData(ItemStack natureCrystal) {
		if (natureCrystal.isEmpty()) return null;
		return natureCrystal.getSubCompound("natureData");
	}

	static public NBTTagCompound getOrCreateData(ItemStack natureCrystal) {
		return natureCrystal.getOrCreateSubCompound("natureData");
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		// 创造模式快捷的移动
		if (playerIn.isCreative() && playerIn.isSneaking()) {
			ItemStack stack = playerIn.getHeldItem(handIn);
			NBTTagCompound data = getData(stack);
			if (data == null) return super.onItemRightClick(worldIn, playerIn, handIn);
			int worldId = Explores.BASE.getWorldId(data);
			BlockPos to = Explores.BASE.getPos(data);
			EntityPortal.moveTo(playerIn, new Vec3d(to).add(0.5, 0.1, 0.5), worldId);
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
		}
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		ExploreManagement.instance.addInformation(stack, tooltip);
	}
}
