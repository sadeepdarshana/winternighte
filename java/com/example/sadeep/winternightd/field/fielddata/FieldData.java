package com.example.sadeep.winternightd.field.fielddata;

import com.example.sadeep.winternightd.misc.Utils;

/**
 * Created by Sadeep on 1/25/2018.
 */

public class FieldData {
    private String id;

    public FieldData() {
        id = Utils.getNewUUID();
    }

    public FieldData(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
