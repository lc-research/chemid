package org.chemid.structure.dbclient.pubchem.beans;

/*
 * Copyright (c) 2016, ChemID. (http://www.chemid.org)
 *
 * ChemID licenses this file to you under the Apache License V 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
public class PubChemESearch {
    private String WebEnv;
    protected String QueryKey;

    public String getWebEnv() {
        return WebEnv;
    }

    public void setWebEnv(String webEnv) {
        this.WebEnv = webEnv;
    }

    public String getQueryKey() {
        return QueryKey;
    }

    public void setQueryKey(String queryKey) {
        this.QueryKey = queryKey;
    }
}


