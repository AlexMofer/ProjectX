package am.project.support.animator;

/**
 * 使用 ValueAnimator.AnimatorUpdateListener 配合ValueAnimator来实现
 * 将在下一个版本去除
 */
@Deprecated
public interface AnimatorCallback {
	void isStart();

	void isStop();

	void isEnd();
}
