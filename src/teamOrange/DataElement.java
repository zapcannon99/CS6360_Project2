package teamOrange;

import java.util.Calendar;
import java.util.Date;
import static teamOrange.Mapper.*;

public class DataElement {
    int datatype;

    Long value_long;
    Double value_double;
    String value_string;

    public DataElement(byte value) { datatype = typeCodeTinyInt; value_long = (long)value; }
    public DataElement(short value) { datatype = typeCodeSmallInt; value_long = (long)value; }
    public DataElement(int value) { datatype = typeCodeInt; value_long = (long)value; }
    public DataElement(long value) { datatype = typeCodeBigInt; value_long = value; }
    public DataElement(float value) { datatype = typeCodeReal; value_double = (double)value; }
    public DataElement(double value) { datatype = typeCodeDouble; value_double = value; }
    public DataElement(Calendar value) {
        if(value.HOUR + value.MINUTE + value.SECOND == 0){
            datatype = typeCodeDate;
        } else {
            datatype = typeCodeDateTime;
        }
        value_long = value.getTimeInMillis();
    }
    public DataElement(String value) {
        int length = typeCodeText + value.length();
        if(length > 0xff){
            // Need to raise error
        } else {
            datatype = length;
            value_string = value;
        }
    }
    // if one passes a null, for object, then one must pass the size of the null
    public DataElement(Object o, byte size){
        if(o != null){
            //throw an error if not null
        } else {
            switch(size){
                case 1:
                    datatype = typeCodeNull1B;
                    break;
                case 2:
                    datatype = typeCodeNull2B;
                    break;
                case 4:
                    datatype = typeCodeNull4B;
                    break;
                case 8:
                    datatype = typeCodeNull8B;
                    break;
                default:
                    //throw an error
                    break;
            }
        }
        ((Integer)o)
    }

    public Object getValue(){
        switch(datatype) {
            case typeCodeNull1B:
                Byte b = null;
                return b;
            case typeCodeNull2B:
                Short s = null;
                return s;
            case typeCodeNull4B:
                Integer i = null;
                return i;
            case typeCodeNull8B:
                Long l = null;
                return l;
            case typeCodeTinyInt:
                // return (byte)long_value // doesn't work in this version of Java
                return value_long.byteValue();
            case typeCodeSmallInt:
                return value_long.shortValue();
            case typeCodeInt:
                return return value_long.intValue();;
            case typeCodeBigInt:
                return value_long;
            case typeCodeReal:
                return value_double.floatValue();
            case typeCodeDouble:
                return value_double;
            case typeCodeDateTime:
            case typeCodeDate:
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(value_long);
            default:
                // Case of typeCodeText
                return value_string;
                break;
        }
    }

}
