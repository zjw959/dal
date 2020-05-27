package logic.constant;
/**
 * @author : DengYing
 * @CreateDate : 2018年2月6日 下午6:21:49
 * @Description ：Please describe this document
 */
public enum ActionEnum {
	DEFAULT(0),
	ADD(1), 
	REMOVE(2), 
	UPDATE(3),
	RESET(4),
	OPEN(5),
	CLOSE(6);

	int code;

	private ActionEnum(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}
	
	
}
