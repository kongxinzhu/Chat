package com.marceme.marcefirebasechat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marceme.marcefirebasechat.R;
import com.marceme.marcefirebasechat.model.ChatMessage;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessageChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ChatMessage> mChatList;
    public static final int SENDER = 0;
    public static final int RECIPIENT = 1;
    private Context mContext;

    public MessageChatAdapter(Context context, List<ChatMessage> listOfFireChats) {
        mChatList = listOfFireChats;
        mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (mChatList.get(position).getRecipientOrSenderStatus() == SENDER) {
            return SENDER;
        } else {
            return RECIPIENT;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case SENDER:
                View viewSender = inflater.inflate(R.layout.layout_sender_message, viewGroup, false);
                viewHolder = new ViewHolderSender(viewSender);
                break;
            case RECIPIENT:
                View viewRecipient = inflater.inflate(R.layout.layout_recipient_message, viewGroup, false);
                viewHolder = new ViewHolderRecipient(viewRecipient);
                break;
            default:
                View viewSenderDefault = inflater.inflate(R.layout.layout_sender_message, viewGroup, false);
                viewHolder = new ViewHolderSender(viewSenderDefault);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        switch (viewHolder.getItemViewType()) {
            case SENDER:
                ViewHolderSender viewHolderSender = (ViewHolderSender) viewHolder;
                configureSenderView(viewHolderSender, position);
                break;
            case RECIPIENT:
                ViewHolderRecipient viewHolderRecipient = (ViewHolderRecipient) viewHolder;
                configureRecipientView(viewHolderRecipient, position);
                break;
        }
    }


    private void configureSenderView(final ViewHolderSender viewHolderSender, int position) {
        final ChatMessage senderFireMessage = mChatList.get(position);

        // set text
        viewHolderSender.getSenderMessageTextView().setText(senderFireMessage.getMessage());


        // set image in message
        String imageURL = senderFireMessage.getImageURL();
        if(imageURL != null && !imageURL.isEmpty()) {
            Picasso.with(mContext).load(senderFireMessage.getImageURL()).into(viewHolderSender.getSenderMessageImageView());
//            Log.e("imageURL sender", "is not empty");
//            viewHolderSender.getSenderMessageImageView().getViewTreeObserver()
//                    .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                                                   @Override
//                                                   public void onGlobalLayout() {
//                                                       viewHolderSender.getSenderMessageImageView().getViewTreeObserver()
//                                                               .removeOnGlobalLayoutListener(this);
//
//                                                       Picasso.with(mContext)
//                                                               .load(senderFireMessage.getImageURL())
//                                                               .resize(viewHolderSender.getSenderMessageImageView().getWidth(), 0)
//                                                               .into(viewHolderSender.getSenderMessageImageView());
//                                                   }
//                                               }
//                    );
        }

//        Picasso.with(mContext).load(senderFireMessage.getImageURL()).resize(0, viewHolderSender.getSenderMessageImageView().getHeight()).into(viewHolderSender.getSenderMessageImageView());
//        Log.e("image message URL", senderFireMessage.getImageURL());


        // set user photo
        DatabaseReference mUserRefDatabase = FirebaseDatabase.getInstance()
                .getReference().child("users").child(senderFireMessage.getSender());
        DatabaseReference mUserPhotoReference = mUserRefDatabase.child("photoURL");

        mUserPhotoReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String url = dataSnapshot.getValue(String.class);
                Picasso.with(mContext).load(url).into(viewHolderSender.getmSenderImageImageView());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void configureRecipientView(final ViewHolderRecipient viewHolderRecipient, int position) {
        final ChatMessage recipientFireMessage = mChatList.get(position);
        viewHolderRecipient.getRecipientMessageTextView().setText(recipientFireMessage.getMessage());

        // set image in message
        String imageURL = recipientFireMessage.getImageURL();
        if(imageURL != null && !imageURL.isEmpty()) {
            Log.e("imageURL recipient", "is not empty");
            viewHolderRecipient.getmRecipientImageImageView().getViewTreeObserver()
                    .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            viewHolderRecipient.getmRecipientMessageImageView().getViewTreeObserver()
                                    .removeOnGlobalLayoutListener(this);

                            Picasso.with(mContext)
                                    .load(recipientFireMessage.getImageURL())
                                    .resize(viewHolderRecipient.getmRecipientMessageImageView().getWidth(), 0)
                                    .into(viewHolderRecipient.getmRecipientMessageImageView());
                        }
                    });
        }

        // set recipient photo
        DatabaseReference mUserRefDatabase = FirebaseDatabase.getInstance()
                .getReference().child("users").child(recipientFireMessage.getSender());
        DatabaseReference mUserPhotoReference = mUserRefDatabase.child("photoURL");

        mUserPhotoReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String url = dataSnapshot.getValue(String.class);
                Picasso.with(mContext).load(url).into(viewHolderRecipient.getmRecipientImageImageView());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return mChatList.size();
    }


    public void refillAdapter(ChatMessage newFireChatMessage) {

        /*add new message chat to list*/
        mChatList.add(newFireChatMessage);

        /*refresh view*/
        notifyItemInserted(getItemCount() - 1);
    }


    public void cleanUp() {
        mChatList.clear();
    }


    /*==============ViewHolder===========*/

    /*ViewHolder for Sender*/

    public class ViewHolderSender extends RecyclerView.ViewHolder {

        private TextView mSenderMessageTextView;
        private ImageView mSenderMessageImageView;
        private ImageView mSenderImageImageView;

        public ViewHolderSender(View itemView) {
            super(itemView);
            mSenderMessageTextView = (TextView) itemView.findViewById(R.id.text_view_sender_message);
            mSenderMessageImageView = (ImageView) itemView.findViewById(R.id.image_view_sender_message_photo);
            mSenderImageImageView = (ImageView) itemView.findViewById(R.id.img_sender_photo);
        }

        public TextView getSenderMessageTextView() {
            return mSenderMessageTextView;
        }


        public ImageView getSenderMessageImageView() {
            return mSenderMessageImageView;
        }

        public ImageView getmSenderImageImageView() {
            return mSenderImageImageView;
        }
    }


    /*ViewHolder for Recipient*/
    public class ViewHolderRecipient extends RecyclerView.ViewHolder {

        private TextView mRecipientMessageTextView;
        private ImageView mRecipientMessageImageView;
        private ImageView mRecipientImageImageView;


        public ViewHolderRecipient(View itemView) {
            super(itemView);
            mRecipientMessageTextView = (TextView) itemView.findViewById(R.id.text_view_recipient_message);
            mRecipientMessageImageView = (ImageView) itemView.findViewById(R.id.image_view_recipient_message_photo);
            mRecipientImageImageView = (ImageView) itemView.findViewById(R.id.img_recipient_photo);
        }

        public ImageView getmRecipientImageImageView() {
            return mRecipientImageImageView;
        }

        public ImageView getmRecipientMessageImageView() {
            return mRecipientMessageImageView;
        }

        public TextView getRecipientMessageTextView() {
            return mRecipientMessageTextView;
        }

    }
}