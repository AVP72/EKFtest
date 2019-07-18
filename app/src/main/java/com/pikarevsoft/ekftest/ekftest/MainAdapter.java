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
import static com.pikarevsoft.ekftest.ekftest.Public.BROADCAST_UPDATE_LIST_WORKER;
import static com.pikarevsoft.ekftest.ekftest.Public.DATA_BIRTHDAY_INTENT;
import static com.pikarevsoft.ekftest.ekftest.Public.FAMILY_INTENT;
import static com.pikarevsoft.ekftest.ekftest.Public.FULL_PARENT_INTENT;
import static com.pikarevsoft.ekftest.ekftest.Public.ID_INTENT;
import static com.pikarevsoft.ekftest.ekftest.Public.NAME_INTENT;
import static com.pikarevsoft.ekftest.ekftest.Public.N_CHILD_INTENT;
import static com.pikarevsoft.ekftest.ekftest.Public.PATRONYMIC_INTENT;
import static com.pikarevsoft.ekftest.ekftest.Public.PROF_INTENT;
import static com.pikarevsoft.ekftest.ekftest.Public.db;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.Item> {

    private ArrayList<Worker> mainArray;

    MainAdapter(ArrayList<Worker> mainArray) {
        this.mainArray = mainArray;
    }

    @NonNull
    @Override
    public Item onCreateViewHolder(@NonNull ViewGroup parent, int iView) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main, parent, false);
        return new Item(view, mainArray);
    }

    @Override
    public void onBindViewHolder(@NonNull Item item, int position) {

        Worker worker = mainArray.get(position);

        item.txtFamily.setText(worker.getFamily());
        item.txtName.setText(worker.getName());
        item.txtPatronymic.setText(worker.getPatronymic());
        item.txtBirthday.setText(Public.convertDateToTxtDDMMYYYY(worker.getDateBirthday()));
        item.txtProf.setText(worker.getProf());
        String nChTxt = item.txtFamily.getContext().getString(R.string.not_children);
        int n = worker.getnChild();
        if (n > 0) nChTxt = Integer.toString(n);
        item.txtChildren.setText(nChTxt);

    }

    @Override
    public int getItemCount() {
        return mainArray.size();
    }

    static class Item extends RecyclerView.ViewHolder {

        Context context;
        TextView txtFamily, txtName, txtPatronymic, txtBirthday, txtProf, txtChildren;

        Item(@NonNull final View itemView, final ArrayList<Worker> mainArray) {
            super(itemView);

            txtFamily = itemView.findViewById(R.id.main_text_family);
            txtName = itemView.findViewById(R.id.main_text_name);
            txtPatronymic = itemView.findViewById(R.id.main_text_patronymic);
            txtBirthday = itemView.findViewById(R.id.main_text_birthday);
            txtProf = itemView.findViewById(R.id.main_text_prof);
            txtChildren = itemView.findViewById(R.id.main_text_children);

            this.context = txtFamily.getContext();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int k = getAdapterPosition();
                    openItemForEdit(k, mainArray);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    setTxtInRed();
                    PopupMenu popupMenu = new PopupMenu(context, itemView);
                    popupMenu.inflate(R.menu.popup_worker);
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
                                
                                case R.id.popup_context_edit:
                                    openItemForEdit(k, mainArray);
                                    break;
                                    
                                case R.id.popup_context_send:
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
                                    
                                case R.id.popup_context_list:
                                    Intent listIntent = new Intent(context, ListChildrenActivity.class);
                                    listIntent.putExtra(ID_INTENT, mainArray.get(k).getId());
                                    String fullName =
                                            mainArray.get(k).getFamily() + " " +
                                            mainArray.get(k).getName() + " " +
                                            mainArray.get(k).getPatronymic();
                                    listIntent.putExtra(FULL_PARENT_INTENT, fullName);
                                    context.startActivity(listIntent);
                                    break;
                                    
                                case R.id.popup_context_delete:

                                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                                    dialog.setTitle(context.getString(R.string.delete_this_line));
                                    dialog.setCancelable(false);

                                    dialog.setPositiveButton(context.getString(R.string.delete), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            deleteWorkerWithChildren(mainArray.get(k).getId());
                                            context.sendBroadcast(new Intent(BROADCAST_UPDATE_LIST_WORKER));
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

        private void deleteWorkerWithChildren(long id) {

            db.delete(Child.sTable, Child.sParent+"=?", new String[]{Long.toString(id)});
            db.delete(Worker.sTable, Worker.sId+"=?", new String[]{Long.toString(id)});
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

        private void openItemForEdit(int k, ArrayList<Worker> mainArray) {
            Intent intent = new Intent(context, EditWorkerActivity.class);
            intent.putExtra(ADAPTER_POSITION, k);
            intent.putExtra(ID_INTENT, mainArray.get(k).getId());
            intent.putExtra(FAMILY_INTENT, mainArray.get(k).getFamily());
            intent.putExtra(NAME_INTENT, mainArray.get(k).getName());
            intent.putExtra(PATRONYMIC_INTENT, mainArray.get(k).getPatronymic());
            intent.putExtra(DATA_BIRTHDAY_INTENT, mainArray.get(k).getDateBirthday());
            intent.putExtra(PROF_INTENT, mainArray.get(k).getProf());
            intent.putExtra(N_CHILD_INTENT, mainArray.get(k).getnChild());
            context.startActivity(intent);
        }

    }
}
