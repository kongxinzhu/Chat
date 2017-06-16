package com.marceme.marcefirebasechat.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.marceme.marcefirebasechat.R;
import com.marceme.marcefirebasechat.model.BlogMessage;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Jerry on 2017/6/16.
 */

public class BlogPostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<BlogMessage> mChatList;
    public static final int SENDER = 0;
    public static final int RECIPIENT = 1;
    public static final int TEXT = 2;
    public static final int IMAGE = 22;
    public static final int SENDER_TEXT = 2;
    public static final int SENDER_IMAGE = 22;
    public static final int RECIPIENT_TEXT = 3;
    public static final int RECIPIENT_IMAGE = 23;
    private Context context;

    public  BlogPostAdapter (List<BlogMessage> listOfFireChats, Context context) {
        this.mChatList = listOfFireChats;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        return mChatList.get(position).getRecipientOrSenderStatus();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int resultViewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());


        switch (resultViewType) {
//            case 2: //sender text
//                View viewSenderText = inflater.inflate(R.layout.text_message_sender, viewGroup, false);
//                viewHolder= new ViewHolderSenderText(viewSenderText);
//                break;
//            case 22: //sender image
//                View viewSenderImage = inflater.inflate(R.layout.image_message_sender, viewGroup, false);
//                viewHolder= new ViewHolderSenderImage(viewSenderImage);
//                break;
            case 3: //recipient text
                View viewRecipientText = inflater.inflate(R.layout.text_message_recipient, viewGroup, false);
                viewHolder=new ViewHolderRecipientText(viewRecipientText);
                break;
            case 23: //recipient image
                View viewRecipientImage = inflater.inflate(R.layout.image_message_recipient, viewGroup, false);
                viewHolder=new ViewHolderRecipientImage(viewRecipientImage);
                break;
            default:
                View viewSenderDefault = inflater.inflate(R.layout.text_message_recipient, viewGroup, false);
                viewHolder= new ViewHolderRecipientText(viewSenderDefault);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        switch (viewHolder.getItemViewType()){
//            case 2:
//                ViewHolderSenderText viewHolderSenderText=(ViewHolderSenderText) viewHolder;
//                configureSenderView(viewHolderSenderText,position);
//                break;
//            case 22:
//                ViewHolderSenderImage viewholderSenderImage=(ViewHolderSenderImage) viewHolder;
//                configureSenderView(viewholderSenderImage,position, context);
//                break;
            case 3:
                ViewHolderRecipientText viewHolderRecipientText=(ViewHolderRecipientText) viewHolder;
                configureRecipientView(viewHolderRecipientText,position);
                break;
            case 23:
                ViewHolderRecipientImage viewHolderRecipientImage=(ViewHolderRecipientImage) viewHolder;
                configureRecipientView(viewHolderRecipientImage,position, context);
                break;
        }
    }

    // TODO: 6/13/17
