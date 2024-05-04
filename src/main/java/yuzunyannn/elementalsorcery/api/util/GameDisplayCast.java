package yuzunyannn.elementalsorcery.api.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentKeybind;
import net.minecraft.util.text.TextComponentScore;
import net.minecraft.util.text.TextComponentSelector;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import yuzunyannn.elementalsorcery.api.ESAPI;
import yuzunyannn.elementalsorcery.util.helper.JavaHelper;

public class GameDisplayCast {

	public final static Map<Class, ICastable> CAST_MAP = new IdentityHashMap<>();
	public final static Object OBJ = new Object();

	/**
	 * @return Display Object :<br/>
	 *         String, GameDisplayCast.OBJ, ITextComponent
	 */
	public static Object cast(Object obj, ICastEnv env) {
		if (obj == null) return null;
		if (obj instanceof IHasGameDisplay) return ((IHasGameDisplay) obj).toDisplayObject();
		if (obj instanceof List) return JavaHelper.toList((List<Object>) obj, o -> cast(o, env));
		ICastable castable = CAST_MAP.get(obj.getClass());
		if (castable == null) return OBJ;
		try {
			obj = castable.cast(obj, env);
			return obj == null ? OBJ : obj;
		} catch (Exception e) {}
		return OBJ;
	}

	public static Object cast(Object obj) {
		return cast(obj, ICastEnv.EMPTY);
	}

	public static final byte TAG_STRING = 0;
	public static final byte TAG_TEXT_COMPONENT = 1;
	public static final byte TAG_TEXT_LIST = 2;
	public static final byte TAG_TEXT_OBJ = 3;
	public static final byte TAG_TEXT_NULL = 4;

	// after cast
	public static void write(PacketBuffer buf, Object displayObject) {
		if (displayObject == null) buf.writeByte(TAG_TEXT_NULL);
		else if (displayObject == OBJ) buf.writeByte(TAG_TEXT_OBJ);
		else if (displayObject instanceof ITextComponent) {
			buf.writeByte(TAG_TEXT_COMPONENT);
			buf.writeTextComponent((ITextComponent) displayObject);
		} else if (displayObject instanceof List) {
			List list = (List) displayObject;
			buf.writeByte(TAG_TEXT_LIST);
			buf.writeShort(list.size());
			for (Object obj : list) write(buf, obj);
		} else if (displayObject instanceof String) {
			buf.writeByte(TAG_STRING);
			buf.writeString(displayObject.toString());
		} else buf.writeByte(TAG_TEXT_OBJ);
	}

	public static Object read(PacketBuffer buf) {
		int type = buf.readByte();
		switch (type) {
		case TAG_TEXT_NULL:
			return null;
		case TAG_TEXT_OBJ:
			return OBJ;
		case TAG_TEXT_COMPONENT:
			try {
				return buf.readTextComponent();
			} catch (IOException e) {
				ESAPI.logger.warn("TextComponent Read Error", e);
				return TextFormatting.RED + "<error>";
			}
		case TAG_TEXT_LIST:
			int elementCount = buf.readShort();
			List<Object> list = new ArrayList<>(elementCount);
			for (int i = 0; i < elementCount; i++) list.add(read(buf));
			return list;
		default:
			return buf.readString(32767);
		}
	}

	static {
		init();
	}

	public static void init() {
		ICastable toString = new CastToString();
		ICastable noCast = new CastDirectRet();
		CAST_MAP.put(boolean.class, toString);
		CAST_MAP.put(Boolean.class, toString);
		CAST_MAP.put(float.class, toString);
		CAST_MAP.put(Float.class, toString);
		CAST_MAP.put(double.class, toString);
		CAST_MAP.put(Double.class, toString);
		CAST_MAP.put(long.class, toString);
		CAST_MAP.put(Long.class, toString);
		CAST_MAP.put(int.class, toString);
		CAST_MAP.put(Integer.class, toString);
		CAST_MAP.put(short.class, toString);
		CAST_MAP.put(Short.class, toString);
		CAST_MAP.put(byte.class, toString);
		CAST_MAP.put(Byte.class, toString);
		CAST_MAP.put(String.class, noCast);
		CAST_MAP.put(UUID.class, toString);
		CAST_MAP.put(TextComponentString.class, noCast);
		CAST_MAP.put(TextComponentTranslation.class, noCast);
		CAST_MAP.put(TextComponentKeybind.class, noCast);
		CAST_MAP.put(TextComponentScore.class, noCast);
		CAST_MAP.put(TextComponentSelector.class, noCast);
		CAST_MAP.put(EnumFacing.class, toString);
	}

	public static class CastDirectRet implements ICastable<Object> {
		@Override
		public Object cast(Object obj, ICastEnv env) {
			return obj;
		}
	}

	public static class CastToString implements ICastable<String> {
		@Override
		public String cast(Object obj, ICastEnv env) {
			return obj.toString();
		}
	}

}
