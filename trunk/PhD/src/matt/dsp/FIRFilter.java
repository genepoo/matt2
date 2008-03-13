package matt.dsp;


public class FIRFilter {

    public static final int LP = 1;
    public static final int BP = 2;
    public static final int HP = 3;
    static int taps = 35;
    float[] a = new float[taps];
    int filterType;

    public void setFilterType(int ft) {
        filterType = ft;
    }

    public float[] filter(float[] ip) {

        float[] paddedip = new float[ip.length + a.length];
        int nSamples = paddedip.length;
        float[] x = new float[taps];
        float[] op = new float[nSamples];
        float y;
        
        for (int i = 0 ; i < a.length ; i ++)
        {
            paddedip[i] = ip[0];
        }
        for (int i = a.length ; i < paddedip.length ; i ++)
        {
            paddedip[i] = ip[i - a.length];
        }

        switch (filterType) {
            case LP:
                // 34th order Kaiser LP filter
                // 0 - 1 kHz passband, 8k samples/s
                a[0] =	0.020934949f;
                a[1] =	0.022163745f;
                a[2] =	0.023352664f;
                a[3] =	0.02449582f;
                a[4] =	0.025587523f;
                a[5] =	0.026622318f;
                a[6] =	0.027594987f;
                a[7] =	0.028500626f;
                a[8] =	0.02933464f;
                a[9] =	0.030092772f;
                a[10] =	0.03077116f;
                a[11] =	0.03136633f;
                a[12] =	0.031875215f;
                a[13] =	0.03229521f;
                a[14] =	0.03262414f;
                a[15] =	0.03286031f;
                a[16] =	0.033002503f;
                a[17] =	0.033049982f;
                a[18] =	0.033002503f;
                a[19] =	0.03286031f;
                a[20] =	0.03262414f;
                a[21] =	0.03229521f;
                a[22] =	0.031875215f;
                a[23] =	0.03136633f;
                a[24] =	0.03077116f;
                a[25] =	0.030092772f;
                a[26] =	0.02933464f;
                a[27] =	0.028500626f;
                a[28] =	0.027594987f;
                a[29] =	0.026622318f;
                a[30] =	0.025587523f;
                a[31] =	0.02449582f;
                a[32] =	0.023352664f;
                a[33] =	0.022163745f;
                a[34] =	0.020934949f;
                break;

            case BP:
                // 34th order Kaiser BP filter
                // 1 kHz centre frequency, 8k samples/s
                a[0]  = a[34] = -6.238957E-3f;
                a[1]  = a[33] =  0.0f;
                a[2]  = a[32] =  1.389077E-3f;
                a[3]  = a[31] = -7.818848E-3f;
                a[4]  = a[30] = -2.251760E-2f;
                a[5]  = a[29] = -2.874291E-2f;
                a[6]  = a[28] = -1.432263E-2f;
                a[7]  = a[27] =  1.935535E-2f;
                a[8]  = a[26] =  5.417910E-2f;
                a[9]  = a[25] =  6.508222E-2f;
                a[10] = a[24] =  3.747954E-2f;
                a[11] = a[23] = -1.967865E-2f;
                a[12] = a[22] = -7.569528E-2f;
                a[13] = a[21] = -9.558527E-2f;
                a[14] = a[20] = -6.276540E-2f;
                a[15] = a[19] =  8.218622E-3f;
                a[16] = a[18] =  7.842754E-2f;
                a[17] =          1.074919E-1f;
                break;

            case HP:
                // 34th order Kaiser HP filter
                // 2 - 4 kHz passband, 8k samples/s
                a[0]  = a[34] = -3.671040E-4f;
                a[1]  = a[33] = -1.433603E-3f;
                a[2]  = a[32] = -2.698704E-4f;
                a[3]  = a[31] =  3.431724E-3f;
                a[4]  = a[30] =  3.629797E-3f;
                a[5]  = a[29] = -3.950445E-3f;
                a[6]  = a[28] = -1.020383E-2f;
                a[7]  = a[27] = -1.033758E-3f;
                a[8]  = a[26] =  1.707373E-2f;
                a[9]  = a[25] =  1.517157E-2f;
                a[10] = a[24] = -1.685950E-2f;
                a[11] = a[23] = -3.861338E-2f;
                a[12] = a[22] = -1.937564E-3f;
                a[13] = a[21] =  6.617677E-2f;
                a[14] = a[20] =  5.988269E-2f;
                a[15] = a[19] = -8.870710E-2f;
                a[16] = a[18] = -3.004827E-1f;
                a[17] =          5.962756E-1f;
                break;
        }

        for (int k=1; k<taps; k++)
            x[k] = 0.0f;

        for (int i=0; i<nSamples; i++) {
            x[0] = paddedip[i];
            y = 0.0f;
            for (int k=0; k<taps; k++)
                y += a[k]*x[k];
            op[i] = y;
            for(int k=taps-1; k>0; k--)
                x[k] = x[k-1];
        }

        float unpaddedop[] = new float[ip.length];
        for (int i = a.length ; i < paddedip.length; i ++)
        {
            unpaddedop[i - a.length] = op[i];
        }
        return unpaddedop;
    }
}



