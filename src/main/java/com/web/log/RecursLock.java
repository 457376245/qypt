package com.web.log;

/**
 * 递归调用控制锁
 * 
 * @author Alai
 *
 *         2018年7月15日
 */
public class RecursLock<T> {

	/**
	 * 递归控制单元，当递归发生时，不允许二次进入，以免造成无限递归
	 */
	public static interface Unit<T> {
		/**
		 * 在递归控制锁控制之下执行
		 * 
		 * @return
		 */
		T onRecursLocked();
	}

	private final ThreadLocal<Boolean> withLock = new ThreadLocal<Boolean>();
	private final T valueOfRecursLocked;

	public RecursLock(T o) {
		this.valueOfRecursLocked = o;
	}

	/**
	 * 在递归控制锁控制下执行控制单元
	 * 
	 * @param unit
	 * @return
	 */
	public final T with(Unit<T> unit) {
		if (Boolean.TRUE.equals(withLock.get())) {
			// 当被锁定时，不允许二次进入，以免造成无限递归
			return valueOfRecursLocked;
		}
		try {
			withLock.set(true); // 锁定
			return unit.onRecursLocked();
		} finally {
			withLock.set(false); // 解锁
		}
	}

}
