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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	private static final int DB_VERSION = 2;
	private static final String DB_NAME = "data";

	public static final String TABLE_NAME = "statistic";
	public static final String ID = "_id";
	public static final String NICKNAME = "nick";
	public static final String LENGTH = "length";
	public static final String TIME = "time";
	public static final String STEPS = "steps";
	public static final String DATE_MINS = "date";
	
	private static final String CREATE_TABLE = "create table " + TABLE_NAME
			+ " ( " + ID + " integer primary key autoincrement, " + NICKNAME
			+ " TEXT, " + LENGTH + " INTEGER, " + TIME + " INTEGER, " + STEPS
			+ " INTEGER, " + DATE_MINS + " INTEGER)";

	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		sqLiteDatabase.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
		if (i == 1) {
			sqLiteDatabase.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN "
					+ DATE_MINS + " INTEGER DEFAULT 0;");
		}
	}

	public static void dropTable(SQLiteDatabase sqLiteDatabase) {
		sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		sqLiteDatabase.execSQL(CREATE_TABLE);
	}
}