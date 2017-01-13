package com.badoo.meetingroom.presentation.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.badoo.meetingroom.R;
import com.badoo.meetingroom.presentation.model.BadooPersonModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class BadooEmailAutoCompleteAdapter extends ArrayAdapter<BadooPersonModel> {

    private final String MY_DEBUG_TAG = "BadooEmailAutoCompleteAdapter";

    private List<BadooPersonModel> items;
    private List<BadooPersonModel> itemsAll;
    private List<BadooPersonModel> suggestions;
    private int viewResourceId;
    private Context mContext;

    public BadooEmailAutoCompleteAdapter(Context context, int viewResourceId, List<BadooPersonModel> items) {
        super(context, viewResourceId, items);
        this.mContext = context;
        this.viewResourceId = viewResourceId;
        this.items = items;
        this.itemsAll = new ArrayList<>();
        this.itemsAll.addAll(items);
        this.suggestions = new ArrayList<>();

    }

    @NonNull @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = layoutInflater.inflate(viewResourceId, null);
        }

        BadooPersonModel badooPersonModel = items.get(position);

        if (badooPersonModel != null) {

            ImageView avatarImgView = (ImageView)v.findViewById(R.id.img_avatar);

            if (badooPersonModel.getAvatarUrl() != null) {
                int width = (int) mContext.getResources().getDimension(R.dimen.auto_complete_avatar_width);
                int height = (int) mContext.getResources().getDimension(R.dimen.auto_complete_avatar_height);
                Glide
                    .with(mContext)
                    .load(badooPersonModel.getAvatarUrl())
                    .centerCrop()
                    .override(width, height)
                    .into(avatarImgView);
            }


            TextView nameTv = (TextView) v.findViewById(R.id.tv_name);
            if (badooPersonModel.getDisplayName() != null) {
                nameTv.setText(badooPersonModel.getDisplayName());
            }

            TextView emailTv = (TextView) v.findViewById(R.id.tv_email_address);
            if (badooPersonModel.getEmailAddress() != null) {
                emailTv.setText(badooPersonModel.getEmailAddress());
            }
        }
        return v;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    private Filter nameFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            return ((BadooPersonModel)(resultValue)).getEmailAddress();
        }
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if(constraint != null) {
                suggestions.clear();
                constraint = constraint.toString().split("@")[0];
                if (constraint.length() == 0) {
                    return new FilterResults();
                }
                for (BadooPersonModel badooPersonModel : itemsAll) {
                    if(badooPersonModel.getDisplayName() != null && badooPersonModel.getDisplayName().toLowerCase().contains(constraint.toString().toLowerCase())){
                        suggestions.add(badooPersonModel);
                    }

                    if (constraint.length() == 2) {
                        if (badooPersonModel.getGivenName() != null &&
                            badooPersonModel.getGivenName().length() >= 0 &&
                            Character.toLowerCase(constraint.charAt(0)) == Character.toLowerCase(badooPersonModel.getGivenName().charAt(0)) &&
                            badooPersonModel.getFamilyName() != null &&
                            badooPersonModel.getFamilyName().length() >= 0 &&
                            Character.toLowerCase(constraint.charAt(1)) == Character.toLowerCase(badooPersonModel.getFamilyName().charAt(0))) {
                            suggestions.add(badooPersonModel);
                        }
                    }

                    if (constraint.length() == 2) {
                        if (badooPersonModel.getGivenName() != null &&
                            badooPersonModel.getGivenName().length() >= 0 &&
                            Character.toLowerCase(constraint.charAt(0)) == Character.toLowerCase(badooPersonModel.getGivenName().charAt(0)) &&
                            badooPersonModel.getMiddleName() != null &&
                            badooPersonModel.getMiddleName().length() >= 0 &&
                            Character.toLowerCase(constraint.charAt(1)) == Character.toLowerCase(badooPersonModel.getMiddleName().charAt(0))) {
                            suggestions.add(badooPersonModel);
                        }
                    }

                    if (constraint.length() == 3) {
                        if (badooPersonModel.getGivenName() != null &&
                            badooPersonModel.getGivenName().length() >= 0 &&
                            Character.toLowerCase(constraint.charAt(0)) == Character.toLowerCase(badooPersonModel.getGivenName().charAt(0)) &&
                            badooPersonModel.getMiddleName() != null &&
                            badooPersonModel.getMiddleName().length() >= 0 &&
                            Character.toLowerCase(constraint.charAt(1)) == Character.toLowerCase(badooPersonModel.getMiddleName().charAt(0)) &&
                            badooPersonModel.getFamilyName() != null &&
                            badooPersonModel.getFamilyName().length() >= 0 && Character.toLowerCase(constraint.charAt(2)) == Character.toLowerCase(badooPersonModel.getFamilyName().charAt(0))) {
                            suggestions.add(badooPersonModel);
                        }
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }
        @Override @SuppressWarnings("unchecked")
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<BadooPersonModel> filteredList = (ArrayList<BadooPersonModel>) results.values;
            if(results.count > 0) {
                clear();
                for (BadooPersonModel badooPersonModel : filteredList) {
                    add(badooPersonModel);
                }
                notifyDataSetChanged();
            }
        }
    };

}
