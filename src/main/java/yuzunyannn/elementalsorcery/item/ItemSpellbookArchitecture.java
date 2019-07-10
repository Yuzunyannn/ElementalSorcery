package yuzunyannn.elementalsorcery.item;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ability.IElementInventory;
import yuzunyannn.elementalsorcery.building.ArcInfo;
import yuzunyannn.elementalsorcery.building.Building;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.capability.Spellbook;
import yuzunyannn.elementalsorcery.entity.EntityBlockThrowEffect;
import yuzunyannn.elementalsorcery.render.item.RenderItemSpellbook;
import yuzunyannn.elementalsorcery.render.item.SpellbookRenderInfo;

public class ItemSpellbookArchitecture extends ItemSpellbook {
	public ItemSpellbookArchitecture() {
		this.setUnlocalizedName("spellbookArchitecture");
	}

	@Override
	public boolean spellBegin(World world, EntityLivingBase entity, ItemStack stack, Spellbook book) {
		if (!(entity instanceof EntityPlayer))
			return false;
		ItemStack arc = findArchitectureCrystal((EntityPlayer) entity);
		if (arc.isEmpty())
			return false;
		ArcInfo info = new ArcInfo(arc, world.isRemote ? Side.CLIENT : Side.SERVER);
		if (info.isMiss())
			return false;
		if (entity.getDistanceSq(info.pos) >= 32 * 32)
			return false;
		if (world.isRemote)
			return true;
		book.obj = info.building.getBuildingBlocks().setPosOff(info.pos).setFace(info.facing);
		return true;
	}

	@Override
	public void spelling(World world, EntityLivingBase entity, ItemStack stack, Spellbook book, int power) {
		if (world.isRemote) {
			this.giveMeParticleAboutSpelling(world, entity, stack, book, power);
			return;
		}
		if (book.obj == null) {
			return;
		}
		if (power < this.getCast(book))
			return;
		Building.BuildingBlocks blocks = ((Building.BuildingBlocks) book.obj);
		while (book.obj != null) {
			if (blocks.next()) {
				// 获取变量
				BlockPos pos = blocks.getPos();
				IBlockState state = blocks.getState();
				ItemStack need = blocks.getItemStack();
				// 如果这块不是符合要求的
				if (!world.getBlockState(pos).equals(state)) {
					// 如果不是创造模式
					if (!((EntityPlayer) entity).isCreative()) {
						ItemStack the = this.hasItem(((EntityPlayer) entity), need);
						if (the.isEmpty()) {
							continue;
						}
						the.shrink(1);
					}
					EntityBlockThrowEffect toBlock = new EntityBlockThrowEffect(world,
							entity.getPositionVector().addVector(0, 1, 0), pos, need, state, ((EntityPlayer) entity));
					if (((EntityPlayer) entity).isCreative())
						toBlock.setBreakBlock();
					// 产生实体，发射方块
					world.spawnEntity(toBlock);
					break;
				}
			} else {
				this.finish(world, entity, book);
				break;
			}
		}
	}

	@Override
	public boolean spellEnd(World world, EntityLivingBase entity, ItemStack stack, Spellbook book, int power) {
		book.obj = null;
		return true;
	}

	private void finish(World world, EntityLivingBase entity, Spellbook book) {
		book.obj = null;
		book.finishSpelling(world, entity);
	}

	@Override
	protected IElementInventory getInventory(ItemStack stack) {
		return new ElementInventory(2);
	}

	@SideOnly(Side.CLIENT)
	@Override
	protected void initRenderInfo(SpellbookRenderInfo info) {
		info.texture = RenderItemSpellbook.instance.TEXTURE_SPELLBOOK_ARC;
	}

	// 寻找符合要求的建筑水晶
	private ItemStack findArchitectureCrystal(EntityPlayer player) {
		if (this.isArchitectureCrystal(player.getHeldItem(EnumHand.OFF_HAND))) {
			return player.getHeldItem(EnumHand.OFF_HAND);
		} else if (this.isArchitectureCrystal(player.getHeldItem(EnumHand.MAIN_HAND))) {
			return player.getHeldItem(EnumHand.MAIN_HAND);
		} else {
			for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
				ItemStack itemstack = player.inventory.getStackInSlot(i);

				if (this.isArchitectureCrystal(itemstack)) {
					return itemstack;
				}
			}
			return ItemStack.EMPTY;
		}
	}

	private boolean isArchitectureCrystal(ItemStack stack) {
		if (ArcInfo.isArc(stack))
			return true;
		return false;
	}

	private ItemStack hasItem(EntityPlayer player, ItemStack stack) {
		for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
			ItemStack itemstack = player.inventory.getStackInSlot(i);
			if (ItemStack.areItemsEqual(stack, itemstack) && ItemStack.areItemStackTagsEqual(stack, itemstack)) {
				return itemstack;
			}
		}
		return ItemStack.EMPTY;
	}

}
