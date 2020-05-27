package com.constant;

import com.enity.InvitationCode;
import com.enity.chinese.ChineseCode;
import com.enity.chinese.ChineseUse;


public enum QueryTypeConstant {
    /** 中文礼包码 */
    CHINESE_CODE("queryCCode", ChineseCode.class),
    /** 中文礼包使用记录 */
    CHINESE_USE("queryUseCCode", ChineseUse.class),
    /** 随机礼包码 */
    INVITATION_CODE("queryInvitCodeSQL", InvitationCode.class),
    /** 礼包 */
    PACKAGE("queryPackage", Package.class);

    private final String value;

    private Class<?> clazz;

    QueryTypeConstant(String value) {
        this.value = value;
    }

    QueryTypeConstant(String value, Class<?> clazz) {
        this.value = value;
        this.clazz = clazz;
    }

    public String value() {
        return value;
    }

    public Class<?> getListener() {
        return clazz;
    }

    public static QueryTypeConstant gerQueryTypeType(String type) {
        for (QueryTypeConstant eeven : QueryTypeConstant.values()) {
            if (eeven.value.equals(type)) {
                return eeven;
            }
        }
        return null;
    }
}
