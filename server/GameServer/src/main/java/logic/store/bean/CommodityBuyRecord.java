package logic.store.bean;


/**
 * 商城购买记录
 */
public class CommodityBuyRecord  {
	
	
	/**
	 * 索引值
	 */
	private String[] indexValues = new String[2];
	
	/**
	* id
	*/
	private long id;
	
	/**
	* 商品Id
	*/
	private int commodityId;
	
	/**
	* 当前购买数
	*/
	private int nowBuyCount;
	
	/**
	* 总购买数
	*/
	private int totalBuyCount;
	
	/**
	* create_date
	*/
	private java.util.Date createDate;
	
	/**
	* modified_date
	*/
	private java.util.Date modifiedDate;

	public String[] getIndexValues() {
		return indexValues;
	}

	public void setIndexValues(String[] indexValues) {
		this.indexValues = indexValues;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getCommodityId() {
		return commodityId;
	}

	public void setCommodityId(int commodityId) {
		this.commodityId = commodityId;
	}

	public int getNowBuyCount() {
		return nowBuyCount;
	}

	public void setNowBuyCount(int nowBuyCount) {
		this.nowBuyCount = nowBuyCount;
	}

	public int getTotalBuyCount() {
		return totalBuyCount;
	}

	public void setTotalBuyCount(int totalBuyCount) {
		this.totalBuyCount = totalBuyCount;
	}

	public java.util.Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(java.util.Date createDate) {
		this.createDate = createDate;
	}

	public java.util.Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(java.util.Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	
	public CommodityBuyRecord(long id, int commodityId, int nowBuyCount, int totalBuyCount) {
		super();
		this.id = id;
		this.commodityId = commodityId;
		this.nowBuyCount = nowBuyCount;
		this.totalBuyCount = totalBuyCount;
		this.createDate = new java.util.Date();
		this.modifiedDate = new java.util.Date();
	}
	
}