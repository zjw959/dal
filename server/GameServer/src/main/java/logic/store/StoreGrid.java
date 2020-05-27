package logic.store;

import java.util.List;


import data.bean.CommodityCfgBean;

/**
 */
public class StoreGrid {
	private int grid;
	private int totalWeight;
	private List<CommodityCfgBean> cfgs;

	public StoreGrid(int grid, int totalWeight, List<CommodityCfgBean> cfgs) {
		super();
		this.grid = grid;
		this.totalWeight = totalWeight;
		this.cfgs = cfgs;
	}

	public int getGrid() {
		return grid;
	}

	public void setGrid(int grid) {
		this.grid = grid;
	}

	public int getTotalWeight() {
		return totalWeight;
	}

	public void setTotalWeight(int totalWeight) {
		this.totalWeight = totalWeight;
	}

	public List<CommodityCfgBean> getCfgs() {
		return cfgs;
	}

	public void setCfgs(List<CommodityCfgBean> cfgs) {
		this.cfgs = cfgs;
	}
	
}
