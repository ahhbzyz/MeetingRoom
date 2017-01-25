package com.badoo.meetingroom.presentation.view.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.badoo.meetingroom.R;
import com.badoo.meetingroom.presentation.model.intf.PersonModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EmailAutoCompleteAdapter extends ArrayAdapter<PersonModel> {

    private List<PersonModel> items;
    private List<PersonModel> itemsAll;
    private List<PersonModel> suggestions;
    private int viewResourceId;
    private Context mContext;

    public EmailAutoCompleteAdapter(Context context, int viewResourceId, List<PersonModel> items) {
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

        PersonModel personModel = items.get(position);

        if (personModel != null) {

            // Image view
            CircleImageView mAvatarImgView = (CircleImageView) v.findViewById(R.id.img_avatar);
            String mAvatarUrl = personModel.getAvatarUrl();
            if (mAvatarUrl != null) {
                Glide.with(mContext)
                    .load(mAvatarUrl).asBitmap().centerCrop()
                    .into(mAvatarImgView);
            }

            Typeface typeface = Typeface.createFromAsset(mContext.getAssets(),"fonts/stolzl_regular.otf");

            // Name
            TextView nameTv = (TextView) v.findViewById(R.id.tv_name);
            if (personModel.getDisplayName() != null) {
                nameTv.setTypeface(typeface);
                nameTv.setText(personModel.getDisplayName());
            }

            // Email address
            TextView emailTv = (TextView) v.findViewById(R.id.tv_email_address);
            if (personModel.getEmailAddress() != null) {
                emailTv.setTypeface(typeface);
                emailTv.setText(personModel.getEmailAddress());
            }
        }
        return v;
    }

    @NonNull @Override
    public Filter getFilter() {
        return nameFilter;
    }

    private Filter nameFilter = new Filter() {

        @Override
        public String convertResultToString(Object resultValue) {
            return ((PersonModel)(resultValue)).getDisplayName();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if(constraint != null) {
                suggestions.clear();

                if (constraint.length() == 0) {
                    return new FilterResults();
                }


                for (PersonModel personModel : itemsAll) {
                    String displayName = personModel.getDisplayName();
                    String givenName = personModel.getGivenName();
                    String middleName = personModel.getMiddleName();
                    String familyName = personModel.getFamilyName();

                    if(displayName != null && displayName.toLowerCase().contains(constraint.toString().toLowerCase())){
                        suggestions.add(personModel);
                    }

                    if (constraint.length() == 2) {
                        if (givenName != null && givenName.length() >= 0 &&
                            Character.toLowerCase(constraint.charAt(0)) == Character.toLowerCase(givenName.charAt(0)) &&
                            familyName != null && familyName.length() >= 0 &&
                            Character.toLowerCase(constraint.charAt(1)) == Character.toLowerCase(familyName.charAt(0))) {
                            suggestions.add(personModel);
                        }
                    }

                    if (constraint.length() == 2) {
                        if (givenName != null && givenName.length() >= 0 &&
                            Character.toLowerCase(constraint.charAt(0)) == Character.toLowerCase(givenName.charAt(0)) &&
                            middleName!= null && middleName.length() >= 0 &&
                            Character.toLowerCase(constraint.charAt(1)) == Character.toLowerCase(middleName.charAt(0))) {
                            suggestions.add(personModel);
                        }
                    }

                    if (constraint.length() == 3) {
                        if (givenName != null && givenName.length() >= 0 &&
                            Character.toLowerCase(constraint.charAt(0)) == Character.toLowerCase(givenName.charAt(0)) &&
                            middleName != null && middleName.length() >= 0 &&
                            Character.toLowerCase(constraint.charAt(1)) == Character.toLowerCase(middleName.charAt(0)) &&
                            familyName != null && familyName.length() >= 0 &&
                            Character.toLowerCase(constraint.charAt(2)) == Character.toLowerCase(familyName.charAt(0))) {
                            suggestions.add(personModel);
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
            ArrayList<PersonModel> filteredList = (ArrayList<PersonModel>) results.values;
            clear();
            if(results.count > 0) {
                addAll(filteredList);

            }
            notifyDataSetChanged();
        }
    };

}
