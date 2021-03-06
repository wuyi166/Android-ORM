/*
 * Copyright 2010-2014 Jamling(li.jamling@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.ieclipse.aorm;

import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import cn.ieclipse.aorm.annotation.ColumnWrap;
import cn.ieclipse.aorm.annotation.Table;

/**
 * Aorm settings
 * 
 * @author Jamling
 *         
 */
public final class Aorm {
    
    private static boolean debug = false;
    private static boolean supportExtend = true;
    private static boolean exactInsertOrUpdate = false;
    private static final String TAG = "AORM";
    
    private Aorm() {
        //
    }
    
    /**
     * Enable/Disable debug to print SQL.
     * 
     * @param enable
     *            debug flag, default false.
     */
    public static void enableDebug(boolean enable) {
        debug = enable;
    }
    
    public static void allowExtend(boolean allow) {
        supportExtend = allow;
    }
    
    public static boolean allowExtend() {
        return supportExtend;
    }
    
    /**
     * Set use actuarial insertOrUpdate
     * 
     * @param exactInsertOrUpdate
     *            If true, will query the object from database, insert if not
     *            exists or update if exist, otherwise insert when PK is 0 or
     *            update when PK more than 0 (maybe update fail)
     */
    public static void setExactInsertOrUpdate(boolean exactInsertOrUpdate) {
        Aorm.exactInsertOrUpdate = exactInsertOrUpdate;
    }
    
    /**
     * Get exactInsertOrUpdat
     * 
     * @return whether use actuarial insertOrUpdate strategy
     */
    static boolean getExactInsertOrUpdate() {
        return Aorm.exactInsertOrUpdate;
    }
    
    /**
     * Print log message on Android using {@link Log android.util.Log}
     * 
     * @param msg
     *            logging message.
     */
    public static void logv(String msg) {
        if (debug) {
            android.util.Log.v(TAG, msg);
        }
    }
    
    public static final String LF = System.getProperty("line.separator");
    
    public static String generateDropDDL(Class<?> tableClass) {
        Table t = tableClass.getAnnotation(Table.class);
        if (t == null) {
            throw new ORMException("No mapping to " + tableClass
                    + ", did you forget add @Table to your class?");
        }
        return "DROP TABLE " + t.name() + " IF EXISTS";
    }
    
    public static String generateCreateDDL(Class<?> tableClass) {
        Table t = tableClass.getAnnotation(Table.class);
        if (t == null) {
            throw new ORMException("No mapping to " + tableClass
                    + ", did you forget add @Table to your class?");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ");
        sb.append(t.name());
        sb.append("(");
        sb.append(LF);
        List<ColumnWrap> list = Mapping.getInstance().getColumns(tableClass);
        for (ColumnWrap cw : list) {
            sb.append(new ColumnMeta(cw.getColumn()).toSQL()).toString();
            sb.append(", ");
            sb.append(LF);
        }
        int len = sb.length() - 2;
        len = len - LF.length();
        sb.delete(len, sb.length());
        sb.append(")");
        sb.append(LF);
        return (sb.toString());
    }
    
    public static void createTable(SQLiteDatabase db, Class<?> tableClass) {
        String sql = generateCreateDDL(tableClass);
        db.execSQL(sql);
    }
    
    public static void dropTable(SQLiteDatabase db, Class<?> tableClass) {
        String sql = generateDropDDL(tableClass);
        db.execSQL(sql);
    }
}
