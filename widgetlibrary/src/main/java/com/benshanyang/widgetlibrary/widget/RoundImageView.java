package com.benshanyang.widgetlibrary.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.benshanyang.widgetlibrary.R;

/**
 * 类描述: 圆角/圆形图片 </br>
 * 时间: 2019/3/21 15:30
 *
 * @author YangKuan
 * @version 1.0.0
 * @since
 */
public class RoundImageView extends AppCompatImageView {
    private static final int DEFAULT_BORDER_COLOR = Color.TRANSPARENT;

    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    private static final int COLOR_DRAWABLE_DIMEN = 2;

    private boolean mIsOval = false;
    private boolean mIsCircle = false;

    private int maskColor;
    private int mBorderWidth;
    private int mBorderColor;

    private int cornerRadius = 0; // 统一设置圆角半径，优先级高于单独设置每个角的半径
    private int cornerTopLeftRadius = 0; // 左上角圆角半径
    private int cornerTopRightRadius = 0; // 右上角圆角半径
    private int cornerBottomLeftRadius = 0; // 左下角圆角半径
    private int cornerBottomRightRadius = 0; // 右下角圆角半径

    private Paint mBitmapPaint;
    private Paint mBorderPaint;
    private ColorFilter mColorFilter;
    private BitmapShader mBitmapShader;
    private boolean mNeedResetShader = false;

    private Path path = new Path();
    private RectF mRectF = new RectF();
    private RectF mDrawRectF = new RectF();

    private Bitmap mBitmap;

    private Matrix mMatrix;
    private int mWidth;
    private int mHeight;
    private ScaleType mLastCalculateScaleType;

    public RoundImageView(Context context) {
        this(context, null);
    }

    public RoundImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mMatrix = new Matrix();

