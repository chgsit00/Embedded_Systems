package edu.example.ssf.mma.statemachine;

public class StateStoppingPreCondition extends AbstractState {

    public StateStoppingPreCondition(IParentStateMachine stateMachine){
        super("STOPPINGPRECONDITION", stateMachine);
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
