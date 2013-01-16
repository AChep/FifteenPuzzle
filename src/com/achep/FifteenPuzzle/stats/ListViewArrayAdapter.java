/*
 * Copyright (C) 2012 AChep@xda <artemchep@gmail.com>
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

import com.achep.FifteenPuzzle.R;
import com.achep.FifteenPuzzle.Utils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ListViewArrayAdapter extends ArrayAdapter<String> {
	private final Activity context;

	private final String[] mUsernames;
	private final String[] mTimes;
	private final String[] mSteps;

	public ListViewArrayAdapter(Activity context, String[] usernames,
			String[] times, String[] steps) {
		super(context, R.layout.list_view_adapter_stats, usernames);
		this.context = context;

		mUsernames = usernames;
		mTimes = times;
		mSteps = steps;
	}

	private static class ViewHolder {
		public TextView titleTextView;
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
			holder.summary1TextView = (TextView) rowView
					.findViewById(R.id.summary1);
			holder.summary2TextView = (TextView) rowView
					.findViewById(R.id.summary2);
			rowView.setTag(holder);
		} else {
			holder = (ViewHolder) rowView.getTag();
		}

		holder.titleTextView.setText(mUsernames[position]);
		holder.summary1TextView.setText(Utils.getFormatedTime(Integer
				.parseInt(mTimes[position])));
		holder.summary2TextView.setText(mSteps[position]);
		return rowView;
	}
}