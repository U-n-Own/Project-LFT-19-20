import java.io.*;
import java.util.*;

public class Translator {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;
    
    SymbolTable st = new SymbolTable();
    CodeGenerator code = new CodeGenerator();
    int count=0;

    public Translator(Lexer l, BufferedReader br) {
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

    public void prog() {        
	// ... completare ...
	if(look.tag == '('){
        int lnext_prog = code.newLabel();
        stat(lnext_prog);
        code.emitLabel(lnext_prog);
        match(Tag.EOF);
        try {
        	code.toJasmin();
        }
        catch(IOException e) {
        	System.out.println("IO error\n");
        };
	}else
		error("Error in prog");
    }

    private void stat(int lnext){
        int lnext_stat = code.newLabel();
        if(look.tag == '('){
            match('(');
            statp(lnext_stat);
            match(')');
            code.emitLabel(lnext_stat);
        }else
            error("error in stat");
    }    

    
    private void statlist(int lnext){

        switch(look.tag){

            case '(':
            stat(lnext);
            statlistp(lnext); 
        break;

            default:
                error("Error statlist: "+ look);
        }
    }

    public void statp(int lnext) {
    int b_false, b_true, id_addr;
        switch(look.tag) {
			case '=':
			//we see the '=', we don't have nothing into the stack
				match('=');
					if (look.tag == Tag.ID) {
					//Here we load the ID into the stack, if it doesn't exist, we create it
						    id_addr = st.lookupAddress(((Word)look).lexeme);
							if (id_addr==-1) {
							    id_addr = count;
							st.insert(((Word)look).lexeme,count++);
						}  
                    //Expr here cause we work on a stack, and we need to put a value first, after this we do the istore of this value into the ID 
                    match(Tag.ID);
                    expr();
                    code.emit(OpCode.istore,id_addr);
					}else
						error("Error in statp after read"+ look);
			break;
			

            case Tag.WHILE:
            
                match(Tag.WHILE);
                int while_label = code.newLabel();
                code.emitLabel(while_label);
                b_true = code.newLabel();
                b_false = lnext;
                bexpr(b_true, b_false);
                code.emitLabel(b_true);
                int while_next = while_label;
                stat(while_next);
                code.emit(OpCode.GOto, while_next);

			break;
				
            case Tag.PRINT:

                match(Tag.PRINT);    
                exprlist('p');
                code.emit(OpCode.invokestatic, 1);
                    
			break;

            case Tag.DO:

                int do_label = code.newLabel();
                match(Tag.DO);
                statlist(lnext);
                code.emitLabel(do_label);//Can remove?

			break;

            case Tag.COND:

                match(Tag.COND); 
                b_true = code.newLabel();
                b_false = code.newLabel();
                bexpr(b_true, b_false);
                code.emitLabel(b_true);
                /*This could be wrong or lack something*/
                stat(lnext);
                code.emit(OpCode.GOto, lnext);
                code.emitLabel(b_false);
                elseopt(lnext);

			break;


            case Tag.READ:
                match(Tag.READ);
                if (look.tag==Tag.ID) {
                    int read_id_addr = st.lookupAddress(((Word)look).lexeme);
                    if (read_id_addr==-1) {
                        read_id_addr = count;
                        st.insert(((Word)look).lexeme,count++);
                    }                    
                    match(Tag.ID);
                    /*Zero to read*/
                    code.emit(OpCode.invokestatic,0);
                    code.emit(OpCode.istore,read_id_addr);   
                }
                else
                    error("Error in grammar (stat) after read with " + look);
                break;

            default:
				error("Error in statp:"+ look);
				break;
			}
    }

    private void exprp() {
        switch(look.tag) {

			case '+':
				match('+');
                exprlist('+');
                break;

			case '*':
				match('*');
                exprlist('*');
                break;

            case '-':
                match('-');
                expr();
                expr();
                code.emit(OpCode.isub);
                break;

			case '/':
                match('/');
                expr();
                expr();
                code.emit(OpCode.idiv);
                break;

		default:
			error("Error in exprp"+ look);
        }
    }
    
    private void statlistp(int lnext){
        if(look.tag == '('){
            stat(lnext);    
            statlistp(lnext);
        }else
            if(look.tag == ')'){
            /*Do nothing*/
            }else
                error("error in statlistp: "+ look);
    }

    private void expr(){

        switch(look.tag){

            case Tag.NUM:
                code.emit(OpCode.ldc, ((NumberTok)look).lexeme);
                match(Tag.NUM);
            break;

            case Tag.ID:
            int   read_id_addr = st.lookupAddress(((Word)look).lexeme);
                if (read_id_addr==-1) {
                    read_id_addr = count;
                    st.insert(((Word)look).lexeme,count++);
                }      
                code.emit(OpCode.iload, read_id_addr);
                match(Tag.ID);
            break;

            case '(':
                match('(');
                exprp();
                match(')');
            break;

            default:
                error("error in expr: "+ look);

        }

    }

    private void exprlist(char op_type){

        if(look.tag == Tag.NUM || look.tag == Tag.ID || look.tag == '('){
            expr();
            exprlistp(op_type);
        }else/*Not sure if necessary*/
            /*if(op_type == 'p')
                code.emit(OpCode.invokestatic, 1);*/
            error("Error in exprlist: "+ look);
    }

    private void exprlistp(char op_type){
        
        if(look.tag == Tag.NUM || look.tag == Tag.ID || look.tag == '('){
            expr();
            if(op_type == '+'){
                code.emit(OpCode.iadd);
            }else 
                if(op_type == '*'){ 
                    code.emit(OpCode.imul);
                }else
                    if(op_type == 'p'){
                        code.emit(OpCode.invokestatic, 1);
                    }
            exprlistp(op_type);
        }
    }

    private  void elseopt(int lnext){
        if(look.tag == '('){
            match('(');
            match(Tag.ELSE);
            stat(lnext);
            match(')');
        }else
            if(look.tag == ')'){
                 //Do nothing
            }else
                error("error in elseopt: "+ look);
    
    }


    private void bexpr(int b_true, int b_false){
        if(look.tag == '('){
            match('(');
            bexprp(b_true, b_false);
            match(')');
        }else
            error("Error in bexpr: "+ look);
    }
    //Operation list: <, >, ==, <=, <>, >=
    private void bexprp(int b_true, int b_false){

        if(look.tag == Tag.RELOP){
            int operation_type = -1;
            
            if(((Word)look).lexeme.equals("<"))
                operation_type = 0;
                else
                    if(((Word)look).lexeme.equals(">"))
                        operation_type = 1;
                        else
                            if(((Word)look).lexeme.equals("=="))
                                operation_type = 2;
                                else
                                    if(((Word)look).lexeme.equals("<="))
                                    operation_type = 3;
                                    else
                                        if(((Word)look).lexeme.equals("<>"))
                                        operation_type = 4;
                                        else
                                            if(((Word)look).lexeme.equals(">="))
                                                operation_type = 5;
            match(Tag.RELOP);
            expr();
            expr();

            switch(operation_type){
                // <
                case 0:
                    code.emit(OpCode.if_icmplt, b_true);
                break;
                // >
                case 1:
                    code.emit(OpCode.if_icmpgt, b_true);
                break;
                // ==
                case 2:
                    code.emit(OpCode.if_icmpeq, b_true);
                break;
                // <=
                case 3:
                    code.emit(OpCode.if_icmple, b_true);
                break;
                // <>
                case 4:
                    code.emit(OpCode.if_icmpne, b_true);
                break;
                // >=
                case 5:
                    code.emit(OpCode.if_icmpge, b_true);
                break;

                default:
                    error("error in bexpr with RELOP type");
            }
        code.emit(OpCode.GOto, b_false);
        }
        else
            error("erro in bexpr: "+ look);

    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "C:\\Users\\cenzo\\OneDrive\\Desktop\\ProgettoLFT_19_20_Vincenzo_Gargano\\ProgettoLFT\\test\\"+args[0]; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Translator traduttore = new Translator(lex, br);
            traduttore.prog();
            System.out.println("Input OK");
            br.close();
        } catch (Exception e) {e.printStackTrace();
        }
    }
}