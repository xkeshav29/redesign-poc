
## Background

### Why?

The current intent handler logic is a set of functions in a huge file of about 2.7k lines.
This voilates the single responsibility principle(SRP) of the [SOLID](https://en.wikipedia.org/wiki/SOLID) which is critical to maintanability. This makes it difficult for developers to interpret, add, edit intent handlers without breaking anything else.

```
The goal of this initiative is to make the intent handling code more maintainable in terms of:
   1. Readability: It is easy to interpret the flow.
   2. Extensible: It is easy to add/edit an existing intent without any side-effects in others.
   3. Testable: Each module is independently testable so that changes can be done with confidence.
```

### What?
```
Redesigning the intent handler.
```

### How?
```
1. Identify the core functions of this module.
2. Identify the entities which can be abstracted out.
3. Define the interface/contract of each.
```

## Re-Design

### Core Functions in this module
```
1. Utility Functions
   a. startUnit: creates  new conversation session start in db
   b. getFirstRefereeDetailTypeResponse: gets referrer from db
   c. getNextInstructions: gets the next set of instructions for given instruction 
   d. continueFromInstructionAtPosition: if current unit is over, move to next
                        if instruction is not response: handle if response can be handled within the service: Multiple logic
                        if not, ask  ARTHA via rest api for multiple intents

   e. handleModuleCompletion: finalize a module by performing actions giving reward. scheduling interview etc
   f. handleUnitCompletionEvents: finalizer for a module
   g. nextUnit: get next unit
   h. getTurnMessage
   i. getChoicesMessageText
   j. getTrueFalseChoicesMessageText
   k. sendMessagesForMultipleChoiceType
   l. sendImageMessages
   m. sendMessagesForTextResponseType
   n. sendNoResponseMessages
   o. sendMessageForInstruction
   p. handleDeadStateForMessenger
   q. nluMatch: artha api call
  
2. Message Handler, consume response from queue: receiveMessageFromUser
   creates message object
   saves in db
   validations and sanity checks: handle unsub  
   handle intent based on current state: Scheduled Interview, chosenAppointmentDay
   if user not found: start first message
   if valid response for corresponding intent, as per scoringService
        send next instruction in the unit
   if invalid response for that corresponding intent,
      check if response matches other intents like lang change, greeting etc and handle
         else
            ask artha service for meaning and if valid intent found, send instruction
   else
      repeat sendMessage(could not understand, repeatInstruction)
```
```
Since Utility functions are not part of the core business logic, they should be isolated from this module.
Core business logic is invoked at receiveMessageFromUser once a message a received from the user.
```
### Entities and Contracts

#### Module
```
Each module consists of a series of instructions
Each instruction corresponds to an intent
Completion of a module triggers specific actions like sending email, sending id for verification etc
```

#### Intent
```
Intent is classified either via regex match or artha api
Each intent has specific handling logic
```
### Sequence Diagram

![Sequence Diagram](https://raw.githubusercontent.com/xkeshav29/vahan-poc/master/Basic%20Sequence%20Diagram.png)

### Proposed Design

Proposed design is based on the principle of Single Responsibility principle(SRP) and Dependency Injection(DI) from [SOLID](https://en.wikipedia.org/wiki/SOLID) design principles
```
Langugage of choice: Java
Reason: Object oriented constructs is a must in order to implement SRP and DI
TypeScript can be used if we want to stick to Nodejs
```
#### Entities(Model classes):

1. [Instruction](src/model/Instruction.java): Encapsulates an instruction
2. [State](src/model/State.java): Encapsulates the current state of a user
3. [User](src/model/User.java): Encapsulates the user

Entities with Types:

|  Entity         | Types       |
| --------------- |-------------|
| IntentHandler    | LanguageChangeIntentHandler, UnsubscribeIntentHandler etc |
| ModuleHandler    |  IdVerificationModuleHandler, JobRecommendationModuleHandler etc |

#### Services:

|  Service         | Responsibility       |
| ---------------  |-------------|
| ChatService        | Process user response and return next instruction |
| ModuleService      |  Module related operations(getNext, getModule)    |
| IntentService      |  Intent related operations(getIntent from artha)  |
| InstructionService |  Instruction related operations(getInstruction)  |
| StateService       |  State related operations(getState,setState from Db)  |

The entry point service is [ChatService](src/service/ChatService.java):
It depends on [ModuleService](src/service/ChatService.java), [StateService](src/service/ChatService.java) and [IntentService](src/service/ChatService.java).

It processes the message sent by a user and returns next instruction.
Self explanatory code:

```java
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
    Instruction currentInstructionHandler = instructionService.get(currentInstructionId).handler();
    if(currentInstructionHandler.isMatch(message)){
      currentInstructionHandler.fulfil(message);
      String nextModuleId = module.getNextInstruction(currentInstructionId)
                      .map(nextInstruction -> currentModuleId)
                      .orElseGet(moduleService.nextModule(currentModuleId));
      if(!nextModuleId.equals(currentModuleId))
          moduleService.getModule(currentModule).handler().onComplete(user);
      String nextInstructionId = module.getNextInstruction(currentInstructionId)
                      .orElse(moduleService.getModule(newModule).handler().getFirstInstructionId());
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
```

#### Benefits of this design

##### Each service performs operations specific to one entity and can be tested independently.
1. Each service has tests specific to its functionality

Eg: [ChatService](src/service/ChatService.java) should have 5 tests:
```
  a. testChatServiceWhenValidResponse
  b. testChatServiceWhenInvalidResponseAndValidIntent
  c. testChatServiceWhenInvalidResponseAndInvalidIntent
  d. testChatServiceWhenModuleComplete
  e. testWhenErrorProcessingMessage
```  
With these tests in place, it becomes easy for developers to change the business logic without the fear of breaking something.

2. Each Intent encapsulates its logic of fulfilment

Eg: [LanguageChangeIntentHandler](src/model/LanguageChangeIntentHandler.java) contains logic to change language
```
If tests for all intent handlers are in place, changes to any intent fulfilment logic will not affect other intents.
```
3. Each module encapsulates the logic to complete

Eg : [IdVerificationModuleHandler](src/model/IdVerificationModuleHandler.java) contains logic to send the id to 3rd party on completion.
```
If tests for all module handlers are in place, changes to any module logic will not affect other modules.
```

##### It is easy to extend add new intents, modules
```
Adding a new module takes no more effort than providing an implementation of ModuleHandler interface as per contract.
Similary, adding a new intent takes no more effort than implementing IntentHandler interface as per contract.
```
Eg: Adding a Request Support call intent.

```java
class SupportCallIntentHandler implements IntentHandler {

   @Override
   void fulfil(User user) {
       supportService.requestSupportCallback(user);
   }

   @Override
   boolean isMatch(String message) {
      return Pattern.match(message, "regex");
   }

}

```
Eg: Adding an Ask Feedback Module.
```java
class AskFeedbackModuleHandler implements ModuleHandler {
   List<Instruction> instructions;

   @Override
   String firstInstructionId(){
      return FIRST_INSTRUCTION_ID;
   }

   @Override
   String getNextInstructionId(String currentInstructionId) {
      //return next instruction id from the list
   }
   
   @Override
   void onComplete(User user) {
      //send the collected feedback to analytics service
   }
}
```






