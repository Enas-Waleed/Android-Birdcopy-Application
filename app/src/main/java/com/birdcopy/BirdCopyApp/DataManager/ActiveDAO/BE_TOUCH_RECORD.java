package com.birdcopy.BirdCopyApp.DataManager.ActiveDAO;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table BE__TOUCH__RECORD.
 */
public class BE_TOUCH_RECORD {

    private Long id;
    /** Not-null value. */
    private String BEUSERID;
    /** Not-null value. */
    private String BELESSONID;
    private Integer BETOUCHTIMES;
    private String BETIMESTAMP;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public BE_TOUCH_RECORD() {
    }

    public BE_TOUCH_RECORD(Long id) {
        this.id = id;
    }

    public BE_TOUCH_RECORD(Long id, String BEUSERID, String BELESSONID, Integer BETOUCHTIMES, String BETIMESTAMP) {
        this.id = id;
        this.BEUSERID = BEUSERID;
        this.BELESSONID = BELESSONID;
        this.BETOUCHTIMES = BETOUCHTIMES;
        this.BETIMESTAMP = BETIMESTAMP;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** Not-null value. */
    public String getBEUSERID() {
        return BEUSERID;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setBEUSERID(String BEUSERID) {
        this.BEUSERID = BEUSERID;
    }

    /** Not-null value. */
    public String getBELESSONID() {
        return BELESSONID;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setBELESSONID(String BELESSONID) {
        this.BELESSONID = BELESSONID;
    }

    public Integer getBETOUCHTIMES() {
        return BETOUCHTIMES;
    }

    public void setBETOUCHTIMES(Integer BETOUCHTIMES) {
        this.BETOUCHTIMES = BETOUCHTIMES;
    }

    public String getBETIMESTAMP() {
        return BETIMESTAMP;
    }

    public void setBETIMESTAMP(String BETIMESTAMP) {
        this.BETIMESTAMP = BETIMESTAMP;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}
