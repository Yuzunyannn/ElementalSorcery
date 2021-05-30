package yuzunyannn.elementalsorcery.entity.fcube;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.capability.FairyCubeMaster;
import yuzunyannn.elementalsorcery.container.ContainerFairyCube;
import yuzunyannn.elementalsorcery.container.ESGuiHandler;
import yuzunyannn.elementalsorcery.entity.EntityPortal;
import yuzunyannn.elementalsorcery.item.ItemFairyCube;
import yuzunyannn.elementalsorcery.network.MessageEntitySync;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.element.EffectElementMove;
import yuzunyannn.elementalsorcery.util.NBTHelper;
import yuzunyannn.elementalsorcery.util.NBTTag;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;

public class EntityFairyCube extends EntityLivingBase implements MessageEntitySync.IRecvData {

	/** 吃一次食物，直接增加经验的cd */
	public static final int FOOD_EXP_CD = 20 * 20;

	public static enum State {
		WATING,
		MOVING;
	}

	protected NonNullList<ItemStack> arrmors = NonNullList.<ItemStack>withSize(6, ItemStack.EMPTY);

	/** 主人的uuid */
	protected UUID playerUUID;
	/** 所有模块 */
	protected List<FairyCubeModule> modules = new ArrayList<>();
	/** 立方体等级 */
	protected float level;
	/** 立方体体力 */
	protected float physicalStrength;
	/** 执行tick */
	protected int execute = 0;
	protected FairyCubeModule executeModule = null;
	/***/
	protected int foodExpCD = 0;

	/** 立方体状态 */
	protected State state = State.WATING;
	/** 立方体主人 */
	protected WeakReference<EntityLivingBase> master;
	/** 根据uuid寻找主人，找不到的情况的cd时间记录 */
	protected long masterFindFailCD = 0;
	protected int masterFindFailTimes = 0;

	/** 吸收元素颜色通用数据，进行展示 */
	public final List<Integer> absorbColors = new ArrayList<>();

	public EntityFairyCube(World worldIn) {
		super(worldIn);
	}

	public EntityFairyCube(EntityPlayer master, NBTTagCompound data) {
		super(master.world);
		this.master = new WeakReference<>(master);
		if (data != null && !data.hasNoTags()) this.readFairyCubeFromNBT(data);
		this.playerUUID = master.getUniqueID();
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.setEntityInvulnerable(true);
		this.setSize(1, 1);
		this.noClip = true;
	}

	public void writeFairyCubeToNBT(NBTTagCompound compound) {
		compound.setFloat("level", level);
		compound.setFloat("p.s.", physicalStrength);
		if (playerUUID != null) compound.setUniqueId("master", playerUUID);
		NBTTagList list = new NBTTagList();
		for (FairyCubeModule module : modules) list.appendTag(module.serializeNBT());
		compound.setTag("modules", list);
	}

	public void readFairyCubeFromNBT(NBTTagCompound compound) {
		level = compound.getFloat("level");
		physicalStrength = compound.getFloat("p.s.");
		if (compound.hasKey("masterMost")) playerUUID = compound.getUniqueId("master");
		NBTTagList list = compound.getTagList("modules", NBTTag.TAG_COMPOUND);
		modules.clear();
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound nbt = list.getCompoundTagAt(i);
			String id = nbt.getString("id");
			FairyCubeModule module = FairyCubeModule.REGISTRY.newInstance(id, this);
			if (module == null) continue;
			module.deserializeNBT(nbt);
			module.onLoaded();
			modules.add(module);
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		this.writeFairyCubeToNBT(compound);
		compound.setShort("foodExpCD", (short) foodExpCD);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		this.readFairyCubeFromNBT(compound);
		foodExpCD = compound.getShort("foodExpCD");
	}

	@Override
	public void setDead() {
		super.setDead();
		if (world.isRemote) this.dispearEffect();
	}

	public void setToDrop() {
		this.setHealth(0);
	}

