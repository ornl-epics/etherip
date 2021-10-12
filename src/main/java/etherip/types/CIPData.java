/*******************************************************************************
 * Copyright (c) 2012 UT-Battelle, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.types;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import etherip.protocol.Connection;

/**
 * ControlNet data types
 * <p>
 * Spec. 5 p.3
 * <p>
 * Raw CIP data is kept in a <code>byte[]</code>. This class can decode the data or manipulate it.
 * <p>
 * Note that all operations that 'set' the value require that the CIPData already holds the respective type. For example, setting a CIPData of type REAL to an integer value will still result in a
 * REAL, not change the type to INT. Setting a CIPData of type INT to a floating point value will truncate the floating point to an integer, since the CIPData remains an INT. Only CIPData with a
 * string-containing STRUCT can be set to a string.
 *
 * @author Kay Kasemir
 */
@SuppressWarnings("nls")
final public class CIPData
{
    public static enum Type
    {
        BOOL(0x00C1, 1),
        SINT(0x00C2, 1),
        INT(0x00C3, 2),
        DINT(0x00C4, 4),
        REAL(0x00CA, 4),
        BITS(0x00D3, 4),
        // Order of enums matter: BITS is the last numeric type (not-string)
        STRUCT(0x02A0, 0),

        /**
         * Experimental: The ENET doc just shows several structures for TIMER, COUNTER, CONTROL and indicates that the T_CIP_STRUCT = 0x02A0 is followed by two more bytes, shown as "?? ??". Looks like
         * for strings, those are always 0x0FCE, followed by DINT length, characters and more zeroes
         */
        STRUCT_STRING(0x0FCE, 0);

        final private short code;
        final private int element_size;

        final private static Map<Short, Type> reverse;

        static
        {
            reverse = new HashMap<>();
            for (final Type t : EnumSet.allOf(Type.class))
            {
                reverse.put(t.code, t);
            }
        }

        public static Type forCode(final short code) throws Exception
        {
            final Type t = reverse.get(code);
            if (reverse == null)
            {
                throw new Exception(
                        "Unknown CIP type code 0x" + Integer.toHexString(code));
            }
            return t;
        }

        private Type(final int code, final int element_size)
        {
            this.code = (short) code;
            this.element_size = element_size;
        }

        @Override
        final public String toString()
        {
            return this.name() + String.format(" (0x%04X)", this.code);
        }
    };

    /** Data type */
    final private Type type;

    /** Number of elements (i.e. number of array elements, not bytes) */
    final private short elements;

    /** Raw data, not including type code or element count */
    final private ByteBuffer data;

    /**
     * Initialize empty CIP data
     *
     * @param type
     *            Data {@link Type}
     * @param elements
     *            Number of elements
     * @throws Exception
     *             when type is not handled
     */
    public CIPData(final Type type, final int elements) throws Exception
    {
        switch (type)
        {
        case BOOL:
        case SINT:
        case INT:
        case DINT:
        case BITS:
        case REAL:
            this.data = ByteBuffer.allocate(type.element_size * elements);
            this.data.order(Connection.BYTE_ORDER);
            this.type = type;
            this.elements = (short) elements;
            break;
        default:
            throw new Exception("Type " + type + " not handled");
        }
    }

    /**
     * Initialize
     *
     * @param type
     *            Data type
     * @param data
     *            Bytes that contain the raw CIP data
     * @throws Exception
     *             when data is invalid
     */
    public CIPData(final Type type, final byte[] data) throws Exception
    {
        this.type = type;
        this.data = ByteBuffer.allocate(data.length);
        this.data.order(Connection.BYTE_ORDER);
        this.data.put(data);
        this.elements = this.determineElementCount();
    }

    /** @return Number of elements */
    final private short determineElementCount() throws Exception
    {
        switch (this.type)
        {
        case BOOL:
        case SINT:
        case INT:
        case DINT:
        case BITS:
        case REAL:
            return (short) (this.data.capacity() / this.type.element_size);
        case STRUCT:
        {
            return 1; // We do not know without byte packing details but at least 1. 
        }
        default:
            throw new Exception("Type " + this.type + " not handled");
        }
    }

    /** @return CIP data type */
    final public Type getType()
    {
        return this.type;
    }

    /**
     * @return Number of elements (numbers in array). Always 1 for String
     */
    final public int getElementCount()
    {
        return this.elements;
    }

    /** @return <code>true</code> if data type is numeric, <code>false</code> for string */
    final public boolean isNumeric()
    {
        return this.type.ordinal() <= Type.BITS.ordinal();
    }

    /**
     * Read CIP data as number
     *
     * @param index
     *            Element index 0, 1, ...
     * @return Numeric value of requested element
     * @throws Exception
     *             on error, if data is not numeric
     * @throws IndexOutOfBoundsException
     *             if index is invalid
     */
    final synchronized public Number getNumber(final int index)
            throws Exception, IndexOutOfBoundsException
    {
        switch (this.type)
        {
        case BOOL:
        case SINT:
            return new Byte(this.data.get(this.type.element_size * index));
        case INT:
            return new Short(
                    this.data.getShort(this.type.element_size * index));
        case DINT:
        case BITS:
            return new Integer(
                    this.data.getInt(this.type.element_size * index));
        case REAL:
            return new Float(
                    this.data.getFloat(this.type.element_size * index));
        default:
            throw new Exception("Cannot retrieve Number from " + this.type);
        }
    }

