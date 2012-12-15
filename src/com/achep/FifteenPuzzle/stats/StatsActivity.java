package com.achep.FifteenPuzzle.stats;

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
import android.widget.ProgressBar;
import android.widget.TextView;

public class StatsActivity extends Activity {

	private TextView[] mTableText = new TextView[3];
	private ImageView mClearButton;
	private ProgressBar mProgressBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stats);

		mTableText[0] = (TextView) findViewById(R.id.nick);
		mTableText[1] = (TextView) findViewById(R.id.time);
		mTableText[2] = (TextView) findViewById(R.id.steps);
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

		new LoadStats().execute("");
	}

	private class LoadStats extends AsyncTask<String, String, String[]> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String[] doInBackground(String... aurl) {
			final DBHelper dbHelper = new DBHelper(StatsActivity.this);
			final SQLiteDatabase db = dbHelper.getWritableDatabase();
			final SharedPreferences prefs = getSharedPreferences(
					"preferences2", 0);

			final int length = prefs.getInt(Settings.Keys.SPREF_PUZZLE_LENGTH,
					PrefPuzzleLengthPicker.DEFAULT);

			Cursor c = db.query(DBHelper.TABLE_NAME, null, DBHelper.LENGTH
					+ " = " + length, null, null, null, DBHelper.TIME);
			String[] str = null;
			if (c != null) {
				str = new String[] { "", "", "" };
				if (c.moveToFirst()) {
					do {
						int i = 0;
						for (String cn : c.getColumnNames()) {
							if (cn.equals(DBHelper.LENGTH)
									|| cn.equals(DBHelper.ID)) {
								continue;
							}
							if (i == 1) {
								str[i] = str[i].concat(Utils.getFormatedTime(c
										.getInt(c.getColumnIndex(cn))) + "\n");
							} else
								str[i] = str[i].concat(c.getString(c
										.getColumnIndex(cn)) + "\n");
							i++;
						}
					} while (c.moveToNext());
				}
				c.close();
			}

			db.close();
			return str;
		}

		@Override
		protected void onPostExecute(String[] str) {
			mProgressBar.setVisibility(View.GONE);
			mClearButton.setVisibility(View.VISIBLE);

			mTableText[0].setText(str[0]);
			mTableText[1].setText(str[1]);
			mTableText[2].setText(str[2]);
		}
	}
}
