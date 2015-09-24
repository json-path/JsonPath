
package com.jayway.jsonpath.internal.token;

/**
 *
 * @author Hunter Payne
 **/
public class FloatToken implements TokenStackElement
{
    public float value;

    public FloatToken(float f)
    {
        value = f;
    }

    public TokenType getType()
    {
        return TokenType.FLOAT_TOKEN;
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

// End FloatToken.java
