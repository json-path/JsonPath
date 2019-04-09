/**
 * 
 */
package com.jayway.jsonpath.spi.json;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 *
 */
public abstract class JsonArrayWrapper<O> implements JsonWrapper<O>, List {
	
	class ListIteratorWrapper implements ListIterator {
		int pos = -1;
		
		ListIteratorWrapper(int pos){
			if(pos < -1 || pos > size()) {
				this.pos = -1;
			} else {
				this.pos = pos;
			}
		}
		
		@Override
		public boolean hasNext() {
			return pos < size()-1;
		}

		@Override
		public Object next() {
			if(pos >= size()-1) {
				return null;
			}
			pos+=1;
			return get(pos);
		}

		@Override
		public boolean hasPrevious() {
			return (pos > 0);
		}

		@Override
		public Object previous() {
			if(pos <= 0) {
				return null;
			}
			pos-=1;
			return get(pos);
		}

		@Override
		public int nextIndex() {
			if(pos >= size()-1) {
				return size()-1;
			}
			return pos+1;
		}

		@Override
		public int previousIndex() {
			if(pos <= 0) {
				return 0;
			}
			return pos-1;
		}

		@Override
		public void remove() {	
			if(pos < 0 || pos > size()-1) {
				return;
			}
			int offset = (pos == size()-1)?-1:0;
			JsonArrayWrapper.this.remove(pos);
			pos+=offset;
		}

		@Override
		public void set(Object e) {
			if(pos < 0 || pos > size()-1) {
				return;
			}	
			JsonArrayWrapper.this.set(pos,e);
		}

		@Override
		public void add(Object e) {	
			if(pos < 0 || pos > size()-1) {
				return;
			}
			JsonArrayWrapper.this.add(pos,e);
		}
	};

	protected abstract void doAdd(int index, Object e);

	protected abstract Object doSet(int index, Object unwrap);

	protected final O instance;
	protected final Class<?> objectClass; 
	protected final Class<?> arrayClass;
	protected final Class<?> objectWrapperClass; 
	protected final Class<?> arrayWrapperClass;

