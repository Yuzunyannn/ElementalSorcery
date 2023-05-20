package yuzunyannn.elementalsorcery.entity;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.ESData;
import yuzunyannn.elementalsorcery.advancement.ESCriteriaTriggers;
import yuzunyannn.elementalsorcery.capability.Adventurer;
import yuzunyannn.elementalsorcery.elf.ElfChamberOfCommerce;
import yuzunyannn.elementalsorcery.elf.ElfConfig;
import yuzunyannn.elementalsorcery.elf.quest.IAdventurer;
import yuzunyannn.elementalsorcery.item.ItemElfPurse;
import yuzunyannn.elementalsorcery.util.helper.EntityHelper;
import yuzunyannn.elementalsorcery.util.helper.NBTHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;

public class EntityItemGoods extends Entity {

	public static EntityItemGoods dropGoods(EntityLivingBase from, ItemStack stack, int cost, boolean isSold) {
		EntityItemGoods goods = new EntityItemGoods(from.world, from.getPositionVector().add(0, from.height / 2, 0),
				stack);
		goods.motionY = 0.25 + from.getRNG().nextFloat() * 0.5;
		goods.motionX = from.getRNG().nextGaussian() * 0.2;
		goods.motionZ = from.getRNG().nextGaussian() * 0.2;
		dropGoods(goods, cost, isSold);
		return goods;
	}

	public static EntityItemGoods dropGoods(World world, Vec3d vec, ItemStack stack, int cost, boolean isSold) {
		EntityItemGoods goods = new EntityItemGoods(world, vec, stack);
		dropGoods(goods, cost, isSold);
		return goods;
	}

	public static EntityItemGoods dropGoods(World world, Vec3d vec, ItemStack stack, int cost, boolean isSold,
			Vec3d speed) {
		EntityItemGoods goods = new EntityItemGoods(world, vec, stack);
		goods.motionY = speed.y;
		goods.motionX = speed.x;
		goods.motionZ = speed.z;
		dropGoods(goods, cost, isSold);
		return goods;
	}

	protected static void dropGoods(EntityItemGoods goods, int cost, boolean isSold) {
		goods.setPrice(cost);
		goods.setSold(isSold);
		goods.world.spawnEntity(goods);
	}

	protected static final DataParameter<ItemStack> ITEM = EntityDataManager.createKey(EntityItemGoods.class,
			DataSerializers.ITEM_STACK);
	protected static final DataParameter<Integer> PRICE = EntityDataManager.createKey(EntityItemGoods.class,
			EntityHelper.DS_INT);
	protected static final DataParameter<Boolean> SOLD = EntityDataManager.createKey(EntityItemGoods.class,
			DataSerializers.BOOLEAN);

	protected int life;
	protected Vec3d relativeVec;

	public EntityItemGoods(World worldIn) {
		super(worldIn);
		this.setSize(0.35F, 0.35F);
		this.rotationYaw = rand.nextFloat() * 360;
		this.life = 20 * 60 * (rand.nextInt(5) + 1);
	}

	public EntityItemGoods(World worldIn, Vec3d vec, ItemStack itemStack) {
		this(worldIn);
		setPosition(vec.x, vec.y, vec.z);
		setItem(itemStack);
	}

	public void setItem(ItemStack stack) {
		this.getDataManager().set(ITEM, stack);
	}

	public ItemStack getItem() {
		return this.getDataManager().get(ITEM);
	}

	public int getPrice() {
		return Math.max(0, this.getDataManager().get(PRICE));
	}

	public void setPrice(int price) {
		this.getDataManager().set(PRICE, price);
	}

	public boolean isSold() {
		return this.getDataManager().get(SOLD);
	}

	public void setSold(boolean sold) {
		this.getDataManager().set(SOLD, sold);
	}

	public void setLife(int life) {
		this.life = life;
	}

	public int getLife() {
		return life;
	}

	public void setRelativeVec(Vec3d relativeVec) {
		this.relativeVec = relativeVec;
	}

