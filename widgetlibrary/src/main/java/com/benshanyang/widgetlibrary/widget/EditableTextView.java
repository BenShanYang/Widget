package com.benshanyang.widgetlibrary.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.CharacterStyle;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.benshanyang.widgetlibrary.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 类描述: 可以编辑的带标题的输入框 </br>
 * 时间: 2020/12/29 9:14
 *
 * @author YangKuan
 * @version 1.0.0
 * @since
 */
public class EditableTextView extends FrameLayout {

    private RelativeLayout titleGroup;//标题布局
    private EditText etInputBar;//输入框
    private ImageView ivInputIcon;//输入框的图标
    private TextView tvInputTitle;//标题
    private TextView tvRequired;//必填项
    private View topBorder;//上边框
    private View bottomBorder;//下边框

    private TextWatcher watcher;//输入框文字改变监听
    private OnFocusChangeListener focusChangeListener;//输入框焦点改变监听
    private InputFilter inputFilter;//过滤器
    private Drawable inputIcon;//输入框图标
    private Drawable requiredIcon;//必填的红星号图标
    private CharSequence title;//输入框标题
    private CharSequence requiredText;//必填的文字
    private CharSequence content;//输入框文字
    private CharSequence hint;//输入框占位文字
    private String digits = "";//过滤条件
    @ColorInt
    private int titleTextColor = 0xFF333333;//标题文字颜色
    @ColorInt
    private int requiredTextColor = 0xFF999999;//必填文字颜色
    @ColorInt
    private int inputIconColor = -1;//输入框图标颜色
    @ColorInt
    private int contentTextColor = 0xFF333333;//输入框内容文字颜色
    @ColorInt
    private int textColorHint = -1;//输入框占位文字的色值
    @ColorInt
    private int topBorderColor = -1;//上边框颜色
    @ColorInt
    private int bottomBorderColor = -1;//下边框颜色
    private float topBorderWidth = 0f;//上边框宽度
    private float bottomBorderWidth = 0f;//下边框宽度
    private float topBorderLeftMargin = 0f;//上边框距离左侧的距离
    private float topBorderRightMargin = 0f;//上边框距离右侧的距离
    private float bottomBorderLeftMargin = 0f;//下边框距离左侧的距离
    private float bottomBorderRightMargin = 0f;//下边框距离右侧的距离

    private float requiredPadding = 0f;//标题和必填文字或星号的距离
    private float inputIconPadding = 0f;//标题和输入框图标之间的间距
    private float titleTextBold = 0f;//标题文字的粗细值 一般设置为0.8，取值范围0~1.0
    private int titleTextSize = -1;//标题文字字号
    private int requiredTextSize;//必填文字的字号
    private int contentTextSize = -1;//输入框内容文字字号
    private int requiredTextGravity = 1;//和标题文字的对齐方式 默认底部对齐
    private int inputType = -1;//输入类型
    private int maxLength = Integer.MAX_VALUE;//最大输入字数
    private boolean editable = true;//输入框是否可以编辑
    private boolean singleLine = false;//设置输入框为单行
    private int gravity = 2;//设置输入框文字的居中方式 默认居右
    private int titleGravity = 0;//标题和输入内容的对齐方式
    private float titleContentSpace = 0f;//标题和内容之间的间隙

    public static final int TOP = 0;
    public static final int LEFT = 0;
    public static final int BOTTOM = 1;
    public static final int RIGHT = 1;
    public static final int CENTER = 2;

