/* $Id: Serializable.java,v 1.2 2006/08/20 15:40:12 wikipedian Exp $ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package othi.thg.common;

/**
 * Interface of all the object that wants to be able to be converted into a
 * stream of bytes.
 */
public interface Serializer {
	/** Method to convert the object into a stream */
	void writeObject(othi.thg.common.OutputSerializer out)
			throws java.io.IOException;

	/** Method to build the object from a stream of bytes */
	void readObject(othi.thg.common.InputSerializer in)
			throws java.io.IOException, java.lang.ClassNotFoundException;
}
