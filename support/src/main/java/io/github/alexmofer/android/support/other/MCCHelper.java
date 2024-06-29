package io.github.alexmofer.android.support.other;

import android.content.Context;

import androidx.annotation.Nullable;

import io.github.alexmofer.android.support.R;

/**
 * MCC辅助
 * Created by Alex on 2023/6/29.
 */
public class MCCHelper {
    public static final int MCC_TEST = 1;// Test network
    public static final int MCC_INTERNAL = 999;// Internal use
    public static final int MCC_GR = 202;// Greece
    public static final int MCC_NL = 204;// Netherlands (Kingdom of the Netherlands)
    public static final int MCC_BE = 206;// Belgium
    public static final int MCC_FR_208 = 208;// France
    public static final int MCC_MC = 212;// Monaco
    public static final int MCC_AD = 213;// Andorra
    public static final int MCC_ES = 214;// Spain
    public static final int MCC_HU = 216;// Hungary
    public static final int MCC_BA = 218;// Bosnia and Herzegovina
    public static final int MCC_HR = 219;// Croatia
    public static final int MCC_RS = 220;// Serbia
    public static final int MCC_XK = 221;// Kosovo
    public static final int MCC_IT = 222;// Italy
    public static final int MCC_RO = 226;// Romania
    public static final int MCC_CH = 228;// Switzerland
    public static final int MCC_CZ = 230;// Czech Republic
    public static final int MCC_SK = 231;// Slovakia
    public static final int MCC_AT = 232;// Austria
    public static final int MCC_GB_234 = 234;// United Kingdom, Guernsey (United Kingdom), Isle of Man (United Kingdom), Jersey (United Kingdom)
    public static final int MCC_GB_235 = 235;// United Kingdom
    public static final int MCC_DK = 238;// Denmark (Kingdom of Denmark)
    public static final int MCC_SE = 240;// Sweden
    public static final int MCC_NO = 242;// Norway
    public static final int MCC_FI = 244;// Finland
    public static final int MCC_LT = 246;// Lithuania
    public static final int MCC_LV = 247;// Latvia
    public static final int MCC_EE = 248;// Estonia
    public static final int MCC_RU = 250;// Russian Federation
    public static final int MCC_UA = 255;// Ukraine
    public static final int MCC_BY = 257;// Belarus
    public static final int MCC_MD = 259;// Moldova
    public static final int MCC_PL = 260;// Poland
    public static final int MCC_DE = 262;// Germany
    public static final int MCC_GI = 266;// Gibraltar (United Kingdom)
    public static final int MCC_PT = 268;// Portugal
    public static final int MCC_LU = 270;// Luxembourg
    public static final int MCC_IE = 272;// Ireland
    public static final int MCC_IS = 274;// Iceland
    public static final int MCC_AL = 276;// Albania
    public static final int MCC_MT = 278;// Malta
    public static final int MCC_CY = 280;// Cyprus
    public static final int MCC_GE = 282;// Georgia
    public static final int MCC_AM = 283;// Armenia
    public static final int MCC_BG = 284;// Bulgaria
    public static final int MCC_TR = 286;// Turkey
    public static final int MCC_FO = 288;// Faroe Islands (Kingdom of Denmark)
    public static final int MCC_AB = 289;// Abkhazia
    public static final int MCC_GL = 290;// Greenland (Kingdom of Denmark)
    public static final int MCC_SM = 292;// San Marino
    public static final int MCC_SI = 293;// Slovenia
    public static final int MCC_MK = 294;// North Macedonia
    public static final int MCC_LI = 295;// Liechtenstein
    public static final int MCC_ME = 297;// Montenegro
    public static final int MCC_CA = 302;// Canada
    public static final int MCC_PM = 308;// Saint Pierre and Miquelon
    public static final int MCC_US_310 = 310;// United States of America, Guam (United States of America), Northern Mariana Islands (United States of America)
    public static final int MCC_US_311 = 311;// United States of America, Guam (United States of America)
    public static final int MCC_US_312 = 312;// United States of America
    public static final int MCC_US_313 = 313;// United States of America
    public static final int MCC_US_314 = 314;// United States of America
    public static final int MCC_US_315 = 315;// United States of America
    public static final int MCC_US_316 = 316;// United States of America
    public static final int MCC_PR = 330;// Puerto Rico (United States of America)
    public static final int MCC_VI = 332;// United States Virgin Islands
    public static final int MCC_MX = 334;// Mexico
    public static final int MCC_JM = 338;// Jamaica
    public static final int MCC_FR_340 = 340;// Guadeloupe (France), Martinique (France), Saint Barthélemy (France), Saint Martin (France)
    public static final int MCC_BB = 342;// Barbados
    public static final int MCC_AG = 344;// Antigua and Barbuda
    public static final int MCC_KY = 346;// Cayman Islands (United Kingdom)
    public static final int MCC_VG = 348;// British Virgin Islands (United Kingdom)
    public static final int MCC_BM = 350;// Bermuda
    public static final int MCC_GD = 352;// Grenada
    public static final int MCC_MS = 354;// Montserrat (United Kingdom)
    public static final int MCC_KN = 356;// Saint Kitts and Nevis
    public static final int MCC_LC = 358;// Saint Lucia
    public static final int MCC_VC = 360;// Saint Vincent and the Grenadines
    public static final int MCC_BQ = 362;// Bonaire, Sint Eustatius, Saba, Curaçao, Sint Maarten
    public static final int MCC_AW = 363;// Aruba
    public static final int MCC_BS = 364;// Bahamas
    public static final int MCC_AI = 365;// Anguilla (United Kingdom)
    public static final int MCC_DM = 366;// Dominica
    public static final int MCC_CU = 368;// Cuba
    public static final int MCC_DO = 370;// Dominican Republic
    public static final int MCC_HT = 372;// Haiti
    public static final int MCC_TT = 374;// Trinidad and Tobago
    public static final int MCC_TC = 376;// Turks and Caicos Islands
    public static final int MCC_AZ = 400;// Azerbaijan
    public static final int MCC_KZ = 401;// Kazakhstan
    public static final int MCC_BT = 402;// Bhutan
    public static final int MCC_IN_404 = 404;// India
    public static final int MCC_IN_405 = 405;// India
    public static final int MCC_IN_406 = 406;// India
    public static final int MCC_PK = 410;// Pakistan
    public static final int MCC_AF = 412;// Afghanistan
    public static final int MCC_LK = 413;// Sri Lanka
    public static final int MCC_MM = 414;// Myanmar
    public static final int MCC_LB = 415;// Lebanon
    public static final int MCC_JO = 416;// Jordan
    public static final int MCC_SY = 417;// Syria
    public static final int MCC_IQ = 418;// Iraq
    public static final int MCC_KW = 419;// Kuwait
    public static final int MCC_SA = 420;// Saudi Arabia
    public static final int MCC_YE = 421;// Yemen
    public static final int MCC_OM = 422;// Oman
    public static final int MCC_AE_424 = 424;// United Arab Emirates
    public static final int MCC_IL = 425;// Israel, Palestine
    public static final int MCC_BH = 426;// Bahrain
    public static final int MCC_QA = 427;// Qatar
    public static final int MCC_MN = 428;// Mongolia
    public static final int MCC_NP = 429;// Nepal
    public static final int MCC_AE_430 = 430;// United Arab Emirates (Abu Dhabi)
    public static final int MCC_AE_431 = 431;// United Arab Emirates (Dubai)
    public static final int MCC_IR = 432;// Iran
    public static final int MCC_UZ = 434;// Uzbekistan
    public static final int MCC_TJ = 436;// Tajikistan
    public static final int MCC_KG = 437;// Kyrgyzstan
    public static final int MCC_TM = 438;// Turkmenistan
    public static final int MCC_JP_440 = 440;// Japan
    public static final int MCC_JP_441 = 441;// Japan
    public static final int MCC_KR = 450;// Korea, South
    public static final int MCC_VN = 452;// Vietnam
    public static final int MCC_HK = 454;// Hong Kong
    public static final int MCC_MO = 455;// Macau
    public static final int MCC_KH = 456;// Cambodia
    public static final int MCC_LA = 457;// Laos
    public static final int MCC_CN_460 = 460;// China
    public static final int MCC_CN_461 = 461;// China
    public static final int MCC_TW = 466;// Taiwan
    public static final int MCC_KP = 467;// Korea, North
    public static final int MCC_BD = 470;// Bangladesh
    public static final int MCC_MV = 472;// Maldives
    public static final int MCC_MY = 502;// Malaysia
    public static final int MCC_AU = 505;// Australia, Norfolk Island
    public static final int MCC_ID = 510;// Indonesia
    public static final int MCC_TL = 514;// East Timor
    public static final int MCC_PH = 515;// Philippines
    public static final int MCC_TH = 520;// Thailand
    public static final int MCC_SG = 525;// Singapore
    public static final int MCC_BN = 528;// Brunei
    public static final int MCC_NZ = 530;// New Zealand
    public static final int MCC_NR = 536;// Nauru
    public static final int MCC_PG = 537;// Papua New Guinea
    public static final int MCC_TO = 539;// Tonga
    public static final int MCC_SB = 540;// Solomon Islands
    public static final int MCC_VU = 541;// Vanuatu
    public static final int MCC_FJ = 542;// Fiji
    public static final int MCC_WF = 543;// Wallis and Futuna
    public static final int MCC_AS = 544;// American Samoa (United States of America)
    public static final int MCC_KI = 545;// Kiribati
    public static final int MCC_NC = 546;// New Caledonia
    public static final int MCC_PF = 547;// French Polynesia (France)
    public static final int MCC_CK = 548;// Cook Islands (Pacific Ocean)
    public static final int MCC_WS = 549;// Samoa
    public static final int MCC_FM = 550;// Federated States of Micronesia
    public static final int MCC_MH = 551;// Marshall Islands
    public static final int MCC_PW = 552;// Palau
    public static final int MCC_TV = 553;// Tuvalu
    public static final int MCC_TK = 554;// Tokelau
    public static final int MCC_NU = 555;// Niue
    public static final int MCC_EG = 602;// Egypt
    public static final int MCC_DZ = 603;// Algeria
    public static final int MCC_MA = 604;// Morocco
    public static final int MCC_TN = 605;// Tunisia
    public static final int MCC_LY = 606;// Libya
    public static final int MCC_GM = 607;// Gambia
    public static final int MCC_SN = 608;// Senegal
    public static final int MCC_MR = 609;// Mauritania
    public static final int MCC_ML = 610;// Mali
    public static final int MCC_GN = 611;// Guinea
    public static final int MCC_CI = 612;// Ivory Coast
    public static final int MCC_BF = 613;// Burkina Faso
    public static final int MCC_NE = 614;// Niger
    public static final int MCC_TG = 615;// Togo
    public static final int MCC_BJ = 616;// Benin
    public static final int MCC_MU = 617;// Mauritius
    public static final int MCC_LR = 618;// Liberia
    public static final int MCC_SL = 619;// Sierra Leone
    public static final int MCC_GH = 620;// Ghana
    public static final int MCC_NG = 621;// Nigeria
    public static final int MCC_TD = 622;// Chad
    public static final int MCC_CF = 623;// Central African Republic
    public static final int MCC_CM = 624;// Cameroon
    public static final int MCC_CV = 625;// Cape Verde
    public static final int MCC_ST = 626;// São Tomé and Príncipe
    public static final int MCC_GQ = 627;// Equatorial Guinea
    public static final int MCC_GA = 628;// Gabon
    public static final int MCC_CG = 629;// Congo
    public static final int MCC_CD = 630;// Democratic Republic of the Congo
    public static final int MCC_AO = 631;// Angola
    public static final int MCC_GW = 632;// Guinea-Bissau
    public static final int MCC_SC = 633;// Seychelles
    public static final int MCC_SD = 634;// Sudan
    public static final int MCC_RW = 635;// Rwanda
    public static final int MCC_ET = 636;// Ethiopia
    public static final int MCC_SO = 637;// Somalia
    public static final int MCC_DJ = 638;// Djibouti
    public static final int MCC_KE = 639;// Kenya
    public static final int MCC_TZ = 640;// Tanzania
    public static final int MCC_UG = 641;// Uganda
    public static final int MCC_BI = 642;// Burundi
    public static final int MCC_MZ = 643;// Mozambique
    public static final int MCC_ZM = 645;// Zambia
    public static final int MCC_MG = 646;// Madagascar
    public static final int MCC_FR_647 = 647;// French Indian Ocean Territories (France), French Indian Ocean Territories (France)
    public static final int MCC_ZW = 648;// Zimbabwe
    public static final int MCC_NA = 649;// Namibia
    public static final int MCC_MW = 650;// Malawi
    public static final int MCC_LS = 651;// Lesotho
    public static final int MCC_BW = 652;// Botswana
    public static final int MCC_SZ = 653;// Swaziland
    public static final int MCC_KM = 654;// Comoros
    public static final int MCC_ZA = 655;// South Africa
    public static final int MCC_ER = 657;// Eritrea
    public static final int MCC_SH = 658;// Saint Helena, Ascension and Tristan da Cunha
    public static final int MCC_SS = 659;// South Sudan
    public static final int MCC_BZ = 702;// Belize
    public static final int MCC_GT = 704;// Guatemala
    public static final int MCC_SV = 706;// El Salvador
    public static final int MCC_HN = 708;// Honduras
    public static final int MCC_NI = 710;// Nicaragua
    public static final int MCC_CR = 712;// Costa Rica
    public static final int MCC_PA = 714;// Panama
    public static final int MCC_PE = 716;// Peru
    public static final int MCC_AR = 722;// Argentina
    public static final int MCC_BR = 724;// Brazil
    public static final int MCC_CL = 730;// Chile
    public static final int MCC_CO = 732;// Colombia
    public static final int MCC_VE = 734;// Venezuela
    public static final int MCC_BO = 736;// Bolivia
    public static final int MCC_GY = 738;// Guyana
    public static final int MCC_EC = 740;// Ecuador
    public static final int MCC_GF = 742;// French Guiana (France)
    public static final int MCC_PY = 744;// Paraguay
    public static final int MCC_SR = 746;// Suriname
    public static final int MCC_UY = 748;// Uruguay
    public static final int MCC_FK = 750;// Falkland Islands (United Kingdom)
    public static final int MCC_IO = 995;// British Indian Ocean Territory (United Kingdom)
    public static final int MCC_901 = 901;// International operators
    public static final int MCC_902 = 902;// International operators
    public static final int MCC_991 = 991;// International operators

