package yuzunyannn.elementalsorcery.tile.md;

import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;
import yuzunyannn.elementalsorcery.api.tile.IGetItemStack;
import yuzunyannn.elementalsorcery.building.Buildings;
import yuzunyannn.elementalsorcery.building.MultiBlock;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.item.ItemParchment;
import yuzunyannn.elementalsorcery.item.crystal.ItemCrystal;
import yuzunyannn.elementalsorcery.parchment.Page;
import yuzunyannn.elementalsorcery.parchment.Pages;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.EffectResonance;
import yuzunyannn.elementalsorcery.util.NBTTag;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;

public class TileMDFrequencyMapping extends TileMDBase implements ITickable, IGetItemStack {

	static public interface IFrequencyMatcher {

		ItemStack match(World world, BlockPos pos, float a, float size, int tick);

		/** 当前匹配的状态记录 */
		NBTTagCompound serializeStatusToNBT();

		void deserializeStatusToNBT(NBTTagCompound tag);

	}

	protected boolean isWorking;
	protected int runTick;
	protected IFrequencyMatcher matcher;
	protected NBTTagCompound matcherStatus;

	protected float preLastA;
	protected float lastA;
	protected int lastSize;

	private MultiBlock structure;

	@Override
	public void onLoad() {
		structure = new MultiBlock(Buildings.MAPPING_ALTAR, this, new BlockPos(0, -2, 0));
	}

	@Override
	protected ItemStackHandler initItemStackHandler() {
		return new ItemStackHandler(1);
	}

	@Override
	public void update() {
		this.autoTransfer();
		runTick++;
		if (!isWorking) {
			if (world.isRemote) return;
			if (runTick % 20 * 2 != 0) return;
			ItemStack stack = this.getStack();
			if (stack.isEmpty()) return;
			isWorking = this.getCurrentCapacity() >= 100 && canMapping(stack) && structure.check(EnumFacing.NORTH);
			if (isWorking) this.sendAndStart();
			return;
		}
		if (!world.isRemote) {
			if (runTick % 20 * 2 != 0) isWorking = structure.check(EnumFacing.NORTH);
			if (isWorking) isWorking = matcher != null && canMapping(this.getStack()) && this.getCurrentCapacity() > 1;
			if (!isWorking) {
				this.sendAndEnd();
				return;
			}
			if (runTick % 4 == 0) this.magicShrink(1);
		}

		float a = 0;
		AxisAlignedBB aabb = new AxisAlignedBB(pos.add(-3, -1, -3), pos.add(3, 1, 3));
		List<EntityItem> list = world.getEntitiesWithinAABB(EntityItem.class, aabb, (entity) -> {
			return entity.getItem().getItem() instanceof ItemCrystal;
		});
		for (EntityItem itemEntity : list) {
			ItemStack itemStack = itemEntity.getItem();
			ItemCrystal crystal = (ItemCrystal) itemStack.getItem();
			itemEntity.setNoDespawn();
			float f = crystal.getFrequency(itemStack);
			float at = MathHelper.sin(f / 800 * runTick);
			itemEntity.posY = at + pos.getY();
			itemEntity.motionY = 0;
			a += at;
		}

		preLastA = lastA;
		lastA = a;
		lastSize = list.size();

		if (world.isRemote) return;
		if (runTick < 20) return;

		// 进行匹配
		ItemStack stack = matcher.match(world, pos, a, list.size(), runTick);
		if (stack.isEmpty()) return;

		isWorking = false;
		inventory.setStackInSlot(0, stack);
		this.sendAndEnd(stack);
	}

	protected void sendAndEnd() {
		this.sendAndEnd(ItemStack.EMPTY);
	}

	protected void sendAndEnd(ItemStack stack) {
		NBTTagCompound nbt = stack.isEmpty() ? new NBTTagCompound() : stack.serializeNBT();
		nbt.setBoolean("end", true);
		matcherStatus = null;
		this.markDirty();
		this.updateToClient(nbt);
	}

