import java.io.*; 
import java.util.*;


public class Lexer {

    public static int line = 1;
    private char peek = ' ';
    
    private void readch(BufferedReader br) {
        try {
            peek = (char) br.read();
        } catch (IOException exc) {
            peek = (char) -1; // ERROR
        }
    }

    public Token lexical_scan(BufferedReader br) {
        while (peek == ' ' || peek == '\t' || peek == '\n'  || peek == '\r') {
            if (peek == '\n') line++;
            readch(br);
        }
        
        switch (peek) {
            case '!':
                peek = ' ';
                return Token.not;
    //Done!
	// ... gestire i casi di (, ), {, }, +, -, *, /, =,, == ; ... //

            case '&':
                readch(br);
                if (peek == '&') {
                    peek = ' ';
                    return Word.and;
                } else {
                    System.err.println("Erroneous character"  + " after & : "  + peek );
                    return null;
                }
            
            case '(':
                peek = ' ';
                return Token.lpt;

            case ')': 
                peek = ' ';
                return Token.rpt;
            
            case '{':
                peek = ' ';
                return Token.lpg;
            
            case '}': 
                peek = ' ';
                return Token.rpg;
            
            case '+':
                peek = ' ';
                return Token.plus;
             
            case '-':
                peek = ' ';
                return Token.minus;
             
            case '*':
                peek = ' ';
                return Token.mult;
            //Commenti: 
            case '/':
            readch(br);
            if(peek == '/'){
                while(peek != '\n' && peek != (char)-1){ 
                readch(br);/*Continuo a leggere*/
                if(peek == (char)-1)
                    return new Token(Tag.EOF);//Se trovo EOF ho terminato
                }
                return lexical_scan(br);
            }else if(peek == '*'){/*  Commento con asterisco */
                    readch(br);
                    while(peek != Tag.EOF){
                        if(peek == '\n'){
                            line++;
                        }

                    else if(peek == '*'){
                        readch(br);
                        if(peek == '/'){
                            peek = ' ';
                            return lexical_scan(br);
                            }

                        }  
                    readch(br);
                    }
                    if(peek == (char)Tag.EOF){
                        System.err.println("Unclosed comment");
                        return null;  
                            }
                    return lexical_scan(br);
            }else          
            return Token.div;
            
             
            case '=':
            readch(br);
            if(peek == '='){
                peek = ' ';
                return Word.eq;
            }else   {
                        return Token.assign;
                    }
            
            case ';':
                peek = ' ';
                return Token.semicolon;

    //Done!
	// ... gestire i casi di ||, <, >, <=, >=, <> ... //
          
            case '|':
            readch(br);
            if(peek == '|'){
                peek = ' ';
                return Word.or;
            }else   {
                    System.err.println("Erroneous character: " + peek );
                    return null;
                    }
            
            case '<':
            readch(br);
            if(peek == '='){
                    peek = ' ';
                    return Word.le;
            }else if(peek =='>'){
                    peek = ' ';
                    return Word.ne; 

            }else   return Word.lt;

            case '>':
            readch(br);
            if(peek == '='){
                    peek = ' ';
                    return Word.ge;

            }else   return Word.gt;                
            
            case (char)-1:
                return new Token(Tag.EOF);

            default:
                if(Character.isLetter(peek) || peek == '_') {
                    String id = "";
	// ... gestire il caso degli identificatori e delle parole chiave // DONE!
                    while(Character.isLetter(peek) || peek == '_' || Character.isDigit(peek)){
                        //concatenating
                        id = id + peek;
                        readch(br);
                    }
                    if(id.equals("cond")){return Word.cond;}
                    else if(id.equals("when")){return Word.when;}
                    else if(id.equals("then")){return Word.then;}
                    else if(id.equals("else")){return Word.elsetok;}
                    else if(id.equals("while")){return Word.whiletok;}
                    else if(id.equals("do")){return Word.dotok;}
                    else if(id.equals( "seq")){return Word.seq;}
                    else if(id.equals("print")){return Word.print;}
                    else if(id.equals("read")){return Word.read;}

                    if(id == "_"){
                        System.out.println("Error id can't be: "+ id);
                        return null;
                    }

                    Word Id = new Word(Tag.ID, id); 
                    return Id;

                }else if(Character.isDigit(peek)) {
    // ... gestire il caso dei numeri ... //    Done!
                        String num = "";
                        NumberTok toknum = null;
                    
                        while(Character.isDigit(peek)){
                            num = num + peek;
                            readch(br);
                        }
                        if(num.length() > 1){
                        //Managing the first is 0, this is not printed, why?
                        if(num.charAt(0)=='0'){
                        System.err.println("Erroneous character: " + peek );
                        return null;
                        }
                        }
                        toknum = new NumberTok(Tag.NUM, Integer.parseInt(num));
                        return toknum;

                } else {
                        System.err.println("Erroneous character: " + peek );
                        return null;
                }
         }
    }
		
    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "C:/Users/Vincenzo/Desktop/Laboratorio_LFT/Lexer&Parser/TestLexer.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Token tok;
            do {
                tok = lex.lexical_scan(br);
                System.out.println("Scan: " + tok);
            } while (tok.tag != Tag.EOF);
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }

}