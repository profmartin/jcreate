/**

Copyright (c) 2012 Patrick Martin <prof.pmartin10@gmail.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), 
to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, 
and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

*/

package edu.ycp;

import java.nio.ByteBuffer;

/**
 * Class that parses incoming ByteBuffers from the CreateHardwareManager.
 * @author pjmartin
 *
 */
public class SensorDataParser {

	private ByteBuffer sensorDataBuffer;
	private ParserState currState;
	private boolean isDataBufReady;
	private int bufferIndex;
	
	private final int FULL_DATA_SIZE = 52; // bytes
	
	private enum ParserState {
		INIT, PARTIAL, COMPLETE;
	}
	
	public SensorDataParser(){
		currState = ParserState.INIT;
		sensorDataBuffer = ByteBuffer.allocate(FULL_DATA_SIZE);
		isDataBufReady = false;
		bufferIndex = 0;
	}
	
	public final void parseData(ByteBuffer inBuf){
		
		if(currState == ParserState.INIT){
			// set the state to Partial to enable parsing
			isDataBufReady = false;
			sensorDataBuffer.clear();
			currState = ParserState.PARTIAL;
		}
		
		if(currState == ParserState.PARTIAL){
			// parse
			for(byte b : inBuf.array()){
				if(bufferIndex < this.FULL_DATA_SIZE){
					sensorDataBuffer.put(bufferIndex, b);
					bufferIndex++;
				}
			}
			if(bufferIndex > (this.FULL_DATA_SIZE - 1)){
				currState = ParserState.COMPLETE;
				bufferIndex = 0;
			}
		}
		
		if(currState == ParserState.COMPLETE){
			isDataBufReady = true;
			currState = ParserState.INIT;
		}
		
	}

	public final boolean isDataBufReady() {
		return isDataBufReady;
	}

	public final ByteBuffer getSensorDataBuffer() {
		return sensorDataBuffer;
	}
	
	public static void main(String[] args){
		SensorDataParser sdp = new SensorDataParser();
		
		for (int i = 0; i < 10; i++){
			System.out.print("Allocating ByteBuffers with sizes:");
		
			int size1 = (int) Math.floor(Math.random()*30)+1;
			int size2 = 52-size1;
			System.out.print(" " + size1 + " and " + size2 + "\n");

			ByteBuffer buf1 = ByteBuffer.allocate(size1);
			buf1.put((byte) 1);
			buf1.put(size1-1, (byte) 9);
			ByteBuffer buf2 = ByteBuffer.allocate(size2);
			buf2.put((byte) 2);
			buf2.put(size2-1, (byte) 9);
			
			sdp.parseData(buf1);
			System.out.println("Is databuffer ready? " + sdp.isDataBufReady);
			sdp.parseData(buf2);
			System.out.println("Is databuffer ready? " + sdp.isDataBufReady);
			
			if(sdp.isDataBufReady()){
				ByteBuffer doneBuf = sdp.getSensorDataBuffer();
				System.out.print("[");
				for(byte b : doneBuf.array()){
					System.out.print(b + " ");
				}
				System.out.print("]\n");
			}
			
		
		}
		
			
	}
	
}
