package yuzunyannn.elementalsorcery.tile.md;

import java.util.List;

import javax.annotation.Nonnull;

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
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.tile.IGetItemStack;
import yuzunyannn.elementalsorcery.api.util.NBTTag;
import yuzunyannn.elementalsorcery.api.util.client.RenderFriend;
import yuzunyannn.elementalsorcery.building.ArcInfo;
import yuzunyannn.elementalsorcery.building.Buildings;
import yuzunyannn.elementalsorcery.building.MultiBlock;
import yuzunyannn.elementalsorcery.item.ItemParchment;
import yuzunyannn.elementalsorcery.item.crystal.ItemCrystal;
import yuzunyannn.elementalsorcery.parchment.Page;
import yuzunyannn.elementalsorcery.parchment.Pages;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.scrappy.EffectResonance;

public class TileMDFrequencyMapping extends TileMDBase implements ITickable, IGetItemStack {

	public static final float FREQUENCY_TICK = 1 / 800f;

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
			float at = MathHelper.sin(f * runTick * FREQUENCY_TICK);
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
		if (item == ESObjects.ITEMS.PARCHMENT) return !Pages.isVaild(stack);
		else if (item == ESObjects.ITEMS.ARCHITECTURE_CRYSTAL) return !ArcInfo.isArc(stack);
		return false;
	}

	protected IFrequencyMatcher getMappingMatcher(ItemStack stack) {
		Item item = stack.getItem();
		if (item == ESObjects.ITEMS.PARCHMENT) return new ParchmentMather();
		else if (item == ESObjects.ITEMS.ARCHITECTURE_CRYSTAL) return new BuildingMather();
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
		return RenderFriend.getPartialTicks(lastA, preLastA, partialTicks) / lastSize;
	}

	@SideOnly(Side.CLIENT)
	public void showEndEffect() {
		EffectResonance effect = new EffectResonance(world, pos.getX() + 0.5f, pos.getY() + 0.4f, pos.getZ() + 0.5f);
		Effect.addEffect(effect.setColor((int) (0xffffff * lastA)));
	}

	public static class Vibrate {

		public final float[] frequencies;

		public Vibrate(String str) {
			frequencies = new float[3];
			int hash = str.hashCode();
			frequencies[0] = ((hash & 0xff) % 200) / 2f;
			frequencies[1] = (((hash >> 8) & 0xff) % 200) / 2f;
			frequencies[2] = (((hash >> 16) & 0xff) % 200) / 2f;
		}

		public Vibrate(float[] frequencies) {
			this.frequencies = frequencies;

		}

		public float amplitude(int tick) {
			float a = 0;
			for (float f : frequencies) a += MathHelper.sin(f * tick * FREQUENCY_TICK);
			return a;
		}
	}

	public static abstract class CommonMather<T> implements IFrequencyMatcher {

		public final String type;

		protected T maySuccessObject;
		protected int successTick;
		protected int index = 1;
		protected float accuracy = 0.02f;

		public CommonMather(String type) {
			this.type = type;
		}

		@Override
		public NBTTagCompound serializeStatusToNBT() {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setBoolean(type, true);
			tag.setShort("i", (short) index);
			tag.setFloat("a", accuracy);
			return tag;
		}

		@Override
		public void deserializeStatusToNBT(NBTTagCompound tag) {
			if (tag.getBoolean(type)) {
				index = tag.getInteger("i");
				accuracy = Math.max(0.01f, tag.getFloat("a"));
			}
		}

		@Override
		public ItemStack match(World world, BlockPos pos, float a, float size, int tick) {

			if (maySuccessObject != null) {
				float at = getAmplitude(maySuccessObject, size, tick);
				if (Math.abs(at - a) < accuracy) {
					successTick++;
					if (successTick >= 32) return getResult(maySuccessObject, size, tick);
					return ItemStack.EMPTY;
				} else maySuccessObject = null;
			}

			for (int i = 0; i < 10; i++) {
				T obj = findNext();
				float at = getAmplitude(obj, size, tick);
				if (Math.abs(at - a) < accuracy) {
					maySuccessObject = obj;
					successTick = 0;
					break;
				}
			}
			return ItemStack.EMPTY;
		}

		abstract public float getAmplitude(T obj, float size, int tick);

		@Nonnull
		abstract public ItemStack getResult(T obj, float size, int tick);

		@Nonnull
		abstract public T findNext();
	}

	public static class ParchmentMather extends CommonMather<Page> {
		public ParchmentMather() {
			super("parchment");
		}

		@Override
		public ItemStack getResult(Page obj, float size, int tick) {
			return ItemParchment.getParchment(obj.getId());
		}

		@Override
		public float getAmplitude(Page obj, float size, int tick) {
			return new Vibrate(obj.getId()).amplitude(tick);
		}

		@Override
		public Page findNext() {
			index = index + 1;
			if (index >= Pages.getCount()) {
				index = 2;
				accuracy = accuracy + 0.01f;
			}
			return Pages.getPage(index);
		}
	}

	public static class BuildingMather extends CommonMather<Buildings.VibrateKey> {

		public BuildingMather() {
			super("buiding");
		}

		@Override
		public ItemStack getResult(Buildings.VibrateKey obj, float size, int tick) {
			return ArcInfo.createArcInfoItem(obj.getKey());
		}

		@Override
		public float getAmplitude(Buildings.VibrateKey obj, float size, int tick) {
			return obj.amplitude(tick);
		}

		@Override
		public Buildings.VibrateKey findNext() {
			index = (index + 1) % Buildings.frequencyMapping.size();
			return Buildings.frequencyMapping.get(index);
		}
	}

}
