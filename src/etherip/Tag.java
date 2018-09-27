/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package etherip;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import etherip.types.CIPData;

/**
 * Tag on the PLC
 * <p>
 * Initially, the data for the tag is read to get the original value and determine the data type. From then on, tag can be written.
 *
 * @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Tag
{
    public enum State
    {
        /** Tag is meant to be read, updating value with latest from device */
        READING,
        /** Tag has value meant to be written to the device */
        TO_BE_WRITTEN,
        /** Value of tag is being written to device right now */
        WRITING
    };

    // SYNC Notes:
    //
    // Tag is accessed by TagList which processes the read/write traffic.
    // At the same time, user code can try to read the current value,
    // or request a new value to be written.
    //
    // getState() etc. are synchronized, but code that requires
    // access to both Tag.state and Tag.value must synchronize on the whole Tag.
    // Avoid locking all the time!
    // Synchronize (this) when accessing Tag.data and/or Tag.state,
    // then unlock as soon as possible.
    //
    // Scenarios:
    //
    // - Simple Read -
    // 1) TagList reads value from device
    // 2) User reads value
    // -> Probably the most likely case. No issues.
    //
    // - Simple Write -
    // 1) User sets new value to be written
    // 2) TagList writes the value
    // -> No overlap
    //
    // - Read while writing -
    // 1) User sets new value to be written
    // 2) TagList starts writing the value (send to device)
    // 3) User reads value
    // -> Will get the value to-be-written, not the current value on the device.
    // So what?
    // 4) TagList finishes writing the value (read 'OK' response)
    // -> No overlap, but CIPData is locked in 1, 2, 3
    //
    // - Try to write while reading -
    // 1) TagList start reading the value
    // 2) User sets new value to be written
    // 3) TagList receives response
    // -> TagList notices that tag switched to write mode.
    // Will NOT update the Tag, but write as in Simple Write
    // on the next TagList.process().
    //
    // - Try to write faster than communication is processed -
    // 1) User sets new value A to be written
    // 2) TagList starts writing the value A
    // 3) User sets yet another value B to be written
    // At this point, MRChipWriteProtocol could be using the same data!
    // CIPData is synchronized, so it will write either A or B, depending
    // on detailed timing.
    // TagList finishes writing the first value (A or B)
    // TagList starts writing the value B
    // TagList finishes writing the value B

    /** Tag name */
    final private String name;

    /** Current value, or the value to be written */
    private CIPData data = null;

    /** State */
    private State state = State.READING;

    /** Listeners */
    final private List<TagListener> listeners = new CopyOnWriteArrayList<>();

    /**
     * Initialize
     *
     * @param name
     *            Tag name
     */
    public Tag(final String name)
    {
        this.name = name;
    }

    /** @return Tag name */
    public String getName()
    {
        return this.name;
    }

    /**
     * @param listener
     *            Listener to add
     */
    public void addListener(final TagListener listener)
    {
        this.listeners.add(listener);
        // Perform initial update if there's already known value
        synchronized (this)
        {
            if (this.data != null)
            {
                listener.tagUpdate(this);
            }
        }
    }

    /**
     * @param listener
     *            Listener to remove
     * @throws IllegalStateException
     *             if listener is not known
     */
    public void removeListener(final TagListener listener)
    {
        if (!this.listeners.remove(listener))
        {
            throw new IllegalStateException("Unknown listener");
        }
    }

    /** @return Tag state */
    public synchronized State getState()
    {
        return this.state;
    }

    /**
     * Update state tag
     * <p>
     * To be called by {@link TagList}
     *
     * @param state
     *            {@link State}
     */
    synchronized void setState(final State state)
    {
        this.state = state;
    }

    /**
     * Get current value of the tag.
     * <p>
     * This is either the most recent value read from the device, or the value that is about to be written to the device.
     *
     * @return {@link CIPData}
     */
    synchronized public CIPData getData()
    {
        return this.data;
    }

    /**
     * Update value of the tag with data read from device
     * <p>
     * To be called by {@link TagList}
     *
     * @param data
     *            {@link CIPData}
     */
    synchronized void setData(final CIPData data)
    {
        this.data = data;
        for (final TagListener listener : this.listeners)
        {
            if (data == null){
                listener.tagError(this);
            }
            else{
                listener.tagUpdate(this);
            }
        }
    }

    /**
     * Set CIP data to be written to the device
     *
     * @param index
     *            Element index 0, 1, ...
     * @param value
     *            Numeric value to write to that element
     * @throws IndexOutOfBoundsException
     *             if index is invalid
     * @throws IllegalStateException
     *             if tag has never been read, so data type is unknown
     * @throws Exception
     *             on invalid data type
     * @throws IndexOutOfBoundsException
     *             if index is invalid
     */
    synchronized public void setWriteValue(final int index, final Number value)
            throws IllegalStateException, Exception, IndexOutOfBoundsException
    {
        if (this.data == null)
        {
            throw new IllegalStateException("Cannot write tag " + this.name
                    + " because data type is unknown");
        }
        this.data.set(index, value);
        this.state = State.TO_BE_WRITTEN;
    }

    @Override
    public String toString()
    {
        final StringBuilder buf = new StringBuilder();
        buf.append("Tag '").append(this.name).append("'");
        if (this.data == null)
        {
            buf.append(" (no value)");
        }
        else
        {
            buf.append(" = ").append(this.data);
        }
        return buf.toString();
    }
}
