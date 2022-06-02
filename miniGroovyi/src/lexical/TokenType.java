package lexical;

public enum TokenType {

    // Specials
	UNEXPECTED_EOF,
	INVALID_TOKEN,
	END_OF_FILE,

	// Symbols
	SEMICOLON,          // ;
	ASSIGN,             // =
    OPEN_CURLY_BRACES,  // { 
    CLOSE_CURLY_BRACES, // }
    OPEN_PARENTHESES,   // (
	CLOSE_PARENTHESES,  // )
	OPEN_BRACKETS,      // [
    CLOSE_BRACKETS,     // ]
    COLON,              // :
    PRIME,              // '
    DOUBLE_PRIME,       // "  
    COMMA,              // ,
	DOT,				//.
	RIGTH_ARROW,   // ->

	// Logic operators
	EQUAL,         // ==
	NOT_EQUAL,     // !=
	LOWER,         // <
	LOWER_EQUAL,   // <=
	GREATER,       // >
	GREATER_EQUAL, // >=
    NOT,   		   // !
	AND,		   // &&
	OR,			   // ||
	CONTAIN,	   // in
	NOT_CONTAIN,   // !in
	AS,			   // as

	// Arithmetic operators
	ADD,           // +
	SUB,           // -
	MUL,           // *
	DIV,           // /
	MOD,           // %
	POWER,		   // **
    ASSING_ADD,    // +=
	ASSIGN_SUB,	   // -=
	ASSING_MUL,    // *=
	ASSING_DIV,    // /=
	ASSING_MOD,	   // %=
	ASSING_POWER,  // *=

	// Keywords
    DEF,           // def
	PROGRAM,       // program
	WHILE,         // while
	DO,            // do
	FOR,		   // for
	DONE,          // done
	IF,            // if
	THEN,          // then
	ELSE,          // else
	OUTPUT,        // output
	TRUE,          // true
	FALSE,         // false
	READ,          // read
    AS_INTEGER,    // as Integer
    SIZE,          // size
    KEYS,          // keys
    SWITCH,        // switch
    IN,            // in
    FOREACH,       // foreach
    CASE,          // case
    PRINTLN,       // println
	PRINT,		   // print
	BOOLEAN,	   // Boolean
	INTEGER,	   // Integer
	STRING,  	   // String
    NULL,		   // null
	EMPTY,		   // empty
	VALUES,		   // values
	DEFAULT, 	   // default

	// Others
	NUMBER,        // number
	VAR,	       // variable
    TEXT		   //string
}
