package yuzunyannn.elementalsorcery.entity;

import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.EffectElementScrew;
import yuzunyannn.elementalsorcery.render.effect.FirewrokShap;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityPortalEffect;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityPortalWorldSecene;
import yuzunyannn.elementalsorcery.tile.altar.TilePortalAltar;
import yuzunyannn.elementalsorcery.util.NBTHelper;

public class EntityPortal extends Entity implements IEntityAdditionalSpawnData {

	public static void createPortal(World world1, BlockPos pos1, World world2, BlockPos pos2) {
		if (world1.isRemote) return;
		createPortal(world1, new Vec3d(pos1).addVector(0.5, 0, 0.5), world2, new Vec3d(pos2).addVector(0.5, 0, 0.5));
	}

	public static void createPortal(World world1, Vec3d pos1, World world2, Vec3d pos2) {
		createPortal(TYPE_PERMANENT, world1, pos1, TYPE_PERMANENT, world2, pos2);
	}

	public static void createPortal(byte type1, World world1, Vec3d pos1, byte type2, World world2, Vec3d pos2) {
		if (world1.isRemote) return;
		if (world1 == world2 && pos1.squareDistanceTo(pos2) < 16) return;

		EntityPortal origin1 = findOther(world1, pos1, 1);
		EntityPortal origin2 = findOther(world2, pos2, 1);
		EntityPortal portal1 = origin1 == null ? new EntityPortal(world1) : origin1;
		EntityPortal portal2 = origin2 == null ? new EntityPortal(world2) : origin2;
		pos1 = origin1 == null ? pos1 : origin1.getPositionVector();
		pos2 = origin2 == null ? pos2 : origin2.getPositionVector();
		portal1.setType(type1).setPosition(pos1);
		portal2.setType(type2).setPosition(pos2);
		portal1.setTo(world2, pos2);
		portal2.setTo(world1, pos1);
		if (origin1 == null) world1.spawnEntity(portal1);
		if (origin2 == null) world2.spawnEntity(portal2);
	}

	public static final byte TYPE_PERMANENT = 0;
	public static final byte TYPE_CHECK_TILE = 1;

	/*** 要到的原始数据 */
	int toWorldId;
	Vec3d toPos;
	/** 传送门制定类型 */
	byte type = TYPE_PERMANENT;

	/** 传送门是否启动 */
	public static final DataParameter<Boolean> OPEN = EntityDataManager.createKey(EntityPortal.class,
			DataSerializers.BOOLEAN);

	public boolean isOpen() {
		return dataManager.get(OPEN);
	}

	public void setOpen(boolean isOpen) {
		if (isOpen() == isOpen) return;
		dataManager.set(OPEN, isOpen);
		dataManager.setDirty(OPEN);
		if (!world.isRemote) {
			MinecraftServer server = world.getMinecraftServer();
			World toWorld = server.getWorld(toWorldId);
			if (toWorld != null) {
				EntityPortal other = findOther(toWorld, toPos, 0.5f);
				if (other != null) other.setOpen(true);
			}
		}
	}

	public EntityPortal setType(byte type) {
		this.type = type;
		return this;
	}

	public EntityPortal(World worldIn) {
		super(worldIn);
		this.setSize(4, 4);
	}

	@Override
	protected void entityInit() {
		dataManager.register(OPEN, false);
		if (world.isRemote) this.initClient();
	}

	@Override
	public void setDead() {
		super.setDead();
		if (world.isRemote) {
			deadEffect();
			this.disposeWorldScene();
		} else {
			if (this.toPos == null) return;
			Vec3d toPos = this.toPos;
			this.toPos = null;
			MinecraftServer server = world.getMinecraftServer();
			World toWorld = server.getWorld(toWorldId);
			if (toWorld != null) {
				EntityPortal other = findOther(toWorld, toPos, 0.5f);
				if (other != null) other.setDead();
			}
		}
	}

	@Override
	public float getEyeHeight() {
		return 1.62F;
	}

