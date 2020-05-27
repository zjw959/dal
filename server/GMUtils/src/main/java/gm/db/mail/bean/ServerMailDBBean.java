package gm.db.mail.bean;

/**
 * 
 * @Description 全服邮件
 * @author LiuJiang
 * @date 2018年7月2日 上午11:38:45
 *
 */
public class ServerMailDBBean extends BaseMailDBBean {

    public ServerMailDBBean copy() {
        ServerMailDBBean bean = new ServerMailDBBean();
        super.copy(bean);
        return bean;
    }
}
