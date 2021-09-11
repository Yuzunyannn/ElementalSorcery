package yuzunyannn.elementalsorcery.tile.altar;

import javax.annotation.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.api.item.IWindmillBlade;
import yuzunyannn.elementalsorcery.api.tile.IGetItemStack;
import yuzunyannn.elementalsorcery.building.Buildings;
import yuzunyannn.elementalsorcery.building.MultiBlock;

public class TileDeconstructWindmill extends TileStaticMultiBlock implements IGetItemStack, ITickable {

	@Override
	public void initMultiBlock() {
		structure = new MultiBlock(Buildings.DECONSTRUCT_WINDMILL, this, new BlockPos(0, -2, 0));
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		blade = nbtGetItemStack(compound, "blade");
		super.readFromNBT(compound);

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		nbtSetItemStack(compound, "blade", blade);
		return super.writeToNBT(compound);
	}

	private ItemStack blade = ItemStack.EMPTY;

	@Override
	public void setStack(ItemStack stack) {
		this.blade = stack;
		this.updateToClient();
		this.markDirty();
	}

	@Override
	public ItemStack getStack() {
		return blade;
	}

	@Override
	public boolean canSetStack(ItemStack stack) {
		return stack.getItem() instanceof IWindmillBlade;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(pos.add(-1, 0, -1), pos.add(1, 9, 1));
	}

	@Override
	public boolean isIntact() {
		int checkInterval = 20 * 30;
		if (!ok) checkInterval = 40;
		if (checkTime++ % checkInterval == 0) {
			EnumFacing facing = structure.face();
			ok = structure.check(facing);
			if (!ok) ok = structure.check(facing.rotateY());
			if (ok) {
				facing = structure.face().rotateY();
				ok = ok && world.isAirBlock(pos.up(5));
				ok = ok && world.isAirBlock(pos.up(5).offset(facing, 1));
				ok = ok && world.isAirBlock(pos.up(5).offset(facing, -1));
				for (int y = 0; y < 4; y++) {
					ok = ok && world.isAirBlock(pos.up(6 + y));
					ok = ok && world.isAirBlock(pos.up(6 + y).offset(facing, 1));
					ok = ok && world.isAirBlock(pos.up(6 + y).offset(facing, 2));
					ok = ok && world.isAirBlock(pos.up(6 + y).offset(facing, -1));
					ok = ok && world.isAirBlock(pos.up(6 + y).offset(facing, -2));
				}
			}
		}
		return this.ok;
	}

	public boolean isIntactWithCheck() {
		return ok;
	}

	@Nullable
	public EnumFacing getOrientation() {
		return structure.face();
	}

	public IWindmillBlade getWindmillBlade() {
		if (blade.isEmpty()) return null;
		Item item = blade.getItem();
		if (item instanceof IWindmillBlade) return (IWindmillBlade) item;
		return null;
	}

	protected float speed = 0;

	@Override
	public void update() {

		if (world.isRemote) updateClient();
		if (!isIntact()) return;

		IWindmillBlade windmillBlade = getWindmillBlade();
		if (windmillBlade == null) {
			speed = speed - speed * 0.1f;
			return;
		}

		speed = speed + (getWindScale(world, pos.up(7)) - speed) * 0.01f;

		try {
			windmillBlade.bladeUpdate(world, pos.up(7), blade, speed, checkTime);
		} catch (Exception e) {
			ElementalSorcery.logger.warn("风车旋转出现异常", e);
			setStack(ItemStack.EMPTY);
		}

	}

	@SideOnly(Side.CLIENT)
	public float bladeRotate;
	@SideOnly(Side.CLIENT)
	public float prevBladeRotate;

	@SideOnly(Side.CLIENT)
	public float bladeShiftRate = 0;
	@SideOnly(Side.CLIENT)
	public float prevBladeShiftRate = 0;

	@SideOnly(Side.CLIENT)
	public void updateClient() {
		prevBladeRotate = bladeRotate;
		prevBladeShiftRate = bladeShiftRate;

		if (blade.isEmpty() || !ok) bladeShiftRate = bladeShiftRate - bladeShiftRate * 0.2f;
		else bladeShiftRate = bladeShiftRate + (1 - bladeShiftRate) * 0.2f;

		bladeRotate += speed * 0.04f;
	}

	/** 获取风级 */
	public static float getWindScale(World world, BlockPos pos) {
		Biome biome = world.getBiome(pos);
		float thunderStrength = world.getThunderStrength(1);
		float rainStrength = world.getRainStrength(1);
		if (!biome.canRain()) rainStrength = thunderStrength = 0;
		rainStrength = rainStrength / 2 + thunderStrength * 1.5f;

		float worldBaseWindScale = 3;
		int worldTime = (int) (world.getTotalWorldTime());
		int timeSpace = worldTime / 20 / 60;
		if (timeSpace % 2 == 0) worldBaseWindScale = 2;
		if (timeSpace % 5 == 0) worldBaseWindScale = 0;
		if (rainStrength > 0) worldBaseWindScale = worldBaseWindScale + 0.5f;

		if (worldBaseWindScale == 0) return 0;

		float temperature = biome.getTemperature(pos);
		temperature = MathHelper.clamp(temperature * temperature, 0, 0.2f);

		int randSeed = pos.getX() * pos.getX() + pos.getZ() + pos.getY() * worldTime;
		randSeed = randSeed ^ randSeed << 3;
		float rand = (Math.abs(randSeed) % 10000) / 10000f;

		float high = pos.getY();
		float h = 130 + rand * 20;
		float dif = Math.abs(h - high);

		float rate = 1;
		if (dif < 75) rate = 1 - dif / 85.3f;
		else rate = 1 / MathHelper.sqrt(dif);

		return worldBaseWindScale * rate * (1 + rand) * (1 + rainStrength) * (1 + temperature);
	}

}