        setScaleType(ScaleType.CENTER_CROP);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView, 0, 0);
        mBorderWidth = ta.getDimensionPixelSize(R.styleable.RoundImageView_borderWidth, 0);
        mBorderColor = ta.getColor(R.styleable.RoundImageView_borderColor, DEFAULT_BORDER_COLOR);
        maskColor = ta.getColor(R.styleable.RoundImageView_maskColor, Color.TRANSPARENT);
        if (maskColor != Color.TRANSPARENT) {
            mColorFilter = new PorterDuffColorFilter(maskColor, PorterDuff.Mode.SRC_ATOP);
        }

        mIsCircle = ta.getBoolean(R.styleable.RoundImageView_isCircle, false);
        if (!mIsCircle) {
            mIsOval = ta.getBoolean(R.styleable.RoundImageView_isOval, false);
        }
        if (!mIsOval) {
            cornerRadius = ta.getDimensionPixelSize(R.styleable.RoundImageView_cornerRadius, 0);
        }

        if (cornerRadius <= 0) {
            cornerTopLeftRadius = ta.getDimensionPixelSize(R.styleable.RoundImageView_cornerTopLeftRadius, cornerTopLeftRadius);// 左上角圆角半径
            cornerTopRightRadius = ta.getDimensionPixelSize(R.styleable.RoundImageView_cornerTopRightRadius, cornerTopRightRadius);// 右上角圆角半径
            cornerBottomLeftRadius = ta.getDimensionPixelSize(R.styleable.RoundImageView_cornerBottomLeftRadius, cornerBottomLeftRadius);// 左下角圆角半径
            cornerBottomRightRadius = ta.getDimensionPixelSize(R.styleable.RoundImageView_cornerBottomRightRadius, cornerBottomRightRadius);// 右下角圆角半径
        }
        ta.recycle();
    }

    @Override
    public void setAdjustViewBounds(boolean adjustViewBounds) {
        if (adjustViewBounds) {
            throw new IllegalArgumentException("不支持adjustViewBounds");
        }
    }

    public void setBorderWidth(int borderWidth) {
        if (mBorderWidth != borderWidth) {
            mBorderWidth = borderWidth;
            invalidate();
        }
    }

    public void setBorderColor(@ColorInt int borderColor) {
        if (mBorderColor != borderColor) {
            mBorderColor = borderColor;
            invalidate();
        }
    }

    public void setCornerRadius(int cornerRadius) {
        if (RoundImageView.this.cornerRadius != cornerRadius) {
            RoundImageView.this.cornerRadius = cornerRadius;
            if (!mIsCircle && !mIsOval) {
                invalidate();
            }
        }
    }

    public void setCornerAroundRadius(int topLeft, int topRight, int bottomLeft, int bottomRight) {
        if (cornerTopLeftRadius != topLeft) {
            cornerTopLeftRadius = topLeft;
        }

        if (cornerTopRightRadius != topRight) {
            cornerTopRightRadius = topRight;
        }

        if (cornerBottomLeftRadius != bottomLeft) {
            cornerBottomLeftRadius = bottomLeft;
        }

        if (cornerBottomRightRadius != bottomRight) {
            cornerBottomRightRadius = bottomRight;
        }

        if (!mIsCircle && !mIsOval && !(cornerRadius > 0)) {
            invalidate();
        }
    }

    public void setMaskColor(@ColorInt int maskColor) {
        if (RoundImageView.this.maskColor != maskColor) {
            RoundImageView.this.maskColor = maskColor;
            if (RoundImageView.this.maskColor != Color.TRANSPARENT) {
                mColorFilter = new PorterDuffColorFilter(RoundImageView.this.maskColor, PorterDuff.Mode.SRC_ATOP);
            } else {
                mColorFilter = null;
            }
            invalidate();
        }
    }

    public void setCircle(boolean isCircle) {
        if (mIsCircle != isCircle) {
            mIsCircle = isCircle;
            requestLayout();
            invalidate();
        }
    }

    public void setOval(boolean isOval) {
        boolean forceUpdate = false;
        if (isOval) {
            if (mIsCircle) {
                // 必须先取消圆形
                mIsCircle = false;
                forceUpdate = true;
            }

        }
        if (mIsOval != isOval || forceUpdate) {
            mIsOval = isOval;
            requestLayout();
            invalidate();
        }
    }

    public int getBorderColor() {
        return mBorderColor;
    }

    public int getBorderWidth() {
        return mBorderWidth;
    }

    public int getCornerRadius() {
        return cornerRadius;
    }

    public int getMaskColor() {
        return maskColor;
    }

    public int getCornerTopLeftRadius() {
        return cornerTopLeftRadius;
    }

    public int getCornerTopRightRadius() {
        return cornerTopRightRadius;
    }

    public int getCornerBottomLeftRadius() {
        return cornerBottomLeftRadius;
    }

    public int getCornerBottomRightRadius() {
        return cornerBottomRightRadius;
    }

    public boolean isCircle() {
        return mIsCircle;
    }

    public boolean isOval() {
        return !mIsCircle && mIsOval;
    }

    public void setColorFilter(ColorFilter cf) {
        if (mColorFilter != cf) {
            mColorFilter = cf;
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
            setMeasuredDimension(widthSize, heightSize);
            return;
        }
        if (mIsCircle) {
            if (widthMode == MeasureSpec.EXACTLY) {
                setMeasuredDimension(widthSize, widthSize);
            } else if (heightMode == MeasureSpec.EXACTLY) {
                setMeasuredDimension(heightSize, heightSize);
            } else {
                if (mBitmap == null) {
                    setMeasuredDimension(0, 0);
                } else {
                    int w = Math.min(mBitmap.getWidth(), widthSize);
                    int h = Math.min(mBitmap.getHeight(), heightSize);
                    int size = Math.min(w, h);
                    setMeasuredDimension(size, size);
                }
            }
            return;
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        setupBitmap();
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        setupBitmap();
    }

    private Bitmap getBitmap() {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            if (bitmap == null) {
                return null;
            }
            float bmWidth = bitmap.getWidth(), bmHeight = bitmap.getHeight();
            if (bmWidth == 0 || bmHeight == 0) {
                return null;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                // ensure minWidth and minHeight
                float minScaleX = getMinimumWidth() / bmWidth, minScaleY = getMinimumHeight() / bmHeight;
                if (minScaleX > 1 || minScaleY > 1) {
                    float scale = Math.max(minScaleX, minScaleY);
                    Matrix matrix = new Matrix();
                    matrix.postScale(scale, scale);

                    return Bitmap.createBitmap(
                            bitmap, 0, 0, (int) bmWidth, (int) bmHeight, matrix, false);
                }
            }
            return bitmap;
        }

        try {
            Bitmap bitmap;

            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(COLOR_DRAWABLE_DIMEN, COLOR_DRAWABLE_DIMEN, BITMAP_CONFIG);
            } else {
                bitmap = Bitmap.createBitmap(
                        drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), BITMAP_CONFIG);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public void setupBitmap() {
        Bitmap bm = getBitmap();
        if (bm == mBitmap) {
            return;
        }
        mBitmap = bm;
        if (mBitmap == null) {
            mBitmapShader = null;
            invalidate();
            return;
        }
        mNeedResetShader = true;
        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        if (mBitmapPaint == null) {
            mBitmapPaint = new Paint();
            mBitmapPaint.setAntiAlias(true);
        }
        mBitmapPaint.setShader(mBitmapShader);
        requestLayout();
        invalidate();
    }

    private void updateBitmapShader() {
        mMatrix.reset();
        mNeedResetShader = false;
        if (mBitmapShader == null || mBitmap == null) {
            return;
        }
        updateMatrix(mMatrix, mBitmap, mRectF);
        mBitmapShader.setLocalMatrix(mMatrix);
        mBitmapPaint.setShader(mBitmapShader);
    }

    private void updateMatrix(@NonNull Matrix matrix, @NonNull Bitmap bitmap, RectF drawRect) {
        final float bmWidth = bitmap.getWidth();
        final float bmHeight = bitmap.getHeight();
        final ScaleType scaleType = getScaleType();
        if (scaleType == ScaleType.MATRIX) {
            updateScaleTypeMatrix(matrix, bitmap, drawRect);
        } else if (scaleType == ScaleType.CENTER) {
            float left = (mWidth - bmWidth) / 2;
            float top = (mHeight - bmHeight) / 2;
            matrix.postTranslate(left, top);
            drawRect.set(
                    Math.max(0, left),
                    Math.max(0, top),
                    Math.min(left + bmWidth, mWidth),
                    Math.min(top + bmHeight, mHeight));
        } else if (scaleType == ScaleType.CENTER_CROP) {
            float scaleX = mWidth / bmWidth, scaleY = mHeight / bmHeight;
            final float scale = Math.max(scaleX, scaleY);
            matrix.setScale(scale, scale);
            matrix.postTranslate(-(scale * bmWidth - mWidth) / 2, -(scale * bmHeight - mHeight) / 2);
            drawRect.set(0, 0, mWidth, mHeight);
        } else if (scaleType == ScaleType.CENTER_INSIDE) {
            float scaleX = mWidth / bmWidth, scaleY = mHeight / bmHeight;
            if (scaleX >= 1 && scaleY >= 1) {
                float left = (mWidth - bmWidth) / 2;
                float top = (mHeight - bmHeight) / 2;
                matrix.postTranslate(left, top);
                drawRect.set(left, top, left + bmWidth, top + bmHeight);
            } else {
                float scale = Math.min(scaleX, scaleY);
                matrix.setScale(scale, scale);
                float bw = bmWidth * scale, bh = bmHeight * scale;
                float left = (mWidth - bw) / 2;
                float top = (mHeight - bh) / 2;
                matrix.postTranslate(left, top);
                drawRect.set(left, top, left + bw, top + bh);
            }
        } else if (scaleType == ScaleType.FIT_XY) {
            float scaleX = mWidth / bmWidth, scaleY = mHeight / bmHeight;
            matrix.setScale(scaleX, scaleY);
            drawRect.set(0, 0, mWidth, mHeight);
        } else {
            float scaleX = mWidth / bmWidth, scaleY = mHeight / bmHeight;
            float scale = Math.min(scaleX, scaleY);
            matrix.setScale(scale, scale);
            float bw = bmWidth * scale, bh = bmHeight * scale;
            if (scaleType == ScaleType.FIT_START) {
                drawRect.set(0, 0, bw, bh);
            } else if (scaleType == ScaleType.FIT_CENTER) {
                float left = (mWidth - bw) / 2;
                float top = (mHeight - bh) / 2;
                matrix.postTranslate(left, top);
                drawRect.set(left, top, left + bw, top + bh);
            } else {
                matrix.postTranslate(mWidth - bw, mHeight - bh);
                drawRect.set(mWidth - bw, mHeight - bh, mWidth, mHeight);
            }
        }

    }

    protected void updateScaleTypeMatrix(@NonNull Matrix matrix, @NonNull Bitmap bitmap, RectF drawRect) {
        matrix.set(getImageMatrix());
        drawRect.set(0, 0, mWidth, mHeight);
    }

    private float[] borderRadii = new float[8];

    private void drawBitmap(Canvas canvas, int borderWidth) {
        final float halfBorderWidth = borderWidth * 1.0f / 2;
        mBitmapPaint.setColorFilter(mColorFilter);

        if (mIsCircle) {
            canvas.drawCircle(mRectF.centerX(), mRectF.centerY(), (Math.min(mRectF.width() / 2, mRectF.height() / 2)) - halfBorderWidth, mBitmapPaint);
        } else {
            mDrawRectF.left = mRectF.left + halfBorderWidth;
            mDrawRectF.top = mRectF.top + halfBorderWidth;
            mDrawRectF.right = mRectF.right - halfBorderWidth;
            mDrawRectF.bottom = mRectF.bottom - halfBorderWidth;
            if (mIsOval) {
                canvas.drawOval(mDrawRectF, mBitmapPaint);
            } else {
                if (cornerRadius > 0) {
                    canvas.drawRoundRect(mDrawRectF, cornerRadius, cornerRadius, mBitmapPaint);
                } else {
                    borderRadii[0] = borderRadii[1] = cornerTopLeftRadius;
                    borderRadii[2] = borderRadii[3] = cornerTopRightRadius;
                    borderRadii[4] = borderRadii[5] = cornerBottomRightRadius;
                    borderRadii[6] = borderRadii[7] = cornerBottomLeftRadius;
                    path.addRoundRect(mDrawRectF, borderRadii, Path.Direction.CCW);
                    canvas.drawPath(path, mBitmapPaint);
                    path.reset();
                }
            }
        }
    }

    private void drawBorder(Canvas canvas, int borderWidth) {
        if (borderWidth > 0) {
            final float halfBorderWidth = borderWidth * 1.0f / 2;
            mBorderPaint.setColor(mBorderColor);
            mBorderPaint.setStrokeWidth(borderWidth);
            if (mIsCircle) {
                canvas.drawCircle(mRectF.centerX(), mRectF.centerY(), Math.min(mRectF.width(), mRectF.height()) / 2 - halfBorderWidth, mBorderPaint);
            } else {
                mDrawRectF.left = mRectF.left + halfBorderWidth;
                mDrawRectF.top = mRectF.top + halfBorderWidth;
                mDrawRectF.right = mRectF.right - halfBorderWidth;
                mDrawRectF.bottom = mRectF.bottom - halfBorderWidth;
                if (mIsOval) {
                    canvas.drawOval(mDrawRectF, mBorderPaint);
                } else {
                    if (cornerRadius > 0) {
                        canvas.drawRoundRect(mDrawRectF, cornerRadius, cornerRadius, mBorderPaint);
                    } else {
                        borderRadii[0] = borderRadii[1] = cornerTopLeftRadius;
                        borderRadii[2] = borderRadii[3] = cornerTopRightRadius;
                        borderRadii[4] = borderRadii[5] = cornerBottomRightRadius;
                        borderRadii[6] = borderRadii[7] = cornerBottomLeftRadius;
                        path.addRoundRect(mDrawRectF, borderRadii, Path.Direction.CCW);
                        canvas.drawPath(path, mBorderPaint);
                        path.reset();
                    }
                }
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth(), height = getHeight();
        if (width <= 0 || height <= 0) {
            return;
        }

        int borderWidth = mBorderWidth;

        if (mBitmap == null || mBitmapShader == null) {
            drawBorder(canvas, borderWidth);
            return;
        }

        if (mWidth != width || mHeight != height
                || mLastCalculateScaleType != getScaleType() || mNeedResetShader) {
            mWidth = width;
            mHeight = height;
            mLastCalculateScaleType = getScaleType();
            updateBitmapShader();
        }
        drawBitmap(canvas, borderWidth);
        drawBorder(canvas, borderWidth);
    }

}
