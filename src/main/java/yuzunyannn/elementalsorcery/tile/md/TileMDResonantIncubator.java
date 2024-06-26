package yuzunyannn.elementalsorcery.tile.md;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;
import yuzunyannn.elementalsorcery.ESData;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.api.IGetItemStack;
import yuzunyannn.elementalsorcery.building.Buildings;
import yuzunyannn.elementalsorcery.building.MultiBlock;
import yuzunyannn.elementalsorcery.config.Config;
import yuzunyannn.elementalsorcery.elf.ElfTime;
import yuzunyannn.elementalsorcery.item.crystal.ItemCrystal;
import yuzunyannn.elementalsorcery.item.crystal.ItemScarletCrystal;
import yuzunyannn.elementalsorcery.logics.EventClient;
import yuzunyannn.elementalsorcery.logics.ITickTask;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.scrappy.EffectResonance;
import yuzunyannn.elementalsorcery.tile.TileLifeDirt;

public class TileMDResonantIncubator extends TileMDBase implements ITickable, IGetItemStack {

	@Config(kind = "tile")
	@Config.NumberRange(min = 0, max = Integer.MAX_VALUE)
	static private int COST_PRE_RESONANT = 20;

	@Override
	protected ItemStackHandler initItemStackHandler() {
		return new ItemStackHandler(1);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return null;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return false;
	}

	protected MultiBlock structure;
	protected boolean ok;

	@Override
	public void onLoad() {
		structure = new MultiBlock(Buildings.RESONANT_INCUBATOR, this, new BlockPos(0, -2, 0));
		structure.addSpecialBlock(new BlockPos(0, 1, 3));
		structure.addSpecialBlock(new BlockPos(2, 1, 2));
		structure.addSpecialBlock(new BlockPos(3, 1, 0));
		structure.addSpecialBlock(new BlockPos(2, 1, -2));
		structure.addSpecialBlock(new BlockPos(0, 1, -3));
		structure.addSpecialBlock(new BlockPos(-2, 1, -2));
		structure.addSpecialBlock(new BlockPos(-3, 1, 0));
		structure.addSpecialBlock(new BlockPos(-2, 1, 2));
	}

	@Override
	public void setStack(ItemStack stack) {
		ItemStack origin = inventory.getStackInSlot(0);
		inventory.setStackInSlot(0, stack);
		if (!ItemStack.areItemsEqualIgnoreDurability(origin, stack)) this.updateToClient(stack.serializeNBT());
	}

	@Override
	public ItemStack getStack() {
		return inventory.getStackInSlot(0);
	}

	/** 每次需求的能量 */
	public int getNeedMagicPreResonance() {
		return COST_PRE_RESONANT;
	}

	// 当前搜索位置
	protected int at = 0;
	// 有没有找到
	protected int notFind = 0;
	// 当前频率
	protected float fre = 50;
	// 上次一的频率差值
	protected float lastFreDiff = 0;
	// 稳定指数
	protected float stable = 100;

