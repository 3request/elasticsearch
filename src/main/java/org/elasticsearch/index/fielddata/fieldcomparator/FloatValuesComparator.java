/*
 * Licensed to ElasticSearch and Shay Banon under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. ElasticSearch licenses this
 * file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.index.fielddata.fieldcomparator;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.FieldComparator;
import org.elasticsearch.index.fielddata.FloatValues;
import org.elasticsearch.index.fielddata.IndexNumericFieldData;

import java.io.IOException;

/**
 */
public class FloatValuesComparator extends FieldComparator<Float> {

    private final IndexNumericFieldData indexFieldData;
    private final float missingValue;

    protected final float[] values;
    private float bottom;
    private FloatValues readerValues;

    public FloatValuesComparator(IndexNumericFieldData indexFieldData, float missingValue, int numHits) {
        this.indexFieldData = indexFieldData;
        this.missingValue = missingValue;
        this.values = new float[numHits];
    }

    @Override
    public int compare(int slot1, int slot2) {
        final float v1 = values[slot1];
        final float v2 = values[slot2];
        if (v1 > v2) {
            return 1;
        } else if (v1 < v2) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public void setBottom(int slot) {
        this.bottom = values[slot];
    }

    @Override
    public int compareBottom(int doc) throws IOException {
        float v2 = readerValues.getValueMissing(doc, missingValue);

        if (bottom > v2) {
            return 1;
        } else if (bottom < v2) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public void copy(int slot, int doc) throws IOException {
        values[slot] = readerValues.getValueMissing(doc, missingValue);
    }

    @Override
    public FieldComparator<Float> setNextReader(AtomicReaderContext context) throws IOException {
        this.readerValues = indexFieldData.load(context).getFloatValues();
        return this;
    }

    @Override
    public Float value(int slot) {
        return Float.valueOf(values[slot]);
    }

    @Override
    public int compareDocToValue(int doc, Float valueObj) throws IOException {
        final float value = valueObj.floatValue();
        float docValue = readerValues.getValueMissing(doc, missingValue);
        if (docValue < value) {
            return -1;
        } else if (docValue > value) {
            return 1;
        } else {
            return 0;
        }
    }
}
