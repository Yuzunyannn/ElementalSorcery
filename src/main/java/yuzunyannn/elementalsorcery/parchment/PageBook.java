package yuzunyannn.elementalsorcery.parchment;

import java.util.Arrays;

public class PageBook extends Page {

	int[] pageIds;
	// 当前页面位置
	int at = 0;
	// 目录当前页数
	int catAt = 0;
	// 每页个数
	final int catPrePage = 5;

	public PageBook() {
		this.pageIds = new int[0];
	}

	public void setPageIds(int[] pageIds) {
		this.pageIds = pageIds;
	}

	@Override
	public void open() {
		at = 0;
		catAt = 0;
	}

	@Override
	public int[] getCatalog() {
		if (catAt * catPrePage + catPrePage >= pageIds.length) {
			return Arrays.copyOfRange(pageIds, catAt * catPrePage, pageIds.length);
		} else {
			return Arrays.copyOfRange(pageIds, catAt * catPrePage, catAt * catPrePage + catPrePage);
		}
	}

	@Override
	public PageSate getState() {
		return Page.PageSate.EXCLUSIVE;
	}

	@Override
	public String getTitle() {
		return "page.catalog";
	}

	@Override
	public int prePage() {
		if (catAt > 0) {
			return this.getId();
		}
		return -1;
	}

	@Override
	public int nextPage() {
		if (pageIds.length > catAt * catPrePage + catPrePage) {
			return this.getId();
		}
		return -1;
	}

	@Override
	public void prePageUpdate() {
		catAt--;
	}

	@Override
	public void nextPageUpdate() {
		catAt++;
	}

	@Override
	public int getCraftingTo(int index) {
		return pageIds[index + catAt * catPrePage];
	}
}