	@Override
	public void update() {
		this.autoTransfer();
		if (world.isRemote) return;
		ItemStack liftDirt = inventory.getStackInSlot(0);
		if (liftDirt.isEmpty() || Block.getBlockFromItem(liftDirt.getItem()) != ESObjects.BLOCKS.LIFE_DIRT
				|| TileLifeDirt.hasPlant(liftDirt)) {
			this.reset();
			return;
		}
		// 多方快
		if (tick % 40 == 0) ok = structure.check(EnumFacing.NORTH);
		if (!ok) {
			this.reset();
			return;
		}
		// 一直没找到
		if (notFind > 16) {
			if (tick % 100 == 0) notFind = 0;
			return;
		}
		// 测试条件
		if (tick % 5 != 0) return;
		if (this.magic.getCount() < this.getNeedMagicPreResonance() || this.magic.getPower() < 25) return;
		BlockPos pos = structure.getSpecialBlockPos(at);
		notFind++;
		// 检测是否有水晶
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof IGetItemStack) {
			ItemStack stack = ((IGetItemStack) tile).getStack();
			this.resonance(stack, liftDirt);
		}
		// 下一个
		at = (at + 1) % structure.getSpecialBlockCount();
		// 稳定太低，爆炸
		if (this.stable <= 0) {
			pos = this.pos;
			world.destroyBlock(this.pos, false);
			world.createExplosion(null, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 5, true);
		}
	}

	/** 共鸣 */
	private void resonance(ItemStack stack, ItemStack liftDirt) {
		// 获取水晶
		Item item = stack.getItem();
		if (!(item instanceof ItemCrystal)) return;
		ItemCrystal crystal = (ItemCrystal) stack.getItem();
		// 计算频率
		float fre = crystal.getFrequency(stack);
		// 频率是上一个的话，直接跳过
		if (fre == this.lastFreDiff) return;
		// 找到清零
		notFind = 0;
		// 减少数据
		this.magic.shrink(this.getNeedMagicPreResonance());
		// 共振
		NBTTagCompound nbt = resonance(fre, liftDirt);
		this.updateToClient(nbt);
	}

	private NBTTagCompound resonance(float fre, ItemStack liftDirt) {
		// 计算频率
		float diff = fre - this.fre;
		this.lastFreDiff = fre;
		this.fre += (diff / 4);
		float originStable = this.stable;
		this.stable -= Math.abs(diff / 100.0f * 1.5f);
		// 查找生成植物
		ItemStack crystal = ItemStack.EMPTY;
		if (!liftDirt.isEmpty()) {
			crystal = findCrystal();
			if (!crystal.isEmpty()) TileLifeDirt.setPlant(liftDirt, crystal);
		}
		// 发送数据到客户端
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setByte("reson", (byte) at);
		if (originStable > 75 && stable < 75 || originStable > 50 && stable < 50 || originStable > 25 && stable < 25)
			nbt.setFloat("stable", stable);
		nbt.setFloat("fre", this.fre);
		if (!crystal.isEmpty()) nbt.setBoolean("fin", true);
		return nbt;
	}

	/** 通过一个给定的频率进行共振，对外调用 */
	public void resonance(float fre) {
		if (world.isRemote) return;
		ItemStack liftDirt = inventory.getStackInSlot(0);
		if (liftDirt.isEmpty() || Block.getBlockFromItem(liftDirt.getItem()) != ESObjects.BLOCKS.LIFE_DIRT
				|| TileLifeDirt.hasPlant(liftDirt)) {
			this.reset();
			return;
		}
		NBTTagCompound nbt = resonance(fre, liftDirt);
		nbt.setFloat("reson", -1);
		this.updateToClient(nbt);
	}

	/** 寻找该频率附近的水晶 */
	public ItemStack findCrystal() {
		final float range = 0.4f;
		ArrayList<ItemCrystal> allCrystal = ItemCrystal.getCrysstals();
		float minFreDiff = Float.MAX_VALUE;
		ItemStack minCry = ItemStack.EMPTY;
		for (ItemCrystal crystal : allCrystal) {
			if (crystal == ESObjects.ITEMS.SCARLET_CRYSTAL) {
				float fre = 50;
				ElfTime time = new ElfTime(world);
				int hour = time.getHour();
				if (hour == 0) fre = time.getMinute() / 12f * 100f;
				else fre = (12 - time.getMinute()) / 12f * 100f;
				float diff = Math.abs(fre - this.fre);
				if (diff > range) continue;
				if (diff < minFreDiff) {
					minFreDiff = diff;
					minCry = ItemScarletCrystal.create(fre);
				}
				continue;
			}
			NonNullList<ItemStack> list = NonNullList.create();
			crystal.getSubItems(CreativeTabs.SEARCH, list);
			for (ItemStack stack : list) {
				float fre = crystal.getFrequency(stack);
				float diff = Math.abs(fre - this.fre);
				if (diff > range) continue;
				if (diff < minFreDiff) {
					minFreDiff = diff;
					minCry = stack;
				}
			}
		}
		return minCry.copy();
	}

	private void reset() {
		notFind = 0;
		fre = 50;
		if (this.stable < 75) {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setFloat("stable", 100);
			this.updateToClient(nbt);
		}
		stable = 100;
	}

	@Override
	public boolean canSetStack(ItemStack stack) {
		if (Block.getBlockFromItem(stack.getItem()) != ESObjects.BLOCKS.LIFE_DIRT) return false;
		return !TileLifeDirt.hasPlant(stack);
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return this.writeToNBT(new NBTTagCompound());
	}

	// 更新自定义标签
	@Override
	public void customUpdate(NBTTagCompound nbt) {
		if (nbt.hasKey("id")) inventory.setStackInSlot(0, new ItemStack(nbt));
		if (nbt.hasKey("stable")) this.stable = nbt.getFloat("stable");
		if (nbt.hasKey("fre")) this.fre = nbt.getFloat("fre");
		if (nbt.hasKey("reson")) {
			showEffect(nbt.getByte("reson"));
			resonantData();
		}
		if (nbt.hasKey("fin")) {
			this.reset();
			BlockPos pos = this.pos;
			float note2 = nbt.getFloat("fre") / 100.0f * 25;
			float fr = (float) Math.pow(2.0D, (double) (note2 - 12) / 12.0D);
			ITickTask task = () -> {
				if (Minecraft.getMinecraft().player == null) return ITickTask.END;
				world.playSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, SoundEvents.BLOCK_NOTE_BELL,
						SoundCategory.RECORDS, 2.0F, fr, true);
				return ITickTask.END;
			};
			for (int i = 0; i < 4; i++) EventClient.addTickTask(task, i * 10);
		}
	}

	@SideOnly(Side.CLIENT)
	public float getStable() {
		return stable;
	}

	@SideOnly(Side.CLIENT)
	public void resonantData() {
		if (Minecraft.getMinecraft().player == null) return;
		EntityPlayer player = Minecraft.getMinecraft().player;
		if (player.getDistanceSq(pos) <= 6 * 6) {
			NBTTagCompound nbt = ESData.getRuntimeData(player);
			nbt.setFloat("resFre", this.fre);
		}
	}

	@SideOnly(Side.CLIENT)
	public void showEffect(int at) {
		if (at >= 0) {
			BlockPos pos = structure.getSpecialBlockPos(at);
			EffectResonance effect = new EffectResonance(world, pos.getX() + 0.5f, pos.getY() + 0.4f,
					pos.getZ() + 0.5f);
			Effect.addEffect(effect.setColor(0xffffff));
			// 音符
			float note = 0;
			// 设置颜色
			TileEntity tile = world.getTileEntity(pos);
			if (tile instanceof IGetItemStack) {
				ItemStack stack = ((IGetItemStack) tile).getStack();
				Item item = stack.getItem();
				if (item instanceof ItemCrystal) {
					effect.setColor(((ItemCrystal) item).getColor(stack));
					note = ((ItemCrystal) item).getFrequency(stack) / 100.0f * 25;
				}
			}
			float f = (float) Math.pow(2.0D, (double) (note - 12) / 12.0D);
			world.playSound((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D,
					SoundEvents.BLOCK_NOTE_CHIME, SoundCategory.RECORDS, 0.75F, f, true);
		}
		// 延迟
		EventClient.addTickTask(() -> {
			if (Minecraft.getMinecraft().player == null) return ITickTask.END;
			BlockPos pos = TileMDResonantIncubator.this.pos;
			EffectResonance effect = new EffectResonance(world, pos.getX() + 0.5f, pos.getY() + 0.45f,
					pos.getZ() + 0.5f);
			Effect.addEffect(effect.setColor(PARTICLE_COLOR[0]));
			float note2 = fre / 100.0f * 25;
			float fr = (float) Math.pow(2.0D, (double) (note2 - 12) / 12.0D);
			world.playSound((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D,
					SoundEvents.BLOCK_NOTE_BELL, SoundCategory.RECORDS, 2.0F, fr, true);
			return ITickTask.END;
		}, 5);
	}

}
