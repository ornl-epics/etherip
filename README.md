Etherip
-------

A Java library for reading and writing tags on AllenBradley Control Logix or Compact Logix
PLCs via the Ethernet/IP protocol (aka DeviceNet-over-Ethernet or CIP-over-Ethernet).

This is a Java implementation of the same protocol that
https://github.com/epics-modules/ether_ip
provides in C.
Like the C implementation, the Java code is based on

 * The generic EtherNet/IP specification available
   from http://www.odva.org.
   This describes the basic CIP commands and how they
   are wrapped for TCP.
   It allows reading the device info like vendor code
   and serial number from the AllenBradley Control Logix ENET module.
 * Allen Bradley document 1756-RM005A-EN-E.pdf,
   "Logix5000 Data Access", which describes the CIP service codes
   specific to the ControlLogix 5000 series.
   It allows reading and writing tags on the controller.
   A newer version seems to be here:
   http://literature.rockwellautomation.com/idc/groups/literature/documents/pm/1756-pm020_-en-p.pdf

What this means:

 * The code can read and write tags from AllenBradley Control Logix PLCs.
   Works with older versions that had separate controller and ENET module
   as well as L8x series that combines ENET module with controller.

 * There is generic EtherNet/IP code for reading attributes of control net objects.
   You might be able to use that to access information on other types of
   EtherNet/IP devices, but as in the Control Logix example you might
   need details about additional, vendor-specific CIP service codes
   to get at the useful data.
 
See also the ICALEPCS 2001 paper "Interfacing the ControlLogix PLC over Ethernet/IP",
https://accelconf.web.cern.ch/ica01/papers/THDT002.pdf

The C implementation provides EPICS device support
for IOCs on top of the basic protocol library.
This Java implementation is currently only the basic read/write library
with unit tests to demonstrate the functionality.
 
For basic read/write, see `test/etherip/EtherIPDemo.java` or `Main.java` (called by `etherip` script) for a simple command line tool.

For a 'scan list' that reads tags all the time, but also allows writing them, see `test/etherip/scan/ScanListTest.java`


Building
--------

Build with maven:

    mvn  -DskipTests=true clean package
    
Develop in Eclipse via File, Import, Maven, Existing Maven Projects.

