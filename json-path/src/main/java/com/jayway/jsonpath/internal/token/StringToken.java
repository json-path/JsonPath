
package com.jayway.jsonpath.internal.token;

import java.util.regex.Pattern;

/**
 *
 * @author Hunter Payne
 **/
public class StringToken extends TokenStackElement
{
    public String value;
    public Pattern pattern;

    public StringToken(String s)
    {
        value = s;
        pattern = null;
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
