package gm.bean;

/***
 *** @author: King
 *** @date : 2018年7月2日 下午12:17:06
 ***/

public class onlinePlayersNum {
    public String channel_id;
    public String channel_app_id;
	public int num;
	
    public onlinePlayersNum(String channel_id, String channel_app_id, int num) {
		super();
		this.channel_id = channel_id;
		this.channel_app_id = channel_app_id;
		this.num = num;
	}
	
    public String getChannel_id() {
		return channel_id;
	}

    public void setChannel_id(String channel_id) {
		this.channel_id = channel_id;
	}

    public String getChannel_app_id() {
		return channel_app_id;
	}

    public void setChannel_app_id(String channel_app_id) {
		this.channel_app_id = channel_app_id;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
}
