/*******************************************************************************
 * Copyright (c) 2017 NETvisor Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip.data;

import java.util.Arrays;
import java.util.HashMap;

/**
 * @see CIP_Vol1_3.3: AppendixB-Status Codes Table 3-5.29 Connection Manager Service Request Error Codes
 * @author László Pataki
 */

public class CipException extends Exception
{
    private static final long serialVersionUID = 2333733809292443987L;

    private static HashMap<Integer, String[]> GENERAL_STATUSES = new HashMap<>();
    static
    {
        //@formatter:off
		GENERAL_STATUSES.put(0x01, new String[]{"Connection failure","A connection related service failed along the connection path."});
		GENERAL_STATUSES.put(0x02, new String[]{"Resource unavailable","Resources needed for the object to perform the requested service were unavailable."});
		GENERAL_STATUSES.put(0x03, new String[]{"Invalid parameter value","See Status Code 0x20","which is the preferred value to use for this condition."});
		GENERAL_STATUSES.put(0x04, new String[]{"Path segment error","The path segment identifier or the segment syntax was not understood by the processing node. Path processing shall stop when a path segment error is encountered."});
		GENERAL_STATUSES.put(0x05, new String[]{"Path destination unknown","The path is referencing an object class, instance or structure element that is not known or is not contained in the processing node. Path processing shall stop when a path destination unknown error is encountered."});
		GENERAL_STATUSES.put(0x06, new String[]{"Partial transfer","Only part of the expected data was transferred."});
		GENERAL_STATUSES.put(0x07, new String[]{"Connection lost","The messaging connection was lost."});
		GENERAL_STATUSES.put(0x08, new String[]{"Service not supported","The requested service was not implemented or was not defined for this Object Class/Instance."});
		GENERAL_STATUSES.put(0x09, new String[]{"Invalid attribute value","Invalid attribute data detected."});
		GENERAL_STATUSES.put(0x0A, new String[]{"Attribute list error","An attribute in the Get_Attribute_List or Set_Attribute_List response has a non-zero status."});
		GENERAL_STATUSES.put(0x0B, new String[]{"Already in requested mode/state"," The object is already in the mode/state being requested by the service."});
		GENERAL_STATUSES.put(0x0C, new String[]{"Object state conflict","The object cannot perform the requested service in its current mode/state."});
		GENERAL_STATUSES.put(0x0D, new String[]{"Object already exists","The requested instance of object to be created already exists."});
		GENERAL_STATUSES.put(0x0E, new String[]{"Attribute not settable","A request to modify a non-modifiable attribute was received."});
		GENERAL_STATUSES.put(0x0F, new String[]{"Privilege violation","A permission/privilege check failed."});
		GENERAL_STATUSES.put(0x10, new String[]{"Device state conflict","The device’s current mode/state prohibits the execution of the requested service."});
		GENERAL_STATUSES.put(0x11, new String[]{"Reply data too large","The data to be transmitted in the response buffer is larger than the allocated response buffer."});
		GENERAL_STATUSES.put(0x12, new String[]{"Fragmentation of a primitive value","The service specified an operation that is going to fragment a primitive data value, i.e. half a REAL data type."});
		GENERAL_STATUSES.put(0x13, new String[]{"Not enough data","The service did not supply enough data to perform the specified operation."});
		GENERAL_STATUSES.put(0x14, new String[]{"Attribute not supported","The attribute specified in the request is not supported."});
		GENERAL_STATUSES.put(0x15, new String[]{"Too much data","The service supplied more data than was expected."});
		GENERAL_STATUSES.put(0x16, new String[]{"Object does not exist","The object specified does not exist in the device."});
		GENERAL_STATUSES.put(0x17, new String[]{"Service fragmentation sequence not in progress","The fragmentation sequence for this service is not currently active for this data."});
		GENERAL_STATUSES.put(0x18, new String[]{"No stored attribute data","The attribute data of this object was not saved prior to the requested service."});
		GENERAL_STATUSES.put(0x19, new String[]{"Store operation failure","The attribute data of this object was not saved due to a failure during the attempt."});
		GENERAL_STATUSES.put(0x1A, new String[]{"Routing failure","request packet too large The service request packet was too large for transmission on a network in the path to the destination. The routing device was forced to abort the service."});
		GENERAL_STATUSES.put(0x1B, new String[]{"Routing failure","response packet too large The service response packet was too large for transmission on a network in the path from the destination. The routing device was forced to abort the service."});
		GENERAL_STATUSES.put(0x1C, new String[]{"Missing attribute list entry data","The service did not supply an attribute in a list of attributes that was needed by the service to perform the requested behavior."});
		GENERAL_STATUSES.put(0x1D, new String[]{"Invalid attribute value list","The service is returning the list of attributes supplied with status information for those attributes that were invalid."});
		GENERAL_STATUSES.put(0x1E, new String[]{"Embedded service error","An embedded service resulted in an error."});
		GENERAL_STATUSES.put(0x1F, new String[]{"Vendor specific error","A vendor specific error has been encountered. The Additional Code Field of the Error Response defines the particular error encountered. Use of this General Error Code should only be performed when none of the Error Codes presented in this table or within an Object Class definition accurately reflect the error."});
		GENERAL_STATUSES.put(0x20, new String[]{"Invalid parameter","A parameter associated with the request was invalid. This code is used when a parameter does not meet the requirements of this specification and/or the requirements defined in an Application Object Specification."});
		GENERAL_STATUSES.put(0x21, new String[]{"Write-once value or medium already written","An attempt was made to write to a write-once medium (e.g. WORM drive, PROM) that has already been written, or to modify a value that cannot be changed once established."});
		GENERAL_STATUSES.put(0x22, new String[]{"Invalid Reply Received","An invalid reply is received (e.g. reply service code does not match the request service code, or reply message is shorter than the minimum expected reply size). This status code can serve for other causes of invalid replies."});
		GENERAL_STATUSES.put(0x23, new String[]{"Buffer Overflow","The message received is larger than the receiving buffer can handle. The entire message was discarded."});
		GENERAL_STATUSES.put(0x24, new String[]{"Message Format Error","The format of the received message is not supported by the server."});
		GENERAL_STATUSES.put(0x25, new String[]{"Key Failure in path","The Key Segment that was included as the first segment in the path does not match the destination module. The object specific status shall indicate which part of the key check failed."});
		GENERAL_STATUSES.put(0x26, new String[]{"Path Size Invalid","The size of the path which was sent with the Service Request is either not large enough to allow the Request to be routed to an object or too much routing data was included."});
		GENERAL_STATUSES.put(0x27, new String[]{"Unexpected attribute in list","An attempt was made to set an attribute that is not able to be set at this time."});
		GENERAL_STATUSES.put(0x28, new String[]{"Invalid Member ID The Member ID","specified in the request does not exist in the specified Class/Instance/Attribute."});
		GENERAL_STATUSES.put(0x29, new String[]{"Member not settable","A request to modify a non-modifiable member was received."});
		GENERAL_STATUSES.put(0x2A, new String[]{"Group 2 only server general failure","This error code may only be reported by DeviceNet Group 2 Only servers with 4K or less code space and only in place of Service not supported, Attribute not supported and Attribute not settable."});
		GENERAL_STATUSES.put(0x2B, new String[]{"Unknown Modbus Error","A CIP to Modbus translator received an unknown Modbus Exception Code."});
        GENERAL_STATUSES.put(0xFF, new String[]{"See extended code"});
		//@formatter:on
    }