    @IntDef({TOP, BOTTOM, CENTER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TitleGravity {
    }

    @IntDef({TOP, BOTTOM, CENTER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RequiredGravity {
    }

    @IntDef({LEFT, RIGHT, CENTER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ContentGravity {
    }

    public EditableTextView(@NonNull Context context) {
        super(context);
        initLayout(context);
        initSetting(context);
    }

    public EditableTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
        initAttrs(context, attrs);
        initSetting(context);
    }

    public EditableTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context);
        initAttrs(context, attrs);
        initSetting(context);
    }

    private void initLayout(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_editable_input, this, false);
        titleGroup = view.findViewById(R.id.title_parent_layout);//标题的布局
        ivInputIcon = view.findViewById(R.id.iv_input_icon);//输入框图标
        tvInputTitle = view.findViewById(R.id.tv_input_title);//标题
        tvRequired = view.findViewById(R.id.tv_required);//必填项
        etInputBar = view.findViewById(R.id.et_inputbar);//输入框
        topBorder = view.findViewById(R.id.top_border);//上边框
        bottomBorder = view.findViewById(R.id.bottom_border);//下边框

        addView(view, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        requiredTextSize = sp2px(12f);//必填的文字字号初始值
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.EditableTextView, 0, 0);
        if (typedArray != null) {
            for (int i = 0; i < typedArray.getIndexCount(); i++) {
                int attr = typedArray.getIndex(i);
                if (attr == R.styleable.EditableTextView_inputIcon) {
                    //输入框图标
                    inputIcon = typedArray.getDrawable(attr);
                } else if (attr == R.styleable.EditableTextView_inputIconPadding) {
                    //标题和输入框图标之间的间距
                    inputIconPadding = typedArray.getDimension(attr, 0f);
                } else if (attr == R.styleable.EditableTextView_inputIconColor) {
                    //输入框图标颜色
                    inputIconColor = typedArray.getColor(attr, -1);
                } else if (attr == R.styleable.EditableTextView_title) {
                    //输入框标题
                    title = typedArray.getText(attr);
                } else if (attr == R.styleable.EditableTextView_titleTextColor) {
                    //标题文字颜色
                    titleTextColor = typedArray.getColor(attr, 0xFF333333);
                } else if (attr == R.styleable.EditableTextView_titleTextSize) {
                    //标题文字字号
                    titleTextSize = typedArray.getDimensionPixelSize(attr, -1);
                } else if (attr == R.styleable.EditableTextView_titleTextBold) {
                    //标题文字的粗细值 一般设置为0.8，取值范围0~1.0
                    titleTextBold = typedArray.getFloat(attr, 0f);
                } else if (attr == R.styleable.EditableTextView_requiredIcon) {
                    //必填的星号
                    requiredIcon = typedArray.getDrawable(attr);
                } else if (attr == R.styleable.EditableTextView_requiredPadding) {
                    //标题和必填文字或星号的距离
                    requiredPadding = typedArray.getDimension(attr, 0f);
                } else if (attr == R.styleable.EditableTextView_requiredText) {
                    //设置必填的文字
                    requiredText = typedArray.getText(attr);
                } else if (attr == R.styleable.EditableTextView_requiredTextColor) {
                    //必填文字颜色
                    requiredTextColor = typedArray.getColor(attr, 0xFF999999);
                } else if (attr == R.styleable.EditableTextView_requiredTextSize) {
                    //必填的文字字号
                    requiredTextSize = typedArray.getDimensionPixelSize(attr, sp2px(12f));
                } else if (attr == R.styleable.EditableTextView_requiredTextGravity) {
                    //和标题文字的对齐方式
                    requiredTextGravity = typedArray.getInt(attr, 1);
                } else if (attr == R.styleable.EditableTextView_contentText) {
                    //输入框文字
                    content = typedArray.getText(attr);
                } else if (attr == R.styleable.EditableTextView_android_hint) {
                    //输入框占位文字
                    hint = typedArray.getText(attr);
                } else if (attr == R.styleable.EditableTextView_contentTextColor) {
                    //输入框内容文字颜色
                    contentTextColor = typedArray.getColor(attr, 0xFF333333);
                } else if (attr == R.styleable.EditableTextView_contentTextSize) {
                    //输入框内容文字字号
                    contentTextSize = typedArray.getDimensionPixelSize(attr, -1);
                } else if (attr == R.styleable.EditableTextView_android_textColorHint) {
                    //输入框占位文字的色值
                    textColorHint = typedArray.getColor(attr, -1);
                } else if (attr == R.styleable.EditableTextView_android_inputType) {
                    //输入框输入类型
                    inputType = typedArray.getInt(attr, -1);
                } else if (attr == R.styleable.EditableTextView_android_maxLength) {
                    //输入框的最多输入字数
                    maxLength = typedArray.getInt(attr, Integer.MAX_VALUE);
                } else if (attr == R.styleable.EditableTextView_android_digits) {
                    //输入框的过滤条件
                    digits = typedArray.getString(attr);
                } else if (attr == R.styleable.EditableTextView_editable) {
                    //输入框是否可以编辑
                    editable = typedArray.getBoolean(attr, true);
                } else if (attr == R.styleable.EditableTextView_gravity) {
                    //设置输入文字的居中位置
                    gravity = typedArray.getInt(attr, 2);
                } else if (attr == R.styleable.EditableTextView_titleContentSpace) {
                    //标题和内容之间的间隙
                    titleContentSpace = typedArray.getDimension(attr, 0f);
                } else if (attr == R.styleable.EditableTextView_titleGravity) {
                    //标题和输入内容的对齐方式
                    titleGravity = typedArray.getInt(attr, 0);
                } else if (attr == R.styleable.EditableTextView_android_singleLine) {
                    //设置输入框是否单行
                    singleLine = typedArray.getBoolean(attr, false);
                } else if (attr == R.styleable.EditableTextView_topBorderColor) {
                    //上边框颜色
                    topBorderColor = typedArray.getColor(attr, -1);
                } else if (attr == R.styleable.EditableTextView_bottomBorderColor) {
                    //下边框颜色
                    bottomBorderColor = typedArray.getColor(attr, -1);
                } else if (attr == R.styleable.EditableTextView_topBorderWidth) {
                    //上边框宽度
                    topBorderWidth = typedArray.getDimension(attr, 0);
                } else if (attr == R.styleable.EditableTextView_bottomBorderWidth) {
                    //下边框宽度
                    bottomBorderWidth = typedArray.getDimension(attr, 0f);
                } else if (attr == R.styleable.EditableTextView_topBorderLeftMargin) {
                    //上边框距离左侧的距离
                    topBorderLeftMargin = typedArray.getDimension(attr, 0f);
                } else if (attr == R.styleable.EditableTextView_topBorderRightMargin) {
                    //上边框距离右侧的距离
                    topBorderRightMargin = typedArray.getDimension(attr, 0f);
                } else if (attr == R.styleable.EditableTextView_bottomBorderLeftMargin) {
                    //下边框距离左侧的距离
                    bottomBorderLeftMargin = typedArray.getDimension(attr, 0f);
                } else if (attr == R.styleable.EditableTextView_bottomBorderRightMargin) {
                    //下边框距离右侧的距离
                    bottomBorderRightMargin = typedArray.getDimension(attr, 0f);
                }
            }
            typedArray.recycle();
        }
    }

    private void initSetting(Context context) {
        if (ivInputIcon != null) {
            ivInputIcon.setImageDrawable(inputIcon);//输入框图标
            //设置输入框图标颜色
            if (inputIconColor != -1) {
                ivInputIcon.setColorFilter(inputIconColor);
            }
            //标题和输入框图标之间的间距
            if (inputIcon != null) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivInputIcon.getLayoutParams();
                params.rightMargin = (int) inputIconPadding;
                ivInputIcon.setLayoutParams(params);
            }
        }
        if (tvInputTitle != null) {
            if (titleTextSize != -1) {
                tvInputTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize);//标题字号
            }
            tvInputTitle.setTextColor(titleTextColor);//标题文字颜色
            setText(tvInputTitle, title, titleTextBold);//设置标题文字、文字粗细
            setDrawable(tvInputTitle, requiredPadding, null, null, requiredIcon, null);//设置标题右侧必填的星号
        }

        //设置标题居中方式
        if (titleGroup != null) {
            LinearLayout.LayoutParams titleGroupParams = (LinearLayout.LayoutParams) titleGroup.getLayoutParams();
            if (titleGravity == 0) {
                //标题居上
                titleGroupParams.gravity = Gravity.TOP;
            } else if (titleGravity == 1) {
                //标题居底部
                titleGroupParams.gravity = Gravity.BOTTOM;
            } else if (titleGravity == 2) {
                //标题居中
                titleGroupParams.gravity = Gravity.CENTER_VERTICAL;
            }
            titleGroup.setLayoutParams(titleGroupParams);
        }

        //必填
        if (tvRequired != null && !TextUtils.isEmpty(requiredText)) {
            //必填和标题文字的间距
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tvRequired.getLayoutParams();
            params.leftMargin = (int) requiredPadding;
            tvRequired.setLayoutParams(params);
            //必填文字
            tvRequired.setText(requiredText);
            //必填文字颜色
            tvRequired.setTextColor(requiredTextColor);
            //必填文字字号
            tvRequired.setTextSize(TypedValue.COMPLEX_UNIT_PX, requiredTextSize);

            if (requiredTextGravity == 0) {
                //上对齐
                RelativeLayout.LayoutParams requiredParams = (RelativeLayout.LayoutParams) tvRequired.getLayoutParams();
                requiredParams.addRule(RelativeLayout.ALIGN_TOP, tvInputTitle.getId());
                tvRequired.setLayoutParams(requiredParams);
            } else if (requiredTextGravity == 1) {
                //下对齐
                RelativeLayout.LayoutParams requiredParams = (RelativeLayout.LayoutParams) tvRequired.getLayoutParams();
                requiredParams.addRule(RelativeLayout.ALIGN_BASELINE, tvInputTitle.getId());
                tvRequired.setLayoutParams(requiredParams);
            } else if (requiredTextGravity == 2) {
                //居中对齐
                RelativeLayout.LayoutParams requiredParams = (RelativeLayout.LayoutParams) tvRequired.getLayoutParams();
                requiredParams.addRule(RelativeLayout.ALIGN_BOTTOM, tvInputTitle.getId());
                requiredParams.addRule(RelativeLayout.ALIGN_TOP, tvInputTitle.getId());
                tvRequired.setLayoutParams(requiredParams);
            }
        }

        //过滤条件
        inputFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
                if (!TextUtils.isEmpty(charSequence)) {
                    String inputStr = charSequence.toString();
                    if (TextUtils.isEmpty(digits)) {
                        //如果没有匹配条件就不去匹配
                        return null;
                    } else if (inputStr.matches(digits)) {
                        //设置了匹配条件 且符合匹配条件
                        return null;
                    } else {
                        //设置了匹配条件但不符合匹配条件
                        return "";
                    }
                } else {
                    //输入的为空则过滤掉
                    return "";
                }
            }
        };
        if (etInputBar != null) {
            //设置输入框是否单行
            etInputBar.setSingleLine(singleLine);
            //输入框占位文字
            etInputBar.setHint(hint);
            //输入框内容
            etInputBar.setText(content);
            if (!TextUtils.isEmpty(content)) {
                etInputBar.setSelection(content.length());
            }
            //输入框文字颜色
            etInputBar.setTextColor(contentTextColor);
            //设置输入框字号
            etInputBar.setTextSize(TypedValue.COMPLEX_UNIT_PX, contentTextSize);
            //输入框输入类型
            if (inputType != -1) {
                etInputBar.setInputType(inputType);
            }
            //设置占位文字的颜色
            if (textColorHint != -1) {
                etInputBar.setHintTextColor(textColorHint);
            }
            //设置过滤器
            etInputBar.setFilters(new InputFilter[]{inputFilter, new InputFilter.LengthFilter(maxLength)});
            //设置输入框是否可以编辑
            etInputBar.setEnabled(editable);
            //设置输入框文字居中位置
            if (gravity == 0) {
                etInputBar.setGravity(Gravity.LEFT);
            } else if (gravity == 1) {
                etInputBar.setGravity(Gravity.RIGHT);
            } else if (gravity == 2) {
                etInputBar.setGravity(Gravity.CENTER_HORIZONTAL);
            }
            //设置标题和内容的间距
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) etInputBar.getLayoutParams();
            params.leftMargin = (int) titleContentSpace;
            etInputBar.setLayoutParams(params);

            etInputBar.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (focusChangeListener != null) {
                        focusChangeListener.onFocusChange(v, hasFocus);
                    }
                }
            });

            etInputBar.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    if (watcher != null) {
                        watcher.beforeTextChanged(s, start, count, after);
                    }
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (watcher != null) {
                        watcher.onTextChanged(s, start, before, count);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (watcher != null) {
                        watcher.afterTextChanged(s);
                    }
                }
            });

        }

        //设置上边框
        if (topBorder != null) {
            //设置上边框的颜色
            if (topBorderColor != -1) {
                topBorder.setBackgroundColor(topBorderColor);
            }
            LinearLayout.LayoutParams topBorderParams = (LinearLayout.LayoutParams) topBorder.getLayoutParams();
            topBorderParams.height = (int) topBorderWidth;//上边框宽度
            topBorderParams.leftMargin = (int) topBorderLeftMargin;//上边框距离左侧的距离
            topBorderParams.rightMargin = (int) topBorderRightMargin;//上边框距离右侧的距离
        }

        //设置下边框
        if (bottomBorder != null) {
            //设置下边框的颜色
            if (bottomBorderColor != -1) {
                bottomBorder.setBackgroundColor(bottomBorderColor);
            }
            LinearLayout.LayoutParams bottomBorderParams = (LinearLayout.LayoutParams) bottomBorder.getLayoutParams();
            bottomBorderParams.height = (int) bottomBorderWidth;//下边框宽度
            bottomBorderParams.leftMargin = (int) bottomBorderLeftMargin;//下边框距离左侧的距离
            bottomBorderParams.rightMargin = (int) bottomBorderRightMargin;//下边框距离右侧的距离
        }

    }

    /**
     * 设置标题和内容的间距
     *
     * @param space
     */
    public void setTitleContentSpace(float space) {
        this.titleContentSpace = space;
        if (etInputBar != null) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) etInputBar.getLayoutParams();
            params.leftMargin = (int) titleContentSpace;
            etInputBar.setLayoutParams(params);
        }
    }

    /**
     * 设置输入框文字居中位置
     *
     * @param gravity
     */
    public void setContentGravity(@ContentGravity int gravity) {
        this.gravity = gravity;
        if (etInputBar != null) {
            if (gravity == 0) {
                etInputBar.setGravity(Gravity.LEFT);
            } else if (gravity == 1) {
                etInputBar.setGravity(Gravity.RIGHT);
            } else if (gravity == 2) {
                etInputBar.setGravity(Gravity.CENTER_HORIZONTAL);
            }
        }
    }

    /**
     * 设置输入框文字字号
     *
     * @param unit
     * @param size
     */
    public void setContentTextSize(int unit, float size) {
        this.contentTextSize = (int) size;
        //设置输入框字号
        if (etInputBar != null) {
            etInputBar.setTextSize(unit, contentTextSize);
        }
    }

    /**
     * 输入框内容
     *
     * @param text
     */
    public void setContent(CharSequence text) {
        this.content = text;
        if (etInputBar != null) {
            etInputBar.setText(content);
        }
    }

    /**
     * 输入框占位文字
     *
     * @param hint
     */
    public void setHint(CharSequence hint) {
        this.hint = hint;
        if (etInputBar != null) {
            etInputBar.setHint(hint);
        }
    }

    /**
     * 设置输入框是否单行
     *
     * @param flag
     */
    public void setSingleLine(boolean flag) {
        this.singleLine = flag;
        if (etInputBar != null) {
            etInputBar.setSingleLine(singleLine);
        }
    }

    /**
     * 设置必填文字和标题文字的对齐方式
     *
     * @param gravity
     */
    public void setRequiredTextGravity(@RequiredGravity int gravity) {
        this.requiredTextGravity = gravity;
        if (tvRequired != null && tvInputTitle != null) {
            if (requiredTextGravity == 0) {
                //上对齐
                RelativeLayout.LayoutParams requiredParams = (RelativeLayout.LayoutParams) tvRequired.getLayoutParams();
                requiredParams.addRule(RelativeLayout.ALIGN_TOP, tvInputTitle.getId());
                tvRequired.setLayoutParams(requiredParams);
            } else if (requiredTextGravity == 1) {
                //下对齐
                RelativeLayout.LayoutParams requiredParams = (RelativeLayout.LayoutParams) tvRequired.getLayoutParams();
                requiredParams.addRule(RelativeLayout.ALIGN_BASELINE, tvInputTitle.getId());
                tvRequired.setLayoutParams(requiredParams);
            } else if (requiredTextGravity == 2) {
                //居中对齐
                RelativeLayout.LayoutParams requiredParams = (RelativeLayout.LayoutParams) tvRequired.getLayoutParams();
                requiredParams.addRule(RelativeLayout.ALIGN_BOTTOM, tvInputTitle.getId());
                requiredParams.addRule(RelativeLayout.ALIGN_TOP, tvInputTitle.getId());
                tvRequired.setLayoutParams(requiredParams);
            }
        }
    }

    /**
     * 设置必填的文字
     *
     * @param text
     */
    public void setRequiredText(CharSequence text) {
        this.requiredText = text;
        if (tvRequired != null) {
            tvRequired.setText(text);
        }
    }

    /**
     * 设置必填的文字字号
     *
     * @param unit
     * @param size
     */
    public void setRequiredTextSize(int unit, float size) {
        this.requiredTextSize = (int) size;
        if (tvRequired != null) {
            tvRequired.setTextSize(unit, size);
        }
    }

    /**
     * 设置必填的文字颜色
     *
     * @param color
     */
    public void setRequiredTextColor(@ColorInt int color) {
        this.requiredTextColor = color;
        if (tvRequired != null) {
            tvRequired.setTextColor(color);
        }
    }

    /**
     * 必填文字和标题文字的间距
     *
     * @param requiredPadding
     */
    public void setRequiredPadding(float requiredPadding) {
        this.requiredPadding = requiredPadding;
        if (tvRequired != null) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tvRequired.getLayoutParams();
            params.leftMargin = (int) requiredPadding;
            tvRequired.setLayoutParams(params);
        }
    }

    /**
     * 设置标题居中方式
     *
     * @param gravity 对齐方式
     */
    public void setTitleGravity(@TitleGravity int gravity) {
        this.titleGravity = gravity;
        if (titleGroup != null) {
            LinearLayout.LayoutParams titleGroupParams = (LinearLayout.LayoutParams) titleGroup.getLayoutParams();
            if (titleGravity == 0) {
                //标题居上
                titleGroupParams.gravity = Gravity.TOP;
            } else if (titleGravity == 1) {
                //标题居底部
                titleGroupParams.gravity = Gravity.BOTTOM;
            } else if (titleGravity == 2) {
                //标题居中
                titleGroupParams.gravity = Gravity.CENTER_VERTICAL;
            }
            titleGroup.setLayoutParams(titleGroupParams);
        }
    }

    /**
     * 设置必填的星星图标
     *
     * @param drawable 图标
     * @param padding  图标和文字的间距
     */
    public void setRequiredIcon(Drawable drawable, float padding) {
        this.requiredIcon = drawable;
        this.requiredPadding = padding;
        if (tvInputTitle != null) {
            setDrawable(tvInputTitle, requiredPadding, null, null, requiredIcon, null);//设置标题右侧必填的星号
        }
    }

    /**
     * 设置标题字号
     *
     * @param unit
     * @param size
     */
    public void setTitleTextSize(int unit, float size) {
        this.titleTextSize = (int) size;
        if (tvInputTitle != null) {
            tvInputTitle.setTextSize(unit, size);//标题字号
        }
    }

    /**
     * 设置标题文字颜色
     *
     * @param color
     */
    public void setTitleTextColor(@ColorInt int color) {
        this.titleTextColor = color;
        if (tvInputTitle != null) {
            tvInputTitle.setTextColor(color);//标题文字颜色
        }
    }

    /**
     * 设置标题
     *
     * @param title 标题文字
     * @param bold  标题文字的加粗尺寸
     */
    public void setTitle(CharSequence title, float bold) {
        this.title = title;
        this.titleTextBold = bold;
        if (tvInputTitle != null) {
            setText(tvInputTitle, title, bold);//设置标题文字、文字粗细
        }
    }

    /**
     * 设置图标颜色
     *
     * @param color
     */
    public void setIconColorFilter(int color) {
        this.inputIconColor = color;
        if (ivInputIcon != null) {
            if (inputIconColor != -1) {
                ivInputIcon.setColorFilter(inputIconColor);
            }
        }
    }

    /**
     * 设置输入框图标
     *
     * @param drawable
     */
    public void setIcon(@Nullable Drawable drawable) {
        this.inputIcon = drawable;
        if (ivInputIcon != null) {
            ivInputIcon.setImageDrawable(drawable);//输入框图标
        }
    }

    /**
     * 标题和输入框图标之间的间距
     *
     * @param padding
     */
    public void setInputIconPadding(float padding) {
        this.inputIconPadding = padding;
        if (inputIcon != null) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivInputIcon.getLayoutParams();
            params.rightMargin = (int) inputIconPadding;
            ivInputIcon.setLayoutParams(params);
        }
    }

    /**
     * 设置上边框的颜色
     *
     * @param topBorderColor 上边框的颜色色值
     */
    public void setTopBorderColor(int topBorderColor) {
        this.topBorderColor = topBorderColor;
        if (topBorder != null) {
            topBorder.setBackgroundColor(topBorderColor);
        }
    }

    /**
     * 设置下边框的颜色
     *
     * @param bottomBorderColor 下边框的颜色色值
     */
    public void setBottomBorderColor(int bottomBorderColor) {
        this.bottomBorderColor = bottomBorderColor;
        if (bottomBorder != null) {
            bottomBorder.setBackgroundColor(bottomBorderColor);
        }
    }

    /**
     * 设置最大文字输入长度
     *
     * @param maxLength 最大输入字数
     */
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
        if (etInputBar != null) {
            if (inputFilter == null) {
                //过滤条件
                inputFilter = new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
                        if (!TextUtils.isEmpty(charSequence)) {
                            String inputStr = charSequence.toString();
                            if (TextUtils.isEmpty(digits)) {
                                //如果没有匹配条件就不去匹配
                                return null;
                            } else if (inputStr.matches(digits)) {
                                //设置了匹配条件 且符合匹配条件
                                return null;
                            } else {
                                //设置了匹配条件但不符合匹配条件
                                return "";
                            }
                        } else {
                            //输入的为空则过滤掉
                            return "";
                        }
                    }
                };
            }
            etInputBar.setFilters(new InputFilter[]{inputFilter, new InputFilter.LengthFilter(maxLength)});
        }
    }

    /**
     * 设置上边框的宽度(粗细)
     *
     * @param topBorderWidth 宽度值
     */
    public void setTopBorderWidth(float topBorderWidth) {
        this.topBorderWidth = topBorderWidth;
        if (topBorder != null) {
            LinearLayout.LayoutParams topBorderParams = (LinearLayout.LayoutParams) topBorder.getLayoutParams();
            topBorderParams.height = (int) topBorderWidth;//上边框宽度
        }
    }

    /**
     * 设置下边框的宽度(粗细)
     *
     * @param bottomBorderWidth 宽度值
     */
    public void setBottomBorderWidth(float bottomBorderWidth) {
        this.bottomBorderWidth = bottomBorderWidth;
        if (bottomBorder != null) {
            LinearLayout.LayoutParams bottomBorderParams = (LinearLayout.LayoutParams) bottomBorder.getLayoutParams();
            bottomBorderParams.height = (int) bottomBorderWidth;//下边框宽度
        }
    }

    /**
     * 设置上边框距离最左边的距离
     *
     * @param topBorderLeftMargin 距离值
     */
    public void setTopBorderLeftMargin(float topBorderLeftMargin) {
        this.topBorderLeftMargin = topBorderLeftMargin;
        LinearLayout.LayoutParams topBorderParams = (LinearLayout.LayoutParams) topBorder.getLayoutParams();
        topBorderParams.leftMargin = (int) topBorderLeftMargin;//上边框距离左侧的距离
    }

    /**
     * 设置上边框距离最右边的距离
     *
     * @param topBorderRightMargin 距离值
     */
    public void setTopBorderRightMargin(float topBorderRightMargin) {
        this.topBorderRightMargin = topBorderRightMargin;
        LinearLayout.LayoutParams topBorderParams = (LinearLayout.LayoutParams) topBorder.getLayoutParams();
        topBorderParams.rightMargin = (int) topBorderRightMargin;//上边框距离右侧的距离
    }

    /**
     * 设置下边框距离最左边的距离
     *
     * @param bottomBorderLeftMargin 距离值
     */
    public void setBottomBorderLeftMargin(float bottomBorderLeftMargin) {
        this.bottomBorderLeftMargin = bottomBorderLeftMargin;
        LinearLayout.LayoutParams bottomBorderParams = (LinearLayout.LayoutParams) bottomBorder.getLayoutParams();
        bottomBorderParams.leftMargin = (int) bottomBorderLeftMargin;//下边框距离左侧的距离
    }

    /**
     * 设置下边框距离最右边的距离
     *
     * @param bottomBorderRightMargin 距离值
     */
    public void setBottomBorderRightMargin(float bottomBorderRightMargin) {
        this.bottomBorderRightMargin = bottomBorderRightMargin;
        LinearLayout.LayoutParams bottomBorderParams = (LinearLayout.LayoutParams) bottomBorder.getLayoutParams();
        bottomBorderParams.rightMargin = (int) bottomBorderRightMargin;//下边框距离右侧的距离
    }

    /**
     * 设置是否可编辑的状态
     *
     * @param editable true可编辑,false不可编辑
     */
    public void setEditable(boolean editable) {
        this.editable = editable;
        if (etInputBar != null) {
            etInputBar.setEnabled(editable);
        }
    }

    /**
     * 设置内容文字颜色
     *
     * @param contentTextColor 内容文字颜色
     */
    public void setContentTextColor(@ColorInt int contentTextColor) {
        this.contentTextColor = contentTextColor;
        if (etInputBar != null) {
            etInputBar.setTextColor(contentTextColor);
        }
    }

    /**
     * 设置提示文字颜色
     *
     * @param textColorHint 提示文字颜色
     */
    public void setTextColorHint(@ColorInt int textColorHint) {
        this.textColorHint = textColorHint;
        if (etInputBar != null) {
            etInputBar.setHintTextColor(textColorHint);
        }
    }

    /**
     * 输入框文字改变监听
     *
     * @param watcher
     */
    public void addTextChangedListener(TextWatcher watcher) {
        this.watcher = watcher;
    }

    /**
     * 输入框焦点改变监听
     *
     * @param focusChangeListener
     */
    public void setOnFocusChangeListener(OnFocusChangeListener focusChangeListener) {
        this.focusChangeListener = focusChangeListener;
    }

    /**
     * 获取输入的内容
     *
     * @return 返回输入框中的内容
     */
    public String getText() {
        if (etInputBar != null) {
            Editable editable = etInputBar.getText();
            return TextUtils.isEmpty(editable) ? "" : editable.toString();
        }
        return "";
    }

    /**
     * 获取输入框控件 用于显示或隐藏键盘
     *
     * @return 返回的输入框控件
     */
    public EditText getEditText() {
        return etInputBar;
    }

    /**
     * 隐藏软键盘
     *
     * @return 是否隐藏成功
     */
    public boolean hideKeyboard() {
        boolean isHide = false;
        if (null != getEditText()) {
            InputMethodManager inputManager = (InputMethodManager) getEditText().getContext().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            // 即使当前焦点不在editText，也是可以隐藏的。
            isHide = inputManager.hideSoftInputFromWindow(getEditText().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        return isHide;
    }

    /**
     * 显示软键盘
     */
    public void showKeyboard() {
        showKeyboard(0);
    }

    /**
     * 显示软键盘
     *
     * @param delay 延时显示软键盘延时的毫秒数
     */
    public void showKeyboard(int delay) {
        if (null != getEditText()) {
            if (getEditText().requestFocus()) {
                if (delay > 0) {
                    getEditText().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            InputMethodManager imm = (InputMethodManager) getEditText().getContext().getApplicationContext()
                                    .getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(getEditText(), InputMethodManager.SHOW_IMPLICIT);
                        }
                    }, delay);
                } else {
                    InputMethodManager imm = (InputMethodManager) getEditText().getContext().getApplicationContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(getEditText(), InputMethodManager.SHOW_IMPLICIT);
                }
            } else {
                Log.w("EditableTextView", "showSoftInput() can not get focus");
            }
        }
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    private int sp2px(float spValue) {
        final float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 为TextView设置Drawable图片
     *
     * @param textView
     * @param drawablePadding 图片和文字的间距
     * @param leftDrawable    左图片
     * @param topDrawable     上图片
     * @param rightDrawable   右图片
     * @param bottomDrawable  下图片
     */
    private void setDrawable(TextView textView, float drawablePadding, Drawable leftDrawable, Drawable topDrawable, Drawable rightDrawable, Drawable bottomDrawable) {
        if (textView != null) {
            textView.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, topDrawable, rightDrawable, bottomDrawable);
            textView.setCompoundDrawablePadding((int) drawablePadding);
        }
    }

    /**
     * 设置字体粗细
     *
     * @param textView  显示文字的控件
     * @param text      要显示的文字
     * @param thickness 文字的粗细程度
     */
    private void setText(@NonNull TextView textView, CharSequence text, final float thickness) {
        if (textView != null && !TextUtils.isEmpty(text)) {
            SpannableStringBuilder spannableString = new SpannableStringBuilder(text);
            spannableString.setSpan(new CharacterStyle() {
                @Override
                public void updateDrawState(TextPaint textPaint) {
                    //tp.setFakeBoldText(true);//一种伪粗体效果，比原字体加粗的效果弱一点
                    //textPaint.setColor(color);//字体颜色
                    textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                    textPaint.setStrokeWidth(thickness > 0 ? thickness : 0);//控制字体加粗的程度
                }
            }, 0, TextUtils.isEmpty(text) ? 0 : text.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            textView.setText(spannableString);
        }
    }

}
