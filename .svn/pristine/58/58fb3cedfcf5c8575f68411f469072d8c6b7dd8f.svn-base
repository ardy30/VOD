package com.ppfuns.ui.view.player.type;


import android.widget.VideoView;

import com.ppfuns.model.entity.Play;
import com.ppfuns.ui.view.player.IMediaControl;
import com.ppfuns.ui.view.player.VideoViews;

/**
 * Created by zpf on 2016/7/19.
 */
public interface PlayerType {

    //前进
    void rewind(IMediaControl mediaController, VideoViews videoViews);


    //后退
    void forward(IMediaControl mediaController, VideoViews videoViews);


    void enter(IMediaControl.ControlCallback mMediaControl, VideoViews videoViews, Play url);


    void updatePlayProgress(VideoViews videoView);

    void updatePlayTime(VideoViews videoView);

    void updateSeekState(boolean isSeeking);

    void updateProgramInfo(Play play);


    void seekTo(VideoView videoView);

    void correctionscPlayTime();


    void customPlayProgress();


    void init();

}
