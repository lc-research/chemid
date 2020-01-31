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

package org.chemid.structure.common;


import org.eclipse.jetty.server.Connector;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.util.Scanner;

public class RestClient {

    public WebTarget getWebResource(String Url) {


        //System.out.println("Set proxy :");
      //  Scanner sc = new Scanner(System.in);
      //  Client client=null;
      //  boolean b = sc.nextBoolean();
        ClientConfig config = new ClientConfig();

        /*config.property(ApacheClientProperties.PROXY_URI, proxyUrl);
        Connector connector = new ApacheConnector(config);
        config.connector(connector);*/


        /*if(b){
            config.property(ClientProperties.PROXY_URI, "cachex.pdn.ac.lk:3128");
             client = ClientBuilder.newClient(config);
        }
        else {
             client = ClientBuilder.newClient(config);
        }/
      config.property(ApacheClientProperties.PROXY_URI, proxyUrl);
        Connector connector = new ApacheConnector(config);
        config.connector(connector);*/
        Client client = ClientBuilder.newClient(config);
        return client.target(Url);
    }
}
