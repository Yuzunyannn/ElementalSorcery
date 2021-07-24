package yuzunyannn.elementalsorcery.tile.altar;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.tile.IAltarWake;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.item.book.ItemSpellbook;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementFly;
import yuzunyannn.elementalsorcery.tile.TileEntityNetwork;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.obj.Vertex;

public class TileElementalCube extends TileEntityNetwork implements ITickable, IAltarWake {

	// 根据仓库和所需，获取一个元素
	public static ElementStack getAndTestElementTransBetweenInventory(ElementStack need, IElementInventory inv,
			IElementInventory inv_other) {
		if (need.isEmpty()) {
			// 如果书其他空闲空间，那就随便找出一个元素来
			for (int a = 0; a < inv_other.getSlots(); a++) {
				ElementStack other_estack = inv_other.getStackInSlot(a).copy();
				other_estack.setCount(1);
				if (other_estack.isEmpty()) continue;
				ElementStack extract = inv_other.extractElement(other_estack, true);
				if (extract.arePowerfulAndMoreThan(other_estack)) {
					if (inv.insertElement(extract, true)) { return extract.copy(); }
				}
			}
		} else {
			// 如果书其他空闲空间，查看是否拥有指定元素
			ElementStack extract = inv_other.extractElement(need, true);
			if (extract.arePowerfulAndMoreThan(need)) {
				if (inv.insertElement(extract, true)) { return extract.copy(); }
			}
		}
		return ElementStack.EMPTY;
	}

	// 获取一次元素动画
	@SideOnly(Side.CLIENT)
	public static void giveParticleElementTo(World world, int color, Vec3d from, Vec3d pto, float possibility) {
		Random rand = world.rand;
		if (rand.nextFloat() > possibility) return;
		EffectElementFly effect;
		if (pto.y > from.y) {
			from = from.addVector(rand.nextDouble() - 0.5, rand.nextDouble() - 0.5, rand.nextDouble() - 0.5);
			pto = pto.addVector(rand.nextDouble() - 0.5, 0, rand.nextDouble() - 0.5);
		} else {
			from = from.addVector(rand.nextDouble() * 0.5 - 0.25, rand.nextDouble() * 0.5 - 0.25,
					rand.nextDouble() * 0.5 - 0.25);
		}
		effect = new EffectElementFly(world, from, pto);
		effect.setColor(color);
		yuzunyannn.elementalsorcery.render.effect.Effect.addEffect(effect);

	}

	// 保存句柄
	private static IStorage<IElementInventory> storage = ElementInventory.ELEMENTINVENTORY_CAPABILITY.getStorage();
	// 仓库
	private IElementInventory inventory = new ElementInventory();

	// 拥有能力
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (ElementInventory.ELEMENTINVENTORY_CAPABILITY == capability) { return true; }
		return super.hasCapability(capability, facing);
	}

	// 获取能力
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (ElementInventory.ELEMENTINVENTORY_CAPABILITY == capability) { return (T) inventory; }
		return super.getCapability(capability, facing);
	}

	// 加载
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		storage.readNBT(ElementInventory.ELEMENTINVENTORY_CAPABILITY, inventory, null,
				compound.getCompoundTag("inventory"));
		if (this.isSending()) {
			if (ElementHelper.isEmpty(inventory)) this.wake = 0;
		}
	}

	// 保存
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setTag("inventory", storage.writeNBT(ElementInventory.ELEMENTINVENTORY_CAPABILITY, inventory, null));
		return super.writeToNBT(compound);
	}

	// 唤醒
	@Override
	public boolean wake(int type, @Nullable BlockPos from) {
		if (!this.world.isRemote) return true;
		this.wake = 80;
		this.markDirty();
		return true;
	}

	@Override
	public void onEmpty() {
		if (!world.isRemote) this.updateToClient();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateEffect(World world, int type, ElementStack estack, Vec3d pos) {
		Vec3d myPos = new Vec3d(this.pos).addVector(0.5, 0.25 + 0.5, 0.5);
		if (type == IAltarWake.SEND) giveParticleElementTo(world, estack.getColor(), myPos, pos, 1);
		else giveParticleElementTo(world, estack.getColor(), pos, myPos, 1);
	}

	// 设置仓库
	public void setElementInventory(IElementInventory inventory) {
		this.inventory = inventory;
		if (world.isRemote) changeColor();
	}

	// 更新规则
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public void update() {
		if (world.isRemote) tick();
		else {

		}
	}

	// 默认颜色
	public static final Vertex ORIGIN_COLOR = new Vertex(29.0F / 255, 115.0F / 255, 109.0F / 255);
	// 默认旋转角度，每tick
	public static final float SOTATE_PRE_TICK = (float) (Math.PI / 4) / 20.0f;
	// 默认站立来的比率增加量
	public static final float WAKE_UP_RARE = 0.025f;
	// 站起来的状态剩余tick
	public int wake = 0;
	// 站立来的比率0-1
	public float wakeRate = 0.0f;
	public float preWakeRate = 0.0f;

	public Vertex color = ORIGIN_COLOR;
	public float colorRate = 0.0f;
	public float detlaCr = 0.01f;

	@SideOnly(Side.CLIENT)
	public void tick() {
		preWakeRate = wakeRate;
		if (wake > 0) {
			if (color == ORIGIN_COLOR) changeColor();
			// 站起来的比率
			wakeRate = MathHelper.clamp(wakeRate + WAKE_UP_RARE, 0, 1);
			// 颜色转变
			colorRate += detlaCr;
			if (colorRate >= 1.0f || colorRate <= 0.0f) {
				detlaCr = -detlaCr;
				if (colorRate <= 0.0f) changeColor();
			}
			wake--;
		} else wakeRate = MathHelper.clamp(wakeRate - WAKE_UP_RARE, 0, 1);
		colorRate *= wakeRate;
	}

	// 切换颜色
	@SideOnly(Side.CLIENT)
	private void changeColor() {
		if (color == ORIGIN_COLOR) color = new Vertex(ORIGIN_COLOR);
		ElementStack estack = ItemSpellbook.giveMeRandomElement(inventory);
		int ecolor = color.toColor();
		if (!estack.isEmpty()) ecolor = estack.getElement().getColor(estack);
		color.toColor(ecolor);
	}

}
