package com.gianlucadp.bakingapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gianlucadp.bakingapp.models.Step;
import com.gianlucadp.bakingapp.utils.Constants;
import com.gianlucadp.bakingapp.utils.NetworkUtils;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class StepFragment extends Fragment {
    @BindView(R.id.tv_step_description)
    TextView mStepDescription;
    @BindView(R.id.iv_step_image)
    ImageView mStepImage;
    SimpleExoPlayer mExoPlayer;
    @BindView(R.id.video_player)
    SimpleExoPlayerView mPlayerView;
    private Step mStep;
    private Unbinder unbinder;
    OnStepSwipeListener mCallback;
    FloatingActionButton mBtnGoNext;
    FloatingActionButton mBtnGoPrevious;

    public StepFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            mCallback = (OnStepSwipeListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnStepSwipeListener");
        }
    }

    public interface OnStepSwipeListener {
        void OnStepSwipe(Constants.DIRECTION direction);
        void OnNoInternetAvailable();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_step_details, container, false);

        // Add a listener that can detect swipes, to change current step
        final GestureDetector gesture = new GestureDetector(getActivity(),
                new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onDown(MotionEvent e) {
                        return true;
                    }

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                           float velocityY) {
                        final int SWIPE_MIN_DISTANCE = 120;
                        final int SWIPE_MAX_OFF_PATH = 250;
                        final int SWIPE_THRESHOLD_VELOCITY = 200;
                        try {
                            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                                return false;
                            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                                getCallback().OnStepSwipe(Constants.DIRECTION.LEFT_TO_RIGHT);

                            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                                getCallback().OnStepSwipe(Constants.DIRECTION.RIGHT_TO_LEFT);

                            }
                        } catch (Exception e) {
                            // nothing
                        }
                        return super.onFling(e1, e2, velocityX, velocityY);
                    }
                });

        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gesture.onTouchEvent(event);
            }
        });



        //If screen is in landscape, add buttons to navigate
        if (rootView.findViewById(R.id.btn_go_next) != null){
            mBtnGoNext = rootView.findViewById(R.id.btn_go_next);
            mBtnGoNext.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    getCallback().OnStepSwipe(Constants.DIRECTION.LEFT_TO_RIGHT);
                }
            });
            mBtnGoPrevious = rootView.findViewById(R.id.btn_go_previous);
            mBtnGoPrevious.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    getCallback().OnStepSwipe(Constants.DIRECTION.RIGHT_TO_LEFT);
                }
            });
        }

        ButterKnife.bind(this, rootView);
        unbinder = ButterKnife.bind(this, rootView);
        // If a previous instance is present, load the step
        if (savedInstanceState != null) {
            mStep = savedInstanceState.getParcelable(Constants.INTENT_STEP);

        }
        String description = "";
        if (mStep.getDescription()!=null){
            // To correct an invalid value in JSON
            description = mStep.getDescription().replace("�", "°");
        }
        mStepDescription.setText(description);
        //If there is a video (check also in thumbnail if present)

        if (NetworkUtils.checkConnection(getContext())) {
            if (mStep.getVideoURL() != null && !mStep.getVideoURL().isEmpty()) {
                mStepImage.setVisibility(View.GONE);
                initializePlayer(Uri.parse(mStep.getVideoURL()), savedInstanceState);
            } else {
                mPlayerView.setVisibility(View.GONE);
            }
            if (mStep.getThumbnailURL() != null && !mStep.getThumbnailURL().isEmpty() && !mStep.getThumbnailURL().endsWith("mp4")) {
                mStepImage.setVisibility(View.VISIBLE);
                Picasso.with(getContext()).load(mStep.getThumbnailURL()).into(mStepImage);
            } else {
                mStepImage.setVisibility(View.GONE);
            }
        }   else
        {
            getCallback().OnNoInternetAvailable();
        }


        return rootView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mExoPlayer != null) {
            mExoPlayer.release();
            mExoPlayer.setPlayWhenReady(false);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mExoPlayer != null) {
            mExoPlayer.release();
            mExoPlayer.setPlayWhenReady(false);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle currentState) {
        currentState.putParcelable(Constants.INTENT_STEP, mStep);
        if (mExoPlayer != null) {
            currentState.putLong(Constants.VIDEO_TIME, mExoPlayer.getCurrentPosition());

            currentState.putBoolean(Constants.VIDEO_STATUS, mExoPlayer.getPlayWhenReady());
        }

    }


    public void setStep(Step step) {
        mStep = step;
    }


    public void initializePlayer(Uri uri, Bundle savedInstanceState) {

        if (mExoPlayer == null) {
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(getContext(), "BakingApp");
            MediaSource mediaSource = new ExtractorMediaSource(uri, new DefaultDataSourceFactory(
                    getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            if (savedInstanceState != null) {
                mExoPlayer.seekTo(savedInstanceState.getLong(Constants.VIDEO_TIME));
                mExoPlayer.setPlayWhenReady(savedInstanceState.getBoolean(Constants.VIDEO_STATUS));
            }else{
                mExoPlayer.setPlayWhenReady(true);
            }

        }
    }

    public OnStepSwipeListener getCallback() {
        return mCallback;
    }
}
