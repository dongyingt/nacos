/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.nacos.client.config.remote;

import com.alibaba.nacos.api.config.remote.request.ConfigChangeListenRequest;
import com.alibaba.nacos.api.config.remote.request.ConfigPublishRequest;
import com.alibaba.nacos.api.config.remote.request.ConfigQueryRequest;
import com.alibaba.nacos.api.config.remote.request.ConfigRemoveRequest;
import com.alibaba.nacos.api.config.remote.response.ConfigChangeListenResponse;
import com.alibaba.nacos.api.config.remote.response.ConfigPubishResponse;
import com.alibaba.nacos.api.config.remote.response.ConfigQueryResponse;
import com.alibaba.nacos.api.config.remote.response.ConfigRemoveResponse;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.remote.request.Request;
import com.alibaba.nacos.api.remote.response.Response;
import com.alibaba.nacos.client.config.utils.ParamUtils;
import com.alibaba.nacos.client.remote.RpcClient;
import com.alibaba.nacos.client.remote.RpcClientFactory;
import com.alibaba.nacos.client.remote.ServerListFactory;

/**
 * config grpc client proxy.
 *
 * @author liuzunfei
 * @version $Id: ConfigGrpcClientProxy.java, v 0.1 2020年07月14日 3:37 PM liuzunfei Exp $
 */

public class ConfigGrpcClientProxy {
    
    private RpcClient rpcClient;
    
    public ConfigGrpcClientProxy() {
        rpcClient = RpcClientFactory.getClient("config");
    }
    
    public Response request(Request request) throws NacosException {
        return rpcClient.request(request);
    }
    
    public RpcClient getRpcClient() {
        return this.rpcClient;
    }
    
    public void initAndStart(ServerListFactory serverListFactory) throws NacosException {
        rpcClient.init(serverListFactory);
        rpcClient.start();
    }
    
    /**
     * send congif change listen request.
     *
     * @param dataId dataId
     * @param group  group
     * @param tenat  tenat
     * @throws NacosException throws when listen fail.
     */
    public void listenConfigChange(String dataId, String group, String tenat) throws NacosException {
    
        if (ParamUtils.useHttpSwitch()) {
            return;
        }
        ConfigChangeListenRequest configChangeListenRequest = ConfigChangeListenRequest
                .buildListenRequest(dataId, group, tenat);
        ConfigChangeListenResponse response = (ConfigChangeListenResponse) rpcClient.request(configChangeListenRequest);
        if (!response.isSuccess()) {
            throw new NacosException(NacosException.SERVER_ERROR, "Fail to Listen Config Change");
        }
    }
    
    /**
     * sned cancel listen congif change request .
     *
     * @param dataId dataId
     * @param group  group
     * @param tenat  tenat
     */
    public void unListenConfigChange(String dataId, String group, String tenat) throws NacosException {
    
        if (ParamUtils.useHttpSwitch()) {
            return;
        }
        ConfigChangeListenRequest configChangeListenRequest = ConfigChangeListenRequest
                .buildUnListenRequest(dataId, group, tenat);
        ConfigChangeListenResponse response = (ConfigChangeListenResponse) rpcClient.request(configChangeListenRequest);
        if (!response.isSuccess()) {
            throw new NacosException(NacosException.SERVER_ERROR, "Fail to UnListen Config Change");
        }
    }
    
    /**
     * query config content by grpc channel .
     *
     * @param dataId dataId
     * @param group  group
     * @param tenat  tenat
     * @return ConfigQueryResponse.
     * @throws NacosException throw where query fail .
     */
    public ConfigQueryResponse queryConfig(String dataId, String group, String tenat) throws NacosException {
    
        ConfigQueryRequest request = ConfigQueryRequest.build(dataId, group, tenat);
        ConfigQueryResponse response = (ConfigQueryResponse) rpcClient.request(request);
        return (ConfigQueryResponse) response;
    }
    
    /**
     * publish config.
     *
     * @param dataid dataid
     * @param group group
     * @param tenat tenat
     * @return push  result.
     * @throws NacosException throw where publish fail.
     */
    public ConfigPubishResponse publishConfig(String dataid, String group, String tenat, String content)
            throws NacosException {
        ConfigPublishRequest request = new ConfigPublishRequest(dataid, group, tenat, content);
        ConfigPubishResponse response = (ConfigPubishResponse) rpcClient.request(request);
        return (ConfigPubishResponse) response;
    }
    
    /**
     * remove config.
     *
     * @param dataid dataid
     * @param group group
     * @param tenat tenat
     * @return response.
     * @throws NacosException throw where publish fail.
     */
    public ConfigRemoveResponse removeConfig(String dataid, String group, String tenat, String tag)
            throws NacosException {
        ConfigRemoveRequest request = new ConfigRemoveRequest(dataid, group, tenat, tag);
        ConfigRemoveResponse response = (ConfigRemoveResponse) rpcClient.request(request);
        return (ConfigRemoveResponse) response;
    }
}