    /**
     * Read CIP data as string
     *
     * @return {@link String}
     * @throws Exception
     *             if data does not contain a string
     */
    final synchronized public String getString() throws Exception
    {
        if (this.type != Type.STRUCT)
        {
            throw new Exception(
                    "Type " + this.type + " does not contain string");
        }
        final short code = this.data.getShort(0);
        final Type el_type = Type.forCode(code);
        if (el_type != Type.STRUCT_STRING)
        {
            throw new Exception(
                    "No string, structure element is of type " + this.type);
        }
        final int len = this.data.getInt(2);
        final byte[] chars = new byte[len];
        for (int i = 0; i < len; ++i)
        {
            chars[i] = this.data.get(6 + i);
        }
        return new String(chars);
    }

    /**
     * Set CIP data
     *
     * @param index
     *            Element index 0, 1, ...
     * @param value
     *            Numeric value to write to that element
     * @throws Exception
     *             on invalid data type
     * @throws IndexOutOfBoundsException
     *             if index is invalid
     */
    final synchronized public void set(final int index, final Number value)
            throws Exception, IndexOutOfBoundsException
    {
        switch (this.type)
        {
        case BOOL:
        case SINT:
            this.data.put(this.type.element_size * index, value.byteValue());
            break;
        case INT:
            this.data.putShort(this.type.element_size * index,
                    value.shortValue());
            break;
        case DINT:
        case BITS:
            this.data.putInt(this.type.element_size * index, value.intValue());
            break;
        case REAL:
            this.data.putFloat(this.type.element_size * index,
                    value.floatValue());
            break;
        default:
            throw new Exception(
                    "Cannot set type " + this.type + " to a number");
        }
    }

    /**
     * Write CIP data as string
     *
     * @param text
     *            {@link String}
     * @throws Exception
     *             if data does not contain a string
     */
    final synchronized public void setString(final String text) throws Exception
    {
        if (this.type != Type.STRUCT)
        {
            throw new Exception(
                    "Type " + this.type + " does not contain string");
        }
        this.data.putShort(0, Type.STRUCT_STRING.code);

        // Try to fit the text,
        // but limit it to size of buffer,
        // starting at the offset for the text
        // (2 byte STRUCT_STRING, 4 byte length)
        // and allow for the final '\0' byte
        final int len = Math.min(text.length(), this.data.capacity() - 6 - 1);
        this.data.putInt(2, len);

        final byte[] chars = text.getBytes();
        for (int i = 0; i < len; ++i)
        {
            this.data.put(6 + i, chars[i]);
        }
        this.data.put(6 + len, (byte) 0);
    }

    /** @return size if bytes of the encoded data */
    final public int getEncodedSize()
    {
        // Type, Elements, raw data
        return 2 + 2 + this.data.capacity();
    }

    /**
     * Encode CIP data bytes into buffer
     *
     * @param buf
     *            {@link ByteBuffer} where data should be placed
     * @throws Exception
     *             on error
     */
    final synchronized public void encode(final ByteBuffer buf) throws Exception
    {
        buf.putShort(this.type.code);
        // STRUCT already contains structure detail, elements etc.
        // For other types, add the element count
        if (this.type == Type.STRUCT)
        {
            this.data.clear();
            final short struct_detail = this.data.getShort();
            if (struct_detail != Type.STRUCT_STRING.code)
            {
                throw new Exception("Can only encode STRUCT_STRING, got 0x"
                        + Integer.toHexString(struct_detail));
            }
            // The data buffer contains the string as _read_:
            // STRUCT, STRUCT_STRING, length, chars.
            // It needs to be written as
            // STRUCT, STRUCT_STRING, _elements_, length, chars.
            buf.putShort(struct_detail);
            buf.putShort(this.elements);
            // Copy length, chars from data into buf
            buf.put(this.data);
        }
        else
        {
            buf.putShort(this.elements);
            buf.put(this.data.array());
        }
    }

    /** @return String representation for debugging */
    @Override
    final synchronized public String toString()
    {
        final StringBuilder result = new StringBuilder();
        result.append("CIP_").append(this.type).append(": ");
        final ByteBuffer buf = this.data.asReadOnlyBuffer();
        buf.order(this.data.order());
        buf.clear();
        switch (this.type)
        {
        case BOOL:
        case SINT:
        {
            final byte[] values = new byte[this.elements];
            buf.get(values);
            result.append(Arrays.toString(values));
            break;
        }
        case INT:
        {
            final short[] values = new short[this.elements];
            for (int i = 0; i < this.elements; ++i)
            {
                values[i] = buf.getShort();
            }
            result.append(Arrays.toString(values));
            break;
        }
        case DINT:
        case BITS:
        {
            final int[] values = new int[this.elements];
            for (int i = 0; i < this.elements; ++i)
            {
                values[i] = buf.getInt();
            }
            result.append(Arrays.toString(values));
            break;
        }
        case REAL:
        {
            final float[] values = new float[this.elements];
            for (int i = 0; i < this.elements; ++i)
            {
                values[i] = buf.getFloat();
            }
            result.append(Arrays.toString(values));
            break;
        }
        case STRUCT:
        {
            final short code = buf.getShort();
            final Type el_type;
            try
            {
                el_type = Type.forCode(code);
            }
            catch (final Exception ex)
            {
                result.append("Structure element with type code 0x"
                        + Integer.toHexString(code));
                break;
            }
            if (el_type == Type.STRUCT_STRING)
            {
                result.append(Type.STRUCT_STRING).append(" ");
                final int len = buf.getInt();
                final byte[] chars = new byte[len];
                buf.get(chars);
                final String value = new String(chars);
                result.append("'").append(value).append("', len " + len);
            }
            else
            {
                result.append("Structure element of type " + this.type);
            }
            break;
        }
        default:
            result.append("Unknown Type " + this.type);
        }
        return result.toString();
    }
}