	@Override
	public String getName() {
		return getItem().getDisplayName();
	}

	@Override
	protected void entityInit() {
		this.getDataManager().register(ITEM, ItemStack.EMPTY);
		this.getDataManager().register(PRICE, 0);
		this.getDataManager().register(SOLD, true);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		setItem(new ItemStack(compound.getCompoundTag("Item")));
		setPrice(compound.getInteger("Price"));
		setSold(compound.getBoolean("Sold"));
		life = compound.getInteger("Life");
		if (NBTHelper.hasVec3d(compound, "rVec")) relativeVec = NBTHelper.getVec3d(compound, "rVec");
		else relativeVec = null;
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		if (!this.getItem().isEmpty()) compound.setTag("Item", getItem().serializeNBT());
		if (getPrice() > 0) compound.setInteger("Price", getPrice());
		if (isSold()) compound.setBoolean("Sold", true);
		if (life > 0) compound.setInteger("Life", life);
		if (relativeVec != null) NBTHelper.setVec3d(compound, "rVec", relativeVec);
	}

	@SideOnly(Side.CLIENT)
	public int getPriceForShow() {
		return getPrice();
	}

	@Override
	public EnumActionResult applyPlayerInteraction(EntityPlayer player, Vec3d vec, EnumHand hand) {
		if (world.isRemote) return EnumActionResult.SUCCESS;
		ItemStack stack = player.getHeldItem(hand);
		if (!stack.isEmpty()) return EnumActionResult.FAIL;
		if (!isSold()) {
			// 好孩子模式hhh
			if (ElfChamberOfCommerce.PLAYER_IS_GOOD_BOY) {
				player.sendMessage(new TextComponentTranslation("say.i.red.label.pick.up")
						.setStyle(new Style().setColor(TextFormatting.DARK_RED)));
				return EnumActionResult.FAIL;
			}
			NBTTagCompound dat = ESData.getRuntimeData(player);
			int i = dat.getByte("redLabelPickUp");
			if (i == 0) {
				player.sendMessage(new TextComponentTranslation("say.i.red.label.pick.up")
						.setStyle(new Style().setColor(TextFormatting.DARK_RED)));
				dat.setByte("redLabelPickUp", (byte) (i + 1));
				return EnumActionResult.FAIL;
			}
			if (steal(player)) {
				give(player, hand);
				return EnumActionResult.SUCCESS;
			}
			return EnumActionResult.FAIL;
		}
		int price = getPrice();
		if (price == 0) {
			give(player, hand);
			return EnumActionResult.SUCCESS;
		}
		int shortBy = ItemElfPurse.extract(player.inventory, price, true);
		if (shortBy > 0) {
			player.sendMessage(new TextComponentTranslation("say.i.green.label.no.money", String.valueOf(shortBy))
					.setStyle(new Style().setColor(TextFormatting.DARK_GREEN)));
			return EnumActionResult.FAIL;
		}
		ItemElfPurse.extract(player.inventory, price, false);
		give(player, hand);
		return EnumActionResult.SUCCESS;
	}

	public boolean steal(EntityLivingBase player) {
		int price = this.getPrice();
		ItemStack stack = getItem();
		if (price < 160) {
			if (price == 0 || Math.max(0.2, price / 160.0) < rand.nextDouble()) {
				player.sendMessage(
						new TextComponentTranslation("elf.red.label.merchant.not.notice", stack.getDisplayName())
								.setStyle(new Style().setColor(TextFormatting.GRAY)));
				return true;
			}
		}
		IAdventurer adventurer = player.getCapability(Adventurer.ADVENTURER_CAPABILITY, null);
		if (adventurer == null) return false;
		float fameDrop = (float) (Math.pow(price / 512.0, 1.1) + price / 1024.0);
		if (fameDrop > 0.5f) fameDrop = 0.5f + fameDrop / 10f;
		ElfConfig.changeFame(player, -fameDrop);
		adventurer.incurDebts(price);
		player.sendMessage(new TextComponentTranslation("elf.red.label.merchant.notice", stack.getDisplayName(),
				String.format("%.2f", fameDrop), String.valueOf(price))
						.setStyle(new Style().setColor(TextFormatting.GRAY)));
		if (player instanceof EntityPlayerMP)
			ESCriteriaTriggers.ES_TRING.trigger((EntityPlayerMP) player, "elf:debtor");
		return true;
	}