	@SideOnly(Side.CLIENT)
	public void dispearEffect() {
		Vec3d pos = this.getPositionVector();
		for (int k = 0; k < 20; ++k) {
			EffectElementMove em = new EffectElementMove(world, pos.addVector(0, 0.5, 0));
			em.g = 0;
			double d2 = this.rand.nextGaussian() * 0.125;
			double d0 = this.rand.nextGaussian() * 0.125;
			double d1 = this.rand.nextGaussian() * 0.125;
			em.setVelocity(d0, d2, d1);
			em.xDecay = em.yDecay = em.zDecay = 0.75;
			if (this.rand.nextBoolean()) em.setColor(0x00d081);
			else em.setColor(0xb9f4e4);
			Effect.addEffect(em);
		}
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	/** 获取玩家，若无法获取到则返回null，服务端和客户端结果未必一样 */
	@Nullable
	public EntityLivingBase getMaster() {
		EntityLivingBase player = master == null ? null : master.get();
		if (player != null && !player.isDead) return player;
		if (masterFindFailTimes > 10) return null;
		this.restoreMaster();
		return master == null ? null : master.get();
	}

	protected void restoreMaster() {
		if (playerUUID == null) return;

		long now = System.currentTimeMillis();
		if (now - masterFindFailCD < 1000 * 10) return;

		master = null;
		List<EntityLivingBase> list = world.getEntities(EntityLivingBase.class, (e) -> {
			return e.getUniqueID().equals(playerUUID);
		});
		if (list.isEmpty()) {
			if (world instanceof WorldServer) {
				WorldServer ws = (WorldServer) world;
				PlayerList pl = ws.getMinecraftServer().getPlayerList();
				EntityLivingBase player = pl.getPlayerByUUID(playerUUID);
				this.setMaster(player);
			}
		} else this.setMaster(list.get(0));
		if (master == null) {
			masterFindFailCD = now;
			masterFindFailTimes++;
		}
	}

	protected void setMaster(EntityLivingBase master) {
		if (master == null) return;
		this.master = new WeakReference<>(master);
		masterFindFailCD = 0;
		masterFindFailTimes = 0;
	}

	public boolean isMaster(EntityLivingBase entity) {
		return entity.getUniqueID().equals(this.playerUUID);
	}

	@Override
	public void onLivingUpdate() {
		if (this.newPosRotationIncrements > 0) {
			double d0 = this.posX + (this.interpTargetX - this.posX) / (double) this.newPosRotationIncrements;
			double d1 = this.posY + (this.interpTargetY - this.posY) / (double) this.newPosRotationIncrements;
			double d2 = this.posZ + (this.interpTargetZ - this.posZ) / (double) this.newPosRotationIncrements;
			double d3 = MathHelper.wrapDegrees(this.interpTargetYaw - (double) this.rotationYaw);
			this.rotationYaw = (float) ((double) this.rotationYaw + d3 / (double) this.newPosRotationIncrements);
			this.rotationPitch = (float) ((double) this.rotationPitch
					+ (this.interpTargetPitch - (double) this.rotationPitch) / (double) this.newPosRotationIncrements);
			--this.newPosRotationIncrements;
			this.setPosition(d0, d1, d2);
			this.setRotation(this.rotationYaw, this.rotationPitch);
		}
		if (world.isRemote) {
			this.updateClientEffect();
			this.updateArmSwingProgress();
			return;
		}
		if (playerUUID == null) {
			this.updateFree();
			return;
		}

		if (this.deathTime > 0) return;

		EntityLivingBase player = this.getMaster();
		if (player == null) return;
		if (player.dimension != this.dimension) {
			EntityPortal.moveTo(this, player.getPositionVector(), player.dimension);
			return;
		}
		this.updateCD();
		switch (state) {
		case MOVING:
			this.updateMoving(player);
			break;
		default:
			this.updateIdle(player);
			break;
		}
		try {
			this.updateModule(player);
		} catch (Exception e) {
			ElementalSorcery.logger.warn("Fairy Cube 執行模块异常", e);
			this.setToDrop();
		}

	}

	protected void updateCD() {
		if (foodExpCD > 0) foodExpCD--;
	}

	protected void updateFree() {
		if (ticksExisted % 40 != 0) return;
		Random rand = this.rand;
		int operator = rand.nextInt(5);
		switch (operator) {
		case 1:
			this.rotationYaw = this.rotationYaw + rand.nextInt(30) - 15;
			break;
		case 2:
			this.rotationPitch = rand.nextInt(60) - 30;
			break;
		case 3:
			Vec3d vec = this.getLookVec();
			this.setEntityBoundingBox(this.getEntityBoundingBox().offset(vec));
			this.resetPositionToBB();
			break;
		case 4:
			if (rand.nextInt(20) == 0) this.setHealth(0);
			break;
		default:
			break;
		}
	}

	protected void updateIdle(EntityLivingBase player) {
		if (ticksExisted % 5 == 0) {
			Vec3d vec = player.getPositionEyes(1);
			Vec3d at = new Vec3d(posX, posY + 0.6, posZ);
			if (vec.squareDistanceTo(at) > 9) {
				state = State.MOVING;
				talkGui = null;
				return;
			}
		}
		if (ticksExisted % 40 == 0 && !isExecuting()) {
			if (talkGui != null) this.setLookAt(talkGui.getPositionEyes(1));
			else {
				Random rand = this.rand;
				if (rand.nextBoolean()) this.rotationYaw = this.rotationYaw + rand.nextInt(30) - 15;
				else this.rotationPitch = rand.nextInt(60) - 30;
			}
		}
	}

	protected void updateMoving(EntityLivingBase player) {
		Vec3d vec = player.getPositionEyes(1);
		Vec3d at = new Vec3d(posX, posY + 0.4, posZ);
		Vec3d dir = vec.subtract(at);
		if (dir.lengthSquared() <= 16 * 16) dir = dir.scale(0.1);
		float raw = (float) MathHelper.atan2(dir.z, dir.x) / 3.1415926f * 180 - 90;
		this.rotationYaw = raw;
		this.rotationPitch = (float) (-dir.y * 90);

		if (vec.squareDistanceTo(at) < 4) {
			state = State.WATING;
		}
		this.setEntityBoundingBox(this.getEntityBoundingBox().offset(dir));
		this.resetPositionToBB();
	}

	public void setLookAt(Vec3d pos) {
		Vec3d dir = pos.subtract(new Vec3d(posX, posY + 0.5, posZ));
		float raw = (float) MathHelper.atan2(dir.z, dir.x) / 3.1415926f * 180 - 90;
		this.rotationYaw = raw;
		this.rotationPitch = (float) (-dir.y * 90);
	}

	public void setLookAt(BlockPos pos) {
		this.setLookAt(new Vec3d(pos).addVector(0.5, 0.5, 0.5));
	}

	static public void addBehavior(EntityLivingBase master, Behavior behavior) {
		IFairyCubeMaster myMaster = master.getCapability(FairyCubeMaster.FAIRY_CUBE_MASTER_CAPABILITY, null);
		if (myMaster == null) return;
		myMaster.addBehavior(master, behavior);
	}

	protected void updateModule(EntityLivingBase master) {
		if (execute > 0) {
			execute = execute - 1;
			executeModule.onExecute(master, execute);
			if (execute == 0) this.addExp(executeModule.getExecuteExpForFairyCube());
			return;
		}
		IFairyCubeMaster myMaster = master.getCapability(FairyCubeMaster.FAIRY_CUBE_MASTER_CAPABILITY, null);
		if (myMaster == null) return;
		// 存在多个，就直接自杀
		if (!myMaster.isMyServant(this)) {
			this.setToDrop();
			return;
		}
		for (FairyCubeModule module : modules) {
			if (!module.isClose()) {
				module.onUpdate(master, myMaster);
				if (execute > 0) {
					executeModule = module;
					float cost = executeModule.getPhysicalStrengthConsume();
					if (this.getPhysicalStrength() > cost) {
						this.physicalStrength = this.physicalStrength - cost;
						executeModule.onStartExecute(master);
						break;
					} else {
						executeModule.onFailExecute();
						execute = 0;
						executeModule = null;
					}
				}
			}
		}
	}

	@Override
	protected void onDeathUpdate() {
		++this.deathTime;
		if (this.deathTime == 20) {
			if (playerUUID != null && !world.isRemote) {
				ItemStack stack = ItemFairyCube.createStackWithEntity(this);
				this.entityDropItem(stack, 0.5f);
			}
			this.setDead();
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void updateArmSwingProgress() {
		if (swingProgress > 0) {
			swingProgress -= (1f / swingProgressInt);
		}
	}

	@SideOnly(Side.CLIENT)
	public void swingArm(int duration) {
		prevSwingProgress = swingProgress = 1f;
		swingProgressInt = duration <= 0 ? 1 : duration;
	}

	public boolean isExecuting() {
		return execute > 0;
	}

	public void doExecute(int duration) {
		this.execute = duration;
	}

	public List<FairyCubeModule> getModules() {
		return modules;
	}

	public FairyCubeModule getModule(ResourceLocation id) {
		for (FairyCubeModule module : modules) {
			if (module.getRegistryName().equals(id)) return module;
		}
		return null;
	}

	public boolean installModule(ResourceLocation id, NBTTagCompound installData) {
		if (modules.size() >= 12) return false; // 只能有12个
		FairyCubeModule module = this.getModule(id);
		if (module != null) return false;
		module = FairyCubeModule.REGISTRY.newInstance(id, this);
		if (module == null) return false;
		installData = installData == null ? new NBTTagCompound() : installData;
		module.onInstall(installData);
		float priority = module.getPriority();
		int index = 0;
		for (index = 0; index < modules.size(); index++) {
			float p = modules.get(index).getPriority();
			if (p < priority) break;
		}
		if (index == modules.size()) modules.add(module);
		else modules.add(index, module);
		return true;
	}

	public void addExp(float count) {
		while (true) {
			int lev = Math.max(MathHelper.ceil(this.getLevel()), 1);
			float decay = (float) Math.pow(lev, 2.4f) * 32;
			float need = 1 - this.getLevelUpgradeProgress();
			if (count / decay < need) {
				level += count / decay;
				return;
			}
			count = count - need * decay;
			level += need;
		}
	}

	public float getLevel() {
		return level;
	}

	public float getLevelUpgradeProgress() {
		return level - MathHelper.floor(level);
	}

	public float getAttributeDefault(String name) {
		if ("level".equals(name)) return this.getLevel();
		return 0;
	}

	public float getAttribute(String name) {
		float attribute = this.getAttributeDefault(name);
		for (FairyCubeModule module : modules) attribute = module.modifyAttribute(name, attribute);
		return attribute;
	}

	public int getCubeLevel() {
		return MathHelper.floor(getAttribute("level"));
	}

	public float getPhysicalStrength() {
		return physicalStrength;
	}

	public boolean levelUpModule(IElementInventory einv) {
		boolean hasAbsorb = false;
		for (FairyCubeModule module : modules) {
			if (module.isClose()) continue;
			hasAbsorb = hasAbsorb || module.absorbElements(einv);
		}
		return hasAbsorb;
	}

	@Override
	public boolean isEntityInvulnerable(DamageSource source) {
		return source != DamageSource.OUT_OF_WORLD;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		return super.attackEntityFrom(source, amount);
	}

	public EntityPlayer talkGui = null;

	@Override
	public EnumActionResult applyPlayerInteraction(EntityPlayer player, Vec3d vec, EnumHand hand) {
		if (ElementalSorcery.isDevelop) {
			if (player.isSneaking() && player.isCreative()) {
				this.setToDrop();
				return EnumActionResult.SUCCESS;
			}
		}

		if (hand != EnumHand.MAIN_HAND) return EnumActionResult.PASS;

		UUID uuid = player.getUniqueID();
		boolean isMaster = uuid.equals(playerUUID);

		if (!isMaster) return EnumActionResult.PASS;
		if (world.isRemote) return EnumActionResult.SUCCESS;
		// 超出次数后，通过交互可以重新认领主人
		EntityLivingBase master = this.getMaster();
		if (master == null && isMaster) this.setMaster(master);

		ItemStack stack = player.getHeldItem(hand);
		if (applyPlayerInteractionItemStack(player, vec, stack)) return EnumActionResult.SUCCESS;

		if (stack.isEmpty() && player.isSneaking()) {
			this.setToDrop();
			return EnumActionResult.SUCCESS;
		}

		if (talkGui == player) return EnumActionResult.FAIL;
		talkGui = player;

		ContainerFairyCube.setFairyCubeContext(this);
		player.openGui(ElementalSorcery.instance, ESGuiHandler.GUI_FAIRY_CUBE, world, 0, 0, 0);
		return EnumActionResult.SUCCESS;
	}

	protected boolean applyPlayerInteractionItemStack(EntityPlayer player, Vec3d vec, ItemStack stack) {
		Item item = stack.getItem();
		if (item instanceof ItemFood) {
			ItemFood food = (ItemFood) item;
			float ha = food.getHealAmount(stack);
			if (foodExpCD <= 0) {
				this.addExp(ha);
				if (foodExpCD <= 0) this.letsHappy(2);
				foodExpCD = FOOD_EXP_CD;
			} else {
				if (physicalStrength < 32) this.letsHappy(1);
			}
			physicalStrength = physicalStrength + ha;
			if (physicalStrength > 20) physicalStrength = physicalStrength * 0.93f;
			stack.shrink(1);
			world.playSound(null, posX, posY, posZ, SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.NEUTRAL, 1, 1);
			return true;
		}
		IElementInventory env = ElementHelper.getElementInventory(stack);
		if (!ElementHelper.isEmpty(env)) {
			this.absorbColors.clear();
			if (this.levelUpModule(env)) {
				env.saveState(stack);
				this.letsElementGrow();
				return true;
			}
		}
		return false;
	}

	@Override
	public Iterable<ItemStack> getArmorInventoryList() {
		return arrmors;
	}

	@Override
	public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn) {
		return arrmors.get(slotIn.getSlotIndex());
	}

	@Override
	public void setItemStackToSlot(EntityEquipmentSlot slotIn, ItemStack stack) {
		arrmors.set(slotIn.getSlotIndex(), stack);
	}

	@Override
	public EnumHandSide getPrimaryHand() {
		return EnumHandSide.LEFT;
	}

	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}

	// 颜色缓存，减少发送量
	private Set<Integer> lastColorsCache;
	private long lastColorCacheTime;

	public void letsElementGrow() {
		if (absorbColors.isEmpty()) return;
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setByte("T", (byte) 3);
		int[] colors = Arrays.stream(absorbColors.toArray(new Integer[absorbColors.size()])).mapToInt(Integer::valueOf)
				.toArray();
		nbt.setIntArray("C", colors);
		MessageEntitySync.sendToClient(this, nbt);
	}

	public void letsHappy(int kind) {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setByte("T", (byte) 2);
		nbt.setByte("K", (byte) kind);
		MessageEntitySync.sendToClient(this, nbt);
	}

	public void letsCastingToBlock(int duration, int[] colors, List<BlockPos> list) {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setByte("T", (byte) 1);
		nbt.setIntArray("C", colors);
		nbt.setShort("D", (short) duration);
		NBTHelper.setBlockPosList(nbt, "P", list);
		MessageEntitySync.sendToClient(this, nbt);
	}

	@Override
	public void onRecv(NBTTagCompound data) {
		byte type = data.getByte("T");
		switch (type) {
		case 1:
			this.doClientCasting(data);
			break;
		case 2:
			this.doClientHappy(data.getInteger("K"));
			break;
		case 3:
			this.doClientHappyElement(data);
			break;
		}
	}

	@SideOnly(Side.CLIENT)
	public int effctKind;

	@SideOnly(Side.CLIENT)
	public int effctRemain;

	@SideOnly(Side.CLIENT)
	public void updateClientEffect() {
		if (effctRemain > 0) {
			effctRemain--;
			if (effctKind == 1 || effctKind == 2) {
				EnumParticleTypes type = effctKind == 1 ? EnumParticleTypes.HEART : EnumParticleTypes.NOTE;
				if (effctRemain % 5 == 0) {
					double d2 = this.rand.nextGaussian() * 0.2;
					double d0 = this.rand.nextGaussian() * 0.2;
					double d1 = this.rand.nextGaussian() * 0.2;
					world.spawnParticle(type, posX + d0, posY + 0.5 + d2, posZ + d1, this.rand.nextGaussian(), 0.0D,
							0.0D);
				}
			} else if (effctKind == 0xffAB) {
				Vec3d vec = new Vec3d(posX, posY + 0.5, posZ);
				EffectElementMove em = new EffectElementMove(world, vec);
				em.g = -0.005f;
				double d2 = this.rand.nextGaussian() * 0.075;
				double d0 = this.rand.nextGaussian() * 0.075;
				double d1 = this.rand.nextGaussian() * 0.075;
				em.setVelocity(d0, d2, d1);
				em.xDecay = em.yDecay = em.zDecay = 0.9;
				em.setColor(absorbColors.get(rand.nextInt(absorbColors.size())));
				Effect.addEffect(em);

			}
		}
	}

	@SideOnly(Side.CLIENT)
	public void doClientHappy(int kind) {
		boolean canShift = true;
		if (effctRemain > 0) {
			canShift = effctKind != 2;
		}
		if (!canShift) return;

		effctKind = kind;
		if (kind == 1) effctRemain = 15;
		else if (kind == 2) effctRemain = 40;
	}

	@SideOnly(Side.CLIENT)
	public void doClientHappyElement(NBTTagCompound data) {
		int[] colors = data.getIntArray("C");
		effctRemain = 20;
		if (effctKind == 0xffAB && effctRemain > 0) {
			for (int i : colors) if (absorbColors.indexOf(i) == -1) absorbColors.add(i);
			return;
		}
		effctKind = 0xffAB;
		absorbColors.clear();
		for (int i : colors) absorbColors.add(i);
		absorbColors.add(0x00d081);
		absorbColors.add(0xb9f4e4);
	}

	@SideOnly(Side.CLIENT)
	public void doClientCasting(NBTTagCompound data) {
		this.swingArm(data.getInteger("D"));
		int[] colors = data.getIntArray("C");
		if (colors.length == 0) colors = new int[] { 0x00d081, 0xb9f4e4 };
		Vec3d vec = this.getPositionVector().addVector(0, 0.5, 0);
		for (int k = 0; k < 20; ++k) {
			EffectElementMove em = new EffectElementMove(world, vec);
			em.g = 0;
			double d2 = this.rand.nextGaussian() * 0.2;
			double d0 = this.rand.nextGaussian() * 0.2;
			double d1 = this.rand.nextGaussian() * 0.2;
			em.setVelocity(d0, d2, d1);
			em.xDecay = em.yDecay = em.zDecay = 0.5;
			em.setColor(colors[rand.nextInt(colors.length)]);
			Effect.addEffect(em);
		}

		List<BlockPos> list = NBTHelper.getBlockPosList(data, "P");
		for (BlockPos pos : list) {
			vec = new Vec3d(pos).addVector(0.5, 0.5, 0.5);
			for (int k = 0; k < 5; ++k) {
				Vec3d at;
				{
					double d2 = this.rand.nextGaussian() * 0.5;
					double d0 = this.rand.nextGaussian() * 0.5;
					double d1 = this.rand.nextGaussian() * 0.5;
					at = vec.addVector(d2, d0, d1);
				}
				EffectElementMove em = new EffectElementMove(world, at);
				em.g = 0;
				{
					double d2 = this.rand.nextGaussian() * 0.01;
					double d0 = this.rand.nextGaussian() * 0.01;
					double d1 = this.rand.nextGaussian() * 0.01;
					em.setVelocity(d2, d0, d1);
				}
				em.xDecay = em.yDecay = em.zDecay = 0.99;
				em.setColor(colors[rand.nextInt(colors.length)]);
				Effect.addEffect(em);
			}
		}
	}
}
