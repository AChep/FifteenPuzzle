package com.achep.FifteenPuzzle;

import com.achep.FifteenPuzzle.GameLogic.OnPuzzleSolvedListener;
import com.achep.FifteenPuzzle.GameView.OnInitializedListener;
import com.achep.FifteenPuzzle.preferences.Settings;
import com.achep.FifteenPuzzle.stats.DBHelper;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

public class GameActivity extends Activity implements OnPuzzleSolvedListener {

	private GameView mGameView;
	private ImageView mShuffleButton;
	private ProgressBar mShuffleProgress;
	private TextView mTitleText;

	private Handler mHandler;
	private boolean mDraw;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_game);

		mGameView = (GameView) findViewById(R.id.game_view);
		mGameView.setOnInitializedListener(new OnInitializedListener() {

			@Override
			public void OnInitialized(GameLogic gameLogic) {
				gameLogic.setOnPuzzleSolvedListener(GameActivity.this);
			}
		});
		mTitleText = (TextView) findViewById(R.id.title);
		mShuffleButton = (ImageView) findViewById(R.id.shuffle);
		mShuffleButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				new ShuffleChips().execute("");
			}
		});
		mShuffleProgress = (ProgressBar) findViewById(R.id.progressbar);
		final ImageView settingsButton = (ImageView) findViewById(R.id.settings);
		settingsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(GameActivity.this, Settings.class)
						.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
			}
		});

		mHandler = new Handler();

	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		mDraw = true;
		mHandler.post(new Runnable() {

			private long timeOld = -1;

			@Override
			public void run() {
				if (!mDraw)
					return;

				if (mGameView.needsUpdate())
					mGameView.invalidate();
				long time = mGameView.getGameLogic().getTime();
				if (time != -1) {
					time = Utils.div(time, 1000);
					if (time != timeOld) {
						mTitleText.setText(getResources().getString(
								R.string.game_time)
								+ ": " + Utils.getFormatedTime(time));
						timeOld = time;
					}
				}

				mHandler.postDelayed(this, 15);
			}
		});
	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		mDraw = false;
	}

	@Override
	public void OnPuzzleSolved(int time, int steps, int length) {
		final LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout ll = (LinearLayout) inflater.inflate(
				R.layout.activity_game_congraz, null, false);

		final PopupWindow pw = new PopupWindow(ll, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT, true);
		pw.showAtLocation(mGameView, Gravity.CENTER, 0, 0);

		TextView rezultText = (TextView) ll.findViewById(R.id.rezult);
		rezultText.setText(getResources().getString(R.string.game_time) + ": "
				+ Utils.getFormatedTime(time) + "\n"
				+ getResources().getString(R.string.game_steps) + ": " + steps);
		ll.findViewById(R.id.dismiss).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				pw.dismiss();
			}
		});

		DBHelper dbOpenHelper = new DBHelper(GameActivity.this);
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		ContentValues cv = new ContentValues();

		final SharedPreferences dsprefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		String userName = dsprefs.getString(Settings.Keys.PREF_USER_NAME,
				getResources().getString(R.string.settings_nickname_default));

		cv.put(DBHelper.NICKNAME, userName);
		cv.put(DBHelper.LENGTH, length);
		cv.put(DBHelper.TIME, time);
		cv.put(DBHelper.STEPS, steps);

		db.insert(DBHelper.TABLE_NAME, null, cv);
		db.close();
	}

	private class ShuffleChips extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mShuffleButton.setVisibility(View.GONE);
			mShuffleProgress.setVisibility(View.VISIBLE);
			mGameView.setTouchAvailabel(false);
		}

		@Override
		protected String doInBackground(String... aurl) {
			mGameView.getGameLogic().start();
			return null;
		}

		@Override
		protected void onPostExecute(String str) {
			mShuffleButton.setVisibility(View.VISIBLE);
			mShuffleProgress.setVisibility(View.GONE);
			mGameView.setTouchAvailabel(true);
		}
	}

}