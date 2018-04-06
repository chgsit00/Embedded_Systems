/*
 *  This file is part of the Multimodal Mobility Analyser(MMA), based
 *  on the Smartphone Sensing Framework (SSF)

    MMA (also SSF) is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    MMA (also SSF) is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with MMA.  If not, see <http://www.gnu.org/licenses/>.
 */

package edu.example.ssf.mma.timer;

import java.util.Timer;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import edu.example.ssf.mma.userInterface.MainActivity;
import edu.example.ssf.mma.config.ConfigApp;
import edu.example.ssf.mma.data.CsvFileWriter;
import edu.example.ssf.mma.data.CurrentTickData;
import edu.example.ssf.mma.hardwareAdapter.HardwareFactory;
import edu.example.ssf.mma.hardwareAdapter.IMicrophone;


/**
 * 
 * This class provides functions to control the state machine of the application, like
 * startStateMachine and stopStateMachine. Furthermore it extends the android.os.Handler class,
 * and implements the message queue endpoint to the statemachine-timer-task. 
 *
 * @version 2.0
 * @author Dionysios Satikidis (dionysios.satikidis@yahoo.de)
 * @version 1.0
 * @author D. Lagamtzis
 * @version 2.0
 */

public class StateMachineHandler extends Handler{

	
	/**
     * References to the timer-task of the state machine, and further flags and configuration
     */
	private StateMachineTimerTask stateMachineTimerTask = null;
	
	/**Refernece to the timer*/
	private Timer timer = null;
	
	/**referencing the cycle time to the cicle time in the ConficApp class*/
	private Integer cycleTimeMs = ConfigApp.globalSampleTimeMs;
	
	/**referencing the delay time to the delay time in the ConficApp class*/
	private Integer delayTimeMs = ConfigApp.delayStateMachineTimerTaskTimeMs;
    
    /**setting the variable for initialising the task to true*/
    private boolean initTask = true;


	/** Interface to the hardware abstraction layer. */
	private IMicrophone microphone = null;



	public StateMachineHandler(Context context) {
		
		/**
		 * Create state-machine task and timer
		 */
		this.stateMachineTimerTask= new StateMachineTimerTask(this);
        this.timer=new Timer();
		/**
		 * Interface to the hardware abstraction layer
		 */
		this.microphone = HardwareFactory.getMicrophone(context);
	}

	@Override
	public void handleMessage(Message msg) {
    	/**
    	 * Fetch data from the state-machine
    	 */
		//Fresh data from the state-machine has arrived. Get data from bundle
		Bundle bundle = msg.getData();
		
		 
		
		//Unpack data from bundle by using message tags defined in StateMachineTimerTask
    	CurrentTickData.actState = bundle.getString(StateMachineTimerTask.actStateMsgTag);
    	CurrentTickData.curTimestamp = bundle.getString(StateMachineTimerTask.curTimestampMsgTag);
    	CurrentTickData.curTick = bundle.getInt(StateMachineTimerTask.curTickMsgTag);
    	
    	/**
    	 * Fetch data from the sensors
    	 * Attention! Some sensor getter-function triggers 
    	 * underlying drivers to update the sensor value!
    	 * Fetch the sensor data just one time per tick! 
    	 * It's better to store data temporary at first.  
    	 */
    	//Fetch data from Sensors before writing it in the CSV

		//Update user-interface State
		MainActivity.actState(CurrentTickData.actState);



		if(MainActivity.mmaCallBackBool) {
			//Log.d("getAccX", "StateMachineHandler.MainActivity.mmaCallBackBool");
				if(ConfigApp.isSimulation){


					//Fetch data from microphone
					CurrentTickData.micMaxAmpl=microphone.getMaxAmplitude();


				}
			else {
					//Log.d("getAccX", "StateMachineHandler.MainActivity.mmaCallBackBool");
					// Try with direct usage of sensor data :)
					CsvFileWriter.writeLine(CurrentTickData.curTick.toString(),
							CurrentTickData.curTimestamp,
							CurrentTickData.micMaxAmpl.toString(),
							CurrentTickData.event
					);
					//Log.d("Time", CurrentTickData.curTimestamp);
				}
		}
		//Log.d("getAccX", "NOT StateMachineHandler.MainActivity.mmaCallBackBool");
    	//Call daddy and say everything is ok, by forwarding received message
    	//super.handleMessage(msg);
    }
	
	/**
	 * Initialises (just once) and starts the state-machine.
	 */
	public void startStateMachine(){
		this.stateMachineTimerTask.startStateMachine();
		if (initTask) {
        	this.timer.schedule(this.stateMachineTimerTask, this.delayTimeMs, this.cycleTimeMs);
        	this.initTask=false;
		}
		this.stateMachineTimerTask.startStateMachine();
   	}
	
	/**
	 * Stops the state machine
	 */
	public void stopStateMachine(){
		this.stateMachineTimerTask.stopStateMachine();
	}
	
	/**
	 * Under Construction: This method allows to change the cycle-time of the state-machine in run-time.
	 * @param cycleTimeMs The cycle time to change 
	 */
	void changeCycleTime(Integer cycleTimeMs){
		this.cycleTimeMs=cycleTimeMs;
		this.stopStateMachine();
		this.startStateMachine();
	}
}

