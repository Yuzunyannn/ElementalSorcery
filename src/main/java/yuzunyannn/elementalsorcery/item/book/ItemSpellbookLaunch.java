package yuzunyannn.elementalsorcery.item.book;

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
import yuzunyannn.elementalsorcery.capability.Spellbook;
import yuzunyannn.elementalsorcery.crafting.ICraftingLaunch;
import yuzunyannn.elementalsorcery.entity.EntityCrafting;
import yuzunyannn.elementalsorcery.render.item.RenderItemSpellbook;
import yuzunyannn.elementalsorcery.render.item.SpellbookRenderInfo;
import yuzunyannn.elementalsorcery.tile.altar.TileStaticMultiBlock;
import yuzunyannn.elementalsorcery.util.text.TextHelper;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

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

	@SideOnly(Side.CLIENT)
	private String getUnlocalizedTypeName(ItemStack stack) {
		String type = this.getBookType(stack);
		String str = TextHelper.castToCamel("launch_" + type);
		return "mantra." + str + ".name";
	}

	@Override
	public boolean spellBegin(World world, EntityLivingBase entity, ItemStack stack, Spellbook book) {
		book.castTime = -1;
		return true;
	}

	@Override
	public void spelling(World world, EntityLivingBase entity, ItemStack stack, Spellbook book, int power) {
		RayTraceResult result = WorldHelper.getLookAtBlock(world, entity, 16);
		if (result == null) {
			book.castTime = -1;
			return;
		}
		BlockPos pos = result.getBlockPos();
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof ICraftingLaunch) {
			if (!world.isRemote) {
				if (this.setAndWait(tile, book, power)) {
					book.finishSpelling(world, entity);
					String type = this.getBookType(stack);
					ICraftingLaunch crafting = (ICraftingLaunch) tile;
					if (crafting.isWorking()) return;
					EntityPlayer player = null;
					if (entity instanceof EntityPlayer) player = (EntityPlayer) entity;
					if (crafting.canCrafting(type, player)) {
						// 开始Crafting！
						EntityCrafting ecrafting = new EntityCrafting(world, pos, type, player);
						world.spawnEntity(ecrafting);
					} else {
						// 不可crafting
						book.castTime = -1;
						world.createExplosion(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 1.0f, false);
					}
				}
			}
		} else {
			book.castTime = -1;
			return;
		}
		if (world.isRemote) {
			this.giveMeParticleAboutSpelling(world, entity, stack, book, power);
			this.giveMeParticleGoToPos(world, entity, book, pos, power);
			return;
		}
	}

	private String getBookType(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) {
			stack.setTagCompound(new NBTTagCompound());
			nbt = stack.getTagCompound();
			nbt.setString("bookType", ICraftingLaunch.TYPE_ELEMENT_CRAFTING);
		}
		return nbt.getString("bookType");
	}

	private void switchBookType(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if (nbt == null) {
			stack.setTagCompound(new NBTTagCompound());
			nbt = stack.getTagCompound();
		}
		String type = nbt.getString("bookType");
		switch (type) {
		case ICraftingLaunch.TYPE_ELEMENT_CRAFTING:
			nbt.setString("bookType", ICraftingLaunch.TYPE_ELEMENT_DECONSTRUCT);
			break;
		case ICraftingLaunch.TYPE_ELEMENT_DECONSTRUCT:
			nbt.setString("bookType", ICraftingLaunch.TYPE_BUILING_RECORD);
			break;
		case ICraftingLaunch.TYPE_BUILING_RECORD:
			nbt.setString("bookType", ICraftingLaunch.TYPE_ELEMENT_CONSTRUCT);
			break;
		case ICraftingLaunch.TYPE_ELEMENT_CONSTRUCT:
			nbt.setString("bookType", ICraftingLaunch.TYPE_ELEMENT_CRAFTING);
			break;
		default:
			nbt.setString("bookType", ICraftingLaunch.TYPE_ELEMENT_CRAFTING);
			break;
		}
	}

	private boolean setAndWait(TileEntity tile, Spellbook book, int power) {
		if (book.castTime < 0) {
			book.castTime = power;
		}
		boolean ok = true;
		if (power - book.castTime > this.getCast(book)) {
			if (tile instanceof TileStaticMultiBlock) {
				ok = ((TileStaticMultiBlock) tile).isIntact();
			}
			return ok;
		}
		return false;
	}
}
