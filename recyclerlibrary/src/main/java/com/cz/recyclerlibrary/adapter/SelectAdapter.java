package com.cz.recyclerlibrary.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.cz.recyclerlibrary.PullToRefreshRecyclerView;
import com.cz.recyclerlibrary.callback.Selectable;

import java.util.ArrayList;
import java.util.List;

import kotlin.ranges.IntRange;

/**
 * Created by cz on 4/3/16.
 * 一个可设置选择模式的数据乱配器
 */
public class SelectAdapter extends RefreshAdapter {
    public static final int MAX_COUNT=Integer.MAX_VALUE;
    public static final int INVALID_POSITION=-1;
    // 三种选择状态
    public static final int CLICK = 0;//单击
    public static final int SINGLE_SELECT = 1;//单选
    public static final int MULTI_SELECT = 2;//多选
    public static final int RECTANGLE_SELECT = 3;//块选择
    private final ArrayList<Integer> multiSelectItems;//选中集
    private PullToRefreshRecyclerView.OnSingleSelectListener singleSelectListener;
    private PullToRefreshRecyclerView.OnMultiSelectListener multiSelectListener;
    private PullToRefreshRecyclerView.OnRectangleSelectListener rectangleSelectListener;
    private int selectPosition;// 选中位置
    private int selectMaxCount;
    private int start, end;//截选范围
    private int mode;//选择模式

    public SelectAdapter(RecyclerView.Adapter adapter) {
        super(adapter);
        selectPosition=-1;
        multiSelectItems = new ArrayList<>();
    }

    /*
     * 设置选择模式
     *
     * @param mode
     */
    public void setSelectMode(int mode) {
        int headersCount = getHeaderViewCount();
        switch (this.mode) {
            case SINGLE_SELECT:
                //清除单选择状态
                int lastSelectPosition=selectPosition;
                selectPosition = -1;
                if(INVALID_POSITION!=lastSelectPosition){
                    notifyItemChanged(lastSelectPosition + headersCount);
                }
                break;
            case MULTI_SELECT:
                List<Integer> lastItems=new ArrayList<>(multiSelectItems);
                multiSelectItems.clear();
                for (Integer position : lastItems) {
                    notifyItemChanged(position+headersCount);
                }
                break;
            case RECTANGLE_SELECT:
                int s = Math.min(start, end) + headersCount, e = Math.max(start, end) + headersCount;
                start = end = 0;
                notifyItemRangeChanged(s, e - s+1);
                break;
        }
        this.mode = mode;
    }

    public void setSelectMaxCount(int count) {
        this.selectMaxCount=count;
    }

    public void setSingleSelectPosition(int position){
        this.selectPosition=position;
        int lastPosition=selectPosition;
        if(0<=position&&position<getItemCount()){
            //如果在正常范围内
            notifyItemChanged(position);
        } else {
            //删掉上一个选择的条目
            notifyItemChanged(lastPosition);
        }
    }

    public int getSingleSelectPosition(){
        return this.selectPosition;
    }


    public void setMultiSelectItems(List<Integer> items){
        multiSelectItems.clear();
        multiSelectItems.addAll(items);
        notifyDataSetChanged();
    }

    public List<Integer> getMultiSelectItems(){
        return multiSelectItems;
    }

    public void setRectangleSelectPosition(int start,int end){
        this.start=start;
        this.end=end;
        notifyItemRangeChanged(start,end-start);
    }

    public IntRange getRectangleSelectPosition(){
        return new IntRange(start,end);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        int headersCount = getHeaderViewCount();
        int footerViewCount = getFooterViewCount();
        switch (mode) {
            case SINGLE_SELECT:
                selectPosition(holder,position,headersCount,footerViewCount,selectPosition + headersCount == position);
                break;
            case MULTI_SELECT:
                selectPosition(holder,position,headersCount,footerViewCount,multiSelectItems.contains(position-headersCount));
                break;
            case RECTANGLE_SELECT:
                int s = Math.min(start + headersCount, end + headersCount);
                int e = Math.max(start + headersCount, end + headersCount);
                selectPosition(holder,position,headersCount,footerViewCount,s <= position && e >= position);
                break;
            default:
                selectPosition(holder,position,headersCount,footerViewCount,false);
        }
    }

    private void selectPosition(RecyclerView.ViewHolder holder,int position,int headerCount,int footerCount,boolean select){
        int itemCount = getItemCount();
        if(null!=adapter&&adapter instanceof Selectable&&position>=headerCount&&position<itemCount-footerCount){
            Selectable selectable=(Selectable)adapter;
            selectable.onSelectItem(holder,position-headerCount,select);
        }
    }


    @Override
    protected boolean onItemClick(View v, int position) {
        super.onItemClick(v, position);
        int headersCount = getHeaderViewCount();
        switch (mode) {
            case MULTI_SELECT:
                int lastSize=multiSelectItems.size();
                selectPosition = start = end = -1;
                if (multiSelectItems.contains(position)) {
                    lastSize--;
                    multiSelectItems.remove(Integer.valueOf(position));
                    notifyItemChanged(position + headersCount);
                } else if(multiSelectItems.size()<selectMaxCount){
                    multiSelectItems.add(Integer.valueOf(position));
                    notifyItemChanged(position + headersCount);
                }
                if (null != multiSelectListener) {
                    multiSelectListener.onMultiSelect(v, multiSelectItems,lastSize,selectMaxCount);
                }
                break;
            case RECTANGLE_SELECT:
                if (-1 != start && -1 != end) {
                    int s = start, e = end;
                    start = end = -1;//重置
                    notifyItemRangeChanged(Math.min(s + headersCount, e + headersCount), Math.max(s + headersCount, e + headersCount));
                } else if (-1 == start) {
                    start = position;
                    notifyItemChanged(start + headersCount);
                } else if (-1 == end) {
                    end = position;
                    if (null != rectangleSelectListener) {
                        rectangleSelectListener.onRectangleSelect(start, end);
                    }
                    notifyItemRangeChanged(Math.min(start + headersCount, end + headersCount), Math.max(start + headersCount, end + headersCount));
                }
                break;
            case SINGLE_SELECT:
                start = end = -1;
                multiSelectItems.clear();
                int last=selectPosition;
                selectPosition = position;
                if (null != singleSelectListener) {
                    singleSelectListener.onSingleSelect(v, position, last);
                }
                if(0<=selectPosition&&INVALID_POSITION!=last){
                    notifyItemChanged(last + headersCount);//通知上一个取消
                }
                notifyItemChanged(position + headersCount);//本次选中
                break;
        }
        return CLICK==mode;
    }

    /*
     * 设置单选选择监听
     *
     * @param singleSelectListener
     */
    public void setOnSingleSelectListener(PullToRefreshRecyclerView.OnSingleSelectListener singleSelectListener) {
        this.singleSelectListener = singleSelectListener;
    }

    /*
    * 设置多选选择监听
    *
    * @param singleSelectListener
    */
    public void setOnMultiSelectListener(PullToRefreshRecyclerView.OnMultiSelectListener multiSelectListener) {
        this.multiSelectListener = multiSelectListener;
    }

    /*
    * 设置截取选择监听
    *
    * @param singleSelectListener
    */
    public void setOnRectangleSelectListener(PullToRefreshRecyclerView.OnRectangleSelectListener rectangleSelectListener) {
        this.rectangleSelectListener = rectangleSelectListener;
    }



}
