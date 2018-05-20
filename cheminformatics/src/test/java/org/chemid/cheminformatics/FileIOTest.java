/*
 *  Copyright (c) 2018, LC-Research. (http://www.lc-research.com)
 *
 *  LC-Research licenses this file to you under the Apache License V 2.0.
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 *  Unless required by applicable law or agreed to in writing, software distributed under the
 *  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 *  CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations under the License.
 */

package org.chemid.cheminformatics;

import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.testng.Assert;

import java.io.FileNotFoundException;
import java.io.IOException;

import static org.testng.Assert.*;

public class FileIOTest {

    @org.testng.annotations.Test
    public void testReadSDF() throws IOException {
        String FilePathToSDF = "src/test/resources/test.sdf";
        IteratingSDFReader sdfReader = FileIO.readSDF(FilePathToSDF);
        int moleculeCount = 0;
        while (sdfReader.hasNext()){
            moleculeCount++;
            sdfReader.next();
        }
        sdfReader.close();
        Assert.assertEquals(moleculeCount,2);
    }
}