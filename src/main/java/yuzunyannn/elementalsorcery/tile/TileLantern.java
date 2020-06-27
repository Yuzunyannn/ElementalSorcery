package yuzunyannn.elementalsorcery.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.FlyingNodeProcessor;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.event.ITickTask;
import yuzunyannn.elementalsorcery.util.NBTHelper;

public class TileLantern extends TileEntityNetwork {

	public static final int MAX_DIS = 32;
	
	private BlockPos pre = null;
	private BlockPos next = null;
	private String playerName = null;

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public String getPlayerName() {
		return playerName;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		if (compound.hasKey("prex"))
			pre = NBTHelper.getBlockPos(compound, "pre");
		if (compound.hasKey("nextx"))
			next = NBTHelper.getBlockPos(compound, "next");
		super.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		if (pre != null)
			NBTHelper.setBlockPos(compound, "pre", pre);
		if (next != null)
			NBTHelper.setBlockPos(compound, "next", next);
		return super.writeToNBT(compound);
	}

	/** 是否为头 */
	public boolean isHead() {
		return pre == null;
	}

	/** 是否为尾 */
	public boolean isTail() {
		return next == null;
	}

	/** 检测并修复连接 */
	public void check() {
		if (pre != null) {
			TileEntity tile = this.world.getTileEntity(pre);
			if (!(tile instanceof TileLantern)) {
				pre = null;
			}
		}
		if (next != null) {
			TileEntity tile = this.world.getTileEntity(next);
			if (!(tile instanceof TileLantern)) {
				next = null;
			}
		}
	}

	/** 离开队列 */
	public void leave() {
		this.check();
		if (this.pre != null && this.next != null) {
			TileLantern tilePre = (TileLantern) world.getTileEntity(pre);
			TileLantern tileNext = (TileLantern) world.getTileEntity(next);
			if (this.pre.distanceSq(this.next) > MAX_DIS * MAX_DIS) {
				tilePre.setNext(null);
				tileNext.setPre(null);
			} else {
				tilePre.setNext(this.next);
				tileNext.setPre(this.pre);
			}
			tilePre.markDirty();
			tileNext.markDirty();
			tilePre.updateToClient();
			tileNext.updateToClient();
		} else if (this.pre != null) {
			TileLantern tilePre = (TileLantern) world.getTileEntity(pre);
			tilePre.setNext(null);
			tilePre.markDirty();
			tilePre.updateToClient();
		} else if (this.next != null) {
			TileLantern tileNext = (TileLantern) world.getTileEntity(next);
			tileNext.setPre(null);
			tileNext.markDirty();
			tileNext.updateToClient();
		}
	}

	/** 连接 */
	public void link(TileLantern tile) {
		this.link(tile, 7, MAX_DIS * MAX_DIS);
	}

	/** 查找并连接 */
	public boolean link(TileLantern tile, int times, double min) {
		if (this == tile)
			return false;
		if (tile == null)
			return false;
		if (times-- <= 0)
			return false;
		double dis = tile.pos.distanceSq(this.pos);
		TileLantern tileNext = null;
		tile.check();
		if (tile.next != null) {
			tileNext = (TileLantern) this.world.getTileEntity(tile.next);
		}
		if (dis > MAX_DIS * MAX_DIS) {
			return this.link(tileNext, times, min);
		}
		if (dis > min) {
			return this.link(tileNext, times, min);
		}
		if (this.link(tileNext, times, dis) == false) {
			this.setNext(tile.pos);
			tile.setPre(this.pos);
			this.markDirty();
			tile.markDirty();
			this.updateToClient();
			tile.updateToClient();
		}
		return true;
	}

	public void setPre(BlockPos pre) {
		this.pre = pre;
	}

	public BlockPos getPre() {
		return pre;
	}

	public void setNext(BlockPos next) {
		this.next = next;
	}

	public BlockPos getNext() {
		return next;
	}

	/** 客户端随机randomTick */
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick() {
		if (Math.random() < 0.02) {
			// 添加新的传送
			EntityPlayer entityplayer = this.world.getClosestPlayer((double) ((float) this.pos.getX() + 0.5F),
					(double) ((float) this.pos.getY() + 0.5F), (double) ((float) this.pos.getZ() + 0.5F), 5.0, false);
			if (entityplayer == null)
				return;
			this.transmit();
		}
	}

