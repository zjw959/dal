package gm;

import logic.support.LogicScriptsUtils;
import net.http.HttpRequestWrapper;
import script.IHttpScript;

/***
 *** @author: King
 *** @date : 2018年6月25日 下午12:15:48
 ***/
public class GMOperationManager {

	private static final GMOperationManager DEFAULT = new GMOperationManager();

	public static GMOperationManager getInstance() {
		return DEFAULT;
	}

	public String Handler(HttpRequestWrapper httpRequest) {
        IHttpScript script = LogicScriptsUtils.getGMHttpScript();
        String ret = script.execute(httpRequest);
        return ret;
	}
}
