package yuzunyannn.elementalsorcery.item.book;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.building.ArcInfo;
import yuzunyannn.elementalsorcery.building.BlockItemTypeInfo;
import yuzunyannn.elementalsorcery.building.BuildingBlocks;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.capability.Spellbook;
import yuzunyannn.elementalsorcery.entity.EntityBlockMove;
import yuzunyannn.elementalsorcery.render.item.RenderItemSpellbook;
import yuzunyannn.elementalsorcery.render.item.SpellbookRenderInfo;

public class ItemSpellbookArchitecture extends ItemSpellbook {
	public ItemSpellbookArchitecture() {
		this.setUnlocalizedName("spellbookArchitecture");
	}

	@Override
	public boolean spellBegin(World world, EntityLivingBase entity, ItemStack stack, Spellbook book) {
		if (!(entity instanceof EntityPlayer)) return false;
		ItemStack arc = findArchitectureCrystal((EntityPlayer) entity);
		if (arc.isEmpty()) return false;
		ArcInfo info = new ArcInfo(arc, world.isRemote ? Side.CLIENT : Side.SERVER);
		if (info.isMiss()) return false;
		if (entity.getDistanceSq(info.pos) >= 32 * 32) return false;
		if (world.isRemote) return true;
		book.obj = info.building.getBuildingIterator().setPosOff(info.pos).setFace(info.facing);
		return true;
	}

	@Override
	public void spelling(World world, EntityLivingBase entity, ItemStack stack, Spellbook book, int power) {
		if (world.isRemote) {
			this.giveMeParticleAboutSpelling(world, entity, stack, book, power);
			return;
		}
		if (book.obj == null) { return; }
		if (power < this.getCast(book)) return;
		BuildingBlocks blocks = ((BuildingBlocks) book.obj);
		while (book.obj != null) {
			if (blocks.next()) {
				// 获取变量
				BlockPos pos = blocks.getPos();
				IBlockState state = blocks.getState();
				ItemStack need = blocks.getItemStack();
				// 如果这块不是符合要求的
				if (!world.getBlockState(pos).equals(state)) {
					if (entity instanceof EntityPlayer) {
						// 如果不是创造模式
						if (!((EntityPlayer) entity).isCreative()) {
							ItemStack the = BlockItemTypeInfo.getItemStackCanUsed(((EntityPlayer) entity).inventory,
									need);
							if (the.getCount() < need.getCount()) continue;
							need = the.splitStack(need.getCount());
						}
					}
					Vec3d from = entity.getPositionVector();
					from = from.addVector(MathHelper.sin(rand.nextFloat() * 6.28f) * 5, rand.nextDouble() + 1,
							MathHelper.sin(rand.nextFloat() * 6.28f) * 5);
					EntityBlockMove toBlock = new EntityBlockMove(world, ((EntityPlayer) entity), from, pos, need,
							state);
					if (state.isOpaqueCube()) {
						if (pos.getY() > from.y) toBlock.getTrace().setOrder(rand.nextBoolean() ? "yxz" : "yzx");
						else toBlock.getTrace().setOrder(rand.nextBoolean() ? "xzy" : "zxy");
					}
					if (((EntityPlayer) entity).isCreative())
						toBlock.setFlag(EntityBlockMove.FLAG_FORCE_DESTRUCT, true);
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
	public void spellEnd(World world, EntityLivingBase entity, ItemStack stack, Spellbook book, int power) {
		book.obj = null;
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

				if (this.isArchitectureCrystal(itemstack)) { return itemstack; }
			}
			return ItemStack.EMPTY;
		}
	}

	private boolean isArchitectureCrystal(ItemStack stack) {
		if (ArcInfo.isArc(stack)) return true;
		return false;
	}

}
