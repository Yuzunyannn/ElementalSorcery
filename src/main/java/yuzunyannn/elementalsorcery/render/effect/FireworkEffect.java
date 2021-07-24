package yuzunyannn.elementalsorcery.render.effect;

import net.minecraft.item.ItemDye;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FireworkEffect {

	static public void spawn(World world, BlockPos pos, int type, int size, float speed, int[] color, int[] colorFade) {
		spawn(world, new Vec3d(pos).addVector(0.5, 0.5, 0.5), type, size, speed, color, colorFade);
	}

	static public void spawn(World world, Vec3d pos, int type, int size, float speed, int[] color, int[] colorFade) {
		NBTTagCompound nbt = FireworkEffect.fastNBT(type, size, speed, color, colorFade);
		Effects.spawnEffect(world, Effects.FIREWROK, pos, nbt);
	}

	static public NBTTagCompound fastNBT(int type, int size, float speed, int[] color, int[] colorFade) {
		NBTTagList list = new NBTTagList();
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setByte("Type", (byte) type);
		nbt.setIntArray("Colors", color);
		nbt.setIntArray("FadeColors", colorFade);
		nbt.setInteger("Size", size);
		nbt.setFloat("Speed", speed);
		list.appendTag(nbt);
		nbt = new NBTTagCompound();
		nbt.setTag("Explosions", list);
		return nbt;
	}

	@SideOnly(Side.CLIENT)
	static public void show(World world, Vec3d pos, NBTTagCompound nbt) {
		if (nbt.hasKey("Explosions", 9)) {
			NBTTagList list = nbt.getTagList("Explosions", 10);
			for (NBTBase base : list) {
				nbt = (NBTTagCompound) base;
				byte type = nbt.getByte("Type");
				int size = nbt.getInteger("Size");
				double speed = nbt.getDouble("Speed");
				boolean trail = nbt.getBoolean("Trail");
				boolean flicker = nbt.getBoolean("Flicker");
				int[] colors = nbt.getIntArray("Colors");
				int[] fadeColors = nbt.getIntArray("FadeColors");
				if (colors.length == 0) colors = new int[] { ItemDye.DYE_COLORS[0] };
				if (fadeColors.length == 0) fadeColors = new int[] { ItemDye.DYE_COLORS[0] };
				switch (type) {
				case 0:
					FirewrokShap.createBall(world, pos, speed, size, colors, fadeColors, trail, flicker);
					break;
				case 1:
					FirewrokShap.createCircle(world, pos, speed, size, colors, fadeColors, trail, flicker);
					break;
				case 10:
					FirewrokShap.createEBall(world, pos, speed, size, colors);
					break;
				case 11:
					FirewrokShap.createECircle(world, pos, speed, size, colors);
				default:
					break;
				}
			}
		}
	}

}
