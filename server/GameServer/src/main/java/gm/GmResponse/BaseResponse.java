package gm.GmResponse;

public class BaseResponse {
	public int result; //错误码
	public String desc; //错误码，
	
	public BaseResponse(int result, String desc) {
		super();
		this.result = result;
		this.desc = desc;
	}
	
	
}
