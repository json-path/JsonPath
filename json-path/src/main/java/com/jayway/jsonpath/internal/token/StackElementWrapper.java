
package com.jayway.jsonpath.internal.token;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Predicate;
import com.jayway.jsonpath.spi.mapper.MappingException;

/**
 *
 */
public class StackElementWrapper implements Predicate.PredicateContext
{
    private Map cache;

    private TokenStack stack;
    private int idx;
    private TokenStackElement elem;

    public StackElementWrapper(TokenStack s, int i)
    {
        stack = s;
        idx = i;
        elem = stack.getStack().get(i);
    }

    public void reset(int i)
    {
        if (idx < i) {
            // we are moving down the stack, so no cache removals are necessary
            idx = i;
            elem = stack.getStack().get(i);
        } else if (idx > i) {
            // we just popped up the stack, so we need to remove some things
            // from the cache
            TokenStackElement elem = null;
            for (int j = i; j < idx; ++j) {
                elem = stack.getStack().get(j);
                popCache(this.elem);
                //cache.remove(this.elem);
            }
            this.elem = elem;
            idx = i;
        } else {
            TokenStackElement elem = stack.getStack().get(i);
            if (this.elem != elem) {
                popCache(this.elem); // just remove the previous thing
                this.elem = elem;
            }
        }
    }

    protected Object unwrap(TokenStackElement elem)
    {
        Object o = cache.get(elem);

        switch (elem.getType()) {
        case ARRAY_TOKEN:
        {
            ArrayToken token = (ArrayToken)elem;
            List l = (List)o;
            if (null == l) {

                l = new ArrayList(token.getIndex() + 1);
                cache.put(elem, l);
            }
            ((ArrayList)l).ensureCapacity(token.getIndex() + 1);
            //for (int i = 0; i < token.getIndex(); ++i) {
            //  l.add(null);
            //}
            l.set(token.getIndex(), unwrap(token.getValue()));
            return l;
        }
        case OBJECT_TOKEN:
        {
            ObjectToken token = (ObjectToken)elem;
            Map m = (Map)o;
            if (null == m) {

                m = new HashMap();
                cache.put(elem, m);
            }
            m.put(token.getKey(), unwrap(token.getValue()));
            return m;
        }
        case STRING_TOKEN:
        {
            if (o != null) return o;
            o = ((StringToken)elem).value;
            //cache.put(elem, o);
            return o;
        }
        case FLOAT_TOKEN:
        {
            if (o != null) return o;
            o = new Float(((FloatToken)elem).value);
            //cache.put(elem, o);
            return o;
        }
        case INTEGER_TOKEN:
        {
            if (o != null) return o;
            o = new Integer(((IntToken)elem).value);
            //cache.put(elem, o);
            return o;
        }
        default:
        {
            assert(false);
        }
        }

        return null;
    }

    public void popCache(TokenStackElement elem)
    {
        cache.remove(elem);
    }

    public Object item()
    {
        return unwrap(elem);
    }

    public <T> T item(Class<T> clazz) throws MappingException
    {
        return configuration().mappingProvider().map(
            item(), clazz, configuration());
    }

    public Object root()
    {
        return this; // ???? maybe unwrap the root element instead,
        // but that's very very very expensive
        // return unwrap(stack.getStack().get(0));
    }

    public Configuration configuration()
    {
        return stack.configuration();
    }
}

