package yuzunyannn.elementalsorcery.grimoire.mantra;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.api.util.IWorldObject;
import yuzunyannn.elementalsorcery.api.util.WorldTarget;
import yuzunyannn.elementalsorcery.block.altar.BlockElementContainer;
import yuzunyannn.elementalsorcery.container.gui.GuiMantraShitf;
import yuzunyannn.elementalsorcery.element.ElementStack;
import yuzunyannn.elementalsorcery.grimoire.ICaster;
import yuzunyannn.elementalsorcery.grimoire.IMantraData;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon;
import yuzunyannn.elementalsorcery.grimoire.MantraDataCommon.CollectResult;
import yuzunyannn.elementalsorcery.grimoire.remote.FMantraElementWhirl;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.item.ItemAncientPaper;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.scrappy.EffectSphericalBlast;
import yuzunyannn.elementalsorcery.tile.ir.TileIceRockStand;
import yuzunyannn.elementalsorcery.util.MathSupporter;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.helper.DamageHelper;
import yuzunyannn.elementalsorcery.util.helper.EntityHelper;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;
import yuzunyannn.elementalsorcery.util.render.RenderHelper;
import yuzunyannn.elementalsorcery.util.render.TextureBinder;
import yuzunyannn.elementalsorcery.util.var.VariableSet;
import yuzunyannn.elementalsorcery.util.var.Variables;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class MantraElementWhirl extends MantraCommon {

	public static void booom(World world, Vec3d at, ElementStack magic, Entity caster) {
		VariableSet set = new VariableSet();
		set.set(Variables.MAGIC, magic);
		set.set(Variables.TICK, 80);
		set.set(VEC, at);
		MantraCommon.fireMantra(world, ESInit.MANTRAS.ELEMENT_WHIRL, caster, set);
	}

	public MantraElementWhirl() {
		this.setTranslationKey("elementWhirl");
		this.setColor(0xce77ff);
		this.setIcon("element_whirl");
		this.setRarity(45);
		this.setOccupation(3);
		this.addFragmentMantraLauncher(new FMantraElementWhirl(this));
	}

	@Override
	public void potentAttack(World world, ItemStack grimoire, ICaster caster, Entity target) {
		super.potentAttack(world, grimoire, caster, target);
	}

	@Override
	public void startSpelling(World world, IMantraData data, ICaster caster) {
		((MantraDataCommon) data).markContinue(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onSpellingEffect(World world, IMantraData data, ICaster caster) {
		super.onSpellingEffect(world, data, caster);
		addEffectIndicatorEffect(world, data, caster);
	}

	@Override
	public void onCollectElement(World world, IMantraData data, ICaster caster, int speedTick) {
		MantraDataCommon mData = (MantraDataCommon) data;
		CollectResult cr = mData.tryCollect(caster, ESInit.ELEMENTS.MAGIC, 10, 50, 2000);
		mData.setProgress(cr.getStackCount(), 2000);
	}

	@Override
	public void endSpelling(World world, IMantraData data, ICaster caster) {
		MantraDataCommon mData = (MantraDataCommon) data;
		ElementStack estack = mData.get(ESInit.ELEMENTS.MAGIC);
		if (estack.getCount() < 500) return;
		WorldTarget wr = caster.iWantBlockTarget();
		BlockPos pos = wr.getPos();
		if (pos == null) return;
		mData.set(Variables.TICK, 80);
		float p = caster.iWantBePotent(2, false) + 1;
		estack.setPower((int) (estack.getPower() * (p * p)));
		EntityHelper.setPositionAndUpdate(caster.iWantDirectCaster(), new Vec3d(pos).add(0.5, 0.5, 0.5));
	}

	@Override
	public boolean afterSpelling(World world, IMantraData mData, ICaster caster) {
		MantraDataCommon data = (MantraDataCommon) mData;
		ElementStack eStack = data.get(ESInit.ELEMENTS.MAGIC);
		if (eStack.isEmpty()) return false;
		int tick = Math.min(data.get(Variables.TICK), 80);
		data.set(Variables.TICK, tick - 1);
		if (tick <= 0) return false;

		double fragment = ElementHelper.toFragment(eStack);
		double size = Math.floor(MathHelper.clamp(Math.sqrt(fragment) / 2048, 1, 8));
		if (world.isRemote) addAfterEffect(data, caster, tick, size);

		float ratio = 1 - tick / 80f;
		if (ratio <= 0.4) size *= MathSupporter.easeInOutElastic(ratio / 0.4f);
		else if (ratio >= 0.9) size *= MathSupporter.easeOutBack((1 - ratio) / 0.1f);

		Entity casterEntity = caster.iWantDirectCaster();
		Vec3d vec = casterEntity.getPositionVector();
		BlockPos pos = new BlockPos(vec);

		int rx = MathHelper.ceil(size);
		if (world.isRemote) return true;
		if (tick % 10 != 0) return true;
		for (int x = -rx; x <= rx; x++) {
			int rzy = MathHelper.ceil(Math.cos(x / (double) rx * Math.PI / 2f) * size);
			for (int z = -rzy; z <= rzy; z++) {
				for (int y = -rzy; y <= rzy; y++) {
					BlockPos at = pos.add(x, y, z);
					if (world.isAirBlock(at)) continue;
					if (BlockHelper.isBedrock(world, pos)) continue;
					affect(world, caster, at, fragment);
				}
			}
		}

		size = size + 0.5f;
		AxisAlignedBB aabb = WorldHelper.createAABB(vec, size, size, size);
		List<Entity> list = world.getEntitiesWithinAABB(Entity.class, aabb);
		for (Entity entity : list) {
			Vec3d eVec = entity.getPositionVector().add(0, entity.height / 2, 0);
			if (vec.squareDistanceTo(eVec) > size * size) continue;
			if (entity instanceof EntityLivingBase) affect(world, caster, (EntityLivingBase) entity, fragment);
			else if (entity instanceof EntityItem) affect(world, caster, ((EntityItem) entity), fragment);
		}
		return true;
	}

	public static void affect(World world, ICaster caster, BlockPos at, double fragment) {
		IBlockState state = world.getBlockState(at);
		Block block = state.getBlock();
		if (block == ESInit.BLOCKS.ELEMENT_REACTOR) return;
		if (block == ESInit.BLOCKS.ESTONE_PRISM) return;
		if (block instanceof BlockElementContainer) {
			IElementInventory eInv = BlockHelper.getElementInventory(world, at, null);
			world.destroyBlock(at, false);
			BlockElementContainer.doExploded(world, at, eInv, caster.iWantCaster().asEntityLivingBase());
		}
		if (world.rand.nextFloat() < 0.25) ItemHelper.dropItem(world, at, new ItemStack(ESInit.ITEMS.MATERIAL_DEBRIS));
		world.destroyBlock(at, false);
	}

	public static void affect(World world, ICaster caster, EntityLivingBase entity, double fragment) {
		if (EntityHelper.isCreative(entity)) return;

		IWorldObject worldObj = caster.iWantCaster();
		double dmg = TileIceRockStand.getDamageWithFragment(fragment) / 4;
		DamageSource ds = DamageHelper.getMagicDamageSource(worldObj.asEntity(), caster.iWantDirectCaster());
		ds.setDamageBypassesArmor();
		entity.attackEntityFrom(ds, (float) dmg);

		Vec3d vec = caster.iWantDirectCaster().getPositionVector();
		Vec3d eVec = entity.getPositionVector().add(0, entity.height / 2, 0);
		Vec3d tar = eVec.subtract(vec);
		double len = tar.length();
		tar = tar.normalize().scale(Math.min(8 / (len + 0.25), 2));
		entity.motionX += tar.x;
		entity.motionY += tar.y;
		entity.motionZ += tar.z;
		entity.velocityChanged = true;
	}

	public static void affect(World world, ICaster caster, EntityItem entityItem, double fragment) {
		if (entityItem.getIsInvulnerable()) return;
		ItemStack stack = entityItem.getItem();
		Item item = stack.getItem();
		if (item == ESInit.ITEMS.ELEMENT_CRACK) return;
		if (item == ESInit.ITEMS.MATERIAL_DEBRIS) return;
		IElementInventory eInv = ElementHelper.getElementInventory(stack);
		BlockElementContainer.doExploded(world, entityItem.getPosition(), eInv,
				caster.iWantCaster().asEntityLivingBase());
		entityItem.setDead();
	}

	@SideOnly(Side.CLIENT)
	public void addAfterEffect(MantraDataCommon data, ICaster caster, int tick, double size) {
		if (data.hasMarkEffect(1000)) return;
		Entity entity = caster.iWantDirectCaster();
		EffectSphericalBlast effect = new EffectSphericalBlast(entity.world, entity.getPositionVector(), (float) size);
		effect.lifeTime = tick;
		effect.color.setColor(0xb736ff);
		Effect.addEffect(effect);
		data.markEffect(1000, effect);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderShiftIcon(NBTTagCompound mantraData, float suggestSize, float suggestAlpha, float partialTicks) {
		TextureBinder.bindTexture(GuiMantraShitf.CIRCLE);
		RenderHelper.drawTexturedRectInCenter(0, 0, suggestSize, suggestSize);
		GlStateManager.color(1, 1, 1);
		TextureBinder.bindTexture(this.icon);
		suggestSize = suggestSize * 0.5f;
		RenderHelper.drawTexturedRectInCenter(0, 0, suggestSize, suggestSize);
	}

}
