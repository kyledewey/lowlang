package lowlang.parser;

import lowlang.tokenizer.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

public class Parser {
    public final Token[] tokens;

    public Parser(final Token[] tokens) {
        this.tokens = tokens;
    }

    public Token getToken(final int position) throws ParseException {
        if (position < 0 || position >= tokens.length) {
            throw new ParseException("Invalid token position: " + position);
        } else {
            return tokens[position];
        }
    } // getToken

    public void assertTokenHereIs(final int position, final Token expected) throws ParseException {
        final Token received = getToken(position);
        if (!expected.equals(received)) {
            throw new ParseException("Expected " + expected.toString() + "; received: " + received.toString());
        }
    } // assertTokenHereIs
    
    public ParseResult<String> parseIdentifier(final int position) throws ParseException {
        final Token token = getToken(position);
        if (token instanceof IdentifierToken) {
            return new ParseResult<String>(((IdentifierToken)token).name, position + 1);
        } else {
            throw new ParseException("Expected identifier token; received: " + token.toString());
        }
    } // parseIdentifier

    public ParseResult<List<Type>> parseTypes(int position) {
        final List<Type> types = new ArrayList<Type>();
        try {
            ParseResult<Type> type = parseType(position);
            types.add(type.result);
            position = type.nextPosition;
            boolean shouldRun = true;
            while (shouldRun) {
                try {
                    assertTokenHereIs(position, new CommaToken());
                    type = parseType(position + 1);
                    types.add(type.result);
                    position = type.nextPosition;
                } catch (final ParseException e) {
                    shouldRun = false;
                }
            }
        } catch (final ParseException e) {}

        return new ParseResult<List<Type>>(types, position);
    } // parseTypes

    public ParseResult<Type> parsePrimaryType(final int position) throws ParseException {
        final Token token = getToken(position);
        if (token instanceof IntToken) {
            return new ParseResult<Type>(new IntType(), position + 1);
        } else if (token instanceof VoidToken) {
            return new ParseResult<Type>(new VoidType(), position + 1);
        } else if (token instanceof BoolToken) {
            return new ParseResult<Type>(new BoolType(), position + 1);
        } else if (token instanceof IdentifierToken) {
            return new ParseResult<Type>(new StructureType(new StructureName(((IdentifierToken)token).name)),
                                         position + 1);
        } else if (token instanceof LeftParenToken) {
            final ParseResult<Type> type = parseType(position + 1);
            assertTokenHereIs(type.nextPosition, new RightParenToken());
            return new ParseResult<Type>(type.result, type.nextPosition + 1);
        } else {
            throw new ParseException("Expected type; received: " + token.toString());
        }
    } // parsePrimaryType

    public ParseResult<Type> parsePointerType(int position) throws ParseException {
        final ParseResult<Type> base = parsePrimaryType(position);
        position = base.nextPosition;
        boolean shouldRun = true;
        int numStars = 0;
        while (shouldRun) {
            try {
                assertTokenHereIs(position, new StarToken());
                numStars++;
                position++;
            } catch (final ParseException e) {
                shouldRun = false;
            }
        }

        Type retval = base.result;
        while (numStars > 0) {
            retval = new PointerType(retval);
            numStars--;
        }
        return new ParseResult<Type>(retval, position);
    } // parsePointerType

    public ParseResult<Type> parseFunctionType(int position) throws ParseException {
        final List<List<Type>> params = new ArrayList<List<Type>>();
        boolean shouldRun = true;
        while (shouldRun) {
            try {
                assertTokenHereIs(position, new LeftParenToken());
                final ParseResult<List<Type>> curParams = parseTypes(position + 1);
                assertTokenHereIs(curParams.nextPosition, new RightParenToken());
                assertTokenHereIs(curParams.nextPosition + 1, new ArrowToken());
                params.add(curParams.result);
                position = curParams.nextPosition + 2;
            } catch (final ParseException e) {
                shouldRun = false;
            }
        }

        final ParseResult<Type> rest = parsePointerType(position);
        Collections.reverse(params);
        Type retval = rest.result;
        for (final List<Type> curParams : params) {
            retval = new FunctionPointerType(curParams, retval);
        }
        return new ParseResult<Type>(retval, rest.nextPosition);
    } // parseFunctionType

    public ParseResult<Type> parseType(final int position) throws ParseException {
        return parseFunctionType(position);
    } // parseType

