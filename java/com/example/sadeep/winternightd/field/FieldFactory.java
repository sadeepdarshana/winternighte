package com.example.sadeep.winternightd.field;

import android.content.Context;

import com.example.sadeep.winternightd.dumping.FieldDataStream;
import com.example.sadeep.winternightd.field.fields.BulletedField;
import com.example.sadeep.winternightd.field.fields.CheckedField;
import com.example.sadeep.winternightd.field.fields.Field;
import com.example.sadeep.winternightd.field.fields.NumberedField;
import com.example.sadeep.winternightd.field.fields.SimpleIndentedField;

/**
 * Created by Sadeep on 10/18/2016.
 */

/**
 * Instantiating all the types of Fields is/(could be) done through this class
 */

final public class FieldFactory {
    private FieldFactory() {} //cannot be instantiated

    //returns a new empty Field of the given fieldType
    public static Field createNewField(Context context, int fieldType, boolean isEditable)    {
        Field newField = null;

        switch (fieldType)
        {
            case SimpleIndentedField.classFieldType:
                newField = new SimpleIndentedField(isEditable,context);
                break;
            case BulletedField.classFieldType:
                newField = new BulletedField(isEditable,context);
                break;
            case NumberedField.classFieldType:
                newField = new NumberedField(isEditable,context);
                break;
            case CheckedField.classFieldType:
                newField = new CheckedField(isEditable,context);
                break;
        }

        return newField;
    }

    public static Field fromFieldDataStream(Context context, FieldDataStream stream, boolean isEditable){

        Field f = createNewField(context, stream.getFieldType(),isEditable);
        f.readFromFieldDataStream(stream);

        return f;
    }
}
