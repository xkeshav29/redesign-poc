@Service
class ChatService {

  private ModuleService moduleService;
  private StateService stateService;
  private IntentService intentService;

  @Inject
  ChatService(ModuleService moduleService, StateService stateService, IntentService intentService) {
    this.moduleService = moduleService;
    this.stateService = stateService;
    this.intentService = intentService;
  }

  /*
   * @param message: Message sent by the user
   * @param userId: Id of the user
   *
   * Process the message sent by the user and return the next instruction
   *
   * @return instructionid of the next instruction
  */
  public String processUserResponse(String message, String userId) {
    State state = stateService.getState(userId);
    String currentModuleId = state.getCurrentModuleId();
    String currentInstructionId = state.getCurrentInstructionId();
    Instruction currentInstruction = instructionService.get(currentInstructionId);
    if(currentInstruction.isMatch(message)){
      currentInstruction.fulfil(message);
      String nextModuleId = module.getNextInstruction(currentInstructionId)
                      .map(nextInstruction -> currentModuleId)
                      .orElse(moduleService.nextModule(currentModuleId));
      String nextInstructionId = module.getNextInstruction(currentInstructionId)
                      .orElse(newModule.getFirstInstructionId());
      state.setInstructionId(nextInstructionId);
      state.setModuleId(nextModuleId);
      stateService.setState(userId, newState); 
      return nextInstructionId;
    } else
      return intentService.getIntent(message)
              .map(intent -> intent.fulfil(userId))
              .orElse(DEFAULT_INSTRUCTION_ID);
    } 
  }

}
