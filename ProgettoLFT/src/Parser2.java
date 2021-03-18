import java.io.*;

class Parser2 {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Parser2(Lexer l, BufferedReader br) {
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
        if(look.tag == Tag.NUM || look.tag == '('){
            expr();
            match(Tag.EOF);
        }
        else{
            error("error in start");
        }
    }

    private void expr() {
        if(look.tag == Tag.EOF){
            error("error in expr");
        }
        term();
        exprp();
    }

    private void exprp() {
 switch (look.tag) {
    case '+':
    match('+');
    term();
    exprp();
    break;

    case '-':
    match('-');
    term();
    exprp();
    break;


    default:
    break;
 }
    }

    private void term() {
        switch(look.tag){
            case ')':
            error("error in term");

            default:
            fact();
            termp();
        }
    }

    private void termp() {
        switch(look.tag){
            case '*':
            match('*');
            fact();
            termp();
            
            break;

            case '/':
            match('/');
            fact();
            termp();
            break;

            case '(':
            error("Error in termp");
            break;

            
            default:
            break;
        }
    }

    private void fact() {
        switch(look.tag){
            case '(':
            match('(');
            expr();
            match(')');
            break;

            case Tag.NUM:
            match(Tag.NUM);
            break;

            default:
            error("on method fact");
            break;
            }
    }
  
    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "./try"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Parser parser = new Parser(lex, br);
            parser.start();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}