package gm.bean;

/***
 *** @author: King
 *** @date : 2018年7月2日 下午12:16:57
 ***/
public class GameServerSwitch {
	public int swithId;
	public int swithStatus;
	public int swithName;
	
	
	public GameServerSwitch(int swithId, int swithStatus) {
		super();
		this.swithId = swithId;
		this.swithStatus = swithStatus;
	}
	
	public int getSwithId() {
		return swithId;
	}
	public void setSwithId(int swithId) {
		this.swithId = swithId;
	}
	public int getSwithStatus() {
		return swithStatus;
	}
	public void setSwithStatus(int swithStatus) {
		this.swithStatus = swithStatus;
	}
	public int getSwithName() {
		return swithName;
	}
	public void setSwithName(int swithName) {
		this.swithName = swithName;
	}
}
