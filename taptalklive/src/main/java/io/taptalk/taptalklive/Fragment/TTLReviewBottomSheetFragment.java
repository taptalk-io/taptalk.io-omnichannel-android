package io.taptalk.taptalklive.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import io.taptalk.TapTalk.View.BottomSheet.TAPAttachmentBottomSheet;

public class TTLReviewBottomSheetFragment extends BottomSheetDialogFragment {


    public TTLReviewBottomSheetFragment() {
        // Required empty public constructor
    }

//    public TTLReviewBottomSheetFragment(TAPAttachmentListener attachmentListener) {
//        this.attachmentListener = attachmentListener;
//    }

    public static TAPAttachmentBottomSheet newInstance() {
        TAPAttachmentBottomSheet fragment = new TAPAttachmentBottomSheet();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(io.taptalk.Taptalk.R.layout.tap_bottom_sheet_recycler, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        recyclerView = view.findViewById(io.taptalk.Taptalk.R.id.recyclerView);
//
//        recyclerView.setAdapter(new TAPAttachmentAdapter(isImagePickerBottomSheet, attachmentListener, onClickListener));
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
//        recyclerView.setHasFixedSize(true);
    }
}
