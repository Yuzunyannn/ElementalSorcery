package yuzunyannn.elementalsorcery.tile.altar;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import yuzunyannn.elementalsorcery.api.tile.IGetItemStack;
import yuzunyannn.elementalsorcery.building.Buildings;
import yuzunyannn.elementalsorcery.building.MultiBlock;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.entity.EntityPortal;
import yuzunyannn.elementalsorcery.init.ESInitInstance;
import yuzunyannn.elementalsorcery.item.crystal.ItemNatureCrystal;
import yuzunyannn.elementalsorcery.render.particle.Effect;
import yuzunyannn.elementalsorcery.render.particle.EffectElementScrew;
import yuzunyannn.elementalsorcery.util.MultiRets;

public class TilePortalAltar extends TileStaticMultiBlock implements IGetItemStack {

	@Override
	public void initMultiBlock() {
		structure = new MultiBlock(Buildings.PORTAL_ALTAR, this, new BlockPos(0, -1, 0));
		structure.addSpecialBlock(new BlockPos(4, 6, 4));
		structure.addSpecialBlock(new BlockPos(-4, 6, -4));
		structure.addSpecialBlock(new BlockPos(-4, 6, 4));
		structure.addSpecialBlock(new BlockPos(4, 6, -4));
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		stack = new ItemStack(compound.getCompoundTag("stack"));
		if (!this.isSending()) enderPower = compound.getInteger("enderPower");
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		if (!stack.isEmpty()) compound.setTag("stack", stack.serializeNBT());
		if (!this.isSending()) compound.setInteger("enderPower", enderPower);
		return super.writeToNBT(compound);
	}

	public static final ElementStack NEED = new ElementStack(ESInitInstance.ELEMENTS.ENDER, 1, 100);
	private ItemStack stack = ItemStack.EMPTY;
	protected int enderPower = 0;

	@Override
	public void setStack(ItemStack stack) {
		this.stack = stack;
		this.updateToClient();
		this.markDirty();
		this.checkAndOpenPortal();
	}

	@Override
	public ItemStack getStack() {
		return stack;
	}

	@Override
	public boolean canSetStack(ItemStack stack) {
		return stack.getItem() == ESInitInstance.ITEMS.NATURE_CRYSTAL;
	}

	@Override
	public boolean checkIntact(MultiBlock structure) {
		return ok = structure.check(EnumFacing.NORTH);
	}

	// 检查并打开传送门
	public void checkAndOpenPortal() {
		if (world.isRemote) return;
		if (stack.isEmpty()) return;
		if (!this.checkIntact(structure)) return;
		MultiRets rets = ItemNatureCrystal.getData(stack, false);
		if (rets.isEmpty()) return;
		BlockPos to = rets.get(0, BlockPos.class);
		Integer toWorldId = rets.get(1, Integer.class);
		World toWorld = world.getMinecraftServer().getWorld(toWorldId);
		if (toWorld == null) return;
		if (!canOpenPortal(to, toWorld)) return;
		ElementStack get = getElementFromSpPlace(NEED, pos.up(3));
		if (get.isEmpty()) return;
		EntityPortal.createPortal(EntityPortal.TYPE_CHECK_TILE, world, new Vec3d(pos.up(3)).addVector(0.5, 0, 0.5),
				EntityPortal.TYPE_PERMANENT, toWorld, new Vec3d(to).addVector(0.5, 0.5, 0.5));
		enderPower = 0;
	}

	public boolean canOpenPortal(BlockPos to, World toWorld) {
		if (this.pos.distanceSq(to) < 16 * 16) return false;
		EntityPortal portal = EntityPortal.findOther(world, new Vec3d(pos.up(3)), 0.5f);
		if (portal != null) return false;
		return true;
	}

	protected int tick;

	public void update(EntityPortal portal) {
		tick++;
		if (world.isRemote) {
			if (portal.isOpen()) {
				if (tick % 20 == 0) getElementFromSpPlace(NEED, pos.up(3));
			} else getElementFromSpPlace(NEED, pos.up(3));
			return;
		}
		if (stack.isEmpty() || enderPower < -20 || !this.isIntact()) {
			portal.setDead();
			return;
		}
		if (portal.isOpen()) {
			if (tick % 10 == 0) {
				// 花费维持
				ElementStack get = getElementFromSpPlace(NEED, pos.up(3));
				if (get.isEmpty()) enderPower -= 5;
				else if (enderPower < 75) enderPower++;
			}
		} else {
			// 开启花费
			if (tick % 2 == 0) {
				enderPower--;
				ElementStack get = getElementFromSpPlace(NEED, pos.up(3));
				if (get.isEmpty()) return;
				enderPower += 2;
				if (enderPower > 75) portal.setOpen(true);
			}
		}
	}

	@Override
	public void genParticleElementTo(boolean isGet, int color, BlockPos from, BlockPos to) {
		Vec3d pos = new Vec3d(to).addVector(0.5f, 1.5f, 0.5f);
		Vec3d at = new Vec3d(from).addVector(0.5f, 0.75f, 0.5f);
		BlockPos tar = this.pos.subtract(from);
		EffectElementScrew e = new EffectElementScrew(world, at, pos).setDirect(new Vec3d(tar.getX(), 1, 0));
		e.setColor(color);
		e.lifeTime = 150;
		Effect.addEffect(e);
	}

	int nowIndex = 0;

	@Override
	public int getStartIndex() {
		return nowIndex = (nowIndex + 1) % structure.getSpecialBlockCount();
	}
}
