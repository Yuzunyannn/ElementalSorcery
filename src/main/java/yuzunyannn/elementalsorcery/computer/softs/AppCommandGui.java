package yuzunyannn.elementalsorcery.computer.softs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import yuzunyannn.elementalsorcery.api.computer.soft.ISoftGuiRuntime;
import yuzunyannn.elementalsorcery.api.util.render.RenderRect;
import yuzunyannn.elementalsorcery.computer.render.AppGuiCommon;
import yuzunyannn.elementalsorcery.computer.render.GDragContainer;
import yuzunyannn.elementalsorcery.computer.render.GEasyLayoutContainer;
import yuzunyannn.elementalsorcery.computer.render.SoftGuiCommon;
import yuzunyannn.elementalsorcery.computer.render.SoftGuiThemePart;
import yuzunyannn.elementalsorcery.computer.soft.CommandParser;
import yuzunyannn.elementalsorcery.computer.soft.OPSender;
import yuzunyannn.elementalsorcery.nodegui.GImage;
import yuzunyannn.elementalsorcery.nodegui.GInput;
import yuzunyannn.elementalsorcery.nodegui.GInputShift;
import yuzunyannn.elementalsorcery.nodegui.IAutoCompletable;
import yuzunyannn.elementalsorcery.util.helper.Color;

public class AppCommandGui extends AppGuiCommon<AppCommand> implements IAutoCompletable {

	protected GInput input;
	protected GDragContainer dragContainer;
	protected GEasyLayoutContainer container;
	protected LinkedHashMap<Integer, AppCommandGRecord> nodeMap = new LinkedHashMap<>();
	protected ArrayList<String> history = new ArrayList<>();

	public AppCommandGui(AppCommand appInst) {
		super(appInst);
	}

	@Override
	protected void onInit(ISoftGuiRuntime runtime) {
		super.onInit(runtime);

		Color color = this.getThemeColor(SoftGuiThemePart.BACKGROUND_1);
		Color color1 = this.getThemeColor(SoftGuiThemePart.OBJECT_1);

		GImage inputBG = new GImage(SoftGuiCommon.TEXTURE_1, FRAME_P1);
		inputBG.setColorRef(color);
		inputBG.setSplit9();
		inputBG.setSize(runtime.getWidth(), Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT + 4);
		inputBG.setPosition(0, runtime.getHeight() - inputBG.getHeight());
		scene.addChild(inputBG);

		input = new GInput();
		inputBG.addChild(input);
		input.setPosition(5, 2);
		input.setSize(inputBG.getWidth() - 10, inputBG.getHeight());
		input.setColorRef(color1);
		input.setPublisher(str -> enter(str));
		input.setHistorian(() -> new GInputShift.Fast(history, true));
		input.setCompleter(this);
		input.setForce(true);

		dragContainer = new GDragContainer(runtime.getWidth(),
				runtime.getHeight() - bar.getHeight() - inputBG.getHeight());
		scene.addChild(dragContainer);
		dragContainer.setPosition(0, bar.getHeight());

		container = new GEasyLayoutContainer();
		container.setMaxWidth(dragContainer.getWidth());
		container.setMargin(new RenderRect(2, 0, 0, 0));
		dragContainer.addContainer(container);

		addAllCmdRecord();

		LinkedList<CMDRecord> records = appInst.getRecordList();
		for (CMDRecord record : records) addHistroy(record.cmd);
	}

	protected void enter(String cmd) {
		input.clear();
		cmd = cmd.trim();
		if (cmd.startsWith(">")) cmd = cmd.substring(1).trim();
		if (cmd.isEmpty()) return;

		OPSender sender = new OPSender("exec");
		sender.write(sender.args(1), cmd);
		runtime.sendOperation(sender.tag());
		addHistroy(cmd);
	}

	protected void addHistroy(String cmd) {
		int historyIndex = history.indexOf(cmd);
		if (historyIndex == -1) {
			history.add(cmd);
			if (history.size() > 32) history.remove(0);
		} else {
			if (historyIndex < history.size() - 1) {
				history.remove(historyIndex);
				history.add(cmd);
			}
		}
	}

	@Override
	public void onRecvMessage(NBTTagCompound nbt) {
		super.onRecvMessage(nbt);
		int n = nbt.getInteger("update");
		if (n > 0) updateCmdRecord(n);
		else {
			if (n == -1) refreshAllCmdRecord();
			else addAllCmdRecord();
		}
	}

	public void addAllCmdRecord() {
		LinkedList<CMDRecord> records = appInst.getRecordList();
		for (CMDRecord record : records) {
			if (!nodeMap.containsKey(record.getId())) refreshCmdRecord(record);
		}
		Iterator<AppCommandGRecord> iter = nodeMap.values().iterator();
		if (iter.hasNext() && nodeMap.size() > 32) {
			AppCommandGRecord node = iter.next();
			iter.remove();
			node.removeFromParent();
		}
		doLayout();
	}

	public void refreshAllCmdRecord() {
		LinkedList<CMDRecord> records = appInst.getRecordList();

		Set<Integer> idSet = new HashSet<>();
		for (CMDRecord record : records) {
			idSet.add(record.getId());
			if (!nodeMap.containsKey(record.getId())) refreshCmdRecord(record);
		}

		{
			Iterator<Integer> iter = nodeMap.keySet().iterator();
			while (iter.hasNext()) {
				Integer id = iter.next();
				if (!idSet.contains(id)) {
					nodeMap.get(id).removeFromParent();
					iter.remove();
				}
			}
		}

		{
			Iterator<AppCommandGRecord> iter = nodeMap.values().iterator();
			if (iter.hasNext() && nodeMap.size() > 32) {
				AppCommandGRecord node = iter.next();
				iter.remove();
				node.removeFromParent();
			}
		}

		doLayout();
	}

	protected void doLayout() {
		double bottomY = dragContainer.getContainerToBottomY();
		container.layout();
		if (Math.abs(bottomY - container.getPostionY()) < 10)
			container.setPositionY(dragContainer.getContainerToBottomY());
	}

	public void updateCmdRecord(int id) {
		CMDRecord record = appInst.getSustainMap().get(id);
		if (record != null) return;
		refreshCmdRecord(record);
	}

	public void refreshCmdRecord(CMDRecord record) {
		AppCommandGRecord node = nodeMap.get(record.getId());
		if (node == null) {
			nodeMap.put(record.getId(), node = new AppCommandGRecord(this));
			container.addChild(node);
			node.refresh(record);
		}
	}

	@Override
	public GInputShift tryComplete(String str, int cursor) {
		CommandParser parser = new CommandParser(str);
		CommandParser.Element element = parser.findElement(cursor);
		if (element == null) return null;
		if (element.getParent() == null) return null;
		if (!element.isArgs()) return null;

		int wordIndex = element.getArgsIndex();
		boolean isNext = element.isCommand() ? element.getEndIndex() <= cursor - 1 : element.getEndIndex() < cursor - 1;

		GInputShift.Fast shift = new GInputShift.Fast();
		shift.alternative.add("network");
		shift.alternative.add("network-ls");

		if (isNext) {
			if (element.isCommand()) shift.prefix = " ";
			shift.startIndex = shift.endIndex = cursor;
		} else {
			shift.startIndex = element.getStartIndex();
			shift.endIndex = element.getEndIndex() + 1;
		}

//		System.out.println(element.getEndIndex() + ":" + cursor);
//		return shift;
		return null;
	}

}
