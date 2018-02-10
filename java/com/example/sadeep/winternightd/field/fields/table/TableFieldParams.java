package com.example.sadeep.winternightd.field.fields.table;

/**
 * Created by Sadeep on 2/10/2018.
 */

public class TableFieldParams {
    public int columnCount;
    public boolean firstRowIsHeader;

    public TableFieldParams(int columnCount, boolean firstRowIsHeader) {
        this.columnCount = columnCount;
        this.firstRowIsHeader = firstRowIsHeader;
    }
}
