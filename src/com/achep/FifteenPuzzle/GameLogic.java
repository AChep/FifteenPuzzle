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

package com.achep.FifteenPuzzle;

import java.util.Random;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.SystemClock;

public class GameLogic {

	public static final int X = 0;
	public static final int Y = 1;

	public static final int ORIENTATION = 0;
	public static final int CHIP = 1;
	public static final int MIN = 2;
	public static final int MAX = 3;

	public static final int ORIENTATION_VERTICAL = 1;
	public static final int ORIENTATION_HORIZONTAL = 0;
	public static final int ORIENTATION_FAIL = -1;

	public static final int TIME_NULL = -1;

	private OnPuzzleSolvedListener mOnPuzzleSolvedListener;

	private final int mLength;
	private final int mChipSize;
	private final int mSpaceId;

	private int[] mTrack;
	private Chip[] mChips;
	private long mStartTime = TIME_NULL;
	private int mSteps = 0;

	public GameLogic(int chipSize, int length) {
		mLength = length;
		mChipSize = chipSize;
		mTrack = new int[mLength * mLength]; // Create array of *shapes-id*
												// positions
		mChips = new Chip[mTrack.length - 1];

		// Fill array
		Bitmap chipBaseBitmap = PuzzleFactory
				.createChipBaseBitmap(getChipSize());
		for (int i = 0; i < mChips.length; i++) {
			mTrack[i] = i;

			// Init our chips
			final Bitmap bitmap = PuzzleFactory.createChipBitmap(i + 1,
					chipBaseBitmap);
			final int[] coordsReal = Coords.convertLineToReal(new int[] { i },
					getLength(), getChipSize());
			mChips[i] = new Chip(bitmap, coordsReal);
		}
		chipBaseBitmap.recycle();
		// Space identificator
		mSpaceId = mChips.length;
		mTrack[mSpaceId] = mSpaceId;
	}

	public int getLength() {
		return mLength;
	}

	public int getChipSize() {
		return mChipSize;
	}

	public int getChipsLength() {
		return mChips.length;
	}

	public Chip getChip(int id) {
		return mChips[id];
	}

	public int getChipId(int linePos) {
		return mTrack[linePos];
	}

	public boolean isSolved() {
		int length = mTrack.length - 3;
		for (int i = 0; i < length; i++) {
			if (mTrack[i] != i)
				return false;
		}
		return mTrack[mTrack.length - 1] == mSpaceId;
	}

	public void completeMoves(int[] movesData) {
		boolean theSame = true;

		// We're should detect space chip by false here
		boolean[] changed = new boolean[mLength];
		// Get const coord id from line type
		int orientationInversed = movesData[ORIENTATION] == 1 ? 0 : 1;
		// Const coordinate of line
		int xyconst = -1;
		// Space isn't just chip
		for (int i = 1; i < mLength; i++) {
			// Get chips id from moves data
			int chipId = movesData[i + MAX];
			// Get chip real coords
			final int[] coordsRealRounded = Coords.roundReal(getChip(chipId)
					.getCoords(), getLength(), getChipSize());
			final int[] coords2d = Coords.convertRealTo2d(coordsRealRounded,
					getChipSize());
			final int coordsLine = Coords
					.convert2dToLine(coords2d, getLength())[0];

			changed[coords2d[movesData[ORIENTATION]]] = true; // not space
			if (mTrack[coordsLine] != chipId)
				theSame = false;
			mTrack[coordsLine] = chipId; // track it
			continueScroll(chipId, coordsRealRounded);
			// getChip(chipId).setCoords(coordsRealRounded); // set coords
			xyconst = coords2d[orientationInversed]; // set const xy
		}
		for (int i = 0; i < mLength; i++) {
			// Stop if it's not space chip
			if (changed[i])
				continue;
			// It's space just here!

			int[] coords2d;
			if (movesData[ORIENTATION] == ORIENTATION_VERTICAL) {
				coords2d = new int[] { xyconst, i };
			} else {
				coords2d = new int[] { i, xyconst };
			}
			final int coordsLine = Coords
					.convert2dToLine(coords2d, getLength())[0];
			mTrack[coordsLine] = mSpaceId;
			break;
		}
		if (!theSame) {
			mSteps++;
			if (mStartTime != TIME_NULL && isSolved()) {
				if (mOnPuzzleSolvedListener != null)
					mOnPuzzleSolvedListener.OnPuzzleSolved(
							(int) Utils.div(getTime(), 1000), mSteps,
							getLength());
				mStartTime = TIME_NULL;
			}
		}
	}

	private void continueScroll(final int id, final int[] coordsEnd) {
		final int[] coordsStart = getChip(id).getCoords().clone();
		final int[] deltaCoords = new int[] { coordsEnd[0] - coordsStart[0],
				coordsEnd[1] - coordsStart[1] };

		float duration0 = 100;
		int mode0;
		if (deltaCoords[0] != 0 && deltaCoords[1] == 0) {
			// HORIZONTAL LINE
			mode0 = 0;
		} else if (deltaCoords[0] == 0 && deltaCoords[1] != 0) {
			// VERTICAL LINE
			mode0 = 1;
		} else
			return;
		duration0 *= (float) Math.abs(deltaCoords[mode0]) * 2 / getChipSize();

		final int duration = Math.round(duration0);
		final int mode = mode0;
		final long endTime = SystemClock.uptimeMillis() + duration;

		final Handler handler = new Handler();
		Runnable runnable = new Runnable() {

			private int mOldCoord = coordsStart[mode];

			@Override
			public void run() {
				if (getChip(id).getCoords()[mode] != mOldCoord) {
					return; // Stop animation if you're pressed this chip
				}

				long deltaTime = endTime - SystemClock.uptimeMillis();
				if (deltaTime > 0) {
					float progress = (float) (duration - deltaTime) / duration;
					mOldCoord = coordsStart[mode]
							+ Math.round(deltaCoords[mode] * progress);
					getChip(id).setCoords(mOldCoord, mode);

					handler.postDelayed(this, 10);
				} else
					getChip(id).setCoords(coordsEnd);
			}
		};
		handler.post(runnable);
	}

