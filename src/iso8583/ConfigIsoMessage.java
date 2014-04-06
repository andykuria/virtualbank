/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package iso8583;
import globalutils.ConfigInfo;
import lib.CommonLib;
/**
 *
 * @author Administrator
 */
public class ConfigIsoMessage {
    public int[] fieldsLength = new int[129];
    public int[] fieldsBinary=new int[0];
    public int[] headerFieldsLength = new int[10];
    private String instCode="";
    private int instType=0;

    public ConfigIsoMessage()
    {
        for(int i=0;i<129;i++)
        {
            fieldsLength[i]=0;
        }
        InitLengthForEachFieldInMessage();
    }
    private void InitLengthForEachFieldInMessage(){
        //Cac thanh phan du lieu cua thong diep, dem tu 0 va khong bi lech
        fieldsLength[0] = 4;   // MTI
        fieldsLength[1] = 32;  // Bitmap, cua CUP do dai lon nhat chi la 16
        fieldsLength[2] = 99;	//PAN
        fieldsLength[3] = 6;	//Processing Code
        fieldsLength[4] = 12;	//Amount Transaction
        fieldsLength[5] = 12;	//Amount Settlement
        fieldsLength[6] = 12;	//Amount Card-holder Billing
        fieldsLength[7] = 10;	//Transmission Date and Time
        fieldsLength[8] = 0;
        fieldsLength[9] = 8;	//Conversion Rate, Settlement
        fieldsLength[10] = 8;	//Conversion Rate, Card-holder Billing
        fieldsLength[11] = 6;	//System Trace Audit Number
        fieldsLength[12] = 6;	//Time, Local Transaction
        fieldsLength[13] = 4;	//Date, Local Transaction
        fieldsLength[14] = 4;	//Expiration Date
        fieldsLength[15] = 4;	//Settlement Date
        fieldsLength[16] = 4;	//Conversion Date
        fieldsLength[17] = 0;
        fieldsLength[18] = 4;	//Merchant Type
        fieldsLength[19] = 3;	//Accepting Institution Country Code
        fieldsLength[20] = 0;
        fieldsLength[21] = 0;
        fieldsLength[22] = 3;	//Point Of Service Entry Mode Code
        fieldsLength[23] = 3;	//Card Sequence Number
        fieldsLength[24] = 0;
        fieldsLength[25] = 2;	//Point of Service Conditional Code
        fieldsLength[26] = 2;	//Point of Service PIN Capture Code
        fieldsLength[27] = 0;	
        fieldsLength[28] = 0;
        fieldsLength[29] = 0;
        fieldsLength[30] = 9;
        fieldsLength[31] = 0;
        fieldsLength[32] = 99;	//Accepting Institution Identification Code
        fieldsLength[33] = 99; 	//Forwarding Institution Identification Code
        fieldsLength[34] = 0;
        fieldsLength[35] = 99;	//Track 2 - Data
        fieldsLength[36] = 999;	//Track 3 - Data
        fieldsLength[37] = 12;	//Retrieval Reference Number
        fieldsLength[38] = 6;	//Authorization Code
        fieldsLength[39] = 2;	//Response Code
        fieldsLength[40] = 0;
        fieldsLength[41] = 8;	//Card Acceptor Terminal Identification
        fieldsLength[42] = 15;	//Card Acceptor Identification Code
        fieldsLength[43] = 40;	//Card Acceptor Name/Location
        fieldsLength[44] = 99;	//Additional Response Data
        fieldsLength[45] = 99;	//Track 1 - Data
        fieldsLength[46] = 999;
        fieldsLength[47] = 999;
        fieldsLength[48] = 999;	//Additional Data Private
        fieldsLength[49] = 3;	//Currency Code Transaction
        fieldsLength[50] = 3;	//Currency Code Settlement
        fieldsLength[51] = 3;	//Currency Code Card-holder Billing
        fieldsLength[52] = 16;	//PIN data. Doi voi CUP dung 8 byte trong khi BN dung 16 byte
        fieldsLength[53] = 16;	//Security Related Control Information
        fieldsLength[54] = 999;	//Additional Amount	
        fieldsLength[55] = 0;
        fieldsLength[56] = 0;
        fieldsLength[57] = 999;	//Additional Data Private
        fieldsLength[58] = 99;
        fieldsLength[59] = 999;	//Data Inquiry
        fieldsLength[60] = 999;	//Self-determined Field
        fieldsLength[61] = 99;	//Card-holder Authentication Information
        fieldsLength[62] = 999;	//Switch Central Data
        fieldsLength[63] = 999;	//Financial Network Data
        fieldsLength[64] = 16;
        fieldsLength[65] = 0;
        fieldsLength[66] = 1;	//Settlement Code
        fieldsLength[67] = 0;    //Cua Banknetvn la 1 trong khi CUP ko su dung truong nay
        fieldsLength[68] = 0;
        fieldsLength[69] = 0;
        fieldsLength[70] = 3;	//Network Management Information Code
        fieldsLength[71] = 0;	//CUP la 0 trong khi BN la 4
        fieldsLength[72] = 0;	//CUP la 0 trong khi BN la 4
        fieldsLength[73] = 0;	//CUP la 0 trong khi BN la 6
        fieldsLength[74] = 10;	//Credit Volume
        fieldsLength[75] = 10;	//Credit Reversal Volume
        fieldsLength[76] = 10;	//Debit Volume
        fieldsLength[77] = 10;	//Debit Reversal Volume
        fieldsLength[78] = 10;	//Transfer Volume
        fieldsLength[79] = 10;	//Transfer Reversal Volume
        fieldsLength[80] = 10;	//Inquiries Volume
        fieldsLength[81] = 10;	//Authorization Volume
        fieldsLength[82] = 12;	//Processing Fee Account of Credits
        fieldsLength[83] = 0;	//Credit, Transaction Fee Amout
        fieldsLength[84] = 12;	//Processing Fee Account of Debit
        fieldsLength[85] = 0;	//Debit, Transaction Fee Amout
        fieldsLength[86] = 16;	//Credit Amount
        fieldsLength[87] = 16;	//Credit Reversal Amount
        fieldsLength[88] = 16;	//Debit Amount
        fieldsLength[89] = 16;	//Debit Reversal Amount
        fieldsLength[90] = 42;	//Original Data Element
        fieldsLength[91] = 0;
        fieldsLength[92] = 0;
        fieldsLength[93] = 0;
        fieldsLength[94] = 0;
        fieldsLength[95] = 42;	//Replacement Amount
        fieldsLength[96] = 16;	//Message Security Code, cua CUP chi la 8 
        fieldsLength[97] = 0;
        fieldsLength[98] = 0;
        fieldsLength[99] = 99;	//Settlement Institution Identification Code
        fieldsLength[100] = 99;	//Receiving Institution Identification Code
        fieldsLength[101] = 0;
        fieldsLength[102] = 99;	//From Account 
        fieldsLength[103] = 99;	//To Account
        fieldsLength[104] = 999;	//Transaciton Description
        fieldsLength[105] = 0;
        fieldsLength[106] = 0;
        fieldsLength[107] = 0;
        fieldsLength[108] = 0;
        fieldsLength[109] = 0;
        fieldsLength[110] = 0;
        fieldsLength[111] = 0;
        fieldsLength[112] = 0;
        fieldsLength[113] = 0;
        fieldsLength[114] = 0;
        fieldsLength[115] = 0;
        fieldsLength[116] = 0;
        fieldsLength[117] = 0;
        fieldsLength[118] = 0;
        fieldsLength[119] = 0;
        fieldsLength[120] = 999;
        fieldsLength[121] = 999;	//CUP Reserved
        fieldsLength[122] = 999;	//Accepting Institution Reserved
        fieldsLength[123] = 999;	//Issuer Institution Reserved
        fieldsLength[124] = 0;
        fieldsLength[125] = 0;
        fieldsLength[126] = 0;
        fieldsLength[127] = 0;
        fieldsLength[128] = 16;

        //Phan Header, chu y dem tu 0 va co do lech
        //Phan nay chi ap dung cho CUP
        headerFieldsLength[0] = 1;   // Header Length
        headerFieldsLength[1] = 1;  // Header Flag and Version
        headerFieldsLength[2] = 4;	//Message Length
        headerFieldsLength[3] = 11;	//Destination ID
        headerFieldsLength[4] = 11;	//Source ID
        headerFieldsLength[5] = 3;	//Reserved for use
        headerFieldsLength[6] = 1;	//Batch Number
        headerFieldsLength[7] = 8;	//Transaction Information
        headerFieldsLength[8] = 1;	//User Information
        headerFieldsLength[9] = 5;	//Rejected Code
    }

