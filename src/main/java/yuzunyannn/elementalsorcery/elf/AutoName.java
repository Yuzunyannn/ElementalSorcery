package yuzunyannn.elementalsorcery.elf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import yuzunyannn.elementalsorcery.ESData;
import yuzunyannn.elementalsorcery.ElementalSorcery;
import yuzunyannn.elementalsorcery.util.IOHelper;
import yuzunyannn.elementalsorcery.util.RandomHelper;
import yuzunyannn.elementalsorcery.util.json.JsonArray;
import yuzunyannn.elementalsorcery.util.json.JsonObject;

/** 自动名称，精灵使用 */
public class AutoName {

	protected final ArrayList<ArrayList<String>> partList = new ArrayList<>();

	public List<String> getPart(int index) {
		return partList.get(index);
	}

	public List<String> newPart() {
		partList.add(new ArrayList<String>());
		return partList.get(partList.size() - 1);
	}

	public int partSize() {
		return partList.size();
	}

	public AutoName() {

	}

	public AutoName(JsonObject jobj) {
		this.fromJson(jobj);
	}

	public void fromJson(JsonObject json) {
		for (int i = 0; i < 5; i++) {
			String key = "part" + i;
			if (json.hasArray(key)) {
				JsonArray jarray = json.getArray(key);
				List<String> part = this.newPart();
				for (int j = 0; j < jarray.size(); j++) {
					if (jarray.hasString(i)) part.add(jarray.getString(i));
				}
			}
		}
	}

	public JsonObject getSerializableElement() {
		JsonObject jobj = new JsonObject();
		for (int i = 0; i < partList.size(); i++) {
			List<String> list = partList.get(i);
			JsonArray jarray = new JsonArray();
			for (String str : list) jarray.append(str);
			if (jarray.size() > 0) jobj.set("part" + i, jarray);
		}
		return jobj;
	}

	/** 获取一个随机名字 */
	public String randomName() {
		if (partList.isEmpty()) return "nobody";
		StringBuilder s = new StringBuilder();
		for (ArrayList<String> part : partList) {
			if (part.isEmpty()) continue;
			s.append(part.get(RandomHelper.rand.nextInt(part.size())));
		}
		String name = s.toString();
		return name.isEmpty() ? "nobody" : name;
	}

	public static String getRandomName() {
		AutoName autoName = autoNames.get(RandomHelper.rand.nextInt(autoNames.size()));
		return autoName.randomName();
	}

	static protected final List<AutoName> autoNames = new ArrayList();

	static public void register(AutoName autoName) {
		autoNames.add(autoName);
	}

	static public void init() {
		File file = ElementalSorcery.data.getFile("elf/names", "");
		List<String> paths = ESData.getFileRecursion(file);
		for (String path : paths) {
			file = new File(path);
			FileInputStream inputStream = null;
			try {
				inputStream = new FileInputStream(file);
				JsonObject jobj = new JsonObject(new InputStreamReader(inputStream, "utf-8"));
				register(new AutoName(jobj));
			} catch (Exception e) {
				ElementalSorcery.logger.warn("读取自动名称出现异常！", e);
			} finally {
				IOHelper.closeQuietly(inputStream);
			}
		}
		if (autoNames.isEmpty()) initDefault();
	}

