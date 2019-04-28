package yuzunyan.elementalsorcery.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyan.elementalsorcery.capability.Spellbook;
import yuzunyan.elementalsorcery.crafting.ICraftingLaunch;
import yuzunyan.elementalsorcery.entity.EntityCrafting;
import yuzunyan.elementalsorcery.render.item.RenderItemSpellbook;
import yuzunyan.elementalsorcery.tile.TileStaticMultiBlock;
import yuzunyan.elementalsorcery.util.WorldHelper;

public class ItemSpellbookLaunch extends ItemSpellbook {
	public ItemSpellbookLaunch() {
		this.setUnlocalizedName("spellbookLaunch");
	}

	@SideOnly(Side.CLIENT)
	@Override
	protected void initRenderInfo(RenderItemSpellbook.RenderInfo info) {
		info.texture = RenderItemSpellbook.instance.TEXTURE_SPELLBOOK_LAUNCH;
	}

	public int getCast(Spellbook book) {
		return 10;
	}

	@Override
	public boolean spellBegin(World world, EntityLivingBase entity, ItemStack stack, Spellbook book) {
		book.cast_time = -1;
		return true;
	}

	@Override
	public void spelling(World world, EntityLivingBase entity, ItemStack stack, Spellbook book, int power) {
		RayTraceResult result = WorldHelper.getLookAtBlock(world, entity, 16);
		if (result == null) {
			book.cast_time = -1;
			return;
		}
		BlockPos pos = result.getBlockPos();
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof ICraftingLaunch) {
			if (!world.isRemote) {
				if (this.setAndWait(tile, book, power)) {
					book.finishSpelling(world, entity);
					ICraftingLaunch.CraftingType type = ICraftingLaunch.CraftingType.ELEMENT_CRAFTING;
					ICraftingLaunch crafting = (ICraftingLaunch) tile;
					if (crafting.isWorking())
						return;
					if (crafting.checkType(type)) {
						// 开始Crafting！
						if (crafting.craftingBegin(type)) {
							if (tile instanceof TileStaticMultiBlock) {
								if (entity instanceof EntityPlayer)
									((TileStaticMultiBlock) tile).setPlayer((EntityPlayer) entity);
							}
							EntityCrafting ecrafting = new EntityCrafting(world, pos, type);
							world.spawnEntity(ecrafting);
						}
					} else {
						// 错误的类型
						book.cast_time = -1;
						world.createExplosion(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 1.0f, false);
					}
				}
			}
		} else {
			book.cast_time = -1;
			return;
		}
		if (world.isRemote) {
			this.giveMeParticleAboutSpelling(world, entity, stack, book, power);
			this.giveMeParticleGoToPos(world, entity, book, pos, power);
			return;
		}
	}

	private boolean setAndWait(TileEntity tile, Spellbook book, int power) {
		if (book.cast_time < 0) {
			book.cast_time = power;
		}
		boolean ok = true;
		if (power - book.cast_time > 30) {
			if (tile instanceof TileStaticMultiBlock) {
				ok = ((TileStaticMultiBlock) tile).isIntact();
			}
			return ok;
		}
		return false;
	}
}
