package logic.dating.handler.dungeon;

import java.util.Map;

import logic.constant.DungeonConstant;
import logic.dungeon.scene.DatingDungeonScene;

/**
 * 关卡约会结算逻辑对象
 * 
 * @author Alan
 *
 */
public class SettledLogicHandler {
	/**
	 * 是否完成了所有的条件
	 * 
	 * @param condition
	 *            约会结算条目配置的结算
	 * @param scene
	 *            关卡约会对象
	 * @return 是否完成条件
	 */
	@SuppressWarnings("rawtypes")
	public boolean isConditionComplete(Map condition, DatingDungeonScene scene) {
		// 条件对象递归,返回终结点结果
		return getBooleanValueExpression(condition, scene).interpret();
	}

	/** 获取条件判定对象 */
	@SuppressWarnings("rawtypes")
	private Expression<Boolean> getBooleanValueExpression(Map condition,
			DatingDungeonScene scene) {
		// 获取运算符标签
		String tag = (String) condition
				.get(DungeonConstant.DATING_CONDITION_TAG);
		Map subCondition = (Map) condition
				.get(DungeonConstant.DATING_CONDITION_TAG_DATA);
		switch (tag) {
		case DungeonConstant.DATING_CONDITION_LOGIC_TAG:
			return getLogicDualNonterminal(subCondition, scene);
		case DungeonConstant.DATING_CONDITION_RELATION_TAG:
			return getRelationDualNonterminal(subCondition, scene);
		}
		return null;
	}

	/** 获取元素数量 */
	private int getElementCount(String name, DatingDungeonScene scene) {
		switch (name) {
		case DungeonConstant.DATING_ELEMENT_SCORE:
			return scene.getTotalScore();
		}
		return 0;
	}

	/** 根据条件获取逻辑运算器 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Expression<Boolean> getRelationDualNonterminal(Map condition,
			DatingDungeonScene scene) {
		// 获取运算符
		String operator = (String) condition
				.get(DungeonConstant.DATING_CONDITION_OPERATOR);
		Map subCondition = (Map) condition
				.get(DungeonConstant.DATING_CONDITION_OPERATOR_DATA);
		// 条件运算左右值必定为终结点对象,并且值只允许一个元素存在
		// 获取右值
		Map<String, Integer> leftValueMap = (Map<String, Integer>) subCondition
				.get(DungeonConstant.DATING_CONDITION_RIGHT_VALUE);
		int ownCount = 0;
		int conditionCount = 0;
		for (Map.Entry<String, Integer> oneAndOnly : leftValueMap.entrySet()) {
			// 当前的特殊性,左值需要从源对象获取
			ownCount = getElementCount(oneAndOnly.getKey(), scene);
			conditionCount = oneAndOnly.getValue();
			break;
		}
		IntegerTerminal leftBean = new IntegerTerminal(ownCount);
		IntegerTerminal rightBean = new IntegerTerminal(conditionCount);
		switch (operator) {
		case DungeonConstant.DATING_CONDITION_RELATION_MORE_THAN:
			return new RelationLarger(leftBean, rightBean);
		case DungeonConstant.DATING_CONDITION_RELATION_MORE_OR_EQUEL:
			return new RelationLargerOrEqual(leftBean, rightBean);
		case DungeonConstant.DATING_CONDITION_RELATION_LESS_THAN:
			return new RelationLess(leftBean, rightBean);
		case DungeonConstant.DATING_CONDITION_RELATION_LESS_OR_EQUEL:
			return new RelationLessOrEqual(leftBean, rightBean);
		case DungeonConstant.DATING_CONDITION_RELATION_EQUAL:
			return new RelationEqual(leftBean, rightBean);
		}
		return null;
	}

	/** 根据条件获取逻辑运算器 */
	@SuppressWarnings("rawtypes")
	private Expression<Boolean> getLogicDualNonterminal(Map condition,
			DatingDungeonScene scene) {
		// 获取运算符
		String operator = (String) condition
				.get(DungeonConstant.DATING_CONDITION_OPERATOR);
		Map subCondition = (Map) condition
				.get(DungeonConstant.DATING_CONDITION_OPERATOR_DATA);
		// 获取左值
		Expression<Boolean> leftBean = getBooleanValueExpression(
				(Map) subCondition
						.get(DungeonConstant.DATING_CONDITION_LEFT_VALUE),
				scene);
		// 获取右值
		Expression<Boolean> rightBean = getBooleanValueExpression(
				(Map) subCondition
						.get(DungeonConstant.DATING_CONDITION_RIGHT_VALUE),
				scene);
		// 运算符对象
		switch (operator) {
		case DungeonConstant.DATING_CONDITION_LOGIC_OR:
			return new LogicOr(leftBean, rightBean);
		case DungeonConstant.DATING_CONDITION_LOGIC_AND:
			return new LogicAnd(leftBean, rightBean);
		}
		return null;
	}

	// 简单解释器

	/** 表达式对象 */
	interface Expression<T> {
		/** 解释结果 */
		T interpret();
	}

	class IntegerTerminal implements Expression<Integer> {
		private int value;

		public IntegerTerminal(int value) {
			this.value = value;
		}

		public Integer interpret() {
			return value;
		}
	}

	/** 二元非终结点对象 */
	abstract class DualNonterminalBean<T, V> implements Expression<V> {
		T leftBean;
		T rightBean;
	}

