
package com.jayway.jsonpath.internal.token;

/**
 *
 * @author Hunter Payne
 **/
public class StringToken extends TokenStackElement
{
    public String value;

    public StringToken(String s)
    {
        value = s;
    }

    public TokenType getType()
    {
        return TokenType.STRING_TOKEN;
    }

    public TokenStackElement getValue()
    {
        return null;
    }

    public void setValue(TokenStackElement elem)
    {
        throw new RuntimeException();
    }
}

// End StringToken.java
