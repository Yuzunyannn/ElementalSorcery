package yuzunyannn.elementalsorcery.entity;

import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityPortalEffect;
import yuzunyannn.elementalsorcery.render.entity.RenderEntityPortalWorldSecene;
import yuzunyannn.elementalsorcery.util.NBTHelper;

public class EntityPortal extends Entity implements IEntityAdditionalSpawnData {

	public static void createPortal(World world1, Vec3d pos1, World world2, Vec3d pos2) {
		if (world1.isRemote) return;
		EntityPortal portal1 = new EntityPortal(world1);
		EntityPortal portal2 = new EntityPortal(world2);
		portal1.setPosition(pos1);
		portal2.setPosition(pos2);
		portal1.setTo(world2, pos2);
		portal2.setTo(world1, pos1);
		world1.getChunkFromBlockCoords(new BlockPos(pos1));
		world2.getChunkFromBlockCoords(new BlockPos(pos2));
		world1.spawnEntity(portal1);
		world2.spawnEntity(portal2);
	}

	public EntityPortal other;
	// 要到的世界
	World toWorld;
	// 要到的原始数据
	int toWorldId;
	Vec3d toPos;

	public EntityPortal(World worldIn) {
		super(worldIn);
		this.setSize(4, 4);
	}

	@Override
	protected void entityInit() {
		if (world.isRemote) this.initClient();
	}

	@Override
	public void setDead() {
		super.setDead();
		if (world.isRemote) this.disposeWorldScene();
	}

	@Override
	public float getEyeHeight() {
		return 1.62F;
	}

	public void setTo(World world, Vec3d to) {
		this.toWorld = world;
		this.toWorldId = world.provider.getDimension();
		this.toPos = to;
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		NBTHelper.setPos(nbt, "otherPos", toPos);
		nbt.setInteger("otherWorld", toWorldId);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		toWorldId = nbt.getInteger("otherWorld");
		toPos = NBTHelper.getPos(nbt, "otherPos");
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

	@SideOnly(Side.CLIENT)
	public void recoveryClient() {
		if (toWorldId == world.provider.getDimension()) toWorld = world;
	}

	@Override
	public void onUpdate() {
		if (toPos == null) {
			this.setDead();
			return;
		}
		// 没有世界进行获取
		if (toWorld == null) {
			MinecraftServer server = world.getMinecraftServer();
			if (server == null) recoveryClient();// 客户端不知道怎么获取其他的维度....
			else {
				// 服务器获取不到直接删除这个门
				toWorld = server.getWorld(toWorldId);
				if (toWorld == null) {
					this.setDead();
					return;
				}
			}
		}
		if (world.isRemote) {
			// 寻找另一半，找不到也没关系，顶多不画了
			if (other == null && toWorld != null) {
				toWorld.getChunkFromBlockCoords(new BlockPos(toPos));
				AxisAlignedBB aabb = new AxisAlignedBB(toPos.x - 0.5, toPos.y - 0.5, toPos.z - 0.5, toPos.x + 0.5,
						toPos.y + 0.5, toPos.z + 0.5);
				List<EntityPortal> list = toWorld.getEntitiesWithinAABB(EntityPortal.class, aabb);
				if (!list.isEmpty()) other = list.get(0);
			}
			this.onUpdateClient();
			return;
		}
		// 传送
		float size = 0.5f;
		AxisAlignedBB aabb = new AxisAlignedBB(posX - size, posY + height / 2 - size, posZ - size, posX + size,
				posY + height / 2 + size, posZ + size);
		List<Entity> list = world.getEntitiesWithinAABB(Entity.class, aabb);
		for (Entity entity : list) {
			if (entity == this) continue;
			if (entity.timeUntilPortal > 0) continue;
			double dis = this.getPositionVector().addVector(0, 2, 0)
					.distanceTo(entity.getPositionVector().addVector(0, entity.getEyeHeight(), 0));
			// double h = entity.posY - this.posY + 0.05f;
			if (dis < 2f && entity.posY >= this.posY && entity.posY + entity.height < this.posY + this.height - 0.2f) {
				Vec3d tar = this.getPositionVector().subtract(entity.getPositionVector()).normalize().scale(1.75f);
				entity.setPositionAndUpdate(toPos.x + tar.x, toPos.y + this.getEyeHeight() - entity.getEyeHeight(),
						toPos.z + tar.z);
				if (entity instanceof EntityItem) {
					entity.motionX = tar.x * 0.8;
					entity.motionZ = tar.z * 0.8;
				}
				entity.timeUntilPortal = 20;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public void initClient() {
		if (ElementalSorcery.config.PORTAL_RENDER_TYPE == 2) drawInstance = new RenderEntityPortalWorldSecene();
		else drawInstance = new RenderEntityPortalEffect(ElementalSorcery.config.PORTAL_RENDER_TYPE == 1);
	}

	/** 客户端更新 */
	@SideOnly(Side.CLIENT)
	public void onUpdateClient() {
		IPortalDraw draw = this.getDraw();
		if (draw.isDispose()) return;
		draw.tick(this);
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