    private static HashMap<Integer, String[]> EXTENDED_STATUSES = new HashMap<>();
    static
    {
        //@formatter:off
		EXTENDED_STATUSES.put(0x0107, new String[]{"TARGET CONNECTION NOT FOUND","This extended status code shall be returned in response to the forward_close request, when the connection that is to be closed is not found at the target node. This extended status code shall only be returned by a target node. Routers shall not generate this extended status code. If the specified connection is not found at the intermediate node, the close request shall still be forwarded using the path specified in the Forward_Close request."});
		EXTENDED_STATUSES.put(0x0204, new String[]{"UNCONNECTED REQUEST TIMED OUT","The Unconnected Request Timed Out error shall occur when the UCMM times out before a reply is received. This may occur for an Unconnected_Send, Forward_Open, or Forward_Close service. This typically means that the UCMM has tried a link specific number of times using a link specific retry timer and has not received an acknowledgement or reply. This may be the result of congestion at the destination node or may be the result of a node not being powered up or present. This extended status code shall be returned by the originating node or any intermediate node."});
		EXTENDED_STATUSES.put(0x0312, new String[]{"LINK ADDRESS NOT VALID","Link Address specified in Port Segment Not Valid This extended status code is the result of a port segment that specifies a link address that is not valid for the target network type. This extended status code shall not be used for link addresses that are valid for the target network type but do not respond."});
		EXTENDED_STATUSES.put(0x0318, new String[]{"LINK ADDRESS TO SELF INVALID","Under some conditions (depends on the device), a link address in the Port Segment which points to the same device (loopback to yourself) is invalid."});
		// 1756-PM020A0-EN-P  p. 25
		EXTENDED_STATUSES.put(0x2107, new String[]{"CIP Write Tag error", "Tag type used in request does not match the target tag's data type"});
		//@formatter:on
    }

