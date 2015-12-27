package com.birdcopy.BirdCopyApp.ContentList;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.birdcopy.BirdCopyApp.Component.ActiveDAO.BE_PUB_LESSON;
import com.birdcopy.BirdCopyApp.Component.Base.ShareDefine;
import com.birdcopy.BirdCopyApp.MainHome.MainActivity;
import com.birdcopy.BirdCopyApp.R;
import com.birdcopy.BirdCopyApp.Component.UI.grid.StaggeredGridView;
import java.util.ArrayList;
import android.os.Handler;

/**
 * Created by songbaoqiang on 6/8/14.
 */
public class LessonListFragment extends Fragment implements LessonDataModle.DealResult
{

    public static final String SAVED_DATA_KEY   = "SAVED_DATA_KEY";
    public final static int SET_LESSONLIST = 0;

    private StaggeredGridView mGridView;
    private LessonListAdapter mAdapter;

    private ArrayList<BE_PUB_LESSON> mData= new ArrayList<BE_PUB_LESSON>();

    private String mTag="";

    public  String mContentType="";
    public  String mDownloadType="";
    public  boolean sortByTime=true;

    int     mMaxNumOfLessons=ShareDefine.MAX_INT;
    int     currentLodingIndex=0;

    Boolean mIsLastRow=false;

    private LessonDataModle mLessonDataModle = new LessonDataModle();

    //是否默认加载数据
    private boolean mLoadDataDefault = false;

    public LessonListFragment()
    {
        // Empty constructor required for News subclasses
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            // TODO Auto-generated method stub
            switch (msg.what)
            {
                case SET_LESSONLIST:
                {
                    // notify the adapter that we can update now
                    mAdapter.notifyDataSetChanged();
                    break;
                }
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /** 此方法意思为fragment是否可见 ,可见时候加载数据 */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        if (isVisibleToUser)
        {
            //fragment可见时加载数据
            if(mData !=null && mData.size() !=0)
            {
                handler.obtainMessage(SET_LESSONLIST).sendToTarget();
            }
            else
            {
                onLoadMoreItems();
            }
        }

        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        super.onCreateView(inflater,container,savedInstanceState);

        if(savedInstanceState!=null)
        {
            String temp = savedInstanceState.getString(SAVED_DATA_KEY);

            if(temp!=null)
            {
                mTag = temp;
            }
        }

        mGridView = (StaggeredGridView)inflater.inflate(R.layout.gridview, container, false);
        if (mAdapter == null) {
            mAdapter = new LessonListAdapter(getActivity(), R.id.lesson_grid_content);
        }
        mGridView.setAdapter(mAdapter);
        mGridView.setOnScrollListener( new AbsListView.OnScrollListener(){


                                           @Override
                                           public void onScrollStateChanged(AbsListView view, int scrollState)
                                           {
                                               if(scrollState==SCROLL_STATE_IDLE && mIsLastRow==true)
                                               {
                                                   onLoadMoreItems();
                                                   mIsLastRow=false;
                                               }

                                               switch (scrollState) {
                                                   // 当不滚动时
                                                   case SCROLL_STATE_IDLE:
                                                   {
                                                       break;
                                                   }
                                               }
                                           }

                                           @Override
                                           public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
                                           {

                                               //判断是否滚到最后一行
                                               if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > 0) {
                                                   mIsLastRow = true;
                                               }
                                           }
                                       }
        );

        mGridView.setOnItemClickListener(new AbsListView.OnItemClickListener(){

            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                BE_PUB_LESSON lesson =  mData.get(position);

                if (lesson!=null){

                    MainActivity mainActivity = (MainActivity)getActivity();

                    mainActivity.showLessonViewWithData(lesson);
                }

                //Toast.makeText(getActivity(), "Item Clicked: " + position, Toast.LENGTH_SHORT).show();
            }
        });

        if(mLoadDataDefault)
        {
            onLoadMoreItems();
        }

        return mGridView;
    }

    private void onLoadMoreItems()
    {
        if (mData.size() <mMaxNumOfLessons)
        {
            currentLodingIndex++;
            mLessonDataModle.setDelegate(this);
            mLessonDataModle.loadMoreLessonData ( mContentType,
                    mDownloadType,
                    mTag,
                    currentLodingIndex,
                    sortByTime);
        }
    }

    public void parseOK(ArrayList<BE_PUB_LESSON> list)
    {
        if(list!=null || list.size()!=0)
        {
            for (BE_PUB_LESSON data : list) {
                mAdapter.add(data);
            }
            // stash all the data in our backing store
            mData.addAll(list);

            handler.obtainMessage(SET_LESSONLIST).sendToTarget();
        }
    }

    @Override
    public void setMaxItems(int itemCount)
    {
        mMaxNumOfLessons = itemCount;
    }

    public void setTagString(String tagString)
    {
        mTag=tagString;
    }

    public String getTagString()
    {
        return  mTag;
    }

    public void setDownloadType(String downloadType)
    {
        mDownloadType = downloadType;
    }

    public  void setContentType(String contentType)
    {
        mContentType = contentType;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        //Store the game state
        outState.putString(SAVED_DATA_KEY, mTag);
    }

    /* 摧毁视图 */
    @Override
    public void onDestroyView()
    {
        // TODO Auto-generated method stub
        super.onDestroyView();
    }

    public void setLoadDataDefault(boolean loadDataDefault)
    {
        mLoadDataDefault = loadDataDefault;
    }
}