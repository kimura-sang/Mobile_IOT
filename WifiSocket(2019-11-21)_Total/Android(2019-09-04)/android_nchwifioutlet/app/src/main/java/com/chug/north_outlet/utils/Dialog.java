package com.chug.north_outlet.utils;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

public class Dialog {

    public static void alertThreeButtons(final Context context, String title, String content) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(content)
                .setPositiveButton("No",
                        new DialogInterface.OnClickListener() {
                            @TargetApi(11)
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(context, "Right button is clicked", Toast.LENGTH_SHORT).show();

                                dialog.cancel();
                            }
                        })
                .setNeutralButton("Unsure",
                        new DialogInterface.OnClickListener() {
                            @TargetApi(11)
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(context, "Center button is clicked", Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @TargetApi(11)
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(context, "Left button is clicked", Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                        }).show();
    }
}
