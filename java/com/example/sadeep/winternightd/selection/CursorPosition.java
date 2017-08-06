package com.example.sadeep.winternightd.selection;

/**
 * Created by Sadeep on 10/21/2016.
 */

/**
 * A class to store a cursor position of a Note.
 *
 * For an EditText the position of the cursor we can store in a simple integer as the index of the character
 * just after the cursor.
 *
 * But when it comes to the cursor position of a Note it is not that easy. Because Note is not a one
 * single text where you can uniquely represent each character by an integer index.
 * The cursor could be in any of its Fields(some of which might not even be text(ex. picture))
 *
 * To overcome this we create this class
 * It has 2 properties,
 *      1. fieldIndex : index of the field where the cursor is.
 *      2. characterIndex : the position of the cursor inside the Field
 *                          (for text Fields this is the index of the character after cursor within the Field.)
 *
 * There are 2 special values for characterIndex,
 *      -1 : represents cursor is at the end of the Field
 *      -2 : represents cursor is at start of the Field
 *
 *
 */
public class CursorPosition {

    public int fieldIndex, characterIndex;

    public static final int CHARACTERINDEX_FIELDSTART = -2;
    public static final int CHARACTERINDEX_FIELDEND = -1;
    public static final int CHARACTERINDEX_ERROR = -3;

    public CursorPosition(int fieldIndex, int characterIndex)
    {
        this.fieldIndex = fieldIndex;
        this.characterIndex = characterIndex;
    }
    //returns the CursorPosition that appears last out of the given two.
    public static CursorPosition max(CursorPosition a, CursorPosition b){
        if(a.fieldIndex>b.fieldIndex)return a;
        if(b.fieldIndex>a.fieldIndex)return b;

        if(a.characterIndex>b.characterIndex)return a;
        if(b.characterIndex>a.characterIndex)return b;

        return a;
    }
    //returns the CursorPosition that appears first out of the given two.
    public static CursorPosition min(CursorPosition a, CursorPosition b){
        if(a.fieldIndex<b.fieldIndex)return a;
        if(b.fieldIndex<a.fieldIndex)return b;

        if(a.characterIndex<b.characterIndex)return a;
        if(b.characterIndex<a.characterIndex)return b;

        return a;
    }

    //is the cursor inside the Field ? (or is it at either of the 2 edges outside the Field)
    public boolean isInternal(){
        if(characterIndex==-2||characterIndex==-1)return false;
        return true;
    }

    //Does 'this' appear after obj2?
    public boolean isGreaterThan(CursorPosition obj2){
        if(this.equals(obj2))return false;

        if(fieldIndex>obj2.fieldIndex)return true;
        if(fieldIndex<obj2.fieldIndex)return false;

        if(this.characterIndex==-1||obj2.characterIndex==-2)return true;
        if(this.characterIndex==-2||obj2.characterIndex==-1)return false;

        if(this.characterIndex>obj2.characterIndex)return true;
        return false;
    }

    //Does 'this' appear before obj2?
    public boolean isLessThan(CursorPosition obj2){
        if(this.equals(obj2))return false;

        if(fieldIndex<obj2.fieldIndex)return true;
        if(fieldIndex>obj2.fieldIndex)return false;

        if(this.characterIndex==-1||obj2.characterIndex==-2)return false;
        if(this.characterIndex==-2||obj2.characterIndex==-1)return true;

        if(this.characterIndex<obj2.characterIndex)return true;
        return false;
    }

    //Do the 2 CursorPositions represent the same position?
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CursorPosition)) return false;

        CursorPosition obj2 = (CursorPosition) o;
        return (fieldIndex == obj2.fieldIndex) && (characterIndex == obj2.characterIndex);

    }
}
