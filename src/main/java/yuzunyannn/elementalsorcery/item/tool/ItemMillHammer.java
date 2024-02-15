package yuzunyannn.elementalsorcery.item.tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.Multimap;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.ESObjects;
import yuzunyannn.elementalsorcery.crafting.ISmashRecipe;
import yuzunyannn.elementalsorcery.crafting.SmashRecipe;
import yuzunyannn.elementalsorcery.crafting.element.ElementMap;
import yuzunyannn.elementalsorcery.item.prop.ItemPadEasyPart;
import yuzunyannn.elementalsorcery.item.prop.ItemPadEasyPart.EnumType;
import yuzunyannn.elementalsorcery.render.effect.Effect;
import yuzunyannn.elementalsorcery.render.effect.Effects;
import yuzunyannn.elementalsorcery.render.effect.ParticleEffects;
import yuzunyannn.elementalsorcery.util.helper.EntityHelper;
import yuzunyannn.elementalsorcery.util.helper.OreHelper;
import yuzunyannn.elementalsorcery.util.helper.OreHelper.OreEnum;
import yuzunyannn.elementalsorcery.util.item.ItemEntityProxy;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;
import yuzunyannn.elementalsorcery.util.json.ItemRecord;
import yuzunyannn.elementalsorcery.util.json.Json;
import yuzunyannn.elementalsorcery.util.json.JsonObject;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class ItemMillHammer extends Item {

	public ItemMillHammer() {
		this.setTranslationKey("millHammer");
		this.setMaxStackSize(1);
		this.setMaxDamage(200);
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
		if (slot == EntityEquipmentSlot.MAINHAND) {
			multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
					new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -3.7, 0));
			multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
					new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", 9, 0));
		}
		return multimap;
	}

	public static void onCreateMillHammer(ItemStack hammer) {
		Map<Enchantment, Integer> enchMap = new HashMap<>();
		enchMap.put(Enchantments.KNOCKBACK, 2);
		EnchantmentHelper.setEnchantments(enchMap, hammer);
	}

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
		stack.damageItem(1, attacker);
		return true;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		float f = playerIn.getCooledAttackStrength(0);
		if (f < 1) return super.onItemRightClick(worldIn, playerIn, handIn);
		playerIn.swingArm(handIn);
		playerIn.resetCooldown();
		ItemStack stack = playerIn.getHeldItem(handIn);
		if (worldIn.isRemote) return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);

		RayTraceResult ray = WorldHelper.getLookAtEntity(worldIn, playerIn, 4,
				e -> e.canBeCollidedWith() || ItemEntityProxy.isItemEntity(e));
		if (ray == null) ray = WorldHelper.getLookAtBlock(worldIn, playerIn, 4, true, false, true);

		if (ray == null) return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);

		stack.damageItem(10, playerIn);

		float damage = (float) playerIn.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
		superAttack(worldIn, ray.hitVec, damage, playerIn);

		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}

	public static void superAttack(World world, Vec3d vec, float damage, EntityLivingBase attacker) {

		AxisAlignedBB aabb = WorldHelper.createAABB(vec, 1.5, 1.5, 1.25);
		List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, aabb, e -> {
			if (ItemEntityProxy.isItemEntity(e)) return true;
			if (e instanceof EntityLivingBase) return !EntityHelper.isSameTeam(attacker, e);
			return false;
		});

		for (Entity entity : entities) {
			double distance = entity.getDistanceSq(vec.x, vec.y - entity.getEyeHeight(), vec.z);
			ItemEntityProxy entityItem = ItemEntityProxy.proxy(entity);
			if (entityItem != null) {
				if (distance > 0.5) continue;
				ItemStack itemStack = entityItem.getItemStack();
				if (itemStack.isEmpty()) continue;
				List<ItemStack> list = new ArrayList<>();
				Vec3d itemVec = entity.getPositionVector();
				int code = ISmashRecipe.smash(world, itemVec, itemStack, list, attacker);
				if (code >= 0) {
					if (code == 1) entityItem.setItemStack(itemStack);
					else if (code == 2) entity.setDead();
					for (ItemStack output : list) ItemHelper.dropItem(world, itemVec, output);
				}
				continue;
			}
			if (entity instanceof EntityLivingBase) {
				DamageSource ds;
				if (attacker instanceof EntityPlayer) ds = DamageSource.causePlayerDamage((EntityPlayer) attacker);
				else ds = DamageSource.causeMobDamage(attacker);
				float realDamage = damage;
				if (distance > 1) realDamage /= distance;
				entity.attackEntityFrom(ds, realDamage);
			}
		}

		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setByte("type", ParticleEffects.MILL_HAMMER);
		Effects.spawnEffect(world, Effects.PARTICLE_EFFECT, vec, nbt);
	}

	@SideOnly(Side.CLIENT)
	public static void doEffect(World world, Vec3d pos, NBTTagCompound nbt) {
		Random rand = Effect.rand;
		for (int i = 0; i < 8; i++) {
			Vec3d rVec = pos.add(new Vec3d(rand.nextGaussian(), rand.nextGaussian(), rand.nextGaussian()).normalize());
			Vec3d speed = new Vec3d(rand.nextGaussian(), 0, rand.nextGaussian()).normalize().scale(0.1).add(0,
					rand.nextDouble() * 0.1, 0);
			world.spawnParticle(EnumParticleTypes.CLOUD, rVec.x, rVec.y, rVec.z, speed.x, speed.y, speed.z);
		}
	}

	public static void registerAll() {
		final ESObjects.Items ITEMS = ESObjects.ITEMS;
		final ESObjects.Blocks BLOCKS = ESObjects.BLOCKS;
		SmashRecipe.register(Items.BONE, new ItemStack(Items.DYE, 3, 15));
		SmashRecipe.register(Items.BLAZE_ROD, new ItemStack(Items.BLAZE_POWDER, 2));
		SmashRecipe.register(Blocks.REDSTONE_TORCH, new ItemStack(Items.REDSTONE, 1));
		SmashRecipe.register(Items.BOAT, new ItemStack(Items.STICK, 3));
		SmashRecipe.register(new ItemStack(Items.ARROW), new ItemStack(Items.FLINT), new ItemStack(Items.FEATHER));
		SmashRecipe.register(ITEMS.ELF_COIN, new ItemStack(ITEMS.ELF_CRYSTAL, 1));
		SmashRecipe.register(ITEMS.NATURE_CRYSTAL, ITEMS.NATURE_DUST).setSoundEvent(SoundEvents.BLOCK_GLASS_BREAK);
		SmashRecipe.register(BLOCKS.ELEMENTAL_CUBE, new ItemStack(ITEMS.ELEMENT_CRYSTAL, 4));
		SmashRecipe.register(ITEMS.MAGIC_STONE, new ItemStack(ITEMS.MAGIC_PIECE, 4))
				.setSoundEvent(SoundEvents.BLOCK_GLASS_BREAK);
		SmashRecipe
				.register(new ItemStack(ITEMS.MAGIC_TERMINAL), ItemPadEasyPart.create(EnumType.FLUORESCENT_PARTICLE, 4),
						ItemPadEasyPart.create(EnumType.CONTROL_CIRCUIT, 1))
				.setSoundEvent(SoundEvents.BLOCK_GLASS_BREAK);
		SmashRecipe
				.register(new ItemStack(ITEMS.TUTORIAL_PAD), ItemPadEasyPart.create(EnumType.FLUORESCENT_PARTICLE, 16),
						ItemPadEasyPart.create(EnumType.CONTROL_CIRCUIT, 3),
						ItemPadEasyPart.create(EnumType.ACCESS_CIRCUIT, 1),
						ItemPadEasyPart.create(EnumType.DISPLAY_CIRCUIT, 1),
						ItemPadEasyPart.create(EnumType.CALCULATE_CIRCUIT, 1))
				.setSoundEvent(SoundEvents.BLOCK_GLASS_BREAK);

		// 矿物
		List<OreEnum> ores = OreHelper.getOreEnumList();
		for (OreEnum ore : ores) {
			NonNullList<ItemStack> oreStacks = ore.getOres();
			if (oreStacks.isEmpty()) continue;
			ItemStack crushedStack = ore.createCrushedOre();
			if (!crushedStack.isEmpty()) {
				crushedStack = crushedStack.copy();
				crushedStack.setCount(crushedStack.getCount() + 1);
				SmashRecipe.register(OreHelper.toIngredient(oreStacks), crushedStack).setMaxUseCount(4);
			}
		}

		// 自定义
		Json.ergodicFile("recipes/smash", (file, json) -> readJson(json));
	}

	static public boolean readJson(JsonObject json) {
		ResourceLocation typePair = ElementMap.getType(json);
		if (typePair == null) return false;
		if (!ESAPI.MODID.equals(typePair.getNamespace())) return false;
		if (!ElementMap.checkModDemands(json)) return false;
		String type = typePair.getPath();
		if (!"smash".equals(type)) return false;
		List<ItemRecord> input = json.needItems("input");
		Ingredient ingredient = ItemRecord.asIngredient(input);
		List<ItemRecord> result = json.needItems("result");
		SmashRecipe recipe = SmashRecipe.register(ingredient, ItemRecord.asItemStackArray(result));
		if (json.hasNumber("onceMax")) recipe.setMaxUseCount(Math.max(1, json.getNumber("onceMax").intValue()));
		return true;
	}
}
