package yuzunyannn.elementalsorcery.elf.quest.loader;

import java.util.Map;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import yuzunyannn.elementalsorcery.elf.quest.QuestType;
import yuzunyannn.elementalsorcery.elf.quest.condition.IQuestConditionPrice;
import yuzunyannn.elementalsorcery.elf.quest.condition.QuestCondition;
import yuzunyannn.elementalsorcery.util.RandomHelper;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

/** 参数获取，这版写的不太好，先凑合了 */
public class ParamObtain {

	static public <T> T parser(JsonObject json, String key, Map<String, Object> context, Class<T> type) {
		Object obj = parser(json, key, context);
		if (type.isAssignableFrom(obj.getClass())) return (T) obj;
		throw new QuestCreateFailException(obj.getClass() + " is get by [" + key + "] but need " + type);
	}

	static public Object parser(JsonObject json, String key, Map<String, Object> context) {
		if (json.hasNumber(key)) return json.getNumber(key);
		else if (json.hasString(key)) return parser(json.getString(key), context);
		throw new QuestCreateFailException(key + "'s type is not string or number");
	}

	static public Object parser(String any, Map<String, Object> context) {
		any = any.trim();
		try {
			if (any.matches("^\\{(.+?)\\}$")) {
				Object obj = parserStringToObject(any, context);
				if (obj == null) throw new QuestCreateFailException("can't find op:" + any);
				return obj;
			}
			return parserStringToString(any, context);
		} catch (ClassCastException | NullPointerException e) {
			throw new QuestCreateFailException(e);
		}

	}

	static <T> T checkAndGet(Map<String, Object> context, String key, Class<T> cls) {
		Object obj = context.get(key);
		if (obj == null) throw new QuestCreateFailException("can't find content [" + key + "]");
		return (T) obj;
	}

	public static BiFunction<String, String, Object> otherHandle;

	static private Object parserStringToObject(String any, Map<String, Object> context) {
		any = any.substring(1, any.length() - 1).trim();

		if (any.charAt(0) == '$') return context.get(any);

		String[] ctrl = any.split("#");
		String type = ctrl[0];

		switch (type) {
		case "player":
			EntityLivingBase player = checkAndGet(context, "player", EntityLivingBase.class);
			if (ctrl.length == 1) return player;
			switch (ctrl[1]) {
			case "name":
				return player.getName();
			}
			break;
		case "has": {
			if (ctrl.length < 2) throw new QuestCreateFailException("has operation is wrong, example:{has#var}");
			return context.get(ctrl[1]) != null;
		}
		case "biome": {
			if (ctrl.length < 2) throw new QuestCreateFailException("biome operation is wrong, example:{biome#random}");
			if ("random".equals(ctrl[1])) {
				Random rand = (Random) context.get("random");
				if (rand == null) rand = RandomHelper.rand;
				return Biome.REGISTRY.getRandomObject(rand);
			} else {
				return Biome.REGISTRY.getObject(new ResourceLocation(ctrl[1]));
			}
		}
		case "enchant": {
			if (ctrl.length < 2)
				throw new QuestCreateFailException("enchant operation is wrong, example:{enchant#minecraft:power@2}");
			Object obj = context.get("$");
			if (!(obj instanceof ItemStack))
				throw new QuestCreateFailException("enchant operation only can be use at item");
			ItemStack stack = (ItemStack) obj;
			boolean isBook = stack.getItem() == Items.ENCHANTED_BOOK;
			String[] ct = ctrl[1].split("@");
			String name = ct[0];
			Enchantment enchantment = null;
			Random rand = (Random) context.get("random");
			if ("random".equals(name)) {
				if (isBook) {
					for (int tryTimes = 0; tryTimes < 8; tryTimes++) {
						Enchantment e = Enchantment.REGISTRY.getRandomObject(rand);
						if (!e.isAllowedOnBooks()) continue;
						enchantment = e;
						break;
					}
				}
				enchantment = enchantment == null ? Enchantment.REGISTRY.getRandomObject(rand) : enchantment;
			} else enchantment = Enchantment.REGISTRY.getObject(new ResourceLocation(name));

			int level = 1;
			if (ct.length >= 2) {
				String levStr = ct[1];
				if ("random".equals(levStr)) level = rand.nextInt(enchantment.getMaxLevel()) + 1;
				else {
					if (levStr.indexOf('~') != -1) level = doRandom(ct, context);
					else level = Integer.parseInt(levStr);
				}
			}

			if (isBook) {
				EnchantmentData eData = new EnchantmentData(enchantment, level);
				ItemEnchantedBook.addEnchantment(stack, eData);
			} else stack.addEnchantment(enchantment, level);
			return true;
		}
		case "prandom": {
			int base = doRandom(ctrl, context);
			QuestType qType = (QuestType) context.get("self");
			Random rand = (Random) context.get("random");
			for (QuestCondition con : qType.getConditions()) {
				if (con instanceof IQuestConditionPrice) base += ((IQuestConditionPrice) con).price(rand);
			}
			return base;
		}
		case "random":
			return doRandom(ctrl, context);
		default:
			if (otherHandle != null) return otherHandle.apply(type, ctrl.length > 1 ? ctrl[1] : null);
			break;
		}

		return null;
	}

	static private int doRandom(String[] ctrl, Map<String, Object> context) {
		if (ctrl.length < 2) throw new QuestCreateFailException("random operation is wrong, example:{random#1~3}");
		try {
			Random rand = (Random) context.get("random");
			if (rand == null) rand = RandomHelper.rand;
			String[] numStr = ctrl[1].split("~");
			int min = Integer.parseInt(numStr[0]);
			int max = Integer.parseInt(numStr[1]);
			return min + rand.nextInt(max - min + 1);
		} catch (Exception e) {
			throw new QuestCreateFailException("random operation is wrong, example:{random#1~3}");
		}
	}

	static private String parserStringToString(String any, Map<String, Object> context) {
		Pattern p = Pattern.compile("(\\{.+?\\})");
		String[] strs = p.split(any);
		Matcher m = p.matcher(any);
		int i = 0;
		StringBuilder builder = new StringBuilder();
		while (m.find()) {
			builder.append(strs[i]);
			builder.append(parserStringToObject(m.group(1), context));
			i++;
		}
		if (i < strs.length) builder.append(strs[i]);
		return builder.toString();
	}

}