    private final int statusCode;

    private String statusName;

    private String statusDescription;

    private final int extendedCode;

    private String extendedStatusName;

    private String extendedStatusDescription;
    
    /** Construct CIP exception that attempts to decode the numeric error
     * 
     *  @param generalStatusCode General status, only using byte 0xFF
     *  @param extendedStatusCode Extended status
     */
    public CipException(final int generalStatusCode, final int extendedStatusCode)
    {
        super(String.format("Status: 0x%02X %s - Extended: 0x%02X %s",
                            generalStatusCode & 0xFF,
                            (GENERAL_STATUSES.containsKey(generalStatusCode & 0xFF)
                             ? Arrays.toString(GENERAL_STATUSES.get(generalStatusCode & 0xFF))
                             : ""
                            ),
                            extendedStatusCode,
                            (EXTENDED_STATUSES.containsKey(extendedStatusCode)
                             ? Arrays.toString(EXTENDED_STATUSES.get(extendedStatusCode))
                             : ""
                            )));

        this.statusCode = generalStatusCode & 0xFF;
        this.extendedCode = extendedStatusCode;

        if (GENERAL_STATUSES.containsKey(generalStatusCode))
        {
            this.statusName = GENERAL_STATUSES.get(this.statusCode)[0];
            this.statusDescription = GENERAL_STATUSES.get(this.statusCode)[1];
        }
        if (generalStatusCode >= 0x2C && generalStatusCode <= 0xCF)
        {
            this.statusName = "";
            this.statusDescription = "Reserved by CIP for future extensions";
        }
        if (generalStatusCode >= 0xD0 && generalStatusCode <= 0xFF)
        {
            this.statusName = "Reserved for Object Class and service errors";
            this.statusDescription = "This range of error codes is to be used to indicate Object Class specific errors. Use of this range should only be performed when none of the Error Codes presented in this table accurately reflect the error that was encountered.";
        }

        if (EXTENDED_STATUSES.containsKey(extendedStatusCode))
        {
            this.extendedStatusName = EXTENDED_STATUSES.get(extendedStatusCode)[0];
            this.extendedStatusDescription = EXTENDED_STATUSES.get(extendedStatusCode)[1];
        }
        else
        {
            this.extendedStatusName = "Unknown";
            this.extendedStatusDescription = "Unknown";
        }
    }

    public int getStatusCode()
    {
        return this.statusCode;
    }

    public String getStatusName()
    {
        return this.statusName;
    }

    public String getStatusDescription()
    {
        return this.statusDescription;
    }

    public int getExtendedStatusCode()
    {
        return this.extendedCode;
    }

    public String getExtendedStatusName()
    {
        return this.extendedStatusName;
    }

    public String getExtendedStatusDescription()
    {
        return this.extendedStatusDescription;
    }
}
