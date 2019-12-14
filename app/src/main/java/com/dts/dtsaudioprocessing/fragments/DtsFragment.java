/* DTS,INC.
 * 5220 LAS VIRGENES ROAD
 * CALABASAS, CA 91302  USA
 *
 * ©DTS, INC.  ALL RIGHTS RESERVED.
 *
 * THIS SOFTWARE, ANY COMPONENTS THEREOF, AND ANY RELATED DOCUMENTATION (THE “PRODUCT”)
 * CONTAINS CONFIDENTIAL PROPRIETARY INFORMATION OWNED BY DTS, INC. AND/OR ITS
 * AFFILIATES (“DTS”) INCLUDING BUT NOT LIMITED TO TRADE SECRETS, KNOW-HOW,
 * TECHNICAL, AND BUSINESS INFORMATION.  UNLESS OTHERWISE PROVIDED UNDER THE
 * TERMS OF A FULLY-EXECUTED WRITTEN AGREEMENT BY AND BETWEEN THE RECIPIENT
 * HEREOF AND DTS, ALL USE, DUPLICATION, DISCLOSURE, OR DISTRIBUTION OF THE
 * PRODUCT, IN ANY FORM, IS PROHIBITED AND IS A VIOLATION OF STATE, FEDERAL, AND
 * INTERNATIONAL LAWS. THE PRODUCT CONTAINS CONFIDENTIAL, PROPRIETARY TRADE SECRETS,
 * AND IS PROTECTED BY APPLICABLE COPYRIGHT LAW AND/OR PATENT LAW. BOTH CIVIL AND
 * CRIMINAL PENALTIES APPLY.
 *
 * ALGORITHMS, DATA STRUCTURES AND METHODS CONTAINED IN THE PRODUCT MAY BE
 * PROTECTED BY ONE OR MORE PATENTS OR PATENT APPLICATIONS. UNLESS OTHERWISE
 * PROVIDED UNDER THE TERMS OF A FULLY-EXECUTED WRITTEN AGREEMENT BY AND BETWEEN
 * THE RECIPIENT HEREOF AND DTS, THE FOLLOWING TERMS SHALL APPLY TO ANY USE OF
 * THE PRODUCT:  (I) USE OF THE PRODUCT IS AT THE RECIPIENT'S SOLE RISK; (II) THE
 * PRODUCT IS PROVIDED "AS IS" AND WITHOUT WARRANTY OF ANY KIND AND DTS EXPRESSLY
 * DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * ANY IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE,
 * REGARDLESS OF WHETHER DTS KNOWS OR HAS REASON TO KNOW OF THE USER'S PARTICULAR
 * NEEDS; (III) DTS DOES NOT WARRANT THAT THE PRODUCT MEET USER'S REQUIREMENTS,
 * OR THAT ANY ALLEGED DEFECTS IN THE PRODUCT WILL BE CORRECTED; (IV) DTS DOES
 * NOT WARRANT THAT THE OPERATION OF ANY HARDWARE OR SOFTWARE ASSOCIATED WITH THE
 * PRODUCT WILL BE UNINTERRUPTED OR ERROR-FREE; AND (V) UNDER NO CIRCUMSTANCES,
 * INCLUDING NEGLIGENCE, SHALL DTS OR THE DIRECTORS, OFFICERS, EMPLOYEES, OR
 * AGENTS OF DTS, BE LIABLE TO USER FOR ANY INCIDENTAL, INDIRECT, SPECIAL, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING BUT NOT LIMITED TO DAMAGES FOR LOSS OF BUSINESS
 * PROFITS, BUSINESS INTERRUPTION, AND LOSS OF BUSINESS INFORMATION) ARISING OUT
 * OF THE USE, MISUSE, OR INABILITY TO USE THE PRODUCT.
 */

package com.dts.dtsaudioprocessing.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.dts.dtssdk.result.DtsResult;
import com.dts.dtsaudioprocessing.interfaces.FragmentStateUpdate;
import com.dts.dtsaudioprocessing.R;
import com.dts.dtsaudioprocessing.listener.DialogListener;

/**
 * A fragment that contains convenient functions that can be called by its child fragments
 */
