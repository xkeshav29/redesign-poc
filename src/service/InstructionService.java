@Service
class InstructionService {

  //key = instruction Id
  private Map<String, Instruction> instructions;

  @Inject
  InstructionService(AppConfig appConfig) {
      //read from config or db and update instructionsMap
  }

  public Instruction getInstruction(String id) {
    return instructions.get(id);
  }

}