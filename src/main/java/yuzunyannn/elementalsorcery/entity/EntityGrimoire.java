package yuzunyannn.elementalsorcery.entity;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.mantra.CastStatus;
import yuzunyannn.elementalsorcery.api.mantra.Mantra;
import yuzunyannn.elementalsorcery.api.mantra.SilentLevel;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.api.util.IWorldObject;
import yuzunyannn.elementalsorcery.api.util.WorldObjectEntity;
import yuzunyannn.elementalsorcery.event.EventClient;
import yuzunyannn.elementalsorcery.event.ITickTask;
import yuzunyannn.elementalsorcery.grimoire.Grimoire;
import yuzunyannn.elementalsorcery.render.item.RenderItemGrimoireInfo;
import yuzunyannn.elementalsorcery.util.MasterBinder;

public class EntityGrimoire extends EntityMantraBase {

	public static void start(World world, EntityLivingBase user, Grimoire grimoire) {
		user.setActiveHand(EnumHand.MAIN_HAND);
		if (world.isRemote) return;
		Grimoire.Info info = grimoire.getSelectedInfo();
		if (info == null) return;
		EntityGrimoire e = new EntityGrimoire(world, user, info.getMantra(), info.getData());
		e.onLockUser();
		world.spawnEntity(e);
	}

	public EntityGrimoire(World worldIn) {
		super(worldIn);
	}

	public EntityGrimoire(World worldIn, EntityLivingBase user, Mantra mantra, NBTTagCompound metaData) {
		this(worldIn, user, mantra, metaData, CastStatus.BEFORE_SPELLING);
	}

	// 这个构造方法是给予某些不通过魔法书进行处理的
	public EntityGrimoire(World worldIn, @Nullable EntityLivingBase user, Mantra mantra, NBTTagCompound metaData,
			CastStatus state) {
		super(worldIn, mantra, metaData, state);
		this.user.setMaster(user);
		this.restoreGrimoire();
	}

	/** 使用者，如果服务器关闭后尽可能会被还原，但是STATE_AFTER_SPELLING中可能存在为null的时候 */
	protected MasterBinder user = new MasterBinder();
	/** 魔导书 */
	public Grimoire grimoire;
	public ItemStack grimoireStack = ItemStack.EMPTY;
//	public NBTTagCompound grimoireDataFromServer;

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		user.readEntityFromNBT(nbt);
	}

	protected void writeEntityToNBT(NBTTagCompound nbt, boolean isSend) {
		super.writeEntityToNBT(nbt, isSend);
		user.writeEntityToNBT(nbt);
	}

	@Override
	protected void restoreUser() {
		user.restoreMaster(world);
	}

	@Override
	protected IWorldObject getUser() {
		return user.getMaster() == null ? null : new WorldObjectEntity(user.getMaster());
	}

	protected boolean restore() {
		boolean isOk = super.restore();
		if (!isOk) return false;
		// 还原魔法书
		if (grimoire == null) {
			restoreGrimoire();
			if (grimoire == null) {
				ESAPI.logger.warn("魔导书还原使grimoire出现异常！" + user.getMaster());
				return false;
			}
		}
		return true;
	}

	protected void restoreGrimoire() {
		// 释放法术后处理，魔法书已经不是必须的了
		if (state == CastStatus.AFTER_SPELLING) grimoire = new Grimoire();
		else {
			EntityLivingBase master = user.getMaster();
			ItemStack hold = master.getHeldItem(EnumHand.MAIN_HAND);
			grimoireStack = hold;
			grimoire = hold.getCapability(Grimoire.GRIMOIRE_CAPABILITY, null);
			if (grimoire != null) grimoire.tryLoadState(hold);
//			if (world.isRemote) {
//				if (grimoireDataFromServer != null && grimoire != null) grimoire.deserializeNBT(grimoireDataFromServer);
//			}
		}

		if (grimoire != null) {
			Grimoire.Info info = grimoire.getSelectedInfo();
			metaData = info == null ? new NBTTagCompound() : info.getData();
		}
	}

	@Override
	protected void onLockUser() {
		EntityLivingBase master = user.getMaster();
		master.timeUntilPortal = 20;
		this.timeUntilPortal = 20;
		this.posX = master.posX;
		this.posY = master.posY;
		this.posZ = master.posZ;
	}

	protected void onSpelling() {
		super.onSpelling();
		if (world.isRemote) {
			RenderItemGrimoireInfo info = grimoire.getRenderInfo();
			info.open();
			info.update();
		}
	}

	protected void onEndSpelling() {
		super.onEndSpelling();
		if (world.isRemote) {
			this.closeBook();
			return;
		}
		if (grimoireStack.isEmpty()) return;
		Grimoire.Info info = grimoire.getSelectedInfo();
		if (info != null && info.getMantra() == mantra) info.setData(metaData);
		grimoire.saveState(grimoireStack);
		// 延迟一下，等待合书动画
//		EventServer.addTask(() -> {
//			// 连开的情况
//			if (user != null && user.isHandActive()) {
//				if (user.getHeldItemMainhand().getCapability(Grimoire.GRIMOIRE_CAPABILITY, null) == grimoire) return;
//			}
//			grimoire.saveState(grimoireStack);
//		}, 5);
	}

	@Override
	public boolean canContinueSpelling() {
		return canContinueSpelling(user.getMaster());
	}

	public static boolean canContinueSpelling(Entity entity) {
		if (entity == null) return false;
		if (entity.isDead) return false;
		if (entity instanceof EntityLivingBase) {
			if (!((EntityLivingBase) entity).isHandActive()) return false;
		} else {
			return false;
		}
		if (ESAPI.silent.isSilent(entity, SilentLevel.SPELL)) return false;
		return true;
	}

	@SideOnly(Side.CLIENT)
	protected void closeBook() {
		if (user == null) return;
		EntityLivingBase master = user.getMaster();
		EventClient.addTickTask(() -> {
			if (grimoire == null) return ITickTask.END;
			RenderItemGrimoireInfo info = grimoire.getRenderInfo();
			info.close();
			boolean flag = info.update() || master.isHandActive() || master.isDead;
			if (flag) info.reset();
			return flag ? ITickTask.END : ITickTask.SUCCESS;
		});
	}

	@Override
	public IElementInventory getElementInventory() {
		return grimoire.getInventory();
	}

	@Override
	public void iWantGivePotent(float potent, float point) {
		grimoire.addPotent(potent, point);
	}

	@Override
	public float iWantBePotent(float point, boolean justTry) {
		float rPoint = Math.min(grimoire.potentPoint, point);
		float potent = grimoire.getPotent() * (rPoint / point);
		if (justTry) return potent;
		grimoire.addPotentPoint(-rPoint);
		return potent;
	}

}
