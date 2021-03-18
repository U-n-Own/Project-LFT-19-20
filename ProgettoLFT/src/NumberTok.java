public class NumberTok extends Token {
    
    //Initialization of lexeme_number
    public int lexeme; 
    
    public NumberTok(int tag, int number){
        super(tag);
        lexeme = number;
    }
    //Printing token
    public String toString(){
        return "<"  + tag + "," + lexeme + ">";
    }


}