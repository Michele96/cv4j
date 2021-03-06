/*
 * Copyright (c) 2017-present, CV4J Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cv4j.core.filters;

import com.cv4j.core.datamodel.ColorProcessor;
import com.cv4j.core.datamodel.image.ImageProcessor;
import com.cv4j.core.utils.SafeCasting;
import com.cv4j.image.util.Tools;
/**
 * The EmbossFilter class.
 * The ginning effect, based on the relief effect, is similar, 
 * but more flexible, allowing more pixel value correction
 */
public class EmbossFilter extends BaseFilter {

	private int colorConstants;
	private boolean out;

	public EmbossFilter() {
		this.out = false;
		this.colorConstants = 100;
	}

	public EmbossFilter(boolean out) {
		this.out = out;
		this.colorConstants = 100;
	}

	@Override
	public ImageProcessor doFilter(ImageProcessor src){
		int offset = 0;

		byte[][] output = new byte[3][R.length];
		for ( int y = 1; y < height-1; y++ ) {
			offset = y*width;
			setOutput(out, output, offset);
		}
		((ColorProcessor)src).putRGB(output[0], output[1], output[2]);

		return src;
	}


	private void setOutput(boolean isOut, byte[][] output, int offset){
		final int value0000FF = 0x0000ff;

		for ( int x = 1; x < width-1; x++ ) {
			int r1 = R[offset] & value0000FF;
			int g1 = G[offset] & value0000FF;
			int b1 = B[offset] & value0000FF;

			int r2 = R[offset + width] & value0000FF;
			int g2 = G[offset + width] & value0000FF;
			int b2 = B[offset + width] & value0000FF;

			int r = 0;
			int g = 0;
			int b = 0;
			if(isOut) {
					r = r1 - r2;
					g = g1 - g2;
					b = b1 - b2;
				} else {
					r = r2 - r1;
					g = g2 - g1;
					b = b2 - b1;
				}
				r = Tools.clamp(r + colorConstants);
				g = Tools.clamp(g + colorConstants);
				b = Tools.clamp(b + colorConstants);

				output[0][offset] = SafeCasting.safeIntToByte(r);
				output[1][offset] = SafeCasting.safeIntToByte(g);
				output[2][offset] = SafeCasting.safeIntToByte(b);
				offset++;
			}
	}
	/**
	 * 
	 * @param out
	 */
	public void setOUT(boolean out) {
		this.out = out;
	}
}
