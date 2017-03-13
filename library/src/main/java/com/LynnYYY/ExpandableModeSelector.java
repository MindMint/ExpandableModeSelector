package com.LynnYYY;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import static com.LynnYYY.ExpandableModeSelector.State.CLOSED;
import static com.LynnYYY.ExpandableModeSelector.State.CLOSING;
import static com.LynnYYY.ExpandableModeSelector.State.OPENED;
import static com.LynnYYY.ExpandableModeSelector.State.OPENING;

/**
 * Created by zhangmint on 2017/3/9.
 */
public class ExpandableModeSelector extends FrameLayout {
    /**
     * 可选择项
     */
    private final List<ImageView> imageViews = new ArrayList<>();
    /**
     * 选择项空间的顶部和底部margin
     */
    private int topMargin,bottomMargin;
    /**
     * 每一个延时单元
     */
    private final int DURATION = 50;
    /**
     * close the menu only if the state is OPENED;
     * open the menu only if the menu is CLOSED;
     */
    private State state = CLOSED;

    enum State {
        CLOSED, OPENED, CLOSING, OPENING
    }

    public ExpandableModeSelector(Context context) {
        this(context, null);
    }

    public ExpandableModeSelector(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandableModeSelector(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean isMenuOpened() {
        return OPENED.equals(state);
    }

    public void openMenu() {
        if (!CLOSED.equals(state)) {
            return;
        }
        state = OPENING;
        AnimatorSet openAnimatorSet = getOpenAnimatorSet();
        openAnimatorSet.start();
    }

    public void closeMenu() {
        if (!OPENED.equals(state)) {
            return;
        }
        state = CLOSING;
        AnimatorSet closeAnimatorSet = getCloseAnimatorSet();
        closeAnimatorSet.start();
    }

    public interface OnItemClickListener {
        /**
         * @param tag addItem时传入的标签{{@link #addItem(int, String)}{@link #addMainItem(int, String)}}
         */
        void onClick(String tag);
    }

    private OnItemClickListener onItemClickListener;

    /**
     * 监听选择项点击事件
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 添加主按钮（初始化时被选中项）
     * @param drawId
     * @param tag
     */
    public void addMainItem(int drawId,String tag){
        addItem(drawId,tag,true);
    }

    /**
     * 添加子选择项（初始化时未被选中）
     * @param drawId
     * @param tag
     */
    public void addItem(int drawId, String tag) {
        addItem(drawId,tag,false);
    }

    private void addItem(int drawId, String tag,boolean isMainItem) {
        final ImageView imageView = (ImageView) LayoutInflater.from(getContext()).inflate(R.layout.mode_item,this,false);
        imageView.setImageResource(drawId);
        imageView.setTag(tag);
        if(isMainItem){
            imageView.setSelected(true);
        }else{
            imageView.setVisibility(INVISIBLE);
        }
        addView(imageView);
        imageViews.add(imageView);

        FrameLayout.LayoutParams layoutParams = (LayoutParams) imageView.getLayoutParams();
        topMargin = layoutParams.topMargin;
        bottomMargin = layoutParams.bottomMargin;

        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (state) {
                    case OPENED:
                        if(!v.isSelected()){
                            List<ImageView> tempList = new ArrayList<>();

                            for(ImageView view:imageViews){
                                if(!view.equals(v)){
                                    tempList.add(view);
                                    view.setSelected(false);
                                }
                            }
                            tempList.add((ImageView) v);
                            imageViews.clear();
                            imageViews.addAll(tempList);
                            v.setSelected(true);
                            if(onItemClickListener != null){
                                onItemClickListener.onClick((String) v.getTag());
                            }
                        }
                        closeMenu();
                        break;
                    case CLOSED:
                        openMenu();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private AnimatorSet getOpenAnimatorSet() {
        Animator[] animators = getOpenAnimators();
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animators);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                state = OPENED;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        return animatorSet;
    }

    private AnimatorSet getCloseAnimatorSet() {
        Animator[] animators = getCloseAnimators();
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animators);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                state = CLOSED;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        return animatorSet;
    }

    /**
     * 生成每一个选择项视图的打开动画
     * @return
     */
    private Animator[] getOpenAnimators() {
        int itemCount = imageViews.size();
        Animator[] animators = new Animator[itemCount];
        for (int i = 0; i < itemCount; i++) {
            final ImageView imageView = imageViews.get(i);
            final FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) imageView.getLayoutParams();

            int offset = (itemCount - i - 1) * (imageView.getHeight() + topMargin) + (itemCount - i) * bottomMargin;
            ValueAnimator valueAnimator = ValueAnimator.ofInt(layoutParams.bottomMargin, offset);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = (Integer) animation.getAnimatedValue();
                    layoutParams.bottomMargin = value;
                    imageView.setLayoutParams(layoutParams);
                }
            });
            valueAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    imageView.setVisibility(VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {

                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            if (offset > 0) {
                valueAnimator.setDuration((itemCount - i) * DURATION * (offset - layoutParams.bottomMargin) / offset);
            }
            valueAnimator.setStartDelay(i * DURATION);
            valueAnimator.setInterpolator(new LinearInterpolator());
            animators[i] = valueAnimator;
        }
        return animators;
    }

    /**
     * 生成每一个选择项视图的关闭动画
     * @return
     */
    private Animator[] getCloseAnimators() {
        int itemCount = imageViews.size();
        Animator[] animators = new Animator[itemCount];
        for (int i = 0; i < itemCount; i++) {
            final ImageView imageView = imageViews.get(i);
            final FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) imageView.getLayoutParams();

            int offset = (itemCount - i) * (imageView.getHeight() + topMargin + bottomMargin);
            ValueAnimator valueAnimator = ValueAnimator.ofInt(layoutParams.bottomMargin, bottomMargin);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = (Integer) animation.getAnimatedValue();
                    layoutParams.bottomMargin = value;
                    imageView.setLayoutParams(layoutParams);
                }
            });
            valueAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if(!imageView.isSelected()){
                        imageView.setVisibility(INVISIBLE);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            if (offset > 0) {
                valueAnimator.setDuration((itemCount - i) * DURATION * layoutParams.bottomMargin / offset);
            }
            valueAnimator.setInterpolator(new LinearInterpolator());
            animators[i] = valueAnimator;
        }
        return animators;
    }
}
