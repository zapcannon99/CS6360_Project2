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
    public int getDatatye() { return datatype; }
    public void setDatatype(int typeCode) { datatype = typeCode; }

    Long value_long;
    Double value_double;
    String value_string;
    ArrayList value_array;

    /**
     * Use this constructor for typeCodeNull. However, you can also use this constructor to manually make a DataElement;
     */
    public DataElement(){ datatype = typeCodeNull; }
    public DataElement(byte value) { datatype = typeCodeTinyInt; value_long = (long)value; }
    public DataElement(short value) { datatype = typeCodeSmallInt; value_long = (long)value; }
    public DataElement(int value) { datatype = typeCodeInt; value_long = (long)value; }
    public DataElement(long value) { datatype = typeCodeBigInt; value_long = value; }
    public DataElement(float value) { datatype = typeCodeDouble; value_double = (double)value; }
    public DataElement(double value) { datatype = typeCodeDouble; value_double = value; }

    /**
     * Do NOT use this constructor to create a DataElement for typeCodeYear. Use DataElementYear instead to
     * differentiate between year and byte.
     * @param value
     */
    public DataElement(Calendar value) {
        if(value.HOUR + value.MINUTE + value.SECOND == 0){
            datatype = typeCodeDate;
        } else {
            if(value.getTimeInMillis() < 86400000){
                datatype = typeCodeTime;
            } else {
                datatype = typeCodeDateTime;
            }
        }
        value_long = value.getTimeInMillis();
    }

    /**
     * Use this to make a DataElement of type typeCodeTime
     * @param time Calendar time
     * @return
     */
    public static DataElement DataElementTime(Calendar time){
        DataElement e = new DataElement(time.getTimeInMillis());
        e.setDatatype(typeCodeTime);
        return e;
    }

    /**
     * Use this to create a new DataElement of typeCodeYear. If a value that can be represented by a byte is passed,
     * then it will treat it as a offset from year 2000. All other numbers will be checked and then readjusted
     * @param year If year is within an unsigned byte, then year will be treated as the offset. Otherwise, year
     *             will be treated as the actual year, and then checked for difference from 2000
     * @return  returns a DataElement of typeCodeYear
     */
    public static DataElement DataElementYear(int year){
        if(year >= -128 && year < 128){
            DataElement e = new DataElement((byte)year);
            e.setDatatype(typeCodeYear);
            return e;
        } else {
            int difference = year - 2000;
            if(difference >= -128 && difference > 127){
                DataElement e = new DataElement(difference);
                e.setDatatype(typeCodeYear);
                return e;
            } else {
                // throw new Exception("ERROR: the given year is out of bounds of limits.");
                System.out.println("ERROR: the given year is out of bounds of limits.");
                return null;
            }
        }
    }

    public DataElement(String value) {
        if(value == null){
            datatype = typeCodeNull;
        } else {
            int length = typeCodeText + value.length();
            if(length > 0xff){
                //throw new Exception("ERROR: size of string exceeds max size allowed for database");
                System.out.println("ERROR: size of string exceeds max size allowed for database");
            } else {
                datatype = length;
                value_string = value;
            }
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

    public Object getValue() {
        switch (datatype) {
            case typeCodeNull:
                return null;
            case typeCodeTinyInt:
                // return (byte)long_value // doesn't work in this version of Java
                return value_long.byteValue();
            case typeCodeSmallInt:
                return value_long.shortValue();
            case typeCodeInt:
                return value_long.intValue();
            case typeCodeBigInt:
                return value_long;
            case typeCodeDouble:
                return value_double;
            case typeCodeDateTime:
            case typeCodeDate:
            case typeCodeTime:
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(value_long);
                return c;
            case typeCodeYear:
                return value_long.intValue() + 2000;
            case typeCodeText:
                return value_string;
            default:
                // Case of typeCodeArray
                return value_array;
        }
    }

    public int sizeof(){
        switch(datatype){
            case typeCodeNull:
                return 0;
            case typeCodeTinyInt:
                // return (byte)long_value // doesn't work in this version of Java
                return 1;
            case typeCodeSmallInt:
                return 2;
            case typeCodeInt:
                return 4;
            case typeCodeBigInt:
                return 8;
            case typeCodeDouble:
                return 8;
            case typeCodeTime:
                return 4;
            case typeCodeDateTime:
            case typeCodeDate:
                return 8;
            case typeCodeYear:
                return 1;
            case typeCodeText:
            default:
                return datatype - typeCodeText;
        }
    }

    /**
     * Use this to get the size of some payload (ArrayList of DataElements in bytes. It does not include any headers that may be
     * generated from said payload (for example, the bytes that specify the column type). It is the number of bytes for
     * that specific payload
     * @param payload
     * @return How many bytes the ArrayList of DataElements would take on disk.
     */
    public static int sizeof(ArrayList<DataElement> payload){
        int size = 0;
        for (DataElement e : payload) {
            size += e.sizeof();
        }
        return size;
    }
}
