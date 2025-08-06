package io.taptalk.taptalklive.Fragment;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.CLEAR_ROOM_LIST;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.CLEAR_ROOM_LIST_BADGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.ROOM_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_SYSTEM_MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.OPEN_CHAT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.REFRESH_TOKEN_RENEWED;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RELOAD_ROOM_LIST;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.SystemMessageAction.UPDATE_ROOM;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.SystemMessageAction.UPDATE_USER;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.TYPING_INDICATOR_TIMEOUT;
import static io.taptalk.taptalklive.Const.TTLConstant.TapTalkInstanceKey.TAPTALK_INSTANCE_KEY;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.taptalk.TapTalk.API.View.TAPDefaultDataView;
import io.taptalk.TapTalk.BuildConfig;
import io.taptalk.TapTalk.Data.Message.TAPMessageEntity;
import io.taptalk.TapTalk.Helper.OverScrolled.OverScrollDecoratorHelper;
import io.taptalk.TapTalk.Helper.TAPBroadcastManager;
import io.taptalk.TapTalk.Helper.TAPChatRecyclerView;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TAPVerticalDecoration;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Helper.TapTalkDialog;
import io.taptalk.TapTalk.Interface.TapTalkNetworkInterface;
import io.taptalk.TapTalk.Listener.TAPChatListener;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Listener.TAPSocketListener;
import io.taptalk.TapTalk.Listener.TapCommonListener;
import io.taptalk.TapTalk.Listener.TapCoreGetStringArrayListener;
import io.taptalk.TapTalk.Listener.TapListener;
import io.taptalk.TapTalk.Manager.AnalyticsManager;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPConnectionManager;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Manager.TAPEncryptorManager;
import io.taptalk.TapTalk.Manager.TAPMessageStatusManager;
import io.taptalk.TapTalk.Manager.TAPNetworkStateManager;
import io.taptalk.TapTalk.Manager.TAPNotificationManager;
import io.taptalk.TapTalk.Manager.TapCoreRoomListManager;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetRoomListResponse;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPTypingModel;
import io.taptalk.TapTalk.View.Activity.TapUIChatActivity;
import io.taptalk.TapTalk.ViewModel.TAPRoomListViewModel;
import io.taptalk.taptalklive.R;
import io.taptalk.taptalklive.TapTalkLive;
import io.taptalk.taptalklive.adapter.TTLCaseListAdapter;
import io.taptalk.taptalklive.model.TTLCaseListModel;

public class TTLCaseListFragment extends Fragment {

    private String TAG = TTLCaseListFragment.class.getSimpleName();

    private Activity activity;
    private TAPSocketListener socketListener;

    private List<TTLCaseListModel> caseLists;
    private Map<String, TTLCaseListModel> caseListMap;
    private TAPChatRecyclerView rvCaseList;
    private LinearLayoutManager llm;
    private TTLCaseListAdapter adapter;
    private HashMap<String, CountDownTimer> typingIndicatorTimeoutTimers;
    private RequestManager glide;
    private TapTalkDialog userNullErrorDialog;
    //private ListItemSwipeCallback listItemSwipeCallback;
    private int roomBadgeCount;
    private int lastBadgeCount;

    private TAPChatListener chatListener;

    public TTLCaseListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ttl_fragment_case_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initListener();
        initData();
        bindViews(view);
        initView();
        viewLoadedSequence();
        TAPBroadcastManager.register(
            activity,
            roomListBroadcastReceiver,
            REFRESH_TOKEN_RENEWED,
            RELOAD_ROOM_LIST,
            CLEAR_ROOM_LIST_BADGE,
            CLEAR_ROOM_LIST,
            OPEN_CHAT
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        TAPNotificationManager.getInstance(TAPTALK_INSTANCE_KEY).setRoomListAppear(true);
        new Thread(() -> TAPChatManager.getInstance(TAPTALK_INSTANCE_KEY).saveMessageToDatabase()).start();
        updateQueryRoomListFromBackground();
        addNetworkListener();
        openTapTalkSocketWhenNeeded();
    }

