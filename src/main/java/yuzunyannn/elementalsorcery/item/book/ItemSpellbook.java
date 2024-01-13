package yuzunyannn.elementalsorcery.item.book;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
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
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.mantra.SilentLevel;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.api.util.client.IRenderLayoutFix;
import yuzunyannn.elementalsorcery.capability.CapabilityProvider;
import yuzunyannn.elementalsorcery.capability.Spellbook;
import yuzunyannn.elementalsorcery.logics.SpellbookOpenMsg;
import yuzunyannn.elementalsorcery.render.effect.particle.ParticleSpellbook;
import yuzunyannn.elementalsorcery.render.effect.particle.ParticleSpellbookSelect;
import yuzunyannn.elementalsorcery.render.effect.particle.ParticleSpellbookTo;
import yuzunyannn.elementalsorcery.render.item.SpellbookRenderInfo;
import yuzunyannn.elementalsorcery.util.helper.EntityHelper;
import yuzunyannn.elementalsorcery.util.item.IItemUseClientUpdate;

public class ItemSpellbook extends Item implements IItemUseClientUpdate, IRenderLayoutFix {

	static public final Random rand = new Random();

	public ItemSpellbook() {
		this.setTranslationKey("spellbook");
		this.setMaxStackSize(1);
		this.setMaxDamage(50);
	}

	@Override
	public boolean isShield(ItemStack stack, EntityLivingBase entity) {
		return true;
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
	 * @param power 积攒的时间
	 */
	public void spelling(World world, EntityLivingBase entity, ItemStack stack, Spellbook book, int power) {
	}

	/**
	 * 结束释放
	 * 
	 * @param power 积攒的时间，大于0表示释放成功
	 * @return 是否同步，true表示需要同步，一般不需要
	 */
	public void spellEnd(World world, EntityLivingBase entity, ItemStack stack, Spellbook book, int power) {

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
		if (book == null) return;
		IElementInventory inventory = book.getInventory();
		if (inventory == null) return;
		inventory.loadState(stack);
		inventory.addInformation(worldIn, tooltip, flagIn);
	}

	// 添加能力
	@Override
	@Nullable
	public net.minecraftforge.common.capabilities.ICapabilityProvider initCapabilities(ItemStack stack,
			@Nullable NBTTagCompound nbt) {
		ICapabilityProvider cap = new CapabilityProvider.SpellbookUseProvider(stack, this.getInventory(stack));
		if (SpellbookRenderInfo.renderInstance != null) {
			Spellbook book = cap.getCapability(Spellbook.SPELLBOOK_CAPABILITY, null);
			this.initRenderInfo(book.renderInfo);
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
		// 是否沉默
		if (EntityHelper.checkSilent(playerIn, SilentLevel.SPELL))
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
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
		if (entityLiving instanceof EntityPlayer)
			this.spellEnd(worldIn, entityLiving, stack, spellbook, this.getMaxItemUseDuration(stack) - timeLeft);
		else this.spellEnd(worldIn, entityLiving, stack, spellbook, -1);
		spellbook.endSpelling(worldIn, entityLiving, stack);
	}

	// 正在使用魔法书，服务端
	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
		if (player.world.isRemote) return;
		Spellbook spellbook = stack.getCapability(Spellbook.SPELLBOOK_CAPABILITY, null);
		if (!spellbook.spelling) return;
		this.spelling(player.world, player, stack, spellbook, this.getMaxItemUseDuration(stack) - count);
	}

	@Override
	public void onUsingTickClient(ItemStack stack, EntityLivingBase player, int count) {
		Spellbook spellbook = stack.getCapability(Spellbook.SPELLBOOK_CAPABILITY, null);
		// 首次
		if (count == this.getMaxItemUseDuration(stack)) SpellbookOpenMsg.addSpellbookOpen(player, stack);
		// 不能继续释放
		if (!spellbook.spelling) return;
		// 调用释放
		this.spelling(player.world, player, stack, spellbook,
				this.getMaxItemUseDuration(stack) - player.getItemInUseCount());
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
		book.renderInfo.tickCount = 0;
	}

	// 渲染信息处理，改变书
	@SideOnly(Side.CLIENT)
	static public void renderChange(Spellbook book, float d) {
		SpellbookRenderInfo info = book.renderInfo;
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
		if (book.renderInfo.bookSpread <= 0) return true;
		return false;
	}

	// 渲染信息处理，结束
	@SideOnly(Side.CLIENT)
	static public void renderEnd(Spellbook book) {
		book.renderInfo.tickCount = 0;
		book.renderInfo.bookRotation = book.renderInfo.bookRotationPrev = 0;
		book.renderInfo.bookSpread = book.renderInfo.bookSpreadPrev = 0;
	}

	// 显示离子效果
	@SideOnly(Side.CLIENT)
	protected void giveMeParticleAboutSpelling(World world, EntityLivingBase entity, ItemStack stack, Spellbook book,
			int power) {
		boolean isYou = entity == Minecraft.getMinecraft().player;
		float want;
		if (isYou && this.getCast(book) > 0) want = power < this.getCast(book) ? power / this.getCast(book) : 0.3f;
		else want = 0.3f;
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
			ElementStack estack = giveMeRandomElement(inventory);
			if (!estack.isEmpty()) color = estack.getColor();
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

	@Override
	@SideOnly(Side.CLIENT)
	public void fixLauout(ItemStack stack) {
		GlStateManager.translate(0, 0.35, 0.2);
		GlStateManager.scale(0.8, 0.8, 0.8);
		GlStateManager.rotate(-90, 1, 0, 0);
	}
}
