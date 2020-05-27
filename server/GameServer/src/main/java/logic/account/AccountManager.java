package logic.account;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import logic.account.bean.Account;

/**
 * 
 * @Description 账号管理器(临时用，结构调整后删除)
 * @author LiuJiang
 * @date 2018年6月3日 下午3:05:50
 *
 */
public class AccountManager {
    
    Map<String,Account> accounts = new ConcurrentHashMap<String,Account>();
	
	public static AccountManager getInstance() {
        return Singleton.INSTANCE.getInstance();
    }

    private enum Singleton {
        INSTANCE;

        AccountManager instance;

        private Singleton() {
        	instance = new AccountManager();
        }

        AccountManager getInstance() {
            return instance;
        }
    }
    
    /**
     *  获取账号
     */
    public Account getAccount(String accountId){
    	return accounts.get(accountId);
    }
    
    /**
     *  放入账号
     */
    public void putAccount(String accountId,Account account){
        accounts.put(accountId, account);
    }
    
    public static void main(String[] args) throws Exception {
	}
}