    @Override
    public void onPause() {
        super.onPause();
        TAPNotificationManager.getInstance(TAPTALK_INSTANCE_KEY).setRoomListAppear(false);
        removeNetworkListener();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        removeSocketListener();
        TAPBroadcastManager.unregister(activity, roomListBroadcastReceiver);
        closeTapTalkSocketWhenNeeded();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        TAPChatManager.getInstance(TAPTALK_INSTANCE_KEY).removeChatListener(chatListener);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden)
            TAPNotificationManager.getInstance(TAPTALK_INSTANCE_KEY).setRoomListAppear(false);
        else {
            TAPNotificationManager.getInstance(TAPTALK_INSTANCE_KEY).setRoomListAppear(true);
            updateQueryRoomListFromBackground();
        }
    }

    private void initListener() {
        if (TapTalk.getTapTalkInstance(TAPTALK_INSTANCE_KEY) != null) {
            TapTalk.getTapTalkInstance(TAPTALK_INSTANCE_KEY).removeGlobalChatListener();
        }
        chatListener = new TAPChatListener() {
            @Override
            public void onReceiveMessageInOtherRoom(TAPMessageModel message) {
                processMessageFromSocket(message);
            }

            @Override
            public void onReceiveMessageInActiveRoom(TAPMessageModel message) {
                processMessageFromSocket(message);
            }

            @Override
            public void onUpdateMessageInOtherRoom(TAPMessageModel message) {
                processMessageFromSocket(message);
            }

            @Override
            public void onUpdateMessageInActiveRoom(TAPMessageModel message) {
                processMessageFromSocket(message);
            }

            @Override
            public void onDeleteMessageInOtherRoom(TAPMessageModel message) {
                processMessageFromSocket(message);
            }

            @Override
            public void onDeleteMessageInActiveRoom(TAPMessageModel message) {
                processMessageFromSocket(message);
            }

            @Override
            public void onSendMessage(TAPMessageModel message) {
                processMessageFromSocket(message);
            }

            @Override
            public void onReadMessage(String roomID) {
//                TAPRoomListModel roomListModel = roomPointer.get(roomID);
//                if (roomListModel != null && roomListModel.isMarkedAsUnread()) {
//                    TapCoreMessageManager.getInstance(TAPTALK_INSTANCE_KEY).markMessageAsRead(roomListModel.getLastMessage().getMessageID());
//                    removeUnreadRoomFromPreference(roomID);
//                }
                updateUnreadCountPerRoom(roomID);
            }

            @Override
            public void onReceiveStartTyping(TAPTypingModel typingModel) {
                showTypingIndicator(typingModel, true);
            }

            @Override
            public void onReceiveStopTyping(TAPTypingModel typingModel) {
                showTypingIndicator(typingModel, false);
            }
        };
        TAPChatManager.getInstance(TAPTALK_INSTANCE_KEY).addChatListener(chatListener);

        typingIndicatorTimeoutTimers = new HashMap<>();

        addSocketListener();
    }

    private void initData() {
        caseLists = new ArrayList<>();
        caseListMap = new HashMap<>();
    }

    private void bindViews(View view) {
        rvCaseList = view.findViewById(R.id.rv_case_list);
    }

    private void initView() {
        activity = getActivity();

        if (null == activity || null == getContext()) {
            return;
        }
        activity.getWindow().setBackgroundDrawable(null);

        glide = Glide.with(this);

        adapter = new TTLCaseListAdapter(caseLists, glide, (caseListModel, position) -> {
            if (activity != null &&
                caseListModel != null &&
                TapTalkLive.getInstance() != null &&
                TapTalkLive.getInstance().tapTalkLiveListener != null
            ) {
                TapTalkLive.getInstance().tapTalkLiveListener.onCaseListItemTapped(activity, caseListModel.getLastMessage());
            }
        });
        llm = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false) {
            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                try {
                    super.onLayoutChildren(recycler, state);
                }
                catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        };
        rvCaseList.setAdapter(adapter);
        rvCaseList.setLayoutManager(llm);
        rvCaseList.setHasFixedSize(true);
