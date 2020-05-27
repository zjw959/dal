package logic.constant;

/**
 * 副本常量 Created by fxf on 2017/8/24.
 */
public interface DungeonConstant {

	/**
	 * 最高星级：三星
	 */
	int CHAPTER_MAX_STAR = 3;

	/**
	 * 主线类型:关卡
	 */
	int MAIN_LINE_TYPE_CHAPTER = 1;

	// int MAIN_LINE_TYPE_CARTOON =2;
	// int MAIN_LINE_TYPE_DIALOGUE =3;
	// int MAIN_LINE_TYPE_CG =4;

	/**
	 * 剧情未通关
	 */
	int STORY_NOT_PASS = 0;
	/**
	 * 剧情通关
	 */
	int STORY_IS_PASS = 1;

	/**
	 * 副本战斗次数刷新类型：不刷新
	 */
	int FIGHT_COUNT_REFRESH_TYPE_NO = 0;
	/**
	 * 副本战斗次数刷新类型：每日刷新
	 */
	int FIGHT_COUNT_REFRESH_TYPE_DAILY = 1;

	/**
	 * 约会副本结算：得分
	 */
	int DUNGEON_DATING_SETTLE_SCORE = 21;
	/**
	 * 约会副本结算：特殊条件
	 */
	int DUNGEON_DATING_SETTLE_SPECIAL = 22;

	// 关卡英雄限定条件
	/**
	 * (0-普通不限定类型)
	 */
	int LIMITED_HEROS_BANNED = 0;
	/**
	 * (1-不能编入自己的精灵)
	 */
	int LIMITED_HEROS_OWNED_FORBIDDEN = 1;
	/**
	 * (2-可以编入自己的精灵)
	 */
	int LIMITED_HEROS_OWNED_PERMITED = 2;
	/**
	 * (3-禁用精灵)
	 */
	int LIMITED_HEROS_DESIGNATED_FORBIDDEN = 3;

	// 关卡英雄列表类型
	/**
	 * 玩家英雄类型
	 */
	int DUNGEON_HERO_LIST_NORMAL = 1;
	/**
	 * 限定英雄类型
	 */
	int DUNGEON_HERO_LIST_LIMITED = 2;

	//<!-- 约会关卡结算条件 --> 
	// 通用标志
	/** 二元运算左值 */
	String DATING_CONDITION_LEFT_VALUE = "left";
	/** 二元运算右值 */
	String DATING_CONDITION_RIGHT_VALUE = "right";
	/** 运算符 */
	String DATING_CONDITION_OPERATOR = "operator";
	/** 运算符条件数据 */
	String DATING_CONDITION_OPERATOR_DATA = "operator_data";
	/** 运算符标签 */
	String DATING_CONDITION_TAG = "tag";
	/** 标签条件数据 */
	String DATING_CONDITION_TAG_DATA = "tag_data";
	
	// 逻辑运算
	/** 逻辑运算符标签 */
	String DATING_CONDITION_LOGIC_TAG = "logic";
	/**
	 * 逻辑或
	 */
	String DATING_CONDITION_LOGIC_OR = "or";
	/**
	 * 逻辑与
	 */
	String DATING_CONDITION_LOGIC_AND = "and";

	// 关系运算
	// 逻辑运算符标签
	String DATING_CONDITION_RELATION_TAG = "relation";
	/**
	 * 等于
	 */
	String DATING_CONDITION_RELATION_EQUAL="eq";
	/**
	 * 大于
	 */
	String DATING_CONDITION_RELATION_MORE_THAN = "mt";
	/**
	 * 大于等于
	 */
	String DATING_CONDITION_RELATION_MORE_OR_EQUEL = "mte";
	/**
	 * 小于
	 */
	String DATING_CONDITION_RELATION_LESS_THAN = "lt";
	/**
	 * 小于等于
	 */
	String DATING_CONDITION_RELATION_LESS_OR_EQUEL = "lte";
	// 约会关卡结算元素
	/**
	 * 得分
	 */
	String DATING_ELEMENT_SCORE = "score";
}
