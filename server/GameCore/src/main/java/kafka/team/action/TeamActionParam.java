package kafka.team.action;

import java.io.Serializable;

public class TeamActionParam implements Serializable {

    private static final long serialVersionUID = 2558008534016971412L;

    public int actionType;

    public String paramName;

    public String param;

    public int getActionType() {
        return actionType;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

}
