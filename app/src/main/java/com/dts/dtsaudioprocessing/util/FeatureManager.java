/*======================================================================*
 DTS, Inc.
 5220 Las Virgenes Road
 Calabasas, CA 91302  USA

 CONFIDENTIAL: CONTAINS CONFIDENTIAL PROPRIETARY INFORMATION OWNED BY
 DTS, INC. AND/OR ITS AFFILIATES ("DTS"), INCLUDING BUT NOT LIMITED TO
 TRADE SECRETS, KNOW-HOW, TECHNICAL AND BUSINESS INFORMATION. USE,
 DISCLOSURE OR DISTRIBUTION OF THE SOFTWARE IN ANY FORM IS LIMITED TO
 SPECIFICALLY AUTHORIZED LICENSEES OF DTS.  ANY UNAUTHORIZED
 DISCLOSURE IS A VIOLATION OF STATE, FEDERAL, AND INTERNATIONAL LAWS.
 BOTH CIVIL AND CRIMINAL PENALTIES APPLY.

 DO NOT DUPLICATE. COPYRIGHT 2015, DTS, INC. ALL RIGHTS RESERVED.
 UNAUTHORIZED DUPLICATION IS A VIOLATION OF STATE, FEDERAL AND
 INTERNATIONAL LAWS.

 ALGORITHMS, DATA STRUCTURES AND METHODS CONTAINED IN THIS SOFTWARE
 MAY BE PROTECTED BY ONE OR MORE PATENTS OR PATENT APPLICATIONS.
 UNLESS OTHERWISE PROVIDED UNDER THE TERMS OF A FULLY-EXECUTED WRITTEN
 AGREEMENT BY AND BETWEEN THE RECIPIENT HEREOF AND DTS, THE FOLLOWING
 TERMS SHALL APPLY TO ANY USE OF THE SOFTWARE (THE "PRODUCT") AND, AS
 APPLICABLE, ANY RELATED DOCUMENTATION:  (i) ANY USE OF THE PRODUCT
 AND ANY RELATED DOCUMENTATION IS AT THE RECIPIENT'S SOLE RISK:
 (ii) THE PRODUCT AND ANY RELATED DOCUMENTATION ARE PROVIDED "AS IS"
 AND WITHOUT WARRANTY OF ANY KIND AND DTS EXPRESSLY DISCLAIMS ALL
 WARRANTIES, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO ANY
 IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 PURPOSE, REGARDLESS OF WHETHER DTS KNOWS OR HAS REASON TO KNOW OF THE
 USER'S PARTICULAR NEEDS; (iii) DTS DOES NOT WARRANT THAT THE PRODUCT
 OR ANY RELATED DOCUMENTATION WILL MEET USER'S REQUIREMENTS, OR THAT
 DEFECTS IN THE PRODUCT OR ANY RELATED DOCUMENTATION WILL BE
 CORRECTED; (iv) DTS DOES NOT WARRANT THAT THE OPERATION OF ANY
 HARDWARE OR SOFTWARE ASSOCIATED WITH THIS DOCUMENT WILL BE
 UNINTERRUPTED OR ERROR-FREE; AND (v) UNDER NO CIRCUMSTANCES,
 INCLUDING NEGLIGENCE, SHALL DTS OR THE DIRECTORS, OFFICERS, EMPLOYEES,
 OR AGENTS OF DTS, BE LIABLE TO USER FOR ANY INCIDENTAL, INDIRECT,
 SPECIAL, OR CONSEQUENTIAL DAMAGES (INCLUDING BUT NOT LIMITED TO
 DAMAGES FOR LOSS OF BUSINESS PROFITS, BUSINESS INTERRUPTION, AND LOSS
 OF BUSINESS INFORMATION) ARISING OUT OF THE USE, MISUSE, OR INABILITY
 TO USE THE PRODUCT OR ANY RELATED DOCUMENTATION.
*======================================================================*/

package com.dts.dtsaudioprocessing.util;

import com.dts.dtssdk.DtsFeature;
import com.dts.dtssdk.DtsFeatureChecker;
import com.dts.dtssdk.util.AudioRoute;

import java.util.Map;

/**
 * Helper class to manage features
 */
public class FeatureManager {
    private static Map<String,String> sCustomerConfig;
    private static final String HIDE_STEREO_PREF = "ui_hide_stereo_preference";
    private static final String HIDE_GEQ_SCREEN = "ui_hide_geq_5";
    private static final String HIDE_DTS = "ui_hide_enable_button";
    private static final String TRUE = "true";
    private static final String FALSE = "false";

    static {
        sCustomerConfig = DtsFeatureChecker.getInstance().getCustomerConfig();
    }

    public static boolean hasDts() {
        return !TRUE.equalsIgnoreCase(sCustomerConfig.get(HIDE_DTS));
    }

    public static boolean hasLineoutHeadphones() {
        return DtsFeatureChecker.getInstance().hasRoute(AudioRoute.LINE_OUT);
    }

    public static boolean hasBluetoothHeadphones() {
        return DtsFeatureChecker.getInstance().hasRoute(AudioRoute.BLUETOOTH);
    }

    public static boolean hasUsbHeadphones() {
        return DtsFeatureChecker.getInstance().hasRoute(AudioRoute.USB);
    }

    public static boolean hasSpeakerMode() {
        return DtsFeatureChecker.getInstance().hasRoute(AudioRoute.INTERNAL_SPEAKERS);
    }

    // DTS Sound uses a 5-band GEQ. As such, check if SDK supports 5-band GEQ
    public static boolean hasGEQ() {
        //check if GEQ license is enabled in SDK and GEQ UI is enabled in customer configuration
        boolean enableGeq = DtsFeatureChecker.getInstance().hasFeature(DtsFeature.GEQ_5) && !TRUE.equalsIgnoreCase(sCustomerConfig.get(HIDE_GEQ_SCREEN));
        return enableGeq;
    }

    public static boolean hasStereoPreference() {
        return !TRUE.equalsIgnoreCase(sCustomerConfig.get(HIDE_STEREO_PREF));
    }

    public static boolean hasTrebleBoost() {
        return DtsFeatureChecker.getInstance().hasFeature(DtsFeature.TREBLE_LEVEL);
    }

    public static boolean hasBassBoost() {
        return DtsFeatureChecker.getInstance().hasFeature(DtsFeature.BASS_LEVEL);
    }

    public static boolean hasDialogEnhancement() {
        return DtsFeatureChecker.getInstance().hasFeature(DtsFeature.DIALOG_LEVEL);
    }
}
