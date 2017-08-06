package com.example.sadeep.winternightd.factories;

import android.content.Context;

import com.example.sadeep.winternightd.fields.BulletedField;
import com.example.sadeep.winternightd.fields.CheckedField;
import com.example.sadeep.winternightd.fields.Field;
import com.example.sadeep.winternightd.fields.NumberedField;
import com.example.sadeep.winternightd.fields.SimpleIndentedField;

/**
 * Created by Sadeep on 10/18/2016.
 */

/**
 * Instantiating all the types of Fields is/(could be) done through this class
 */

final public class FieldsFactory {
    private FieldsFactory() {} //cannot be instantiated

    //returns a new empty Field of the given fieldType
    public static Field createNewField(int fieldType, boolean isEditable, Context context)
    {
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
}
