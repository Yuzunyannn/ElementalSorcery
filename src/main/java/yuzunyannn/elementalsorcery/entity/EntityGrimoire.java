package yuzunyannn.elementalsorcery.entity;

import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.event.ITickTask;
import yuzunyannn.elementalsorcery.grimoire.Grimoire;
import yuzunyannn.elementalsorcery.render.item.RenderItemGrimoireInfo;

public class EntityGrimoire extends Entity implements IEntityAdditionalSpawnData {

	public static void start(World world, EntityLivingBase user) {
		user.setActiveHand(EnumHand.MAIN_HAND);
		if (world.isRemote) return;
		EntityGrimoire e = new EntityGrimoire(world, user);
		e.lockUser();
		world.spawnEntity(e);
	}

	public EntityGrimoire(World worldIn) {
		super(worldIn);
	}

	public EntityGrimoire(World worldIn, EntityLivingBase user) {
		super(worldIn);
		this.user = user;
		this.userUUID = user.getUniqueID().toString();
	}

	@Override
	public void setDead() {
		if (!isDead) this.onDead();
		super.setDead();
	}

	public static final int STATE_BEFORE_SPELLING = 0;
	public static final int STATE_SPELLING = 1;
	public static final int STATE_AFTER_SPELLING = 2;

	protected byte state = STATE_BEFORE_SPELLING;
	protected int tick;
	/** 使用者，如果服务器关闭后尽可能会被还原，但是STATE_AFTER_SPELLING中可能存在为null的时候 */
	protected EntityLivingBase user;
	protected String userUUID;

	public Grimoire grimoire;

	@Override
	protected void entityInit() {
		this.setSize(0.1f, 0.1f);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		userUUID = compound.getString("user");
		tick = compound.getInteger("tick");
		state = compound.getByte("state");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		compound.setString("user", userUUID);
		compound.setInteger("tick", tick);
		compound.setByte("state", state);
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeEntityToNBT(nbt);
		ByteBufUtils.writeTag(buffer, nbt);
	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {
		readEntityFromNBT(ByteBufUtils.readTag(additionalData));
	}

	/** 需要精准还原，所以使用uuid */
	protected void restoreUser() {
		List<EntityLivingBase> list = world.getEntities(EntityLivingBase.class, (e) -> {
			return e.getUniqueID().toString().equals(userUUID);
		});
		if (list.isEmpty()) return;
		user = list.get(0);
	}

	protected void restoreGrimoire() {
		ItemStack hold = user.getHeldItem(EnumHand.MAIN_HAND);
		grimoire = hold.getCapability(Grimoire.GRIMOIRE_CAPABILITY, null);
		if (grimoire == null) {
			// 释放后处理，魔法书已经不是必须的了
			if (state == STATE_AFTER_SPELLING) grimoire = new Grimoire();
		}
	}

	protected boolean restore() {
		// 还原user
		if (user == null) {
			this.restoreUser();
			if (user == null) {
				ElementalSorcery.logger.warn("魔导书还原使用者出现异常！", userUUID);
				return false;
			}
		}
		// 还原魔法书
		if (grimoire == null) {
			restoreGrimoire();
			if (grimoire == null) {
				ElementalSorcery.logger.warn("魔导书还原使grimoire出现异常！", userUUID);
				return false;
			}
		}
		return true;
	}

	protected void lockUser() {
		user.timeUntilPortal = 20;
		this.timeUntilPortal = 20;
		this.posX = user.posX;
		this.posY = user.posY;
		this.posZ = user.posZ;
	}

	@Override
	public void onUpdate() {
		try {
			this.onEntityUpdate();
		} catch (Exception e) {
			ElementalSorcery.logger.warn("魔导书使用过程中出现异常！", e);
			super.setDead();
		}
	}

	@Override
	public void onEntityUpdate() {
		// 还原一些内容
		if (!this.restore()) {
			this.setDead();
			return;
		}
		tick++;
		switch (state) {
		case STATE_BEFORE_SPELLING:
			this.lockUser();
			this.onBeforeSpelling();
			state = STATE_SPELLING;
			break;
		case STATE_SPELLING:
			this.lockUser();
			// 客户端开书
			if (world.isRemote) {
				RenderItemGrimoireInfo info = grimoire.getRenderInfo();
				info.open();
				info.update();
			}
			this.onSpelling();
			if (!user.isHandActive() || user.isDead) state = STATE_AFTER_SPELLING;
			break;
		default:
			this.onAfterSpelling();
			break;
		}
	}

	protected void onDead() {
		// 客户端关书
		if (world.isRemote) {
			if (user != null) {
				EventClient.addTickTask(() -> {
					RenderItemGrimoireInfo info = grimoire.getRenderInfo();
					info.close();
					boolean flag = info.update() || user.isHandActive() || user.isDead;
					if (flag) info.reset();
					return flag ? ITickTask.END : ITickTask.SUCCESS;
				});
			}
		}
	}

	protected void onBeforeSpelling() {

	}

	protected void onSpelling() {

	}

	protected void onAfterSpelling() {
		this.setDead();
	}
}
