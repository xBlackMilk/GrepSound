package com.grepsound.fragments;

import android.app.Fragment;
import android.content.*;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.grepsound.R;
import com.grepsound.model.Track;
import com.grepsound.services.AudioService;
import com.grepsound.views.CircularSeekBar;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class PlayerFragment extends Fragment implements OnSeekBarChangeListener {

	private static final String TAG = PlayerFragment.class.getSimpleName();

	private BroadcastReceiver audioPlayerBroadcastReceiver = new AudioPlayerBroadCastReceiver();
	private AudioService audioPlayer;
	private Intent audioPlayerIntent;
	SharedPreferences prefs;
	private boolean isTracking;

	private Handler mHandler = new Handler();
	private Timer timer;
	private UpdaterTask updater;
	RelativeLayout mSliderButton;

    private Track trackPlaying;
    TextView songDuration, currentTime;
    ImageButton mPlayPauseButton;
    private CircularSeekBar mCircularSeekBar;


    public interface info {
		String POS_PLAYLIST = "pos_playlist";
		String PROGRESS = "new_progress";
	}

	private class UpdaterTask extends TimerTask {

		int duration;
		int current;
		Handler callback;

		public UpdaterTask(int dur, int cur, Handler c) {
			duration = dur;
			current = cur;
			callback = c;
		}

		@Override
		public void run() {

			if (current >= duration) {
				timer.cancel();
				callback.post(new Runnable() {

					@Override
					public void run() {

					}
				});

				return;
			}

			current += 1000;

			callback.post(new Runnable() {

				@Override
				public void run() {
                    Log.i(TAG, "Update");
				}
			});

		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		// bind to service

		audioPlayerBroadcastReceiver = new AudioPlayerBroadCastReceiver();
		// IntentFilter filter = new IntentFilter(AudioService.UPDATE_PLAYLIST);
		IntentFilter filter = new IntentFilter(AudioService.INFO_TRACK);
		getActivity().registerReceiver(audioPlayerBroadcastReceiver, filter);

	}

	public void playTrack(ArrayList<Track> playList, long album_id, int pos) {
		//audioPlayer.updatePlaylist(playList, album_id);

		Intent intent = new Intent(AudioService.commands.PLAY);
		intent.putExtra(PlayerFragment.info.POS_PLAYLIST, pos);
		getActivity().sendBroadcast(intent);
	}

	@Override
	public void onPause() {
		super.onPause();
		getActivity().unregisterReceiver(audioPlayerBroadcastReceiver);
		audioPlayerBroadcastReceiver = null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View rootView = inflater.inflate(R.layout.frag_player, null);

        mPlayPauseButton = (ImageButton) rootView.findViewById(R.id.play_pause);
        mCircularSeekBar = (CircularSeekBar) rootView.findViewById(R.id.progress);



        mPlayPauseButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent request = new Intent(AudioService.commands.RESUME);
                getActivity().sendBroadcast(request);
            }
        });

		return rootView;
	}

	private class AudioPlayerBroadCastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (timer != null) {
				timer.cancel();
				updater.cancel();
			}
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		Log.i(TAG, "onStartTrackingTouch");
		isTracking = true;
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		Log.i(TAG, "onStopTrackingTouch");
		isTracking = false;
		Intent intent = new Intent(AudioService.commands.SEEK_MOVED);
		intent.putExtra(info.PROGRESS, seekBar.getProgress());
		getActivity().sendBroadcast(intent);
	}

}