    private MCCHelper() {
        //no instance
    }

    /**
     * 通过资源获取MCC
     *
     * @param context Context
     * @return MCC
     */
    public static int getMCC(Context context) {
        try {
            return context.getResources().getInteger(R.integer.mcc);
        } catch (Throwable t) {
            return MCC_TEST;
        }
    }

    /**
     * 通过MCC获取国家码
     *
     * @param context Context
     * @return 国家码
     */
    @Nullable
    public static String getISO3166(Context context) {
        switch (getMCC(context)) {
            default:
            case MCC_TEST:
            case MCC_INTERNAL:
            case MCC_901:
            case MCC_902:
            case MCC_991:
                return null;
            case MCC_AB:
                return "AB";
            case MCC_AF:
                return "AF";
            case MCC_AL:
                return "AL";
            case MCC_DZ:
                return "DZ";
            case MCC_AS:
                return "AS";
            case MCC_AD:
                return "AD";
            case MCC_AO:
                return "AO";
            case MCC_AI:
                return "AI";
            case MCC_AG:
                return "AG";
            case MCC_AR:
                return "AR";
            case MCC_AM:
                return "AM";
            case MCC_AW:
                return "AW";
            case MCC_AU:
                return "AU";
            case MCC_AT:
                return "AT";
            case MCC_AZ:
                return "AZ";
            case MCC_BS:
                return "BS";
            case MCC_BH:
                return "BH";
            case MCC_BD:
                return "BD";
            case MCC_BB:
                return "BB";
            case MCC_BY:
                return "BY";
            case MCC_BE:
                return "BE";
            case MCC_BZ:
                return "BZ";
            case MCC_BJ:
                return "BJ";
            case MCC_BM:
                return "BM";
            case MCC_BT:
                return "BT";
            case MCC_BO:
                return "BO";
            case MCC_BQ:
                return "BQ";
            case MCC_BA:
                return "BA";
            case MCC_BW:
                return "BW";
            case MCC_BR:
                return "BR";
            case MCC_IO:
                return "IO";
            case MCC_VG:
                return "VG";
            case MCC_BN:
                return "BN";
            case MCC_BG:
                return "BG";
            case MCC_BF:
                return "BF";
            case MCC_BI:
                return "BI";
            case MCC_KH:
                return "KH";
            case MCC_CM:
                return "CM";
            case MCC_CA:
                return "CA";
            case MCC_CV:
                return "CV";
            case MCC_KY:
                return "KY";
            case MCC_CF:
                return "CF";
            case MCC_TD:
                return "TD";
            case MCC_CL:
                return "CL";
            case MCC_CN_460:
            case MCC_CN_461:
                return "CN";
            case MCC_CO:
                return "CO";
            case MCC_KM:
                return "KM";
            case MCC_CG:
                return "CG";
            case MCC_CK:
                return "CK";
            case MCC_CR:
                return "CR";
            case MCC_HR:
                return "HR";
            case MCC_CU:
                return "CU";
            case MCC_CY:
                return "CY";
            case MCC_CZ:
                return "CZ";
            case MCC_CD:
                return "CD";
            case MCC_DK:
                return "DK";
            case MCC_DJ:
                return "DJ";
            case MCC_DM:
                return "DM";
            case MCC_DO:
                return "DO";
            case MCC_TL:
                return "TL";
            case MCC_EC:
                return "EC";
            case MCC_EG:
                return "EG";
            case MCC_SV:
                return "SV";
            case MCC_GQ:
                return "GQ";
            case MCC_ER:
                return "ER";
            case MCC_EE:
                return "EE";
            case MCC_ET:
                return "ET";
            case MCC_FK:
                return "FK";
            case MCC_FO:
                return "FO";
            case MCC_FJ:
                return "FJ";
            case MCC_FI:
                return "FI";
            case MCC_FR_208:
            case MCC_FR_340:
            case MCC_FR_647:
                return "FR";
            case MCC_GF:
                return "GF";
            case MCC_PF:
                return "PF";
            case MCC_GA:
                return "GA";
            case MCC_GM:
                return "GM";
            case MCC_GE:
                return "GE";
            case MCC_DE:
                return "DE";
            case MCC_GH:
                return "GH";
            case MCC_GI:
                return "GI";
            case MCC_GR:
                return "GR";
            case MCC_GL:
                return "GL";
            case MCC_GD:
                return "GD";
            case MCC_GT:
                return "GT";
            case MCC_GN:
                return "GN";
            case MCC_GW:
                return "GW";
            case MCC_GY:
                return "GY";
            case MCC_HT:
                return "HT";
            case MCC_HN:
                return "HN";
            case MCC_HK:
                return "HK";
            case MCC_HU:
                return "HU";
            case MCC_IS:
                return "IS";
            case MCC_IN_404:
            case MCC_IN_405:
            case MCC_IN_406:
                return "IN";
            case MCC_ID:
                return "ID";
            case MCC_IR:
                return "IR";
            case MCC_IQ:
                return "IQ";
            case MCC_IE:
                return "IE";
            case MCC_IL:
                return "IL";
            case MCC_IT:
                return "IT";
            case MCC_CI:
                return "CI";
            case MCC_JM:
                return "JM";
            case MCC_JP_440:
            case MCC_JP_441:
                return "JP";
            case MCC_JO:
                return "JO";
            case MCC_KZ:
                return "KZ";
            case MCC_KE:
                return "KE";
            case MCC_KI:
                return "KI";
            case MCC_KP:
                return "KP";
            case MCC_KR:
                return "KR";
            case MCC_XK:
                return "XK";
            case MCC_KW:
                return "KW";
            case MCC_KG:
                return "KG";
            case MCC_LA:
                return "LA";
            case MCC_LV:
                return "LV";
            case MCC_LB:
                return "LB";
            case MCC_LS:
                return "LS";
            case MCC_LR:
                return "LR";
            case MCC_LY:
                return "LY";
            case MCC_LI:
                return "LI";
            case MCC_LT:
                return "LT";
            case MCC_LU:
                return "LU";
            case MCC_MO:
                return "MO";
            case MCC_MK:
                return "MK";
            case MCC_MG:
                return "MG";
            case MCC_MW:
                return "MW";
            case MCC_MY:
                return "MY";
            case MCC_MV:
                return "MV";
            case MCC_ML:
                return "ML";
            case MCC_MT:
                return "MT";
            case MCC_MH:
                return "MH";
            case MCC_MR:
                return "MR";
            case MCC_MU:
                return "MU";
            case MCC_MX:
                return "MX";
            case MCC_FM:
                return "FM";
            case MCC_MD:
                return "MD";
            case MCC_MC:
                return "MC";
            case MCC_MN:
                return "MN";
            case MCC_ME:
                return "ME";
            case MCC_MS:
                return "MS";
            case MCC_MA:
                return "MA";
            case MCC_MZ:
                return "MZ";
            case MCC_MM:
                return "MM";
            case MCC_NA:
                return "NA";
            case MCC_NR:
                return "NR";
            case MCC_NP:
                return "NP";
            case MCC_NL:
                return "NL";
            case MCC_NC:
                return "NC";
            case MCC_NZ:
                return "NZ";
            case MCC_NI:
                return "NI";
            case MCC_NE:
                return "NE";
            case MCC_NG:
                return "NG";
            case MCC_NU:
                return "NU";
            case MCC_NO:
                return "NO";
            case MCC_OM:
                return "OM";
            case MCC_PK:
                return "PK";
            case MCC_PW:
                return "PW";
            case MCC_PA:
                return "PA";
            case MCC_PG:
                return "PG";
            case MCC_PY:
                return "PY";
            case MCC_PE:
                return "PE";
            case MCC_PH:
                return "PH";
            case MCC_PL:
                return "PL";
            case MCC_PT:
                return "PT";
            case MCC_PR:
                return "PR";
            case MCC_QA:
                return "QA";
            case MCC_RO:
                return "RO";
            case MCC_RU:
                return "RU";
            case MCC_RW:
                return "RW";
            case MCC_SH:
                return "SH";
            case MCC_KN:
                return "KN";
            case MCC_LC:
                return "LC";
            case MCC_PM:
                return "PM";
            case MCC_VC:
                return "VC";
            case MCC_WS:
                return "WS";
            case MCC_SM:
                return "SM";
            case MCC_ST:
                return "ST";
            case MCC_SA:
                return "SA";
            case MCC_SN:
                return "SN";
            case MCC_RS:
                return "RS";
            case MCC_SC:
                return "SC";
            case MCC_SL:
                return "SL";
            case MCC_SG:
                return "SG";
            case MCC_SK:
                return "SK";
            case MCC_SI:
                return "SI";
            case MCC_SB:
                return "SB";
            case MCC_SO:
                return "SO";
            case MCC_ZA:
                return "ZA";
            case MCC_SS:
                return "SS";
            case MCC_ES:
                return "ES";
            case MCC_LK:
                return "LK";
            case MCC_SD:
                return "SD";
            case MCC_SR:
                return "SR";
            case MCC_SZ:
                return "SZ";
            case MCC_SE:
                return "SE";
            case MCC_CH:
                return "CH";
            case MCC_SY:
                return "SY";
            case MCC_TW:
                return "TW";
            case MCC_TJ:
                return "TJ";
            case MCC_TZ:
                return "TZ";
            case MCC_TH:
                return "TH";
            case MCC_TG:
                return "TG";
            case MCC_TK:
                return "TK";
            case MCC_TO:
                return "TO";
            case MCC_TT:
                return "TT";
            case MCC_TN:
                return "TN";
            case MCC_TR:
                return "TR";
            case MCC_TM:
                return "TM";
            case MCC_TC:
                return "TC";
            case MCC_TV:
                return "TV";
            case MCC_UG:
                return "UG";
            case MCC_UA:
                return "UA";
            case MCC_AE_424:
            case MCC_AE_430:
            case MCC_AE_431:
                return "AE";
            case MCC_GB_234:
            case MCC_GB_235:
                return "GB";
            case MCC_US_310:
            case MCC_US_311:
            case MCC_US_312:
            case MCC_US_313:
            case MCC_US_314:
            case MCC_US_315:
            case MCC_US_316:
                return "US";
            case MCC_VI:
                return "VI";
            case MCC_UY:
                return "UY";
            case MCC_UZ:
                return "UZ";
            case MCC_VU:
                return "VU";
            case MCC_VE:
                return "VE";
            case MCC_VN:
                return "VN";
            case MCC_WF:
                return "WF";
            case MCC_YE:
                return "YE";
            case MCC_ZM:
                return "ZM";
            case MCC_ZW:
                return "ZW";
        }
    }
}
