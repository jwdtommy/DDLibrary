package com.dd.news.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by J.Tommy on 17/3/2.
 * 用draw的方式实现标签云，避免addView带来的性能问题，尤其是在列表滚动的过程中
 */
public class TagGroupView extends View {
    private ArrayList<Cell> cells = new ArrayList<>();
    String[] test = new String[]{"一", "二二", "三三三", "四四四四", "五五五五五","七七七七七七七七七七七"};

    public TagGroupView(Context context) {
        super(context);
        setTags(test);
    }

    public TagGroupView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTags(test);
    }

    public void setTags(String[] tags) {
        cells = new ArrayList<>();
        for (String tag : tags) {
            cells.add(Cell.create(tag));
        }
        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int cellStartX=0;
        int cellStartY=0;
        int lastCellstartX = 0;
        int lastCellstartY = 0;
        boolean needChangeLine = false;

        for (Cell cell : cells) {
            int cellWidth = cell.getMeasureWidth();
            int cellHeight = cell.getMeasureHeight();

            if (lastCellstartX + cellWidth > getWidth()) {
                needChangeLine = true;
            } else {
                needChangeLine = false;
            }

            if (needChangeLine) {
                lastCellstartX = 0;
                lastCellstartY += cellHeight;
            } else {
                lastCellstartX += cellWidth;
            }
            Rect rect = new Rect();
            rect.left = lastCellstartX;
            rect.right = lastCellstartX + cellWidth;
            rect.top = lastCellstartY;
            rect.bottom = lastCellstartY + cellHeight;
            canvas.save();
            canvas.clipRect(rect);
            cell.draw(canvas);
            canvas.restore();
        }
    }

    private static class Cell {
        public String mText;
        public int mTextColor = Color.parseColor("#FF666666");
        public int mTextSize = 40;
        public int mBackground = Color.parseColor("#11FF0000");

        private Canvas mCanvas;
        private TextPaint mTextPaint;
        private Rect mRect;

        private Cell(String text) {
            mText = text;
            mTextPaint = new TextPaint();
            mTextPaint.setTextSize(mTextSize);
            mTextPaint.setColor(mTextColor);
        }

        public int getMeasureWidth() {
            return (int) StaticLayout.getDesiredWidth(mText, mTextPaint);
        }

        public int getMeasureHeight() {
            Paint.FontMetrics fm = mTextPaint.getFontMetrics();
            return (int) Math.ceil(fm.descent - fm.ascent);
        }

        public static final Cell create(String text) {
            Cell cell = new Cell(text);
            return cell;
        }

        public void draw(Canvas canvas) {
            mCanvas = canvas;
            mCanvas.drawColor(mBackground);
            mCanvas.drawText(mText, 0, 0, mTextPaint);
        }
    }
}
