package com.dd.news.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import com.dd.framework.utils.UIUtils;
import java.util.ArrayList;
/**
 * Created by J.Tommy on 17/3/2.
 * 用draw的方式实现标签云，避免addView带来的性能问题，尤其是在列表滚动的过程中
 */
public class TagGroupView extends View {
    private ArrayList<Cell> cells = new ArrayList<>();
    String[] test1 = new String[]{"一", "二二", "三三三", "四四四四", "五五五五五"};
    String[] test2 = new String[]{"一", "二二", "三三三", "四四四四", "五五五五五", "六六六六六六"};
    String[] test3 = new String[]{"一", "二二", "三三三", "四四四四", "五五五五五", "六六六六六六", "七七七七七七七"};

    public TagGroupView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TagGroupView(Context context) {
        super(context);
    }

    public TagGroupView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

//    Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case 1:
//                    setTags(test1);
//                    mHandler.sendEmptyMessageDelayed(2, 5000);
//                    break;
//                case 2:
//                    setTags(test2);
//                    mHandler.sendEmptyMessageDelayed(3, 5000);
//                    break;
//                case 3:
//                    setTags(test3);
//                    break;
//                default:
//                    break;
//            }
//        }
//    };
//
//    public void changeDataByTime() {
//        mHandler.sendEmptyMessageDelayed(1, 5000);
//    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int totalHeight = measure();
//        Log.i("jwd", "onMeasure totalHeight=" + totalHeight);
        reset();
        setMeasuredDimension(width, totalHeight);
    }

    private void reset() {
        cellX = 0;
        cellY = 0;
        cellWidth = 0;
        cellHeight = 0;
        lastCellX = 0;
        lastCellY = 0;
        lastCellWidth = 0;
    }

    public void setTags(final String[] tags) {
        cells = new ArrayList<>();
        for (String tag : tags) {
            cells.add(Cell.create(tag));
        }
        if (needChangeLine()) {
            requestLayout();
        } else {
            postInvalidate();
        }
    }

    private boolean needChangeLine() {
        final int totalHeight = measure();
        reset();
        if (getMeasuredHeight() == totalHeight) {
            return false;
        } else {
            return true;
        }
    }

    int cellX;
    int cellY;
    int cellWidth;
    int cellHeight;
    int lastCellX;
    int lastCellY;
    int lastCellWidth;
    int horizontalSpacing = 20;//水平间距
    int verticalSpacing = 40;//竖直间距
    int cellPaddingLeft = UIUtils.dip2px(6);
    int cellPaddingright = UIUtils.dip2px(6);
    int cellPaddingTop = UIUtils.dip2px(5);
    int cellPaddingBottom = UIUtils.dip2px(5);
    RectF rect = new RectF();

    private int measure() {
        if (cells == null) {
            return 0;
        }
        for (Cell cell : cells) {
            cell.mPaddingLeft = cellPaddingLeft;
            cell.mPaddingRight = cellPaddingright;
            cell.mPaddingTop = cellPaddingTop;
            cell.mPaddingBottom = cellPaddingBottom;

            cellWidth = cell.getMeasureWidth();
            cellHeight = cell.getMeasureHeight();
            //draw
            if (lastCellX + lastCellWidth + cellWidth + horizontalSpacing >getMeasuredWidth()) {
                cellX = 0;
                cellY += cell.getMeasureHeight() + verticalSpacing;
            } else {
                cellX = lastCellX + lastCellWidth + horizontalSpacing;
            }
            rect = new RectF();
            rect.set(cellX, cellY, cellX + cellWidth, cellY + cellHeight);
            cell.setRect(rect);
            lastCellWidth = cellWidth;
            lastCellX = cellX;
            lastCellY = cellY;
        }
        return lastCellY + cellHeight;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (cells == null) {
            return;
        }
        for (Cell cell : cells) {
            canvas.save();
            canvas.clipRect(cell.mRect);
            cell.draw(canvas);
            canvas.restore();
        }
    }

    private static class Cell {
        public String mText;
        public int mTextColor = Color.parseColor("#ff333333");
        public int mTextSize = UIUtils.dip2px(25);
        public int mBackground = Color.parseColor("#fff6f6f6");

        private Canvas mCanvas;
        private TextPaint mTextPaint;
        private Paint mBgPaint;
        private RectF mRect;
        private int mPaddingLeft;
        private int mPaddingRight;
        private int mPaddingTop;
        private int mPaddingBottom;

        private Cell(String text) {
            mText = text;
            mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            mTextPaint.setTextSize(mTextSize);
            mTextPaint.setColor(mTextColor);
            mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mBgPaint.setColor(mBackground);
        }

        public void setRect(RectF rect) {
            mRect = rect;
        }

        public int getMeasureWidth() {
            return (int) StaticLayout.getDesiredWidth(mText, mTextPaint) + mPaddingLeft + mPaddingRight;
        }

        public int getMeasureHeight() {
            Paint.FontMetrics fm = mTextPaint.getFontMetrics();
            return (int) Math.ceil(fm.descent - fm.ascent) + mPaddingTop + mPaddingBottom;
        }

        public static final Cell create(String text) {
            Cell cell = new Cell(text);
            return cell;
        }

        public void draw(Canvas canvas) {
            mCanvas = canvas;
            mCanvas.drawRoundRect(mRect, UIUtils.dip2px(8), UIUtils.dip2px(8), mBgPaint);
            Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
            int baseline = (int) ((mRect.bottom + mRect.top - fontMetrics.bottom - fontMetrics.top) / 2);
            mTextPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(mText, mRect.centerX(), baseline, mTextPaint);
        }
    }
}
