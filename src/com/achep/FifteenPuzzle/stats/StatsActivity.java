/*
 * Copyright (C) 2012-2013 AChep@xda <artemchep@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.achep.FifteenPuzzle.stats;

import java.util.ArrayList;

import com.achep.FifteenPuzzle.R;
import com.achep.FifteenPuzzle.Utils;
import com.achep.FifteenPuzzle.preferences.PrefPuzzleLengthPicker;
import com.achep.FifteenPuzzle.preferences.Settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class StatsActivity extends Activity {

	// Action bar
	private ImageView mClearButton;
	private ImageView mGraphButton;
	private ProgressBar mProgressBar;

	private ListView mListView;
	private StatsData mStatsData;

	private TextView mTimeSortTextView;
	private TextView mStepsSortTextView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stats);

		mListView = (ListView) findViewById(R.id.list_view);
		mTimeSortTextView = (TextView) findViewById(R.id.time_sort);
		mTimeSortTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mStatsData == null)
					return;

				mStatsData.sort(StatsData.SORT_BY_TIME);
				setListViewAdapter(StatsData.SORT_BY_TIME);
			}
		});
		mStepsSortTextView = (TextView) findViewById(R.id.steps_sort);
		mStepsSortTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mStatsData == null)
					return;

				mStatsData.sort(StatsData.SORT_BY_STEPS);
				setListViewAdapter(StatsData.SORT_BY_STEPS);
			}
		});
		mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
		mGraphButton = (ImageView) findViewById(R.id.graph);
		mGraphButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				GraphsView gv = new GraphsView(StatsActivity.this);

				gv.addPoints(mStatsData.getTimeSecs(), 0xffff8020);
				gv.addPoints(mStatsData.getSteps(), 0xffc060ff);

				new AlertDialog.Builder(StatsActivity.this)
						.setIcon(android.R.drawable.ic_dialog_info)
						.setTitle(R.string.stats_graph_title)
						.setView(gv)
						.setNegativeButton(android.R.string.cancel, null)
						.show();
			}
		});
		mClearButton = (ImageView) findViewById(R.id.clear);
		mClearButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Build confirm dialog
				new AlertDialog.Builder(StatsActivity.this)
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setTitle(R.string.settings_statistic_drop_title)
						.setMessage(R.string.settings_statistic_drop_message)
						.setPositiveButton(android.R.string.ok,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// Drop the database
										final DBHelper dbHelper = new DBHelper(
												StatsActivity.this);
										final SQLiteDatabase db = dbHelper
												.getWritableDatabase();
										DBHelper.dropTable(db);
										db.close();

										// Finish activity
										StatsActivity.this.finish();
									}

								})
						.setNegativeButton(android.R.string.cancel, null)
						.show();
			}
		});
		// Set back button pop-up pattern
		findViewById(R.id.back).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(StatsActivity.this, Settings.class));
				finish();
			}
		});

		new LoadDatabaseStats().execute();
	}

	private void setListViewAdapter(int sort) {
		mListView.setAdapter(new ListViewArrayAdapter(this, mStatsData
				.getUsernames(), mStatsData.getTimeSecs(), mStatsData
				.getSteps(), sort));
	}

	private class LoadDatabaseStats extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... void_) {
			SharedPreferences prefs = getSharedPreferences("preferences2", 0);
			int length = prefs.getInt(Settings.Keys.SPREF_PUZZLE_LENGTH,
					PrefPuzzleLengthPicker.DEFAULT);

			DBHelper dbHelper = new DBHelper(StatsActivity.this);
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			Cursor c = db.query(DBHelper.TABLE_NAME, null, DBHelper.LENGTH
					+ " = " + length, null, null, null, DBHelper.TIME);

			ArrayList<String> nicknames = new ArrayList<String>();
			ArrayList<Integer> times = new ArrayList<Integer>();
			ArrayList<Integer> steps = new ArrayList<Integer>();
			if (c != null) {
				if (c.moveToFirst()) {
					do {
						for (String cn : c.getColumnNames()) {
							if (cn.equals(DBHelper.NICKNAME)) {
								nicknames
										.add(c.getString(c.getColumnIndex(cn)));
							} else if (cn.equals(DBHelper.TIME)) {
								times.add(c.getInt(c.getColumnIndex(cn)));
							} else if (cn.equals(DBHelper.STEPS)) {
								steps.add(c.getInt(c.getColumnIndex(cn)));
							}
						}
					} while (c.moveToNext());
				}
				c.close();
			}
			db.close();

			length = nicknames.size();
			int[] timesInt = new int[length];
			int[] stepsInt = new int[length];
			for (int i = 0; i < length; i++) {
				timesInt[i] = times.get(i);
				stepsInt[i] = steps.get(i);
			}
			mStatsData = new StatsData(nicknames.toArray(new String[length]),
					timesInt, stepsInt);
			return null;
		}

		@Override
		protected void onPostExecute(Void void_) {
			mProgressBar.setVisibility(View.GONE);
			mClearButton.setVisibility(View.VISIBLE);
			mGraphButton.setVisibility(View.VISIBLE);

			setListViewAdapter(StatsData.SORT_BY_TIME);
		}
	}

}

class StatsData {

	public static final int SORT_BY_TIME = 0;
	public static final int SORT_BY_STEPS = 1;

	public static final int USER_INFO_PLAYED_GAMES = 0;
	public static final int USER_INFO_TOTAL_TIME = 1;
	public static final int USER_INFO_TOTAL_STEPS = 2;

	private String[] mUsernames;
	private int[] mTimeSecs;
	private int[] mSteps;

	public StatsData(String[] usernames, int[] timeSecs, int[] steps) {
		mUsernames = usernames;
		mTimeSecs = timeSecs;
		mSteps = steps;
	}

	public void sort(int type) {
		int[] changes;
		if (type == SORT_BY_TIME) {
			changes = Utils.sort(mTimeSecs);

			String[] usernames = mUsernames.clone();
			int[] steps = mSteps.clone();
			for (int i = 0; i < changes.length; i++) {
				mUsernames[i] = usernames[changes[i]];
				mSteps[i] = steps[changes[i]];
			}
		} else if (type == SORT_BY_STEPS) {
			changes = Utils.sort(mSteps);

			String[] usernames = mUsernames.clone();
			int[] time = mTimeSecs.clone();
			for (int i = 0; i < changes.length; i++) {
				mUsernames[i] = usernames[changes[i]];
				mTimeSecs[i] = time[changes[i]];
			}
		}
	}

	public int[] getUserInfo(String username) {
		int playedGames = 0;
		int totalTime = 0;
		int totalSteps = 0;

		int usernameLength = username.length();
		for (int i = 0; i < mUsernames.length; i++) {
			if (usernameLength != mUsernames[i].length()
					|| !username.equals(mUsernames[i]))
				continue;

			playedGames++;
			totalTime += mTimeSecs[i];
			totalSteps += mSteps[i];
		}

		int[] userinfo = new int[3];
		userinfo[USER_INFO_PLAYED_GAMES] = playedGames;
		userinfo[USER_INFO_TOTAL_TIME] = totalTime;
		userinfo[USER_INFO_TOTAL_STEPS] = totalSteps;
		return userinfo;
	}

	public String[] getUsernames() {
		return mUsernames;
	}

	public int[] getTimeSecs() {
		return mTimeSecs;
	}

	public int[] getSteps() {
		return mSteps;
	}
}
