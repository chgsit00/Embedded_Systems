package edu.example.ssf.mma.statemachine;

public class StateTrackingPreCondition extends AbstractState{
    public StateTrackingPreCondition(IParentStateMachine stateMachine){
        super("TRACKINGPRECONDITION", stateMachine);
    }

    @Override
    public void doit() {
        getParentStateMachine().setStateLabel(this.getStateName());
    }

    @Override
    public void entry() {

    }

    @Override
    public void exit() {

    }
}
