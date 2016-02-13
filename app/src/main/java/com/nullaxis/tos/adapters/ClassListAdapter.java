package com.nullaxis.tos.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nullaxis.tos.R;
import com.nullaxis.tos.models.ClassProgression;
import com.nullaxis.tos.models._Class;
import com.nullaxis.tos.helper.ItemTouchHelperAdapter;
import com.nullaxis.tos.helper.ItemTouchHelperViewHolder;
import com.nullaxis.tos.helper.OnStartDragListener;

public class ClassListAdapter extends RecyclerView.Adapter<ClassListAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {

    private static final String LOG_TAG = "ClassListAdapter";

    private ClassProgression progression;
    private Context mContext;
    private final OnStartDragListener mDragStartListener;

    public ClassListAdapter(Context mContext, OnStartDragListener mDragStartListener, ClassProgression progression) {
        this.mContext = mContext;
        this.mDragStartListener = mDragStartListener;
        this.progression = progression;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_class, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        _Class _class = progression.getProgressionClass(position);

        holder.className.setText(_class.getName());
        holder.classIcon.setImageResource(_class.getIcon_resource_id());

        // Add Rank Stars
        holder.classStars.removeAllViews();
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20,
                mContext.getResources().getDisplayMetrics());
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20,
                mContext.getResources().getDisplayMetrics());
        int i = 0;
        int classCircle = progression.getClassCircleByPosition(position);
        while (i < 3){
            ImageView star = new ImageView(mContext);
            if (i < classCircle){
                star.setImageResource(R.drawable.ic_action_star);
            } else {
                star.setImageResource(R.drawable.ic_action_star_empty);
            }
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
            star.setLayoutParams(layoutParams);
            holder.classStars.addView(star);
            i++;
        }

        // Start a drag whenever the handle view it touched
        holder.handleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });
    }

    @Override
    public void onItemDismiss(int position) {
        progression.removeProgressionClass(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        progression.switchClassPositions(fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onClearView() {
        Log.d(LOG_TAG, "Calling onClearView");
        progression.notifyUpdate();
    }

    @Override
    public int getItemCount() {
        return progression.getProgressionSize();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {

        public final TextView className;
        public final ImageView classIcon;
        public final ImageView handleView;
        public final LinearLayout classStars;

        public ItemViewHolder(View itemView) {
            super(itemView);
            className = (TextView) itemView.findViewById(R.id.className);
            classIcon = (ImageView) itemView.findViewById(R.id.classIcon);
            handleView = (ImageView) itemView.findViewById(R.id.handle);
            classStars = (LinearLayout) itemView.findViewById(R.id.starsContainer);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }
}