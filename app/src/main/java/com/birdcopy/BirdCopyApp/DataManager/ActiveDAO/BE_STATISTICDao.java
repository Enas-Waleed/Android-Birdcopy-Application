package com.birdcopy.BirdCopyApp.DataManager.ActiveDAO;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table BE__STATISTIC.
*/
public class BE_STATISTICDao extends AbstractDao<BE_STATISTIC, Long> {

    public static final String TABLENAME = "BE__STATISTIC";

    /**
     * Properties of entity BE_STATISTIC.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property BEUSERID = new Property(1, String.class, "BEUSERID", false, "BEUSERID");
        public final static Property BETOUCHCOUNT = new Property(2, int.class, "BETOUCHCOUNT", false, "BETOUCHCOUNT");
        public final static Property BEMONEYCOUNT = new Property(3, int.class, "BEMONEYCOUNT", false, "BEMONEYCOUNT");
        public final static Property BEGIFTCOUNT = new Property(4, int.class, "BEGIFTCOUNT", false, "BEGIFTCOUNT");
        public final static Property BETIMES = new Property(5, Integer.class, "BETIMES", false, "BETIMES");
        public final static Property BEQRCOUNT = new Property(6, int.class, "BEQRCOUNT", false, "BEQRCOUNT");
        public final static Property BETIMESTAMP = new Property(7, String.class, "BETIMESTAMP", false, "BETIMESTAMP");
    };


    public BE_STATISTICDao(DaoConfig config) {
        super(config);
    }
    
    public BE_STATISTICDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'BE__STATISTIC' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'BEUSERID' TEXT NOT NULL ," + // 1: BEUSERID
                "'BETOUCHCOUNT' INTEGER NOT NULL ," + // 2: BETOUCHCOUNT
                "'BEMONEYCOUNT' INTEGER NOT NULL ," + // 3: BEMONEYCOUNT
                "'BEGIFTCOUNT' INTEGER NOT NULL ," + // 4: BEGIFTCOUNT
                "'BETIMES' INTEGER," + // 5: BETIMES
                "'BEQRCOUNT' INTEGER NOT NULL ," + // 6: BEQRCOUNT
                "'BETIMESTAMP' TEXT NOT NULL );"); // 7: BETIMESTAMP
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'BE__STATISTIC'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, BE_STATISTIC entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getBEUSERID());
        stmt.bindLong(3, entity.getBETOUCHCOUNT());
        stmt.bindLong(4, entity.getBEMONEYCOUNT());
        stmt.bindLong(5, entity.getBEGIFTCOUNT());
 
        Integer BETIMES = entity.getBETIMES();
        if (BETIMES != null) {
            stmt.bindLong(6, BETIMES);
        }
        stmt.bindLong(7, entity.getBEQRCOUNT());
        stmt.bindString(8, entity.getBETIMESTAMP());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public BE_STATISTIC readEntity(Cursor cursor, int offset) {
        BE_STATISTIC entity = new BE_STATISTIC( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // BEUSERID
            cursor.getInt(offset + 2), // BETOUCHCOUNT
            cursor.getInt(offset + 3), // BEMONEYCOUNT
            cursor.getInt(offset + 4), // BEGIFTCOUNT
            cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5), // BETIMES
            cursor.getInt(offset + 6), // BEQRCOUNT
            cursor.getString(offset + 7) // BETIMESTAMP
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, BE_STATISTIC entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setBEUSERID(cursor.getString(offset + 1));
        entity.setBETOUCHCOUNT(cursor.getInt(offset + 2));
        entity.setBEMONEYCOUNT(cursor.getInt(offset + 3));
        entity.setBEGIFTCOUNT(cursor.getInt(offset + 4));
        entity.setBETIMES(cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5));
        entity.setBEQRCOUNT(cursor.getInt(offset + 6));
        entity.setBETIMESTAMP(cursor.getString(offset + 7));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(BE_STATISTIC entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(BE_STATISTIC entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
