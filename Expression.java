package cxz173430;
/**
 * @author 		Churong Zhang 
 * 				cxz173430
 * 				Sept 25 2018
 * 				Dr. Raghavachari
 * 				This class is used to convert and evaluate math infix expression to postfix expression 
 * 				and expression tree
 * 				//Project 1////
 */
import java.util.List;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Stack;
import java.io.FileNotFoundException;
import java.io.File;

import java.lang.NumberFormatException;


/** Class to store a node of expression tree
    For each internal node, element contains a binary operator
    List of operators: +|*|-|/|%|^
    Other tokens: (|)
    Each leaf node contains an operand (long integer)
*/

public class Expression {
	/*
	 * enum value for different tokens
	 * use to identify different tokens
	 */
    public enum TokenType {  // NIL is a special token that can be used to mark bottom of stack
	PLUS, TIMES, MINUS, DIV, MOD, POWER, OPEN, CLOSE, NIL, NUMBER, 
    }
    /*
     * The Token class is use to store operator or operand
     */
    public static class Token {
    /*
     * 		to identify whether it is a operand or operator
     */
	TokenType token;
	
	/*
	 * 		for the precedence of operator
	 */
	int priority; 	
	/*
	 * 		used to store number of token = NUMBER
	 */
	Long number;
	/*
	 *		store the string value of the token
	 */
	String string;		
	/*
	 * Constructor for operator token
	 * @param op the TokenType
	 * @param pri the precedence of the operator
	 * @param tok the string value of the token
	 */
	Token(TokenType op, int pri, String tok) {
	    token = op;
	    priority = pri;
	    number = null;
	    string = tok;
	}
	/*
	 * Constructor for number.  To be called when other options have been exhausted.
	 * @param tok the string value of the token
	 */
	Token(String tok) {
	    token = TokenType.NUMBER;
	    try {
	    	number = Long.parseLong(tok);
	    }
	    catch(NumberFormatException e)
	    {
	    	System.out.println("Invalid Input for token: \"" + tok + "\"");
	    	throw new NumberFormatException();
	    }
	    string = tok;
	}
	/*
	 * check if the token is an operand 
	 * @return true if the token is an operand
	 */
	boolean isOperand() { return token == TokenType.NUMBER; }
	/*
	 * get the number value if the token is an operand
	 * @return the value of an operand token
	 */
	public long getValue() {
	    return isOperand() ? number : 0;
	}
	/*
	 * return the string value of the token
	 * @return the string value of the token
	 * @see java.lang.Object#toString()
	 */
	public String toString() { return string; }
    }
    /*
     * element store whehter the token is an operand or operator
     */
    Token element;
    /*
     * the left and right is use when the element is an operator
     */
    Expression left, right;

    // Create token corresponding to a string
    // tok is "+" | "*" | "-" | "/" | "%" | "^" | "(" | ")"| NUMBER
    // NUMBER is either "0" or "[-]?[1-9][0-9]*
    
    /*
     * check what token is the string value
     * @param tok the string value of an incoming token
     * @return the new token that was created base on the string value
     */
    static Token getToken(String tok) {  // To do
	Token result;
	switch(tok) {
	case "+":
	    result = new Token(TokenType.PLUS, 1, tok);  // modify if priority of "+" is not 1
	    break;
	case "-":
	    result = new Token(TokenType.MINUS, 1, tok);  
	    break;
	case "*":
	    result = new Token(TokenType.TIMES, 2, tok);  
	    break;
	case "/":
	    result = new Token(TokenType.DIV, 2, tok);  
	    break;
	case "%":
	    result = new Token(TokenType.MOD, 2, tok);  
	    break;
	case "^":
	    result = new Token(TokenType.POWER, 3, tok);  
	    break;
	case "(":
	    result = new Token(TokenType.OPEN, 4, tok);  
	    break;
	case ")":
	    result = new Token(TokenType.CLOSE, 4, tok);  
	    break;
	  
	default:
	    result = new Token(tok);
	    break;
	}
	return result;
    }
    /*
     * Default Constructor
     * create a Expression with nothing in it
     */
    private Expression() {
	element = null;
    }
    /*
     * create an expression with a operator and the left and right expression
     * @param oper the operator of the new expression
     * @param left the left expression
     * @param right the right expression
     */
    private Expression(Token oper, Expression left, Expression right) {
	this.element = oper;
	this.left = left;
	this.right = right;
    }
    /*
     * Constructor for a number
     * @param num the token contain the number
     */
    private Expression(Token num) {
	this.element = num;
	this.left = null;
	this.right = null;
    }