    public ParseResult<Lhs> parsePrimaryLhs(final int position) throws ParseException {
        final Token token = getToken(position);
        if (token instanceof IdentifierToken) {
            return new ParseResult<Lhs>(new VariableLhs(new Variable(((IdentifierToken)token).name)),
                                        position + 1);
        } else {
            throw new ParseException("Expected lhs; received: " + token.toString());
        }
    } // parsePrimaryLhs

    public ParseResult<Lhs> parseAccessLhs(int position) throws ParseException {
        final ParseResult<Lhs> base = parsePrimaryLhs(position);
        position = base.nextPosition;
        final List<FieldName> fields = new ArrayList<FieldName>();
        boolean shouldRun = true;
        while (shouldRun) {
            try {
                assertTokenHereIs(position, new DotToken());
                final ParseResult<String> id = parseIdentifier(position + 1);
                fields.add(new FieldName(id.result));
                position = id.nextPosition;
            } catch (final ParseException e) {
                shouldRun = false;
            }
        }

        Lhs retval = base.result;
        for (final FieldName fieldName : fields) {
            retval = new FieldAccessLhs(retval, fieldName);
        }
        return new ParseResult<Lhs>(retval, position);
    } // parseAccessLhs

    public ParseResult<Lhs> parseStarLhs(int position) throws ParseException {
        int numStars = 0;
        boolean shouldRun = true;
        while (shouldRun) {
            try {
                final Token token = getToken(position);
                if (token instanceof StarToken) {
                    numStars++;
                    position++;
                } else {
                    throw new ParseException("Needed *; received: " + token.toString());
                }
            } catch (final ParseException e) {
                shouldRun = false;
            }
        }

        final ParseResult<Lhs> rest = parseAccessLhs(position);
        Lhs retval = rest.result;
        while (numStars > 0) {
            retval = new DereferenceLhs(retval);
            numStars--;
        }
        return new ParseResult<Lhs>(retval, rest.nextPosition);
    } // parseStarLhs

    public ParseResult<Lhs> parseLhs(final int position) throws ParseException {
        return parseStarLhs(position);
    } // parseLhs

    public ParseResult<List<Exp>> parseExps(int position) throws ParseException {
        final List<Exp> exps = new ArrayList<Exp>();
        try {
            ParseResult<Exp> exp = parseExp(position);
            exps.add(exp.result);
            position = exp.nextPosition;
            boolean shouldRun = true;
            while (shouldRun) {
                try {
                    assertTokenHereIs(position, new CommaToken());
                    exp = parseExp(position + 1);
                    exps.add(exp.result);
                    position = exp.nextPosition;
                } catch (final ParseException e) {
                    shouldRun = false;
                }
            }
        } catch (final ParseException e) {}

        return new ParseResult<List<Exp>>(exps, position);
    } // parseExps

    public ParseResult<Exp> parsePrimaryExp(final int position) throws ParseException {
        final Token token = getToken(position);
        if (token instanceof IntLiteralToken) {
            return new ParseResult<Exp>(new IntegerLiteralExp(((IntLiteralToken)token).value), position + 1);
        } else if (token instanceof TrueToken) {
            return new ParseResult<Exp>(new BooleanLiteralExp(true), position + 1);
        } else if (token instanceof FalseToken) {
            return new ParseResult<Exp>(new BooleanLiteralExp(false), position + 1);
        } else if (token instanceof IdentifierToken) {
            return new ParseResult<Exp>(new VariableExp(new Variable(((IdentifierToken)token).name)), position + 1);
        } else if (token instanceof LeftParenToken) {
            final ParseResult<Exp> exp = parseExp(position + 1);
            assertTokenHereIs(exp.nextPosition, new RightParenToken());
            return new ParseResult<Exp>(exp.result, exp.nextPosition + 1);
        } else if (token instanceof SizeofToken) {
            assertTokenHereIs(position + 1, new LeftParenToken());
            final ParseResult<Type> type = parseType(position + 2);
            assertTokenHereIs(type.nextPosition, new RightParenToken());
            return new ParseResult<Exp>(new SizeofExp(type.result), type.nextPosition + 1);
        } else if (token instanceof MallocToken) {
            assertTokenHereIs(position + 1, new LeftParenToken());
            final ParseResult<Exp> exp = parseExp(position + 2);
            assertTokenHereIs(exp.nextPosition, new RightParenToken());
            return new ParseResult<Exp>(new MallocExp(exp.result), exp.nextPosition + 1);
        } else if (token instanceof SingleAndToken) {
            final ParseResult<Lhs> lhs = parseLhs(position + 1);
            return new ParseResult<Exp>(new AddressOfExp(lhs.result), lhs.nextPosition);
        } else {
            throw new ParseException("Expected primary expression; received: " + token.toString());
        }
    } // parsePrimaryExp

