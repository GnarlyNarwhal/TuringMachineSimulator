# TuringMachineSimulator

## About

Allows the generation of and testing of Turing machines. 

## Feature List

- [Turing machine generation](https://github.com/GnarlyNarwhal/TuringMachineSimulator#defining-turing-machines)
- [A CLI](https://github.com/GnarlyNarwhal/TuringMachineSimulator#command-line-interface)
#### Potential features...probably not
- A debug mode for running the Turing machines. (NOT for debugging the generation
of the machines. Sorry but I'm not touching that parser code ever again.)

## Turing Machines

### Explanation

I am not anywhere near qualified enough to explain the significance of Turing
machines (my primary source is wikipedia), and I'm not even really qualified to
explain what they are, but I will try. A Turing machine is comprised of four
components. There is the tape which is an infinitely long set of cells which can
each hold a symbol. The symbols each cell is allowed to hold is specific to the
particular machine but can be defined as anything you want. The next component is
the head. The head acts a bit like a read/write head on a hard drive. It goes over
the cells in the tape and reads/writes symbols from/to them. The third component of
a Turing machine is the state register. The state register stores the current
state of the machine. A state defines how the machine behaves when it reads a
particular symbol. The final component is the instruction set. The instruction set
defines all the states of the machine. It tells the machine how to react to each
symbol read for each state that the machine has. Each state can be defined by a set
of what are called transition functions. One way of defining a transition function is
with a 5-tuple `(A, B, C, D, E)`. The way a transition function is
defined is as follows. If the state of the machine is `A` and the head is currently
pointing to symbol `B` (aka the head read symbol `B`) then write symbol `C` to the
cell which the head is pointing to, move one cell in the direction indicated by `D`
(or not at all if `D` indicates no movement), and finally set the state of the
machine to state `E`. This process continues on indefinitely until the machine
reaches the `HALT` state. A formal definition exists for a Turing machine which
defines itas a 7-tuple. I won't be defining it in that way, but I can list the seven
components of the 7-tuple in simpler terms. The seven components are listed below.
 - The list of acceptable states. This is the set of states which the machine is allowed to be in.
 - The set of symbols that the tape is allowed to store. Aka the tape alphabet.
 - The blank symbol. This is the default symbol in each cell and is considered a blank cell.
 - The set of allowed input symbols. This is the set of symbols that are allowed to be
 input onto the tape before the machine begins execution.
 - The initial state of the machine. This is one of the states from the acceptable list of states.
 - The halt states of the machine. These are the states which upon being reached will halt the
 execution of the machine. This can be one or more states from the list of acceptable states.
 - The set of transition functions by which the machine operates. These are the functions which
 tell the machine how to react after reading a certain symbol.

### Example

Imagine we have a Turing machine as shown below. The head currently points to a cell
with the symbol `c` and the current state is S1 where S1 is defined by the 5-tuple `(S1, 'c', '&', right, S2)`.
```
   ---------------------------
 <-   | a | b | c | d | e |   ->
   ---------------------------
          ______^______
         |  State: S1  |
          -------------
```
The head starts by reading the symbol `c` from the tape. As seen in our definition of S1
the head now does three things.

1. Write `&` to the current cell
```
   ---------------------------
 <-   | a | b | & | d | e |   ->
   ---------------------------
          ______^______
         |  State: S1  |
          -------------
```
2. Move the head one cell to the right
```
   ---------------------------
 <-   | a | b | & | d | e |   ->
   ---------------------------
              ______^______
             |  State: S1  |
              -------------
```
3. Change to the state GN
```
   ---------------------------
 <-   | a | b | & | d | e |   ->
   ---------------------------
              ______^______
             |  State: S2  |
              -------------
```
The machine would then repeat these steps using the rules defined for state S2
which we would have defined if this were a real machine.

### More Info

Just as a final note, take everything here with a grain of salt. I would be entirely
unsurprised if someone who knew what they were talking about took issue with what I
have said. However I think this is a good enough explanation to give people some
idea of what a Turing machine is. If you want a more informed explanation of a
Turing machine or want to understand the significance of them in terms of computational
theory then there are plenty of resources out there which do a much better job than
I could hope to do. If you don't know where to start consider reading the
[wikipedia page](https://en.wikipedia.org/wiki/Turing_machine).

## Command Line Interface

This program offers a simplisitic and clunky command line interface to run your Turing machines.
The CLI has 5 commands at the moment. The first is `help` which lists the commands and provides a
brief description of each command. The second command is `quit` which simply quits the program.
The final three commands are related to actually running your Turing machines. To start there is
the `load` command which loads a Turing machine from a `.gtm` file. The command takes one parameter
which is the path to the file. The next step is the `set` command. This command prompts you for four
variables: the input string, the index to start writing the index string at, the length of the tape,
and the starting index of the head. This command does not need to be called before every run. Only
when you want to change one of those parameters. Also you can't edit just one you have to set
every one every time the command is called. The final command is the `execute` command. This just
executes the currently loaded Turing machine with the current parameters set by the `set` command.
Once the machine halts the tape is printed to the screen.

## Defining Turing Machines

The definition of a Turing machine in a `.gtm` file is strictly a sequence of state and
transition function definitions. Here is how the 7 components for the Turing machine are determined from the `.gtm` file.
- The set of acceptable states is the set of all states defined in the file and the implicitly defined `HALT!` state
- The alphabet for these Turing machines is always defined to be `Integer.MIN_VALUE` to `Integer.MAX_VALUE`.
However if no transition function, for the current symbol being read during the current state of the machine, is defined the machine will
emit an error and terminate effectively limiting the alphabet to those set of characters for which a transition
state is defined.
- `'\0'` or `0` (equivalent) is always defined to be the blank symbol.
- There is no explicit input character set enforcment. It is up to the user to ensure the input they are using
is acceptable for the machine they are using.
- The initial state of the machine is always the first state defined in the `.gtm` file.
- `HALT!` is automatically defined to be the halting state.
- The transition functions are all defined in the `.gtm` file.

### Syntax

The syntax used by `.gtm` files is loosely inspired by `JSON` and by extension c-style languages.
Each file is a list of zero or more states each of which is a list of zero or more transition
functions. The syntax for defining a new state is as follows. The word `state` all lower case
followed by the name of the state. Finally a set of curly braces `{}` are added which enclose
all the transition functions for that state. State names can be any combination of letter,
numbers, or underscores. State names are case sensitive.

#### Example

```
// Recommended formatting
state ExampleState {
    // List of transition functions
}

state    SecondExample
{
    // More transition functions
}

state
FinalExampleState
{ /* Even more transition functions */ }
```

The syntax for defining transition functions is as follows. The word `symbol` all lower case
followed by the read symbol for that transition function. Finally a set of curly braces `{}`
are added which enclose the transition function information. A symbol can be on of three things.
The first type is a character literal which is a character enclosed in single quotes `'`.
`\n`, `\t`, and `\0` are the only supported escape sequences. `\` does not need to be escaped
by a second backslash. Do not use `\\`. The second type of symbol is a hexadecimal number.
Hexadecimal numbers are prefixed with `0x` and support both upper and lower case. The final
type of symbol is a decimal integer. Note that decimal numbers can be negative while
hexadecimal numbers cannot be made negative.

#### Example
```
state IrrelevantState {
    // Recommended formatting
    symbol ' ' { /* Transition function info */ }
    
    symbol '\n'
    { /* More transition function info */ }
    
    // Recommended formatting (except normalize your captilization)
    symbol 0xf0Ad9 {
        // Even more transntion function info
    }
    
    symbol -255
    {
        // But wait! There's more!
    }
}
```
Each transition function requires three pieces of information. It requires the write symbol, the movement,
and the state to transition to. The symbol syntax for the write symbol follows the same rules as the syntax
for the read symbol. Movement can be one of `left`, `right`, or `none`. The target state is simply the name
of the state to transition to. This can be the same state as it is in. This can also be states which are
undefined now but are defined later in the file.

#### Example
```
// Recommended formatting
state DemoState {
    symbol '0' { '1', right, DemoState  }
    symbol '1' { '1', left,  DemoState2 }
}

state DemoState2 {
    symbol '0' {
        '0', left, DemoState2
    }
    
    symbol ' ' {' ',right,DemoState}
    symbol '1' {    '1', left,DemoState
    }
}
```

The last piece of relevant information is that comments are supported. Both block comments `/*...*/` and single
line comments `//...` are supported.
