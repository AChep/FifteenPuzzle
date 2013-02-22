/*
 * Copyright (C) 2013 AChep@xda <artemchep@gmail.com>
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.achep.FifteenPuzzle.R;
import com.achep.FifteenPuzzle.utils.Utils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ListViewArrayAdapter extends ArrayAdapter<String> {

	private static final int TEXT_COLOR_HIGHLIGHTED = 0xff40dd20;

	private final Activity context;

	private final String[] mUsernames;
	private final String[] mDates;
	private final String[] mTimes;
	private final String[] mSteps;

	private final int mSortType;

	@SuppressWarnings("deprecation")
	public ListViewArrayAdapter(Activity context, String[] usernames,
			int[] times, int[] steps, int[] dates, int sort) {
		super(context, R.layout.list_view_adapter_stats, usernames);
		this.context = context;

		mUsernames = usernames;
		mSortType = sort;

		SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy, hh:mm",
				Locale.getDefault());
		sdf.setTimeZone(TimeZone.getDefault());

		mTimes = new String[times.length];
		mSteps = new String[times.length];
		mDates = new String[times.length];
		for (int i = 0; i < times.length; i++) {
			mTimes[i] = Utils.timeGetFormatedTimeFromSeconds(times[i]);
			mSteps[i] = Integer.toString(steps[i]);

			if (dates[i] != 0) {
				Date date = new Date(0);
				date.setMinutes(dates[i]);
				mDates[i] = sdf.format(date);
			}
		}
	}

	private static class ViewHolder {
		public TextView titleTextView;
		public TextView dateTextView;
		public TextView summary1TextView;
		public TextView summary2TextView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(R.layout.list_view_adapter_stats, null,
					true);
			holder = new ViewHolder();
			holder.titleTextView = (TextView) rowView.findViewById(R.id.title);
			holder.dateTextView = (TextView) rowView.findViewById(R.id.date);
			holder.summary1TextView = (TextView) rowView
					.findViewById(R.id.summary1);
			holder.summary2TextView = (TextView) rowView
					.findViewById(R.id.summary2);
			rowView.setTag(holder);
		} else {
			holder = (ViewHolder) rowView.getTag();
		}

		holder.titleTextView.setText((position + 1) + ". "
				+ mUsernames[position]);
		holder.summary1TextView.setText(mTimes[position]);
		holder.summary2TextView.setText(mSteps[position]);
		if (mDates[position] != null) {
			holder.dateTextView.setText(mDates[position]);
			holder.dateTextView.setVisibility(View.VISIBLE);
		} else
			holder.dateTextView.setVisibility(View.GONE);

		if (mSortType == StatsData.SORT_BY_DATE) {
			holder.dateTextView.setTextColor(TEXT_COLOR_HIGHLIGHTED);
		} else if (mSortType == StatsData.SORT_BY_TIME) {
			holder.summary1TextView.setTextColor(TEXT_COLOR_HIGHLIGHTED);
		} else if (mSortType == StatsData.SORT_BY_STEPS) {
			holder.summary2TextView.setTextColor(TEXT_COLOR_HIGHLIGHTED);
		}
		return rowView;
	}
}