	protected void sendAndStart() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setBoolean("start", true);
		runTick = 0;
		matcher = this.getMappingMatcher(getStack());
		if (matcherStatus != null) {
			matcher.deserializeStatusToNBT(matcherStatus);
			matcherStatus = null;
		}
		this.markDirty();
		this.updateToClient(nbt);
	}

	protected boolean canMapping(ItemStack stack) {
		Item item = stack.getItem();
		if (item == ESInit.ITEMS.PARCHMENT) return !Pages.isVaild(stack);
		return false;
	}

	protected IFrequencyMatcher getMappingMatcher(ItemStack stack) {
		Item item = stack.getItem();
		if (item == ESInit.ITEMS.PARCHMENT) return new ParchmentMather();
		return null;
	}

	@Override
	public void setStack(ItemStack stack) {
		ItemStack origin = inventory.getStackInSlot(0);
		inventory.setStackInSlot(0, stack);
		if (!ItemStack.areItemsEqualIgnoreDurability(origin, stack)) {
			this.markDirty();
			this.updateToClient(stack.serializeNBT());
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void customUpdate(NBTTagCompound nbt) {
		if (nbt.hasKey("id")) inventory.setStackInSlot(0, new ItemStack(nbt));
		if (nbt.hasKey("start")) {
			isWorking = true;
			runTick = 0;
		}
		if (nbt.hasKey("end")) {
			isWorking = false;
			if (nbt.hasKey("id")) this.showEndEffect();
		}
	}

	@Override
	public ItemStack getStack() {
		return inventory.getStackInSlot(0);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		if (matcher != null) {
			NBTTagCompound status = matcher.serializeStatusToNBT();
			if (status != null) nbt.setTag("matcher", status);
		}
		return super.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("matcher", NBTTag.TAG_COMPOUND)) matcherStatus = nbt.getCompoundTag("matcher");
		super.readFromNBT(nbt);
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return super.writeToNBT(new NBTTagCompound());
	}

	@SideOnly(Side.CLIENT)
	public float getHigh(float partialTicks) {
		if (lastSize == 0 || !isWorking) return 0;
		return RenderHelper.getPartialTicks(lastA, preLastA, partialTicks) / lastSize;
	}

	@SideOnly(Side.CLIENT)
	public void showEndEffect() {
		EffectResonance effect = new EffectResonance(world, pos.getX() + 0.5f, pos.getY() + 0.4f, pos.getZ() + 0.5f);
		Effect.addEffect(effect.setColor((int) (0xffffff * lastA)));
	}

	public static class ParchmentMather implements IFrequencyMatcher {

		protected Page maySuccess;
		protected int successTick;
		protected int index = 1;
		protected float accuracy = 0.02f;

		private float getPageA(Page page, int tick) {
			int hash = page.getId().hashCode();
			float f1 = (hash & 0xff) % 100;
			float f2 = ((hash >> 8) & 0xff) % 100;
			float f3 = ((hash >> 16) & 0xff) % 100;
			return MathHelper.sin(f1 / 800 * tick) + MathHelper.sin(f2 / 800 * tick) + MathHelper.sin(f3 / 800 * tick);
		}

		@Override
		public ItemStack match(World world, BlockPos pos, float a, float size, int tick) {

			if (maySuccess != null) {
				float at = getPageA(maySuccess, tick);
				if (Math.abs(at - a) < accuracy) {
					successTick++;
					if (successTick >= 32) return ItemParchment.getParchment(maySuccess.getId());
					return ItemStack.EMPTY;
				} else maySuccess = null;
			}

			for (int i = 0; i < 10; i++) {
				index = index + 1;
				if (index >= Pages.getCount()) {
					index = 2;
					accuracy = accuracy + 0.01f;
				}
				Page page = Pages.getPage(index);
				float at = getPageA(page, tick);
				if (Math.abs(at - a) < 0.1) {
					maySuccess = page;
					successTick = 0;
					break;
				}
			}
			return ItemStack.EMPTY;
		}

		@Override
		public NBTTagCompound serializeStatusToNBT() {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean("parchment", true);
			tag.setShort("i", (short) index);
			tag.setFloat("a", accuracy);
			return tag;
		}

		@Override
		public void deserializeStatusToNBT(NBTTagCompound tag) {
			if (tag.hasKey("parchment")) {
				index = tag.getInteger("i");
				accuracy = Math.max(0.01f, tag.getFloat("a"));
			}
		}

	}

}
