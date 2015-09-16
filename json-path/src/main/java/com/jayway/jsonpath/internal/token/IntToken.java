
package com.jayway.jsonpath.internal.token;

/**
 *
 * @author Hunter Payne
 **/
public class IntToken extends TokenStackElement
{
    public int value;

    public IntToken(int f)
    {
        value = f;
    }

    public TokenType getType()
    {
        return TokenType.INTEGER_TOKEN;
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

// End IntToken.java
