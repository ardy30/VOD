package com.ppfuns.vod.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ppfuns.model.entity.SubjectContent;
import com.ppfuns.model.entity.Subjects;
import com.ppfuns.ui.view.BaseLoopPlayView;
import com.ppfuns.ui.view.VodPlayImages;
import com.ppfuns.util.LogUtils;
import com.ppfuns.util.https.ContractUrl;
import com.ppfuns.util.https.HttpUtil;
import com.ppfuns.vod.R;
import com.umeng.analytics.MobclickAgent;
import com.zhy.http.okhttp.callback.Callback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by fangyuan on 2016/6/17.
 */
public class SubjectDetailsActivity extends BaseActivity implements BaseLoopPlayView.SelectItem, BaseLoopPlayView.SelectItemClickListener {
    @BindView(R.id.subjectName)
    TextView subjectName;
    @BindView(R.id.items)
    TextView items;
    @BindView(R.id.subjectDes)
    TextView subjectDes;
    @BindView(R.id.subjectDes1)
    TextView subjectDes1;
    @BindView(R.id.filmName)
    TextView filmName;
    @BindView(R.id.score)
    TextView score;
    @BindView(R.id.filmDes)
    TextView filmDes;
    @BindView(R.id.iv_arbj)
    ImageView arbj;
    @BindView(R.id.tv_counts)
    TextView tvCoounts;
    @BindView(R.id.ac_main_vodplay)
    VodPlayImages vodPlayImages;

//    private Subject.HttpCommon.ResultBean bean;
    private Subjects bean;