	static private void initDefault() {
		AutoName autoName = new AutoName();
		String[] surname = { "赵", "钱", "孙", "李", "周", "吴", "郑", "王", "冯", "陈", "楮", "卫", "蒋", "沈", "韩", "杨", "朱", "秦",
				"尤", "许", "何", "吕", "施", "张", "孔", "曹", "严", "华", "金", "魏", "陶", "姜", "戚", "谢", "邹", "喻", "柏", "水", "窦",
				"章", "云", "苏", "潘", "葛", "奚", "范", "彭", "郎", "鲁", "韦", "昌", "马", "苗", "凤", "花", "方", "俞", "任", "袁", "柳",
				"酆", "鲍", "史", "唐", "欧阳", "太史", "端木", "上官", "司马", "东方", "独孤", "南宫", "万俟", "闻人", "夏侯", "诸葛", "尉迟", "公羊",
				"赫连", "澹台", "皇甫", "宗政", "濮阳", "公冶", "太叔", "申屠", "公孙", "慕容", "仲孙", "钟离", "长孙", "宇文", "城池", "司徒", "鲜于",
				"司空", "闾丘", "子车", "亓官", "司寇", "巫马", "公西", "颛孙", "壤驷", "公良", "漆雕", "乐正", "宰父", "谷梁", "拓跋", "夹谷", "轩辕",
				"令狐", "段干", "百里", "呼延", "东郭", "南门", "羊舌", "微生", "公户", "公玉", "公仪", "梁丘", "公仲", "公上", "公门", "公山", "公坚",
				"左丘", "公伯", "西门", "公祖", "第五", "公乘", "贯丘", "公皙", "南荣", "东里", "东宫", "仲长", "子书", "子桑", "即墨", "达奚", "褚师",
				"吴铭" };
		String name[] = { "吹雪", "观博", "欣竹", "欣阳", "刚军", "扬阳", "靖阳", "熙阳", "嘉萱", "铭阳", "飞", "雨荨", "文博", "诗含", "诗若", "辰海",
				"晓雨", "展鸣", "晓春", "洪文", "默", "轩杰", "金海", "俊杰", "展旭", "建烁", "婧琪", "婧涵", "诗晴", "传浩", "怡萍", "诗涵", "雅婷",
				"雅涵", "萍", "晓萍", "兴飞", "小平", "建龙", "宇谟", "子辰", "辰", "湍灵", "骅株", "春莲", "娟敏", "智涵", "欣妍", "慧妍", "雅静",
				"月婷", "雨婷", "芸馨", "韵涵", "涵韵", "雨欣", "馨蕾", "静媛", "子涵", "雨泽", "静蕾", "茛淯", "珑沧", "芮娟", "梓萱", "轶诚", "嘉文",
				"晓朋", "一凡", "昊楠", "浩楠", "瑞君", "佳宁", "雨杨", "昊然", "浩然", "滕浩", "雨菡", "海一", "晨宸", "之政", "晨菲", "修闻", "宁夫",
				"轩", "春菲", "佳涵", "耀宇", "耀雨", "翠", "鑫雨", "涵熙", "继欣", "菲", "兰月", "兰欣", "岚欣", "懿明", "淑菲", "荣凯", "海瑶", "涵雅",
				"晨曦", "麟炜", "茜", "子萱", "玥菲", "雯菲", "云涵", "靖雯", "馨怡", "江", "运浩", "飞飞", "强", "国馨", "国鑫", "雅雯", "炳君", "海宇",
				"海林", "瑾瑜", "成龙", "嘉麟", "芸惜", "芸希", "瀚曦", "晏", "鑫龙", "嘉懿", "永琴", "贝馨", "润芝", "润龙", "浩龙", "焱涵", "芙",
				"敖菲", "子君", "少兹", "子晨", "辰曦", "云曦", "梓恒", "晏畅", "晏郡", "洛瑜", "朔瑜", "晃郡", "展彰", "展荣", "展郡", "展瑜", "朔诰",
				"洛荣", "紫涵", "蕾", "津", "轩旗", "津诰", "津郡", "津荣", "津畅", "津飒", "津彰", "津瑜", "桀诰", "晃荣", "轩语", "桀瑔", "晃瑜", "晃",
				"荣泽", "小强", "晏诰", "桀畅", "仓", "月", "氏", "勿", "欠", "风", "丹", "匀", "乌", "凤", "勾", "文", "六", "方", "火", "为",
				"斗", "忆", "订", "计", "户", "认", "心", "尺", "引", "丑", "巴", "孔", "队", "办", "以", "允", "予", "劝", "双", "书", "幻",
				"玉", "刊", "示", "末", "未", "击", "打", "巧", "正", "扑", "扒", "功", "扔", "去", "甘", "世", "古", "节", "本", "术", "可",
				"约", "纪", "驰", "巡", "寿", "哥", "弄", "麦", "形", "进", "戒", "吞", "远", "违", "运", "扶", "抚", "坛", "技", "坏", "扰",
				"拒", "找", "批", "扯", "址", "走", "抄", "坝", "贡", "攻", "赤", "折", "抓", "扮", "抢", "孝", "均", "抛", "投", "坟", "抗",
				"坑", "坊", "抖", "护", "壳", "志", "扭", "块", "声", "把", "报", "却", "劫", "芽", "花", "芹", "芬", "苍", "芳", "严", "芦",
				"劳", "克", "苏", "杆", "杠", "杜", "材", "村", "杏", "极", "李", "杨", "求", "更", "束", "豆", "两", "丽", "医", "辰", "励",
				"否", "还", "歼", "来", "连", "步", "坚", "旱", "盯", "呈", "时", "吴", "助", "县", "里", "呆", "园", "旷", "围", "呀", "吨",
				"足", "邮", "男", "困", "吵", "串", "员", "听", "吩", "吹", "呜", "吧", "吼", "别", "岗", "帐", "财", "针", "钉", "告", "我",
				"乱", "利", "秃", "秀", "私", "每", "兵", "估", "体", "何", "但", "伸", "作", "伯", "伶", "佣", "低", "你", "住", "位", "伴",
				"身", "皂", "佛", "近", "彻", "役", "返", "余", "希", "坐", "谷", "妥", "含", "邻", "岔", "肝", "肚", "肠", "龟", "免", "狂",
				"犹", "角", "删", "条", "卵", "岛", "迎", "饭", "饮", "系", "言", "冻", "状", "亩", "况", "床", "库", "疗", "应", "冷", "这",
				"序", "辛", "弃", "冶", "忘", "闲", "间", "闷", "判", "灶", "灿", "弟", "汪", "沙", "汽", "沃", "泛", "沟", "没", "沈", "沉",
				"怀", "忧", "快", "完", "宋", "宏", "牢", "究", "穷", "灾", "良", "证", "启", "评", "补", "初", "社", "识", "诉", "诊", "词",
				"译", "君", "灵", "即", "层", "尿", "尾", "迟", "局", "改", "张", "忌", "际", "陆", "阿", "陈", "阻", "附", "妙", "妖", "妨",
				"努", "忍", "劲", "鸡", "驱", "纯", "纱", "纳", "纲", "杰", "述", "枕", "丧", "或", "画", "卧", "事", "刺", "枣", "雨", "卖",
				"矿", "码", "厕", "奔", "奇", "奋", "态", "欧", "垄", "妻", "轰", "顷", "转", "斩", "轮", "软", "到", "非", "叔", "肯", "齿",
				"些", "虎", "虏", "肾", "贤", "尚", "旺", "具", "果", "废", "净", "盲", "放", "刻", "育", "闸", "闹", "郑", "券", "卷", "单",
				"炒", "炊", "炕", "炎", "炉", "沫", "浅", "法", "泄", "河", "沾", "泪", "油", "泊", "沿", "泡", "注", "泻", "泳", "泥", "沸",
				"波", "泼", "泽", "治", "怖", "性", "怕", "怜", "怪", "学", "宝", "宗", "定", "宜", "审", "宙", "官", "空", "帘", "实", "试",
				"郎", "妹", "姑", "姐", "姓", "始", "驾", "参", "艰", "线", "练", "组", "细", "驶", "织", "终", "驻", "驼", "绍", "经", "贯",
				"奏", "春", "帮", "珍", "玻", "毒", "型", "挂", "封", "持", "项", "垮", "挎", "城", "挠", "政", "赴", "赵", "挡", "挺", "括",
				"拴", "拾", "挑", "指", "垫", "挣", "挤", "拼", "挖", "按", "挥", "挪", "某", "甚", "革", "荐", "巷", "带", "草", "茧", "茶",
				"荒", "茫", "荡", "荣", "故", "胡", "南" };
		autoName.newPart().addAll(Arrays.asList(surname));
		autoName.newPart().addAll(Arrays.asList(name));
		register(autoName);
		// 写入
		File file = ElementalSorcery.data.getFile("elf/names", "default.json");
		OutputStream out = null;
		try {
			out = new FileOutputStream(file);
			out.write(autoName.getSerializableElement().toString().getBytes("utf-8"));
		} catch (Exception e) {} finally {
			IOHelper.closeQuietly(out);
		}
	}

}