    public ParseResult<DotOrCall> parseDotOrCall(final int position) throws ParseException {
        final Token token = getToken(position);
        if (token instanceof DotToken) {
            final ParseResult<String> id = parseIdentifier(position + 1);
            return new ParseResult<DotOrCall>(new Dot(new FieldName(id.result)),
                                              id.nextPosition);
        } else if (token instanceof LeftParenToken) {
            final ParseResult<List<Exp>> exps = parseExps(position + 1);
            assertTokenHereIs(exps.nextPosition, new RightParenToken());
            return new ParseResult<DotOrCall>(new Call(exps.result), exps.nextPosition + 1);
        } else {
            throw new ParseException("Expected dot or call; received: " + token.toString());
        }
    } // parseDotOrCall

    public ParseResult<Exp> parseDotOrCallExp(int position) throws ParseException {
        final ParseResult<Exp> base = parsePrimaryExp(position);
        final List<DotOrCall> intermediates = new ArrayList<DotOrCall>();
        position = base.nextPosition;
        boolean shouldRun = true;
        while (shouldRun) {
            try {
                final ParseResult<DotOrCall> dotOrCall = parseDotOrCall(position);
                intermediates.add(dotOrCall.result);
                position = dotOrCall.nextPosition;
            } catch (final ParseException e) {
                shouldRun = false;
            }
        }

        Exp retval = base.result;
        for (final DotOrCall dotOrCall : intermediates) {
            retval = dotOrCall.toExp(retval);
        }
        return new ParseResult<Exp>(retval, position);
    } // parseDotOrCallExp

    public ParseResult<CastOrMemItem> parseCastOrMemItem(final int position) throws ParseException {
        final Token token = getToken(position);
        if (token instanceof LeftParenToken) {
            final ParseResult<Type> type = parseType(position + 1);
            assertTokenHereIs(type.nextPosition, new RightParenToken());
            return new ParseResult<CastOrMemItem>(new Cast(type.result), type.nextPosition + 1);
        } else if (token instanceof StarToken) {
            return new ParseResult<CastOrMemItem>(new StarMemItem(), position + 1);
        } else {
            throw new ParseException("Expected cast or *; received: " + token.toString());
        }
    } // parseCastOrMemItem

    public ParseResult<Exp> parseCastOrMemExp(int position) throws ParseException {
        final List<CastOrMemItem> items = new ArrayList<CastOrMemItem>();
        boolean shouldRun = true;
        while (shouldRun) {
            try {
                final ParseResult<CastOrMemItem> item = parseCastOrMemItem(position);
                items.add(item.result);
                position = item.nextPosition;
            } catch (final ParseException e) {
                shouldRun = false;
            }
        }

        final ParseResult<Exp> rest = parseDotOrCallExp(position);
        Exp retval = rest.result;
        Collections.reverse(items);
        for (final CastOrMemItem item : items) {
            retval = item.toExp(retval);
        }
        
        return new ParseResult<Exp>(retval, position);
    } // parseCastOrMemExp

    public ParseResult<Exp> parseMultExp(int position) throws ParseException {
        final ParseResult<Exp> left = parseCastOrMemExp(position);
        Exp retval = left.result;
        position = left.nextPosition;
        boolean shouldRun = true;
        while (shouldRun) {
            try {
                final Token token = getToken(position);
                if (token instanceof StarToken) {
                    final ParseResult<Exp> right = parseCastOrMemExp(position + 1);
                    retval = new BinopExp(retval, new MultOp(), right.result);
                    position = right.nextPosition;
                } else if (token instanceof DivToken) {
                    final ParseResult<Exp> right = parseCastOrMemExp(position + 1);
                    retval = new BinopExp(retval, new DivOp(), right.result);
                    position = right.nextPosition;
                } else {
                    throw new ParseException("Expected * or /; received: " + token.toString());
                }
            } catch (final ParseException e) {
                shouldRun = false;
            }
        }

        return new ParseResult<Exp>(retval, position);
    } // parseMultExp

