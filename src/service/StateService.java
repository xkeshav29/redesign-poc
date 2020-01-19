@Service
class StateService {

  private StateRepo stateRepo;
  
  @Inject
  StateService(StateRepo stateRepo) {
    this.stateRepo = stateRepo;
  }

  public State getState(User user) {
    //read user's current state from Db
    //SELECT currentInstructionId, currentModuleId FROM state WHERE userID = user.ID;
  }

  public void setState(User user, State state) {
    //update user's current state into Db
    //UPDATE state SET currentInstructionId=state.currentInstructionId,
    // currentModuleId=state.currentModuleId WHERE id = user.id
  }

}