    //Ham nay se lay ve do dai cua cac truong theo tung to chuc ket noi
    public int[] getFieldsLength(){
        //ConfigIsoMessage.InitLengthForEachFieldInMessage();
        int[] lengthF = new int[fieldsLength.length];
        for(int i= 0; i < fieldsLength.length; i++)
        {
                lengthF[i] = fieldsLength[i];
        }		
        

        return lengthF;
    }

    public void initFromCfg(ConfigInfo pisocfg)
    {
        for(int i=0;i<129;i++)
        {
            fieldsLength[i]= pisocfg.getIntValue("CFG", String.valueOf(i) );
        }
        
        if (!pisocfg.getValue("CFG", "BINARY_FIELDS").equals(""))
        {
            String[] fieldsBinaryStr =pisocfg.getValue("CFG", "BINARY_FIELDS").split(" ");
            fieldsBinary=new int[fieldsBinaryStr.length];
            for(int iB=0;iB<fieldsBinaryStr.length;iB++)
            {
                fieldsBinary[iB]=CommonLib.valueOf(fieldsBinaryStr[iB]);
            }
        }
        instCode=pisocfg.getValue("CFG", "CODE");
        instType=pisocfg.getIntValue("CFG", "TYPE");
        
    }

    public String getInstCode() {
        return instCode;
    }

    public int getInstType() {
        return instType;
    }
    
    
    
    public boolean checkBinaryField(int pindexoffield)
    {
        for(int i=0;i<fieldsBinary.length;i++)
        {
            if (fieldsBinary[i]==pindexoffield) return true;
        }
        return false;
        
    }
    
    

}