	public JsonArrayWrapper(O instance, 
			Class<?> objectClass, 
			Class<O> arrayClass, 
			Class<?> objectWrapperClass, 
			Class<?> arrayWrapperClass ) {
		this.objectClass = objectClass;
		this.arrayClass = arrayClass;
		this.objectWrapperClass = objectWrapperClass;
		this.arrayWrapperClass = arrayWrapperClass;
		
		O arrayInstance = instance;
		
		if(arrayInstance == null) {
			try {
				arrayInstance = (O) this.arrayClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new NullPointerException("No attached instance");
			}
		} else {
			arrayInstance = instance;
		}
		this.instance = arrayInstance;
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public O unwrap() {
		return this.instance;
	}

	@Override
	public boolean contains(Object o) {
		Iterator iterator = iterator();
		for(;iterator.hasNext();) {
			Object obj = iterator.next();
			if((o==null && obj==null) || (o!=null && o.equals(obj))){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public Object[] toArray() {
		Object[] array = new Object[size()];
		int pos=0;
		Iterator iterator = iterator();
		for(;iterator.hasNext();) {
			array[pos++]= iterator.next();
		}
		return array;
	}

	@Override
	public Object[] toArray(Object[] a) {
		Object[] array = null;
		if(a == null || a.length < size()) {
			array = new Object[size()];
		} else {
			array = a;
		}		
		int pos=0;
		Iterator iterator = iterator();
		for(;iterator.hasNext();) {
			array[pos++]= iterator.next();
		}
		return array;
	}

	@Override
	public boolean add(Object e) {
		if(e!=null && JsonWrapper.class.isAssignableFrom(e.getClass())) {
			return add(((JsonWrapper)e).unwrap());
		}
		doAdd(size(), e);
		return true;
	}

	@Override
	public void add(int index, Object e) {
		if(e!=null && JsonWrapper.class.isAssignableFrom(e.getClass())) {
			doAdd(index, ((JsonWrapper)e).unwrap());
			return;
		}
		doAdd(index, e);
	}

	@Override
	public Object set(int index, Object e) {
		if(e!=null && JsonWrapper.class.isAssignableFrom(e.getClass())) {
			return doSet(index, ((JsonWrapper)e).unwrap());
		}
		return doSet(index, e);
	}

	@Override
	public boolean remove(Object o) {
		int index = indexOf(o);
		if(index < 0) {
			return false;
		}
		Object obj = remove(index);		
		return (o == null)?obj==null:true;
	}

	@Override
	public boolean containsAll(Collection c) {
		Iterator iterator = c.iterator();
		for(;iterator.hasNext();) {
			if(!contains(iterator.next())){
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean addAll(Collection c) {
		int ind=-1;
		Iterator iterator = c.iterator();
		for(;iterator.hasNext();) {
			ind+=add(iterator.next())?1:0;
		}
		return ind < 0;
	}

	@Override
	public boolean addAll(int index, Collection c) {
		int ind=-1;
		Iterator iterator = c.iterator();
		for(;iterator.hasNext();) {			
			add(index++,iterator.next());
			ind+=1;
		}
		return ind > -1;
	}

	@Override
	public boolean removeAll(Collection c) {
		int ind=-1;
		Iterator iterator = iterator();
		for(;iterator.hasNext();) {			
			if(c.contains(iterator.next())) {
				iterator.remove();
				ind+=1;
			}
		}
		return ind > -1;
	}

	@Override
	public boolean retainAll(Collection c) {
		int ind=-1;
		Iterator iterator = iterator();
		for(;iterator.hasNext();) {			
			if(!c.contains(iterator.next())) {
				iterator.remove();
				ind+=1;
			}
		}
		return ind > -1;
	}

	@Override
	public int indexOf(Object o) {
		int pos = -1;
		int ind = -1;
		Iterator iterator = iterator();
		Object obj = null;
		for(;iterator.hasNext();) {
			pos+=1;
			obj = iterator.next();
			if((o == null && obj==null) || (o!=null && o.equals(obj))){
				ind = pos;
				break;
			}
		}
		return ind;
	}

	@Override
	public int lastIndexOf(Object o) {
		int pos = size();
		int ind = -1;
		ListIterator li = listIterator(pos);	
		for(;li.hasPrevious();) {
			pos-=1;
			Object obj = li.previous();
			if((o == null && obj == null)||(o!=null && o.equals(obj))) {
				ind = pos;
				break;
			}
		}
		return ind;
	}

	@Override
	public Iterator iterator() {		
		return listIterator();
	}
	
	@Override
	public ListIterator listIterator() {		
		return new ListIteratorWrapper(-1);
	}

	@Override
	public ListIterator listIterator(int index) {
		return new ListIteratorWrapper(index);
	}
	
	@Override
	public void clear() {
		while(!isEmpty()) {
			remove(0);
		}		
	}

	public String toString() {
		return this.instance.toString();
	}
	
	public boolean equals(Object o){
		if(o == null) {
			return false;
		}
		if (o == this) {
            return true;
		}
		if(arrayClass.isAssignableFrom(o.getClass())) {
			return this.instance.equals(o);
		}
        if (!(o instanceof List))
            return false;

        ListIterator e1 = listIterator();
        ListIterator e2 = ((List) o).listIterator();
        while (e1.hasNext() && e2.hasNext()) {
            Object o1 = e1.next();
            Object o2 = e2.next();            
            Object effectiveValue = o1; 
            try {
	        	if (o1!=null && objectClass.isAssignableFrom(o1.getClass())) {
					effectiveValue =  objectWrapperClass.getConstructor(objectClass).newInstance(o1);        				
	    		} else if (o1!=null &&  arrayClass.isAssignableFrom(o1.getClass())) {
	    			effectiveValue =  arrayWrapperClass.getConstructor(arrayClass).newInstance(o1);        		
	    		}
			} catch(Exception ex) {
				return false;
			}
            if (!(effectiveValue==null ? o2==null : effectiveValue.equals(o2)))
                return false;
        }
        return !(e1.hasNext() || e2.hasNext());
	}
	
	@Override
	public List subList(int fromIndex, int toIndex) {
		return null;
	}
}