	/**
	 * 返回值为布尔类型的运算
	 * <p>
	 * 组合符号"()"包含在左右值的概念中
	 */
	abstract class BooleanValueDualNonterminal<T> extends
			DualNonterminalBean<T, Boolean> {

	}

	/**
	 * 逻辑运算
	 * <p>
	 * 考虑规则复杂度不高,这里不处理短路运算
	 */
	abstract class LogicDualNonterminal extends
			BooleanValueDualNonterminal<Expression<Boolean>> {

	}

	/** 逻辑或运算 */
	class LogicOr extends LogicDualNonterminal {
		public LogicOr(Expression<Boolean> leftBean,
				Expression<Boolean> rightBean) {
			this.leftBean = leftBean;
			this.rightBean = rightBean;
		}

		@Override
		public Boolean interpret() {
			return leftBean.interpret() || rightBean.interpret();
		}

	}

	/** 逻辑与运算 */
	class LogicAnd extends LogicDualNonterminal {
		public LogicAnd(Expression<Boolean> leftBean,
				Expression<Boolean> rightBean) {
			this.leftBean = leftBean;
			this.rightBean = rightBean;
		}

		@Override
		public Boolean interpret() {
			return leftBean.interpret() && rightBean.interpret();
		}

	}

	/** 关系运算 */
	abstract class RelationDualNonterminal extends
			BooleanValueDualNonterminal<Expression<Integer>> {

	}

	/** 等于运算 */
	class RelationEqual extends RelationDualNonterminal {
		public RelationEqual(Expression<Integer> leftBean,
				Expression<Integer> rightBean) {
			this.leftBean = leftBean;
			this.rightBean = rightBean;
		}

		@Override
		public Boolean interpret() {
			return leftBean.interpret() == rightBean.interpret();
		}

	}

	/** 大于运算 */
	class RelationLarger extends RelationDualNonterminal {
		public RelationLarger(Expression<Integer> leftBean,
				Expression<Integer> rightBean) {
			this.leftBean = leftBean;
			this.rightBean = rightBean;
		}

		@Override
		public Boolean interpret() {
			return leftBean.interpret() > rightBean.interpret();
		}

	}

	/** 大于等于运算 */
	class RelationLargerOrEqual extends RelationDualNonterminal {
		public RelationLargerOrEqual(Expression<Integer> leftBean,
				Expression<Integer> rightBean) {
			this.leftBean = leftBean;
			this.rightBean = rightBean;
		}

		@Override
		public Boolean interpret() {
			return leftBean.interpret() >= rightBean.interpret();
		}

	}

	/** 小于运算 */
	class RelationLess extends RelationDualNonterminal {
		public RelationLess(Expression<Integer> leftBean,
				Expression<Integer> rightBean) {
			this.leftBean = leftBean;
			this.rightBean = rightBean;
		}

		@Override
		public Boolean interpret() {
			return leftBean.interpret() < rightBean.interpret();
		}

	}

	/** 小于等于运算 */
	class RelationLessOrEqual extends RelationDualNonterminal {
		public RelationLessOrEqual(Expression<Integer> leftBean,
				Expression<Integer> rightBean) {
			this.leftBean = leftBean;
			this.rightBean = rightBean;
		}

		@Override
		public Boolean interpret() {
			return leftBean.interpret() <= rightBean.interpret();
		}

	}

	public static SettledLogicHandler getInstance() {
		return Singleton.INSTANCE.getInstance();
	}

	private enum Singleton {
		INSTANCE;
		SettledLogicHandler instance;

		Singleton() {
			instance = new SettledLogicHandler();
		}

		public SettledLogicHandler getInstance() {
			return instance;
		}
	}
	//
	// public static void main(String[] args) {
	// Map condition = JSON
	// .parseObject(
	// "{\"tag\":\"logic\",\"tag_data\":{\"operator\":\"or\",\"operator_data\":{\"left\":{\"tag\":\"relation\",\"tag_data\":{\"operator\":\"lt\",\"operator_data\":{\"right\":{\"score\":60}}}},\"right\":{\"tag\":\"relation\",\"tag_data\":{\"operator\":\"mt\",\"operator_data\":{\"right\":{\"score\":60}}}}}}}",
	// Map.class);
	//
	// List conditions = JSON
	// .parseObject(
	// "[{\"tag\":\"relation\",\"tag_data\":{\"operator\":\"mte\",\"operator_data\":{\"right\":{\"score\":60}}}},{\"tag\":\"relation\",\"tag_data\":{\"operator\":\"mte\",\"operator_data\":{\"right\":{\"score\":70}}}},{\"tag\":\"relation\",\"tag_data\":{\"operator\":\"mte\",\"operator_data\":{\"right\":{\"score\":90}}}}]",
	// List.class);
	// SettledLogicHandler s = new SettledLogicHandler();
	// DatingDungeonScene scene = new DatingDungeonScene(0);
	// scene.setCurrentDatingBean(new DungeonDatingBean(0, 0, 60, 0, 0, null,
	// 0, null));
	// scene.setTotalScore(59);
	// System.out.println(s.isConditionComplete((Map)conditions.get(0), scene));
	// scene.setTotalScore(61);
	// System.out.println(s.isConditionComplete((Map)conditions.get(1), scene));
	// scene.setTotalScore(99);
	// System.out.println(s.isConditionComplete((Map)conditions.get(2), scene));
	// }
}