public class DtsFragment extends Fragment implements FragmentStateUpdate{

    private static final String TAG = DtsFragment.class.getSimpleName();

    public DtsFragment() {}

    /**
     * Convenient function to show confirmation dialog
     *
     * @param message The message to be displayed in the Dialog
     * @param confirmationText the text for confirmation button
     * @param listener DialogListener to be executed when user presses confirm
     */
    protected void showConfirmationDialog(String message, String confirmationText, final DialogListener listener) {

        try {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);

            // Set title and message
            dialogBuilder
                    .setMessage(message)
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setPositiveButton(confirmationText, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            listener.onDialogConfirm();
                        }
                    });

            // Create dialog and show
            dialogBuilder.create().show();
        } catch (Exception e) {
            // Showing this dialog may fail (throws Exception) if this function is called after the
            // activity has already been destroyed.
            // See: http://stackoverflow.com/questions/5934050/check-whether-activity-is-active/8963867#8963867
            // http://stackoverflow.com/questions/3256152/how-to-check-whether-an-activity-is-running-or-not?lq=1
            // As such, to prevent the app from crashing, try displaying the dialog. If that fails,
            // then don't display anything
            Log.d(TAG, "Attempted to display confirmation dialog, but activity may be destroyed already.");
            Log.d(TAG, "Dialog message: " + message);
        }
    }

    /**
     * Convenient function to show that a certain action has been completed.
     *
     * @param message The message to be displayed in the Dialog
     * @param listener DialogListener to be executed when user presses OK
     */
    protected void showCompletedDialog(String message, final DialogListener listener) {
        try {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);

            // Set title and message
            dialogBuilder
                    .setMessage(message)
                    .setCancelable(false)
                    .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            listener.onDialogConfirm();
                        }
                    });

            // Create dialog and show
            dialogBuilder.create().show();
        } catch (Exception e) {
            // Showing this dialog may fail (throws Exception) if this function is called after the
            // activity has already been destroyed.
            // See: http://stackoverflow.com/questions/5934050/check-whether-activity-is-active/8963867#8963867
            // http://stackoverflow.com/questions/3256152/how-to-check-whether-an-activity-is-running-or-not?lq=1
            // As such, to prevent the app from crashing, try displaying the dialog. If that fails,
            // then don't display anything
            Log.d(TAG, "Attempted to display confirmation dialog, but activity may be destroyed already.");
            Log.d(TAG, "Dialog message: " + message);
        }
    }

    /**
     * Convenient function to show Alert Dialog with the message
     *
     * @param title to be shown in the Alert Dialog
     * @param message to be shown in the Alert Dialog
     */
    protected void showAlertDialog(String title, String message) {
        try {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);

            // Set title and message
            dialogBuilder
                    .setTitle(title)
                    .setMessage(message)
                    .setCancelable(false)
                    .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            // Create dialog and show
            dialogBuilder.create().show();
        } catch (Exception e) {
            // Showing this dialog may fail (throws Exception) if this function is called after the
            // activity has already been destroyed.
            // See: http://stackoverflow.com/questions/5934050/check-whether-activity-is-active/8963867#8963867
            // http://stackoverflow.com/questions/3256152/how-to-check-whether-an-activity-is-running-or-not?lq=1
            // As such, to prevent the app from crashing, try displaying the dialog. If that fails,
            // then don't display anything
            Log.d(TAG, "Attempted to display dialog, but activity may be destroyed already.");
            Log.d(TAG, "Dialog title: " + title);
            Log.d(TAG, "Dialog message: " + message);
        }
    }

    /**
     * Convenient function to show Alert Dialog (for errors) with the DtsResult as message
     *
     * @param result to be shown in the Alert Dialog
     */
    protected void showErrorDialog(DtsResult result) {
        showAlertDialog("DTS Error", "DTS returned error code: " + result.getResultCode()
                + ". " + result.getResultMessage());
    }

    protected void showErrorDialog(String action, DtsResult result) {
        showAlertDialog("DTS Error", action + " returned error code: " + result.getResultCode()
                + ". " + result.getResultMessage());
    }

    public void updateState(){};
}
