package kafka.team.param.g2f;

public class ResChasmStartFightParam {

    private long teamId;
    private int resultCode;
    private int netType;
    
    public long getTeamId() {
        return teamId;
    }
    
    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }
    
    public int getResultCode() {
        return resultCode;
    }
    
    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }
    
    public int getNetType() {
        return netType;
    }
    
    public void setNetType(int netType) {
        this.netType = netType;
    }
    
    public enum ResultCode {
        SUCESS(0),
        PERMISSION_DENIED(1),
        REPEAT_HERO(2),
        NOT_READY(3),
        ROOM_LACKING(4);
       
        private int code;
        
        private ResultCode(int code) {
            this.code = code;
        }
        
        public int getCode() {
            return code;
        }
        
        public static ResultCode getResultCode(int code) {
            for (ResultCode c : ResultCode.values()) {
                if (c.compare(code)) {
                    return c;
                }
            }
            return ResultCode.SUCESS;
        }

        public boolean compare(int code) {
            return this.code == code;
        }
    }
}
