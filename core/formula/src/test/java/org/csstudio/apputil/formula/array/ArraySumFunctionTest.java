/*
 * Copyright (C) 2019 European Spallation Source ERIC.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */

package org.csstudio.apputil.formula.array;

import org.epics.util.array.ArrayDouble;
import org.epics.vtype.Alarm;
import org.epics.vtype.Display;
import org.epics.vtype.Time;
import org.epics.vtype.VDouble;
import org.epics.vtype.VNumberArray;
import org.epics.vtype.VType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ArraySumFunctionTest {

    @Test
    public void compute() {
        ArraySumFunction scaleArrayFunction = new ArraySumFunction();

        assertEquals("arraySum", scaleArrayFunction.getName());
        assertEquals("array", scaleArrayFunction.getCategory());

        VType offset = VDouble.of(2.0, Alarm.none(), Time.now(), Display.none());
        VType array = VNumberArray.of(ArrayDouble.of(1.0, 2.0, 3.0), Alarm.none(), Time.now(), Display.none());

        VNumberArray result = (VNumberArray)scaleArrayFunction.compute(array, offset);
        assertEquals(3, result.getData().size());
        assertTrue(result.getData().getDouble(0) == 3);
        assertTrue(result.getData().getDouble(1) == 4);
        assertTrue(result.getData().getDouble(2) == 5);

        result = (VNumberArray)scaleArrayFunction.compute(offset, offset);
        assertTrue(Double.valueOf(result.getData().getDouble(0)).equals(Double.NaN));

        result = (VNumberArray)scaleArrayFunction.compute(offset, array);
        assertTrue(Double.valueOf(result.getData().getDouble(0)).equals(Double.NaN));
    }
}