	@SideOnly(Side.CLIENT)
	/** 想要传送 */
	public void wantTransmit() {
		this.transmit();
	}

	/** 传送，引路 */
	@SideOnly(Side.CLIENT)
	public void transmit() {
		if (next == null)
			return;
		EventClient.addTickTask(new TransmitFire(world, pos, next));
	}

	/** 火焰展示特效 */
	@SideOnly(Side.CLIENT)
	public static class TransmitFire implements ITickTask {
		private Path path = null;
		private final Vec3d target;
		private int life;
		private double x, y, z;
		private double vx = 0, vy = 0, vz = 0;
		private double ax = 0, ay = 0, az = 0;
		private final World world;
		private int tick = 0;
		private EntityLiving vest;
		private static final int TIME_TICK = 10;

		public TransmitFire(World world, BlockPos from, BlockPos target) {
			this.x = from.getX() + 0.5;
			this.y = from.getY() + 0.5;
			this.z = from.getZ() + 0.5;
			this.world = world;
			this.target = new Vec3d(target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5);
			this.life = 20 * 40;
			vest = new EntityLiving(this.world) {
				{
					this.setSize(0.5f, 0.5f);
				}
			};
		}

		public void setDead() {
			this.life = 0;
		}

		// 更新一次
		public void update() {
			if (this.path == null || this.path.isFinished()) {
				this.findPath();
				if (this.path == null) {
					this.setDead();
					return;
				}
			}
			this.tick++;
			if (this.tick > TIME_TICK) {
				Vec3d at = new Vec3d(this.x, this.y, this.z);
				if (target.squareDistanceTo(at) < 4) {
					if (target.squareDistanceTo(at) < 0.5) {
						this.setDead();
						return;
					}
					this.ax = (target.x - this.x - 0.5 * this.vx * TIME_TICK * TIME_TICK) * 3
							/ (TIME_TICK * TIME_TICK * TIME_TICK);
					this.ay = (target.y - this.y - 0.5 * this.vy * TIME_TICK * TIME_TICK) * 3
							/ (TIME_TICK * TIME_TICK * TIME_TICK);
					this.az = (target.z - this.z - 0.5 * this.vz * TIME_TICK * TIME_TICK) * 3
							/ (TIME_TICK * TIME_TICK * TIME_TICK);
				} else {
					Vec3d vec = path.getCurrentPos();
					vec = vec.addVector(0.5, 0.5, 0.5);
					path.incrementPathIndex();
					this.ax = (vec.x - this.x - 0.5 * this.vx * TIME_TICK * TIME_TICK) * 3
							/ (TIME_TICK * TIME_TICK * TIME_TICK);
					this.ay = (vec.y - this.y - 0.5 * this.vy * TIME_TICK * TIME_TICK) * 3
							/ (TIME_TICK * TIME_TICK * TIME_TICK);
					this.az = (vec.z - this.z - 0.5 * this.vz * TIME_TICK * TIME_TICK) * 3
							/ (TIME_TICK * TIME_TICK * TIME_TICK);
					this.tick = 0;
				}
			}
			x += vx;
			y += vy;
			z += vz;
			vx += ax;
			vy += ay;
			vz += az;
			if (EventClient.tick % 3 == 0)
				this.effect();
		}

		// 寻找一条新的路
		private void findPath() {
			FlyingNodeProcessor node = new FlyingNodeProcessor();
			node.setCanEnterDoors(true);
			node.setCanSwim(true);
			PathFinder finder = new PathFinder(node);
			vest.setPosition(x, y, z);
			path = finder.findPath(world, vest, new BlockPos(this.target.x, this.target.y, this.target.z), MAX_DIS);
		}

		// 效果
		private void effect() {
			double d0 = x;
			double d1 = y;
			double d2 = z;
			world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, 0.0D, 0.0D, 0.0D);
			world.spawnParticle(EnumParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D);
		}

		@Override
		public int onTick() {
			this.update();
			if (this.world != Minecraft.getMinecraft().world)
				return ITickTask.END;
			this.life--;
			if (this.life <= 0)
				return ITickTask.END;
			return ITickTask.SUCCESS;
		}
	}

}