	public void setTo(World world, Vec3d to) {
		this.toWorldId = world.provider.getDimension();
		this.toPos = to;
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		NBTHelper.setPos(nbt, "otherPos", toPos);
		nbt.setInteger("otherWorld", toWorldId);
		nbt.setBoolean("open", dataManager.get(OPEN));
		nbt.setByte("type", type);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		toWorldId = nbt.getInteger("otherWorld");
		toPos = NBTHelper.getPos(nbt, "otherPos");
		type = nbt.getByte("type");
		dataManager.set(OPEN, nbt.getBoolean("open"));
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		NBTTagCompound nbt = new NBTTagCompound();
		writeEntityToNBT(nbt);
		ByteBufUtils.writeTag(buffer, nbt);
	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {
		if (!this.world.isRemote) return;
		NBTTagCompound nbt = ByteBufUtils.readTag(additionalData);
		readEntityFromNBT(nbt);
	}

	// 寻找传送门
	public static EntityPortal findOther(World world, Vec3d pos, float size) {
		world.getChunkFromBlockCoords(new BlockPos(pos));
		AxisAlignedBB aabb = new AxisAlignedBB(pos.x - size, pos.y - size, pos.z - size, pos.x + size, pos.y + size,
				pos.z + size);
		List<EntityPortal> list = world.getEntitiesWithinAABB(EntityPortal.class, aabb);
		if (!list.isEmpty()) return list.get(0);
		return null;
	}

	@Override
	public void onUpdate() {
		if (toPos == null) {
			this.setDead();
			return;
		}
		if (world.isRemote) {
			this.onUpdateClient();
			return;
		}
		// 类型调用
		switch (type) {
		case 1:
			this.updateTileEntity();
			break;
		}
		// 传送
		if (isOpen()) updateTransmit();
	}

	// 更新由tileEntity创建的门
	protected void updateTileEntity() {
		TileEntity tile = world.getTileEntity(this.getPosition().down(3));
		if (!(tile instanceof TilePortalAltar)) {
			this.setDead();
			return;
		}
		TilePortalAltar portalAltar = (TilePortalAltar) tile;
		portalAltar.update(this);
	}

	// 更新传送
	protected void updateTransmit() {
		float size = 0.5f;
		AxisAlignedBB aabb = new AxisAlignedBB(posX - size, posY + height / 2 - size, posZ - size, posX + size,
				posY + height / 2 + size, posZ + size);
		List<Entity> list = world.getEntitiesWithinAABB(Entity.class, aabb);
		for (Entity entity : list) {
			if (entity == this) continue;
			if (entity.timeUntilPortal > 0) continue;
			double dis = this.getPositionVector().addVector(0, 2, 0)
					.distanceTo(entity.getPositionVector().addVector(0, entity.getEyeHeight(), 0));
			double h = entity.posY - this.posY + 0.05f;
			if (dis < 2f && entity.posY >= this.posY && entity.posY + entity.height < this.posY + this.height - 0.2f) {
				Vec3d tar = this.getPositionVector().subtract(entity.getPositionVector()).normalize().scale(2f);
				moveTo(entity, toPos.addVector(tar.x, h, tar.z), toWorldId);
				entity.motionX = tar.x * 0.8;
				entity.motionZ = tar.z * 0.8;
				entity.timeUntilPortal = 20;
			}
		}
	}

	// 进行传送
	public static void moveTo(Entity entity, Vec3d to, int toWorld) {
		if (entity.world.isRemote) return;
		if (entity.world.provider.getDimension() == toWorld) entity.setPositionAndUpdate(to.x, to.y, to.z);
		else entity.changeDimension(toWorld, new Teleporter(to));
	}

	// 传送
	protected static class Teleporter implements ITeleporter {
		final Vec3d to;

		public Teleporter(Vec3d to) {
			this.to = to;
		}

		@Override
		public void placeEntity(World world, Entity entity, float yaw) {
			entity.setLocationAndAngles(to.x, to.y, to.z, entity.rotationYaw, entity.rotationPitch);
		}

	}

	@SideOnly(Side.CLIENT)
	public void initClient() {
		if (ElementalSorcery.config.PORTAL_RENDER_TYPE == 2) drawInstance = new RenderEntityPortalWorldSecene();
		else drawInstance = new RenderEntityPortalEffect(ElementalSorcery.config.PORTAL_RENDER_TYPE == 1);
	}

	// 临时变量客户端用
	public EntityPortal other;
	public boolean noTryFindOther = true;

	/** 客户端更新 */
	@SideOnly(Side.CLIENT)
	public void onUpdateClient() {
		if (type == 1) {
			this.updateTileEntity();
			if (!isOpen()) return;
		} else {
			if (!isOpen()) {
				this.onOpenTick();
				return;
			}
		}
		// 寻找另一半，找不到也没关系，顶多不画了
		if (other == null && noTryFindOther) {
			if (toWorldId == world.provider.getDimension()) {
				other = findOther(world, toPos, 0.5f);
				noTryFindOther = false;
			} else noTryFindOther = false;
		}
		// 绘图
		IPortalDraw draw = this.getDraw();
		if (draw.isDispose()) return;
		draw.tick(this);
	}

	@SideOnly(Side.CLIENT)
	public void onOpenTick() {
		Vec3d pos = this.getPositionVector().addVector(0, this.height / 2, 0);
		Vec3d at = pos.add(new Vec3d(3, 0, 3));
		EffectElementScrew e = new EffectElementScrew(world, at, pos).setDirect(new Vec3d(1, 1, 0));
		e.setColor(118f / 255, 41f / 255, 141f / 255);
		Effect.addEffect(e);

		at = pos.add(new Vec3d(-3, 0, -3));
		e = new EffectElementScrew(world, at, pos).setDirect(new Vec3d(1, -1, 0));
		e.setColor(118f / 255, 41f / 255, 141f / 255);
		Effect.addEffect(e);
	}

	@SideOnly(Side.CLIENT)
	public IPortalDraw getDraw() {
		return (IPortalDraw) drawInstance;
	}

	@SideOnly(Side.CLIENT)
	private void disposeWorldScene() {
		IPortalDraw draw = this.getDraw();
		if (draw.isDispose()) return;
		draw.dispose();
	}

	@SideOnly(Side.CLIENT)
	public void deadEffect() {
		FirewrokShap.createECircle(world, this.getPositionVector().addVector(0, this.height / 2, 0), 0.5, 4,
				new int[] { 0x76298d, 0xa957c1 });
	}

	/** 绘画类型，客户端使用 */
	private Object drawInstance;

	@SideOnly(Side.CLIENT)
	public interface IPortalDraw {

		public boolean isDispose();

		public void dispose();

		public void render(EntityPortal entity);

		public void tick(EntityPortal entity);
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		try {
			this.disposeWorldScene();
		} catch (Throwable e) {}
	}

	public void setPosition(Vec3d pos) {
		this.setPosition(pos.x, pos.y, pos.z);
	}

}
