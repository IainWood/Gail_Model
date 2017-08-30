package iain.application;
import java.lang.Math;

class RiskCalculator{
    //region Members

    //this is to store beta values
    private double[][] bet2 = new double[8][12];
    private double[] bet = new double[8];

    private double[] rf = new double[2];
    private double[] abs = new double[216];

    private double[] rlan = new double[14];
    private double[] rmu = new double[14];
    private double[] sumb = new double[216];
    private double[] sumbb = new double[216];
    private double[] t = new double[15];

    private double[][] rmu2 = new double[14][12];//[14,6];
    private double[][] rlan2 = new double[14][12]; //[14,6];[84]

    private double[][] rf2 = new double[2][13]; //[12]

    //endregion

    RiskCalculator(){
        Initialize();
    }

    /*
    * Initialize the arrays
    */
    private void Initialize()
    {
        double age = 20.0; // the first age boundary [20-90]
        for(int i = 0; i < 15; i++){
            t[i] = age;
            age += 5;
        }

    /*
    age specific competing hazards (h2) - BCPT model or STAR model
    SEER mortality 1985:87, excluding death from breast cancer - white, African American)
    US   mortality 1990:96, excluding death from breast cancer -     hispanic)
    ages [20:25), [25:30), [30:35) .... [70:74), [75:80), [80:85), [85:90)
    */

        double[] rmu_values_white = {49.3, 53.1, 62.5, 82.5, 130.7, 218.1,
                365.5, 585.2, 943.9, 1502.8, 2383.9, 3883.2, 6682.8, 14490.8};

        double[] rmu_values_white_2 = {44.12, 52.54, 67.46, 90.92,
                125.34, 195.7, 329.84, 546.22,
                910.35, 1418.54, 2259.35, 3611.46,
                6136.26, 14206.63};

        double[] rmu_values_african_american = {0.00074354, 0.00101698,
                0.00145937, 0.00215933, 0.00315077, 0.00448779, 0.00632281, 0.00963037,
                0.01471818, 0.01471818, 0.02116304, 0.03266035, 0.04564087, 0.06835185, 0.13271262};

        double[] rmu_values_african_american_2 = {0.00074354, 0.00101698,
                0.00145937, 0.00215933, 0.00315077, 0.00448779, 0.00632281,  0.00963037,
                0.01471818, 0.02116304, 0.03266035, 0.04564087, 0.06835185, 0.13271262};

        double[] rmu_values_hispanic = {43.7, 53.3, 70.0, 89.7, 116.3,
                170.2, 264.6, 421.6, 696.0, 1086.7,
                1685.8, 2515.6, 4186.6, 8947.6};

        final double RMU_MULTIPLIER = 0.00001;
        final int ARRAY_LENGTH = 14;

        for(int i = 0; i < ARRAY_LENGTH; i++){
            rmu2[i][0] = rmu_values_white[i] * RMU_MULTIPLIER;
            rmu2[i][1] = rmu_values_african_american[i];
            rmu2[i][2] = rmu_values_hispanic[i] * RMU_MULTIPLIER;
            rmu2[i][3] = rmu_values_white_2[i] * RMU_MULTIPLIER;
            rmu2[i][4] = rmu_values_african_american_2[i];
            rmu2[i][5] = rmu_values_hispanic[i] * RMU_MULTIPLIER;
        }

        double[] rlan2_values_white = {1.0, 7.6, 26.6, 66.1, 126.5, 186.6,
                221.1, 272.1, 334.8, 392.3, 417.8,
                443.9, 442.1, 410.9};

        double[] rlan2_values_african_american = {0.00002696, 0.00011295, 0.00031094,
                0.00067639, 0.00119444, 0.00187394, 0.00241504, 0.00291112, 0.00310127,
                0.00366560, 0.00393132, 0.00408951, 0.00396793, 0.00363712};

        double[] rlan2_values_hispanic = {2.00, 7.10, 19.70, 43.80, 81.10,
                130.70, 157.40, 185.70, 215.10, 251.20, 284.60, 275.70, 252.30, 203.90};

        double[] rlan2_values_white_2 = {1.22, 7.41, 22.97, 56.49, 116.45,
                195.25, 261.54, 302.79, 367.57, 420.29, 473.08, 494.25, 479.76, 401.06};

        double[] rlan2_values_african_american_2 = {0.00002696, 0.00011295,
                0.00031094, 0.00067639, 0.00119444, 0.00187394, 0.00241504, 0.00291112,
                0.00310127, 0.00366560, 0.00393132, 0.00408951, 0.00396793, 0.00363712};

        final double RLAN2_MULTIPLIER = 0.00001;

        for(int i = 0; i < ARRAY_LENGTH; i++){
            rlan2[i][0] = rlan2_values_white[i] * RLAN2_MULTIPLIER;
            rlan2[i][1] = rlan2_values_african_american[i];
            rlan2[i][2] = rlan2_values_hispanic[i] * RLAN2_MULTIPLIER;
            rlan2[i][3] = rlan2_values_white_2[i] * RLAN2_MULTIPLIER;
            rlan2[i][4] = rlan2_values_african_american_2[i];
            rlan2[i][5] = rlan2_values_hispanic[i] * RLAN2_MULTIPLIER;
        }

        // White & Other women logistic regression coefficients - GAIL model (BCDDP)

        bet2[0][0] = -0.7494824600;     // intercept            1/12/99 & 11/13/07
        bet2[1][0] = 0.0108080720;     // age >= 50 indicator

        bet2[2][0] = 0.0940103059;     // age menarchy
        bet2[3][0] = 0.5292641686;     // # of breast biopsy
        bet2[4][0] = 0.2186262218;     // age 1st live birth
        bet2[5][0] = 0.9583027845;     // # 1st degree relatives with breast ca

        bet2[6][0] = -0.2880424830;     // # breast biopsy * age >=50 indicator
        bet2[7][0] = -0.1908113865;     // age 1st live birth * # 1st degree rel

        // African American women  logistic regression coefficients - CARE model

        bet2[0][1] = -0.3457169653;     // intercept                      11/13/07
        bet2[1][1] = 0.0334703319;     // age >= 50 indicator set � to 0 in PGM

        bet2[2][1] = 0.2672530336;     // age menarchy
        bet2[3][1] = 0.1822121131;     // # of breast biopsy
        bet2[4][1] = 0.0000000000;     // age 1st live birth
        bet2[5][1] = 0.4757242578;     // # 1st degree relatives with breast ca

        bet2[6][1] = -0.1119411682;     // # breast biopsy * age >=50 indicator
        bet2[7][1] = 0.0000000000;     // age 1st live birth * # 1st degree rel

        // Hispanic women   logistic regression coefficients - GAIL model (BCDDP)

        bet2[0][2] = -0.7494824600;     // intercept            1/12/99 & 11/13/07
        bet2[1][2] = 0.0108080720;     // age >= 50 indicator

        bet2[2][2] = 0.0940103059;     // age menarchy
        bet2[3][2] = 0.5292641686;     // # of breast biopsy
        bet2[4][2] = 0.2186262218;     // age 1st live birth
        bet2[5][2] = 0.9583027845;     // # 1st degree relatives with breast ca

        bet2[6][2] = -0.2880424830;     // # breast biopsy * age >=50 indicator
        bet2[7][2] = -0.1908113865;     // age 1st live birth * # 1st degree rel

        //American-Asian Beta
        for(int i = 6; i <= 11; i++){
            bet2[0][i] = 0.00000000000000; //  intercept
            bet2[1][i] = 0.00000000000000; //  age >= 50 indicator
            bet2[2][i] = 0.07499257592975; //age menarchy
            bet2[3][i] = 0.55263612260619; //   # of breast biopsy
            bet2[4][i] = 0.27638268294593; //  age 1st live birth
            bet2[5][i] = 0.79185633720481; //   # 1st degree relatives with breast ca
            bet2[6][i] = 0.00000000000000; //  # breast biopsy * age >=50 indicator
            bet2[7][i] = 0.00000000000000; //   age 1st live birth * # 1st degree rel
        }

    /* age 1st live birth * # 1st degree rel */

        // conversion factors (1-attributable risk) used in BCPT model

        rf2[0][0] = 0.5788413;         // age < 50, race=white,other        1/12/99
        rf2[1][0] = 0.5788413;         // age >=50, race=white,other


    /* 11/27/2007 SRamaiah.
    * Based on Journal(JNCI djm223 LM) published on Dec 05, 2007 by Gail and other scientists,
    * The new values are being used for african american woman
    * as there were some major descrenpancies between CARE model and GAIL Model
    */

        rf2[0][1] = 0.72949880;         // age < 50, race=African American     12/19/2007 based on david pee's input
        rf2[1][1] = 0.74397137;         // age >=50, race=African American

        rf2[0][2] = 0.5788413;         // age < 50, race=hispanic   5/12/2000
        rf2[1][2] = 0.5788413;         // age >=50, race=hispanic

        // conversion factors (1-attributable risk) used for "average woman"

        rf2[0][3] = 1.0;                // age < 50, race=white avg woman      11/21
        rf2[1][3] = 1.0;                // age >=50, race=white avg woman

        rf2[0][4] = 1.0;                // age < 50, race=African American avg woman      11/21
        rf2[1][4] = 1.0;                // age >=50, race=African American avg woman

        rf2[0][5] = 1.0;                // age < 50, race=hispanic avg woman    5/12
        rf2[1][5] = 1.0;                // age >=50, race=hispanic avg woman

        //American-Asian conversion factor
        for(int i = 6; i <= 11; i++){
            rf2[0][i] = 0.47519806426735;                // age < 50, avg woman
            rf2[1][i] = 0.50316401683903;                // age >=50, avg woman
        }

        rf2[0][12] = 1.0;                // age < 50, race=hispanic avg woman    5/12
        rf2[1][12] = 1.0;                // age >=50, race=hispanic avg woman


        //*** SEER18 incidence 1998:02 - chinese  Jan052010;
        final double[] INCIDENCE_CHINESE = {0.000004059636, 0.000045944465,
                0.000188279352, 0.000492930493, 0.000913603501, 0.001471537353,
                0.001421275482, 0.001970946494, 0.001674745804, 0.001821581075,
                0.001834477198, 0.001919911972, 0.002233371071, 0.002247315779};

        //*** NCHS mortality 1998:02,    chinese  Jan052010;
        final double[] MORTALITY_CHINESE = {0.000210649076, 0.000192644865,
                0.000244435215, 0.000317895949, 0.000473261994, 0.000800271380,
                0.001217480226, 0.002099836508, 0.003436889186, 0.006097405623,
                0.010664526765, 0.020148678452, 0.037990796590, 0.098333900733};

        //*** SEER18 incidence 1998:02 - japanese  Jan052010;
        final double[] INCIDENCE_JAPANESE = {0.000000000001, 0.000099483924,
                0.000287041681, 0.000545285759, 0.001152211095, 0.001859245108,
                0.002606291272, 0.003221751682, 0.004006961859, 0.003521715275,
                0.003593038294, 0.003589303081, 0.003538507159,0.002051572909};

        //*** NCHS mortality 1998:02,    japanese  Jan052010;
        final double[] MORTALITY_JAPANESE = {0.000173593803, 0.000295805882,
                0.000228322534, 0.000363242389, 0.000590633044, 0.001086079485,
                0.001859999966, 0.003216600974, 0.004719402141, 0.008535331402,
                0.012433511681, 0.020230197885, 0.037725498348, 0.106149118663};

        //*** SEER18 incidence 1998:02 - filipino  Jan052010;
        final double[] INCIDENCE_FILIPINO = {0.000007500161, 0.000081073945,
                0.000227492565, 0.000549786433, 0.001129400541, 0.001813873795,
                0.002223665639, 0.002680309266, 0.002891219230, 0.002534421279,
                0.002457159409, 0.002286616920, 0.001814802825, 0.001750879130};

        //*** NCHS mortality 1998:02,    filipino  Jan052010;
        final double[] MORTALITY_FILIPINO = {0.000229120979, 0.000262988494,
                0.000314844090, 0.000394471908, 0.000647622610, 0.001170202327,
                0.001809380379, 0.002614170568, 0.004483330681, 0.007393665092,
                0.012233059675, 0.021127058106, 0.037936954809,0.085138518334};

        //*** SEER18 incidence 1998:02 - hawaiian  Jan052010;
        final double[] INCIDENCE_HAWAIIAN = {0.000045080582, 0.000098570724,
                0.000339970860, 0.000852591429, 0.001668562761, 0.002552703284,
                0.003321774046, 0.005373001776, 0.005237808549, 0.005581732512,
                0.005677419355, 0.006513409962, 0.003889457523, 0.002949061662};

        //*** NCHS mortality 1998:02,    hawaiian  Jan052010;
        final double[] MORTALITY_HAWAIIAN = {0.000563507269, 0.000369640217,
                0.001019912579, 0.001234013911, 0.002098344078, 0.002982934175,
                0.005402445702, 0.009591474245, 0.016315472607, 0.020152229069,
                0.027354838710, 0.050446998723, 0.072262026612, 0.145844504021};

        //*** SEER18 incidence 1998:02 - other pacific islander  Jan052010;
        final double[] INCIDENCE_PACIFIC = {0.000000000001, 0.000071525212,
                0.000288799028, 0.000602250698, 0.000755579402, 0.000766406354,
                0.001893124938, 0.002365580107, 0.002843933070, 0.002920921732,
                0.002330395655, 0.002036291235, 0.001482683983, 0.001012248203};

        //*** NCHS mortality 1998:02,    other pacific islander  Jan052010;
        final double[] MORTALITY_PACIFIC = {0.000465500812, 0.000600466920,
                0.000851057138, 0.001478265376, 0.001931486788, 0.003866623959,
                0.004924932309, 0.008177071806, 0.008638202890, 0.018974658371,
                0.029257567105, 0.038408980974, 0.052869579345, 0.074745721133};

        //*** SEER18 incidence 1998:02 - other asian  Jan052010;
        final double[] INCIDENCE_ASIAN = {0.000012355409, 0.000059526456,
                0.000184320831, 0.000454677273, 0.000791265338, 0.001048462801,
                0.001372467817, 0.001495473711, 0.001646746198, 0.001478363563,
                0.001216010125, 0.001067663700, 0.001376104012, 0.000661576644};

        //*** NCHS mortality 1998:02,    other asian Jan052010;
        final double[] MORTALITY_ASIAN = {0.000212632332, 0.000242170741,
                0.000301552711, 0.000369053354, 0.000543002943, 0.000893862331,
                0.001515172239, 0.002574669551, 0.004324370426, 0.007419621918,
                0.013251765130, 0.022291427490, 0.041746550635, 0.087485802065};

        for(int i = 0; i < 14; i++){
            rlan2[i][6] = INCIDENCE_CHINESE[i];
            rmu2[i][6] = MORTALITY_CHINESE[i];

            rlan2[i][7] = INCIDENCE_JAPANESE[i];
            rmu2[i][7] = MORTALITY_JAPANESE[i];

            rlan2[i][8] = INCIDENCE_FILIPINO[i];
            rmu2[i][8] = MORTALITY_FILIPINO[i];

            rlan2[i][9] = INCIDENCE_HAWAIIAN[i];
            rmu2[i][9] = MORTALITY_HAWAIIAN[i];

            rlan2[i][10] = INCIDENCE_PACIFIC[i];
            rmu2[i][10] = MORTALITY_PACIFIC[i];

            rlan2[i][11] = INCIDENCE_ASIAN[i];
            rmu2[i][11] = MORTALITY_ASIAN[i];
        }

    }