    public ParseResult<Exp> parseAddExp(int position) throws ParseException {
        final ParseResult<Exp> left = parseMultExp(position);
        Exp retval = left.result;
        position = left.nextPosition;
        boolean shouldRun = true;
        while (shouldRun) {
            try {
                final Token token = getToken(position);
                if (token instanceof PlusToken) {
                    final ParseResult<Exp> right = parseMultExp(position + 1);
                    retval = new BinopExp(retval, new PlusOp(), right.result);
                    position = right.nextPosition;
                } else if (token instanceof MinusToken) {
                    final ParseResult<Exp> right = parseMultExp(position + 1);
                    retval = new BinopExp(retval, new MinusOp(), right.result);
                    position = right.nextPosition;
                } else {
                    throw new ParseException("Expected + or -; received: " + token.toString());
                }
            } catch (final ParseException e) {
                shouldRun = false;
            }
        }

        return new ParseResult<Exp>(retval, position);
    } // parseAddExp

    public ParseResult<Exp> parseCompareExp(int position) throws ParseException {
        final ParseResult<Exp> left = parseAddExp(position);
        Exp retval = left.result;
        position = left.nextPosition;
        boolean shouldRun = true;
        while (shouldRun) {
            try {
                final Token token = getToken(position);
                if (token instanceof LessThanToken) {
                    final ParseResult<Exp> right = parseAddExp(position + 1);
                    retval = new BinopExp(retval, new LessThanOp(), right.result);
                    position = right.nextPosition;
                } else {
                    throw new ParseException("Expected <; received: " + token.toString());
                }
            } catch (final ParseException e) {
                shouldRun = false;
            }
        }

        return new ParseResult<Exp>(retval, position);
    } // parseCompareExp

    public ParseResult<Exp> parseEqualsExp(int position) throws ParseException {
        final ParseResult<Exp> left = parseCompareExp(position);
        Exp retval = left.result;
        position = left.nextPosition;
        boolean shouldRun = true;
        while (shouldRun) {
            try {
                final Token token = getToken(position);
                if (token instanceof DoubleEqualsToken) {
                    final ParseResult<Exp> right = parseCompareExp(position + 1);
                    retval = new BinopExp(retval, new EqualsOp(), right.result);
                    position = right.nextPosition;
                } else {
                    throw new ParseException("Expected ==; received: " + token.toString());
                }
            } catch (final ParseException e) {
                shouldRun = false;
            }
        }

        return new ParseResult<Exp>(retval, position);
    } // parseEqualsExp

    public ParseResult<Exp> parseExp(final int position) throws ParseException {
        return parseEqualsExp(position);
    } // parseExp

    public ParseResult<VariableDeclaration> parseVardec(final int position) throws ParseException {
        final ParseResult<Type> type = parseType(position);
        final ParseResult<String> id = parseIdentifier(type.nextPosition);
        return new ParseResult<VariableDeclaration>(new VariableDeclaration(type.result,
                                                                            new Variable(id.result)),
                                                    id.nextPosition);
    } // parseVardec

    public ParseResult<List<Stmt>> parseStmts(int position) {
        final List<Stmt> stmts = new ArrayList<Stmt>();
        boolean shouldRun = true;
        while (shouldRun) {
            try {
                final ParseResult<Stmt> stmt = parseStmt(position);
                stmts.add(stmt.result);
                position = stmt.nextPosition;
            } catch (final ParseException e) {
                shouldRun = false;
            }
        }

        return new ParseResult<List<Stmt>>(stmts, position);
    } // parseStmts