    private List<SubjectContent> subjectContentList = new ArrayList<>();
    private int subjectId; //专题Id
    private int cpId; //栏目Id
    private String mPlatform = "0"; //平台 "0" 机顶盒 "1"
    public String subjectUrl = ContractUrl.getVodUrl()  + "/r/subject/"; //专题详情
    private String[] imgUrlArr;
    private boolean fromServer;
    private String subjectJson = "";
    private String   film;//影片介绍
    private String TAG = "SubjectDetailsActivity";
    private int dalenTime = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subject_details2);
        ButterKnife.bind(this);

        filmDes.setText("");
        bean = (Subjects) getIntent().getSerializableExtra("subjectDetails");
        subjectId = getIntent().getIntExtra("subjectId", 0);
        cpId = vodColumnId;
        LogUtils.i(TAG, "subjectId: " + subjectId);
        if (bean != null) {
            if (bean.subjectContentList != null &&
                    bean.subjectContentList.size() > 0) {
                subjectContentList.addAll(bean.subjectContentList);
                initImgs();
            }
        } else {
            fromServer = true;
            subjectContentList = new ArrayList<>();
        }
        initSubjectDes();
        initView();
        if (fromServer) {
            getSubjectData(subjectId, cpId);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //LogUtils.i(TAG, "onStart: " + " fromServer: "+ fromServer);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_DPAD_DOWN){
            if(film.length()>=150) {

                if (film.length() > 0) {
                    Intent intent = new Intent(SubjectDetailsActivity.this, FilmIntroducedActivity.class);
                    intent.putExtra("filmDes", film);
                    intent.putExtra(AlbumActivity.COL_CONTENT_ID, cpId);
                    startActivity(intent);
                }
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HttpUtil.cancelTag(SubjectDetailsActivity.this);
        if (!subjectContentList.isEmpty()) {
            subjectContentList.clear();
        }
        subjectContentList = null;

    }

    @Override
    public void reLoad() {
        super.reLoad();
        getSubjectData(subjectId, cpId);
    }

    //获取专题详情
    private void getSubjectData(int subjectId, int col_contentId) {
        handler.sendEmptyMessageDelayed(1,dalenTime);
        String url = subjectUrl + subjectId + "-" + col_contentId + "-" + mPlatform + ".json";
        HttpUtil.getHttpHtml(url, SubjectDetailsActivity.this, new Callback<com.ppfuns.model.entity.Response<List<Subjects>>>() {
            @Override
            public com.ppfuns.model.entity.Response<List<Subjects>> parseNetworkResponse(Response response, int id) throws Exception {
                String htmlStr = response.body().string();
                LogUtils.i(TAG, "htmlStr: " + htmlStr);
//                if(subjectJson.equals(htmlStr)){
//                    return null;
//                }
                subjectJson = htmlStr;
                Gson gson = new Gson();
                return gson.fromJson(htmlStr, new TypeToken<com.ppfuns.model.entity.Response<List<Subjects>>>(){}.getType());
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                handler.removeMessages(1);
                dissmissLoadingDialog();
                showDialogTips(getString(R.string.data_error));
            }

            @Override
            public void onResponse(com.ppfuns.model.entity.Response<List<Subjects>> response, int id) {
                handler.removeMessages(1);
                dissmissLoadingDialog();
                com.ppfuns.model.entity.Response<List<Subjects>> subjectbean = response;
                if (subjectbean != null &&
                        subjectbean.data != null &&
                        subjectbean.data.result != null &&
                        subjectbean.data.result .size() > 0) {
                    bean = subjectbean.data.result .get(0);
                    initSubjectDes();
                    subjectContentList.clear();
                    subjectContentList.addAll(bean.subjectContentList);
                    initImgs();
                } else {
                   // ToastUtils.showShort(SubjectDetailsActivity.this, "no data");
                    if(subjectContentList==null||subjectContentList.size()==0){
                        vodPlayImages.setFocusable(false);
                    }
                }
            }
        });
    }

    private void initImgs() {
        if(subjectContentList!=null&&
                subjectContentList.size()>0){
            imgUrlArr = new String[subjectContentList.size()];
            for (int i = 0; i < subjectContentList.size(); i++) {
                imgUrlArr[i] = subjectContentList.get(i).contentPosters;
            }
            int viewWidth = getResources().getDimensionPixelSize(R.dimen.subject_detail_content_width);
            int viewHeight = getResources().getDimensionPixelSize(R.dimen.subject_detail_content_height);
            vodPlayImages.setImageViewUrls(imgUrlArr);
            vodPlayImages.setShowImageNum(4)
                    .setmContentViewWidth(viewWidth)
                    .setmContentViewHeight(viewHeight)
                    .setmContentViewPadding(0)
                    .setmAnimatorDuration(300)
                    .setFirstFocusItem(1);
            vodPlayImages.initView();
            selectItem(0);
        }else{
//            vodPlayImages.setVisibility(View.GONE);
//            vodPlayImages.setFocusable(false);
        }
//        else{
//            vodPlayImages.setShowImageNum(4)
//                    .setmContentViewWidth(585)
//                    .setmContentViewHeight(780)
//                    .setmContentViewPadding(0)
//                    .setmAnimatorDuration(300)
//                    .setFirstFocusItem(1);
//            vodPlayImages.initView();
//        }

    }

    private void initView() {
        vodPlayImages.setSelectItem(this);
        vodPlayImages.setSelectItemClickListener(this);
    }

    private void initSubjectDes() {
        if (bean != null) {
            if (!TextUtils.isEmpty(bean.subjectName)) {
                if (bean.subjectName.contains("AR")){
                    //AR专区
                    arbj.setVisibility(View.VISIBLE);
                    tvCoounts.setVisibility(View.VISIBLE);
                }
                subjectName.setText(bean.subjectName);
            }
            if (bean.subjectContentList!= null) {
                items.setText(getString(R.string.totaltext) +
                        bean.subjectContentList.size() + getString(R.string.totaltext1));
                tvCoounts.setText(getString(R.string.totaltext) +
                        bean.subjectContentList.size() + "部作品");
            }
//            if (!TextUtils.isEmpty(bean.subjectTypeDesc)) {
//                subjectDes.setText(bean.subjectTypeDesc);
//            } else {
//                subjectDes.setText(R.string.empty);
//            }
            if (!TextUtils.isEmpty(bean.subjectDesc)) {
                String[] array = bean.subjectDesc.split("\\|");
                if(array!=null&&array.length>0){
                    LogUtils.i("CollectAdapter","array: "+ array[0]);
                    if(array.length==1){
                        subjectDes.setText(R.string.empty);
                        subjectDes1.setText(bean.subjectDesc);
                    }else if(array.length==2){
                        subjectDes.setText(array[0]);
                        subjectDes1.setText(array[1]);
                    }
                }else{
                    subjectDes.setText(R.string.empty);
                    subjectDes1.setText(bean.subjectDesc);
                }
            } else {
                subjectDes1.setText(R.string.empty);
            }
        }
    }

    //根据选中项修改影片描述
    private void changeSelectText(int position) {
       SubjectContent bean = subjectContentList.get(position % subjectContentList.size());
        if (bean != null) {
            if (!TextUtils.isEmpty(bean.contentName)) {
                filmName.setText(bean.contentName);
            } else {
                filmName.setText(R.string.empty);
            }
            score.setText(String.valueOf(bean.score));
            StringBuffer sb = new StringBuffer();

            if(!TextUtils.isEmpty(bean.directors)){
                sb.append(getString(R.string.director)).append(bean.directors).append(getString(R.string.wrap));
            }
            if(!TextUtils.isEmpty(bean.actors)){
                sb.append(getString(R.string.actors)).append(bean.actors).append(getString(R.string.wrap));
            }
            if(!TextUtils.isEmpty(bean.labels)){
                sb.append(getString(R.string.labels)).append(bean.labels).append(getString(R.string.wrap));
            }
            if(!TextUtils.isEmpty(bean.albumDesc)){
                sb.append(getString(R.string.desc)).append(bean.albumDesc).append(getString(R.string.wrap));
            }

            if (!TextUtils.isEmpty(sb.toString())) {

                if(sb.toString().length()<150){
                filmDes.setText(sb.toString());
            }else {
                    String str=sb.toString();
                   str=str.substring(0,150);
                    str=(str+" 。。。。(按向下键显示全部)");

                    filmDes.setText(str);
                }
            } else {
                filmDes.setText(R.string.empty);
            }
            film=sb.toString();

        }
    }

    @Override
    public void selectItem(int point) {
        LogUtils.i(TAG, "point: " + point);
        changeSelectText(point);
    }

    @Override
    public void selectItemClick(int point) {
        if(subjectContentList!=null&&subjectContentList.size()>0){
            if (subjectContentList.get(point % subjectContentList.size()).contentType==5){
                //跳转详细也
                ArrayList<SubjectContent> arrayList= (ArrayList<SubjectContent>) subjectContentList;
                Intent intent = new Intent(SubjectDetailsActivity.this, ARdetailActivity.class);
                intent.putExtra(ARdetailActivity.POS, point);
                intent.putExtra(ARdetailActivity.LIST, arrayList);
                intent.putExtra(AlbumActivity.COL_CONTENT_ID, cpId);
                SubjectDetailsActivity.this.startActivity(intent);
            }else {
                Intent intent = new Intent(SubjectDetailsActivity.this, AlbumActivity.class);
                intent.putExtra(AlbumActivity.ALBUM_ID, subjectContentList.get(point % subjectContentList.size()).contentId);
                intent.putExtra(AlbumActivity.COL_CONTENT_ID, cpId);
                SubjectDetailsActivity.this.startActivity(intent);
            }

        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
        MobclickAgent.onResume(this);
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    showLoadingProgressDialog("");
                    break;
            }
        }
    };
}
