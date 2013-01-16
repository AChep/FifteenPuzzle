package com.achep.FifteenPuzzle.stats;

import java.util.ArrayList;

import com.achep.FifteenPuzzle.R;
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
	private ProgressBar mProgressBar;

	private ListView mListView;
	private String[][] mDatabase;

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
				if (mDatabase == null)
					return;

				mListView.setAdapter(new ListViewArrayAdapter(
						StatsActivity.this, mDatabase[0], mDatabase[1],
						mDatabase[2]));
			}
		});
		mStepsSortTextView = (TextView) findViewById(R.id.steps_sort);
		mStepsSortTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mDatabase == null)
					return;

				mListView.setAdapter(new ListViewArrayAdapter(
						StatsActivity.this, mDatabase[0], mDatabase[1],
						mDatabase[2]));
			}
		});
		mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
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

	private class LoadDatabaseStats extends AsyncTask<Void, Void, String[][]> {

		@Override
		protected String[][] doInBackground(Void... a) {
			SharedPreferences prefs = getSharedPreferences("preferences2", 0);
			int length = prefs.getInt(Settings.Keys.SPREF_PUZZLE_LENGTH,
					PrefPuzzleLengthPicker.DEFAULT);

			DBHelper dbHelper = new DBHelper(StatsActivity.this);
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			Cursor c = db.query(DBHelper.TABLE_NAME, null, DBHelper.LENGTH
					+ " = " + length, null, null, null, DBHelper.TIME);

			ArrayList<String>[] database = new ArrayList[3];
			if (c != null) {
				database[0] = new ArrayList<String>();
				database[1] = new ArrayList<String>();
				database[2] = new ArrayList<String>();
				if (c.moveToFirst()) {
					do {
						for (String cn : c.getColumnNames()) {
							if (cn.equals(DBHelper.NICKNAME)) {
								database[0].add(c.getString(c
										.getColumnIndex(cn)));
							} else if (cn.equals(DBHelper.TIME)) {
								database[1].add(c.getString(c
										.getColumnIndex(cn)));
							} else if (cn.equals(DBHelper.STEPS)) {
								database[2].add(c.getString(c
										.getColumnIndex(cn)));
							}
						}
					} while (c.moveToNext());
				}
				c.close();
			}
			db.close();

			String[][] databaseStr = new String[3][];
			databaseStr[0] = database[0]
					.toArray(new String[database[0].size()]);
			databaseStr[1] = database[1]
					.toArray(new String[database[1].size()]);
			databaseStr[2] = database[2]
					.toArray(new String[database[2].size()]);

			return databaseStr;
		}

		@Override
		protected void onPostExecute(String[][] str) {
			mProgressBar.setVisibility(View.GONE);
			mClearButton.setVisibility(View.VISIBLE);

			mDatabase = str;
			mListView.setAdapter(new ListViewArrayAdapter(StatsActivity.this,
					mDatabase[0], mDatabase[1], mDatabase[2]));
		}
	}

}
