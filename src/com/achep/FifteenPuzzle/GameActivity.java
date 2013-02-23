package com.achep.FifteenPuzzle;

import com.achep.FifteenPuzzle.GameView.ActivityInterface;
import com.achep.FifteenPuzzle.preferences.Settings;
import com.achep.FifteenPuzzle.stats.DBHelper;
import com.achep.FifteenPuzzle.updater.AsyncCheckVersion;
import com.achep.FifteenPuzzle.utils.Utils;
import com.achep.FifteenPuzzle.widget.ActionBar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GameActivity extends Activity implements ActivityInterface,
		OnClickListener {

	private GameView mGameView;

	private ActionBar mActionBar;
	private ImageView mShuffleButton;
	private ImageView mSettingsButton;
	private ProgressBar mProgressBar;

	private Handler mHandler;
	private boolean mDraw;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_game);

		mGameView = (GameView) findViewById(R.id.game_view);
		mGameView.setActivityInterface(this);

		mActionBar = (ActionBar) findViewById(R.id.action_bar);
		mActionBar.actionBarSetTitle(getTitle());

		mShuffleButton = mActionBar.actionBarInitAndAddImageButton(
				R.string.action_bar_new_game, R.drawable.ic_actionbar_refresh);
		mShuffleButton.setOnClickListener(this);

		mProgressBar = mActionBar.actionBarInitAndAddProgressBar();
		mProgressBar.setVisibility(View.GONE);

		mSettingsButton = mActionBar.actionBarInitAndAddImageButton(
				R.string.action_bar_settings, R.drawable.ic_actionbar_settings);
		mSettingsButton.setOnClickListener(this);

		mHandler = new Handler();
		new AsyncCheckVersion().execute(this);
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		mDraw = true;
		mHandler.post(new Runnable() {

			private long timeOld = 0;
			private String titleLabel = getResources().getString(
					R.string.game_time)
					+ ": ";

			@Override
			public void run() {
				if (!mDraw)
					return;

				long time = Utils.mathDiv(mGameView.getGameTimeMillis(), 1000);
				if (time != timeOld) {
					mActionBar.actionBarSetTitle(titleLabel
							+ Utils.timeGetFormatedTimeFromSeconds(time));
					timeOld = time;
				}

				mHandler.postDelayed(this, 400);
			}
		});
	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		mDraw = false;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		mGameView.recycleBitmaps();
	}

	@Override
	public void onClick(View v) {
		if (v.equals(mShuffleButton)) {
			new ShuffleChips().execute();
		} else if (v.equals(mSettingsButton)) {
			startActivity(new Intent(GameActivity.this, Settings.class)
					.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
		}
	}

	@Override
	public void onGameOver(final int steps, long timeMillis, final int length) {
		final int time = (int) Utils.mathDiv(timeMillis, 1000);
		final SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);

		RelativeLayout root = (RelativeLayout) LayoutInflater.from(this)
				.inflate(R.layout.popupwindow_gameover, null);

		TextView result = (TextView) root.findViewById(R.id.results);
		result.setText(this.getResources().getString(
				R.string.game_gameover_results,
				Utils.timeGetFormatedTimeFromSeconds(time), steps + ""));

		final String usernameSettings = sp.getString(
				Settings.Keys.PREF_USER_NAME,
				getResources().getString(R.string.settings_nickname_default));
		final EditText username = (EditText) root.findViewById(R.id.username);
		username.setText(usernameSettings);

		final PopupWindow pw = new PopupWindow(root, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT, true);
		pw.showAtLocation(mGameView, Gravity.CENTER, 0, 0);

		Button dismiss = (Button) root.findViewById(R.id.dismiss);
		dismiss.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				final String usernameCurrent = username.getText().toString();
				if (!usernameSettings.equals(usernameCurrent)) {
					SharedPreferences.Editor spe = sp.edit();
					spe.putString(Settings.Keys.PREF_USER_NAME, usernameCurrent);
					if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
						// Froyobread-
						spe.commit();
					} else {
						// Gingerbread+
						spe.apply();
					}
				}
				new Thread() {
					@Override
					public void run() {
						DBHelper.insert(GameActivity.this, usernameCurrent,
								length, time, steps);
					}
				}.run();

				// Exit
				pw.dismiss();
			}
		});
	}

	private class ShuffleChips extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mShuffleButton.setVisibility(View.GONE);
			mProgressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(Void... avoid) {
			mGameView.newGame();
			return null;
		}

		@Override
		protected void onPostExecute(Void str) {
			mGameView.postInvalidate();
			mShuffleButton.setVisibility(View.VISIBLE);
			mProgressBar.setVisibility(View.GONE);
		}
	}
}
