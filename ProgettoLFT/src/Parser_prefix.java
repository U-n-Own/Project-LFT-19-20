import java.io.*;



public class Parser_prefix {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Parser_prefix(Lexer l, BufferedReader br) {
        lex = l;
        pbr = br;
        move();
    }

    void move() {
        look = lex.lexical_scan(pbr);
        System.out.println("token = " + look);
    }

    void error(String s) {
	throw new Error("near line " + lex.line + ": " + s);
    }

    void match(int t) {
	if (look.tag == t) {
	    if (look.tag != Tag.EOF) move();
	} else error("syntax error");
    }

    public void start() {
        if(look.tag == '('){
            prog();
            match(Tag.EOF);
        }else
        error("Error at start of program");
    }

    private void prog(){
        if(look.tag == Tag.EOF){
            error("error in program");
        }else
        stat();
    }

    private void stat(){
        switch(look.tag){
            case '(':
            match('(');
            statp();
            match(')');
            break;

            default:
            error("error in stat");
        }
    }

    private void statlist(){
        switch(look.tag){
            case '(':
            stat();
            statlistp();
            break;

            default:
            error("error in statlist");
        }
    }

    private void statlistp(){
        switch(look.tag){
            case '(':
            stat();
            statlistp();
            break;

            case ')':
            case Tag.EOF:
            break;
            
            default:
            error("error in statlist");
        }
    }

    private void statp(){
        switch(look.tag){
            case '=':
            match('=');
            match(Tag.ID);
            expr();
            break;

            case Tag.COND:
            match(Tag.COND);
            bexpr();
            stat();
            elseopt();
            break;
            
            case Tag.WHILE:
            match(Tag.WHILE);
            bexpr();
            stat();
            break;

            case Tag.DO:
            match(Tag.DO);
            statlist();
            break;

            case Tag.PRINT:
            match(Tag.PRINT);
            exprlist();
            break;

            case Tag.READ:
            match(Tag.READ);
            match(Tag.ID);
            break;

            default:
            error("Error in statp");
        }
    }

    private void elseopt(){
        switch(look.tag){
            case '(':
            match('(');
            match(Tag.ELSE);
            stat();
            match(')');
            break;

            case ')':
            case Tag.EOF:
            break;

            default:
            error("Error in elsopt");
        }
    }

    private void bexpr(){
        switch(look.tag){
            case '(':
            match('(');
            bexprp();
            match(')');
            break;

            default:
            error("Error in bexpr");
        }
    }

    private void bexprp(){
        switch(look.tag){
            case Tag.RELOP:
            match(Tag.RELOP);
            expr();
            expr();
            break;

            default:
            error("Erro in bexprp");
        }
    }

    private void expr(){
        switch(look.tag){
            case '(':
            match('(');
            exprp();
            match(')');
            break;

            case Tag.NUM:
            match(Tag.NUM);
            break;

            case Tag.ID:
            match(Tag.ID);
            break;

            default:
            error("error in expr");

        }
    }

    private void exprp(){
        switch(look.tag){
            case '+':
            match('+');
            exprlist();
            break;

            case '-':
            match('-');
            expr();
            expr();
            break;

            case '*':
            match('*');
            exprlist();
            break;

            case '/':
            match('/');
            expr();
            expr();
            break;
            
            default:
            error("Error in exprp");
        }
    }

    private void exprlist(){
        switch(look.tag){
            case '(':
            case Tag.ID:
            case Tag.NUM:
            expr();
            exprlistp();
            break;

            default:
            error("Error in exprlist");
        }
    }


    private void exprlistp(){
        switch(look.tag){
            case '(':
            case Tag.ID:
            case Tag.NUM:
            expr();
            exprlistp();
            break;

            case Tag.EOF:
            case ')':
            break;

            default:
            error("Error in exprlistp");
        }
    }


    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "/test/TestLexer.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Parser_prefix parser = new Parser_prefix(lex, br);
            parser.start();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}