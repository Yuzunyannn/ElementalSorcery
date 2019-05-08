package yuzunyan.elementalsorcery.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyan.elementalsorcery.capability.Spellbook;
import yuzunyan.elementalsorcery.crafting.ICraftingLaunch;
import yuzunyan.elementalsorcery.entity.EntityCrafting;
import yuzunyan.elementalsorcery.render.item.SpellbookRenderInfo;
import yuzunyan.elementalsorcery.render.item.RenderItemSpellbook;
import yuzunyan.elementalsorcery.tile.TileStaticMultiBlock;
import yuzunyan.elementalsorcery.util.WorldHelper;

public class ItemSpellbookLaunch extends ItemSpellbook {
	public ItemSpellbookLaunch() {
		this.setUnlocalizedName("spellbookLaunch");
	}

	@SideOnly(Side.CLIENT)
	@Override
	protected void initRenderInfo(SpellbookRenderInfo info) {
		info.texture = RenderItemSpellbook.instance.TEXTURE_SPELLBOOK_LAUNCH;
	}

	@Override
	public int getCast(Spellbook book) {
		return 30;
	}

	@Override
	public void swap(World world, EntityLivingBase entity, ItemStack stack, Spellbook book) {
		this.switchBookType(stack);
		if (world.isRemote && entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			player.sendMessage(new TextComponentTranslation(this.getUnlocalizedTypeName(stack)));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		tooltip.add(TextFormatting.GOLD + I18n.format("info.launchbook.shift"));
		tooltip.add(TextFormatting.DARK_AQUA + I18n.format(this.getUnlocalizedTypeName(stack)));
	}

	private String getUnlocalizedTypeName(ItemStack stack) {
		ICraftingLaunch.CraftingType type = this.getBookType(stack);
		String str = "";
		switch (type) {
		case ELEMENT_CRAFTING:
			str = "info.launchbook.craft";
			break;
		case ELEMENT_DECONSTRUCT:
			str = "info.launchbook.dec";
			break;
		}
		return str;
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
					ICraftingLaunch.CraftingType type = this.getBookType(stack);
					ICraftingLaunch crafting = (ICraftingLaunch) tile;
					if (crafting.isWorking())
						return;
					if (crafting.checkType(type)) {
						EntityPlayer player = null;
						if (entity instanceof EntityPlayer)
							player = (EntityPlayer) entity;
						// 开始Crafting！
						if (crafting.craftingBegin(type, player)) {
							EntityCrafting ecrafting = new EntityCrafting(world, pos, type, player);
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

	private ICraftingLaunch.CraftingType getBookType(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) {
			stack.setTagCompound(new NBTTagCompound());
			nbt = stack.getTagCompound();
		}
		return ICraftingLaunch.CraftingType.values()[nbt.getInteger("bookType")];
	}

	private void switchBookType(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) {
			stack.setTagCompound(new NBTTagCompound());
			nbt = stack.getTagCompound();
		}
		ICraftingLaunch.CraftingType type = ICraftingLaunch.CraftingType.values()[nbt.getInteger("bookType")];
		switch (type) {
		case ELEMENT_CRAFTING:
			nbt.setInteger("bookType", ICraftingLaunch.CraftingType.ELEMENT_DECONSTRUCT.ordinal());
			break;
		case ELEMENT_DECONSTRUCT:
			nbt.setInteger("bookType", ICraftingLaunch.CraftingType.ELEMENT_CRAFTING.ordinal());
			break;
		}
	}

	private boolean setAndWait(TileEntity tile, Spellbook book, int power) {
		if (book.cast_time < 0) {
			book.cast_time = power;
		}
		boolean ok = true;
		if (power - book.cast_time > this.getCast(book)) {
			if (tile instanceof TileStaticMultiBlock) {
				ok = ((TileStaticMultiBlock) tile).isIntact();
			}
			return ok;
		}
		return false;
	}
}
