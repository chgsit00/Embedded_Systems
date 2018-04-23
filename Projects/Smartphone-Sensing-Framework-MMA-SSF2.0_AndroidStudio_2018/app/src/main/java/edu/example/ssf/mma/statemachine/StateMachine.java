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
    GNU v3 General Public License for more details.

    Released under GNU v3
    
    You should have received a copy of the GNU General Public License
    along with MMA.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.example.ssf.mma.statemachine;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.Date;
import java.util.function.Consumer;

import be.tarsos.dsp.onsets.OnsetHandler;
import edu.example.ssf.mma.hardwareAdapter.ClapDetector;


// TODO: Auto-generated Javadoc

/**
 * Interprets the accelerometer-data and determines in which state the user is at the moment.
 *
 * @author Dionysios Satikidis (dionysios.satikidis@gmail.com)
 * @version 1.0
 */

public class StateMachine implements IStateMachine, IParentStateMachine {

    private final ClapDetector clapDetector;
    /**
     * state of the state machine when not in the state "WALKING" or "DRIVING". Also the state the state machine is in at the start.
     */
    private AbstractState unknown = null;

    /**
     * state of the state machine when not in the state "UNKNOWN" or "DRIVING".
     */
    private AbstractState walking = null;

    /**
     * state of the state machine when not in the state "UNKNOWN" or "WALKING".
     */
    private AbstractState driving = null;

    /**
     * setting the abstract state in the beginning to null.
     */
    private AbstractState actState = null;

    /**
     * setting the next state in the beginning to null.
     */
    private AbstractState nextState = null;

    /**
     * setting the label of the state in the beginning to "N.N."
     */
    private String stateLabel = "N.N.";

    private Date lastClapDetected = null;

    @RequiresApi(api = Build.VERSION_CODES.N)
    private Boolean setAndCheckForDoubleClap() {
        int maxTimeBetweenClapsInMs = 1000;
        // init first clap
        if (lastClapDetected == null) {
            onWriteToAppLog.accept("Single Clap detected");
            lastClapDetected = new Date();
            return false;
        }
        // last clap less then n ms in the past
        if ((new Date()).getTime() - lastClapDetected.getTime() < maxTimeBetweenClapsInMs) {
            onWriteToAppLog.accept("Double Clap detected");
            lastClapDetected = null;
            return true;
        }
        // new first clap
        onWriteToAppLog.accept("Single Clap detected");
        lastClapDetected = new Date();
        return false;
    }

    public Consumer<String> onWriteToAppLog = null;

    public Runnable onToggle = null;

    /**
     * Instantiates a new state machine.
     */
    public StateMachine() {
        this.unknown = new StateUnknown(this);
        this.walking = new StateIdle(this);
        this.driving = new StateTrackingActivity(this);

        this.actState = this.unknown;
        this.nextState = this.unknown;
        this.clapDetector = new ClapDetector(new OnsetHandler() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void handleOnset(double time, double salience) {
                if (setAndCheckForDoubleClap()) {
                    onToggle.run();
                }
                Log.d("clap", "Clap detected!");
            }
        });
    }


    /**
     * This method checks if a transition change has occurred and if the state label has to be changed.
     */

    @Override
    public void transisionCheck() {
        /**
         * Calculate absolute value of the acceleration vector a
         */


        if (this.actState instanceof StateUnknown) {
            //Log.d("STATE_MACHINE", "UNKNOWN");
            this.nextState = this.unknown;
        } else if (this.actState instanceof StateTrackingActivity) {
            //Log.d("STATE_MACHINE", "DRIVING");
			/*PushToLosant ptl = new PushToLosant();
			ptl.pushtoLosant();*/
            this.nextState = this.unknown;
        } else if (this.actState instanceof StateIdle) {
            //Log.d("STATE_MACHINE", "WALKING");
            this.nextState = this.unknown;
        }
    }

    /**
     * changes the label text to another state.
     */
    @Override
    public void stateCheck() {
        this.actState.doit();
        if (this.actState != this.nextState) {
            this.actState.exit();
            this.nextState.entry();
        }
        this.actState = this.nextState;
        this.stateLabel = this.nextState.getStateName();
    }


    /**
     * gets the state label
     *
     * @return returns the statelabel
     */
    @Override
    public String getStateLabel() {
        return this.stateLabel;
    }

    /**
     * sets the state label.
     *
     * @param label uesd String to set StateLabel to "TEST"
     */
    @Override
    public void setStateLabel(String label) {
        this.stateLabel = label;
    }

    /**
     * Instantiates the actual state, the next state and the state label.
     */
    @Override
    public void initStateMachine() {
        this.actState = this.unknown;
        this.nextState = this.unknown;
        this.stateLabel = this.unknown.getStateName();
    }
}

