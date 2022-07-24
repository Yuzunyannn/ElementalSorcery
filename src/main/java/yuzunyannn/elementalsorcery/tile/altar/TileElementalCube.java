package yuzunyannn.elementalsorcery.tile.altar;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.tile.IAltarWake;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.block.altar.BlockElementCube;
import yuzunyannn.elementalsorcery.capability.ElementInventory;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.item.book.ItemSpellbook;
import yuzunyannn.elementalsorcery.render.effect.batch.EffectElementFly;
import yuzunyannn.elementalsorcery.tile.TileEntityNetwork;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.element.ElementInventoryStronger;
import yuzunyannn.elementalsorcery.util.helper.ColorHelper;

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
			from = from.add(rand.nextDouble() - 0.5, rand.nextDouble() - 0.5, rand.nextDouble() - 0.5);
			pto = pto.add(rand.nextDouble() - 0.5, 0, rand.nextDouble() - 0.5);
		} else {
			from = from.add(rand.nextDouble() * 0.5 - 0.25, rand.nextDouble() * 0.5 - 0.25,
					rand.nextDouble() * 0.5 - 0.25);
		}
		effect = new EffectElementFly(world, from, pto);
		effect.setColor(color);
		yuzunyannn.elementalsorcery.render.effect.Effect.addEffect(effect);

	}

	// 仓库
	protected ElementInventoryStronger inventory = new ElementInventoryStronger();

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (ElementInventory.ELEMENTINVENTORY_CAPABILITY == capability) return true;
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (ElementInventory.ELEMENTINVENTORY_CAPABILITY == capability) return (T) inventory;
		return super.getCapability(capability, facing);
	}

	@Override
	protected void setWorldCreate(World worldIn) {
		this.setWorld(worldIn);
	}

	// 加载
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		inventory.deserializeNBT(nbt.getCompoundTag("inventory"));
		if (nbt.hasKey("color")) setDyeColor(EnumDyeColor.byMetadata(nbt.getByte("color")));
		if (isSending()) {
			if (ElementHelper.isEmpty(inventory)) this.wake = 0;
			if (world.isRemote) changeColor();
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setTag("inventory", inventory.serializeNBT());
		if (dyeColor != null) nbt.setByte("color", (byte) dyeColor.getMetadata());
		return super.writeToNBT(nbt);
	}

	public ElementInventoryStronger getElementInventory() {
		return inventory;
	}

	// 唤醒
	@Override
	public boolean wake(int type, @Nullable BlockPos from) {
		if (!world.isRemote) {
			this.markDirty();
			return true;
		}
		this.wake = 80;
		return true;
	}

	@Override
	public void onInventoryStatusChange() {
		if (!world.isRemote) this.updateToClient();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateEffect(World world, int type, ElementStack estack, Vec3d pos) {
		Vec3d myPos = new Vec3d(this.pos).add(0.5, 0.25 + 0.5, 0.5);
		if (type == IAltarWake.SEND) giveParticleElementTo(world, estack.getColor(), myPos, pos, 1);
		else giveParticleElementTo(world, estack.getColor(), pos, myPos, 1);
	}

	// 设置仓库
	public void setElementInventory(IElementInventory inventory) {
		ElementHelper.toElementInventory(inventory, this.inventory);
		if (world.isRemote) changeColor();
	}

	@Override
	public void update() {
		if (world.isRemote) tick();
		else {

		}
	}

	// 记录的颜色类型
	protected EnumDyeColor dyeColor = null;

	public void setDyeColor(@Nullable EnumDyeColor dyeColor) {
		this.dyeColor = dyeColor;
		if (world.isRemote) this.setColorType(BlockElementCube.toColorType(dyeColor));
	}

	public EnumDyeColor getDyeColor() {
		return dyeColor;
	}

	// 客户端使用的真实颜色
	protected BlockElementCube.Color colorType = BlockElementCube.Color.defaultColor;

	public void setColorType(BlockElementCube.Color colorType) {
		this.colorType = colorType;
	}

	@SideOnly(Side.CLIENT)
	public Vec3d getBaseColor() {
		return colorType.getBaseColor();
	}

	@SideOnly(Side.CLIENT)
	public Vec3d getCoverColor() {
		return colorType.getCoverColor();
	}

	// 默认旋转角度，每tick
	public static final float SOTATE_PRE_TICK = (float) (Math.PI / 4) / 20.0f;
	// 默认站立来的比率增加量
	public static final float WAKE_UP_RARE = 0.02f;
	// 站起来的状态剩余tick
	public int wake = 0;
	// 站立来的比率0-1
	@SideOnly(Side.CLIENT)
	public float wakeRate;
	@SideOnly(Side.CLIENT)
	public float preWakeRate;
	@SideOnly(Side.CLIENT)
	public float rotationRate;
	@SideOnly(Side.CLIENT)
	public float preRotationRate;

	public Vec3d color = Vec3d.ZERO;
	public float colorRate = 0.0f;
	public float detlaCr = 0.01f;

	@SideOnly(Side.CLIENT)
	public void tick() {
		if (color == Vec3d.ZERO) changeColor();
		preWakeRate = wakeRate;
		preRotationRate = rotationRate;
		if (wake > 0) {
			// 站起来的比率
			wakeRate = MathHelper.clamp(wakeRate + WAKE_UP_RARE, 0, 1);
			// 旋转
			rotationRate = rotationRate + 1;
			if (rotationRate > 360) {
				rotationRate -= 360;
				preRotationRate -= 360;
			}
			// 颜色转变
			colorRate += detlaCr;
			if (colorRate >= 1.0f || colorRate <= 0.0f) {
				detlaCr = -detlaCr;
				if (colorRate <= 0.0f) changeColor();
			}
			wake--;
		} else {
			if (rotationRate > 0) {
				rotationRate = rotationRate + 1;
				if (rotationRate > 360) preRotationRate = rotationRate = 0;
				colorRate += detlaCr;
				if (colorRate >= 1.0f || colorRate <= 0.0f) {
					detlaCr = -detlaCr;
					if (colorRate <= 0.0f) changeColor();
				}
			} else {
				wakeRate = MathHelper.clamp(wakeRate - WAKE_UP_RARE, 0, 1);
				colorRate = colorRate - colorRate * 0.05f;
			}
		}
	}

	// 切换颜色
	@SideOnly(Side.CLIENT)
	private void changeColor() {
		ElementStack estack = ItemSpellbook.giveMeRandomElement(inventory);
		int ecolor = ColorHelper.color(getCoverColor());
		if (!estack.isEmpty()) ecolor = estack.getElement().getColor(estack);
		color = ColorHelper.color(ecolor);
	}

}