    // parses the statements that cannot be unambiguously distinguished by
    // reading a single token at the beginning
    public ParseResult<Stmt> parseNonObviousStmt(final int position) throws ParseException {
        try {
            final ParseResult<VariableDeclaration> vardec = parseVardec(position);
            assertTokenHereIs(vardec.nextPosition, new SingleEqualsToken());
            final ParseResult<Exp> exp = parseExp(vardec.nextPosition + 1);
            assertTokenHereIs(exp.nextPosition, new SemicolonToken());
            return new ParseResult<Stmt>(new VariableDeclarationInitializationStmt(vardec.result,
                                                                                   exp.result),
                                         exp.nextPosition + 1);
        } catch (final ParseException e1) {
            try {
                final ParseResult<Lhs> lhs = parseLhs(position);
                assertTokenHereIs(lhs.nextPosition, new SingleEqualsToken());
                final ParseResult<Exp> exp = parseExp(lhs.nextPosition + 1);
                assertTokenHereIs(exp.nextPosition, new SemicolonToken());
                return new ParseResult<Stmt>(new AssignmentStmt(lhs.result,
                                                                exp.result),
                                             exp.nextPosition + 1);
            } catch (final ParseException e2) {
                final ParseResult<Exp> exp = parseExp(position);
                assertTokenHereIs(exp.nextPosition, new SemicolonToken());
                return new ParseResult<Stmt>(new ExpStmt(exp.result),
                                             exp.nextPosition + 1);
            }
        }
    } // parseNonObviousStmt
    
    public ParseResult<Stmt> parseStmt(final int position) throws ParseException {
        final Token token = getToken(position);
        if (token instanceof IfToken) {
            assertTokenHereIs(position + 1, new LeftParenToken());
            final ParseResult<Exp> guard = parseExp(position + 2);
            assertTokenHereIs(guard.nextPosition, new RightParenToken());
            final ParseResult<Stmt> ifTrue = parseStmt(guard.nextPosition + 1);
            try {
                final Token nextToken = getToken(ifTrue.nextPosition);
                if (nextToken instanceof ElseToken) {
                    final ParseResult<Stmt> ifFalse = parseStmt(ifTrue.nextPosition + 1);
                    return new ParseResult<Stmt>(new IfStmt(guard.result,
                                                            ifTrue.result,
                                                            Optional.of(ifFalse.result)),
                                                 ifFalse.nextPosition);
                } else {
                    throw new ParseException("Expected else; received: " + nextToken.toString());
                }
            } catch (final ParseException e) {}
            return new ParseResult<Stmt>(new IfStmt(guard.result,
                                                    ifTrue.result,
                                                    Optional.empty()),
                                         ifTrue.nextPosition);
        } else if (token instanceof WhileToken) {
            assertTokenHereIs(position + 1, new LeftParenToken());
            final ParseResult<Exp> guard = parseExp(position + 2);
            assertTokenHereIs(guard.nextPosition, new RightParenToken());
            final ParseResult<Stmt> body = parseStmt(guard.nextPosition + 1);
            return new ParseResult<Stmt>(new WhileStmt(guard.result, body.result),
                                         body.nextPosition);
        } else if (token instanceof BreakToken) {
            assertTokenHereIs(position + 1, new SemicolonToken());
            return new ParseResult<Stmt>(new BreakStmt(), position + 2);
        } else if (token instanceof ContinueToken) {
            assertTokenHereIs(position + 1, new SemicolonToken());
            return new ParseResult<Stmt>(new ContinueStmt(), position + 2);
        } else if (token instanceof ReturnToken) {
            try {
                final ParseResult<Exp> exp = parseExp(position + 1);
                assertTokenHereIs(exp.nextPosition, new SemicolonToken());
                return new ParseResult<Stmt>(new ReturnStmt(Optional.of(exp.result)),
                                             exp.nextPosition + 1);
            } catch (final ParseException e) {}
            assertTokenHereIs(position + 1, new SemicolonToken());
            return new ParseResult<Stmt>(new ReturnStmt(Optional.empty()),
                                         position + 2);
        } else if (token instanceof LeftCurlyBraceToken) {
            final ParseResult<List<Stmt>> stmts = parseStmts(position + 1);
            assertTokenHereIs(stmts.nextPosition, new RightCurlyBraceToken());
            return new ParseResult<Stmt>(new BlockStmt(stmts.result),
                                         stmts.nextPosition + 1);
        } else {
            return parseNonObviousStmt(position);
        }
    } // parseStmt

