package com.gianlucadp.bakingapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gianlucadp.bakingapp.models.Step;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepViewHolder> {
    private Context context; // Not needed for now but implemented for possible future changes
    private List<Step> StepsList;
    private RecipeFragment masterFragment;
    private int lastViewedStep;


    public StepsAdapter(Context context, RecipeFragment master, List<Step> Steps) {
        this.context = context;
        this.StepsList = Steps;
        this.masterFragment = master;
    }

    @Override
    public int getItemCount() {
        if (StepsList == null) {
            return 0;
        } else {
            return StepsList.size();
        }
    }

    public void setStepsList(List<Step> Steps) {
        this.StepsList = Steps;
    }

    @Override
    public StepViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.recyclerview_step_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);

        return new StepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StepViewHolder holder, int position) {
        holder.loadStep(position);
    }


    public class StepViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_step_summary)
        TextView mTextViewStepSummary;
        @BindView(R.id.im_step_status)
        ImageView mStepStatus;

        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = getAdapterPosition();
                masterFragment.getCallback().onStepSelected(currentPosition);
            }
        };

        public StepViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(mOnClickListener);

        }

        public void loadStep(int position) {
            if (StepsList != null && StepsList.size() >= position) {
                Step currentStep = StepsList.get(position);
                mTextViewStepSummary.setText(currentStep.getShortDescription());
            }
            if (lastViewedStep == position) {
                mStepStatus.setImageResource(R.drawable.ic_play_arrow);
            } else {
                mStepStatus.setImageResource(R.drawable.ic_empty_circle);
            }
        }
    }

    public void setLastViewedStep(int position) {
        this.lastViewedStep = position;
    }
}

