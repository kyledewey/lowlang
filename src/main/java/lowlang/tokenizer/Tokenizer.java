package lowlang.tokenizer;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Tokenizer {
    public static final Map<String, Token> RESERVED_WORDS =
        new HashMap<String, Token>() {{
            put("int", new IntToken());
            put("void", new VoidToken());
            put("bool", new BoolToken());
            put("true", new TrueToken());
            put("false", new FalseToken());
            put("sizeof", new SizeofToken());
            put("malloc", new MallocToken());
            put("print", new PrintToken());
            put("if", new IfToken());
            put("else", new ElseToken());
            put("while", new WhileToken());
            put("break", new BreakToken());
            put("continue", new ContinueToken());
            put("return", new ReturnToken());
            put("struct", new StructToken());
        }};

    public static final List<Pair<String, Token>> SYMBOLS =
        new ArrayList<Pair<String, Token>>() {{
            add(new Pair<String, Token>("=>", new ArrowToken()));
            add(new Pair<String, Token>("==", new DoubleEqualsToken()));
            add(new Pair<String, Token>("=", new SingleEqualsToken()));
            add(new Pair<String, Token>(",", new CommaToken()));
            add(new Pair<String, Token>("(", new LeftParenToken()));
            add(new Pair<String, Token>(")", new RightParenToken()));
            add(new Pair<String, Token>("*", new StarToken()));
            add(new Pair<String, Token>(".", new DotToken()));
            add(new Pair<String, Token>("&", new SingleAndToken()));
            add(new Pair<String, Token>("/", new DivToken()));
            add(new Pair<String, Token>("+", new PlusToken()));
            add(new Pair<String, Token>("-", new MinusToken()));
            add(new Pair<String, Token>("<", new LessThanToken()));
            add(new Pair<String, Token>(";", new SemicolonToken()));
            add(new Pair<String, Token>("{", new LeftCurlyBraceToken()));
            add(new Pair<String, Token>("}", new RightCurlyBraceToken()));
        }};

    public final String input;
    private int position;

    public Tokenizer(final String input) {
        this.input = input;
        position = 0;
    }

    public void skipWhitespace() {
        while (position < input.length() &&
               Character.isWhitespace(input.charAt(position))) {
            position++;
        }
    }
    
    // returns null if it couldn't read one
    public IntLiteralToken readIntLiteral() {
        String digits = "";
        char c;
        
        while (position < input.length() &&
               Character.isDigit(c = input.charAt(position))) {
            digits += c;
            position++;
        }

        if (digits.length() > 0) {
            return new IntLiteralToken(Integer.parseInt(digits));
        } else {
            return null;
        }
    }

    // returns null if it couldn't read one
    public Token readSymbol() {
        for (final Pair<String, Token> pair : SYMBOLS) {
            if (input.startsWith(pair.first, position)) {
                position += pair.first.length();
                return pair.second;
            }
        }
        return null;
    }
    
    public Token readReservedWordOrIdentifier() {
        String characters = "";
        char c;
        if (Character.isLetter(c = input.charAt(position))) {
            characters += c;
            position++;
            while (position < input.length() &&
                   Character.isLetterOrDigit(c = input.charAt(position))) {
                characters += c;
                position++;
            }
            final Token candidate = RESERVED_WORDS.get(characters);
            if (candidate == null) {
                return new IdentifierToken(characters);
            } else {
                return candidate;
            }
        } else {
            return null;
        }
    }

    public Token tokenizeSingle() throws TokenizerException {
        assert(position < input.length());
        Token retval = readIntLiteral();
        if (retval != null) {
            return retval;
        }
        retval = readSymbol();
        if (retval != null) {
            return retval;
        }
        retval = readReservedWordOrIdentifier();
        if (retval != null) {
            return retval;
        } else {
            throw new TokenizerException("Expected token; got: " + input.charAt(position));
        }
    }
    
    public Token[] tokenize() throws TokenizerException {
        final List<Token> tokens = new ArrayList<Token>();
        skipWhitespace();
        while (position < input.length()) {
            tokens.add(tokenizeSingle());
            skipWhitespace();
        }
        return tokens.toArray(new Token[tokens.size()]);
    }
    
    public static Token[] tokenize(final String input) throws TokenizerException {
        return new Tokenizer(input).tokenize();
    }

    public static Token[] tokenize(final File input) throws TokenizerException, IOException {
        return tokenize(Files.readString(input.toPath()));
    }
}