//    //set message text and photo, also can set text time
//    private void configureSenderView(ViewHolderSenderText viewHolderSenderText, int position) {
//        BlogMessage senderMessageText= mChatList.get(position);
//        viewHolderSenderText.getSenderMessageTextView().setText(senderMessageText.getText());
//        CircleImageView iview = viewHolderSenderText.getmSenderPhoto();
//
//        if (senderMessageText.getPhotoUrl() != null) {
//            Glide.with(context).load(senderMessageText.getPhotoUrl()).into(iview);
//        } else {
//            iview.setImageDrawable(ContextCompat.getDrawable(context,
//                    R.drawable.ic_action_user));
//        }
//
//    }
//
//    //use glide to load image url to imageview
//    private void configureSenderView(ViewHolderSenderImage viewHolderSenderImage, int position, Context context) {
//        Message senderMessageImage= mChatList.get(position);
//        if (senderMessageImage.getImageUrl() != null) {
//            Glide.with(context).load(senderMessageImage.getImageUrl()).into(viewHolderSenderImage.getSenderMessageImageView());
//        }
//        CircleImageView iview = viewHolderSenderImage.getmSenderPhoto();
//
//        if (senderMessageImage.getPhotoUrl() != null) {
//            Glide.with(context).load(senderMessageImage.getPhotoUrl()).into(iview);
//        } else {
//            iview.setImageDrawable(ContextCompat.getDrawable(context,
//                    R.drawable.ic_action_user));
//        }
//
//    }

    private void configureRecipientView(ViewHolderRecipientText viewHolderRecipientText, int position) {
        BlogMessage recipientMessageText = mChatList.get(position);
        viewHolderRecipientText.getRecipientMessageTextView().setText(recipientMessageText.getDescription());
        CircleImageView iview = viewHolderRecipientText.getmRecipientPhoto();

        if (recipientMessageText.getPhotoURL() != null) {
            Glide.with(context).load(recipientMessageText.getPhotoURL()).into(iview);
        } else {
            iview.setImageDrawable(ContextCompat.getDrawable(context,
                    R.drawable.ic_action_user));
        }
    }

    private void configureRecipientView(ViewHolderRecipientImage viewHolderRecipientImage, int position, Context context) {
        BlogMessage recipientMessageImage= mChatList.get(position);

        if (recipientMessageImage.getImageURL() != null) {
            Glide.with(context).load(recipientMessageImage.getImageURL()).into(viewHolderRecipientImage.mRecipientMessageImageView);
        }
        CircleImageView iview = viewHolderRecipientImage.getmRecipientPhoto();

        if (recipientMessageImage.getPhotoURL() != null) {
            Glide.with(context).load(recipientMessageImage.getPhotoURL()).into(iview);
        } else {
            iview.setImageDrawable(ContextCompat.getDrawable(context,
                    R.drawable.ic_action_user));
        }

    }

    @Override
    public int getItemCount() {
        return mChatList.size();
    }


    public void refillAdapter(BlogMessage newFireChatMessage){

        /*add new message chat to list*/
        mChatList.add(newFireChatMessage);

        /*refresh view*/
        notifyItemInserted(getItemCount()-1);
    }


    public void cleanUp() {
        mChatList.clear();
    }


    /*==============ViewHolder===========*/

    /*ViewHolder for Sender*/

//    public class ViewHolderSenderText extends RecyclerView.ViewHolder {
//
//        private TextView mSenderMessageTextView;
//        private CircleImageView mSenderPhoto;
//
//        public ViewHolderSenderText(View itemView) {
//            super(itemView);
//            mSenderMessageTextView =(TextView)itemView.findViewById(R.id.txt_content);
//            mSenderPhoto = (CircleImageView) itemView.findViewById(R.id.img_user_image);
//
//        }
//
//        public TextView getSenderMessageTextView() {
//            return mSenderMessageTextView;
//        }
//
//        public CircleImageView getmSenderPhoto() {
//            return mSenderPhoto;
//        }
//
//    }
//
//    public class ViewHolderSenderImage extends RecyclerView.ViewHolder {
//        private ImageView mSenderMessageImageView;
//        private CircleImageView mSenderPhoto;
//
//        public ViewHolderSenderImage(View itemView) {
//            super(itemView);
//            mSenderMessageImageView = (ImageView) itemView.findViewById(R.id.chat_image);
//            mSenderPhoto = (CircleImageView) itemView.findViewById(R.id.img_user_image);
//        }
//
//        public ImageView getSenderMessageImageView() {
//            return mSenderMessageImageView;
//        }
//
//        public CircleImageView getmSenderPhoto() {
//            return mSenderPhoto;
//        }
//    }


    /*ViewHolder for Recipient*/
    public class ViewHolderRecipientText extends RecyclerView.ViewHolder {

        private TextView mRecipientMessageTextView;
        private CircleImageView mRecipientPhoto;

        public ViewHolderRecipientText(View itemView) {
            super(itemView);
            mRecipientMessageTextView=(TextView)itemView.findViewById(R.id.txt_content);
            mRecipientPhoto = (CircleImageView) itemView.findViewById(R.id.img_contact_image);
        }

        public TextView getRecipientMessageTextView() {
            return mRecipientMessageTextView;
        }

        public CircleImageView getmRecipientPhoto() {
            return mRecipientPhoto;
        }

    }

    public class ViewHolderRecipientImage extends RecyclerView.ViewHolder {

        private ImageView mRecipientMessageImageView;
        private CircleImageView mRecipientPhoto;

        public ViewHolderRecipientImage(View itemView) {
            super(itemView);
            mRecipientMessageImageView=(ImageView)itemView.findViewById(R.id.chat_image);
            mRecipientPhoto = (CircleImageView) itemView.findViewById(R.id.img_contact_image);
        }

        public ImageView getRecipientMessageTextView() {
            return mRecipientMessageImageView;
        }

        public CircleImageView getmRecipientPhoto() {
            return mRecipientPhoto;
        }

    }
}