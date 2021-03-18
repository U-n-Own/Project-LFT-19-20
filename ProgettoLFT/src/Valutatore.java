import java.io.*;


public class Valutatore {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Valutatore(Lexer l, BufferedReader br) { 
	lex = l; 
	pbr = br;
	move(); 
    }
   
    void move() { 
    // come in Esercizio 3.1
    look = lex.lexical_scan(pbr);
    System.out.println("token = " + look);
    }

    void error(String s) { 
    // come in Esercizio 3.1
    throw new Error("near line " + lex.line + ": " + s);
    }

    void match(int t) {
    // come in Esercizio 3.1
    if (look.tag == t) {
	    if (look.tag != Tag.EOF) move();
	} else error("syntax error");
    }

    public void start() { 
	    int expr_val;

        
        if(look.tag == Tag.NUM || look.tag == '('){
            
    	expr_val = expr();
	    match(Tag.EOF);
        System.out.println(expr_val);
        }
        else
            error("Error at start");

	    
    }

    private int expr() { 
	    int term_val, exprp_val = 0;
        // ... completare ...
        switch(look.tag){
        
        case '(':
        case Tag.NUM:
    	term_val = term();
        exprp_val = exprp(term_val);
        break;
        
        default:
        error("error in expr");
        }

	    return exprp_val;
    }

    private int exprp(int exprp_i) {
	    int term_val, exprp_val = 0;
	    switch (look.tag) {
	    case '+':
            match('+');
            term_val = term();
            exprp_val = exprp(exprp_i + term_val);
            break;
        case '-':
            match('-');
            term_val = term();
            exprp_val = exprp(exprp_i - term_val);
            break;

        default:
        exprp_val = exprp_i;
    	
        }
        
        return exprp_val;
    }

    private int term() { 
        int fact_val, termp_val=0;
    
        switch(look.tag){
            
        case '(':
        case Tag.NUM:
        fact_val = fact();
        termp_val = termp(fact_val);
        break;

        default: 
        error("error in term");
        }

        return termp_val;
    }
    
    private int termp(int termp_i) { 
    int fact_val = 0, termp_val = 0;
    switch (look.tag) {
    case '*':
        match('*');
        fact_val = fact();
        termp_val = termp(termp_i * fact_val);
        break;
    case '/':
        match('/');
        termp_val = term();
        termp_val = termp(termp_i / fact_val);
        break;

    default:
    termp_val = termp_i;
    }
    return termp_val;
    }
    
    private int fact() { 
    int fact_val;
    switch(look.tag){
        case '(':
        match('(');
        fact_val = expr();
        match(')');
        break;

        case Tag.NUM:
        NumberTok number = (NumberTok)look;
        match(Tag.NUM);
        fact_val = number.lexeme;
        break;

        default:
        fact_val = 0;
        error("Error in fact");

    }
    return fact_val;
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "test/TestLexer.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Valutatore valutatore = new Valutatore(lex, br);
            valutatore.start();
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}