	public void give(EntityLivingBase entity, EnumHand hand) {
		ItemStack stack = entity.getHeldItem(hand);
		if (stack.isEmpty()) entity.setHeldItem(hand, getItem());
		else ItemHelper.dropItem(world, entity.getPositionEyes(0), getItem());
		this.setDead();
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (source.canHarmInCreative()) {
			this.setDead();
			return true;
		}
		if (source.isFireDamage()) {
			if (rand.nextDouble() < 0.2) {
				this.setDead();
				return true;
			}
		}
		Entity entity = source.getImmediateSource();
		if (entity == null) {
			if (rand.nextDouble() < 0.1) {
				this.setDead();
				return true;
			}
			return false;
		}
		if (world.isRemote) return false;
		Vec3d tar = this.getPositionVector().subtract(entity.getPositionEyes(0)).normalize()
				.scale(Math.min(0.1 * amount, 1));
		this.addVelocity(tar.x, Math.abs(tar.y), tar.z);
		this.markVelocityChanged();
		return false;
	}

	protected void collideWithNearbyEntities() {
		List<Entity> list = this.world.getEntitiesInAABBexcluding(this, this.getEntityBoundingBox(), (e) -> {
			return e instanceof EntityItemGoods;
		});
		if (list.isEmpty()) return;

		for (int l = 0; l < list.size(); ++l) {
			Entity entity = list.get(l);
			entity.applyEntityCollision(this);
		}

	}

	@Override
	public void onUpdate() {
		ItemStack itemStack = this.getItem();
		if (itemStack.isEmpty()) {
			this.setDead();
			return;
		}
		if (this.life > 0) {
			this.life--;
			if (this.life == 0) {
				this.setDead();
				return;
			}
		}
		super.onUpdate();
	}

	@Override
	public void onEntityUpdate() {
		super.onEntityUpdate();
		double d0 = this.motionX;
		double d1 = this.motionY;
		double d2 = this.motionZ;
		if (!this.hasNoGravity()) this.motionY -= 0.03999999910593033D;
		if (this.world.isRemote) this.noClip = false;
		else this.noClip = this.pushOutOfBlocks(this.posX,
				(this.getEntityBoundingBox().minY + this.getEntityBoundingBox().maxY) / 2.0D, this.posZ);
		this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
		float f = 0.98F;
		if (this.onGround) {
			BlockPos underPos = new BlockPos(MathHelper.floor(this.posX),
					MathHelper.floor(this.getEntityBoundingBox().minY) - 1, MathHelper.floor(this.posZ));
			net.minecraft.block.state.IBlockState underState = this.world.getBlockState(underPos);
			f = underState.getBlock().getSlipperiness(underState, this.world, underPos, this) * 0.98F;
		}
		this.motionX *= (double) f;
		this.motionY *= 0.9800000190734863D;
		this.motionZ *= (double) f;
		if (this.onGround) this.motionY *= -0.5D;
		if (!this.world.isRemote) {
			double d3 = this.motionX - d0;
			double d4 = this.motionY - d1;
			double d5 = this.motionZ - d2;
			double d6 = d3 * d3 + d4 * d4 + d5 * d5;
			if (d6 > 0.01D) this.isAirBorne = true;
		}
		collideWithNearbyEntities();
		if (isSold()) {
			if (relativeVec != null) {
				double distance = relativeVec.subtract(this.getPositionVector()).length();
				if (distance > 16) setSold(false);
			}
		}

	}

}
