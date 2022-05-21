package yuzunyannn.elementalsorcery.element;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import yuzunyannn.elementalsorcery.api.crafting.IToElementInfo;
import yuzunyannn.elementalsorcery.api.util.IWorldObject;
import yuzunyannn.elementalsorcery.api.util.WorldTarget;
import yuzunyannn.elementalsorcery.crafting.element.ElementMap;
import yuzunyannn.elementalsorcery.element.explosion.EEMetal;
import yuzunyannn.elementalsorcery.element.explosion.ElementExplosion;
import yuzunyannn.elementalsorcery.init.ESInit;
import yuzunyannn.elementalsorcery.util.VariableSet;
import yuzunyannn.elementalsorcery.util.element.DrinkJuiceEffectAdder;
import yuzunyannn.elementalsorcery.util.element.ElementHelper;
import yuzunyannn.elementalsorcery.util.helper.BlockHelper;
import yuzunyannn.elementalsorcery.util.helper.OreHelper;
import yuzunyannn.elementalsorcery.util.helper.OreHelper.OreEnum;
import yuzunyannn.elementalsorcery.util.item.ItemHelper;
import yuzunyannn.elementalsorcery.world.JuiceMaterial;

public class ElementMetal extends ElementCommon {

	public static final int COLOR = 0xFFD700;
	protected List<ItemStack> ores;

	public ElementMetal() {
		super(COLOR, "metal");
		setTransition(2.5f, 292.5f, 60);
		setLaserCostOnce(2, 50);
	}

	@Override
	public ElementStack starFlowerCasting(World world, BlockPos pos, ElementStack estack, int tick) {
		if (world.isRemote) return estack;
		if (tick % 100 != 0) return estack;
		int range = getStarFlowerRange(estack);
		BlockPos at = BlockHelper.tryFind(world, (w, p) -> {
			IBlockState state = w.getBlockState(p);
			return state.getBlock() == Blocks.STONE || state.getBlock() == Blocks.COBBLESTONE;
		}, pos, Math.min(estack.getPower() / 50, 16), range, range);
		if (at == null) return estack;
		if (ores == null) this.initCanUseOres();

		ItemStack ore = ores.get(world.rand.nextInt(ores.size()));
		IToElementInfo info = ElementMap.instance.toElement(ore);
		ElementStack[] elements = info.element();
		ElementStack meta = elements[0];

		int rCount = (int) (meta.getCount() * (0.7 + world.rand.nextFloat() * 0.5));
		int rPower = (int) (meta.getCount() * (0.5 + world.rand.nextFloat() * 1));
		if (estack.getCount() > rCount && estack.getCount() > rPower) {
			Block block = Block.getBlockFromItem(ore.getItem());
			IBlockState state = block.getStateFromMeta(ore.getMetadata());
			world.setBlockState(at, state);
			world.playEvent(2001, at, Block.getStateId(world.getBlockState(at)));
			estack.shrink(rCount);
		}

		return estack;
	}

	public void initCanUseOres() {
		ores = new ArrayList<>();
		for (String name : OreDictionary.getOreNames()) {
			if (!name.startsWith("ore")) continue;
			NonNullList<ItemStack> list = OreDictionary.getOres(name);
			for (ItemStack stack : list) {
				IToElementInfo info = ElementMap.instance.toElement(stack);
				if (info == null) continue;
				ElementStack[] elements = info.element();
				if (elements.length > 0 && elements[0].getElement() != this) break;
				if (Block.getBlockFromItem(stack.getItem()) != Blocks.AIR) ores.add(stack);
				break;
			}
		}
	}

	@Override
	public ElementExplosion newExplosion(World world, Vec3d pos, ElementStack eStack, EntityLivingBase attacker) {
		return new EEMetal(world, pos, ElementExplosion.getStrength(eStack), eStack);
	}

	@Override
	protected void addDrinkJuiceEffect(DrinkJuiceEffectAdder helper) {

		helper.preparatory(MobEffects.BLINDNESS, 8, 0);
		helper.check(JuiceMaterial.ELF_FRUIT, 375).join();

		helper.preparatory(ESInit.POTIONS.FLUORESCE_WALKER, 30, 75);
		helper.check(JuiceMaterial.APPLE, 75).checkRatio(JuiceMaterial.MELON, 0.1f, 0.5f).join();

		helper.preparatory(MobEffects.NIGHT_VISION, 35, 0);
		helper.check(JuiceMaterial.ELF_FRUIT, 100).checkRatio(JuiceMaterial.APPLE, 0.2f, 1.2f).join();

		helper.preparatory(ESInit.POTIONS.GOLDEN_EYE, 35, 100);
		helper.check(JuiceMaterial.ELF_FRUIT, 100).checkRatio(JuiceMaterial.MELON, 0.2f, 1.2f).join();

	}

	@Override
	protected void onExecuteLaser(World world, IWorldObject caster, WorldTarget target, ElementStack storage,
			VariableSet content) {
		if (world.isRemote) return;

		Entity entity = target.getEntity();
		if (entity != null) {
			if (entity instanceof EntityLivingBase) {
				EntityLivingBase living = (EntityLivingBase) entity;
				int power = storage.getPower();
				int lev = (int) MathHelper.clamp(power / 200f, 1, 3);
				int time = storage.getPower() / 50 * 40;
				living.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, time, lev));
				living.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, time, lev));
				living.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, time, lev));
				living.addPotionEffect(new PotionEffect(MobEffects.GLOWING, time / 2, 1));
				living.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, time / 2, 1));
				living.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, time / 4, 1));
				living.addPotionEffect(new PotionEffect(MobEffects.POISON, time / 5, 1));
			}
			return;
		}

		BlockPos pos = target.getPos();
		if (BlockHelper.isBedrock(world, pos)) return;
		IBlockState state = world.getBlockState(pos);
		OreEnum ore = OreHelper.getOreInfo(state);
		if (ore == null) return;

		ItemStack stack = ore.createOreProduct(0);
		if (stack.isEmpty()) return;
		IToElementInfo info = ElementMap.instance.toElement(stack);
		if (info == null) return;
		ElementStack[] eStacks = info.element();
		if (eStacks.length != 1) canGO: {
			if (eStacks.length == 2 && eStacks[1].isMagic()) break canGO;
			return;
		}
		ElementStack eStack = eStacks[0];
		if (eStack.getElement() != this) return;

		storage = storage.copy();
		storage.setCount(1);
		double storageFragment = ElementHelper.toFragment(storage);
		double targetFragment = ElementHelper.toFragment(eStack);
		double ratio = Math.min(1.4, storageFragment / targetFragment);
		if (ratio > 0.5) ratio = 0.5 + (ratio - 0.5) * 0.5f;
		if (rand.nextDouble() < ratio) ItemHelper.dropItem(world, pos, stack);
	}

}
