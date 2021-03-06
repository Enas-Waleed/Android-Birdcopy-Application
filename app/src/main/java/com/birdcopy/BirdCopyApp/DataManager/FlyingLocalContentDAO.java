package com.birdcopy.BirdCopyApp.DataManager;

import com.birdcopy.BirdCopyApp.DataManager.ActiveDAO.BE_LOCAl_LESSONDao;
import com.birdcopy.BirdCopyApp.DataManager.ActiveDAO.BE_LOCAl_LESSON;

import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.QueryBuilder;

import java.util.List;

/**
 * Created by songbaoqiang on 6/13/14.
 */

public class FlyingLocalContentDAO {

    private BE_LOCAl_LESSONDao lessonDao;

    public FlyingLocalContentDAO()
    {
        lessonDao = FlyingDBManager.getLocalContentDao();
    }

    public BE_LOCAl_LESSON loadLesson(long id)
    {
        return lessonDao.load(id);
    }

    public List<BE_LOCAl_LESSON> loadAllData()
    {
        return lessonDao.loadAll();
    }

    /**
     * query list with where clause
     * ex: begin_date_time >= ? AND end_date_time <= ?
     * @param where where clause, include 'where' word
     * @param params query parameters
     * @return
     */

    public List<BE_LOCAl_LESSON> querylLesson(String where, String... params)
    {
        return lessonDao.queryRaw(where, params);
    }

    /**
     * insert or update localLessonData
     * @param localLessonData
     * @return insert or update localLessonData id
     */
    public long savelLesson(BE_LOCAl_LESSON localLessonData)
    {

        if (localLessonData!=null)
        {

            BE_LOCAl_LESSON lesson = selectWithLessonID(localLessonData.getBEUSERID(),localLessonData.getBELESSONID());

            if (lesson==null)
            {
                return lessonDao.insertOrReplace(localLessonData);
            }
            else
            {
                long id =lesson.getId();
                localLessonData.setId(id);
                lessonDao.update(localLessonData);

                return id;
            }
        }
        else {

            return 0;
        }
    }

    /**
     * insert or update localLessonDataList use transaction
     * @param list
     */
    public void saveLessonLists(final List<BE_LOCAl_LESSON> list)
    {
        if(list == null || list.isEmpty()){
            return;
        }
        lessonDao.getSession().runInTx(new Runnable() {
            @Override
            public void run() {
                for(int i=0; i<list.size(); i++){
                    BE_LOCAl_LESSON localLessonData = list.get(i);
                    lessonDao.insertOrReplace(localLessonData);
                }
            }
        });

    }

    /**
     * delete all localLessonData
     */
    public void deleteAllData(){
        lessonDao.deleteAll();
    }

    /**
     * delete localLessonData by id
     * @param id
     */
    public void deleteLesson(long id)
    {
        lessonDao.deleteByKey(id);
    }

    public void deleteLesson(BE_LOCAl_LESSON note)
    {
        lessonDao.delete(note);
    }

    public  BE_LOCAl_LESSON  selectWithLessonID(String userID,String lessonID)
    {

        //BE_LOCAl_LESSONDao.

        BE_LOCAl_LESSON localLesson = lessonDao.queryBuilder()
                .where(BE_LOCAl_LESSONDao.Properties.BELESSONID.eq(lessonID))
                .where(BE_LOCAl_LESSONDao.Properties.BEUSERID.eq(userID))
                .unique();

        return localLesson;
    }

    public  void  deleteWithLessonID(String userID,String lessonID)
    {

        QueryBuilder<BE_LOCAl_LESSON> qb = lessonDao.queryBuilder();
        DeleteQuery<BE_LOCAl_LESSON> bd = qb
                .where(BE_LOCAl_LESSONDao.Properties.BEUSERID.eq(userID))
                .where(BE_LOCAl_LESSONDao.Properties.BELESSONID.eq(lessonID))
                .buildDelete();
        bd.executeDeleteWithoutDetachingEntities();
    }

    public void updateLocalContentURL(String userID,String lessonID,double timeStamp)
    {

        if (lessonID!=null)
        {
            BE_LOCAl_LESSON lessondata =selectWithLessonID(userID,lessonID);
            lessondata.setBESTAMP(timeStamp);
            savelLesson(lessondata);
        }
    }
}
