package yuzunyannn.elementalsorcery.entity.fcube;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.api.element.ElementStack;
import yuzunyannn.elementalsorcery.api.entity.FairyCubeModule;
import yuzunyannn.elementalsorcery.api.tile.IElementInventory;
import yuzunyannn.elementalsorcery.api.util.ItemMatchResult;
import yuzunyannn.elementalsorcery.api.util.MatchHelper;
import yuzunyannn.elementalsorcery.util.TextHelper;
import yuzunyannn.elementalsorcery.util.world.WorldHelper;

public class FairyCubeModuleInGame {

	private static void register(String id, Class<? extends FairyCubeModule> cls) {
		FairyCubeModule.REGISTRY.register(new ResourceLocation(ESAPI.MODID, id), cls);
	}

	static public void registerAll() {
		register("destory_block", FCMDestoryBlock.class);
		register("attr_silk", FCMSilk.class);
		register("fortune", FCMFortune.class);
		register("place_block", FCMPlaceBlock.class);
		register("heal", FCMHeal.class);
		register("lightweight", FCMLightweight.class);
		register("attack", FCMAttack.class);
		register("plunder", FCMPlunder.class);
		register("attack_range", FCMAttackRange.class);
		register("attack_critical", FCMAttackCritical.class);
		register("exp_up", FCMExpUp.class);
		register("ender_chest", FCMEnderChest.class);
		register("farm", FCMFarm.class);
	}

	@SideOnly(Side.CLIENT)
	private static void register(String id, IFairyCubeModuleClient render) {
		IFairyCubeModuleClient.HANDLER.put(new ResourceLocation(ESAPI.MODID, id), render);
	}

	@SideOnly(Side.CLIENT)
	private static void register(String id, int x, int y) {
		String name = TextHelper.castToCamel(id);
		register(id, new IFairyCubeModuleClient.FairyCubeModuleDeafultRender(x * 32, 224 - y * 32, name));
	}

	@SideOnly(Side.CLIENT)
	private static void register(String id, int x, int y, String customName) {
		register(id, new IFairyCubeModuleClient.FairyCubeModuleDeafultRender(x * 32, 224 - y * 32, customName) {
			@Override
			public String getDiplayName() {
				return I18n.format(unlocalizedName);
			}
		});
	}

	@SideOnly(Side.CLIENT)
	static public void registerAllRender() {
		register("destory_block", 0, 0);
		register("attr_silk", 1, 0);
		register("fortune", 2, 0);
		register("place_block", 0, 1);
		register("heal", 1, 1);
		register("lightweight", 2, 1);
		register("attack", 3, 0);
		register("plunder", 4, 0);
		register("attack_range", 5, 0);
		register("attack_critical", 6, 0);
		register("exp_up", 7, 0);
		register("ender_chest", 7, 1, "tile.enderChest.name");
		register("farm", 3, 1);
	}

	/** 通用匹配 */
	public static boolean matchAndConsumeForCraft(World world, BlockPos pos, IElementInventory inv,
			List<ItemStack> needItems, List<ElementStack> needElements) {
	
		for (ElementStack estack : needElements) {
			ElementStack extractStack = inv.extractElement(estack, true);
			if (!extractStack.arePowerfulAndMoreThan(estack)) return false;
		}
	
		List<Ingredient> list = new ArrayList<>(needItems.size());
		for (ItemStack stack : needItems) list.add(Ingredient.fromStacks(stack));
		AxisAlignedBB aabb = WorldHelper.createAABB(pos, 1.5, 1.5, 1);
		ItemMatchResult result = MatchHelper.unorderMatchInWord(list, world, aabb);
		if (!result.isSuccess()) return false;
	
		result.doShrink();
		for (ElementStack estack : needElements) inv.extractElement(estack, false);
	
		return true;
	}
}
