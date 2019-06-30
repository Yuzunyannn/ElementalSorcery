package yuzunyan.elementalsorcery.item;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyan.elementalsorcery.api.ability.IElementInventory;
import yuzunyan.elementalsorcery.api.element.ElementStack;
import yuzunyan.elementalsorcery.api.util.ElementHelper;
import yuzunyan.elementalsorcery.capability.CapabilityProvider;
import yuzunyan.elementalsorcery.capability.Spellbook;
import yuzunyan.elementalsorcery.render.item.SpellbookRenderInfo;
import yuzunyan.elementalsorcery.render.particle.ParticleSpellbook;
import yuzunyan.elementalsorcery.render.particle.ParticleSpellbookSelect;
import yuzunyan.elementalsorcery.render.particle.ParticleSpellbookTo;

public class ItemSpellbook extends Item {

	static public final Random rand = new Random();

	public ItemSpellbook() {
		this.setUnlocalizedName("spellbook");
		this.setMaxStackSize(1);
	}

	/**
	 * 开始释放
	 * 
	 * @return 是否成功释放，false表示释放失败
	 */
	public boolean spellBegin(World world, EntityLivingBase entity, ItemStack stack, Spellbook book) {
		return true;
	}

	/**
	 * 持续释放
	 * 
	 * @param power
	 *            积攒的时间
	 */
	public void spelling(World world, EntityLivingBase entity, ItemStack stack, Spellbook book, int power) {
	}

	/**
	 * 结束释放
	 * 
	 * @param power
	 *            积攒的时间，大于0表示释放成功
	 * @return 是否同步，true表示需要同步，一般不需要
	 */
	public boolean spellEnd(World world, EntityLivingBase entity, ItemStack stack, Spellbook book, int power) {
		return false;
	}

	/**
	 * 交换，玩家潜行时候使用魔导书会调用这个函数
	 * 
	 */
	public void swap(World world, EntityLivingBase entity, ItemStack stack, Spellbook book) {

	}

	/** 获取蓄力时间（施法前摇） */
	public int getCast(Spellbook book) {
		return 40;
	}

	/** 获取仓库 */
	protected IElementInventory getInventory(ItemStack stack) {
		return null;
	}

	/** 获取材质初始化info */
	@SideOnly(Side.CLIENT)
	protected void initRenderInfo(SpellbookRenderInfo info) {
	}