//        float clamp;
//        try {
//            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
//            clamp = Math.round(64 * (float) displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
//        } catch (Exception e) {
//            clamp = 48f;
//        }
//        listItemSwipeCallback = new ListItemSwipeCallback(TAPTALK_INSTANCE_KEY);
//        listItemSwipeCallback.setClamp(clamp);
//        listItemSwipeCallback.setListener(onMoveAndSwipeListener);
//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(listItemSwipeCallback);
//        itemTouchHelper.attachToRecyclerView(rvCaseList);
        OverScrollDecoratorHelper.setUpOverScroll(rvCaseList, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        SimpleItemAnimator messageAnimator = (SimpleItemAnimator) rvCaseList.getItemAnimator();
        if (null != messageAnimator) messageAnimator.setSupportsChangeAnimations(false);
    }

    private void openTapTalkSocketWhenNeeded() {
        if (TapTalk.getTapTalkSocketConnectionMode(TAPTALK_INSTANCE_KEY) == TapTalk.TapTalkSocketConnectionMode.CONNECT_IF_NEEDED &&
                !TapTalk.isConnected(TAPTALK_INSTANCE_KEY) && TapTalk.isForeground) {
            TapTalk.connect(TAPTALK_INSTANCE_KEY, new TapCommonListener() {
            });
        }
    }

    private void closeTapTalkSocketWhenNeeded() {
        if (TapTalk.getTapTalkSocketConnectionMode(TAPTALK_INSTANCE_KEY) == TapTalk.TapTalkSocketConnectionMode.CONNECT_IF_NEEDED
                && TapTalk.isConnected(TAPTALK_INSTANCE_KEY)) {
            TapTalk.disconnect(TAPTALK_INSTANCE_KEY);
        }
    }

    private void viewLoadedSequence() {
        if (TAPRoomListViewModel.isShouldNotLoadFromAPI(TAPTALK_INSTANCE_KEY) && null != TAPChatManager.getInstance(TAPTALK_INSTANCE_KEY).getActiveUser()) {
            // Load room list from database if app is on foreground
            TAPDataManager.getInstance(TAPTALK_INSTANCE_KEY).getRoomList(true, dbListener);
        }
        else if (null != TAPChatManager.getInstance(TAPTALK_INSTANCE_KEY).getActiveUser()) {
            // Run full cycle if app is on background or on first open
            // TODO: 18 Feb 2020 DATABASE FIRST QUERY CALLED TWICE WHEN CLOSING APP (NOT KILLED)
            runFullRefreshSequence();
        }
        else if (TapTalk.checkTapTalkInitialized() && TapTalk.isAuthenticated(TAPTALK_INSTANCE_KEY)) {
            // Clear data when refresh token is expired
            AnalyticsManager.getInstance(TAPTALK_INSTANCE_KEY).trackEvent("View Loaded Sequence Failed");
            TapTalk.clearAllTapTalkData(TAPTALK_INSTANCE_KEY);
            for (TapListener listener : TapTalk.getTapTalkListeners(TAPTALK_INSTANCE_KEY)) {
                listener.onTapTalkRefreshTokenExpired();
            }
        }
        else if (null == TAPChatManager.getInstance(TAPTALK_INSTANCE_KEY).getActiveUser()) {
            // Show setup failed if active user is null
            if (BuildConfig.DEBUG && null != activity) {
                if (null == userNullErrorDialog) {
                    userNullErrorDialog = new TapTalkDialog(new TapTalkDialog.Builder(activity)
                            .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                            .setTitle(getString(io.taptalk.TapTalk.R.string.tap_error))
                            .setMessage(getString(io.taptalk.TapTalk.R.string.tap_error_active_user_is_null))
                            .setCancelable(false)
                            .setPrimaryButtonTitle(getString(io.taptalk.TapTalk.R.string.tap_ok)));
                }
                userNullErrorDialog.show();
            }
            Log.e(TAG, getString(io.taptalk.TapTalk.R.string.tap_error_active_user_is_null));
        }
    }

    private void getDatabaseAndAnimateResult() {
        TAPDataManager.getInstance(TAPTALK_INSTANCE_KEY).getRoomList(true, dbListener);
    }

    private void runFullRefreshSequence() {
        // Check and update unread badge before updating view if recycler is not empty
        // Update unread badge after view is updated if recycler is empty
        TAPDataManager.getInstance(TAPTALK_INSTANCE_KEY).getRoomList(TAPChatManager.getInstance(TAPTALK_INSTANCE_KEY).getSaveMessages(), caseLists.size() > 0, dbListener);
    }

    private void fetchDataFromAPI() {
        if (TapTalkLive.isTapTalkLiveInitialized) {
            // Call to refresh new messages
            TAPDataManager.getInstance(TAPTALK_INSTANCE_KEY).getNewAndUpdatedMessage(roomListView);
        } else {
            // Call on first load
            // TODO: CALL CASE LIST?
//            TAPDataManager.getInstance(TAPTALK_INSTANCE_KEY).getMessageRoomListAndUnread(TAPChatManager.getInstance(TAPTALK_INSTANCE_KEY).getActiveUser().getUserID(), roomListView);
        }
    }

    private void reloadLocalDataAndUpdateUILogic(boolean isAnimated) {
        if (null != activity && null != getContext()) {
            activity.runOnUiThread(() -> {
                if (adapter != null) {
                    adapter.setItems(caseLists, false);
                    while (rvCaseList.getItemDecorationCount() > 0) {
                        rvCaseList.removeItemDecorationAt(0);
                    }
                    rvCaseList.addItemDecoration(new TAPVerticalDecoration(0, TAPUtils.dpToPx(getResources(), 16f), adapter.getItemCount() - 1));
                }
                if (!TAPRoomListViewModel.isShouldNotLoadFromAPI(TAPTALK_INSTANCE_KEY)) {
                    fetchDataFromAPI();
                }
            });
        }
    }

    private void processMessageFromSocket(TAPMessageModel message) {
        TTLCaseListModel caseList = caseListMap.get(message.getRoom().getRoomID());

        if (null != caseList && null != message.getIsHidden() && !message.getIsHidden()) {
            // Received message in an existing room list
            caseList.setLastMessageTimestamp(message.getCreated());
            TAPMessageModel roomLastMessage = caseList.getLastMessage();

            if (roomLastMessage.getLocalID().equals(message.getLocalID()) && null != activity) {
                // Update room list's last message data
                roomLastMessage.updateValue(message);
                activity.runOnUiThread(() -> adapter.notifyItemChanged(caseLists.indexOf(caseList)));
            } else if (roomLastMessage.getCreated() < message.getCreated()) {
                int oldPos = caseLists.indexOf(caseList);
                // Update room list's last message with the new message from socket
                caseList.setLastMessage(message);

                // Add unread count by 1 if sender is not self
                if (!caseList.getLastMessage().getUser().getUserID().equals(
                        TAPChatManager.getInstance(TAPTALK_INSTANCE_KEY)
                                .getActiveUser().getUserID())) {
                    caseList.setUnreadCount(caseList.getUnreadCount() + 1);
                }

                // Show mention badge if user is mentioned
                if (TAPUtils.isActiveUserMentioned(caseList.getLastMessage(),
                        TAPChatManager.getInstance(TAPTALK_INSTANCE_KEY).getActiveUser())) {
                    caseList.setUnreadMentions(caseList.getUnreadMentions() + 1);
                }

                // Move room to top
                caseLists.remove(caseList);
                caseLists.add(0, caseList);

                if (null != activity) {
                    activity.runOnUiThread(() -> {
                        adapter.notifyItemChanged(oldPos);
                        adapter.notifyItemMoved(oldPos, 0);
                        // Scroll to top
                        if (llm.findFirstCompletelyVisibleItemPosition() == 0)
                            rvCaseList.scrollToPosition(0);
                    });
                }
            }
        } else if (null != activity && null != message.getIsHidden() && !message.getIsHidden()) {
            // Received message in a new room list
            TTLCaseListModel newCaseList = new TTLCaseListModel(message);
            if (!newCaseList.getLastMessage().getUser().getUserID()
                    .equals(TAPChatManager.getInstance(TAPTALK_INSTANCE_KEY)
                            .getActiveUser().getUserID())) {
                newCaseList.setUnreadCount(1);
            }

            if (TAPUtils.isActiveUserMentioned(newCaseList.getLastMessage(),
                    TAPChatManager.getInstance(TAPTALK_INSTANCE_KEY).getActiveUser())) {
                newCaseList.setUnreadMentions(1);
            }

            caseLists.add(0, newCaseList);
            caseListMap.put(message.getRoom().getRoomID(), newCaseList);
            activity.runOnUiThread(() -> {
                if (0 == adapter.getItems().size())
                    adapter.addItem(newCaseList);
                adapter.notifyItemInserted(0);

                if (llm.findFirstCompletelyVisibleItemPosition() == 0)
                    rvCaseList.scrollToPosition(0);
            });
        }

        if (null != caseList && message.getType() == TYPE_SYSTEM_MESSAGE &&
                null != message.getAction() &&
                (message.getAction().equals(UPDATE_ROOM) || message.getAction().equals(UPDATE_USER))) {
            // Update room details
            activity.runOnUiThread(() -> adapter.notifyItemChanged(caseLists.indexOf(caseList)));
        }
        calculateBadgeCount();
    }

    private void showTypingIndicator(TAPTypingModel typingModel, boolean isTyping) {
        String roomID = typingModel.getRoomID();
        if (null == activity || !caseListMap.containsKey(roomID) || null == typingModel.getUser()) {
            return;
        }
        TTLCaseListModel caseListModel = caseListMap.get(roomID);
        if (null == caseListModel) {
            return;
        }

        activity.runOnUiThread(() -> {
            if (isTyping) {
                caseListModel.getTypingUsers().put(typingModel.getUser().getUserID(), typingModel.getUser());
                CountDownTimer roomTimer = typingIndicatorTimeoutTimers.get(roomID);
                if (null != roomTimer) {
                    roomTimer.cancel();
                } else {
                    roomTimer = new CountDownTimer(TYPING_INDICATOR_TIMEOUT, 1000L) {
                        @Override
                        public void onTick(long l) {

                        }

                        @Override
                        public void onFinish() {
                            // Hide typing status on countdown finish
                            caseListModel.getTypingUsers().clear();
                            typingIndicatorTimeoutTimers.remove(roomID);
                            adapter.notifyItemChanged(caseLists.indexOf(caseListModel));
                        }
                    };
                    typingIndicatorTimeoutTimers.put(roomID, roomTimer);
                }
                // Restart typing timeout timer
                roomTimer.start();
            } else {
                caseListModel.removeTypingUser(typingModel.getUser().getUserID());
            }

            adapter.notifyItemChanged(caseLists.indexOf(caseListModel));
        });
    }

    private void getUnreadRoomList() {
        TapCoreRoomListManager.getInstance(TAPTALK_INSTANCE_KEY).getMarkedAsUnreadChatRoomList(new TapCoreGetStringArrayListener() {
            @Override
            public void onSuccess(@NonNull ArrayList<String> arrayList) {
                for (String id : arrayList) {
//                    updateRoomUnreadMark(id, true);
                    adapter.notifyItemChanged(caseLists.indexOf(caseListMap.get(id)));
                }
                calculateBadgeCount();
            }

            @Override
            public void onError(@org.jetbrains.annotations.Nullable String errorCode, @org.jetbrains.annotations.Nullable String errorMessage) {
                super.onError(errorCode, errorMessage);
                calculateBadgeCount();
            }
        });
    }

    private TAPDefaultDataView<TAPGetRoomListResponse> roomListView = new TAPDefaultDataView<>() {
        @Override
        public void onSuccess(TAPGetRoomListResponse response) {
            if (response.getMessages().size() > 0) {
                List<TAPMessageEntity> tempMessage = new ArrayList<>();
                List<TAPMessageModel> deliveredMessages = new ArrayList<>();
//                List<String> userIds = new ArrayList<>();
//                List<TAPUserModel> userModels = new ArrayList<>();
                for (HashMap<String, Object> messageMap : response.getMessages()) {
                    try {
                        TAPMessageModel message = TAPEncryptorManager.getInstance().decryptMessage(messageMap);
                        if (message != null) {
                            TAPMessageEntity entity = TAPMessageEntity.fromMessageModel(message);
                            tempMessage.add(entity);

                            // Save undelivered messages to list
                            if (null == message.getIsDelivered() || (null != message.getIsDelivered() && !message.getIsDelivered())) {
                                deliveredMessages.add(message);
                            }

                            // Update Contact
//                        if (message.getUser().getUserID().equals(TAPChatManager.getInstance(TAPTALK_INSTANCE_KEY).getActiveUser().getUserID())) {
//                            // User is self, get other user data from API
//                            userIds.add(TAPChatManager.getInstance(TAPTALK_INSTANCE_KEY).getOtherUserIdFromRoom(message.getRoom().getRoomID()));
//                        } else {
//                            // Save user data to contact manager
//                            userModels.add(message.getUser());
//                        }

                            if (null != message.getIsDeleted() && message.getIsDeleted()) {
                                TAPDataManager.getInstance(TAPTALK_INSTANCE_KEY).deletePhysicalFile(entity);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
//                TAPContactManager.getInstance(TAPTALK_INSTANCE_KEY).updateUserData(userModels);

                // Update status to delivered
                if (deliveredMessages.size() > 0) {
                    TAPMessageStatusManager.getInstance(TAPTALK_INSTANCE_KEY).updateMessageStatusToDelivered(deliveredMessages);
                }

                // Get updated other user data from API
//                if (userIds.size() > 0) {
//                    TAPDataManager.getInstance(TAPTALK_INSTANCE_KEY).getMultipleUsersByIdFromApi(userIds, getMultipleUserView);
//                }

                // Save message to database
                TAPDataManager.getInstance(TAPTALK_INSTANCE_KEY).insertToDatabase(tempMessage, false, new TAPDatabaseListener() {
                    @Override
                    public void onInsertFinished() {
                        // Reload newest room list from database
                        getDatabaseAndAnimateResult();
                    }
                });
            } else {
                reloadLocalDataAndUpdateUILogic(true);
            }

            getUnreadRoomList();
            TAPRoomListViewModel.setShouldNotLoadFromAPI(TAPTALK_INSTANCE_KEY, true);
        }
    };

    private TAPDatabaseListener<TAPMessageEntity> dbListener = new TAPDatabaseListener<>() {

        @Override
        public void onSelectFinishedWithUnreadCount(List<TAPMessageEntity> entities, Map<String, Integer> unreadMap, Map<String, Integer> mentionMap) {
            List<TTLCaseListModel> selectedCaseLists = new ArrayList<>();
            caseListMap.clear();
            int count = 0; // FIXME Count to load room list every 10 items
            int limit = 25;
            for (TAPMessageEntity entity : entities) {
                TAPMessageModel message = TAPMessageModel.fromMessageEntity(entity);
                TTLCaseListModel caseList = new TTLCaseListModel(message);
                if (null != unreadMap.get(entity.getRoomID())) {
                    caseList.setUnreadCount(unreadMap.get(entity.getRoomID()));
                    caseList.setUnreadMentions(mentionMap.get(entity.getRoomID()));
                }
                selectedCaseLists.add(caseList);
                caseListMap.put(message.getRoom().getRoomID(), caseList);
                if (++count % limit == 0) {
                    caseLists = selectedCaseLists;
                    activity.runOnUiThread(() -> adapter.setItems(caseLists));
                    limit = limit * 2;
                }
            }
            caseLists = selectedCaseLists;
            reloadLocalDataAndUpdateUILogic(false);
            getUnreadRoomList();
        }

        @Override
        public void onCountedUnreadCount(String roomID, int unreadCount, int mentionCount) {
            try {
                if (null != activity && caseListMap.containsKey(roomID) && null != caseListMap.get(roomID)) {
                    caseListMap.get(roomID).setUnreadCount(unreadCount);
                    caseListMap.get(roomID).setUnreadMentions(mentionCount);
                    activity.runOnUiThread(() -> adapter.notifyItemChanged(caseLists.indexOf(caseListMap.get(roomID))));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSelectedRoomList(List<TAPMessageEntity> entities, Map<String, Integer> unreadMap, Map<String, Integer> mentionMap) {
            List<TTLCaseListModel> selectedCaseLists = new ArrayList<>();
            //caseListMap.clear();
            for (TAPMessageEntity entity : entities) {
                TAPMessageModel message = TAPMessageModel.fromMessageEntity(entity);
                TTLCaseListModel caseList = new TTLCaseListModel(message);
                if (null != unreadMap.get(entity.getRoomID())) {
                    caseList.setUnreadCount(unreadMap.get(entity.getRoomID()));
                    caseList.setUnreadMentions(mentionMap.get(entity.getRoomID()));
                }
                selectedCaseLists.add(caseList);
                caseListMap.put(message.getRoom().getRoomID(), caseList);
            }

            caseLists = selectedCaseLists;
            reloadLocalDataAndUpdateUILogic(false);
            calculateBadgeCount();
        }
    };

//    OnMoveAndSwipeListener onMoveAndSwipeListener = new OnMoveAndSwipeListener() {
//        @Override
//        public void onItemClick(int position) {
//            TAPRoomListModel room = caseLists.get(position);
//            String roomId = room.getLastMessage().getRoom().getRoomID();
//            if (room.isMarkedAsUnread() || room.getNumberOfUnreadMessages() > 0) {
//                // read button
//                TapCoreMessageManager.getInstance(TAPTALK_INSTANCE_KEY).markAllMessagesInRoomAsRead(roomId);
//                updateRoomUnreadMark(roomId, false);
//            } else {
//                // unread button
//                TapCoreRoomListManager.getInstance(TAPTALK_INSTANCE_KEY).markChatRoomAsUnread(roomId, null);
//                //addUnreadRoomToPreference(roomId);
//                updateRoomUnreadMark(roomId, true);
//            }
//            adapter.notifyItemChanged(position);
//        }
//    };
//
//    private void removeUnreadRoomFromPreference(String roomId) {
//        ArrayList<String> unreadRoomListIds = TAPDataManager.getInstance(TAPTALK_INSTANCE_KEY).getUnreadRoomIDs();
//        unreadRoomListIds.remove(roomId);
//        TAPDataManager.getInstance(TAPTALK_INSTANCE_KEY).saveUnreadRoomIDs(unreadRoomListIds);
//        updateRoomUnreadMark(roomId, false);
//    }
//
//    private void addUnreadRoomToPreference(String roomId) {
//        ArrayList<String> unreadRoomListIds = TAPDataManager.getInstance(TAPTALK_INSTANCE_KEY).getUnreadRoomIDs();
//        if (!unreadRoomListIds.contains(roomId)) {
//            unreadRoomListIds.add(roomId);
//            TAPDataManager.getInstance(TAPTALK_INSTANCE_KEY).saveUnreadRoomIDs(unreadRoomListIds);
//            updateRoomUnreadMark(roomId, true);
//        }
//    }
//
//    private void updateRoomUnreadMark(String roomId, boolean isMarkAsUnread) {
//        TAPRoomListModel room = caseListMap.get(roomId);
//        if (room != null) {
//            room.setMarkedAsUnread(isMarkAsUnread);
//            if (!isMarkAsUnread) {
//                room.setNumberOfUnreadMessages(0);
//            }
//        }
//    }

    private void updateQueryRoomListFromBackground() {
        if (TAPDataManager.getInstance(TAPTALK_INSTANCE_KEY).isNeedToQueryUpdateRoomList()) {
            runFullRefreshSequence();
            TAPDataManager.getInstance(TAPTALK_INSTANCE_KEY).setNeedToQueryUpdateRoomList(false);
        }
    }

    private final TapTalkNetworkInterface networkListener = () -> {
        if (TapTalkLive.isTapTalkLiveInitialized) {
            updateQueryRoomListFromBackground();
        }
    };

    private void addNetworkListener() {
        TAPNetworkStateManager.getInstance(TAPTALK_INSTANCE_KEY).addNetworkListener(networkListener);
    }

    private void removeNetworkListener() {
        TAPNetworkStateManager.getInstance(TAPTALK_INSTANCE_KEY).removeNetworkListener(networkListener);
    }

    private void updateUnreadCountPerRoom(String roomID) {
        new Thread(() -> {
            TTLCaseListModel caseList = caseListMap.get(roomID);
            if (null != getActivity() && null != caseList) {
                Integer roomUnreadList = TAPMessageStatusManager.getInstance(TAPTALK_INSTANCE_KEY).getUnreadList().get(roomID);
                if (null != roomUnreadList) {
                    if (roomUnreadList <= caseList.getUnreadCount()) {
                        // Subtract unread count from room
                        caseList.setUnreadCount(caseList.getUnreadCount() - roomUnreadList);
                    } else {
                        // Set room unread count to 0
                        caseList.setUnreadCount(0);
                    }
                    TAPMessageStatusManager.getInstance(TAPTALK_INSTANCE_KEY).clearUnreadListPerRoomID(roomID);
                }
                Integer roomUnreadMention = TAPMessageStatusManager.getInstance(TAPTALK_INSTANCE_KEY).getUnreadMention().get(roomID);
                if (null != roomUnreadMention) {
                    if (roomUnreadMention <= caseList.getUnreadMentions()) {
                        // Subtract unread mention from room
                        caseList.setUnreadMentions(caseList.getUnreadMentions() - roomUnreadMention);
                    } else {
                        // Set room unread mention to 0
                        caseList.setUnreadMentions(0);
                    }
                    TAPMessageStatusManager.getInstance(TAPTALK_INSTANCE_KEY).clearUnreadMentionPerRoomID(roomID);
                }
                getActivity().runOnUiThread(() -> adapter.notifyItemChanged(caseLists.indexOf(caseList)));
            }
            calculateBadgeCount();
        }).start();
    }

    private BroadcastReceiver roomListBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null == intent.getAction()) {
                return;
            }
            switch (intent.getAction()) {
                case REFRESH_TOKEN_RENEWED:
                    if (null != userNullErrorDialog) {
                        userNullErrorDialog.dismiss();
                    }
                    // Token refreshed
                    initView();
                    viewLoadedSequence();
                    break;
                case CLEAR_ROOM_LIST:
                    // Logged out
                    initView();
                    viewLoadedSequence();
                    break;
                case OPEN_CHAT:
                    // Open Chat
                    TAPMessageModel messageModel = intent.getParcelableExtra(MESSAGE);
                    if (messageModel != null) {
                        TAPRoomModel room = messageModel.getRoom();
                        TapUIChatActivity.start(
                                context,
                                TAPTALK_INSTANCE_KEY,
                                room.getRoomID(),
                                room.getName(),
                                room.getImageURL(),
                                room.getType(),
                                room.getColor(),
                                messageModel.getLocalID());
                    }
                    break;
                default:
                    // Update room list
                    String roomID = intent.getStringExtra(ROOM_ID);
                    switch (intent.getAction()) {
                        case RELOAD_ROOM_LIST:
                            if (null != adapter) {
                                adapter.notifyItemChanged(caseLists.indexOf(
                                        caseListMap.get(roomID)));
                            }
                            break;
                        case CLEAR_ROOM_LIST_BADGE:
                            TTLCaseListModel caseListModel = caseListMap.get(roomID);
                            if (null != caseListModel) {
                                caseListModel.setUnreadCount(0);
                                caseListModel.setUnreadMentions(0);
                                TAPMessageStatusManager.getInstance(TAPTALK_INSTANCE_KEY).clearUnreadListPerRoomID(roomID);
                                TAPMessageStatusManager.getInstance(TAPTALK_INSTANCE_KEY).clearUnreadMentionPerRoomID(roomID);
                            }
                            break;
                    }
                    break;
            }
        }
    };

    private void calculateBadgeCount() {
        roomBadgeCount = 0;
        try {
            for (Map.Entry<String, TTLCaseListModel> entry : caseListMap.entrySet()) {
                if (entry.getValue().getUnreadCount() > 0) {
                    roomBadgeCount = roomBadgeCount + entry.getValue().getUnreadCount();
                }
//                else if (entry.getValue().isMarkedAsUnread()) {
//                    roomBadgeCount = roomBadgeCount + 1;
//                }
            }
        } catch (ConcurrentModificationException e) { // FIXME: 5 Dec 2019
            e.printStackTrace();
        }
        if (lastBadgeCount != roomBadgeCount) {
            for (TapListener listener : TapTalk.getTapTalkListeners(TAPTALK_INSTANCE_KEY)) {
                listener.onTapTalkUnreadChatRoomBadgeCountUpdated(roomBadgeCount);
            }
            lastBadgeCount = roomBadgeCount;
        }
    }

    private void addSocketListener() {
        if (TapTalk.getTapTalkSocketConnectionMode(TAPTALK_INSTANCE_KEY) == TapTalk.TapTalkSocketConnectionMode.CONNECT_IF_NEEDED) {
            socketListener = new TAPSocketListener() {
                @Override
                public void onSocketDisconnected() {
                    if (!TapTalk.isConnected(TAPTALK_INSTANCE_KEY) && TapTalk.isForeground) {
                        TapTalk.connect(TAPTALK_INSTANCE_KEY, new TapCommonListener() {});
                    }
                }
            };
            TAPConnectionManager.getInstance(TAPTALK_INSTANCE_KEY).addSocketListener(socketListener);
        }
    }

    private void removeSocketListener() {
        TAPConnectionManager.getInstance(TAPTALK_INSTANCE_KEY).removeSocketListener(socketListener);
    }
}
