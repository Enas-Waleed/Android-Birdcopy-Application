package com.birdcopy.BirdCopyApp.IM;

import android.annotation.TargetApi;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.birdcopy.BirdCopyApp.Content.FlyingWebViewActivity;
import com.birdcopy.BirdCopyApp.DataManager.FlyingDataManager;
import com.birdcopy.BirdCopyApp.DataManager.FlyingIMContext;
import com.birdcopy.BirdCopyApp.Download.FlyingFileManager;
import com.birdcopy.BirdCopyApp.Http.FlyingHttpTool;
import com.birdcopy.BirdCopyApp.MainHome.MainActivity;
import com.birdcopy.BirdCopyApp.MyApplication;
import com.birdcopy.BirdCopyApp.Scan.BitmapToText;
import com.birdcopy.BirdCopyApp.ShareDefine;
import com.koushikdutta.ion.Ion;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.model.GroupUserInfo;
import io.rong.imkit.model.UIConversation;
import io.rong.imkit.widget.provider.CameraInputProvider;
import io.rong.imkit.widget.provider.ImageInputProvider;
import io.rong.imkit.widget.provider.InputProvider;
import io.rong.imkit.widget.provider.TextInputProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Discussion;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.DiscussionNotificationMessage;
import io.rong.message.ImageMessage;
import io.rong.message.InformationNotificationMessage;
import io.rong.message.LocationMessage;
import io.rong.message.RichContentMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;
import io.rong.notification.PushNotificationMessage;


import com.birdcopy.BirdCopyApp.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by zhjchen on 1/29/15.
 */

/**
 * 融云SDK事件监听处理。
 * 把事件统一处理，开发者可直接复制到自己的项目中去使用。
 * <p/>
 * 该类包含的监听事件有：
 * 1、消息接收器：OnReceiveMessageListener。
 * 2、发出消息接收器：OnSendMessageListener。
 * 3、用户信息提供者：GetUserInfoProvider。
 * 4、好友信息提供者：GetFriendsProvider。
 * 5、群组信息提供者：GetGroupInfoProvider。
 * 6、设置会话界面操作的监听器:ConversationBehaviorListener.
 * 7、连接状态监听器，以获取连接相关状态：ConnectionStatusListener。
 * 8、地理位置提供者：LocationProvider。
 * 9、自定义 push 通知： OnReceivePushMessageListener。
 * 10、会话列表界面操作的监听器：ConversationListBehaviorListener。
 */