	// 信息
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		Spellbook book = stack.getCapability(Spellbook.SPELLBOOK_CAPABILITY, null);
		if (book == null)
			return;
		IElementInventory inventory = book.getInventory();
		if (inventory == null)
			return;
		inventory.loadState(stack);
		ElementHelper.addElementInformation(inventory, worldIn, tooltip, flagIn);
	}

	// 添加能力
	@Override
	@Nullable
	public net.minecraftforge.common.capabilities.ICapabilityProvider initCapabilities(ItemStack stack,
			@Nullable NBTTagCompound nbt) {
		ICapabilitySerializable<NBTTagCompound> cap = new CapabilityProvider.SpellbookUseProvider(stack,
				this.getInventory(stack));
		if (SpellbookRenderInfo.renderInstance != null) {
			Spellbook book = cap.getCapability(Spellbook.SPELLBOOK_CAPABILITY, null);
			this.initRenderInfo(book.render_info);
		}
		return cap;
	}

	// 动画
	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.BLOCK;
	}

	// 使用魔法书
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		if (!stack.hasCapability(Spellbook.SPELLBOOK_CAPABILITY, null))
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
		Spellbook spellbook = stack.getCapability(Spellbook.SPELLBOOK_CAPABILITY, null);
		// 蹲着
		if (playerIn.isSneaking()) {
			this.swap(worldIn, playerIn, stack, spellbook);
			return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
		}
		// 开始释放！
		spellbook.initSpelling(worldIn, stack, playerIn, handIn);
		if (!this.spellBegin(worldIn, playerIn, stack, spellbook))
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
		// 开始释放
		spellbook.beginSpelling(worldIn, playerIn, handIn);

		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}

	// 结束使用魔法书
	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
		Spellbook spellbook = stack.getCapability(Spellbook.SPELLBOOK_CAPABILITY, null);
		boolean sync = false;
		if (entityLiving instanceof EntityPlayer)
			sync = this.spellEnd(worldIn, entityLiving, stack, spellbook, this.getMaxItemUseDuration(stack) - timeLeft);
		else
			sync = this.spellEnd(worldIn, entityLiving, stack, spellbook, -1);
		spellbook.endSpelling(worldIn, entityLiving, stack, sync);
	}

	// 正在使用魔法书，服务端
	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
		if (player.world.isRemote)
			return;
		Spellbook spellbook = stack.getCapability(Spellbook.SPELLBOOK_CAPABILITY, null);
		if (spellbook.spelling) {
			this.spelling(player.world, player, stack, spellbook, this.getMaxItemUseDuration(stack) - count);
		}
	}

	// 正在使用魔法书，客户端
	@SideOnly(Side.CLIENT)
	public void onUsingTickClient(EntityLivingBase entity, ItemStack stack, Spellbook spellbook) {
		if (spellbook.spelling) {
			this.spelling(entity.world, entity, stack, spellbook,
					this.getMaxItemUseDuration(stack) - entity.getItemInUseCount());
		}
	}

	// 最长使用时间
	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 72000;
	}

	// 渲染信息处理，开始打开书
	@SideOnly(Side.CLIENT)
	static public void renderStart(Spellbook book) {
		book.spelling = true;
		book.render_info.tickCount = 0;
	}

	// 渲染信息处理，改变书
	@SideOnly(Side.CLIENT)
	static public void renderChange(Spellbook book, float d) {
		SpellbookRenderInfo info = book.render_info;
		info.tickCount++;
		info.bookSpreadPrev = info.bookSpread;
		info.pageFlipPrev = info.pageFlip;
		info.bookRotationPrev = info.bookRotation;
		// 打开
		info.bookSpread += d;
		info.bookSpread = info.bookSpread > 1.0F ? 1.0F : info.bookSpread;
		info.pageFlip += (1.0f - info.bookSpread) * 1.25;
		info.pageFlip *= 0.85F;
		// 旋转
		info.bookRotation = info.bookSpread * (float) Math.PI * 0.5F;
	}

	// 渲染完成（阶段性）
	@SideOnly(Side.CLIENT)
	static public void renderFinish(Spellbook book) {
		book.spelling = false;
	}

	// 渲染信息处理，打开过程
	@SideOnly(Side.CLIENT)
	static public void renderOpen(Spellbook book) {
		renderChange(book, 0.05F);
	}

	// 渲染信息处理，关闭过程
	@SideOnly(Side.CLIENT)
	static public boolean renderClose(Spellbook book) {
		renderChange(book, -0.05F);
		if (book.render_info.bookSpread <= 0)
			return true;
		return false;
	}

	// 渲染信息处理，结束
	@SideOnly(Side.CLIENT)
	static public void renderEnd(Spellbook book) {
		book.render_info.tickCount = 0;
		book.render_info.bookRotation = book.render_info.bookRotationPrev = 0;
		book.render_info.bookSpread = book.render_info.bookSpreadPrev = 0;
	}

	// 显示离子效果
	@SideOnly(Side.CLIENT)
	protected void giveMeParticleAboutSpelling(World world, EntityLivingBase entity, ItemStack stack, Spellbook book,
			int power) {
		boolean is_your = entity == Minecraft.getMinecraft().player;
		float want;
		if (is_your && this.getCast(book) > 0)
			want = power < this.getCast(book) ? power / this.getCast(book) : 0.3f;
		else
			want = 0.3f;
		if (Math.random() < want) {
			showParticle(world, entity, giveMeAColor(book.getInventory()));
		}
	}

	// 显示输送的离子效果
	@SideOnly(Side.CLIENT)
	protected void giveMeParticleGoToPos(World world, EntityLivingBase entity, Spellbook book, BlockPos pos,
			int power) {
		if (power % 2 == 0) {
			Vec3d from;
			if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 0
					&& Minecraft.getMinecraft().player == entity) {
				float x = -MathHelper.cos(entity.renderYawOffset * 0.017453292F) * 0.25f;
				float z = -MathHelper.sin(entity.renderYawOffset * 0.017453292F) * 0.25f;
				from = entity.getPositionVector().add(new Vec3d(x, 1.5, z));
			} else {
				float x = -MathHelper.sin(entity.renderYawOffset * 0.017453292F) * 0.5f;
				float z = MathHelper.cos(entity.renderYawOffset * 0.017453292F) * 0.5f;
				from = entity.getPositionVector().add(new Vec3d(x, 1.5, z)
						.rotateYaw(entity.getActiveHand() == EnumHand.MAIN_HAND ? -0.34907F : 0.34907F));
			}
			Vec3d vPos = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
			ParticleSpellbookTo effect = new ParticleSpellbookTo(world, from, vPos);
			Minecraft.getMinecraft().effectRenderer.addEffect(effect);
		}
	}

	// 显示离子效果
	@SideOnly(Side.CLIENT)
	protected void giveMeParticleAboutSelect(World world, Spellbook book, BlockPos pos, int power) {
		if (power % 5 == 0) {
			ParticleSpellbookSelect effect = new ParticleSpellbookSelect(world, pos);
			effect.setColor(giveMeAColor(book.getInventory()));
			Minecraft.getMinecraft().effectRenderer.addEffect(effect);
		}
	}

	// 显示离子效果
	@SideOnly(Side.CLIENT)
	protected void giveMeParticleAboutSelect(World world, Spellbook book, Entity entity, int power) {
		if (power % 5 == 0) {
			ParticleSpellbookSelect effect = new ParticleSpellbookSelect(world, entity);
			effect.setColor(giveMeAColor(book.getInventory()));
			Minecraft.getMinecraft().effectRenderer.addEffect(effect);
		}
	}

	protected int giveMeAColor(IElementInventory inventory) {
		int color = 0xffffffff;
		if (inventory != null) {
			ElementStack estack = this.giveMeRandomElement(inventory);
			if (!estack.isEmpty())
				color = estack.getColor();
		}
		return color;
	}

	public static ElementStack giveMeRandomElement(IElementInventory inventory) {
		return inventory.getStackInSlot(rand.nextInt(inventory.getSlots()));
	}

	// 显示粒子效果
	@SideOnly(Side.CLIENT)
	static public void showParticle(World world, EntityLivingBase player, int color) {
		Vec3d from;
		if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 0 && Minecraft.getMinecraft().player == player) {
			float x = -MathHelper.cos(player.renderYawOffset * 0.017453292F) * 0.25f;
			float z = -MathHelper.sin(player.renderYawOffset * 0.017453292F) * 0.25f;
			from = player.getPositionVector().add(new Vec3d(x, 1.5, z));
		} else {
			float x = -MathHelper.sin(player.renderYawOffset * 0.017453292F) * 0.5f;
			float z = MathHelper.cos(player.renderYawOffset * 0.017453292F) * 0.5f;
			from = player.getPositionVector().add(new Vec3d(x, 1.5, z)
					.rotateYaw(player.getActiveHand() == EnumHand.MAIN_HAND ? -0.34907F : 0.34907F));
		}
		Vec3d speed = player.getLookVec().scale(Math.random() * 1.2 - 0.4);
		ParticleSpellbook effect = new ParticleSpellbook(world, from, speed, player);
		effect.setColor(color);
		Minecraft.getMinecraft().effectRenderer.addEffect(effect);
	}
}
