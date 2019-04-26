/**
 * This class helps provide a way of keeping together the datatype and value. If one can think of a better method, let me know.
 *
 * Basically, you just pass whatever you need into DataElement and it should handle it for you
 * Then if you want to get the value out, .getValue()
 * There is also a helper function called sizeof(), dunno if we need it though
 *
 *
 * NEEDS TO BE REWORKED FOR NEW TYPECODES IN MAPPING
 */

package teamOrange;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import static teamOrange.Mapper.*;

public class DataElement {
    int datatype;

    Long value_long;
    Double value_double;
    String value_string;
    ArrayList value_array;

    public DataElement(byte value) { datatype = typeCodeTinyInt; value_long = (long)value; }
    public DataElement(short value) { datatype = typeCodeSmallInt; value_long = (long)value; }
    public DataElement(int value) { datatype = typeCodeInt; value_long = (long)value; }
    public DataElement(long value) { datatype = typeCodeBigInt; value_long = value; }
    //public DataElement(float value) { datatype = typeCodeReal; value_double = (double)value; }
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
        if(value == null){
            datatype = typeCodeNull;
        } else {
            datatype = typeCodeText;
        }
        int length = typeCodeText + value.length(); //why typeCodeText + value.length()???
        if(length > 0xff){
            // Need to raise error
        } else {
            datatype = length;
            value_string = value;
        }
    }
    public DataElement(ArrayList value) {
        if(value == null){
            datatype = typeCodeNull;
        } else {

        }
        int length = value.size();
        datatype = length;
        value_array = value;
    }
    public DataElement(Object value, byte dataType) {
        switch(dataType){
            case Mapper.typeCodeNull:
                //figure this out, need to index null set attributes?
                break;
            case Mapper.typeCodeTinyInt:
                byte tinyintval = (Byte) value;
                new DataElement(tinyintval);
                break;
            case Mapper.typeCodeSmallInt:
                short smallval = (Short) value;
                new DataElement(smallval);
                break;
            case Mapper.typeCodeInt:
                int intval = (Integer) value;
                new DataElement(intval);
                break;
            case Mapper.typeCodeBigInt:
                long bigintval = (Long) value;
                new DataElement(bigintval);
                break;
            case Mapper.typeCodeDouble:
                long doubleval = (Long) value;
                new DataElement(doubleval);
                break;
            case Mapper.typeCodeYear:
                byte yearval = (Byte) value;
                new DataElement(yearval);
                break;
            case Mapper.typeCodeTime:
                int timeval = (Integer) value;
                new DataElement(timeval);
                break;
            case Mapper.typeCodeDateTime:
                long datetimeval = (Long) value;
                new DataElement(datetimeval);
                break;
            case Mapper.typeCodeDate:
                long dateval = (Long) value;
                new DataElement(dateval);
                break;
            default: //String
                String stringval = (String) value;
                new DataElement(stringval);
                break;
        }
    }

    public Object getValue(){
        switch(datatype) {
            case typeCodeNull:
                return null;
            case typeCodeTinyInt:
                // return (byte)long_value // doesn't work in this version of Java
                return value_long.byteValue();
            case typeCodeSmallInt:
                return value_long.shortValue();
            case typeCodeInt:
                return value_long.intValue();;
            case typeCodeBigInt:
                return value_long;
//            case typeCodeReal:
//                return value_double.floatValue();
            case typeCodeDouble:
                return value_double;
            case typeCodeDateTime:
            case typeCodeDate:
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(value_long);
            case typeCodeText:
                return value_string;
            default:
                // Case of typeCodeArray
                return value_array;
            break;
        }
    }

    public int sizeof(){
        switch(datatype){
            case typeCodeNull1B:
            case typeCodeTinyInt:
            case typeCodeNull2B:
            case typeCodeSmallInt:
            case typeCodeNull4B:
            case typeCodeReal:
            case typeCodeInt:
            case typeCodeNull8B:
            case typeCodeBigInt:
            case typeCodeDouble:
            case typeCodeDateTime:
            case typeCodeDate:
            case typeCodeText:
        }
    }
}