	/**
	 * Returns specific moves data Moves data (as constants here): 0 -
	 * ORIENTATION 1 - CHIP 2 - MIN 3 - MAX 4*- chips on line
	 */
	public int[] getAvailableMoves(int chipLinePos) {
		// Moves data
		int[] data = new int[MAX + mLength];

		int[] space2dPos = Coords.convertLineTo2d(
				Utils.findElement(mTrack, new int[] { mSpaceId }), getLength());
		int[] chip2dPos = Coords.convertLineTo2d(new int[] { chipLinePos },
				getLength());
		// BEGIN

		int xyconst = chip2dPos[X];
		if (xyconst == space2dPos[X]) {
			if (chip2dPos[Y] != space2dPos[Y])
				data[ORIENTATION] = ORIENTATION_VERTICAL;
			else
				return null;
		} else {
			xyconst = chip2dPos[Y];
			if (xyconst == space2dPos[Y])
				data[ORIENTATION] = ORIENTATION_HORIZONTAL;
			else
				return null;

		}
		
		data[CHIP] = mTrack[chipLinePos];
		
		int[] dataRange = new int[2];
		if (space2dPos[data[ORIENTATION]] - chip2dPos[data[ORIENTATION]] > 0) {
			dataRange[0] = chip2dPos[data[ORIENTATION]];
			dataRange[1] = dataRange[0] + 1;
		} else { // We can move it to -1
			dataRange[1] = chip2dPos[data[ORIENTATION]];
			dataRange[0] = dataRange[1] - 1;
		}
		int[] dataRangeReal = Coords.convert2dToReal(dataRange, getChipSize());
		data[MIN] = dataRangeReal[0];
		data[MAX] = dataRangeReal[1];

		int length = 0;
		for (int i = 0; i < mLength; i++) {
			int linePos;
			if (data[ORIENTATION] == ORIENTATION_VERTICAL) {
				linePos = Coords.convert2dToLine(new int[] { xyconst, i },
						getLength())[0];
			} else {
				linePos = Coords.convert2dToLine(new int[] { i, xyconst },
						getLength())[0];
			}

			if (mTrack[linePos] != mSpaceId) {
				data[MAX + (length += 1)] = mTrack[linePos];
			}
		}
		return data;
	}

	public void start() {
		shuffleChips();
		mSteps = 0;
		mStartTime = SystemClock.uptimeMillis();
	}

	public long getTime() {
		return mStartTime != TIME_NULL ? SystemClock.uptimeMillis()
				- mStartTime : mStartTime;
	}

	public void setOnPuzzleSolvedListener(OnPuzzleSolvedListener l) {
		mOnPuzzleSolvedListener = l;
	}

	public interface OnPuzzleSolvedListener {
		public void OnPuzzleSolved(int time, int steps, int length);
	}

	/**
	 * Shuffle tracking data randomly Warning: Do not forget to do restore
	 * after!
	 */
	private void shuffleChips() {
		final Random random = new Random();
		final int trackLength = mTrack.length;
		final int length = getLength();

		// Inverse track!
		for (int i = 0; i < trackLength; i++) {
			mTrack[i] = trackLength - 1 - i;
		}

		// Space coordinates
		int[] space2d = new int[] { 0, 0 };

		int reback = -1;
		int shuffling = trackLength * trackLength;
		for (int i = 0; i < shuffling; i++) {
			// Get random coords
			final int[] switchChip2d = new int[2];
			switch (random.nextInt(4)) {
			case 0: // TO RIGHT
				if (space2d[0] != length - 1 && reback != 0) {
					switchChip2d[0] = space2d[0] + 1;
					switchChip2d[1] = space2d[1];

					reback = 2;
					break;
				}
			case 1: // TO TOP
				if (space2d[1] != 0 && reback != 1) {
					switchChip2d[0] = space2d[0];
					switchChip2d[1] = space2d[1] - 1;

					reback = 3;
					break;
				}
			case 2: // TO LEFT
				if (space2d[0] != 0 && reback != 2) {
					switchChip2d[0] = space2d[0] - 1;
					switchChip2d[1] = space2d[1];

					reback = 0;
					break;
				}
			case 3: // TO BOTTOM
				if (space2d[1] != length - 1 && reback != 3) {
					switchChip2d[0] = space2d[0];
					switchChip2d[1] = space2d[1] + 1;

					reback = 1;
					break;
				}
			default:
				continue;
			}
			final int space = Coords.convert2dToLine(space2d, length)[0];
			final int switchChip = Coords.convert2dToLine(switchChip2d, length)[0];

			// Space chip now knows as selected one
			space2d[0] = switchChip2d[0];
			space2d[1] = switchChip2d[1];

			// Swap tracking array
			int k = mTrack[space];
			mTrack[space] = mTrack[switchChip];
			mTrack[switchChip] = k;
		}

		// Restore chip's positions by track data
		for (int i = 0; i < trackLength; i++) {
			// Filter space chip
			if (mTrack[i] == mSpaceId)
				continue;
			// Set chip's coords
			getChip(mTrack[i]).setCoords(
					Coords.convertLineToReal(new int[] { i }, getLength(),
							getChipSize()));
		}
	}

}