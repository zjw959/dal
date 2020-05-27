package logic.dating.bean;

import java.util.List;
import java.util.Map;

/***
 * 
 * 在序列化或者反序列化的時候需要不可实例化对象
 * 所以 把CurrentDatingBean 当成一个不可实例化 对象 让 DatingBaseBean来实例化CurrentDatingBean
 * 相当于实例化CurrentDatingBean 需要new DatingBaseBean
 * 
 * @author lihongji
 * 
 *
 */
public class DatingBaseBean extends CurrentDatingBean {

    public DatingBaseBean(long id, int datingType, int score, int favorReward, int currentCid,
            Map<Integer, List<Integer>> currentScript, int scriptId, List<Integer> roleIds) {
        super(id, datingType, score, favorReward, currentCid, currentScript, scriptId, roleIds);
    }

}
