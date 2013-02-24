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

package com.achep.FifteenPuzzle.utils;

public class Project {

	/**
	 * Official release build
	 */
	public static final boolean RELEASE_BUILD = false;

	/**
	 * Enable it to see a bit more info.
	 */
	public static final boolean DEBUG = RELEASE_BUILD ? !RELEASE_BUILD : false;

	/**
	 * Path to project's data folder
	 */
	public static final String DATA_FOLDER = "/Android/data/com.achep.FifteenPuzzles/";
}