    /* Given a list of tokens corresponding to an infix expression,
     * @param exp the list of token that corresponding to it.
     * @return the expression tree
     */
    public static Expression infixToExpression(List<Token> exp) { 
    Stack<Token> operator = new Stack<>();
    operator.push(new Token(TokenType.NIL, 0 , "|"));
    Stack<Expression> expression = new Stack<Expression>();
    Iterator<Token> iter = exp.iterator();
    int precedence = 1;
    while(iter.hasNext())
    {
    	Token token = iter.next();
    	if(token.isOperand())
    	{
    		expression.push(new Expression(token));
    	}
    	else
    	{

    		if(token.token == TokenType.OPEN)
    		{
    			precedence = precedence * 5;
   				operator.push(token);
   			}
   			else if(token.token == TokenType.CLOSE)
   			{
   				while(operator.peek().token != TokenType.OPEN)
    			{
    				Expression right = expression.pop();
    				Expression left = expression.pop();
   					Expression tree = new Expression(operator.pop(), left, right);
   					expression.push(tree);
    				
    			}
    			operator.pop();
    			precedence = precedence/5;
    		}
    		else if(token.priority * precedence <= operator.peek().priority)
   			{
   				while(operator.peek().priority >= token.priority * precedence )
   				{
   					Expression right = expression.pop();
   					Expression left = expression.pop();
   					Expression tree = new Expression(operator.pop(), left, right);
   					expression.push(tree);
    					
    			}
    			token.priority = token.priority * precedence;
    			operator.push(token);
   			}
   			else
   			{
   				token.priority = token.priority * precedence;
       			operator.push(token);
    		}
    	}
    }
    while(operator.peek().token != TokenType.NIL)
    {
    	Expression right = expression.pop();
    	Expression left;
		if(expression.isEmpty())
		{
			left = new Expression(new Token("0"));
		}
		else
		{
			left = expression.pop();
		}
		Expression tree = new Expression(operator.pop(), left, right);
		expression.push(tree);
    }
    
	return expression.pop();
    }

   
    /*
     * Given a list of tokens corresponding to an infix expression,
     * @param exp the list of token that corresponding to it.
     * @return the postfix list of token that is equivalent to the infix
     */
    public static List<Token> infixToPostfix(List<Token> exp) { 
	Stack<Token> operator = new Stack<>();
	operator.push(new Token(TokenType.NIL, 0 , "|"));
	List<Token> postFix = new LinkedList<>();
	Iterator<Token> iter = exp.iterator();
    int precedence = 1;
    while(iter.hasNext())
    {
    	Token token = iter.next();
    	if(token.isOperand())
    	{
    		postFix.add(token);
    	}
    	else
    	{

    		if(token.token == TokenType.OPEN)
    		{
    			precedence = precedence * 5;
   				operator.push(token);
   			}
   			else if(token.token == TokenType.CLOSE)
   			{
   				while(operator.peek().token != TokenType.OPEN)
    			{
    				postFix.add(operator.pop());
    			}
    			operator.pop();
    			precedence = precedence/5;
    		}
    		else if(token.priority * precedence <= operator.peek().priority)
   			{
   				while(operator.peek().priority >= token.priority * precedence )
   				{
   					postFix.add(operator.pop());
    			}
    			token.priority = token.priority * precedence;
    			operator.push(token);
   			}
   			else
   			{
   				token.priority = token.priority * precedence;
       			operator.push(token);
    		}
    	}
    }
    while(operator.peek().token != TokenType.NIL)
    {
    	postFix.add(operator.pop());
    }
    return postFix;
    }

 
    /*
     * Given a postfix expression, evaluate it and return its value.
     * Precondition: the parameter list must be in PostFix
     * @param exp the postfix expression
     * @return the answer for the postfix expression
     */
    public static long evaluatePostfix(List<Token> exp) {  // To do
	Iterator<Token> iter = exp.iterator();
	Stack<Token> operand = new Stack<>();
	while(iter.hasNext())
	{
		Token token = iter.next();
		if(token.isOperand())
		{
			operand.push(token);
		}
		else
		{
			long result = 0;
			long right = operand.pop().number;
			long left = operand.pop().number;
			
			switch(token.token)
    		{
    		case PLUS:
    			result = left + right;
    			break;
    		case TIMES:  
    			result = left * right;
    			break;
    		case MINUS:  
    			result = left - right;
    			break;
    		case DIV:  
    			result = left / right;
    			break;
    		case MOD:  
    			result = left % right;
    			break;
    		case POWER:  
    			result = (long)Math.pow(left, right);
    			break;
    		default:
    		}
			operand.push(new Token(""+ result));
		}
	}
	return operand.pop().number;
    }

    /*
     * Given an expression tree, evaluate it and return its value.
     * Precondition: the tree must be a valid expression tree
     * @param tree the expression tree
     * @return the answer for the expression tree
     */
    public static long evaluateExpression(Expression tree) {  
    	if(tree.left == null && tree.right == null)
    	{
    		return tree.element.number;
    	}
    	else
    	{
    		long result = 0;
    		long left = evaluateExpression(tree.left);
    		long right = evaluateExpression(tree.right);
    		switch(tree.element.token)
    		{ // PLUS, TIMES, MINUS, DIV, MOD, POWER,
    		case PLUS:
    			result = left + right;
    			break;
    		case TIMES:  
    			result = left * right;
    			break;
    		case MINUS:  
    			result = left - right;
    			break;
    		case DIV:  
    			result = left / right;
    			break;
    		case MOD:  
    			result = left % right;
    			break;
    		case POWER:  
    			result = (long)Math.pow(left, right);
    			break;
    		default:
    			
    		}
    		return result;
    	}
	
    }

    // sample main program for testing
    public static void main(String[] args) throws FileNotFoundException {
	Scanner in;
	
	
	if (args.length > 0) {
	    File inputFile = new File(args[0]);
	    in = new Scanner(inputFile);
	} else {
	    in = new Scanner(System.in);
	}
	// test from a file
	in = new Scanner(new File("input.txt"));
	
	int count = 0;
	while(in.hasNext()) {
	    String s = in.nextLine();
	    List<Token> infix = new LinkedList<>();
	    Scanner sscan = new Scanner(s);
	    int len = 0;
	    while(sscan.hasNext()) {
		infix.add(getToken(sscan.next()));
		len++;
	    }
	    if(len > 0) {
		count++;
		System.out.println("Expression number: " + count);
		System.out.println("Infix expression: " + infix);
		
		
		
		Expression exp = infixToExpression(infix);
		List<Token> post = infixToPostfix(infix);
		System.out.println("Postfix expression: " + post);
		long pval = evaluatePostfix(post);
		long eval = evaluateExpression(exp);
		
		System.out.println("Postfix eval: " + pval + " Exp eval: " + eval + "\n");
	    }
	}
    }
}
