package yuzunyannn.elementalsorcery.entity;

import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
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
import yuzunyannn.elementalsorcery.render.entity.RenderEntityPortalWorldSecene;
import yuzunyannn.elementalsorcery.util.NBTHelper;

public class EntityPortal extends Entity implements IEntityAdditionalSpawnData {

	public static void createPortal(World fromWorld, Vec3d fromPos, World toWorld, Vec3d toPos) {
		if (fromWorld.isRemote) return;
		EntityPortal portal = new EntityPortal(fromWorld);
		portal.setPosition(fromPos);
		portal.setTo(toWorld, toPos);
		fromWorld.spawnEntity(portal);
	}

	public EntityVest vest;
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
		// Minecraft mc = Minecraft.getMinecraft();
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
		// 创建一个绘图用的马甲
		if (vest == null) {
			if (world.isRemote) {
				if (toWorld != null) {
					toWorld.getChunkFromBlockCoords(new BlockPos(toPos));
					AxisAlignedBB aabb = new AxisAlignedBB(toPos.x - 0.5, toPos.y - 0.5, toPos.z - 0.5, toPos.x + 0.5,
							toPos.y + 0.5, toPos.z + 0.5);
					List<EntityVest> list = toWorld.getEntitiesWithinAABB(EntityVest.class, aabb);
					if (!list.isEmpty()) this.vest = list.get(0);
				}
			} else {
				vest = new EntityVest(toWorld, v -> {
					return !isDead;
				});
				toWorld.getChunkFromBlockCoords(new BlockPos(toPos));
				vest.setPosition(toPos.x, toPos.y, toPos.z);
				toWorld.spawnEntity(vest);
			}
		}
		if (world.isRemote) {
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
			double dis = this.getPositionVector().addVector(0, this.getEyeHeight(), 0)
					.distanceTo(entity.getPositionVector().addVector(0, entity.getEyeHeight(), 0));
			double h = entity.posY - this.posY + 0.1f;
			if (dis < 1.5f && Math.abs(h) < 0.5f) {
				Vec3d tar = this.getPositionVector().subtract(entity.getPositionVector()).normalize().scale(1.75f);
				entity.setPositionAndUpdate(toPos.x + tar.x, toPos.y + this.getEyeHeight() - entity.getEyeHeight(),
						toPos.z + tar.z);
				entity.timeUntilPortal = 20;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public void initClient() {
		if (ElementalSorcery.config.PORTAL_RENDER_LEVEL == 0) drawInstance = new IPortalDraw() {
		};
		else drawInstance = new RenderEntityPortalWorldSecene();
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

		default public boolean isDispose() {
			return true;
		}

		default public void dispose() {

		}

		default public void render(EntityPortal entity) {

		}

		default public void tick(EntityPortal entity) {

		}
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