public final class RongCloudEvent implements
        RongIMClient.OnReceiveMessageListener,
        RongIM.OnSendMessageListener,
        RongIM.UserInfoProvider,
        RongIM.GroupInfoProvider,
        RongIM.ConversationBehaviorListener,
        RongIMClient.ConnectionStatusListener,
        RongIMClient.OnReceivePushMessageListener,
        RongIM.ConversationListBehaviorListener,
        RongIM.GroupUserInfoProvider {

    private static final String TAG = RongCloudEvent.class.getSimpleName();

    private static RongCloudEvent mRongCloudInstance;

    private Context mContext;


    private Handler mHandler;

    /**
     * 初始化 RongCloud.
     *
     * @param context 上下文。
     */
    public static void init(Context context) {

        if (mRongCloudInstance == null) {

            synchronized (RongCloudEvent.class) {

                if (mRongCloudInstance == null) {
                    mRongCloudInstance = new RongCloudEvent(context);
                }
            }
        }
    }

    /**
     * 构造方法。
     *
     * @param context 上下文。
     */
    private RongCloudEvent(Context context) {
        mContext = context;
        initDefaultListener();
    }


    /**
     * 获取RongCloud 实例。
     *
     * @return RongCloud。
     */
    public static RongCloudEvent getInstance() {
        return mRongCloudInstance;
    }

    /**
     * RongIM.init(this) 后直接可注册的Listener。
     */
    private void initDefaultListener() {

        RongIM.setUserInfoProvider(this, true);//设置用户信息提供者。
        RongIM.setGroupInfoProvider(this, true);//设置群组信息提供者。
        RongIM.setConversationBehaviorListener(this);//设置会话界面操作的监听器。
        //RongIM.setLocationProvider(this);//设置地理位置提供者,不用位置的同学可以注掉此行代码
        RongIM.setConversationListBehaviorListener(this);//会话列表界面操作的监听器
        RongIM.getInstance().setSendMessageListener(this);//设置发出消息接收监听器.
        RongIM.setGroupUserInfoProvider(this, true);
//        RongIM.setOnReceivePushMessageListener(this);//自定义 push 通知。
        //消息体内是否有 userinfo 这个属性
//        RongIM.getInstance().setMessageAttachedUserInfo(true);
    }

    /**
     * 连接成功注册。
     * <p/>
     * 在RongIM-connect-onSuccess后调用。
     */
    public void setOtherListener() {

        RongIM.getInstance().getRongIMClient().setOnReceiveMessageListener(this);//设置消息接收监听器。
        RongIM.getInstance().getRongIMClient().setConnectionStatusListener(this);//设置连接状态监听器。

        TextInputProvider textInputProvider = new TextInputProvider(RongContext.getInstance());
        RongIM.setPrimaryInputProvider(textInputProvider);

//        扩展功能自定义
        InputProvider.ExtendProvider[] provider = {
                new ImageInputProvider(RongContext.getInstance()),//图片
                new CameraInputProvider(RongContext.getInstance()),//相机
        };

        InputProvider.ExtendProvider[] provider1 = {
                new ImageInputProvider(RongContext.getInstance()),//图片
                new CameraInputProvider(RongContext.getInstance()),//相机
                new ContactsProvider(RongContext.getInstance()),//通讯录
        };

        RongIM.getInstance().resetInputExtensionProvider(Conversation.ConversationType.PRIVATE, provider);
        RongIM.getInstance().resetInputExtensionProvider(Conversation.ConversationType.DISCUSSION, provider1);
        RongIM.getInstance().resetInputExtensionProvider(Conversation.ConversationType.GROUP, provider1);
        RongIM.getInstance().resetInputExtensionProvider(Conversation.ConversationType.CUSTOMER_SERVICE, provider1);
        RongIM.getInstance().resetInputExtensionProvider(Conversation.ConversationType.CHATROOM, provider1);
    }

    /**
     * 自定义 push 通知。
     *
     * @param msg
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onReceivePushMessage(PushNotificationMessage msg) {

        //自定义备用
        /*
        Intent intent = new Intent();
        Uri uri;

        intent.setAction(Intent.ACTION_VIEW);

        Conversation.ConversationType conversationType = msg.getConversationType();

        uri = Uri.parse("rong://" + RongContext.getInstance().getPackageName()).buildUpon().appendPath("conversationlist").build();
        intent.setData(uri);
        Log.d(TAG, "onPushMessageArrive-url:" + uri.toString());

        Notification notification = null;

        PendingIntent pendingIntent = PendingIntent.getActivity(RongContext.getInstance(), 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (android.os.Build.VERSION.SDK_INT < 11) {
            notification = new Notification(RongContext.getInstance().getApplicationInfo().icon, "自定义 notification", System.currentTimeMillis());

            notification.setLatestEventInfo(RongContext.getInstance(), "自定义 title", "这是 Content:" + msg.getObjectName(), pendingIntent);
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            notification.defaults = Notification.DEFAULT_SOUND;
        } else {

            notification = new Notification.Builder(RongContext.getInstance())
                    .setLargeIcon(getAppIcon())
                    .setSmallIcon(R.drawable.ic_rongcloud)
                    .setTicker("自定义 notification")
                    .setContentTitle("自定义 title")
                    .setContentText("这是 Content:" + msg.getObjectName())
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL).build();

        }

        NotificationManager nm = (NotificationManager) RongContext.getInstance().getSystemService(RongContext.getInstance().NOTIFICATION_SERVICE);

        nm.notify(0, notification);
        */

        return true;
    }

    private Bitmap getAppIcon() {
        BitmapDrawable bitmapDrawable;
        Bitmap appIcon;
        bitmapDrawable = (BitmapDrawable) RongContext.getInstance().getApplicationInfo().loadIcon(RongContext.getInstance().getPackageManager());
        appIcon = bitmapDrawable.getBitmap();
        return appIcon;
    }

    /**
     * 接收消息的监听器：OnReceiveMessageListener 的回调方法，接收到消息后执行。
     *
     * @param message 接收到的消息的实体信息。
     * @param left    剩余未拉取消息数目。
     */
    @Override
    public boolean onReceived(Message message, int left) {

        MessageContent messageContent = message.getContent();

        if (messageContent instanceof TextMessage) {//文本消息

            TextMessage textMessage = (TextMessage) messageContent;
            textMessage.getExtra();
            Log.d(TAG, "onReceived-TextMessage:" + textMessage.getContent());
        } else if (messageContent instanceof ImageMessage) {//图片消息

            ImageMessage imageMessage = (ImageMessage) messageContent;
            Log.d(TAG, "onReceived-ImageMessage:" + imageMessage.getRemoteUri());
        } else if (messageContent instanceof VoiceMessage) {//语音消息

            VoiceMessage voiceMessage = (VoiceMessage) messageContent;
            Log.d(TAG, "onReceived-voiceMessage:" + voiceMessage.getUri().toString());
        } else if (messageContent instanceof RichContentMessage) {//图文消息

            RichContentMessage richContentMessage = (RichContentMessage) messageContent;
            Log.d(TAG, "onReceived-RichContentMessage:" + richContentMessage.getContent());
        } else if (messageContent instanceof InformationNotificationMessage) {//小灰条消息

            InformationNotificationMessage informationNotificationMessage = (InformationNotificationMessage) messageContent;
            Log.e(TAG, "onReceived-informationNotificationMessage:" + informationNotificationMessage.getMessage());
            //if (FlyingIMContext.getInstance() != null)
            //  getFriendByUserIdHttpRequest = FlyingIMContext.getInstance().getUserInfoByUserId(message.getSenderUserId(), (ApiCallback<User>) this);
        }
        /*else if (messageContent instanceof AgreedFriendRequestMessage) {//好友添加成功消息

            AgreedFriendRequestMessage agreedFriendRequestMessage = (AgreedFriendRequestMessage) messageContent;
            Log.d(TAG, "onReceived-deAgreedFriendRequestMessage:" + agreedFriendRequestMessage.getMessage());
            Intent in = new Intent();
            in.setAction(MainActivity.ACTION_DMEO_AGREE_REQUEST);
            in.putExtra("AGREE_REQUEST", true);
            mContext.sendBroadcast(in);
        } else if (messageContent instanceof ContactNotificationMessage) {//好友添加消息

            ContactNotificationMessage contactContentMessage = (ContactNotificationMessage) messageContent;
            Log.d(TAG, "onReceived-ContactNotificationMessage:getExtra;" + contactContentMessage.getExtra());
            Log.d(TAG, "onReceived-ContactNotificationMessage:+getmessage:" + contactContentMessage.getMessage().toString());
            Intent in = new Intent();
            in.setAction(MainActivity.ACTION_DMEO_RECEIVE_MESSAGE);
            in.putExtra("rongCloud", contactContentMessage);
            in.putExtra("has_message", true);
            mContext.sendBroadcast(in);
        }
        */else if (messageContent instanceof DiscussionNotificationMessage) {//讨论组通知消息

            DiscussionNotificationMessage discussionNotificationMessage = (DiscussionNotificationMessage) messageContent;
            Log.d(TAG, "onReceived-discussionNotificationMessage:getExtra;" + discussionNotificationMessage.getOperator());
            setDiscussionName(message.getTargetId());
        } else {
            Log.d(TAG, "onReceived-其他消息，自己来判断处理");
        }

        //通知更新菜单
        Intent in = new Intent();
        in.setAction(MainActivity.ACTION_RONGCLOUD_RECEIVE_MESSAGE);
        in.putExtra("rongMessage", message);
        in.putExtra("has_message", true);
        mContext.sendBroadcast(in);

        return false;

    }

    /**
     * 讨论组名称修改后刷新本地缓存
     *
     * @param targetId 讨论组 id
     */
    private void setDiscussionName(String targetId) {

        if (RongIM.getInstance() != null && RongIM.getInstance().getRongIMClient() != null) {
            RongIM.getInstance().getRongIMClient().getDiscussion(targetId, new RongIMClient.ResultCallback<Discussion>() {
                @Override
                public void onSuccess(Discussion discussion) {

                    RongIM.getInstance().refreshDiscussionCache(discussion);
                    Log.i(TAG, "------discussion.getName---" + discussion.getName());
                }

                @Override
                public void onError(RongIMClient.ErrorCode e) {

                }
            });
        }
    }

    /**
     * 消息发送前监听器处理接口（是否发送成功可以从SentStatus属性获取）。
     *
     * @param message 发送的消息实例。
     * @return 处理后的消息实例。
     */
    @Override
    public Message onSend(Message message) {


        MessageContent messageContent = message.getContent();

        if (messageContent instanceof TextMessage) {//文本消息
            TextMessage textMessage = (TextMessage) messageContent;
            Log.e("qinxiao", "--onSend:" + textMessage.getContent() + ", extra=" + message.getExtra());
        }


        return message;
    }

    /**
     * 消息在UI展示后执行/自己的消息发出后执行,无论成功或失败。
     *
     * @param message 消息。
     */
    @Override
    public boolean onSent(Message message, RongIM.SentMessageErrorCode sentMessageErrorCode) {
        Log.e("qinxiao", "onSent:" + message.getObjectName() + ", extra=" + message.getExtra());

        if (message.getSentStatus() == Message.SentStatus.FAILED) {

            if (sentMessageErrorCode == RongIM.SentMessageErrorCode.NOT_IN_CHATROOM) {//不在聊天室

            } else if (sentMessageErrorCode == RongIM.SentMessageErrorCode.NOT_IN_DISCUSSION) {//不在讨论组

            } else if (sentMessageErrorCode == RongIM.SentMessageErrorCode.NOT_IN_GROUP) {//不在群组

            } else if (sentMessageErrorCode == RongIM.SentMessageErrorCode.REJECTED_BY_BLACKLIST) {//你在他的黑名单中
                //WinToast.toast(mContext, "你在对方的黑名单中");
            }
        }

        MessageContent messageContent = message.getContent();

        if (messageContent instanceof TextMessage) {//文本消息
            TextMessage textMessage = (TextMessage) messageContent;
            Log.e(TAG, "onSent-TextMessage:" + textMessage.getContent());
        } else if (messageContent instanceof ImageMessage) {//图片消息
            ImageMessage imageMessage = (ImageMessage) messageContent;
            Log.d(TAG, "onSent-ImageMessage:" + imageMessage.getRemoteUri());
        } else if (messageContent instanceof VoiceMessage) {//语音消息
            VoiceMessage voiceMessage = (VoiceMessage) messageContent;
            Log.d(TAG, "onSent-voiceMessage:" + voiceMessage.getUri().toString());
        } else if (messageContent instanceof RichContentMessage) {//图文消息
            RichContentMessage richContentMessage = (RichContentMessage) messageContent;
            Log.d(TAG, "onSent-RichContentMessage:" + richContentMessage.getContent());
        } else {
            Log.d(TAG, "onSent-其他消息，自己来判断处理");
        }
        return false;
    }

    /**
     * 用户信息的提供者：GetUserInfoProvider 的回调方法，获取用户信息。
     *
     * @param userId 用户 Id。
     * @return 用户信息，（注：由开发者提供用户信息）。
     */
    @Override
    public UserInfo getUserInfo(String userId) {
        /**
         * demo 代码  开发者需替换成自己的代码。
         */

        if (userId == null)
            return null;
        return FlyingIMContext.getInstance().getUserInfoByRongId(userId);
    }


    /**
     * 群组信息的提供者：GetGroupInfoProvider 的回调方法， 获取群组信息。
     *
     * @param groupId 群组 Id.
     * @return 群组信息，（注：由开发者提供群组信息）。
     */
    @Override
    public Group getGroupInfo(String groupId) {
        /**
         * demo 代码  开发者需替换成自己的代码。
         */
        if (FlyingIMContext.getInstance().getGroupMap() == null)
            return null;

        return FlyingIMContext.getInstance().getGroupMap().get(groupId);
    }

    /**
     * 会话界面操作的监听器：ConversationBehaviorListener 的回调方法，当点击用户头像后执行。
     *
     * @param context          应用当前上下文。
     * @param conversationType 会话类型。
     * @param user             被点击的用户的信息。
     * @return 返回True不执行后续SDK操作，返回False继续执行SDK操作。
     */
    @Override
    public boolean onUserPortraitClick(Context context, Conversation.ConversationType conversationType, UserInfo user) {

        /**
         * demo 代码  开发者需替换成自己的代码。
         */
        if (user != null) {

            if (conversationType.equals(Conversation.ConversationType.PUBLIC_SERVICE) ||
                    conversationType.equals(Conversation.ConversationType.APP_PUBLIC_SERVICE)) {

                RongIM.getInstance().startPublicServiceProfile(mContext, conversationType, user.getUserId());
            }
            if(conversationType==Conversation.ConversationType.CHATROOM||
                    conversationType== Conversation.ConversationType.GROUP||
                    conversationType== Conversation.ConversationType.DISCUSSION)
            {

                String currentRongID = FlyingDataManager.getCurrentRongID();

                if (RongIM.getInstance() != null &&
                        user!=null &&
                        !user.getUserId().endsWith(currentRongID)
                        )
                {
                    RongIM.getInstance().startConversation(context, Conversation.ConversationType.PRIVATE, user.getUserId(), user.getName());
                }
            }
            else
            {

                Toast.makeText(context, "查看用户详细信息－》下个版本推出", Toast.LENGTH_SHORT).show();
                /*
                Log.d("Begavior", conversationType.getName() + ":" + user.getName());
                Intent in = new Intent(context, DePersonalDetailActivity.class);
                in.putExtra("USER", user);
                in.putExtra("SEARCH_USERID", user.getUserId());
                context.startActivity(in);
                */
            }
        }

        return true;
    }

    @Override
    public boolean onUserPortraitLongClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
        Log.e(TAG, "----onUserPortraitLongClick");

        return false;
    }

    /**
     * 会话界面操作的监听器：ConversationBehaviorListener 的回调方法，当点击消息时执行。
     *
     * @param context 应用当前上下文。
     * @param message 被点击的消息的实体信息。
     * @return 返回True不执行后续SDK操作，返回False继续执行SDK操作。
     */
    @Override
    public boolean onMessageClick(final Context context, final View view, final Message message) {

        Log.e(TAG, "----onMessageClick");

        /**
         * demo 代码  开发者需替换成自己的代码。
         */
        if  (message.getContent() instanceof LocationMessage){

            LocationMessage locationMessage = (LocationMessage) message.getContent();

            String geoString= String.format("geo:%s,%s?q=%s",
                    String.valueOf(locationMessage.getLat()),
                    String.valueOf(locationMessage.getLng()),
                    locationMessage.getPoi());

            Uri mUri = Uri.parse(geoString);
            Intent mIntent = new Intent(Intent.ACTION_VIEW,mUri);
            context.startActivity(mIntent);

        }
        else  if  (message.getContent() instanceof RichContentMessage)
        {
            RichContentMessage mRichContentMessage = (RichContentMessage) message.getContent();

            String urlString =mRichContentMessage.getUrl();

            String lessonID = ShareDefine.getLessonIDFromOfficalURL(urlString);

            if (lessonID.length()!=0)
            {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("lessonID", lessonID);
                context.startActivity(intent);
            }
            else
            {
                Intent intent = new Intent(context,FlyingWebViewActivity.class);
                intent.putExtra("url", urlString);
                context.startActivity(intent);
            }

            Log.d("Begavior", "extra:" + mRichContentMessage.getExtra());

        }
        else if (message.getContent() instanceof ImageMessage)
        {

            ImageMessage imageMessage = (ImageMessage) message.getContent();
            Intent intent = new Intent(context, ShowPhotoActivity.class);

            intent.putExtra("photo", imageMessage.getRemoteUri());

            context.startActivity(intent);
        }
        else {

            return false;
        }

        Log.d("Begavior", message.getObjectName() + ":" + message.getMessageId());

        return true;
    }


    /**
     * 当点击链接消息时执行。
     *
     * @param context 上下文。
     * @param link    被点击的链接。
     * @return 如果用户自己处理了点击后的逻辑处理，则返回 true， 否则返回 false, false 走融云默认处理方式。
     */
    @Override
    public boolean onMessageLinkClick(Context context, String link) {

        Intent intent = new Intent(context,FlyingWebViewActivity.class);

        intent.putExtra("url", link);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        context.startActivity(intent);

        return true;
    }

    @Override
    public boolean onMessageLongClick(final Context context, View view, Message message) {

       if (message.getContent() instanceof RichContentMessage)
        {
            /*
            RichContentMessage mRichContentMessage = (RichContentMessage) message.getContent();

            String urlString =mRichContentMessage.getUrl();

            String lessonID = ShareDefine.getLessonIDFromOfficalURL(urlString);

            if (lessonID.length()!=0)
            {
            Intent intent = new Intent(context, FlyingLocationActivity.class);
            intent.putExtra("location", message.getContent());
            context.startActivity(intent);

            }
            else
            {
                Intent intent = new Intent(context,FlyingWebViewActivity.class);
                intent.putExtra("url", urlString);
                context.startActivity(intent);
            }

            Log.d("Begavior", "extra:" + mRichContentMessage.getExtra());
            */

            return false;
        }
        else if (message.getContent() instanceof ImageMessage)
        {
            final ImageMessage imageMessage = (ImageMessage) message.getContent();
            final Uri uri = imageMessage.getRemoteUri();

            int itmesRes= R.array.dealWtihPicWaysQR;

            new MaterialDialog.Builder(context)
                    .items(itmesRes)
                    .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            /**
                             * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                             * returning false here won't allow the newly selected radio button to actually be selected.
                             **/
                            switch (which) {
                                case 0:
                                {
                                    Picasso.with(context)
                                            .load(uri.toString())
                                            .into(new Target() {
	                                            @Override
	                                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

		                                            FlyingFileManager.savePhotoToLocal(bitmap, null);
		                                            Toast.makeText(context, "已经成功保存图片", Toast.LENGTH_SHORT).show();
	                                            }

	                                            @Override
	                                            public void onBitmapFailed(Drawable errorDrawable) {

	                                            }

	                                            @Override
	                                            public void onPrepareLoad(Drawable placeHolderDrawable) {

	                                            }
                                            });

                                    break;
                                }

                                case 1:

                                    break;

                                case 2:
                                {
	                                Picasso.with(context)
			                                .load(uri.toString())
			                                .into(new Target() {
				                                @Override
				                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

					                                String barcode = new BitmapToText(bitmap).getText();

					                                if (barcode != null) {
						                                dealWithScanString(context,barcode);
					                                }
					                                else
					                                {
						                                Toast.makeText(context, "没有什么解析结果！", Toast.LENGTH_SHORT).show();
					                                }				                                }

				                                @Override
				                                public void onBitmapFailed(Drawable errorDrawable) {

					                                Toast.makeText(context, "获取图片失败，无法解析！", Toast.LENGTH_SHORT).show();

				                                }

				                                @Override
				                                public void onPrepareLoad(Drawable placeHolderDrawable) {

				                                }
			                                });
                                    break;
                                }
                            }

                            return true;
                        }
                    })
                    .show();

            return true;
        }
        else if (message.getContent() instanceof TextMessage) {

            final TextMessage textMessage = (TextMessage) message.getContent();

            int itmesRes=R.array.dealWtihTextWays;

            new MaterialDialog.Builder(context)
                    .items(itmesRes)
                    .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            /**
                             * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                             * returning false here won't allow the newly selected radio button to actually be selected.
                             **/
                            switch (which)
                            {
                                case 0:

                                    ClipboardManager clipboard = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
                                    ClipData textCd = ClipData.newPlainText("clipboardText",textMessage.getContent());
                                    clipboard.setPrimaryClip(textCd);

                                    break;

                                case 1:

                                    break;
                            }

                            return true;
                        }
                    })
                    .show();

            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * 连接状态监听器，以获取连接相关状态:ConnectionStatusListener 的回调方法，网络状态变化时执行。
     *
     * @param status 网络状态。
     */
    @Override
    public void onChanged(ConnectionStatus status) {
        Log.d(TAG, "onChanged:" + status);
        if (status.getMessage().equals(ConnectionStatus.DISCONNECTED.getMessage())) {
        }
    }

    /**
     * 点击会话列表 item 后执行。
     *
     * @param context      上下文。
     * @param view         触发点击的 View。
     * @param conversation 会话条目。
     * @return 返回 true 不再执行融云 SDK 逻辑，返回 false 先执行融云 SDK 逻辑再执行该方法。
     */
    @Override
    public boolean onConversationClick(Context context, View view, UIConversation conversation) {

        if (conversation.getConversationType()== Conversation.ConversationType.PRIVATE ||
                conversation.getConversationType()== Conversation.ConversationType.SYSTEM||
                conversation.getConversationType()== Conversation.ConversationType.CUSTOMER_SERVICE||
                conversation.getConversationType()== Conversation.ConversationType.APP_PUBLIC_SERVICE||
                conversation.getConversationType()== Conversation.ConversationType.PUBLIC_SERVICE)
        {

            String name = FlyingIMContext.getInstance().getUserNameByUserId(conversation.getConversationTargetId());

            RongIM.getInstance().startPrivateChat(context,conversation.getConversationTargetId(), name);


//            if (RongIM.getInstance() != null && userInfo!=null)
//            {
////                Intent intent = new Intent(context, com.birdcopy.BirdCopyApp.IM.FlyingConversationActivity.class);
////                intent.setAction(Intent.ACTION_VIEW);
////                intent.putExtra("ConversationType", "PRIVATE");
////                intent.putExtra("title", userInfo.getName());
////                intent.putExtra("targetId",userInfo.getUserId());
////                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////
////                context.startActivity(intent);
//
//
//                //RongIM.getInstance().startConversation(context, Conversation.ConversationType.PRIVATE, conversation.getConversationTargetId(), "title");
//            }
        }
        return true;

    }

    @Override
    public boolean onConversationPortraitClick(Context context, Conversation.ConversationType conversationType, String s) {
        return false;
    }

    @Override
    public boolean onConversationPortraitLongClick(Context context, Conversation.ConversationType conversationType, String s) {
        return false;
    }

    /**
     * 长按会话列表 item 后执行。
     *
     * @param context      上下文。
     * @param view         触发点击的 View。
     * @param conversation 长按会话条目。
     * @return 返回 true 不再执行融云 SDK 逻辑，返回 false 先执行融云 SDK 逻辑再执行该方法。
     */
    @Override
    public boolean onConversationLongClick(Context context, View view, UIConversation conversation) {
        return false;
    }

    /**
     * 可以根据群组 id 修改群成员的群昵称
     * @param groupId
     * @param userId
     * @return
     */
    @Override
    public GroupUserInfo getGroupUserInfo(String groupId, String userId) {

        GroupUserInfo groupUserInfo = new GroupUserInfo("49", "22830", "hehe");
        RongIM.getInstance().refreshGroupUserInfoCache(groupUserInfo);
        return groupUserInfo;
    }

    public  void dealWithScanString(final Context context,String scanStr)
    {
        String type = ShareDefine.judgeScanType(scanStr);

        if (type.equals(ShareDefine.KQRTyepeWebURL))
        {
            String lessonID = ShareDefine.getLessonIDFromOfficalURL(scanStr);
            if (lessonID != null)
            {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("lessonID", lessonID);
                context.startActivity(intent);
            }
            else
            {
                Intent webAdvertisingActivityIntent = new Intent(context, FlyingWebViewActivity.class);
                webAdvertisingActivityIntent.putExtra("url", scanStr);
                context.startActivity(webAdvertisingActivityIntent);
            }
        }
        else if (type.equals(ShareDefine.KQRTyepeChargeCard))
        {
            if (scanStr != null)
            {
                FlyingHttpTool.chargingCrad(FlyingDataManager.getCurrentPassport(),
                        FlyingDataManager.getBirdcopyAppID(),
                        scanStr,
                        new FlyingHttpTool.ChargingCradListener() {
                            @Override
                            public void completion(String resultStr) {

                                Toast.makeText(context, resultStr, Toast.LENGTH_LONG).show();
                            }
                        });
            }
        }
        else if (type.equals(ShareDefine.KQRTypeLogin))
        {
            if (scanStr != null)
            {

                String loginID = ShareDefine.getLoginIDFromQR(scanStr);

                if (loginID!=null) {

                    FlyingHttpTool.loginWithQR(loginID,
                            FlyingDataManager.getCurrentPassport(),
		                    FlyingDataManager.getBirdcopyAppID(),
                            new FlyingHttpTool.LoginWithQRListener() {
                                @Override
                                public void completion(boolean isOK) {

                                    if (isOK)
                                    {
                                        Toast.makeText(context, "登录成功", Toast.LENGTH_LONG).show();
                                    }
                                    else
                                    {
                                        Toast.makeText(context, "登录失败", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        }
        else  if (type.equals(ShareDefine.KQRTyepeCode))
        {
            if (scanStr != null)
            {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("lessonID", scanStr);
                context.startActivity(intent);
            }
        }
    }
}
