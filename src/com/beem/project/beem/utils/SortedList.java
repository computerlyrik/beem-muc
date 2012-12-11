/*
    BEEM is a videoconference application on the Android Platform.

    Copyright (C) 2009 by Frederic-Charles Barthelery,
                          Jean-Manuel Da Silva,
                          Nikita Kozlov,
                          Philippe Lago,
                          Jean Baptiste Vergely,
                          Vincent Veronis.

    This file is part of BEEM.

    BEEM is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    BEEM is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with BEEM.  If not, see <http://www.gnu.org/licenses/>.

    Please send bug reports with examples or suggestions to
    contact@beem-project.com or http://dev.beem-project.com/

*/
package com.beem.project.beem.utils;

import java.util.List;
import java.util.Comparator;
import java.util.ListIterator;
import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;

/**
 * This class add a sort by insertion to a List.
 * All methods which allow you to insert an object at a specific index
 * will throw an UnsupportedOperationException.
 *
 * @author Da Risk <da_risk@beem-project.com>
 * @param <E> the type of elements maintained by this list
 */
public class SortedList<E> implements List<E> {

    private final List<E> mBackend;
    private final Comparator<? super E> mComparator;

    /**
     * Create a SortedList. The existing elements will be sorted.
     *
     * @param list list to sort
     * @param mComparator mComparator to use.
     */
    public SortedList(final List<E> list, final Comparator<? super E> mComparator) {
	this.mComparator = mComparator;
	this.mBackend = list;
	Collections.sort(mBackend, mComparator);
    }

    @Override
    public int size() {
	return mBackend.size();
    }

    @Override
    public boolean isEmpty() {
	return mBackend.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
	return mBackend.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
	return new SortedListIterator<E>(mBackend.listIterator());
    }

    @Override
    public Object[] toArray() {
	return mBackend.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
	return mBackend.toArray(a);
    }

    @Override
    public boolean add(E e) {
	for (ListIterator<E> it = mBackend.listIterator(); it.hasNext();) {
	    if (mComparator.compare(e, it.next()) < 0) {
		if (it.hasPrevious()) {
		    it.previous();
		}
		it.add(e);
		return true;
	    }
	}
	mBackend.add(e);
	return true;
    }

    @Override
    public boolean remove(Object o) {
	return mBackend.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
	return mBackend.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
	boolean result = false;
	for (E e : c) {
	    boolean t = add(e);
	    if (t) {
		result = t;
	    }
	}
	return result;
    }

    /**
     * Add all the elements in the specified collection.
     * The index param is ignored.
     *
     * @param index ignored
     * @param c collection containing elements to be added to this list
     * @return true if this list changed as a result of the call
     */
    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
	return addAll(c);
    }

    /**
     * Add all the elements in the specified collection.
     * The index param is ignored.
     *
     * @param l collection containing elements to be added to this list
     * @return true if this list changed as a result of the call
     * @see addAll(Collection)
     */
    public boolean addAll(SortedList<? extends E> l) {
	if (!l.isEmpty()) {
	    if (mBackend.isEmpty()) {
		return mBackend.addAll(l);
	    }
	    boolean result = false;
	    E myfirst = mBackend.get(0);
	    E last = l.get(l.size() - 1);
	    E mylast = mBackend.get(mBackend.size() - 1);
	    E first = l.get(0);
	    if (mComparator.compare(last, myfirst) < 0) {
		result = mBackend.addAll(0, l);
	    } else if (mComparator.compare(first, mylast) > 0) {
		result = mBackend.addAll(l);
	    } else {
		Collection<? extends E> c = l;
		result = addAll(c);
	    }
	    return result;
	}
	return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
	return mBackend.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
	return mBackend.retainAll(c);
    }

    @Override
    public void clear() {
	mBackend.clear();
    }

    @Override
    public boolean equals(Object o) {
	return mBackend.equals(o);
    }

    @Override
    public int hashCode() {
	return mBackend.hashCode();
    }

    @Override
    public E get(int index) {
	return mBackend.get(index);
    }

    @Override
    public E set(int index, E element) {
	throw new UnsupportedOperationException("set() is not supported in SortedList");
    }

    @Override
    public void add(int index, E element) {
	throw new UnsupportedOperationException("add at specific index is not supported in SortedList");
    }

    @Override
    public E remove(int index) {
	return mBackend.remove(index);
    }

    @Override
    public int indexOf(Object o) {
	return mBackend.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
	return mBackend.lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
	return new SortedListIterator<E>(mBackend.listIterator());
    }

    @Override
    public ListIterator<E> listIterator(int index) {
	return new SortedListIterator<E>(mBackend.listIterator(index));
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
	return mBackend.subList(fromIndex, toIndex);
    }

    @Override
    public String toString() {
	return mBackend.toString();
    }

    /**
     * A SortedList.iterator don't allow list modification.
     * It use the mBackend iterator for the other operations.
     */
    private class SortedListIterator<E> implements ListIterator<E> {

	private ListIterator<E> mIt;

	/**
	 * Construct SortedList.Iterator.
	 *
	 * @param iterator the iterator of the backend list
	 */
	SortedListIterator(final ListIterator<E> iterator) {
	    mIt = iterator;
	}

	@Override
	public void add(E e) {
	    throw new UnsupportedOperationException("add() not supported in SortedList iterator");
	}

	@Override
	public boolean hasNext() {
	    return mIt.hasNext();
	}

	@Override
	public E next() {
	    return mIt.next();
	}

	@Override
	public boolean hasPrevious() {
	    return mIt.hasPrevious();
	}

	@Override
	public E previous() {
	    return mIt.previous();
	}

	@Override
	public int nextIndex() {
	    return mIt.nextIndex();
	}

	@Override
	public int previousIndex() {
	    return mIt.previousIndex();
	}

	@Override
	public void remove() {
	    mIt.remove();
	}

	@Override
	public void set(E e) {
	    throw new UnsupportedOperationException("set () not supported in SortedList iterator");
	}
    }
}
