# vahan-poc

## Background

### Why?
```
The current intent handler logic is a set of functions in a huge file of about 2.7k lines.
This voilates the single responsibility principle(SRP) of the <a href="https://en.wikipedia.org/wiki/SOLID">SOLID design principles</a> which is critical to maintanability.
This makes it difficult for developers to interpret, add, edit intent handlers without breaking anything else.

The goal of this initiative is to make the intent handling code more maintainable in terms of:
   1. Readability: It is easy to interpret the flow.
   2. Extensible: It is easy to add/edit an existing intent without any side-effects in others.
   3. Testable: Each module is independently testable so that changes can be done with more confidence.
```

### What?
```
Redesigning the intent handler module:
```

### How?
```
1. Identify the core functions of this module.
2. Define the interface/contract of each.
```