    public ParseResult<StructureDeclaration> parseStructDec(int position) throws ParseException {
        assertTokenHereIs(position, new StructToken());
        final ParseResult<String> name = parseIdentifier(position + 1);
        assertTokenHereIs(name.nextPosition, new LeftCurlyBraceToken());
        final List<VariableDeclaration> contents = new ArrayList<VariableDeclaration>();
        boolean shouldRun = true;
        position = name.nextPosition + 1;
        while (shouldRun) {
            try {
                final ParseResult<VariableDeclaration> vardec = parseVardec(position);
                assertTokenHereIs(vardec.nextPosition, new SemicolonToken());
                contents.add(vardec.result);
                position = vardec.nextPosition + 1;
            } catch (final ParseException e) {
                shouldRun = false;
            }
        }
        assertTokenHereIs(position++, new RightCurlyBraceToken());
        assertTokenHereIs(position++, new SemicolonToken());
        return new ParseResult<StructureDeclaration>(new StructureDeclaration(new StructureName(name.result),
                                                                              contents),
                                                     position);
    } // parseStructDec

    public ParseResult<List<StructureDeclaration>> parseStructDecs(int position) {
        final List<StructureDeclaration> structs = new ArrayList<StructureDeclaration>();
        boolean shouldRun = true;
        while (shouldRun) {
            try {
                final ParseResult<StructureDeclaration> struct = parseStructDec(position);
                structs.add(struct.result);
                position = struct.nextPosition;
            } catch (final ParseException e) {
                shouldRun = false;
            }
        }
        return new ParseResult<List<StructureDeclaration>>(structs, position);
    } // parseStructDecs

    public ParseResult<List<VariableDeclaration>> parseParams(int position) throws ParseException {
        final List<VariableDeclaration> params = new ArrayList<VariableDeclaration>();
        try {
            ParseResult<VariableDeclaration> vardec = parseVardec(position);
            params.add(vardec.result);
            position = vardec.nextPosition;
            boolean shouldRun = true;
            while (shouldRun) {
                try {
                    assertTokenHereIs(position, new CommaToken());
                    vardec = parseVardec(position + 1);
                    params.add(vardec.result);
                    position = vardec.nextPosition;
                } catch (final ParseException e) {
                    shouldRun = false;
                }
            }
        } catch (final ParseException e) {}
        return new ParseResult<List<VariableDeclaration>>(params, position);
    } // parseParams
                    
    public ParseResult<FunctionDefinition> parseFunction(final int position) throws ParseException {
        final ParseResult<Type> type = parseType(position);
        final ParseResult<String> name = parseIdentifier(type.nextPosition);
        assertTokenHereIs(name.nextPosition, new LeftParenToken());
        final ParseResult<List<VariableDeclaration>> params = parseParams(name.nextPosition + 1);
        assertTokenHereIs(params.nextPosition, new RightParenToken());
        assertTokenHereIs(params.nextPosition + 1, new LeftCurlyBraceToken());
        final ParseResult<List<Stmt>> bodyStmts = parseStmts(params.nextPosition + 2);
        assertTokenHereIs(bodyStmts.nextPosition, new RightCurlyBraceToken());
        return new ParseResult<FunctionDefinition>(new FunctionDefinition(type.result,
                                                                          new FunctionName(name.result),
                                                                          params.result,
                                                                          bodyStmts.result),
                                                   bodyStmts.nextPosition + 1);
    } // parseFunction

    public ParseResult<List<FunctionDefinition>> parseFunctions(int position) {
        final List<FunctionDefinition> functions = new ArrayList<FunctionDefinition>();
        boolean shouldRun = true;
        while (shouldRun) {
            try {
                final ParseResult<FunctionDefinition> function = parseFunction(position);
                functions.add(function.result);
                position = function.nextPosition;
            } catch (final ParseException e) {
                shouldRun = false;
            }
        }
        return new ParseResult<List<FunctionDefinition>>(functions, position);
    } // parseFunctions
    
    public ParseResult<Program> parseProgram(final int position) throws ParseException {
        final ParseResult<List<StructureDeclaration>> structDecs = parseStructDecs(position);
        final ParseResult<List<FunctionDefinition>> functions = parseFunctions(structDecs.nextPosition);
        return new ParseResult<Program>(new Program(structDecs.result, functions.result),
                                        functions.nextPosition);
    } // parseProgram

    public Program parseProgram() throws ParseException {
        final ParseResult<Program> program = parseProgram(0);
        if (program.nextPosition == tokens.length) {
            return program.result;
        } else {
            throw new ParseException("Remaining tokens at end, starting at: " + program.nextPosition);
        }
    } // parseProgram

    public static Program parse(final Token[] tokens) throws ParseException {
        return new Parser(tokens).parseProgram();
    } // parse
} // Parser