    double CalculateRisk(
            int riskindex,
            int CurrentAge,
            int ProjectionAge,
            int AgeIndicator,
            int numberOfBiopsy,
            int menarcheAge,
            int FirstLiveBirthAge,
            int FirstDegRelatives,
            double rhyp,
            int irace) {
    /*
    RiskIndex           [1 Abs, 2 Avg]
    , CurrentAge		    //[t1]
    , ProjectionAge	        //[t2]
    , AgeIndicator	        //[i0]
    , NumberOfBiopsy	    //[i2]
    , MenarcheAge		    //[i1]
    , FirstLiveBirthAge     //[i3]
    , EverHadBiopsy	        //[iever]
    , HyperPlasia		    //[ihyp]
    , FirstDegRelatives     //[i4]
    , RHyperPlasia	        //[rhyp]
    , Race			        //[race]
    */

        double retval;
    /* Local variables */
        int i, j, k;
        double r;
        int ni;
        double ti;
        int ns;
        double ts;
        int incr, ilev;
        double[][] r8iTox2 = new double[216][9];
        //HACK
        ni = 0;
        ns = 0;
        ti = (double)(CurrentAge);
        ts = (double)(ProjectionAge);

    /*11/29/2007 SR: setting BETA to race specific lnRR*/
        for(i = 0; i < 8; i++){
            bet[i] = bet2[i][irace - 1];   //index starts from 0 hence irace-1
        }


    /*11/29/2007 SR: recode agemen for African American women*/
        if(irace == 2){                 // for African American women
            if(menarcheAge == 2){
                menarcheAge = 1;        // recode agemen=2 (age<12) to agmen=1 [12,13]
                FirstLiveBirthAge = 0;  // set age 1st live birth to 0
            }
        }

        for(i = 1; i <= 15; ++i){
      /* i-1=14 ==> current age=85, max for curre */
            if(ti < t[i - 1]){
                ni = i - 1;	/* ni holds the index for current */
                break; //goto L70;
            }
        }

        for(i = 1; i <= 15; ++i){
            if(ts <= t[i - 1]){
                ns = i - 1;	/* ns holds the index for risk as */
                break;	//goto L80;
            }
        }

        incr = 0;
        if(riskindex == 2 && irace < 7){
            //HACK CHECK THIS
            incr = 3;
        }

    /* for race specific "avg women" */
    /* otherwise use cols 1,2,3 depen */
    /* on users race                5 */
    /* use cols 4,5,6 from rmu, rla */

    /* select race specific */
        int cindx;  //column index
        cindx = incr + irace - 1;

        //Console.WriteLine("------------------Contents of rmu");
        for(i = 0; i < 14; ++i){
            rmu[i] = rmu2[i][cindx];	/* competeing baseline h */
            rlan[i] = rlan2[i][cindx];	/* br ca composite incid */
            //Console.WriteLine(string.Format("{0} {1} {2}", i, cindx, rmu[i].ToString("F")));
        }
        //Console.WriteLine("ns={0}", ns);
        //PrintArray(rlan, "rlan");
        //PrintArray(rmu, "rmu");

        rf[0] = rf2[0][incr + irace - 1];/* selecting correct fac */
        rf[1] = rf2[1][incr + irace - 1];/* based on race */
        if(riskindex == 2 && irace >= 7){
            rf[0] = rf2[0][12];/* selecting correct fac */
            rf[1] = rf2[1][12];/* based on race */

        }

        if(riskindex >= 2){
      /* set risk factors to */
            menarcheAge = 0;	    // baseline age menarchy
            numberOfBiopsy = 0;	    // # of previous biop
            FirstLiveBirthAge = 0;	// age 1st live birth
            FirstDegRelatives = 0;	// # 1st degree relat
            rhyp = 1.0;	            // set hyperplasia to 1.0
        }

        ilev = AgeIndicator * 108 + menarcheAge * 36 + numberOfBiopsy * 12 + FirstLiveBirthAge * 3 + FirstDegRelatives + 1;	/* matrix of */

    /* covariate */
    /* range of 1 */
    /* AgeIndicator: age ge 50 ind  0=[20, 50) */
    /*                    1=[50, 85) */
    /* MenarcheAge: age menarchy   0=[14, 39] U 99 (unknown) */
    /*                    1=[12, 14) */
    /*                    2=[ 7, 12) */
    /* NumberOfBiopsy: # biopsy       0=0 or (99 and ever had biopsy=99 */
    /*                    1=1 or (99 and ever had biopsy=1 y */
    /*                    2=[ 2, 30] */
    /* FirstLiveBirthAge: age 1st live   0=<20, 99 (unknown) */
    /*                    1=[20, 25) */
    /*                    2=[25, 30) U 0 */
    /*                    3=[30, 55] */
    /* FirstDegRelatives: 1st degree rel 0=0, 99 (unknown) */
    /*                    1=1 */
    /*                    2=[2, 31] */
    /* **  Correspondence between exposure level and covariate factors X */
    /* **  in the logistic model */
    /* **  i-to-X correspondence */
    /* index in r8i */
        for(k = 0; k < 216; ++k){
      /* col1: intercept o */
            r8iTox2[k][0] = 1.0;
        }

        for(k = 0; k < 108; ++k){
      /* col2: indicator for age */
            r8iTox2[k][1] = 0.0;
            r8iTox2[108 + k][1] = 1.0;
        }

        for(j = 1; j <= 2; ++j){
      /* col3: age menarchy cate */
            for(k = 1; k <= 36; ++k){
                r8iTox2[(j - 1) * 108 + k - 1][2] = 0.0;
                r8iTox2[(j - 1) * 108 + 36 + k - 1][2] = 1.0;
                r8iTox2[(j - 1) * 108 + 72 + k - 1][2] = 2.0;
            }
        }

        for(j = 1; j <= 6; ++j){
      /* col4: # biopsy cate */
            for(k = 1; k <= 12; ++k){
                r8iTox2[(j - 1) * 36 + k - 1][3] = 0.0;
                r8iTox2[(j - 1) * 36 + 12 + k - 1][3] = 1.0;
                r8iTox2[(j - 1) * 36 + 24 + k - 1][3] = 2.0;
            }
        }

        for(j = 1; j <= 18; ++j){
      /* col5: age 1st live birt */
            for(k = 1; k <= 3; ++k){
                r8iTox2[(j - 1) * 12 + k - 1][4] = 0.0;
                r8iTox2[(j - 1) * 12 + 3 + k - 1][4] = 1.0;
                r8iTox2[(j - 1) * 12 + 6 + k - 1][4] = 2.0;
                r8iTox2[(j - 1) * 12 + 9 + k - 1][4] = 3.0;
            }
        }

        for(j = 1; j <= 72; ++j){
      /* col6: # 1st degree re */
            r8iTox2[(j - 1) * 3 + 1 - 1][5] = 0.0;
            r8iTox2[(j - 1) * 3 + 2 - 1][5] = 1.0;
            r8iTox2[(j - 1) * 3 + 3 - 1][5] = 2.0;
        }

        for(i = 0; i < 216; ++i){
      /* col8: age 1st live*# r */
      /* col7: age*#biop intera */
            r8iTox2[i][6] = r8iTox2[i][1] * r8iTox2[i][3];
            r8iTox2[i][7] = r8iTox2[i][4] * r8iTox2[i][5];
        }

        for(i = 0; i < 216; ++i){
            //HACK r8iTox2[i + 1727] = 1.0;
            r8iTox2[i][8] = 1.0;
        }


    /* **  Computation of breast cancer risk */
    /* **  sum(bi*Xi) for all covariate patterns */
        for(i = 0; i < 216; ++i){
      /* ln relative risk from BCDDP */
            sumb[i] = 0.0;
            for(j = 0; j < 8; ++j){
                sumb[i] += (bet[j] * r8iTox2[i][j]);
            }
        }

        for(i = 0; i < 216; i++){
            if(i <= 108){
                sumbb[i] = sumb[i] - bet[0]; /* eliminate int */
            } else {
                sumbb[i] = sumb[i] - bet[0] - bet[1];  /* eliminate intercept */
            }
        }

        for(i = 0; i < 14; i++){
            if(i <= 6){
                rlan[i] *= rf[0]; /* age specific baseline hazard */
            } else {
                rlan[i] *= rf[1]; /* age specific baseline hazard a */
            }
        }

        i = ilev;	/* index ilev of range 1- */
    /* setting i to covariate p */
        //HACK CHECK LOG VALUE
        sumbb[i - 1] += Math.log(rhyp);
        if(i <= 108){
            sumbb[i + 107] += Math.log(rhyp);
        }
        //Console.WriteLine("sumbb  0th Elmnt {0} 107th Elmnt{1}", sumbb[0], sumbb[107]);

        if(ts <= t[ni]){
      /* same 5 year age risk in */
      /* age & projection age wi */
            abs[i - 1] = 1.0 - Math.exp(-(rlan[ni - 1] * Math.exp(sumbb[
                    i - 1]) + rmu[ni - 1]) * (ts - ti));
            abs[i - 1] = abs[i - 1] * rlan[ni - 1] * Math.exp(
                    sumbb[i - 1]) / (rlan[ni - 1] * Math.exp(sumbb[
                    i - 1]) + rmu[ni - 1]);	/* breast cance */
        } else {
      /* 5 year age risk interval */
      /* calculate risk from */
      /* 1st age interval */
      /* age & projection age not i */
            abs[i - 1] = 1.0 - Math.exp(-(rlan[ni - 1] * Math.exp(sumbb[
                    i - 1]) + rmu[ni - 1]) * (t[ni] - ti));
            abs[i - 1] = abs[i - 1] * rlan[ni - 1] * Math.exp(
                    sumbb[i - 1]) / (rlan[ni - 1] * Math.exp(sumbb[
                    i - 1]) + rmu[ni - 1]);	/* age in */
      /* risk f */
            if(ns - ni > 0){
                if((double)(ProjectionAge) > 50.0 && (double)(CurrentAge) < 50.0){
          /* a */
          /* s */
          /* a */
          /* calculate ris */
          /* last age inte */
          /*
          Console.WriteLine("value of r={0} ns={1} i={2}", r.ToString("F5"), ns.ToString("F5"), i.ToString("F5"));
          Console.WriteLine("rlan[ns - 1] \t sumbb[i + 107] \t rmu[ns - 1] \t ts \t t[ns - 1] \t math");
          Console.Write("{0} \t {1} \t {2} \t {3} \t {4}", rlan[ns - 1].ToString("F5"), sumbb[i + 107].ToString("F5"), rmu[ns - 1].ToString("F5"), ts.ToString("F5"), t[ns - 1].ToString("F5"));
          Console.Write(" \t {0}", Math.Exp(sumbb[i + 107]).ToString("F5"));
          Console.WriteLine();
          */

                    r = 1.0 - Math.exp(-(rlan[ns - 1] * Math.exp(sumbb[i + 107]) + rmu[ns - 1]) * (ts - t[ns - 1]));
                    //Console.WriteLine("value of r {0}: ", r.ToString("F5"));
                    r = r * rlan[ns - 1] * Math.exp(sumbb[i + 107]) / (rlan[ns - 1] * Math.exp(sumbb[i + 107]) + rmu[ns - 1]);
                    //Console.WriteLine("value of r {0}: ", r.ToString("F5"));
                    r *= Math.exp(-(rlan[ni - 1] * Math.exp(sumbb[i - 1]) + rmu[ni - 1]) * (t[ni] - ti));
                    //Console.WriteLine("value of r {0}: ", r.ToString("F5"));
                    if(ns - ni > 1){
                        menarcheAge = ns - 1;
                        for(j = ni + 1; j <= menarcheAge; ++j){
                            if(t[j - 1] >= 50.0){
                                r *= Math.exp(-(rlan[j - 1] * Math.exp(sumbb[
                                        i + 107]) + rmu[j - 1]) * (t[
                                        j] - t[j - 1]));
                            } else {
                                r *= Math.exp(-(rlan[j - 1] * Math.exp(sumbb[
                                        i - 1]) + rmu[j - 1]) * (t[j]
                                        - t[j - 1]));
                            }
                        }
                    }
                    abs[i - 1] += r;
                } else {
          /* calculate risk from */
          /* last age interval */
          /* ages do not stradle */
                    r = 1.0 - Math.exp(-(rlan[ns - 1] * Math.exp(sumbb[i - 1])
                            + rmu[ns - 1]) * (ts - t[ns - 1]));
                    r = r * rlan[ns - 1] * Math.exp(sumbb[i - 1]) / (
                            rlan[ns - 1] * Math.exp(sumbb[i - 1]) +
                                    rmu[ns - 1]);
                    r *= Math.exp(-(rlan[ni - 1] * Math.exp(sumbb[i - 1]) +
                            rmu[ni - 1]) * (t[ni] - ti));
                    if(ns - ni > 1){
                        menarcheAge = ns - 1;
                        for(j = ni + 1; j <= menarcheAge; ++j){
                            r *= Math.exp(-(rlan[j - 1] * Math.exp(sumbb[i -
                                    1]) + rmu[j - 1]) * (t[j] - t[
                                    j - 1]));
                        }
                    }
                    abs[i - 1] += r;
                }
            }

            if(ns - ni > 1){
                if((double)(ProjectionAge) > 50.0 && (double)(CurrentAge) < 50.0){
          /* calculate risk from */
          /* intervening age int */
                    menarcheAge = ns - 1;
                    for(k = ni + 1; k <= menarcheAge; ++k){
                        if(t[k - 1] >= 50.0){
                            r = 1.0 - Math.exp(-(rlan[k - 1] * Math.exp(sumbb[
                                    i + 107]) + rmu[k - 1]) * (t[k] -
                                    t[k - 1]));
                            r = r * rlan[k - 1] * Math.exp(sumbb[i +
                                    107]) / (rlan[k - 1] * Math.exp(sumbb[
                                    i + 107]) + rmu[k - 1]);
                        } else {
                            r = 1.0 - Math.exp(-(rlan[k - 1] * Math.exp(sumbb[
                                    i - 1]) + rmu[k - 1]) * (t[k] -
                                    t[k - 1]));
                            r = r * rlan[k - 1] * Math.exp(sumbb[i - 1]
                            ) / (rlan[k - 1] * Math.exp(sumbb[i -
                                    1]) + rmu[k - 1]);
                        }
                        r *= Math.exp(-(rlan[ni - 1] * Math.exp(sumbb[i - 1])
                                + rmu[ni - 1]) * (t[ni] - ti));
                        numberOfBiopsy = k - 1;
                        for(j = ni + 1; j <= numberOfBiopsy; ++j){
                            if(t[j - 1] >= 50.0){
                                r *= Math.exp(-(rlan[j - 1] * Math.exp(sumbb[
                                        i + 107]) + rmu[j - 1]) * (t[
                                        j] - t[j - 1]));
                            } else {
                                r *= Math.exp(-(rlan[j - 1] * Math.exp(sumbb[
                                        i - 1]) + rmu[j - 1]) * (t[j]
                                        - t[j - 1]));
                            }
                        }
                        abs[i - 1] += r;
                    }
                } else {
          /* calculate risk from */
          /* intervening age int */
                    menarcheAge = ns - 1;
                    for(k = ni + 1; k <= menarcheAge; ++k){
                        r = 1.0 - Math.exp(-(rlan[k - 1] * Math.exp(sumbb[i -
                                1]) + rmu[k - 1]) * (t[k] - t[k -
                                1]));
                        r = r * rlan[k - 1] * Math.exp(sumbb[i - 1]) /
                                (rlan[k - 1] * Math.exp(sumbb[i - 1]) +
                                        rmu[k - 1]);
                        r *= Math.exp(-(rlan[ni - 1] * Math.exp(sumbb[i - 1])
                                + rmu[ni - 1]) * (t[ni] - ti));
                        numberOfBiopsy = k - 1;
                        for(j = ni + 1; j <= numberOfBiopsy; ++j){
                            r *= Math.exp(-(rlan[j - 1] * Math.exp(sumbb[i -
                                    1]) + rmu[j - 1]) * (t[j] - t[
                                    j - 1]));
                        }
                        abs[i - 1] += r;
                    }
                }
            }
        }


        retval = abs[i - 1];
        return retval;
    }

}
