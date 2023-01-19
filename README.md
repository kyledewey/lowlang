# Low-Level Compiler Example #

This language has the following features:

- Compiles to a low-level target (specifically MIPS assembly)
- Pointers
- Function pointers

## Abstract Grammar ##

```
i is an integer
var is a variable
fn is a function name
sn is a structure name
field is the name of a field in a structure
type ::= `int` | `void` | `bool` | // basic primitive types
         sn |                      // name of a user-defined struct
         type `*` |                // pointers
         `(` type* `)` `=>` type   // function pointers
op ::= `+` | `-` | `*` | `/` | `==` | `<`
lhs ::= var | lhs `.` field | `*` lhs       // used on left-hand side of assignment
exp ::= i | `true` | `false` | var |
        malloc `(` exp `)` |  // allocates a number of bytes of memory. Undefined behavior if parameter <= 0
        sizeof `(` type `)` | // returns the number of bytes a value of type "type" consumes
        exp op exp |
        sn `(` exp* `)` |  // creates a structure on the stack
        fn `(` exp* `)` |  // calls a function
        exp `(` exp* `)` | // function call through pointer
        `(` type `)` exp | // cast
        `&` lhs |          // address-of data
        `&` fn  |          // address-of function
        `*` exp |          // dereference
        exp `.` field      // structure field access
vardec ::= type var
stmt ::= `if` `(` exp `)` stmt [`else` stmt] |
         `while` `(` exp `)` stmt |
         `break` `;` |
         `continue` `;` |
         vardec `=` exp `;` | // combined variable declaration and initialization
         lhs `=` exp `;` |    // assignment
         `return` [exp] `;` | // return
         `{` stmt* `}` |      // blocks
         exp `;`              // expression statement
structDec ::= sn { vardec* }
fDef ::= type fn(vardec*) { stmt }
program ::= structDec* fDef*
```

Something like [`free`](https://www.tutorialspoint.com/c_standard_library/c_function_free.htm) is intentionally missing from the language, even though it's low-level.
This is because the simulator for our compilation target ([SPIM](https://pages.cs.wisc.edu/~larus/spim.html)) is missing `free`.
(Longer version: `malloc` and `free` should be implemented as library routines.
  Our `malloc` is implemented via a SPIM syscall that internally just calls [`sbrk`](https://linux.die.net/man/2/sbrk), which changes the size of the underlying heap, nothing more.
  Implementing this properly requires library support, and implementing our own `malloc` and `free` is a major undertaking in and of itself.)

The typechecker and related semantic analysis needs to check:

- The types line up in expected places
- Function calls take expected parameters of expected types
- Stack structure creation takes expected parameters of expected types
- Structure field access accesses existant fields
- Functions return expected types
- Used variables have been declared
- `break` and `continue` are used within the body of `while`

## Concrete Grammar ##

Precedence is loosely based on [C](https://en.cppreference.com/w/c/language/operator_precedence).

Structure creation, direct function calls, and indirect function calls cannot be distinguished purely syntactically, so these are a call-like.
Address-of similarly can be data or a function.

Can get the address of:
- Variable
- Function name
- Field of a struct

```
i is an integer
id is an identifier
types ::= [type (`,` type)*]
primaryType ::= `int` | `void` | `bool` | id | `(` type `)`
pointerType ::= primaryType (`*`)*
functionType ::= (`(` types `)` `=>`)* pointerType
type ::= functionType
primaryLhs ::= id
accessLhs ::= primaryLhs (`.` id)*
starLhs ::= (`*`)* accessLhs
lhs ::= starLhs
exps ::= [exp (`,` exp)*]
primaryExp ::= i | `true` | `false` | id | `(` exp `)` |
               `sizeof` `(` type `)` |
               `malloc` `(` exp `)` |
               `&` lhs
dotOrCall ::= `.` id | `(` exps `)`
dotOrCallExp::= primaryExp dotOrCall*
castOrMemItem ::= `(` type `)` | `*`
castOrMemExp ::= castOrMemItem* dotOrCallExp
multExp ::= castOrMemExp ((`*` | `/`) castOrMemExp)*
addExp ::= multExp ((`+` | `-`) multExp)*
compareExp ::= addExp (`<` addExp)*
equalsExp ::= compareExp (`==` compareExp)*
exp ::= equalsExp
vardec ::= type var
stmt ::= `if` `(` exp `)` stmt [`else` stmt] |
         `while` `(` exp `)` stmt |
         `break` `;` |
         `continue` `;` |
         `return` [exp] `;` |      // return
         `{` stmt* `}` |           // blocks
         `print` `(` exp `)` `;` | // printing
         vardec `=` exp `;` |      // combined variable declaration and initialization
         lhs `=` exp `;` |         // assignment
         exp `;`                   // expression statement
structDec ::= `struct` id `{` (vardec `;`)* `}` `;`
params ::= [vardec (`,` vardec)*]
fDef ::= type id `(` params `)` `{` stmt* `}`
program ::= structDec* fDef*
```

### Tokens ###

- IdentifierToken(String)
- IntLiteralToken(int)
- CommaToken: 0
- IntToken: 1
- VoidToken: 2
- BoolToken: 3
- LeftParenToken: 4
- RightParenToken: 5
- StarToken: 6
- ArrowToken: 7
- DotToken: 8
- SingleAndToken: 9
- TrueToken: 10
- FalseToken: 11
- SizeofToken: 12
- MallocToken: 13
- DivToken: 14
- PlusToken: 15
- MinusToken: 16
- LessThanToken: 17
- DoubleEqualsToken: 18
- IfToken: 19
- ElseToken: 20
- WhileToken: 21
- SemicolonToken: 22
- BreakToken: 23
- ContinueToken: 24
- SingleEqualsToken: 25
- ReturnToken: 26
- StructToken: 27
- PrintToken: 28
- LeftCurlyBraceToken: 29
- RightCurlyBraceToken: 30

# Running the Compiler

```console
mvn exec:java -Dexec.mainClass="lowlang.Compiler" -Dexec.args="examples/perimeter.lowlang output.asm"
spim -quiet -file output.asm
```

# Running End-to-End Tests

```console
mvn test
```
