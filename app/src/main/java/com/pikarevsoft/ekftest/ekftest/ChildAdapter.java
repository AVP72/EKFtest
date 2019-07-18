package com.pikarevsoft.ekftest.ekftest;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.pikarevsoft.ekftest.ekftest.Public.ADAPTER_POSITION;
import static com.pikarevsoft.ekftest.ekftest.Public.BROADCAST_UPDATE_LIST_CHILDREN;
import static com.pikarevsoft.ekftest.ekftest.Public.DATA_BIRTHDAY_INTENT;
import static com.pikarevsoft.ekftest.ekftest.Public.FAMILY_INTENT;
import static com.pikarevsoft.ekftest.ekftest.Public.ID_INTENT;
import static com.pikarevsoft.ekftest.ekftest.Public.NAME_INTENT;
import static com.pikarevsoft.ekftest.ekftest.Public.PATRONYMIC_INTENT;
import static com.pikarevsoft.ekftest.ekftest.Public.db;

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.Item> {

    private ArrayList<Child> childArrayList;

    ChildAdapter(ArrayList<Child> childArrayList) {
        this.childArrayList = childArrayList;
    }

    @NonNull
    @Override
    public ChildAdapter.Item onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_child, parent, false);
        return new Item(view, childArrayList);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildAdapter.Item item, int position) {

        Child child = childArrayList.get(position);
        item.txtFamily.setText(child.getFamily());
        item.txtName.setText(child.getName());
        item.txtPatronymic.setText(child.getPatronymic());
        item.txtBirthday.setText(Public.convertDateToTxtDDMMYYYY(child.getDateBirthday()));

    }

    @Override
    public int getItemCount() {
        return childArrayList.size();
    }

    static class Item extends RecyclerView.ViewHolder {

        Context context;
        TextView txtFamily, txtName, txtPatronymic, txtBirthday;

        Item(@NonNull final View itemView, final ArrayList<Child> childArrayList) {
            super(itemView);

            txtFamily = itemView.findViewById(R.id.child_txt_family);
            txtName = itemView.findViewById(R.id.child_txt_name);
            txtPatronymic = itemView.findViewById(R.id.child_txt_patronymic);
            txtBirthday = itemView.findViewById(R.id.child_txt_date_birthday);

            this.context = txtFamily.getContext();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int k = getAdapterPosition();
                    Intent intent = new Intent(context, EditChildActivity.class);
                    intent.putExtra(ADAPTER_POSITION, childArrayList.get(k).getId());
                    intent.putExtra(ID_INTENT, childArrayList.get(k).getParent());
                    intent.putExtra(FAMILY_INTENT, childArrayList.get(k).getFamily());
                    intent.putExtra(NAME_INTENT, childArrayList.get(k).getName());
                    intent.putExtra(PATRONYMIC_INTENT, childArrayList.get(k).getPatronymic());
                    intent.putExtra(DATA_BIRTHDAY_INTENT, childArrayList.get(k).getDateBirthday());
                    context.startActivity(intent);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    setTxtInRed();
                    PopupMenu popupMenu = new PopupMenu(context, itemView);
                    popupMenu.inflate(R.menu.popup_children);
                    popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
                        @Override
                        public void onDismiss(PopupMenu menu) {
                            setTxtInBlack();
                        }
                    });

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            setTxtColor(R.color.black);
                            final int k = getAdapterPosition();

                            switch (item.getItemId()){

                                case R.id.popup_children_edit:
                                    openItemForEdit(k, childArrayList);
                                    break;

                                case R.id.popup_children_send:
                                    Intent sendTaskIntent = new Intent();
                                    sendTaskIntent.setAction(Intent.ACTION_SEND);
                                    String sendTaskString =
                                            txtFamily.getText().toString() + " " +
                                                    txtName.getText().toString() + " " +
                                                    txtPatronymic.getText().toString();

                                    sendTaskIntent.putExtra(Intent.EXTRA_TEXT, sendTaskString);
                                    sendTaskIntent.setType("text/plain");
                                    context.startActivity(sendTaskIntent);
                                    break;

                                case R.id.popup_children_delete:

                                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                                    dialog.setTitle(context.getString(R.string.delete_this_line));
                                    dialog.setCancelable(false);

                                    dialog.setPositiveButton(context.getString(R.string.delete), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            deleteChildren(childArrayList.get(k).getId());
                                            context.sendBroadcast(new Intent(BROADCAST_UPDATE_LIST_CHILDREN));
                                        }});
                                    dialog.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // ничего не делать, просто закрыть диалог
                                        }
                                    });
                                    dialog.show();

                                    break;

                                default:
                                    Toast.makeText(context, context.getString(R.string.choose_not), Toast.LENGTH_LONG).show();
                            }

                            return true;
                        }
                    });

                    popupMenu.show();
                    return true;
                }
            });
        }

        private void deleteChildren(long id) {
            db.delete(Child.sTable, Child.sId+"=?", new String[]{Long.toString(id)});
        }

        private void setTxtInRed() {
            setTxtColor(R.color.red);
        }
        private void setTxtInBlack() {
            setTxtColor(R.color.black);
        }
        private void setTxtColor(int color){
            txtFamily.setTextColor(context.getResources().getColor(color));
            txtName.setTextColor(context.getResources().getColor(color));
            txtPatronymic.setTextColor(context.getResources().getColor(color));
        }

        private void openItemForEdit(int k, ArrayList<Child> Array) {
            Intent intent = new Intent(context, EditChildActivity.class);
            intent.putExtra(ADAPTER_POSITION, Array.get(k).getId());
            intent.putExtra(ID_INTENT, Array.get(k).getParent());
            intent.putExtra(FAMILY_INTENT, Array.get(k).getFamily());
            intent.putExtra(NAME_INTENT, Array.get(k).getName());
            intent.putExtra(PATRONYMIC_INTENT, Array.get(k).getPatronymic());
            intent.putExtra(DATA_BIRTHDAY_INTENT, Array.get(k).getDateBirthday());
            context.startActivity(intent);
        }

    }
}
