package yuzunyannn.elementalsorcery.util;

import java.lang.reflect.Field;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import yuzunyannn.elementalsorcery.ElementalSorcery;

public class WorldHelper {

	static public boolean canChangeRender() {
		return Minecraft.getMinecraft().isSingleplayer()
				|| (Minecraft.getMinecraft().world != null && Minecraft.getMinecraft().world.isRemote);
	}

	/**
	 * 根据世界获取对应的纬度id，可能会出现null的返回，表明获取失败！ 注：该函数获取到的结果不符合预期
	 */
	@Deprecated
	static public Integer getDimensionId(World world) {
		try {
			Field field = ReflectionHelper.findField(WorldInfo.class, "dimension", "field_76105_j", "p");
			return (Integer) field.get(world.getWorldInfo());
		} catch (Exception e) {
			ElementalSorcery.logger.error("获取世界纬度信息时候出现异常：", e);
		}
		return null;
	}

	/** 获取生物正在看的方块 */
	@Nullable
	static public RayTraceResult getLookAtBlock(World world, EntityLivingBase entity, float distance) {
		Vec3d vstart = entity.getPositionEyes(1.0F);
		Vec3d vend = entity.getLookVec().scale(distance).add(vstart);
		RayTraceResult reulst = world.rayTraceBlocks(vstart, vend);
		if (reulst == null || reulst.typeOfHit != RayTraceResult.Type.BLOCK)
			return null;
		return reulst;
	}

	/** 获取生物正在看的实体 */
	@Nullable
	static public <T extends Entity> T getLookAtEntity(World world, EntityLivingBase entity,
			Class<? extends T> classEntity, float distance) {

		final double pre_range = 0.5f;

		Vec3d vstart = entity.getPositionEyes(1.0F);
		Vec3d vend = entity.getLookVec().scale(distance).add(vstart);
		int times = MathHelper.ceil(vend.subtract(vstart).lengthVector() / (pre_range * 2));
		Vec3d tar = vend.subtract(vstart).normalize().scale(pre_range * 2);
		List<T> list = null;
		T hitEntity = null;
		for (int i = 0; i < times; i++) {
			AxisAlignedBB aabb = new AxisAlignedBB(vstart.x - pre_range, vstart.y - pre_range, vstart.z - pre_range,
					vstart.x + pre_range, vstart.y + pre_range, vstart.z + pre_range);
			list = world.getEntitiesWithinAABB(classEntity, aabb);
			// 下一个检查点
			vstart = vstart.add(tar);
			// 如果到方块了，就停下来
			IBlockState state = world.getBlockState(new BlockPos(vstart.x, vstart.y, vstart.z));
			if (state.getMaterial().isOpaque())
				break;
			// 判断list
			if (list.isEmpty())
				continue;
			hitEntity = list.get(0);
			if (hitEntity == entity) {
				hitEntity = null;
				list.remove(0);
				if (list.isEmpty())
					continue;
				break;
			}
			break;
		}
		// 寻找最准确的位置
		if (list != null && !list.isEmpty()) {
			int min_index = 0;
			double max_dot = 0;
			tar = tar.normalize();
			for (int i = 0; i < list.size(); i++) {
				T e = list.get(i);
				Vec3d to = e.getPositionVector().subtract(entity.getPositionEyes(1.0F)).normalize();
				double num = to.dotProduct(tar);
				if (num > max_dot) {
					max_dot = num;
					min_index = i;
				}
			}
			hitEntity = list.get(min_index);
		}
		return hitEntity;
	}

	static public void newLightning(World world, BlockPos pos) {
		newLightning(world, pos, false);
	}

	static public void newLightning(World world, BlockPos pos, boolean effectOnly) {
		world.spawnEntity(new EntityLightningBolt(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, effectOnly));
	}

	/** ES时间类 */
	public static class WorldTime {
		public int time;

		public WorldTime(World world) {
			this.time = (int) ((world.getWorldTime() + 1800) % 24000);
		}

		public boolean at(Period period) {
			if (period.start <= time && period.end > time)
				return true;
			return false;
		}

		public static enum Period {
			DAWN(0, 1800), MORNING(1800, 6900), AFTERNOON(6900, 12000), DUSK(12000, 13800), NIGHT(13800, 24000),
			MIDNIGHT(17700, 20100), DAY(1800, 12000);
			public int start, end;

			Period(int start, int end) {
				this.start = start;
				this.end = end;
			}
		};
	};

}
