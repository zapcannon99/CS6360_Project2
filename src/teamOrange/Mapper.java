package teamOrange;

public class Mapper {
    static final String prompt = "OrangeSQL> ";
    static final String version = "v0.0";
    static final String copyright = "Â©2019 Team Orange";
    static final int pageSize = 512;

    static final byte interiorIndexBTreePage= 0x02;
    static final byte interiorTableBTreePage= 0x05;
    static final byte leafIndexBTreePage= 0x0a;
    static final byte leafTableBTreePage= 0x0d;

    // Serial Type Codes
    static final byte typeCodeNull = 0x00;
    static final byte typeCodeTinyInt = 0x01;
    static final byte typeCodeSmallInt = 0x02;
    static final byte typeCodeInt = 0x03;
    static final byte typeCodeBigInt = 0x04;
    static final byte typeCodeDouble = 0x05;
    static final byte typeCodeYear = 0x06;

    static final byte typeCodeTime = 0x08;

    static final byte typeCodeDateTime = 0x0A;
    static final byte typeCodeDate = 0x0B;
    static final byte typeCodeText = 0x0C;
}
