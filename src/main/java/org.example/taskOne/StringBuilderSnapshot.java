package org.example.taskOne;


import java.util.ArrayDeque;
import java.util.Arrays;

public class StringBuilderSnapshot implements CharSequence {
    static final int MAX_LENGTH = Integer.MAX_VALUE >> 1;

    private char[] value;

    private int count;

    private ArrayDeque<Snapshot> previousStates;

    private static final char[] EMPTYVALUE = new char[0];

    public StringBuilderSnapshot() {
        value = EMPTYVALUE;
        this.previousStates = new ArrayDeque<>();
    }


    public StringBuilderSnapshot(int capacity) {
        if (capacity >= MAX_LENGTH) {
            throw new OutOfMemoryError("String size is " + capacity +
                    ", should be less than " + MAX_LENGTH);
        }
        this.value = new char[capacity];
        this.previousStates = new ArrayDeque<>();
    }


    public StringBuilderSnapshot(String str) {
        int length = str.length();
        int capacity = (length < Integer.MAX_VALUE - 16)
                ? length + 16 : Integer.MAX_VALUE;
        if (capacity >= MAX_LENGTH) {
            throw new OutOfMemoryError("String size is " + capacity +
                    ", should be less than " + MAX_LENGTH);
        }
        this.value = new char[capacity];
        this.previousStates = new ArrayDeque<>();
        append(str);
        previousStates.pop();
    }

    public StringBuilderSnapshot(CharSequence seq) {
        int length = seq.length();
        int capacity = (length < Integer.MAX_VALUE - 16)
                ? length + 16 : Integer.MAX_VALUE;
        if (capacity >= MAX_LENGTH) {
            throw new OutOfMemoryError("UTF16 String size is " + capacity +
                    ", should be less than " + MAX_LENGTH);
        }
        this.value = new char[capacity];
        this.previousStates = new ArrayDeque<>();
        append(seq);
        previousStates.pop();
    }

    public StringBuilderSnapshot append(String str) {
        if (str == null) {
            return this;
        }
        saveSnapshot();
        int len = str.length();

        ensureCapacity(count + len);
        putStringAt(count, str);
        count += len;
        return this;
    }

    public StringBuilderSnapshot append(CharSequence s) {
        if (s == null) {
            return this;
        }
        return this.append(s.toString());
    }

    public StringBuilderSnapshot insert(int index, String str) {
        if (str == null) return this;
        if (index > count || index < 0) throw new IndexOutOfBoundsException(index + " > or < length");
        saveSnapshot();
        int len = str.length();
        ensureCapacity(count + len);
        System.arraycopy(value, index, value, index + len, count - index);
        putStringAt(index, str);
        count += len;
        return this;
    }


    private void ensureCapacity(int minCapacity){
        if (minCapacity >= MAX_LENGTH) {
            throw new OutOfMemoryError("String size is " + minCapacity +
                    ", should be less than " + MAX_LENGTH);
        }
        if(minCapacity > value.length) {
            value = Arrays.copyOf(value,
                    minCapacity << 1);
        }

    }

    private void putStringAt(int index, String str) {
        int putLength = str.length();
        System.arraycopy(str.toCharArray(),0 , value, index, putLength);
    }




    @Override
    public int length() {
        return count;
    }

    @Override
    public char charAt(int index) {
        if(index > count || index < 0) throw new IndexOutOfBoundsException(index + " > or < length");
        return value[index];
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        if(end > count || start < 0 || start > end) throw new IndexOutOfBoundsException( "start or end index not include in string");
        return String.valueOf(value, start, end-start);
    }

    @Override
    public String toString() {
        return new String(value, 0, count);
    }

    record Snapshot(char[] value, int count) {
        public Snapshot {
            value = Arrays.copyOf(value, count);
        }
    }

    public void saveSnapshot() {
        this.previousStates.addFirst(new Snapshot(value, count));
    }

    public void undo() {
        if(previousStates.isEmpty()) return;
        var first  = previousStates.pop();
        this.value = Arrays.copyOf(first.value(), first.value.length);
        count = first.count();
    }

}
